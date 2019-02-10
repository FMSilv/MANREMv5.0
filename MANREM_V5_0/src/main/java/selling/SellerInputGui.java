package selling;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import tools.TimeChooser;
import FIPA.DateTime;
import expertsystem.ExpertSystem;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.BorderUIResource;
import personalassistant.NewAgent_Info;
import risk.Risk;

public class SellerInputGui extends JFrame {

    private double[] min_p = {36.95, 39.2, 48.71, 46.91, 35.29, 47.06};

    private Font font_1 = new Font("Arial", Font.BOLD, 13);
    private Font font_2 = new Font("Arial", Font.BOLD, 11);
    private JTextField[] tf_price_target;
    private JTextField[] tf_volume_target;
    private JTextField[] tf_price;
    private JTextField[] tf_volume;
    private JTextField[] tf_price_limit;
    public ArrayList<Double> price_mec = new ArrayList<>();
    private JTextField[] tf_price_mec;
    private JTextField[] tf_volume_limit;
    private JTextField[] sort_alg;

    private JTextField[] tf_personal_info, tf_price_send, tf_volume_send, Concession;
    private JTextField[] ES_profile;
    private boolean price_listener_occupied = false;
    protected JMenuItem protocol_list = new JMenuItem("Alternating Offers");
    protected JMenuItem function_linear = new JMenuItem("Linear");
    protected JMenu menu = new JMenu("Preference");
    protected JMenuItem additive = new JMenuItem("Additive Function");
    protected JMenuItem multiplicative = new JMenuItem("Multiplicative Function");
    protected JMenuItem benefit = new JMenuItem("Benefit Function");
    protected JMenuItem riskfunc = new JMenuItem("Risk Function");
    protected JMenuItem rigriskfunc = new JMenuItem("Rigorous Risk Function");
    protected JMenu menu1 = new JMenu("Strategy");
    protected JMenu concession = new JMenu("Concession Making");
    protected JMenu problem = new JMenu("Problem Solving");
    protected JMenu contending = new JMenu("Contending");
    protected JMenu matching = new JMenu("Matching");
    protected JMenu risks = new JMenu("Risk Strategy");
//    protected JMenu demand = new JMenu("Demand Response Strategy");
    protected JMenuItem making = new JMenuItem("Compromise");
    protected JMenuItem low = new JMenuItem("Low-Priority Concession");
    protected JMenuItem logrolling = new JMenuItem("Modified Logrolling");
    protected JMenuItem non = new JMenuItem("Non-specific Conpensation");
    protected JMenuItem multiple = new JMenuItem("Multiple Simultaneous Offers");
    protected JMenuItem slowly = new JMenuItem("Conceding Slowly");
    protected JMenuItem threats = new JMenuItem("Threats");
    protected JMenuItem promises = new JMenuItem("Promises");
    protected JMenuItem intrasigent = new JMenuItem("Intrasigent Priority");
    protected JMenuItem E_R = new JMenuItem("Volume Conceder");
    protected JMenu Demand = new JMenu("Price Management");
    protected JMenuItem vectorial = new JMenuItem("Vectorial Distance");
    protected JMenuItem maxbenefit = new JMenuItem("Maximum Benefit");
    protected JMenuItem riskstrat = new JMenuItem("Negotiation Risk Strategy");
    protected JMenuItem von = new JMenuItem("Von Neumann-Morgenstern");
    protected JMenu behaviour = new JMenu("Behaviour");
    protected JMenu goal = new JMenu("Goal");
    protected JMenuItem sStrategy = new JMenuItem("Single Strategy");
    protected JMenuItem collaboration = new JMenuItem("Collaboration");
    protected JMenuItem accomodation = new JMenuItem("Accomodation");
    protected JMenuItem competition = new JMenuItem("Competition");
    protected JMenuItem mScore = new JMenuItem("max Score");
    protected JMenuItem mSdeal = new JMenuItem("max Score with deal");
    protected JMenuItem deal = new JMenuItem("deal");
    protected JMenuItem ESdeadline = new JMenuItem("deadline");
    protected JMenuItem userDefined = new JMenuItem("User Defined");
    protected JMenuItem conceder = new JMenuItem("Time Conceder");
    protected JMenuItem boulware = new JMenuItem("Time Boulware");
    protected JMenuItem tit = new JMenuItem("Tit-For-Tat");
    protected JMenuItem inversetit = new JMenuItem("Inverse Tit-For-Tat");
    protected JMenuItem randTit = new JMenuItem("Random Tit-For-Tat");
    private String[] seller_list = {"N2KPower", "RES_Corporation"};
    private String[] contract_list = {"Forward Contract", "Contract For Difference", "Option Contract"};
//    private String[] protocol_list = {"Alternating Offers"};
//    private String[] preference_list = {"Additive Function"};
    private String[] strategy_list = {"Compromise", "Low-Priority Concession", "Volume Conceder", "Time Conceder", "Time Boulware", "Tit-For-Tat", "Random Tit-For-Tat", "Intrasigent Priority"};
//    private String[] strategy_list = {"Price Management"};
    private String[] algorithm_list = {"Vectorial Distance", "Maximum Benefit"};
    public String[] ESlist;
    private String[] strategy_arg = {"srcm", "lpcm", "edcm", "conc", "tbou", "tft", "rtft"};
    private String[] tactics = {"Competition", "Collaboration", "Accomodation", "max Score", "max Score with deal", "deal", "deadline", "User Defined"};
    public String tactic = "";
    public String[] listConcession;
    public SellerGui Parent;
    private JComboBox predefined_ES = new JComboBox(tactics);
    private final Seller seller;
    private String location = "images\\";
    private String[] sort_titles = {"Price", "Volume", "User Defined"};
    private JComboBox cb_sort_titles = new JComboBox(sort_titles);
    private String icon_agenda_location = location + "icon1.png";
    private ArrayList<String> personal_info;
    DecimalFormat twodecimal = new DecimalFormat("0.00");
    DecimalFormat threedecimal = new DecimalFormat("0.000");
    int test2 = 0;
    String[] list3;
    public int[] auxlist;
//    public String[] listaux;
    public int[] listsort;
    String[] list4 = new String[1];
    String[][] sol = new String[strategy_list.length][2];
    private boolean sort_listener_occupied = false;
    private boolean concession_listener_occupied = false;
    double auxBcmp = 0;
    double contract;
    int test = 0;
    double[] auxCmp;

    public final int DEFAULT = 24;
    public int PERIODS = 24;
    double[] deviation = new double[PERIODS];
    public String HOURS;
    public String[] houraux;
    String[][] CF = {{"100", "100", "-100", "-100", "-100", "100", "50", "100", "100", "100", "100"}, {"50", "50", "100", "50", "50", "50", "100", "100", "100", "100", "100"}, {"100", "0", "0", "100", "100", "0", "50", "100", "100", "100", "100"}, {"100", "100", "0", "0", "0", "100", "100", "100", "100", "100", "100"}, {"100", "80", "0", "0", "90", "0", "100", "100", "100", "100", "100"}, {"100", "0", "100", "0", "80", "0", "50", "100", "100", "100", "100"}, {"100", "0", "0", "100", "100", "0", "0", "100", "100", "100", "100"}, {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"}};

    String[][] sendCF = new String[1][CF[0].length];
    private boolean ES_listener_occupied = false;

    private SellerTechnology setPowerPlant;

    public SellerInputGui(Seller seller) {
        this.seller = seller;
        tf_price_target = new JTextField[PERIODS];
        tf_volume_target = new JTextField[PERIODS];
        tf_price_limit = new JTextField[PERIODS];
        tf_price_mec = new JTextField[PERIODS];
        tf_volume_limit = new JTextField[PERIODS];
        tf_personal_info = new JTextField[4];
        tf_price_send = new JTextField[PERIODS + 1];
        tf_volume_send = new JTextField[PERIODS];
        tf_price = new JTextField[PERIODS];
        tf_volume = new JTextField[PERIODS];
        Concession = new JTextField[PERIODS];
        list3 = new String[PERIODS];
        auxCmp = new double[PERIODS];
        listsort = new int[PERIODS];
        listConcession = new String[PERIODS];
        auxlist = new int[PERIODS];
        sort_alg = new JTextField[PERIODS];
        ES_profile = new JTextField[CF[0].length];
        ESlist = new String[sendCF[0].length];
        deviation = new double[2 * PERIODS];
    }

    public String askContract(SellerGui parent, String proposal) {

        String contract_proposed;
//        if (proposal != null) {
//            date_proposed.setTime(Long.valueOf(proposal));
//        } else {
//            date_proposed.setTime(System.currentTimeMillis());
//        }

        JPanel panel = new JPanel(new BorderLayout());

        if (proposal != null) {
            JPanel panel_center = new JPanel();
            panel_center.setLayout(new GridBagLayout());
            panel_center.setMinimumSize(new Dimension(350, 60));
            panel_center.setPreferredSize(new Dimension(350, 60));

            JLabel l = new JLabel("Do you accept a " + proposal + "?");

            l.setHorizontalAlignment(SwingConstants.LEFT);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(50, 40, 50, 70);
            panel_center.add(l, gridBagConstraints);
            panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

//                panel.add(panel_north, BorderLayout.NORTH);
            panel.add(panel_center, BorderLayout.CENTER);
            String[] choices = {"Accept", "Propose"};
            int result = JOptionPane.showOptionDialog(parent, panel, "Define Type of Contract", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
            if (result == 1) {
//                TimeChooser tc = new TimeChooser(date_proposed);
//                int result_date = tc.showEditTimeDlg(parent);
//                while (result_date == 1) {
//                    tc = new TimeChooser(date_proposed);
//                    result_date = tc.showEditTimeDlg(parent);
//                }
//                if (result_date == 0) {
//                    return String.valueOf(tc.getDate().getTime());
//                }
            } else if (result == 0) {
                return proposal;
            }
        } else {

            JPanel panel_center = new JPanel();
            panel_center.setLayout(new GridBagLayout());
            panel_center.setMinimumSize(new Dimension(350, 60));
            panel_center.setPreferredSize(new Dimension(350, 60));

            JLabel l = new JLabel("Contract:");

            l.setHorizontalAlignment(SwingConstants.LEFT);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(50, 40, 50, 70);
            panel_center.add(l, gridBagConstraints);

            JComboBox cb_contract = new JComboBox(contract_list);
            cb_contract.setMinimumSize(new Dimension(180, 25));
            cb_contract.setPreferredSize(new Dimension(180, 25));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new Insets(47, -30, 50, 50);
            panel_center.add(cb_contract, gridBagConstraints);

            panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

//                panel.add(panel_north, BorderLayout.NORTH);
            panel.add(panel_center, BorderLayout.CENTER);

            int result = JOptionPane.showOptionDialog(parent, panel, "Contract Type", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

            if (result == 0) {
                seller.setNegotiationContract((String) cb_contract.getSelectedItem());
            }
//                int result_date = tc.showEditTimeDlg(parent);
//                while (result_date == 1) {
//                    tc = new TimeChooser(date_proposed);
//                    result_date = tc.showEditTimeDlg(parent);
//                }
//                if (result_date == 0) {
//                    return String.valueOf(tc.getDate().getTime());
//                }

        }

        return null;
    }

    public void askTargets(SellerGui parent) {

        String[] my_prices_per = new String[PERIODS];
        if (seller.risk == 0) {
            String my_prices = seller.searchBelief("myagent", "prices");
            String[] my_prices_array_aux = my_prices.split(";")[2].split("-");

            String[] my_prices_array = new String[24];

            for (int i = 0; i < PERIODS; i++) {
                my_prices_per[i] = "0";
            }

//        String my_volumes = seller.searchBelief("myagent", "volumes");
//        String[] my_volumes_array_aux= my_volumes.split(";")[2].split("-");
//        String[] my_volumes_array= new String[PERIODS];
            for (int i = 0; i < my_prices_array_aux.length; i++) {
                my_prices_array[i] = my_prices_array_aux[i].split("_")[1];
//            my_volumes_array[i]=my_volumes_array_aux[i].split("_")[1];
            }

            if (PERIODS != 24) {

                int z = 0;
                for (int j = 0; j < PERIODS; j++) {
                    for (int i = 0; i < (int) Double.parseDouble(houraux[j + 24]); i++) {
                        int a = (int) Double.parseDouble(houraux[z]);
                        my_prices_per[j] = twodecimal.format(Double.parseDouble(my_prices_per[j]) + Double.parseDouble(my_prices_array[a - 1])).replace(",", ".");;
                        z++;
                    }
                    my_prices_per[j] = twodecimal.format((Double.parseDouble(my_prices_per[j])) / Double.parseDouble(houraux[j + 24])).replace(",", ".");;
                }
            } else {

                for (int i = 0; i < my_prices_array.length; i++) {
                    my_prices_per[i] = my_prices_array[i];
                }
            }
        }

        JPanel panel = new JPanel(new BorderLayout());

        //Panel north
        JPanel panel_north = new JPanel();
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setMinimumSize(new Dimension(300, 360));
        panel_center.setPreferredSize(new Dimension(300, 360));

        if (PERIODS <= 12 && PERIODS > 6) {
            panel_center.setMinimumSize(new Dimension(300, 360));
            panel_center.setPreferredSize(new Dimension(300, 360));
        }
        if (PERIODS > 12) {
            panel_center.setMinimumSize(new Dimension(450, 360));
            panel_center.setPreferredSize(new Dimension(450, 360));
        }

        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(400, 65));
        panel_north.setPreferredSize(new Dimension(400, 65));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(345, 60));
        panel_text_background.setPreferredSize(new Dimension(345, 60));
        label_text.setMinimumSize(new Dimension(345, 60));
        label_text.setPreferredSize(new Dimension(345, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Initial Values (Targets)</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Panel center
        // Label names
        JLabel l = new JLabel("Period load");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 30);
//        panel_center.add(l, gridBagConstraints);

        JLabel l1 = new JLabel("Price (€/MWh)");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
        panel_center.add(l1, gridBagConstraints1);

//        JLabel l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 2;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        if (PERIODS > 12) {
            l1 = new JLabel("Price (€/MWh)");
            l1.setFont(font_1);
            l1.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 4;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
            panel_center.add(l1, gridBagConstraints1);

//        l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 5;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        }

        //Label textfields
        for (int i = 0; i < PERIODS; i++) {

            l = new JLabel("Period " + (i + 1) + ":");
            l.setFont(font_1);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 1;
            if (i >= 12 && PERIODS > 12) {
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = i + 1 - 12;
            }
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 3);
            panel_center.add(l, gridBagConstraints);

            tf_price_target[i] = new JTextField(64);
            if (seller.risk == 0) {
                tf_price_target[i].setText(my_prices_per[i]);
            }
//            tf_price_target[i].setText(twodecimal.format(min_p[i]*1.35).replace(",", "."));
            if (seller.risk == 1) {
                tf_price_target[i].setText("" + seller.prices_target.get(i));
            }
            tf_price_target[i].setMinimumSize(new Dimension(70, 25));
            tf_price_target[i].setPreferredSize(new Dimension(70, 25));
//            tf_price_target[i].setEnabled(false);
            tf_price_target[i].setEditable(false);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i + 1;
            if (i >= 12) {
                gridBagConstraints.gridx = 4;
                gridBagConstraints.gridy = i + 1 - 12;
            }
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new Insets(0, 10, 0, 3);
            panel_center.add(tf_price_target[i], gridBagConstraints);

//            tf_volume_target[i] = new JTextField(64);
////            tf_volume_target[i].setText(String.valueOf((Math.random() * 10) + 50).substring(0, 2));
//            tf_volume_target[i].setText(tf_volume[i].getText());
//            tf_volume_target[i].setMinimumSize(new Dimension(70, 25));
//            tf_volume_target[i].setPreferredSize(new Dimension(70, 25));
//            tf_volume_target[i].setEditable(false);
//            gridBagConstraints = new GridBagConstraints();
//            gridBagConstraints.gridx = 2;
//            gridBagConstraints.gridy = i + 1;
//            if (i>=12){
//            gridBagConstraints.gridx = 5;
//            gridBagConstraints.gridy = i + 1-12;
//            }
//            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//            gridBagConstraints.insets = new Insets(0, 10, 0, 3);
//            panel_center.add(tf_volume_target[i], gridBagConstraints);
        }

        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        String[] choices = {"OK", "Continue"};

//        int result = JOptionPane.showOptionDialog(parent, panel, "Seller Targets", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
//
////        while (result != -1 && (checkEmptyFields(tf_price_target) || checkEmptyFields(tf_volume_target))) {
//            while (result != -1 && (checkEmptyFields(tf_price_target) )) {
//
//            JOptionPane.showMessageDialog(parent, new JLabel("<html>Some inputs are missing</html>"), "Targets", JOptionPane.ERROR_MESSAGE);
//            result = JOptionPane.showOptionDialog(parent, panel, "Targets", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
//
//        }
//
//        if (result == 0) {
//            ArrayList<Double> price_target_final = new ArrayList<>();
//            ArrayList<Double> volume_target_final = new ArrayList<>();
//
//            for (int i = 0; i < tf_price_target.length; i++) {
////                price_target_final.add(Double.valueOf(tf_price_target[i].getText()));
//                 price_target_final.add(Double.valueOf(tf_price[i].getText()));
////                volume_target_final.add(Double.valueOf(tf_volume_target[i].getText()));
//                volume_target_final.add(Double.valueOf(tf_volume[i].getText()));
//            }
//
//            seller.setPricesTarget(price_target_final);
//            seller.setVolumesTarget(volume_target_final);
//            seller.checkIfReadyToNegotiate();
//        } else if (result == 1) {
        ArrayList<Double> price_target_final = new ArrayList<>();
        ArrayList<Double> volume_target_final = new ArrayList<>();

        for (int i = 0; i < tf_price_target.length; i++) {
//                price_target_final.add(Double.valueOf(tf_price_target[i].getText()));
            price_target_final.add(Double.valueOf(tf_price_target[i].getText()));
//                volume_target_final.add(Double.valueOf(tf_volume_target[i].getText()));
            volume_target_final.add(Double.valueOf(tf_volume[i].getText()));

        }

        seller.setPricesTarget(price_target_final);
        seller.setVolumesTarget(volume_target_final);

        askLimits(parent);

        seller.checkIfReadyToNegotiate();
//        }

    }

    public void askLimits(SellerGui parent) {

        String my_prices_min = seller.searchPartialBelief("myagent", "pricesMin;");
        String[] my_prices_min_array_aux = my_prices_min.split(";")[2].split("-");
        String[] my_prices_min_array = new String[DEFAULT];
        String[] my_prices_per = new String[PERIODS];
        for (int i = 0; i < PERIODS; i++) {
            my_prices_per[i] = "0";
        }
//        String my_volumes = seller.searchPartialBelief("myagent", "volumes;");
//        String[] my_volumes_array_aux= my_volumes.split(";")[2].split("-");
//        String[] my_volumes_array= new String[PERIODS];
        for (int i = 0; i < my_prices_min_array_aux.length; i++) {
            my_prices_min_array[i] = my_prices_min_array_aux[i].split("_")[1];
//            my_volumes_array[i]=my_volumes_array_aux[i].split("_")[1];
        }

        if (PERIODS != 24) {

            int z = 0;
            for (int j = 0; j < PERIODS; j++) {
                for (int i = 0; i < (int) Double.parseDouble(houraux[j + 24]); i++) {
                    int a = (int) Double.parseDouble(houraux[z]);
                    my_prices_per[j] = twodecimal.format(Double.parseDouble(my_prices_per[j]) + Double.parseDouble(my_prices_min_array[a - 1])).replace(",", ".");;
                    z++;
                }
                my_prices_per[j] = twodecimal.format((Double.parseDouble(my_prices_per[j])) / Double.parseDouble(houraux[j + 24])).replace(",", ".");;
            }
        } else {

            for (int i = 0; i < my_prices_min_array.length; i++) {
                my_prices_per[i] = my_prices_min_array[i];
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setMinimumSize(new Dimension(300, 360));
        panel_center.setPreferredSize(new Dimension(300, 360));
        if (PERIODS <= 12 && PERIODS > 6) {
            panel_center.setMinimumSize(new Dimension(300, 360));
            panel_center.setPreferredSize(new Dimension(300, 360));
        }
        if (PERIODS > 12) {
            panel_center.setMinimumSize(new Dimension(450, 360));
            panel_center.setPreferredSize(new Dimension(450, 360));
        }

        // Panel north
        JPanel panel_north = new JPanel();

        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(350, 65));
        panel_north.setPreferredSize(new Dimension(350, 65));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(295, 60));
        panel_text_background.setPreferredSize(new Dimension(295, 60));
        label_text.setMinimumSize(new Dimension(295, 60));
        label_text.setPreferredSize(new Dimension(295, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Enter your Ultimate Fallback Position or Limits</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Panel center
        // Label names
        JLabel l = new JLabel("Period load");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 30);
//        panel_center.add(l, gridBagConstraints);

        JLabel l1 = new JLabel("Price (€/MWh)");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
        panel_center.add(l1, gridBagConstraints1);

//        JLabel l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 2;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        if (PERIODS > 12) {
            l1 = new JLabel("Price (€/MWh)");
            l1.setFont(font_1);
            l1.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 4;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
            panel_center.add(l1, gridBagConstraints1);

//        l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 5;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        }

        //Label textfields
        for (int i = 0; i < PERIODS; i++) {

            l = new JLabel("Period " + (i + 1) + ":");
            l.setFont(font_1);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
            if (i >= 12 && PERIODS > 12) {
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 3);
            }
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;

            panel_center.add(l, gridBagConstraints);

            tf_price_limit[i] = new JTextField(64);
//            System.out.println("Preço: "+tf_price_limit[i].getText());
            if (seller.risk == 1) {
                tf_price_limit[i].setText("" + seller.prices_limit.get(i));
            }
            if (seller.risk == 0) {
                tf_price_limit[i].setText(my_prices_per[i]);
            }
            tf_price_limit[i].setMinimumSize(new Dimension(87, 25));
            tf_price_limit[i].setPreferredSize(new Dimension(87, 25));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.insets = new Insets(0, 5, 0, 30);
            if (i >= 12) {
                gridBagConstraints.gridx = 4;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            }
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

            panel_center.add(tf_price_limit[i], gridBagConstraints);

//            tf_volume_limit[i] = new JTextField(64);
////            tf_volume_limit[i].setText(String.valueOf((Math.random() * 10) + 30).substring(0, 2));
//            tf_volume_limit[i].setText(tf_volume[i].getText());
//            tf_volume_limit[i].setMinimumSize(new Dimension(70, 25));
//            tf_volume_limit[i].setPreferredSize(new Dimension(70, 25));
//            tf_volume_limit[i].setEditable(false);
//            gridBagConstraints = new GridBagConstraints();
//            gridBagConstraints.gridx = 2;
//            gridBagConstraints.gridy = i + 1;
//            if (i>=12){
//            gridBagConstraints.gridx = 5;
//            gridBagConstraints.gridy = i + 1-12;
//            }
//            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//            gridBagConstraints.insets = new Insets(0, 10, 0, 3);
//            panel_center.add(tf_volume_limit[i], gridBagConstraints);
        }

        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

//        String[] choices = {"Back","OK", "Continue"};
        String[] choices = {"Back", "OK", "Continue"};

        int result = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        while (result != -1 && checkEmptyFields(tf_price_limit)) {

            JOptionPane.showMessageDialog(parent, new JLabel("<html>Some inputs are missing</html>"), "Limits", JOptionPane.ERROR_MESSAGE);
            result = JOptionPane.showOptionDialog(parent, panel, "Limits", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        }
//        if (result == 0) {
//        askTargets(parent);
//        return;
//        }else
        if (result == 1) {
            ArrayList<Double> price_limit_final = new ArrayList<>();
            ArrayList<Double> volume_limit_final = new ArrayList<>();

            for (int i = 0; i < tf_price_limit.length; i++) {
                price_limit_final.add(Double.valueOf(tf_price_limit[i].getText()));
            }

            seller.setPricesLimit(price_limit_final);
            seller.setVolumesLimit(volume_limit_final);
            seller.checkIfReadyToNegotiate();

        } else if (result == 2) {
            ArrayList<Double> price_limit_final = new ArrayList<>();
            ArrayList<Double> volume_limit_final = new ArrayList<>();

            for (int i = 0; i < tf_price_limit.length; i++) {
                price_limit_final.add(Double.valueOf(tf_price_limit[i].getText()));

            }
            seller.setPricesLimit(price_limit_final);
            seller.setVolumesLimit(volume_limit_final);
            askPreference(parent);
            seller.checkIfReadyToNegotiate();
        } else if (result == 0) {
            if (seller.risk == 1) {
                askPriceLimits(parent);
            }
            if (seller.risk == 0) {
                askPriorities(parent);
            }
        }
    }

    public void askPreference(SellerGui parent) {

        Parent = parent;

        JFrame frame = new JFrame("Strategies");
        if (seller.ES != 2) {
            frame = new JFrame("Tactics");
        }
        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
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
        label_text.setText("<html>Indicate Your Preferences to Rate Offers</html>");
//                if (seller.ES!=2){
//           label_text.setText("<html>Choose preference and tactic<br>to use during negotiation</html>");
//        }
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 60));
            panel_pic_background.setPreferredSize(new Dimension(55, 60));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new JPanel();
        panel_center.setLayout(new GridBagLayout());
        panel_center.setPreferredSize(new Dimension(350, 300));
        panel_center.setMinimumSize(new Dimension(350, 300));

        JLabel l = new JLabel("Scoring Function");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(50, 40, 50, 70);
        panel_center.add(l, gridBagConstraints);

//        JComboBox cb_protocol = new JComboBox(protocol_list);
        JMenu protocol = new JMenu("Linear");
//        protocol[0] = protocol_list;
        JMenu protocol1 = ComboMenuBar.createMenu(protocol.getText());
        protocol1.add(function_linear);

        ComboMenuBar comboprotocol = new ComboMenuBar(protocol1);

        comboprotocol.setMinimumSize(new Dimension(180, 25));
        comboprotocol.setPreferredSize(new Dimension(180, 25));
//        comboprotocol.setEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(47, -30, 50, 50);
        panel_center.add(comboprotocol, gridBagConstraints);

        l = new JLabel("Preference Model:");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 50, 30);
        panel_center.add(l, gridBagConstraints);

//        JComboBox cb_preference = new JComboBox(preference_list);
//                JMenu preference = new JMenu("Benefit Function");
//        preference[0] = preference_list;
        JMenu preference1 = ComboMenuBar.createMenu(additive.getText());

        preference1.add(additive);
        preference1.add(multiplicative);
        preference1.add(benefit);

        ComboMenuBar combopreference = new ComboMenuBar(preference1);
        combopreference.setMinimumSize(new Dimension(180, 25));
        combopreference.setPreferredSize(new Dimension(180, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 50, 50);
        panel_center.add(combopreference, gridBagConstraints);

        l = new JLabel("Risk Model:");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 40, 40);
        panel_center.add(l, gridBagConstraints);

//        JComboBox cb_preference = new JComboBox(preference_list);
//        JMenu preference = new JMenu("Cost Function");
//        preference[0] = preference_list;
        JMenu preference2 = ComboMenuBar.createMenu(rigriskfunc.getText());

//        preference1.setSelected(riskstrat);
//          preference1.add(von);
        preference2.add(von);
        preference2.add(riskfunc);
        preference2.add(rigriskfunc);

        ComboMenuBar combopreference2 = new ComboMenuBar(preference2);
        combopreference2.setMinimumSize(new Dimension(180, 25));
        combopreference2.setPreferredSize(new Dimension(180, 25));
        if (seller.risk == 0) {
            combopreference2.setEnabled(false);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 40, 50);/*top,left,buttom,right*/

        panel_center.add(combopreference2, gridBagConstraints);

        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        Container pane = frame.getContentPane();
        pane.add(panel, BorderLayout.PAGE_START);

        String[] choices1 = {"Back", "OK", "Continue"};
        int result = JOptionPane.showOptionDialog(parent, pane, "Producer: " + seller.getLocalName(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices1, null);

        if (result == 0) {
            askLimits(parent);
            return;
        } else if (result == 1 || result == 2) {
            seller.setNegotiationProtocol((String) comboprotocol.getSelectedItem());
            seller.setNegotiationPreference((String) combopreference.getSelectedItem());
            seller.setNegotiationRiskPreference((String) combopreference2.getSelectedItem());
//            String aux=(String)comboMenu.getSelectedItem();
//            if (aux != "Vectorial Distance" && aux!="Maximum Benefit" ){
//            seller.setNegotiationStrategy((String) comboMenu.getSelectedItem());
//            }else{
//                seller.setNegotiationStrategyAlgorithm((String) comboMenu.getSelectedItem());
//                seller.setNegotiationStrategy("Price Management");
//            }
//            seller.setNegotiationStrategyAlgorithm((String) cb_algorithm.getSelectedItem());
            seller.checkIfReadyToNegotiate();

        }
        if (result == 2) {
            askProtocolAndStrategy(parent);

        }
        if (test2 < 1) {
//       int test=seller.checkIfReadyToNegotiate();
            if (test == 0) {
                test2++;
//       seller.defineDeadline();
            }
        }
    }

    public void askProtocolAndStrategy(SellerGui parent) {

        JFrame frame = new JFrame("Strategies");
        if (seller.ES != 2) {
            frame = new JFrame("Tactics");
        }
        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
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
        label_text.setText("<html>Indicate Your Initial Negotiation Strategy</html>");
//                if (seller.ES!=2){
//           label_text.setText("<html>Choose preference and tactic<br>to use during negotiation</html>");
//        }
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 60));
            panel_pic_background.setPreferredSize(new Dimension(55, 60));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        JPanel panel_center = new JPanel();
        panel_center.setLayout(new GridBagLayout());
        panel_center.setPreferredSize(new Dimension(400, 250));
        panel_center.setMinimumSize(new Dimension(400, 250));

