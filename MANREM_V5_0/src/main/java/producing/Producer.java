package producing;

import Trader.AgentData;
import Trader.Trader;
import graphics.utility;
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
import javax.swing.JOptionPane;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import scheduling.EnterGENCO;
import scheduling.MediumValueException;
import scheduling.MinMaxException;
import scheduling.ProducerScheduling;
import xml.FileManager;
import wholesalemarket_SMP.InputData_Agents;

public class Producer extends Trader {

    InputData_Agents mainGenerator; 
    private int phase = 0;
    private FileManager file_manager = new FileManager(getLocalName());
    public EnterGENCO setPowerPlant;
    private HashMap<String, ArrayList<String>> beliefs_about_others = new HashMap();
    private ArrayList<String> beliefs_about_myagent = new ArrayList<>();
    private ArrayList<String> my_prices = new ArrayList<>();
    private AID system_agent = new AID("PersonalAssistant", AID.ISLOCALNAME);
    public double calculatedscore = 0.0, counteroffer = 0;
    private ArrayList<AID> opponents = new ArrayList<>();
    private ArrayList<String> agenda_items = new ArrayList<>();
    protected ProducerGui gui = null;
    public ArrayList<double[]> received_history = new ArrayList<double[]>();
    protected ProducerInputGui input_gui = new ProducerInputGui(this);
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
    private ArrayList<Producer_TechnologyData> list_PowerPlants;
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
    
    private boolean isPool = false;
    private boolean isSMP = false;
    private boolean isLMP = false;
    private boolean isOTC = false;
    /**
     * 4 types of Producing technologies Thermal, Hydro, Wind and Solar
     * Availiable_Tech[0] -> Thermal
     * Availiable_Tech[1] -> Hydro
     * Availiable_Tech[2] -> Wind
     * Availiable_Tech[3] -> Solar 
     */
    private boolean[] Availiable_Tech = new boolean[4]; 
    public ArrayList<DataHydro> DataHydro = new ArrayList();
    public ArrayList<DataThermal> DataThermal = new ArrayList();
    public ArrayList<DataWind> DataWind = new ArrayList();
    public String Strategy;
    
    public AgentData information;

    @Override
    protected void setup() {
        this.information = new AgentData();
        this.addBehaviour(new MessageManager());
        executePhase(0);
    }

