/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package market.panel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import personalassistant.PersonalAssistant;

/**
 *
 * @author Hugo
 */
public class DR extends javax.swing.JPanel {

    public PersonalAssistant mark;
    /**
     * Creates new form risk
     */
    public DR(PersonalAssistant market) {
        mark=market;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
//        jLabel3 = new javax.swing.JLabel();
        
        Listener listener = new Listener();
        
        

        jLabel1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel1.setText("DR Program");

//        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel2.setText("Please select the customer's Demand ");

        jCheckBox5.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jCheckBox5.setText("Time-of-use Rates");
        jCheckBox5.addItemListener(listener);
        jCheckBox5.setSelected(true);
       

        jCheckBox6.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jCheckBox6.setText("Day-ahead hourly Pricing");
        jCheckBox6.addItemListener(listener);

        jCheckBox7.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jCheckBox7.setText("Real time hourly Pricing");
        jCheckBox7.addItemListener(listener);
        
        jCheckBox9.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jCheckBox9.setText("Shifting");
        jCheckBox9.addItemListener(listener);

        jCheckBox8.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jCheckBox8.setText("Foregoing");
        jCheckBox8.addItemListener(listener);

        jCheckBox10.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jCheckBox10.setText("None");
        jCheckBox10.addItemListener(listener);
        jCheckBox10.setSelected(true);

        jLabel4.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel4.setText("Customer Load");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel5.setText("Response");

//        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
//        jLabel3.setText("Response  Program and Load response.");

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
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox9)
                                    .addComponent(jCheckBox8)
                                    .addComponent(jCheckBox10)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox6)
                                    .addComponent(jCheckBox5)
                                    .addComponent(jCheckBox7)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                            .addComponent(jLabel2)
//                            .addComponent(jLabel3)
                )))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
