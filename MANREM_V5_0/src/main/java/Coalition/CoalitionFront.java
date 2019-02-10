/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Coalition;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Paulo Bonifacio
 */
public class CoalitionFront extends Agent {
    
    //<editor-fold defaultstate="collapsed" desc="Variables Declaration">
    private HashMap <String, ArrayList<String>>                 BeliefsOfOthers;       
    private HashMap <String, ArrayList<Double>>                 PriceVolume;
    private HashMap <AID, ArrayList<ArrayList<ACLMessage>>>     MsgHistoric;
    private HashMap <AID, Integer>                              MsgID;     
    private HashMap <String, ArrayList<Double>>                 Prices;
    private ArrayList<String>                                   ThisAgentBeliefs;
    private ArrayList<String>                                   ThisAgentInfo;
    private ArrayList<AID>                                      CoalitionMembers;
    private ArrayList<AID>                                      AvailableSellers;  
    private CoalitionGui                                        myGui;
    public  HashMap                                             BackupBelifs;
    private AID[]                                               cList;                                                         // List of The Colaition Agents present in the DF - Usage - refreshAgentList()
    private AID                                                 system_agent;
    private ArrayList                                           msgLog;
    public  AID                                                 negotiationSeller;
    private int                                                 state,recivedMsg;
    private String                                              pricesContent;
    private double[]                                            tariff_rcv;
    DecimalFormat twodecimal  =   new DecimalFormat("0.00");    
    private String                                              ProposedDeadline;
    private DesicionProtocol                                    dPrtk;             //*******************
    public  boolean                                             CoalNegoStart, SellerNegoStart, NoOffer, informMsg;
    public  String                                              priceVolIndex, rplString;      // index for price and Volume for belifs
    public  int                                                 priceVolIntIndex;
    private CoalitionOntology                                   cOntology;
    private SellerOntology                                      sOntology;
    private DeadLineOntology                                    DldOntology;
    private ContactOntology                                     Contology;
    private PreNegoOntology                                     PreNOntology;             
   //</editor-fold>
    

   @Override
    protected void                              setup(){
     
       BeliefsOfOthers  =   new HashMap();
       PriceVolume      =   new HashMap();
       BackupBelifs     =   new HashMap();
       ThisAgentBeliefs =   new ArrayList();
       ThisAgentInfo    =   new ArrayList();
       CoalitionMembers =   new ArrayList();
       AvailableSellers =   new ArrayList();
       msgLog           =   new ArrayList();
       myGui            =   new CoalitionGui(this);
       //myGui            =   new BackupGui(this);
       dPrtk            =   new DesicionProtocol(this, myGui);                           //**********************
       cOntology        =   new CoalitionOntology(this, myGui, dPrtk, sOntology);
       sOntology        =   new SellerOntology(this, myGui, dPrtk, cOntology);
       DldOntology      =   new DeadLineOntology();
       Contology        =   new ContactOntology();
       PreNOntology     =   new PreNegoOntology();             
       recivedMsg       =   0;    
       priceVolIntIndex =   0;
       priceVolIndex    =   "0";
       CoalNegoStart    =   false;
       SellerNegoStart  =   false;
       informMsg        =   false;
       NoOffer          =   true;
       system_agent     =   new AID("Market", AID.ISLOCALNAME);
       myGui.setVisible(true);
       addBehaviour(new contactMarket());
       addBehaviour(new MessageManager());
       registerWithDF("Coalition Front" , "Coalition Coordination");
     
       //refreshAgentList("Coalition Member");
      
                                            //**************************
   }
 

    protected void                              takedown(){
        try{
            DFService.deregister(this);
        }catch (FIPAException e){}
        
        System.out.println(getAID().getLocalName() + "... Closing... \n");
        if (myGui != null){
            myGui.setVisible(false);
            myGui.dispose();
        }
    }
    
    public ArrayList<String>                    getMyAgentBeliefs(){
        return this.ThisAgentBeliefs;
    }
    
    public HashMap <String, ArrayList<String>>  getBeliefsOfOthers(){
        return this.BeliefsOfOthers;
    }
    
    public ArrayList<AID>                       getCoalitionMembers(){
        return this.CoalitionMembers;
    }
    
    public ArrayList<AID>                       getAvailabelSellers(){
        return this.AvailableSellers;
    }
    
    public HashMap<String, ArrayList<Double>>   getPriceVolume(){
        return this.PriceVolume;
    }
    
    public void                                 addPriceVolume(String AgentName, Double PriveVolume){
        if(getPriceVolume().containsKey(AgentName)){
            ArrayList<Double> list  =   getPriceVolume().get(AgentName);
            list.add(PriveVolume);
            getPriceVolume().put(AgentName, list);
        }else{
            ArrayList<Double> list  =   new ArrayList<>();
            list.add(PriveVolume);
            getPriceVolume().put(AgentName, list);
        }
    }
        
