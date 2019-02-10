package selling;

import graphics.utility;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.awt.Color;
import java.util.*;
import xml.FileManager;

public class Seller extends Agent {

    private int phase = 0;
    private FileManager file_manager;
    private HashMap<String, ArrayList<String>> beliefs_about_others = new HashMap();
    private ArrayList<String> beliefs_about_myagent = new ArrayList<>();
    private ArrayList<String> my_prices = new ArrayList<>();
    private AID system_agent = new AID("Market", AID.ISLOCALNAME);
    public double calculatedscore = 0.0, counteroffer = 0;
    private ArrayList<AID> opponents = new ArrayList<>();
    private ArrayList<String> agenda_items = new ArrayList<>();
    protected SellerGui gui = null;
    public ArrayList<double[]> received_history = new ArrayList<double[]>();
    protected SellerInputGui input_gui = new SellerInputGui(this);
    public ArrayList<String> Sellers = new ArrayList<String>();
    public int exist = 0;
    private AID opponent = new AID();
    final int N_PERIODS = 24;
    private Date deadline = new Date(System.currentTimeMillis() + 86400L * 7000);
    private String contract;
    protected final ArrayList<Double> utilities = new ArrayList<>();
    public ArrayList<Double> prices_target;
    private ArrayList<Double> volumes_target;
    public ArrayList<Double> prices_limit;
    private ArrayList<Double> volumes_limit;
    private ArrayList<String> personal_info;
    private ArrayList<Seller_TechnologyData> list_PowerPlants;
    private String negotiation_protocol;
    public String negotiation_strategy;
    private String negotiation_contract;
    public String contractduration;
    private String negotiation_strategy_algorithm;
    private String negotiation_preference, negotiation_riskpreference;
    public int PERIODS = 24;
    public int VOLUME = 1, risk = 0, ES = 0;
    public double sharing_risk = 0.5;
    public String HOURS;
    
    
    private boolean isPool;
    private boolean isOTC;

    @Override
    protected void setup() {
        file_manager = new FileManager(getLocalName());

        this.addBehaviour(new MessageManager());
        executePhase(0);
    }
    
    class MessageManager extends CyclicBehaviour {

        MessageTemplate mt_ontology = MessageTemplate.and(MessageTemplate.MatchOntology("Market_Ontology"), MessageTemplate.MatchProtocol("no_protocol"));

        @Override
        public void action() {

            ACLMessage msg = myAgent.receive(mt_ontology);
            if (msg != null) {          
                
                if (msg.getContent().contains(";profile;")) {
                    addBelif(msg.getContent().split(";")[0], msg.getContent());
                    gui.updateLog2("Profile received from buyer", Color.BLUE);

                    if (phase == 3) {
                        phase = 4;
                    }
                } else if (msg.getContent().equals("init_conflict_validation")) {
                } else if (msg.getContent().equals("init_prices_request_protocol")) {
                } else if (msg.getContent().equals("init_agenda_definition_protocol")) {
                } else if (msg.getContent().equals("init_deadline_definition_protocol")) {
                    addBehaviour(new deadlineDefinitionProtocol(msg));
                } else if (msg.getContent().equals("init_contract_definition_protocol")) {
                    addBehaviour(new Seller.contractDefinitionProtocol(msg));
                } else if (msg.getContent().equals("end_negotiation")) {
                    input_gui.finish(gui, "");
//                    doDelete();
                    gui.setVisible(false);
                    executePhase(0);
                }

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
            
            System.out.println("ESTOU AQUI 2");
            
            if(msg.getContent().equals("Pool")){
                System.out.println("Agente seller recebeu mensagem a dizer que é simulação Pool");
                set_isPool(true);
            }else if(msg.getContent().equals("OTC")){
                System.out.println("Agente seller recebeu mensagem a dizer que é simulação OTC");
                set_isOTC(true);
            }
            
        }
        
        private void resolveCFP(ACLMessage msg){
            
                
        }
            
            
    }
        
    public void set_isPool(boolean _isPool){
        this.isPool = _isPool;
    }
    
    public void set_isOTC(boolean _isOTC){
        this.isOTC = _isOTC;
    }
    
    public boolean get_isPool(){
        return this.isPool;
    }
    
    public boolean get_isOTC(){
        return this.isOTC;
    }

    public void executePhase(int phase) {

        this.phase = phase;
        
        switch(phase){
            case 0:
                // Ask personal info to send to market agent
                if (Sellers.size() == 0) {
                    input_gui.askPersonalInfo();
                    Sellers.add(getLocalName());
                    exist = 0;
                } else {
                    int j = 0;
                    for (int i = 0; i < Sellers.size(); i++) {
                        if (getLocalName().equals(Sellers.get(i))) {
                            i = Sellers.size();
                            j = 1;
                            exist = 1;
                        }
                    }
                    if (j == 0) {
                        input_gui.askPersonalInfo();
                        Sellers.add(getLocalName());
                        exist = 0;
                    }
                }

                // Add behaviour to contact market agent and await an opponent attribution
                addBehaviour(new helloAndGetOpponentProtocol());

                break;

            case 1:
                //read belifs about opponent in file: Agent Data\%opponent-name%\beliefs_%opponent-name%.xml
                readBeliefs(getOpponent().getLocalName());
                readBeliefs(getLocalName());
                InitGui();
                gui.guiEnableButtons(1);
//                gui.askUserPrices(false);
//                sendPricesAndVolumes();
                break;
            case 2:
                // Ask user prices and send them to the buyer, this is used when the buyer
                // asks for this specific belief and the agent does not have a belief regarding the
                // prices, or when the user wishes to publicize the prices
//                gui.askUserPrices(false);
//                sendPricesAndVolumes();
                break;

            case 4:
                // Agent waits for negotiation initiation
                gui.guiEnableButtons(2);
                break;
        }
    }

