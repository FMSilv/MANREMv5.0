package buying;

import Trader.AgentData;
import Trader.Trader;
import graphics.utility;
import graphics.volume;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import xml.FileManager;

public class Buyer extends Trader {

    private int phase = 0;
    private FileManager file_manager = new FileManager(getLocalName());
    private ArrayList<String> objectives_list = new ArrayList<>();
    private ArrayList<String> agenda_items = new ArrayList<>();
    private HashMap<String, ArrayList<String>> beliefs_about_others = new HashMap();
    private ArrayList<String> beliefs_about_myagent = new ArrayList<>();
    public ArrayList<double[]> received_history = new ArrayList<double[]>();
    public ArrayList<String> Buyers = new ArrayList<String>();
    public int exist = 0;
    protected final ArrayList<Double> utilities = new ArrayList<>();
    public double[][] volumes = null;
    public double calculatedscore = 0.0;
//    public ArrayList<Double> utilities;
    private AID system_agent = new AID("PersonalAssistant", AID.ISLOCALNAME);
    private AID opponent = new AID();
    protected boolean prices_received_recently = false;
    protected BuyerGui gui = null;

//    protected market.Market market = market.Init();
    protected BuyerInputGui input_gui = new BuyerInputGui(this);
    //Number of periods in a day to be negotiated;
    public final int N_PERIODS = 24;
    public int PERIODS = 24;
    public int VOLUME = 0, risk = 0, ES = 0;
    public String HOURS;

//    public final int N_PERIODS = market.N_PERIODS;
    //Negotiation deadline
    private Date deadline = new Date(System.currentTimeMillis() + 86400L * 7000);
    public String contract;
    public String contractduration;
    public ArrayList<Double> prices_target;
    public ArrayList<Double> volumes_target;
    public ArrayList<Double> prices_limit;
    private ArrayList<Double> volumes_limit;
    private ArrayList<Double> volumes_min;
    private ArrayList<String> personal_info;
    private String negotiation_protocol;
    public String negotiation_strategy;
    public int other_prices = 0;
    public int DR;
    public double sharing_risk = 0.5;
    private String negotiation_preference;
    private String negotiation_riskpreference;
    private String negotiation_contract;
    
    
    private boolean isPool = false;
    private boolean isSMP = false;
    private boolean isLMP = false;
    private boolean isOTC = false;
    
    public String Strategy;
    
    public AgentData information;
    

    @Override
    protected void setup() {
        this.information = new AgentData();
        this.addBehaviour(new MessageManager());
        executePhase(0);
    }
    
    class MessageManager extends CyclicBehaviour {