    public String                               searchBelief(String MemberName, String MemberBelief){
        if (MemberName.equals("myagent")) {
            for (int i = 0; i < getMyAgentBeliefs().size(); i++) {
                if (getMyAgentBeliefs().get(i).contains(MemberBelief)){
                    return getMyAgentBeliefs().get(i);
                }
            }
        } else if (getBeliefsOfOthers().containsKey(MemberName)) {
            for (int i = 0; i < getBeliefsOfOthers().get(MemberName).size(); i++) {
                if (getBeliefsOfOthers().get(MemberName).get(i).contains(MemberBelief)) {
                    return getBeliefsOfOthers().get(MemberName).get(i);
                }
            }
        }
        return null;
    }
    
    public String                               searchPartialBelief(String name, String belief) {
        // Checks if any belifs currently owned by the agent contains the segment in part_belif, if so returns it
        if ((name.equals("myagent") || name.equals(getLocalName())) && !getMyAgentBeliefs().isEmpty()) {
            for (int i = 0; i < getMyAgentBeliefs().size(); i++) {
                if (getMyAgentBeliefs().get(i).contains(belief)) {
                    return getMyAgentBeliefs().get(i);
                }
            }
        } else if (getBeliefsOfOthers().containsKey(name)) {
            for (int i = 0; i < getBeliefsOfOthers().get(name).size(); i++) {
                if (getBeliefsOfOthers().get(name).get(i).contains(belief)) {
                    return getBeliefsOfOthers().get(name).get(i);
                }
            }
        }
        return "-1";
    }
    
    public void                                 addBelif(String AgentName, String AgentBelief){
        if(AgentName.equals("myagent")){
            getMyAgentBeliefs().add(AgentBelief);
        }else if(getBeliefsOfOthers().containsKey(AgentName)){
            ArrayList<String> list = getBeliefsOfOthers().get(AgentName);
            list.add(AgentBelief);
            getBeliefsOfOthers().put(AgentName, list);
        } else{
            ArrayList<String> list = new ArrayList<>();
            list.add(AgentBelief);
            getBeliefsOfOthers().put(AgentName, list);
        }
    }
            
                                                                                // Add new agent to Coalition or Vendor List and to Gui Interface
                                                                                // In - AID Agent Name * Agent Type (String)
    private void                                addNewAgent(AID AgentName, String AgentType){
        switch (AgentType){
            case "buyer":
                this.CoalitionMembers.add(AgentName);
                break;
            case "seller":
                this.AvailableSellers.add(AgentName);
                break;
         }
         myGui.addAgent(AgentName.getLocalName(), AgentType);
    }
    
                                                                                //  Used to Register Agent in the DF
                                                                                // Recives - Service Name * Service Type
    private void                                registerWithDF(String ServiceType, String ServiceName){
                                                                                //  YELLOW PAGES REGISTER ***************
       DFAgentDescription ADcrpt                = new DFAgentDescription();
       ADcrpt.setName(getAID());
       ServiceDescription SDcrpt                = new ServiceDescription();
       SDcrpt.setType(ServiceType);
       SDcrpt.setName(ServiceName);
       ADcrpt.addServices(SDcrpt);
       try{
           DFService.register(this, ADcrpt);  
       } catch(FIPAException e){}
    }
    
  
        