        JLabel l = new JLabel("Scoring Function");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(50, 40, 50, 70);
////        panel_center.add(l, gridBagConstraints);
//
////        JComboBox cb_protocol = new JComboBox(protocol_list);
//         JMenu protocol = new JMenu("Linear");
////        protocol[0] = protocol_list;
//        JMenu protocol1 = ComboMenuBar.createMenu(protocol.getText());
//         protocol1.add(protocol_list);
//         
//         ComboMenuBar comboprotocol = new ComboMenuBar(protocol1);
//         
//        comboprotocol.setMinimumSize(new Dimension(180, 25));
//        comboprotocol.setPreferredSize(new Dimension(180, 25));
////        comboprotocol.setEnabled(false);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new Insets(47, -30, 50, 50);
////        panel_center.add(comboprotocol, gridBagConstraints);
//
//        l = new JLabel("Preference:");
//        l.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(0, 40, 50, 30);
////        panel_center.add(l, gridBagConstraints);
//
////        JComboBox cb_preference = new JComboBox(preference_list);
////                JMenu preference = new JMenu("Benefit Function");
////        preference[0] = preference_list;
//               JMenu preference1 = ComboMenuBar.createMenu(additive.getText());
////        if(seller.risk==0){
//          preference1.add(additive);
//          preference1.add(multiplicative);
//         preference1.add(benefit);
////        }else{
////            preference1 = ComboMenuBar.createMenu(riskfunc.getText());
////            preference1.add(riskfunc);
////        }
//         ComboMenuBar combopreference = new ComboMenuBar(preference1);
//        combopreference.setMinimumSize(new Dimension(180, 25));
//        combopreference.setPreferredSize(new Dimension(180, 25));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new Insets(-3, -30, 50, 50);
////        panel_center.add(combopreference, gridBagConstraints);

        l = new JLabel("<html>Bargaining<br>Strategy:</html>");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 50, 30);
        panel_center.add(l, gridBagConstraints);

        JMenu[] menus = new JMenu[5];
//    menus[0] = menu1;
        menus[0] = concession;
        menus[1] = problem;
        menus[2] = matching;
        menus[3] = contending;
        menus[4] = risks;
//    menus[2] = demand;
//    menus[3]= Demand;

//    menus[0].add(menus[1]);
//    menus[0].add(menus[2]);
//    menus[0].add(menus[3]);
        menus[0].add(making);
        menus[0].add(E_R);
        menus[0].add(conceder);
        menus[0].add(menus[2]);
        menus[3].add(boulware);
        menus[3].add(slowly);
        menus[3].add(threats);
        menus[3].add(promises);
        menus[2].add(tit);
        menus[2].add(inversetit);
        menus[2].add(randTit);
        menus[1].add(low);
        menus[1].add(logrolling);
        menus[1].add(non);
        menus[1].add(multiple);
        menus[1].add(intrasigent);
        menus[4].add(riskstrat);
//    menus[2].add(menus[3]);
//    menus[3].add(vectorial);
//    menus[3].add(maxbenefit);

        JMenu menu = ComboMenuBar.createMenu(menu1.getText());
        menu.add(menus[0]);
        menu.add(menus[1]);
        menu.add(menus[3]);
        menu.add(menus[4]);
////    menu.addSeparator();
//    menu.add(menus[2]);
//    menu.add(menus[3]);

        ComboMenuBar comboMenu = new ComboMenuBar(menu);

        comboMenu.setMinimumSize(new Dimension(180, 25));
        comboMenu.setPreferredSize(new Dimension(180, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 50, 50);
        panel_center.add(comboMenu, gridBagConstraints);

        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panel.add(panel_center, BorderLayout.CENTER);

        l = new JLabel("<html>Risk Management<br>Strategy:</html>");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 40, 40);
        panel_center.add(l, gridBagConstraints);

//        JComboBox cb_preference = new JComboBox(preference_list);
//        JMenu preference = new JMenu("Cost Function");
//        preference[0] = preference_list;
        JMenu riskmang = ComboMenuBar.createMenu(riskstrat.getText());
//        preference1.setSelected(riskstrat);
//          preference1.add(von);
//          riskmang.add(riskstrat);

        ComboMenuBar comboriskmang = new ComboMenuBar(riskmang);
        comboriskmang.setMinimumSize(new Dimension(180, 25));
        comboriskmang.setPreferredSize(new Dimension(180, 25));
        if (seller.risk == 0) {
            comboriskmang.setEnabled(false);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 40, 50);/*top,left,buttom,right*/

        panel_center.add(comboriskmang, gridBagConstraints);

        l = new JLabel("<html>DR Management<br>Strategy:</html>");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 40, 30);