        //Message mask for initialization messages <----------------------------------------------
        MessageTemplate mt_hello = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), 
                MessageTemplate.MatchProtocol("hello_protocol"));
        
        
        //Message mask for all subsequent messages
        MessageTemplate mt_ontology = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), 
                MessageTemplate.MatchProtocol("no_protocol"));
        
        @Override
        public void action() {

            ACLMessage msg = myAgent.receive(mt_ontology);
            if (msg != null) {
                if (msg.getOntology().equals("market_ontology")) {
                    
                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve(msg);
                    
                }
 
                
                
//<-----------------------------------------------------------------------------        
// Commented previous code because it was not functional right now. João de Sá
//<-----------------------------------------------------------------------------                
                
//                if (msg.getContent().contains("prices;")) {
//                    addBelif(msg.getContent().split(";")[0], msg.getContent());
//                    setPricesReceivedRecently(true);
//                    gui.updateLog2("Prices received from seller", Color.BLUE);
//                    other_prices = 1;
//                    checkIfReadyToNegotiate();
////                    if (phase == 2) {
////                        phase = 3;
////                    }
//                } else if (msg.getContent().equals("init_deadline_definition_protocol")) {
//                    addBehaviour(new deadlineDefinitionProtocol(msg));
//                } else if (msg.getContent().equals("init_contract_definition_protocol")) {
//                    addBehaviour(new contractDefinitionProtocol(msg));
//                } else if (msg.getContent().equals("end_negotiation")) {
//                    input_gui.finish(gui, "");
////                    doDelete();
//                    gui.setVisible(false);
//                    executePhase(0);
//                }

            } else {
                block();
            }
        }
    }
    
    
    class MarketOntology {
        
        private void resolve(ACLMessage msg) {
            if (msg.getPerformative() == ACLMessage.INFORM) {
                resolveInform(msg);
            }
            if (msg.getPerformative() == ACLMessage.CFP) {
                resolveCFP(msg);
            }
        }
        
        private void resolveInform(ACLMessage msg){
            if(msg.getContent().contains("Dayahead")){
                set_isPool(true);
                if(msg.getContent().contains("SMP")){
                    set_isSMP(true);
                }else if(msg.getContent().contains("LMP")){
                    set_isLMP(true);
                }
            }else if(msg.getContent().equals("OTC")){
                set_isOTC(true);
            }else if(msg.getContent().contains("Participating")){
                // If agent receives message informing it's participating, Participating
                // variable is set to true and Phase is executed
                information.setParticipating(true);
                // Chosen Strategy is also Stored
                if(msg.getContent().contains("Default")){
                    Strategy = "Default";
                }
                executePhase(1);
            }

        }
        
        private void resolveCFP(ACLMessage msg){
            
                
        }
            
            
    }
    
    public void read_info() {
        try {
            
            File f = new File("files\\"+getLocalName()+"\\information.xls");
            
            Workbook wb;
            
            wb = Workbook.getWorkbook(f);
            
            // First Sheet has basic agent information
            Sheet s = wb.getSheet(0);
            int row = s.getRows();
            int col = s.getColumns();
            Cell c;
            
            // First col of file has name, but local name is used instead
            c = s.getCell(0,0);
            this.information.setName(getLocalName());
            
            // Second col of file has address
            c = s.getCell(1,0);
            this.information.setAddress(c.getContents());
            
            // Third col of file has Phone Number in
            c = s.getCell(2,0);
            this.information.setPhone_number(c.getContents());
            
            // Fourth col of file has Email
            c = s.getCell(3,0);
            this.information.setEmail(c.getContents());
            
            // Fifth col of file has Objective
            c = s.getCell(4,0);
            this.information.setObjective(c.getContents());
            
            // Initial value of Participating value is set to false
            this.information.setParticipating(false);
            
        } catch (IOException ex) {
            Logger.getLogger(Buyer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(Buyer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
        
    public void set_isPool(boolean _isPool){
        this.isPool = _isPool;
    }
    
    public void set_isOTC(boolean _isOTC){
        this.isOTC = _isOTC;
    }
    
    public void set_isSMP(boolean _isSMP){
        this.isSMP = _isSMP;
    }
    
    public void set_isLMP(boolean _isLMP){
        this.isLMP = _isLMP;
    }
    
    public boolean get_isPool(){
        return this.isPool;
    }
    
    public boolean get_isOTC(){
        return this.isOTC;
    }
    
    public boolean get_isSMP(){
        return this.isSMP;
    }
    
    public boolean get_isLMP(){
        return this.isLMP;
    }
    
    @Override
    public void executePhase(int phase) {

        this.phase = phase;
        
        switch(phase){
            case 0:
                // Ask personal info to send to market agent
                
                // Reads first page of info file, which contains basic agent info
                this.read_info();
                
                // Send information to PersonalAssisntant
                addBehaviour(new helloProtocol());
                
//                if (Buyers.size() == 0) {
//                    input_gui.askPersonalInfo();
//                    Buyers.add(getLocalName());
//                    exist = 0;
//                } else {
//                    int j = 0;
//                    for (int i = 0; i < Buyers.size(); i++) {
//                        if (getLocalName().equals(Buyers.get(i))) {
//                            i = Buyers.size();
//                            j = 1;
//                            exist = 1;
//                        }
//                    }
//                    if (j == 0) {
//                        input_gui.askPersonalInfo();
//                        Buyers.add(getLocalName());
//                        exist = 0;
//                    }
//                }
//                
//
//                addBehaviour(new helloAndGetOpponent());
                break;
                
            case 1:
                // phase 1 generates offers
                
                this.information.initializePower();
                this.information.initializePrice();
                
                
                if(Strategy.contains("Default")){
                    // Default strategy means reading input data from excel file on the folder of this Agent
                    // The file is named Standard_Strat.xls
                    
                    read_Standardstrat();
                    
                }
                //Missing code for other strategies
                
                // Sendo offer data to PersonalAssitant
                send_Offers();
            
        }
        
        
        
        
//<-----------------------------------------------------------------------------        
// Commented previous Switch because it was not functional right now. João de Sá
//<-----------------------------------------------------------------------------
        
        
        
        
//        switch (phase) {
//
//            case 0:
//                //Ask personal info to send to market agent
//
//                if (Buyers.size() == 0) {
//                    input_gui.askPersonalInfo();
//                    Buyers.add(getLocalName());
//                    exist = 0;
//                } else {
//                    int j = 0;
//                    for (int i = 0; i < Buyers.size(); i++) {
//                        if (getLocalName().equals(Buyers.get(i))) {
//                            i = Buyers.size();
//                            j = 1;
//                            exist = 1;
//                        }
//                    }
//                    if (j == 0) {
//                        input_gui.askPersonalInfo();
//                        Buyers.add(getLocalName());
//                        exist = 0;
//                    }
//                }
//
//                addBehaviour(new helloAndGetOpponent());
//                break;
//
//            //Read objectives, plan creation and interpretarion
//            case 1:
//                //read belifs about opponent in file: Agent Data\%opponent-name%\beliefs_%opponent-name%.xml
//                readBeliefs(getOpponent().getLocalName());
//                readBeliefs(getLocalName());
//                //initiate GUI
//                InitGui();
//                //Enable necessary buttons;
//                gui.guiEnableButtons(1);
//
//                if (!"-1".equals(searchPartialBelief(getOpponent().getLocalName(), "prices"))) {
//                    phase = 2;
//                    gui.updateLog2("Awaiting seller price information", Color.BLUE);
//                }
//                break;
//
//            case 3:
//                gui.guiEnableButtons(2);
//                break;
//
//            case 4:
//                //Initiates negotiation
//                MarketBuyerAgent market_buyer = new MarketBuyerAgent(this, input_gui);
//                market_buyer.purchase("", ArrayListToArray(prices_target), ArrayListToArray(prices_limit),
//                        ArrayListToArray(volumes_target), ArrayListToArray(volumes_limit), ArrayListToArray(volumes_min),
//                        negotiation_strategy, negotiation_preference, negotiation_riskpreference, DR, deadline, contract, ArrayListToArray(volumes_target), Double.parseDouble(contractduration),
//                        ArrayListToArray(transformOpponentBeliefToPrice(getOpponent().getLocalName())));
//                break;
//
//        }

    }
    
    private void send_Offers(){
        
        ACLMessage info_msg = new ACLMessage(ACLMessage.INFORM);

        String agent_offer = "Offer " + this.information.getName() + " Buyer";
        
        agent_offer = agent_offer + " Price ";
        
        for(int i = 0; i < this.information.getPrice().size(); i++){
            agent_offer = agent_offer + this.information.getPrice().get(i) + " ";
        }
        
        agent_offer = agent_offer + "Power ";
        
        for(int i = 0; i < this.information.getPower().size(); i++){
            agent_offer = agent_offer + this.information.getPower().get(i) + " ";
        }
        
        agent_offer = agent_offer + "end";
        
        info_msg.setContent(agent_offer);
        info_msg.setOntology("market_ontology");
        info_msg.setProtocol("no_protocol");
        info_msg.addReceiver(system_agent);
        send(info_msg);
        
    }
    
    private void read_Standardstrat(){
        File f = new File("files\\"+getLocalName()+"\\Standard_strat.xls");

        Workbook wb;
        
        try {
            wb = Workbook.getWorkbook(f);
            
            Sheet s = wb.getSheet(0);
            int row = s.getRows();
            int col = s.getColumns();
            Cell c;
            
            for(int i = 1; i < row; i++){
                c = s.getCell(2,i);
                this.information.setPower(Float.parseFloat(c.getContents()));
                
                c = s.getCell(3,i);
                this.information.setPrice(Float.parseFloat(c.getContents()));
            }
            
//            System.out.println("Printing price and power");
//            for(int i = 0; i < this.information.getPrice().size(); i++){
//                System.out.print("Period: " + (i+1));
//                System.out.print(" Price: " + this.information.getPrice().get(i));
//                System.out.println(" Power: " + this.information.getPower().get(i));
//            }

        } catch (IOException ex) {
            Logger.getLogger(Buyer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(Buyer.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    
    private class helloProtocol extends Behaviour {
        
        private int step = 0;

        @Override
        public void action() {

            switch (step) {
                case 0:
                    
                    // Message to be sent will have the following format
                    // "Name";"Address";"PhoneNumber";"Email";"Objective"
                    // The fields between "" refer to information within the dataStructures of this Agent
                    
                    
                    // First message contains basic information
                    // Each field is separated by ";"
                    ACLMessage info_msg = new ACLMessage(ACLMessage.INFORM);

                    String agent_info = "" + information.getName() + ";" + "isBuyer" + ";" + information.getAddress()
                            + ";" + information.getPhone_number() + ";" + information.getEmail() + ";" + information.getObjective()+ ";";
                    
                    info_msg.setContent(agent_info);
                    info_msg.setOntology("market_ontology");
                    info_msg.setProtocol("hello_protocol");
                    info_msg.addReceiver(system_agent);
                    send(info_msg);
                  
                    step = 1;
                    block();
                    break;

            }    
                        
        }
    @Override
        public boolean done() {

            if (step == 1) {
                
                return true;

            } else {
                return false;
            }

        }
        
    }
    
    

    public int getPhase(){
        return this.phase;
    }
    
    public FileManager getFileManager() {
        return file_manager;
    }

    public void InitGui() {

        gui = new BuyerGui(this);

    }

    public boolean isPricesReceivedRecently() {
        return prices_received_recently;
    }

    public void setPricesReceivedRecently(boolean prices_asked) {
        this.prices_received_recently = prices_asked;
    }

    public void updateBelifsFile() {
        file_manager.printXmlBelief(getLocalName(), getLocalName(), getBelifsAboutMyAgent());
        Object[] keys = getBelifsAboutOthers().keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            file_manager.printXmlBelief(getLocalName(), keys[i].toString(), getBelifsAboutOthers().get(keys[i]));
        }
    }

    public HashMap<String, ArrayList<String>> getBelifsAboutOthers() {
        return this.beliefs_about_others;
    }

    public ArrayList<String> getBelifsAboutMyAgent() {
        return this.beliefs_about_myagent;
    }

    public AID getOpponent() {
        return this.opponent;
    }

    public void setOpponent(AID opponent) {
        this.opponent = opponent;
    }

    public ArrayList<String> getAgendaItems() {
        return agenda_items;
    }

    public void setAgendaItems(ArrayList<String> agenda_items) {
        this.agenda_items = agenda_items;
    }

    public ArrayList<String> getObjectivesList() {
        return objectives_list;
    }

    public void setObjectivesList(ArrayList<String> objectives_list) {
        this.objectives_list = objectives_list;
    }

    public BuyerGui getGui() {
        return gui;
    }

    public BuyerInputGui getInputGui() {
        return input_gui;
    }

    public int checkIfReadyToNegotiate() {
        if (this.prices_target != null && this.prices_limit != null && this.negotiation_strategy != null && this.other_prices != 0/*&& this.negotiation_protocol != null  && this.deadline != null*/) {
            executePhase(3);
            return 1;
        }
        return 0;
    }

    protected void terminateAgent() {
        ACLMessage msg_end = new ACLMessage(ACLMessage.INFORM);
        msg_end.setContent("end_negotiation");
        msg_end.setOntology("market_ontology");
        msg_end.setProtocol("no_protocol");
        msg_end.setReplyWith(String.valueOf(System.currentTimeMillis()));
        msg_end.addReceiver(getOpponent());

        // ends the negotiation
        send(msg_end);
//        doDelete();
        gui.dispose();
        executePhase(0);
    }

    protected void utility(BuyerGui parent, int choice, String title) {

        String[] Name = {"Sent Proposals", "Received Proposals", "Calculated Proposals"};

        utility demo = null;

        if (choice == 1) {
            demo = new utility(title, utilities, 2, "Time", "Buyer's Utility", Name, choice, 1, calculatedscore);
        } else {
            demo = new utility(title, utilities, 2, "Proposal", "Buyer's Utility", Name, choice, 1, calculatedscore);
        }
        demo.pack();
        demo.setLocation(parent.getX() - 100, parent.getY() + 100);
//        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    protected void volume(BuyerGui parent, String title) {

        volume demo = null;

        String[] lines = {"Initial Volume", "Actual Volume"};
        String[] columns = new String[PERIODS];
        for (int i = 0; i < PERIODS; i++) {
            columns[i] = "" + (i + 1);
        }
        demo = new volume(title, volumes, lines, columns);

        demo.pack();
        demo.setLocation(parent.getX() - 150, parent.getY() + 100);
//        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    public int checkIfContract() {
        if (this.contract != null) {
            return 1;
        }
        return 0;
    }

    public void askUserProfile() {
        // Asks user for the profile values and adds a profile value to it's beliefs
        String profile = getInputGui().askUserProfile(gui);
        addBelif("myagent", getLocalName() + ";" + "profile;" + profile);
    }

    private void readBeliefs(String name) {
        ArrayList<String> beliefs = file_manager.readBeliefsFile(name);
        for (int i = 1; i < beliefs.size(); i++) {
            addBelif(beliefs.get(0), beliefs.get(i));
        }
    }

    public ArrayList<String> transformOpponentBeliefToPriceString(String name) {
        // Creates an array containing the opponented prices, based on the agent's own belief
        if (getBelifsAboutOthers().containsKey(name)) {
            ArrayList<String> prices = new ArrayList<>();

            String belief = searchBelief(name, "prices");

            if (belief != null) {
                String[] content_split = belief.split(";");
                String[] content_split_2 = content_split[2].split("-");
                for (int j = 0; j < content_split_2.length; j++) {
                    prices.add(content_split_2[j].split("_")[0]);
                    prices.add(content_split_2[j].split("_")[1]);
                }
            }

            return prices;
        }
        return null;
    }

    public ArrayList<Double> transformOpponentBeliefToPrice(String name) {
        if (getBelifsAboutOthers().containsKey(name)) {
            ArrayList<Double> prices = new ArrayList<>();

            String belief = searchBelief(name, "prices");

            if (belief != null) {
                String[] content_split = belief.split(";");
                String[] content_split_2 = content_split[2].split("-");
                for (int j = 0; j < content_split_2.length; j++) {
                    prices.add(Double.valueOf(content_split_2[j].split("_")[1]));
                }
            }

            return prices;
        }
        return null;
    }

    public ArrayList<Double> transformMyBeliefToVolume() {

        if (searchBelief("myagent", "volumes") != null) {
            ArrayList<Double> volumes = new ArrayList<>();

            String belief = searchBelief("myagent", "volumes");

            if (belief != null) {
                String[] content_split = belief.split(";");
                String[] content_split_2 = content_split[2].split("-");
                for (int j = 0; j < content_split_2.length; j++) {
                    volumes.add(Double.valueOf(content_split_2[j].split("_")[1]));
                }
            }

            return volumes;
        }
        return null;
    }

    public ArrayList<Double> transformMyBeliefToContract() {

        if (searchBelief("myagent", "volumes") != null) {
            ArrayList<Double> contract = new ArrayList<>();

            String belief = searchBelief("myagent", "contract");

            if (belief != null) {
                String[] content_split = belief.split(";");
                String[] content_split_2 = content_split[2].split("-");
                for (int j = 0; j < content_split_2.length; j++) {
                    contract.add(Double.valueOf(content_split_2[j].split("_")[1]));
                }
            }

            return contract;
        }
        return null;
    }

    public String transformPrimitiveValuesToString(ArrayList<String> values) {
        String s = "";
        for (int i = 0; i < values.size() - 2; i = i + 2) {
            s = s + values.get(i) + "-" + values.get(i + 1) + "-";
        }
        s = s + values.get(values.size() - 2) + "-" + values.get(values.size() - 1);
        return s;
    }

    protected void addBelif(String name, String belief) {
        // While adding a belief, if one already exists witht he same description, the new one replaces it
        if (name.equals("myagent") || name.equals(getLocalName())) {
            if (searchBelief(getLocalName(), belief.split(";")[1]) != null) {
                removePartialBelief(getLocalName(), belief.split(";")[1]);
            }
            getBelifsAboutMyAgent().add(belief);
        } else if (getBelifsAboutOthers().containsKey(name)) {

            if (searchBelief(name, belief.split(";")[1]) != null) {
                removePartialBelief(name, belief.split(";")[1]);
            }
            ArrayList<String> list = getBelifsAboutOthers().get(name);
            list.add(belief);
            getBelifsAboutOthers().put(name, list);
        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add(belief);
            getBelifsAboutOthers().put(name, list);
        }
    }

    private boolean beliefExists(String name, String belief) {
        //checks if the belief is owned by the agent (must be completly equal, not just the description)
        if (name.equals("myagent") || name.equals(getLocalName())) {
            for (int i = 0; i < getBelifsAboutMyAgent().size(); i++) {
                if (getBelifsAboutMyAgent().get(i).equals(belief)) {
                    return true;
                }
            }
        } else if (getBelifsAboutOthers().containsKey(name)) {
            for (int i = 0; i < getBelifsAboutOthers().get(name).size(); i++) {
                if (getBelifsAboutOthers().get(name).get(i).equals(belief)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String searchPartialBelief(String name, String belief) {
        // Checks if any belifs currently owned by the agent contains the segment in part_belif, if so returns it
        if ((name.equals("myagent") || name.equals(getLocalName())) && !getBelifsAboutMyAgent().isEmpty()) {
            for (int i = 0; i < getBelifsAboutMyAgent().size(); i++) {
                if (getBelifsAboutMyAgent().get(i).contains(belief)) {
                    return getBelifsAboutMyAgent().get(i);
                }
            }
        } else if (getBelifsAboutOthers().containsKey(name)) {
            for (int i = 0; i < getBelifsAboutOthers().get(name).size(); i++) {
                if (getBelifsAboutOthers().get(name).get(i).contains(belief)) {
                    return getBelifsAboutOthers().get(name).get(i);
                }
            }
        }
        return "-1";
    }

    protected String searchBelief(String name, String belief_header) {
        // Checks if the belief is owned by the agent (must be completly equal) and returns it
        if ((name.equals("myagent") || name.equals(getLocalName())) && !getBelifsAboutMyAgent().isEmpty()) {
            for (int i = 0; i < getBelifsAboutMyAgent().size(); i++) {
                if (getBelifsAboutMyAgent().get(i).split(";")[1].equals(belief_header)) {
                    return getBelifsAboutMyAgent().get(i);
                }
            }
        } else if (getBelifsAboutOthers().containsKey(name)) {
            for (int i = 0; i < getBelifsAboutOthers().get(name).size(); i++) {
                if (getBelifsAboutOthers().get(name).get(i).split(";")[1].equals(belief_header)) {
                    return getBelifsAboutOthers().get(name).get(i);
                }
            }
        }
        return null;
    }

    private void removeBelief(String name, String belief) {
        // Removes the exact belief received in the string 
        if (name.equals("myagent") || name.equals(getLocalName())) {
            for (int i = 0; i < getBelifsAboutMyAgent().size(); i++) {
                if (getBelifsAboutMyAgent().get(i).equals(belief)) {
                    getBelifsAboutMyAgent().remove(i);
                }
            }
        } else if (getBelifsAboutOthers().containsKey(name)) {
            for (int i = 0; i < getBelifsAboutOthers().get(name).size(); i++) {
                if (getBelifsAboutOthers().get(name).get(i).equals(belief)) {
                    getBelifsAboutOthers().get(name).remove(i);
                    if (getBelifsAboutOthers().get(name).isEmpty()) {
                        getBelifsAboutOthers().remove(name);
                        return;
                    }
                }
            }
        }
    }

    private void removePartialBelief(String name, String belief) {
        // Removes a belief that partially contain the received belief
        if (name.equals("myagent") || name.equals(getLocalName())) {
            for (int i = 0; i < getBelifsAboutMyAgent().size(); i++) {
                if (getBelifsAboutMyAgent().get(i).contains(belief)) {
                    getBelifsAboutMyAgent().remove(i);
                }
            }
        } else if (getBelifsAboutOthers().containsKey(name)) {
            for (int i = 0; i < getBelifsAboutOthers().get(name).size(); i++) {
                if (getBelifsAboutOthers().get(name).get(i).contains(belief)) {
                    getBelifsAboutOthers().get(name).remove(i);

                }
            }
        }
    }

    public double[] ArrayListToArray(ArrayList<Double> array_list) {
        double[] array = new double[this.PERIODS];

        for (int i = 0; i < array_list.size(); i++) {
            array[i] = array_list.get(i);
        }
        return array;
    }

    public void setPricesTarget(ArrayList<Double> price_target_final) {
        this.prices_target = price_target_final;
    }

    public void setContractDuration(String contract_target_final) {
        this.contractduration = contract_target_final;
    }

    public void setVolumesTarget(ArrayList<Double> volume_target_final) {
        this.volumes_target = volume_target_final;
    }

    public void setPricesLimit(ArrayList<Double> price_limit_final) {
        this.prices_limit = price_limit_final;
    }

    public void setVolumesLimit(ArrayList<Double> volume_limit_final) {
        this.volumes_limit = volume_limit_final;
    }

    public void setVolumesMin(ArrayList<Double> volume_min_final) {
        this.volumes_min = volume_min_final;
    }
//  public void setUtilities(ArrayList<Double> utilities) {
//        this.utilities = utilities;
//    }

    public void setNegotiationProtocol(String protocol) {
        this.negotiation_protocol = protocol;
    }

    public void setNegotiationStrategy(String strategy) {
        this.negotiation_strategy = strategy;
    }

    public void setDemandResponse(int DR) {
        this.DR = DR;
    }

    public void setNegotiationContract(String contract) {
        this.negotiation_contract = contract;
    }

    public void setNegotiationPreference(String preference) {
        this.negotiation_preference = preference;
    }

    public void setNegotiationRiskPreference(String preference) {
        this.negotiation_riskpreference = preference;
    }

    protected void defineDeadline() {
//        addBehaviour(new deadlineDefinitionProtocol(null));
    }

//            protected void defineContract() {
//        addBehaviour(new Buyer.contractDefinitionProtocol(null));
//    }
    public void setPersonalInfo(ArrayList<String> personal_info) {
        this.personal_info = personal_info;
    }

    void sendProfile() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology("market_ontology");
        msg.setProtocol("no_protocol");
        String content = searchBelief("myagent", "profile");
        msg.addReceiver(getOpponent());
        msg.setContent(content);
        send(msg);
//        printMessage(msg, false, "Sending profile");
    }

    private class helloAndGetOpponent extends Behaviour {

        private MessageTemplate mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        private int step = 0;

        @Override
        public void action() {

            switch (step) {
                case 0:
                    ACLMessage msg_exist = new ACLMessage(ACLMessage.INFORM);
                    String agent_info = ";name_" + personal_info.get(0) + ";address_" + personal_info.get(1) + ";telephone_" + personal_info.get(2) + ";email_" + personal_info.get(3) + ";";
                    msg_exist.setContent(getLocalName() + ";is_buyer" + agent_info);
                    msg_exist.setOntology("market_ontology");
                    msg_exist.setProtocol("no_protocol");
                    msg_exist.addReceiver(system_agent);
                    if (exist == 0) {
                        send(msg_exist);
                    }

                    ACLMessage msg_cfp_seller = new ACLMessage(ACLMessage.CFP);
                    msg_cfp_seller.setContent("propose_opponent");
                    msg_cfp_seller.setOntology("market_ontology");
                    msg_cfp_seller.setProtocol("no_protocol");
                    addBelif("myagent", getLocalName() + ";waiting_for_opponent");
                    msg_cfp_seller.addReceiver(system_agent);
                    send(msg_cfp_seller);

                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));

                    step = 1;

                    block();
                    break;

                case 1:

                    ACLMessage msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg != null) {
                            if (beliefExists("myagent", getLocalName() + ";waiting_for_opponent")) {
                                removeBelief("myagent", getLocalName() + ";waiting_for_opponent");

                                String[] content_information = msg.getContent().split(";");
                                setOpponent(new AID(content_information[0], AID.ISLOCALNAME));

                                step = 2;
                            }
                        } else {
                            block();
                        }

                    } else {
                        block();
                    }

                case 2:
                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("contract_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg != null) {
                            if (msg.getOntology().equals("contract_ontology")) {

                                setNegotiationContract(msg.getContent());

                                step = 3;

                            }
                        } else {
                            block();
                        }

                    } else {
                        block();
                    }

                case 3:
                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("day_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg != null) {
                            if (msg.getOntology().equals("day_ontology")) {

                                setContractDuration(msg.getContent());

                                step = 4;

                            }
                        } else {
                            block();
                        }

                    } else {
                        block();
                    }

                case 4:
                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("inf_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg != null) {
                            if (msg.getOntology().equals("inf_ontology")) {

                                PERIODS = Integer.parseInt(msg.getContent());
                                volumes = new double[2][PERIODS];
                                if (PERIODS == 24) {
                                    step = 6;
                                } else {
                                    step = 5;
                                }
                            }
                        } else {
                            block();
                        }

                    } else {
                        block();
                    }
                case 5:
                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("hour_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));

                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg != null) {
                            if (msg.getOntology().equals("hour_ontology")) {
                                HOURS = msg.getContent();
                                step = 6;
                            } else {
                                block();
                            }

                        } else {
                            block();
                        }
                    }
                case 6:
                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("volume_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg != null) {
                            if (msg.getOntology().equals("volume_ontology")) {

                                VOLUME = Integer.parseInt(msg.getContent());

                                step = 7;

                            }
                        } else {
                            block();
                        }

                    } else {
                        block();
                    }
                case 7:
                    mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("risk_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                    msg = myAgent.receive(mt);
                    if (msg != null) {
                        if (msg != null) {
                            if (msg.getOntology().equals("risk_ontology")) {

                                risk = Integer.parseInt(msg.getContent());

                                step = 8;

                            }
                        } else {
                            block();
                        }

                    } else {
                        block();
                    }
//                    case 8:
//                         mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("ES_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
//                                            msg = myAgent.receive(mt);
//                    if (msg != null) {
//                        if (msg != null) {
//                             if (msg.getOntology().equals("ES_ontology")) {
//                                
//                                ES = Integer.parseInt(msg.getContent());
//                                                         
//                                
//                                step = 9;
//                               
//                            }
//                        } else {
//                            block();
//                        }
//
//                    } else {
//                        block();
//                    }                       
            }

        }

        @Override
        public boolean done() {

            if (step == 8) {
                executePhase(1);
                return true;

            } else {
                return false;
            }

        }
    }

    private class deadlineDefinitionProtocol extends Behaviour {

        private int step = 0;
        ACLMessage msg = null;
        ACLMessage reply = null;
        MessageTemplate mt = null;

        public deadlineDefinitionProtocol(ACLMessage msg) {
            this.msg = msg;
        }

        @Override
        public void action() {
            switch (step) {
                case 0:

                    if (msg != null) {  // Protocol initiated by opponent
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.AGREE);
                        reply.setProtocol("deadline_definition_protocol");
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                        mt = MessageTemplate.and(MessageTemplate.and(
                                MessageTemplate.MatchOntology("market_ontology"),
                                MessageTemplate.MatchProtocol("deadline_definition_protocol")), MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                        send(reply);
                        step = 2;   //Wait for a propose
                        block();
                        break;
                    } else {    //Send a request for protocol init
                        msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("init_deadline_definition_protocol");
                        msg.setOntology("market_ontology");
                        msg.setProtocol("no_protocol");
                        msg.setReplyWith(String.valueOf(System.currentTimeMillis()));
                        msg.addReceiver(getOpponent());

                        mt = MessageTemplate.and(MessageTemplate.and(
                                MessageTemplate.MatchOntology("market_ontology"),
                                MessageTemplate.MatchProtocol("deadline_definition_protocol")),
                                MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                        send(msg);
                        step = 1;   //Wait for protocol init agree
                        block();
                        break;
                    }

                case 1: //Wait for protocol init agree
                    msg = receive(mt);
                    if (msg == null) {
                        block();
                        break;
                    }

                    if (msg.getPerformative() == ACLMessage.AGREE) {
                        //Send first proposal
                        String date = getInputGui().askDeadline(gui, null);
                        System.out.println(" \n date " + date);
                        if (date == null) {
                            //Send end protocol inform
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("end_deadline_definition_protocol");
                            send(reply);
                            step = 3;
                            break;
                        }
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(date);
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                        mt = MessageTemplate.and(MessageTemplate.and(
                                MessageTemplate.MatchOntology("market_ontology"),
                                MessageTemplate.MatchProtocol("deadline_definition_protocol")),
                                MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                        send(reply);
                        step = 2;   //Wait for a propose
                        block();
                        break;
                    }

                case 2: //Wait for proposal
                    msg = receive(mt);
                    if (msg == null) {
                        block();
                        break;
                    }
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        String date = getInputGui().askDeadline(gui, msg.getContent());
                        System.out.println(" \n date " + date);
                        if (date == null) {
                            //Send end protocol inform
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("end_deadline_definition_protocol");
                            send(reply);
                            step = 3;
                            break;
                        } else if (date.equals(msg.getContent())) {
                            //  Accept proposal
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            reply.setContent(date);
                            reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                            mt = MessageTemplate.and(MessageTemplate.and(
                                    MessageTemplate.MatchOntology("market_ontology"),
                                    MessageTemplate.MatchProtocol("deadline_definition_protocol")),
                                    MessageTemplate.MatchInReplyTo(reply.getReplyWith()));
                            send(reply);
                            deadline = new Date(Long.valueOf(msg.getContent()));
                            checkIfReadyToNegotiate();
                            block();
                            break;

                            //  Send propose msg
                        } else if (date != null) {
                            // Send proposal
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(date);
                            reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                            mt = MessageTemplate.and(MessageTemplate.and(
                                    MessageTemplate.MatchOntology("market_ontology"),
                                    MessageTemplate.MatchProtocol("deadline_definition_protocol")),
                                    MessageTemplate.MatchInReplyTo(reply.getReplyWith()));
                            send(reply);
                            block();
                            break;
                        } else {
                            // Send end protocol inform
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("end_deadline_definition_protocol");
                            send(reply);
                            step = 3;
                            break;
                        }

                    } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        deadline = new Date(Long.valueOf(msg.getContent()));
                        checkIfReadyToNegotiate();
                        // Send end protocol inform
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("end_deadline_definition_protocol");
                        send(reply);
                        step = 3;
                        break;

                    } else if (msg.getPerformative() == ACLMessage.INFORM) {
                        if (msg.getContent().contains("end_deadline_definition_protocol")) {
                            step = 3;
                            break;
                        }
                    }

                    break;
            }
        }

        @Override
        public boolean done() {
            if (step == 3) {
                return true;
            }
            return false;
        }
    }

    private class contractDefinitionProtocol extends Behaviour {

        private int step = 0;
        ACLMessage msg = null;
        ACLMessage reply = null;
        MessageTemplate mt = null;

        public contractDefinitionProtocol(ACLMessage msg) {
            this.msg = msg;

        }

        @Override
        public void action() {

            switch (step) {
                case 0:

                    if (msg != null) {  // Protocol initiated by opponent
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.AGREE);
                        reply.setProtocol("contract_definition_protocol");
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                        mt = MessageTemplate.and(MessageTemplate.and(
                                MessageTemplate.MatchOntology("market_ontology"),
                                MessageTemplate.MatchProtocol("contract_definition_protocol")), MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                        send(reply);
                        step = 2;   //Wait for a propose
                        block();
                        break;
                    } else {    //Send a request for protocol init
                        msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("init_contract_definition_protocol");
                        msg.setOntology("market_ontology");
                        msg.setProtocol("no_protocol");
                        msg.setReplyWith(String.valueOf(System.currentTimeMillis()));
                        msg.addReceiver(getOpponent());

                        mt = MessageTemplate.and(MessageTemplate.and(
                                MessageTemplate.MatchOntology("market_ontology"),
                                MessageTemplate.MatchProtocol("contract_definition_protocol")),
                                MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                        send(msg);

                        step = 1;   //Wait for protocol init agree
                        block();
                        break;
                    }

                case 1: //Wait for protocol init agree
                    msg = receive(mt);
                    if (msg == null) {
                        block();
                        break;
                    }

                    if (msg.getPerformative() == ACLMessage.AGREE) {
                        //send first proposal
                        String contract = getInputGui().askContract(gui, null);
                        if (contract == null) {
                            //Send end protocol inform
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("end_contract_definition_protocol");
                            send(reply);
                            step = 3;
                            break;
                        }
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(contract);
                        reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                        mt = MessageTemplate.and(MessageTemplate.and(
                                MessageTemplate.MatchOntology("market_ontology"),
                                MessageTemplate.MatchProtocol("contract_definition_protocol")),
                                MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                        send(reply);
                        step = 2;   //Wait for a propose
                        block();
                        break;
                    }

                case 2: //Wait for proposal
                    msg = receive(mt);
                    if (msg == null) {
                        block();
                        break;
                    }

                    if (msg.getPerformative() == ACLMessage.PROPOSE) {

                        String contract = getInputGui().askContract(gui, msg.getContent());

                        if (contract == null) {
                            //Send end protocol inform
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("end_contract_definition_protocol");
                            send(reply);
                            step = 3;
                            break;
                        } else if (contract.equals(msg.getContent())) {
                            //  Accept proposal
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            reply.setContent(contract);
                            reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                            mt = MessageTemplate.and(MessageTemplate.and(
                                    MessageTemplate.MatchOntology("market_ontology"),
                                    MessageTemplate.MatchProtocol("contract_definition_protocol")),
                                    MessageTemplate.MatchInReplyTo(reply.getReplyWith()));
                            send(reply);
                            contract = msg.getContent();
                            checkIfReadyToNegotiate();
                            block();
                            break;

                            //  Send propose msg
                        } else if (contract != null) {
                            // Send proposal
                            reply = msg.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(contract);
                            reply.setReplyWith(String.valueOf(System.currentTimeMillis()));

                            mt = MessageTemplate.and(MessageTemplate.and(
                                    MessageTemplate.MatchOntology("market_ontology"),
                                    MessageTemplate.MatchProtocol("contract_definition_protocol")),
                                    MessageTemplate.MatchInReplyTo(reply.getReplyWith()));

                            send(reply);

                            block();
                            break;
                        }

                    } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        contract = msg.getContent();
                        checkIfReadyToNegotiate();
                        //Send end protocol inform
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("end_contract_definition_protocol");
                        send(reply);
                        step = 3;
                        break;

                    } else if (msg.getPerformative() == ACLMessage.INFORM) {
                        if (msg.getContent().contains("end_contract_definition_protocol")) {
                            step = 3;
                            break;
                        }
                    }

                    break;
            }
        }

        @Override
        public boolean done() {
            if (step == 3) {
                return true;
            }
            return false;
        }
    }
}