                                                                                // **************************************************************
                                                                                // state machine linked to the software current status
                                                                                // cases 0 to 3 || case 
    public void                                 stateMachine(int state, String tmpStr){
        this.state  =   state;
        switch (state){
            case    0:                                                          //  state  0 - Request inital offer to seller
                ContactSellerState(negotiationSeller);                                                       // we start with an initial fixed volume of energy for all buyers
                break;
            case    1:                                                          //  state 1 - Request offers to coalition members (whitdout inital seller offer)
                ContactCoalitionState(CoalitionMembers);       
                break;
            case    2:                                                          //  state 2 - Sets Deadline for all participants  
                DldOntology.setDeadlineRequest(CoalitionMembers);
                ProposedDeadline = tmpStr;   
                sendConfig2Agents();
                break;
            case    3:                                                          //  state 3 - send price information to coalition
                addBehaviour(new commToCoalition());                        
                myGui.addInfoToNegotiation("Prices Sent To Coalition ..."); 
                break;
            case    4:                                                          //  state 4 - send offer to seller 
                
                if ( SellerNegoStart ){
                    double[] msgPrcVolStream = dPrtk.msgPrcVolStream();
                    sOntology.sendProposal2Seller(msgPrcVolStream);
                    myGui.addInfoToTextArea("Offer sent to Seller...");
                }else {
                    sOntology.sendNegoReq2Seller();
                    myGui.addInfoToNegotiation("Waiting for Seller...");
                    
                }
                               
                break;
           case     5:
               
               if(SellerNegoStart && CoalNegoStart ){
                   sOntology.send1stOffer();
                   //NoOffer = false;
                   //myGui.addInfoToNegotiation("Sent Proposal to Avliate To Members");
               }
               break;
          
           case     6:
               if (tmpStr.equals("INIT COALITION")){
                   cOntology.requestNegotiation(CoalitionMembers);
               }
               break;
        }
    }
                                                                                // Inital com. state for negotiation
                                                                                // requests inital offer to seller
                                                                                // ************ To Behaviour?? ***********
    private void                                ContactSellerState(AID negotiationSeller){  
                                                                                
        ACLMessage sellerMsg = new ACLMessage(ACLMessage.PROPOSE);        
        sellerMsg.addReceiver(negotiationSeller);
        sellerMsg.setReplyWith("msg" + System.currentTimeMillis()); 
        sellerMsg.setConversationId("INITIAL CONTACT ID");
        sellerMsg.setOntology("market_ontology");
        sellerMsg.setProtocol("hello_protocol");
        sellerMsg.setContent(this.getLocalName() + ";opponent_proposal");
        send(sellerMsg);
        myGui.addInfoToNegotiation("Seller Updated as : " + negotiationSeller.getLocalName());      
        
        sellerMsg = new ACLMessage(ACLMessage.PROPOSE);        
        sellerMsg.addReceiver(negotiationSeller);
        sellerMsg.setConversationId("CONTRACT TYPE ID");
        sellerMsg.setOntology("contract_ontology");
        sellerMsg.setProtocol("hello_protocol");
        sellerMsg.setContent("Forward Contract");
        send(sellerMsg);
        
        sellerMsg = new ACLMessage(ACLMessage.PROPOSE);        
        sellerMsg.addReceiver(negotiationSeller);
        sellerMsg.setConversationId("CONTRACT DURATION ID");
        sellerMsg.setOntology("day_ontology");
        sellerMsg.setProtocol("hello_protocol");
        sellerMsg.setContent("365");
        send(sellerMsg);
        
        sellerMsg = new ACLMessage(ACLMessage.PROPOSE);        
        sellerMsg.addReceiver(negotiationSeller);
        sellerMsg.setConversationId("NEGOTIATION PERIODS ID");
        sellerMsg.setOntology("inf_ontology");
        sellerMsg.setProtocol("hello_protocol");
        sellerMsg.setContent(String.valueOf("2"));
        send(sellerMsg);
        
        sellerMsg = new ACLMessage(ACLMessage.PROPOSE);        
        sellerMsg.addReceiver(negotiationSeller);
        sellerMsg.setConversationId("VOLUMES ID");
        sellerMsg.setOntology("volume_ontology");
        sellerMsg.setProtocol("hello_protocol");
        sellerMsg.setContent(String.valueOf("0"));
        send(sellerMsg);
        
        sellerMsg = new ACLMessage(ACLMessage.PROPOSE);        
        sellerMsg.addReceiver(negotiationSeller);
        sellerMsg.setConversationId("RISK ID");
        sellerMsg.setOntology("risk_ontology");
        sellerMsg.setProtocol("hello_protocol");
        sellerMsg.setContent(String.valueOf("0"));
        send(sellerMsg);
        
        
   }      
                                                                                
    public void                                 setSeller(AID seller){          // stets the AID identity of the seller for negotiation pruposes
        negotiationSeller = seller;
        myGui.addInfoToTextArea("Seller as :" + negotiationSeller.getLocalName());
    }      // Machine State 1 - Sets up Pre Negotiation
     
    protected void              setPersonalInfo(ArrayList<String> personal_info) {
        this.ThisAgentInfo  = personal_info;
    }
    
