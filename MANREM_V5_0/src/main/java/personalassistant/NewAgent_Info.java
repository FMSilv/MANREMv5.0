package personalassistant;

import Coalition.CoalitionGui;
import buying.Buyer;
import buying.BuyerInputGui;
import jade.core.AID;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import selling.Seller;
import selling.SellerInputGui;

public class NewAgent_Info extends javax.swing.JFrame {
    
    private PersonalAssistant PA;
    private boolean isProducer;
    private String address;
    private String phone;
    private String email;
    private String name;
    private String objective;
    private ProducerData Producer;
    private BuyerData Buyer;

    public NewAgent_Info(boolean isProducer, ProducerData Producer, BuyerData Buyer, PersonalAssistant PA) {
        
        this.isProducer = isProducer;
        
        if(isProducer){
            this.name = Producer.getName();
            this.address = Producer.getAddress();
            this.phone = Producer.getPhone_number();
            this.email = Producer.getEmail();
            this.objective = Producer.getObjective();
            this.isProducer = isProducer;
            this.Producer = Producer;
        }else {
            this.name = Buyer.getName();
            this.address = Buyer.getAddress();
            this.phone = Buyer.getPhone_number();
            this.email = Buyer.getEmail();
            this.objective = Buyer.getObjective();
            this.Buyer = Buyer;
        }
        
        this.PA = PA;
        
        initComponents();
        if(isProducer)
            setWindow("GenCo");
        else
            setWindow("Retailer");
        setForms();

    }

    private void setForms() {
        jTextField_Name.setText(name);
        jTextField_Address.setText(address);
        jTextField_Telephone.setText(phone);
        jTextField_Email.setText(email);
        jTextField_Objective.setText(objective);
    }

    private void setWindow(String _title) {
        //this.setAlwaysOnTop(true);
        this.setTitle("New " + _title);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        jTextField_Name.setEditable(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_NewAgent = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_Name = new javax.swing.JTextField();
        jTextField_Address = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_Telephone = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_Email = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_Objective = new javax.swing.JTextField();
        jButton_Create = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel_NewAgent.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "New Agent", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("Name :");

        jLabel2.setText("Address :");

        jTextField_Name.setText("jTextField1");

        jTextField_Address.setText("jTextField2");
        jTextField_Address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_AddressActionPerformed(evt);
            }
        });

        jLabel3.setText("Telephone :");

        jTextField_Telephone.setText("jTextField3");

        jLabel4.setText("E-mail :");

        jTextField_Email.setText("jTextField4");

        jLabel5.setText("Objective:");

        jTextField_Objective.setText("jTextField1");

        javax.swing.GroupLayout jPanel_NewAgentLayout = new javax.swing.GroupLayout(jPanel_NewAgent);
        jPanel_NewAgent.setLayout(jPanel_NewAgentLayout);
        jPanel_NewAgentLayout.setHorizontalGroup(
            jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_NewAgentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_NewAgentLayout.createSequentialGroup()
                        .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                        .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_Objective, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Email, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Telephone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_NewAgentLayout.createSequentialGroup()
                        .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_Address, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_Name, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel_NewAgentLayout.setVerticalGroup(
            jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_NewAgentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_Address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_Telephone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel_NewAgentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_Objective, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton_Create.setText("Create");
        jButton_Create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CreateActionPerformed(evt);
            }
        });

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_NewAgent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_Create, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_NewAgent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Create)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_CreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CreateActionPerformed
        // TODO add your handling code here:
        ArrayList<String> personal_info = new ArrayList<>();
        if (jTextField_Name.getText().equalsIgnoreCase("New Name") || jTextField_Name.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid Agent Name!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            personal_info.add(jTextField_Name.getText());
            personal_info.add(jTextField_Address.getText());
            personal_info.add(jTextField_Telephone.getText());
            personal_info.add(jTextField_Email.getText());


            
            
            if(isProducer){
                PA.addAgent(new AID(this.Producer.getName(), AID.ISLOCALNAME), "producer", Producer, null);
                ProducerTechPortfolio Portfolio = new ProducerTechPortfolio(Producer, PA);
                Portfolio.setVisible(true);
            }else{
                PA.addAgent(new AID(this.Buyer.getName(), AID.ISLOCALNAME), "buyer", null, Buyer);
            }
            
            this.dispose();
        }
    }//GEN-LAST:event_jButton_CreateActionPerformed

    private void jTextField_AddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_AddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_AddressActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        if(isProducer){
            PA.killAgent(name.replace(" ", "_"), "producing.Producer");
        }else{
            PA.killAgent(name.replace(" ", "_"), "buying.Buyer");
        }
            
        
        this.dispose();
        
        
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_Create;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel_NewAgent;
    private javax.swing.JTextField jTextField_Address;
    private javax.swing.JTextField jTextField_Email;
    private javax.swing.JTextField jTextField_Name;
    private javax.swing.JTextField jTextField_Objective;
    private javax.swing.JTextField jTextField_Telephone;
    // End of variables declaration//GEN-END:variables
}
