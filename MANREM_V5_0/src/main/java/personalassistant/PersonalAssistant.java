package personalassistant;

import Trader.AgentData;
import buying.BuyerInputGui;
import wholesalemarket_LMP.Wholesale_InputData;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import market.panel.DR;
import market.panel.deadline;
import market.panel.protocol;
import market.panel.rbuyer;
import market.panel.rseller;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import scheduling.EnterGENCO;
import tools.TimeChooser;



public class PersonalAssistant extends Agent {
    
    private Bilateral_ContractType_Form contractTypeForm;
    Bilateral_NegotiationOption negotiationForm;
    
    private HashMap<String, ArrayList<String>> beliefs_about_others = new HashMap();
    private ArrayList<String> beliefs_about_myagent = new ArrayList<>();
    private String[] contract_list = {"Forward Contract", "Contract For Difference", "Option Contract"};
    private String[] protocol_list = {"Alternating Offers"};
    public String agent_type = "";
    
    public ArrayList<AID> seller_names = new ArrayList<>();
    public ArrayList<AID> buyer_names = new ArrayList<>();
    public ArrayList<AID> producer_names = new ArrayList<>();
    public ArrayList<AID> largeConsumer_names = new ArrayList<>();
    public ArrayList<AID> mediumConsumer_names = new ArrayList<>();
    private AID MarketOperator = new AID("MarketOperator", AID.ISLOCALNAME);
    public PersonalAssistantGUI mo_gui;
    private AID[] new_pair;
    public BuyerInputGui buy_gui;
    private ArrayList<AID[]> negotiation_pairs = new ArrayList<>();
    private HashMap<AID, ArrayList<ArrayList<ACLMessage>>> message_history = new HashMap<>();
    private HashMap<AID, Integer> message_conversation_ids = new HashMap<>();
    public int text = 0;
    public int N_PERIODS = 24;
    public javax.swing.JSlider jS1;
    public javax.swing.JTextField jT1;
    public javax.swing.JTextField jT2;
    public javax.swing.JTextField jTseller;
    public javax.swing.JTextField jTbuyer;

    ImageIcon negicon = new ImageIcon("images\\icon1.png");
    ImageIcon conicon = new ImageIcon("images\\contract.png");
    ImageIcon buyicon = new ImageIcon("images\\buyicon.png");
    ImageIcon sellicon = new ImageIcon("images\\retailicon.jpg");

    public int demandresponse = 0, seller_risk = 0, buyer_risk = 0;
    public double buyer_risk_exposure = 0, seller_risk_exposure = 1;
    public int contractduration = 5;
    public String contract = "Forward Contract";
    public ArrayList<String> HOURS = new ArrayList<>();
    private Font font_1 = new Font("Arial", Font.BOLD, 13);
    private String location = "images\\";
    private String icon_agenda_location = location + "icon1.png";
    private String icon_risk = location + "icon1.png";
    public Date sellerdeadline, buyerdeadline;
    private JTextField[] limits, duration, periods;
    public ArrayList<String> hours = new ArrayList<>();
    JCheckBox chinButton;
    JCheckBox chinButton2;
    
    
    public ArrayList<BuyerData> Buyers_Information = new ArrayList();
    public ArrayList<ProducerData> Producers_Information = new ArrayList();
    
    
    //private int inteiro = Wholesale_InputData.

    // <-------------------------------------------------------------------------------------------------------------
    // TEMPORARIO!!!!
    
    private MarketParticipants participants;
    
    // <-------------------------------------------------------------------------------------------------------------
    
    //================================ Bilateral Var Info ================================
    private int bilateral_contractType = 0; // 1- Standarized; 2-Negotiated
    private int bilateral_contractDuration;
    private int bilateral_tradingProcess = 0; //1- Alternating Offers; 2- Contract Net
    private int bilateral_tariff;
    private int bilateral_hoursPeriod;
    private int bilateral_year, bilateral_month, bilateral_day, bilateral_hour, bilateral_minutes;
    private EnterGENCO genco;

    // Usar os métodos get já desenvolvidos para obtenção das variáveis do contrato bilateral estabelecidas no menu "Markets"
    //=====================================================================================
    @Override
    protected void setup() {

        this.addBehaviour(new MessageManager());
        mo_gui = new PersonalAssistantGUI(this);    

        
//        buy_gui = new Init(this);
    }

    public void addAgent(AID agent, String type, ProducerData newProducer, BuyerData newBuyer) {
        if (type.equals("seller")) {
            this.seller_names.add(agent);
        } else if (type.equals("producer")) {
            this.Producers_Information.add(newProducer);
            this.producer_names.add(agent);
            String name;
            name = newProducer.getName().replace(" ", "_");
            addBelif(newProducer.getName(), name + ";" + "isSeller");
            addBelif(newProducer.getName(), name + ";" + "name_" + "name");
            addBelif(newProducer.getName(), name + ";" + "address_" + newProducer.getAddress());
            addBelif(newProducer.getName(), name + ";" + "telephone_" + newProducer.getPhone_number());
            addBelif(newProducer.getName(), name + ";" + "email_" + newProducer.getEmail());
        } else if (type.equals("buyer")) {
            this.Buyers_Information.add(newBuyer);
            this.buyer_names.add(agent);
            String name;
            name = newBuyer.getName().replace(" ", "_");
            addBelif(newBuyer.getName(), name + ";" + "isBuyer");
            addBelif(newBuyer.getName(), name + ";" + "name_" + "name");
            addBelif(newBuyer.getName(), name + ";" + "address_" + newBuyer.getAddress());
            addBelif(newBuyer.getName(), name + ";" + "telephone_" + newBuyer.getPhone_number());
            addBelif(newBuyer.getName(), name + ";" + "email_" + newBuyer.getEmail());
        } else if (type.equals("large_consumer")) {
            this.largeConsumer_names.add(agent);
        } else if (type.equals("coalition")) {
            this.largeConsumer_names.add(agent);
        }else if (type.equals("consumer")) {
            this.mediumConsumer_names.add(agent);
        } else if (type.equals("MarketOperator")) {
            this.MarketOperator = agent;
        } 
        mo_gui.addAgent(agent.getLocalName(), type);
    }

    private HashMap<String, ArrayList<String>> getBelifsAboutOthers() {
        return this.beliefs_about_others;
    }
    
    public void startsimulation(boolean isSMPsym, boolean isSMPasym, boolean isLMP, boolean isOTC){
        
        // Sends message to MarketOperator to start the simulation
        AID rec = new AID("MarketOperator", AID.ISLOCALNAME);
        
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology("market_ontology");
        msg.setProtocol("no_protocol");
        
        if(isSMPsym){
            this.Write_Input_File();
            msg.setContent("Start Simulation SMPsym");
        } else if(isSMPasym) {
            msg.setContent("Start Simulation SMPasym");
        } else if(isLMP) {
            msg.setContent("Start Simulation LMP");
        } else if(isOTC) {
            msg.setContent("Start Simulation OTC");
        }
        
        msg.addReceiver(rec);
        
        send(msg);
        
    }
    