    private void                                sellerOpponentAs( ACLMessage msg){
        String[] content_information     = msg.getContent().split(";");
        setSeller(new AID(content_information[0], AID.ISLOCALNAME));
        
    }
    
    
                                                                                // no DF
    private void                                ContactCoalitionState(ArrayList<AID>  list){
        myGui.addInfoToNegotiation("Setting up Buyers..." );
        ACLMessage cMsg = new ACLMessage(ACLMessage.PROPOSE);
        cMsg.setOntology("COALITION");
        cMsg.setProtocol("HELLO");
        cMsg.setConversationId("INITIAL CONTACT");
        cMsg.setContent(this.getLocalName() + ";opponent_proposal");  
        for (int i =0 ; i < list.size(); i++){
            
            cMsg.addReceiver(list.get(i));
         System.out.println("...Sent..." + i + "\n");
        }
        send(cMsg);
        
        /*
         * ACLMessage sellerMsg = new ACLMessage(ACLMessage.PROPOSE);        
        sellerMsg.addReceiver(negotiationSeller);
        sellerMsg.setReplyWith("msg" + System.currentTimeMillis()); 
        sellerMsg.setConversationId("INITIAL CONTACT ID");
        sellerMsg.setOntology("market_ontology");
        sellerMsg.setProtocol("hello_protocol");
        sellerMsg.setContent(this.getLocalName() + ";opponent_proposal");
        send(sellerMsg);
        myGui.addInfoToNegotiation("Seller Updated as : " + negotiationSeller.getLocalName());    
         */
        
        cMsg = new ACLMessage(ACLMessage.PROPOSE);
        cMsg.setProtocol("HELLO");
        cMsg.setConversationId("CONTRACT TYPE ID");
        cMsg.setOntology("contract_ontology");
        cMsg.setProtocol("hello_protocol");
        cMsg.setContent("Forward Contract");
        for (int i =0 ; i < list.size(); i++){
            
            cMsg.addReceiver(list.get(i));
         System.out.println("...Sent..." + i + "\n");
        }
        send(cMsg);
        
        cMsg = new ACLMessage(ACLMessage.PROPOSE);
        cMsg.setConversationId("CONTRACT DURATION ID");
        cMsg.setOntology("day_ontology");
        cMsg.setProtocol("hello_protocol");
        cMsg.setContent("365");
        for (int i =0 ; i < list.size(); i++){
            
            cMsg.addReceiver(list.get(i));
         System.out.println("...Sent..." + i + "\n");
        }
        send(cMsg);
        
        cMsg = new ACLMessage(ACLMessage.PROPOSE);
        cMsg.setConversationId("NEGOTIATION PERIODS ID");
        cMsg.setOntology("inf_ontology");
        cMsg.setProtocol("hello_protocol");
        cMsg.setContent(String.valueOf("2"));
        for (int i =0 ; i < list.size(); i++){
            
            cMsg.addReceiver(list.get(i));
         System.out.println("...Sent..." + i + "\n");
        }
        send(cMsg);
        
        cMsg = new ACLMessage(ACLMessage.PROPOSE);
        cMsg.setConversationId("VOLUMES ID");
        cMsg.setOntology("volume_ontology");
        cMsg.setProtocol("hello_protocol");
        cMsg.setContent(String.valueOf("0"));
        for (int i =0 ; i < list.size(); i++){
            
            cMsg.addReceiver(list.get(i));
         System.out.println("...Sent..." + i + "\n");
        }
        send(cMsg);
        
        cMsg = new ACLMessage(ACLMessage.PROPOSE);
        cMsg.setConversationId("RISK ID");
        cMsg.setOntology("risk_ontology");
        cMsg.setProtocol("hello_protocol");
        cMsg.setContent(String.valueOf("0")); 
        for (int i =0 ; i < list.size(); i++){
            
            cMsg.addReceiver(list.get(i));
         System.out.println("...Sent..." + i + "\n");
        }
        send(cMsg);
      
    }
    //--------------------------------------

                                                                                    // partial function
    private void                                splitContentToPrices(String content){
        String[]  splt = content.split(";",2);
        String price = splt[1];
        
        pricesContent = this.getLocalName() + ";" + price;    
    }
    
    private void                                loadThisAgentData(){
        
        // eventually read form file
        
        
    }

    
                                                                                // **************************************************************
    /**
     *  Noes On pingColaition() and pingAliveCoalition() 
     *  pingCoalition() executes a new behavior -> pingAliveCoalition() to request all coalition members to send and Alive Type Reply
     *  the first step is to send a REQUEST type message to all coalition members [present in cList - form the DF OR 
     *  in CoalitionMembers - from the ACLMessage Connection list]
     *  the next step is to recive messages of the INFORM type with the Ping-protocol - this is done until all messages recieved - 
     *  i.e. until CoalitonMembers.Size = rCount
     * 
     * The systems as strange behavior - it works in RUNTIME with a sniffer agent active and sniffing all members of the coalition
     * It works in DebugMode [generally]
     * Sniffer agent shows all messages revived processed and replied to BUT sometimes the system [i.e. - CoalitionFront] does not present them all
     * 
     * 
     * 
     */
    
