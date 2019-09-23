package personalassistant;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import DayAheadInterface.DayaheadinterfaceBDI;
//import java.util.Arrays;
import OTC.OTC_Controller;
import OTC.Participants;
import OTC.Results;
import externalassistant.ExternalAssistantBDI;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SUtil;
import jadex.commons.future.ITuple2Future;
import scheduling.EnterGENCO;
import selling.RiskAttitudeForm;
import wholesalemarket_LMP.ProducerInputData_Dynamic;
import wholesalemarket_LMP.Wholesale_InputData;
//import wholesalemarket_LMP.Producer_InputParameters;
import wholesalemarket_LMP.simul.WholesaleMarket;
//Adding imports for OTC João de Sá <-------------------------------------------
//import wholesalemarket_LMP.Pricing_Mechanism_Form;
import wholesalemarket_SMP.SMP_Market_Controller;


//<----------------------------------------------------------------------------
public class PersonalAssistantGUI extends JFrame {
    
    //JFrame Screen_frame = new JFrame("ScreenLayout_Frame");
    
    private Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    private int init_size_x = 1000;
    private int init_size_y = 800;
    private JList<String> list_market = new JList<>();
    private JList<String> list_sellers = new JList<>();
    private JList<String> list_buyers = new JList<>();
    private JScrollPane scroll_sellers = new JScrollPane();
    private DefaultListModel producer_names = new DefaultListModel();
    private DefaultListModel seller_names = new DefaultListModel();
    private DefaultListModel market_names = new DefaultListModel();
    private DefaultListModel aggregators_names = new DefaultListModel();
    private DefaultListModel largeConsumer_names = new DefaultListModel();
    private DefaultListModel mediumConsumer_names = new DefaultListModel();
    private DefaultListModel transCos_names = new DefaultListModel();
    private DefaultListModel buyer_names = new DefaultListModel();
    
    //Added by Filipe Silvério
    private static DecimalFormat df = new DecimalFormat("00");
    private static DecimalFormat df2 = new DecimalFormat("#00.00");
    
    // Adding boolean values that save which type of market simulation will be performed
    // <------------------------------------------------------------------------------
    public Boolean isPool = false;
    public Boolean isOTC = false;
    public Boolean isSMP = false;
    // João de Sá
    
    
    private JTextArea seller_info = new JTextArea("");
    private JTextArea market_info = new JTextArea("Market Operator & \nSystem Operator");
    private JScrollPane scroll_buyers = new JScrollPane();
    private JList<String> list_Aggregators = new JList<>();
    private JList<String> list_LargeConsumers = new JList<>();
    private JList<String> list_MediumConsumers = new JList<>();
    private JList<String> list_TransmCos = new JList<>();
    private JScrollPane scroll_market = new JScrollPane();
    private JScrollPane scroll_Aggregators = new JScrollPane();
    private JScrollPane scroll_LargeConsumer = new JScrollPane();
    private JScrollPane scroll_MediumConsumer = new JScrollPane();
    private JScrollPane scroll_Transm_Dist = new JScrollPane();
    private JTextArea buyer_info = new JTextArea("");
    private JTextArea consumer_info = new JTextArea("");
    private JPanel panel_left = new JPanel(new BorderLayout(0, 10));
    private JPanel panel_leftcenter = new JPanel();
    private JPanel panel_rightcenter = new JPanel();
    private JPanel panel_retailler = new JPanel();

    private JPanel panel_market = new JPanel(new BorderLayout(0, 10));
    private JPanel panel_trader = new JPanel();

    private JPanel panel_LargeConsumer = new JPanel(new BorderLayout(0, 10));
    private JPanel panel_Aggregators = new JPanel(new BorderLayout(0, 10));
    private JPanel panel_MediumConsumer = new JPanel(new BorderLayout(0, 10));
    private JPanel panel_Transm_Dist = new JPanel(new BorderLayout(0, 10));
    private JPanel panel_center = new JPanel();
    private JPanel panel_right = new JPanel(new BorderLayout(0, 10));