//        panel_center.add(l, gridBagConstraints);

        JMenu DR = ComboMenuBar.createMenu(maxbenefit.getText());

        DR.add(vectorial);
        DR.add(maxbenefit);

        ComboMenuBar comboDR = new ComboMenuBar(DR);
        comboDR.setMinimumSize(new Dimension(180, 25));
        comboDR.setPreferredSize(new Dimension(180, 25));
        if (seller.VOLUME == 0) {
            comboDR.setEnabled(false);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(-3, -30, 50, 50);
//        panel_center.add(comboDR, gridBagConstraints);

//        l = new JLabel("Algorithm:");
//        l.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 3;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(0, 40, 50, 30);
//        panel_center.add(l, gridBagConstraints);
//
//        JComboBox cb_algorithm = new JComboBox(algorithm_list);
//        cb_algorithm.setMinimumSize(new Dimension(180, 25));
//        cb_algorithm.setPreferredSize(new Dimension(180, 25));
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 3;
//        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new Insets(-3, -30, 50, 50);
//        panel_center.add(cb_algorithm, gridBagConstraints);
        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        Container pane = frame.getContentPane();
        pane.add(panel, BorderLayout.PAGE_START);

        String[] choices1 = {"Back", "OK"};
        int result = JOptionPane.showOptionDialog(parent, pane, "Producer: " + seller.getLocalName(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices1, null);

        if (result == 0) {
            askPreference(parent);
            return;
        } else if (result == 1) {
//            seller.setNegotiationProtocol((String) comboprotocol.getSelectedItem());
//            seller.setNegotiationPreference((String) combopreference.getSelectedItem());
            String aux = (String) comboMenu.getSelectedItem();
            if (aux != "Vectorial Distance" && aux != "Maximum Benefit") {
                seller.setNegotiationStrategy((String) comboMenu.getSelectedItem());
            } else {
                seller.setNegotiationStrategyAlgorithm((String) comboMenu.getSelectedItem());
                seller.setNegotiationStrategy("Price Management");
            }
//            seller.setNegotiationStrategyAlgorithm((String) cb_algorithm.getSelectedItem());
            seller.checkIfReadyToNegotiate();

        }
//        seller.setNegotiationPreference("Benefit Function");
        String str = (String) comboMenu.getSelectedItem();
        if (str == "Strategy") {
            String[] choices = {"OK", "Random"};
            int aux = JOptionPane.showOptionDialog(parent, "You must choose a strategy to negotiate or choose a Random Strategy.", " WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, null);
            if (aux == 1) {
                int strat = (int) Math.round(Math.random() * (strategy_list.length - 1));
//                buyer.getGui().updateLog1("\n"+ strat+ (Math.round(Math.random()*strategy_list.length))+" \n\n");
                seller.setNegotiationStrategy(strategy_list[strat]);
                if (strategy_list[strat] == "Price Management") {
                    int algo = (int) Math.round(Math.random() * (algorithm_list.length - 1));
                    seller.getGui().updateLog1("\n Random Strategy: " + strategy_list[strat] + " Algorithm: " + algorithm_list[algo] + " \n\n");
                } else {
                    seller.getGui().updateLog1("\n Random Strategy: " + strategy_list[strat] + "\n\n");
                }
            } else {
                askProtocolAndStrategy(parent);
            }
        } else {
            seller.getGui().updateLog1("\nStrategy: " + str + "\n\n");
        }
        if (test2 < 1) {
//       int test=seller.checkIfReadyToNegotiate();
            if (test == 0) {
                test2++;
//       seller.defineDeadline();
            }
        }

    }

    class ComboPanel extends JPanel {

        ComboPanel(String title, JComponent c) {
            setLayout(new FlowLayout());
            setBorder(new TitledBorder(title));
            add(c);
        }
    }

    public ArrayList<String> askAgenda(SellerGui parent, ArrayList<String> content_items_array) {

        JPanel panel = new JPanel();

        JCheckBox cb_prices = new JCheckBox();
        cb_prices.setSelected(true);
        JCheckBox cb_volumes = new JCheckBox();
        cb_volumes.setSelected(true);
        JCheckBox cb_extras = new JCheckBox();

        //Panel north
        JPanel panel_north = new JPanel();
        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(300, 60));
        panel_north.setPreferredSize(new Dimension(300, 60));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(245, 6));
        panel_text_background.setPreferredSize(new Dimension(245, 60));
        label_text.setMinimumSize(new Dimension(240, 50));
        label_text.setPreferredSize(new Dimension(240, 50));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Choose agenda items to<br>propose to your opponent</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 60));
            panel_pic_background.setPreferredSize(new Dimension(55, 60));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> volumes = new ArrayList<>();
        ArrayList<String> extras = new ArrayList<>();

        for (int i = 0; i < content_items_array.size(); i++) {
            if (content_items_array.get(i).contains("price") && !content_items_array.get(i).contains("extra")) {
                prices.add(content_items_array.get(i));
            } else if (content_items_array.get(i).contains("volume")) {
                volumes.add(content_items_array.get(i));
            } else if (content_items_array.get(i).contains("extra")) {
                extras.add(content_items_array.get(i));
            }
        }
        //Panel center
        JPanel panel_center = new JPanel();
        panel_center.setLayout(new GridBagLayout());
        panel_center.setMinimumSize(new Dimension(50, 150));
        panel_center.setPreferredSize(new Dimension(50, 150));

        JLabel label_prices_text = new JLabel("Prices");
        label_prices_text.setFont(font_1);
        label_prices_text.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        panel_center.add(label_prices_text, gridBagConstraints);

        cb_prices.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 0);
        panel_center.add(cb_prices, gridBagConstraints);

        JLabel label_volumes_text = new JLabel("Volumes");
        label_volumes_text.setFont(font_1);
        label_volumes_text.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        panel_center.add(label_volumes_text, gridBagConstraints);

        cb_volumes.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 0);
        panel_center.add(cb_volumes, gridBagConstraints);

        if (!extras.isEmpty()) {
            JLabel label_extras_text = new JLabel("Extras");
            label_extras_text.setFont(font_1);
            label_extras_text.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
            panel_center.add(label_extras_text, gridBagConstraints);

            cb_extras.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 0);
            panel_center.add(cb_extras, gridBagConstraints);
        }

        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.setLayout(new BorderLayout());
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        ArrayList<String> proposal = new ArrayList<>();

        int result = JOptionPane.showOptionDialog(parent, panel, "Producer's Agenda Proposal", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (result == 0) {
            if (cb_prices.isSelected()) {
                proposal.addAll(prices);
            }
            if (cb_volumes.isSelected()) {
                proposal.addAll(volumes);
            }
            if (cb_extras.isSelected()) {
                proposal.addAll(extras);
            }
        }
        return proposal;
    }

    public String[] askPublicise(SellerGui parent, boolean forced) {

        String[] price_volumes_to_publicise = new String[2];
//        String price_volumes_to_publicise = "";
        price_volumes_to_publicise[0] = "";
        price_volumes_to_publicise[1] = "";

        JPanel panel = new JPanel(new BorderLayout());

        //Panel north
        JPanel panel_north = new JPanel();
        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        JPanel panel_center = new JPanel();

        panel_north.setMinimumSize(new Dimension(300, 65));
        panel_north.setPreferredSize(new Dimension(300, 65));
        panel_text_background.setMinimumSize(new Dimension(300, 60));
        panel_text_background.setPreferredSize(new Dimension(300, 60));
        panel_center.setMinimumSize(new Dimension(300, 250));
        panel_center.setPreferredSize(new Dimension(300, 250));
        if (PERIODS <= 12 && PERIODS > 6) {
            panel_center.setMinimumSize(new Dimension(300, 360));
            panel_center.setPreferredSize(new Dimension(300, 360));
        }
        if (PERIODS > 12) {
            panel_center.setMinimumSize(new Dimension(450, 360));
            panel_center.setPreferredSize(new Dimension(450, 360));
        }

        panel_north.setLayout(new BorderLayout());

        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        label_text.setMinimumSize(new Dimension(345, 60));
        label_text.setPreferredSize(new Dimension(345, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        if (forced) {
            label_text.setText("<html>Your opponent whishes to know<br>your intended values:<br></html>");
        } else {
            label_text.setText("<html>Indicate the Initial Values for Price</html>");
        }
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Panel center
        panel_center.setLayout(new GridBagLayout());

        // Label names
        JLabel l = new JLabel("Period load");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 30);
//        panel_center.add(l, gridBagConstraints);

        JLabel l1 = new JLabel("Price (€/MWh)");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
        panel_center.add(l1, gridBagConstraints1);

//        JLabel l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 2;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        if (PERIODS > 12) {
            l1 = new JLabel("Price (€/MWh)");
            l1.setFont(font_1);
            l1.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 4;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
            panel_center.add(l1, gridBagConstraints1);

//        l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 5;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        }
        //Label textfields

        for (int i = 0; i < PERIODS; i++) {

            l = new JLabel("Period " + (i + 1) + ":");
            l.setFont(font_1);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
            if (i >= 12 && PERIODS > 12) {
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 3);
            }

            panel_center.add(l, gridBagConstraints);

            tf_price[i].setMinimumSize(new Dimension(87, 25));
            tf_price[i].setPreferredSize(new Dimension(87, 25));
            tf_price[i].setText("" + seller.prices_target.get(i));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new Insets(0, 5, 0, 30);
            if (i >= 12) {
                gridBagConstraints.gridx = 4;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            }

            panel_center.add(tf_price[i], gridBagConstraints);

//            tf_volume[i].setMinimumSize(new Dimension(70, 25));
//            tf_volume[i].setPreferredSize(new Dimension(70, 25));
//            gridBagConstraints = new GridBagConstraints();
//            gridBagConstraints.gridx = 2;
//            gridBagConstraints.gridy = i + 1;
//             if (i>=12){
//            gridBagConstraints.gridx = 5;
//            gridBagConstraints.gridy = i + 1-12;
//            }
//            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//            gridBagConstraints.insets = new Insets(0, 10, 0, 3);
//            panel_center.add(tf_volume[i], gridBagConstraints);
        }

        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

        String[] choices = {"Send", "Cancel"};
        String[] choices1 = {"Yes", "No"};
        int result = 1;

        while (result == 1) {
            result = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName()/*+ " Publicity"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

            while (result != -1 && (checkEmptyFields(tf_price))) {

                JOptionPane.showMessageDialog(parent, new JLabel("<html>Some inputs are missing</html>"), "Publicity", JOptionPane.ERROR_MESSAGE);
                result = JOptionPane.showOptionDialog(parent, panel, "Publicity", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

            }

            if (result == 0) {
//            System.out.println(tf_price.length);
                for (int i = 0; i <= 2 * tf_price.length - 1; i++) {

                    if (i < tf_price.length - 1) {
                        price_volumes_to_publicise[0] = price_volumes_to_publicise[0] + "p" + (+i + 1) + "_" + tf_price[i].getText() + "-";
                    }

                    if (i == tf_price.length - 1) {
                        price_volumes_to_publicise[0] = price_volumes_to_publicise[0] + "p" + (+i + 1) + "_" + tf_price[i].getText();
                    }
                    if (i > tf_price.length - 1 && i < 2 * tf_price.length - 1) {
                        price_volumes_to_publicise[1] = price_volumes_to_publicise[1] + "v" + (+i - PERIODS + 1) + "_" + tf_volume[i - PERIODS].getText() + "-";
                    }
                    if (i == 2 * tf_price.length - 1) {
                        price_volumes_to_publicise[1] = price_volumes_to_publicise[1] + "v" + (+i - PERIODS + 1) + "_" + tf_volume[i - PERIODS].getText();
                    }
//                    System.out.println(price_volumes_to_publicise[0]);
//                    System.out.println(price_volumes_to_publicise[1]);

                }
//            price_volumes_to_publicise = price_volumes_to_publicise + "v" + (+tf_price.length) + "_" + tf_price[tf_price.length - 1].getText();

            } else if (result == 1) {

                int aux = JOptionPane.showOptionDialog(parent, "Do you really want to finish the negotation?", " WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices1, null);

                if (aux == 0) {
                    seller.terminateAgent();
                    return null;
                }
            }
        }
//        int test=seller.checkIfContract();
//        if (test==0){
//       seller.defineContract();
//        }
        ArrayList<Double> price_target_final = new ArrayList<>();
        ArrayList<Double> volume_target_final = new ArrayList<>();

        for (int i = 0; i < tf_price.length; i++) {
//                price_target_final.add(Double.valueOf(tf_price_target[i].getText()));
            tf_price_target[i] = tf_price[i];
            price_target_final.add(Double.valueOf(tf_price[i].getText()));
            volume_target_final.add(Double.valueOf(tf_volume[i].getText()));
        }

        seller.setPricesTarget(price_target_final);
        seller.setVolumesTarget(volume_target_final);

        return price_volumes_to_publicise;

    }

    public void askPriorities(SellerGui parent) {

        if (seller.PERIODS != 24) {
            redefine();
        }

        String[] my_prices_per = new String[PERIODS];
        String[] my_volume_per = new String[PERIODS];
        String[] my_prices_array = new String[DEFAULT];
        String[] my_volumes_array = new String[DEFAULT];
        for (int i = 0; i < PERIODS; i++) {
            my_prices_per[i] = "0";
            my_volume_per[i] = "0";
        }

        for (int i = 0; i < tf_price.length; i++) {
            tf_price[i] = new JTextField(64);
            tf_volume[i] = new JTextField(64);
        }

        if (!seller.searchPartialBelief("myagent", "prices;").equals("-1")) {
            String my_prices = seller.searchPartialBelief("myagent", "prices;");
            String[] my_prices_max_array_aux = my_prices.split(";")[2].split("-");
            for (int i = 0; i < my_prices_max_array_aux.length; i++) {
                if (PERIODS == 24) {
                    tf_price[i].setText(my_prices_max_array_aux[i].split("_")[1].toString());
//            tf_price[i].setText(String.valueOf(0.95*Double.parseDouble(my_prices_max_array_aux[i].split("_")[1].toString())));
                } else {
                    my_prices_array[i] = my_prices_max_array_aux[i].split("_")[1];
                }
            }

        }
        if (!seller.searchPartialBelief("myagent", "volumes;").equals("-1")) {
            String my_volumes = seller.searchPartialBelief("myagent", "volumes;");
            String[] my_volumes_max_array_aux = my_volumes.split(";")[2].split("-");
            for (int i = 0; i < my_volumes_max_array_aux.length; i++) {
                if (PERIODS == 24) {
                    tf_volume[i].setText(my_volumes_max_array_aux[i].split("_")[1].toString());
                } else {
                    my_volumes_array[i] = my_volumes_max_array_aux[i].split("_")[1];
                }
            }

        }
        if (PERIODS != 24) {
            HOURS = seller.HOURS;
            houraux = HOURS.split(",");

            int z = 0;
            for (int j = 0; j < PERIODS; j++) {
                for (int i = 0; i < (int) Double.parseDouble(houraux[j + 24]); i++) {
                    int a = (int) Double.parseDouble(houraux[z]);
                    my_prices_per[j] = twodecimal.format(Double.parseDouble(my_prices_per[j]) + Double.parseDouble(my_prices_array[a - 1])).replace(",", ".");
                    my_volume_per[j] = twodecimal.format(Double.parseDouble(my_volume_per[j]) + Double.parseDouble(my_volumes_array[a - 1])).replace(",", ".");
                    z++;
                }
                my_prices_per[j] = twodecimal.format((Double.parseDouble(my_prices_per[j])) / Double.parseDouble(houraux[j + 24])).replace(",", ".");
                my_volume_per[j] = twodecimal.format((Double.parseDouble(my_volume_per[j])) / Double.parseDouble(houraux[j + 24])).replace(",", ".");
                tf_price[j].setText(my_prices_per[j]);
                tf_volume[j].setText(my_volume_per[j]);
            }
        } else {

            for (int i = 0; i < my_prices_array.length; i++) {
                my_prices_per[i] = my_prices_array[i];
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
//        panel.setMinimumSize(new Dimension(400, 300));
//        panel.setPreferredSize(new Dimension(400, 300));

        //Panel north
        JPanel panel_north = new JPanel();

        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(365, 65));
        panel_north.setPreferredSize(new Dimension(365, 65));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(365, 60));
        panel_text_background.setPreferredSize(new Dimension(365, 60));
        label_text.setMinimumSize(new Dimension(365, 60));
        label_text.setPreferredSize(new Dimension(365, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);

        label_text.setText("<html>Rank-order the Periods, i. e., define the most<br>important, the second most important, and so on</html>");

        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Panel center
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new FlowLayout());
        panel_center.setMinimumSize(new Dimension(250, 37));
        panel_center.setPreferredSize(new Dimension(250, 37));
        panel_center.add(cb_sort_titles);
        cb_sort_titles.setSelectedIndex(0);
        cb_sort_titles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (!sort_listener_occupied) {
                    sort_listener_occupied = true;
                    ArrayList<Double> auxsol = new ArrayList<>();
                    int aux = 0;

                    if (cb_sort_titles.getSelectedItem().equals("Price")) {
                        for (int i = 0; i < PERIODS; i++) {
                            auxsol.add(Double.valueOf(tf_price[i].getText()));
                        }
                        for (int i = 0; i < PERIODS; i++) {
                            listsort[auxsol.indexOf(Collections.max(auxsol))] = (i + 1);
                            aux = auxsol.indexOf(Collections.max(auxsol));
                            auxsol.remove(aux);
                            auxsol.add(aux, -200.00);
                        }

                    } else if (cb_sort_titles.getSelectedItem().equals("Volume")) {
                        for (int i = 0; i < PERIODS; i++) {
                            auxsol.add(Double.valueOf(tf_price[i].getText()));
                        }
                        for (int i = 0; i < PERIODS; i++) {
                            listsort[auxsol.indexOf(Collections.max(auxsol))] = (i + 1);
                            aux = auxsol.indexOf(Collections.max(auxsol));
                            auxsol.remove(aux);
                            auxsol.add(aux, -200.00);
                        }
                    }
                    if (cb_sort_titles.getSelectedIndex() != sort_titles.length - 1) {
                        for (int i = 0; i < PERIODS; i++) {
                            sort_alg[i].setText("" + listsort[i]);
                        }

                    } else {
                        for (int i = 0; i < PERIODS; i++) {
                            sort_alg[i].setText("" + i);
                        }

                    }
                    sort_listener_occupied = false;
                }
            }
        });

        JPanel panel_south = new JPanel();

        panel_south.setLayout(new GridBagLayout());
        panel_south.setMinimumSize(new Dimension(250, 250));
        panel_south.setPreferredSize(new Dimension(250, 250));
        if (PERIODS <= 12 && PERIODS > 6) {
            panel_south.setMinimumSize(new Dimension(250, 350));
            panel_south.setPreferredSize(new Dimension(250, 350));;
        }
        if (PERIODS > 12) {
            panel_south.setMinimumSize(new Dimension(400, 350));
            panel_south.setPreferredSize(new Dimension(400, 350));;
        }

//        panel_south.setMinimumSize(new Dimension((int)auxperiodsx*400, (int)auxperiodsy*400));
//        panel_south.setPreferredSize(new Dimension((int)auxperiodsx*400, (int)auxperiodsy*400));
//         panel_south.setMinimumSize(new Dimension(400, 200));
//        panel_south.setPreferredSize(new Dimension(400, 200));
        // Label names
        JLabel l = new JLabel("Period load");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
//        panel_south.add(l, gridBagConstraints);

//        JLabel l1 = new JLabel("Energy (kWh)");
//        l1.setFont(font_1);
//        l1.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
//        gridBagConstraints1.gridx = 1;
//        gridBagConstraints1.gridy = 0;
//        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
//        panel_south.add(l1, gridBagConstraints1);
        if (PERIODS > 12) {
//        l = new JLabel("Period load");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 2;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
//        panel_south.add(l, gridBagConstraints);

//        l1 = new JLabel("Energy (kWh)");
//        l1.setFont(font_1);
//        l1.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints1 = new GridBagConstraints();
//        gridBagConstraints1.gridx = 3;
//        gridBagConstraints1.gridy = 0;
//        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
//        panel_south.add(l1, gridBagConstraints1);
        }
//        JLabel l2 = new JLabel("day(s)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 3;
//        gridBagConstraints2.gridy = 7;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(5, 20, 10, 15);
//        panel_south.add(l2, gridBagConstraints2);

        //Label textfields
        for (int i = 0; i < PERIODS; i++) {
            int j = i;

            l = new JLabel("Period " + (i + 1) + ":");

            l.setFont(font_1);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();

            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 1;
//            }if((i>=6 && i<12)){
//            gridBagConstraints.gridx = 2;
//            gridBagConstraints.gridy = i-6 + 1;
//            }if((i>=12 && i<18)){
//            gridBagConstraints.gridx = 0;
//            gridBagConstraints.gridy = i-6 + 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 3);
            if ((i >= 12 && PERIODS > 12)) {
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = i - 12 + 1;
            }
            if (i >= PERIODS) {
                gridBagConstraints.gridx = 0;
                gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 3);
                if (PERIODS > 12) {
                    gridBagConstraints.gridx = 1;
                    gridBagConstraints.gridy = 13;
                }
            }

//            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 3);
            panel_south.add(l, gridBagConstraints);

            sort_alg[i] = new JTextField(64);

//            tf_volume_profile[i].setText(String.valueOf(i + 1));
            ArrayList<Double> auxsol = new ArrayList<>();
            int aux = 0;

            for (int u = 0; u < PERIODS; u++) {
                auxsol.add(Double.valueOf(tf_price[u].getText()));
            }
            for (int u = 0; u < PERIODS; u++) {
                listsort[auxsol.indexOf(Collections.max(auxsol))] = (u + 1);
                aux = auxsol.indexOf(Collections.max(auxsol));
                auxsol.remove(aux);
                auxsol.add(aux, -200.00);
            }

            sort_alg[i].setText("" + listsort[i]);

            sort_alg[i].setMinimumSize(new Dimension(70, 25));
            sort_alg[i].setPreferredSize(new Dimension(70, 25));

            sort_alg[i].getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkChanges();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkChanges();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkChanges();
                }

                private void checkChanges() {
                    if (!sort_listener_occupied) {
                        sort_listener_occupied = true;

                        for (int i = 0; i < PERIODS; i++) {
                            auxlist[i] = (int) Math.round(Double.valueOf(sort_alg[i].getText()));
                        }

                        if (Arrays.equals(auxlist, listsort)) {
                            cb_sort_titles.setSelectedIndex(cb_sort_titles.getSelectedIndex());

                        } else {
                            cb_sort_titles.setSelectedIndex(sort_titles.length - 1);
                        }
                        sort_listener_occupied = false;
                    }
                }
            });
//            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(0, 5, 0, 3);
            if (i < 12) {
                gridBagConstraints.gridx = 1;
            }
            if (i >= 12 && i < PERIODS) {
                gridBagConstraints.gridx = 3;
            }
            if (i >= PERIODS) {
                gridBagConstraints.gridx = 1;
                if (i > 12) {
                    gridBagConstraints.gridx = 2;
                }
                gridBagConstraints.insets = new Insets(20, 5, 0, 3);
            }
//            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

            panel_south.add(sort_alg[i], gridBagConstraints);

        }
//        JScrollPane scrollpane = new JScrollPane(panel_south,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        scrollpane.setPreferredSize(new Dimension (400,400));
//        panel_south.setBorder(new BevelBorder(BevelBorder.LOWERED));
//        JScrollPane scrollpane = new JScrollPane(panel_south);

        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
        panel.add(panel_south, BorderLayout.SOUTH);
//        panel.add(scrollpane, BorderLayout.SOUTH);
        String[] choices = {"OK", "Continue"};
        String[] choices1 = {"Yes", "No"};
        int result = 1;

//        while (result==1){ 
        result = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName()/*+" Priorities"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        while (result != -1 && checkEmptyFields(sort_alg)) {
            JOptionPane.showMessageDialog(parent, new JLabel("<html>Some inputs are missing</html>"), "Priorities", JOptionPane.ERROR_MESSAGE);
            result = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName()/*+" Priorities"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
        }
        if (result == 0) {

        } else if (result == 1) {

            if (seller.risk == 0) {
                askTargets(parent);
            } else {
                askPriceLimits(parent);
            }

//              askTargets(parent);        
        }
//        }

//        buyer.setContractDuration(tf_volume_profile[PERIODS].getText());
//        int test=buyer.checkIfContract();
//        if (test==0){
//        buyer.defineContract();
//        }
//        buyer.checkIfReadyToNegotiate();
//        askLimits(parent);
    }

    public void askPriceLimits(SellerGui parent) {

        String my_prices_mec = seller.searchPartialBelief("myagent", "pricesMec;");
        String[] my_prices_mec_array_aux = my_prices_mec.split(";")[2].split("-");
        String[] my_prices_mec_array = new String[DEFAULT];
        String[] my_prices_per_mec = new String[PERIODS];
        for (int i = 0; i < PERIODS; i++) {
            my_prices_per_mec[i] = "0";
        }
//        String my_volumes = seller.searchPartialBelief("myagent", "volumes;");
//        String[] my_volumes_array_aux= my_volumes.split(";")[2].split("-");
//        String[] my_volumes_array= new String[PERIODS];
        for (int i = 0; i < my_prices_mec_array_aux.length; i++) {
            my_prices_mec_array[i] = my_prices_mec_array_aux[i].split("_")[1];
//            my_volumes_array[i]=my_volumes_array_aux[i].split("_")[1];
        }
//        deviation= Risk.deviation(PERIODS);
        if (PERIODS != 24) {

            int z = 0;
            for (int j = 0; j < PERIODS; j++) {
                for (int i = 0; i < (int) Double.parseDouble(houraux[j + 24]); i++) {
                    int a = (int) Double.parseDouble(houraux[z]);
                    my_prices_per_mec[j] = twodecimal.format(Double.parseDouble(my_prices_per_mec[j]) + Double.parseDouble(my_prices_mec_array[a - 1])).replace(",", ".");;
                    z++;
                }
                my_prices_per_mec[j] = twodecimal.format((Double.parseDouble(my_prices_per_mec[j])) / Double.parseDouble(houraux[j + 24])).replace(",", ".");;
            }
        } else {

            for (int i = 0; i < my_prices_mec_array.length; i++) {
                my_prices_per_mec[i] = my_prices_mec_array[i];
                deviation[i] = 0.96 * Double.valueOf(my_prices_per_mec[i]) - Double.valueOf(my_prices_per_mec[i]);
                deviation[i + PERIODS] = 1.05 * Double.valueOf(my_prices_per_mec[i]) - Double.valueOf(my_prices_per_mec[i]);
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setMinimumSize(new Dimension(300, 360));
        panel_center.setPreferredSize(new Dimension(300, 360));
        if (PERIODS <= 12 && PERIODS > 6) {
            panel_center.setMinimumSize(new Dimension(300, 360));
            panel_center.setPreferredSize(new Dimension(300, 360));
        }
        if (PERIODS > 12) {
            panel_center.setMinimumSize(new Dimension(500, 360));
            panel_center.setPreferredSize(new Dimension(500, 360));
        }

        // Panel north
        JPanel panel_north = new JPanel();

        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(450, 65));
        panel_north.setPreferredSize(new Dimension(450, 65));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(395, 60));
        panel_text_background.setPreferredSize(new Dimension(395, 60));
        label_text.setMinimumSize(new Dimension(395, 60));
        label_text.setPreferredSize(new Dimension(395, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Enter your Market Range prediction (€/MWh)</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Panel center
        // Label names
        JLabel l = new JLabel("Period load");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 30);
//        panel_center.add(l, gridBagConstraints);

        JLabel l1 = new JLabel("Min");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
        panel_center.add(l1, gridBagConstraints1);

        l1 = new JLabel("Price");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
        panel_center.add(l1, gridBagConstraints1);

        l1 = new JLabel("Max");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
        panel_center.add(l1, gridBagConstraints1);

//        JLabel l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 2;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        if (PERIODS > 12) {
            l1 = new JLabel("Min");
            l1.setFont(font_1);
            l1.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 5;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
            panel_center.add(l1, gridBagConstraints1);

            l1 = new JLabel("Price");
            l1.setFont(font_1);
            l1.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 6;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
            panel_center.add(l1, gridBagConstraints1);

            l1 = new JLabel("Max");
            l1.setFont(font_1);
            l1.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 7;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints1.insets = new java.awt.Insets(10, 5, 5, 5);
            panel_center.add(l1, gridBagConstraints1);
//        l2 = new JLabel("Energy (kWh)");
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints2 = new GridBagConstraints();
//        gridBagConstraints2.gridx = 5;
//        gridBagConstraints2.gridy = 0;
//        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints2.insets = new java.awt.Insets(10, 5, 5, 5);
//        panel_center.add(l2, gridBagConstraints2);
        }

        //Label textfields
        for (int i = 0; i < PERIODS; i++) {

            l = new JLabel("Period " + (i + 1) + ":");
            l.setFont(font_1);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
            if (i >= 12 && PERIODS > 12) {
                gridBagConstraints.gridx = 4;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 3);
            }
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;

            panel_center.add(l, gridBagConstraints);

            tf_price_limit[i] = new JTextField(64);
            tf_price_limit[i].setText(twodecimal.format(Double.valueOf(my_prices_per_mec[i]) + deviation[i]).replace(",", "."));
//            tf_price_limit[i].setText(twodecimal.format(0.95*Double.valueOf(my_prices_per_mec[i])).replace(",", "."));
            tf_price_limit[i].setMinimumSize(new Dimension(40, 25));
            tf_price_limit[i].setPreferredSize(new Dimension(40, 25));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            if (i >= 12) {
                gridBagConstraints.gridx = 5;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            }
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

            panel_center.add(tf_price_limit[i], gridBagConstraints);

            tf_price_mec[i] = new JTextField(64);
            tf_price_mec[i].setText(my_prices_per_mec[i]);
            tf_price_mec[i].setMinimumSize(new Dimension(40, 25));
            tf_price_mec[i].setPreferredSize(new Dimension(40, 25));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            if (i >= 12) {
                gridBagConstraints.gridx = 6;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            }
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

            panel_center.add(tf_price_mec[i], gridBagConstraints);

            tf_price_target[i] = new JTextField(64);
            tf_price_target[i].setText(twodecimal.format(Double.valueOf(my_prices_per_mec[i]) + deviation[i + PERIODS]).replace(",", "."));
//            tf_price_target[i].setText(twodecimal.format(1.07*Double.valueOf(my_prices_per_mec[i])).replace(",", "."));
            tf_price_target[i].setMinimumSize(new Dimension(40, 25));
            tf_price_target[i].setPreferredSize(new Dimension(40, 25));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.insets = new Insets(0, 5, 0, 30);
            if (i >= 12) {
                gridBagConstraints.gridx = 7;
                gridBagConstraints.gridy = i + 1 - 12;
                gridBagConstraints.insets = new Insets(0, 5, 0, 0);
            }
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;

            panel_center.add(tf_price_target[i], gridBagConstraints);

//            tf_volume_limit[i] = new JTextField(64);
////            tf_volume_limit[i].setText(String.valueOf((Math.random() * 10) + 30).substring(0, 2));
//            tf_volume_limit[i].setText(tf_volume[i].getText());
//            tf_volume_limit[i].setMinimumSize(new Dimension(70, 25));
//            tf_volume_limit[i].setPreferredSize(new Dimension(70, 25));
//            tf_volume_limit[i].setEditable(false);
//            gridBagConstraints = new GridBagConstraints();
//            gridBagConstraints.gridx = 2;
//            gridBagConstraints.gridy = i + 1;
//            if (i>=12){
//            gridBagConstraints.gridx = 5;
//            gridBagConstraints.gridy = i + 1-12;
//            }
//            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//            gridBagConstraints.insets = new Insets(0, 10, 0, 3);
//            panel_center.add(tf_volume_limit[i], gridBagConstraints);
        }

        panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);

//        String[] choices = {"Back","OK", "Continue"};
        String[] choices = {"Back", "OK", "Continue"};

        int result = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        while (result != -1 && checkEmptyFields(tf_price_limit) && checkEmptyFields(tf_price_mec) && checkEmptyFields(tf_price_target)) {

            JOptionPane.showMessageDialog(parent, new JLabel("<html>Some inputs are missing</html>"), "Limits", JOptionPane.ERROR_MESSAGE);
            result = JOptionPane.showOptionDialog(parent, panel, "Limits", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

        }
//        if (result == 0) {
//        askTargets(parent);
//        return;
//        }else
        if (result == 1 || result == 2) {
            ArrayList<Double> price_limit_final = new ArrayList<>();
            ArrayList<Double> price_target_final = new ArrayList<>();

            for (int i = 0; i < tf_price_limit.length; i++) {
                price_limit_final.add(Double.valueOf(tf_price_limit[i].getText()));
                price_target_final.add(Double.valueOf(tf_price_target[i].getText()));
                price_mec.add(Double.valueOf(tf_price_mec[i].getText()));
            }

//            seller.setPricesLimit(price_mec);
            seller.setPricesLimit(price_limit_final);
            seller.setPricesTarget(price_target_final);
            seller.checkIfReadyToNegotiate();

            if (result == 2) {
                askTargets(parent);
            }
//            ArrayList<Double> price_limit_final = new ArrayList<>();
//            ArrayList<Double> volume_limit_final = new ArrayList<>();
//
//            for (int i = 0; i < tf_price_limit.length; i++) {
//                price_limit_final.add(Double.valueOf(tf_price_limit[i].getText()));
//
//            }
//            seller.setPricesLimit(price_limit_final);
//            seller.setVolumesLimit(volume_limit_final);

        } else if (result == 0) {
            askPriorities(parent);
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

    public String askDeadline(SellerGui parent, String proposal) {

        Date date_proposed = new Date();
        if (proposal != null) {
            date_proposed.setTime(Long.valueOf(proposal));
        } else {
            date_proposed.setTime(System.currentTimeMillis());
        }

        JPanel panel = new JPanel(new BorderLayout());

        //Panel north
        JPanel panel_north = new JPanel();

        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(400, 70));
        panel_north.setPreferredSize(new Dimension(400, 70));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(345, 60));
        panel_text_background.setPreferredSize(new Dimension(345, 60));
        label_text.setMinimumSize(new Dimension(345, 60));
        label_text.setPreferredSize(new Dimension(345, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        if (proposal != null) {
            label_text.setText("<html>Opponent deadline proposal:<br>" + date_proposed.toString() + "</html>");
        } else {
            label_text.setText("<html>Enter inital deadline proposal</html>");
        }
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Panel center
        panel.add(panel_north, BorderLayout.NORTH);

        if (proposal != null) {
            String[] choices = {"Accept", "Propose"};
            int result = JOptionPane.showOptionDialog(parent, panel, "Deadline", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
            if (result == 1) {
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
                if (result_date == 0) {
                    test++;
                    return String.valueOf(tc.getDate().getTime());
                }
            } else if (result == 0) {
                test++;
                return proposal;
            }
        } else {
            String[] choices = {"Propose"};
            int result = JOptionPane.showOptionDialog(parent, panel, "Deadline", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
            if (result == 0) {
                TimeChooser tc = new TimeChooser(date_proposed);
                int result_date = tc.showEditTimeDlg(parent);
                System.out.println(date_proposed.getTime() + "   " + System.currentTimeMillis());
                while (tc.getDate().getTime() <= System.currentTimeMillis()) {
                    String[] choices2 = {"OK"};
                    int aux = JOptionPane.showOptionDialog(parent, "You must choose a date higher than the current date", " WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices2, null);
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
                if (result_date == 0) {
                    test++;
                    return String.valueOf(tc.getDate().getTime());
                }
            }
        }

        return null;
    }

    public void setPersonalInfo(ArrayList<String> _infoList) {
        for (int i = 0; i < _infoList.size(); i++) {
            System.out.println(_infoList.get(i).toString());
        }
        seller.setPersonalInfo(_infoList);
    }

    public void askPersonalInfo() {
        if (setPowerPlant != null) {
            setPowerPlant.setVisible(false);
        }
        //NewAgent_Info newForm = new NewAgent_Info(this, null, seller.getLocalName());
        //newForm.setVisible(true);
        //seller.setPersonalInfo(personal_info);
        ArrayList<String> initial_values = new ArrayList();
        String str = seller.getLocalName();
        initial_values.add(str.replace("_", " "));
        initial_values.add("Rua das Palmeiras");
        initial_values.add("912362654");
        //initial_values.add("211246756");
        initial_values.add(str.replace("_", "") + "@generator.com");

        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
        //JPanel panel_north = new JPanel();

        /*panel_north.setLayout(new BorderLayout());
         panel_north.setMinimumSize(new Dimension(400, 65));
         panel_north.setPreferredSize(new Dimension(400, 65));
         panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));*/
        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(245, 60));
        panel_text_background.setPreferredSize(new Dimension(245, 60));
        label_text.setMinimumSize(new Dimension(245, 60));
        label_text.setPreferredSize(new Dimension(245, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setText("<html>Please enter the producer's information</html>");
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        //panel_north.add(panel_text_background, BorderLayout.CENTER);

        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            //panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(SellerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Panel center
        JPanel panel_center = new JPanel();

        panel_center.setLayout(new GridBagLayout());
        panel_center.setMinimumSize(new Dimension(400, 200));
        panel_center.setPreferredSize(new Dimension(400, 200));

        // Label names
        JLabel l = new JLabel("Name :");
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 10, 30);
        panel_center.add(l, gridBagConstraints);

        l = new JLabel("Address :");
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 10, 30);
        panel_center.add(l, gridBagConstraints);

        l = new JLabel("Telephone :");
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 10, 30);
        panel_center.add(l, gridBagConstraints);

        l = new JLabel("E-mail :");
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 10, 30);
        panel_center.add(l, gridBagConstraints);

        //Label textfields
        for (int i = 0; i < 4; i++) {

            tf_personal_info[i] = new JTextField(64);
            tf_personal_info[i].setText(initial_values.get(i));
            tf_personal_info[i].setMinimumSize(new Dimension(230, 25));
            tf_personal_info[i].setPreferredSize(new Dimension(230, 25));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new Insets(12, 5, 5, 5);
            tf_personal_info[0].setEditable(false);
            panel_center.add(tf_personal_info[i], gridBagConstraints);
        }

        //panel_center.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panel_center.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Personal Info", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        //panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
        Object[] options = {"Cancel", "Next"};
        int result = JOptionPane.showOptionDialog(null, panel, "Generator Info", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

        while (result != -1 && (checkEmptyFields(tf_personal_info))) {

            JOptionPane.showMessageDialog(null, new JLabel("<html>Some inputs are missing</html>"), "Generator Info", JOptionPane.ERROR_MESSAGE);
            result = JOptionPane.showOptionDialog(null, panel, "Personal Info", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        }

        if (result == 1) {
            ArrayList<String> personal_info = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                personal_info.add(tf_personal_info[i].getText());
            }
            if(setPowerPlant == null) {
                setPowerPlant = new SellerTechnology(seller, this);
            } 
            seller.setPersonalInfo(personal_info);
            setPowerPlant.setVisible(true);
            
        } else {
            this.dispose();
        }
    }
    
    public void setPersonalInfo_aux(ArrayList<String> _personal_info) {
        this.personal_info = _personal_info; 
    }
    
    public ArrayList<String> getPersonalInfo_aux() {
        return this.personal_info;
    }

    public void ChangeStrategy(SellerGui parent) {

        JPanel panel_center = new JPanel();
        panel_center.setLayout(new GridBagLayout());
        panel_center.setMinimumSize(new Dimension(200, 100));
        panel_center.setPreferredSize(new Dimension(200, 100));

        JLabel l = new JLabel("Strategy:");
        l.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        panel_center.add(l, gridBagConstraints);

        JMenu[] menus = new JMenu[5];

        menus[0] = concession;
        menus[1] = problem;
        menus[2] = matching;
        menus[3] = contending;
        menus[4] = risks;
//    menus[2] = demand;
//    menus[3]= Demand;

//    menus[0].add(menus[1]);
//    menus[0].add(menus[2]);
//    menus[0].add(menus[3]);
        menus[0].add(making);
        menus[0].add(E_R);
        menus[0].add(conceder);
        menus[0].add(menus[2]);
        menus[3].add(boulware);
        menus[2].add(tit);
        menus[2].add(inversetit);
        menus[2].add(randTit);
        menus[1].add(low);
        menus[1].add(intrasigent);
        menus[4].add(riskstrat);

        JMenu menu = ComboMenuBar.createMenu(menu1.getText());
        menu.add(menus[0]);
        menu.add(menus[1]);
        menu.add(menus[3]);
        menu.add(menus[4]);
////    menu.addSeparator();
//    menu.add(menus[2]);
//    menu.add(menus[3]);

        ComboMenuBar comboMenu = new ComboMenuBar(menu);

        comboMenu.setMinimumSize(new Dimension(150, 25));
        comboMenu.setPreferredSize(new Dimension(150, 25));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        panel_center.add(comboMenu, gridBagConstraints);

        String[] choices2 = {"Set", "Cancel"};

        int b = JOptionPane.showOptionDialog(parent, panel_center, "Producer: " + seller.getLocalName()/*+" Strategy"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices2, null);
        if (b == 0) {
            seller.setNegotiationStrategy((String) comboMenu.getSelectedItem());
        }
    }

    public void ES(SellerGui parent) {
        String[][] user = {{"moderate"}};
        String[] choices = {"Keep", "Set", "Choose"};
        int[] w = {1};
        double aux = 0;
        String[][] CFs = new String[1][sendCF[0].length - 2];

        for (int i = 0; i < sendCF[0].length - 3; i++) {
            CFs[0][i] = sendCF[0][i];
        }
        CFs[0][CFs[0].length - 1] = sendCF[0][sendCF[0].length - 2];

        if (seller.received_history.size() <= 1) {

        } else {
            for (int j = 0; j < PERIODS; ++j) {
                aux = aux + 1 - (seller.received_history.get(seller.received_history.size() - 1)[j] / seller.received_history.get(seller.received_history.size() - 2)[j]);
            }
            aux = aux / PERIODS;
            if (aux > 15.0) {
                user[0][0] = "soft";
                w[0] = 2;
                CFs[0][CFs[0].length - 1] = sendCF[0][sendCF[0].length - 1];
            } else if (aux < 5.0) {
                user[0][0] = "tough";
                w[0] = 0;
                CFs[0][CFs[0].length - 1] = sendCF[0][sendCF[0].length - 3];
            }
        }

        try {
            ExpertSystem ES = new ExpertSystem(strategy_list, tactic, user, w, CFs);
            sol = ES.sol;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sol.length; i++) {
            for (int j = 0; j < sol.length; j++) {
                if (sol[i][0].equals(strategy_arg[j])) {
                    sol[i][0] = strategy_list[j];
                    j = sol.length;
                }
            }
        }
        JPanel panel = new JPanel(new BorderLayout());

        // Panel north
        JPanel panel_north = new JPanel();

        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(400, 65));
        panel_north.setPreferredSize(new Dimension(400, 65));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        //Panel center - icon
        JPanel panel_center = new JPanel();

//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);
        panel_center.setMinimumSize(new Dimension(400, 100));
        panel_center.setPreferredSize(new Dimension(400, 250));

        panel_center.setLayout(new GridBagLayout());
        String[] all = new String[sol.length + 1];
        all[0] = "Do you want to change your " + seller.negotiation_strategy + " Strategy?";
        JLabel l = new JLabel(all[0]);
//        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        panel_north.add(l, BorderLayout.CENTER);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        l = new JLabel(" ");
//        l.setFont(font_2);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        panel_center.add(l, gridBagConstraints);
        for (int i = 0; i < sol.length; i++) {
            all[i + 1] = "Solution " + (i + 1) + ": " + "Strategy " + sol[i][0] + " with CF= " + sol[i][1] + "\n";
            l = new JLabel("Solution " + (i + 1) + ": ");
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.insets = new Insets(10, 10, 5, 5);
            panel_center.add(l, gridBagConstraints);
            l = new JLabel("Strategy " + sol[i][0]);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.insets = new Insets(10, 5, 5, 5);
            panel_center.add(l, gridBagConstraints);
            l = new JLabel(" CF= " + sol[i][1]);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints.gridx = 2;
            gridBagConstraints.insets = new Insets(10, 5, 5, 10);
            panel_center.add(l, gridBagConstraints);
        }
//                 System.out.println("Buyer\n"+all);

        //Panel center
//        JTextField[] prices = new JTextField[s.length];
//        for (int i = 0; i < s.length; i++) {
//        if(i>12){
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = i+1; 
//        }
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 3);/*top,left,buttom,right*/
//        if (i<3){
//            gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 3);/*top,left,buttom,right*/
//        }
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
        int b = 1;

        while (b == 1) {
            int a = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName() + " is using a " + tactic + " tactic", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
//        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

            if (a == 1) {
                seller.setNegotiationStrategy(sol[0][0]);
                b = 0;
            } else if (a == 2) {

                panel_center = new JPanel();
                panel_center.setLayout(new GridBagLayout());
                panel_center.setMinimumSize(new Dimension(200, 100));
                panel_center.setPreferredSize(new Dimension(200, 100));

                l = new JLabel("Strategy:");
                l.setHorizontalAlignment(SwingConstants.LEFT);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                panel_center.add(l, gridBagConstraints);

                JMenu[] menus = new JMenu[5];

                menus[0] = concession;
                menus[1] = problem;
                menus[2] = matching;
                menus[3] = contending;
                menus[4] = risks;
//    menus[2] = demand;
//    menus[3]= Demand;

//    menus[0].add(menus[1]);
//    menus[0].add(menus[2]);
//    menus[0].add(menus[3]);
                menus[0].add(making);
                menus[0].add(E_R);
                menus[0].add(conceder);
                menus[0].add(menus[2]);
                menus[3].add(boulware);
                menus[2].add(tit);
                menus[2].add(inversetit);
                menus[2].add(randTit);
                menus[1].add(low);
                menus[1].add(intrasigent);
                menus[4].add(riskstrat);

                JMenu menu = ComboMenuBar.createMenu(menu1.getText());
                menu.add(menus[0]);
                menu.add(menus[1]);
                menu.add(menus[3]);
                menu.add(menus[4]);
////    menu.addSeparator();
//    menu.add(menus[2]);
//    menu.add(menus[3]);

                ComboMenuBar comboMenu = new ComboMenuBar(menu);

                comboMenu.setMinimumSize(new Dimension(150, 25));
                comboMenu.setPreferredSize(new Dimension(150, 25));
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new Insets(0, 0, 0, 0);
                panel_center.add(comboMenu, gridBagConstraints);

                String[] choices2 = {"Set", "Cancel"};
                b = JOptionPane.showOptionDialog(parent, panel_center, "Producer: " + seller.getLocalName()/*+" Strategy"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices2, null);
                if (b == 0) {
                    seller.setNegotiationStrategy((String) comboMenu.getSelectedItem());
                }

            } else {
                b = 0;
            }
        }
    }

    public void askUserES(SellerGui parent) {

        JPanel panel = new UserES();
//        JPanel panel= new Expert();
        String[] choices = {"OK", "Cancel"};
        int result = 1;

        while (result == 1) {
            result = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName() + " Expert System Profile", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

            while (result != -1 && checkEmptyFields(ES_profile)) {
                JOptionPane.showMessageDialog(parent, new JLabel("<html>Some inputs are missing</html>"), "Expert System Profile", JOptionPane.ERROR_MESSAGE);
                result = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName() + " Expert System Profile", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
            }
            if (result == 0) {

                for (int i = 0; i < sendCF[0].length; i++) {
                    sendCF[0][i] = ES_profile[i].getText();
                }
            }
        }
    }

    public class UserES extends javax.swing.JPanel {

        public UserES() {
            initComponents();
        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
        public void initComponents() {
            JLabel jLabel1 = new javax.swing.JLabel();
            JLabel jLabel2 = new javax.swing.JLabel();
            JLabel jLabel3 = new javax.swing.JLabel();
            JLabel jLabel4 = new javax.swing.JLabel();

            JLabel jLabel5 = new javax.swing.JLabel();

            JLabel jLabel6 = new javax.swing.JLabel();

            JLabel jLabel7 = new javax.swing.JLabel();

            JLabel jLabel8 = new javax.swing.JLabel();
            JLabel jLabel9 = new javax.swing.JLabel();
            JLabel jLabel10 = new javax.swing.JLabel();

            JLabel jLabel11 = new javax.swing.JLabel();
            JLabel jLabel12 = new javax.swing.JLabel();

            JLabel jLabel13 = new javax.swing.JLabel();

            JLabel jLabel14 = new javax.swing.JLabel();

            JLabel jLabel15 = new javax.swing.JLabel();

            JLabel jLabel17 = new javax.swing.JLabel();

            JLabel jLabel18 = new javax.swing.JLabel();

            JTextField jTextField7 = new JTextField(3);
            JTextField jTextField8 = new JTextField(3);
            JTextField jTextField9 = new JTextField(3);
            JTextField jTextField10 = new JTextField(3);

            for (int i = 0; i < ES_profile.length; i++) {
                ES_profile[i] = new JTextField(3);
                ES_profile[i].setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
                ES_profile[i].setText("-100");
                ES_profile[i].getDocument().addDocumentListener(new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        checkChanges();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        checkChanges();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        checkChanges();
                    }

                    private void checkChanges() {
                        if (!ES_listener_occupied) {
                            ES_listener_occupied = true;

                            for (int i = 0; i < sendCF[0].length; i++) {
                                ESlist[i] = ES_profile[i].getText();
                            }
                            for (int i = 0; i < CF.length - 1; i++) {
                                if (Arrays.equals(ESlist, CF[i])) {
                                    predefined_ES.setSelectedIndex(i);
                                    i = sendCF.length - 1;
                                } else {
                                    predefined_ES.setSelectedIndex(CF.length - 1);
                                }

                            }
                            ES_listener_occupied = false;
                        }
                    }
                });

            }

            jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
            jLabel1.setText("Tactic:");

            jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            jLabel2.setText("Characteristics:");

            jLabel3.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel3.setText("General Benefit");

            jLabel4.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel4.setText("Benefit Set");

            jLabel5.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel5.setText("Opponent Satisfaction");

            jLabel6.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel6.setText("Deadline");

            jLabel7.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel7.setText("Consistence");

            jLabel8.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            jLabel8.setText("Oponnent ");

            jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            jLabel9.setText("Feedback:");

            jLabel10.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel10.setText("Risk");

//       JTextField jTextField7=new JTextField(64);
            jTextField7.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jTextField7.setText("null");
            jTextField7.setEditable(false);

//       JTextField jTextField8=new JTextField(64);
            jTextField8.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jTextField8.setText("null");
            jTextField8.setEditable(false);

            jLabel11.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel11.setText("Relationship:");

            jLabel12.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel12.setText("Party Reputation");

//       JTextField jTextField9=new JTextField(64);
            jTextField9.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jTextField9.setText("null");
            jTextField9.setEditable(false);

            jLabel13.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel13.setText("Party Relationship");

//       JTextField jTextField10=new JTextField(3);
            jTextField10.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jTextField10.setText("null");
            jTextField10.setEditable(false);

            jLabel14.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel14.setText("Negotiator");

            jLabel15.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel15.setText("Tough");

            jLabel17.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel17.setText("Moderate");

            jLabel18.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
            jLabel18.setText("Soft");

            predefined_ES.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
            predefined_ES.setModel(new javax.swing.DefaultComboBoxModel(tactics));
            if (tactic.equals("")) {
                predefined_ES.setSelectedIndex(tactics.length - 1);
            } else {
                for (int i = 0; i < tactics.length; i++) {
                    if (tactics[i].equals(tactic)) {
                        predefined_ES.setSelectedIndex(i);
                        i = tactics.length;
                    }
                }
            }
            predefined_ES.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    if (!ES_listener_occupied) {
                        ES_listener_occupied = true;

                        for (int i = 0; i < tactics.length - 1; i++) {
                            if (predefined_ES.getSelectedItem().equals(tactics[i])) {
                                sendCF[0] = CF[i];
                                tactic = tactics[i];
                                i = tactics.length;
                            }
                        }
                        if (predefined_ES.getSelectedIndex() != tactics.length - 1) {
                            for (int i = 0; i < sendCF[0].length; i++) {
                                ES_profile[i].setText(sendCF[0][i].toString());
                            }

                        } else {
                            tactic = tactics[tactics.length - 1];
                            for (int i = 0; i < sendCF[0].length; i++) {
                                ES_profile[i].setText("0");
                            }

                        }
                        ES_listener_occupied = false;
                    }
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel8)
                                                    .addComponent(jLabel2)
                                                    .addComponent(jLabel9)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addGap(10, 10, 10)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addComponent(ES_profile[6], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(ES_profile[0], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel14)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(ES_profile[7], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel11)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(layout.createSequentialGroup()
                                                                    .addGap(88, 88, 88)
                                                                    .addComponent(jLabel10)
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                            .addComponent(jLabel7)
                                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                            .addComponent(ES_profile[5], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                            .addComponent(jLabel6)
                                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                            .addComponent(ES_profile[4], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                            .addComponent(jLabel5)
                                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                            .addComponent(ES_profile[3], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                    .addComponent(jLabel3)
                                                                                    .addComponent(jLabel4))
                                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                    .addComponent(ES_profile[2], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                    .addComponent(ES_profile[1], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addComponent(jLabel12)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addComponent(jLabel13)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                            .addComponent(jLabel18)
                                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                            .addComponent(ES_profile[10], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                            .addComponent(jLabel17)
                                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                            .addComponent(ES_profile[9], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                            .addComponent(jLabel15)
                                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                            .addComponent(ES_profile[8], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                    .addGroup(layout.createSequentialGroup()
                                            .addGap(95, 95, 95)
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(predefined_ES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 10, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(predefined_ES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(ES_profile[0], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(81, 81, 81)
                                            .addComponent(jLabel8)
                                            .addGap(2, 2, 2)
                                            .addComponent(jLabel9)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(ES_profile[6], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addContainerGap())
                                    .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addGap(25, 25, 25)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel4)
                                                                    .addComponent(ES_profile[2], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel5)
                                                                    .addComponent(ES_profile[3], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel6)
                                                                    .addComponent(ES_profile[4], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel7)
                                                                    .addComponent(ES_profile[5], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGap(22, 22, 22)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel10)
                                                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel3)
                                                                    .addComponent(ES_profile[1], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGap(141, 141, 141)))
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                            .addGap(17, 17, 17)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel11)
                                                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGap(49, 49, 49)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(ES_profile[7], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(jLabel14)))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel12)
                                                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel13)
                                                                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel15)
                                                                    .addComponent(ES_profile[8], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel17)
                                                                    .addComponent(ES_profile[9], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                    .addComponent(jLabel18)
                                                                    .addComponent(ES_profile[10], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGap(21, 21, 21))))))
            );
        }// </editor-fold>

    }

    public int inter(String[] s, SellerGui parent, String[] choices, int box) {
//        int a=JOptionPane.showConfirmDialog(null, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        UIManager um = new UIManager();
//          um.put("OptionPane.messageForeground", Color.black);
//          um.put("Panel.background", Color.CYAN);

//        JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
////        split_pane_log_image.setOneTouchExpandable(true);
//        split_pane_log_image.setDividerLocation(0);
//        split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));
        Border border = new BorderUIResource(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        um.put("OptionPane.background", Color.BLUE.darker().darker());
        ImageIcon icon = new ImageIcon("images\\retail_energy_globe2.jpg");
        if (seller.getLocalName().equals(seller_list[1])) {
            um.put("OptionPane.background", Color.BLUE.darker().darker().darker().darker());
//          um.put("OptionPane.background", Color.YELLOW.darker().darker().darker());
            icon = new ImageIcon("images\\res2.jpg");
        }
        um.put("OptionPane.border", border);

        //         um.put("OptionPane.messageForeground", Color.CYAN);
//          um.put("OptionPane.title", Color.CYAN);
//          Image img = icon.getImage().getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH);
//          Image img = icon.getImage();
//////          Image img = myIcon2.getImage();  
////          BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB); 
////          Graphics g = bi.createGraphics();  
////          g.drawImage(img, 100,0, img.getWidth(null), img.getHeight(null), null, null);  
////          Image icon1 = new ImageIcon(bi).getImage().getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH);
//          icon = new ImageIcon(img);
//          icon = icon.getImage().getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH);
//         JPanel panel = new JPanel(new BorderLayout());
////
//        // Panel north
//
//        JPanel panel_north = new JPanel();
//
//        panel_north.setLayout(new BorderLayout());
//        panel_north.setMinimumSize(new Dimension(400, 60));
//        panel_north.setPreferredSize(new Dimension(400, 60));
//        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));
        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//          getContentPane().add(panel_left, BorderLayout.WEST);
        if (box == 0) {
            panel_center.setMinimumSize(new Dimension(300, 200));
            panel_center.setPreferredSize(new Dimension(300, 350));
            if (PERIODS > 12) {
                panel_center.setMinimumSize(new Dimension(300, 400));
                panel_center.setPreferredSize(new Dimension(300, 500));
            }
            if (PERIODS > 6 && PERIODS <= 12) {
                panel_center.setMinimumSize(new Dimension(300, 200));
                panel_center.setPreferredSize(new Dimension(300, 350));
            }
        } else {
            panel_center.setMinimumSize(new Dimension(300, 300));
            panel_center.setPreferredSize(new Dimension(350, 350));
            if (PERIODS > 12) {
                panel_center.setMinimumSize(new Dimension(400, 400));
                panel_center.setPreferredSize(new Dimension(400, 400));
            }
            if (PERIODS > 6 && PERIODS <= 12) {
                panel_center.setMinimumSize(new Dimension(400, 400));
                panel_center.setPreferredSize(new Dimension(400, 400));
//        JOptionPane.getRootFrame().setSize(300, 400);
            }
        }

        JPanel panel_icon_background = new JPanel();
        JLabel label_icon = new JLabel();
//        panel_icon_background.setMinimumSize(new Dimension(350, 50));
//        panel_icon_background.setPreferredSize(new Dimension(350, 50));
//        label_icon.setMinimumSize(new Dimension(350, 50));
//        label_icon.setPreferredSize(new Dimension(350, 50));
        label_icon.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 10, 5);
//        label_text.setText("<html>Please enter the seller's information</html>");
//        label_icon.setText(s);
        label_icon.setIcon(icon);
//        label_icon.setFont(font_1);
        panel_icon_background.add(label_icon);
//        panel_center.add(panel_icon_background, BorderLayout.NORTH);

        panel_center.add(panel_icon_background, BorderLayout.NORTH);
//        panel_center.setBackground(Color.WHITE);

        panel_center.setLayout(new GridBagLayout());

        //Panel center
        JTextField[] prices = new JTextField[s.length];

        for (int i = 0; i < s.length; i++) {

            JLabel l = new JLabel(s[i]);
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 3);/*top,left,buttom,right*/

            if (i < 3) {
                gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 3);/*top,left,buttom,right*/

            }
            panel_center.add(l, gridBagConstraints);
        }

//        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
//        panel.add(panel_left, BorderLayout.WEST);

        int a = JOptionPane.showOptionDialog(parent, panel, seller.getLocalName() + " Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
//        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        return a;

    }

    public int inter2(double[] cmp, double[] rcv, double Bcmp, double Brcv, SellerGui parent, String[] choices, int box, int num, int prop, double contr, double[] current) {
//        int a=JOptionPane.showConfirmDialog(null, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

        //Panel north
        list4[0] = threedecimal.format(Bcmp).replace(",", ".");;
        auxBcmp = Bcmp;
        contract = contr;
        String[] choices2 = {"Yes", "No"};
        String[] choices3 = {"OK"};
//            double auxcmp=0;
        UIManager um = new UIManager();
//          um.put("OptionPane.messageForeground", Color.black);
//          um.put("Panel.background", Color.CYAN);
        Border border = new BorderUIResource(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        um.put("OptionPane.background", Color.BLUE.darker().darker());
        ImageIcon icon = new ImageIcon("images\\retail_energy_globe2.jpg");
        String b = "images\\retail_energy_globe2.jpg";
        if (seller.getLocalName().equals(seller_list[1])) {
            um.put("OptionPane.background", Color.BLUE.darker().darker().darker().darker());
//          um.put("OptionPane.background", Color.YELLOW.darker().darker().darker());
            icon = new ImageIcon("images\\res2.jpg");
            b = "images\\res2.jpg";
        }
        um.put("OptionPane.border", border);
//         um.put("OptionPane.messageForeground", Color.DARK_GRAY);
//          um.put("OptionPane.title", Color.CYAN);
//          ImageIcon icon = new ImageIcon("C:\\Documents and Settings\\mit\\My Documents\\My Pictures\\img.gif");

//       int a = JOptionPane.showOptionDialog(parent, s, buyer.getLocalName()+" Starting Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
////        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
//        return a;
        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//                  JPanel panel_north = new JPanel(new GridLayout(1, 2));
        JPanel panel_north = new JPanel(new BorderLayout());
        JPanel panel_north1 = new JPanel();
        JPanel panel_north2 = new JPanel();
//                  panel_north1.setLayout(new GridBagLayout());
        panel_north2.setLayout(new GridBagLayout());

        JPanel panel_south = new JPanel();
//        panel_north.setLayout(new BorderLayout());
        panel_center.setLayout(new FlowLayout());
        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        JPanel panel_icon_background = new JPanel();
        JLabel label_icon = new JLabel();

        if (box == 0) {
            panel_center.setMinimumSize(new Dimension(100, 15));
            panel_center.setPreferredSize(new Dimension(200 + 50 * 1, 30));
            panel_north.setMinimumSize(new Dimension(100, 50));
            panel_north.setPreferredSize(new Dimension(200 + 50 * 1, 100));
//        panel_north1.setMinimumSize(new Dimension(100, 100));
//        panel_north1.setPreferredSize(new Dimension(100, 100));
//        panel_north2.setMinimumSize(new Dimension(150, 100));
//        panel_north2.setPreferredSize(new Dimension(150, 100));
            panel_south.setMinimumSize(new Dimension(250, 100));
            panel_south.setPreferredSize(new Dimension(250 + 50 * 1, 200));
            panel_text_background.setMinimumSize(new Dimension(100, 15));
            panel_text_background.setPreferredSize(new Dimension(250, 30));
            label_text.setMinimumSize(new Dimension(100, 15));
            label_text.setPreferredSize(new Dimension(200 + 50 * 1, 30));
            panel_icon_background.setMinimumSize(new Dimension(100, 50));
            panel_icon_background.setPreferredSize(new Dimension(100, 100));
            label_icon.setMinimumSize(new Dimension(100, 50));
            label_icon.setPreferredSize(new Dimension(100, 100));

            if (PERIODS > 12) {
                panel_south.setMinimumSize(new Dimension(300, 400));
                panel_south.setPreferredSize(new Dimension(300 + 100 * 1, 350));
            }
            if (PERIODS > 6 && PERIODS <= 12) {
                panel_south.setMinimumSize(new Dimension(250, 400));
                panel_south.setPreferredSize(new Dimension(250 + 50 * 1, 350));
            }

        } else {
            panel_center.setMinimumSize(new Dimension(200, 15));
            panel_center.setPreferredSize(new Dimension(250 + 70 * seller.VOLUME, 30));
            panel_north.setMinimumSize(new Dimension(200, 50));
            panel_north.setPreferredSize(new Dimension(200 + 70 * seller.VOLUME, 100));
//        panel_north1.setMinimumSize(new Dimension(75, 100));
//        panel_north1.setPreferredSize(new Dimension(75+35*buyer.VOLUME, 100));
//        panel_north2.setMinimumSize(new Dimension(150, 100));
//        panel_north2.setPreferredSize(new Dimension(150+35*buyer.VOLUME, 100));
            panel_south.setMinimumSize(new Dimension(300, 200));
            panel_south.setPreferredSize(new Dimension(350 + 0 * seller.VOLUME, 200));
            panel_text_background.setMinimumSize(new Dimension(200, 15));
            panel_text_background.setPreferredSize(new Dimension(230 + 70 * seller.VOLUME, 30));
            label_text.setMinimumSize(new Dimension(200, 15));
            label_text.setPreferredSize(new Dimension(250 + 70 * seller.VOLUME, 30));
            panel_icon_background.setMinimumSize(new Dimension(100, 50));
            panel_icon_background.setPreferredSize(new Dimension(100, 100));
            label_icon.setMinimumSize(new Dimension(100, 50));
            label_icon.setPreferredSize(new Dimension(100, 100));

            if (PERIODS > 12) {
                panel_south.setMinimumSize(new Dimension(400, 350));
                panel_south.setPreferredSize(new Dimension(400 + 140 * seller.VOLUME, 350));
            }
            if (PERIODS > 6 && PERIODS <= 12) {
                panel_south.setMinimumSize(new Dimension(300, 400));
                panel_south.setPreferredSize(new Dimension(350 + 0 * seller.VOLUME, 350));
            }
        }

//        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));
        label_icon.setHorizontalAlignment(SwingConstants.CENTER);
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
//        label_text.setText("<html>Please enter the seller's information</html>");
//        label_icon.setText(s);
        label_icon.setIcon(icon);
//        label_icon.setFont(font_1);
        panel_icon_background.add(label_icon);
//        panel_center.add(panel_icon_background, BorderLayout.NORTH);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panel_north1.add(panel_icon_background, gridBagConstraints);

        JLabel l = new JLabel("Tactic:");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        if (seller.ES == 1) {
            panel_north2.add(l, gridBagConstraints);
        }

//        panel_center.setBackground(Color.WHITE);
//         JPanel panel_text_background = new JPanel();
//        JLabel label_text = new JLabel();
//        panel_text_background.setMinimumSize(new Dimension(300, 60));
//        panel_text_background.setPreferredSize(new Dimension(300, 60));
//        label_text.setMinimumSize(new Dimension(200, 15));
//        label_text.setPreferredSize(new Dimension(400, 60));
        JMenu[] menus = new JMenu[5];

        //    menus[0] = menu1;
        menus[0] = concession;
        menus[1] = problem;
        menus[2] = matching;
        menus[3] = contending;
        menus[4] = risks;
//    menus[2] = demand;
//    menus[3]= Demand;

//    menus[0].add(menus[1]);
//    menus[0].add(menus[2]);
//    menus[0].add(menus[3]);
        menus[0].add(making);
        menus[0].add(E_R);
        menus[0].add(conceder);
        menus[0].add(menus[2]);
        menus[3].add(boulware);
        menus[2].add(tit);
        menus[2].add(inversetit);
        menus[2].add(randTit);
        menus[1].add(low);
        menus[1].add(intrasigent);
        menus[4].add(riskstrat);

        JMenu[] menuES = new JMenu[2];

        //    menus[0] = menu1;
        menuES[0] = behaviour;
        menuES[1] = goal;

//    menus[2] = demand;
//    menus[3]= Demand;
//    menus[0].add(menus[1]);
//    menus[0].add(menus[2]);
//    menus[0].add(menus[3]);
        menuES[0].add(collaboration);
        menuES[0].add(accomodation);
        menuES[0].add(competition);
        menuES[1].add(mScore);
        menuES[1].add(mSdeal);
        menuES[1].add(deal);
        menuES[1].add(ESdeadline);

        Parent = parent;
        final JMenu menES = ComboMenuBar.createMenu(tactic);

        menES.add(menuES[0]);
        menES.add(menuES[1]);
        menES.add(userDefined);
        menES.add(sStrategy);
        menES.setFont(font_2);

////    menu.addSeparator();
//    menu.add(menus[2]);
//    menu.add(menus[3]);
        ComboMenuBar comboMenu = new ComboMenuBar(menES);

        comboMenu.setMinimumSize(new Dimension(180, 25));
        comboMenu.setPreferredSize(new Dimension(180, 25));
        comboMenu.setFont(font_2);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(10, 5, 5, 0);
        if (seller.ES == 1) {
            panel_north2.add(comboMenu, gridBagConstraints);
        }

        l = new JLabel("Strategy:");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = seller.ES;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, -5, 5, 10);

        panel_north2.add(l, gridBagConstraints);

        JMenu menu = ComboMenuBar.createMenu(seller.negotiation_strategy);

        menu.add(menus[0]);
        menu.add(menus[1]);
        menu.add(menus[3]);
        menu.add(menus[4]);
        menu.setFont(font_2);
////    menu.addSeparator();
//    menu.add(menus[2]);
//    menu.add(menus[3]);

//        JMenu menu2 = ComboMenuBar.createMenu(buyer.negotiation_strategy);
        ComboMenuBar comboMenu2 = new ComboMenuBar(menu);

        comboMenu2.setMinimumSize(new Dimension(180, 25));
        comboMenu2.setPreferredSize(new Dimension(180, 25));
        comboMenu2.setFont(font_2);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = seller.ES;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(10, 5, 5, 0);

        panel_north2.add(comboMenu2, gridBagConstraints);

        panel_north.add(panel_north1, BorderLayout.WEST);
        panel_north.add(panel_north2, BorderLayout.CENTER);

        //        panel_center.add(panel_icon_background, BorderLayout.NORTH);
//        panel_center.setBackground(Color.WHITE);
//         JPanel panel_text_background = new JPanel();
//        JLabel label_text = new JLabel();
//        panel_text_background.setMinimumSize(new Dimension(300, 60));
//        panel_text_background.setPreferredSize(new Dimension(300, 60));
//        label_text.setMinimumSize(new Dimension(200, 60));
//        label_text.setPreferredSize(new Dimension(365, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
        label_text.setFont(font_2);
        label_text.setText("Send First Proposal?");
//        int propaux=0;
//        if (num==0){
//            propaux=1;
//        }
        if (box != 0 && prop == 0) {
            label_text.setText("Received " + seller.getOpponent().getLocalName() + " Proposal " + num);
//            label_text.setText("Send Counter-Proposal "+(num-propaux));
        } else if (box != 0 && prop != 0) {
            panel_center.setMinimumSize(new Dimension(200, 30));
            panel_center.setPreferredSize(new Dimension(400, 60));
            panel_text_background.setMinimumSize(new Dimension(200, 30));
            panel_text_background.setPreferredSize(new Dimension(400, 60));
            label_text.setMinimumSize(new Dimension(200, 30));
            label_text.setPreferredSize(new Dimension(400, 60));
            label_text.setText("<html>You have Received the " + seller.getOpponent().getLocalName() + "<br>Proposal " + num + ", Better than the calculated one.<br>Acceptance is recomended.</html>");
        }

        label_text.setFont(font_2);
        panel_text_background.add(label_text);
        panel_center.add(panel_text_background, BorderLayout.SOUTH);

        panel_south.setLayout(new GridBagLayout());

        //Panel south
//        JTextField[] prices = new JTextField[cmp.length];
//        JLabel l = new JLabel("Send First Proposal?");
//        if (box!=0){
//        l = new JLabel("Received "+buyer.getOpponent().getLocalName()+" Proposal "+num);
//        }
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
        if (box != 0) {
            l = new JLabel("Received:");
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(5, 13, 5, 5);/*top,left,buttom,right*/

            panel_south.add(l, gridBagConstraints);

            l = new JLabel(threedecimal.format(Brcv / 1000).replace(",", "."));
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
            gridBagConstraints.insets = new java.awt.Insets(5, 13, 5, 5);/*top,left,buttom,right*/

            panel_south.add(l, gridBagConstraints);
        }

        l = new JLabel("Send:");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1 + box;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("Benefit (k€):");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("€/MWh");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1 + box;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

//         l = new JLabel("kWh");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 5+box;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
        if (seller.VOLUME == 1) {
            l = new JLabel("Energy (kWh)");
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 2 + box;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

            panel_south.add(l, gridBagConstraints);
        }

        if (PERIODS > 12) {
            if (box != 0) {
                l = new JLabel("Received:");
                l.setFont(font_2);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 4 + seller.VOLUME;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(5, 13, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);

            }
            if (seller.VOLUME == 1) {
                l = new JLabel("Energy(kWh)");
                l.setFont(font_2);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 5 + 2 * box;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

                panel_south.add(l, gridBagConstraints);
            }
            l = new JLabel("Send:");
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3 + seller.VOLUME + 2 * box;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
            gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);

            l = new JLabel("€/MWh");
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3 + seller.VOLUME + 2 * box;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

            panel_south.add(l, gridBagConstraints);
        }

        for (int i = 0; i < PERIODS; i++) {
            auxCmp[i] = cmp[i + PERIODS];
            list3[i] = String.valueOf(cmp[i]);

            l = new JLabel("Period " + (i + 1) + ": ");
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 3;
            if (i >= 12) {
                gridBagConstraints.gridx = 2 + seller.VOLUME + box;
                gridBagConstraints.gridy = i + 3 - 12;
            }
            gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 3);/*top,left,buttom,right*/

            panel_south.add(l, gridBagConstraints);

//        l = new JLabel("€/MWh");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 2+box;
//        gridBagConstraints.gridy = i+2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
//        
//         l = new JLabel("kWh");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 5+box;
//        gridBagConstraints.gridy = i+2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
            tf_volume_send[i] = new JTextField(64);
            tf_volume_send[i].setText(twodecimal.format(cmp[i + PERIODS]).replace(",", "."));
            tf_volume_send[i].setFont(font_2);
            tf_volume_send[i].setMinimumSize(new Dimension(40, 20));
            tf_volume_send[i].setPreferredSize(new Dimension(40, 20));
            tf_volume_send[i].setEditable(false);
//        tf_volume_send[i].getDocument().addDocumentListener(new DocumentListener() {
//
//                @Override
//                public void insertUpdate(DocumentEvent e) {
//                    checkChanges2();
////                   
//                }
//
//                @Override
//                public void removeUpdate(DocumentEvent e) {
//                    checkChanges2();
////                   
//                }
//
//                @Override
//                public void changedUpdate(DocumentEvent e) {
//                    checkChanges2();
////                   
//                }
//
//                private void checkChanges2() {
//                    if (!volume_listener_occupied) {
//                        volume_listener_occupied = true;
//                          auxCcmp=0;                      
////                       
//                            for (int j = 0; j < 6; j++) {
//                            list3[j+6] = tf_volume_send[j].getText();
//                            if (!list3[j+6].equals("")){
//                            auxCcmp=auxCcmp+Double.valueOf(list3[j])*Double.valueOf(list3[j+6])*Double.valueOf(tf_volume_profile[6].getText())/(1000000);
//                            }
//                        }
//                            price_listener_occupied = true;
//                            tf_price_send[6].setText(threedecimal.format(auxCcmp).replace(",", "."));
//                            price_listener_occupied= false;
//                            volume_listener_occupied = false;
//                            
//                            
//                    }
//                }
//            });

            gridBagConstraints = new GridBagConstraints();

            gridBagConstraints.gridx = 2 + box;
            gridBagConstraints.gridy = i + 3;
            if (i >= 12) {
                gridBagConstraints.gridx = 5 + 2 * box;
                gridBagConstraints.gridy = i + 3 - 12;
            }
            gridBagConstraints.anchor = GridBagConstraints.CENTER;
            gridBagConstraints.insets = new Insets(0, 5, 0, 3);
            if (seller.VOLUME == 1) {
                panel_south.add(tf_volume_send[i], gridBagConstraints);
            }

            if (box != 0) {
                l = new JLabel(twodecimal.format(rcv[i]).replace(",", "."));
                l.setFont(font_2);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = i + 3;
                if (i >= 12) {
                    gridBagConstraints.gridx = 4 + seller.VOLUME;
                    gridBagConstraints.gridy = i + 3 - 12;
                }
                gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
                gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 3);/*top,left,buttom,right*/

                panel_south.add(l, gridBagConstraints);
            }
        }
//          l = new JLabel("k€");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 2+box;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);

        for (int i = 0; i < PERIODS + 1; i++) {
            tf_price_send[i] = new JTextField(64);
            if (i == PERIODS) {
                tf_price_send[i].setText(threedecimal.format(auxBcmp / 1000).replace(",", "."));
                tf_price_send[i].setEditable(false);
            } else {
                tf_price_send[i].setText(twodecimal.format(cmp[i]).replace(",", "."));
            }
            tf_price_send[i].setFont(font_2);
            tf_price_send[i].setMinimumSize(new Dimension(40, 20));
            tf_price_send[i].setPreferredSize(new Dimension(40, 20));
            tf_price_send[i].getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkChanges2();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkChanges2();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkChanges2();
                }

                private void checkChanges2() {
                    if (!price_listener_occupied) {
                        price_listener_occupied = true;
                        auxBcmp = 0;
                        for (int j = 0; j < PERIODS; j++) {
                            list3[j] = tf_price_send[j].getText();
                            if (!list3[j].equals("")) {
                                auxBcmp = auxBcmp + (Double.valueOf(list3[j]) - Double.valueOf(tf_price_limit[j].getText())) * auxCmp[j] * contract / (1000000);
                            }
                        }
                        tf_price_send[PERIODS].setText(threedecimal.format(auxBcmp).replace(",", "."));
                        price_listener_occupied = false;
//                         

                    }
                }
            });