    public void                                 createAgent(String AgentName, String ClassName){                // Creates a new agnet via the platform controller
        PlatformController container = getContainerController();                // In - Agent Name * Agent Type i.e. "buying.Buyer" "selling.Seller" ... 
        try{
            AgentController newBuyer = container.createNewAgent(AgentName,ClassName ,null);
            newBuyer.start();     
        }catch (Exception e){}
        
        // <editor-fold defaultstate="collapsed" desc="AMS Agent creation"> 
        /*
               getContentManager().registerLanguage(new jade.content.lang.sl.SLCodec());
               getContentManager().registerOntology(JADEManagementOntology.getInstance());
               CreateAgent newBuyer = new CreateAgent();
               newBuyer.setAgentName(AgentName);
               newBuyer.setClassName(ClassName);
               newBuyer.setContainer((ContainerID) this.here());
               Action actExpr = new Action(getAMS(), newBuyer);
               ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
               request.addReceiver(getAMS());
               request.setOntology(JADEManagementOntology.getInstance().getName());
               request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
               request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
               try{
                   getContentManager().fillContent(request, actExpr);
                   addBehaviour(new AchieveREInitiator(this, request){
                   @Override
                   protected void handleInform(ACLMessage inform){
                       System.out.println("Agent Created");
                   }
                   @Override
                   protected void handleFailure(ACLMessage failure){
                       System.out.println("Error in Agent Creation");
                   }
               });
               }catch(Codec.CodecException | OntologyException ac){}          
           */
            // </editor-fold>   
    }
    
    
    public void                                 killAgent(String AgentName, String ClassName){
        PlatformController container = getContainerController();
        try{
            AgentController x = container.getAgent(AgentName);
            x.kill();    
            
        }catch (Exception e){}
        ;
       
                                                                                // code missing to remove agent from list ******************
    }
    
    

                                                                               
   class                            MessageManager extends CyclicBehaviour{                     
        @Override
        public void action(){
            ACLMessage msg = myAgent.receive();
            if (msg != null){
                MessageParameters mp = new MessageParameters();
                mp.getMessageStatus(msg);
                
                if (mp.prtkHello && mp.oMarket && mp.cOpponent_Proposal){        
                    sellerOpponentAs(msg);
                    if (!informMsg && CoalitionMembers != null){
                        cOntology.resolveInform(msg, mp, AvailableSellers);
                    }
                                                                    // initial messages     - Contact Protocol  
                }else if (mp.prtkCONTACT || (mp.prtkno && mp.oMarket)){                                            
                    Contology.resolve(msg);
                } else if( mp.prtkHello){
                    sOntology.resolveHello(msg, mp);
                                                                                 // Recive inital prices from seller
                }else if((mp.prtkNO || mp.prtkno)  && (mp.oMARKET || mp.oMarket)){
               
                    PreNOntology.resolve(msg);                                   
                } else if(mp.prtkINFORM && mp.oCOALITION){
                    cOntology.resolveInform(msg, mp, AvailableSellers);                   
                                                                                    // DeadLine definition for Coalition
                }else if((mp.oCOALITION    ||  mp.oMARKET || mp.oMarket)    &&  mp.prtkDEALDILNE){
                    DldOntology.resolve(msg, mp);                                       
                                                                                    // Negotiation Ontology for Coalition members
                }else if(mp.oCOALITION  &&  mp.prtkNEGOTIATION){                    
                    cOntology.resolve(msg, mp);       
                }else if((mp.oMARKET || mp.oMarket) &&  (mp.prtkNEGOTIATION || mp.prtkNegotiation)){
                    sOntology.resolve(msg, mp);         
                }else if(mp.oCOALITION  &&  (mp.prtkSSV || mp.prtkSBV || mp.prtkFUM)){                    
                    cOntology.resolveStrategy(msg, mp);
                }
            } else{ block();}
        }
    }
                                                                                //  methods to filter message contents  used 
                                                                                //  in MessaManager()
   class                            MessageParameters{              // message geeneral parameters passed by reference
        boolean oCOALITION,    oMARKET;                                                // avoids to have to re-write checking parametres
                                                                                 // everytime we want to use them
        boolean prtkNO,     prtkPING,   prtkNEGOTIATION,  prtkCONTACT,    
                prtkDEALDILNE,  prtkSPLMAJ, prtkPRENEGOTIATION, prtkINFORM,
                prtkNegotiation  ;            // **** private wridraw from class to allow new class file Negotiation
         
        
        boolean cPROPOSE_OFFER,  cISBUYER,    cISSELLER,   cINIT_DEADLINE, 
                cEND_DEADLINE,  dldDefPrtk, cINIT_NEGOTIATION,  cInit_Negotiation,
                cOpponent_Proposal;
                
        boolean initSellerC, SUCCESSID, FAILID, AVALIATEID, PROPOSALSID;
        
        boolean pINFORM,    pPROPOSE,    pREQUEST,     pAGREE,   pACCEPT_PROPOSAL, 
                pREJECT_PROPOSAL, pQUERY_IF;                                       
        
        boolean SSV, FUM, SBV, SV, RE, AUCT, SPLMAJ;
        
        //
        boolean prtkSSV, prtkSBV, prtkFUM;
        boolean prtkno;
        boolean prtkHello;
        
        boolean oMarket, oDay, oVolume, oRisk, oHour, oInf, oContract;
        
        
        
