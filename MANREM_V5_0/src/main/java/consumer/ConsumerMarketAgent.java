/**
 * Market Consumer Agent Consumer class.
 *
 */
package consumer;

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
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import xml.XMLReader;

public class ConsumerMarketAgent {

//    private final   MessageTemplate     cONTOLOGY;        
//    private final   MessageTemplate     prtkNEGOTIATION; 
//    private final   MessageTemplate     pREQUEST;
//    private final   MessageTemplate     pPROPOSE;
//    private final   MessageTemplate     idACCEPT;
//    private final   MessageTemplate     AVALIATEID;
    private         ConfigOntology      configOntology;
    private         XMLReader           xmlr;
    private int                         step; 
    private double[]                    sent_history;
    private         Consumer            consumer;
    private long                        time;

    public                          ConsumerMarketAgent(Consumer consumer) {
        
 
        this.consumer           =   consumer;

    }

    /**
     * This method is called by the GUI when the user inserts a new Market Consumer
     * Agent
     *
     * @param title The name of agent
     * @param initPrice The inicial price to buy electricity
     * @param maxPrice The maximum acceptable price to buy electricity
     * @param energy The energy of agent
     * @param deadline The deadline by which to buy the energy
     *
     */
    /* public void                     purchase(String title, double[] initPrice, double[] maxPrice, double[] energy, double[] maxEnergy, double[] minEnergy, String str, Date deadline, double[] profile_sent_to_seller, double[] prices_received_from_seller) {
     * consumer.addBehaviour(new PurchaseManager(title, initPrice, maxPrice, energy, minEnergy, maxEnergy, str, deadline, profile_sent_to_seller, prices_received_from_seller));
     * }*/
    public void purchase(String title, double[] initPrice, double[] maxPrice, double[] energy, double[] maxEnergy, double[] minEnergy, String str, String pre, String prerisk,int DR, Date deadline,String contract, double[] profile_sent_to_seller, double contractduration, double[] prices_received_from_seller) {
        consumer.addBehaviour(new PurchaseManager(title, initPrice, maxPrice, energy, minEnergy, maxEnergy, str,pre,prerisk,DR, deadline,contract, profile_sent_to_seller, contractduration, prices_received_from_seller));
    }

    private class                   PurchaseManager extends TickerBehaviour {

        // <editor-fold defaultstate="collapsed" desc="Inital Variables"> 
            /*
            int n_proposals_sent = 0;
            int n_proposals_received = 0;
            private String title;
            private double[] initPrice = new double[6];
            private double[] lim = new double[6];
            private double[] energy = new double[6];
            private double[] minEnergy = new double[6];
            private double[] maxEnergy = new double[6];
            private String str;
            private long deadline;
            private int N_ISSUES = 12;
            private double[] current_prices_volumes = new double[N_ISSUES];
            private double[] Cf = {0.15, 0.10, 0.35, 0.10, 0.15, 0.20};
            private double[] Cfv = {-1.0, -1.0, -1.0, -1.0, -1.0, -1.0};
            private int flag, nticks;
            private long initTime, deltaT;
            double[] tariff_cmp_previous = new double[12];
            // The negoctiation strategies
            String s1 = "Concession Making";
            String s2 = "Low-Priority Concession";
            String s3 = "E-R Concession";
            String s4 = "Demand Management";
            double[] profile_sent_to_seller = new double[6];
            double[] prices_received_from_seller = new double[6];
            private CostManagerBuyer cost_manager_buyer;
            ArrayList<double[]> choosen_volumes_history = new ArrayList<>();
           /* */
        // </editor-fold>  
        
        int                 n_proposals_sent        =   0;
        int                 n_proposals_received    =   0;
        int                 case4                   =   0;
        final int           PERIODS                 =   consumer.input_gui.PERIODS;
        private String      title;
        private double[]    initPrice               =   new double[PERIODS];
        private double[]    lim                     =   new double[PERIODS];
        private double[]    limneg                  =   new double[PERIODS];
        private double[]    energy                  =   new double[PERIODS];
        private double[]    minEnergy               =   new double[PERIODS];
        private double[]    maxEnergy               =   new double[PERIODS];
        private String      PreFunction, PreRiskFunction;
        double[]            weight                  =   new double[PERIODS];
        private double      deadlineinternal;
        private long        deadlinetest;
        private String      str;
        private String      contract;
//        private double[] deviation= Risk.deviation(PERIODS);
        private double[]    deviationlim            =   new double[PERIODS];
        private double[]    deltaP                  =   new double[PERIODS];
        private double      lbda                    =   0.5;
        private double[]    k                       =   new double[PERIODS];
        
        private long        deadline;
        private int         N_ISSUES                =   2*PERIODS;
        private int         DR                      =   0;
        private double      CS                      =   1.0;
        private double[]    current_prices_volumes  =   new double[N_ISSUES];
        private double[]    Cf                      =   new double[PERIODS];
        private double[]    Cfv                     =   new double[PERIODS];
        private double[]    Cf2                     =   new double[PERIODS];
        private double[]    Cf3                     =   new double[PERIODS];
        double[]            V                       =   new double[PERIODS];
        double[]            c                       =   new double[PERIODS];
        private long        currentTime, time1, timei2;
        private double      timei, min, div;
        private double      Cf4, Cf5;
        private double[]    Cf67                    =   {0.00, 0.00, 0.00, 0.00, 0.00, 0.00};
        
        private int         flag, nticks;
        private long        initTime, deltaT;
        double[]            tariff_cmp_previous     =   new double[2*PERIODS];
        double[]            tariff_rcv              =   new double[2*PERIODS];
        
        double              i                       =   Math.random();
        double              B4i                     =   2.0; // Beginning of range.
        double              B4f                     =   3.0; // End of range.
        double              B4                      =   B4i + i * (B4f - B4i);
        double              j                       =   Math.random();
        double              B5i                     =   0.2; // Beginning of range.
        double              B5f                     =   0.3; // End of range.
        double              B5                      =   B5i + i * (B5f - B5i);
        double              w                       =   Math.random();
        int                 mi                      =   1; // Beginning of range.
        int                 M                       =   3; // End of range.
        int                 m                       =   (int) (mi + w * (M - mi));
        
        // The negoctiation strategies
        String              s1                      =   "Compromise";
        String              s2                      =   "Low-Priority Concession";
        String              s3                      =   "Volume Conceder";
        String              s4                      =   "Demand Management";
        String              s5                      =   "Time Conceder";
        String              s6                      =   "Time Boulware";
        String              s7                      =   "Tit-For-Tat";
        String              s8                      =   "Random Tit-For-Tat";
        String              s9                      =   "Intrasigent Priority";
        String              s10                     =   "Inverse Tit-For-Tat Behaviour";
        String              s11                     =   "Negotiation Risk Strategy";
        String              s12                     =   "Conceding Slowly";
        String              p1                      =   "Additive Function";
        String              p2                      =   "Cost Function";
        String              pr1                     =   "Risk Function";
        String              pr2                     =   "Von Neumann-Morgenstern";
        String              pr3                     =   "Rigorous Risk Function"; 
        
        double[]            profile_sent_to_seller  =   new double[PERIODS];
        double              contractduration;
        
        ArrayList<double[]>         choosen_volumes_history     =   new ArrayList<>();
        private ArrayList<double[]> received_history            =   new ArrayList<double[]>();
        double[]                    prices_received_from_seller =   new double[PERIODS];
        
        private     CostManagerBuyer    cost_manager_buyer;
        
       // private PurchaseManager(String t, double[] ip, double[] mp, double[] en, double[] me, double[] max_e, String s, Date d, double[] profile_sent_to_seller, double[] prices_received_from_seller) {
        private PurchaseManager(String t, double[] ip, double[] mp, double[] en, double[] me, double[] max_e, String s,String pre,String prerisk,int dr, Date d,String contract, double[] profile_sent_to_seller, double contractduration, double[] prices_received_from_seller) { 
            
            super(consumer, 1);         // tick every 1/4minute
            title           =   t;
            initPrice       =   ip;
            lim             =   mp;
            energy          =   en;
            str             =   s;
            nticks          =   0;
            minEnergy       =   me;
            maxEnergy       =   max_e;
            DR              =   dr;
            PreFunction     =   pre;
            sent_history    =   new double[2*PERIODS];
            PreRiskFunction =   prerisk;
            deadlinetest    =   (d.getTime()-System.currentTimeMillis());
            deadline        =   d.getTime();
            initTime        =   System.currentTimeMillis();
            deltaT          =   deadline - initTime;
            
            this.contract                       =   contract;
            this.contractduration               =   contractduration;
            this.profile_sent_to_seller         =   profile_sent_to_seller;
            this.prices_received_from_seller    =   prices_received_from_seller;

            
        }

