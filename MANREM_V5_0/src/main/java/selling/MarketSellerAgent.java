/**
 * Market Seller Agent SellerAgent class.
 *
 */
package selling;

/**
 * Java and Jade libraries S
 */
import FIPA.DateTime;
import jade.core.*;
import jade.core.behaviours.*;

import jade.lang.acl.*;

import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;

import java.util.*;
import java.util.Vector;
import java.util.Date;

import java.io.*;
import java.text.DecimalFormat;
import java.util.logging.*;
import javax.swing.JOptionPane;
import risk.Risk;

public class MarketSellerAgent {
    // The list of known buyer agents

    private Vector buyerAgents = new Vector();
    private int step = 0;
    long time = 0;
    private int maxPrice;
    private int repliesCnt = 0; // The counter of replies from buyer agents
    private double[] sent_history;
    private Seller seller;

    public MarketSellerAgent(Seller seller) {
        this.seller = seller;
    }

    /**
     * This method is called by the GUI when the user inserts a new Market
     * Seller Agent
     *
     * @param title The The name of agent
     * @param initialPrice The initial price
     * @param minPrice The minimum price
     * @param energy The energy of agent
     * @param deadline The deadline by which to sell the energy
     *
     */
    public void NegotiationOfBilateralContracts(String title, double[] initPrice, double[] minPrice, double[] energy, String strName, String strAlgorithm, String preference, String riskpre, Date deadline, String contract, double[] prices_sent_to_buyer, double[] profile_received_from_buyer) {
        seller.addBehaviour(new NegotiationManager(seller, title, initPrice, minPrice, energy, strName, strAlgorithm, preference, riskpre, deadline, contract, prices_sent_to_buyer, profile_received_from_buyer));
    }

    private class NegotiationManager extends TickerBehaviour {

        int n_proposals_sent = 0;
        int n_proposals_received = 0;
        ArrayList<double[]> proposals_history = new ArrayList<>();
        int t = -1;
        final int PERIODS = seller.input_gui.PERIODS;
        int N_ISSUES = 2 * PERIODS;
        int case4 = 0;
        private String title;
        private String PreFunction, PreRiskFunction;
        private double[] initPrice = new double[N_ISSUES];
        AID system_agent = new AID("Market", AID.ISLOCALNAME);
        AID market_operator = new AID("MarketOperator", AID.ISLOCALNAME);
        private double[] lim = new double[N_ISSUES];
        private double[] limneg = new double[PERIODS];
        private double[] energy = new double[N_ISSUES];
        private String str, contract;
        private double[] k = new double[PERIODS];
        private String strategy_algorithm;
        private long deadline, deadline2;
        private double deadlineinternal;
//        private double[] deviation= Risk.deviation(PERIODS);
        private double lbda = 0.5;
        private double[] deviationlim = new double[PERIODS];
        private double[] deltaP = new double[PERIODS];
        private double[] currentPrice = new double[N_ISSUES];
        // private double[] Cf = {0.15, 0.2, 0.3};
        private double[] Cf = new double[PERIODS];
        private double[] Cfv = new double[PERIODS];
        private double[] Cf2 = new double[PERIODS];
        private double[] Cf3 = new double[PERIODS];
        private long currentTime, time1, timei2;
        private double timei, min, div, ipmed;
        private double Cf4, Cf5;
        private double[] Cf67 = {0.00, 0.00, 0.00, 0.00, 0.00, 0.00};
        private double Price, Temp;
        private long initTime, flag, deltaT;
        private ArrayList<double[]> received_history = new ArrayList<double[]>();

        double i = Math.random();
        double B4i = 2.0; // Beginning of range.
        double B4f = 3.0; // End of range.
        double B4 = B4i + i * (B4f - B4i);
        double j = Math.random();
        double B5i = 0.2; // Beginning of range.
        double B5f = 0.3; // End of range.
        double B5 = B5i + i * (B5f - B5i);
        double w = Math.random();
        double[] weight = new double[PERIODS];
        double cftotal = 0;

        int mi = 1; // Beginning of range.
        int M = 3; // End of range.
        int m = (int) (mi + w * (M - mi));
        // The negoctiation strategies
        String s1 = "Compromise";
        String s2 = "Low-Priority Concession";
        String s3 = "Volume Conceder";
        String s4 = "Price Management";
        String s5 = "Time Conceder";
        String s6 = "Time Boulware";
        String s7 = "Tit-For-Tat";
        String s8 = "Random Tit-For-Tat";
        String s9 = "Intrasigent Priority";
        String s10 = "Inverse Tit-For-Tat Behaviour";
        String s11 = "Negotiation Risk Strategy";
        String p1 = "Additive Function";
        String p2 = "Benefit Function";
        String pr1 = "Risk Function";
        String pr2 = "Von Neumann-Morgenstern";
        String pr3 = "Rigorous Risk Function";
        double[] prices_sent_to_buyer = new double[PERIODS];
        double[] profile_received_from_buyer = new double[PERIODS + 1];
        private boolean s4_vectorial_distance = false;
        double[] tariff_cmp_previous = new double[2 * PERIODS];

        private NegotiationManager(Seller a, String t, double[] ip, double[] mp, double[] en, String s, String s_algorithm, String pre, String riskpre, Date d, String c, double[] prices_sent_to_buyer, double[] profile_received_from_buyer) {
            super(a, 1); // tick every 1/4 minute
            title = t;

            // Initial Prices, Limits, and Energy Volumes
            initPrice = ip;
            lim = mp;
            energy = en;
            str = s;
            contract = c;
            sent_history = new double[2 * PERIODS];
            PreFunction = pre;
            PreRiskFunction = riskpre;

            strategy_algorithm = s_algorithm;
            deadline2 = (d.getTime() - System.currentTimeMillis());
            deadline = d.getTime();
            deadlineinternal = (4.1 * deadline2) / (24 * 60 * 60 * 1000);

            // Starting the alternanting offers protocol
            myAgent.addBehaviour(new AlternantingOffersProtocol(this, lim));

            initTime = System.currentTimeMillis();
            deltaT = ((deadline - initTime) > 0 ? (deadline - initTime) : 15000);
            this.profile_received_from_buyer = profile_received_from_buyer;
            this.prices_sent_to_buyer = prices_sent_to_buyer;
        }