        private void                                getMessageStatus(ACLMessage msg){                          // all results are True/False
            if (msg.getOntology() != null ){
                oCOALITION          =   msg.getOntology().equals("COALITION");
                oMARKET             =   msg.getOntology().equals("MARKET");
                oMarket             =   msg.getOntology().equals("market_ontology");
                oDay                =   msg.getOntology().equals("day_ontology");
                oInf                =   msg.getOntology().equals("inf_ontology");
                oHour               =   msg.getOntology().equals("hour_ontology");
                oVolume             =   msg.getOntology().equals("volume_ontology");
                oRisk               =   msg.getOntology().equals("risk_ontology");
                oContract           =   msg.getOntology().equals("contract_ontology");
            }    
            if (msg.getProtocol() != null){
                prtkNO              =   msg.getProtocol().equals("NONE");
                prtkno              =   msg.getProtocol().equals("no_protocol");
                prtkPING            =   msg.getProtocol().equals("Ping_Protocol");
                prtkHello           =   msg.getProtocol().equals("hello_protocol");
                prtkNEGOTIATION     =   msg.getProtocol().equals("NEGOTIATION");
                prtkNegotiation     =   msg.getProtocol().equals("negotiation_protocol");
                prtkCONTACT         =   msg.getProtocol().equals("CONTACT");
                prtkDEALDILNE       =   msg.getProtocol().equals("DEADLINE");
                prtkSSV             =   msg.getProtocol().equals("SSV");
                prtkSBV             =   msg.getProtocol().equals("SBV");
                prtkFUM             =   msg.getProtocol().equals("FUM");
                prtkSPLMAJ          =   msg.getProtocol().equals("SPLMAJ");
                prtkPRENEGOTIATION  =   msg.getProtocol().equals("PRE_NEGOTIATION");
                prtkINFORM          =   msg.getProtocol().equals("INFORM");
                
            }
            if (msg.getContent() != null){
                cPROPOSE_OFFER      =   msg.getContent().contains("PROPOSE OFFER");
                cISBUYER            =   msg.getContent().contains(";is_buyer");
                cISSELLER           =   msg.getContent().contains(";is_seller");
                cINIT_DEADLINE      =   msg.getContent().contains("INITIATE DEADLINE DEFINITION");
                cEND_DEADLINE       =   msg.getContent().contains("TERMINATE DEADLINE DEFINITION");
                cINIT_NEGOTIATION   =   msg.getContent().contains("INITIATE NEGOTIATION");   
                cInit_Negotiation   =   msg.getContent().contains("init_negotiation"); 
                cOpponent_Proposal  =   msg.getContent().contains("opponent_proposal");
            }
            if(msg.getConversationId() != null){
                SUCCESSID           =   msg.getConversationId().contains("NEGOTIATION SUCCESS ID");
                FAILID              =   msg.getConversationId().contains("NEGOTIATION FAILED ID");
                AVALIATEID          =   msg.getConversationId().contains("PROPOSAL TO AVALIATE ID");
                PROPOSALSID         =   msg.getConversationId().contains("PROPOSALS EXCHANGE ID");
                initSellerC         =   msg.getConversationId().equals("SELLER CONTACT ID");
                SSV                 =   msg.getConversationId().contains("SSV");
                FUM                 =   msg.getConversationId().contains("FUM");
                SBV                 =   msg.getConversationId().contains("BORDA VOTES");
                SPLMAJ              =   msg.getConversationId().contains("SPLMAJ");
            }
            
           
            
            pINFORM                 =   msg.getPerformative() == ACLMessage.INFORM;  
            pPROPOSE                =   msg.getPerformative() == ACLMessage.PROPOSE;
            pREQUEST                =   msg.getPerformative() == ACLMessage.REQUEST;
            pAGREE                  =   msg.getPerformative() == ACLMessage.AGREE;
            pACCEPT_PROPOSAL        =   msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL; 
            pREJECT_PROPOSAL        =   msg.getPerformative() == ACLMessage.REJECT_PROPOSAL;
            pQUERY_IF               =   msg.getPerformative() == ACLMessage.QUERY_IF;
            
            
            
           
           
        }
        
        //private void setMessageStatus
        
    }
    
                                                                                //  agent registation and inital contact
    private class                               ContactOntology{
        
        private void                                resolve(ACLMessage msg){
            if (msg.getPerformative()   ==  ACLMessage.INFORM){
                resolveInform(msg);}
            if (msg.getPerformative()   ==  ACLMessage.CFP){
                resolveCFP(msg);}
        }
        
       
        private void                                resolveCFP(ACLMessage msg){
            String cont = msg.getContent();
            if(cont.contains("Propose_Offer") || cont.contains("propose_opponent")){
                addBelif(msg.getSender().getLocalName(), msg.getSender().getLocalName() + " Waiting for Energy Price Offer");}
        }
        
