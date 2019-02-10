package producing;

import selling.*;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;

public class ProducerGui extends JFrame {

    private Producer seller;
    private Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    private JTextArea text_log = new JTextArea("");
    private JScrollPane scroll_log = new JScrollPane(text_log);
    private JPanel panel_center = new JPanel();
    private JMenuItem menu_file_terminate = new JMenuItem("Terminate Agent");
    private JMenuItem menu_file_save_belief = new JMenuItem("Save Beliefs File");
    private JMenuItem menu_actions_publicise_prices = new JMenuItem("Publicise");
    private JMenuItem menu_actions_contract_type = new JMenuItem("Contract Type");
    private JMenuItem menu_negotiation_negotiate = new JMenuItem("Negotiate");
    private JMenuItem menu_view_utility = new JMenuItem("Utility History");
    private JMenuItem menu_proposal_time = new JMenuItem("Utility Time Proposal");
    private JLabel label_image1 = new JLabel(new ImageIcon("images\\seller_image_2.png"));
    private JLabel label_image2 = new JLabel(new ImageIcon("images\\powerplant.png"));
    private JLabel label_image = new JLabel();
    private JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT, label_image, scroll_log);
    protected JMenu menu_negotiation_pre_negotiate = new JMenu("Pre-negotiation");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_targets = new JMenuItem("Define Targets");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_limits = new JMenuItem("Define Limits");
    protected JMenuItem menu_negotiation_pre_negotiate_define_limits = new JMenuItem("Price Limits");
    protected JMenuItem menu_negotiation_pre_negotiate_define_preferences = new JMenuItem("Bargaining Preferences");
    protected JMenuItem menu_negotiation_pre_negotiate_define_preferences_strategies = new JMenuItem("Initial Strategy");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_dealine = new JMenuItem("Dealine");
    protected JMenuItem menu_negotiation_pre_negotiate_define_priorities = new JMenuItem("Issue Priorities");
    protected JMenuItem menu_negotiation_pre_negotiate_define_marketlimits = new JMenuItem("Market Price Range");
    protected JMenu menu_negotiation_newstrategy = new JMenu("New Strategy");
    protected JMenuItem user_choice = new JMenuItem("User Choice");
    protected JMenu newstrategy_dynamic = new JMenu("System Recommendation Choice");
    private javax.swing.JCheckBoxMenuItem dynamic_yes = new javax.swing.JCheckBoxMenuItem("Yes");
    private javax.swing.JCheckBoxMenuItem dynamic_no = new javax.swing.JCheckBoxMenuItem("No");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_targets = new JMenuItem("Price Limits");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_limits = new JMenuItem("Volume Limits");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_preferences_strategies = new JMenuItem("Preferences and Strategies");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_dealine = new JMenuItem("Deadline");
//    protected JMenu menu_negotiation_negotiation = new JMenu("Negotiation");
    protected JMenuItem menu_negotiation_initialoffer = new JMenuItem("Initial Offer");
    protected JMenuItem menu_negotiation_counteroffer = new JMenuItem("Counter-Offer");

    public ProducerGui(Producer seller) {
        Listener listener = new Listener();
        this.seller = seller;
        this.setTitle("Seller: " + seller.getLocalName());
        this.setSize(400, 600);
        this.setLocation((int) ((screen_size.getWidth() / 5) - (this.getSize().getWidth() / 2)), (int) ((screen_size.getHeight() / 2) - (this.getSize().getHeight() / 2)));

        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        text_log.setEditable(false);
        text_log.setMinimumSize(new Dimension(200, 200));
        text_log.setEditable(false);
        if (this.seller.getLocalName().equals("RES")) {
            label_image = label_image2;
        } else {
            label_image = label_image1;
        }
        split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT, label_image, scroll_log);
        label_image.setMinimumSize(new Dimension(0, 0));

        JMenuBar menu_bar = new JMenuBar();
        JMenu menu_file = new JMenu("File");
        menu_bar.add(menu_file);
        JMenu menu_view = new JMenu("View");
        menu_view.setMinimumSize(new Dimension(35, 10));

        JMenu menu_actions = new JMenu("Action");
