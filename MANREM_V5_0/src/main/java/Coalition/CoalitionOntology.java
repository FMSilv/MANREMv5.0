/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Coalition;

import Coalition.CoalitionFront.MessageParameters;
import consumer.ConsumerMarketAgent;
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
public class CoalitionOntology {
    private CoalitionFront                                  cFront;
    private CoalitionGui                                    cGui;
    //private BackupGui                                       cGui; 
    private DesicionProtocol                                dPrtk;
    private double[]                                        tariff_rcv;
    private HashMap <String, ArrayList<String>>             cBelifs;
    private ArrayList <AID>                                 cMembers;
    private SellerOntology                                  cMarketO;
    private int                                             recivedMsg, rAcptmsg, cReady;
    DecimalFormat                                           twodecimal;     
    private int                                             ProposalSize;
    private ArrayList<String>                               Profiles;
    
    
    
    
    public CoalitionOntology(CoalitionFront cFront, CoalitionGui cGui, DesicionProtocol dPrtk, SellerOntology cMarketO){
        this.cFront     =   cFront;
        this.cGui       =   cGui;
        this.dPrtk      =   dPrtk;
        this.cMarketO   =   cMarketO;
      
        cBelifs         =   cFront.getBeliefsOfOthers();
        cMembers        =   cFront.getCoalitionMembers();     
        twodecimal      =   new DecimalFormat("0.00");  
        recivedMsg      =   0;
        rAcptmsg        =   0;
        cReady          =   0;
        Profiles        =   new ArrayList<>();
        
        
    }
    
    public void                 resolve(ACLMessage rmsg, MessageParameters mp){
         if((mp.pREQUEST || mp.pACCEPT_PROPOSAL) && mp.cINIT_NEGOTIATION ){
                System.out.println("\n Inital Negotiaton Request");
                startRequest(rmsg, mp); 
         
         } else if(mp.AVALIATEID){ 
                    avaliateProposal(rmsg, mp);
         } else if (mp.pPROPOSE && mp.PROPOSALSID){
                sortOffers(rmsg);
         }else if(mp.pACCEPT_PROPOSAL && (mp.PROPOSALSID || mp.AVALIATEID) ){
             sortConfirmation(rmsg, mp);
         
                }
       
    }
    
    public void                 resolveStrategy(ACLMessage rmsg, MessageParameters mp){
        
        dPrtk.resolveMSG(rmsg, mp);
        /*if(mp.SSV){
            dPrtk.resolveSSV(rmsg, mp);
        } else if(mp.SBV){
            dPrtk.resolveSBV(rmsg, mp);
        } else if(mp.FUM){
            //fumOnt.Resolve(rmsg, mp);
           // dPrtk.resolveFUM(rmsg, mp);
            dPrtk.resolveMSG(rmsg, mp);
        } /**/
        
    }
    
    public void                 resolveInform(ACLMessage rmsg,MessageParameters mp, ArrayList<AID> list){
        
        if (mp.oCOALITION){
            Profiles.add(rmsg.getContent());                                         // stores consumer profiles to agragate a build a 
        }
        if (Profiles.size() == cMembers.size()){
            if ( !list.isEmpty()  || cFront.negotiationSeller != null){
                prices2S(list, rmsg);
                cFront.informMsg = true;
            }else{
                cGui.addInfoToTextArea("Waiting for Seller to Connect...");
            }
        }
            
            
    }
    
    private void                startRequest(ACLMessage msg, MessageParameters mp)  {
                                                                                                            // must be immproved..... *******
            if (mp.pACCEPT_PROPOSAL){
                 cGui.addInfoToNegotiation("\n Negotiation Offer Accepted by Coalition Member: " + msg.getSender().getLocalName());
                 cReady++;
            }
            else{
                ACLMessage rpl  =   msg.createReply();
                rpl.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                rpl.setContent("INITIATE NEGOTIATION");
                cFront.send(rpl);
                cGui.addInfoToNegotiation("\n Accepted Negotiation Proposal form Coalition Member: " + msg.getSender().getLocalName());
                cReady++;
            }
          if(cReady == cMembers.size()){
              cFront.CoalNegoStart    =   true;
              cFront.stateMachine(5, null);           
          }
        }
    