        public void onTick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime > deadline) {
                // Deadline expired
                consumer.getGui().updateLog2("NEGOTIATION TERMINATE!" + title);
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
            
            double vototal=0, cftotal=0, v=0;
//            double Cf1 = 0.1667;
            double Cf1=0.1;
            double arisk=0.0;
            double brisk=0.0;
          
            if (n_proposals_sent == 0) {
                time1 = System.currentTimeMillis();
            }
            if(DR==1){
                CS=1;
            }
                
//            System.out.print("estrategia"+ str);
           
            //Initial Prices -- Initial Proposal
            if (flag == 0 && !str.equals(s4)) {
                for (int i = 0; i < initPrice.length; ++i) {
                    current_prices_volumes[i] = initPrice[i];
                    sent_history[i]=initPrice[i];
                    current_prices_volumes[i + PERIODS] = energy[i];
                    consumer.volumes[0][i]=energy[i];
                    limneg[i]=lim[i];
                    tariff_rcv[i]=prices_received_from_seller[i];
                    tariff_rcv[i+PERIODS]=energy[i];
                    if(consumer.risk==1){
                        k[i]=-1/lbda*(Math.log(0.01)/(limneg[i]-initPrice[i]));
                    }
//                    System.out.println(" \n"+ i + "  " +energy[i] +" \n ");
                }
             
                                                if (DR==1){
                       for (int i=0; i<PERIODS;i++){
                       V[i]=current_prices_volumes[i+PERIODS];
                       c[i]=tariff_rcv[i];
                       if(n_proposals_received==0){
                           c[i]=prices_received_from_seller[i];
                       }
                                  }
            
           
                      
		try {
			V=new lpmin().execute(V, c, m, this.minEnergy,this.maxEnergy,PERIODS);
                        
		}
                		catch (LpSolveException e) {
			e.printStackTrace();
		}
                for (int i=0; i<PERIODS;i++){
                       current_prices_volumes[i+PERIODS]=Double.valueOf(twodecimal.format(V[i]).replace(",", "."));
                }
        }
                consumer.received_history.add(tariff_rcv);
                flag = 1;
            } else {
                   for (int i = 0; i < initPrice.length; ++i) {
                current_prices_volumes[i + PERIODS] = energy[i];
                        }
                if (n_proposals_received >1){
                    consumer.received_history.add(tariff_rcv);
                    for (int i=0;i < initPrice.length; ++i){
                        if (current_prices_volumes[i+PERIODS]>tariff_rcv[i+PERIODS]){
                            if(tariff_rcv[i+PERIODS]>minEnergy[i]){
                                current_prices_volumes[i+PERIODS]=tariff_rcv[i+PERIODS];
                            }else{
                                
//                                
//                                consumer.getGui().updateLog1("Seller don't have enough energy to supply");
//                                consumer.getGui().updateLog1("");
//                                consumer.getGui().updateLog1("****************************************************************************************************************");
//                                consumer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
//                                consumer.getGui().updateLog1("****************************************************************************************************************");
                                }
                            
                        }
                        }
                                if (DR==1){
                       for (int i=0; i<PERIODS;i++){
                       V[i]=current_prices_volumes[i+PERIODS];
                       c[i]=tariff_rcv[i];
                       if(n_proposals_received==0){
                           c[i]=prices_received_from_seller[i];
                       }
                                  }
            
           
                      
		try {
			V=new lpmin().execute(V, c, m, this.minEnergy,this.maxEnergy,PERIODS);
                        
		}
                		catch (LpSolveException e) {
			e.printStackTrace();
		}
                for (int i=0; i<PERIODS;i++){
                       current_prices_volumes[i+PERIODS]=Double.valueOf(twodecimal.format(V[i]).replace(",", "."));
                }
        }
//                       time=System.currentTimeMillis();               
                }
                if(consumer.risk==1){
                    
//                     for (int i=0;i < PERIODS; ++i){
//                         arisk=lim[i]-current_prices_volumes[i];
////                    deviationlim[i]=Math.min(arisk,deviation[i]);
//                            }
//                     deltaP=Risk.deltaPb(lbda, current_prices_volumes, limneg, consumer.sharing_risk, consumer.input_gui.deviation,k);
                          for (int i=0;i < PERIODS; ++i){
                              deltaP[i]=100;
                              brisk=lim[i]-current_prices_volumes[i];
                              limneg[i]=current_prices_volumes[i]+Math.min(Math.abs(deltaP[i]),brisk);
                          }  
                }else{
                  for (int i=0;i < PERIODS; ++i){
                              limneg[i]=lim[i];
                          }  
                }
                vototal=0;
                 for (int i=0;i < PERIODS; ++i){
                   vototal=vototal+current_prices_volumes[PERIODS+i];  
                 } 
                 cftotal=0;
                for (int i=0;i < PERIODS; ++i){
                   weight[i] = current_prices_volumes[PERIODS+i] / vototal;
                   cftotal=cftotal+1/weight[i];
                 } 
//            cftotal=1/weight[0]+1/weight[1]+1/weight[2]+1/weight[3]+1/weight[4]+1/weight[5];
            System.out.println("\nbuyer\n");
            for (int i = 0; i < PERIODS; i++) {
                sent_history[i]=current_prices_volumes[i];
                Cf[i]=(PERIODS/6)*(Cf1/0.1667)*(1/weight[i])/cftotal;
                weight[i] = roundThreeDecimals(weight[i]);
                System.out.println("peso "+i+": "+weight[i]+"\n");
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
                    if (DR!=1 || n_proposals_sent>0){
                    for (int j = 0; j < PERIODS; ++j) {
                        temp1 = Cf1 * CS*(limneg[j] - current_prices_volumes[j]);
                        temp2 = current_prices_volumes[j] + temp1;
                        current_prices_volumes[j] = temp2;
//                        current_prices_volumes[j + 6] = energy[j];
//                        System.out.println(" /n " +step+" /n " +lim[j]+current_prices_volumes[j]+" /n ");
                    }
                    }
                }// end if s1
                else {
                    if (str.equals(s2)) {
                        
                        double vtotal=0.0;
                        for (int j = 0; j < PERIODS; ++j) {
                           vtotal=vtotal+current_prices_volumes[0+PERIODS];
                        }
                        
                        for (int j = 0; j < PERIODS; ++j) {
                           Cf[j]=current_prices_volumes[0+PERIODS]/vtotal;
                        }
                
                        // -----<<< Low-Priority Concession >>>-----
                        for (int j = 0; j < PERIODS; ++j) {
                            if(current_prices_volumes[j]!=0){
                            temp1 = 0.3*Cf[j] * (limneg[j] - current_prices_volumes[j]);
                            temp2 = current_prices_volumes[j] + temp1;
                            }else{
                                temp2=0.00;
                            }
                            current_prices_volumes[j] = temp2;
                        }
                    }// end if s2
                    else {
                        if (str.equals(s3)) {

                            v=-Math.log(Cf1)*energy.length;
                            v=-Math.log(Cf1)*3;
//                            System.out.println(" v "+ v);
                                
                            // -----<<< E-R Concession Strategy >>>-----
                            for (int i = 0; i < energy.length; ++i) {
                                energyTotal = energyTotal + current_prices_volumes[i+energy.length];
                            }
                            for (int j = 0; j < energy.length; ++j) {
                                if(current_prices_volumes[j]!=0){
                                temp3 = current_prices_volumes[j+energy.length] / energyTotal;
                                temp4 = Math.exp(-v * temp3);
                                Cfv[j] = temp4;
                                temp1 = Cfv[j] * (limneg[j] - current_prices_volumes[j]);
                                temp2 = current_prices_volumes[j] + temp1;
                                 }else{
                                temp2=0.00;
                            }
                                current_prices_volumes[j] = temp2;
//                                current_prices_volumes[j + 6] = energy[j];
                            }
                        }// end if s3
                        else {
                            if (str.equals(s4)) {

                                //-----<<< Demand Management Strategy >>>-----
                                CostManagerBuyer cost_manager_buyer = new CostManagerBuyer(tariff_cmp_previous.clone(), tariff_rcv.clone(), minEnergy, maxEnergy, limneg, choosen_volumes_history,0);

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
                                choosen_volumes_history.add(Arrays.copyOfRange(current_prices_volumes, PERIODS, 2*PERIODS));

                            }// end if s4
                        else {
                            if (str.equals(s5)) {
                                // Conceder, Time Concession Strategy:
                                double k = 0.1;
                                time = System.currentTimeMillis()-time;
//                                timei2 = System.currentTimeMillis()-time;
                    //            if (timei2 == time1) {
                    //                System.out.print("Problem!!!");
                    //            }
//                                timei = timei2 - time1;
                                timei=timei+time;
//                                System.out.println(" /n " +timei);
//                                timei++;
                                System.out.println(" \n buyer times: \n timei " +timei+" \n timei2 " +timei2+" \n time1 " +time1);
                                if (timei > deadlineinternal) {
                                    step = 4;
                                } else {
                                    min = Math.min(timei, deadlineinternal);
                                    div = min / deadlineinternal;
                                    Cf4 = k + (1 - k) * Math.pow(div, (1 / B4));
                                    for (int j = 0; j < initPrice.length; ++j) {
                                        temp1 = CS*Cf4 * (limneg[j] - current_prices_volumes[j]);
                                        temp2 = current_prices_volumes[j] + temp1;
                                        current_prices_volumes[j] = temp2;
                                      
                                    }
                                }
                            }
                        else {
                            // Boulware, Time Concession Strategy:
                            if (str.equals(s6))  {
                                double k = 0.1;
//                                timei2 = System.currentTimeMillis()-time;
                                time = System.currentTimeMillis()-time;
                    //            if (timei2 == time1) {
                    //                System.out.print("Problem!!!");
                    //            }
//                                time1++;
                                timei=timei+time;
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
                                        temp1 = CS*Cf5 * (limneg[j] - current_prices_volumes[j]);
                                        temp2 = current_prices_volumes[j] + temp1;
                                        current_prices_volumes[j] = temp2;
                                      
                                    }
                                }
                            }
                        else {
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
                                        temp1 = CS*Cf[j] * (limneg[j] - current_prices_volumes[j]);
                                        temp2 = current_prices_volumes[j] + temp1;
                                        current_prices_volumes[j] = temp2;
                                    } else {
                                        double max = Math.max(((received_history.get(received_history.size() - 2)[j] / received_history.get(received_history.size() - 1)[j]) * current_prices_volumes[j]), initPrice[j]);
                                        temp1 = Math.min(max, limneg[j]);
                                        current_prices_volumes[j] = temp1;
                                    }
                                }
                            }
                        else {
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
                                        temp1 = CS*Cf[j] * (limneg[j] - current_prices_volumes[j]);
                                        temp2 = current_prices_volumes[j] + temp1;
                                        current_prices_volumes[j] = temp2;
                                    } else {
                                        double max = Math.max(current_prices_volumes[j] + (received_history.get(received_history.size() - 2)[j] - received_history.get(received_history.size() - 1)[j]) + Math.pow(- 1, s) * m, initPrice[j]);
                                        temp1 = Math.min(max, limneg[j]);
                                        current_prices_volumes[j] = temp1;
                                    }
                                }
                            }else {
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
                            temp1 = CS* Cf[j] * (limneg[j] - current_prices_volumes[j]);
                            temp2 = current_prices_volumes[j] + temp1;
                            current_prices_volumes[j] = temp2; 
                             }
                                                   
                           }else {
                           if (str.equals(s10)) {
                               
                             //Inverse Tit-For-Tat, "Opponent" Behaviour Strategy:
                               double[] aux = new double[PERIODS];
                               int i=0;
                               double a=0, average=0;
                               
                                 if (n_proposals_received != 1) {
                                received_history.add(tariff_rcv);
                                }
                            
                                for (int j = 0; j < initPrice.length; ++j) {
                                    if (received_history.size() == 1) {
                                        temp1 = CS*Cf[j] * (lim[j] - current_prices_volumes[j]);
                                        temp2 = current_prices_volumes[j] + temp1;
                                        current_prices_volumes[j] = temp2;
                                    } else {
                                        if (i<6){
                                        for (i=0;i<initPrice.length; ++i){
                                        aux[i]=(received_history.get(received_history.size() - 2)[i] / received_history.get(received_history.size() - 1)[i]);
                                        a=a+aux[i];
                                        }
                                        average=a/initPrice.length;
                                        }
                                        aux[j]=average-(aux[j]-average);
                                        double max = Math.max((aux[j] * current_prices_volumes[j]), initPrice[j]);
//                                        System.out.println("\n -2: "+received_history.get(received_history.size() - 2)[j]+"\n -1: "+received_history.get(received_history.size() - 1)[j]);
                                        temp1 = Math.min(max, lim[j]);
                                        current_prices_volumes[j] = temp1;
                                    }
                                }
                               }else {
                           if (str.equals(s11)) {
                        int epsilon=32;
                        for (int j = 0; j < initPrice.length; ++j) {
//                        System.out.println(" /n " +step+" /n " +lim[j]+currentPrice[j]+" /n ");
                        temp1 = Cf1 * (limneg[j]-current_prices_volumes[j])*Math.pow((1.0-((Math.pow(consumer.input_gui.deviation[j+PERIODS]*consumer.sharing_risk,2))/(initPrice[j]))),epsilon*lbda);
                        temp2 = current_prices_volumes[j] + temp1;
                        current_prices_volumes[j] = temp2;
                    } 
                           }else{
                                               if (str.equals(s12)) {


                    for (int j = 0; j < PERIODS; ++j) {
                        if(current_prices_volumes[j]!=0){
                        temp1 = 0.2 * CS*(limneg[j] - current_prices_volumes[j]);
                        temp2 = current_prices_volumes[j] + temp1;
                        }else
                        {
                            temp2=0.00;
                        }
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
                         }

                                return current_prices_volumes;
                            } // end strategy
            
            private void intrasigentWeights() {
                    int aux=0,test=-1,test2=-1;
                    double sumweight=0,cftotal=0;
                    double[] auxweight = new double[PERIODS];

                    for (int j=0; j<weight.length; j++){
                    auxweight[j] = weight[j];
                    }
                  for (int j=0; j<weight.length; j++){
                    for (int i=0; i<weight.length; i++){
                        if(i!=j){
                            if (1.3*auxweight[j]< auxweight[i]){
                                weight[j]=auxweight[j];
                                i=PERIODS;
                                if(test==j){
                                    aux--;
                                }
                            }
                            if (i<PERIODS){
                            if ((auxweight[j]>= 1.5*auxweight[i]) ){
                            weight[j]=0;
        //                    Cf[j]=0;
                            if(test!=j){
                            aux++;
                            }
                            test=j;
                            test2=test;
                        }
                        }
                        }
                    }
                }

                   for (int i=0; i<weight.length; i++){
                    sumweight=sumweight+weight[i];
                }
                if (weight.length-aux >1){
                for (int i=0; i<weight.length; i++){
                    if (weight[i]!=(0)){
                        weight[i]=weight[i]/sumweight;
                       cftotal+= 1/weight[i];
                    }
                }for (int i=0; i<weight.length; i++){
                    if (weight[i]!=(0)){
                    Cf[i]=(1/weight[i])/cftotal;
                    }else {
                        Cf[i]=0;
                    }
                }
                }else{
                    for (int i=0; i<weight.length; i++){
                        if (j==i){
                        Cf[test]=0.5;
                    }else{
                          Cf[i]=0;  
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
    
    // <editor-fold defaultstate="collapsed" desc="Purchase Manager OLD">    
    /*    
        public double[] strategy(double[] tariff_rcv, double[] tariff_cmp_previous) {
            double temp1, temp2;
            double temp3, temp4;
            double energyTotal = 0.0;

            //Initial Prices -- Initial Proposal
            if (flag == 0 && !str.equals(s4)) {
                for (int i = 0; i < initPrice.length; ++i) {
                    current_prices_volumes[i] = initPrice[i];
                    current_prices_volumes[i + 6] = energy[i];
                }
                flag = 1;
            } else {

                // The different strategies
                if (str.equals(s1)) {

                    // -----<<< Concession Making Strategy >>>-----
                    double Cf1 = 0.15;
                    for (int j = 0; j < 6; ++j) {
                        temp1 = Cf1 * (lim[j] - current_prices_volumes[j]);
                        temp2 = current_prices_volumes[j] + temp1;
                        current_prices_volumes[j] = temp2;
                        current_prices_volumes[j + 6] = energy[j];
                    }
                }// end if s1
                else {
                    if (str.equals(s2)) {

                        // -----<<< Low-Priority Concession >>>-----
                        for (int j = 0; j < 6; ++j) {
                            temp1 = Cf[j] * (lim[j] - current_prices_volumes[j]);
                            temp2 = current_prices_volumes[j] + temp1;
                            current_prices_volumes[j] = temp2;
                            //adicionado
                            current_prices_volumes[j + 6] = energy[j];
                        }
                    }// end if s2
                    else {
                        if (str.equals(s3)) {

                            // -----<<< E-R Concession Strategy >>>-----
                            for (int i = 0; i < energy.length; ++i) {
                                energyTotal = energyTotal + energy[i];
                            }
                            for (int j = 0; j < 6; ++j) {
                                temp3 = energy[j] / energyTotal;
                                temp4 = Math.exp(-3 * temp3);
                                Cfv[j] = temp4;
                                temp1 = Cfv[j] * (lim[j] - current_prices_volumes[j]);
                                temp2 = current_prices_volumes[j] + temp1;
                                current_prices_volumes[j] = temp2;
                                current_prices_volumes[j + 6] = energy[j];
                            }
                        }// end if s3
                        else {
                            if (str.equals(s4)) {

                                //-----<<< Demand Management Strategy >>>-----
                                CostManagerBuyer cost_manager_buyer = new CostManagerBuyer(tariff_cmp_previous.clone(), tariff_rcv.clone(), minEnergy, maxEnergy, lim, choosen_volumes_history);

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
                                choosen_volumes_history.add(Arrays.copyOfRange(current_prices_volumes, 6, 12));

                            }// end if s4   
                        }
                    }
                }
            }
            return current_prices_volumes;
        } // end strategy

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
/**/
    // </editor-fold>
    
    /**
     * *********************************************
     *
     * Implementation of Demand Management Strategy
     *
     * *********************************************
     *
     */
    
    // <editor-fold defaultstate="collapsed" desc="Cost Manager Buyer"> 
   
   
    public class                    CostManagerBuyer {

        final int                                       PERIODS                 =   consumer.input_gui.PERIODS;
        private HashMap<Double, ArrayList<double[]>>    cost_list               =   new HashMap<>();
        private final Double                            volume_addition_delta   =   3.5;
        private final Double                            volume_sum_delta_search =   2.5;
        private double[]                                current_prices          =   new double[6];
        private double[]                                volumes_limits_min;
        private double[]                                volumes_limits_max;
        private double[]                                seller_current_prices_volumes;
        private double[]                                buyer_previous_prices_volumes;
        private double[]                                buyer_current_volumes;
        private int                                     aux;
        private final double[]                          prices_limits_max;
        ArrayList<double[]>                             volumes_history         =   new ArrayList<>();
        
        
        
        public CostManagerBuyer(double[] buyer_previous_prices_volumes, double[] seller_current_prices_volumes, double[] volumes_limits_min, double[] volumes_limits_max, double[] prices_limits_max, ArrayList<double[]> choosen_volumes_history, int aux) {

            this.prices_limits_max              =   prices_limits_max;
            this.volumes_limits_min             =   volumes_limits_min;
            this.volumes_limits_max             =   volumes_limits_max;
            this.seller_current_prices_volumes  =   seller_current_prices_volumes;
            this.buyer_previous_prices_volumes  =   buyer_previous_prices_volumes;
            this.volumes_history                =   choosen_volumes_history;
            this.aux                            =   aux;

        }

        public double[] execute() {
            calculateNewPrices();
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
                consumer.getGui().updateLog1("Internal error");
                System.exit(0);
            }
        }

        //-----<<< Creates an auxiliar list of possible benefits and respective prices in the range of minimum and maximum volumes >>>-----
        private void createDeltaCostListAux(double[] volumes) {

            double      sum_previous_volumes    =   calculateVolumeSum(volumes);
            double[]    volumes_aux             =   new double[PERIODS];

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

        //-----<<< Calculates the cost of the consumer agent >>>----- 
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

            double[] prices_volumes = new double[2*PERIODS];

            for (int i = 0; i < PERIODS; i++) {
                prices_volumes[i] = current_prices[i];
                prices_volumes[i + PERIODS] = new_volumes[i];
            }
            return prices_volumes;
        }

        //-----<<< Calculates the buyers new prices >>>-----      
        private void calculateNewPrices() {

            for (int i = 0; i < this.current_prices.length; i++) {
                current_prices[i] = buyer_previous_prices_volumes[i] + (0.07 * buyer_previous_prices_volumes[i]);
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
                this.seller_current_prices_volumes[i]           =   publicity_prices[i];
                this.seller_current_prices_volumes[i + PERIODS] =   publicity_volumes[i];
            }

        }

        private void setPreviousCmp(double[] publicity_prices, double[] publicity_volumes) {
            for (int i = 0; i < publicity_prices.length; i++) {
                this.buyer_previous_prices_volumes[i]           =   publicity_prices[i];
                this.buyer_previous_prices_volumes[i + PERIODS] =   publicity_volumes[i];
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
    }// End Cost Manager Consumer
    
    
    
    // </editor-fold>   
    

    private class                   MarketNegotiator extends Behaviour {

        
        private PurchaseManager manager;
        final int           N_ISSUES    =   consumer.input_gui.PERIODS;
        private String      title       =   "Energy Market";
        private String      send        =   "";
        int                 lnght;
        private double[]    max_p       =   new double[N_ISSUES];
        private double[]    max_e       =   new double[N_ISSUES];
        double[]            tariff_rcv  =   new double[N_ISSUES * 2];
        private double[]            tariff_cmp  =   new double[N_ISSUES * 2];
        double[]            weight      =   new double[N_ISSUES];
        //double[]            weight      =   {0.40, 0.50, 0.10, 0.40, 0.50, 0.10};
        ACLMessage          msg         =   null;
        ACLMessage          reply       =   null;
        DecimalFormat       twodecimal  =   new DecimalFormat("0.00");
        Boolean             contact     =   false;
        private double[][]  proposals;
        final double[]      vo;
        private Date        d1;
        int                 aux         =   -1,     aux2    =   -1,     sair    =   0;
        private int         rcvMSG      =   0,      size    =   0;
        double              Crcv        =   0.0,    Ccmp    =   0.0,    Cmax    =   0.0, 
                            Amax        =   0.0,    Dmax    =   0.0,    Arcv    =   0.0, 
                            Acmp        =   0.0,    Drcv    =   0.0,    Dcmp    =   0.0,
                            vototal     =   0;
        
        String[]            choices1        =   {"Send", "Cancel"};
        String[]            choices2        =   {"Accept","Send","New","Withdraw"};
        String[]            choices21       =   {"Accept","Send","Withdraw"};
        String[]            choices3        =   {"Accept","Cancel"};
        String[]            choices4        =   {"OK"};
        DecimalFormat       threedecimal    =   new DecimalFormat("0.000");
        

        public MarketNegotiator(PurchaseManager m, double[] maxp, double[] maxe) {
            super(null);
            manager         =   m;
            max_p           =   maxp;
            max_e           =   maxe;
           // mp            =   new MessageParameters();   
            configOntology  =   new ConfigOntology();
            xmlr            =   new XMLReader(consumer.getLocalName(), "Buyers"); 
            this.vo         =   m.energy;
                                                                                // Pesos definidos pelo volume de compra
            for (int i=0;i < N_ISSUES; ++i){
                vototal  =   vototal +   vo[i];  
            } 
            for (int i=0;i < N_ISSUES; ++i){
                weight[i]   =     vo[i] / vototal;
                weight[i]   =     roundThreeDecimals(weight[i]);
            }
        }
        
 
        
        @Override
        public void action() {
            
           
            
            
                    
      
           /**/
            ACLMessage msgA = myAgent.receive();
            if (msgA != null){
                MessageParameters mp = new MessageParameters();
                mp.getMessageStatus(msgA);     
                if (mp.oCOALITION && mp.prtkNEGOTIATION){
                    if(mp.cINIT_NEGOTIATION){
                        initiateNegotiation(msgA, mp);                        
                    } else if (mp.INFORMID){
                        informPV(msgA, mp);
                    } else if (mp.AVALIATEID){
                        avaliateProposal(msgA, mp);
                    } else if (mp.SUCCESSID || mp.FAILID){
                        terminateProtocol(msgA, mp);
                    } else if (mp.cNEWROUND){
                        newRound();
                    }                    
                }else if( mp.oCOALITION && mp.prtkSSV){
                    resolveSSV(msgA, mp);
                }else if( mp.oCOALITION && mp.prtkSBV){
                    resolveSBV(msgA, mp);
                }else if( mp.oCOALITION && mp.prtkFUM){
                    resolveFUM(msgA, mp);
                }else if(mp.oCOALITION && mp.prtkCONFIG){
                    configOntology.resolve(msg, mp);
                }
                
            } else if (msgA == null && (!contact)){
                /*
                msgA = new ACLMessage(ACLMessage.REQUEST);
                msgA.setOntology("COALITION");
                msgA.setProtocol("NEGOTIATION");
                msgA.setContent("INITIATE NEGOTIATION");
                msgA.addReceiver(consumer.getOpponent());
                msgA.setReplyWith(String.valueOf(System.currentTimeMillis()));
                myAgent.send(msgA);
                contact = true; */
                block();
            }else block(); 
            /**/
            //<editor-fold defaultstate="collapsed" desc="switch">
           /*       switch (step) {
              
              case 0:                                                         // cONTOLOGY * prtkNEGOTIATION * pREQUEST
              step    =   step0();
              break;
              
              case 1:
              step    =   step1();
              break;
              
              case 2:
              step    =   step2();
              break;
              
              case 3:
                mt          =   MessageTemplate.and(coANDnego,AVALIATEID);
                msg = myAgent.receive(mt);
                if (msg == null) {
                  block();
                  break;
                }
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                  if (msg.hasByteSequenceContent()) {
                    try {
                        tariff_rcv = readProposal(msg);
                    } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                    manager.n_proposals_received++;
                  }
                  lnght       =   tariff_rcv.length/2;
                  tariff_cmp  =   (double[]) manager.strategy(tariff_rcv, tariff_cmp);
                  Crcv        =   0;
                  Ccmp        =   0;
                  //if (manager.str.equals(manager.s4)) {

                    for (int i = 0; i < lnght; ++i) {
                        Crcv = Crcv + tariff_rcv[i + lnght] * tariff_rcv[i];
                        Ccmp = Ccmp + tariff_cmp[i + lnght] * tariff_cmp[i];
                    }
                 //}                                                            // CASE A
                 if (Crcv > Cmax) {
                    reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    reply.setContent("OUT OF BOUNDS - Crcv > Cmax - NAY VOTE");
                    myAgent.send(reply);
                    consumer.getGui().updateLog1("OUT OF BOUNDS - Crcv > Cmax - NAY VOTE \n");
                    consumer.getGui().updateLog1("WAITING FOR COLAITION RESPONcE \n");
                    //terminate("FAIL");
                    step = 4;  // Withdrawn negotiation 
                    //terminate("FAIL");
                    break;
                  } else if (Crcv > Ccmp || manager.n_proposals_received <= 1) {
                            if (manager.n_proposals_sent >= 6) {
                                reply = msg.createReply();
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                myAgent.send(reply);
                                consumer.getGui().updateLog1("EXCEDDED NUMBER OF OFFERS - MSG > 6 \n");
                                consumer.getGui().updateLog1("WAITING FOR COALITION RESPONcE \n");
                                step = 4;  // Withdrawn negotiation
                                break;
                            } else {
                                reply = msg.createReply();
                                reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                                reply.setPerformative(ACLMessage.QUERY_IF);
                                reply.setContent("Proposal Below Target Price - Crcv > Ccmp");
                                myAgent.send(reply);
                                consumer.getGui().updateLog1("BELOW TARGET PRICE - REQUEST CONTER-OFFER\n");
                                consumer.getGui().updateLog1("WAITING FOR COALITION RESPONCE \n");
                                step = 6;
                                break;
                            }
                        } else {
                                reply = msg.createReply();
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                reply.setContent("PROPOSAL IS ACCEPTABLE - YAY VOTE");
                                                                          //****************************
                            try {
                                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                                oos2.writeObject(tariff_rcv);
                                oos2.close();
                                reply.setByteSequenceContent(baos2.toByteArray());
                            } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
                            myAgent.send(reply);
                            String info = "\tAccept Received Proposal at:\n";
                            printRcvP(tariff_rcv, info);
                            consumer.getGui().updateLog1("Sent ACCEPT PROPOSAL  Message \n");
                            terminate("SUCCESS");
                            step = 4;
                            break;
                            }
                } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            if (msg.hasByteSequenceContent()) {
                            try {
                            tariff_rcv = readProposal(msg);
                            } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                            }
                            String info = "\tAccept Received Proposal at:\n";
                            printRcvP(tariff_rcv, info);
                            terminate("SUCCESS");
                            step = 4;
                            break;
                 } else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                                    terminate("FAIL");
                 } else if (msg.getPerformative() == ACLMessage.REQUEST){
                     step   =   2;
                     break;
                 }
                //**********************************
                //step    =   step3();
                break;
              case 4:                                                        // wait for coalition ends negotiation
                step    =   step4();
                break;
              
              case 6:
                  
                 step   =   step6(); 
              }
                  
                  
                  
                  */
            //</editor-fold>
        }

        @Override
        public boolean done() {
        return step == 5;
    }
    
        //*********************************************************************
        
        private void                    initiateNegotiation(ACLMessage msg, MessageParameters mp){
            if (mp.pREQUEST){
              
                reply   =   msg.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent("INITIATE NEGOTIATION");
                reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                myAgent.send(reply);
                consumer.getGui().updateLog1("****************************************************************************************************************");
                consumer.getGui().updateLog1("             **                                                     STARTING NEGOTIATION                                                **");
                consumer.getGui().updateLog1("****************************************************************************************************************");
            }  else if(mp.pACCEPT_PROPOSAL){
                    consumer.getGui().updateLog1("****************************************************************************************************************");
                    consumer.getGui().updateLog1("             **                                                     STARTING NEGOTIATION                                                **");
                    consumer.getGui().updateLog1("****************************************************************************************************************");
            }    
            Cmax    =   0; 
            Amax    =   0;
            for (int i = 0; i < max_p.length; i++) {
                Cmax    =   Cmax    +   max_p[i]    *   max_e[i]  *   manager.contractduration;
                Amax    =   Amax    +   (max_p[i]   *   max_e[i]  *   manager.contractduration)   *   weight[i];
            }
            if (manager.PreFunction.equals(manager.p1)) {
                Dmax    =   Amax;   
            }else if (manager.PreFunction.equals(manager.p2)) {
                       Dmax    =   Cmax; 
                }
            if(consumer.risk==1){
                Dmax    =   Cmax;
             }      
            manager.n_proposals_sent = 0;
            block();
        }     
        
        private void                    informPV(ACLMessage msg, MessageParameters mp){
        
            if (mp.pREQUEST && mp.cPRICESVOLUMES){
                // to do inform prices and volumes
               // consumer.sendPricesAndVolumes(msg);
            }else if( mp.pREQUEST && mp.cVOLUMES){
                // to do inform volumes
                
            }
        }
        
        private void                    avaliateProposal(ACLMessage msg, MessageParameters mp){
         
       //<editor-fold defaultstate="collapsed" desc="avaliate old ">
            /*
            if(mp.pPROPOSE){
                if (msg.hasByteSequenceContent()) {
                    try {
                        tariff_rcv  =   readProposal(msg);
                        lnght       =   tariff_rcv.length/2;
                    } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                    String info =   "Evaluating Coalition Proposal...";
                    printRcvP(tariff_rcv, info);               
                    tariff_cmp  =   (double[]) manager.strategy(tariff_rcv, tariff_cmp);
                    Crcv        =   0;
                    Ccmp        =   0;
                    manager.n_proposals_received++;
                    //if (manager.str.equals(manager.s4)) {
                        for (int i = 0; i < lnght; ++i) {
                            Crcv = Crcv + tariff_cmp[i + lnght] * tariff_rcv[i]; //*********** tariff_rcv[i + lnght] *** done to use own power requirments and not ref power from coslition
                            Ccmp = Ccmp + tariff_cmp[i + lnght] * tariff_cmp[i];
                        }
                    //}
                    if (Crcv > Cmax ) {
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        reply.setContent("OUT OF BOUNDS - Crcv > Cmax - NAY VOTE");
                        myAgent.send(reply);
                        terminate("FAIL", msg);
                         block();
                    }else if (Crcv > Ccmp || manager.n_proposals_received <= 1){
                        reply = msg.createReply();
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                        reply.setPerformative(ACLMessage.QUERY_IF);
                        reply.setContent("Proposal Below Target Price - Crcv > Ccmp");
                        myAgent.send(reply);
                        block();
                    }else {
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        reply.setContent("PROPOSAL IS ACCEPTABLE - YAY VOTE");
                        myAgent.send(reply);
                        block();
                        
                    }
                }
             }else if (mp.pREQUEST){
                        calculateProposal(msg, mp);
                    } else {consumer.getGui().updateLog1("ERROR... NO BYTE STRING IN AVALIATE_PROPOSAL()");
               }
               * */
          //</editor-fold>
            
            if(mp.pPROPOSE){
                if (msg.hasByteSequenceContent()) {
                    try {
                        tariff_rcv  =   readProposal(msg);
                        lnght       =   tariff_rcv.length / 2;
                    } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                    
                    if(manager.n_proposals_received <   1){
                        manager.prices_received_from_seller =   tariff_rcv;
                    }
                    if(manager.n_proposals_received >   0){
                        consumer.input_gui.offer(consumer.gui,1);
                    }
                    consumer.getGui().updateLog1("\tReceived Proposal to buy at:");
                    for (int i = 0; i < N_ISSUES ; i++) {
                        consumer.getGui().updateLog1("\tPrice " + (i + 1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy" + (i + 1) + " = " + manager.energy[i]  + "kWh");
                    }
                    consumer.getGui().updateLog1("\tContract Duration: "+Math.round(manager.contractduration)+" days");
                    consumer.getGui().updateLog1("\tTotal Cost Computed= " + threedecimal.format(Ccmp/(1e3))+ " k€");
                    consumer.getGui().updateLog1("\tTotal Cost Received= " + threedecimal.format(Crcv/(1e3))+ " k€\n\n");
                    manager.n_proposals_received++;
                    if (manager.n_proposals_received == 1) {
                        manager.received_history.add(tariff_rcv);
                    }
                }
//                while(consumer.gui.counteroffer ==  0   &&  manager.n_proposals_sent    !=  0){}    ///********************* Redundant !!!!!!!!!!!!!!!!!
                if (manager.n_proposals_sent >= 1) {
                while(consumer.gui.counteroffer ==  0){
                    consumer.checkCO();
                }
                }
                    avaliateCounterOffer(msg, mp);
                
                System.out.println("Estou fora:");
                
            
          
            }else if (mp.pREQUEST){
                        calculateProposal(msg, mp);
                    } else {consumer.getGui().updateLog1("ERROR... NO BYTE STRING IN AVALIATE_PROPOSAL()");
               }    
            }
        

        private void                    avaliateCounterOffer(ACLMessage msg, MessageParameters mp){
                  
                consumer.gui.counteroffer=0;
                consumer.counteroffer=0;
                if(consumer.ES  ==  1   &&   !consumer.input_gui.tactic.equals(consumer.input_gui.sStrategy.getText())){
                    consumer.input_gui.ES(consumer.input_gui.Parent);
                }
                tariff_cmp  =   (double[]) manager.strategy(tariff_rcv, tariff_cmp);
                if (manager.n_proposals_received    >=  1){
                    for (int i = 0; i < manager.initPrice.length; ++i){
//                        if(tariff_rcv[i + N_ISSUES] < manager.minEnergy[i]){
//                                sair    =   4;
//                        }
                    }
                }            
//                    int result = JOptionPane.showConfirmDialog(null, ("\nReceived "+consumer.getOpponent().getLocalName()+ " Proposal "+manager.n_proposals_received+"\n\n                      Received             Send? \n"+"Price 1:           " + twodecimal.format(tariff_rcv[0])+ "                 "+twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " + twodecimal.format(tariff_rcv[1])+ "                 "+twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " + twodecimal.format(tariff_rcv[2])+ "                 "+twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " + twodecimal.format(tariff_rcv[3])+ "                 "+twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " + twodecimal.format(tariff_rcv[4])+ "                 "+twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " + twodecimal.format(tariff_rcv[5])+ "                 "+twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nCost:               " + Math.round(Crcv)+ "                "+Math.round(Ccmp) + "             €\n\n\n"), consumer.getLocalName()+" Send Counter-Proposal "+(manager.n_proposals_sent+1), JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
////                Object proposal="\n\nReceived "+consumer.getOpponent().getLocalName()+ " Proposal "+manager.n_proposals_received+"\n\n                      Received             Send? \n"+"Price 1:           " + twodecimal.format(tariff_rcv[0])+ "                 "+twodecimal.format(tariff_cmp[0]) + "        €/MWh\n"+"Price 2:           " + twodecimal.format(tariff_rcv[1])+ "                 "+twodecimal.format(tariff_cmp[1]) + "        €/MWh\n"+"Price 3:           " + twodecimal.format(tariff_rcv[2])+ "                 "+twodecimal.format(tariff_cmp[2]) + "        €/MWh\n"+"Price 4:           " + twodecimal.format(tariff_rcv[3])+ "                 "+twodecimal.format(tariff_cmp[3]) + "        €/MWh\n"+"Price 5:           " + twodecimal.format(tariff_rcv[4])+ "                 "+twodecimal.format(tariff_cmp[4]) + "        €/MWh\n"+"Price 6:           " + twodecimal.format(tariff_rcv[5])+ "                 "+twodecimal.format(tariff_cmp[5]) + "        €/MWh\n"+"\nCost:               " + twodecimal.format(Crcv/(1e6))+ "                    "+twodecimal.format(Ccmp/(1e6)) + "             M€\n\n\n";
////                      time=System.currentTimeMillis();
                       String[] proposal= new String[N_ISSUES+3];         
                    proposal[0]="Received "+consumer.getOpponent().getLocalName()+ " Proposal "+manager.n_proposals_received;
                    proposal[1]="                    Received            Send? ";
                    Crcv=0;Ccmp=0;Arcv=0;Acmp=0;Drcv=0;Dcmp=0;
                    for (int i = 0; i < N_ISSUES; i++) {
                                                  
                    Crcv = Crcv + tariff_cmp[i + N_ISSUES] * tariff_rcv[i]*manager.contractduration/(1000);
                    Ccmp = Ccmp + tariff_cmp[i + N_ISSUES] * tariff_cmp[i]*manager.contractduration/(1000);
//                    if (manager.n_proposals_sent==0){
                    Arcv=Arcv+(manager.lim[i]-tariff_rcv[i])/(manager.lim[i]-manager.initPrice[i])*weight[i];
                    Acmp=Acmp+(manager.lim[i]-tariff_cmp[i])/(manager.lim[i]-manager.initPrice[i])*weight[i];
                    

//                    }
//                                Arcv=Arcv+(tariff_rcv[i + N_ISSUES] * tariff_rcv[i]*manager.contractduration/(1000))*weight[i];
//                                Acmp=Acmp+(tariff_cmp[i + N_ISSUES] * tariff_cmp[i]*manager.contractduration/(1000))*weight[i];
                            
    
                                      
                    proposal[i+2]="Price "+(i+1)+":           " +twodecimal.format(tariff_rcv[i])+ "                 "+twodecimal.format(tariff_cmp[i]) + "        €/MWh";
                    }
                              if (consumer.risk==0&&manager.PreFunction.equals(manager.p1)) {
                                Drcv=Arcv;
                                Dcmp=Acmp;
                            }else if (consumer.risk==0&&manager.PreFunction.equals(manager.p2)) {
                                Drcv=Crcv;
                                Dcmp=Ccmp; 
                            } 
                                       if(consumer.risk==1&& manager.PreRiskFunction.equals(manager.pr1)){
                                Drcv=Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES;
                                Dcmp=Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES;
                            }else if(consumer.risk==1&& manager.PreRiskFunction.equals(manager.pr2)){
                                for (int i = 0; i < N_ISSUES; ++i) {
                                Drcv = Drcv +((1-Math.exp(manager.lbda*(-tariff_rcv[i]+(consumer.input_gui.price_mec.get(i) +consumer.input_gui.deviation[i+N_ISSUES]))/(consumer.input_gui.deviation[i+N_ISSUES]-consumer.input_gui.deviation[i])))/(1-Math.exp(manager.lbda)))/N_ISSUES; 
                                Dcmp = Dcmp +((1-Math.exp(manager.lbda*(-tariff_cmp[i]+(consumer.input_gui.price_mec.get(i)+consumer.input_gui.deviation[i+N_ISSUES]))/(consumer.input_gui.deviation[i+N_ISSUES]-consumer.input_gui.deviation[i])))/(1-Math.exp(manager.lbda)))/N_ISSUES; 
                               
                            }
                                }else if(consumer.risk==1&& manager.PreRiskFunction.equals(manager.pr3)){
                                     for (int i = 0; i < N_ISSUES; ++i) {
                                   Drcv = Drcv +((1-Math.exp(manager.lbda*(manager.limneg[i]-(tariff_rcv[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))/(manager.limneg[i]-(manager.initPrice[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))))/(1-Math.exp(manager.lbda)))/N_ISSUES;         
                                   Dcmp = Dcmp +((1-Math.exp(manager.lbda*(manager.limneg[i]-(tariff_cmp[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))/(manager.limneg[i]-(manager.initPrice[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))))/(1-Math.exp(manager.lbda)))/N_ISSUES;  
                                     }
                            }
                    proposal[N_ISSUES+2]="Cost:            " + threedecimal.format(Crcv/(1e3))+ "             "+threedecimal.format(Ccmp/(1e3)) + "          k€";
                    if (manager.n_proposals_sent==0){
//                        consumer.utilities.add(Drcv*100);
//                        System.out.println("\nBuyer: Recebe1\n"+100.0*Drcv);
//                        consumer.utilities.add(Dcmp*100);
//                        consumer.utilities.add(100.0); 
                    }
                    aux=-1;
                    aux2=-1;
//                      aux2=consumer.input_gui.inter(proposal,consumer.gui,choices2,1);
                    if (manager.case4==0){
//                           while(consumer.gui.counteroffer==0&&manager.n_proposals_sent!=0){
////                      consumer.gui.stop();
//                            }
//                           consumer.gui.counteroffer=0;
                     
                        
                      aux2=consumer.input_gui.inter2(tariff_cmp,tariff_rcv,Ccmp,Crcv,consumer.gui,choices2,1,manager.n_proposals_received,0,sent_history);
                   
                     for (int i=0; i<2*N_ISSUES;i++){
                        tariff_cmp[i]=Double.valueOf(consumer.input_gui.list3[i]);
                        if(i>=N_ISSUES && consumer.DR==1){
                            consumer.volumes[1][i-N_ISSUES]=tariff_cmp[i];
                        }
                    }
                 
                     
                    }else{
                        aux2=1;
                        
                        manager.case4=0;
                    }
                      time=System.currentTimeMillis();
                           for (int i=0; i<2*N_ISSUES;i++){
                        tariff_cmp[i]=Double.valueOf(consumer.input_gui.list3[i]);
                    }
                

                                   if (aux==1 || aux2==2){
                    reply   =   msg.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    reply.setContent("CANCEL NEGOTIATION");
                    myAgent.send(reply);
                    send="CANCEL NEGOTIATION";
                    consumer.input_gui.finish(consumer.gui,send);
                    send    =   "Received proposal does not meet the minimum acceptable level";
                    terminate("FAIL", msg);
                    block();
                   }
                else if(aux2==0){
                                                   // Accept received proposal
                       consumer.calculatedscore=Acmp*100.0;
                            consumer.getGui().updateLog1("                ");
                            consumer.getGui().updateLog1("****************************************************************************************************************");
                            consumer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                            consumer.getGui().updateLog1("****************************************************************************************************************");

//                            if (manager.str.equals(manager.s4)) {
                                consumer.getGui().updateLog1("\tAccept Received Proposal at:");
                                 for (int i = 0; i < N_ISSUES; i++) {
                                consumer.getGui().updateLog1("\tPrice "+(i+1)+" = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy"+(i+1)+" = " + twodecimal.format(tariff_rcv[N_ISSUES+i]) + "kWh");
                                                         }
                                consumer.getGui().updateLog1("\tContract Duration: "+Math.round(manager.contractduration)+" days\n\n");
//                                consumer.getGui().updateLog1(" ");
                                Ccmp=0;Crcv=0;Arcv=0;Acmp=0;Drcv=0;Dcmp=0;
                            for (int i = 0; i < N_ISSUES; ++i) {
                                Crcv = Crcv + tariff_rcv[i + N_ISSUES] * tariff_rcv[i]*manager.contractduration/(1000);
                                Ccmp = Ccmp + tariff_cmp[i + N_ISSUES] * tariff_cmp[i]*manager.contractduration/(1000);
                               Arcv=Arcv+(manager.lim[i]-tariff_rcv[i])/(manager.lim[i]-manager.initPrice[i]) *weight[i];
                                Acmp=Acmp+(manager.lim[i]-tariff_cmp[i])/(manager.lim[i]-manager.initPrice[i])*weight[i];
                            }
                             
                            if (consumer.risk==0&&manager.PreFunction.equals(manager.p1)) {
                                Drcv=Arcv;
                                Dcmp=Acmp;
                            }else if (consumer.risk==0&&manager.PreFunction.equals(manager.p2)) {
                                Drcv=Crcv;
                                Dcmp=Ccmp; 
                            
                            } if(consumer.risk==1&& manager.PreRiskFunction.equals(manager.pr1)){
                                Drcv=Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES;
                                Dcmp=Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES;
                            }else if(consumer.risk==1&& manager.PreRiskFunction.equals(manager.pr2)){
                                for (int i = 0; i < N_ISSUES; ++i) {
                                Drcv = Drcv +((1-Math.exp(manager.lbda*(-tariff_rcv[i]+(consumer.input_gui.price_mec.get(i) +consumer.input_gui.deviation[i+N_ISSUES]))/(consumer.input_gui.deviation[i+N_ISSUES]-consumer.input_gui.deviation[i])))/(1-Math.exp(manager.lbda)))/N_ISSUES; 
                                Dcmp = Dcmp +((1-Math.exp(manager.lbda*(-tariff_cmp[i]+(consumer.input_gui.price_mec.get(i)+consumer.input_gui.deviation[i+N_ISSUES]))/(consumer.input_gui.deviation[i+N_ISSUES]-consumer.input_gui.deviation[i])))/(1-Math.exp(manager.lbda)))/N_ISSUES; 
                               
                            }
                            }else if(consumer.risk==1&& manager.PreRiskFunction.equals(manager.pr3)){
                                     for (int i = 0; i < N_ISSUES; ++i) {
                                   Drcv = Drcv +((1-Math.exp(manager.lbda*(manager.limneg[i]-(tariff_rcv[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))/(manager.limneg[i]-(manager.initPrice[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))))/(1-Math.exp(manager.lbda)))/N_ISSUES;         
                                   Dcmp = Dcmp +((1-Math.exp(manager.lbda*(manager.limneg[i]-(tariff_cmp[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))/(manager.limneg[i]-(manager.initPrice[i]+consumer.input_gui.deviation[i+N_ISSUES]*consumer.sharing_risk))))/(1-Math.exp(manager.lbda)))/N_ISSUES;  
                                     }
                            }
                            //                                consumer.getGui().updateLog1("******************************");

                        //() STEP =2                *** USER TO DECIDE !!!!!
                    reply = msg.createReply();
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    reply.setContent("PROPOSAL IS ACCEPTABLE - YAY VOTE");
                    myAgent.send(reply);
                    block();
                }                          
                else {                                                         //  *****************
                                reply = msg.createReply();
                                reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                                reply.setPerformative(ACLMessage.QUERY_IF);
                                reply.setContent("Proposal Below Target Price - Crcv > Ccmp");
                                myAgent.send(reply);
                                block();
                                
                            }
    }
    
            
 
        
        private void                    calculateProposal(ACLMessage msg, MessageParameters mp){
            
            //<editor-fold defaultstate="collapsed" desc="calculate proposal old ">
            /*
            reply  =   msg.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setConversationId("PROPOSALS EXCHANGE ID");
            reply.setReplyWith(String.valueOf(System.currentTimeMillis()));          
            if (manager.n_proposals_received == 0) {
                tariff_cmp = (double[]) manager.strategy(tariff_rcv, tariff_cmp);
            }
            try {
                ByteArrayOutputStream baos  = new ByteArrayOutputStream();
                ObjectOutputStream oos      = new ObjectOutputStream(baos);
                oos.writeObject(tariff_cmp);
                oos.close();
                reply.setByteSequenceContent(baos.toByteArray());
            } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
            String info   =   "Sent Proposal to Sell at:\n";
            printRcvP(tariff_cmp, info);
            manager.n_proposals_sent++;
            myAgent.send(reply);
            /**/
            //</editor-fold>
            
            // **********************************************************************
            
            reply  =   msg.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setConversationId("PROPOSALS EXCHANGE ID");
            reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
            
            if (manager.n_proposals_received == 0) {
                while(consumer.gui.counteroffer   ==  0   &&  manager.n_proposals_sent    !=  0){} ///**************Redundant !!!!!!!!!!!!!!!!
                consumer.gui.counteroffer   =   0;
                if(consumer.ES  ==  1   &&   !consumer.input_gui.tactic.equals(consumer.input_gui.sStrategy.getText())){
                    consumer.input_gui.ES(consumer.input_gui.Parent);
                }   
                tariff_cmp = (double[]) manager.strategy(tariff_rcv, tariff_cmp);          
                manager.n_proposals_received++;
            }
            if (manager.n_proposals_sent    <   1   &&  manager.n_proposals_received    <   1) {
                String[] proposal= new String[N_ISSUES + 1];
                proposal[0] =   "          Send First Proposal? ";
                for (int i = 0; i < N_ISSUES ; i++) {
                    proposal[i+1]   =   "Price " + (i + 1) + ":           "   + twodecimal.format(tariff_cmp[i])  +   "        €/MWh";
                }
                aux     =   -1;
                aux2    =   -1;
                Ccmp    =   0;
                Crcv    =   0;
                for (int i  =   0; i    <   N_ISSUES ; i++){
                    Ccmp    =   Ccmp + tariff_cmp[i + N_ISSUES ] * tariff_cmp[i]  *   manager.contractduration    /   (1000);
                }
                aux =   consumer.input_gui.inter2(tariff_cmp,tariff_rcv,Ccmp,0.0,consumer.gui,choices1,0,0,0,sent_history);
                for (int i  =   0; i    <   2 * N_ISSUES ;i++){
                    tariff_cmp[i]   =   Double.valueOf(consumer.input_gui.list3[i]);
                }   
            
            } else{
                String[] proposal   =   new String[N_ISSUES  + 3];         
                proposal[0]         =   "Received "+consumer.getOpponent().getLocalName()+ " Proposal "+manager.n_proposals_received;
                proposal[1]         =   "                    Received            Send? ";
                
                utilityComputation();
                
                for (int i = 0; i < N_ISSUES ; i++) {    
                     proposal[i + 2]   =   "Price "    +   (i+1)   +  ":           " + twodecimal.format(tariff_rcv[i])    +    "                 "    +   twodecimal.format(tariff_cmp[i])    +   "        €/MWh";
                }     
                proposal[N_ISSUES  + 2]   =   "Cost:            " + threedecimal.format(Crcv/(1e3)) + "             " + threedecimal.format(Ccmp / (1e3)) + "          k€";
                if (manager.n_proposals_sent    ==  0){}                        //***************************
                aux     =   -1;
                aux2    =   -1;
                if (manager.case4   ==  0){
                    
                    aux2  =   consumer.input_gui.inter2(tariff_cmp,tariff_rcv,Ccmp,Crcv,consumer.gui,choices2,1,manager.n_proposals_received,0,sent_history);
                    
                    for (int i = 0; i < 2 * N_ISSUES ;i++){
                        tariff_cmp[i]   =   Double.valueOf(consumer.input_gui.list3[i]);
                        if(i    >=  N_ISSUES  &&  consumer.DR ==  1){
                            consumer.volumes[1][i - N_ISSUES ]  =   tariff_cmp[i];
                        }
                    }
                }else{
                    aux2            =   1;
                    manager.case4   =   0;
                }
                time  = System.currentTimeMillis();
                for (int i  =   0; i    <   2   *  N_ISSUES ;i++){
                    tariff_cmp[i]   =   Double.valueOf(consumer.input_gui.list3[i]);
                }
                
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(tariff_cmp);
                oos.close();
                reply.setByteSequenceContent(baos.toByteArray());
            } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
            
            if (aux ==  1   ||  aux2    ==  2){
                consumer.calculatedscore    =   Acmp * 100.0;
                reply   =   msg.createReply();
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                myAgent.send(reply);
                consumer.getGui().updateLog1("\n"/*+consumer.getLocalName()+*/+"You have terminated the negotiation");
                send    =   "You have terminated the negotiation";
                consumer.input_gui.finish(consumer.gui,send);
                step    =   4;
            }else if(aux2  ==  0){
                                                   // Accept received proposal
                consumer.calculatedscore=Acmp*100.0;
                consumer.getGui().updateLog1("                ");
                consumer.getGui().updateLog1("****************************************************************************************************************");
                consumer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                consumer.getGui().updateLog1("****************************************************************************************************************");
                consumer.getGui().updateLog1("\tAccept Received Proposal at:");
                for (int i = 0; i < N_ISSUES ; i++) {
                    consumer.getGui().updateLog1("\tPrice "+(i+1)+" = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy"+(i+1)+" = " + twodecimal.format(tariff_cmp[N_ISSUES  + i]) + "kWh");
                }
                consumer.getGui().updateLog1("\tContract Duration: "+Math.round(manager.contractduration)+" days\n\n");
                Ccmp    =   0;
                Crcv    =   0;
                Arcv    =   0;
                Acmp    =   0;
                Drcv    =   0;
                Dcmp    =   0;
                for (int i = 0; i < N_ISSUES ; ++i) {
                    Crcv    =   Crcv + tariff_cmp[i + N_ISSUES ] * tariff_rcv[i]  * manager.contractduration / (1000);
                    Ccmp    =   Ccmp + tariff_cmp[i + N_ISSUES ] * tariff_cmp[i]  * manager.contractduration / (1000);
                    Arcv    =   Arcv + (manager.lim[i] - tariff_rcv[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                    Acmp    =   Acmp + (manager.lim[i] - tariff_cmp[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                }       
                if (consumer.risk    ==  0   && manager.PreFunction.equals(manager.p1)) {
                    Drcv    =   Arcv;
                    Dcmp    =   Acmp;
                }else if (consumer.risk ==   0  &&  manager.PreFunction.equals(manager.p2)) {
                                Drcv=Crcv;
                                Dcmp=Ccmp; 
                } if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr1)){
                    Drcv    =   Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                    Dcmp    =   Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr2)){
                        for (int i = 0; i < N_ISSUES ; ++i) {
                            Drcv    =   Drcv + ((1 - Math.exp(manager.lbda * (-tariff_rcv[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ; 
                            Dcmp    =   Dcmp + ((1 - Math.exp(manager.lbda * (-tariff_cmp[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;                      
                        }
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr3)){
                        for (int i = 0; i < N_ISSUES ; ++i) {
                            Drcv    =   Drcv + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_rcv[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;         
                            Dcmp    =   Dcmp + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_cmp[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;  
                        }
                }
                consumer.getGui().updateLog1("Total Cost computed: " + threedecimal.format(Ccmp/(1e3))+ " k€");
                consumer.getGui().updateLog1("Total Cost received:     " + threedecimal.format(Crcv/(1e3))+ " k€");
                reply = msg.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                try {
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                    oos2.writeObject(tariff_rcv);
                    oos2.close();
                    reply.setByteSequenceContent(baos2.toByteArray());
                } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
                reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                myAgent.send(reply);
                consumer.getGui().updateLog1("                Sent ACCEPT PROPOSAL  Message");          
                //consumer.addBehaviour(new MessageManager());
                //step = 5;
            }else{
                myAgent.send(reply);
                consumer.utilities.add(Dcmp*100.0);
                consumer.getGui().updateLog1("Sent Proposal to Sell at:"+manager.contract);
                for (int i = 0; i < N_ISSUES ; i++) {
                    consumer.getGui().updateLog1("Price "+(i+1)+" = " + twodecimal.format(tariff_cmp[i]) + "€/MWh " + " Energy"+(i+1)+" = " + twodecimal.format(tariff_cmp[N_ISSUES +i]) + "kWh");
                }
                consumer.getGui().updateLog1("Contract Duration = "+Math.round(manager.contractduration)+" days");
                consumer.getGui().updateLog1("Total Cost computed = " + threedecimal.format(Ccmp/(1e3))+ " k€\n\n");
                manager.n_proposals_sent++;
                block();
            }
            /*
            String info   =   "Sent Proposal to Sell at:\n";
            printRcvP(tariff_cmp, info);
            manager.n_proposals_sent++;
            myAgent.send(reply);
            /**/
       
            //<editor-fold defaultstate="collapsed" desc="CASE2 ">
            /*
            if (manager.n_proposals_received == 0) {
                while(consumer.gui.counteroffer   ==  0   &&  manager.n_proposals_sent    !=  0){}
                consumer.gui.counteroffer   =   0;
                if(consumer.ES  ==  1   &&   !consumer.input_gui.tactic.equals(consumer.input_gui.sStrategy.getText())){
                    consumer.input_gui.ES(consumer.input_gui.Parent);
                }   
                tariff_cmp = (double[]) manager.strategy(tariff_rcv, tariff_cmp);          
                manager.n_proposals_received++;
            }
            if (manager.n_proposals_sent    <   1   &&  manager.n_proposals_received    <   1) {
                String[] proposal= new String[N_ISSUES + 1];
                proposal[0] =   "          Send First Proposal? ";
                for (int i = 0; i < N_ISSUES ; i++) {
                    proposal[i+1]   =   "Price "+(i+1)+":           "   + twodecimal.format(tariff_cmp[i])  +   "        €/MWh";
                }
                aux     =   -1;
                aux2    =   -1;
                Ccmp    =   0;
                Crcv    =   0;
                for (int i  =   0; i    <   N_ISSUES ; i++){
                    Ccmp    =   Ccmp + tariff_cmp[i + N_ISSUES ] * tariff_cmp[i]  *   manager.contractduration    /   (1000);
                }
                aux =   consumer.input_gui.inter2(tariff_cmp,tariff_rcv,Ccmp,0.0,consumer.gui,choices1,0,0,0,sent_history);
                for (int i  =   0; i    <   2 * N_ISSUES ;i++){
                    tariff_cmp[i]   =   Double.valueOf(consumer.input_gui.list3[i]);
                }
            }else{
                String[] proposal   =   new String[N_ISSUES  + 3];         
                proposal[0]         =   "Received "+consumer.getOpponent().getLocalName()+ " Proposal "+manager.n_proposals_received;
                proposal[1]         =   "                    Received            Send? ";
                
                Crcv    =   0;  
                Ccmp    =   0;  
                Arcv    =   0;  
                Acmp    =   0;  
                Drcv    =   0;  
                Dcmp    =   0;
                for (int i = 0; i < N_ISSUES ; i++) {                                  
                    Crcv    =   Crcv    +   tariff_rcv[i + N_ISSUES ] *   tariff_rcv[i]   *   manager.contractduration    /(1000);
                    Ccmp    =   Ccmp    +   tariff_cmp[i + N_ISSUES ] *   tariff_cmp[i]   *   manager.contractduration    /(1000);
                    Arcv    =   Arcv    +   (manager.lim[i] -   tariff_rcv[i])  /   (manager.lim[i] -   manager.initPrice[i])   *   weight[i];
                    Acmp    =   Acmp    +   (manager.lim[i] -   tariff_cmp[i])  /   (manager.lim[i] -   manager.initPrice[i])   *   weight[i];
                    proposal[i + 2]   =   "Price "    +   (i+1)   +  ":           " + twodecimal.format(tariff_rcv[i])    +    "                 "    +   twodecimal.format(tariff_cmp[i])    +   "        €/MWh";
                }
                if (consumer.risk   ==  0   &&  manager.PreFunction.equals(manager.p1)) {
                    Drcv    =   Arcv;
                    Dcmp    =   Acmp;
                }else if (consumer.risk ==  0   &&  manager.PreFunction.equals(manager.p2)) {
                    Drcv    =   Crcv;
                    Dcmp    =   Ccmp; 
                } 
                if(consumer.risk    ==  1   &&  manager.PreRiskFunction.equals(manager.pr1)){
                    Drcv    =   Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                    Dcmp    =   Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr2)){
                    for (int i = 0; i < N_ISSUES ; ++i) {
                        Drcv    =   Drcv    +   ((1 - Math.exp(manager.lbda * (-tariff_rcv[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1-Math.exp(manager.lbda))) / N_ISSUES ; 
                        Dcmp    =   Dcmp    +   ((1 - Math.exp(manager.lbda * (-tariff_cmp[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1-Math.exp(manager.lbda))) / N_ISSUES ; 
                    }
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr3)){
                    for (int i = 0; i < N_ISSUES ; ++i) {
                        Drcv    =   Drcv    +   ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_rcv[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;         
                        Dcmp    =   Dcmp    +   ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_cmp[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;  
                    }
                }
                proposal[N_ISSUES  + 2]   =   "Cost:            " + threedecimal.format(Crcv/(1e3)) + "             " + threedecimal.format(Ccmp / (1e3)) + "          k€";
                if (manager.n_proposals_sent    ==  0){}
                aux     =   -1;
                aux2    =   -1;
                if (manager.case4   ==  0){
                    aux2  =   consumer.input_gui.inter2(tariff_cmp,tariff_rcv,Ccmp,Crcv,consumer.gui,choices2,1,manager.n_proposals_received,0,sent_history);
                    for (int i = 0; i < 2 * N_ISSUES ;i++){
                        tariff_cmp[i]   =   Double.valueOf(consumer.input_gui.list3[i]);
                        if(i    >=  N_ISSUES  &&  consumer.DR ==  1){
                            consumer.volumes[1][i - N_ISSUES ]  =   tariff_cmp[i];
                        }
                    }
                }else{
                    aux2            =   1;
                    manager.case4   =   0;
                }
                time  = System.currentTimeMillis();
                for (int i  =   0; i    <   2   *  N_ISSUES ;i++){
                    tariff_cmp[i]   =   Double.valueOf(consumer.input_gui.list3[i]);
                }
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(tariff_cmp);
                oos.close();
                reply.setByteSequenceContent(baos.toByteArray());
            } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
            if (aux ==  1   ||  aux2    ==  2){
                consumer.calculatedscore    =   Acmp * 100.0;
                reply = msg.createReply();
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                myAgent.send(reply);
                consumer.getGui().updateLog1("\n"/*+consumer.getLocalName()+*///+"You have terminated the negotiation");
               /**//* send="You have terminated the negotiation";
                consumer.input_gui.finish(consumer.gui,send);
                step=4;
                break;
            } else if(aux2  ==  0){
                                                   // Accept received proposal
                consumer.calculatedscore=Acmp*100.0;
                consumer.getGui().updateLog1("                ");
                consumer.getGui().updateLog1("****************************************************************************************************************");
                consumer.getGui().updateLog1("             **                                                     TERMINATING NEGOTIATION                                                **");
                consumer.getGui().updateLog1("****************************************************************************************************************");
                consumer.getGui().updateLog1("\tAccept Received Proposal at:");
                for (int i = 0; i < N_ISSUES ; i++) {
                    consumer.getGui().updateLog1("\tPrice "+(i+1)+" = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy"+(i+1)+" = " + twodecimal.format(tariff_rcv[N_ISSUES  + i]) + "kWh");
                }
                consumer.getGui().updateLog1("\tContract Duration: "+Math.round(manager.contractduration)+" days\n\n");
                Ccmp    =   0;
                Crcv    =   0;
                Arcv    =   0;
                Acmp    =   0;
                Drcv    =   0;
                Dcmp    =   0;
                for (int i = 0; i < N_ISSUES ; ++i) {
                    Crcv    =   Crcv + tariff_rcv[i + N_ISSUES ] * tariff_rcv[i]  * manager.contractduration / (1000);
                    Ccmp    =   Ccmp + tariff_cmp[i + N_ISSUES ] * tariff_cmp[i]  * manager.contractduration / (1000);
                    Arcv    =   Arcv + (manager.lim[i] - tariff_rcv[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                    Acmp    =   Acmp + (manager.lim[i] - tariff_cmp[i]) / (manager.lim[i] - manager.initPrice[i]) * weight[i];
                }       
                if (consumer.risk    ==  0   && manager.PreFunction.equals(manager.p1)) {
                    Drcv    =   Arcv;
                    Dcmp    =   Acmp;
                }else if (consumer.risk ==   0  &&  manager.PreFunction.equals(manager.p2)) {
                                Drcv=Crcv;
                                Dcmp=Ccmp; 
                } if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr1)){
                    Drcv    =   Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                    Dcmp    =   Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr2)){
                        for (int i = 0; i < N_ISSUES ; ++i) {
                            Drcv    =   Drcv + ((1 - Math.exp(manager.lbda * (-tariff_rcv[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ; 
                            Dcmp    =   Dcmp + ((1 - Math.exp(manager.lbda * (-tariff_cmp[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;                      
                        }
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr3)){
                        for (int i = 0; i < N_ISSUES ; ++i) {
                            Drcv    =   Drcv + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_rcv[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;         
                            Dcmp    =   Dcmp + ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_cmp[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;  
                        }
                }
                consumer.getGui().updateLog1("Total Cost computed: " + threedecimal.format(Ccmp/(1e3))+ " k€");
                consumer.getGui().updateLog1("Total Cost received:     " + threedecimal.format(Crcv/(1e3))+ " k€");
                reply = msg.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                try {
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
                    oos2.writeObject(tariff_rcv);
                    oos2.close();
                    reply.setByteSequenceContent(baos2.toByteArray());
                } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
                reply.setReplyWith(String.valueOf(System.currentTimeMillis()));
                myAgent.send(reply);
                consumer.getGui().updateLog1("                Sent ACCEPT PROPOSAL  Message");          
                consumer.addBehaviour(new MessageManager());
                step = 5;
                break;
            } else{ 
                myAgent.send(reply);
                consumer.utilities.add(Dcmp*100.0);
                consumer.getGui().updateLog1("Sent Proposal to Sell at:"+manager.contract);
                for (int i = 0; i < N_ISSUES ; i++) {
                    consumer.getGui().updateLog1("Price "+(i+1)+" = " + twodecimal.format(tariff_cmp[i]) + "€/MWh " + " Energy"+(i+1)+" = " + twodecimal.format(tariff_cmp[N_ISSUES +i]) + "kWh");
                }
                consumer.getGui().updateLog1("Contract Duration = "+Math.round(manager.contractduration)+" days");
                consumer.getGui().updateLog1("Total Cost computed = " + threedecimal.format(Ccmp/(1e3))+ " k€\n\n");
                manager.n_proposals_sent++;
                step = 3;
                block();
                break;
            }
            /**/
            //</editor-fold>
                 
          
          
          
        }
        
        private void                    terminateProtocol(ACLMessage msg, MessageParameters mp){
            if(mp.SUCCESSID){
                terminate("SUCCESS", msg);                
            } else if(mp.FAILID){
                terminate("FAIL", msg);
            }
        }
       
      //  <editor-fold defaultstate="collapsed" desc="SSV Methods ">    
        private void                    resolveSSV(ACLMessage msg, MessageParameters mp){
           // double[] tariff_rcv    =    null;
            if(mp.pPROPOSE){
                if (msg.hasByteSequenceContent()) {
                    try {
                        tariff_rcv =   readProposal(msg);
                        lnght   =   tariff_rcv.length/2;
                    } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                    String info =   "Evaluating SSV Proposal...";
                    printRcvP(tariff_rcv, info);           
                    /*                    try {
                     * Thread.sleep((long)(Math.random() * 500));
                     * } catch (InterruptedException ex) {
                     * Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);
                     * }*/
                    tariff_cmp  =   (double[]) manager.strategy(tariff_rcv, tariff_cmp);
                    Crcv        =   0;
                    Ccmp        =   0;
                    if (manager.str.equals(manager.s4)) {
                        for (int i = 0; i < lnght; ++i) {
                            Crcv = Crcv + tariff_cmp[i + lnght] * tariff_rcv[i];
                            Ccmp = Ccmp + tariff_cmp[i + lnght] * tariff_cmp[i];
                        }
                    }
                    if (Crcv > Cmax || Crcv > Ccmp) {
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        reply.setContent(" Crcv > Ccmp || Crcv > Cmax - NAY VOTE");
                        myAgent.send(reply);
                        String info1 = "Evaluated Proposal " + msg.getConversationId();
                        consumer.getGui().updateLog1("\t" + info1);
                        block();
                    }else {
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        reply.setContent("PROPOSAL IS ACCEPTABLE - YAY VOTE");
                        myAgent.send(reply);
                        String info1 = "Evaluated Proposal" + msg.getConversationId();
                        consumer.getGui().updateLog1("\t" + info1);
                        block();
                        
                    }
                }
        
            }
        }
      //    </editor-fold>  
        
      //  <editor-fold defaultstate="collapsed" desc="SBV Methods ">    
        private void                    resolveSBV(ACLMessage msg, MessageParameters mp){
           
           // double[][] proposals = null;
            
            if(mp.pINFORM){
                size = Integer.valueOf(msg.getContent());
                proposals = new double[size][];
                
            } else if(mp.pPROPOSE){
                if (msg.hasByteSequenceContent()) {
                    try {
                        tariff_rcv =   readProposal(msg);
                    } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                    proposals[rcvMSG] = tariff_rcv;
                    rcvMSG++;
                    String info =   "Recived SBV Proposal..." + rcvMSG + "/" + String.valueOf(size);
                    consumer.getGui().updateLog1("\t" + info + "\n");    
                }
                if(rcvMSG == size){
                    int[] bordaChoice   =   sortSBV(proposals);
                    sendSBVmsg(bordaChoice, msg.getSender());
                    rcvMSG  =   0;
                }
            }
        }
        
        private int[]                    sortSBV(double[][] proposals){
            
            double[]    cproposal   =   new double[tariff_rcv.length/2];
            double[]    toRank      =   new double[proposals.length];
            double[]    copy        =   new double[proposals.length];      
            int[]       bordaChoice =   new int[proposals.length];
       
            for(int i = 0; i < proposals.length; i++){
                cproposal   =   proposals[i];
//                tariff_cmp  =   (double[]) manager.strategy(cproposal, tariff_cmp);
                Crcv=0;
                for (int j = 0; j < lnght; j++) {
                            Crcv    =   Crcv + manager.energy[j] * cproposal[j];
                }
                toRank[i]   =   Crcv;
            }
            copy    =   (double[])  toRank.clone();
            Arrays.sort(copy);
            for (int j = 0 ; j < toRank.length; j++){
                double target   =   toRank[j];
                bordaChoice[j]  =   (Arrays.binarySearch(copy, target)+ 1);            
            }        
            return bordaChoice;
        }
        
        private void                    sendSBVmsg(int[] bordaChoice, AID destination){
            ACLMessage msgBRD  = new ACLMessage(ACLMessage.INFORM);
            
            msgBRD.addReceiver(destination);
            msgBRD.setOntology("COALITION");
            msgBRD.setProtocol("SBV");
            msgBRD.setConversationId("BORDA VOTES");
            try {
                ByteArrayOutputStream baos  = new ByteArrayOutputStream();
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(bordaChoice);
                    oos.close();
                    msgBRD.setByteSequenceContent(baos.toByteArray());
                }
                
            } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
            
            myAgent.send(msgBRD);
            
            String info     =   "Borda Results:\n";
            consumer.getGui().updateLog1("\t" + info + "\n");  
            for (int i = 0; i < bordaChoice.length; i++){
                consumer.getGui().updateLog1("\t" + bordaChoice[i] + "\n");
            }
            
            /*try {
            ByteArrayOutputStream baos  = new ByteArrayOutputStream();
            ObjectOutputStream oos      = new ObjectOutputStream(baos);
            oos.writeObject(tariff_cmp);
            oos.close();
            reply.setByteSequenceContent(baos.toByteArray());
          } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}*/
            
        }
        
      // </editor-fold>
                
      //  <editor-fold defaultstate="collapsed" desc="FUM Methods ">  
        private void                    resolveFUM(ACLMessage msg, MessageParameters mp){
            
            if(mp.pPROPOSE){
                if(mp.AVALIATEID){
                    if(msg.hasByteSequenceContent()){
                        try {
                             tariff_rcv =   readProposal(msg);
                             lnght      =   tariff_rcv.length/2;
                        } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                        String info =   "Evaluating FUM Price Proposal...";
                        consumer.getGui().updateLog1("\t" + info + "\n");    
                        avaliateFUM(msg, mp);
                        
                    }
                } else if(msg.hasByteSequenceContent()){
                    try {
                             tariff_rcv =   readProposal(msg);
                             lnght   =   tariff_rcv.length/2;
                    } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                    
                }
            } else if(mp.pREQUEST){
                if(msg.hasByteSequenceContent()){
                    try {
                             tariff_rcv =   readProposal(msg);
                             lnght   =   tariff_rcv.length/2;
                    } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                }
                
                newFumProposal(msg);
            }
            
            
        }
        
        
        private void avaliateFUM(ACLMessage msg, MessageParameters mp){
            
            //tariff_cmp  =   (double[]) manager.strategy(tariff_rcv, tariff_cmp);
            Crcv=0;Ccmp=0;
            for (int i = 0; i < tariff_rcv.length; ++i) {
                Crcv = Crcv + tariff_cmp[i + lnght] * tariff_rcv[i]; //*********** tariff_rcv[i + lnght] *** done to use own power requirments and not ref power from coslition
                Ccmp = Ccmp + tariff_cmp[i + lnght] * tariff_cmp[i];
            }/**/
            double[]    avaliated   =   new double[tariff_rcv.length];
            int go  =   0, nogo =   0;
            
            for (int i = 0; i < tariff_rcv.length; ++i) {
                if((tariff_rcv[i] <= roundTwoDecimals(tariff_cmp[i])) || Crcv > Ccmp ){
                     avaliated[i]   =   1;
                     go++;
                }else{
                    avaliated[i]    =   0;
                    nogo++;
                }
            }
            reply = msg.createReply();
            
            
            if (nogo > go) {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                reply.setContent(" NOGO VOTES : " + Integer.toString(nogo)+ "/" + Integer.toString(avaliated.length));
            }else {
               
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent("GO VOTES : " + Integer.toString(go)+"/" + Integer.toString(avaliated.length));           
            }
            reply.setConversationId("FUM RPL");
            myAgent.send(reply);
            String info =   "...DONE...";
            consumer.getGui().updateLog1("\t" + info + "\n");    
            block();
        }

        private void        newFumProposal(ACLMessage msg){
            
          reply  =   msg.createReply();
          reply.setPerformative(ACLMessage.PROPOSE);
          reply.setConversationId("NEW FUM PROPOSAL");
          reply.setReplyWith(String.valueOf(System.currentTimeMillis()));          
          double[] newFumtariff = (double[]) manager.strategy(tariff_rcv, tariff_cmp);       
          try {
            ByteArrayOutputStream baos  = new ByteArrayOutputStream();
              try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                  oos.writeObject(newFumtariff);
              }
            reply.setByteSequenceContent(baos.toByteArray());
          } catch (IOException ex) {Logger.getLogger(MarketNegotiator.class.getName());}
          String info   =   "New FUM Proposal Sent at:\n";
          consumer.getGui().updateLog1("\t" + info + "\n");     
          myAgent.send(reply);
        }
        
      // </editor-fold>
        
        
        private void                    printRcvP(double[] tariff_rcv, String info){
            consumer.getGui().updateLog1("\t" + info);
            int lnght = tariff_rcv.length/2;
            for (int i = 0; i < lnght; i++){
                consumer.getGui().updateLog1   ("\tPrice"  + i + "= " + twodecimal.format(tariff_rcv[i])           + "€/MWh " 
                                            + " Energy" + i + "= " + twodecimal.format(tariff_rcv[i + lnght])   + "kWh");
            }    
            consumer.getGui().updateLog1("\n");
        }
        
        double                          roundTwoDecimals(double num) {
            double result = num * 100;
            result = Math.round(result);
            result = result / 100;
            return result;
        }
               
        private void                    terminate(String type, ACLMessage msg){
            double[] finalVolPrice = null;
            if(msg.hasByteSequenceContent()){
                try {
                         finalVolPrice  =   readProposal(msg);                        
                        } catch (ClassNotFoundException ex) {Logger.getLogger(ConsumerMarketAgent.class.getName()).log(Level.SEVERE, null, ex);}
                        
            }
            switch (type) {
                case "FAIL":
                    String info1 =   "!!! FAIL !!! LAST PROPOSAL !!!\n";
                    printRcvP(finalVolPrice, info1);
                    consumer.getGui().updateLog1("AGENT UNABLE TO REACH AGREMENT - NEGOTIATION FAILED \n");
                    consumer.getGui().updateLog1("WAITING FOR FINAL DECISION FROM COALITION \n");
                    break;
                case "SUCCESS":
                    String info2 =   " *** SUCCESS *** LAST PROPOSAL ***\n";
                    printRcvP(finalVolPrice, info2);
                    consumer.getGui().updateLog1("AGENT NEGOTIATION SUCCESSFULL            \n");
                    consumer.getGui().updateLog1("WAITING FOR FINAL DECISION FROM COALITION \n");
                    break;
            }
        }
        
        private void                    utilityComputation(){
                Crcv    =   0;  
                Ccmp    =   0;  
                Arcv    =   0;  
                Acmp    =   0;  
                Drcv    =   0;  
                Dcmp    =   0;
                for (int i = 0; i < N_ISSUES ; i++) {                                  
                    Crcv    =   Crcv    +   tariff_cmp[i + N_ISSUES ] *   tariff_rcv[i]   *   manager.contractduration    /(1000);
                    Ccmp    =   Ccmp    +   tariff_cmp[i + N_ISSUES ] *   tariff_cmp[i]   *   manager.contractduration    /(1000);
                    Arcv    =   Arcv    +   (manager.lim[i] -   tariff_rcv[i])  /   (manager.lim[i] -   manager.initPrice[i])   *   weight[i];
                    Acmp    =   Acmp    +   (manager.lim[i] -   tariff_cmp[i])  /   (manager.lim[i] -   manager.initPrice[i])   *   weight[i];
                    
                   // in calculateproposal************ proposal[i + 2]   =   "Price "    +   (i+1)   +  ":           " + twodecimal.format(tariff_rcv[i])    +    "                 "    +   twodecimal.format(tariff_cmp[i])    +   "        €/MWh";
                }
                if (consumer.risk   ==  0   &&  manager.PreFunction.equals(manager.p1)) {
                    Drcv    =   Arcv;
                    Dcmp    =   Acmp;
                }else if (consumer.risk ==  0   &&  manager.PreFunction.equals(manager.p2)) {
                    Drcv    =   Crcv;
                    Dcmp    =   Ccmp; 
                } 
                if(consumer.risk    ==  1   &&  manager.PreRiskFunction.equals(manager.pr1)){
                    Drcv    =   Risk.ubuyer(manager.lbda, tariff_rcv, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                    Dcmp    =   Risk.ubuyer(manager.lbda, tariff_cmp, manager.lim, consumer.sharing_risk, consumer.input_gui.deviation,manager.k)/N_ISSUES ;
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr2)){
                    for (int i = 0; i < N_ISSUES ; ++i) {
                        Drcv    =   Drcv    +   ((1 - Math.exp(manager.lbda * (-tariff_rcv[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1-Math.exp(manager.lbda))) / N_ISSUES ; 
                        Dcmp    =   Dcmp    +   ((1 - Math.exp(manager.lbda * (-tariff_cmp[i] + (consumer.input_gui.price_mec.get(i) + consumer.input_gui.deviation[i + N_ISSUES ])) / (consumer.input_gui.deviation[i + N_ISSUES ] - consumer.input_gui.deviation[i]))) / (1-Math.exp(manager.lbda))) / N_ISSUES ; 
                    }
                }else if(consumer.risk  ==  1   &&  manager.PreRiskFunction.equals(manager.pr3)){
                    for (int i = 0; i < N_ISSUES ; ++i) {
                        Drcv    =   Drcv    +   ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_rcv[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;         
                        Dcmp    =   Dcmp    +   ((1 - Math.exp(manager.lbda * (manager.limneg[i] - (tariff_cmp[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)) / (manager.limneg[i] - (manager.initPrice[i] + consumer.input_gui.deviation[i + N_ISSUES ] * consumer.sharing_risk)))) / (1 - Math.exp(manager.lbda))) / N_ISSUES ;  
                    }
                }
            
        }
        
        
    }//End Marcket Negotiator
    
    class                           MessageParameters{              // message geeneral parameters passed by reference
        boolean oCOALITION,    oMARKET;                                                // avoids to have to re-write checking parametres
                                                                                 // everytime we want to use them
        boolean prtkNO,     prtkPING,   prtkNEGOTIATION,  prtkCONTACT,
                prtkCONFIG, prtkDEALDILNE;            // **** private wridraw from class to allow new class file Negotiation
         
        
        boolean cPROPOSE_OFFER, cISBUYER,   cISSELLER,  cINIT_DEADLINE, 
                cEND_DEADLINE,  cINIT_NEGOTIATION,  cPRICESVOLUMES, cVOLUMES, cCOUNTEROFFER,
                cNEWROUND,      cCONFIG;
                
        boolean initSellerC, SUCCESSID, FAILID, AVALIATEID, PROPOSALSID, INFORMID;
        
        boolean pINFORM,    pPROPOSE,    pREQUEST,     pAGREE,   pACCEPT_PROPOSAL, 
                pREJECT_PROPOSAL, pQUERY_IF;          
        //
        private boolean prtkSSV, prtkSBV, prtkFUM;
        
        
        
        private void                                getMessageStatus(ACLMessage msg){                          // all results are True/False
            if (msg.getOntology() != null ){
                oCOALITION          =   msg.getOntology().equals("COALITION");
                oMARKET             =   msg.getOntology().equals("MARKET");
            }    
            if (msg.getProtocol() != null){
                prtkNO              =   msg.getProtocol().equals("NONE");
                prtkPING            =   msg.getProtocol().equals("PING");
                prtkCONFIG          =   msg.getProtocol().equals("CONFIG");
                prtkNEGOTIATION     =   msg.getProtocol().equals("NEGOTIATION");    
                prtkCONTACT         =   msg.getProtocol().equals("CONTACT");
                prtkDEALDILNE       =   msg.getProtocol().equals("DEADLINE");
                prtkSSV             =   msg.getProtocol().equals("SSV");
                prtkSBV             =   msg.getProtocol().equals("SBV");
                prtkFUM             =   msg.getProtocol().equals("FUM");
                
            }
            if (msg.getContent() != null){
                cPROPOSE_OFFER      =   msg.getContent().contains("PROPOSE OFFER");
                cISBUYER            =   msg.getContent().contains(";is_buyer");
                cISSELLER           =   msg.getContent().contains(";is_seller");
                cINIT_DEADLINE      =   msg.getContent().contains("INITIATE DEADLINE DEFINITION");
                cEND_DEADLINE       =   msg.getContent().contains("TERMINATE DEADLINE DEFINITION");
                cINIT_NEGOTIATION   =   msg.getContent().contains("INITIATE NEGOTIATION");    
                cPRICESVOLUMES      =   msg.getContent().contains("PRICES AND VOLUMES");
                cVOLUMES            =   msg.getContent().contains("VOLUMES");    
                cCOUNTEROFFER       =   msg.getContent().contains("REQUEST CONTER OFFER");
                cNEWROUND           =   msg.getContent().contains("NEW NEGOTIATION ROUND");
                cCONFIG             =   msg.getContent().contains("LOAD CONFIG ");
            }
            if (msg.getConversationId() != null) {
                initSellerC         =   msg.getConversationId().contains("SELLER CONTACT ID");
                SUCCESSID           =   msg.getConversationId().contains("NEGOTIATION SUCCESS ID");
                FAILID              =   msg.getConversationId().contains("NEGOTIATION FAILED ID");
                AVALIATEID          =   msg.getConversationId().contains("PROPOSAL TO AVALIATE ID");
                PROPOSALSID         =   msg.getConversationId().contains("PROPOSALS EXCHANGE ID");
                INFORMID            =   msg.getConversationId().contains("INFORM VOLUMES ID");
            }
            
            pINFORM                 =   msg.getPerformative() == ACLMessage.INFORM;  
            pPROPOSE                =   msg.getPerformative() == ACLMessage.PROPOSE;
            pREQUEST                =   msg.getPerformative() == ACLMessage.REQUEST;
            pAGREE                  =   msg.getPerformative() == ACLMessage.AGREE;
            pACCEPT_PROPOSAL        =   msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL; 
            pREJECT_PROPOSAL        =   msg.getPerformative() == ACLMessage.REJECT_PROPOSAL;
            pQUERY_IF               =   msg.getPerformative() == ACLMessage.QUERY_IF;
            
            
            
            //
            
        }
        
        //private void setMessageStatus
        
    }
    
    private double[]                readProposal( ACLMessage msg) throws ClassNotFoundException{
            double[] priceVolStream = null;
            
            if (msg.hasByteSequenceContent()){
                ObjectInputStream ois = null;
                try {
                    byte[] byteSequenceContent = msg.getByteSequenceContent();
                    ByteArrayInputStream bais = new ByteArrayInputStream(byteSequenceContent);
                    ois = new ObjectInputStream(bais);
                    priceVolStream = (double[]) ois.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(MarketNegotiator.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        ois.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MarketNegotiator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } 
                
            }    
            
            return priceVolStream;
        }
    
    
                                                                                //initiates a new negotiational round ** diferent coalition strategies***
    private void                    newRound(){
        // save final prices    
        // reload inital prices 
        
    }
  
                                                                                // this claass must be moved to consumer !!!!
                                                                                /* Message Parameters are as folows:
                                                                                 * Protocol - CONFIG
                                                                                 * Ontology - COALITION
                                                                                 * Perfromative - INFORM.
                                                                                 */
    private class                   ConfigOntology{
        
        String type =   "Buyer";
        ArrayList<String>   cfg = new ArrayList<>();   
        
        private void            resolve(ACLMessage msg, MessageParameters mp){
            
            if(mp.cCONFIG){
            
            }
        }
    }
    
    double                          roundThreeDecimals(double d) {
            double result = d * 1000;
            result = Math.round(result);
            result = result / 1000;
            return result;    
        }
}