        // Strategy Handler
        public double[] strategy(double[] tariff_rcv, double[] tariff_cmp_previous) {
            double temp1, temp2;
            double temp3, temp4;
            //int strCode;
            double energyTotal = 0.0;
            double iptotal = 0, v;
//            double Cf1 = 0.1667;
            double Cf1 = 0.25;
            double arisk = 0.0;
            double brisk = 0.0;

            if (n_proposals_sent == 0) {
                time1 = System.currentTimeMillis();
            }

            //Initial Prices -- Initial Proposal
//            if (flag == 0 && !str.equals(s4)) {
            if (flag == 0) {

                ipmed = (iptotal / initPrice.length);
                System.out.println("\nseller\n");
                for (int i = 0; i < initPrice.length; i++) {

                    Cf[i] = (PERIODS / 6) * (Cf1 / 0.1667) * (1 / weight[i]) / cftotal;
//                weight[i] = roundThreeDecimals(weight[i]);
                    System.out.println("peso " + i + ": " + weight[i] + "\n");
                }

                for (int i = 0; i < initPrice.length; ++i) {
                    currentPrice[i] = initPrice[i];
                    sent_history[i] = initPrice[i];
                    currentPrice[i + PERIODS] = energy[i];
                    if (n_proposals_received == 0) {
                        if (energy[i] < profile_received_from_buyer[i]) {
                            currentPrice[i + PERIODS] = energy[i];
                        } else {
                            currentPrice[i + PERIODS] = profile_received_from_buyer[i];
                        }
                    } else {
                        if (n_proposals_received > 0) {
                            if (energy[i] < tariff_rcv[i + PERIODS]) {
                                currentPrice[i + PERIODS] = energy[i];
                            } else {
                                currentPrice[i + PERIODS] = tariff_rcv[i + PERIODS];
                            }
                        }
                    }

                    flag = 1;

                }
            }//Computed Prices -- Second and Next Proposals
            if (n_proposals_received != 0) {
                seller.received_history.add(tariff_rcv);

                for (int i = 0; i < initPrice.length; ++i) {
                    sent_history[i] = currentPrice[i];
                    if (n_proposals_received == 0) {
                        if (energy[i] < profile_received_from_buyer[i]) {
                            currentPrice[i + PERIODS] = energy[i];
                        } else {
                            currentPrice[i + PERIODS] = profile_received_from_buyer[i];
                        }
                    } else {
                        if (n_proposals_received > 0) {
                            if (energy[i] < tariff_rcv[i + PERIODS]) {
                                currentPrice[i + PERIODS] = energy[i];
                            } else {
                                currentPrice[i + PERIODS] = tariff_rcv[i + PERIODS];
                            }
                        }
                    }
//                    System.out.println(" /n preco" +currentPrice[i]+" energia " +currentPrice[i + 6]+" /n "+" tarifa recebida "+tariff_rcv[i+6]);
                }
                if (seller.risk == 1) {

//                     for (int i=0;i < PERIODS; ++i){
//                         arisk=currentPrice[i]-lim[i];
//                    deviationlim[i]=Math.min(arisk,deviation[i]);
//                            }
//                     deltaP=Risk.deltaPs(lbda, currentPrice, seller.input_gui.price_mec, seller.sharing_risk, seller.input_gui.deviation,k);
                    for (int i = 0; i < PERIODS; ++i) {
                        deltaP[i] = 100;
                        brisk = currentPrice[i] - lim[i];
                        limneg[i] = currentPrice[i] - Math.min(Math.abs(deltaP[i]), brisk);
                    }
                } else {
                    for (int i = 0; i < PERIODS; ++i) {
                        limneg[i] = lim[i];
                    }
                }
                // The different Strategies
                if (str.equals(s1)) {

                    // Concession Making Strategy
                    for (int j = 0; j < initPrice.length; ++j) {
//                        System.out.println(" /n " +step+" /n " +lim[j]+currentPrice[j]+" /n ");
                        temp1 = Cf1 * (currentPrice[j] - limneg[j]);
                        temp2 = currentPrice[j] - temp1;
                        currentPrice[j] = temp2;
                    }
                } else {
                    if (str.equals(s2)) {

                        // Low-Priority Concession
                        for (int j = 0; j < initPrice.length; ++j) {
                            temp1 = Cf[j] * (currentPrice[j] - limneg[j]);
//                            System.out.println(" \n Cf" +Cf[j]);
                            temp2 = currentPrice[j] - temp1;
                            currentPrice[j] = temp2;
                        }
                    } else {
                        if (str.equals(s3)) {

                            // E-R Concession Strategy
                            v = -Math.log(Cf1) * energy.length;
                            for (int i = 0; i < energy.length; ++i) {
                                energyTotal = energyTotal + currentPrice[i + PERIODS];
                            }
                            for (int j = 0; j < initPrice.length; ++j) {
                                temp3 = currentPrice[j + PERIODS] / energyTotal;
                                temp4 = (ipmed / limneg[j]) * Math.exp(-v * temp3);
//                                temp4 = Math.exp(-v * temp3);
                                Cfv[j] = temp4;
                                temp1 = Cfv[j] * (currentPrice[j] - limneg[j]);
                                temp2 = currentPrice[j] - temp1;
                                currentPrice[j] = temp2;
                            }
                        } else {
                            if (str.equals(s4)) {

                                //-----<<< Price Management Strategy >>>-----
                                if (strategy_algorithm != null) {
                                    if (strategy_algorithm.equals("Maximum Benefit")) {
                                        s4_vectorial_distance = false;
                                    } else {
                                        s4_vectorial_distance = true;
                                    }
                                }
                                for (int i = 0; i < initPrice.length; ++i) {
                                    tariff_rcv[i + PERIODS] = currentPrice[i + PERIODS];
                                }

                                BenefitManagerSeller benefit_manager_seller = new BenefitManagerSeller(tariff_rcv.clone(), tariff_cmp_previous.clone(), limneg, initPrice, s4_vectorial_distance);
                                if (profile_received_from_buyer != null) {

                                    // 1th to initiate the negotiation, send 1th proposal 
                                    if (checkEmptyTariff(tariff_cmp_previous) && checkEmptyTariff(tariff_rcv)) {
//                                        for (int i = 0; i < prices_sent_to_buyer.length; i++) {
//                                            currentPrice[i] = prices_sent_to_buyer[i];
//                                            currentPrice[i + 6] = profile_received_from_buyer[i];
//                                        }
                                        this.tariff_cmp_previous = currentPrice.clone();

                                        // 2nd to initiate the negotiation, send 1th proposal
                                    } else if (checkEmptyTariff(tariff_cmp_previous)) {
//                                        for (int i = 0; i < prices_sent_to_buyer.length; i++) {
//                                            currentPrice[i] = prices_sent_to_buyer[i];
//                                            currentPrice[i + 6] = tariff_rcv[i + 6];
//                                        }
                                        this.tariff_cmp_previous = currentPrice.clone();
                                    } else {
                                        this.tariff_cmp_previous = currentPrice.clone();
                                        currentPrice = benefit_manager_seller.execute();
                                    }
                                }

                            } else {
                                if (str.equals(s5)) {
                                    // Conceder, Time Concession Strategy:
                                    double k = 0.1;
//                                timei2 = System.currentTimeMillis();
//                                    if (timei2 == time1) {
//                                        System.out.print("Problem!!!");
//                                    }
//                                timei = timei2 - time1;
//                                System.out.println(" /n " +timei);
                                    time = System.currentTimeMillis() - time;
                                    timei = timei + time;
//                                time++;

                                    if (timei > deadlineinternal) {
                                        step = 4;
                                    } else {
                                        min = Math.min(timei, deadlineinternal);
                                        div = min / deadlineinternal;
                                        Cf4 = k + (1 - k) * Math.pow(div, (1 / B4));
                                        for (int j = 0; j < initPrice.length; ++j) {
                                            temp1 = Cf4 * (currentPrice[j] - limneg[j]);
                                            temp2 = currentPrice[j] - temp1;
                                            currentPrice[j] = temp2;
                                        }
                                    }
                                } else {
                                    // Boulware, Time Concession Strategy:
                                    if (str.equals(s6)) {
                                        double k = 0.1;
//                                timei2 = System.currentTimeMillis();
//                    //            if (timei2 == time1) {
//                    //                System.out.print("Problem!!!");
//                    //            }
//                                timei = timei2 - time1;
//                                System.out.println(" /n " +timei);
                                        time = System.currentTimeMillis() - time;
                                        timei = timei + time;
//                                time++;

                                        if (timei > deadlineinternal) {
                                            step = 4;
                                        } else {
                                            min = Math.min(timei, deadlineinternal);
                                            div = min / deadlineinternal;
                                            Cf5 = k + (1 - k) * Math.pow(div, (1 / B5));
                                            for (int j = 0; j < initPrice.length; ++j) {
                                                temp1 = Cf5 * (currentPrice[j] - limneg[j]);
                                                temp2 = currentPrice[j] - temp1;
                                                currentPrice[j] = temp2;
                                            }
                                        }
                                    } else {
                                        if (str.equals(s7)) {
                                            // Tit-For-Tat, "Opponent" Behaviour Strategy:
                                            if (n_proposals_received != 1) {
                                                received_history.add(tariff_rcv);
                                            }

//                                if (n_proposals_sent == 1) {
//                                    for (int j = 0; j < initPrice.length; ++j) {
//                                        double z1 = Math.random();
//                                        double range11 = 0.15; // Beginning of range.
//                                        double range12 = 0.30; // End of range.
//                                        double Cfmore = range11 + z1 * (range12 - range11);
//                                        double z2 = Math.random();
//                                        double range21 = 0.01; // Beginning of range.
//                                        double range22 = 0.15; // End of range.
//                                        double Cfless = range21 + z2 * (range22 - range21);
//                                        if (j == 0 || j == 1) {
//                                            Cf67[j] = Cfmore;
//                                        } else {
//                                            Cf67[j] = Cfless;
//                                        }
//                                        Cf67[5] = 1 - (Cf67[0] + Cf67[1] + Cf67[2] + Cf67[3] + Cf67[4]);
//                                    }
//                                }
                                            for (int j = 0; j < initPrice.length; ++j) {
//                                    System.out.println("preco " + currentPrice[j]+ " limite "+ lim[j]);
                                                if (received_history.size() == 1) {
//                                        temp1 = Cf67[j] * currentPrice[j];
                                                    temp1 = Cf[j] * (currentPrice[j] - limneg[j]);
                                                    temp2 = currentPrice[j] - temp1;
                                                    currentPrice[j] = temp2;
                                                } else {
                                                    double max = Math.max(((received_history.get(received_history.size() - 2)[j] / received_history.get(received_history.size() - 1)[j]) * currentPrice[j]), limneg[j]);
                                                    temp1 = Math.min(max, initPrice[j]);
                                                    currentPrice[j] = temp1;
                                                }
                                            }
                                        } else {
                                            if (str.equals(s8)) {
                                                // Random Tit-For-Tat, "Opponent" Behaviour Strategy:
                                                if (n_proposals_received != 1) {
                                                    received_history.add(tariff_rcv);
                                                }

//                                if (n_proposals_sent == 1) {
//                                    for (int j = 0; j < initPrice.length; ++j) {
//                                        double z1 = Math.random();
//                                        double range11 = 0.15; // Beginning of range.
//                                        double range12 = 0.30; // End of range.
//                                        double Cfmore = range11 + z1 * (range12 - range11);
//                                        double z2 = Math.random();
//                                        double range21 = 0.01; // Beginning of range.
//                                        double range22 = 0.15; // End of range.
//                                        double Cfless = range21 + z2 * (range22 - range21);
//                                        if (j == 0 || j == 1) {
//                                            Cf67[j] = Cfmore;
//                                        } else {
//                                            Cf67[j] = Cfless;
//                                        }
//                                        Cf67[5] = 1 - (Cf67[0] + Cf67[1] + Cf67[2] + Cf67[3] + Cf67[4]);
//                                    }
//                                }
                                                int s = 0; // Considering that the value of s is always decreasing.
                                                for (int j = 0; j < initPrice.length; ++j) {
//                                    System.out.println("preco " + currentPrice[j]+ " limite "+ lim[j]+ " preco inicial "+initPrice[j]);
                                                    if (received_history.size() == 1) {
//                                        temp1 = Cf67[j] * currentPrice[j];
                                                        temp1 = Cf[j] * (currentPrice[j] - limneg[j]);
                                                        temp2 = currentPrice[j] - temp1;
                                                        currentPrice[j] = temp2;
                                                    } else {
                                                        double max = Math.max(currentPrice[j] + (received_history.get(received_history.size() - 2)[j] - received_history.get(received_history.size() - 1)[j]) + Math.pow(- 1, s) * m, limneg[j]);
                                                        temp1 = Math.min(max, initPrice[j]);
                                                        currentPrice[j] = temp1;
                                                    }
                                                }
                                            } else {
                                                if (str.equals(s9)) {
                                                    // -----<<< Intrasigent Priority Concession >>>-----
                                                    intrasigentWeights();
                                                    for (int j = 0; j < initPrice.length; ++j) {
                                                        temp1 = Cf[j] * (currentPrice[j] - limneg[j]);
//                            System.out.println(" \n Cf" +Cf[j]);
                                                        temp2 = currentPrice[j] - temp1;
                                                        currentPrice[j] = temp2;
                                                    }
                                                } else {
                                                    if (str.equals(s10)) {

                                                        //Inverse Tit-For-Tat, "Opponent" Behaviour Strategy:
                                                        double[] aux = new double[PERIODS];
                                                        int i = 0;
                                                        double a = 0, average = 0;

                                                        if (n_proposals_received != 1) {
                                                            received_history.add(tariff_rcv);
                                                        }

                                                        for (int j = 0; j < initPrice.length; ++j) {
                                                            if (received_history.size() == 1) {
                                                                temp1 = Cf[j] * (currentPrice[j] - limneg[j]);
                                                                temp2 = currentPrice[j] + temp1;
                                                                currentPrice[j] = temp2;
                                                            } else {
                                                                if (i < PERIODS) {
                                                                    for (i = 0; i < initPrice.length; ++i) {
                                                                        aux[i] = (received_history.get(received_history.size() - 2)[i] / received_history.get(received_history.size() - 1)[i]);
                                                                        a = a + aux[i];
                                                                    }
                                                                    average = a / initPrice.length;
                                                                }
                                                                aux[j] = average - (aux[j] - average);
                                                                double max = Math.max((aux[j] * currentPrice[j]), limneg[j]);
//                                        System.out.println("\n -2: "+received_history.get(received_history.size() - 2)[j]+"\n -1: "+received_history.get(received_history.size() - 1)[j]);
                                                                temp1 = Math.min(max, initPrice[j]);
                                                                currentPrice[j] = temp1;
                                                            }
                                                        }
                                                    } else {
                                                        if (str.equals(s11)) {

                                                            int epsilon = 32;
                                                            for (int j = 0; j < initPrice.length; ++j) {
//                        System.out.println(" /n " +step+" /n " +lim[j]+currentPrice[j]+" /n ");
                                                                temp1 = Cf1 * (currentPrice[j] - limneg[j]) * Math.pow((1.0 - ((Math.pow(seller.input_gui.deviation[j + PERIODS] * (1.0 - seller.sharing_risk), 2)) / (initPrice[j]))), epsilon * lbda);
                                                                temp2 = currentPrice[j] - temp1;
                                                                currentPrice[j] = temp2;
                                                            }
                                                        }
                                                    }
                                                }// end if s4 
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return currentPrice;
        } //end strategy()

        private void intrasigentWeights() {
            int aux = 0, test = -1, test2 = -1;
            double sumweight = 0, cftotal = 0;
            double[] auxweight = new double[PERIODS];

            for (int j = 0; j < weight.length; j++) {
                auxweight[j] = weight[j];
            }
            for (int j = 0; j < weight.length; j++) {
                for (int i = 0; i < weight.length; i++) {
                    if (i != j) {
                        if (1.3 * auxweight[j] < auxweight[i]) {
                            weight[j] = auxweight[j];
                            i = PERIODS;
                            if (test == j) {
                                aux--;
                            }
                        }
                        if (i < PERIODS) {
                            if ((auxweight[j] >= 1.5 * auxweight[i])) {
                                weight[j] = 0;
                                //                    Cf[j]=0;
                                if (test != j) {
                                    aux++;
                                }
                                test = j;
                                test2 = test;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < weight.length; i++) {
                sumweight = sumweight + weight[i];
            }
            if (weight.length - aux > 1) {
                for (int i = 0; i < weight.length; i++) {
                    if (weight[i] != (0)) {
                        weight[i] = weight[i] / sumweight;
                        cftotal += 1 / weight[i];
                    }
                }
                for (int i = 0; i < weight.length; i++) {
                    if (weight[i] != (0)) {
                        Cf[i] = (1 / weight[i]) / cftotal;
                    } else {
                        Cf[i] = 0;
                    }
                }
            } else {
                for (int i = 0; i < weight.length; i++) {
                    if (j == i) {
                        Cf[test] = 0.5;
                    } else {
                        Cf[i] = 0;
                    }

                }
            }
        }

        public void onStart() {
            super.onStart();
        }

        public void onTick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime > deadline) {
                // Deadline expired
                seller.getGui().updateLog1("\n Deadline End\nNEGOTIATION TERMINATE!" + title);
                stop();
            }
        }

        //-----<<< Check if recived tariff is empty >>>-----
        private boolean checkEmptyTariff(double[] tariff_rcv) {
            for (int i = 0; i < tariff_rcv.length; i++) {
                if (tariff_rcv[i] != 0.0) {
                    return false;
                }
            }
            return true;
        }
    } // End of Negotiation Manager

    /**
     * ********************************************
     *
     * Implementation of Price Management Strategy
     *
     * ********************************************
     *
     */
    public class BenefitManagerSeller {

        private HashMap<Double, ArrayList<double[]>> benefit_list = new HashMap<>();
        private final Double benefit_serch_delta_initial = 50.0;
        private final Double benefit_search_delta_final = 100.0;
        final int PERIODS = seller.input_gui.PERIODS;
        private final Double price_addition_delta = 2.5;
        private double[] prices_limits_min;
        private double[] prices_limits_max;
        private double[] seller_prices_volumes_previous;
        private double[] buyer_prices_volumes_current;
        private boolean vectorial_distance = false;

        public BenefitManagerSeller(double[] buyer_prices_volumes, double[] seller_prices_volumes_previous, double[] prices_limits_min, double[] prices_limits_max, boolean vectorial_distance) {
            this.prices_limits_min = prices_limits_min;
            this.prices_limits_max = prices_limits_max;
            this.buyer_prices_volumes_current = buyer_prices_volumes;
            this.seller_prices_volumes_previous = seller_prices_volumes_previous;
            this.vectorial_distance = vectorial_distance;
            for (int i = 0; i < buyer_prices_volumes.length; i++) {
//            System.out.println(" /n energia compra " +buyer_prices_volumes[i]+" energia vende " +seller_prices_volumes_previous[i]+" /n ");
            }
        }

        public double[] execute() {
            createDeltaBenefitList();
            return chooseBenefitFromList();
        }

        //-----<<< Creates a list of possible benefits and respective prices in the range of defined benefits >>>-----
        private void createDeltaBenefitList() {
            double[] prices = new double[PERIODS];

            for (int j = 0; j < prices.length; j++) {
                prices[j] = this.seller_prices_volumes_previous[j];
            }

            HashMap<Double, ArrayList<double[]>> aux = new HashMap<>();
            setBenefitList(aux);

            //Sets the range of benefits to search 
            double u_aux = calculateBenefitPrice(prices) - benefit_serch_delta_initial;
            double i = u_aux - getBenefitSearchDelta();
            while (getBenefitList().isEmpty()) {
                createDeltaBenefitListAux(prices, i, u_aux);
                i = i - getBenefitSearchDelta();
            }

            System.out.println();
        }

        //-----<<< Creates an auxiliar list of possible benefits and respective prices in the range of minimum and maximum prices >>>-----
        private void createDeltaBenefitListAux(double[] prices, double i, double b_initial) {
            double[] prices_aux = new double[6];

            for (double a = getPricesLimitsMin()[0]; a <= getPricesLimitsMax()[0]; a = a + getPriceAddition()) {
                for (double b = getPricesLimitsMin()[1]; b <= getPricesLimitsMax()[1]; b = b + getPriceAddition()) {
                    for (double c = getPricesLimitsMin()[2]; c <= getPricesLimitsMax()[2]; c = c + getPriceAddition()) {
                        for (double d = getPricesLimitsMin()[3]; d <= getPricesLimitsMax()[3]; d = d + getPriceAddition()) {
                            for (double e = getPricesLimitsMin()[4]; e <= getPricesLimitsMax()[4]; e = e + getPriceAddition()) {
                                for (double f = getPricesLimitsMin()[5]; f <= getPricesLimitsMax()[5]; f = f + getPriceAddition()) {

                                    prices_aux[0] = a;
                                    prices_aux[1] = b;
                                    prices_aux[2] = c;
                                    prices_aux[3] = d;
                                    prices_aux[4] = e;
                                    prices_aux[5] = f;

                                    if (!Arrays.equals(prices, prices_aux)) {
                                        double b_aux = calculateBenefitPrice(prices_aux);
                                        if (b_aux <= b_initial && b_aux > i) {
                                            addToBenefitList(b_aux, prices_aux);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //-----<<< Calculates the benefit of the seller agent >>>-----   
        private double calculateBenefitPrice(double[] prices) {
            double benefit = 0.0;

            for (int i = 0; i < PERIODS; i++) {
                double price = prices[i];
                double price_min = getPricesLimitsMin()[i];
                double volume = buyer_prices_volumes_current[i + PERIODS];
                double benefit_2 = (price * volume) - (price_min * volume);
                benefit = benefit + benefit_2;
            }
            return roundTwoDecimals(benefit);
        }

        //-----<<< Rounds the value of obtained benefit two decimals >>>-----
        public double roundTwoDecimals(double num) {
            double result = num * 100;
            result = Math.round(result);
            result = result / 100;
            return result;
        }

        //-----<<< Adds possible benefits and respective prices to benefit list >>>-----       
        private void addToBenefitList(double benefit, double[] prices) {

            if (getBenefitList().containsKey(benefit)) {
                getBenefitList().get(benefit).add(prices.clone());
            } else {
                ArrayList<double[]> list = new ArrayList<>();
                list.add(prices.clone());
                getBenefitList().put(benefit, list);
            }
        }

        //-----<<< Choose the maximum benefit and respective prices from the list >>>-----
        private double[] chooseBenefitFromList() {

            Object[] benefit_values = new Object[getBenefitList().keySet().size()];
            benefit_values = getBenefitList().keySet().toArray();
            Arrays.sort(benefit_values);

            double[] new_prices = new double[PERIODS];
            double nearest_vectorial_distance = Double.MAX_VALUE;
            double sum_maximum_benefit = Double.MIN_VALUE;

            for (int i = 0; i < benefit_values.length; i++) {
                for (int j = 0; j < benefit_list.get(benefit_values[i]).size(); j++) {
                    if (vectorial_distance) {
                        // Vectorial distance option   
                        double distance_aux = calculateVectorialDistance(getBenefitList().get(benefit_values[i]).get(j));
                        if (distance_aux < nearest_vectorial_distance) {
                            nearest_vectorial_distance = distance_aux;
                            new_prices = benefit_list.get(benefit_values[i]).get(j);
                        }
                    } else {
                        // Maximum benefit option            
                        double sum_benefit_aux = calculateSumBenefit(getBenefitList().get(benefit_values[i]).get(j));
                        if (sum_benefit_aux > sum_maximum_benefit) {
                            sum_maximum_benefit = sum_benefit_aux;
                            new_prices = benefit_list.get(benefit_values[i]).get(j);
                        }
                    }
                }
            }

            double[] prices_volumes_final = new double[2 * PERIODS];
            for (int i = 0; i < PERIODS; i++) {
                prices_volumes_final[i] = new_prices[i];
                prices_volumes_final[i + PERIODS] = buyer_prices_volumes_current[i + PERIODS];
            }
            double d = calculateBenefitPrice(prices_volumes_final);
            System.out.println(d);
            return prices_volumes_final;
        }

        //-----<<< 1st Criterion of choice for Price - Vectorial Distance >>>-----
        private double calculateVectorialDistance(double[] possible_new_prices) {
            double distance = 0.0;

            for (int i = 0; i < possible_new_prices.length; i++) {
                distance = distance + Math.pow(this.seller_prices_volumes_previous[i] - possible_new_prices[i], 2);
            }
            distance = Math.sqrt(distance);
            return Math.abs(distance);
        }

        //-----<<< 2nd Criterion of choice for Price - Maximum Benefit >>>-----
        private double calculateSumBenefit(double[] possible_new_prices) {

            double sum_benefit = 0.0;
            for (int i = 0; i < possible_new_prices.length; i++) {
                sum_benefit = sum_benefit + ((possible_new_prices[i] - getPricesLimitsMin()[i]) * buyer_prices_volumes_current[i + PERIODS]);
            }
            return sum_benefit;
        }

        public HashMap<Double, ArrayList<double[]>> getBenefitList() {
            return benefit_list;
        }

        public void setBenefitList(HashMap<Double, ArrayList<double[]>> benefit_list) {
            this.benefit_list = benefit_list;
        }

        public Double getBenefitSearchDelta() {
            return benefit_search_delta_final;
        }

        public Double getPriceAddition() {
            return price_addition_delta;
        }

        private double[] getPricesLimitsMin() {
            return this.prices_limits_min;
        }

        private double[] getPricesLimitsMax() {
            return this.prices_limits_max;
        }
    } //End Benefit Manager Seller

    /*
     * Inner class AlternantingOffersProtocol This is the behaviour used by
     * Market-seller agents to implement the protocol of alternanting offers,
     * i.e., agents exchange offers and counter-offers until they find an
     * agreement or either they reach a deadline or they decide to opt out the
     * negotiation.
     */
    private class AlternantingOffersProtocol extends Behaviour {

        // The template to receive replies
        final int PERIODS = seller.input_gui.PERIODS;
        int N_ISSUES = PERIODS;
        private double[] min_p = new double[N_ISSUES];
        private double[] min_e = new double[N_ISSUES];
        double Bmin = Double.MAX_VALUE, Amin = 0, Dmin = 0;
        private String title;
        private NegotiationManager pmanager;
        private double Brcv = 0.0, Bcmp = 0.0, Arcv = 0.0, Acmp = 0.0, Drcv = 0.0, Dcmp = 0.0;
        private String send;
        double[] tariff_rcv = new double[N_ISSUES * 2];
        double[] tariff_cmp = new double[N_ISSUES * 2];
        // double[] weight = {0.25, 0.30, 0.45};
//       double[] weight = new double[N_ISSUES];
        private double[] ip;
        private double iptotal;
        ACLMessage msg = null;
        ACLMessage reply = null;
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        public AlternantingOffersProtocol(NegotiationManager pm, double[] minp) {

            super(null);
            pmanager = pm;
            min_p = minp;
            iptotal = 0;

            this.ip = pm.lim;
            for (int i = 0; i < PERIODS; ++i) {
                iptotal = iptotal + ip[i];
            }
            for (int i = 0; i < PERIODS; ++i) {
                pmanager.weight[i] = ip[i] / iptotal;
                pmanager.cftotal = pmanager.cftotal + 1 / pmanager.weight[i];

            }

            for (int i = 0; i < PERIODS; i++) {
                pmanager.weight[i] = roundThreeDecimals(pmanager.weight[i]);
            }
        }

        public void action() {

            //Round decimal numbers
            DecimalFormat twodecimal = new DecimalFormat("0.00");
            DecimalFormat threedecimal = new DecimalFormat("0.000");
            String[] choices1 = {"Send", "Cancel"};
            String[] choices2 = {"Accept", "Send", "New", "Withdraw"};
            String[] choices21 = {"Accept", "Send", "Withdraw"};
            String[] choices3 = {"Accept", "Cancel"};
            String[] choices4 = {"OK"};
            int aux = -1, aux2 = -1;

            switch (step) {

                case 0:

                    pmanager.n_proposals_sent = 0;

                    Drcv = 0;
                    Dcmp = 0;

                    if (seller.risk == 1) {
                        for (int i = 0; i < PERIODS; ++i) {
                            pmanager.k[i] = 0;
                            pmanager.limneg[i] = pmanager.lim[i];
                            pmanager.k[i] = -1 / pmanager.lbda * (Math.log(0.01) / (pmanager.initPrice[i] - seller.input_gui.price_mec.get(i)));
                        }
                    }

                    if (seller.risk == 0 && pmanager.PreFunction.equals(pmanager.p1)) {

                        for (int i = 0; i < PERIODS; ++i) {
                            Dcmp = Dcmp + (pmanager.initPrice[i] - pmanager.lim[i]) / (pmanager.initPrice[i] - pmanager.lim[i]) * pmanager.weight[i];

//                                Arcv=Arcv+(tariff_rcv[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] * (tariff_rcv[i] - min_p[i])/(1000))*weight[i];
//                                Acmp=Acmp+(tariff_cmp[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] *(tariff_cmp[i] -  min_p[i])/(1000))*weight[i];
                        }
                    } else if (seller.risk == 0 && pmanager.PreFunction.equals(pmanager.p2)) {
                        for (int i = 0; i < PERIODS; ++i) {
                            Dcmp = Dcmp + pmanager.energy[i] * pmanager.profile_received_from_buyer[PERIODS] * (pmanager.initPrice[i] - min_p[i]) / (1000);
                        }
                    }
                    if (seller.risk == 1 && pmanager.PreRiskFunction.equals(pmanager.pr1)) {

                        Dcmp = Risk.useller(pmanager.lbda, pmanager.initPrice, seller.input_gui.price_mec, seller.sharing_risk, seller.input_gui.deviation, pmanager.k) / PERIODS;
                    } else if (seller.risk == 1 && pmanager.PreRiskFunction.equals(pmanager.pr2)) {
                        for (int i = 0; i < PERIODS; ++i) {
                            Dcmp = Dcmp + ((1 - Math.exp(pmanager.lbda * (pmanager.initPrice[i] - pmanager.lim[i]) / (seller.input_gui.deviation[i + PERIODS] - seller.input_gui.deviation[i]))) / (1 - Math.exp(pmanager.lbda))) / PERIODS;
                        }
                    } else if (seller.risk == 1 && pmanager.PreRiskFunction.equals(pmanager.pr3)) {
                        for (int i = 0; i < PERIODS; ++i) {

                            Dcmp = Dcmp + ((1 - Math.exp(pmanager.lbda * (pmanager.initPrice[i] - (pmanager.limneg[i] + seller.input_gui.deviation[i + PERIODS] * (1 - seller.sharing_risk))) / (pmanager.initPrice[i] - (pmanager.limneg[i] + seller.input_gui.deviation[i + PERIODS] * (1 - seller.sharing_risk))))) / (1 - Math.exp(pmanager.lbda))) / PERIODS;
                        }
                    }
//                    seller.utilities.add(100.0*Dcmp);
//                    System.out.println("\nSeller: Calcula1\n"+100.0*Dcmp);
                    msg = myAgent.receive(mt);

                    if (msg == null) {
                        msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setOntology("market_ontology");
                        msg.setProtocol("negotiation_protocol");
                        msg.setContent("init_negotiation");
                        msg.addReceiver(seller.getOpponent());
                        msg.setReplyWith(String.valueOf(System.currentTimeMillis()));

                        mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
                        myAgent.send(msg);

                        step = 1; //wait for accept

                        block();
                        break;

                    } else {
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                        mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                        myAgent.send(reply);

                        seller.getGui().updateLog1("****************************************************************************************************************");
                        seller.getGui().updateLog1("             **                                                     STARTING NEGOTIATION                                                **");
                        seller.getGui().updateLog1("****************************************************************************************************************");
                        step = 3; //wait for proposal

                        block();
                        break;
                    }

                case 1:

                    msg = myAgent.receive(mt);
                    if (msg == null) {
                        block();
                        break;
                    }

                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        seller.getGui().updateLog1("****************************************************************************************************************");
                        seller.getGui().updateLog1("             **                                                     STARTING NEGOTIATION                                                **");
                        seller.getGui().updateLog1("****************************************************************************************************************");
                        step = 2;
                        break;
                    }

                case 2:

                    reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                    if (pmanager.n_proposals_received == 0) {
                        while (seller.counteroffer == 0 && pmanager.n_proposals_sent != 0) {
//                     buyer.gui.stop();

                        }
                        seller.counteroffer = 0;
                        if (seller.ES == 1 && !seller.input_gui.tactic.equals(seller.input_gui.sStrategy.getText())) {
                            seller.input_gui.ES(seller.input_gui.Parent);
                        }
                        tariff_cmp = (double[]) pmanager.strategy(tariff_rcv, tariff_cmp);
                    }
//                    try {
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        ObjectOutputStream oos = new ObjectOutputStream(baos);
//                        oos.writeObject(tariff_cmp);
//                        oos.close();
//                        reply.setByteSequenceContent(baos.toByteArray());
//                    } catch (IOException ex) {
//                        Logger.getLogger(AlternantingOffersProtocol.class.getName());
//                    }
                    if (pmanager.n_proposals_sent < 1 && pmanager.n_proposals_received < 1) {
//                    int result = JOptionPane.showConfirmDialog(null, ("\n\n          Send First Proposal? \n\n"+"Price 1:           " +twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_cmp[5]) + "        €/MWh\n\n\n"), seller.getLocalName()+" Starting Negotation", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
//                    String proposal="\n\n          Send First Proposal? \n\n"+"Price 1:           " +twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_cmp[5]) + "        €/MWh\n\n\n";
                        String[] proposal = new String[PERIODS + 1];
                        proposal[0] = "          Send First Proposal? ";
                        for (int i = 0; i < PERIODS; i++) {
                            proposal[i + 1] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_cmp[i]) + "        €/MWh";
                        }
                        Bcmp = 0;
                        for (int i = 0; i < PERIODS; i++) {
                            Bcmp = Bcmp + tariff_cmp[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] * (tariff_cmp[i] - min_p[i]) / (1000);
                        }
//                    aux=seller.input_gui.inter(proposal,seller.gui,choices1,0);
//                    aux=seller.input_gui.inter2(tariff_cmp,tariff_rcv,Bcmp,0.0,seller.gui,choices1,0,0,0,pmanager.profile_received_from_buyer[PERIODS]);
                        aux = 0;
//                        for (int i=0; i<PERIODS;i++){
//                        tariff_cmp[i]=Double.valueOf(seller.input_gui.list3[i]);
//                    }

                    } else {
//                    int result = JOptionPane.showConfirmDialog(null, ("\nReceived "+seller.getOpponent().getLocalName()+ " Proposal "+pmanager.n_proposals_received+"\n\n                      Received             Send? \n"+"Price 1:           " + twodecimal.format(tariff_rcv[0])+ "                 "+twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " + twodecimal.format(tariff_rcv[1])+ "                 "+twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " + twodecimal.format(tariff_rcv[2])+ "                 "+twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " + twodecimal.format(tariff_rcv[3])+ "                 "+twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " + twodecimal.format(tariff_rcv[4])+ "                 "+twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " + twodecimal.format(tariff_rcv[5])+ "                 "+twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nBenefit:            " + Math.round(Brcv)+ "                   "+Math.round(Bcmp) + "             €\n\n\n"), seller.getLocalName()+" Send Counter-Proposal", JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
//                String proposal="\n\nReceived "+seller.getOpponent().getLocalName()+ " Proposal "+pmanager.n_proposals_received+"\n\n                      Received             Send? \n"+"Price 1:           " + twodecimal.format(tariff_rcv[0])+ "                 "+twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " + twodecimal.format(tariff_rcv[1])+ "                 "+twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " + twodecimal.format(tariff_rcv[2])+ "                 "+twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " + twodecimal.format(tariff_rcv[3])+ "                 "+twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " + twodecimal.format(tariff_rcv[4])+ "                 "+twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " + twodecimal.format(tariff_rcv[5])+ "                 "+twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nBenefit:            " + twodecimal.format(Brcv/(1e6))+ "                   "+twodecimal.format(Bcmp/(1e6)) + "             M€\n\n\n";
                        String[] proposal = new String[PERIODS + 3];
                        proposal[0] = "Received " + seller.getOpponent().getLocalName() + " Proposal " + pmanager.n_proposals_received;
                        proposal[1] = "                    Received            Send? ";
                        for (int i = 0; i < PERIODS; i++) {
                            proposal[i + 2] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_rcv[i]) + "                 " + twodecimal.format(tariff_cmp[i]) + "        €/MWh";
                        }
                        proposal[PERIODS + 2] = "Benefit:         " + threedecimal.format(Brcv / (1e3)) + "             " + threedecimal.format(Bcmp / (1e3)) + "          k€";
//                aux2=seller.input_gui.inter(proposal,seller.gui,choices2,1);

                        if (pmanager.case4 == 0) {

                            aux2 = seller.input_gui.inter2(tariff_cmp, tariff_rcv, Bcmp, Brcv, seller.gui, choices2, 1, pmanager.n_proposals_received, 0, pmanager.profile_received_from_buyer[PERIODS], sent_history);

                            for (int i = 0; i < PERIODS; i++) {
                                tariff_cmp[i] = Double.valueOf(seller.input_gui.list3[i]);
                            }

                        } else {
                            aux2 = 1;
                            pmanager.case4 = 0;
                        }
                    }
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(tariff_cmp);
                        oos.close();
                        reply.setByteSequenceContent(baos.toByteArray());
                    } catch (IOException ex) {
                        Logger.getLogger(AlternantingOffersProtocol.class.getName());
                    }
                    if (aux == 1 || aux2 == 2) {
                        seller.calculatedscore = (Acmp * 100);
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        myAgent.send(reply);
                        send = "You have terminated the negotiation";
                        seller.getGui().updateLog1("\n" +/*buyer.getLocalName()+*/ "You have terminated the negotiation");
                        seller.input_gui.finish(seller.gui, send);
                        step = 4;
                        break;
                    } else if (aux2 == 0) {
                        seller.calculatedscore = Acmp * 100;
                        seller.getGui().updateLog1("                ");
                        seller.getGui().updateLog1("****************************************************************************************************************");
                        seller.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                        seller.getGui().updateLog1("****************************************************************************************************************");

//                            if (pmanager.str.equals(pmanager.s4)) {
                        seller.getGui().updateLog1("\tAccept Received Proposal at:");
                        for (int i = 0; i < PERIODS; i++) {
                            seller.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[i + PERIODS]) + "kWh");
                        }
                        seller.getGui().updateLog1("Contract Duration: " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) + " days\n\n");
//                                seller.getGui().updateLog1(" ");
//                                seller.getGui().updateLog1("******************************");
                        seller.getGui().updateLog1("Total Benefit computed:     " + threedecimal.format(Bcmp / (1e3)) + " k€");
                        seller.getGui().updateLog1("Total Benefit received:     " + threedecimal.format(Brcv / (1e3)) + " k€");

                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        try {
                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                            oos2.writeObject(tariff_rcv);
                            oos2.close();
                            reply.setByteSequenceContent(baos2.toByteArray());
                        } catch (IOException ex) {
                            Logger.getLogger(AlternantingOffersProtocol.class.getName());
                        }
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                        myAgent.send(reply);
                        seller.getGui().updateLog1("                Sent ACCEPT PROPOSAL  Message");
//                            seller.input_gui.congrat(seller.gui);
                        String[] sendcontract = new String[PERIODS + 1];

                        sendcontract[0] = seller.getOpponent().getLocalName() + " signed a " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) * 24 + "h contract with " + seller.getLocalName();
                        for (int i = 0; i < PERIODS; i++) {
                            sendcontract[i + 1] = "Price " + (i + 1) + ":       " + twodecimal.format(tariff_rcv[i]) + "  €/MWh";
                        }

                        ACLMessage msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        msg_exist.setContent(sendcontract[0]);
                        msg_exist.setOntology("market_ontology");
                        msg_exist.setProtocol("no_protocol");
//                    msg_exist.addReceiver(pmanager.system_agent);
                        msg_exist.addReceiver(pmanager.market_operator);
                        seller.send(msg_exist);
                        String Content = "\n" + sendcontract[0] + "\n\n";
                        for (int i = 0; i < PERIODS; i++) {
                            Content = Content + "          Energy " + (i + 1) + ":       " + twodecimal.format(tariff_rcv[i + PERIODS]) + "  kWh" + "\n";
                        }
                        Content = Content + "\n\nContract Duration: " + (Math.round(pmanager.profile_received_from_buyer[PERIODS]) * 24) + "h";
//                    msg_exist.setContent("\n"+sendcontract[0]+"\n\n\t\t"+sendcontract[1]+"          Energy 1:       " + twodecimal.format(tariff_rcv[6]) + "  kWh"+"\n"+sendcontract[2]+ "          Energy 2:       " + twodecimal.format(tariff_rcv[7]) + "  kWh"+"\n"+sendcontract[3]+ "          Energy 3:       " + twodecimal.format(tariff_rcv[8]) + "  kWh"+"\n"+sendcontract[4]+ "          Energy 4:       " + twodecimal.format(tariff_rcv[9]) + "  kWh"+"\n"+sendcontract[5]+ "          Energy 5:       " + twodecimal.format(tariff_rcv[10]) + "  kWh"+"\n"+sendcontract[6]+ "          Energy 6:       " + twodecimal.format(tariff_rcv[11]) + "  kWh\n\nContract Duration: "+Math.round(pmanager.profile_received_from_buyer[6])*24+"h");
                        msg_exist.setContent(Content);
                        seller.send(msg_exist);
                        MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
                        MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));

                        msg = myAgent.receive(mt1);
                        while (msg == null) {
                            msg = myAgent.receive(mt2);
                            if (msg == null) {
                                msg = myAgent.receive(mt1);
                            }
                        }
                        if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                            msg_exist.setOntology("marketoperator_ontology");
                            msg_exist.setProtocol("negotiation_protocol");
                            msg_exist.setContent("Our deal have been confimed by the Market Operator");
                            msg_exist.addReceiver(seller.getOpponent());
                            seller.send(msg_exist);
                            seller.input_gui.congrat(seller.gui);
                        } else {
                            msg_exist = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                            msg_exist.setOntology("marketoperator_ontology");
                            msg_exist.setProtocol("negotiation_protocol");
                            msg_exist.setContent("Our deal have been rejected by the Market Operator");
                            msg_exist.addReceiver(seller.getOpponent());
                            seller.send(msg_exist);
                            seller.input_gui.decline(seller.gui);
                        }
                        step = 4;
                        break;
                    } else {
                        seller.utilities.add(Dcmp * 100);
//                    System.out.println("\nSeller: Calcula2\n"+100.0*Dcmp);
                        myAgent.send(reply);

//                    if (pmanager.str.equals(pmanager.s4)) {
                        seller.getGui().updateLog1("Sent Proposal to Sell at:" + pmanager.contract);
                        for (int i = 0; i < PERIODS; i++) {
                            seller.getGui().updateLog1("Price " + (i + 1) + " = " + twodecimal.format(tariff_cmp[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_cmp[PERIODS + i]) + "kWh");
                        }
                        seller.getGui().updateLog1("Contract Duration: " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) + " days\n\n");
                        seller.getGui().updateLog1("Total Benefit computed: " + threedecimal.format(Bcmp / (1e3)) + " k€");

//                        seller.getGui().updateLog1("******************************");
//                        seller.getGui().updateLog1("Total Benefit computed: " + twodecimal.format(Bcmp));
//                        seller.getGui().updateLog1("Total Benefit received: " + twodecimal.format(Brcv));
//                        seller.getGui().updateLog1("******************************");
//                    }
//                    else {
//                        seller.getGui().updateLog1("\tSent Proposal to Sell at:  Price 1 = " + twodecimal.format(tariff_cmp[0]) + "€/MWh");
//                        seller.getGui().updateLog1("\tPrice 2 = " + twodecimal.format(tariff_cmp[1]) + "€/MWh");
//                        seller.getGui().updateLog1("\tPrice 3 = " + twodecimal.format(tariff_cmp[2]) + "€/MWh");
//                        seller.getGui().updateLog1("\tPrice 4 = " + twodecimal.format(tariff_cmp[3]) + "€/MWh");
//                        seller.getGui().updateLog1("\tPrice 5 = " + twodecimal.format(tariff_cmp[4]) + "€/MWh");
//                        seller.getGui().updateLog1("\tPrice 6 = " + twodecimal.format(tariff_cmp[5]) + "€/MWh");
//                    }
                        pmanager.n_proposals_sent++;
                        step = 3;
                        block();
                        break;
                    }

                case 3:

                    time = System.currentTimeMillis();
                    msg = myAgent.receive(mt);
                    if (msg == null) {
                        block();
                        break;
                    }

                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        if (msg.hasByteSequenceContent()) {
                            byte[] data = msg.getByteSequenceContent();
                            ByteArrayInputStream bais = new ByteArrayInputStream(data);
                            try {
                                ObjectInputStream ois = new ObjectInputStream(bais);
                                try {
                                    tariff_rcv = (double[]) ois.readObject();

//                                    if (pmanager.str.equals(pmanager.s4)) {
                                    if (pmanager.n_proposals_received == 0) {
                                        Bmin = 0;
                                        Amin = 0;
                                        for (int i = 0; i < min_p.length; i++) {
                                            Bmin = Bmin + min_p[i] * tariff_rcv[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS];
                                            Amin = Amin + (min_p[i] * tariff_rcv[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS]) * pmanager.weight[i];
                                        }
                                        if (pmanager.PreFunction.equals(pmanager.p1)) {
                                            Dmin = Amin;
                                        } else {
                                            if (pmanager.PreFunction.equals(pmanager.p2)) {
                                                Dmin = Bmin;
                                            }
                                        }
                                        if (seller.risk == 1) {
                                            Dmin = 10 * Amin;
                                        }
                                    }
                                    if (pmanager.n_proposals_received >= 0) {
                                        seller.input_gui.offer(seller.gui, 1);
                                    }
                                    seller.getGui().updateLog1("\tReceived Proposal to buy at:");
                                    for (int i = 0; i < PERIODS; i++) {
                                        seller.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[i + PERIODS]) + "kWh");
                                    }
                                    seller.getGui().updateLog1("\tContract Duration: " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) + " days\n");
                                    seller.getGui().updateLog1("\tTotal Benefit received:    " + threedecimal.format(Brcv / (1e3)) + " k€\n\n");

//                                    }
//                                    else {
//                                        seller.getGui().updateLog1("      Received Proposal to buy at:  Price 1 = " + twodecimal.format(tariff_rcv[0]) + "€/MWh");
//                                        seller.getGui().updateLog1("                                    Price 2 = " + twodecimal.format(tariff_rcv[1]) + "€/MWh");
//                                        seller.getGui().updateLog1("                                    Price 3 = " + twodecimal.format(tariff_rcv[2]) + "€/MWh");
//                                        seller.getGui().updateLog1("                                    Price 4 = " + twodecimal.format(tariff_rcv[3]) + "€/MWh");
//                                        seller.getGui().updateLog1("                                    Price 5 = " + twodecimal.format(tariff_rcv[4]) + "€/MWh");
//                                        seller.getGui().updateLog1("                                    Price 6 = " + twodecimal.format(tariff_rcv[5]) + "€/MWh");
//                                        seller.getGui().updateLog1("  ");
//                                    }
                                    pmanager.n_proposals_received++;
                                    if (pmanager.n_proposals_received == 1) {
                                        pmanager.received_history.add(tariff_rcv);
                                    }
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(AlternantingOffersProtocol.class.getName());
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(AlternantingOffersProtocol.class.getName());
                            }
                        }
                        while (seller.counteroffer == 0 && pmanager.n_proposals_sent != 0) {
//                     buyer.gui.stop();

                        }
                        seller.counteroffer = 0;
                        //Computing New Prices
                        if (seller.ES == 1 && !seller.input_gui.tactic.equals(seller.input_gui.sStrategy.getText())) {
                            seller.input_gui.ES(seller.input_gui.Parent);
                        }
                        tariff_cmp = (double[]) pmanager.strategy(tariff_rcv, tariff_cmp);

                        //Computing benefits of received and new - Ready to send - Proposals
                        Brcv = 0;
                        Bcmp = 0;
                        Arcv = 0;
                        Acmp = 0;
                        Drcv = 0;
                        Dcmp = 0;

//                        if (pmanager.str.equals(pmanager.s4)) {
                        for (int i = 0; i < PERIODS; ++i) {
                            Brcv = Brcv + tariff_rcv[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] * (tariff_rcv[i] - min_p[i]) / (1000);
                            Bcmp = Bcmp + tariff_cmp[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] * (tariff_cmp[i] - min_p[i]) / (1000);
                            Arcv = Arcv + (tariff_rcv[i] - pmanager.lim[i]) / (pmanager.initPrice[i] - pmanager.lim[i]) * pmanager.weight[i];
                            Acmp = Acmp + (tariff_cmp[i] - pmanager.lim[i]) / (pmanager.initPrice[i] - pmanager.lim[i]) * pmanager.weight[i];

//                                Arcv=Arcv+(tariff_rcv[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] * (tariff_rcv[i] - min_p[i])/(1000))*weight[i];
//                                Acmp=Acmp+(tariff_cmp[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] *(tariff_cmp[i] -  min_p[i])/(1000))*weight[i];
                        }

                        if (seller.risk == 0 && pmanager.PreFunction.equals(pmanager.p1)) {
                            Drcv = Arcv;
                            Dcmp = Acmp;
                        } else if (seller.risk == 0 && pmanager.PreFunction.equals(pmanager.p2)) {
                            Drcv = Brcv;
                            Dcmp = Bcmp;
                        }
                        if (seller.risk == 1 && pmanager.PreRiskFunction.equals(pmanager.pr1)) {
                            Drcv = Risk.useller(pmanager.lbda, tariff_rcv, seller.input_gui.price_mec, seller.sharing_risk, seller.input_gui.deviation, pmanager.k) / PERIODS;
                            Dcmp = Risk.useller(pmanager.lbda, tariff_cmp, seller.input_gui.price_mec, seller.sharing_risk, seller.input_gui.deviation, pmanager.k) / PERIODS;
                        } else if (seller.risk == 1 && pmanager.PreRiskFunction.equals(pmanager.pr2)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(pmanager.lbda * (tariff_rcv[i] - pmanager.lim[i]) / (seller.input_gui.deviation[i + PERIODS] - seller.input_gui.deviation[i]))) / (1 - Math.exp(pmanager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(pmanager.lbda * (tariff_cmp[i] - pmanager.lim[i]) / (seller.input_gui.deviation[i + PERIODS] - seller.input_gui.deviation[i]))) / (1 - Math.exp(pmanager.lbda))) / PERIODS;
                            }
                        } else if (seller.risk == 1 && pmanager.PreRiskFunction.equals(pmanager.pr3)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(pmanager.lbda * (tariff_rcv[i] - (pmanager.limneg[i] + seller.input_gui.deviation[i + PERIODS] * (1 - seller.sharing_risk))) / (pmanager.initPrice[i] - (pmanager.limneg[i] + seller.input_gui.deviation[i + PERIODS] * (1 - seller.sharing_risk))))) / (1 - Math.exp(pmanager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(pmanager.lbda * (tariff_cmp[i] - (pmanager.limneg[i] + seller.input_gui.deviation[i + PERIODS] * (1 - seller.sharing_risk))) / (pmanager.initPrice[i] - (pmanager.limneg[i] + seller.input_gui.deviation[i + PERIODS] * (1 - seller.sharing_risk))))) / (1 - Math.exp(pmanager.lbda))) / PERIODS;
                            }
                        }

                        seller.utilities.add(Drcv * 100);
//                            System.out.println("\nSeller: Recebe1\n"+100.0*Drcv);
//                            seller.utilities.add(Dcmp*100);
                        System.out.println("\nSeller:\n\nCalculated Score:" + Dcmp + "\nReceived Score:" + Drcv);
//                        }
//                        else {

//                            for (int i = 0; i < 6; ++i) {
//////                                Brcv = Brcv + weight[i] * tariff_rcv[i];
//////                                Bcmp = Bcmp + weight[i] * tariff_cmp[i];
//////                                 System.out.println(" /n contra-proposta "+tariff_rcv[i]+" /n energia " +tariff_rcv[i + 6]+" /n min_p" +min_p[i]+" /n proposta "+tariff_cmp[i] + tariff_cmp[i + 6] + min_p[i]+" /n ");
////                                Brcv = Brcv + tariff_rcv[i + 6] * tariff_rcv[i] - tariff_rcv[i + 6] * min_p[i];
////                                Bcmp = Bcmp + tariff_cmp[i + 6] * tariff_cmp[i] - tariff_cmp[i + 6] * min_p[i];
////                            }
//                        }
                        if (Drcv > Dmin) {
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            myAgent.send(reply);
                            send = "Received proposal does not meet the minimum acceptable level";
                            seller.getGui().updateLog1("Received proposal does not meet the minimum acceptable level");
                            seller.getGui().updateLog1("");
                            seller.getGui().updateLog1("                ");
                            seller.getGui().updateLog1("****************************************************************************************************************");
                            seller.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                            seller.getGui().updateLog1("****************************************************************************************************************");
                            seller.getGui().updateLog1("");
                            seller.input_gui.finish(seller.gui, send);
                            step = 4;  // Withdrawn negotiation
                            break;
                        } else // Teste whether to accept or reject the received proposal
                        if (Drcv < Dcmp || pmanager.n_proposals_received <= 1) {

                            pmanager.timei2 = System.currentTimeMillis();

                            //            if (timei2 == time1) {
                            //                System.out.print("Problem!!!");
                            //            }
//                                pmanager.timei = pmanager.timei2 - pmanager.time1;
//                                System.out.println(" /n " +timei);
                            System.out.println(" \nseller \n timei " + pmanager.timei + " \n deadline " + pmanager.deadlineinternal);

                            // Test number of proposal sent
                            if (pmanager.n_proposals_sent >= 20 || ((pmanager.str.equals(pmanager.s5) || pmanager.str.equals(pmanager.s6)) && pmanager.timei > pmanager.deadlineinternal)) {

                                if (pmanager.n_proposals_sent >= 20) {
                                    send = "Exceeded the maximum number of bids allowed (<7)";
                                    seller.getGui().updateLog1("Exceeded the maximum number of bids allowed (<7)");
                                    seller.input_gui.finish(seller.gui, send);
                                } else {
                                    seller.getGui().updateLog1("Deadline End");
                                    send = "Deadline End";
                                    seller.input_gui.finish(seller.gui, send);
                                }

                                reply = msg.createReply();
                                reply = msg.createReply();
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                myAgent.send(reply);
                                seller.getGui().updateLog1("");
                                seller.getGui().updateLog1("                ");
                                seller.getGui().updateLog1("****************************************************************************************************************");
                                seller.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                                seller.getGui().updateLog1("****************************************************************************************************************");
                                seller.getGui().updateLog1("");

                                step = 4;  // Withdrawn negotiation
                                break;
                            } else {

                                step = 2;
                                break;
                            }
                        } else {
                            System.out.println("Seller " + Brcv + " \n propostas" + (pmanager.n_proposals_received + pmanager.n_proposals_sent));
//                            int result = JOptionPane.showConfirmDialog(null, ("\n\nAccept "+seller.getOpponent().getLocalName()+" Proposal "+pmanager.n_proposals_received+"?\n\n"+"Price 1:           " +twodecimal.format(tariff_rcv[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_rcv[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_rcv[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_rcv[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_rcv[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_rcv[5]) + "        €/MWh\n"+"\nReceived Benefit:      "+Math.round(Brcv)+"   €"+"\nCalculated Benefit:   "+Math.round(Bcmp)+"   €\n\n\n"), seller.getLocalName()+" Finishing Negotiation", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
//                            String proposal="\n\nAccept "+seller.getOpponent().getLocalName()+" Proposal "+pmanager.n_proposals_received+"?\n\n"+"Price 1:           " +twodecimal.format(tariff_rcv[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_rcv[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_rcv[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_rcv[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_rcv[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_rcv[5]) + "        €/MWh\n"+"\nReceived Benefit:      "+twodecimal.format(Brcv/(1e6))+"   M€"+"\nCalculated Benefit:   "+twodecimal.format(Bcmp/(1e6))+"   M€\n\n\n";
                            String[] proposal = new String[PERIODS + 3];
                            proposal[0] = "Accept " + seller.getOpponent().getLocalName() + " Proposal " + pmanager.n_proposals_received + "?";
                            for (int i = 0; i < PERIODS; i++) {
                                proposal[i + 1] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_rcv[i]) + "        €/MWh";
                            }
                            proposal[PERIODS + 1] = "Received Benefit:   " + threedecimal.format(Brcv / (1e3)) + " k€";
                            proposal[PERIODS + 2] = "Calculated Benefit: " + threedecimal.format(Bcmp / (1e3)) + " k€";

//                            aux=seller.input_gui.inter(proposal,seller.gui,choices3,1);
//                        while(seller.counteroffer==0&&pmanager.n_proposals_sent!=0){
////                     buyer.gui.stop();
//                                                    
//                            }
//                            seller.counteroffer=0;
                            aux2 = seller.input_gui.inter2(tariff_cmp, tariff_rcv, Bcmp, Brcv, seller.gui, choices2, 1, pmanager.n_proposals_received, 1, pmanager.profile_received_from_buyer[PERIODS], sent_history);

                            if (aux2 == 1) {
//                                seller.utilities.add(Dcmp*100);
                                pmanager.case4 = 1;
                                step = 2;
                                break;
                            } else if (aux2 == 2) {
                                reply = msg.createReply();
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                myAgent.send(reply);
                                send = "You have terminated the negotiation";
                                seller.getGui().updateLog1("\n" +/*buyer.getLocalName()+*/ "You have terminated the negotiation");
                                seller.input_gui.finish(seller.gui, send);
                                step = 4;
                                break;
                            } else if (aux2 == 0) {
                                // Accept received proposal
                                seller.calculatedscore = (Acmp * 100);
                                seller.getGui().updateLog1("                ");
                                seller.getGui().updateLog1("****************************************************************************************************************");
                                seller.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                                seller.getGui().updateLog1("****************************************************************************************************************");

//                            if (pmanager.str.equals(pmanager.s4)) {
                                seller.getGui().updateLog1("Accept Received Proposal at:");
                                for (int i = 0; i < PERIODS; i++) {
                                    seller.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[i + PERIODS]) + "kWh");
                                }
                                seller.getGui().updateLog1("Contract Duration: " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) + " days\n\n");
//                                seller.getGui().updateLog1(" ");
//                                seller.getGui().updateLog1("******************************");
                                seller.getGui().updateLog1("Total Benefit computed: " + threedecimal.format(Bcmp / (1e3)) + " k€");
                                seller.getGui().updateLog1("Total Benefit received:    " + threedecimal.format(Brcv / (1e3)) + " k€");
//                                seller.getGui().updateLog1("******************************");
//                                seller.getGui().updateLog1(" ");
//                            }
//                            else {
//
//
//                                seller.getGui().updateLog1("ACCEPT Received Proposal:");
//                                seller.getGui().updateLog1("Price 1 = " + twodecimal.format(tariff_rcv[0]) + "€/MWh");
//                                seller.getGui().updateLog1("Price 2 = " + twodecimal.format(tariff_rcv[1]) + "€/MWh");
//                                seller.getGui().updateLog1("Price 3 = " + twodecimal.format(tariff_rcv[2]) + "€/MWh");
//                                seller.getGui().updateLog1("Price 4 = " + twodecimal.format(tariff_rcv[3]) + "€/MWh");
//                                seller.getGui().updateLog1("Price 5 = " + twodecimal.format(tariff_rcv[4]) + "€/MWh");
//                                seller.getGui().updateLog1("Price 6 = " + twodecimal.format(tariff_rcv[5]) + "€/MWh");
//                            }
                                reply = msg.createReply();
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                try {
                                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                    ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                                    oos2.writeObject(tariff_rcv);
                                    oos2.close();
                                    reply.setByteSequenceContent(baos2.toByteArray());
                                } catch (IOException ex) {
                                    Logger.getLogger(AlternantingOffersProtocol.class.getName());
                                }
                                reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                                myAgent.send(reply);
                                seller.getGui().updateLog1("                Sent ACCEPT PROPOSAL  Message");

                                String[] sendcontract = new String[PERIODS + 1];
                                for (int i = 0; i < sendcontract.length - 1; i++) {
                                    sendcontract[i + 1] = proposal[i + 1];
                                }
                                sendcontract[0] = seller.getOpponent().getLocalName() + " signed a " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) * 24 + "h contract with " + seller.getLocalName();

                                ACLMessage msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                msg_exist.setContent(sendcontract[0]);
                                msg_exist.setOntology("market_ontology");
                                msg_exist.setProtocol("no_protocol");
//                    msg_exist.addReceiver(pmanager.system_agent);
                                msg_exist.addReceiver(pmanager.market_operator);
                                seller.send(msg_exist);
                                String Content = "\n" + sendcontract[0] + "\n\n";
                                for (int i = 0; i < PERIODS; i++) {
                                    Content = Content + "          Energy " + (i + 1) + ":       " + twodecimal.format(tariff_rcv[i + PERIODS]) + "  kWh" + "\n";
                                }
                                Content = Content + "\n\nContract Duration: " + (Math.round(pmanager.profile_received_from_buyer[PERIODS]) * 24) + "h";
//                    msg_exist.setContent("\n"+sendcontract[0]+"\n\n\t\t"+sendcontract[1]+"          Energy 1:       " + twodecimal.format(tariff_rcv[6]) + "  kWh"+"\n"+sendcontract[2]+ "          Energy 2:       " + twodecimal.format(tariff_rcv[7]) + "  kWh"+"\n"+sendcontract[3]+ "          Energy 3:       " + twodecimal.format(tariff_rcv[8]) + "  kWh"+"\n"+sendcontract[4]+ "          Energy 4:       " + twodecimal.format(tariff_rcv[9]) + "  kWh"+"\n"+sendcontract[5]+ "          Energy 5:       " + twodecimal.format(tariff_rcv[10]) + "  kWh"+"\n"+sendcontract[6]+ "          Energy 6:       " + twodecimal.format(tariff_rcv[11]) + "  kWh\n\nContract Duration: "+Math.round(pmanager.profile_received_from_buyer[6])*24+"h");
                                msg_exist.setContent(Content);
                                seller.send(msg_exist);
                                MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
                                MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));