    public void Write_Input_File(){
        try {
            //create .xls and create a worksheet.
            int rownumber = 0;
            FileOutputStream fos = new FileOutputStream("input.xls");
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("Sheet1");
            HSSFRow row;
            HSSFCell cell;
            
            row = worksheet.createRow((short) rownumber);
            rownumber++;
            cell = row.createCell((short) 0);
            cell.setCellValue("Player");
            cell = row.createCell((short) 1);
            cell.setCellValue("Period");
            cell = row.createCell((short) 2);
            cell.setCellValue("Power");
            cell = row.createCell((short) 3);
            cell.setCellValue("Price");
            
            for(int i = 0; i < this.Buyers_Information.size(); i++){
                
                if(this.Buyers_Information.get(i).getParticipating()){
                    for(int j=0; j < this.Buyers_Information.get(i).getPower().size(); j++){
                        row = worksheet.createRow((short) (rownumber));
                        rownumber++;
                        cell = row.createCell((short) 0);
                        cell.setCellValue(this.Buyers_Information.get(i).getName());
                        cell = row.createCell((short) 1);
                        cell.setCellValue("" + (j+1));
                        cell = row.createCell((short) 2);
                        cell.setCellValue("" + this.Buyers_Information.get(i).getPower().get(j));
                        cell = row.createCell((short) 3);
                        cell.setCellValue("" + this.Buyers_Information.get(i).getPrice().get(j));
                    }
                }
            }
            
            row = worksheet.createRow((short) rownumber);
            rownumber++;
            
            row = worksheet.createRow((short) rownumber);
            rownumber++;
            cell = row.createCell((short) 0);
            cell.setCellValue("Player");
            cell = row.createCell((short) 1);
            cell.setCellValue("Period");
            cell = row.createCell((short) 2);
            cell.setCellValue("Power");
            cell = row.createCell((short) 3);
            cell.setCellValue("Price");
                
            for(int i = 0; i < this.Producers_Information.size(); i++){
                
                if(this.Producers_Information.get(i).getParticipating()){
                    for(int j=0; j < this.Producers_Information.get(i).getPower().size(); j++){
                        row = worksheet.createRow((short) (rownumber));
                        rownumber++;
                        cell = row.createCell((short) 0);
                        cell.setCellValue(this.Producers_Information.get(i).getName());
                        cell = row.createCell((short) 1);
                        cell.setCellValue("" + (j+1));
                        cell = row.createCell((short) 2);
                        cell.setCellValue("" + this.Producers_Information.get(i).getPower().get(j));
                        cell = row.createCell((short) 3);
                        cell.setCellValue("" + this.Producers_Information.get(i).getPrice().get(j));
                    }
                }
            }
                    
//
//            //Create ROW-1
//            HSSFRow row1 = worksheet.createRow((short) 0);
//
//            //Create COL-A from ROW-1 and set data
//            HSSFCell cellA1 = row1.createCell((short) 0);
//            cellA1.setCellValue("Sno");
//
//
//            //Create COL-B from row-1 and set data
//            HSSFCell cellB1 = row1.createCell((short) 1);
//            cellB1.setCellValue("Name");
//
//
//            //Create COL-C from row-1 and set data
//            HSSFCell cellC1 = row1.createCell((short) 2);
//            cellC1.setCellValue("Coisas");
//
//            //Create COL-D from row-1 and set data
//            HSSFCell cellD1 = row1.createCell((short) 3);
//            cellD1.setCellValue("Cenas");


            //Save the workbook in .xls file
            workbook.write(fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


// João de Sá
    // <---------------------------------------------------------------------------------------------------
    // TEMPORARIO!!!
    /*
    public void chooseParticipants(boolean isProducer, boolean isDayAhead, boolean isSMP, boolean isOTC){
        String[] agentNames = null;
        
        participants = new MarketParticipants(this, isProducer, isDayAhead, isSMP, isOTC);
        participants.setVisible(true);
    }
    */
    // <------------------------------------------------------------------------------------------------------
    public void start_OTC_simul(String Agent){
        
        AID rec = new AID(Agent, AID.ISLOCALNAME);
        
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setOntology("market_ontology");
        msg.setProtocol("no_protocol");
        msg.setContent("Sending simulation information");
        msg.addReceiver(rec);
        
        send(msg);
    }
    
    public void send_OTC_sim_data(String Agent, String[] message){
        
        AID rec = new AID(Agent, AID.ISLOCALNAME);
        
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology("market_ontology");
        msg.setProtocol("no_protocol");
        msg.setContent("" + message[0] + " " + message[1] + " " + message[2] + " " 
                + message[3] + " volume_bids " + message[4] + "price_bids " + message[5] + "end");
        msg.addReceiver(rec);
        

        
        send(msg);
        
    }
    
    
// Inform Method makes market agent send the results of the SMP simulation to 
// the respective agent. The receiver of the message comes as an argument (Rec)

    
    public void inform(AID Rec, String prices, String volumes){
        
        //Send prices
        
        ACLMessage msg_prices = new ACLMessage(ACLMessage.INFORM);
        msg_prices.setOntology("Market_Ontology");
        msg_prices.setProtocol("hello_protocol");
        msg_prices.setContent(prices);  //Need to make this send prices and volumes
        msg_prices.addReceiver(Rec);
        
        send(msg_prices);
        
        //Send Volumes
        
        ACLMessage msg_volumes = new ACLMessage(ACLMessage.INFORM);
        msg_volumes.setOntology("Market_Ontology");
        msg_volumes.setProtocol("hello_protocol");
        msg_volumes.setContent(volumes);  //Need to make this send prices and volumes
        msg_volumes.addReceiver(Rec);
        
        send(msg_volumes);
        
        //Need to make agents handle the messages
        
        
    }
    
// Modificações <---------------------------------------------------------------
    
    
    public class MessageManager extends CyclicBehaviour {

        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("no_protocol"));
        MessageTemplate hello_mt = MessageTemplate.and(MessageTemplate.MatchOntology("market_ontology"), MessageTemplate.MatchProtocol("hello_protocol"));
        @Override
        public void action() {
            
            ACLMessage msg = myAgent.receive(mt);
            ACLMessage hello_msg = myAgent.receive(hello_mt);
            
            
            if (msg != null) {
                if (msg.getOntology().equals("market_ontology")) {
                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve(msg);
                }
            } else if(hello_msg != null){
                if (hello_msg.getOntology().equals("market_ontology")) {
                    MarketOntology market_ontology = new MarketOntology();
                    market_ontology.resolve_hello(hello_msg);
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
            if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                if (text == 0) {
                    mo_gui.text_log.append(msg.getContent());
                    text++;
                } else {
//                    JPanel panel = new JPanel(new BorderLayout());
//          //Panel center - icon
//                    JPanel panel_center = new JPanel();
//                    panel_center.setMinimumSize(new Dimension(100, 50));
//                    panel_center.setPreferredSize(new Dimension(200, 150));
                    Object aux = msg.getContent();

                    String[] choices4 = {"OK"};
//                     JOptionPane.showOptionDialog(null,aux, "System: New Bilateral Contract deal Received", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices4,null);
//                    System.out.println(msg.getContent());
                }

            }
        }
        
        private void resolve_hello(ACLMessage msg) {
            
            if(msg.getContent().contains("isProducer")){
                String info = msg.getContent();
                
                StoreProducerInfo(info);
                
            }else if(msg.getContent().contains("isBuyer")){
                String info = msg.getContent();
                
                StoreBuyerInfo(info);
                        
                
            }
            
        }
        
        private void resolveCFP(ACLMessage msg) {
            String content = msg.getContent();
            if (content.contains("propose_opponent")) {
                addBelif(msg.getSender().getLocalName(), msg.getSender().getLocalName() + ";waiting_for_opponent");
            }
        }

        public void resolveInform(ACLMessage msg) {
            String content = msg.getContent();
            if (content.contains(";is_buyer") || content.contains(";is_producer") ||content.contains(";is_seller") || content.contains(";is_coalition") || content.contains(";is_consumer")) {
                String[] content_information = content.split(";");
                String agent_name = content_information[0];
//                String agent_type = content_information[1].split("_")[1];

                if (agent_name.equals(msg.getSender().getLocalName())) {

                    String[] content_split = content.split(";");
                    addBelif(agent_name, content_split[0] + ";" + content_split[1]);
                    addBelif(agent_name, content_split[0] + ";" + content_split[2]);
                    addBelif(agent_name, content_split[0] + ";" + content_split[3]);
                    addBelif(agent_name, content_split[0] + ";" + content_split[4]);
                    addBelif(agent_name, content_split[0] + ";" + content_split[5]);
//                    if (agent_type.equals("buyer")){
//                        if(largeConsumer_names.contains(agent_name)){
//                            agent_type="large_consumer";
//                        }else if(mediumConsumer_names.contains(agent_name)){
//                            agent_type="consumer";}
//                    }
//                    addAgent(new AID(agent_name, AID.ISLOCALNAME), agent_type);

                }
            }else if(content.contains("Offer")){
                
                Store_Offer_Data(content);
                
            }else if(content.contains("Results")){
                if(content.contains("SMPsym")){
                    Store_and_send_SMP_results(content);
                }else if(content.contains("SMPasym")){
                    Store_and_send_SMP_results(content);
                }
                
                
            }
        }
    }
    
    private void Store_and_send_SMP_results(String Results){
        String[] Data; // contains whole message
        String Price; // contains period price
        String[] Info; // contains message part that refers to a single Agent
        String content; // content of message to be sent to each participating agent
        int k = -1; // contains agent index
        int f = -1; // contains buyers information start
        AID rec; // receiver of message
        
        // Set up message with results to be sent to each participating Agent
        ACLMessage Propose_msg = new ACLMessage(ACLMessage.PROPOSE);
        Propose_msg.setOntology("market_ontology");
        Propose_msg.setProtocol("no_protocol");
        Data = Results.split(";");
        
        if(Data[0].contains("SMPsym")){
            content = Data[0] + ";" + Data[1] + ";";
            Price = Data[1];
            
            //Data[2] is "Producers"
            
            for(int i = 3; !Data[i].contains("Buyers"); i++){
                Info = Data[i].split(" ");
                
                // Find Producer index
                for(int l = 0; l < this.Producers_Information.size(); l++){
                    if(this.Producers_Information.get(l).getName().equals(Info[0])){
                        k = l; // k contains Producer Index
                    }
                }
                if(k!=-1){
                    
                    
                    
                    this.Producers_Information.get(k).Initialize_Market_Price_Sym();
                    this.Producers_Information.get(k).Initialize_Traded_power_Sym();
                    
                    for(int j = 1; !Info[j].contains("end"); j++){
                        this.Producers_Information.get(k).setMarket_Price_Sym(Double.parseDouble(Price));
                        this.Producers_Information.get(k).setTraded_power_Sym(Double.parseDouble(Info[j]));
                    }
                    
                    content = Data[0] + ";" + Data[1] + ";" + Data[i];
                    rec = new AID("" + Info[0], AID.ISLOCALNAME);
                    Propose_msg.setContent(content);
                    Propose_msg.addReceiver(rec);
                    send(Propose_msg);
                    Propose_msg.clearAllReceiver();
                }else{
                    System.out.println("Agent " + Info[0] + " was not found on list");
                }
                
                f = i;
            }
            k = -1;
            
            // Data[f+1] = Buyers
            
            for(int i = f+2; i < Data.length; i++){
                Info = Data[i].split(" ");
                // Find Producer index
                for(int l = 0; l < this.Buyers_Information.size(); l++){
                    if(this.Buyers_Information.get(l).getName().equals(Info[0])){
                        k = l; // k contains Producer Index
                    }
                }
                if(k!=-1){
                    
                    this.Buyers_Information.get(k).Initialize_Market_Price_Sym();
                    this.Buyers_Information.get(k).Initialize_Traded_power_Sym();
                    
                    for(int j = 1; !Info[j].contains("end"); j++){
                        this.Buyers_Information.get(k).setMarket_Price_Sym(Double.parseDouble(Price));
                        this.Buyers_Information.get(k).setTraded_power_Sym(Double.parseDouble(Info[j]));
                    }
                    
                    content = Data[0] + ";" + Data[1] + ";" + Data[i];
                    rec = new AID("" + Info[0], AID.ISLOCALNAME);
                    Propose_msg.setContent(content);
                    Propose_msg.addReceiver(rec);
                    send(Propose_msg);
                    Propose_msg.clearAllReceiver();
                }else{
                    System.out.println("Agent " + Info[0] + " was not found on list");
                }
                
            }
            
            
        }else if(Data[0].contains("SMPasym")){
            
            content = Data[0] + ";" + Data[1] + ";";
            Price = Data[1];
            
            //Data[2] is "Producers"
            
            for(int i = 3; !Data[i].contains("Buyers"); i++){
                Info = Data[i].split(" ");
                
                // Find Producer index
                for(int l = 0; l < this.Producers_Information.size(); l++){
                    if(this.Producers_Information.get(l).getName().equals(Info[0])){
                        k = l; // k contains Producer Index
                    }
                }
                if(k!=-1){
                    
                    
                    
                    this.Producers_Information.get(k).Initialize_Market_Price_aSym();
                    this.Producers_Information.get(k).Initialize_Traded_power_aSym();
                    
                    for(int j = 1; !Info[j].contains("end"); j++){
                        this.Producers_Information.get(k).setMarket_Price_aSym(Double.parseDouble(Price));
                        this.Producers_Information.get(k).setTraded_power_aSym(Double.parseDouble(Info[j]));
                    }
                    
                    content = Data[0] + ";" + Data[1] + ";" + Data[i];
                    rec = new AID("" + Info[0], AID.ISLOCALNAME);
                    Propose_msg.setContent(content);
                    Propose_msg.addReceiver(rec);
                    send(Propose_msg);
                    Propose_msg.clearAllReceiver();
                }else{
                    System.out.println("Agent " + Info[0] + " was not found on list");
                }
                
                f = i;
            }
            k = -1;
            
            // Data[f+1] = Buyers
            
            for(int i = f+2; i < Data.length; i++){
                Info = Data[i].split(" ");
                // Find Producer index
                for(int l = 0; l < this.Buyers_Information.size(); l++){
                    if(this.Buyers_Information.get(l).getName().equals(Info[0])){
                        k = l; // k contains Producer Index
                    }
                }
                if(k!=-1){
                    
                    this.Buyers_Information.get(k).Initialize_Market_Price_aSym();
                    this.Buyers_Information.get(k).Initialize_Traded_power_aSym();
                    
                    for(int j = 1; !Info[j].contains("end"); j++){
                        this.Buyers_Information.get(k).setMarket_Price_aSym(Double.parseDouble(Price));
                        this.Buyers_Information.get(k).setTraded_power_aSym(Double.parseDouble(Info[j]));
                    }
                    
                    content = Data[0] + ";" + Data[1] + ";" + Data[i];
                    rec = new AID("" + Info[0], AID.ISLOCALNAME);
                    Propose_msg.setContent(content);
                    Propose_msg.addReceiver(rec);
                    send(Propose_msg);
                    Propose_msg.clearAllReceiver();
                }else{
                    System.out.println("Agent " + Info[0] + " was not found on list");
                }
                
            }
            
        }
        
    }
    
    private void Store_Offer_Data(String content){
        String[] data;
        int k = -1;
        int j = 0;
        data = content.split(" ");
        
        if(data[2].contains("Producer")){
            for(int i = 0; i < this.Producers_Information.size(); i++){
                if(this.Producers_Information.get(i).getName().equals(data[1])){
                    k=i;
                    break;
                }
            }
            if(k!=-1){
                for(int i = 4; !data[i].contains("Power"); i++){
                        this.Producers_Information.get(k).setPrice(Float.parseFloat(data[i]));
                        j=i;
                }
                
                for(int i = j+2; !data[i].contains("end"); i++){
                        this.Producers_Information.get(k).setPower(Float.parseFloat(data[i]));
                }
                
                this.mo_gui.show_offer_window(k, true);
                
            }else{
                System.out.println("Error: Producer name on message and producer name on list don't match!!");
            }
            
        }else if(data[2].contains("Buyer")){
            for(int i = 0; i < this.Buyers_Information.size(); i++){
                if(this.Buyers_Information.get(i).getName().equals(data[1])){
                    k=i;
                    break;
                }
            }
            if(k!=-1){
                for(int i = 4; !data[i].contains("Power"); i++){
                        this.Buyers_Information.get(k).setPrice(Float.parseFloat(data[i]));
                        j=i;
                }
                
                for(int i = j+2; !data[i].contains("end"); i++){
                        this.Buyers_Information.get(k).setPower(Float.parseFloat(data[i]));
                }
                
                this.mo_gui.show_offer_window(k, false);
                
            }else{
                System.out.println("Error: Buyer name on message and buyer name on list don't match!!");
            }
        }
        
    }

    private void StoreProducerInfo(String info){
        
        // Info string has the following format
        // "Name";"Address";"PhoneNumber";"Email";"Objective";tech;"ThermalTechnolgies";"WindTechnologies";"HydroTechnologies"
        // The fields between "" refer to information about the current Agent
        // If the agent doesn't have a particular technology, the respective field will be empty
        // Differente avaliable technologies of a ceratain type will be separated by "_"
        
        
        ProducerData newProducer = new ProducerData();
        String Name;
        String Address;
        String Phone_Number;
        String EMail;
        String Objective;

        // data[0] to data[5] will be basic producer agent info
        String[] data;
        data = info.split(";");
        
        Name = data[0];
        Address = data[2];
        Phone_Number = data[3];
        EMail = data[4];
        Objective = data[5];
        
        newProducer.setName(Name);
        newProducer.setAddress(Address);
        newProducer.setPhone_number(Phone_Number);
        newProducer.setEmail(EMail);
        newProducer.setObjective(Objective);
        newProducer.setParticipating(false);
        newProducer.setStrategy("None");
        
        // data[6] will be "tech"
        
        // data[7] is for thermal technologies
        String[] TechData;
        TechData = data[7].split("_");
        int i = 0;
        if(!TechData[0].equals(" ")){
            while(i<TechData.length){
                newProducer.addDataThermal(TechData[i], Double.parseDouble(TechData[i+1]), 
                        Double.parseDouble(TechData[i+2]), TechData[i+3], Double.parseDouble(TechData[i+4]));
                i=i+5;
            }
        }
        // data[8] is for wind technologies
        TechData = data[8].split("_");
        i = 0;
        if(!TechData[0].equals(" ")){
            while(i<TechData.length){
                newProducer.addDataWind(TechData[i], Double.parseDouble(TechData[i+1]), 
                        Double.parseDouble(TechData[i+2]), Double.parseDouble(TechData[i+3]));
                i=i+4;
            }
        }

        // data[9] is for Hydro technologies
        TechData = data[9].split("_");
        i = 0;
        if(!TechData[0].equals(" ")){
            while(i<TechData.length){
                newProducer.addDataHydro(TechData[i], Double.parseDouble(TechData[i+1]), 
                        Double.parseDouble(TechData[i+2]));
                i=i+3;
            }
        }

        NewAgent_Info AgentInfo = new NewAgent_Info(true, newProducer, null, this);
        AgentInfo.setVisible(true);
        
                
    }
    
    private void StoreBuyerInfo(String info){
        
        BuyerData newBuyer = new BuyerData();
        String Name;
        String Address;
        String Phone_Number;
        String EMail;
        String Objective;
        
         // data[0] to data[5] will be basic producer agent info
        String[] data;
        data = info.split(";");
        
        Name = data[0];
        Address = data[2];
        Phone_Number = data[3];
        EMail = data[4];
        Objective = data[5];
        
        newBuyer.setName(Name);
        newBuyer.setAddress(Address);
        newBuyer.setPhone_number(Phone_Number);
        newBuyer.setEmail(EMail);
        newBuyer.setObjective(Objective);
        newBuyer.setParticipating(false);
        
        NewAgent_Info AgentInfo = new NewAgent_Info(false, null, newBuyer, this);
        AgentInfo.setVisible(true);
    }
    
    
    public void sendMarketSelection(boolean isDayahead, boolean isSMP, boolean isOTC){
        AID rec;
        
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology("market_ontology");
        msg.setProtocol("no_protocol");
        
        String content;
        
        content = "Market ";
        
        if(isDayahead){
            content = content + "Dayahead ";
            if(isSMP){
                content = content + "SMP";
            }else{
                content = content + "LMP";
            }
        }
        if(isOTC){
            content = content + "OTC";
        }
        
        msg.setContent(content);
        
        for(int i = 0; i < Buyers_Information.size(); i++){
            rec = new AID(Buyers_Information.get(i).getName().replace(" ", "_"), AID.ISLOCALNAME);
            msg.addReceiver(rec);  
        }
        
        
        send(msg);
        msg.clearAllReceiver();
        
        for(int i = 0; i < Producers_Information.size(); i++){
            rec = new AID(Producers_Information.get(i).getName().replace(" ", "_"), AID.ISLOCALNAME);
            msg.addReceiver(rec);
        }
        send(msg);
    }
    
    public void inform_participants(boolean isProducer, String AgentName, String Strategy){
        
        AID rec;
        String content;
        
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setOntology("market_ontology");
        msg.setProtocol("no_protocol");
        
        content = "Participating " + Strategy;
        
        msg.setContent(content);
        
        rec = new AID(AgentName.replace(" ", "_"), AID.ISLOCALNAME);
        
        msg.addReceiver(rec);
        
        send(msg);
        
    }
    
    
    
    
    
    
    
    
    
    
    public void addNegotiationPairAndInformThem(AID[] new_pair) {

        this.new_pair = new_pair;
        this.negotiation_pairs.add(new_pair);
        ACLMessage msg_seller = new ACLMessage(ACLMessage.PROPOSE);
        msg_seller.setOntology("market_ontology");
        msg_seller.setProtocol("hello_protocol");
        msg_seller.setContent(new_pair[1].getLocalName() + ";opponent_proposal");
        msg_seller.addReceiver(new_pair[0]);

        ACLMessage msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
        msg_buyer.setOntology("market_ontology");
        msg_buyer.setProtocol("hello_protocol");
        msg_buyer.setContent(new_pair[0].getLocalName() + ";opponent_proposal");
        msg_buyer.addReceiver(new_pair[1]);

        send(msg_seller);
        send(msg_buyer);

        msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
        msg_buyer.setOntology("market_ontology");
        msg_buyer.setProtocol("hello_protocol");
        msg_buyer.setContent(new_pair[0].getLocalName() + ";opponent_proposal");
        msg_buyer.addReceiver(MarketOperator);

        send(msg_buyer);

        msg_seller = new ACLMessage(ACLMessage.PROPOSE);
        msg_seller.setOntology("contract_ontology");
        msg_seller.setProtocol("hello_protocol");
        msg_seller.setContent(contract);
        msg_seller.addReceiver(new_pair[0]);

        msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
        msg_buyer.setOntology("contract_ontology");
        msg_buyer.setProtocol("hello_protocol");
        msg_buyer.setContent(contract);
        msg_buyer.addReceiver(new_pair[1]);

        send(msg_seller);
        send(msg_buyer);

        msg_seller = new ACLMessage(ACLMessage.PROPOSE);
        msg_seller.setOntology("day_ontology");
        msg_seller.setProtocol("hello_protocol");
        msg_seller.setContent(String.valueOf(contractduration));
        msg_seller.addReceiver(new_pair[0]);

        msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
        msg_buyer.setOntology("day_ontology");
        msg_buyer.setProtocol("hello_protocol");
        msg_buyer.setContent(String.valueOf(contractduration));
        msg_buyer.addReceiver(new_pair[1]);

        send(msg_seller);
        send(msg_buyer);

        msg_seller = new ACLMessage(ACLMessage.PROPOSE);
        msg_seller.setOntology("inf_ontology");
        msg_seller.setProtocol("hello_protocol");
        msg_seller.setContent(String.valueOf(N_PERIODS));
        msg_seller.addReceiver(new_pair[0]);

        msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
        msg_buyer.setOntology("inf_ontology");
        msg_buyer.setProtocol("hello_protocol");
        msg_buyer.setContent(String.valueOf(N_PERIODS));
        msg_buyer.addReceiver(new_pair[1]);

        send(msg_seller);
        send(msg_buyer);

        if (N_PERIODS != 24) {
            String[] hoursaux = new String[1];
            hoursaux[0] = "";
            for (int i = 0; i < HOURS.size(); i++) {
                hoursaux[0] = hoursaux[0] + HOURS.get(i) + ",";
            }

            msg_seller = new ACLMessage(ACLMessage.PROPOSE);
            msg_seller.setOntology("hour_ontology");
            msg_seller.setProtocol("hello_protocol");
            msg_seller.setContent(hoursaux[0]);
            msg_seller.addReceiver(new_pair[0]);

            msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
            msg_buyer.setOntology("hour_ontology");
            msg_buyer.setProtocol("hello_protocol");
            msg_buyer.setContent(hoursaux[0]);
            msg_buyer.addReceiver(new_pair[1]);

            send(msg_seller);
            send(msg_buyer);

        }

        msg_seller = new ACLMessage(ACLMessage.PROPOSE);
        msg_seller.setOntology("volume_ontology");
        msg_seller.setProtocol("hello_protocol");
        msg_seller.setContent(String.valueOf(demandresponse));
        msg_seller.addReceiver(new_pair[0]);

        msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
        msg_buyer.setOntology("volume_ontology");
        msg_buyer.setProtocol("hello_protocol");
        msg_buyer.setContent(String.valueOf(demandresponse));
        msg_buyer.addReceiver(new_pair[1]);

        send(msg_seller);
        send(msg_buyer);

        msg_seller = new ACLMessage(ACLMessage.PROPOSE);
        msg_seller.setOntology("risk_ontology");
        msg_seller.setProtocol("hello_protocol");
        msg_seller.setContent("" + seller_risk);
        msg_seller.addReceiver(new_pair[0]);

        msg_buyer = new ACLMessage(ACLMessage.PROPOSE);
        msg_buyer.setOntology("risk_ontology");
        msg_buyer.setProtocol("hello_protocol");
        msg_buyer.setContent("" + buyer_risk);
        msg_buyer.addReceiver(new_pair[1]);

        send(msg_seller);
        send(msg_buyer);
    }

    public PersonalAssistant Init() {

        N_PERIODS = 24;
        return this;
    }

//    @SuppressWarnings("unchecked")
//    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
//    public void initComponents() {
//        
//
//        jLabel1 = new javax.swing.JLabel();
//        jLabel2 = new javax.swing.JLabel();
//        jLabel3 = new javax.swing.JLabel();
//        jComboBox1 = new javax.swing.JComboBox();
//        jComboBox2 = new javax.swing.JComboBox();
//        jLabel4 = new javax.swing.JLabel();
//        jSlider1 = new javax.swing.JSlider();
//        jLabel5 = new javax.swing.JLabel();
//        jTextField1 = new javax.swing.JTextField();
//        jTextField2 = new javax.swing.JTextField();
//        jSeparator1 = new javax.swing.JSeparator();
//        jLabel6 = new javax.swing.JLabel();
//        jLabel7 = new javax.swing.JLabel();
//        jCheckBox1 = new javax.swing.JCheckBox();
//        jCheckBox2 = new javax.swing.JCheckBox();
//        jSeparator2 = new javax.swing.JSeparator();
//        jLabel8 = new javax.swing.JLabel();
//        jLabel9 = new javax.swing.JLabel();
//        jCheckBox3 = new javax.swing.JCheckBox();
//        jCheckBox4 = new javax.swing.JCheckBox();
//        jSeparator3 = new javax.swing.JSeparator();
////        jLabel10 = new javax.swing.JLabel();
////        jLabel11 = new javax.swing.JLabel();
//        jLabel12 = new javax.swing.JLabel();
////        jCheckBox5 = new javax.swing.JCheckBox();
////        jCheckBox6 = new javax.swing.JCheckBox();
////        jCheckBox7 = new javax.swing.JCheckBox();
////        jLabel13 = new javax.swing.JLabel();
////        jCheckBox8 = new javax.swing.JCheckBox();
////        jCheckBox9 = new javax.swing.JCheckBox();
////        jCheckBox10 = new javax.swing.JCheckBox();
//        
//        /////////////////ALTERAÇÔES//////////////////////////
//        /////////////////////////////////////////////////////
//        jCheckBox1.setSelected(false);
//        jCheckBox2.setSelected(true);
//        jCheckBox3.setSelected(false);
//        jCheckBox4.setSelected(true);
////        jCheckBox5.setSelected(false);
////        jCheckBox6.setSelected(false);
////        jCheckBox7.setSelected(true);
////        jCheckBox8.setSelected(false);
////        jCheckBox9.setSelected(false);
////        jCheckBox10.setSelected(true);
//        ////////////////////////////////////////////////////
//        ///////////////////////////////////////////////////
//        
//jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
//        jLabel1.setText("Contract");
//
//        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel3.setText("Type:");
//
//        jComboBox2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Forward Contract", "Contract For Difference", "Option Contract" }));
//
//        jSlider1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
//        jSlider1.setMajorTickSpacing(1);
//        jSlider1.setMaximum(24);
//        jSlider1.setMinimum(1);
//        jSlider1.setMinorTickSpacing(1);
//        jSlider1.setPaintLabels(true);
//        jSlider1.setPaintTicks(true);
//        jSlider1.setToolTipText("");
//
//        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel5.setText("Hour");
//
//        jTextField2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jTextField2.setText("365");
//
//        jLabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
//        jLabel6.setText("Demand Response Management");
//
//        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel7.setText("Managing Energy Volumes During Negotiation:");
//
//        jCheckBox1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jCheckBox1.setText("yes");
//        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
//            public void itemStateChanged(java.awt.event.ItemEvent evt) {
//                jCheckBox1ItemStateChanged(evt);
//            }
//        });
//
//        jCheckBox2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jCheckBox2.setText("no");
//        jCheckBox2.addItemListener(new java.awt.event.ItemListener() {
//            public void itemStateChanged(java.awt.event.ItemEvent evt) {
//                jCheckBox2ItemStateChanged(evt);
//            }
//        });
//
//        jSeparator2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//        jSeparator2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
//
//        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
//        jLabel8.setText("Price Risk");
//
//        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel9.setText("Managing Price Risk During Negotiation:");
//
//        jCheckBox3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jCheckBox3.setText("yes");
//        jCheckBox3.addItemListener(new java.awt.event.ItemListener() {
//            public void itemStateChanged(java.awt.event.ItemEvent evt) {
//                jCheckBox3ItemStateChanged(evt);
//            }
//        });
//
//        jCheckBox4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jCheckBox4.setText("no");
//        jCheckBox4.addItemListener(new java.awt.event.ItemListener() {
//            public void itemStateChanged(java.awt.event.ItemEvent evt) {
//                jCheckBox4ItemStateChanged(evt);
//            }
//        });
//
//        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel12.setText("Duration (days):");
//
//        jTextField1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
////        jTextField1.setText("1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19;20;21;22;23;24");
//        jTextField1.setText("1-4;5-8;9-12;13-16;17-20;21-24");
//
//
//        jSeparator3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//        jSeparator3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
//
//        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
//        jLabel2.setText("Day Periods");
//
//        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
//        this.setLayout(layout);
//        layout.setHorizontalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addComponent(jSeparator1)
//            .addComponent(jSeparator2)
//            .addGroup(layout.createSequentialGroup()
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(0, 0, Short.MAX_VALUE)
//                        .addComponent(jLabel9))
//                    .addComponent(jLabel8))
//                .addGap(8, 8, 8)
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addComponent(jCheckBox4)
//                    .addComponent(jCheckBox3))
//                .addGap(100, 100, 100))
//            .addGroup(layout.createSequentialGroup()
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addComponent(jLabel1)
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(79, 79, 79)
//                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                            .addGroup(layout.createSequentialGroup()
//                                .addComponent(jLabel12)
//                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
//                            .addGroup(layout.createSequentialGroup()
//                                .addComponent(jLabel3)
//                                .addGap(29, 29, 29)
//                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
//                    .addComponent(jLabel6)
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(85, 85, 85)
//                        .addComponent(jLabel7)
//                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                            .addComponent(jCheckBox1)
//                            .addComponent(jCheckBox2))))
//                .addContainerGap())
//            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
//                .addGap(0, 0, Short.MAX_VALUE)
//                .addComponent(jLabel5)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addContainerGap())
//            .addComponent(jSeparator3)
//            .addGroup(layout.createSequentialGroup()
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
//                    .addComponent(jLabel2))
//                .addGap(0, 0, Short.MAX_VALUE))
//        );
//        layout.setVerticalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGroup(layout.createSequentialGroup()
//                .addContainerGap()
//                .addComponent(jLabel1)
//                .addGap(18, 18, 18)
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                    .addComponent(jLabel3)
//                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
//                .addGap(10, 10, 10)
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                    .addComponent(jLabel12)
//                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
//                .addGap(18, 18, 18)
//                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addGap(2, 2, 2)
//                .addComponent(jLabel2)
//                .addGap(18, 18, 18)
//                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
//                    .addComponent(jLabel5))
//                .addGap(18, 18, 18)
//                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(4, 4, 4)
//                        .addComponent(jLabel8)
//                        .addGap(18, 18, 18)
//                        .addComponent(jLabel9)
//                        .addGap(27, 27, 27))
//                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
//                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(jCheckBox3)
//                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(jCheckBox4)
//                        .addGap(11, 11, 11)))
//                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                    .addGroup(layout.createSequentialGroup()
//                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(jLabel6)
//                        .addGap(18, 18, 18)
//                        .addComponent(jLabel7))
//                    .addGroup(layout.createSequentialGroup()
//                        .addGap(26, 26, 26)
//                        .addComponent(jCheckBox1)
//                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(jCheckBox2)))
//                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//        );
//    }// </editor-fold>
//
//       ////////////////////////////////////////////////////////////////////////////////////////                               
//        /////////////////////////////////////////////////////////////////////////////////////
//                                     
//
//                                        
//
//    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
//
//        if((evt.getStateChange() != ItemEvent.DESELECTED)){
//            if(jCheckBox2.isSelected()){
//            jCheckBox2.setSelected(false);
//            demandresponse=1;
//        }
//        }else{
//        if((evt.getStateChange() == ItemEvent.DESELECTED)){
//            if(jCheckBox2.isSelected()){
//                
//            }else{
//                jCheckBox2.setSelected(true);
//                demandresponse=0;
//            }
//        }
//        }
//    }                                           
//
//    private void jCheckBox2ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
//      if((evt.getStateChange() != ItemEvent.DESELECTED)){
//              if(jCheckBox1.isSelected()){
//            jCheckBox1.setSelected(false);
//            demandresponse=0;
//        }
//      }else{
//        if((evt.getStateChange() == ItemEvent.DESELECTED)){
//            if(jCheckBox1.isSelected()){
//                
//            }else{
//                jCheckBox1.setSelected(true);
//                demandresponse=1;
//            }
//        }        // TODO add your handling code here:
//    } 
//    }
//        private void jCheckBox3ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
//
//        if((evt.getStateChange() != ItemEvent.DESELECTED)){
//            if(jCheckBox4.isSelected()){
//            jCheckBox4.setSelected(false);
//            buyer_risk=1;
//            seller_risk=1;
//        }
//        }else{
//        if((evt.getStateChange() == ItemEvent.DESELECTED)){
//            if(jCheckBox4.isSelected()){
//                
//            }else{
//                jCheckBox4.setSelected(true);
//                buyer_risk=0;
//                seller_risk=0;
//            }
//        }
//        }
//    }                                           
//
//    private void jCheckBox4ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
//      if((evt.getStateChange() != ItemEvent.DESELECTED)){
//              if(jCheckBox3.isSelected()){
//            jCheckBox3.setSelected(false);
//            buyer_risk=0;
//            seller_risk=0;
//        }
//      }else{
//        if((evt.getStateChange() == ItemEvent.DESELECTED)){
//            if(jCheckBox3.isSelected()){
//                
//            }else{
//                jCheckBox3.setSelected(true);
//                buyer_risk=1;
//                seller_risk=1;
//            }
//        }        // TODO add your handling code here:
//    } 
//    }
////        private void jCheckBox567ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
////      
////            Object source = evt.getItemSelectable();
////            
////            if(source==jCheckBox5 &&(evt.getStateChange() != ItemEvent.DESELECTED)){
////              if(jCheckBox6.isSelected()){
////            jCheckBox6.setSelected(false);
////            ESseller=0;
////              }else{
////                  if(jCheckBox7.isSelected()){
////            jCheckBox7.setSelected(false);
////            ESseller=0;
////              }
////              }
////            }else{
////                if(source==jCheckBox6 &&(evt.getStateChange() != ItemEvent.DESELECTED)){
////              if(jCheckBox5.isSelected()){
////            jCheckBox5.setSelected(false);
////            ESseller=1;
////              }else{
////                  if(jCheckBox7.isSelected()){
////            jCheckBox7.setSelected(false);
////            ESseller=1;
////              }
////              }
////            }else{
////                 if(source==jCheckBox7 &&(evt.getStateChange() != ItemEvent.DESELECTED)){
////              if(jCheckBox6.isSelected()){
////            jCheckBox6.setSelected(false);
////            ESseller=2;
////              }else{
////                  if(jCheckBox5.isSelected()){
////            jCheckBox5.setSelected(false);
////            ESseller=2;
////              }
////              }   
////                
////                
////            
////      }else{
////        if(source==jCheckBox5&&(evt.getStateChange() == ItemEvent.DESELECTED)){
////            if(jCheckBox6.isSelected()||jCheckBox7.isSelected()){
////                
////            }else{
////                jCheckBox5.setSelected(true);
////            }
//////        }        // TODO add your handling code here:
//////    } 
////    
////    }else{
////        if(source==jCheckBox6&&(evt.getStateChange() == ItemEvent.DESELECTED)){
////            if(jCheckBox5.isSelected()||jCheckBox7.isSelected()){
////                
////            }else{
////                jCheckBox6.setSelected(true);
////            }
//////        }        // TODO add your handling code here:
//////    } 
////    
////            }else{
////        if(source==jCheckBox7&&(evt.getStateChange() == ItemEvent.DESELECTED)){
////            if(jCheckBox6.isSelected()||jCheckBox5.isSelected()){
////                
////            }else{
////                jCheckBox7.setSelected(true);
////            }
//////        }        // TODO add your handling code here:
//////    } 
////        }
////        }
////        }
////                 }
////                }
////            }
////    }
////                private void jCheckBox8910ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
////      
////            Object source = evt.getItemSelectable();
////            
////            if(source==jCheckBox8 &&(evt.getStateChange() != ItemEvent.DESELECTED)){
////              if(jCheckBox9.isSelected()){
////            jCheckBox9.setSelected(false);
////            ESbuyer=0;
////              }else{
////                  if(jCheckBox10.isSelected()){
////            jCheckBox10.setSelected(false);
////            ESbuyer=0;
////              }
////              }
////            }else{
////                if(source==jCheckBox9 &&(evt.getStateChange() != ItemEvent.DESELECTED)){
////              if(jCheckBox8.isSelected()){
////            jCheckBox8.setSelected(false);
////            ESbuyer=1;
////              }else{
////                  if(jCheckBox10.isSelected()){
////            jCheckBox10.setSelected(false);
////            ESbuyer=1;
////              }
////              }
////            }else{
////                 if(source==jCheckBox10 &&(evt.getStateChange() != ItemEvent.DESELECTED)){
////              if(jCheckBox9.isSelected()){
////            jCheckBox9.setSelected(false);
////            ESbuyer=2;
////              }else{
////                  if(jCheckBox8.isSelected()){
////            jCheckBox8.setSelected(false);
////            ESbuyer=2;
////              }
////              }   
////                
////                
////            
////      }else{
////        if(source==jCheckBox8&&(evt.getStateChange() == ItemEvent.DESELECTED)){
////            if(jCheckBox9.isSelected()||jCheckBox10.isSelected()){
////                
////            }else{
////                jCheckBox8.setSelected(true);
////            }
//////        }        // TODO add your handling code here:
//////    } 
////    
////    }else{
////        if(source==jCheckBox9&&(evt.getStateChange() == ItemEvent.DESELECTED)){
////            if(jCheckBox8.isSelected()||jCheckBox10.isSelected()){
////                
////            }else{
////                jCheckBox9.setSelected(true);
////            }
//////        }        // TODO add your handling code here:
//////    } 
////    
////            }else{
////        if(source==jCheckBox10&&(evt.getStateChange() == ItemEvent.DESELECTED)){
////            if(jCheckBox9.isSelected()||jCheckBox8.isSelected()){
////                
////            }else{
////                jCheckBox10.setSelected(true);
////            }
//////        }        // TODO add your handling code here:
//////    } 
////        }
////        }
////        }
////                 }
////                }
////            }
////    }
//    // Variables declaration - do not modify                     
//
//    private javax.swing.JLabel jLabel1;
////    private javax.swing.JLabel jLabel10;
////    private javax.swing.JLabel jLabel11;
//    private javax.swing.JLabel jLabel12;
////    private javax.swing.JLabel jLabel13;
//    private javax.swing.JLabel jLabel2;
//    private javax.swing.JLabel jLabel3;
//    private javax.swing.JLabel jLabel4;
//    private javax.swing.JLabel jLabel5;
//    private javax.swing.JLabel jLabel6;
//    private javax.swing.JLabel jLabel7;
//    private javax.swing.JLabel jLabel8;
//    private javax.swing.JLabel jLabel9;
//    private javax.swing.JSeparator jSeparator1;
//    private javax.swing.JSeparator jSeparator2;
//    private javax.swing.JSeparator jSeparator3;
//
//    
//    // End of variables declaration                   
//}
    public void demandmanagement(PersonalAssistantGUI parent) {

        String[] choices = {"Save", "Cancel"};

        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
//        Listener2 listener2 = new Listener2();
        JPanel panel_north = new JPanel();
        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(350, 60));
        panel_north.setPreferredSize(new Dimension(350, 60));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(295, 6));
        panel_text_background.setPreferredSize(new Dimension(295, 60));
        label_text.setMinimumSize(new Dimension(290, 50));
        label_text.setPreferredSize(new Dimension(290, 50));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Customer's Demand Response Program</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_risk));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new DR(this);

        panel_center.setMinimumSize(new Dimension(350, 100));
        panel_center.setPreferredSize(new Dimension(350, 250));
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        int result = JOptionPane.showOptionDialog(parent, panel, "DR Management", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        if (result == 1) {
            demandresponse = 0;

        }

    }

    public void tools(PersonalAssistantGUI parent) {

        String[] choices = {"Save", "Cancel"};
        JPanel panel1 = new JPanel();
//            panel1=new Tools(this);
//           Tools mainFrame	= new Tools(this);
//		mainFrame.setVisible( true );

        int result = JOptionPane.showOptionDialog(parent, new Tools(this), "Preferences", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

//        if(result==1){
//            demandresponse=0;
//            
//        }
    }

    public void newAgentForm(int _agentType) {
        NewAgent_Name newForm = new NewAgent_Name(this, _agentType);
        newForm.setVisible(true);
    }
    
//    public void genco_menu(PersonalAssistantGUI parent) {
//        
//         JPanel panel = new JPanel(new BorderLayout());
//        
//         genco = new EnterGENCO();
//         genco.setVisible(true);
////         agentInfo = "producing.Producer";
//        String[] choices1 = {"Cancel", "Next"};
//    }

    public void producer_menu(PersonalAssistantGUI parent) {

        String[] GENCOs = {"GenCo1","GenCo2","GenCo3","GenCo4","GenCo5","GenCo6","GenCo7","GenCo8", "GenCo9", "GenCo10"};

        JPanel panel = new JPanel(new BorderLayout());
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setPreferredSize(new Dimension(270, 100));
        panel_center.setMinimumSize(new Dimension(270, 100));

        JLabel l = new JLabel("GenCos: ");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 40, 40);
        panel_center.add(l, gridBagConstraints);

        JComboBox menu2 = new JComboBox();
        String[] auxName;
        
        for (String gen1 : GENCOs) {
            if (checkseller(gen1) == 0) {
                menu2.addItem(gen1);
            }
        }
        auxName = new String[menu2.getItemCount()];
        for (int i = 0; i < auxName.length; i++) {
            auxName[i] = menu2.getItemAt(i).toString();
        }
        if (auxName.length > 0) {
            LoadAgent_Name openWindow = new LoadAgent_Name(this, 4, auxName);
            openWindow.setVisible(true);
        }
        
        menu2.setMinimumSize(new Dimension(190, 25));
        menu2.setPreferredSize(new Dimension(190, 25));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 25, 50);

        panel_center.add(menu2, gridBagConstraints);

        panel.add(panel_center, BorderLayout.CENTER);

        String[] choices1 = {"Cancel", "Next"};
        /*int result = JOptionPane.showOptionDialog(parent, panel, "Generators (GenCos)", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices1, choices1[1]);

        if (result == 0) {
            return;
        } else if (result == 1) {
            createAgent((String) menu2.getSelectedItem(), "selling.Seller");
        }*/
    }

    public int checkseller(String Seller) {
        int j = 0;
        for (int i = 0; i < seller_names.size(); i++) {
            if (Seller.trim().replace(" ", "_").equals(seller_names.get(i).getLocalName().trim().replace(" ", "_"))) {
                i = seller_names.size();
                j = 1;
            }
        }
        return j;
    }

                   public void coallition_menu(PersonalAssistantGUI parent){
        
        JMenuItem   Cm1             =   new JMenuItem("Coalition_One");
        JMenuItem   Cm2             =   new JMenuItem("Coalition_Two");
        JMenuItem   Cm0             =   new JMenuItem("Coalition");
        JMenu       Coalition       =   new JMenu("Coalition");
        
        JMenu      menu             =   ComboMenuBar.createMenu("Coalition");
        
        JPanel panel        =   new JPanel(new BorderLayout());          
        JPanel panel_center =   new JPanel();
        
        panel_center.setLayout(new GridBagLayout());
        panel_center.setPreferredSize(new Dimension(100, 100));
        panel_center.setMinimumSize(new Dimension(100, 100));
                         
        JLabel l            =   new JLabel("Coalition :");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints   =   new GridBagConstraints();
        gridBagConstraints.gridx    =   0;
        gridBagConstraints.gridy    =   1;
        gridBagConstraints.anchor   =   java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets   =   new java.awt.Insets(0, 40, 40, 30);
        panel_center.add(l, gridBagConstraints);
    
        menu.add(Cm0);
        menu.add(Cm1);
        menu.add(Cm2);

        ComboMenuBar comboMenu      =   new ComboMenuBar(menu);

        comboMenu.setMinimumSize(new Dimension(180, 25));
        comboMenu.setPreferredSize(new Dimension(180, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx    =   1;
        gridBagConstraints.gridy    =   1;
        gridBagConstraints.anchor   =   GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets   =   new Insets(-3, -30, 25, 50);
        panel_center.add(comboMenu, gridBagConstraints);
        
        
        panel.add(panel_center, BorderLayout.CENTER);
        
//        String toString = menu.getSelectedObjects().toString();
        
        String[] choices1           =   {"Cancel","OK"};
        int result                  =   JOptionPane.showOptionDialog(parent, panel, "Coalition: ", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices1, null);
        
        if (result == 0) {
            return;
        }else if (result == 1) {
                //createAgent(toString,"Coalition.CoalitionFront");
                createAgent("Coalition","Coalition.CoalitionFront");
        }
    
    }
    
                   
    public void consumer_menu(PersonalAssistantGUI parent) {

        /* JMenuItem DO = new JMenuItem("David_Owen");
         JMenuItem SCO = new JMenuItem("SCO_Corporation");
         JMenuItem EC = new JMenuItem("Electro_Center");
         */
        String[] LSEs = {"David_Owen", "David_Aggregation"};
        /*String DO = "David_Owen";
         String SCO = "SCO_Corporation";
         String EC = "Electro_Center";
         String TE = "Electrical_Supplier";*/

//        JMenuItem David_Owen = new JMenuItem("D");
//        JMenu Buyer = new JMenu("Buyer");
        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setPreferredSize(new Dimension(270, 100));
        panel_center.setMinimumSize(new Dimension(270, 100));
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//                  JPanel panel_north = new JPanel(new GridLayout(1, 2));
        JLabel l = new JLabel("ConsumerCos: ");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 40, 30);
        panel_center.add(l, gridBagConstraints);

//            JMenu[] menus = new JMenu[1];
//   
//    menus[0] = Seller;
//   
//
//    menus[0].add(RES);
        //JMenu menu = ComboMenuBar.createMenu("Supplier");
        JComboBox menu2 = new JComboBox();
        String[] auxName;

        for (String lse1 : LSEs) {
            if (checkbuyer(lse1) == 0) {
                menu2.addItem(lse1);
            }
        }
        auxName = new String[menu2.getItemCount()];
        for (int i = 0; i < auxName.length; i++) {
            auxName[i] = menu2.getItemAt(i).toString();
        }
        if (auxName.length > 0) {
            LoadAgent_Name openWindow = new LoadAgent_Name(this, 3, auxName);
            openWindow.setVisible(true);
        }
        /*
         if(checkbuyer(DO)==0) {
         menu2.addItem(DO);
         }
         if(checkbuyer(EC)==0) {
         menu2.addItem(EC);
         }
         if(checkbuyer(SCO)==0) {
         menu2.addItem(SCO);
         }
         if(checkbuyer(TE)==0) {
         menu2.addItem(TE);
         }
         */
        /*int j = checkbuyer(DO.getText());

         if (j == 0) {
         menu.add(DO);
         }
         j = checkbuyer(EC.getText());;
         if (j == 0) {
         menu.add(EC);
         }
         j = checkbuyer(SCO.getText());
         if (j == 0) {
         menu.add(SCO);
         }*/
////    menu.addSeparator();
//    menu.add(menus[2]);
//    menu.add(menus[3]);
        /*ComboMenuBar comboMenu = new ComboMenuBar(menu);

         comboMenu.setMinimumSize(new Dimension(180, 25));
         comboMenu.setPreferredSize(new Dimension(180, 25));*/
        menu2.setMinimumSize(new Dimension(180, 25));
        menu2.setPreferredSize(new Dimension(180, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 25, 50);
        //panel_center.add(comboMenu, gridBagConstraints);
        panel_center.add(menu2, gridBagConstraints);
        panel.add(panel_center, BorderLayout.CENTER);

        String[] choices1 = {"Cancel", "Next"};
        /*int result = JOptionPane.showOptionDialog(parent, panel, "Retailers (RetailCos) ", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices1, choices1[1]);

         if (result == 0 || result == -1) {

         return;
         } else if (result == 1) {

         //createAgent((String) comboMenu.getSelectedItem(), "buying.Buyer");
         createAgent((String) menu2.getSelectedItem(), "buying.Buyer");
         //        createAgent("David_Owen","buying.Buyer");
         }*/
    }
                   
    public void buyer_menu(PersonalAssistantGUI parent) {

        /* JMenuItem DO = new JMenuItem("David_Owen");
         JMenuItem SCO = new JMenuItem("SCO_Corporation");
         JMenuItem EC = new JMenuItem("Electro_Center");
         */
        String[] LSEs = {"RetailCO1", "RetailCO2", "RetailCO3", "RetailCO4"};
        /*String DO = "David_Owen";
         String SCO = "SCO_Corporation";
         String EC = "Electro_Center";
         String TE = "Electrical_Supplier";*/

//        JMenuItem David_Owen = new JMenuItem("D");
//        JMenu Buyer = new JMenu("Buyer");
        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setPreferredSize(new Dimension(270, 100));
        panel_center.setMinimumSize(new Dimension(270, 100));
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//                  JPanel panel_north = new JPanel(new GridLayout(1, 2));
        JLabel l = new JLabel("RetailCos: ");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 40, 30);
        panel_center.add(l, gridBagConstraints);

//            JMenu[] menus = new JMenu[1];
//   
//    menus[0] = Seller;
//   
//
//    menus[0].add(RES);
        //JMenu menu = ComboMenuBar.createMenu("Supplier");
        JComboBox menu2 = new JComboBox();
        String[] auxName;

        for (String lse1 : LSEs) {
            if (checkbuyer(lse1) == 0) {
                menu2.addItem(lse1);
            }
        }
        auxName = new String[menu2.getItemCount()];
        for (int i = 0; i < auxName.length; i++) {
            auxName[i] = menu2.getItemAt(i).toString();
        }
        if (auxName.length > 0) {
            LoadAgent_Name openWindow = new LoadAgent_Name(this, 2, auxName);
            openWindow.setVisible(true);
        }
        /*
         if(checkbuyer(DO)==0) {
         menu2.addItem(DO);
         }
         if(checkbuyer(EC)==0) {
         menu2.addItem(EC);
         }
         if(checkbuyer(SCO)==0) {
         menu2.addItem(SCO);
         }
         if(checkbuyer(TE)==0) {
         menu2.addItem(TE);
         }
         */
        /*int j = checkbuyer(DO.getText());

         if (j == 0) {
         menu.add(DO);
         }
         j = checkbuyer(EC.getText());;
         if (j == 0) {
         menu.add(EC);
         }
         j = checkbuyer(SCO.getText());
         if (j == 0) {
         menu.add(SCO);
         }*/
////    menu.addSeparator();
//    menu.add(menus[2]);
//    menu.add(menus[3]);
        /*ComboMenuBar comboMenu = new ComboMenuBar(menu);

         comboMenu.setMinimumSize(new Dimension(180, 25));
         comboMenu.setPreferredSize(new Dimension(180, 25));*/
        menu2.setMinimumSize(new Dimension(180, 25));
        menu2.setPreferredSize(new Dimension(180, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 25, 50);
        //panel_center.add(comboMenu, gridBagConstraints);
        panel_center.add(menu2, gridBagConstraints);
        panel.add(panel_center, BorderLayout.CENTER);

        String[] choices1 = {"Cancel", "Next"};
        /*int result = JOptionPane.showOptionDialog(parent, panel, "Retailers (RetailCos) ", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices1, choices1[1]);

         if (result == 0 || result == -1) {

         return;
         } else if (result == 1) {

         //createAgent((String) comboMenu.getSelectedItem(), "buying.Buyer");
         createAgent((String) menu2.getSelectedItem(), "buying.Buyer");
         //        createAgent("David_Owen","buying.Buyer");
         }*/
    }

    public int checkbuyer(String Buyer) {
        int j = 0;
        for (int i = 0; i < buyer_names.size(); i++) {
            
            if (Buyer.trim().replace(" ", "_").equals(buyer_names.get(i).getLocalName().trim().replace(" ", "_"))) {
                i = buyer_names.size();
                j = 1;
            }
        }
        return j;
    }

    public void createAgent(String AgentName, String ClassName) {                // Creates a new agent via the platform controller
        PlatformController container = getContainerController();                // In - Agent Name * Agent Type i.e. "buying.Buyer" "selling.Seller" ... 
        try {
            AgentController newBuyer = container.createNewAgent(AgentName, ClassName, null);
            newBuyer.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Entrou no catch!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void killAgent(String AgentName, String ClassName) {
        PlatformController container = getContainerController();
        try {
            AgentController x = container.getAgent(AgentName);
            x.kill();

        } catch (Exception e) {
        }
    }

    public void seller_risk(PersonalAssistantGUI parent) {

        String[] choices = {"Save", "Cancel"};
        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
//        Listener2 listener2 = new Listener2();
        JPanel panel_north = new JPanel();
        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(350, 60));
        panel_north.setPreferredSize(new Dimension(350, 60));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(295, 6));
        panel_text_background.setPreferredSize(new Dimension(295, 60));
        label_text.setMinimumSize(new Dimension(290, 50));
        label_text.setPreferredSize(new Dimension(290, 50));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Enter the Generators's Risk Preferences</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_risk));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new rseller(this);

        panel_center.setMinimumSize(new Dimension(350, 100));
        panel_center.setPreferredSize(new Dimension(350, 250));
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        int result = JOptionPane.showOptionDialog(parent, panel, "Risk Preferences", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        if (result == 1) {
            seller_risk_exposure = 1.0;
            seller_risk = 0;
        }
        if (result == 0) {
//          seller_risk_exposure=Double.valueOf(jTseller.getText()); 
        }

    }

    public void buyer_risk(PersonalAssistantGUI parent) {

        String[] choices = {"Save", "Cancel"};
        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
//        Listener2 listener2 = new Listener2();
        JPanel panel_north = new JPanel();
        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(350, 60));
        panel_north.setPreferredSize(new Dimension(350, 60));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(295, 6));
        panel_text_background.setPreferredSize(new Dimension(295, 60));
        label_text.setMinimumSize(new Dimension(290, 50));
        label_text.setPreferredSize(new Dimension(290, 50));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Enter the Buyer's Risk Preferences</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_risk));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new rbuyer(this);

        panel_center.setMinimumSize(new Dimension(350, 100));
        panel_center.setPreferredSize(new Dimension(350, 250));
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        int result = JOptionPane.showOptionDialog(parent, panel, "Risk Preferences", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        if (result == 1) {
            buyer_risk_exposure = 0;
            buyer_risk = 0;
        }
        if (result == 0) {
//          buyer_risk_exposure=Double.valueOf(jTbuyer.getText()); 
        }
    }

    public void setContractOption_Windows(PersonalAssistantGUI parent) {
        contractTypeForm = new Bilateral_ContractType_Form(this);
        contractTypeForm.setVisible(false);

        negotiationForm = new Bilateral_NegotiationOption(this);
        negotiationForm.setVisible(false);
    }

    public void setContractType() {
        contractTypeForm.setVisible(true);
        negotiationForm.setVisible(false);
    }

    public void setNegotiationType() {
        negotiationForm.setVisible(true);
        contractTypeForm.setVisible(false);
    }

    public int getBilateral_contractType() {
        return bilateral_contractType;
    }

    public void setBilateral_contractType(int bilateral_contractType) {
        this.bilateral_contractType = bilateral_contractType;
        if(bilateral_contractType == 1) {
            mo_gui.setMarketSimulAvailable(false, false);
        }
    }

    public int getBilateral_contractDuration() {
        return bilateral_contractDuration;
    }

    public void setBilateral_contractDuration(int bilateral_contractDuration) {
        this.bilateral_contractDuration = bilateral_contractDuration;
    }

    public int getBilateral_tradingProcess() {
        return bilateral_tradingProcess;
    }

    public void setBilateral_tradingProcess(int bilateral_tradingProcess) {
        this.bilateral_tradingProcess = bilateral_tradingProcess;
    }

    public int getBilateral_tariff() {
        return bilateral_tariff;
    }

    public void setBilateral_tariff(int bilateral_tariff) {
        this.bilateral_tariff = bilateral_tariff;
    }

    public int getBilateral_hoursPeriod() {
        return bilateral_hoursPeriod;
    }

    public void setBilateral_hoursPeriod(int bilateral_hoursPeriod) {
        this.bilateral_hoursPeriod = bilateral_hoursPeriod;
    }

    public void setBilateral_DeadlineDate(int _year, int _month, int _day, int _hour, int _minutes) {
        bilateral_year = _year;
        bilateral_month = _month;
        bilateral_day = _day;
        bilateral_hour = _hour;
        bilateral_minutes = _minutes;
    }

    public int getBilateral_year() {
        return bilateral_year;
    }

    public int getBilateral_month() {
        return bilateral_month;
    }

    public int getBilateral_day() {
        return bilateral_day;
    }

    public int getBilateral_hour() {
        return bilateral_hour;
    }

    public int getBilateral_minutes() {
        return bilateral_minutes;
    }

    public void setMenus_availability(boolean _isContractNet) {
        mo_gui.setMarketOptionsAvailable(false, false, _isContractNet);
    }

    public void setprotocol(PersonalAssistantGUI parent) {

        String[] choices = {"Next", "Cancel"};

        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
//        Listener2 listener2 = new Listener2();
        JPanel panel_north = new JPanel();
        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(350, 60));
        panel_north.setPreferredSize(new Dimension(350, 60));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(295, 6));
        panel_text_background.setPreferredSize(new Dimension(295, 60));
        label_text.setMinimumSize(new Dimension(290, 50));
        label_text.setPreferredSize(new Dimension(290, 50));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Select a Format for the Trading Process</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_risk));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new protocol(this);

