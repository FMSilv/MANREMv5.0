/**
 * Market Buyer Agent Buyer class.
 *
 */
package buying;

/**
 * Java and Jade libraries
 *
 */
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.Date;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import lpmin.lpmin;
import risk.Risk;
import lpsolve.LpSolveException;

public class MarketBuyerAgent {

    private int step = 0;
    long time = 0;
    // The list of known seller agents
    // Private Vector sellerAgents = new Vector();
    private Buyer buyer;
    private double[] sent_history;

//    public BuyerInputGui gui;
    public MarketBuyerAgent(Buyer buyer, BuyerInputGui myGui) {
        this.buyer = buyer;

    }

    /**
     * This method is called by the GUI when the user inserts a new Market Buyer
     * Agent
     *
     * @param title The name of agent
     * @param initPrice The inicial price to buy electricity
     * @param maxPrice The maximum acceptable price to buy electricity
     * @param energy The energy of agent
     * @param deadline The deadline by which to buy the energy
     *
     */
    public void purchase(String title, double[] initPrice, double[] maxPrice, double[] energy, double[] maxEnergy, double[] minEnergy, String str, String pre, String prerisk, int DR, Date deadline, String contract, double[] profile_sent_to_seller, double contractduration, double[] prices_received_from_seller) {
        buyer.addBehaviour(new PurchaseManager(title, initPrice, maxPrice, energy, minEnergy, maxEnergy, str, pre, prerisk, DR, deadline, contract, profile_sent_to_seller, contractduration, prices_received_from_seller));
    }

    private class PurchaseManager extends TickerBehaviour {

        int n_proposals_sent = 0;
        int n_proposals_received = 0;
        int case4 = 0;
        final int PERIODS = buyer.input_gui.PERIODS;
        private String title;
        private double[] initPrice = new double[PERIODS];
        private double[] lim = new double[PERIODS];
        private double[] limneg = new double[PERIODS];
        private double[] energy = new double[PERIODS];
        private double[] minEnergy = new double[PERIODS];
        private double[] maxEnergy = new double[PERIODS];
        private String PreFunction, PreRiskFunction;
        double[] weight = new double[PERIODS];
        private double deadlineinternal;
        private long deadlinetest;
        private String str;
        private String contract;
//        private double[] deviation= Risk.deviation(PERIODS);
        private double[] deviationlim = new double[PERIODS];
        private double[] deltaP = new double[PERIODS];
        private double lbda = 0.5;
        private double[] k = new double[PERIODS];

        private long deadline;
        private int N_ISSUES = 2 * PERIODS;
        private int DR = 0;
        private double CS = 1.0;
        private double[] current_prices_volumes = new double[N_ISSUES];
        private double[] Cf = new double[PERIODS];
        private double[] Cfv = new double[PERIODS];
        private double[] Cf2 = new double[PERIODS];
        private double[] Cf3 = new double[PERIODS];
        double[] V = new double[PERIODS];
        double[] c = new double[PERIODS];
        private long currentTime, time1, timei2;
        private double timei, min, div;
        private double Cf4, Cf5;
        private double[] Cf67 = {0.00, 0.00, 0.00, 0.00, 0.00, 0.00};
        private ArrayList<double[]> received_history = new ArrayList<double[]>();
        private int flag, nticks;
        private long initTime, deltaT;
        double[] tariff_cmp_previous = new double[2 * PERIODS];
        double[] tariff_rcv = new double[2 * PERIODS];

        double i = Math.random();
        double B4i = 2.0; // Beginning of range.
        double B4f = 3.0; // End of range.
        double B4 = B4i + i * (B4f - B4i);
        double j = Math.random();
        double B5i = 0.2; // Beginning of range.
        double B5f = 0.3; // End of range.
        double B5 = B5i + i * (B5f - B5i);
        double w = Math.random();
        int mi = 1; // Beginning of range.
        int M = 3; // End of range.
        int m = (int) (mi + w * (M - mi));

        // The negoctiation strategies
        String s1 = "Compromise";
        String s2 = "Low-Priority Concession";
        String s3 = "Volume Conceder";
        String s4 = "Demand Management";
        String s5 = "Time Conceder";
        String s6 = "Time Boulware";
        String s7 = "Tit-For-Tat";
        String s8 = "Random Tit-For-Tat";
        String s9 = "Intrasigent Priority";
        String s10 = "Inverse Tit-For-Tat Behaviour";
        String s11 = "Negotiation Risk Strategy";
        String p1 = "Additive Function";
        String p2 = "Cost Function";
        String pr1 = "Risk Function";
        String pr2 = "Von Neumann-Morgenstern";
        String pr3 = "Rigorous Risk Function";

        double[] profile_sent_to_seller = new double[PERIODS];
        double contractduration;
        double[] prices_received_from_seller = new double[PERIODS];
        private CostManagerBuyer cost_manager_buyer;
        ArrayList<double[]> choosen_volumes_history = new ArrayList<>();

        private PurchaseManager(String t, double[] ip, double[] mp, double[] en, double[] me, double[] max_e, String s, String pre, String prerisk, int dr, Date d, String contract, double[] profile_sent_to_seller, double contractduration, double[] prices_received_from_seller) {
            super(buyer, 1); // tick every 1/4minute
            title = t;
            initPrice = ip;

            lim = mp;
            energy = en;
            str = s;
            nticks = 0;
            minEnergy = me;
            maxEnergy = max_e;
            DR = dr;
            PreFunction = pre;
            sent_history = new double[2 * PERIODS];
            PreRiskFunction = prerisk;
            deadlinetest = (d.getTime() - System.currentTimeMillis());
//            System.out.println(" \n deadlinetest " +deadlinetest);
            deadlineinternal = (4.1 * deadlinetest) / (24 * 60 * 60 * 1000);
//            for (i=0; i<6; i++){
            this.profile_sent_to_seller = profile_sent_to_seller;
            this.contract = contract;
            this.contractduration = contractduration;
//            }
            this.prices_received_from_seller = prices_received_from_seller;

            deadline = d.getTime();
            initTime = System.currentTimeMillis();
            deltaT = deadline - initTime;
//            time1 = System.currentTimeMillis();
        }