    /*private JSplitPane split_pane_sellers = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
     scroll_sellers, seller_info);*/
    private JSplitPane split_pane_buyers = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            scroll_buyers, buyer_info);
    /*private JSplitPane split_pane_trader = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            panel_right, panel_LargeConsumer);*/
    private JSplitPane split_pane_retaillers = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            panel_left, panel_market);
    private JSplitPane split_pane_market = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            scroll_market, market_info);
    private JSplitPane split_pane_consumer = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            scroll_LargeConsumer, consumer_info);
    public JTextArea text_log = new JTextArea("Log");
    private JScrollPane scroll_log = new JScrollPane(text_log);
    //private JMenu menu_agents_new = new JMenu("New");
    private JMenu menu_agents_load = new JMenu("Load");

    private JMenuItem menu_load_producer = new JMenuItem("Generators (GenCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_load_retailer = new JMenuItem("Retailers (RetailCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_load_virtual = new JMenuItem("Transmission Companies (TransCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_load_distribution = new JMenuItem("Distribution Companies (DistCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_load_consumer = new JMenuItem("Consumers", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_load_coalition = new JMenuItem("Coalitions", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_new_producer = new JMenuItem("Generators (GenCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_new_retailer = new JMenuItem("Retailers (RetailCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_new_virtual = new JMenuItem("Transmission Companies (TransCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_new_distribution = new JMenuItem("Distribution Companies (DistCos)", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenuItem menu_new_consumer = new JMenuItem("Consumers", new ImageIcon("images\\agents\\new2.jpg"));
    private JMenu menu_agents_new = new JMenu("New");
    private JMenuItem menu_agents_save = new JMenuItem("Save", new ImageIcon("images\\agents\\save.jpg"));
    private JMenuItem menu_agents_kill = new JMenuItem("Kill", new ImageIcon("images\\agents\\kill.jpg"));
    private JMenuItem exit_menu = new JMenuItem("Exit");
    ImageIcon exit = new ImageIcon("images\\exit2.png");
    ImageIcon tool = new ImageIcon("images\\tool.jpg");
    protected JButton menu_exit = new JButton(exit);
    protected JButton menu_space = new JButton();
    protected JButton menu_tool = new JButton(tool);
    private JMenuItem wholesale_sellers = new JMenuItem("Generators");
    private JMenu pool_trading = new JMenu("Pool Trading");
    
    
    // <--------------------------------------------------------------------------------------------------------------------------------
    // TEMPORARY STUFF!!!!!!!
    
    private JMenuItem choose_Producer = new JMenuItem("Generators");
    private JMenuItem choose_Buyer = new JMenuItem("Retailers");
    
    
    private JMenuItem case_study = new JMenuItem("Case Study");
    private JMenuItem last_simulation = new JMenuItem("Last Simulation");
    private JMenuItem case_study_nowind = new JMenuItem("Case Study no Wind");
    
    
    //<--------------------------------------------------------------------------------------------------------------------------
    
    // adding options for OTC participation on Participants Menu. João de Sá <--
    
    private JMenu OTC_Trading = new JMenu("OTC Trading");
    //private JMenu OTC_pool_Trading = new JMenu("Simultaneous OTC and Pool trading");

    // <------------------------------------------------------------------------
    private JMenuItem wholesale_buyers = new JMenuItem("Retailers");
    private JMenu wholesale_casestudies = new JMenu("Case Studies");
    private JMenuItem wholesale_case1 = new JMenuItem("Case Study 1");
    private JMenuItem wholesale_case2 = new JMenuItem("Case Study 2");
    private JMenuItem wholesale_case3 = new JMenuItem("Case Study 3");
    private JMenu bilateral_trading = new JMenu("Bilateral Trading");
    private JMenu menu_alternatingOffers = new JMenu("Alternating Offers");
    private JMenu menu_contractNet = new JMenu("Contract Net");
    private JMenuItem menu_alternatingOffers_seller = new JMenuItem("Generators");
    private JMenuItem menu_alternatingOffers_buyer = new JMenuItem("Retailers");
    private JMenuItem menu_alternatingOffers_coalitions = new JMenuItem("Large Consumers");
    private JMenuItem menu_alternatingOffers_consumers = new JMenuItem("Consumers");
    private JMenuItem menu_contractNet_seller = new JMenuItem("Sellers");
    private JMenuItem menu_contractNet_buyer = new JMenuItem("Buyers");

    private JMenu menu_action_seller = new JMenu("Seller");
    private JMenuItem seller_personal = new JMenuItem("Personal Info", KeyEvent.VK_P);
    private JMenuItem seller_risk = new JMenuItem("Risk Preference", KeyEvent.VK_P);
    private JMenu menu_action_buyer = new JMenu("Buyer");
    private JMenuItem buyer_personal = new JMenuItem("Personal Info", KeyEvent.VK_P);
    private JMenuItem buyer_risk = new JMenuItem("Risk Preference", KeyEvent.VK_P);
    private JMenuItem buyer_DR = new JMenuItem("DR Management", KeyEvent.VK_P);
    private JMenu wholesale_option = new JMenu("Wholesale");
    private JMenuItem wholesale_simulation = new JMenuItem("LMP", KeyEvent.VK_P);
    private JMenuItem wholesale_defaultCase = new JMenuItem("LMP Ex", KeyEvent.VK_P);
    private JMenu marketPool_simulation = new JMenu("SMP");
    private JMenuItem marketPool_Sym_simulation = new JMenuItem("Symmetrical", KeyEvent.VK_P);
    private JMenuItem marketPool_aSym_simulation = new JMenuItem("Assymmetrical", KeyEvent.VK_P);
    private JMenuItem marketPool_defaultCase = new JMenuItem("Start[SMP Default Case]", KeyEvent.VK_P);
    private JMenuItem wholesale_defaultCase2 = new JMenuItem("Start [Default Case 2]", KeyEvent.VK_P);
    private JMenu bilateral_simulation = new JMenu("Bilateral");
    private JMenuItem menu_action_pairing = new JMenuItem("Start", KeyEvent.VK_P);
    private JMenuItem menu_action_pause = new JMenuItem("Pause", KeyEvent.VK_P);
    private JMenuItem menu_action_stop = new JMenuItem("Stop", KeyEvent.VK_P);
    private JMenu menu_action_pairing_SPOT = new JMenu("Start");
    private JMenuItem menu_action_pause_SPOT = new JMenuItem("Pause", KeyEvent.VK_P);
    private JMenuItem menu_action_stop_SPOT = new JMenuItem("Stop", KeyEvent.VK_P);
    private JMenuItem menu_action_options = new JMenuItem("Options", KeyEvent.VK_P);
    private JMenuItem dayAhead_Option = new JMenuItem("Day-ahead Market");
    //Adding more items for OTC. João de Sá  <----------------------------------
    
    private JMenuItem OTC = new JMenuItem("OTC");
    private JMenuItem OTC_generators = new JMenuItem("Generators");
    private JMenuItem OTC_retailers = new JMenuItem("Retailers");
    private JMenuItem OTC_simul = new JMenuItem("OTC");
    
    

    
    // <------------------------------------------------------------------------
    private JMenuItem intraday_Option = new JMenuItem("Intraday Market");
    private JMenuItem realtime_Option = new JMenuItem("Real-time Market");
    private JMenuItem dayAheadMarket_SMP = new JMenuItem("System Marginal Price");
    private JMenuItem intradayMarket_SMP = new JMenuItem("System Marginal Price");
    private JMenuItem dayAheadMarket_LMP = new JMenuItem("Locational Marginal Price");
    private JMenuItem intradayMarket_LMP = new JMenuItem("Locational Marginal Price");
    private JMenuItem forward_bi_Market = new JMenuItem("Forward Market");
    private JMenuItem futures_bi_Market = new JMenuItem("Futures Market");
    private JMenuItem options_bi_Market = new JMenuItem("Options Market");
    private JMenu energyMarket = new JMenu("Energy Market");
    private JMenu OTCs = new JMenu("Over-the-Counter Contracts");
    private JMenuItem ancillaryService = new JMenuItem("Ancillary-service Market");
    private JMenuItem capacityMarket = new JMenuItem("Capacity Market");
    private JMenuItem transmissionMarket = new JMenuItem("Transmission-rights Market");

    private boolean isPool_LMP = false;
    private boolean isContractNet = false;
    private boolean isPool_SMP = false;
    private boolean isBilateral = false;

    /*private JMenu retail = new JMenu("Retail PersonalAssistant");
     private javax.swing.JCheckBoxMenuItem spot = new javax.swing.JCheckBoxMenuItem("Spot");
     private javax.swing.JCheckBoxMenuItem bilateral = new javax.swing.JCheckBoxMenuItem("Bilateral Contracting");
     private javax.swing.JCheckBoxMenuItem futures = new javax.swing.JCheckBoxMenuItem("Futures");*/
    //private JMenuItem negotiation_periods = new JMenuItem("Time Periods");
    //private JMenuItem negotiation_protocol = new JMenuItem("Trading Protocol");
    //private JMenuItem buyer_deadline = new JMenuItem("Deadline");
    //private JMenuItem seller_deadline = new JMenuItem("Deadline");
    //private JMenuItem contract_type = new JMenuItem("Type and Duration");
//    private JMenuItem negotiation_protocol = new JMenuItem("Trading Protocol");
//    private JMenuItem negotiation_deadline = new JMenuItem("Deadline");
    private JLabel label_image = new JLabel(new ImageIcon("images\\Market_Intelligence.jpg"));
    private JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private Listener listener = new Listener();
    private ListListener list_listener = new ListListener();
    private PersonalAssistantBDI market;
    
    private Wholesale_InputData wholesale = new Wholesale_InputData(market);
    private SMP_Market_Controller marketPool_SMP = new SMP_Market_Controller();
    //private Pricing_Mechanism_Form priceMechanism = new Pricing_Mechanism_Form(this, wholesale, marketPool_SMP);
    
    
    private DayaheadinterfaceBDI dayaheadMarket = new DayaheadinterfaceBDI(this, wholesale, marketPool_SMP);
    private DayaheadinterfaceBDI dayaheadMarketChooseParticipant = new DayaheadinterfaceBDI();
    
    private Offers_Data Offers_Data_Window;

    // Adding variables for OTC João de Sá <------------------------------------
    
    private Participants participants;
    private Results results;
    private OTC_Controller OTC_Controller = new OTC_Controller();
    
    // <------------------------------------------------------------------------
    
    private Color color_top = new Color(0, 204, 0);
    private Color color_center = new Color(0, 102, 204);
    private Color color_south = new Color(204, 102, 0);
    //private Color color_back = new Color(224, 224, 224);
    private Color color_back = Color.white;
    private Color color_buyer = Color.CYAN.darker().darker();
    private Color color_consumer = Color.CYAN.brighter();
    private Color color_largeConsumer = new Color(153, 204, 255);
    private Color color_aggregators = Color.ORANGE;
    private Color color_seller = Color.ORANGE.darker();
    private Color color_market = Color.GREEN.brighter();
    private Font font_1 = new Font("Arial", Font.BOLD, 12);
    private Color color_font_1 = Color.white;
    public  EnterGENCO genco;

    private ProducerInputData_Dynamic window;

    public String sellerNames[] = null;
    private int MARKET_TYPE = 0;
    
    
    public PersonalAssistantGUI(PersonalAssistantBDI market) {
        this.market = market;
        initComponents();
        
        this.setTitle("Multi-agent plataform");
//        this.setPreferredSize(new Dimension(900, 600));
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        //this.setPreferredSize(new Dimension((int) screen_size.getWidth(), (int) screen_size.getHeight()));
        //this.setLocation((int) ((screen_size.getWidth() / 2) - (this.getPreferredSize().getWidth() / 2)), (int) ((screen_size.getHeight() / 2) - (this.getPreferredSize().getHeight() / 2)));

//        this.setLocation((int) (screen_size.getWidth()) , (int) (screen_size.getHeight()));
        //menu_agents_new.setIcon(new ImageIcon("images\\agents\\new2.jpg"));
        menu_agents_load.setIcon(new ImageIcon("images\\agents\\load.jpg"));
        menu_agents_new.setIcon(new ImageIcon("images\\agents\\new2.jpg"));
        text_log.setEditable(false);
        text_log.setText("Market system initiated;\n");
        text_log.setMinimumSize(new Dimension(200, 200));
        label_image.setMinimumSize(new Dimension(0, 0));
        BufferedImage image = resizeImage(new ImageIcon("images\\Market_Intelligence.jpg").getImage(), (int) screen_size.getWidth() - 400, (int) screen_size.getHeight() - 400);
        label_image = new JLabel(new ImageIcon(image));
        split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT, label_image, jSplitPane_weblog);

        JMenuBar menu_bar = new JMenuBar();
        JMenu file_menu = new JMenu("Agents");
        file_menu.setMnemonic(KeyEvent.VK_F);
        menu_bar.add(file_menu);
        JMenu market_menu = new JMenu("Markets");
        market_menu.setMnemonic(KeyEvent.VK_F);
        menu_bar.add(market_menu);
//         JMenu trading_menu = new JMenu("Trading");
////        JMenu menu_action_options = new JMenu("Options");
//        menu_bar.add(trading_menu);
        JMenu action_menu = new JMenu("Participants");
//        JMenu menu_action_options = new JMenu("Options");
        menu_bar.add(action_menu);
        //JMenu negotiation_menu = new JMenu("Trading");
        //menu_bar.add(negotiation_menu);
        //JMenu contract_menu = new JMenu("Contract");
        //menu_bar.add(contract_menu);
        JMenu simulation_menu = new JMenu("Simulation");
        menu_bar.add(simulation_menu);
//        JMenu exit_menu = new JMenu("Exit");
        //###########
        exit_menu.setMaximumSize(new Dimension(7, 25));
        exit_menu.setBackground(Color.WHITE);
        menu_bar.add(exit_menu);
//        menu_exit.setFont(new Font("Arial Black", Font.PLAIN, 12));
//        menu_exit.setSize(exit.getIconHeight(), exit.getIconWidth());

//        menu_exit.setPreferredSize(new Dimension(exit.getIconHeight(), exit.getIconWidth()));
        //###########
        //menu_exit.setMaximumSize(new Dimension(exit.getIconHeight(), exit.getIconWidth()));
//        menu_exit.setFocusPainted(true);
//        menu_exit.setContentAreaFilled(true);
        Border emptyBorder = BorderFactory.createEmptyBorder();
        //###########
        //menu_exit.setBorder(emptyBorder);
        menu_space.setBorder(emptyBorder);
//        menu_exit.setBackground(Color.RED);
//        menu_exit.setForeground(Color.WHITE);
//        menu_exit.setPreferredSize(new Dimension(5, 5));
        menu_bar.add(Box.createGlue());
        menu_bar.add(jLabel_date);
        menu_bar.add(jLabel_clock);
//        menu_bar.add()
        //menu_bar.add(menu_exit);
        menu_bar.setBackground(Color.WHITE);
//        menu_bar.add(menu_space);
        menu_tool.setMaximumSize(new Dimension(tool.getIconHeight(), tool.getIconWidth()));
        menu_tool.setBorder(emptyBorder);

//        menu_bar.setSize(new Dimension(5, 5));
        menu_agents_new.add(menu_new_producer);
        menu_agents_new.add(menu_new_retailer);
        menu_agents_new.add(menu_new_virtual);
        menu_agents_new.add(menu_new_distribution);
        menu_agents_new.add(menu_new_consumer);

        file_menu.add(menu_agents_new);
        menu_agents_load.add(menu_load_producer);
        menu_agents_load.add(menu_load_retailer);
        menu_agents_load.add(menu_load_virtual);
        menu_agents_load.add(menu_load_distribution);
        menu_agents_load.add(menu_load_consumer);
        menu_agents_load.add(menu_load_coalition);
        file_menu.add(menu_agents_load);
        file_menu.add(menu_agents_save);
        file_menu.add(menu_agents_kill);

        list_sellers.setModel(seller_names);
        list_buyers.setModel(buyer_names);
        list_market.setModel(market_names);
        list_Aggregators.setModel(aggregators_names);
        list_LargeConsumers.setModel(largeConsumer_names);
        list_MediumConsumers.setModel(mediumConsumer_names);
        list_TransmCos.setModel(transCos_names);

        
        /**
         * @author Filipe Silvério
         */
        menu_agents_new.setIcon(new ImageIcon("images\\agents\\new2.jpg"));

//        file_menu.setIcon(new ImageIcon("\\personalassistant\\images\\user-52m.png"));
//        market_menu.setIcon(new ImageIcon("\\personalassistant\\images\\graph-sd.png"));
//        action_menu.setIcon(new ImageIcon("\\personalassistant\\images\\collaboration.png"));
//        simulation_menu.setIcon(new ImageIcon("\\personalassistant\\images\\pie-chart.png"));
//        exit_menu.setIcon(new ImageIcon("\\personalassistant\\images\\exit4.png"));
        
        file_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/personalassistant/images/user-52m.png")));
        market_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/personalassistant/images/graph-sd.png")));
        action_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/personalassistant/images/collaboration.png")));
        simulation_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/personalassistant/images/pie-chart.png")));
        exit_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/personalassistant/images/exit4.png")));     
                
        /*spot.setEnabled(false);
         bilateral.setSelected(true);
         futures.setEnabled(false);*/
        exit_menu.addActionListener(listener);

        menu_new_producer.addActionListener(listener);
        menu_new_retailer.addActionListener(listener);
        menu_new_consumer.addActionListener(listener);
        menu_load_producer.addActionListener(listener);
        menu_load_consumer.addActionListener(listener);
        menu_load_coalition.addActionListener(listener);
        menu_load_retailer.addActionListener(listener);

        /*spot.addActionListener(listener);
         bilateral.addActionListener(listener);
         futures.addActionListener(listener);*/
        wholesale_option.addActionListener(listener);
        wholesale_simulation.addActionListener(listener);
        wholesale_defaultCase.addActionListener(listener);
        marketPool_simulation.addActionListener(listener);
        marketPool_Sym_simulation.addActionListener(listener);
        marketPool_aSym_simulation.addActionListener(listener);
//        dayAhead_Option.addActionListener(listener);  Was added twice!!
        // Adding listeners for OTC menu's options. João de Sá <----------------
        
        OTC.addActionListener(listener);
        OTC_generators.addActionListener(listener);
        OTC_retailers.addActionListener(listener);
        OTC_simul.addActionListener(listener);
        
        // <--------------------------------------------------------------------
        intraday_Option.addActionListener(listener);
        realtime_Option.addActionListener(listener);
        marketPool_defaultCase.addActionListener(listener);
        wholesale_defaultCase2.addActionListener(listener);
        menu_action_pairing_SPOT.addActionListener(listener);
        menu_action_pairing.addActionListener(listener);
        buyer_personal.addActionListener(listener);
        buyer_risk.addActionListener(listener);
        buyer_DR.addActionListener(listener);
        seller_personal.addActionListener(listener);
        seller_risk.addActionListener(listener);
        menu_action_options.addActionListener(listener);
//        menu_action_options.add(OPTION_VOLUMES);
        //negotiation_periods.addActionListener(listener);
        //negotiation_protocol.addActionListener(listener);
        //seller_deadline.addActionListener(listener);
        //buyer_deadline.addActionListener(listener);
        //.addActionListener(listener);
//        menu_action_options.addActionListener(listener);
        menu_exit.addActionListener(listener);
        menu_tool.addActionListener(listener);
        list_sellers.addListSelectionListener(list_listener);
        list_buyers.addListSelectionListener(list_listener);
//        System.out.println("TAMANHO: "+screen_size.getHeight());

        dayAheadMarket_SMP.addActionListener(listener);
        dayAheadMarket_LMP.addActionListener(listener);
        intradayMarket_SMP.addActionListener(listener);
        intradayMarket_LMP.addActionListener(listener);
        forward_bi_Market.addActionListener(listener);
        futures_bi_Market.addActionListener(listener);
        options_bi_Market.addActionListener(listener);
        energyMarket.addActionListener(listener);
        OTCs.addActionListener(listener);
        ancillaryService.addActionListener(listener);
        capacityMarket.addActionListener(listener);
        transmissionMarket.addActionListener(listener);
        dayAhead_Option.addActionListener(listener);
        realtime_Option.addActionListener(listener);
        intraday_Option.addActionListener(listener);
        wholesale_sellers.addActionListener(listener);
        wholesale_buyers.addActionListener(listener);
        wholesale_case1.addActionListener(listener);
        wholesale_case2.addActionListener(listener);
        wholesale_case3.addActionListener(listener);
        menu_alternatingOffers_seller.addActionListener(listener);
        menu_alternatingOffers_buyer.addActionListener(listener);
        menu_alternatingOffers_coalitions.addActionListener(listener);
        menu_alternatingOffers_consumers.addActionListener(listener);

        // <--------------------------------------------------------------------------------------------------------------------------------
    // TEMPORARY STUFF!!!!!!!
    
        choose_Producer.addActionListener(listener);
        choose_Buyer.addActionListener(listener);
        action_menu.add(choose_Producer);
        action_menu.add(choose_Buyer);
        choose_Producer.setEnabled(false);
        choose_Buyer.setEnabled(false);
    
    //<--------------------------------------------------------------------------------------------------------------------------
    
        /*retail.add(spot);
         retail.add(bilateral);
         retail.add(futures);*/
        pool_trading.add(wholesale_sellers);
        pool_trading.add(wholesale_buyers);
        pool_trading.add(wholesale_casestudies);
        wholesale_casestudies.add(wholesale_case1);
        wholesale_casestudies.add(wholesale_case2);
        wholesale_casestudies.add(wholesale_case3);
        bilateral_trading.add(menu_alternatingOffers);
        bilateral_trading.add(menu_contractNet);

        menu_alternatingOffers.add(menu_alternatingOffers_seller);
        menu_alternatingOffers.add(menu_alternatingOffers_buyer);
        menu_alternatingOffers.add(menu_alternatingOffers_coalitions);
        menu_alternatingOffers.add(menu_alternatingOffers_consumers);
        //menu_action_seller.add(seller_deadline);
        menu_contractNet.add(menu_contractNet_seller);
        menu_contractNet.add(menu_contractNet_buyer);

        market_menu.add(energyMarket);
        market_menu.add(OTCs);
        market_menu.add(ancillaryService);
        market_menu.add(transmissionMarket);
        market_menu.add(capacityMarket);
        market_menu.add(new JSeparator());
        market_menu.add(forward_bi_Market);
        market_menu.add(futures_bi_Market);
        market_menu.add(options_bi_Market);

        bilateral_trading.setEnabled(false);
        pool_trading.setEnabled(false);
        bilateral_simulation.setEnabled(false);
        wholesale_option.setEnabled(false);
        // Seting OTC options as not enabled before selecting market type. João de Sá
        
        OTC_Trading.setEnabled(false);
        OTC_simul.setEnabled(false);

        // <--------------------------------------------------------------------
        //market_menu.add(retail);
//        trading_menu.add(menu_action_options);
        action_menu.add(pool_trading);
        // Adding OTC options to action menu. João de Sá <----------------------
        
        action_menu.add(OTC_Trading);
        OTC_Trading.add(OTC_generators);
        OTC_Trading.add(OTC_retailers);
        


        
        // <--------------------------------------------------------------------
        action_menu.add(bilateral_trading);
        //action_menu.add(menu_action_seller);
        //action_menu.add(menu_action_buyer);
        //negotiation_menu.add(negotiation_periods);
        //negotiation_menu.add(negotiation_protocol);
//        negotiation_menu.add(negotiation_deadline);
        //contract_menu.add(contract_type);
        wholesale_option.add(menu_action_pairing_SPOT);
        wholesale_option.add(menu_action_pause_SPOT);
        wholesale_option.add(menu_action_stop_SPOT);

        menu_action_pairing_SPOT.add(marketPool_simulation);
        menu_action_pairing_SPOT.add(wholesale_simulation);
        marketPool_simulation.add(marketPool_Sym_simulation);
        marketPool_simulation.add(marketPool_aSym_simulation);
        //menu_action_pairing_SPOT.add(wholesale_defaultCase);

        /*wholesale_option.add(wholesale_simulation);
         wholesale_option.add(wholesale_defaultCase);
         wholesale_option.add(marketPool_simulation);*/
        energyMarket.add(dayAhead_Option);
        energyMarket.add(intraday_Option);
        energyMarket.add(realtime_Option);
        //wholesale_option.add(marketPool_defaultCase);
        //dayAhead_Option.add(dayAheadMarket_SMP);
        //intraday_Option.add(intradayMarket_SMP);
        //dayAhead_Option.add(dayAheadMarket_LMP);
        //intraday_Option.add(intradayMarket_LMP);

        // Adding menu options to OTC. João de Sá <-----------------------------
        
        OTCs.add(OTC);
        
        
        
        // <--------------------------------------------------------------------
        
        //wholesale_option.add(wholesale_defaultCase2);
        bilateral_simulation.add(menu_action_pairing);
        bilateral_simulation.add(menu_action_pause);
        bilateral_simulation.add(menu_action_stop);
        simulation_menu.add(wholesale_option);
        simulation_menu.add(bilateral_simulation);
        // Adding simulation options for OTC. João de Sá <----------------------
        
        simulation_menu.add(OTC_simul);
        
        simulation_menu.add(case_study);
        simulation_menu.add(case_study_nowind);
        
        simulation_menu.add(last_simulation);
        
        case_study.addActionListener(listener);
        case_study_nowind.addActionListener(listener);
        last_simulation.addActionListener(listener);
        // <--------------------------------------------------------------------
        /*simulation_menu.add(menu_action_pairing);
         simulation_menu.add(menu_action_pause);
         simulation_menu.add(menu_action_stop);*/

        panel_left.setPreferredSize(new Dimension(180, (int) screen_size.getHeight() / 3 - 20));
        panel_market.setPreferredSize(new Dimension(180, (int) screen_size.getHeight() / 3 - 20));
        panel_Transm_Dist.setPreferredSize(new Dimension(180, (int) screen_size.getHeight() / 3 - 20));
//        panel_retailler.setPreferredSize(new Dimension(180, 0));

        panel_right.setPreferredSize(new Dimension(180, (int) screen_size.getHeight() / 3 - 20));
        panel_Aggregators.setPreferredSize(new Dimension(180, (int) screen_size.getHeight() / 3 - 20));
        panel_LargeConsumer.setPreferredSize(new Dimension(180, (int) screen_size.getHeight() / 3 - 20));
        panel_MediumConsumer.setPreferredSize(new Dimension(180, (int) screen_size.getHeight() / 3 - 20));
//        panel_leftcenter.setLayout(new GridBagLayout());
        panel_leftcenter.setPreferredSize(new Dimension(5, 5));
        panel_rightcenter.setPreferredSize(new Dimension(5, 5));
        panel_center.setPreferredSize(new Dimension(init_size_x - 400, init_size_y - 72));

        panel_left.setBackground(color_seller);
        panel_market.setBackground(color_center);
        panel_Aggregators.setBackground(color_aggregators);
        panel_LargeConsumer.setBackground(color_consumer);
        panel_MediumConsumer.setBackground(color_market);
        panel_Transm_Dist.setBackground(color_south);
//        panel_retailler.setBackground(color_seller);
        panel_center.setBackground(color_back);
        panel_retailler.setBackground(color_back);
        panel_trader.setBackground(color_back);
        panel_right.setBackground(color_buyer);

//        getContentPane().add(panel_left, BorderLayout.WEST);
        getContentPane().add(panel_retailler, BorderLayout.WEST);
        getContentPane().add(panel_center, BorderLayout.CENTER);
//        getContentPane().add(panel_right, BorderLayout.EAST);
        getContentPane().add(panel_trader, BorderLayout.EAST);

//panel_retailler.setLayout(new FlowLayout());
        panel_center.setLayout(new FlowLayout());
        seller_info.setPreferredSize(new Dimension(100, 100));
        scroll_sellers.setPreferredSize(new Dimension(100, 100));
        scroll_buyers.setPreferredSize(new Dimension(100, 100));
        scroll_market.setPreferredSize(new Dimension(100, 100));
        scroll_Aggregators.setPreferredSize(new Dimension(100, 100));
        scroll_LargeConsumer.setPreferredSize(new Dimension(100, 100));
        scroll_MediumConsumer.setPreferredSize(new Dimension(100, 100));
        scroll_Transm_Dist.setPreferredSize(new Dimension(100, 100));
        buyer_info.setPreferredSize(new Dimension(100, 100));
        consumer_info.setPreferredSize(new Dimension(100, 100));

        split_pane_buyers.setOneTouchExpandable(true);
        split_pane_buyers.setDividerLocation(100);
        split_pane_buyers.setPreferredSize(new Dimension(170, 200));
        //split_pane_trader.setOneTouchExpandable(true);
        //split_pane_trader.setDividerLocation((int) screen_size.getHeight() / 2 - 50);
        //split_pane_trader.setPreferredSize(new Dimension(170, 200));
        split_pane_retaillers.setOneTouchExpandable(true);
        split_pane_retaillers.setDividerLocation((int) screen_size.getHeight() / 2 - 50);
        split_pane_retaillers.setPreferredSize(new Dimension(5, 5));
        /*split_pane_sellers.setDividerLocation(100);
         split_pane_sellers.setPreferredSize(new Dimension(170, 200));
         split_pane_sellers.setOneTouchExpandable(true);
         split_pane_sellers.setDividerLocation(100);
         split_pane_sellers.setPreferredSize(new Dimension(170, 200));*/
        split_pane_market.setOneTouchExpandable(true);
        split_pane_market.setDividerLocation(100);
        split_pane_market.setPreferredSize(new Dimension(170, 200));
        split_pane_consumer.setOneTouchExpandable(true);
        split_pane_consumer.setDividerLocation(100);
        split_pane_consumer.setPreferredSize(new Dimension(170, 200));

        // Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        scroll_buyers.setMinimumSize(minimumSize);
        scroll_sellers.setMinimumSize(minimumSize);
        scroll_market.setMinimumSize(minimumSize);
        scroll_Aggregators.setMinimumSize(minimumSize);
        scroll_LargeConsumer.setMinimumSize(minimumSize);
        scroll_MediumConsumer.setMinimumSize(minimumSize);
        scroll_Transm_Dist.setMinimumSize(minimumSize);

        JLabel label_buyers_title_top = new JLabel("Retailers (RetailCos)", SwingConstants.CENTER);
        label_buyers_title_top.setForeground(color_font_1);
        label_buyers_title_top.setFont(font_1);
        label_buyers_title_top.setPreferredSize(new Dimension(label_buyers_title_top.getText().length() * 7, 13));
        JLabel label_buyers_title_south = new JLabel("Retailer's Info", SwingConstants.CENTER);
        label_buyers_title_south.setForeground(color_font_1);
        label_buyers_title_south.setFont(font_1);
        label_buyers_title_south.setPreferredSize(new Dimension(label_buyers_title_south.getText().length() * 7, 13));

        panel_right.add(label_buyers_title_top, BorderLayout.NORTH);
        panel_right.add(scroll_buyers, BorderLayout.CENTER);
        //panel_right.add(split_pane_buyers, BorderLayout.CENTER);
        //panel_right.add(label_buyers_title_south, BorderLayout.SOUTH);

        JLabel label_sellers_title_top = new JLabel("Generating Companies", SwingConstants.CENTER);
        label_sellers_title_top.setForeground(color_font_1);
        label_sellers_title_top.setFont(font_1);
        label_sellers_title_top.setPreferredSize(new Dimension(label_sellers_title_top.getText().length() * 7, 13));
        JLabel label_sellers_title_south = new JLabel("Generator's Info", SwingConstants.CENTER);
        label_sellers_title_south.setForeground(color_font_1);
        label_sellers_title_south.setFont(font_1);
        label_sellers_title_south.setPreferredSize(new Dimension(label_sellers_title_south.getText().length() * 7, 13));

        JLabel label_market_title_top = new JLabel("Trading Coordination", SwingConstants.CENTER);
        label_market_title_top.setForeground(color_font_1);
        label_market_title_top.setFont(font_1);
        label_market_title_top.setPreferredSize(new Dimension(label_market_title_top.getText().length() * 7, 13));
        JLabel label_market_title_south = new JLabel("Operator's Info", SwingConstants.CENTER);
        label_market_title_south.setForeground(color_font_1);
        label_market_title_south.setFont(font_1);
        label_market_title_south.setPreferredSize(new Dimension(label_market_title_south.getText().length() * 6, 13));

        JLabel label_Aggregators_title_top = new JLabel("Aggregators", SwingConstants.CENTER);
        label_Aggregators_title_top.setForeground(color_font_1);
        label_Aggregators_title_top.setFont(font_1);
        label_Aggregators_title_top.setPreferredSize(new Dimension(label_Aggregators_title_top.getText().length() * 8, 13));
        
        JLabel label_Largeconsumer_title_top = new JLabel("Large Consumers", SwingConstants.CENTER);
        label_Largeconsumer_title_top.setForeground(color_font_1);
        label_Largeconsumer_title_top.setFont(font_1);
        label_Largeconsumer_title_top.setPreferredSize(new Dimension(label_Largeconsumer_title_top.getText().length() * 8, 13));
        JLabel label_Largeconsumer_title_south = new JLabel("Consumer's Info", SwingConstants.CENTER);
        label_Largeconsumer_title_south.setForeground(color_font_1);
        label_Largeconsumer_title_south.setFont(font_1);
        label_Largeconsumer_title_south.setPreferredSize(new Dimension(label_Largeconsumer_title_south.getText().length() * 7, 13));

        JLabel label_MediumConsumer_title_top = new JLabel("Small Consumers", SwingConstants.CENTER);
        label_MediumConsumer_title_top.setForeground(color_font_1);
        label_MediumConsumer_title_top.setFont(font_1);
        label_MediumConsumer_title_top.setPreferredSize(new Dimension(label_MediumConsumer_title_top.getText().length() * 8, 13));
        JLabel label_MediumConsumer_title_south = new JLabel("Consumer's Info", SwingConstants.CENTER);
        label_MediumConsumer_title_south.setForeground(color_font_1);
        label_MediumConsumer_title_south.setFont(font_1);
        label_MediumConsumer_title_south.setPreferredSize(new Dimension(label_MediumConsumer_title_south.getText().length() * 7, 13));

        JLabel label_TransCos = new JLabel("TransCos and DistCos", SwingConstants.CENTER);
        label_TransCos.setForeground(color_font_1);
        label_TransCos.setFont(font_1);
        label_TransCos.setPreferredSize(new Dimension(label_TransCos.getText().length() * 8, 13));
        JLabel label_TransCos_title_south = new JLabel("Consumer's Info", SwingConstants.CENTER);
        label_TransCos_title_south.setForeground(color_font_1);
        label_TransCos_title_south.setFont(font_1);
        label_TransCos_title_south.setPreferredSize(new Dimension(label_TransCos_title_south.getText().length() * 7, 13));

        panel_left.add(label_sellers_title_top, BorderLayout.NORTH);
        panel_left.add(scroll_sellers, BorderLayout.CENTER);
//        panel_left.add(split_pane_sellers, BorderLayout.CENTER);
        //panel_left.add(label_sellers_title_south, BorderLayout.SOUTH);

        panel_market.add(label_market_title_top, BorderLayout.NORTH);
        panel_market.add(scroll_market, BorderLayout.CENTER);
        //panel_market.add(split_pane_market, BorderLayout.CENTER);
        //panel_market.add(label_market_title_south, BorderLayout.SOUTH);

        panel_Aggregators.add(label_Aggregators_title_top, BorderLayout.NORTH);
        panel_Aggregators.add(scroll_Aggregators, BorderLayout.CENTER);
        
        panel_LargeConsumer.add(label_Largeconsumer_title_top, BorderLayout.NORTH);
        panel_LargeConsumer.add(scroll_LargeConsumer, BorderLayout.CENTER);
        //panel_consumer.add(split_pane_consumer, BorderLayout.CENTER);
        //panel_consumer.add(label_consumer_title_south, BorderLayout.SOUTH);

        panel_MediumConsumer.add(label_MediumConsumer_title_top, BorderLayout.NORTH);
        panel_MediumConsumer.add(scroll_MediumConsumer, BorderLayout.CENTER);

        panel_Transm_Dist.add(label_TransCos, BorderLayout.NORTH);
        panel_Transm_Dist.add(scroll_Transm_Dist, BorderLayout.CENTER);

        //panel_leftcenter.add(split_pane_retaillers, BorderLayout.CENTER);
//        String a="PersonalAssistant Operator"; 
        market_names.addElement("Market Operator");
        market_names.addElement("System Operator");
        scroll_market.setViewportView(list_market);
        list_market.setSelectedIndex(0);

        //consumer_names.addElement("Tom_Britton");
        //consumer_names.addElement("David_Owen");
        scroll_Aggregators.setViewportView(list_Aggregators);
        scroll_LargeConsumer.setViewportView(list_LargeConsumers);
        scroll_MediumConsumer.setViewportView(list_MediumConsumers);
        scroll_Transm_Dist.setViewportView(list_TransmCos);
//        list_consumer.setSelectedIndex(0);

        panel_retailler.add(panel_left, BorderLayout.NORTH);
        panel_retailler.add(panel_market, BorderLayout.CENTER);
        panel_retailler.add(panel_Transm_Dist, BorderLayout.SOUTH);

        panel_trader.add(panel_right);
        panel_trader.add(panel_Aggregators);
        panel_trader.add(panel_LargeConsumer);
        panel_trader.add(panel_MediumConsumer);

        //panel_trader.add(panel_rightcenter, BorderLayout.CENTER);
        //panel_trader.add(panel_consumer, BorderLayout.SOUTH);
        split_pane_log_image.setPreferredSize(new Dimension(100, 100));
        panel_center.add(split_pane_log_image);

        this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {

            @Override
            public void ancestorMoved(HierarchyEvent e) {
                //do nothing
            }

            @Override
            public void ancestorResized(HierarchyEvent e) {
                //scroll_sellers.setPreferredSize(new Dimension(scroll_sellers.getPreferredSize().width, (int) screen_size.getHeight() / 5));

                scroll_sellers.setPreferredSize(new Dimension(scroll_sellers.getPreferredSize().width, getHeight() / 4));

                scroll_market.setPreferredSize(new Dimension(scroll_market.getPreferredSize().width, (int) screen_size.getHeight() / 5));
                scroll_Aggregators.setPreferredSize(new Dimension(scroll_Aggregators.getPreferredSize().width, (int) screen_size.getHeight() / 4));
                scroll_LargeConsumer.setPreferredSize(new Dimension(scroll_LargeConsumer.getPreferredSize().width, (int) screen_size.getHeight() / 4));
                scroll_MediumConsumer.setPreferredSize(new Dimension(scroll_MediumConsumer.getPreferredSize().width, (int) screen_size.getHeight() / 4));
                scroll_buyers.setPreferredSize(new Dimension(scroll_buyers.getPreferredSize().width, (int) screen_size.getHeight() / 4));
                scroll_Transm_Dist.setPreferredSize(new Dimension(scroll_Transm_Dist.getPreferredSize().width, (int) screen_size.getHeight() / 4));

                split_pane_buyers.setPreferredSize(new Dimension(split_pane_buyers.getPreferredSize().width, (int) screen_size.getHeight() / 3 + 40));
                //split_pane_sellers.setPreferredSize(new Dimension(split_pane_sellers.getPreferredSize().width, (int) screen_size.getHeight() / 3 + 40));
                split_pane_market.setPreferredSize(new Dimension(split_pane_market.getPreferredSize().width, (int) screen_size.getHeight() / 3 + 40));
                split_pane_consumer.setPreferredSize(new Dimension(split_pane_consumer.getPreferredSize().width, (int) screen_size.getHeight() / 3 + 40));
                split_pane_retaillers.setPreferredSize(new Dimension(split_pane_retaillers.getPreferredSize().width, split_pane_retaillers.getPreferredSize().height));
                //split_pane_trader.setPreferredSize(new Dimension(split_pane_trader.getPreferredSize().width, split_pane_trader.getPreferredSize().height));
                split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 400, getHeight() - 72));

                split_pane_buyers.setDividerLocation(0.7);
                //split_pane_sellers.setDividerLocation(0.7);
                split_pane_market.setDividerLocation(0.7);
                split_pane_consumer.setDividerLocation(0.7);
                split_pane_retaillers.setDividerLocation(0.5);
                //split_pane_trader.setDividerLocation(0.5);
                split_pane_log_image.setDividerLocation(0.5);
                split_pane_log_image.setEnabled(false);
                
            }
        });

        this.setJMenuBar(menu_bar);
        this.pack();
        this.setVisible(true);
        this.setMinimumSize(new Dimension(600, 200));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        clock();
        String[] dateArray = new String[3];
        DateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
        Date today = Calendar.getInstance().getTime();
        String reportDate = dateformat.format(today);
        jLabel_date.setText(reportDate+"   ");
    }
    
    public void InformMarketSelection(boolean isDayahead, boolean isSMP, boolean isOTC){
        
//        market.sendMarketSelection(isDayahead, isSMP, isOTC);
        
    }

    void addAgent(String localName, String type) {
        if (type.equals("seller")) {
            text_log.append(localName + " (seller) has connected;\n");
        } else if (type.equals("producer")) {
            //producer_names.addElement(localName);
            seller_names.addElement(localName);
            text_log.append(localName + " (producer) has connected;\n");
        } else if (type.equals("buyer")) {
            buyer_names.addElement(localName);
            text_log.append(localName + " (buyer) has connected;\n");
        } else if (type.equals("large_consumer")) {
            largeConsumer_names.addElement(localName);
            text_log.append(localName + " (large consumer) has connected;\n");
        } else if (type.equals("coalition")) {
            largeConsumer_names.addElement(localName);
            text_log.append(localName + " (coalition) has connected;\n");
        }else if (type.equals("consumer")) {
            mediumConsumer_names.addElement(localName);
            text_log.append(localName + " (small/medium consumer) has connected;\n");
        }
        repaint();
    }

    public DefaultListModel getSeller_names() {
        return seller_names;
    }
    
     public DefaultListModel getProducer_names() {
        return producer_names;
    }

    public DefaultListModel getBuyer_names() {
        return buyer_names;
    }

    public DefaultListModel getLargeConsumer_names() {
        return largeConsumer_names;
    }

    public DefaultListModel getMediumConsumer_names() {
        return mediumConsumer_names;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (list_sellers.getSelectedIndex() == -1) {
            list_sellers.setSelectedIndex(0);
        }

        if (list_buyers.getSelectedIndex() == -1) {
            list_buyers.setSelectedIndex(0);
        }

        scroll_sellers.setViewportView(list_sellers);
        scroll_buyers.setViewportView(list_buyers);

        seller_info.repaint();
        buyer_info.repaint();

        text_log.repaint();
    }

    public void setMarketOptionsAvailable(boolean _isPoolTrading, boolean _isLMP, boolean _isContractNet) {
        isPool_LMP = false;
        isContractNet = _isContractNet;
        isPool_SMP = false;
        isBilateral = false;
        setMarketSimulAvailable(_isPoolTrading, _isLMP);
        if (_isPoolTrading) {
            bilateral_trading.setEnabled(false);
            pool_trading.setEnabled(true);
            if (_isLMP) {
                isPool_LMP = true;
            } else {
                isPool_SMP = true;
            }
        } else {
            bilateral_trading.setEnabled(true);
            pool_trading.setEnabled(false);
            if (isContractNet) {
                menu_contractNet.setEnabled(true);
                menu_alternatingOffers.setEnabled(false);
                bilateral_simulation.setEnabled(false);
            } else {
                menu_contractNet.setEnabled(false);
                menu_alternatingOffers.setEnabled(true);
                bilateral_simulation.setEnabled(true);
            }
        }
    }

    public boolean getIsPool_LMP() {
        return isPool_LMP;
    }

    public void setIsPool_LMP(boolean isPool_LMP) {
        this.isPool_LMP = isPool_LMP;
    }

    public boolean getIsPool_SMP() {
        return isPool_SMP;
    }

    public void setIsPool_SMP(boolean isPool_SMP) {
        this.isPool_SMP = isPool_SMP;
    }

    public boolean getIsBilateral() {
        return isBilateral;
    }

    public void setIsBilateral(boolean isBilateral) {
        this.isBilateral = isBilateral;
    }

    public void setMarketSimulAvailable(boolean _isPoolTrading, boolean _isLMP) {
        if (_isPoolTrading) {
            wholesale_option.setEnabled(true);
            bilateral_simulation.setEnabled(false);
            if (_isLMP) {

                wholesale_simulation.setEnabled(true);
                marketPool_simulation.setEnabled(false);
            } else {
                wholesale_simulation.setEnabled(false);
                marketPool_simulation.setEnabled(true);
            }
        } else {
            if ((market.getBilateral_contractType() == 1)) {
                bilateral_simulation.setEnabled(true);
                wholesale_option.setEnabled(false);
            } else {
                bilateral_simulation.setEnabled(false);
                wholesale_option.setEnabled(false);
            }
        }
    }

    void show_offer_window(int k, boolean isProducer) {
        
        this.Offers_Data_Window = new Offers_Data(this.market, isProducer, k);
        this.Offers_Data_Window.setVisible(true);
        
        
    }

    private class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

//            if (e.getSource().equals(list_sellers)) {
//                AID seller_selected = new AID(list_sellers.getSelectedValue(), AID.ISLOCALNAME);
//                seller_info.setText("Name: " + market.searchPartialBelief(seller_selected.getLocalName(), "name").split(";")[1].split("_")[1] + "\n");
//                seller_info.append("Address: " + market.searchPartialBelief(seller_selected.getLocalName(), "address").split(";")[1].split("_")[1] + "\n");
//                seller_info.append("Telephone: " + market.searchPartialBelief(seller_selected.getLocalName(), "telephone").split(";")[1].split("_")[1] + "\n");
//                //seller_info.append("Fax: " + market.searchPartialBelief(seller_selected.getLocalName(), "fax").split(";")[1].split("_")[1] + "\n");
//                seller_info.append("E-mail: " + market.searchPartialBelief(seller_selected.getLocalName(), "email").split(";")[1].split("_")[1] + "\n");
//                repaint();
//            } else if (e.getSource().equals(list_buyers)) {
//                AID buyer_selected = new AID(list_buyers.getSelectedValue(), AID.ISLOCALNAME);
//                buyer_info.setText("Name: " + market.searchPartialBelief(buyer_selected.getLocalName(), "name").split(";")[1].split("_")[1] + "\n");
//                buyer_info.append("Address: " + market.searchPartialBelief(buyer_selected.getLocalName(), "address").split(";")[1].split("_")[1] + "\n");
//                buyer_info.append("Telephone: " + market.searchPartialBelief(buyer_selected.getLocalName(), "telephone").split(";")[1].split("_")[1] + "\n");
//                //buyer_info.append("Fax: " + market.searchPartialBelief(buyer_selected.getLocalName(), "fax").split(";")[1].split("_")[1] + "\n");
//                buyer_info.append("E-mail: " + market.searchPartialBelief(buyer_selected.getLocalName(), "email").split(";")[1].split("_")[1] + "\n");
//                repaint();
//            }
        }
    }

    private class Listener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(menu_exit) || e.getSource().equals(exit_menu)) {
                //String[] seller_name = (market.getProducerNames().toString().split(","));
                //System.out.println( seller_name[0].split("name ")[1].split("@")[0]);
                //System.out.println( seller_name[1].split("name ")[1].split("@")[0]);

                System.exit(0);
            } else if (e.getSource().equals(menu_tool)) {
                market.tools(null);
            } else if (e.getSource().equals(seller_risk)) {
                market.seller_risk(null);
            } else if (e.getSource().equals(menu_new_producer)) {
                market.newAgentForm(1);
            } else if (e.getSource().equals(menu_new_retailer)) {
                market.newAgentForm(2);
            } else if (e.getSource().equals(menu_load_producer)) {
                market.agent_type="producer";
                market.producer_menu(null);
//                market.genco_menu(null);
//                genco = new EnterGENCO();
//                genco.setVisible(true);
//                market.createAgent(genco.getName(),"producing.Producer");
            } else if (e.getSource().equals(menu_load_retailer)) {
                market.agent_type="buyer";
                market.buyer_menu(null);
            } else if (e.getSource().equals(menu_load_consumer)) {
                market.agent_type="consumer";
                market.consumer_menu(null);
            }else if (e.getSource().equals(menu_load_coalition)) {
                market.agent_type="coalition";
                market.coallition_menu(null);
            }else if (e.getSource().equals(buyer_risk)) {
                market.buyer_risk(null);
            } else if (e.getSource().equals(buyer_DR)) {
                market.demandmanagement(null);
            } else if (e.getSource().equals(buyer_personal)) {

            } else if (e.getSource().equals(seller_personal)) {

            } else if (e.getSource().equals(dayAhead_Option)) {
                
                isPool = true;
                isOTC = false;
                
                //<----------------------------------------------------------------------------------------------------
                // TEMPORARIO!!!!
                
                choose_Producer.setEnabled(true);
                choose_Buyer.setEnabled(true);
                
                
                
                // When dayAhead is chosen, OTC trading is disabled. João de Sá
                OTC_Trading.setEnabled(false);
             
                dayaheadMarket.pricingMechanismForm("DayAheadMarket");
                
                //priceMechanism.setVisible(true);
                //priceMechanism.editWindow_options(true);
                SMP_Market_Controller.START_HOUR = 0;
                SMP_Market_Controller.END_HOUR = 23;
                WholesaleMarket.START_HOUR = 0;
                WholesaleMarket.END_HOUR = 23;
                WholesaleMarket.HOUR_PER_DAY = 24;
                
                
            // Adding listeners for OTC. João de Sá <-------------------------------    
            } else if (e.getSource().equals(OTC)){
                
                
                //<----------------------------------------------------------------------------------------------------
                // TEMPORARIO!!!!
                
                choose_Producer.setEnabled(true);
                choose_Buyer.setEnabled(true);
                
                // -----------------------------------------------------------------------------------------------------
                
                isOTC = true;
                isPool = false;
                
                InformMarketSelection(false, false, true);

                OTC_Trading.setEnabled(true);
                pool_trading.setEnabled(false);
                
            } else if (e.getSource().equals(OTC_generators)){
                try {
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();

                    if (sellerNumb > 0 && buyerNumb > 0) {
                        OTC_Controller.start_InputData(market, true, seller_names, buyer_names);
                        OTC_simul.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                }
                                
            } else if (e.getSource().equals(OTC_retailers)){
                try {
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();

                    if (sellerNumb > 0 && buyerNumb > 0) {
                        OTC_Controller.start_InputData(market, false, seller_names, buyer_names);
                        OTC_simul.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                }




// <----------------------------------------------------------------------------------------------------------------------------
// TEMPORARIO!!!!!                
            } else if (e.getSource().equals(choose_Producer)){
                try {
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();

                    if (sellerNumb > 0 && buyerNumb > 0) {
                        System.out.println("VAI ESCOLHER O PRODUTOR A PARTICIPAR NO MERCADO!!");
                        //market.chooseParticipants(true, isPool, isSMP, isOTC);
                        dayaheadMarketChooseParticipant.chooseParticipants(market, true, isPool, isSMP, isOTC);
                    } else {
                        JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                }
                
            } else if (e.getSource().equals(case_study)){
                
                marketPool_SMP.casetudy(true, market);
                
            } else if (e.getSource().equals(case_study_nowind)){  
                
                marketPool_SMP.casetudy(false, market);
                
            }
            else if(e.getSource().equals(last_simulation))
            {
            	
   		     Connection conn = null; 
		      Statement stmt = null;
		      String resultSetString = null;
		      try {
		         Class.forName("org.h2.Driver");
		         conn = DriverManager.getConnection("jdbc:h2:file:D:\\Work\\eclipse\\workspace-fsilverio\\git\\MANREMv5.0.git\\MANREM_V5_0\\database\\h2db","root","root");
		         stmt = conn.createStatement(
                       ResultSet.TYPE_SCROLL_INSENSITIVE,
                       ResultSet.CONCUR_UPDATABLE);
		         String sql = "SELECT sim.RESULTS from SIMULATIONS_DATA sim order by DATE asc";
		         ResultSet rs = stmt.executeQuery(sql);
		         while(rs.next()) {
		        	 resultSetString = rs.getString("RESULTS");
		          }
		         rs.close();
		      } catch(SQLException se) {
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter os dados da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);

		         se.printStackTrace();
		      } catch(Exception ex) { 
		    	  JOptionPane.showMessageDialog(null, "Não foi possível obter os dados da ultima simulação.", "INFO", JOptionPane.INFORMATION_MESSAGE);
		         ex.printStackTrace(); 
		      } finally { 
		         try { 
		            if(stmt!=null) stmt.close();  
		         } catch(SQLException se2) { 
		         }
		         try { 
		            if(conn!=null) conn.close(); 
		         } catch(SQLException se) { 
		            se.printStackTrace(); 
		         }
		      }
		      
		      
	    	  JOptionPane.showMessageDialog(null, resultSetString, "INFO", JOptionPane.INFORMATION_MESSAGE);

//        		market.Store_and_send_SMP_results(resultSetString);
        		
        	}
            else if (e.getSource().equals(choose_Buyer)){
                try {
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();

                    if (sellerNumb > 0 && buyerNumb > 0) {
                        System.out.println("VAI ESCOLHER RETALHISTA A PARTICIPAR NO MERCADO!!");
                        //market.chooseParticipants(false, isPool, isSMP, isOTC);
                        dayaheadMarketChooseParticipant.chooseParticipants(market, false, isPool, isSMP, isOTC);
                    } else {
                        JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                }
                

// <----------------------------------------------------------------------------------------------------------------------------

               
            } else if (e.getSource().equals(OTC_simul)){
              
//                market.createAgent("OTC_Market_Controller", "OTC.OTC_Market_Operator");
//                market.start_OTC_simul("OTC_Market_Controller");
                try{
                    String[] message;
                    for(int i = 0; i < seller_names.getSize(); i++){
                        message = OTC_Controller.Message_generator(true, i);
//                        market.send_OTC_sim_data("OTC_Market_Controller", message);
                    }
                
                    for(int i = 0; i < buyer_names.getSize(); i++){
                        message = OTC_Controller.Message_generator(false, i);
//                        market.send_OTC_sim_data("OTC_Market_Controller", message);
                    }
                    
//                    market.start_OTC_simul("OTC_Market_Controller");
                }    
                catch(Exception ex){
                    
                    JOptionPane.showMessageDialog(null, "One or more of the participants are missing!!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                        
                }
           
            } else if (e.getSource().equals(intraday_Option)) {
                dayaheadMarket.pricingMechanismForm("IntraDayMarket");
                //priceMechanism.setVisible(true);
                //priceMechanism.editWindow_options(false);
            } else if (e.getSource().equals(wholesale_sellers)) {
                try {
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();

                    if (sellerNumb > 0 && buyerNumb > 0) {
                        if (isPool_SMP) {
                            marketPool_SMP.start_InputData(market, true, seller_names, buyer_names);
                        } else if (isPool_LMP) {
                            wholesale.setProducerAgentName(seller_names);
                            wholesale.setSupplierAgentName(buyer_names);
                            //Producer_InputParameters lmp_window = new Producer_InputParameters(wholesale);
                            wholesale.activeFrame_PRODUCER();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                }

            } else if (e.getSource().equals(wholesale_buyers)) {
                try {
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();

                    if (sellerNumb > 0 && buyerNumb > 0) {
                        if (isPool_SMP) {                            
                            marketPool_SMP.start_InputData(market,false, seller_names, buyer_names);
                        } else if (isPool_LMP) {
                            wholesale.setProducerAgentName(seller_names);
                            wholesale.setSupplierAgentName(buyer_names);
                            wholesale.activeFrame_SUPPLIER();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                }
            } else if (e.getSource().equals(wholesale_case1)) {
                   try {
            marketPool_SMP.SMPCaseStudy("Case Study 1");
        } catch (Exception ex) {
       
        }
            } else if (e.getSource().equals(wholesale_case2)) {
                try {
           

                } catch (Exception ex) {
                }
            } else if (e.getSource().equals(wholesale_case3)) {
                try {
    
                } catch (Exception ex) {
                }
            } else if (e.getSource().equals(intradayMarket_LMP)) {
                try {
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();

                    if (sellerNumb > 0 && buyerNumb > 0) {
                        wholesale.setProducerAgentName(seller_names);
                        wholesale.setSupplierAgentName(buyer_names);
                        wholesale.activeFrame_GRID(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "The number of sellers and buyers must be greater than zero!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "ERRO No MarketGui");
                }
            } else if (e.getSource().equals(menu_alternatingOffers_seller)) {
                try{
                    int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize()+largeConsumer_names.getSize()+mediumConsumer_names.getSize();
//
                    if (sellerNumb > 0 && buyerNumb > 0) {
                        wholesale.setProducerAgentName(seller_names);
//                        wholesale.setSupplierAgentName(buyer_names);
                        RiskAttitudeForm newWindow = new RiskAttitudeForm(wholesale, 0);
                        newWindow.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Option not available! Please insert agents!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Option not available! Please contact system administrator!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource().equals(menu_alternatingOffers_buyer)) {
//                System.out.println("Entrou no Buyer");
                try{
                     int sellerNumb = seller_names.getSize();
                    int buyerNumb = buyer_names.getSize();
//
                    if (sellerNumb > 0 && buyerNumb > 0) {
//                        wholesale.setProducerAgentName(seller_names);
                        list_MediumConsumers.setSelectedIndex(-1);
                        list_LargeConsumers.setSelectedIndex(-1);
                        wholesale.setSupplierAgentName(buyer_names);
                        RiskAttitudeForm newWindow = new RiskAttitudeForm(wholesale, 2);
                        newWindow.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Option not available! Please insert agents!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Option not available! Please contact system administrator!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                }
               } else if (e.getSource().equals(menu_alternatingOffers_coalitions)) {
                       try{
                                                int sellerNumb = seller_names.getSize();
               int buyerNumb = largeConsumer_names.getSize();
               
                    if (sellerNumb > 0 && buyerNumb > 0) {
                        list_buyers.setSelectedIndex(-1);
                        list_MediumConsumers.setSelectedIndex(-1);
                        wholesale.setLConsumerAgentName(largeConsumer_names);
                    RiskAttitudeForm newWindow = new RiskAttitudeForm(wholesale, 3);
                    newWindow.setVisible(true);
                } else {
                        JOptionPane.showMessageDialog(null, "Option not available! Please insert agents!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                    }
                    }catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Option not available! Please contact system administrator!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                }
                       } else if (e.getSource().equals(menu_alternatingOffers_consumers)) {
                       try{
                                                int sellerNumb = seller_names.getSize();
       int buyerNumb = mediumConsumer_names.getSize();
       
                    if (sellerNumb > 0 && buyerNumb > 0) {
                        list_buyers.setSelectedIndex(-1);
                        list_LargeConsumers.setSelectedIndex(-1);
                        wholesale.setConsumerAgentName(mediumConsumer_names);
                    RiskAttitudeForm newWindow = new RiskAttitudeForm(wholesale, 4);
                    newWindow.setVisible(true);
                } else {
                        JOptionPane.showMessageDialog(null, "Option not available! Please insert agents!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                    }
                    }catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Option not available! Please contact system administrator!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource().equals(forward_bi_Market)) {
                try {
                    market.setContractOption_Windows(null);
                    market.setContractType();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "ERRO No MarketGui");
                }
            } else if (e.getSource().equals(OTCs)) {
                        JOptionPane.showMessageDialog(null, "OTCs Not Available!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else if (e.getSource().equals(ancillaryService)) {
                JOptionPane.showMessageDialog(null, "Ancillary Service Not Available!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else if (e.getSource().equals(capacityMarket)) {
                JOptionPane.showMessageDialog(null, "Capacity Market Not Available!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else if (e.getSource().equals(wholesale_simulation)) {
                try {
                    wholesalemarket_LMP.simul.WholesaleMarket aux = new WholesaleMarket(wholesale);
                    aux.startSimulation();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Wholesale Simulation not available! Please insert agents' data!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource().equals(marketPool_Sym_simulation)) {
                System.out.println("Market Pool Simulation");
                market.startsimulation(true, false, false, false);

            } else if (e.getSource().equals(marketPool_aSym_simulation)) {
                System.out.println("Market Pool Simulation");
                market.startsimulation(false, true, false, false);
                try {
                    marketPool_SMP.run(false, market);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Wholesale Simulation not available! Please insert agents' data!",
                            "Warning", JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (e.getSource().equals(menu_action_pairing)) 
            {
                if (list_sellers.getSelectedIndex() != -1 && (list_buyers.getSelectedIndex() != -1)||(list_MediumConsumers.getSelectedIndex() != -1)||(list_LargeConsumers.getSelectedIndex() != -1)) {
//                    AID[] pair = {new AID(list_sellers.getSelectedValue(), AID.ISLOCALNAME), new AID(list_buyers.getSelectedValue(), AID.ISLOCALNAME)};
                    if(list_MediumConsumers.getSelectedIndex() != -1){
//                    pair[1] = new AID(list_MediumConsumers.getSelectedValue(), AID.ISLOCALNAME);
                    } else if(list_LargeConsumers.getSelectedIndex() != -1){
//                        pair[1] = new AID(list_LargeConsumers.getSelectedValue(), AID.ISLOCALNAME);
                    }
                    
                    
//                    String belief_seller = pair[0].getLocalName() + ";waiting_for_opponent";
//                    String belief_buyer = pair[1].getLocalName() + ";waiting_for_opponent";
//                    if (market.beliefExists(pair[0].getLocalName(), belief_seller) && market.beliefExists(pair[1].getLocalName(), belief_buyer)) {
//                        market.removeBelief(pair[0].getLocalName(), belief_seller);
//                        market.removeBelief(pair[1].getLocalName(), belief_buyer);
//                        market.addNegotiationPairAndInformThem(pair);
//                    } else {
//                        market.removeBelief(pair[0].getLocalName(), belief_seller);
//                        market.removeBelief(pair[1].getLocalName(), belief_buyer);
//                        market.addNegotiationPairAndInformThem(pair);
//                    }
                }
            }
        }
    }

    public int getMARKET_TYPE() {
        return MARKET_TYPE;
    }

    public void setMARKET_TYPE(int MARKET_TYPE) {
        this.MARKET_TYPE = MARKET_TYPE;
    }

    public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
    
  
    
    /**
     * @author Filipe Silvério
     * 
     * Components Initiation
     */
    public void initComponents(){
        panel_trader.setLayout(new BoxLayout(panel_trader, BoxLayout.Y_AXIS));
        panel_retailler.setLayout(new BoxLayout(panel_retailler, BoxLayout.Y_AXIS));
        jTextArea_webtext = new javax.swing.JTextArea();
        jTextArea_webtext.setEditable(false);
        jTextArea_webtext.setColumns(20);
        jTextArea_webtext.setRows(5);
        //System.out.println(Arrays.toString(ExternalAssistantBDI.ptmarginalenergyprices));
        //System.out.println(Arrays.toString(ExternalAssistantBDI.esmarginalenergyprices));
        //System.out.println(df2.format(ExternalAssistantBDI.ptmarginalenergyprices[3]));

        if(Arrays.toString(ExternalAssistantBDI.ptmarginalenergyprices).equals(Arrays.toString(ExternalAssistantBDI.esmarginalenergyprices))){
            jTextArea_webtext.setText(" "+Arrays.toString(ExternalAssistantBDI.date)+"  -  Preço marginal no sistema eléctrico Português (EUR/MWh):\n"+ "  Hora:      01h      02h      03h     04h     05h      06h     07h     08h     09h     10h     11h      12h     13h      14h     15h     16h     17h      18h     19h      20h     21h     22h      23h     24h  \n Preço:    "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[0])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[1])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[2])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[3])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[4])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[5])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[6])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[7])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[8])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[9])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[10])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[11])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[12])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[13])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[14])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[15])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[16])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[17])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[18])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[19])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[20])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[21])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[22])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[23])+"   \n");
            System.out.println("Preços Marginais de Portugal e Espanha são iguais");
        }else{
            jTextArea_webtext.setText(" "+Arrays.toString(ExternalAssistantBDI.date)+"  -  Preço marginal no sistema eléctrico Português (EUR/MWh):\n"+ "  Hora:      01h      02h      03h     04h     05h      06h     07h     08h     09h     10h     11h      12h     13h      14h     15h     16h     17h      18h     19h      20h     21h     22h      23h     24h  \n Preço:    "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[0])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[1])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[2])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[3])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[4])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[5])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[6])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[7])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[8])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[9])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[10])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[11])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[12])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[13])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[14])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[15])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[16])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[17])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[18])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[19])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[20])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[21])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[22])+"  "+df2.format(ExternalAssistantBDI.ptmarginalenergyprices[23])+"   \n"+"\n "+Arrays.toString(ExternalAssistantBDI.date)+"  -  Preço marginal no sistema eléctrico Espanhol (EUR/MWh):\n"+ "  Hora:      01h      02h      03h     04h     05h      06h     07h     08h     09h     10h     11h      12h     13h      14h     15h     16h     17h      18h     19h      20h     21h     22h      23h     24h  \n Preço:    "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[0])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[1])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[2])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[3])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[4])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[5])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[6])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[7])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[8])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[9])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[10])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[11])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[12])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[13])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[14])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[15])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[16])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[17])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[18])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[19])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[20])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[21])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[22])+"  "+df2.format(ExternalAssistantBDI.esmarginalenergyprices[23])+"   \n");
        }
        
        jTextArea_webtext.setToolTipText("");
        jScrollPane_webtext = new javax.swing.JScrollPane();
        jScrollPane_webtext.setViewportView(jTextArea_webtext);
        jSplitPane_weblog = new javax.swing.JSplitPane();
        jSplitPane_weblog.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane_weblog.setBottomComponent(jScrollPane_webtext);
        jSplitPane_weblog.setTopComponent(scroll_log);
        jSplitPane_weblog.setEnabled(false);
        jSplitPane_weblog.setDividerSize(2);
        jSplitPane_weblog.setResizeWeight(0.7);
        jLabel_clock = new javax.swing.JLabel();
        jLabel_clock.setFont(new java.awt.Font("Century Gothic", 0, 14));
        jLabel_clock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/personalassistant/images/clock.png")));