//            if (auxCcmp==0 && Ccmp!=0){
//           for (i = 0; i < 6; i++) {
////                            list3[i] = tf_price_send[i].getText();     
//           auxCcmp=auxCcmp+Double.valueOf(list3[i])*auxCmp[i];      
//                        }
//            }

            gridBagConstraints = new GridBagConstraints();
            if (i == PERIODS) {
                gridBagConstraints.gridx = 1 + box;
                gridBagConstraints.gridy = 1;
            } else {
                gridBagConstraints.gridx = 1 + box;
                gridBagConstraints.gridy = i + 3;
                if (i >= 12) {
                    gridBagConstraints.gridx = 3 + seller.VOLUME + 2 * box;
                    gridBagConstraints.gridy = i + 3 - 12;
                }
            }
            gridBagConstraints.anchor = GridBagConstraints.CENTER;
            gridBagConstraints.insets = new Insets(0, 5, 0, 3);
            panel_south.add(tf_price_send[i], gridBagConstraints);

        }

//        cost_changed[0] = new JTextField(64);
//        cost_changed[0].setText(String.valueOf(auxCcmp));
//        cost_changed[0].setMinimumSize(new Dimension(70, 25));
//        cost_changed[0].setPreferredSize(new Dimension(70, 25));
//        cost_changed[0].addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                if (!profile_listener_occupied) {
//                    profile_listener_occupied = true;
////                    String[] volumes = new String[buyer.PERIODS];
////                 
////                    if (cb_predefined_volume_values.getSelectedIndex() != predefined_values_titles.length - 1) {
////                        for (int i = 0; i < buyer.PERIODS; i++) {
////                            tf_volume_profile[i].setText(volumes[i].toString());
////                        }
//////                        tf_volume_profile[6].setText(my_contract_array[0].toString());
////                    }
//                    auxCcmp=10;
//                    profile_listener_occupied = false;
//                }
//            }
//        });
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 4;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(cost_changed[0], gridBagConstraints);
        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
        panel.add(panel_south, BorderLayout.SOUTH);

        int a = 2;
        while (a == 2 || a == 3) {
            a = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName()/*+" Negotiation"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
            if (a == 3) {
                border = new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                um.put("OptionPane.border", border);
                int aux = JOptionPane.showOptionDialog(parent, "Do you really want to finish the negotation?", " WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices2, null);
                border = new BorderUIResource(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                um.put("OptionPane.border", border);
                if (aux == 0) {

                    return 2;
                }
            } else if (a == 2) {
                border = new BorderUIResource(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                um.put("OptionPane.border", border);
                int aux2 = inter3(auxBcmp, Brcv, parent, current);
                if (aux2 == 0) {
                    return 1;
                }
            }
            String aux2 = (String) comboMenu2.getSelectedItem();
            if (aux2 != "Vectorial Distance" && aux2 != "Maximum Benefit") {
                seller.setNegotiationStrategy((String) comboMenu2.getSelectedItem());
            } else {
                seller.setNegotiationStrategyAlgorithm((String) comboMenu2.getSelectedItem());
                seller.setNegotiationStrategy("Price Management");
            }
//       int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
            if (seller.ES == 1) {
                if (!tactic.equals((String) comboMenu.getSelectedItem())) {
                    tactic = (String) comboMenu.getSelectedItem();
                    if (!tactic.equals(sStrategy.getText())) {
                        askUserES(parent);
                    }
                }
            }

            int j = 0;
            while (j >= 0) {
                j = 0;
                auxBcmp = 0;
                for (int i = 0; i < PERIODS; i++) {
                    if ((Double.valueOf(list3[i]) < Double.valueOf(tf_price_limit[i].getText()))/*||(Double.valueOf(list3[i])> Double.valueOf(tf_price_target[i].getText()))*/) {
                        j++;
                        if ((Double.valueOf(list3[i]) < Double.valueOf(tf_price_limit[i].getText()))) {
                            price_listener_occupied = true;
                            list3[i] = tf_price_limit[i].getText();
                            tf_price_send[i].setText(list3[i]);
                            price_listener_occupied = false;
//               } if((Double.valueOf(list3[i])> Double.valueOf(tf_price_target[i].getText()))){
//                   price_listener_occupied = true;
//                   list3[i]=tf_price_target[i].getText();
//                   tf_price_send[i].setText(list3[i]);
//                   price_listener_occupied = false;
                        }
                        auxBcmp = auxBcmp + (Double.valueOf(list3[j]) - Double.valueOf(tf_price_limit[j].getText())) * auxCmp[j] * contract / (1000000);
                    } else {
                        auxBcmp = auxBcmp + (Double.valueOf(list3[j]) - Double.valueOf(tf_price_limit[j].getText())) * auxCmp[j] * contract / (1000000);
                    }
                }
                if (j > 0) {
                    price_listener_occupied = true;
                    tf_price_send[PERIODS].setText(threedecimal.format(auxBcmp).replace(",", "."));
                    price_listener_occupied = false;
                    border = new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    um.put("OptionPane.border", border);
                    JOptionPane.showOptionDialog(parent, "Some value(s) exceeded the pre-negotiation Limits", " WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices3, null);
                    border = new BorderUIResource(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    um.put("OptionPane.border", border);
                    a = JOptionPane.showOptionDialog(parent, panel, "Seller " + seller.getLocalName() + " Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
                } else {
                    j = -1;
                }
            }
        }
        return a;

    }

    public int inter3(double Bcmp, double Brcv, SellerGui parent, final double[] current) {
//        int a=JOptionPane.showConfirmDialog(null, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

        //Panel north
        list4[0] = threedecimal.format(Bcmp).replace(",", ".");;
        auxBcmp = Bcmp;
        String[] choices2 = {"Send", "Back"};
        String[] choices3 = {"OK"};
//            double auxcmp=0;
        UIManager um = new UIManager();
//          um.put("OptionPane.messageForeground", Color.black);
//          um.put("Panel.background", Color.CYAN);
//                if(buyer.ES==1){
//                    ES(parent);
//                }

        Border border = new BorderUIResource(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        um.put("OptionPane.background", Color.BLUE.darker().darker());
        ImageIcon icon = new ImageIcon("images\\Energy_Buying_Group2.jpg");
        if (seller.getLocalName().equals(seller_list[1])) {
            um.put("OptionPane.background", Color.ORANGE.darker().darker().darker().darker());
            icon = new ImageIcon("images\\david_aggregation2.jpg");
        }
        um.put("OptionPane.border", border);
//         um.put("OptionPane.messageForeground", Color.DARK_GRAY);
//          um.put("OptionPane.title", Color.CYAN);
//          ImageIcon icon = new ImageIcon("C:\\Documents and Settings\\mit\\My Documents\\My Pictures\\img.gif");

//       int a = JOptionPane.showOptionDialog(parent, s, buyer.getLocalName()+" Starting Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
////        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
//        return a;
        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//                  JPanel panel_north = new JPanel(new GridLayout(1, 2));
        JPanel panel_north = new JPanel(new BorderLayout());
        JPanel panel_north1 = new JPanel();
        JPanel panel_north2 = new JPanel();
//                  panel_north1.setLayout(new GridBagLayout());
        panel_north2.setLayout(new GridBagLayout());

        JPanel panel_south = new JPanel();
//        panel_north.setLayout(new BorderLayout());
        panel_center.setLayout(new FlowLayout());
        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        JPanel panel_icon_background = new JPanel();
        JLabel label_icon = new JLabel();

        panel_center.setMinimumSize(new Dimension(200, 15));
        panel_center.setPreferredSize(new Dimension(320, 30));
        panel_north.setMinimumSize(new Dimension(200, 50));
        panel_north.setPreferredSize(new Dimension(270, 100));
//        panel_north1.setMinimumSize(new Dimension(75, 100));
//        panel_north1.setPreferredSize(new Dimension(75+35*buyer.VOLUME, 100));
//        panel_north2.setMinimumSize(new Dimension(150, 100));
//        panel_north2.setPreferredSize(new Dimension(150+35*buyer.VOLUME, 100));
        panel_south.setMinimumSize(new Dimension(300, 200));
        panel_south.setPreferredSize(new Dimension(350, 200));
        panel_text_background.setMinimumSize(new Dimension(200, 15));
        panel_text_background.setPreferredSize(new Dimension(300, 30));
        label_text.setMinimumSize(new Dimension(200, 15));
        label_text.setPreferredSize(new Dimension(320, 30));
        panel_icon_background.setMinimumSize(new Dimension(100, 50));
        panel_icon_background.setPreferredSize(new Dimension(100, 100));
        label_icon.setMinimumSize(new Dimension(100, 50));
        label_icon.setPreferredSize(new Dimension(100, 100));

        if (PERIODS > 12) {
            panel_south.setMinimumSize(new Dimension(400, 350));
            panel_south.setPreferredSize(new Dimension(540, 350));
        }
        if (PERIODS > 6 && PERIODS <= 12) {
            panel_south.setMinimumSize(new Dimension(300, 400));
            panel_south.setPreferredSize(new Dimension(350, 350));
        }

//        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));
        label_icon.setHorizontalAlignment(SwingConstants.CENTER);
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
//        label_text.setText("<html>Please enter the seller's information</html>");
//        label_icon.setText(s);
        label_icon.setIcon(icon);
//        label_icon.setFont(font_1);
        panel_icon_background.add(label_icon);
//        panel_center.add(panel_icon_background, BorderLayout.NORTH);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panel_north1.add(panel_icon_background, gridBagConstraints);

        Parent = parent;

        JLabel l = new JLabel("<html> <br><br> Prepare counter-offer by indicating Concession Rates<br><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Received Benefit: "
                + threedecimal.format(Brcv / 1000).replace(",", ".") + " k€</html>");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = seller.ES;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(10, 5, 5, 0);
        panel_north2.add(l, gridBagConstraints);

//        panel_north2.add(comboMenu2, gridBagConstraints);
        panel_north.add(panel_north1, BorderLayout.WEST);
        panel_north.add(panel_north2, BorderLayout.CENTER);

        label_text.setHorizontalAlignment(SwingConstants.CENTER);

        label_text.setText("Send First Proposal?");
        label_text.setFont(font_2);
//        int propaux=0;
//        if (num==0){
//            propaux=1;
//        }
//        if (box!=0 && prop==0){
//        label_text.setText("Received "+buyer.getOpponent().getLocalName()+" Proposal "+num);
//        label_text.setFont(font_2);
////        label_text.setText("Send Counter-Proposal "+(num-propaux));
//        } else if(box!=0 && prop!=0){
//        panel_center.setMinimumSize(new Dimension(200, 30));
//        panel_center.setPreferredSize(new Dimension(400, 60));
//        panel_text_background.setMinimumSize(new Dimension(200, 30));
//        panel_text_background.setPreferredSize(new Dimension(400, 60));
//        label_text.setMinimumSize(new Dimension(200, 30));
//        label_text.setPreferredSize(new Dimension(400, 60));
//           label_text.setText("<html>You have Received the "+buyer.getOpponent().getLocalName()+"<br>Proposal "+num+", Better than the calculated one.<br>Acceptance is recomended.</html>"); 
//        }

        label_text.setFont(font_2);
        panel_text_background.add(label_text);
//        panel_center.add(panel_text_background, BorderLayout.SOUTH);

        panel_south.setLayout(new GridBagLayout());

        //Panel south
//        JTextField[] prices = new JTextField[cmp.length];
//        JLabel l = new JLabel("Send First Proposal?");
//        if (box!=0){
//        l = new JLabel("Received "+buyer.getOpponent().getLocalName()+" Proposal "+num);
//        }
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
        l = new JLabel("<html>Current<br>Price:</html>");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 13, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

//                l= new JLabel("<html>Concession<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;%</html>");
        l = new JLabel("<html>Concession</html>");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("<html>New<br>Price</html>");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

//              l= new JLabel("<html>Received<br>Cost</html>");
//        l.setFont(font_2);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 4;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
//        
//        l = new JLabel(threedecimal.format(Brcv/1000).replace(",", "."));
//        l.setFont(font_2);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 4;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 13, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
        l = new JLabel("Cost (k€):");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("€/MWh");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("%");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("€/MWh");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("€/MWh");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("%");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        l = new JLabel("€/MWh");
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);

        if (PERIODS > 12) {

//        l= new JLabel("Received:");
//        l.setFont(font_2);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 4+buyer.VOLUME;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.insets = new java.awt.Insets(5, 13, 5, 5);/*top,left,buttom,right*/
////        panel_south.add(l, gridBagConstraints);
//        
//                l= new JLabel("To Send:");
//        l.setFont(font_2);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 3+buyer.VOLUME+2*box;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);/*top,left,buttom,right*/
////        panel_south.add(l, gridBagConstraints);
//        l = new JLabel("€/MWh");
//        l.setFont(font_2);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 3+buyer.VOLUME+2*box;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);
        }

        double Bcurrent = 0;
        for (int i = 0; i < PERIODS; i++) {
            Bcurrent = Bcurrent + current[i] * (Double.valueOf(list3[i]) - Double.valueOf(tf_price_limit[i].getText())) * contract / (1000000);
//            auxBcmp=auxBcmp+(Double.valueOf(list3[j])-Double.valueOf(tf_price_limit[j].getText()))*auxCmp[j]*contract/(1000000);
//            auxCmp[i]=Double.valueOf(list3[i+PERIODS]);

            l = new JLabel("Period " + (i + 1) + ": ");
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i + 3;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 3);
            if (i >= 12) {
                gridBagConstraints.gridx = 4;
                gridBagConstraints.gridy = i + 3 - 12;
                gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 9);
            }
            gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
            /*top,left,buttom,right*/
            panel_south.add(l, gridBagConstraints);