        panel_center.setMinimumSize(new Dimension(350, 100));
        panel_center.setPreferredSize(new Dimension(350, 200));
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        int result = JOptionPane.showOptionDialog(parent, panel, "Trading Protocol",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

        // If Next Button -> result = 0
        // If Cancel Button -> result = 1;
        if (result == 0) {
            timeperiods(null);
//          buyer_risk_exposure=0;
//          buyer_risk=0;
        } else {
        }

    }

    public void setcontract(PersonalAssistantGUI parent) {

        String[] choices = {"Save", "Cancel", "<< Back"};

        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
//        Listener2 listener2 = new Listener2();
        JPanel panel_north = new JPanel();
        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(350, 60));
        panel_north.setPreferredSize(new Dimension(350, 60));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(295, 6));
        panel_text_background.setPreferredSize(new Dimension(295, 60));
        label_text.setMinimumSize(new Dimension(290, 50));
        label_text.setPreferredSize(new Dimension(290, 50));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Indicate the type of Contract and Duration</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_risk));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new contract();

        panel_center.setMinimumSize(new Dimension(350, 100));
        panel_center.setPreferredSize(new Dimension(350, 200));
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        int result = JOptionPane.showOptionDialog(parent, panel, "Contract Type and Duration",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

        limits = new JTextField[2];
        limits[0] = new JTextField(64);
        limits[1] = new JTextField(64);

        if (result == 2) {
            askDeadline(null, 1);
        }
        if (result == 1) {
            contract = "Forward Contract";
        }
        if (result == 0) {
            double day = 0;
            day = (Double.valueOf(jT2.getText()));
            this.contractduration = (int) day;
        }

    }

    public void timeperiods(PersonalAssistantGUI parent) {

        String[] choices = {"Next >>", "Cancel", "<< Back"};
        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
//        Listener2 listener2 = new Listener2();
        JPanel panel_north = new JPanel();
        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(500, 60));
        panel_north.setPreferredSize(new Dimension(500, 60));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(500, 6));
        panel_text_background.setPreferredSize(new Dimension(500, 60));
        label_text.setMinimumSize(new Dimension(500, 50));
        label_text.setPreferredSize(new Dimension(500, 50));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Enter the Periods of the Day</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_risk));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new tperiods();

        panel_center.setMinimumSize(new Dimension(500, 100));
        panel_center.setPreferredSize(new Dimension(500, 200));
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        int result = JOptionPane.showOptionDialog(parent, panel, "Time Periods",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

        String s = jT1.getText();
//        limits[0].setText(jT1.getText());
//        limits[1].setText(jT1.getText());
        while (result != -1 && s == null) {

            JOptionPane.showMessageDialog(parent, new JLabel("<html>Please insert the negotiation Hours</html>"), "Targets", JOptionPane.ERROR_MESSAGE);
            result = JOptionPane.showOptionDialog(parent, new tperiods(), "Time Periods", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
            s = jT1.getText();
        }

        switch (result) {
            case 0: // Save Button

//            contract=((String) jComboBox2.getSelectedItem());
//            double d=0, day=0;
//            d=(Double.valueOf(jSlider1.getValue()));
//             this.N_PERIODS=(int)d;
                this.N_PERIODS = jS1.getValue();
                int[] array = new int[N_PERIODS + 1];
//             day=(Double.valueOf(jTextField2.getText()));
//             this.contractduration=(int)day;

//             for (int i=0; i<N_PERIODS; i++){
//                 array[i]=0;
//             }
                if (this.N_PERIODS != 24) {
//                    String s =jT1.getText();
                    String[] my_hours_array_aux = s.split(";");
                    String[] my_hours_array = new String[24 + N_PERIODS];
                    String[] hours_aux = new String[2];
                    for (int i = 0; i < my_hours_array_aux.length; i++) {
                        if (my_hours_array_aux[i].contains(",")) {
                            String[] hours = my_hours_array_aux[i].split(",");
                            for (int j = 0; j < hours.length; j++) {
                                if (hours[j].contains("-")) {
                                    hours_aux = hours[j].split("-");
                                    for (int z = 0; z < 1 + (Double.valueOf(hours_aux[1])) - (Double.valueOf(hours_aux[0])); z++) {
                                        my_hours_array[array[0]] = String.valueOf(Double.valueOf(hours_aux[0]) + z);
                                        array[0]++;
                                    }
                                } else {
                                    my_hours_array[array[0]] = hours[j];
                                    array[0]++;
                                }
                            }

                        } else {
                            if (my_hours_array_aux[i].contains("-")) {
                                hours_aux = my_hours_array_aux[i].split("-");
                                for (int z = 0; z < 1 + (Double.valueOf(hours_aux[1])) - (Double.valueOf(hours_aux[0])); z++) {
                                    my_hours_array[array[0]] = String.valueOf(Double.valueOf(hours_aux[0]) + z);
                                    array[0]++;
                                }
                            }
                        }
                        array[i + 1] = array[0];

                        for (int y = 0; y < i; y++) {
                            array[i + 1] = array[i + 1] - array[i];
                        }
                        if (array[i + 1] <= 0) {
                            my_hours_array[i] = my_hours_array_aux[i];
                            array[i + 1] = 1;
                        }
                        my_hours_array[24 + i] = String.valueOf(array[i + 1]);
                    }
                    for (int j = 0; j < my_hours_array.length; j++) {
                        getHours().add(my_hours_array[j]);
                    }
//                buy_gui.redefine();
                }
                // Seller Deadline
                askDeadline(null, 0);
                break;
            case 1: // Cancel Button
                break;
            case 2: // Back Button
                this.setprotocol(null);
                break;
        }

    }

    public void askDeadline(PersonalAssistantGUI parent, int agent) {

        Date date_proposed = new Date();

        date_proposed.setTime(System.currentTimeMillis());
//        
//
//        JPanel panel = new JPanel(new BorderLayout());
//
//        //Panel north
//        JPanel panel_north = new JPanel();
//
//        panel_north.setLayout(new BorderLayout());
//        panel_north.setMinimumSize(new Dimension(400, 70));
//        panel_north.setPreferredSize(new Dimension(400, 70));
//        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));
//
//        JPanel panel_text_background = new JPanel();
//        JLabel label_text = new JLabel();
//        panel_text_background.setMinimumSize(new Dimension(345, 60));
//        panel_text_background.setPreferredSize(new Dimension(345, 60));
//        label_text.setMinimumSize(new Dimension(345, 60));
//        label_text.setPreferredSize(new Dimension(345, 60));
//        label_text.setHorizontalAlignment(SwingConstants.CENTER);
//       
//            label_text.setText("<html>Enter your deadline </html>");
//        
//        label_text.setFont(font_1);
//        panel_text_background.add(label_text);
//        panel_north.add(panel_text_background, BorderLayout.CENTER);
//
//
//        try {
//            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
//            JPanel panel_pic_background = new JPanel();
//            JLabel label_pic = new JLabel(new ImageIcon(picture));
//            panel_pic_background.setMinimumSize(new Dimension(55, 55));
//            panel_pic_background.setPreferredSize(new Dimension(55, 55));
//            panel_pic_background.add(label_pic);
//            panel_north.add(panel_pic_background, BorderLayout.EAST);
//        } catch (IOException ex) {
//            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
//        }
////        
//        panel.add(panel_north, BorderLayout.NORTH);

        String[] choices = {"Set", "<< Back"};
        String title = "";
        switch (agent) {
            case 0:
                title = "Seller Deadline";
                break;
            case 1:
                title = "Buyer Deadline";
                break;
            default:
                JOptionPane.showMessageDialog(null, "AGENT ERROR",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                break;
        }
        int result = JOptionPane.showOptionDialog(parent, new deadline(), title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

        switch (result) {
            case 1:
                switch (agent) {
                    case 0:
                        // Get back to Time Periods
                        timeperiods(null);
                        break;
                    case 1:
                        // Get back to Seller Deadline
                        askDeadline(null, 0);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "AGENT ERROR",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                        break;
                }
                break;
            case 0: // Button Set
                TimeChooser tc = new TimeChooser(date_proposed);
                int result_date = tc.showEditTimeDlg(parent);
                while (tc.getDate().getTime() <= System.currentTimeMillis()) {
                    String[] choices2 = {"OK"};
                    int aux = JOptionPane.showOptionDialog(parent, "You must choose a date higher than the current date.", " WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices2, null);
                    if (aux == 0) {
                        tc = new TimeChooser(date_proposed);
                        result_date = tc.showEditTimeDlg(parent);
                        System.out.println(tc.getDate().getTime() + "   " + System.currentTimeMillis());
                    }
                }
                while (result_date == 1) {
                    tc = new TimeChooser(date_proposed);
                    result_date = tc.showEditTimeDlg(parent);
                }
                if (agent == 0) {
                    sellerdeadline = tc.getDate();
                    askDeadline(null, 1);
                }
                if (agent == 1) {
                    buyerdeadline = tc.getDate();
                    setcontract(null);
                }
                break;
        }
    }

    public void Volumes(PersonalAssistantGUI parent) {

        periods = new JTextField[1];
//            limits = new JTextField[1];

        Listener listener = new Listener();

        JPanel panel = new JPanel(new BorderLayout());

        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setMinimumSize(new Dimension(200, 75));
        panel_center.setPreferredSize(new Dimension(200, 75));

        // Panel north
        chinButton = new JCheckBox("Yes");
        chinButton.setMnemonic(KeyEvent.VK_0);
        chinButton.setSelected(true);

        //Register a listener for the check boxes.
        chinButton.addItemListener(listener);
        chinButton.setMinimumSize(new Dimension(50, 25));
        chinButton.setPreferredSize(new Dimension(50, 25));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 30, 0, 100);
        panel_center.add(chinButton, gridBagConstraints);

        chinButton2 = new JCheckBox("No");
        chinButton2.setMnemonic(KeyEvent.VK_0);
        chinButton2.setSelected(false);

        //Register a listener for the check boxes.
        chinButton2.addItemListener(listener);
        chinButton2.setMinimumSize(new Dimension(50, 25));
        chinButton2.setPreferredSize(new Dimension(50, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(0, 100, 0, 0);
        panel_center.add(chinButton2, gridBagConstraints);

        JLabel l = new JLabel("<html>Do you want to change your Volumes?</html>");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        panel_center.add(l, gridBagConstraints);

        panel.add(panel_center, BorderLayout.CENTER);

        String[] choices = {"Save", "Cancel"};

        int result = JOptionPane.showOptionDialog(parent, panel, "Demand Response", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

//        while (result != -1 && (checkEmptyFields(periods) || checkEmptyFields(limits))) {
//
//            JOptionPane.showMessageDialog(parent, new JLabel("<html>Some inputs are missing</html>"), "Targets", JOptionPane.ERROR_MESSAGE);
//            result = JOptionPane.showOptionDialog(parent, panel, "Targets", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
//
//        }
        if (result == 0) {

        }

    }

    private class Listener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            Object source = e.getItemSelectable();

            if (source == chinButton && e.getStateChange() != ItemEvent.DESELECTED) {
                demandresponse = 1;
//        buyer.setDemandResponse(demandresponse);
                if (chinButton2.isSelected()) {
                    chinButton2.setSelected(false);
                }
//         chinButton.setSelected(true);
            }
            if (source == chinButton && e.getStateChange() == ItemEvent.DESELECTED) {
                demandresponse = 0;
//            buyer.setDemandResponse(demandresponse);
                if (!chinButton2.isSelected()) {
                    chinButton2.setSelected(true);
                }

//             chinButton.setSelected(false);
            }
            if (source == chinButton2 && e.getStateChange() != ItemEvent.DESELECTED) {
                demandresponse = 0;
//        buyer.setDemandResponse(demandresponse);
                if (chinButton.isSelected()) {
                    chinButton.setSelected(false);
                }
//         chinButton.setSelected(true);

            }
            if (source == chinButton2 && e.getStateChange() == ItemEvent.DESELECTED) {
                demandresponse = 1;
//            buyer.setDemandResponse(demandresponse);
                if (!chinButton.isSelected()) {
                    chinButton.setSelected(true);
                }
//             chinButton.setSelected(false);
            }

        }
    }

    public boolean checkEmptyFields(JTextField[] text_fields) {
        for (int i = 0; i < text_fields.length; i++) {
            if (text_fields[i].getText().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void removeNegotiationPair(AID[] pair) {

        this.negotiation_pairs.remove(pair);
    }

    public ArrayList<String> getBelifsAboutMyAgent() {
        return this.beliefs_about_myagent;
    }

    public ArrayList<String> getHours() {
        return this.HOURS;
    }

    private void addBelif(String name, String belief) {

        if (name.equals("myagent")) {
            getBelifsAboutMyAgent().add(belief);
        } else if (getBelifsAboutOthers().containsKey(name)) {
            ArrayList<String> list = getBelifsAboutOthers().get(name);
            list.add(belief);
            getBelifsAboutOthers().put(name, list);
        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add(belief);
            getBelifsAboutOthers().put(name, list);
        }
    }

    public boolean beliefExists(String name, String belief) {

        if (name.equals("myagent")) {
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

    public String searchPartialBelief(String name, String belief) {

        if (name.equals("myagent")) {
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
        return null;
    }

    public void removeBelief(String name, String belief) {

        if (name.equals("myagent")) {
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

    public ArrayList<AID> getBuyerNames() {
        return buyer_names;
    }

    public ArrayList<AID> getSellerNames() {
        return seller_names;
    }
    
    public ArrayList<AID> getLargeConsumersNames() {
        return largeConsumer_names;
    }

    public ArrayList<AID> getMediumConsumerNames() {
        return mediumConsumer_names;
    }
 public ArrayList<AID> getProducerNames() {
        return producer_names;
    }
    public class BuyerAgentsName {

        public String BuyerAgentsName() {
            return getSellerNames().toString();
        }
    }

    public class tperiods extends javax.swing.JPanel {

        /**
         * Creates new form tperiods
         */
        public tperiods() {
            initComponents();
        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jT1 = new javax.swing.JTextField();
            jLabel2 = new javax.swing.JLabel();
//        jSeparator3 = new javax.swing.JSeparator();
            jLabel5 = new javax.swing.JLabel();
            jS1 = new javax.swing.JSlider();
//        jLabel3 = new javax.swing.JLabel();

            jT1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
            jT1.setText("1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19;20;21;22;23;24");

            jLabel2.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jLabel2.setText("Periods");

//        jSeparator3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//        jSeparator3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
            jLabel5.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jLabel5.setText("Hour");

            jS1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
            jS1.setMajorTickSpacing(1);
            jS1.setMaximum(24);
            jS1.setMinimum(1);
            jS1.setMinorTickSpacing(1);
            jS1.setPaintLabels(true);
            jS1.setPaintTicks(true);
            jS1.setToolTipText("");

//        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel3.setText("Please enter the Periods of the day that you want to Negotiate.");
            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap(50, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jT1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(50, 50, 50))
                    .addGroup(layout.createSequentialGroup()
                            .addGap(78, 78, 78)
                            //                .addComponent(jLabel3)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    //                    .addComponent(jSeparator3)
                                    .addComponent(jS1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                            .addContainerGap())
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            //                .addComponent(jLabel3)
                            //                .addGap(26, 26, 26)
                            //                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            //                .addGap(2, 2, 2)
                            .addComponent(jLabel2)
                            .addGap(18, 18, 18)
                            .addComponent(jS1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                            .addGap(30, 30, 30))
            );
        }// </editor-fold>

        // Variables declaration - do not modify
        private javax.swing.JLabel jLabel2;
//    private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel5;

//    private javax.swing.JSeparator jSeparator3;
        // End of variables declaration
    }

    public class contract extends javax.swing.JPanel {

        /**
         * Creates new form risk
         */
        public contract() {
            initComponents();
        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
//        jSeparator1 = new javax.swing.JSeparator();
//        jLabel2 = new javax.swing.JLabel();
            jCheckBox5 = new javax.swing.JCheckBox();
            jCheckBox6 = new javax.swing.JCheckBox();
            jCheckBox7 = new javax.swing.JCheckBox();
            jLabel12 = new javax.swing.JLabel();
            jT2 = new javax.swing.JTextField();

            jLabel1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jLabel1.setText("Type:");

//        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel2.setText("Indicate the type of Contract and the duration ");
            jCheckBox5.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jCheckBox5.setText("Forward Contract");
            jCheckBox5.setSelected(true);
//        jCheckBox5.setEnabled(false);

            jCheckBox6.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jCheckBox6.setText("Contract For Differences");
//        jCheckBox6.setEnabled(false);

            jCheckBox7.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jCheckBox7.setText("Option Contract");
//        jCheckBox7.setEnabled(false);

            jLabel12.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jLabel12.setText("Duration (days):");

            jT2.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
            jT2.setText("" + contractduration);
            jT2.setPreferredSize(new Dimension(40, 25));

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    //            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(19, 19, 19)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel12)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(jT2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel1)
                                                            .addGap(33, 33, 33)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addComponent(jCheckBox6)
                                                                    .addComponent(jCheckBox5)
                                                                    .addComponent(jCheckBox7)))))
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(22, 22, 22)
                                    //                        .addComponent(jLabel2)
                                    ))
                            .addContainerGap(22, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            //                .addGap(21, 21, 21)
                            //                .addComponent(jLabel2)
                            //                .addGap(27, 27, 27)
                            //                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(24, 24, 24)
                            .addComponent(jCheckBox5)
                            .addGap(0, 0, 0)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCheckBox6)
                                    .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCheckBox7)
                            .addGap(50, 50, 50)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(jT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(50, Short.MAX_VALUE))
            );
        }// </editor-fold>

        private class ContractListener implements ItemListener {

            public void itemStateChanged(ItemEvent e) {

                Object source = e.getItemSelectable();

                if (source == jCheckBox5 && e.getStateChange() != ItemEvent.DESELECTED) {
                    contract = jCheckBox5.getName();
//        buyer.setDemandResponse(demandresponse);
                    if (jCheckBox6.isSelected()) {
                        jCheckBox6.setSelected(false);
                    }
                    if (jCheckBox7.isSelected()) {
                        jCheckBox7.setSelected(false);
                    }
//         chinButton.setSelected(true);
                }
                if (source == jCheckBox5 && e.getStateChange() == ItemEvent.DESELECTED) {

//            buyer.setDemandResponse(demandresponse);
                    if (jCheckBox6.isSelected()) {
                        contract = jCheckBox6.getName();
                    }
                    if (jCheckBox7.isSelected()) {
                        contract = jCheckBox7.getName();
                    }
                    if (!jCheckBox6.isSelected() && !jCheckBox7.isSelected()) {
                        jCheckBox5.setSelected(true);
                    }

//             chinButton.setSelected(false);
                }
                if (source == jCheckBox6 && e.getStateChange() != ItemEvent.DESELECTED) {
                    contract = jCheckBox6.getName();;
//        buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        jCheckBox5.setSelected(false);
                    }
                    if (jCheckBox7.isSelected()) {
                        jCheckBox7.setSelected(false);
                    }

                }
                if (source == jCheckBox6 && e.getStateChange() == ItemEvent.DESELECTED) {

//            buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        contract = jCheckBox5.getName();
                    }
                    if (jCheckBox7.isSelected()) {
                        contract = jCheckBox7.getName();
                    }
                    if (!jCheckBox5.isSelected() && !jCheckBox7.isSelected()) {
                        jCheckBox6.setSelected(true);
                    }
//             chinButton.setSelected(false);
                }
                if (source == jCheckBox7 && e.getStateChange() != ItemEvent.DESELECTED) {
                    contract = jCheckBox7.getName();
//        buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        jCheckBox5.setSelected(false);
                    }
                    if (jCheckBox6.isSelected()) {
                        jCheckBox6.setSelected(false);
                    }

                }
                if (source == jCheckBox7 && e.getStateChange() == ItemEvent.DESELECTED) {

//            buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        contract = jCheckBox5.getName();
                    }
                    if (jCheckBox6.isSelected()) {
                        contract = jCheckBox6.getName();
                    }
                    if (!jCheckBox6.isSelected() && !jCheckBox5.isSelected()) {
                        jCheckBox7.setSelected(true);
                    }
//             chinButton.setSelected(false);
                }

            }
        }

        // Variables declaration - do not modify
        public javax.swing.JCheckBox jCheckBox5;
        public javax.swing.JCheckBox jCheckBox6;
        public javax.swing.JCheckBox jCheckBox7;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel12;
//    private javax.swing.JLabel jLabel2;
//    private javax.swing.JSeparator jSeparator1;

        // End of variables declaration
    }
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    /**
     *
     * @author Hugo
     */
    public class risk_seller extends javax.swing.JPanel {

        public PersonalAssistant mark;

        /**
         * Creates new form risk
         */
        public risk_seller(PersonalAssistant market) {
            mark = market;
            initComponents();

        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            jSeparator1 = new javax.swing.JSeparator();
            jLabel2 = new javax.swing.JLabel();
            jCheckBox5 = new javax.swing.JCheckBox();
            jCheckBox6 = new javax.swing.JCheckBox();
            jCheckBox7 = new javax.swing.JCheckBox();
            jLabel3 = new javax.swing.JLabel();
            jTseller = new javax.swing.JTextField();
//        SpinnerModel sm = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
//        jSpinner1 = new javax.swing.JSpinner(sm);

            Listener listener = new Listener();

            jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jLabel1.setText("Agent:");

            jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jLabel2.setText("Please enter the Generator's Risk Preference");

            jCheckBox5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jCheckBox5.setText("Risk-Averse");
            jCheckBox5.addItemListener(listener);

            jCheckBox6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jCheckBox6.setText("Risk-Neutral");
            jCheckBox6.setSelected(true);
            jCheckBox6.addItemListener(listener);

            jCheckBox7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jCheckBox7.setText("Risk-Seeking");
            jCheckBox7.addItemListener(listener);

            jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jLabel3.setText("Risk Exposure:");

//        jSpinner1.setRequestFocusEnabled(false);
//          jSpinner1.addChangeListener(new ChangeListener() {
//
//        @Override
//        public void stateChanged(ChangeEvent e) {
//            mark.seller_risk_exposure=(double)jSpinner1.getValue();
//        }
//    });
//        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)jSpinner1.getEditor();  
//        DecimalFormat format = editor.getFormat();  
//        format.setMinimumFractionDigits(1);  
//        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
//        Dimension d = jSpinner1.getPreferredSize();  
//        d.width = 40;  
//        jSpinner1.setPreferredSize(d); 
//        jSpinner1.setMinimumSize(new Dimension(20, 20));
            jTseller.setText("0.0");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                            .addGap(19, 19, 19)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jTseller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addGap(28, 28, 28)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jCheckBox6)
                                                    .addComponent(jCheckBox5)
                                                    .addComponent(jCheckBox7))))
                            .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addContainerGap())
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addComponent(jLabel2)
                            .addGap(24, 24, 24)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(24, 24, 24)
                            .addComponent(jCheckBox5)
                            .addGap(0, 0, 0)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCheckBox6)
                                    .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCheckBox7)
                            .addGap(24, 24, 24)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jTseller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }// </editor-fold>

        private class Listener implements ItemListener {

            public void itemStateChanged(ItemEvent e) {

                Object source = e.getItemSelectable();

                if (source == jCheckBox5 && e.getStateChange() != ItemEvent.DESELECTED) {
                    mark.seller_risk = 1;
//        seller.setDemandResponse(demandresponse);
                    if (jCheckBox6.isSelected()) {
                        jCheckBox6.setSelected(false);
                    }
                    if (jCheckBox7.isSelected()) {
                        jCheckBox7.setSelected(false);
                    }
//         chinButton.setSelected(true);
                }
                if (source == jCheckBox5 && e.getStateChange() == ItemEvent.DESELECTED) {

//            seller.setDemandResponse(demandresponse);
                    if (jCheckBox6.isSelected()) {
                        mark.seller_risk = 0;
                    }
                    if (jCheckBox7.isSelected()) {
                        mark.seller_risk = 2;
                    }
                    if (!jCheckBox6.isSelected() && !jCheckBox7.isSelected()) {
                        jCheckBox5.setSelected(true);
                    }

//             chinButton.setSelected(false);
                }
                if (source == jCheckBox6 && e.getStateChange() != ItemEvent.DESELECTED) {
                    mark.seller_risk = 0;
//        seller.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        jCheckBox5.setSelected(false);
                    }
                    if (jCheckBox7.isSelected()) {
                        jCheckBox7.setSelected(false);
                    }

                }
                if (source == jCheckBox6 && e.getStateChange() == ItemEvent.DESELECTED) {

//            seller.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        mark.seller_risk = 1;
                    }
                    if (jCheckBox7.isSelected()) {
                        mark.seller_risk = 2;
                    }
                    if (!jCheckBox5.isSelected() && !jCheckBox7.isSelected()) {
                        jCheckBox6.setSelected(true);
                    }
//             chinButton.setSelected(false);
                }
                if (source == jCheckBox7 && e.getStateChange() != ItemEvent.DESELECTED) {
                    mark.seller_risk = 2;
//        seller.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        jCheckBox5.setSelected(false);
                    }
                    if (jCheckBox6.isSelected()) {
                        jCheckBox6.setSelected(false);
                    }

                }
                if (source == jCheckBox7 && e.getStateChange() == ItemEvent.DESELECTED) {

//            seller.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        mark.seller_risk = 1;
                    }
                    if (jCheckBox6.isSelected()) {
                        mark.seller_risk = 0;
                    }
                    if (!jCheckBox6.isSelected() && !jCheckBox5.isSelected()) {
                        jCheckBox7.setSelected(true);
                    }
