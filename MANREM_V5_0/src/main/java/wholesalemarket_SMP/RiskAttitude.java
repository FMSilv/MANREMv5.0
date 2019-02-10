package wholesalemarket_SMP;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JOptionPane;
import scheduling.ProducerScheduling;
import wholesalemarket_LMP.ProducerInputData_Dynamic;
import wholesalemarket_LMP.Producer_InputParameters;

public class RiskAttitude extends javax.swing.JFrame {

    private ProducerInputData_Dynamic frame_Dynamic_Demand;
    private Producer_InputParameters mainSupplier;
    private InputData_Agents mainGenerator;
    private int algoritm;
    private String agentName;
    private Agent market;

    public RiskAttitude(Agent Market, Producer_InputParameters _mainSupplier, InputData_Agents _mainGenerator, String _name, int _algorithm) {
        market=Market;
        initComponents();
        algoritm = _algorithm;
        if (algoritm == 1) {
            mainSupplier = _mainSupplier;
        } else {
            mainGenerator = _mainGenerator;
        }
        agentName = _name;
        defineWindow(agentName);

        this.getRootPane().setDefaultButton(jButton_Next);
        jCheckBox_riskB.setSelected(true);

        jCheckBox_riskA.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_riskB.setSelected(false);
                    jCheckBox_riskC.setSelected(false);
                }
            }
        });

        jCheckBox_riskB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_riskA.setSelected(false);
                    jCheckBox_riskC.setSelected(false);
                }
            }
        });

        jCheckBox_riskC.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_riskA.setSelected(false);
                    jCheckBox_riskB.setSelected(false);
                }
            }
        });
        // Change panel name to Market: (market type)
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Market: Pool", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

    }

    private void defineWindow(String _name) {
        this.setTitle(_name);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jButton_Back.setEnabled(false);
        jComboBox_Business.removeAllItems();
        jComboBox_Business.addItem("Select business strategy");
    }

    private String verifChaeckBox() {
        String warning = "";
        if (!jCheckBox_riskA.isSelected() && !jCheckBox_riskB.isSelected() && !jCheckBox_riskC.isSelected()) {
            warning += "You must select one risk attitude option!\n";
        }
        return warning;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox_riskA = new javax.swing.JCheckBox();
        jCheckBox_riskB = new javax.swing.JCheckBox();
        jCheckBox_riskC = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_Business = new javax.swing.JComboBox();
        jButton_Cancel = new javax.swing.JButton();
        jButton_Next = new javax.swing.JButton();
        jButton_Back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Info", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("Risk attitude :");

        jCheckBox_riskA.setText("Risk-averse");

        jCheckBox_riskB.setText("Risk-neutral");

        jCheckBox_riskC.setText("Risk-seeking");

        jLabel2.setText("Business Strategy :");

        jComboBox_Business.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_Business.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_BusinessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox_riskC)
                            .addComponent(jCheckBox_riskB)
                            .addComponent(jCheckBox_riskA)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jComboBox_Business, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox_riskA)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox_riskB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox_riskC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_Business, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(23, 23, 23))
        );

        jButton_Cancel.setText("Cancel");
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });

        jButton_Next.setText("Next");
        jButton_Next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_NextActionPerformed(evt);
            }
        });

        jButton_Back.setText("Back");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 1, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_Next, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Next)
                    .addComponent(jButton_Cancel)
                    .addComponent(jButton_Back))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton_CancelActionPerformed

    private void jButton_NextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NextActionPerformed
        String warning = verifChaeckBox();
        if (warning.isEmpty()) {
            if (algoritm == 1) {
                mainSupplier.openRiskAttitude();
//                                        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
//        msg.setOntology("market_ontology");
//        msg.setProtocol("hello_protocol");
//        msg.setContent("spot");
//        msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));       
//        market.send(msg); 
//        msg.setContent("spot");
//                new ProducerScheduling(agentName, mainGenerator).setVisible(true);
//                        while(0==ProducerScheduling.a){
//            
//        }
//                       mainGenerator.openRiskAttitude();
                this.dispose();
            } else {
                mainGenerator.openRiskAttitude();
//                        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
//        msg.setOntology("market_ontology");
//        msg.setProtocol("hello_protocol");
//        msg.setContent("spot");
//        msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));       
//        market.send(msg); 
                
                //new ProducerScheduling(agentName, mainGenerator).setVisible(true);
                this.dispose();
//                                       while(0==ProducerScheduling.a){
//            
//        }
//                mainGenerator.openRiskAttitude();
                
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, warning,
                        "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_NextActionPerformed

    private void jComboBox_BusinessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_BusinessActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_BusinessActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Next;
    private javax.swing.JCheckBox jCheckBox_riskA;
    private javax.swing.JCheckBox jCheckBox_riskB;
    private javax.swing.JCheckBox jCheckBox_riskC;
    private javax.swing.JComboBox jComboBox_Business;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