//        tf_volume_send[i] = new JTextField(64);
//        tf_volume_send[i].setText(twodecimal.format(cmp[i+PERIODS]).replace(",", "."));
//        tf_volume_send[i].setFont(font_2);
//        tf_volume_send[i].setMinimumSize(new Dimension(40, 20));
//        tf_volume_send[i].setPreferredSize(new Dimension(40, 20));
//        tf_volume_send[i].getDocument().addDocumentListener(new DocumentListener() {
//
//                @Override
//                public void insertUpdate(DocumentEvent e) {
//                    checkChanges2();
////                   
//                }
//
//                @Override
//                public void removeUpdate(DocumentEvent e) {
//                    checkChanges2();
////                   
//                }
//
//                @Override
//                public void changedUpdate(DocumentEvent e) {
//                    checkChanges2();
////                   
//                }
//
//                private void checkChanges2() {
//                    if (!volume_listener_occupied) {
//                        volume_listener_occupied = true;
//                          auxCcmp=0;                      
////                       
//                            for (int j = 0; j < PERIODS; j++) {
//                            list3[j+PERIODS] = tf_volume_send[j].getText();
//                            if (!list3[j+6].equals("")){
//                            auxCcmp=auxCcmp+Double.valueOf(list3[j])*Double.valueOf(list3[j+PERIODS])*Double.valueOf(tf_volume_profile[PERIODS].getText())/(1000000);
//                            }
//                        }
//                            price_listener_occupied = true;
//                            tf_price_send[PERIODS].setText(threedecimal.format(auxCcmp).replace(",", "."));
//                            price_listener_occupied= false;
//                            volume_listener_occupied = false;
//                            
//                            
//                    }
//                }
//            });
//         gridBagConstraints = new GridBagConstraints();
//
//        gridBagConstraints.gridx = 2+box;
//        gridBagConstraints.gridy = i + 3;
//        if(i>=12){
//        gridBagConstraints.gridx = 5+2*box;
//        gridBagConstraints.gridy = i + 3-12;
//        }
//        gridBagConstraints.anchor = GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new Insets(0, 5, 0, 3);
//        
////        panel_south.add(tf_volume_send[i], gridBagConstraints);  
            l = new JLabel(twodecimal.format(current[i]).replace(",", "."));
            l.setFont(font_2);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i + 3;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 3);/*top,left,buttom,right*/

            if (i >= 12) {
                gridBagConstraints.gridx = 5;
                gridBagConstraints.gridy = i + 3 - 12;
                gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 9);/*top,left,buttom,right*/

            }
            gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;

            panel_south.add(l, gridBagConstraints);

        }
        l = new JLabel(threedecimal.format(Bcurrent).replace(",", "."));
        l.setFont(font_2);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 13, 5, 5);/*top,left,buttom,right*/

        panel_south.add(l, gridBagConstraints);
