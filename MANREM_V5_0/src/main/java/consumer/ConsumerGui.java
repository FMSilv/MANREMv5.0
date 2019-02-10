package consumer;

import jade.core.AID;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

public class ConsumerGui extends JFrame {
    
    private Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    //private Buyer buyer;
    private Consumer consumer;
    public int counteroffer=0, counter=0;
    private JTextArea text_log = new JTextArea("");
    private JScrollPane scroll_log = new JScrollPane(text_log);
    private JPanel panel_center = new JPanel();
    private JMenuItem menu_file_save_belief = new JMenuItem("Save Beliefs File");
    private JMenuItem menu_file_terminate = new JMenuItem("Terminate Agent");
    private JMenuItem menu_view_utility = new JMenuItem("Utility History");
    private JMenuItem menu_proposal_time = new JMenuItem("Utility Time Proposal");
    private JMenuItem menu_view_volume = new JMenuItem("Volume Variation");
    protected JMenuItem menu_action_profile = new JMenuItem("Send Profile");
    private JMenuItem menu_actions_contract_type = new JMenuItem("Contract Type");
//    protected JMenu menu_negotiation_pre_negotiate = new JMenu("Pre-negotiation");
    protected JMenuItem menu_negotiation_pre_negotiate_define_priorities = new JMenuItem("Issue Priorities");
    protected JMenuItem menu_negotiation_pre_negotiate_define_targets = new JMenuItem("Price Limits");
    protected JMenuItem menu_negotiation_pre_negotiate_define_marketlimits = new JMenuItem("Market Price Range");
    protected JMenuItem menu_negotiation_pre_negotiate_define_limits = new JMenuItem("Volume Limits");
    protected JMenuItem menu_negotiation_pre_negotiate_define_preferences = new JMenuItem("Bargaining Preferences");
    protected JMenuItem menu_negotiation_pre_negotiate_define_preferences_strategies = new JMenuItem("Initial Strategy");
//    protected JMenuItem menu_negotiation_pre_negotiate_define_dealine = new JMenuItem("Deadline");
//    protected JMenu menu_negotiation_negotiation = new JMenu("Negotiation");
    protected JMenuItem menu_negotiation_initialoffer = new JMenuItem("Initial Offer");
    protected JMenuItem menu_negotiation_counteroffer = new JMenuItem("Couter Offer");
    protected JMenuItem menu_negotiation_offerhistory = new JMenuItem("Offer History");
    protected JMenu menu_negotiation_newstrategy = new JMenu("New Strategy");
    protected JMenuItem user_choice = new JMenuItem("User Choice");
    protected JMenu newstrategy_dynamic = new JMenu("System Recommendation Choice");
    private javax.swing.JCheckBoxMenuItem dynamic_yes=new javax.swing.JCheckBoxMenuItem("Yes");
    private javax.swing.JCheckBoxMenuItem dynamic_no=new javax.swing.JCheckBoxMenuItem("No");
    private JLabel label_image1 = new JLabel(new ImageIcon("images\\buyer_image_2.png"));
    private JLabel label_image2 = new JLabel(new ImageIcon("images\\lisbon_night.png"));
    private JLabel label_image = new JLabel();
    
    private JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT, label_image1, scroll_log);
            
    protected Component menu_contextual_separator = Box.createHorizontalStrut((int)screen_size.getWidth());
    protected JMenuBar menu_bar = new JMenuBar();
   
    public ConsumerGui(Consumer consumer) {
        //this.buyer = buyer;
        this.consumer   =   consumer;

        Listener listener = new Listener();
        this.setTitle("Buyer: "+consumer.getLocalName());
        this.setSize(new Dimension(400, 600));
        this.setLocation((int) ((screen_size.getWidth() / 5) * 4 - (this.getSize().getWidth() / 2)), (int) ((screen_size.getHeight() / 2) - (this.getSize().getHeight() / 2)));
        
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        
        text_log.setMinimumSize(new Dimension(200, 200));
        text_log.setEditable(false);
        if (this.consumer.getLocalName().equals("David Owen")){
            label_image=label_image2;
                } else {
        label_image=label_image1;
                }
        split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT, label_image, scroll_log);
        label_image.setMinimumSize(new Dimension(0, 0));
        
        JMenu menu_file = new JMenu("File");
        menu_file.setMinimumSize(new Dimension(30, 10));
        menu_bar.add(menu_file);
        JMenu menu_view = new JMenu("View");
        menu_view.setMinimumSize(new Dimension(35, 10));
        
        JMenu menu_action = new JMenu("Action");
        menu_action.setMinimumSize(new Dimension(50, 10));
