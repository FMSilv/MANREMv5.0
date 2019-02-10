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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Scorp
 */
public class SellerOntology {
    private CoalitionFront                                  cFront;
    private CoalitionGui                                    cGui;
    //private BackupGui                                       cGui;
    private DesicionProtocol                                dPrtk;
    private double[]                                        tariff_rcv,firstProposal ;
    private HashMap <String, ArrayList<String>>             cBelifs;
    private ArrayList <AID>                                 cMembers;
    private AID                                             cMember;
    private ArrayList<AID>                                  sMembers;
    private CoalitionOntology                               cCoalition;
    private int                                             recivedMsg, numberOfNegotiationPeriods, negotiationVolumes, negotiationRisk;
    private String                                          contractType, contractDuration, Hours;
    DecimalFormat                                           twodecimal;  
    private int                                             sReady;
    ACLMessage                                              msg, msgA;
  
    
    //private MessageParameters                               mp;
    
    public SellerOntology(CoalitionFront cFront, CoalitionGui cGui, DesicionProtocol dPrtk, CoalitionOntology cCoalition){
        this.cFront     =   cFront;
        this.cGui       =   cGui;
        this.dPrtk      =   dPrtk;
        this.cCoalition =   cCoalition;
        cBelifs         =   cFront.getBeliefsOfOthers();
        cMembers        =   cFront.getCoalitionMembers();     
        sMembers        =   cFront.getAvailabelSellers();
        //        sMembers.add(cFront.negotiationSeller);
        twodecimal      =   new DecimalFormat("0.00");  
        recivedMsg      =   0;
        sReady          =   0;    
        firstProposal    =   null;
        
    }
    
    
        public void                                 resolveHello(ACLMessage msg, MessageParameters mp){
        
        if(mp.oMARKET){
            String[] sellerName =   msg.getContent().split(";");            
            AID sellerMember    =   new AID(sellerName[0], AID.ISLOCALNAME);
            cFront.setSeller(sellerMember);
            cGui.addInfoToTextArea("Seller as: " + cFront.negotiationSeller.getLocalName());
        } else if(mp.oContract){
            contractType        =   msg.getContent();
            cGui.addInfoToTextArea("Contract Type: " + contractType);
        } else if(mp.oDay){
            contractDuration    =   msg.getContent();
            cGui.addInfoToTextArea("Contract Duration: " + contractDuration);
        }else if(mp.oInf){
            numberOfNegotiationPeriods  =   Integer.parseInt(msg.getContent());
            cGui.addInfoToTextArea("Negotiation Periods: " + Integer.toString(numberOfNegotiationPeriods));
        }else if(mp.oHour){
            Hours               =   msg.getContent();
        }else if(mp.oVolume){
            negotiationVolumes          =   Integer.parseInt(msg.getContent());
            cGui.addInfoToTextArea("Negotiation Volumes: " + Integer.toString(negotiationVolumes));
        }else if(mp.oRisk){
            negotiationRisk     =   Integer.parseInt(msg.getContent()); 
            cGui.addInfoToTextArea("Negotiation Risk: " + Integer.toString(negotiationRisk));
        }
            
    }
    
      
        public void                                 resolve(ACLMessage msg, MessageParameters mp){
            if(mp.pREQUEST && (mp.cINIT_NEGOTIATION || mp.cInit_Negotiation)){
                startReqReply(msg, mp);
            }else if(mp.pPROPOSE){
               // prices2C(cMembers, msg);
                cFront.rplString    = msg.getReplyWith();           
                if(cFront.CoalNegoStart){
                    try {

                      sendPrices2Coaltion(cMembers, msg);
                      } catch (ClassNotFoundException ex) {
                      Logger.getLogger(SellerOntology.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        firstProposal = recivedSellerProposal(msg);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SellerOntology.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                /**/
                    
            } else if(mp.pACCEPT_PROPOSAL){
                        if (cFront.SellerNegoStart == false){
                            cFront.SellerNegoStart = true;
                            cFront.stateMachine(4,null);                                           // not complete
                            cGui.addInfoToNegotiation("Accepted Negotiation from Coalition ");
                        }else if(cFront.SellerNegoStart && mp.SUCCESSID){
                            cFront.SellerNegoStart = false;
                            cGui.addInfoToNegotiation("***  Negotiation Sucessfull ***");
                            sendAcceptProposal(cMembers, msg);
                            
                        }
            }
        }
                                                                                // Acknowlege Negotiation Request from Seller
        private void                                startReqReply(ACLMessage msg, MessageParameters mp )  {             
            if (mp.pACCEPT_PROPOSAL){
                cGui.addInfoToNegotiation("\n Negotiation Offer Accepted by Seller: " + msg.getSender().getLocalName());
                sReady++;
            }else{
                ACLMessage rpl  =   msg.createReply();
                rpl.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                cGui.addInfoToNegotiation("\n Accepted Negotiation Proposal form Seller"
                        + "\n Waiting for Offer from :" + msg.getSender().getLocalName());
                cFront.send(rpl);
                sReady++;
            }
           
           // if(sReady == sMembers.size()){
              cFront.SellerNegoStart    =   true;
              //cFront.stateMachine(5, null);      
              cFront.stateMachine(6, "INIT COALITION");
              
          //}
            
          cGui.changeTab(2);
        }
        
       
                                                                                // Request Negotiaton to Seller
        public void                                 sendNegoReq2Seller(){
            ACLMessage msg  =   new ACLMessage(ACLMessage.REQUEST);                
            msg.addReceiver(cFront.negotiationSeller);
            msg.setOntology("market_ontology");
            msg.setProtocol("negotiation_protocol");
            msg.setContent("INITIATE NEGOTIATION");
            msg.setReplyWith(String.valueOf(System.currentTimeMillis()));
            cFront.send(msg);
            
 
        }
        
        public void                                 send1stOffer(){
            ACLMessage msg  =   new ACLMessage(ACLMessage.PROPOSE);                
            
            
            msg.setOntology         ("COALITION");
            msg.setProtocol         ("NEGOTIATION");;
            
            msg.setConversationId("1ST PROPOSAL TO AVALIATE ID");
            msg.setReplyWith(String.valueOf(System.currentTimeMillis()));
            
            for (int i =0 ; i < cMembers.size(); i++){
                msg.addReceiver(cMembers.get(i));
            }
            
            
            
            ObjectOutputStream oos  =   null;
            try {    
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(firstProposal);
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
            
            cFront.send(msg);
        }
                                                                                // Proposed value has combined volume for all colaition members
        public void                                 sendProposal2Seller(double[] propStream){
            
            ACLMessage msg  =   new ACLMessage(ACLMessage.PROPOSE);
            
            msg.addReceiver (cFront.negotiationSeller);
            msg.setOntology("market_ontology");
            msg.setProtocol("negotiation_protocol");
            msg.setConversationId("PRICES AND VOLUMES AS BITSTREAM");
            msg.setInReplyTo(cFront.rplString);
            
            ObjectOutputStream oos  =   null;
            try {    
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(propStream);
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
            
            cFront.send(msg);      
            cFront.priceVolIntIndex ++;
            cFront.priceVolIndex    =   Integer.toString(cFront.priceVolIntIndex);
           
        }
        
        private double[]                            recivedSellerProposal( ACLMessage msg) throws ClassNotFoundException{
            double[] priceVolStream     =   null;
            
            if (msg.hasByteSequenceContent()){
                ObjectInputStream ois   =   null;
                try {
                    byte[] byteSequenceContent = msg.getByteSequenceContent();
                    ByteArrayInputStream bais = new ByteArrayInputStream(byteSequenceContent);
                    ois = new ObjectInputStream(bais);
                    priceVolStream = (double[]) ois.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        ois.close();
                    } catch (IOException ex) {
                        Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } 
                
            }    
            
            return priceVolStream;
        }
    
    
        public void                                 prices2C(ArrayList<AID> list, ACLMessage msg){
        
            msgA = new ACLMessage(ACLMessage.INFORM);
            msgA.setOntology("COALITION");
            msgA.setProtocol("PRE_NEGOTIATION");
            msgA.setContent(msg.getContent());
            msgA.setConversationId("REFERENCE PRICES ID");
            for(int i = 0; i < list.size(); i++){
                msgA.addReceiver(list.get(i));
            }
           
            cFront.send(msgA);
       
        }
        
        private void                                sendPrices2Coaltion(ArrayList<AID>  list, ACLMessage msgR) throws ClassNotFoundException{
       
        double[] priceVol = recivedSellerProposal(msgR);
        
        int pricesN = (priceVol.length)/2;  
            cGui.addInfoToNegotiation(msgR.getSender().getLocalName() + " Seller Combined Proposal: \n");
            String recivedPrices      = "Prices" + cFront.priceVolIndex;
            String recivedVolumes     = "Volumes"+ cFront.priceVolIndex;
        for (int i = 0; i < pricesN; i++){
            cGui.addInfoToNegotiation("\tPrice " + (i+1) + " = " + twodecimal.format(priceVol[i]) 
                         + "€/MWh " + " Energy " + (i+1) + " = " + twodecimal.format(priceVol[i+pricesN]) + "kWh");
        }
                                                                                // will not work with changing volmes per coalition member
                                                                                // will require to send individual messages to each member OR
   //     rplString    = msgR.getReplyWith();                                                                           // will require that each emember subtract its energy consumption
                                                                                // form the aggaregate total
        //Double[] memberVolumes = dPrtk.string2Double();
        
        /* for (int i = 0; i < cMembers.size(); i++){
         * 
         * }*/
        //cGui.addInfoToNegotiation("Recived Seller Proposal, Streaming to Coaliton Members.");
       
        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        
        msg.setOntology         ("COALITION");
        msg.setProtocol         ("NEGOTIATION");
        msg.setConversationId   ("PROPOSAL TO AVALIATE ID");
        msg.setReplyWith        (String.valueOf(System.currentTimeMillis()));
        for (int i =0 ; i < list.size(); i++){
            msg.addReceiver(list.get(i));
        }
        
      
        
      
         ObjectOutputStream oos  =   null;
            try {    
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(priceVol);
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
        
        cFront.send(msg);
        cGui.addInfoToNegotiation("Sent Seller Proposal to Members");
        }
        
        private void                                sendAcceptProposal(ArrayList<AID> list, ACLMessage msgR) {
           
            double[] priceVol = null;
            try {
                priceVol = recivedSellerProposal(msgR);
            } catch (ClassNotFoundException ex) {Logger.getLogger(SellerOntology.class.getName()).log(Level.SEVERE, null, ex);}
            
            
            
           
            for(int j = 0; j < cMembers.size(); j++){
                cMember            =   cMembers.get(j);
                ACLMessage msg1 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);       
                msg1.setOntology         ("COALITION");
                msg1.setProtocol         ("NEGOTIATION");
                msg1.setConversationId   ("NEGOTIATION SUCCESS ID");
                msg1.addReceiver(cMember);
                
                String sVolumes    =   cFront.searchPartialBelief(cMember.getLocalName(), "Volumes0:");
                sVolumes           =   dPrtk.splitString(sVolumes);
                Double[] dVolumes  =   dPrtk.string2Double(sVolumes);
             
                if(priceVol.length != 0){
                    int pricesN = (priceVol.length)/2;                               // local variable used to dived the bitstream in prices and volumes
                    //cGui.addInfoToNegotiation("Proposal to buy at: \n");
                    //recivedPrices      = "Prices" + cFront.priceVolIndex;
                    //recivedVolumes     = "Volumes"+ cFront.priceVolIndex;
                    for (int i = 0; i < pricesN; i++){
                        //cGui.addInfoToNegotiation("\tPrice" + (i+1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy "+ (i+1) + " = " + twodecimal.format(tariff_rcv[i+pricesN]) + "kWh");
                        //String recivedPrices   =   recivedPrices  + ":" + twodecimal.format(tariff_rcv[i]);
                        //String recivedVolumes  =   recivedVolumes + ":" + twodecimal.format(tariff_rcv[i+ pricesN]);
                        priceVol[i + pricesN] = dVolumes[i];
                    }
                }
                ObjectOutputStream oos  =   null;
                try {    
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(baos);
                    oos.writeObject(priceVol);
                    oos.close();
                    msg1.setByteSequenceContent(baos.toByteArray());
                } catch (IOException ex) {
                    Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        oos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
            cFront.send(msg1);
         }
        //*/
        //*********************************************************
    }    
        
}
