/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Coalition;

import Coalition.CoalitionFront.MessageParameters;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Paulo Bonifacio
 */
public class                                DesicionProtocol {

    private ArrayList <AID>                         CoalMemb;
    private CoalitionFront                          coor;
    private CoalitionGui                               cGui;
    private String[]                                price;       
    private String[]                                volume;      
    private Double[][]                              prices;    
    private Double[][]                              volumes;   
    private String                                  PriceF, proposal;
    private int                                     priceQt;
    public int                                      Recievedmsg, index1, index2, n, bordaVotes, go, nogo;
    public int[][]                                  voteDistribution;   
    ArrayList<AID>                                  fumMembers;                 //***************
    private FUMOntology                             fumOnt;
    private SBVOntology                             sbvOnt;
    private SSVOntology                             ssvOnt;
    private SPLMajOntology                          splMajOnt;
    
    
    public                                  DesicionProtocol(CoalitionFront coor, CoalitionGui cGui){
        this.coor = coor;
        this.cGui = cGui;  
                                                                                // ************* 
        CoalMemb            =   coor.getCoalitionMembers();

       
        
    }
    
    public void                             DesicionPtrkManager(String DesicionType){
      
        price               =   new String[CoalMemb.size()];
        volume              =   new String[CoalMemb.size()];
        prices              =   new Double[CoalMemb.size()][CoalMemb.size()];
        volumes             =   new Double[CoalMemb.size()][CoalMemb.size()];
        fumOnt              =   new FUMOntology();                              //*********************
        sbvOnt              =   new SBVOntology();
        ssvOnt              =   new SSVOntology();
        splMajOnt           =   new SPLMajOntology();
        
       
      
        switch (DesicionType) {
            case    "Auction":
                //JOptionPane.showMessageDialog(this, "Not Supported Yet", "Not Supported Yet", JOptionPane.INFORMATION_MESSAGE);
                break;
            case    "Full Unanimity Mediated - FUM":
                fumOnt.run();
                
                break;
            case    "Representative":
                break;
            case    "Similarity Simple Vote - SSV":
                
               
                ssvOnt.Run();
                break;
            case    "Similarity Unanimity Borda - SBV":
                sbvOnt.Run();
              
                break;
            case    "Simple Majority":
               // String proposal =  splMajority(prices, price);
                proposal =  splMajority();
                System.out.println("\n"  +  proposal);
                PriceF = proposal;
                String  finalVstr   = energy2String(aggregateEnergy(volumes));
                System.out.println("\n"+ finalVstr);
                coor.stateMachine(4, null);              
                break;
        }
    }
        
   
    private String                          splMajority(){    
        int     votesCast   =   0;
        int     index       =   0;
        int[]   votes       =   new int[CoalMemb.size()];
        boolean same;
        
        volumesAndPrices();                                                     // arrays of strings and double with prices and volumes
        
        do{ 
            for(int j = 0; j < CoalMemb.size(); j++){
                same = Arrays.equals(prices[index], prices[j]);
                if (same){
                    votes[index] ++;
                    votesCast ++;
                 }
            }
            if(votesCast == CoalMemb.size()){
                break;}    
            index ++;
        }while (index < CoalMemb.size());
              
       int mostVoted = votes[0], finalIndex =0;
       
       for (int i = 1; i < votes.length ; i++){
           if( votes[i] > mostVoted ){
               mostVoted = votes[i];
               finalIndex = i;
           }
       }
        
       return price[finalIndex];
    }
   
   
    private void                            strategyAuction(Double[][] prices, String[] price){
        
    }
   
    public void                             resolveMSG(ACLMessage rmsg, MessageParameters mp){
        if(mp.SSV){
            ssvOnt.Resolve(rmsg, mp);
        } else if(mp.SBV){
            sbvOnt.Resolve(rmsg, mp);
        } else if(mp.FUM){
            fumOnt.Resolve(rmsg, mp);
        } else if(mp.SPLMAJ){
            
        }
        
    }
    
   //</editor-fold>
   
   
   
   // <editor-fold defaultstate="collapsed" desc="methods for scanning/spliting/converting Volumes and Prices ">  
   private String                           getPV(AID cMember, String type){
       String pBelif, pv;
       
       pBelif = coor.searchBelief(cMember.getLocalName(), type);
       pv = splitString(pBelif);
       return pv;
   }
   