//        menu_bar.add(menu_action);
         JMenu pre_negotiation = new JMenu("Pre-negotiation");
        pre_negotiation.setMinimumSize(new Dimension(100, 10));
        menu_bar.add(pre_negotiation);
        JMenu menu_negotiation = new JMenu("Negotiation");
        menu_negotiation.setMinimumSize(new Dimension(77, 10));
        menu_bar.add(menu_negotiation);
        menu_bar.add(menu_view);
        
        menu_bar.add(menu_contextual_separator);
        
       menu_contextual_separator.setMinimumSize(new Dimension(0,0));
       
        menu_action.add(menu_action_profile);
        menu_action.add(menu_actions_contract_type);
        menu_file.add(menu_file_save_belief);
        menu_file.add(menu_file_terminate);
//        menu_negotiation.add(menu_negotiation_pre_negotiate);
        menu_view.add(menu_view_utility);
        menu_view.add(menu_proposal_time);
        menu_view.add(menu_view_volume);
        pre_negotiation.add(menu_negotiation_pre_negotiate_define_priorities);
        pre_negotiation.add(menu_negotiation_pre_negotiate_define_marketlimits);
        pre_negotiation.add(menu_negotiation_pre_negotiate_define_targets);
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
        
//        menu_negotiation.setEnabled(false);
//        pre_negotiation.setEnabled(true);
        menu_view_utility.setEnabled(false);
        menu_view_volume.setEnabled(false);
        menu_proposal_time.setEnabled(false);
        menu_negotiation_pre_negotiate_define_priorities.setEnabled(false);
        menu_negotiation_pre_negotiate_define_targets.setEnabled(false);
        menu_negotiation_pre_negotiate_define_preferences.setEnabled(false);
        menu_negotiation_pre_negotiate_define_preferences_strategies.setEnabled(false);
//        menu_negotiation_pre_negotiate_define_dealine.setEnabled(false);
        menu_negotiation_pre_negotiate_define_marketlimits.setEnabled(false);
        menu_negotiation_pre_negotiate_define_limits.setEnabled(false);
        menu_negotiation_initialoffer.setEnabled(false);
        menu_negotiation_counteroffer.setEnabled(false);
        menu_negotiation_newstrategy.setEnabled(true);
        
        split_pane_log_image.setOneTouchExpandable(true);
        split_pane_log_image.setDividerLocation(0.5);
        split_pane_log_image.setPreferredSize(new Dimension(getWidth() - 30, getHeight() - 70));
        
        
        panel_center.setPreferredSize(new Dimension(400, 400));
        panel_center.setBackground(Color.WHITE);
        container.add(panel_center, BorderLayout.CENTER);
        panel_center.setLayout(new FlowLayout());
        scroll_log.setPreferredSize(new Dimension(panel_center.getPreferredSize().width, panel_center.getPreferredSize().height));
        panel_center.add(split_pane_log_image, BorderLayout.NORTH);

        this.setJMenuBar(menu_bar);
        
        menu_negotiation_pre_negotiate_define_priorities.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_targets.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_preferences.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_preferences_strategies.addActionListener(listener);