        public void                                 resolveInform(ACLMessage msg){
             String cont = msg.getContent();
            if (cont.contains(";is_buyer") || cont.contains(";is_seller")) {
                String[] content_information = cont.split(";");
                String CoalitionMemberName = content_information[0];
                String CoalitionMemberType = content_information[1].split("_")[1];
                
                if (CoalitionMemberName.equals(msg.getSender().getLocalName())) {
                    String[] content_split = cont.split(";");
                    addBelif(CoalitionMemberName, content_split[0] + ";" + content_split[1]);
                    addBelif(CoalitionMemberName, content_split[0] + ";" + content_split[2]);
                    addBelif(CoalitionMemberName, content_split[0] + ";" + content_split[3]);
                    addBelif(CoalitionMemberName, content_split[0] + ";" + content_split[4]);
                    addBelif(CoalitionMemberName, content_split[0] + ";" + content_split[5]);
                    addBelif(CoalitionMemberName, content_split[0] + ";" + content_split[6]);
                    addNewAgent(new AID(CoalitionMemberName, AID.ISLOCALNAME), CoalitionMemberType);
                }
            }
            if (msg.getOntology().equals("market_ontology") && cont.contains("prices;")){
                sOntology.prices2C(CoalitionMembers, msg);
            }
            
        }
        
     
    }            
    

    
    private class                               PreNegoOntology{
        
        private String initPrices;
        