        public void onTick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime > deadline) {
                // Deadline expired
                buyer.getGui().updateLog2("\n Deadline End \n NEGOTIATION TERMINATE!" + title);
                stop();
            } else {
                if (nticks == 0) {
                    nticks = 1;
                    myAgent.addBehaviour(new MarketNegotiator(this, lim, maxEnergy));
                }
            }
        }

        // Strategy Handler
        public double[] strategy(double[] tariff_rcv, double[] tariff_cmp_previous) {
            double temp1, temp2;
            double temp3, temp4;
            double energyTotal = 0.0;
            DecimalFormat twodecimal = new DecimalFormat("0.00");

            double vototal = 0, cftotal = 0, v = 0;
//            double Cf1 = 0.1667;
            double Cf1 = 0.7;
            double arisk = 0.0;
            double brisk = 0.0;

            if (n_proposals_sent == 0) {
                time1 = System.currentTimeMillis();
            }
            if (DR == 1) {
                CS = 1;
            }

//            System.out.print("estrategia"+ str);
            //Initial Prices -- Initial Proposal
            if (flag == 0 && !str.equals(s4)) {
                for (int i = 0; i < initPrice.length; ++i) {
                    current_prices_volumes[i] = initPrice[i];
                    sent_history[i] = initPrice[i];
                    current_prices_volumes[i + PERIODS] = energy[i];
                    buyer.volumes[0][i] = energy[i];
                    limneg[i] = lim[i];
                    tariff_rcv[i] = prices_received_from_seller[i];
                    tariff_rcv[i + PERIODS] = energy[i];
                    if (buyer.risk == 1) {
                        k[i] = -1 / lbda * (Math.log(0.01) / (limneg[i] - initPrice[i]));
                    }
//                    System.out.println(" \n"+ i + "  " +energy[i] +" \n ");
                }
                if (DR == 1) {
                    for (int i = 0; i < PERIODS; i++) {
                        V[i] = current_prices_volumes[i + PERIODS];
                        c[i] = tariff_rcv[i];
                        if (n_proposals_received == 0) {
                            c[i] = prices_received_from_seller[i];
                        }
                    }

                    try {
                        V = new lpmin().execute(V, c, m, this.minEnergy, this.maxEnergy, PERIODS);

                    } catch (LpSolveException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < PERIODS; i++) {
                        current_prices_volumes[i + PERIODS] = Double.valueOf(twodecimal.format(V[i]).replace(",", "."));
                    }
                }
                buyer.received_history.add(tariff_rcv);
                flag = 1;
            } else {
                if (n_proposals_received > 1) {
                    buyer.received_history.add(tariff_rcv);
                    for (int i = 0; i < initPrice.length; ++i) {
                        if (current_prices_volumes[i + PERIODS] > tariff_rcv[i + PERIODS]) {
                            if (tariff_rcv[i + PERIODS] > minEnergy[i]) {
                                current_prices_volumes[i + PERIODS] = tariff_rcv[i + PERIODS];
                            } else {

//                                
//                                buyer.getGui().updateLog1("Seller don't have enough energy to supply");
//                                buyer.getGui().updateLog1("");
//                                buyer.getGui().updateLog1("****************************************************************************************************************");
//                                buyer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
//                                buyer.getGui().updateLog1("****************************************************************************************************************");
                            }

                        }
                    }
                    if (DR == 1) {
                        for (int i = 0; i < PERIODS; i++) {
                            V[i] = current_prices_volumes[i + PERIODS];
                            c[i] = tariff_rcv[i];
                            if (n_proposals_received == 0) {
                                c[i] = prices_received_from_seller[i];
                            }
                        }

                        try {
                            V = new lpmin().execute(V, c, m, this.minEnergy, this.maxEnergy, PERIODS);

                        } catch (LpSolveException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < PERIODS; i++) {
                            current_prices_volumes[i + PERIODS] = Double.valueOf(twodecimal.format(V[i]).replace(",", "."));
                        }
                    }
//                       time=System.currentTimeMillis();               
                }
                if (buyer.risk == 1) {

//                     for (int i=0;i < PERIODS; ++i){
//                         arisk=lim[i]-current_prices_volumes[i];
////                    deviationlim[i]=Math.min(arisk,deviation[i]);
//                            }
//                     deltaP=Risk.deltaPb(lbda, current_prices_volumes, limneg, buyer.sharing_risk, buyer.input_gui.deviation,k);
                    for (int i = 0; i < PERIODS; ++i) {
                        deltaP[i] = 100;
                        brisk = lim[i] - current_prices_volumes[i];
                        limneg[i] = current_prices_volumes[i] + Math.min(Math.abs(deltaP[i]), brisk);
                    }
                } else {
                    for (int i = 0; i < PERIODS; ++i) {
                        limneg[i] = lim[i];
                    }
                }
                vototal = 0;
                for (int i = 0; i < PERIODS; ++i) {
                    vototal = vototal + current_prices_volumes[PERIODS + i];
                }
                cftotal = 0;
                for (int i = 0; i < PERIODS; ++i) {
                    weight[i] = current_prices_volumes[PERIODS + i] / vototal;
                    cftotal = cftotal + 1 / weight[i];
                }
//            cftotal=1/weight[0]+1/weight[1]+1/weight[2]+1/weight[3]+1/weight[4]+1/weight[5];
                System.out.println("\nbuyer\n");
                for (int i = 0; i < PERIODS; i++) {
                    sent_history[i] = current_prices_volumes[i];
                    Cf[i] = (PERIODS / 6) * (Cf1 / 0.1667) * (1 / weight[i]) / cftotal;
                    weight[i] = roundThreeDecimals(weight[i]);
                    System.out.println("peso " + i + ": " + weight[i] + "\n");
                }
//                if (DR==1 && !str.equals(s4)){
//                    CostManagerBuyer cost_manager_buyer = new CostManagerBuyer(tariff_cmp_previous.clone(), tariff_rcv.clone(), minEnergy, maxEnergy, lim, choosen_volumes_history,1);
//
//                if (prices_received_from_seller != null) {
//
//                    if (checkEmptyTariff(tariff_rcv)) {
//                        cost_manager_buyer.setPublicity(prices_received_from_seller, profile_sent_to_seller);
//                    }
//                    if (checkEmptyTariff(tariff_cmp_previous)) {
//                        cost_manager_buyer.setPreviousCmp(initPrice, profile_sent_to_seller);
//                    }
//                    this.tariff_cmp_previous = current_prices_volumes.clone();
//                }
//                current_prices_volumes = cost_manager_buyer.execute();
//                choosen_volumes_history.add(Arrays.copyOfRange(current_prices_volumes, 6, 12));
//                }
                // The different strategies
                if (str.equals(s1)) {

                    // -----<<< Concession Making Strategy >>>-----
                    if (DR != 1 || n_proposals_sent > 0) {
                        for (int j = 0; j < PERIODS; ++j) {
                            temp1 = Cf1 * CS * (limneg[j] - current_prices_volumes[j]);
                            temp2 = current_prices_volumes[j] + temp1;
                            current_prices_volumes[j] = temp2;
//                        current_prices_volumes[j + 6] = energy[j];
//                        System.out.println(" /n " +step+" /n " +lim[j]+current_prices_volumes[j]+" /n ");
                        }
                    }
                }// end if s1
                else {
                    if (str.equals(s2)) {

//                 for (int i=0;i < PERIODS; ++i){
//                   vototal=vototal+current_prices_volumes[PERIODS+i];  
//                 } 
//                for (int i=0;i < PERIODS; ++i){
//                   weight[i] = current_prices_volumes[PERIODS+i] / vototal;
//                   cftotal=cftotal+1/weight[i];
//                 } 
//                 System.out.println("\nbuyer\n");
//            for (int i = 0; i < PERIODS; i++) {
//                Cf[i]=(1/weight[i])/cftotal;
//                weight[i] = roundThreeDecimals(weight[i]);
//                System.out.println("peso "+i+": "+weight[i]+"\n");
//            } 
                        // -----<<< Low-Priority Concession >>>-----
                        for (int j = 0; j < PERIODS; ++j) {
                            temp1 = CS * Cf[j] * (limneg[j] - current_prices_volumes[j]);
                            temp2 = current_prices_volumes[j] + temp1;
                            current_prices_volumes[j] = temp2;
                            //adicionado
//                            current_prices_volumes[j + 6] = energy[j];
                        }
                    }// end if s2
                    else {
                        if (str.equals(s3)) {

                            v = -Math.log(Cf1) * energy.length;
//                            System.out.println(" v "+ v);

                            // -----<<< E-R Concession Strategy >>>-----
                            for (int i = 0; i < energy.length; ++i) {
                                energyTotal = energyTotal + current_prices_volumes[i + energy.length];
                            }
                            for (int j = 0; j < energy.length; ++j) {
                                temp3 = current_prices_volumes[j + energy.length] / energyTotal;
                                temp4 = Math.exp(-v * temp3);
                                Cfv[j] = temp4;
                                temp1 = CS * Cfv[j] * (limneg[j] - current_prices_volumes[j]);
                                temp2 = current_prices_volumes[j] + temp1;
                                current_prices_volumes[j] = temp2;
//                                current_prices_volumes[j + 6] = energy[j];
                            }
                        }// end if s3
                        else {
                            if (str.equals(s4)) {

                                //-----<<< Demand Management Strategy >>>-----
                                CostManagerBuyer cost_manager_buyer = new CostManagerBuyer(tariff_cmp_previous.clone(), tariff_rcv.clone(), minEnergy, maxEnergy, limneg, choosen_volumes_history, 0);

                                if (prices_received_from_seller != null) {

                                    if (checkEmptyTariff(tariff_rcv)) {
                                        cost_manager_buyer.setPublicity(prices_received_from_seller, profile_sent_to_seller);
                                    }
                                    if (checkEmptyTariff(tariff_cmp_previous)) {
                                        cost_manager_buyer.setPreviousCmp(initPrice, profile_sent_to_seller);
                                    }
                                    this.tariff_cmp_previous = current_prices_volumes.clone();
                                }
                                current_prices_volumes = cost_manager_buyer.execute();
                                choosen_volumes_history.add(Arrays.copyOfRange(current_prices_volumes, PERIODS, 2 * PERIODS));

                            }// end if s4
                            else {
                                if (str.equals(s5)) {
                                    // Conceder, Time Concession Strategy:
                                    double k = 0.1;
                                    time = System.currentTimeMillis() - time;
//                                timei2 = System.currentTimeMillis()-time;
                                    //            if (timei2 == time1) {
                                    //                System.out.print("Problem!!!");
                                    //            }
//                                timei = timei2 - time1;
                                    timei = timei + time;
//                                System.out.println(" /n " +timei);
//                                timei++;
                                    System.out.println(" \n buyer times: \n timei " + timei + " \n timei2 " + timei2 + " \n time1 " + time1);
                                    if (timei > deadlineinternal) {
                                        step = 4;
                                    } else {
                                        min = Math.min(timei, deadlineinternal);
                                        div = min / deadlineinternal;
                                        Cf4 = k + (1 - k) * Math.pow(div, (1 / B4));
                                        for (int j = 0; j < initPrice.length; ++j) {
                                            temp1 = CS * Cf4 * (limneg[j] - current_prices_volumes[j]);
                                            temp2 = current_prices_volumes[j] + temp1;
                                            current_prices_volumes[j] = temp2;

                                        }
                                    }
                                } else {
                                    // Boulware, Time Concession Strategy:
                                    if (str.equals(s6)) {
                                        double k = 0.1;
//                                timei2 = System.currentTimeMillis()-time;
                                        time = System.currentTimeMillis() - time;
                    //            if (timei2 == time1) {
                                        //                System.out.print("Problem!!!");
                                        //            }
//                                time1++;
                                        timei = timei + time;
//                                timei = timei2 - time1;
//                                System.out.println(" /n " +timei);
//                                System.out.println(" /n timei " +timei+" /n timei2 " +timei2+" /n time1 " +time1);
                                        if (timei > deadlineinternal) {
                                            step = 4;
                                        } else {
                                            min = Math.min(timei, deadlineinternal);
                                            div = min / deadlineinternal;
                                            Cf5 = k + (1 - k) * Math.pow(div, (1 / B5));
                                            for (int j = 0; j < initPrice.length; ++j) {
                                                temp1 = CS * Cf5 * (limneg[j] - current_prices_volumes[j]);
                                                temp2 = current_prices_volumes[j] + temp1;
                                                current_prices_volumes[j] = temp2;

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
                                                if (received_history.size() == 1) {
                                                    temp1 = CS * Cf[j] * (limneg[j] - current_prices_volumes[j]);
                                                    temp2 = current_prices_volumes[j] + temp1;
                                                    current_prices_volumes[j] = temp2;
                                                } else {
                                                    double max = Math.max(((received_history.get(received_history.size() - 2)[j] / received_history.get(received_history.size() - 1)[j]) * current_prices_volumes[j]), initPrice[j]);
                                                    temp1 = Math.min(max, limneg[j]);
                                                    current_prices_volumes[j] = temp1;
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
                                                    if (received_history.size() == 1) {
                                                        temp1 = CS * Cf[j] * (limneg[j] - current_prices_volumes[j]);
                                                        temp2 = current_prices_volumes[j] + temp1;
                                                        current_prices_volumes[j] = temp2;
                                                    } else {
                                                        double max = Math.max(current_prices_volumes[j] + (received_history.get(received_history.size() - 2)[j] - received_history.get(received_history.size() - 1)[j]) + Math.pow(- 1, s) * m, initPrice[j]);
                                                        temp1 = Math.min(max, limneg[j]);
                                                        current_prices_volumes[j] = temp1;
                                                    }
                                                }
                                            } else {
                                                if (str.equals(s9)) {
                                                    // -----<<< Intrasigent Priority Concession >>>-----
//                 for (int i=0;i < PERIODS; ++i){
//                   vototal=vototal+current_prices_volumes[PERIODS+i];  
//                 } 
//                for (int i=0;i < PERIODS; ++i){
//                   weight[i] = current_prices_volumes[PERIODS+i] / vototal;
//                   cftotal=cftotal+1/weight[i];
//                 } 
//            System.out.println("\nbuyer\n");
//            for (int i = 0; i < PERIODS; i++) {
//                Cf[i]=(1/weight[i])/cftotal;
//                weight[i] = roundThreeDecimals(weight[i]);
//                System.out.println("peso "+i+": "+weight[i]+"\n");
//            } 
                                                    intrasigentWeights();

                                                    for (int j = 0; j < PERIODS; ++j) {
                                                        temp1 = CS * Cf[j] * (limneg[j] - current_prices_volumes[j]);
                                                        temp2 = current_prices_volumes[j] + temp1;
                                                        current_prices_volumes[j] = temp2;
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
                                                                temp1 = CS * Cf[j] * (lim[j] - current_prices_volumes[j]);
                                                                temp2 = current_prices_volumes[j] + temp1;
                                                                current_prices_volumes[j] = temp2;
                                                            } else {
                                                                if (i < 6) {
                                                                    for (i = 0; i < initPrice.length; ++i) {
                                                                        aux[i] = (received_history.get(received_history.size() - 2)[i] / received_history.get(received_history.size() - 1)[i]);
                                                                        a = a + aux[i];
                                                                    }
                                                                    average = a / initPrice.length;
                                                                }
                                                                aux[j] = average - (aux[j] - average);
                                                                double max = Math.max((aux[j] * current_prices_volumes[j]), initPrice[j]);
//                                        System.out.println("\n -2: "+received_history.get(received_history.size() - 2)[j]+"\n -1: "+received_history.get(received_history.size() - 1)[j]);
                                                                temp1 = Math.min(max, lim[j]);
                                                                current_prices_volumes[j] = temp1;
                                                            }
                                                        }
                                                    } else {
                                                        if (str.equals(s11)) {
                                                            int epsilon = 32;
                                                            for (int j = 0; j < initPrice.length; ++j) {
//                        System.out.println(" /n " +step+" /n " +lim[j]+currentPrice[j]+" /n ");
                                                                temp1 = Cf1 * (limneg[j] - current_prices_volumes[j]) * Math.pow((1.0 - ((Math.pow(buyer.input_gui.deviation[j + PERIODS] * buyer.sharing_risk, 2)) / (initPrice[j]))), epsilon * lbda);
                                                                temp2 = current_prices_volumes[j] + temp1;
                                                                current_prices_volumes[j] = temp2;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return current_prices_volumes;
        } // end strategy

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

        public String getStr() {
            return str;
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
    } // End Purchase Manager

    /**
     * *********************************************
     *
     * Implementation of Demand Management Strategy
     *
     * *********************************************
     *
     */
    public class CostManagerBuyer {

        final int PERIODS = buyer.input_gui.PERIODS;
        private HashMap<Double, ArrayList<double[]>> cost_list = new HashMap<>();
        private final Double volume_addition_delta = 3.5;
        private final Double volume_sum_delta_search = 2.5;
        private double[] current_prices = new double[PERIODS];
        private double[] volumes_limits_min;
        private double[] volumes_limits_max;
        private double[] seller_current_prices_volumes;
        private double[] buyer_previous_prices_volumes;
        private double[] buyer_current_volumes;
        private final double[] prices_limits_max;
        private int aux;
        ArrayList<double[]> volumes_history = new ArrayList<>();

        public CostManagerBuyer(double[] buyer_previous_prices_volumes, double[] seller_current_prices_volumes, double[] volumes_limits_min, double[] volumes_limits_max, double[] prices_limits_max, ArrayList<double[]> choosen_volumes_history, int aux) {

            this.prices_limits_max = prices_limits_max;
            this.volumes_limits_min = volumes_limits_min;
            this.volumes_limits_max = volumes_limits_max;
            this.seller_current_prices_volumes = seller_current_prices_volumes;
            this.buyer_previous_prices_volumes = buyer_previous_prices_volumes;
            this.volumes_history = choosen_volumes_history;
            this.aux = aux;

        }

        public double[] execute() {
//            if (aux==0){
            calculateNewPrices();
//            }
            createDeltaCostList();

            return chooseCostFromList();
        }

        //-----<<< Creates a list of possible costs and respective volumes >>>-----
        private void createDeltaCostList() {

            double[] volumes = new double[PERIODS];
            for (int j = 0; j < volumes.length; j++) {
                volumes[j] = this.buyer_previous_prices_volumes[j + PERIODS];
            }

            HashMap<Double, ArrayList<double[]>> aux = new HashMap<>();
            setCostList(aux);
            createDeltaCostListAux(volumes);
            if (getCostList().isEmpty()) {
                buyer.getGui().updateLog1("Internal error");
                System.exit(0);
            }
        }

        //-----<<< Creates an auxiliar list of possible benefits and respective prices in the range of minimum and maximum volumes >>>-----
        private void createDeltaCostListAux(double[] volumes) {

            double sum_previous_volumes = calculateVolumeSum(volumes);
            double[] volumes_aux = new double[PERIODS];

            for (double a = getVolumesLimMin()[0]; a <= getVolumesLimMax()[0]; a = a + volume_addition_delta) {
                for (double b = getVolumesLimMin()[1]; b <= getVolumesLimMax()[1]; b = b + volume_addition_delta) {
                    for (double c = getVolumesLimMin()[2]; c <= getVolumesLimMax()[2]; c = c + volume_addition_delta) {
                        for (double d = getVolumesLimMin()[3]; d <= getVolumesLimMax()[3]; d = d + volume_addition_delta) {
                            for (double e = getVolumesLimMin()[4]; e <= getVolumesLimMax()[4]; e = e + volume_addition_delta) {
                                for (double f = getVolumesLimMin()[5]; f <= getVolumesLimMax()[5]; f = f + volume_addition_delta) {
                                    volumes_aux[0] = a;
                                    volumes_aux[1] = b;
                                    volumes_aux[2] = c;
                                    volumes_aux[3] = d;
                                    volumes_aux[4] = e;
                                    volumes_aux[5] = f;

                                    Boolean already_sent = false;

                                    for (int i = 0; i < volumes_history.size(); i++) {
                                        if (Arrays.equals(volumes_history.get(i), volumes_aux)) {
                                            already_sent = true;
                                        }
                                    }
                                    double sum_volumes_aux = calculateVolumeSum(volumes_aux);

                                    if (!already_sent && (sum_volumes_aux > (sum_previous_volumes - this.volume_sum_delta_search)) && (sum_volumes_aux < (sum_previous_volumes + this.volume_sum_delta_search))) {

                                        double c_aux = calculateCostVolume(volumes_aux);
                                        addToCostList(c_aux, volumes_aux);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //-----<<< Calculates the cost of the buyer agent >>>----- 
        private double calculateCostVolume(double[] volumes) {

            double cost = 0.0;
            for (int i = 0; i < PERIODS; i++) {

                double volume = volumes[i];
                double price = seller_current_prices_volumes[i];
                double cost_2 = (price * volume);
                cost = cost + cost_2;
            }
            return roundTwoDecimals(cost);
        }

        //-----<<< Rounds the value of obtained cost two decimals >>>-----
        public double roundTwoDecimals(double num) {
            double result = num * 100;
            result = Math.round(result);
            result = result / 100;
            return result;
        }

        //-----<<< Adds possible costs and respective volumes to benefit list >>>----- 
        private void addToCostList(double cost, double[] volumes) {

            if (getCostList().containsKey(cost)) {
                getCostList().get(cost).add(volumes.clone());
            } else {
                ArrayList<double[]> list = new ArrayList<>();
                list.add(volumes.clone());
                getCostList().put(cost, list);
            }
        }

        //-----<<< Choose the minimum cost and respective volumes from the list >>>-----
        private double[] chooseCostFromList() {

            double[] opponents_prices = new double[PERIODS];
            for (int j = 0; j < opponents_prices.length; j++) {
                opponents_prices[j] = this.seller_current_prices_volumes[j];
            }
            Object[] keys_array = new Object[getCostList().keySet().size()];
            keys_array = getCostList().keySet().toArray();
            Arrays.sort(keys_array);

            double[] new_volumes = new double[PERIODS];
            double sum_cost_minimum = Double.MAX_VALUE;

            //-----<<< Criterion of choice for Volumes - Minimum Cost >>>-----
            for (int i = 0; i < keys_array.length; i++) {
                double sum_cost_aux = 0.0;
                for (int j = 0; j < getCostList().get(keys_array[i]).size(); j++) {
                    for (int z = 0; z < new_volumes.length; z++) {
                        sum_cost_aux = sum_cost_aux + getCostList().get(keys_array[i]).get(j)[z] * opponents_prices[z];
                    }
                    if (sum_cost_aux < sum_cost_minimum) {
                        sum_cost_minimum = sum_cost_aux;
                        new_volumes = getCostList().get(keys_array[i]).get(j);
                    }
                }
            }

            double[] prices_volumes = new double[2 * PERIODS];

            for (int i = 0; i < PERIODS; i++) {
                prices_volumes[i] = current_prices[i];
                prices_volumes[i + PERIODS] = new_volumes[i];
            }
            return prices_volumes;
        }

        //-----<<< Calculates the buyers new prices >>>-----      
        private void calculateNewPrices() {

            for (int i = 0; i < this.current_prices.length; i++) {
                if (aux == 0) {
                    current_prices[i] = buyer_previous_prices_volumes[i] + (0.07 * buyer_previous_prices_volumes[i]);
                }
                current_prices[i] = buyer_previous_prices_volumes[i];
            }
            for (int i = 0; i < this.prices_limits_max.length; i++) {
                if (this.current_prices[i] > this.prices_limits_max[i]) {
                    this.current_prices[i] = this.prices_limits_max[i];
                }
            }
        }

        //-----<<< Calculates the total Volume >>>-----
        private double calculateVolumeSum(double[] volumes) {

            double sum = 0.0;
            for (int i = 0; i < volumes.length; i++) {
                sum = sum + volumes[i];
            }
            return sum;
        }

        private void setPublicity(double[] publicity_prices, double[] publicity_volumes) {
            for (int i = 0; i < publicity_prices.length; i++) {
                this.seller_current_prices_volumes[i] = publicity_prices[i];
                this.seller_current_prices_volumes[i + PERIODS] = publicity_volumes[i];
            }

        }

        private void setPreviousCmp(double[] publicity_prices, double[] publicity_volumes) {
            for (int i = 0; i < publicity_prices.length; i++) {
                this.buyer_previous_prices_volumes[i] = publicity_prices[i];
                this.buyer_previous_prices_volumes[i + PERIODS] = publicity_volumes[i];
            }

        }

        public HashMap<Double, ArrayList<double[]>> getCostList() {
            return cost_list;
        }

        public void setCostList(HashMap<Double, ArrayList<double[]>> cost_list) {
            this.cost_list = cost_list;
        }

        private double[] getVolumesLimMin() {
            return this.volumes_limits_min;
        }

        private double[] getVolumesLimMax() {
            return this.volumes_limits_max;
        }
    }// End Cost Manager Buyer

    private class MarketNegotiator extends Behaviour {

        final int PERIODS = buyer.input_gui.PERIODS;
        int N_ISSUES = PERIODS;
        private String title = "Energy Market";
        private String send = "";
        private PurchaseManager manager;
        private double[] max_p = new double[N_ISSUES];
        private double[] max_e = new double[N_ISSUES];
        private AID bestSeller; // The seller agent who provides the best offer
        private int bestPrice; // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private int countproposal = 0;
        private Date d1;
        private double Crcv = 0.0, Ccmp = 0.0, Arcv = 0.0, Acmp = 0.0, Drcv = 0.0, Dcmp = 0.0;

        double[] tariff_rcv = new double[N_ISSUES * 2];
        double[] tariff_cmp = new double[N_ISSUES * 2];
        // double[] weight = {0.40, 0.50, 0.10};
        double[] weight = new double[N_ISSUES];
        private final double[] vo;
        double vototal = 0;
        ACLMessage msg = null;
        ACLMessage reply = null;
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        double Cmax = 0.0, Amax = 0.0, Dmax = 0.0;

        public MarketNegotiator(PurchaseManager m, double[] maxp, double[] maxe) {
            super(null);

            manager = m;
            max_p = maxp;
            max_e = maxe;

            this.vo = m.energy;
// Pesos definidos pelo volume de compra
            for (int i = 0; i < PERIODS; ++i) {
                vototal = vototal + vo[i];
            }
            for (int i = 0; i < PERIODS; ++i) {
                weight[i] = vo[i] / vototal;
                weight[i] = roundThreeDecimals(weight[i]);
            }

        }

        public void action() {

            //Round decimal numbers
            int sair = 0;

            int aux = -1, aux2 = -1;
            String[] choices1 = {"Send", "Cancel"};
            String[] choices2 = {"Accept", "Send", "New", "Withdraw"};
            String[] choices21 = {"Accept", "Send", "Withdraw"};
            String[] choices3 = {"Accept", "Cancel"};
            String[] choices4 = {"OK"};
            DecimalFormat twodecimal = new DecimalFormat("0.00");
            DecimalFormat threedecimal = new DecimalFormat("0.000");

            switch (step) {

                case 0:
                    // Checks the maximum cost of buyer agent
                    Cmax = 0;
                    Amax = 0;
                    for (int i = 0; i < max_p.length; i++) {
                        Cmax = Cmax + max_p[i] * max_e[i] * manager.contractduration;
                        Amax = Amax + (max_p[i] * max_e[i] * manager.contractduration) * weight[i];
                    }
                    if (manager.PreFunction.equals(manager.p1)) {
                        Dmax = Amax;
                    } else {
                        if (manager.PreFunction.equals(manager.p2)) {
                            Dmax = Cmax;
                        }
                    }
                    if (buyer.risk == 1) {
                        Dmax = Cmax;

                    }

                    manager.n_proposals_sent = 0;
                    msg = myAgent.receive(mt);

                    if (msg == null) {
                        msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setOntology("market_ontology");
                        msg.setProtocol("negotiation_protocol");
                        msg.setContent("init_negotiation");
                        msg.addReceiver(buyer.getOpponent());
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

                        buyer.getGui().updateLog1("****************************************************************************************************************");
                        buyer.getGui().updateLog1("             **                                                     STARTING NEGOTIATION                                                **");
                        buyer.getGui().updateLog1("****************************************************************************************************************");
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
                        buyer.getGui().updateLog1("****************************************************************************************************************");
                        buyer.getGui().updateLog1("             **                                                     STARTING NEGOTIATION                                                **");
                        buyer.getGui().updateLog1("****************************************************************************************************************");
                        step = 2;
                        break;
                    }

                case 2:

                    reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                    if (manager.n_proposals_received == 0) {
//                        time=System.currentTimeMillis();
                        while (buyer.gui.counteroffer == 0 && manager.n_proposals_sent != 0) {
//                          buyer.gui.stop();
                        }
                        buyer.gui.counteroffer = 0;
                        if (buyer.ES == 1 && !buyer.input_gui.tactic.equals(buyer.input_gui.sStrategy.getText())) {
                            buyer.input_gui.ES(buyer.input_gui.Parent);
                        }

                        tariff_cmp = (double[]) manager.strategy(tariff_rcv, tariff_cmp);

                        manager.n_proposals_received++;
                    }

//                    try {
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        ObjectOutputStream oos = new ObjectOutputStream(baos);
//                        oos.writeObject(tariff_cmp);
//                        oos.close();
//                        reply.setByteSequenceContent(baos.toByteArray());
//                    } catch (IOException ex) {
//                        Logger.getLogger(MarketNegotiator.class.getName());
//                    }
                    if (manager.n_proposals_sent < 1 && manager.n_proposals_received < 1) {
//                    int result = JOptionPane.showConfirmDialog(null, ("\n\n          Send First Proposal? \n\n"+"Price 1:           " +twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_cmp[5]) + "        €/MWh\n\n\n"), buyer.getLocalName()+" Starting Negotiation", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
//                   Object proposal="\n\n          Send First Proposal? \n\n"+"Price 1:           " +twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_cmp[5]) + "        €/MWh\n\n\n";
                        String[] proposal = new String[PERIODS + 1];
                        proposal[0] = "          Send First Proposal? ";
                        for (int i = 0; i < PERIODS; i++) {
                            proposal[i + 1] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_cmp[i]) + "        €/MWh";
                        }
                        aux = -1;
                        aux2 = -1;
                        Ccmp = 0;
                        Crcv = 0;
                        for (int i = 0; i < PERIODS; i++) {
                            Ccmp = Ccmp + tariff_cmp[i + PERIODS] * tariff_cmp[i] * manager.contractduration / (1000);
                        }
//                    aux=buyer.input_gui.inter(proposal,buyer.gui,choices1,0);

//                    buyer.utilities.add(100.0);
//                                                while(buyer.gui.counteroffer==0&&manager.n_proposals_sent!=0){
////                     buyer.gui.stop();
//                                                    
//                            }
//                            buyer.gui.counteroffer=0;
                        aux = buyer.input_gui.inter2(tariff_cmp, tariff_rcv, Ccmp, 0.0, buyer.gui, choices1, 0, 0, 0, sent_history);

                        for (int i = 0; i < 2 * PERIODS; i++) {
                            tariff_cmp[i] = Double.valueOf(buyer.input_gui.list3[i]);
                        }

                    } else {

//                    int result = JOptionPane.showConfirmDialog(null, ("\nReceived "+buyer.getOpponent().getLocalName()+ " Proposal "+manager.n_proposals_received+"\n\n                      Received             Send? \n"+"Price 1:           " + twodecimal.format(tariff_rcv[0])+ "                 "+twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " + twodecimal.format(tariff_rcv[1])+ "                 "+twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " + twodecimal.format(tariff_rcv[2])+ "                 "+twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " + twodecimal.format(tariff_rcv[3])+ "                 "+twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " + twodecimal.format(tariff_rcv[4])+ "                 "+twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " + twodecimal.format(tariff_rcv[5])+ "                 "+twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nCost:               " + Math.round(Crcv)+ "                "+Math.round(Ccmp) + "             €\n\n\n"), buyer.getLocalName()+" Send Counter-Proposal "+(manager.n_proposals_sent+1), JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
//                Object proposal="\n\nReceived "+buyer.getOpponent().getLocalName()+ " Proposal "+manager.n_proposals_received+"\n\n                      Received             Send? \n"+"Price 1:           " + twodecimal.format(tariff_rcv[0])+ "                 "+twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " + twodecimal.format(tariff_rcv[1])+ "                 "+twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " + twodecimal.format(tariff_rcv[2])+ "                 "+twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " + twodecimal.format(tariff_rcv[3])+ "                 "+twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " + twodecimal.format(tariff_rcv[4])+ "                 "+twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " + twodecimal.format(tariff_rcv[5])+ "                 "+twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nCost:               " + twodecimal.format(Crcv/(1e6))+ "                    "+twodecimal.format(Ccmp/(1e6)) + "             M€\n\n\n";
//                      time=System.currentTimeMillis();
                        String[] proposal = new String[PERIODS + 3];
                        proposal[0] = "Received " + buyer.getOpponent().getLocalName() + " Proposal " + manager.n_proposals_received;
                        proposal[1] = "                    Received            Send? ";
                        Crcv = 0;
                        Ccmp = 0;
                        Arcv = 0;
                        Acmp = 0;
                        Drcv = 0;
                        Dcmp = 0;
                        for (int i = 0; i < PERIODS; i++) {

                            Crcv = Crcv + tariff_rcv[i + PERIODS] * tariff_rcv[i] * manager.contractduration / (1000);
                            Ccmp = Ccmp + tariff_cmp[i + PERIODS] * tariff_cmp[i] * manager.contractduration / (1000);
//                    if (manager.n_proposals_sent==0){
                            Arcv = Arcv + (manager.lim[i] - tariff_rcv[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                            Acmp = Acmp + (manager.lim[i] - tariff_cmp[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];

//                    }
//                                Arcv=Arcv+(tariff_rcv[i + PERIODS] * tariff_rcv[i]*manager.contractduration/(1000))*weight[i];
//                                Acmp=Acmp+(tariff_cmp[i + PERIODS] * tariff_cmp[i]*manager.contractduration/(1000))*weight[i];
                            proposal[i + 2] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_rcv[i]) + "                 " + twodecimal.format(tariff_cmp[i]) + "        €/MWh";
                        }
                        if (buyer.risk == 0 && manager.PreFunction.equals(manager.p1)) {
                            Drcv = Arcv;
                            Dcmp = Acmp;
                        } else if (buyer.risk == 0 && manager.PreFunction.equals(manager.p2)) {
                            Drcv = Crcv;
                            Dcmp = Ccmp;
                        }
                        if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr1)) {
                            Drcv = Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, buyer.sharing_risk, buyer.input_gui.deviation, manager.k) / PERIODS;
                            Dcmp = Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, buyer.sharing_risk, buyer.input_gui.deviation, manager.k) / PERIODS;
                        } else if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr2)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(manager.lbda * (-tariff_rcv[i] + (buyer.input_gui.price_mec.get(i) + buyer.input_gui.deviation[i + PERIODS])) / (buyer.input_gui.deviation[i + PERIODS] - buyer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(manager.lbda * (-tariff_cmp[i] + (buyer.input_gui.price_mec.get(i) + buyer.input_gui.deviation[i + PERIODS])) / (buyer.input_gui.deviation[i + PERIODS] - buyer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / PERIODS;

                            }
                        } else if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr3)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_rcv[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_cmp[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                            }
                        }
                        proposal[PERIODS + 2] = "Cost:            " + threedecimal.format(Crcv / (1e3)) + "             " + threedecimal.format(Ccmp / (1e3)) + "          k€";
                        if (manager.n_proposals_sent == 0) {
//                        buyer.utilities.add(Drcv*100);
//                        System.out.println("\nBuyer: Recebe1\n"+100.0*Drcv);
//                        buyer.utilities.add(Dcmp*100);
//                        buyer.utilities.add(100.0); 
                        }
                        aux = -1;
                        aux2 = -1;
//                      aux2=buyer.input_gui.inter(proposal,buyer.gui,choices2,1);
                        if (manager.case4 == 0) {
//                           while(buyer.gui.counteroffer==0&&manager.n_proposals_sent!=0){
////                      buyer.gui.stop();
//                            }
//                           buyer.gui.counteroffer=0;

                            aux2 = buyer.input_gui.inter2(tariff_cmp, tariff_rcv, Ccmp, Crcv, buyer.gui, choices2, 1, manager.n_proposals_received, 0, sent_history);

                            for (int i = 0; i < 2 * PERIODS; i++) {
                                tariff_cmp[i] = Double.valueOf(buyer.input_gui.list3[i]);
                                if (i >= PERIODS && buyer.DR == 1) {
                                    buyer.volumes[1][i - PERIODS] = tariff_cmp[i];
                                }
                            }

                        } else {
                            aux2 = 1;

                            manager.case4 = 0;
                        }
                        time = System.currentTimeMillis();
                        for (int i = 0; i < 2 * PERIODS; i++) {
                            tariff_cmp[i] = Double.valueOf(buyer.input_gui.list3[i]);
                        }
                    }
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(tariff_cmp);
                        oos.close();
                        reply.setByteSequenceContent(baos.toByteArray());
                    } catch (IOException ex) {
                        Logger.getLogger(MarketNegotiator.class.getName());
                    }
                    if (aux == 1 || aux2 == 2) {
                        buyer.calculatedscore = Acmp * 100.0;
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        myAgent.send(reply);
                        buyer.getGui().updateLog1("\n"/*+buyer.getLocalName()+*/ + "You have terminated the negotiation");
                        send = "You have terminated the negotiation";
                        buyer.input_gui.finish(buyer.gui, send);
                        step = 4;
                        break;
                    } else if (aux2 == 0) {
                        // Accept received proposal
                        buyer.calculatedscore = Acmp * 100.0;
                        buyer.getGui().updateLog1("                ");
                        buyer.getGui().updateLog1("****************************************************************************************************************");
                        buyer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                        buyer.getGui().updateLog1("****************************************************************************************************************");

//                            if (manager.str.equals(manager.s4)) {
                        buyer.getGui().updateLog1("\tAccept Received Proposal at:");
                        for (int i = 0; i < PERIODS; i++) {
                            buyer.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[PERIODS + i]) + "kWh");
                        }
                        buyer.getGui().updateLog1("\tContract Duration: " + Math.round(manager.contractduration) + " days\n\n");
//                                buyer.getGui().updateLog1(" ");
                        Ccmp = 0;
                        Crcv = 0;
                        Arcv = 0;
                        Acmp = 0;
                        Drcv = 0;
                        Dcmp = 0;
                        for (int i = 0; i < PERIODS; ++i) {
                            Crcv = Crcv + tariff_rcv[i + PERIODS] * tariff_rcv[i] * manager.contractduration / (1000);
                            Ccmp = Ccmp + tariff_cmp[i + PERIODS] * tariff_cmp[i] * manager.contractduration / (1000);
                            Arcv = Arcv + (manager.lim[i] - tariff_rcv[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                            Acmp = Acmp + (manager.lim[i] - tariff_cmp[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                        }

                        if (buyer.risk == 0 && manager.PreFunction.equals(manager.p1)) {
                            Drcv = Arcv;
                            Dcmp = Acmp;
                        } else if (buyer.risk == 0 && manager.PreFunction.equals(manager.p2)) {
                            Drcv = Crcv;
                            Dcmp = Ccmp;

                        }
                        if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr1)) {
                            Drcv = Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, buyer.sharing_risk, buyer.input_gui.deviation, manager.k) / PERIODS;
                            Dcmp = Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, buyer.sharing_risk, buyer.input_gui.deviation, manager.k) / PERIODS;
                        } else if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr2)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(manager.lbda * (-tariff_rcv[i] + (buyer.input_gui.price_mec.get(i) + buyer.input_gui.deviation[i + PERIODS])) / (buyer.input_gui.deviation[i + PERIODS] - buyer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(manager.lbda * (-tariff_cmp[i] + (buyer.input_gui.price_mec.get(i) + buyer.input_gui.deviation[i + PERIODS])) / (buyer.input_gui.deviation[i + PERIODS] - buyer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / PERIODS;

                            }
                        } else if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr3)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_rcv[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_cmp[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                            }
                        }

//                                buyer.getGui().updateLog1("******************************");
                        buyer.getGui().updateLog1("Total Cost computed: " + threedecimal.format(Ccmp / (1e3)) + " k€");
                        buyer.getGui().updateLog1("Total Cost received:     " + threedecimal.format(Crcv / (1e3)) + " k€");

                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        try {
                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                            oos2.writeObject(tariff_rcv);
                            oos2.close();
                            reply.setByteSequenceContent(baos2.toByteArray());
                        } catch (IOException ex) {
                            Logger.getLogger(MarketNegotiator.class.getName());
                        }
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                        myAgent.send(reply);
                        buyer.getGui().updateLog1("                Sent ACCEPT PROPOSAL  Message");

//                    MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
//                    MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
//                    
//                    msg = myAgent.receive(mt1);
//                    while(msg==null ){
//                       msg = myAgent.receive(mt2);
//                       if(msg==null){
//                          msg = myAgent.receive(mt1);  
//                       }
//                    }
//                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
//                   buyer.input_gui.congrat(buyer.gui);
//                    } else{
//                   buyer.input_gui.decline(buyer.gui);
//                    }       
//                           
                        buyer.addBehaviour(new MessageManager());
                        step = 5;
                        break;
                    } else {
                        myAgent.send(reply);
                        buyer.utilities.add(Dcmp * 100.0);
//                    System.out.println("\nBuyer: Calcula1\n"+100.0*Drcv);

//                    if (manager.str.equals(manager.s4)) {
                        buyer.getGui().updateLog1("Sent Proposal to Sell at:" + manager.contract);
                        for (int i = 0; i < PERIODS; i++) {
                            buyer.getGui().updateLog1("Price " + (i + 1) + " = " + twodecimal.format(tariff_cmp[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_cmp[PERIODS + i]) + "kWh");
                        }
                        buyer.getGui().updateLog1("Contract Duration = " + Math.round(manager.contractduration) + " days");
                        buyer.getGui().updateLog1("Total Cost computed = " + threedecimal.format(Ccmp / (1e3)) + " k€\n\n");

//                        buyer.getGui().updateLog1("******************************");
//                        buyer.getGui().updateLog1("Total Cost computed: " + twodecimal.format(Ccmp));
//                        buyer.getGui().updateLog1("Total Cost received: " + twodecimal.format(Crcv));
//                        buyer.getGui().updateLog1("******************************");
//                    }
//                    else {
//
//                        buyer.getGui().updateLog1("Sent Proposal to Sell at:");
//                        buyer.getGui().updateLog1("Price 1 = " + twodecimal.format(tariff_cmp[0]) + "€/MWh");
//                        buyer.getGui().updateLog1("Price 2 = " + twodecimal.format(tariff_cmp[1]) + "€/MWh");
//                        buyer.getGui().updateLog1("Price 3 = " + twodecimal.format(tariff_cmp[2]) + "€/MWh");
//                        buyer.getGui().updateLog1("Price 4 = " + twodecimal.format(tariff_cmp[3]) + "€/MWh");
//                        buyer.getGui().updateLog1("Price 5 = " + twodecimal.format(tariff_cmp[4]) + "€/MWh");
//                        buyer.getGui().updateLog1("Price 6 = " + twodecimal.format(tariff_cmp[5]) + "€/MWh");
//
//                    }
                        manager.n_proposals_sent++;
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

//                                    if(manager.n_proposals_received==0){
//                                        buyer.input_gui.offer(buyer.gui,0);
//                                    }
                                    if (manager.n_proposals_received < 1) {
                                        manager.prices_received_from_seller = tariff_rcv;
                                    }
                                    if (manager.n_proposals_received > 0) {
                                        buyer.input_gui.offer(buyer.gui, 1);
                                    }
//                                    if (manager.str.equals(manager.s4)) {
                                    buyer.getGui().updateLog1("\tReceived Proposal to buy at:");
                                    for (int i = 0; i < PERIODS; i++) {
                                        buyer.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[i + PERIODS]) + "kWh");
                                    }
                                    buyer.getGui().updateLog1("\tContract Duration: " + Math.round(manager.contractduration) + " days");
                                    buyer.getGui().updateLog1("\tTotal Cost Computed= " + threedecimal.format(Ccmp / (1e3)) + " k€");
                                    buyer.getGui().updateLog1("\tTotal Cost Received= " + threedecimal.format(Crcv / (1e3)) + " k€\n\n");
//                                    }
//                                    else {
//
//                                        buyer.getGui().updateLog1("      Received Proposal to buy at:  Price 1 = " + twodecimal.format(tariff_rcv[0]) + "€/MWh");
//                                        buyer.getGui().updateLog1("                                    Price 2 = " + twodecimal.format(tariff_rcv[1]) + "€/MWh");
//                                        buyer.getGui().updateLog1("                                    Price 3 = " + twodecimal.format(tariff_rcv[2]) + "€/MWh");
//                                        buyer.getGui().updateLog1("                                    Price 4 = " + twodecimal.format(tariff_rcv[3]) + "€/MWh");
//                                        buyer.getGui().updateLog1("                                    Price 5 = " + twodecimal.format(tariff_rcv[4]) + "€/MWh");
//                                        buyer.getGui().updateLog1("                                    Price 6 = " + twodecimal.format(tariff_rcv[5]) + "€/MWh");
//
//                                        buyer.getGui().updateLog1("  ");
//                                    }
                                    manager.n_proposals_received++;
                                    if (manager.n_proposals_received == 1) {
                                        manager.received_history.add(tariff_rcv);
                                    }
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(MarketNegotiator.class.getName());
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(MarketNegotiator.class.getName());
                            }
                        }

                        //Computing New Prices
                        while (buyer.gui.counteroffer == 0 && manager.n_proposals_sent != 0) {
//                          buyer.gui.stop();
                        }
                        buyer.gui.counteroffer = 0;
                        if (buyer.ES == 1 && !buyer.input_gui.tactic.equals(buyer.input_gui.sStrategy.getText())) {
                            buyer.input_gui.ES(buyer.input_gui.Parent);
                        }

                        tariff_cmp = (double[]) manager.strategy(tariff_rcv, tariff_cmp);

                        //Computing Costs of received and new - Ready to send - Proposals
                        Crcv = 0;
                        Ccmp = 0;
                        Acmp = 0;
                        Arcv = 0;
                        Dcmp = 0;
                        Drcv = 0;

                        if (manager.n_proposals_received >= 1) {
                            for (int i = 0; i < manager.initPrice.length; ++i) {
                                if (tariff_rcv[i + PERIODS] < manager.minEnergy[i]) {
                                    sair = 4;
                                }
                            }
                        }

//                        if (manager.str.equals(manager.s4)) {
                        for (int i = 0; i < PERIODS; ++i) {
                            Crcv = Crcv + tariff_rcv[i + PERIODS] * tariff_rcv[i] * manager.contractduration / (1000);
                            Ccmp = Ccmp + tariff_cmp[i + PERIODS] * tariff_cmp[i] * manager.contractduration / (1000);
                            Arcv = Arcv + (manager.lim[i] - tariff_rcv[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                            Acmp = Acmp + (manager.lim[i] - tariff_cmp[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                        }

                        if (buyer.risk == 0 && manager.PreFunction.equals(manager.p1)) {
                            Drcv = Arcv;
                            Dcmp = Acmp;
                        } else if (buyer.risk == 0 && manager.PreFunction.equals(manager.p2)) {
                            Drcv = Crcv;
                            Dcmp = Ccmp;
                        }
                        if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr1)) {
                            Drcv = Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, buyer.sharing_risk, buyer.input_gui.deviation, manager.k) / PERIODS;
                            Dcmp = Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, buyer.sharing_risk, buyer.input_gui.deviation, manager.k) / PERIODS;
                        } else if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr2)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(manager.lbda * (-tariff_rcv[i] + (buyer.input_gui.price_mec.get(i) + buyer.input_gui.deviation[i + PERIODS])) / (buyer.input_gui.deviation[i + PERIODS] - buyer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(manager.lbda * (-tariff_cmp[i] + (buyer.input_gui.price_mec.get(i) + buyer.input_gui.deviation[i + PERIODS])) / (buyer.input_gui.deviation[i + PERIODS] - buyer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / PERIODS;

                            }
                        } else if (buyer.risk == 1 && manager.PreRiskFunction.equals(manager.pr3)) {
                            for (int i = 0; i < PERIODS; ++i) {
                                Drcv = Drcv + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_rcv[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                                Dcmp = Dcmp + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_cmp[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + buyer.input_gui.deviation[i + PERIODS] * buyer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / PERIODS;
                            }
                        }
                        buyer.utilities.add(Drcv * 100);
//                            System.out.println("\nBuyer: Recebe2\n"+100.0*Drcv);

                        System.out.println("\nBuyer:\n\nCalculated Score:" + Dcmp + "\nReceived Score:" + Drcv);
//                        }
//                        else {
//                             for (int i = 0; i < 6; ++i) {
//                                Crcv = Crcv + tariff_rcv[i + 6] * tariff_rcv[i];
//                                Ccmp = Ccmp + tariff_cmp[i + 6] * tariff_cmp[i];
//                            }
//                            for (int i = 0; i < 6; ++i) {
//                                Crcv = Crcv + weight[i] * tariff_rcv[i];
//                                Ccmp = Ccmp + weight[i] * tariff_cmp[i];
//                            }
//                        }
                        // Teste whether to accept or reject the received proposal
                        if (Drcv > Dmax) {
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            myAgent.send(reply);
                            send = "Received proposal does not meet the minimum acceptable level";
                            buyer.getGui().updateLog1("Received proposal does not meet the minimum acceptable level");
                            buyer.getGui().updateLog1("");
                            buyer.getGui().updateLog1("                ");
                            buyer.getGui().updateLog1("****************************************************************************************************************");
                            buyer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                            buyer.getGui().updateLog1("****************************************************************************************************************");
                            buyer.getGui().updateLog1("");
                            buyer.input_gui.finish(buyer.gui, send);
                            step = 4;  // Withdrawn negotiation
                            break;
                        } else if (sair == 4) {
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            myAgent.send(reply);
                            send = "Seller don't have enough energy to supply";
                            buyer.input_gui.finish(buyer.gui, send);
                            buyer.getGui().updateLog1("Seller don't have enough energy to supply");
                            buyer.getGui().updateLog1("");
                            buyer.getGui().updateLog1("****************************************************************************************************************");
                            buyer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                            buyer.getGui().updateLog1("****************************************************************************************************************");
                        } else if ((Drcv > Dcmp && manager.PreFunction.equals(manager.p2)) || (Drcv < Dcmp && (manager.PreFunction.equals(manager.p1) || buyer.risk == 1)) || (manager.n_proposals_received <= 1)) {

//                            manager.timei2 = System.currentTimeMillis();
//                            
//                    //            if (timei2 == time1) {
//                    //                System.out.print("Problem!!!");
//                    //            }
//                                manager.timei = manager.timei2 - manager.time1;
//                                System.out.println(" /n " +timei);
                            System.out.println(" \n timei " + manager.timei + " \n deadline " + manager.deadlineinternal);

                            // Test number of proposal sent
                            if (manager.n_proposals_sent >= 20 || ((manager.str.equals(manager.s5) || manager.str.equals(manager.s6)) && manager.timei > manager.deadlineinternal)) {
                                reply = msg.createReply();
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                myAgent.send(reply);
                                if (manager.n_proposals_sent >= 20) {
                                    buyer.getGui().updateLog1("Exceeded the maximum number of bids allowed (<7)");
                                    send = "Exceeded the maximum number of bids allowed (<7)";
                                    buyer.input_gui.finish(buyer.gui, send);
                                } else {
                                    buyer.getGui().updateLog1("Deadline End");
                                    send = "Deadline End";
                                    buyer.input_gui.finish(buyer.gui, send);
                                }
                                buyer.getGui().updateLog1("");
                                buyer.getGui().updateLog1("                ");
                                buyer.getGui().updateLog1("****************************************************************************************************************");
                                buyer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                                buyer.getGui().updateLog1("****************************************************************************************************************");
                                buyer.getGui().updateLog1("");

                                step = 4;  // Withdrawn negotiation
                                break;
                            } else {

                                step = 2;
                                break;
                            }
                        } else {
                            Crcv = 0;
                            Ccmp = 0;
                            for (int i = 0; i < PERIODS; ++i) {
                                Crcv = Crcv + tariff_rcv[i + PERIODS] * tariff_rcv[i] * manager.contractduration / (1000);
                                Ccmp = Ccmp + tariff_cmp[i + PERIODS] * tariff_cmp[i] * manager.contractduration / (1000);

                            }
                            System.out.println("Buyer " + Arcv + "Custo: " + Crcv + "\n propostas " + (manager.n_proposals_received + manager.n_proposals_sent));
//                            int result = JOptionPane.showConfirmDialog(null, ("\n\nAccept "+buyer.getOpponent().getLocalName()+" Proposal "+manager.n_proposals_received+"?\n\n"+"Price 1:           " +twodecimal.format(tariff_rcv[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_rcv[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_rcv[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_rcv[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_rcv[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_rcv[5]) + "        €/MWh\n"+"\nReceived Cost:      "+twodecimal.format(Crcv)+"   €"+"\nCalculated Cost:   "+Math.round(Ccmp)+"   €\n\n\n"), buyer.getLocalName()+" Finishing Negotiation", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
//                            Object proposal="\n\nAccept "+buyer.getOpponent().getLocalName()+" Proposal "+manager.n_proposals_received+"?\n\n"+"Price 1:           " +twodecimal.format(tariff_rcv[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_rcv[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_rcv[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_rcv[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_rcv[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_rcv[5]) + "        €/MWh\n"+"\nReceived Cost:      "+twodecimal.format(Crcv/(1e6))+"   k€"+"\nCalculated Cost:   "+twodecimal.format(Ccmp/(1e6))+"   M€\n\n\n";
                            String[] proposal = new String[PERIODS + 3];

                            proposal[0] = "Accept " + buyer.getOpponent().getLocalName() + " Proposal " + manager.n_proposals_received + "?";
                            for (int i = 0; i < PERIODS; i++) {
                                proposal[i + 1] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_rcv[i]) + "        €/MWh";
                            }
                            proposal[PERIODS + 1] = "Received Cost:     " + threedecimal.format(Crcv / (1e3)) + "   k€";
                            proposal[PERIODS + 2] = "Calculated Cost:   " + threedecimal.format(Ccmp / (1e3)) + "   k€";
//                            aux=buyer.input_gui.inter(proposal,buyer.gui, choices3,1);     

                            aux2 = buyer.input_gui.inter2(tariff_cmp, tariff_rcv, Ccmp, Crcv, buyer.gui, choices2, 1, manager.n_proposals_received, 1, sent_history);

                            for (int i = 0; i < 2 * PERIODS; i++) {
                                tariff_cmp[i] = Double.valueOf(buyer.input_gui.list3[i]);
                                if (i >= PERIODS && buyer.DR == 1) {
                                    buyer.volumes[1][i - PERIODS] = tariff_cmp[i];
                                }
                            }

                            if (aux2 == 1) {
//                                  reply = msg.createReply();
//                    reply.setPerformative(ACLMessage.PROPOSE);
//                    reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
//                                        try {
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        ObjectOutputStream oos = new ObjectOutputStream(baos);
//                        oos.writeObject(tariff_cmp);
//                        oos.close();
//                        reply.setByteSequenceContent(baos.toByteArray());
//                    } catch (IOException ex) {
//                        Logger.getLogger(MarketNegotiator.class.getName());
//                    }
//                                        myAgent.send(reply);
//                    
//              
////                    if (manager.str.equals(manager.s4)) {
//                        buyer.getGui().updateLog1("Sent Proposal to Sell at:"+manager.contract);
//                        buyer.getGui().updateLog1("Price 1 = " + twodecimal.format(tariff_cmp[0]) + "€/MWh " + " Energy 1 = " + twodecimal.format(tariff_cmp[6]) + "kWh");
//                        buyer.getGui().updateLog1("Price 2 = " + twodecimal.format(tariff_cmp[1]) + "€/MWh " + " Energy 2 = " + twodecimal.format(tariff_cmp[7]) + "kWh");
//                        buyer.getGui().updateLog1("Price 3 = " + twodecimal.format(tariff_cmp[2]) + "€/MWh " + " Energy 3 = " + twodecimal.format(tariff_cmp[8]) + "kWh");
//                        buyer.getGui().updateLog1("Price 4 = " + twodecimal.format(tariff_cmp[3]) + "€/MWh " + " Energy 4 = " + twodecimal.format(tariff_cmp[9]) + "kWh");
//                        buyer.getGui().updateLog1("Price 5 = " + twodecimal.format(tariff_cmp[4]) + "€/MWh " + " Energy 5 = " + twodecimal.format(tariff_cmp[10]) + "kWh");
//                        buyer.getGui().updateLog1("Price 6 = " + twodecimal.format(tariff_cmp[5]) + "€/MWh " + " Energy 6 = " + twodecimal.format(tariff_cmp[11]) + "kWh");
//                        buyer.getGui().updateLog1("  ");
//
//
//                       
//                    manager.n_proposals_sent++;
//                                buyer.utilities.add(Dcmp*100.0);
                                manager.case4 = 1;
                                step = 2;
                                break;
                            } else if (aux2 == 2) {
                                buyer.calculatedscore = Acmp * 100.0;
                                reply = msg.createReply();
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                myAgent.send(reply);
                                buyer.getGui().updateLog1("\n" +/*buyer.getLocalName()+*/ "You have terminated the negotiation");
                                send = "You have terminated the negotiation";
                                buyer.input_gui.finish(buyer.gui, send);
                                step = 4;
                                break;
                            } else if (aux2 == 0) {

                                // Accept received proposal
                                buyer.getGui().updateLog1("                ");
                                buyer.getGui().updateLog1("****************************************************************************************************************");
                                buyer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                                buyer.getGui().updateLog1("****************************************************************************************************************");
                                buyer.calculatedscore = Acmp * 100.0;
//                            if (manager.str.equals(manager.s4)) {
                                buyer.getGui().updateLog1("\tAccept Received Proposal at:");
                                for (int i = 0; i < PERIODS; i++) {
                                    buyer.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[i + PERIODS]) + "kWh");
                                }
                                buyer.getGui().updateLog1("\tContract Duration: " + Math.round(manager.contractduration) + " days");
//                                buyer.getGui().updateLog1(" ");

//                                buyer.getGui().updateLog1("******************************");
                                buyer.getGui().updateLog1("Total Cost computed: " + threedecimal.format(Ccmp / (1e3)) + " k€");
                                buyer.getGui().updateLog1("Total Cost received:     " + threedecimal.format(Crcv / (1e3)) + " k€\n\n");
//                                buyer.getGui().updateLog1("******************************");
//                                buyer.getGui().updateLog1(" ");
//                            }
//                            else {
//
//                                buyer.getGui().updateLog1("     ACCEPT Received Proposal:  Price 1 = " + twodecimal.format(tariff_rcv[0]) + "€/MWh");
//                                buyer.getGui().updateLog1("                                Price 2 = " + twodecimal.format(tariff_rcv[1]) + "€/MWh");
//                                buyer.getGui().updateLog1("                                Price 3 = " + twodecimal.format(tariff_rcv[2]) + "€/MWh");
//                                buyer.getGui().updateLog1("                                Price 4 = " + twodecimal.format(tariff_rcv[3]) + "€/MWh");
//                                buyer.getGui().updateLog1("                                Price 5 = " + twodecimal.format(tariff_rcv[4]) + "€/MWh");
//                                buyer.getGui().updateLog1("                                Price 6 = " + twodecimal.format(tariff_rcv[5]) + "€/MWh");
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
                                    Logger.getLogger(MarketNegotiator.class.getName());
                                }
                                reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                                myAgent.send(reply);
                                buyer.getGui().updateLog1("                Sent ACCEPT PROPOSAL  Message");
//                                                                           MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
//                    MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
//                    
////                    msg = myAgent.receive(mt1);
////                    while(msg==null ){
////                       msg = myAgent.receive(mt2);
////                       if(msg==null){
////                          msg = myAgent.receive(mt1);  
////                       }
////                    }
////                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
////                   buyer.input_gui.congrat(buyer.gui);
////                    } else{
////                   buyer.input_gui.decline(buyer.gui);;
////                    } 
                                buyer.addBehaviour(new MessageManager());
                                step = 5;
                                break;
                            }
                        }
                    } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        buyer.getGui().updateLog1("                ");
                        buyer.getGui().updateLog1("****************************************************************************************************************");
                        buyer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                        buyer.getGui().updateLog1("****************************************************************************************************************");

                        // check if the data is sent by byte code.
                        if (msg.hasByteSequenceContent()) {
                            byte[] data = msg.getByteSequenceContent();
                            ByteArrayInputStream bais2 = new ByteArrayInputStream(data);
                            try {
                                ObjectInputStream ois2 = new ObjectInputStream(bais2);
                                try {
                                    tariff_rcv = (double[]) ois2.readObject();
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(MarketNegotiator.class.getName());
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(MarketNegotiator.class.getName());
                            }
                        }
                        Ccmp = 0;
                        Crcv = 0;
                        for (int i = 0; i < PERIODS; ++i) {
                            Crcv = Crcv + tariff_rcv[i + PERIODS] * tariff_rcv[i] * manager.contractduration / (1000);
                            Ccmp = Ccmp + tariff_cmp[i + PERIODS] * tariff_cmp[i] * manager.contractduration / (1000);
                        }
                        System.out.println("Buyer " + Acmp + "\nCusto: " + Arcv + "\n propostas " + (manager.n_proposals_received + manager.n_proposals_sent));
//                        if (manager.str.equals(manager.s4)) {
//                           int result = JOptionPane.showConfirmDialog(null, ("\n\n"+buyer.getOpponent().getLocalName()+" Accept Proposal "+manager.n_proposals_sent +"\n\n"+"Price 1:           " +twodecimal.format(tariff_rcv[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_rcv[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_rcv[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_rcv[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_rcv[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_rcv[5]) + "        €/MWh\n"+"\nFinal Cost:     "+Math.round(Crcv)+"   € \n\n\n"), buyer.getLocalName()+" Finishing Negotiation", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
//                           Object proposal="\n\n"+buyer.getOpponent().getLocalName()+" Accept Proposal "+manager.n_proposals_sent +"\n\n"+"Price 1:           " +twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " +twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " +twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " +twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " +twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " +twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nFinal Cost:     "+twodecimal.format(Ccmp/(1e6))+"   M€ \n\n\n";
                        String[] proposal = new String[PERIODS + 2];

                        proposal[0] = buyer.getOpponent().getLocalName() + " Accept Proposal " + manager.n_proposals_sent;
                        proposal[1] = "\n\nFinal Cost:      " + threedecimal.format(Ccmp / (1e3)) + "    k€           Energy(kWh)\n";
                        for (int i = 0; i < PERIODS; i++) {
                            proposal[i + 2] = "Price " + (i + 1) + ":           " + twodecimal.format(tariff_cmp[i]) + "  €/MWh         " + twodecimal.format(tariff_cmp[PERIODS + i]);
                            if (i >= 9) {
                                proposal[i + 2] = "Price " + (i + 1) + ":         " + twodecimal.format(tariff_cmp[i]) + "  €/MWh         " + twodecimal.format(tariff_cmp[PERIODS + i]);

                            }
                        }

                        buyer.input_gui.inter(proposal, buyer.gui, choices4, 0);
                        //                           gui.inter(s);
                        buyer.getGui().updateLog1("Received ACCEPT Proposal:");
                        for (int i = 0; i < PERIODS; i++) {
                            buyer.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + twodecimal.format(tariff_rcv[PERIODS + i]) + "kWh");
                        }
                        buyer.getGui().updateLog1("Contract Duration: " + Math.round(manager.contractduration) + " days\n\n");
//                            buyer.getGui().updateLog1(" ");
//                            buyer.getGui().updateLog1("******************************");
                        buyer.getGui().updateLog1("Total Cost computed: " + threedecimal.format(Ccmp / (1e3)) + " k€");
                        buyer.getGui().updateLog1("Total Cost received: " + threedecimal.format(Crcv / (1e3)) + " k€");

                        buyer.addBehaviour(new MessageManager());

//                            buyer.getGui().updateLog1("******************************");
//                        }
//                        else {
//                            buyer.getGui().updateLog1("     Received ACCEPT Proposal:  Price 1 = " + twodecimal.format(tariff_rcv[0]) + "€/MWh");
//                            buyer.getGui().updateLog1("                                Price 2 = " + twodecimal.format(tariff_rcv[1]) + "€/MWh");
//                            buyer.getGui().updateLog1("                                Price 3 = " + twodecimal.format(tariff_rcv[2]) + "€/MWh");
//                            buyer.getGui().updateLog1("                                Price 4 = " + twodecimal.format(tariff_rcv[3]) + "€/MWh");
//                            buyer.getGui().updateLog1("                                Price 5 = " + twodecimal.format(tariff_rcv[4]) + "€/MWh");
//                            buyer.getGui().updateLog1("                                Price 6 = " + twodecimal.format(tariff_rcv[5]) + "€/MWh");
//                        } 
//                    MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
//                    MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
//                    msg = myAgent.receive(mt1);
//                    while(msg==null ){
//                       msg = myAgent.receive(mt2);
//                       if(msg==null){
//                          msg = myAgent.receive(mt1);  
//                       }
//                    }
//                    if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
//                   buyer.input_gui.congrat(buyer.gui);
//                    } else{
//                   buyer.input_gui.decline(buyer.gui);;
//                    } 
                        step = 5;
                        break;

                    } else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                        send = "";
                        buyer.input_gui.finish(buyer.gui, send);
                        buyer.getGui().updateLog1("                ");
                        buyer.getGui().updateLog1("****************************************************************************************************************");
                        buyer.getGui().updateLog1("             **                                 OPPONENT TERMINATED NEGOTIATION                               **");
                        buyer.getGui().updateLog1("****************************************************************************************************************");
                    }
            }
        }

        @Override
        public boolean done() {
            return step == 4;
        }

        public class MessageManager extends CyclicBehaviour {

            MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
            MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("marketoperator_ontology"), MessageTemplate.MatchProtocol("negotiation_protocol")), MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
//        MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol"));
            
            
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(mt1);
                while (msg == null) {
                    msg = myAgent.receive(mt2);
                    if (msg == null) {
                        msg = myAgent.receive(mt1);
                    }
                }
//            ACLMessage msg2 = myAgent.receive(mt2);
                if (msg != null) {
                    if (msg.getOntology().equals("marketoperator_ontology")) {
                        if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            buyer.input_gui.congrat(buyer.gui);
                        } else {
                            buyer.input_gui.decline(buyer.gui);
                        }
                    }
//            }if (msg2 != null) {
//                if (msg2.getOntology().equals("market_ontology")) {
//                    MarketOntology market_ontology = new MarketOntology();
//                    market_ontology.resolve(msg2);
//                }
                } else {
                    block();
                }

            }
        }
    }

    double roundThreeDecimals(double d) {
        double result = d * 1000;
        result = Math.round(result);
        result = result / 1000;
        return result;
    }//End Marcket Negotiator
}