//             chinButton.setSelected(false);
                }

            }
        }
//     private class Listener2 implements ChangeListener {
//public void StateChanged(ChangeEvent e) {
//  
//    Object source = e.getSource();
//    
//    if (source == JSpinner1 && e.getStateChange() != ItemEvent.DESELECTED) {
//    
//}
//}
//     }
        // Variables declaration - do not modify
        public javax.swing.JCheckBox jCheckBox5;
        public javax.swing.JCheckBox jCheckBox6;
        public javax.swing.JCheckBox jCheckBox7;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JSeparator jSeparator1;
//    public javax.swing.JSpinner jSpinner1;

        // End of variables declaration
    }
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    /**
     *
     * @author Hugo
     */
    public class risk_buyer extends javax.swing.JPanel {

        public PersonalAssistant mark;

        /**
         * Creates new form risk
         */
        public risk_buyer(PersonalAssistant market) {
            mark = market;
            initComponents();

        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            jSeparator1 = new javax.swing.JSeparator();
            jLabel2 = new javax.swing.JLabel();
            jCheckBox5 = new javax.swing.JCheckBox();
            jCheckBox6 = new javax.swing.JCheckBox();
            jCheckBox7 = new javax.swing.JCheckBox();
            jLabel3 = new javax.swing.JLabel();
//        SpinnerModel sm = new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1);
//        jSpinner1 = new javax.swing.JSpinner(sm);
            jTbuyer = new javax.swing.JTextField();

            Listener listener = new Listener();

            jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jLabel1.setText("Agent:");

            jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jLabel2.setText("Please enter the buyer's Risk Preference");

            jCheckBox5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jCheckBox5.setText("Risk-Averse");
            jCheckBox5.addItemListener(listener);

            jCheckBox6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jCheckBox6.setText("Risk-Neutral");
            jCheckBox6.addItemListener(listener);
            jCheckBox6.setSelected(true);

            jCheckBox7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jCheckBox7.setText("Risk-Seeking");
            jCheckBox7.addItemListener(listener);

            jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
            jLabel3.setText("Risk Exposure:");

            jTbuyer.setText("0.0");

//        jSpinner1.setRequestFocusEnabled(false);
//          jSpinner1.addChangeListener(new ChangeListener() {
//
////        @Override
////        public void stateChanged(ChangeEvent e) {
////            mark.buyer_risk_exposure=(double)jSpinner1.getValue();
////        }
////    });
//        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)jSpinner1.getEditor();  
//        DecimalFormat format = editor.getFormat();  
//        format.setMinimumFractionDigits(1);  
//        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
//        Dimension d = jSpinner1.getPreferredSize();  
//        d.width = 40;  
//        jSpinner1.setPreferredSize(d); 
//        jSpinner1.setMinimumSize(new Dimension(20, 20));
            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                            .addGap(19, 19, 19)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jTbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addGap(28, 28, 28)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jCheckBox6)
                                                    .addComponent(jCheckBox5)
                                                    .addComponent(jCheckBox7))))
                            .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2)
                            .addContainerGap())
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addGap(24, 24, 24)
                            .addComponent(jLabel2)
                            .addGap(24, 24, 24)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(24, 24, 24)
                            .addComponent(jCheckBox5)
                            .addGap(0, 0, 0)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCheckBox6)
                                    .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCheckBox7)
                            .addGap(24, 24, 24)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(jTbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }// </editor-fold>

        private class Listener implements ItemListener {

            public void itemStateChanged(ItemEvent e) {

                Object source = e.getItemSelectable();

                if (source == jCheckBox5 && e.getStateChange() != ItemEvent.DESELECTED) {
                    mark.buyer_risk = 1;
//        buyer.setDemandResponse(demandresponse);
                    if (jCheckBox6.isSelected()) {
                        jCheckBox6.setSelected(false);
                    }
                    if (jCheckBox7.isSelected()) {
                        jCheckBox7.setSelected(false);
                    }
//         chinButton.setSelected(true);
                }
                if (source == jCheckBox5 && e.getStateChange() == ItemEvent.DESELECTED) {

//            buyer.setDemandResponse(demandresponse);
                    if (jCheckBox6.isSelected()) {
                        mark.buyer_risk = 0;
                    }
                    if (jCheckBox7.isSelected()) {
                        mark.buyer_risk = 2;
                    }
                    if (!jCheckBox6.isSelected() && !jCheckBox7.isSelected()) {
                        jCheckBox5.setSelected(true);
                    }

//             chinButton.setSelected(false);
                }
                if (source == jCheckBox6 && e.getStateChange() != ItemEvent.DESELECTED) {
                    mark.buyer_risk = 0;
//        buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        jCheckBox5.setSelected(false);
                    }
                    if (jCheckBox7.isSelected()) {
                        jCheckBox7.setSelected(false);
                    }

                }
                if (source == jCheckBox6 && e.getStateChange() == ItemEvent.DESELECTED) {

//            buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        mark.buyer_risk = 1;
                    }
                    if (jCheckBox7.isSelected()) {
                        mark.buyer_risk = 2;
                    }
                    if (!jCheckBox5.isSelected() && !jCheckBox7.isSelected()) {
                        jCheckBox6.setSelected(true);
                    }
//             chinButton.setSelected(false);
                }
                if (source == jCheckBox7 && e.getStateChange() != ItemEvent.DESELECTED) {
                    mark.buyer_risk = 2;
//        buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        jCheckBox5.setSelected(false);
                    }
                    if (jCheckBox6.isSelected()) {
                        jCheckBox6.setSelected(false);
                    }

                }
                if (source == jCheckBox7 && e.getStateChange() == ItemEvent.DESELECTED) {

//            buyer.setDemandResponse(demandresponse);
                    if (jCheckBox5.isSelected()) {
                        mark.buyer_risk = 1;
                    }
                    if (jCheckBox6.isSelected()) {
                        mark.buyer_risk = 0;
                    }
                    if (!jCheckBox6.isSelected() && !jCheckBox5.isSelected()) {
                        jCheckBox7.setSelected(true);
                    }
//             chinButton.setSelected(false);
                }

            }
        }