                                msg = myAgent.receive(mt1);
                                while (msg == null) {
                                    msg = myAgent.receive(mt2);
                                    if (msg == null) {
                                        msg = myAgent.receive(mt1);
                                    }
                                }
                                if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                                    msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                    msg_exist.setProtocol("negotiation_protocol");
                                    msg_exist.setOntology("marketoperator_ontology");
                                    msg_exist.setContent("Our deal have been confimed by the Market Operator");
                                    msg_exist.addReceiver(seller.getOpponent());
                                    seller.send(msg_exist);
                                    seller.input_gui.congrat(seller.gui);
                                } else {
                                    msg_exist = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                                    msg_exist.setProtocol("negotiation_protocol");
                                    msg_exist.setOntology("marketoperator_ontology");
                                    msg_exist.setContent("Our deal have been rejected by the Market Operator");
                                    msg_exist.addReceiver(seller.getOpponent());
                                    seller.send(msg_exist);
                                    seller.input_gui.decline(seller.gui);
                                }
//                    ACLMessage msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//                    msg_exist.setContent(String.valueOf(tariff_rcv));
//                    msg_exist.setOntology("market_ontology");
//                    msg_exist.setProtocol("no_protocol");
//                    msg_exist.addReceiver(pmanager.system_agent);
//                    myAgent.send(msg_exist);