//          l = new JLabel("k€");
//        l.setFont(font_1);
//        l.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 2+box;
//        gridBagConstraints.gridy = 1;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 5);/*top,left,buttom,right*/
//        panel_south.add(l, gridBagConstraints);

        for (int i = 0; i < PERIODS; i++) {
            Concession[i] = new JTextField(64);

            Concession[i].setText(twodecimal.format(100 * (current[i] - Double.valueOf(list3[i])) / current[i]).replace(",", "."));

            Concession[i].setFont(font_2);
            Concession[i].setMinimumSize(new Dimension(40, 20));
            Concession[i].setPreferredSize(new Dimension(40, 20));
            Concession[i].getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkChanges2();
//                   
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkChanges2();
//                   
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkChanges2();
//                   
                }

                private void checkChanges2() {
                    if (!concession_listener_occupied) {
                        concession_listener_occupied = true;

//                       
                        for (int j = 0; j < PERIODS; j++) {
                            listConcession[j] = Concession[j].getText();
                            if (!listConcession[j].equals("") && price_listener_occupied == false && !listConcession[j].equals(twodecimal.format(100 * (current[j] - Double.valueOf(list3[j])) / current[j]).replace(",", "."))) {
                                tf_price_send[j].setText(twodecimal.format(current[j] - Double.valueOf(listConcession[j]) * current[j] / 100).replace(",", "."));
                            }
                        }

                        concession_listener_occupied = false;

                    }
                }

            });

            gridBagConstraints = new GridBagConstraints();

            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = i + 3;
            gridBagConstraints.insets = new Insets(0, 5, 0, 3);
            if (i >= 12) {
                gridBagConstraints.gridx = 6;
                gridBagConstraints.gridy = i + 3 - 12;
                gridBagConstraints.insets = new Insets(0, 15, 0, 9);
            }

            gridBagConstraints.anchor = GridBagConstraints.CENTER;

            panel_south.add(Concession[i], gridBagConstraints);

        }

        for (int i = 0; i < PERIODS + 1; i++) {
            tf_price_send[i] = new JTextField(64);
            if (i == PERIODS) {
                tf_price_send[i].setText(threedecimal.format(auxBcmp / 1000).replace(",", "."));
                tf_price_send[i].setEditable(false);
            } else {
                tf_price_send[i].setText(twodecimal.format(Double.valueOf(list3[i])).replace(",", "."));
            }
            tf_price_send[i].setFont(font_2);
            tf_price_send[i].setMinimumSize(new Dimension(40, 20));
            tf_price_send[i].setPreferredSize(new Dimension(40, 20));
            tf_price_send[i].getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkChanges2();
//                   
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkChanges2();
//                   
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkChanges2();
//                   
                }

                private void checkChanges2() {
                    if (!price_listener_occupied) {
                        price_listener_occupied = true;
                        auxBcmp = 0;
//                       
                        for (int j = 0; j < PERIODS; j++) {
                            list3[j] = tf_price_send[j].getText();
                            if (!list3[j].equals("")) {
                                auxBcmp = auxBcmp + (Double.valueOf(list3[j]) - Double.valueOf(tf_price_limit[j].getText())) * auxCmp[j] * contract / (1000000);
                            }
                            if (!list3[j].equals("")) {
                                if (concession_listener_occupied == false && !listConcession[j].equals(twodecimal.format(100 * (current[j] - Double.valueOf(list3[j])) / current[j]).replace(",", "."))) {
                                    Concession[j].setText(twodecimal.format(100 * (current[j] - Double.valueOf(list3[j])) / current[j]).replace(",", "."));
                                }
                            }
                        }
                        tf_price_send[PERIODS].setText(threedecimal.format(auxBcmp).replace(",", "."));

                        price_listener_occupied = false;

                    }
                }

            });

            gridBagConstraints = new GridBagConstraints();
            if (i == PERIODS) {
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 1;
            } else {
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = i + 3;
                gridBagConstraints.insets = new Insets(0, 5, 0, 3);
                if (i >= 12) {
                    gridBagConstraints.gridx = 7;
                    gridBagConstraints.gridy = i + 3 - 12;
                    gridBagConstraints.insets = new Insets(0, 15, 0, 3);
                }
            }
            gridBagConstraints.anchor = GridBagConstraints.CENTER;

            panel_south.add(tf_price_send[i], gridBagConstraints);

        }

        panel.add(panel_north, BorderLayout.NORTH);