    public void updateBelifsFile() {
        // Replaces the belief file content to replicate the currest set of beliefs by the agent
        file_manager.printXmlBelief(getLocalName(), getLocalName(), getBelifsAboutMyAgent());
        Object[] keys = getBelifsAboutOthers().keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            file_manager.printXmlBelief(getLocalName(), keys[i].toString(), getBelifsAboutOthers().get(keys[i]));
        }
    }

    public void InitGui() {

        gui = new SellerGui(this);
    }

    private void readBeliefs(String name) {
        ArrayList<String> beliefs = file_manager.readBeliefsFile(name);
        for (int i = 1; i < beliefs.size(); i++) {
            addBelif(beliefs.get(0), beliefs.get(i));
        }
    }

    public ArrayList<String> getMyPricesVolumes() {

        String belief = searchBelief("myagent", "prices");
        if (belief != null) {
            String[] content = belief.split(";");
            String[] content_aux = content[2].split("-");

            for (int j = 0; j < content_aux.length; j++) {
                String[] content_aux_2 = content_aux[j].split("_");
                my_prices.add(content_aux_2[0]);
                my_prices.add(content_aux_2[1]);

            }
        }

        return my_prices;
    }

    public ArrayList<String> getMyValuesNamesOnly() {

        String belief = searchBelief("myagent", "prices");
        if (belief != null) {
            String[] content = belief.split(";");
            String[] content_aux = content[2].split("-");

            for (int j = 0; j < content_aux.length; j++) {
                String[] content_aux_2 = content_aux[j].split("_");
                my_prices.add(content_aux_2[0]);

            }
        }

        return my_prices;
    }

    public void sendPricesAndVolumes() {

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology("market_ontology");
        String content = searchBelief("myagent", "prices");
        msg.addReceiver(getOpponent());
        msg.setContent(content);
        msg.setProtocol("no_protocol");
        send(msg);
    }

    public ArrayList<String> getAgendaItems() {
        return agenda_items;
    }

    public void setAgendaItems(ArrayList<String> agenda_items) {
        this.agenda_items = agenda_items;
    }

    public ArrayList<AID> getOpponents() {
        return this.opponents;
    }

    public HashMap<String, ArrayList<String>> getBelifsAboutOthers() {
        return this.beliefs_about_others;
    }

    public ArrayList<String> getBelifsAboutMyAgent() {
        return this.beliefs_about_myagent;
    }