                                step = 4;
                                break;
                            }
                        }

                    } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        seller.getGui().updateLog1("                ");
                        seller.getGui().updateLog1("****************************************************************************************************************");
                        seller.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                        seller.getGui().updateLog1("****************************************************************************************************************");

                        // check if the data is sent by byte code.
                        if (msg.hasByteSequenceContent()) {
                            byte[] data = msg.getByteSequenceContent();
                            ByteArrayInputStream bais2 = new ByteArrayInputStream(data);
                            try {
                                ObjectInputStream ois2 = new ObjectInputStream(bais2);
                                try {
                                    tariff_rcv = (double[]) ois2.readObject();
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(AlternantingOffersProtocol.class.getName());
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(AlternantingOffersProtocol.class.getName());
                            }
                        }
//                        int result = JOptionPane.showConfirmDialog(null, ("\n\n"+seller.getOpponent().getLocalName()+" Accept Proposal "+pmanager.n_proposals_sent +"\n\n"+"Price 1:           " +twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nFinal Benefit:     "+Math.round(Bcmp)+"   €\n\n\n"), seller.getLocalName()+" Finishing Negotiation", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
//                        String proposal="\n\n"+seller.getOpponent().getLocalName()+" Accept Proposal "+pmanager.n_proposals_sent +"\n\n"+"Price 1:           " +twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nFinal Benefit:     "+twodecimal.format(Bcmp/(1e6))+"   M€\n\n\n";
                        String[] proposal = new String[PERIODS + 2];
                        String[] proposal2 = new String[PERIODS + 2];
                        Bcmp = 0;
                        Brcv = 0;
                        for (int i = 0; i < PERIODS; ++i) {
                            Brcv = Brcv + tariff_rcv[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] * (tariff_rcv[i] - min_p[i]) / (1000);
                            if (pmanager.n_proposals_sent == 0) {
                                tariff_cmp[i] = pmanager.initPrice[i];
                            }
                            Bcmp = Bcmp + tariff_rcv[i + PERIODS] * pmanager.profile_received_from_buyer[PERIODS] * (tariff_cmp[i] - min_p[i]) / (1000);
                        }
                        proposal2[0] = seller.getOpponent().getLocalName() + " Accept Proposal " + pmanager.n_proposals_sent;
                        proposal2[1] = "\nFinal Benefit:  " + threedecimal.format(Bcmp / (1e3)) + "    k€           Energy (kWh)";
                        for (int i = 0; i < PERIODS; i++) {
                            proposal2[i + 2] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_cmp[i]) + "  €/MWh         " + twodecimal.format(tariff_cmp[PERIODS + i]);
                            if (i >= 9) {
                                proposal2[i + 2] = "Price " + (i + 1) + ":         " + twodecimal.format(tariff_cmp[i]) + "  €/MWh         " + twodecimal.format(tariff_cmp[PERIODS + i]);
                            }
                        }