//                .addGap(20, 20, 20)
//                .addComponent(jLabel2)
//                .addGap(2, 2, 2)
//                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jCheckBox5)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox6)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel5))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jCheckBox8)
                        .addGap(0, 0, 0)
                        .addComponent(jCheckBox9)
                        .addGap(0, 0, 0)
                        .addComponent(jCheckBox10)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
    }// </editor-fold>

     private class Listener implements ItemListener {
public void itemStateChanged(ItemEvent e) {
  
    Object source = e.getItemSelectable();

    if (source == jCheckBox5 && e.getStateChange() != ItemEvent.DESELECTED) {
//        mark.buyer_risk=0;
//        buyer.setDemandResponse(demandresponse);
        if(jCheckBox6.isSelected()){
         jCheckBox6.setSelected(false);
        }
        if(jCheckBox7.isSelected()){
         jCheckBox7.setSelected(false);
        }
//         chinButton.setSelected(true);
    }
     if (source == jCheckBox5 && e.getStateChange() == ItemEvent.DESELECTED) {
            
//            buyer.setDemandResponse(demandresponse);
            if(jCheckBox6.isSelected()){
//         mark.buyer_risk=1;
        }
               if(jCheckBox7.isSelected()){
//         mark.buyer_risk=2;
        }
             if(!jCheckBox6.isSelected()&&!jCheckBox7.isSelected()){
                 jCheckBox5.setSelected(true);
             }
               
               
//             chinButton.setSelected(false);
        }
         if (source == jCheckBox6 && e.getStateChange() != ItemEvent.DESELECTED) {
//        mark.buyer_risk=1;
//        buyer.setDemandResponse(demandresponse);
        if(jCheckBox5.isSelected()){
         jCheckBox5.setSelected(false);
        }
        if(jCheckBox7.isSelected()){
         jCheckBox7.setSelected(false);
        }
    
    }
     if (source == jCheckBox6 && e.getStateChange() == ItemEvent.DESELECTED) {
            
//            buyer.setDemandResponse(demandresponse);
            if(jCheckBox5.isSelected()){
//         mark.buyer_risk=0;
        }
               if(jCheckBox7.isSelected()){
//         mark.buyer_risk=2;
        }
               if(!jCheckBox5.isSelected()&&!jCheckBox7.isSelected()){
                 jCheckBox6.setSelected(true);
             }
//             chinButton.setSelected(false);
        }
              if (source == jCheckBox7 && e.getStateChange() != ItemEvent.DESELECTED) {
//        mark.buyer_risk=2;
//        buyer.setDemandResponse(demandresponse);
        if(jCheckBox5.isSelected()){
         jCheckBox5.setSelected(false);
        }
        if(jCheckBox6.isSelected()){
         jCheckBox6.setSelected(false);
        }
    
    }
     if (source == jCheckBox7 && e.getStateChange() == ItemEvent.DESELECTED) {
            
//            buyer.setDemandResponse(demandresponse);
            if(jCheckBox5.isSelected()){
//        mark.buyer_risk=0;
        }
               if(jCheckBox6.isSelected()){
//         mark.buyer_risk=1;
        }
               if(!jCheckBox6.isSelected()&&!jCheckBox5.isSelected()){
                 jCheckBox7.setSelected(true);
             }
//             chinButton.setSelected(false);
        }
     if (source == jCheckBox8 && e.getStateChange() != ItemEvent.DESELECTED) {
        mark.demandresponse=1;
//        buyer.setDemandResponse(demandresponse);
        if(jCheckBox9.isSelected()){
         jCheckBox9.setSelected(false);
        }
        if(jCheckBox10.isSelected()){
         jCheckBox10.setSelected(false);
        }
//         chinButton.setSelected(true);
    }
     if (source == jCheckBox8 && e.getStateChange() == ItemEvent.DESELECTED) {
            
//            buyer.setDemandResponse(demandresponse);
            if(jCheckBox9.isSelected()){
         mark.demandresponse=1;
        }
               if(jCheckBox10.isSelected()){
         mark.demandresponse=0;
        }
             if(!jCheckBox9.isSelected()&&!jCheckBox10.isSelected()){
                 jCheckBox8.setSelected(true);
             }
               
               
//             chinButton.setSelected(false);
        }
         if (source == jCheckBox9 && e.getStateChange() != ItemEvent.DESELECTED) {
        mark.demandresponse=1;
//        buyer.setDemandResponse(demandresponse);
        if(jCheckBox8.isSelected()){
         jCheckBox8.setSelected(false);
        }
        if(jCheckBox10.isSelected()){
         jCheckBox10.setSelected(false);
        }
    
    }
     if (source == jCheckBox9 && e.getStateChange() == ItemEvent.DESELECTED) {
            
//            buyer.setDemandResponse(demandresponse);
            if(jCheckBox8.isSelected()){
         mark.buyer_risk=0;
        }
               if(jCheckBox10.isSelected()){
         mark.demandresponse=0;
        }
               if(!jCheckBox8.isSelected()&&!jCheckBox10.isSelected()){
                 jCheckBox9.setSelected(true);
             }
//             chinButton.setSelected(false);
        }
              if (source == jCheckBox10 && e.getStateChange() != ItemEvent.DESELECTED) {
        mark.demandresponse=0;
//        buyer.setDemandResponse(demandresponse);
        if(jCheckBox8.isSelected()){
         jCheckBox8.setSelected(false);
        }
        if(jCheckBox9.isSelected()){
         jCheckBox9.setSelected(false);
        }
    
    }
     if (source == jCheckBox10 && e.getStateChange() == ItemEvent.DESELECTED) {
            
//            buyer.setDemandResponse(demandresponse);
            if(jCheckBox8.isSelected()){
        mark.demandresponse=1;
        }
               if(jCheckBox9.isSelected()){
         mark.demandresponse=1;
        }
               if(!jCheckBox8.isSelected()&&!jCheckBox9.isSelected()){
                 jCheckBox10.setSelected(true);
             }
//             chinButton.setSelected(false);
        }
}
}

    // Variables declaration - do not modify
    public javax.swing.JCheckBox jCheckBox10;
    public javax.swing.JCheckBox jCheckBox5;
    public javax.swing.JCheckBox jCheckBox6;
    public javax.swing.JCheckBox jCheckBox7;
    public javax.swing.JCheckBox jCheckBox8;
    public javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JLabel jLabel1;
//    private javax.swing.JLabel jLabel2;
//    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
//    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration
}