//        jLabel_clock.setIcon(new ImageIcon("\\main\\java\\personalassistant\\images\\clock.jpg"));
        jLabel_date = new javax.swing.JLabel();
        jLabel_date.setFont(new java.awt.Font("Century Gothic", 0, 14));
    }
    /**
     * @author Filipe Silvério
     * 
     * function that starts the clock
     */
    public void clock(){
        final String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday", "Friday", "Saturday" };
        
        Thread clock = new Thread(){
            public void run(){
                try{
                    while(true){
                        Calendar cal = new GregorianCalendar();
                        //int day = cal.get(Calendar.DAY_OF_MONTH);
                        //int month = cal.get(Calendar.MONTH)+1;
                        //int year = cal.get(Calendar.YEAR);

                        String second = df.format(cal.get(Calendar.SECOND));
                        String minute = df.format(cal.get(Calendar.MINUTE));
                        String hour = df.format(cal.get(Calendar.HOUR_OF_DAY));
                        
                        //jLabel_clock.setText(hour+":"+minute+":"+second+" - "+year+"/"+month+"/"+day+"   ");
                        jLabel_clock.setText(strDays[cal.get(Calendar.DAY_OF_WEEK) - 1]+" - "+hour+":"+minute+":"+second+"   ");
                        
                        sleep(1000);
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        clock.start();
    }
    
    
    // Variables declaration - do not modify 
    private javax.swing.JSplitPane jSplitPane_weblog;
    private javax.swing.JScrollPane jScrollPane_webtext;
    private javax.swing.JTextArea jTextArea_webtext;
    protected javax.swing.JLabel jLabel_clock;
    protected javax.swing.JLabel jLabel_date;
    // End of variables declaration       
    
}