//                    proposal2[0]=seller.getOpponent().getLocalName()+" Accept Proposal "+pmanager.n_proposals_sent;
//                    proposal2[2]="Price 1:           " +twodecimal.format(tariff_cmp[0]) + "  €/MWh         "+twodecimal.format(tariff_cmp[6]);
//                    proposal2[3]="Price 2:           " +twodecimal.format(tariff_cmp[1]) + "                      "+twodecimal.format(tariff_cmp[7]);
//                    proposal2[4]="Price 3:           " +twodecimal.format(tariff_cmp[2]) + "                      "+twodecimal.format(tariff_cmp[8]);
//                    proposal2[5]="Price 4:           " +twodecimal.format(tariff_cmp[3]) + "                      "+twodecimal.format(tariff_cmp[9]);
//                    proposal2[6]="Price 5:           " +twodecimal.format(tariff_cmp[4]) + "                      "+twodecimal.format(tariff_cmp[10]);
//                    proposal2[7]="Price 6:           " +twodecimal.format(tariff_cmp[5]) + "                      "+twodecimal.format(tariff_cmp[11]);
//                    proposal2[1]="Final Benefit:  " +threedecimal.format(Bcmp/(1e3))  + "    k€           Energy (kWh)";
                        seller.input_gui.inter(proposal2, seller.gui, choices4, 0);
                        System.out.println("Seller " + Bcmp + " \n propostas" + (pmanager.n_proposals_received + pmanager.n_proposals_sent));