    public void setPricesTarget(ArrayList<Double> price_target_final) {
        this.prices_target = price_target_final;
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

    public void setNegotiationProtocol(String protocol) {
        this.negotiation_protocol = protocol;
    }

    public void setNegotiationStrategy(String strategy) {
        this.negotiation_strategy = strategy;
    }

    public void setNegotiationContract(String contract) {
        this.negotiation_contract = contract;
    }

    public void setContractDuration(String contract_target_final) {
        this.contractduration = contract_target_final;
    }

    public void setNegotiationStrategyAlgorithm(String algorithm) {
        this.negotiation_strategy_algorithm = algorithm;
    }

    public void setNegotiationPreference(String preference) {
        this.negotiation_preference = preference;
    }

    public void setNegotiationRiskPreference(String preference) {
        this.negotiation_riskpreference = preference;
    }

    public SellerGui getGui() {
        return gui;
    }

    public SellerInputGui getInputGui() {
        return input_gui;
    }

    public String transformPriceVolumeToBelief(ArrayList<String> prices) {
        String belief = "prices;";
        for (int i = 0; i < prices.size() - 2; i = i + 2) {
            belief = belief + prices.get(i) + "_" + prices.get(i + 1) + "-";
        }
        belief = belief + prices.get(prices.size() - 2) + "_" + prices.get(prices.size() - 1);
        return belief;
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
        //removes the exact belief received in the string
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

    public AID getOpponent() {
        return this.opponent;
    }

    public void setOpponent(AID opponent) {
        this.opponent = opponent;
    }

    public int checkIfReadyToNegotiate() {
        if (this.prices_target != null && this.prices_limit != null && this.negotiation_strategy != null /*&& this.negotiation_protocol != null && this.deadline != null*/) {
            executePhase(4);
            return 1;
        }
        return 0;
    }

    public int checkIfContract() {
        if (this.contract != null) {
            return 1;
        }
        return 0;
    }

    void negotiation() {
        MarketSellerAgent market_seller = new MarketSellerAgent(this);
        market_seller.NegotiationOfBilateralContracts("", ArrayListToArray(prices_target),
                ArrayListToArray(prices_limit), ArrayListToArray(volumes_target), negotiation_strategy, negotiation_strategy_algorithm, negotiation_preference, negotiation_riskpreference, deadline, contract,
                ArrayListToArray(transformMyBeliefToPrice()), ArrayListToArray2(transformOpponentBeliefToProfile(getOpponent().getLocalName())));
    }

    private double[] ArrayListToArray(ArrayList<Double> array_list) {
        double[] array = new double[this.PERIODS];

        for (int i = 0; i < array_list.size(); i++) {
            array[i] = array_list.get(i);
        }
        return array;
    }

    private double[] ArrayListToArray2(ArrayList<Double> array_list) {
        double[] array = new double[this.PERIODS + 1];

        for (int i = 0; i < array_list.size(); i++) {
            array[i] = array_list.get(i);
        }
        return array;
    }

    protected void defineDeadline() {
//        addBehaviour(new deadlineDefinitionProtocol(null));
    }

    public void setPersonalInfo(ArrayList<String> personal_info) {
        this.personal_info = personal_info;
    }

    public ArrayList<Double> transformOpponentBeliefToProfile(String name) {

        if (getBelifsAboutOthers().containsKey(name)) {
            ArrayList<Double> volumes = new ArrayList<>();

            String belief = searchBelief(name, "profile");

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

    protected void utility(SellerGui parent, int choices, String name) {

        String[] Name = {"Sent Proposals", "Received Proposals", "Calculated Proposals"};

        utility demo = null;
        if (choices == 1) {
//            System.out.println("\nSeller Utilities:\n");
//            for(int i=0; i<utilities.size();i++){
//                System.out.println(utilities.get(i) +"\n");
//            }
            demo = new utility(name, utilities, 2, "Time", "Seller's Utility", Name, choices, 0, calculatedscore);
        } else {
            demo = new utility(name, utilities, 2, "Proposal", "Seller's Utility", Name, choices, 0, calculatedscore);
        }
        demo.pack();
        demo.setLocation(parent.getX() + 100, parent.getY() + 100);
//        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    public ArrayList<Double> transformMyBeliefToPrice() {

        if (searchBelief("myagent", "prices") != null) {
            ArrayList<Double> prices = new ArrayList<>();

            String belief = searchBelief("myagent", "prices");

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

    private class helloAndGetOpponentProtocol extends Behaviour {

        private MessageTemplate mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        private int step = 0;

        @Override
        public void action() {

            switch (step) {
                case 0:
                    ACLMessage msg_exist = new ACLMessage(ACLMessage.INFORM);
                    String agent_info = ";name_" + personal_info.get(0) + ";address_" + personal_info.get(1) + ";telephone_" + personal_info.get(2) + ";email_" + personal_info.get(3);
                    msg_exist.setContent(getLocalName() + ";is_seller" + agent_info);
                    msg_exist.setOntology("market_ontology");
                    msg_exist.setProtocol("no_protocol");
                    msg_exist.addReceiver(system_agent);
                    if (exist == 0) {
                        send(msg_exist);
                    }

                    ACLMessage msg_cfp_buyer = new ACLMessage(ACLMessage.CFP);
                    msg_cfp_buyer.setContent("propose_opponent");
                    msg_cfp_buyer.setOntology("market_ontology");
                    msg_cfp_buyer.setProtocol("no_protocol");
                    addBelif("myagent", getLocalName() + ";waiting_for_opponent");
                    msg_cfp_buyer.addReceiver(system_agent);
                    send(msg_cfp_buyer);

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

    public class sendproposal extends Behaviour {

        private MessageTemplate mt = MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("hello_protocol")), MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        private int step = 0;

        @Override
        public void action() {

            ACLMessage msg_exist = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            String agent_info = ";name_" + personal_info.get(0) + ";address_" + personal_info.get(1) + ";telephone_" + personal_info.get(2) + ";fax_" + personal_info.get(3) + ";email_" + personal_info.get(4);
            msg_exist.setContent(getLocalName() + ";is_seller" + agent_info);
            msg_exist.setOntology("market_ontology");
            msg_exist.setProtocol("no_protocol");
            msg_exist.addReceiver(system_agent);
            send(msg_exist);

            block();

        }

        @Override
        public boolean done() {

            if (step == 2) {
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
                        //send first proposal
                        String date = getInputGui().askDeadline(gui, null);
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
                        }

                    } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        deadline = new Date(Long.valueOf(msg.getContent()));
                        checkIfReadyToNegotiate();
                        //Send end protocol inform
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
        String aux;
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
                        aux = contract;
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
                        aux = contract;

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
                            aux = contract;
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
                            contract = aux;
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

    public ArrayList<Seller_TechnologyData> getList_PowerPlants() {
        return list_PowerPlants;
    }

    public void setList_PowerPlants(ArrayList<Seller_TechnologyData> list_PowerPlants) {
        this.list_PowerPlants = list_PowerPlants;
    }
}