    private void                avaliateProposal(ACLMessage msg, MessageParameters mp){
        if(mp.pQUERY_IF){
            ACLMessage reply   =   msg.createReply();
            reply.setPerformative(ACLMessage.REQUEST);
            reply.setContent("REQUEST CONTER OFFER");
            cFront.send(reply);
        } else if(mp.pACCEPT_PROPOSAL){
            cGui.addInfoToNegotiation("RECIVED ACCEPT PROPOSAL FROM :" + msg.getSender().getLocalName());
            sortConfirmation(msg, mp);
            // Proposal acepted by member  -  yay votes ++
        } else if(mp.pREJECT_PROPOSAL){
            cGui.addInfoToNegotiation("RECIEVED REJECT PROPOSAL FROM :" + msg.getSender().getLocalName());
            // proposal rejected by member - nay votes ++
        }
        
    }
    
    private void                sortOffers(ACLMessage msg) {
            String recivedPrices, recivedVolumes;
            
            cGui.addInfoToNegotiation("Recived offer from Coalition Member: " + msg.getSender().getLocalName() + "\n");
            try {       
                tariff_rcv = recivedProposal(msg);
            } catch (ClassNotFoundException ex) {Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);}
        
            if(tariff_rcv.length != 0){
                int pricesN = (tariff_rcv.length)/2;                               // local variable used to dived the bitstream in prices and volumes
                cGui.addInfoToNegotiation("Proposal to buy at: \n");
                recivedPrices      = "Prices" + cFront.priceVolIndex;
                recivedVolumes     = "Volumes"+ cFront.priceVolIndex;
                for (int i = 0; i < pricesN; i++){
                    cGui.addInfoToNegotiation("\tPrice" + (i+1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy "+ (i+1) + " = " + twodecimal.format(tariff_rcv[i+pricesN]) + "kWh");
                    recivedPrices   =   recivedPrices  + ":" + twodecimal.format(tariff_rcv[i]);
                    recivedVolumes  =   recivedVolumes + ":" + twodecimal.format(tariff_rcv[i+ pricesN]);
                    
                }
                
                for (int i = 0; i < tariff_rcv.length; i++){
                    cFront.addPriceVolume(msg.getSender().getLocalName(), tariff_rcv[i]);
                }
                cFront.addBelif(msg.getSender().getLocalName(), recivedPrices);           // currently not adding local name to bellif string
                cFront.addBelif(msg.getSender().getLocalName(), recivedVolumes);          // i.e.: Prices:XX.YY instead of: Buyer:Pices:XX.YY
                cGui.addInfoToTextArea(msg.getSender().getLocalName() + "\n");
                cGui.addInfoToTextArea(recivedPrices  + "\n");                            //**************************
                cGui.addInfoToTextArea(recivedVolumes + "\n");
      
                recivedMsg ++;
                if (recivedMsg == cMembers.size()){
                    //cGui.addInfoToTextArea("** Select Best Offer **");
                    selectBestOffer();
                    recivedMsg = 0;                                                    // resets recived messages to
                }
         
            }
      }
            
    private double[]            recivedProposal( ACLMessage msg) throws ClassNotFoundException{
            double[] priceVolStream = null;
            
            if (msg.hasByteSequenceContent()){
                ObjectInputStream ois = null;
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
         
    private void                selectBestOffer(){
            //dPrtk.DesicionPtrkManager("SimpleMajority");
            ProposalSize    =   cMembers.size() * cMembers.size();
            String startegy =   cGui.TeamStrategy;
            dPrtk.DesicionPtrkManager(cGui.TeamStrategy);
            
        }
    
    private void                sortConfirmation(ACLMessage okmsg, MessageParameters mp){
           String recivedPrices, recivedVolumes;
           
           cGui.addInfoToNegotiation("************************************************************");
           cGui.addInfoToNegotiation("Negotiation Acepted by :" + okmsg.getSender().getLocalName());
           cGui.addInfoToNegotiation("************************************************************");
           
          if (okmsg.hasByteSequenceContent()){
            try {       
                  tariff_rcv = recivedProposal(okmsg);
              } catch (ClassNotFoundException ex) {Logger.getLogger(CoalitionFront.class.getName()).log(Level.SEVERE, null, ex);}
            if(tariff_rcv.length != 0){
              int pricesN = (tariff_rcv.length)/2;                               // local variable used to dived the bitstream in prices and volumes
              cGui.addInfoToNegotiation(okmsg.getSender().getLocalName() + " Acepted Proposal to buy at: \n");
              recivedPrices      = "Prices"  + cFront.priceVolIndex;
              recivedVolumes     = "Volumes" + cFront.priceVolIndex;
              for (int i = 0; i < pricesN; i++){
                  cGui.addInfoToNegotiation("\tPrice" + (i+1) + " = " + twodecimal.format(tariff_rcv[i]) + "€/MWh " + " Energy "+ (i+1) + " = " + twodecimal.format(tariff_rcv[i+pricesN]) + "kWh");
                  recivedPrices   =   recivedPrices  + ":" + twodecimal.format(tariff_rcv[i]);
                  recivedVolumes  =   recivedVolumes + ":" + twodecimal.format(tariff_rcv[i+ pricesN]);
              }
              cFront.addBelif(okmsg.getSender().getLocalName(), recivedPrices);           // currently not adding local name to bellif string
              cFront.addBelif(okmsg.getSender().getLocalName(), recivedVolumes);          // i.e.: Prices:XX.YY instead of: Buyer:Pices:XX.YY
            }      
          }
          rAcptmsg ++;
          if (rAcptmsg  == cMembers.size()){
            ACLMessage msg = new ACLMessage (ACLMessage.ACCEPT_PROPOSAL);
            msg.addReceiver(cFront.negotiationSeller);
            msg.setConversationId("NEGOTIATION SUCCESS ID");
            msg.setOntology("market_ontology");
            msg.setProtocol("negotiation_protocol");
            msg.setInReplyTo(cFront.rplString);
            try {
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos;
                 oos = new ObjectOutputStream(baos);
                 oos.writeObject(tariff_rcv);
                 oos.close();
                 msg.setByteSequenceContent(baos.toByteArray());
               } catch (IOException ex) {Logger.getLogger(CoalitionOntology.class.getName()).log(Level.SEVERE, null, ex);}    
            cFront.send(msg); 
            msg.removeReceiver(cFront.negotiationSeller);
            
            msg.setOntology         ("COALITION");
            msg.setProtocol         ("NEGOTIATION");
            msg.setConversationId   ("NEGOTIATION SUCCESS ID");
            for (int i =0 ; i < cMembers.size(); i++){
                msg.addReceiver(cMembers.get(i));
            }
            cFront.send(msg);
          } else{
              cGui.addInfoToNegotiation("Waiting for the Accord of the other Coalition members...");
              if ("Full Unanimity Mediated - FUM".equals(cGui.TeamStrategy)) {
                  dPrtk.fumMembers.remove(okmsg.getSender());
              }
              
              
          }
       }
    
    public void                 prices2S(ArrayList<AID> list, ACLMessage msg){
        
            String newContent;
            
            newContent      =   changeProfileName(msg.getContent());
            ACLMessage msgA =   new ACLMessage(ACLMessage.INFORM);
            msgA.setOntology("market_ontology");
            msgA.setProtocol("no_protocol");
            msgA.setContent(newContent);
            msgA.setConversationId("REFERENCE PRICES ID");
            if ("Auction".equals(cGui.TeamStrategy)){                           // readi to use an auction startegy ** Prices difused by all sellers
                for(int i = 0; i < list.size(); i++){
                    msgA.addReceiver(list.get(i));
                }
            } else if(cFront.negotiationSeller == null){
                    msgA.addReceiver(list.get(0));
            } else{
                    msgA.addReceiver(cFront.negotiationSeller);
            }
            cFront.send(msgA);
        }
    
    private String              changeProfileName(String profile){
        String newProfile, a, b, c ;
        
        String[] profileSplt    =   profile.split(";", 2);
        a                       =   cFront.getAID().getLocalName();
        b                       =   ";";
        c                       =   profileSplt[1];
        
        newProfile              =   a + b + c;
                
        return newProfile;
    }
    
    public void                requestNegotiation(ArrayList<AID> list){
        
        ACLMessage msgC     =   new ACLMessage(ACLMessage.REQUEST );
        msgC.setContent("INITIATE NEGOTIATION");
        msgC.setProtocol("NEGOTIATION");
        msgC.setOntology("COALITION");
        for(int i = 0; i < list.size(); i++){
            msgC.addReceiver(list.get(i));
        }
        
        cFront.send(msgC);
         
    }

}