//                        if (pmanager.str.equals(pmanager.s4)) {
                        seller.getGui().updateLog1("\tReceived ACCEPT Proposal:");
                        for (int i = 0; i < PERIODS; i++) {
                            seller.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[PERIODS + i]) + "kWh");
                        }
                        seller.getGui().updateLog1("\tContract Duration: " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) + " days\n\n");
//                            seller.getGui().updateLog1("******************************");
                        seller.getGui().updateLog1("Total Benefit computed: " + threedecimal.format(Bcmp / (1e3)) + " k€");
                        seller.getGui().updateLog1("Total Benefit received:     " + threedecimal.format(Brcv / (1e3)) + " k€");
//                            seller.getGui().updateLog1("******************************");

//                        }
//                        else {
//                            seller.getGui().updateLog1("\tReceived ACCEPT Proposal:    Price 1 = " + twodecimal.format(tariff_rcv[0]) + "€/MWh");
//                            seller.getGui().updateLog1("                                                             Price 2 = " + twodecimal.format(tariff_rcv[1]) + "€/MWh");
//                            seller.getGui().updateLog1("                                                             Price 3 = " + twodecimal.format(tariff_rcv[2]) + "€/MWh");
//                            seller.getGui().updateLog1("                                                             Price 4 = " + twodecimal.format(tariff_rcv[3]) + "€/MWh");
//                            seller.getGui().updateLog1("                                                             Price 5 = " + twodecimal.format(tariff_rcv[4]) + "€/MWh");
//                            seller.getGui().updateLog1("                                                             Price 6 = " + twodecimal.format(tariff_rcv[5]) + "€/MWh");
//                        }
//                                seller.input_gui.congrat(seller.gui);
                        String[] sendcontract = new String[PERIODS + 1];
                        for (int i = 0; i < sendcontract.length - 1; i++) {
                            sendcontract[i + 1] = proposal[i + 2];
                        }
                        sendcontract[0] = seller.getOpponent().getLocalName() + " signed a " + Math.round(pmanager.profile_received_from_buyer[PERIODS]) * 24 + "h contract with " + seller.getLocalName();

                        ACLMessage msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        msg_exist.setContent(sendcontract[0]);
                        msg_exist.setOntology("market_ontology");
                        msg_exist.setProtocol("no_protocol");