        private void                                resolve(ACLMessage msg){
            System.out.println(" Recived Base Offer From Seller :" + negotiationSeller.getLocalName());
            initPrices = msg.getContent();                                      // control variable ... to delete
            splitContentToPrices(initPrices);
            if (msg.getContent().contains("prices;")) {
                addBelif(msg.getContent().split(";")[0], msg.getContent());
            }
            myGui.addInfoToNegotiation("Recived Base Offer form Seller");
            sOntology.prices2C(CoalitionMembers, msg);
           
            
            stateMachine(3,null);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="DeadLine Ontology "> 
    private class                               DeadLineOntology{
        private String      DeadLine;
        
        private void                                resolve(ACLMessage msg, MessageParameters mp){
            if(mp.cINIT_DEADLINE && mp.pREQUEST){                                     
               msgDeadLine_1(msg);}
            if(mp.pPROPOSE){
                msgDeadLine_2(msg);}
            if(mp.pINFORM && mp.cEND_DEADLINE && mp.prtkDEALDILNE){
                 msgDeadLine_3(msg);}
            if(mp.pAGREE ){
                setDeadLineStr(msg);}
            if(mp.pACCEPT_PROPOSAL){
                msgDeadLine_3(msg);}
            
        }
        private void                                msgDeadLine_1(ACLMessage msg){
            myGui.addInfoToTextArea("Recived DeadLine Definition Request From: " + msg.getSender().getLocalName());
            ACLMessage createReply = msg.createReply();
            SetupDeadLineState(createReply, msg.getContent());  
        }
        private void                                msgDeadLine_2(ACLMessage msg){
            ACLMessage createReply = msg.createReply();
            DeadLine = msg.getContent();
            createReply.setContent(DeadLine);
            createReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            send(createReply);
        }
        private void                                msgDeadLine_3(ACLMessage msg){
            myGui.addInfoToTextArea("DeadLine Set For " + msg.getSender().getLocalName());           
        }
        private void                                setDeadlineRequest(ArrayList<AID>  list){
            
            ACLMessage msgsetDate = new ACLMessage(ACLMessage.PROPOSE);
            msgsetDate.setOntology("COALITION");
            msgsetDate.setProtocol("PRE_NEGOTIATION");
            msgsetDate.setContent("INITIATE DEADLINE DEFINITION");
            //msgsetDate.setReplyWith(DeadLine);
            for (int i =0 ; i < list.size(); i++){
                msgsetDate.addReceiver(list.get(i));
                System.out.println(i +" - DeadLine Request Sent..." +  "\n");
            }
            send(msgsetDate);
            
            ACLMessage msgsetDateII =   new ACLMessage(ACLMessage.PROPOSE);
            msgsetDateII.setOntology("MARKET");
            msgsetDateII.setContent("INITIATE DEADLINE DEFINITION");
            msgsetDateII.setProtocol("PRE_NEGOTIATION");
            msgsetDateII.addReceiver(negotiationSeller);
            send(msgsetDateII);
        }
        private void                                setDeadLineStr(ACLMessage msg){
            
            ACLMessage reply = msg.createReply();
            reply.setContent(ProposedDeadline);
            reply.setPerformative(ACLMessage.PROPOSE);
            send(reply);
        }
    
        
    }
    
        private void                                SetupDeadLineState( ACLMessage reply, String Content){
        
        //reply.setOntology("COALITION");
        //reply.setProtocol("DEADLINE");
        reply.setPerformative(ACLMessage.AGREE);
        reply.setContent(Content);
        send(reply);     
        
    }
    
        
        private void                               sendConfig2Agents(){
            
            if (myGui.LdCfgSeller){
                ACLMessage msgS = new ACLMessage(ACLMessage.INFORM); 
                for (int i = 0 ; i < AvailableSellers.size(); i++){
                    msgS.addReceiver(AvailableSellers.get(i));
                }
                msgS.setProtocol("PRE_NEGOTIATION");
                msgS.setOntology("MARKET");
                msgS.setConversationId("CONFIG");
                msgS.setContent("LOAD CONFIG ");
                send(msgS);
            }
            if (myGui.LdCfgCoal){
                ACLMessage msgC = new ACLMessage(ACLMessage.INFORM); 
                for (int i =0 ; i < CoalitionMembers.size(); i++){
                    msgC.addReceiver(CoalitionMembers.get(i));
                }
                msgC.setProtocol("PRE_NEGOTIATION");
                msgC.setOntology("COALITION");
                msgC.setConversationId("CONFIG");
                msgC.setContent("LOAD CONFIG ");
                send(msgC);
            }
        }
    // </editor-fold>
    

    
   // <editor-fold defaultstate="colapsed" desc="Alive Contatct Ontology">
        private class                       contactMarket extends OneShotBehaviour{
            
            //private int         step            =   0;
            //private int         alldone         =   0;
            //private boolean     donne           =   false;
            //private boolean   currentState    =   false;
        
            @Override
            public void action(){
                myGui.askPersonalInfo();
                contactANDinfo();
                /*                switch (step) {
                 * case 0:
                 * contactANDinfo();
                 * step = 1;
                 * block();
                 * break;
                 * case 1:
                 * boolean state = opponentANDconfig();
                 * if (state){
                 * donne   =   true;
                 * }
                 * block();
                 * break;
                 * 
                 * }*/
            }
            
            /* @Override
             * public boolean done(){
             * return donne;
             * 
             * }*/
            
            private void            contactANDinfo(){
                
                ACLMessage msg_exist = new ACLMessage(ACLMessage.INFORM);
                String agent_info = ";name_" + ThisAgentInfo.get(0) + ";address_" + ThisAgentInfo.get(1) + ";telephone_" + ThisAgentInfo.get(2) + ";fax_" + ThisAgentInfo.get(3) + ";email_" + ThisAgentInfo.get(4);
                msg_exist.setContent(getLocalName() + ";is_coalition" + agent_info);
                msg_exist.setOntology("market_ontology");
                msg_exist.setProtocol("no_protocol");
                msg_exist.addReceiver(system_agent);
                send(msg_exist);

                ACLMessage msg_cfp_buyer = new ACLMessage(ACLMessage.CFP);
                msg_cfp_buyer.setContent("propose_opponent");
                msg_cfp_buyer.setOntology("market_ontology");
                msg_cfp_buyer.setProtocol("no_protocol");
                addBelif("myagent", getLocalName() + ";waiting_for_opponent");
                msg_cfp_buyer.addReceiver(system_agent);
                send(msg_cfp_buyer);
            }
            
            /*            private boolean         opponentANDconfig(){
             * 
             * ACLMessage msgc = myAgent.receive();
             * MessageParameters mp = new MessageParameters();
             * mp.getMessageStatus(msgc);
             * 
             * if (mp.oMarket && mp.prtkHello){
             * 
             * }else if(mp.oContract){
             * 
             * }else if(mp.oDay){
             * 
             * }else if(mp.oInf){
             * 
             * }else if (mp.oRisk){
             * 
             * }else if (mp.oVolume){
             * 
             * }
             * 
             * 
             * 
             * if (alldone){
             * return true;
             * } else {
             * return false;
             * }
             * }*/
            
        }
   
   // </editor-fold>         
    
            
    // <editor-fold defaultstate="collapsed" desc="Communicate Ontology "> 
    private class                               commToCoalition extends OneShotBehaviour{
        @Override
        public void action(){
            communicateCoal cl = new communicateCoal();
            cl.sendComm(CoalitionMembers, pricesContent);
        }
    }
 
    private class                               communicateCoal{
        
        private void                                sendComm(ArrayList<AID> list, String prices){
            
            ACLMessage msgsend = new ACLMessage(ACLMessage.INFORM);
            msgsend.setConversationId("PRICE INFORM ID");
            msgsend.setProtocol("PRE_NEGOTIATION");
            msgsend.setOntology("COALITION");
            msgsend.setContent(prices);
            msgsend.setReplyWith("msg" + System.currentTimeMillis());       //  Create unique reply to msg 
            for (int i =0 ; i < list.size(); i++){
                msgsend.addReceiver(list.get(i));
                System.out.println("...Sent..." + i + "\n");
            }
            send(msgsend);                                          // problems with diffusion (simultameous replyes (sinffer agent))
            
        }
        
    }
    // </editor-fold>
    

   
    //******************************************************************************************
    
   

    
    
    }
    