                                                                                //splites the contents of a price or volumes string
                                                                                // mark spepart
   public String                            splitString(String str){            
       String[]  splt       =   str.split(":",2);
       
       String    splited    =   splt[1];
       
       return   splited;
   }
 
   public Double[]                          string2Double(String str){
      String[]  splt    = str.split(":");
      Double[]  doubled = new Double[splt.length];
      priceQt = splt.length;
      
      for (int i = 0; i<splt.length; i++){
          //if()
          doubled[i] = Double.parseDouble(splt[i].replaceAll(",", "."));
      }
      return    doubled;
              
   }
   
   private void                             volumesAndPrices(){                 // sets volume and prices as doubçle vector.
                                                                                //  common to all desicion types. 
       
       for (int i =0; i<CoalMemb.size(); i++){
            price[i]    = getPV(CoalMemb.get(i), "Prices" + coor.priceVolIndex);
            volume[i]   = getPV(CoalMemb.get(i), "Volumes" + coor.priceVolIndex);   
            prices[i]   = string2Double(price[i]);     
            volumes[i]  = string2Double(volume[i]);
       }
       
   }            
   
   private double[]                         aggregateEnergy(Double[][] volumes){
       double[]   combinedVol = new double[priceQt];
       
       for (int i = 0; i < priceQt; i++){
           for (int j = 0 ; j < CoalMemb.size(); j++){
                combinedVol[i] = combinedVol[i] + volumes[j][i];
           }
       }
       
       return combinedVol;
   }
   
   private String                           energy2String(double[] vol){
       String volStr   = "volumes";
       
        for (int i = 0; i < vol.length ; i++){
            volStr = volStr + ":" + Double.toString(vol[i]);       
        }
        String finalVolStr = volStr.replaceAll("\\.", ",");
        
        return finalVolStr;
   }
  private String                            double2String(double[] value){
      
      String strValue = Double.toString(value[0]);
       
       for (int i = 1; i < value.length; i++){
           strValue = strValue + ":"+ Double.toString(value[i]);
       }
       return strValue;
   }    /**/
      
   
   public double[]                          msgPrcVolStream (){
       double[] msgStream       =   new double[priceQt*2];
       double[] aggregateEnergy =   aggregateEnergy(volumes);                     // aggregate energy volumes as double ***
       Double[] string2Double   =   string2Double(PriceF);
       
       
       for (int i = 0; i < priceQt ; i ++){
           msgStream[i] =  string2Double[i];
           
           msgStream[i + priceQt] = aggregateEnergy[i]; 
                   
       }
           
       
       return msgStream;
   }
   
   private double[]                         msgStream(int index){                                // may need to be changed just to prices in future work ******
        
        double[] mStream       =   new double[priceQt*2];
        
        for (int i = 0; i < priceQt ; i++){
            mStream[i]  =   prices[index][i];
            mStream[i + priceQt]  =   volumes[index][i];
        }
        
        return mStream;
    }
   
   //</editor-fold>
   
   // <editor-fold defaultstate="collapsed" desc="SSV Methods ">  
 
    
    private class                   SSVOntology{
        
        int[]   voteVector          =   new int[CoalMemb.size()]; 
        int[][] voteDistribution    =   new int[CoalMemb.size()][CoalMemb.size()];
        String  step                =   "A";    
        String  proposal;
        int     Recivedmsg, index1, index2, n;
        
        private void                    Run(){
             switch (step){
                case "A":
                    StepA();
                    break;
                case "B":
                    StepB();
                    break;
            }
        }
        
        private void                    StepA(){
                

           volumesAndPrices();
           Recievedmsg  =   0;
           index1       =   0; 
           index2       =   0;
           n            =   0;

           for (int i = 0; i < CoalMemb.size(); i++){
               double[] mStream     =   msgStream(i); 
               Proposal2Members(CoalMemb, mStream, i);          
           } 
            step = "B";
        }
        
        private void                    StepB(){
            proposal = ReturnProposal();
            PriceF = proposal;
            String  finalVstr   = energy2String(aggregateEnergy(volumes));
            System.out.println("\n"+ finalVstr);
            step = "A";
            coor.stateMachine(4, null);       
        }
        