    @Override
    public void executePhase(int phase) {

        this.phase = phase;
        switch (phase) {

            case 0:
                // Ask personal info to send to market agent
                
                // Reads first page of info file, which contains basic agent info
                this.read_info();
                
                // Read other pages of information.xls file which contain available production technologies and their data
                boolean[] _avaliable_Tech = new boolean[4];
                _avaliable_Tech = this.read_tech();
                this.setAvailiable_Tech(_avaliable_Tech);
                
                // Send information to PersonalAssisntant
                addBehaviour(new helloProtocol());
                
                // Code for introducing new producer to Personal Assistant has been changed.
                // Previous Code is commented below
                
                
//                ArrayList<String> personal_info = new ArrayList<>();
//                if (Sellers.size() == 0) {
//                    input_gui.setPowerPlant = new EnterGENCO(this.getLocalName());      
//                    input_gui.setPowerPlant.setVisible(true);
//                    Sellers.add(getLocalName());
//  
//                } else {
//                    int j = 0;
//                    for (int i = 0; i < Sellers.size(); i++) {
//                        if (getLocalName().equals(Sellers.get(i))) {
//                            i = Sellers.size();
//                            j = 1;
//                            exist = 1;
//                        }
//                    }
//                    if (j == 0) {
//                        input_gui.setPowerPlant = new EnterGENCO(this.getLocalName()); 
//                        setPowerPlant.setVisible(true);
//                        Sellers.add(getLocalName());
//                        exist = 0;
//                    }
//                }
//                for (int i = 0; i < setPowerPlant.personal_info.size(); i++) {
//                    personal_info.add(setPowerPlant.personal_info.get(i));
//                }
//                
//                
//                
//                setPersonalInfo(personal_info);
//
//                addBehaviour(new helloAndGetOpponentProtocol());

                break;
            
            case 1:
                // phase 1 generates offers
                
                this.information.initializePower();
                this.information.initializePrice();
                
                if(Strategy.contains("Default")){
                    // Standard strategy means reading input data from excel file on the folder of this Agent
                    // The file is named Standard_Strat.xls
                    
                    read_Standardstrat();
                }
                //Missing code for other strategies
                
                // Sendo offer data to PersonalAssitant
                send_Offers();
                
//<-----------------------------------------------------------------------------        
// Commented previous Switch because it was not functional right now. João de Sá
//<-----------------------------------------------------------------------------                
        
//            case 0:
//                // Ask personal info to send to market agent
//                ArrayList<String> personal_info = new ArrayList<>();
//                if (Sellers.size() == 0) {
//                    input_gui.setPowerPlant = new EnterGENCO(this.getLocalName());      
//                    input_gui.setPowerPlant.setVisible(true);
//                    Sellers.add(getLocalName());
//  
//                } else {
//                    int j = 0;
//                    for (int i = 0; i < Sellers.size(); i++) {
//                        if (getLocalName().equals(Sellers.get(i))) {
//                            i = Sellers.size();
//                            j = 1;
//                            exist = 1;
//                        }
//                    }
//                    if (j == 0) {
//                        input_gui.setPowerPlant = new EnterGENCO(this.getLocalName()); 
//                        setPowerPlant.setVisible(true);
//                        Sellers.add(getLocalName());
//                        exist = 0;
//                    }
//                }
//                for (int i = 0; i < setPowerPlant.personal_info.size(); i++) {
//                    personal_info.add(setPowerPlant.personal_info.get(i));
//                }
//                setPersonalInfo(personal_info);
//
//                addBehaviour(new helloAndGetOpponentProtocol());
//
//                break;
//            case 1:
//                //read belifs about opponent in file: Agent Data\%opponent-name%\beliefs_%opponent-name%.xml
//                readBeliefs(getOpponent().getLocalName());
//                readBeliefs(getLocalName());
//                InitGui();
//                gui.guiEnableButtons(1);
////                gui.askUserPrices(false);
////                sendPricesAndVolumes();
//                break;
//            case 2:
//                // Ask user prices and send them to the buyer, this is used when the buyer
//                // asks for this specific belief and the agent does not have a belief regarding the
//                // prices, or when the user wishes to publicize the prices
////                gui.askUserPrices(false);
////                sendPricesAndVolumes();
//                break;
//
//            case 4:
//                // Agent waits for negotiation initiation
//                gui.guiEnableButtons(2);
//                break;
        }
    }
    
    private void send_Offers(){
        
        ACLMessage info_msg = new ACLMessage(ACLMessage.INFORM);

        String agent_offer = "Offer " + this.information.getName() + " Producer";
        
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
            int row = 25; //s.getRows()
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
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    class MessageManager extends CyclicBehaviour {

        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol"));

        @Override
        public void action() {

            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                if (msg.getOntology().equals("market_ontology")) {
                    
                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve(msg);
                    
                }

//<-----------------------------------------------------------------------------        
// Commented previous code because it was not functional right now. João de Sá
//<-----------------------------------------------------------------------------                
                
//                if (msg.getContent().contains(";profile;")) {
//                    addBelif(msg.getContent().split(";")[0], msg.getContent());
//                    gui.updateLog2("Profile received from buyer", Color.BLUE);
//
//                    if (phase == 3) {
//                        phase = 4;
//                    }
//                } else if (msg.getContent().equals("init_conflict_validation")) {
//                } else if (msg.getContent().equals("init_prices_request_protocol")) {
//                } else if (msg.getContent().equals("init_agenda_definition_protocol")) {
//                } else if (msg.getContent().equals("init_deadline_definition_protocol")) {
//                    addBehaviour(new deadlineDefinitionProtocol(msg));
//                } else if (msg.getContent().equals("init_contract_definition_protocol")) {
//                    addBehaviour(new Producer.contractDefinitionProtocol(msg));
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
                }else if(msg.getContent().contains("Moghimi")){
                    Strategy = "Moghimi";
                }else if(msg.getContent().contains("Zhang")){
                    Strategy = "Zhang";
                }else if(msg.getContent().contains("Conejo")){
                    Strategy = "Conejo";
                }
                executePhase(1);
            }
        }
        