//        menu_bar.add(menu_actions);
        JMenu pre_negotiation = new JMenu("Pre-negotiation");
        pre_negotiation.setMinimumSize(new Dimension(100, 10));
        menu_bar.add(pre_negotiation);
        JMenu menu_negotiation = new JMenu("Negotiation");
        menu_bar.add(menu_negotiation);
        menu_bar.add(menu_view);

        menu_file.addSeparator();

        menu_file.add(menu_file_save_belief);
        menu_file.add(menu_file_terminate);
        menu_view.add(menu_view_utility);
        menu_view.add(menu_proposal_time);
        menu_actions.add(menu_actions_publicise_prices);
        menu_actions.add(menu_actions_contract_type);

        split_pane_log_image.setOneTouchExpandable(true);
        split_pane_log_image.setDividerLocation(0.5);
        split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));

        panel_center.setPreferredSize(new Dimension(400, 400));
        panel_center.setBackground(Color.WHITE);
        container.add(panel_center, BorderLayout.CENTER);
        panel_center.setLayout(new FlowLayout());
        scroll_log.setPreferredSize(new Dimension(panel_center.getPreferredSize().width, panel_center.getPreferredSize().height));
        panel_center.add(split_pane_log_image, BorderLayout.NORTH);

        updateLog1("Myagent:\t" + seller.getName());
        updateLog1("Opponent:\t" + seller.getOpponent().getName());
        this.setJMenuBar(menu_bar);
        menu_actions_publicise_prices.addActionListener(listener);
        menu_actions_contract_type.addActionListener(listener);

        pre_negotiation.add(menu_negotiation_pre_negotiate_define_priorities);
//        pre_negotiation.add(menu_negotiation_pre_negotiate_define_targets);
        pre_negotiation.add(menu_negotiation_pre_negotiate_define_marketlimits);
        pre_negotiation.add(menu_negotiation_pre_negotiate_define_limits);
        pre_negotiation.add(menu_negotiation_pre_negotiate_define_preferences);
        pre_negotiation.add(menu_negotiation_pre_negotiate_define_preferences_strategies);
//        menu_negotiation_pre_negotiate.add(menu_negotiation_pre_negotiate_define_dealine);
        menu_negotiation.add(menu_negotiation_initialoffer);
        menu_negotiation.add(menu_negotiation_counteroffer);
        newstrategy_dynamic.add(dynamic_yes);
        newstrategy_dynamic.add(dynamic_no);
        dynamic_yes.setSelected(false);
        dynamic_no.setSelected(true);
//        newstrategy_dynamic.setS
        menu_negotiation_newstrategy.add(user_choice);
        menu_negotiation_newstrategy.add(newstrategy_dynamic);
        menu_negotiation.add(menu_negotiation_newstrategy);

//                menu_negotiation_pre_negotiate_define_targets.addActionListener(listener);
        menu_file_terminate.addActionListener(listener);
        menu_view_utility.addActionListener(listener);
        menu_proposal_time.addActionListener(listener);
        user_choice.addActionListener(listener);