//        panel.add(panel_center, BorderLayout.CENTER);
        panel.add(panel_south, BorderLayout.SOUTH);

        int a = 2;
        while (a == 2) {

            a = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName()/*+" Negotiation"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices2, null);

            int j = 0;
            while (j >= 0) {
                j = 0;
                auxBcmp = 0;
                for (int i = 0; i < PERIODS; i++) {
                    if ((Double.valueOf(list3[i]) < Double.valueOf(tf_price_limit[i].getText()))/*||(Double.valueOf(list3[i])> Double.valueOf(tf_price_target[i].getText()))*/) {
                        j++;
                        if ((Double.valueOf(list3[i]) < Double.valueOf(tf_price_limit[i].getText()))) {
                            price_listener_occupied = true;
                            list3[i] = tf_price_limit[i].getText();
                            tf_price_send[i].setText(list3[i]);
                            price_listener_occupied = false;
//               } if((Double.valueOf(list3[i])> Double.valueOf(tf_price_target[i].getText()))){
//                   price_listener_occupied = true;
//                   list3[i]=tf_price_target[i].getText();
//                   tf_price_send[i].setText(list3[i]);
//                   price_listener_occupied = false;
                        }
                        auxBcmp = auxBcmp + (Double.valueOf(list3[j]) - Double.valueOf(tf_price_limit[j].getText())) * auxCmp[j] * contract / (1000000);
                    } else {
                        auxBcmp = auxBcmp + (Double.valueOf(list3[j]) - Double.valueOf(tf_price_limit[j].getText())) * auxCmp[j] * contract / (1000000);
                    }
                }
                if (j > 0) {
                    price_listener_occupied = true;
                    tf_price_send[PERIODS].setText(threedecimal.format(auxBcmp).replace(",", "."));
                    price_listener_occupied = false;
                    border = new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    um.put("OptionPane.border", border);
                    JOptionPane.showOptionDialog(parent, "Some value(s) exceeded the pre-negotiation Limits", " WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, choices3, null);
                    border = new BorderUIResource(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    um.put("OptionPane.border", border);
                    a = JOptionPane.showOptionDialog(parent, panel, "Seller " + seller.getLocalName() + " Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices2, null);
                } else {
                    j = -1;
                }
            }
        }
//        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        return a;

    }

    public int offer(SellerGui parent, int aux) {
//        
//          ImageIcon icon = new ImageIcon("images\\retail_energy_globe2.jpg");
        ImageIcon icon = new ImageIcon("images\\icon1.png");

        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//          getContentPane().add(panel_left, BorderLayout.WEST);
        UIManager um = new UIManager();
//          um.put("OptionPane.messageForeground", Color.black);
//          um.put("Panel.background", Color.CYAN);

//        JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
////        split_pane_log_image.setOneTouchExpandable(true);
//        split_pane_log_image.setDividerLocation(0);
//        split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));
        Border border = new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//          um.put("OptionPane.background", Color.BLUE.darker().darker().darker());
        um.put("OptionPane.border", border);

        panel_center.setMinimumSize(new Dimension(100, 50));
        panel_center.setPreferredSize(new Dimension(200, 100));
//        JOptionPane.getRootFrame().setSize(300, 400);

        JPanel panel_icon_background = new JPanel();
        JLabel label_icon = new JLabel();
//        panel_icon_background.setMinimumSize(new Dimension(350, 50));
//        panel_icon_background.setPreferredSize(new Dimension(350, 50));
//        label_icon.setMinimumSize(new Dimension(350, 50));
//        label_icon.setPreferredSize(new Dimension(350, 50));
        label_icon.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
//        label_text.setText("<html>Please enter the seller's information</html>");
//        label_icon.setText(s);
        label_icon.setIcon(icon);
//        label_icon.setFont(font_1);
        panel_icon_background.add(label_icon);
//        panel_center.add(panel_icon_background, BorderLayout.NORTH);

        panel_center.add(panel_icon_background, BorderLayout.NORTH);
//        panel_center.setBackground(Color.WHITE);

        panel_center.setLayout(new GridBagLayout());

        //Panel center
        JLabel l = new JLabel("Received Offer");
        if (aux == 1) {
            l = new JLabel("Received Counter-Offer");
//         l = new JLabel("Agenda Accepted"); 
        }
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_center.add(l, gridBagConstraints);

//        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
//        panel.add(panel_left, BorderLayout.WEST);
        String[] choices4 = {"OK"};
        ImageIcon icon1 = new javax.swing.ImageIcon("images\\icon1.png");

        int a = JOptionPane.showOptionDialog(parent, panel, "Producer: " + seller.getLocalName()/*+" Terminating Negotiation"*/, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices4, null);
//        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        return a;

    }

    public int congrat(SellerGui parent) {
//        
//          ImageIcon icon = new ImageIcon("images\\retail_energy_globe2.jpg");
        ImageIcon icon = new ImageIcon("images\\icon1.png");

        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//          getContentPane().add(panel_left, BorderLayout.WEST);
        UIManager um = new UIManager();
//          um.put("OptionPane.messageForeground", Color.black);
//          um.put("Panel.background", Color.CYAN);

//        JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
////        split_pane_log_image.setOneTouchExpandable(true);
//        split_pane_log_image.setDividerLocation(0);
//        split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));
        Border border = new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//          um.put("OptionPane.background", Color.BLUE.darker().darker().darker());
        um.put("OptionPane.border", border);

        panel_center.setMinimumSize(new Dimension(100, 50));
        panel_center.setPreferredSize(new Dimension(200, 150));
//        JOptionPane.getRootFrame().setSize(300, 400);

        JPanel panel_icon_background = new JPanel();
        JLabel label_icon = new JLabel();
//        panel_icon_background.setMinimumSize(new Dimension(350, 50));
//        panel_icon_background.setPreferredSize(new Dimension(350, 50));
//        label_icon.setMinimumSize(new Dimension(350, 50));
//        label_icon.setPreferredSize(new Dimension(350, 50));
        label_icon.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
//        label_text.setText("<html>Please enter the seller's information</html>");
//        label_icon.setText(s);
        label_icon.setIcon(icon);
//        label_icon.setFont(font_1);
        panel_icon_background.add(label_icon);
//        panel_center.add(panel_icon_background, BorderLayout.NORTH);

        panel_center.add(panel_icon_background, BorderLayout.NORTH);
//        panel_center.setBackground(Color.WHITE);

        panel_center.setLayout(new GridBagLayout());

        //Panel center
        JLabel l = new JLabel("Congratulations!");
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_center.add(l, gridBagConstraints);

        JLabel l1 = new JLabel("You have reached a Mutually Beneficial");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_center.add(l1, gridBagConstraints);

        JLabel l2 = new JLabel("Agreement with " + seller.getOpponent().getLocalName());
        l2.setFont(font_1);
        l2.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_center.add(l2, gridBagConstraints);
//        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
//        panel.add(panel_left, BorderLayout.WEST);
        String[] choices4 = {"OK"};
        ImageIcon icon1 = new javax.swing.ImageIcon("images\\icon1.png");

        int a = JOptionPane.showOptionDialog(parent, panel, "Seller " + seller.getLocalName() + " Terminating Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices4, null);
//        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        return a;

    }

    public int decline(SellerGui parent) {
//        
//          ImageIcon icon = new ImageIcon("images\\retail_energy_globe2.jpg");
        ImageIcon icon = new ImageIcon("images\\icon1.png");

        JPanel panel = new JPanel(new BorderLayout());
        //Panel center - icon
        JPanel panel_center = new JPanel();
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);

//          getContentPane().add(panel_left, BorderLayout.WEST);
        UIManager um = new UIManager();
//          um.put("OptionPane.messageForeground", Color.black);
//          um.put("Panel.background", Color.CYAN);

//        JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
////        split_pane_log_image.setOneTouchExpandable(true);
//        split_pane_log_image.setDividerLocation(0);
//        split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));
        Border border = new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//          um.put("OptionPane.background", Color.BLUE.darker().darker().darker());
        um.put("OptionPane.border", border);

        panel_center.setMinimumSize(new Dimension(100, 50));
        panel_center.setPreferredSize(new Dimension(200, 150));
//        JOptionPane.getRootFrame().setSize(300, 400);

        JPanel panel_icon_background = new JPanel();
        JLabel label_icon = new JLabel();
//        panel_icon_background.setMinimumSize(new Dimension(350, 50));
//        panel_icon_background.setPreferredSize(new Dimension(350, 50));
//        label_icon.setMinimumSize(new Dimension(350, 50));
//        label_icon.setPreferredSize(new Dimension(350, 50));
        label_icon.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
//        label_text.setText("<html>Please enter the seller's information</html>");
//        label_icon.setText(s);
        label_icon.setIcon(icon);
//        label_icon.setFont(font_1);
        panel_icon_background.add(label_icon);
//        panel_center.add(panel_icon_background, BorderLayout.NORTH);

        panel_center.add(panel_icon_background, BorderLayout.NORTH);
//        panel_center.setBackground(Color.WHITE);

        panel_center.setLayout(new GridBagLayout());

        //Panel center
        JLabel l = new JLabel("You deal have been declined!");
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_center.add(l, gridBagConstraints);

        JLabel l1 = new JLabel("Sorry, your deal with " + seller.getOpponent().getLocalName() + " have");
        l1.setFont(font_1);
        l1.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_center.add(l1, gridBagConstraints);

        JLabel l2 = new JLabel("been rejected by the Market Operator");
        l2.setFont(font_1);
        l2.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/

        panel_center.add(l2, gridBagConstraints);
//        panel.add(panel_north, BorderLayout.NORTH);
        panel.add(panel_center, BorderLayout.CENTER);
//        panel.add(panel_left, BorderLayout.WEST);
        String[] choices4 = {"OK"};
        ImageIcon icon1 = new javax.swing.ImageIcon("images\\icon1.png");

        int a = JOptionPane.showOptionDialog(parent, panel, "Seller " + seller.getLocalName() + " Terminating Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices4, null);
//        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        return a;

    }

    public void finish(SellerGui parent, String msg) {
//        
//          ImageIcon icon = new ImageIcon("images\\retail_energy_globe2.jpg");
//          ImageIcon icon = new ImageIcon("images\\icon1.png");

//          JPanel panel = new JPanel(new BorderLayout());
//          //Panel center - icon
//          JPanel panel_center = new JPanel();
//                          panel_center.add(split_pane_log_image, BorderLayout.NORTH);
//          getContentPane().add(panel_left, BorderLayout.WEST);
        UIManager um = new UIManager();
//          um.put("OptionPane.messageForeground", Color.black);
//          um.put("Panel.background", Color.CYAN);

//        JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
////        split_pane_log_image.setOneTouchExpandable(true);
//        split_pane_log_image.setDividerLocation(0);
//        split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));
        Border border = new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//          um.put("OptionPane.background", Color.BLUE.darker().darker().darker());
        um.put("OptionPane.border", border);

//        panel_center.setMinimumSize(new Dimension(100, 50));
//        panel_center.setPreferredSize(new Dimension(200, 150));
//        JOptionPane.getRootFrame().setSize(300, 400);
//        JPanel panel_icon_background = new JPanel();
//        JLabel label_icon = new JLabel();
////        panel_icon_background.setMinimumSize(new Dimension(350, 50));
////        panel_icon_background.setPreferredSize(new Dimension(350, 50));
////        label_icon.setMinimumSize(new Dimension(350, 50));
////        label_icon.setPreferredSize(new Dimension(350, 50));
//        label_icon.setHorizontalAlignment(SwingConstants.LEFT);
//        GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
////        label_text.setText("<html>Please enter the seller's information</html>");
////        label_icon.setText(s);
//        label_icon.setIcon(icon);
////        label_icon.setFont(font_1);
//        panel_icon_background.add(label_icon);
////        panel_center.add(panel_icon_background, BorderLayout.NORTH);
//        
//        panel_center.add(panel_icon_background, BorderLayout.NORTH);
////        panel_center.setBackground(Color.WHITE);
//
//        panel_center.setLayout(new GridBagLayout());
        //Panel center
        JLabel l = new JLabel(seller.getOpponent().getLocalName() + " Terminated the Negotiation");
        if (!msg.equals("")) {
            l = new JLabel(msg);
        }
        l.setFont(font_1);
        l.setHorizontalAlignment(SwingConstants.CENTER);
//         GridBagConstraints gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_center.add(l, gridBagConstraints);

//        JLabel l1 = new JLabel("You have reached a Mutually Beneficial");
//        l1.setFont(font_1);
//        l1.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 2;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_center.add(l1, gridBagConstraints);
//        
//         JLabel l2 = new JLabel("Agreement with "+seller.getOpponent().getLocalName());
//        l2.setFont(font_1);
//        l2.setHorizontalAlignment(SwingConstants.CENTER);
//        gridBagConstraints = new GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 3;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
//        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);/*top,left,buttom,right*/
//        panel_center.add(l2, gridBagConstraints);
////        panel.add(panel_north, BorderLayout.NORTH);
//          panel.add(panel_center, BorderLayout.CENTER);
////        panel.add(panel_left, BorderLayout.WEST);
        String[] choices4 = {"OK"};
//       ImageIcon icon1 = new javax.swing.ImageIcon("images\\icon1.png");

        int a = JOptionPane.showOptionDialog(parent, l, "Seller " + seller.getLocalName() + " Terminating Negotiation", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices4, null);
//        int a=JOptionPane.showConfirmDialog(parent,panel, s, buyer.getLocalName()+" Starting Negotiation",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

    }

    public void redefine() {

        PERIODS = seller.PERIODS;
        tf_price = new JTextField[PERIODS];
        tf_volume = new JTextField[PERIODS];
        tf_price_target = new JTextField[PERIODS];
        tf_volume_target = new JTextField[PERIODS];
        tf_price_limit = new JTextField[PERIODS];
        Concession = new JTextField[PERIODS];
        listConcession = new String[PERIODS];
        tf_volume_limit = new JTextField[PERIODS];
        tf_personal_info = new JTextField[4];
        tf_price_send = new JTextField[PERIODS + 1];
        tf_volume_send = new JTextField[PERIODS];
        list3 = new String[PERIODS];
        auxCmp = new double[PERIODS];
        listsort = new int[PERIODS];
        auxlist = new int[PERIODS];
        sort_alg = new JTextField[PERIODS];
        deviation = new double[2 * PERIODS];
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

        selling.ArrowIcon iconRenderer;

        public ComboMenu(String label) {
            super(label);
            iconRenderer = new selling.ArrowIcon(SwingConstants.SOUTH, true);
            setBorder(new EtchedBorder());
            setIcon(new selling.BlankIcon(null, 11));
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
        return new selling.ComboMenuBar.ComboMenu(label);
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