//        menu_negotiation_pre_negotiate_define_dealine.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_limits.addActionListener(listener);
        menu_negotiation_pre_negotiate_define_marketlimits.addActionListener(listener);
        menu_negotiation_initialoffer.addActionListener(listener);
        menu_negotiation_newstrategy.addActionListener(listener);
        menu_negotiation_counteroffer.addActionListener(listener);
        menu_view_utility.addActionListener(listener);
        menu_proposal_time.addActionListener(listener);
        user_choice.addActionListener(listener);
        menu_view_volume.addActionListener(listener);
        dynamic_yes.addActionListener(listener);
        dynamic_no.addActionListener(listener);
        
        menu_file_save_belief.addActionListener(listener);
        menu_file_terminate.addActionListener(listener);
        
        menu_action_profile.addActionListener(listener);
        
        
        updateLog1("Myagent:\t" + consumer.getName());
        updateLog1("Opponent:\t" + consumer.getOpponent().getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

               
        this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {
            
            @Override
            public void ancestorMoved(HierarchyEvent e) {
                // do nothing
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
    
    
    void guiEnableButtons(int phase) {
        
        switch (phase) {
            
            case 1:
                consumer.askUserProfile();
                consumer.sendProfile();
                 menu_negotiation_pre_negotiate_define_targets.setEnabled(true);
                menu_actions_contract_type.setEnabled(false);
                menu_action_profile.setEnabled(false);
                menu_negotiation_pre_negotiate_define_priorities.setEnabled(true);
                if (consumer.VOLUME==1){
                menu_negotiation_pre_negotiate_define_limits.setEnabled(true);
                }else{
                    menu_negotiation_pre_negotiate_define_limits.setEnabled(false);
                }
                if(consumer.risk==1){
                menu_negotiation_pre_negotiate_define_marketlimits.setEnabled(true);
                }
                menu_negotiation_pre_negotiate_define_targets.setEnabled(true);
                menu_negotiation_pre_negotiate_define_preferences.setEnabled(true);
                menu_negotiation_pre_negotiate_define_preferences_strategies.setEnabled(true);
//                menu_negotiation_pre_negotiate_define_dealine.setEnabled(true);
                menu_negotiation_initialoffer.setEnabled(false);
                menu_negotiation_newstrategy.setEnabled(true);
                break;
            
            case 2:
                
//                menu_negotiation_pre_negotiate.setEnabled(false);
                 
                menu_negotiation_pre_negotiate_define_limits.setEnabled(false);
                menu_negotiation_pre_negotiate_define_marketlimits.setEnabled(false);
                menu_negotiation_pre_negotiate_define_priorities.setEnabled(false);
                menu_negotiation_pre_negotiate_define_targets.setEnabled(false);
                menu_negotiation_pre_negotiate_define_preferences.setEnabled(false);
                menu_negotiation_pre_negotiate_define_preferences_strategies.setEnabled(false);
//                menu_negotiation_pre_negotiate_define_dealine.setEnabled(false);
                menu_negotiation_initialoffer.setEnabled(true);
//                menu_negotiation_counteroffer.setEnabled(false);
                menu_negotiation_newstrategy.setEnabled(true);
                menu_view_utility.setEnabled(true);
                menu_proposal_time.setEnabled(true);
                if(consumer.VOLUME==1){
                menu_view_volume.setEnabled(true);
                }
                
                break;
            
        }
    }
    
    public void stop(){
        if(counter==0){
        Listener listener = new Listener();
        counter=1;
        }
        while(counteroffer==0){
            int a=0;
        }
        counter=0;
    }
    
    private class Listener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(menu_negotiation_pre_negotiate_define_targets)) {
                consumer.getInputGui().askTargets(ConsumerGui.this);
                } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_priorities)) {
                consumer.getInputGui().askPriorities(ConsumerGui.this);
            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_limits)) {
                consumer.getInputGui().askLimits(ConsumerGui.this);
                } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_marketlimits)) {
                consumer.getInputGui().askPriceLimits(ConsumerGui.this);
            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_preferences_strategies)) {
                consumer.getInputGui().askProtocolAndStrategy(ConsumerGui.this);
                } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_preferences)) {
                consumer.getInputGui().askPreference(ConsumerGui.this);
//            } else if (e.getSource().equals(menu_negotiation_pre_negotiate_define_dealine)) {
//                consumer.defineDeadline();
            } else if (e.getSource().equals(menu_negotiation_initialoffer)) {
                menu_negotiation_counteroffer.setEnabled(true);
                menu_negotiation_initialoffer.setEnabled(false);
                consumer.executePhase(4);
            } else if (e.getSource().equals(menu_negotiation_counteroffer)) {
                System.out.println("Counter-offer:");
                counteroffer=1;
//                consumer.checkCO();
                } else if (e.getSource().equals(user_choice)) {
                consumer.getInputGui().ChangeStrategy(ConsumerGui.this);
            } else if (e.getSource().equals(menu_action_profile)) {
                consumer.askUserProfile();
                consumer.sendProfile();
                } else if (e.getSource().equals(dynamic_yes)) {
                dynamic_no.setSelected(false);
                consumer.ES=1;
                consumer.getInputGui().askUserES(ConsumerGui.this);
                } else if (e.getSource().equals(dynamic_no)) {
                    dynamic_yes.setSelected(false);
                consumer.ES=0;
            } else if (e.getSource().equals(menu_file_save_belief)) {
               consumer.updateBelifsFile();
            }  else if (e.getSource().equals(menu_file_terminate)) {
                   consumer.terminateAgent();
                 }  else if (e.getSource().equals(menu_view_utility)) {
                   consumer.utility(ConsumerGui.this,0,"Utility History");
                   }  else if (e.getSource().equals(menu_proposal_time)) {
                   consumer.utility(ConsumerGui.this,1,"Negotiation Dance");     
                   }  else if (e.getSource().equals(menu_view_volume)) {
                   consumer.volume(ConsumerGui.this,"Volume Variation");  
                   
                   
            }
        }
    }
}