//                    msg_exist.addReceiver(pmanager.system_agent);
                        msg_exist.addReceiver(pmanager.market_operator);
                        seller.send(msg_exist);
                        String Content = "\n" + sendcontract[0] + "\n\n";
                        for (int i = 0; i < PERIODS; i++) {
                            Content = Content + "          Energy " + (i + 1) + ":       " + twodecimal.format(tariff_rcv[i + PERIODS]) + "  kWh" + "\n";
                        }
                        Content = Content + "\n\nContract Duration: " + (Math.round(pmanager.profile_received_from_buyer[PERIODS]) * 24) + "h";
                        msg_exist.setContent(Content);
                        seller.send(msg_exist);
//                  msg = myAgent.receive(mt);
                        MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
                        MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));

                        msg = myAgent.receive(mt1);
                        while (msg == null) {
                            msg = myAgent.receive(mt2);
                            if (msg == null) {
                                msg = myAgent.receive(mt1);
                            }
                        }
                        if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                            msg_exist.setProtocol("negotiation_protocol");
                            msg_exist.setOntology("marketoperator_ontology");
                            msg_exist.setContent("Our deal have been confimed by the Market Operator");
                            msg_exist.addReceiver(seller.getOpponent());
                            seller.send(msg_exist);
                            seller.input_gui.congrat(seller.gui);
                        } else {
                            msg_exist = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                            msg_exist.setProtocol("negotiation_protocol");
                            msg_exist.setOntology("marketoperator_ontology");
                            msg_exist.setContent("Our deal have been rejected by the Market Operator");
                            msg_exist.addReceiver(seller.getOpponent());
                            seller.send(msg_exist);
                            seller.input_gui.decline(seller.gui);
                        }
//                        seller.addBehaviour(seller.new sendproposal());
                        step = 4;
                        break;

                    } else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                        send = "";
                        seller.input_gui.finish(seller.gui, send);
                        seller.getGui().updateLog1("                ");
                        seller.getGui().updateLog1("****************************************************************************************************************");
                        seller.getGui().updateLog1("             **                                 OPPONENT TERMINATED NEGOTIATION                               **");
                        seller.getGui().updateLog1("****************************************************************************************************************");
                    }
            }
        }

        @Override
        public boolean done() {
            return step == 4;
        }
    }

    double roundThreeDecimals(double d) {
        double result = d * 1000;
        result = Math.round(result);
        result = result / 1000;
        return result;
    }
}
