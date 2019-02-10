package selling;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JOptionPane;
import personalassistant.PersonalAssistant;
import wholesalemarket_LMP.Wholesale_InputData;

public class RiskAttitudeForm extends javax.swing.JFrame {

    private Wholesale_InputData market;
    private PersonalAssistant data;
    
    private String agentTotalNames;
    private String[] agentName;
    private boolean isSeller;
    
    private boolean isBuyer;
    private boolean isLConsumer;
    private boolean isConsumer;
    
    public RiskAttitudeForm(Wholesale_InputData _market, int agentType) {
        market = _market;
        isSeller = true;
        isBuyer = true;
        isLConsumer = true;
        isConsumer = true;
        String title;
        initComponents();
        if(agentType == 0) {
            title = "Seller: Generator";
            isBuyer = false;
            isLConsumer = false;
            isConsumer = false;
        } else if(agentType == 1) {
            title = "Seller: Retailer";
            isBuyer = false;
            isLConsumer = false;
            isConsumer = false;
        } else if(agentType == 2) {
            title = "Buyer: Retailer";
            isSeller = false;
            isLConsumer = false;
            isConsumer = false;
        } else if(agentType == 3) {
            title = "Buyer: Coalition";
            isSeller = false;
            isBuyer = false;
            isConsumer = false;
        } else {
            title = "Buyer: Consumer";
            isSeller = false;
            isBuyer = false;
            isLConsumer = false;
        }
        defineWindow(title);
        setAgentComboBox();
        
        this.getRootPane().setDefaultButton(jButton_Save);
        jCheckBox_RiskNeutral.setSelected(true);

        jCheckBox_RiskAverse.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_RiskNeutral.setSelected(false);
                    jCheckBox_RiskSeeking.setSelected(false);
                }
            }
        });

        jCheckBox_RiskNeutral.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_RiskAverse.setSelected(false);
                    jCheckBox_RiskSeeking.setSelected(false);
                }
            }
        });

        jCheckBox_RiskSeeking.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_RiskAverse.setSelected(false);
                    jCheckBox_RiskNeutral.setSelected(false);
                }
            }
        });
    }
    
    private void defineWindow(String _name) {
        this.setTitle(_name);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
    }
    
    private void setAgentComboBox() {
        if (isSeller) {
            agentTotalNames = market.getProducerNames();
            System.out.println("Names:" + agentTotalNames);
            agentName = market.splitAgentTotalNames(agentTotalNames, market.getTotalProducer());
        } else if (isBuyer) {
            agentTotalNames = market.getSupplierNames();
            agentName = market.splitAgentTotalNames(agentTotalNames, market.getTotalSupplier());
        } else if (isLConsumer) {
//         agentTotalNames = market.largeConsumer_names
//            for (int i=0; i<data.largeConsumer_names.size(); i++){
            agentTotalNames = market.getLConsumerNames();
            agentName = market.splitAgentTotalNames(agentTotalNames, market.getTotalLConsumer());
//        }
        }else if (isConsumer) {
//         agentTotalNames = market.largeConsumer_names
//            for (int i=0; i<data.mediumConsumer_names.size(); i++){
                agentTotalNames = market.getConsumerNames();
            agentName = market.splitAgentTotalNames(agentTotalNames, market.getTotalConsumer());
         }
        else {
            agentTotalNames = market.getSupplierNames();
            agentName = market.splitAgentTotalNames(agentTotalNames, market.getTotalSupplier());
        }
//        }
            initComboBox();
    }

    public void initComboBox() {
        jComboBox_Name.removeAllItems();
        jComboBox_Name.addItem("Select Agent");

        for (String agentName1 : agentName) {
            jComboBox_Name.addItem(agentName1);
        }
        if (jComboBox_Name.getItemCount() == 0) {
            jComboBox_Name.addItem("");
        }
        jComboBox_Name.setSelectedIndex(0);
    }
    
    private String verifCheckBox() {
    String warning = "";
    if(jComboBox_Name.getSelectedIndex() == 0) {
        warning += "You must select one agent!\n";
    }
    if (!jCheckBox_RiskAverse.isSelected() && !jCheckBox_RiskNeutral.isSelected() && !jCheckBox_RiskSeeking.isSelected()) {
        warning += "You must select one risk attitude option!\n";
    }
    return warning;
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_Name = new javax.swing.JComboBox();
        jCheckBox_RiskAverse = new javax.swing.JCheckBox();
        jCheckBox_RiskNeutral = new javax.swing.JCheckBox();
        jCheckBox_RiskSeeking = new javax.swing.JCheckBox();
        jButton_Save = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Info", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("Agent :");

        jLabel2.setText("Risk Attitude :");

        jComboBox_Name.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jCheckBox_RiskAverse.setText("Risk-averse");
        jCheckBox_RiskAverse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_RiskAverseActionPerformed(evt);
            }
        });

        jCheckBox_RiskNeutral.setText("Risk-neutral");

        jCheckBox_RiskSeeking.setText("Risk-seeking");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBox_RiskNeutral)
                        .addGap(65, 65, 65))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(155, 155, 155)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox_RiskSeeking)
                            .addComponent(jCheckBox_RiskAverse))
                        .addContainerGap(61, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox_Name, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addComponent(jCheckBox_RiskAverse)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCheckBox_RiskNeutral))
                .addGap(18, 18, 18)
                .addComponent(jCheckBox_RiskSeeking)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jButton_Save.setText("Save");
        jButton_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_SaveActionPerformed(evt);
            }
        });

        jButton_Cancel.setText("Cancel");
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton_Save, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Cancel)
                    .addComponent(jButton_Save))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox_RiskAverseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_RiskAverseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox_RiskAverseActionPerformed

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton_CancelActionPerformed

    private void jButton_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SaveActionPerformed
        // TODO add your handling code here:
        String warning = verifCheckBox();
        if(warning.isEmpty()) {
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, warning,
            "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_SaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Save;
    private javax.swing.JCheckBox jCheckBox_RiskAverse;
    private javax.swing.JCheckBox jCheckBox_RiskNeutral;
    private javax.swing.JCheckBox jCheckBox_RiskSeeking;
    private javax.swing.JComboBox jComboBox_Name;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