        private void resolveCFP(ACLMessage msg){

        }
            
            
    }
    
    public void read_info() {
        File f = new File("files\\"+getLocalName()+"\\information.xls");

        Workbook wb;
        try {
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
            
            // Initial value of Information variable is set to false
            this.information.setParticipating(false);
            
        } catch (BiffException ex) {
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean[] read_tech(){
        boolean[] _avaliable_Tech = new boolean[4];
        File f = new File("files\\"+getLocalName()+"\\information.xls");
        
        for(int i = 0; i < _avaliable_Tech.length; i++){
            _avaliable_Tech[i] = false;
        }
        
        Workbook wb;
        try {
            wb = Workbook.getWorkbook(f);
            
            // Sheet with numbers higher than 0 contain technology information
            for(int i = 1; i < wb.getNumberOfSheets(); i++){
                Sheet s = wb.getSheet(i);

                if(s.getName().equals("Thermal")){
                    _avaliable_Tech[0] = true;
                    this.storeThermalData(s);
                }else if(s.getName().equals("Hydro")){
                    _avaliable_Tech[1] = true;
                    this.storeHydroData(s);
                }else if(s.getName().equals("Wind")){
                    _avaliable_Tech[2] = true;
                    this.storeWindData(s);
                }else if(s.getName().equals("Solar")){
                    _avaliable_Tech[3] = true;
                    this.storeSolarData(s);
                }
                
                int row = s.getRows();
                int col = s.getColumns();
                Cell c;
            }
            
            return _avaliable_Tech;
            
            
        } catch (BiffException ex) {
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return _avaliable_Tech;
    }
    
    
    
    private class helloProtocol extends Behaviour {
        
        private int step = 0;

        @Override
        public void action() {

            switch (step) {
                case 0:
                    
                    // Message to be sent will have the following format
                    // "Name";"Address";"PhoneNumber";"Email";"Objective";tech;"ThermalTechnolgies";"WindTechnologies";"HydroTechnologies"
                    // The fields between "" refer to information within the dataStructures of this Agent
                    // If the agent doesn't have a particular technology, the respective field will be empty
                    // Differente avaliable technologies of a ceratain type will be separated by "_"
                    
                    
                    // First part of the message contains basic information
                    // Each field is separated by ";"
                    ACLMessage info_msg = new ACLMessage(ACLMessage.INFORM);

                    String agent_info = "" + information.getName() + ";" + "isProducer" + ";" + information.getAddress()
                            + ";" + information.getPhone_number() + ";" + information.getEmail() + ";" + information.getObjective()+ ";";
                    
                    

                    // Second part of the message contains avaliable technology information
                    // Each technology is separated by ";"
                    // Each field of information for a particular technology is separated by "_"
                    
                    
                    agent_info = agent_info + "tech;";
                    
                    for(int i=0; i < DataThermal.size(); i++){
                        agent_info = agent_info + DataThermal.get(i).getID() + "_";
                        agent_info = agent_info + DataThermal.get(i).getMaxP() + "_";
                        agent_info = agent_info + DataThermal.get(i).getMinP() + "_";
                        agent_info = agent_info + DataThermal.get(i).getFuel() + "_";
                        agent_info = agent_info + DataThermal.get(i).FCost + "_";
                    }
                    if(DataThermal.isEmpty()){
                        agent_info = agent_info + " ";
                    }
                    agent_info = agent_info + ";";
                    for(int i=0; i < DataWind.size(); i++){
                        agent_info = agent_info + DataWind.get(i).getID() + "_";
                        agent_info = agent_info + DataWind.get(i).MaxP + "_";
                        agent_info = agent_info + DataWind.get(i).MinP + "_";
                        agent_info = agent_info + DataWind.get(i).getFCost() + "_";
                    }
                    if(DataWind.isEmpty()){
                        agent_info = agent_info + " ";
                    }
                    agent_info = agent_info + ";";
                    for(int i=0; i < DataHydro.size(); i++){
                        agent_info = agent_info + DataHydro.get(i).getID() + "_";
                        agent_info = agent_info + DataHydro.get(i).getPi() + "_";
                        agent_info = agent_info + DataHydro.get(i).getFCost() + "_";
                    }
                    if(DataHydro.isEmpty()){
                        agent_info = agent_info + " ";
                    }
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
    
    public void set_isPool(boolean _isPool){
        this.isPool = _isPool;
    }
    
    public void set_isSMP(boolean _isSMP){
        this.isSMP = _isSMP;
    }
    
    public void set_isLMP(boolean _isLMP){
        this.isLMP = _isLMP;
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
    
    public boolean get_isSMP(){
        return this.isSMP;
    }
    
    public boolean get_isLMP(){
        return this.isLMP;
    }
    
    public int getPhase(){
        return this.phase;
    }
    
    public void setAvailiable_Tech(boolean[] _Availiable_Tech){
        this.Availiable_Tech = _Availiable_Tech;
    }
    
    public boolean getAvailiable_Tech(int i){
        return this.Availiable_Tech[i];
    }

    public void storeThermalData(Sheet s){
        
        int row = s.getRows();
        Cell c;
        
        String GENCO_name;      
        String ID;
        String Fuel;
        double MinP;
        double MaxP; 
        double RU; //Ramp-up
        double RD; //Ramp-down
        double MinOn;
        double MinOff;
        double PrevProd; //Production at t0
        double FCost;
        double VCost;
        double SUcost;
        double SDcost;
        int initStatus;
        double FuelCfixed;
        double FuelCvar;
        double EmCO2;
        double EmNO2;
        
        DataThermal new_Data;
        
        for(int i = 2; i < row; i++){
            GENCO_name = getLocalName();
            c = s.getCell(0, i);
            ID = c.getContents();
            c = s.getCell(1, i);
            Fuel = c.getContents();
            c = s.getCell(2, i);
            MinP = Double.parseDouble(c.getContents());
            c = s.getCell(3, i);
            MaxP = Double.parseDouble(c.getContents()); 
            c = s.getCell(4, i);
            RU = Double.parseDouble(c.getContents()); //Ramp-up
            c = s.getCell(5, i);
            RD = Double.parseDouble(c.getContents()); //Ramp-down
            c = s.getCell(6, i);
            MinOn = Double.parseDouble(c.getContents());
            c = s.getCell(7, i);
            MinOff = Double.parseDouble(c.getContents());
            c = s.getCell(17, i);
            PrevProd = Double.parseDouble(c.getContents()); //Production at t0
            c = s.getCell(11, i);
            FCost = Double.parseDouble(c.getContents());
            c = s.getCell(12, i);
            VCost = Double.parseDouble(c.getContents());
            c = s.getCell(8, i);
            SUcost = Double.parseDouble(c.getContents());
            c = s.getCell(9, i);
            SDcost = Double.parseDouble(c.getContents());
            c = s.getCell(10, i);
            initStatus = Integer.parseInt(c.getContents());
            c = s.getCell(13, i);
            FuelCfixed = Double.parseDouble(c.getContents());
            c = s.getCell(14, i);
            FuelCvar = Double.parseDouble(c.getContents());
            c = s.getCell(15, i);
            EmCO2 = Double.parseDouble(c.getContents());
            c = s.getCell(16, i);
            EmNO2 = Double.parseDouble(c.getContents());
            
            try {
                new_Data = new DataThermal(GENCO_name, ID,  Fuel,  MinP,  MaxP,  RU,  RD,
                        MinOn,  MinOff,  PrevProd, FCost,  VCost,  SUcost, SDcost,  initStatus,
                        FuelCfixed,  FuelCvar,  EmCO2,  EmNO2);
                
                this.DataThermal.add(new_Data);
                
            } catch (MinMaxException | MediumValueException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
  
        }

    }
    
    public void storeHydroData(Sheet s){
        
        int row = s.getRows();
        Cell c;
        
        String GENCO_name;  
        String ID;
        double MinReserve;
        double MaxReserve;
        double InitReserve;
        double MinDisch;
        double MaxDisch;
        double VCost;
        String Cascadeorder;
        double pl1;
        double pl2;
        double pl3;
        double pl4;
        double Ul;
        double P01;
        double P02;
        double P03;
        double Pi;
        double curve;
        double startupcost;
        double inflow;
        double mediumlevel;
        double upperlevel;
        double FCost;
        double prevProduction;
        
        DataHydro new_Data;
        
        for(int i = 1; i < row; i++){
            GENCO_name = getLocalName();
            c = s.getCell(0, i);
            ID = c.getContents();
            c = s.getCell(1, i);
            MinDisch = Double.parseDouble(c.getContents());
            c = s.getCell(2, i);
            MaxDisch = Double.parseDouble(c.getContents());
            c = s.getCell(3, i);
            InitReserve = Double.parseDouble(c.getContents());
            c = s.getCell(4, i);
            mediumlevel = Double.parseDouble(c.getContents());
            c = s.getCell(5, i);
            upperlevel = Double.parseDouble(c.getContents());
            c = s.getCell(6, i);
            MinReserve = Double.parseDouble(c.getContents());
            c = s.getCell(7, i);
            MaxReserve = Double.parseDouble(c.getContents());
            c = s.getCell(8, i);
            startupcost = Double.parseDouble(c.getContents());
            c = s.getCell(9, i);
            pl1 = Double.parseDouble(c.getContents());
            c = s.getCell(10, i);
            pl2 = Double.parseDouble(c.getContents());
            c = s.getCell(11, i);
            pl3 = Double.parseDouble(c.getContents());
            c = s.getCell(12, i);
            pl4 = Double.parseDouble(c.getContents());
            c = s.getCell(13, i);
            Ul = Double.parseDouble(c.getContents());
            c = s.getCell(14, i);
            curve = Double.parseDouble(c.getContents());
            c = s.getCell(15, i);
            P01 = Double.parseDouble(c.getContents());
            c = s.getCell(16, i);
            P02 = Double.parseDouble(c.getContents());
            c = s.getCell(17, i);
            P03 = Double.parseDouble(c.getContents());
            c = s.getCell(18, i);
            Pi = Double.parseDouble(c.getContents());
            c = s.getCell(19, i);
            inflow = Double.parseDouble(c.getContents());
            c = s.getCell(20, i);
            FCost = Double.parseDouble(c.getContents());
            c = s.getCell(21, i);
            VCost = Double.parseDouble(c.getContents());
            c = s.getCell(22, i);
            prevProduction = Double.parseDouble(c.getContents());
            Cascadeorder = "";

            try {
                new_Data = new DataHydro(GENCO_name, ID, MinReserve, MaxReserve, InitReserve, MinDisch,
                        MaxDisch, VCost, Cascadeorder, pl1, pl2, pl3, pl4, Ul, P01, P02, P03, Pi, curve,
                        startupcost, inflow, mediumlevel, upperlevel, FCost, prevProduction);
                
                this.DataHydro.add(new_Data);
                
            } catch (MinMaxException | MediumValueException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
  
    }
    
    public void storeWindData(Sheet s){
        
        int row = s.getRows();
        Cell c;
        
        DataWind new_Data;
        
        String GENCO_name;   
        String ID;
        double MinP;
        double MaxP;
        double FCost;
        double VCost;
        double [] Production;
        
        for(int i = 2; i < row; i++){
            
            GENCO_name = getLocalName();
            c = s.getCell(0, i);
            ID = c.getContents();
            c = s.getCell(1, i);
            MinP = Double.parseDouble(c.getContents());
            c = s.getCell(2, i);
            MaxP = Double.parseDouble(c.getContents());
            c = s.getCell(3, i);
            FCost = Double.parseDouble(c.getContents());
            c = s.getCell(4, i);
            VCost = Double.parseDouble(c.getContents());
            
            // Os valores de forecast sao aleatorios e gerados sempre que o ficheiro excel é aberto
            double[] forecastWind = new double [24];
            for (int j = 0; j < 24; j++){
                int k = j+5;
                c = s.getCell(k, i);
                forecastWind[j] = Double.parseDouble(c.getContents());
            }
            Production = forecastWind;
            
            try {
                new_Data = new DataWind(GENCO_name, ID, MinP, MaxP, FCost, VCost, Production);
                
                this.DataWind.add(new_Data);
                
            } catch (MinMaxException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    public void storeSolarData(Sheet s){
        
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

        gui = new ProducerGui(this);
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

    public ProducerGui getGui() {
        return gui;
    }

    public ProducerInputGui getInputGui() {
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
        MarketProducerAgent market_seller = new MarketProducerAgent(this);
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

//        protected void defineContract() {
//        addBehaviour(new contractDefinitionProtocol(null));
//    }
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

    protected void utility(ProducerGui parent, int choices, String name) {

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
//                            if (msg.getContent().equals("spot")){
//                                
////                                new ProducerScheduling(personal_info.get(0)).setVisible(true);
////                                mainGenerator.openRiskAttitude();
//                            
//                            }else{
                            if (beliefExists("myagent", getLocalName() + ";waiting_for_opponent")) {
                                removeBelief("myagent", getLocalName() + ";waiting_for_opponent");

                                String[] content_information = msg.getContent().split(";");
                                setOpponent(new AID(content_information[0], AID.ISLOCALNAME));

                                step = 2;
                            }
//                            }
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

    public ArrayList<Producer_TechnologyData> getList_PowerPlants() {
        return list_PowerPlants;
    }

    public void setList_PowerPlants(ArrayList<Producer_TechnologyData> list_PowerPlants) {
        this.list_PowerPlants = list_PowerPlants;
    }
}