//        menu_negotiation_pre_negotiate_define_dealine.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_marketlimits.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_limits.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_preferences.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_preferences_strategies.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_priorities.addActionListener(listener);
        menu_negotiation_initialoffer.addActionListener(listener);
        menu_negotiation_counteroffer.addActionListener(listener);
        dynamic_yes.addActionListener(listener);
        dynamic_no.addActionListener(listener);

        menu_file_save_belief.addActionListener(listener);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {

            @Override
            public void ancestorMoved(HierarchyEvent e) {
                //do nothing
            }

            @Override
            public void ancestorResized(HierarchyEvent e) {
                split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));
                split_pane_log_image.setDividerLocation(0.5);
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        split_pane_log_image.repaint();
    }

    void guiEnableButtons(int phase) {

        switch (phase) {

            case 1:

                menu_negotiation_pre_negotiate.setEnabled(true);
                menu_actions_contract_type.setEnabled(false);
                menu_actions_publicise_prices.setEnabled(false);
                menu_view_utility.setEnabled(false);
                menu_proposal_time.setEnabled(false);
                if (seller.risk == 1) {
                    menu_negotiation_pre_negotiate_define_marketlimits.setEnabled(true);
                } else {
                    menu_negotiation_pre_negotiate_define_marketlimits.setEnabled(false);
                }
                menu_negotiation_pre_negotiate_define_limits.setEnabled(true);
//                menu_negotiation_pre_negotiate_define_targets.setEnabled(true);
                menu_negotiation_pre_negotiate_define_preferences_strategies.setEnabled(true);
                menu_negotiation_pre_negotiate_define_preferences.setEnabled(true);
//                menu_negotiation_pre_negotiate_define_dealine.setEnabled(true);
                menu_negotiation_initialoffer.setEnabled(false);
                menu_negotiation_counteroffer.setEnabled(false);
                menu_negotiation_newstrategy.setEnabled(true);
                menu_negotiation_pre_negotiate_define_priorities.setEnabled(true);
                break;

            case 2:

                menu_negotiation_pre_negotiate_define_limits.setEnabled(false);
                menu_negotiation_pre_negotiate_define_marketlimits.setEnabled(false);
                menu_negotiation_pre_negotiate_define_priorities.setEnabled(false);

                menu_negotiation_pre_negotiate_define_preferences_strategies.setEnabled(false);
                menu_negotiation_pre_negotiate_define_preferences.setEnabled(false);
//                menu_negotiation_pre_negotiate_define_dealine.setEnabled(false);
                menu_negotiation_initialoffer.setEnabled(true);
//                menu_negotiation_counteroffer.setEnabled(false);
                menu_negotiation_newstrategy.setEnabled(true);
                menu_view_utility.setEnabled(true);
                menu_proposal_time.setEnabled(true);

                break;

        }
    }

    public void askUserAgenda(String[] content_items_array) {

        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> volumes = new ArrayList<>();
        ArrayList<String> extras = new ArrayList<>();

        for (int i = 0; i < content_items_array.length; i++) {
            if (content_items_array[i].contains("price") && !content_items_array[i].contains("extra")) {
                prices.add(content_items_array[i]);
            } else if (content_items_array[i].contains("volume")) {
                volumes.add(content_items_array[i]);
            } else if (content_items_array[i].contains("extra")) {
                extras.add(content_items_array[i]);
            }
        }
        JCheckBox prices_cb = new JCheckBox("Prices");
        JCheckBox volumes_cb = new JCheckBox("Volumes");
        JCheckBox extras_cb = new JCheckBox("Extras");

        Container container = new Container();
        container.setLayout(new BorderLayout());

        JLabel label_text = new JLabel();

        label_text.setText("<html>Your opponent has proposed the following agenda items<br><br>Please select which ones you agree with:<br></html>");

        label_text.setPreferredSize(new Dimension(100, 120));
        JPanel myPanel = new JPanel();

        myPanel.setLayout(new FlowLayout());
        myPanel.add(prices_cb);
        myPanel.add(volumes_cb);
        if (!extras.isEmpty()) {
            myPanel.add(extras_cb);
        }
        myPanel.add(Box.createVerticalStrut(5));

        container.add(myPanel, BorderLayout.SOUTH);
        container.add(label_text, BorderLayout.NORTH);

        int result = JOptionPane.showConfirmDialog(this, container, seller.getLocalName(), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {

            while (!prices_cb.isSelected() && !volumes_cb.isSelected() && !extras_cb.isSelected()) {
                label_text.setText("<html>No item selected\n Please select at least one item<br></html>");
                result = JOptionPane.showConfirmDialog(this, container, "No item selected<br>Please select at least one item", JOptionPane.OK_CANCEL_OPTION);
            }

            if (result == JOptionPane.OK_OPTION) {
                ArrayList agenda_counter_proposal = new ArrayList();

                if (extras_cb.isSelected()) {
                    agenda_counter_proposal.addAll(extras);
                }
                if (prices_cb.isSelected()) {
                    agenda_counter_proposal.addAll(prices);
                }
                if (volumes_cb.isSelected()) {
                    agenda_counter_proposal.addAll(volumes);
                }
                seller.setAgendaItems(agenda_counter_proposal);
            }
        }
    }

    public void askUserPrices(boolean forced) {

        String[] price_volumes_to_publicise = seller.getInputGui().askPublicise(this, forced);
//       System.out.println(price_volumes_to_publicise[0]);
//                    System.out.println(price_volumes_to_publicise[1]);
        if (price_volumes_to_publicise != null) {
            seller.addBelif("myagent", seller.getLocalName() + ";" + "prices;" + price_volumes_to_publicise[0]);
            seller.addBelif("myagent", seller.getLocalName() + ";" + "volumes;" + price_volumes_to_publicise[1]);
        }

    }

    public void updateLog2(String s, Color c) {
        text_log.setForeground(c);
        this.text_log.append("\n\n " + s);
        text_log.setForeground(Color.BLACK);
    }

    public void updateLog2(String s) {
        this.text_log.append("\n\n " + s);
    }

    public void updateLog1(String s, Color c) {
        text_log.setForeground(c);
        this.text_log.append("\n " + s);
        text_log.setForeground(Color.BLACK);
    }

    public void updateLog1(String s) {
        this.text_log.append("\n " + s);
    }

    private class Listener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(menu_actions_publicise_prices)) {
                seller.executePhase(2);

//            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_targets)) {
//                seller.getInputGui().askTargets(SellerGui.this);
            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_priorities)) {
                seller.getInputGui().askPriorities(ProducerGui.this);
            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_marketlimits)) {
                seller.getInputGui().askPriceLimits(ProducerGui.this);
            } else if (e.getSource().equals(user_choice)) {
                seller.getInputGui().ChangeStrategy(ProducerGui.this);
            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_limits)) {
                seller.getInputGui().askLimits(ProducerGui.this);
            } else if (e.getSource().equals(dynamic_yes)) {
                dynamic_no.setSelected(false);
                seller.ES = 1;
                seller.getInputGui().askUserES(ProducerGui.this);
            } else if (e.getSource().equals(dynamic_yes)) {
                dynamic_yes.setSelected(false);
                seller.ES = 0;
            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_preferences_strategies)) {
                seller.getInputGui().askProtocolAndStrategy(ProducerGui.this);
            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_preferences)) {
                seller.getInputGui().askPreference(ProducerGui.this);
//            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_dealine)) {
//                seller.defineDeadline();
            } else if (e.getSource().equals(menu_negotiation_initialoffer)) {
                menu_negotiation_counteroffer.setEnabled(true);
                menu_negotiation_initialoffer.setEnabled(false);
                seller.gui.askUserPrices(false);
                seller.sendPricesAndVolumes();
                seller.negotiation();
            } else if (e.getSource().equals(menu_negotiation_counteroffer)) {
                seller.counteroffer = 1;
            } else if (e.getSource().equals(menu_file_save_belief)) {
                seller.updateBelifsFile();
            } else if (e.getSource().equals(menu_file_terminate)) {
                seller.terminateAgent();
            } else if (e.getSource().equals(menu_view_utility)) {
                seller.utility(ProducerGui.this, 0, "Utility History");
            } else if (e.getSource().equals(menu_proposal_time)) {
                seller.utility(ProducerGui.this, 1, "Negotiation Dance");

            }
        }
    }
}