        private void                    Proposal2Members(ArrayList<AID> destination, double[] priceVol, int proposalnumber){
        
            ACLMessage  msg =   new ACLMessage(ACLMessage.PROPOSE);
            String      pr  =   String.valueOf(proposalnumber);
            msg.setOntology         ("COALITION");
            msg.setProtocol         ("SSV");
            msg.setConversationId   ("SSV - " + pr);
            msg.setReplyWith(String.valueOf(System.currentTimeMillis()));
            for (int i =0 ; i < destination.size(); i++){
                msg.addReceiver(destination.get(i));
            }

            ObjectOutputStream oos  =   null;
                try {    
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(baos);
                    oos.writeObject(priceVol);
                    oos.close();
                    msg.setByteSequenceContent(baos.toByteArray());
                } catch (IOException ex) {Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        oos.close();
                    } catch (IOException ex) {Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);}
                }

            coor.send(msg);
            cGui.addInfoToTextArea("Sent Proposal " + proposalnumber + " to all" );
                                                                                    /*********************************
                                                                                     * NOTE  - This Delay was necessary because this tread
                                                                                     * sometimes blocks (does not happen in fast machines) losing 1 or two of the response 
                                                                                     * msgs from the coalition members
                                                                                     * this leads to a premanent program loop in the recieving of awsers of the proposals
                                                                                     * a better way to circumvent this problem is send messages by proposal and wait for 
                                                                                     * for proposal X answer before next round of msg.
                                                                                     *********************************/                                        
                try {           
                    Thread.sleep((long)(Math.random() * 1000));
                } catch (InterruptedException ex) {Logger.getLogger(DesicionProtocol.class.getName()).log(Level.SEVERE, null, ex);}
        }
        
        private void                    Resolve(ACLMessage rmsg, MessageParameters mp){
            Recievedmsg++;
            if (mp.pACCEPT_PROPOSAL){
                voteDistribution[index1][index2]++; //= voteDistribution[index1][index2] + 1;
            }        
            if (Recievedmsg == (CoalMemb.size() + n)){
                index2++;
                index1 = 0;
                n += CoalMemb.size();
            }else{
                index1 ++;
            }  
            if (Recievedmsg == (CoalMemb.size() * CoalMemb.size())){
                StepB();
            }
        }
        
        private int                     SortVotes(){
            int voteIndex       = 0; 
            int mostVoted       = 0;

            for (int i = 0; i < CoalMemb.size(); i++){
               for (int j = 0; j < CoalMemb.size(); j++){
                   if (voteDistribution[j][i] >= 1){
                       voteVector[i]++;
                   }
                }
            }

            for (int i = 0; i < voteVector.length; i++){
                if( voteVector[i] > mostVoted ){
                   mostVoted = voteVector[i];
                   voteIndex = i;
               }
            }
            return voteIndex;
        }
        
        private String                  ReturnProposal(){
            
            int index = SortVotes();
            return price[index];
        }
        
    }
   //</editor-fold>
     
   //  <editor-fold defaultstate="collapsed" desc="SBV Methods ">  
    
   
        
   private class                SBVOntology{
        
        
        int [] msgbordaValue        =   null;
        String      step            =   "A";
        boolean     firstrun        =   true;
        int[]       bordaValue      =   new int[CoalMemb.size()];
        int[][]     voteDistribution;  
        int         Recivedmsg, index1, index2, n;
        String      proposal;
       
        private void         Run(){
           switch (step){
                case "A":
                    StepA();
                    break;
                case "B":
                    StepB();
                    break;
            }
       }
        
        private void         StepA(){
            
            volumesAndPrices();
            voteDistribution    =   new int[CoalMemb.size()][CoalMemb.size()];
     
            Recievedmsg  =   0;
            index1       =   0; 
            index2       =   0;
            n            =   0;
           
            for (int i = 0; i < CoalMemb.size(); i++){    
                Proposal2Member(CoalMemb.get(i));          
             } 
            step = "B";
        }
        
        private void         StepB(){
            PriceF  = proposal;
            String  finalVstr   = energy2String(aggregateEnergy(volumes));
            System.out.println("\n"+ finalVstr);
            step = "A";
            coor.stateMachine(4, null);
        }
        
        private void         Resolve(ACLMessage rmsg, MessageParameters mp){
            


            if (rmsg.hasByteSequenceContent()){
                ObjectInputStream ois   =   null;
                try {
                    byte[] byteSequenceContent  =   rmsg.getByteSequenceContent();
                    ByteArrayInputStream bais   =   new ByteArrayInputStream(byteSequenceContent);
                    ois     =   new ObjectInputStream(bais);
                    try {
                        msgbordaValue   =   (int[]) ois.readObject();
                    } catch (ClassNotFoundException ex) {Logger.getLogger(DesicionProtocol.class.getName()).log(Level.SEVERE, null, ex);}
                } catch (IOException ex) {Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);} 
                finally {try {ois.close();} catch (IOException ex) {Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);}}         
            }  

            for (int i = 0; i < bordaValue.length; i ++){
                  bordaValue[i] = bordaValue[i] + msgbordaValue[i];
                }
            Recievedmsg++;
            
            if (Recievedmsg == CoalMemb.size()){
                Proposal2Seller(bordaValue);
            }
        }
        
        private void         Proposal2Member(AID member){
            
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);                    // frist message contains the number of proposals
            msg.addReceiver(member);
            msg.setOntology         ("COALITION");
            msg.setProtocol         ("SBV");
            msg.setContent(String.valueOf(CoalMemb.size()));
            coor.send(msg);

            for (int i = 0; i < CoalMemb.size(); i ++){ 
                ACLMessage msg1 = new ACLMessage(ACLMessage.PROPOSE);
                msg1.setOntology         ("COALITION");
                msg1.setProtocol         ("SBV");

                String pr = String.valueOf(i);
                msg1.setConversationId   (pr);
                //msg1.setReplyWith(String.valueOf(System.currentTimeMillis()));
                msg1.addReceiver(member);       
                ObjectOutputStream oos  =   null;
                    try {    
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        oos = new ObjectOutputStream(baos);
                        oos.writeObject(msgStream(i));                              ////***********************///
                        oos.close();
                        msg1.setByteSequenceContent(baos.toByteArray());
                    } catch (IOException ex) {Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);} 
                    finally {try {oos.close();} catch (IOException ex) { Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex); }}

                coor.send(msg1);
                cGui.addInfoToTextArea("Sent Proposal " + i + " to "  + CoalMemb.get(i).getLocalName());
            }
        }
        
        
        private void         Proposal2Seller(int[] bordavalue){
            int  minValue   =   bordavalue[0];
            int  point      =   0;
                
            for (int i = 0 ; i < bordavalue.length; i ++){
                if(bordavalue[i] < minValue){  
                    minValue = bordavalue[i];
                    point =i;
                }  
            }
        
//            point = Arrays.binarySearch(bordavalue, minValue);
        
            proposal =   price[point];  
            StepB();
        }
   }
    
    //  </editor-fold>
    
   //  <editor-fold defaultstate="collapsed" desc="FUM Methods ">  
                                                                                /****
                                                                                 * INITIAL FUM IS BASED ON COMPOSED PROPOSAL FOR THE
                                                                                 * LOWEST VALUE FOR EACH TIME/PRICE SLOT.
                                                                                 * EVENTUALLY THERE WILL HAVE TO BE MADE ROOM TO INCULDE FLEXIBILITY FOR EACH 
                                                                                 * ATRIBUTE.
                                                                                 * NOT CONSIDERING OPPONENT ATTRIBUTE PREFERENCES AT THE TIME!
                                                                                 */

    
    private class                    FUMOntology{
        
        String          step        =   "A";
        boolean         firstrun    =   true;
        int             mediatemsg  =   0;
       // ArrayList<AID>  fumMembers;
        String          proposalFum;
        
        private void        run(){
            switch (step){
                case "A":
                    StepA();
                    break;
                case "B":
                    StepB();
                    break;
            }
        }
        
        private void        StepA(){
            
            volumesAndPrices();

            int         proposalsLenght =   prices[0].length; 
                        Recievedmsg     =   0;
                        go              =   0;
                        nogo            =   0;
            double[]    proposalFUMd    =   new double[proposalsLenght];
            double      minValue        =   0;


            if (firstrun){
                    fumMembers      =   CoalMemb;
                    firstrun     =   false;
            }                                           
            for (int i = 0; i < proposalsLenght; i++){
               for(int j = 0; j < fumMembers.size(); j++){
                  minValue         =   prices[j][i];     
                  if(prices[j][i] < minValue){
                      minValue  =   prices[j][i];   
                  } 
               } 
               proposalFUMd[i]  =   minValue;  
            } 

                                                                                    // Price is ATTRIBUTE 1
            Proposal2Members(fumMembers, proposalFUMd, 1);
            proposalFum = double2String(proposalFUMd);
            step = "B";
        }
        
        private void        StepB(){
            PriceF  =   proposalFum;
            String  finalVstr   = energy2String(aggregateEnergy(volumes));
            System.out.println("\n"+ finalVstr);
            step    =   "A";
            coor.stateMachine(4, null);       
        }
              
        private void        Resolve(ACLMessage msg, MessageParameters mp){
            Recievedmsg++;
            if(mp.pACCEPT_PROPOSAL){
                go++;
            }else if(mp.pREJECT_PROPOSAL){
                nogo++;
            }
            if (go == CoalMemb.size()){
                StepB();
            }else if (Recievedmsg == CoalMemb.size() && nogo > 0){
                cGui.addInfoToNegotiation("FUM Negotiation Round " + Integer.toString(mediatemsg + 1) + " Failed ");
                cGui.addInfoToNegotiation("New Proposals Request Sent to Members...");
                Mediate();
            }
         }
     
        private void        Proposal2Members(ArrayList<AID> members, double[] proposals, int attributenumber){
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            String attnmb = String.valueOf(attributenumber);
            msg.setOntology         ("COALITION");
            msg.setProtocol         ("FUM");
            msg.setConversationId   ("PROPOSAL TO AVALIATE ID  - " + attnmb);
            msg.setReplyWith(String.valueOf(System.currentTimeMillis()));
            for (int i =0 ; i < members.size(); i++){
                msg.addReceiver(members.get(i));
            }

            ObjectOutputStream oos  =   null;
                try {    
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(baos);
                    oos.writeObject(proposals);
                    oos.close();
                    msg.setByteSequenceContent(baos.toByteArray());
                } catch (IOException ex) {
                    Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        oos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            coor.send(msg);
            cGui.addInfoToTextArea("Sent Attribute " + attributenumber + " to all" );
                                                                                    /*********************************
                                                                                     * NOTE  - This Delay was necessary because this tread
                                                                                     * sometimes blocks (does not happen in fast machines) losing 1 or two of the response 
                                                                                     * msgs from the coalition members
                                                                                     * this leads to a premanent program loop in the recieving of awsers of the proposals
                                                                                     * a better way to circumvent this problem is send messages by proposal and wait for 
                                                                                     * for proposal X answer before next round of msg.
                                                                                     *********************************/                                        
                try {           
                    Thread.sleep((long)(Math.random() * 1000));
                } catch (InterruptedException ex) {
                    Logger.getLogger(DesicionProtocol.class.getName()).log(Level.SEVERE, null, ex);
                }

        }
        
        private void        Mediate(){
            
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); 
            msg.setOntology         ("COALITION");
            msg.setProtocol         ("FUM");
            msg.setConversationId   ("REQUEST NEW PROPOSAL ID ");
            msg.setReplyWith(String.valueOf(System.currentTimeMillis()));
            for (int i =0 ; i < CoalMemb.size(); i++){
                msg.addReceiver(CoalMemb.get(i));
            }
            step = "A";
            mediatemsg++;
            coor.send(msg);
            try {           
                    Thread.sleep((long)(Math.random() * 1000));
                } catch (InterruptedException ex) {Logger.getLogger(DesicionProtocol.class.getName()).log(Level.SEVERE, null, ex);}
        }
    }
    
    
    // </editor-fold>
    
   
    
    private class       SPLMajOntology{
   
        int     votesCast   =   0;
        int     index       =   0;
        int[]   votes       =   new int[CoalMemb.size()];
        boolean same;
        
        private void    Run(){
             int mostVoted  =   votes[0], 
                 finalIndex =   0;
            volumesAndPrices();                                                     // arrays of strings and double with prices and volumes
            do{ 
                for(int j = 0; j < CoalMemb.size(); j++){
                    same = Arrays.equals(prices[index], prices[j]);
                    if (same){
                        votes[index] ++;
                        votesCast ++;
                     }
                }
                if(votesCast == CoalMemb.size()){
                    break;}    
                index ++;
            }while (index < CoalMemb.size());

          
           
           for (int i = 1; i < votes.length ; i++){
               if( votes[i] > mostVoted ){
                   mostVoted = votes[i];
                   finalIndex = i;
               }
            }
           Proposal2Seller(finalIndex);
        }
        
        private void Proposal2Seller( int propindex){
            
            PriceF =  price[propindex];
            System.out.println("\n"  +  PriceF);
            String  finalVstr   = energy2String(aggregateEnergy(volumes));
            System.out.println("\n"+ finalVstr);
            coor.stateMachine(4, null);      
        }
        
    }
    
 }
    
    