//     private class Listener2 implements ChangeListener {
//public void StateChanged(ChangeEvent e) {
//  
//    Object source = e.getSource();
//    
//    if (source == JSpinner1 && e.getStateChange() != ItemEvent.DESELECTED) {
//    
//}
//}
//     }
        // Variables declaration - do not modify
        public javax.swing.JCheckBox jCheckBox5;
        public javax.swing.JCheckBox jCheckBox6;
        public javax.swing.JCheckBox jCheckBox7;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JSeparator jSeparator1;
//    public javax.swing.JSpinner jSpinner1;
//    public javax.swing.JTextField jTbuyer;
        // End of variables declaration
    }

    public class Tools
            extends JInternalFrame {

        private JTabbedPane tabbedPane;
        private JTabbedPane panel1;
        private JTabbedPane panel2;
        private JTabbedPane panel3;
        private JTabbedPane panel4;

        public Tools(PersonalAssistant market) {
            // NOTE: to reduce the amount of code in this example, it uses
            // panels with a NULL layout.  This is NOT suitable for
            // production code since it may not display correctly for
            // a look-and-feel.

            setTitle("Preferences");
            setSize(600, 600);
            setBackground(Color.gray);

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BorderLayout());
            getContentPane().add(topPanel);

            // Create the tab pages
//		createPage1();
//		createPage2();
//		createPage3();
            panel1 = new JTabbedPane();
            panel1.add("Time Periods", new tperiods());
            panel1.add("Trading Protocol", new protocol(market));

            panel2 = new JTabbedPane();
            panel2.add("Type and Duration", new contract());

            panel3 = new JTabbedPane();
            panel3.add("Risk Preference", new risk_seller(market));

            panel4 = new JTabbedPane();
            panel4.add("Risk Preference", new risk_buyer(market));
            panel4.add("DR Management", new DR(market));
//                panel3.add("Deadline", new deadline());
//                panel3.add("Deadline", new setDeadline());

//                panel2.add("Trading Protocol", new protocol(market));
            // Create a tabbed pane
            tabbedPane = new JTabbedPane();
            Border emptyBorder = BorderFactory.createEmptyBorder();

//                tabbedPane.setMaximumSize(new Dimension(negicon.getIconHeight(), negicon.getIconWidth()));
            tabbedPane.setBorder(emptyBorder);
            UIManager.getDefaults().put("tabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            UIManager.getDefaults().put("tabbedPane.tabsOverlapBorder", true);
            UIManager.getDefaults().put("panel1.contentBorderInsets", new Insets(0, 0, 0, 0));
            UIManager.getDefaults().put("panel1.tabsOverlapBorder", true);

//                panel1.setMaximumSize(new Dimension(20, 20));
            panel1.setBorder(emptyBorder);

            tabbedPane.addTab(null, negicon, panel1);
            tabbedPane.setBorder(emptyBorder);
//                tabbedPane.setMaximumSize(new Dimension(20, 20));
            tabbedPane.setBorder(emptyBorder);
            tabbedPane.addTab(null, conicon, panel2);
            tabbedPane.addTab(null, sellicon, panel3);
            tabbedPane.addTab(null, buyicon, panel4);
//                tabbedPane.setMaximumSize(new Dimension(20, 20));
            tabbedPane.setBorder(emptyBorder);
//                 UIManager.getDefaults().put("tabbedPane.contentBorderInsets", new Insets(0,0,0,0));
//                UIManager.getDefaults().put("tabbedPane.tabsOverlapBorder", true);
//                UIManager.getDefaults().put("panel1.contentBorderInsets", new Insets(0,0,0,0));
//                UIManager.getDefaults().put("panel1.tabsOverlapBorder", true);
//                                   tabbedPane.setUI(new BasicTabbedPaneUI() {
//        private final Insets borderInsets = new Insets(0, 0, 0, 0);
//        @Override
//        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
//        }
//        @Override
//        protected Insets getContentBorderInsets(int tabPlacement) {
//            return borderInsets;
//        }
//    });
//		tabbedPane.addTab( "Page 2", panel2 );
//		tabbedPane.addTab( "Page 3", panel3 );
//                topPanel.setBorder(emptyBorder);
            topPanel.add(tabbedPane, BorderLayout.CENTER);
            this.setVisible(true);
//                topPanel.setVisible( true );
        }
    }
}

class ComboMenuBar extends JMenuBar {

    JMenu menu;

    Dimension preferredSize;

    public ComboMenuBar(JMenu menu) {
        this.menu = menu;

        Color color = UIManager.getColor("Menu.selectionBackground");
        UIManager.put("Menu.selectionBackground", UIManager
                .getColor("Menu.background"));
        menu.updateUI();
        UIManager.put("Menu.selectionBackground", color);

        ComboMenuBar.MenuItemListener listener = new ComboMenuBar.MenuItemListener();
        setListener(menu, listener);

        add(menu);
    }

    class MenuItemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            menu.setText(item.getText());
            menu.requestFocus();
        }
    }

    private void setListener(JMenuItem item, ActionListener listener) {
        if (item instanceof JMenu) {
            JMenu menu = (JMenu) item;
            int n = menu.getItemCount();
            for (int i = 0; i < n; i++) {
                setListener(menu.getItem(i), listener);
            }
        } else if (item != null) { // null means separator
            item.addActionListener(listener);
        }
    }

    public String getSelectedItem() {
        return menu.getText();
    }

    public void setPreferredSize(Dimension size) {
        preferredSize = size;
    }

    public Dimension getPreferredSize() {
        if (preferredSize == null) {
            Dimension sd = super.getPreferredSize();
            Dimension menuD = getItemSize(menu);
            Insets margin = menu.getMargin();
            Dimension retD = new Dimension(menuD.width, margin.top
                    + margin.bottom + menuD.height);
            menu.setPreferredSize(retD);
            preferredSize = retD;
        }
        return preferredSize;
    }

    private Dimension getItemSize(JMenu menu) {
        Dimension d = new Dimension(0, 0);
        int n = menu.getItemCount();
        for (int i = 0; i < n; i++) {
            Dimension itemD;
            JMenuItem item = menu.getItem(i);
            if (item instanceof JMenu) {
                itemD = getItemSize((JMenu) item);
            } else if (item != null) {
                itemD = item.getPreferredSize();
            } else {
                itemD = new Dimension(0, 0); // separator
            }
            d.width = Math.max(d.width, itemD.width);
            d.height = Math.max(d.height, itemD.height);
        }
        return d;
    }

    public static class ComboMenu extends JMenu {

        personalassistant.ArrowIcon iconRenderer;

        public ComboMenu(String label) {
            super(label);
            iconRenderer = new personalassistant.ArrowIcon(SwingConstants.SOUTH, true);
            setBorder(new EtchedBorder());
            setIcon(new personalassistant.BlankIcon(null, 11));
            setHorizontalTextPosition(JButton.LEFT);
            setFocusPainted(true);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension d = this.getPreferredSize();
            int x = Math.max(0, d.width - iconRenderer.getIconWidth() - 3);
            int y = Math.max(0,
                    (d.height - iconRenderer.getIconHeight()) / 2 - 2);
            iconRenderer.paintIcon(this, g, x, y);
        }
    }

    public static JMenu createMenu(String label) {
        return new personalassistant.ComboMenuBar.ComboMenu(label);
    }

}

class ArrowIcon implements Icon, SwingConstants {

    private static final int DEFAULT_SIZE = 11;

    //private static final int DEFAULT_SIZE = 5;
    private int size;

    private int iconSize;

    private int direction;

    private boolean isEnabled;

    private BasicArrowButton iconRenderer;

    public ArrowIcon(int direction, boolean isPressedView) {
        this(DEFAULT_SIZE, direction, isPressedView);
    }

    public ArrowIcon(int iconSize, int direction, boolean isEnabled) {
        this.size = iconSize / 2;
        this.iconSize = iconSize;
        this.direction = direction;
        this.isEnabled = isEnabled;
        iconRenderer = new BasicArrowButton(direction);
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        iconRenderer.paintTriangle(g, x, y, size, direction, isEnabled);
    }

    public int getIconWidth() {
        //int retCode;
        switch (direction) {
            case NORTH:
            case SOUTH:
                return iconSize;
            case EAST:
            case WEST:
                return size;
        }
        return iconSize;
    }

    public int getIconHeight() {
        switch (direction) {
            case NORTH:
            case SOUTH:
                return size;
            case EAST:
            case WEST:
                return iconSize;
        }
        return size;
    }
}

class BlankIcon implements Icon {

    private Color fillColor;

    private int size;

    public BlankIcon() {
        this(null, 11);
    }

    public BlankIcon(Color color, int size) {
        //UIManager.getColor("control")
        //UIManager.getColor("controlShadow")
        fillColor = color;

        this.size = size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (fillColor != null) {
            g.setColor(fillColor);
            g.drawRect(x, y, size - 1, size - 1);
        }
    }

    public int getIconWidth() {
        return size;
    }

    public int getIconHeight() {
        return size;
    }
    
    
    
}
