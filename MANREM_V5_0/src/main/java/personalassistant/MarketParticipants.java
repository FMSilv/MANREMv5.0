/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalassistant;

//import jade.core.Agent;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
//import jxl.read.biff.BiffException;



public class MarketParticipants extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    

    private PersonalAssistant PA;
    private boolean isProducer;
    private final String[] Table = {"Id", "Name", "Day Ahead", "OTC"};
    String AgentName;
    String[] AgentNames;

    boolean isOTC; // Variable that holds if simulation to perform is OTC
    boolean isDayAhead; // Variable that holds if simulation to perform is Pool
    boolean isSMP;
    
    
     public MarketParticipants() {
        initComponents();
    }
    
    //Alternate Constructors for class MarketParticipants 
     
    public MarketParticipants(PersonalAssistant _PA, boolean _isProducer, boolean _isDayAhead, boolean _isSMP, boolean _isOTC) {
        
        initComponents();
        
        isDayAhead = _isDayAhead;
        isSMP = _isSMP;
        isOTC = _isOTC;
        
        PA = _PA;
        isProducer = _isProducer;
        
        defineWindow(isProducer);
        setLabel(isProducer);
        setAgentComboBox(isProducer);
        
        String[][] TableData;
        TableData = generateTableData();
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(TableData, Table));
  
    }
    
    private String[][] generateTableData(){
        
        int TableSize=0;
        int j;
        int k = 0;
        
        if(isProducer){
            TableSize = PA.Producers_Information.size();
        }else{
            TableSize = PA.Buyers_Information.size();
        }
            
        String [][] TableData = new String [TableSize][4];
        
        if(isProducer){
            for(int i = 0; i < PA.Producers_Information.size(); i++){
                if(PA.Producers_Information.get(i).getParticipating()){
                    
                    TableData[i][0] = "" + k;
                    TableData[i][1] = PA.Producers_Information.get(i).getName();
                    if(isDayAhead){
                        TableData[i][2] = "yes";
                    }else{
                        TableData[i][2] = "no";
                    }   
                    if(isOTC){
                        TableData[i][3] = "yes";
                    }else{
                        TableData[i][3] = "no";
                    }
                    k++;
                }
            }
        } else {
            for(int i = 0; i < PA.Buyers_Information.size(); i++){
                if(PA.Buyers_Information.get(i).getParticipating()){
                    
                    TableData[i][0] = "" + k;
                    TableData[i][1] = PA.Buyers_Information.get(i).getName();
                    if(isDayAhead){
                        TableData[i][2] = "yes";
                    }else{
                        TableData[i][2] = "no";
                    }   
                    if(isOTC){
                        TableData[i][3] = "yes";
                    }else{
                        TableData[i][3] = "no";
                    }
                    k++;
                }
            }
        }
        

        return TableData;
    }
        
    public MarketParticipants(boolean isSeller){   //Lembrar de tirar este depois!
        initComponents();
        
        defineWindow(isSeller);
        setLabel(isSeller);
        setAgentComboBox(isSeller);
        
    }
    
    
    private void defineWindow(boolean isSeller) {
        if(isSeller){
            this.setTitle("GenCo Data");
        }
        else{
            this.setTitle("RetailCo Data");
        }
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        jButtonNext.setEnabled(false);
    }
    
    
    private void setLabel(boolean isSeller){
        
        if (isSeller) {
            jLabel1.setText("Generator:");
        } else {
            jLabel1.setText("Retailer:");
        }
        
    }
    
    public void printOutputTable(Object[][] insertData, String[] columnSize) {
        DefaultTableModel table = new DefaultTableModel(insertData, columnSize);
        jTable1.setModel(table);
        configOutputTable(columnSize.length);
    }
    
    private void configOutputTable(int _size){

        for (int i = 0; i < _size; i++) {
            jTable1.getColumnModel().getColumn(i).setMinWidth(70);
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        jTable1.setDefaultRenderer(Object.class, render);
    }
    
    private void setAgentComboBox(boolean isProducer) {
        
        
        if(isProducer){
            AgentNames = new String[PA.Producers_Information.size()];
            
            for(int i = 0; i < PA.Producers_Information.size(); i++){
                AgentNames[i] = PA.Producers_Information.get(i).getName();
            }
            
        } else {
            AgentNames = new String[PA.Producers_Information.size()];
            
            for(int i = 0; i < PA.Buyers_Information.size(); i++){
                AgentNames[i] = PA.Buyers_Information.get(i).getName();
            }
        }
        
        initComboBox(AgentNames);
    }
    
    
    public void initComboBox(String[] AgentNames) {
        

        jComboBox1.removeAllItems();
        jComboBox1.addItem("Select Agent");

        for (int i = 0; i < AgentNames.length; i++) {

           jComboBox1.addItem(AgentNames[i]);
        }
        jComboBox1.setSelectedIndex(0);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonNext = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonNext.setText("Next");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(34, 34, 34)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCancel)
                .addGap(18, 18, 18)
                .addComponent(jButtonNext)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNext)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed

        MarketInfo marketinfo = new MarketInfo(PA, AgentName, isDayAhead, isSMP ,isOTC, isProducer);
        marketinfo.setVisible(true);
        
        this.dispose();
     
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        int i;
        i = jComboBox1.getSelectedIndex();

        if(i > 0){
            AgentName = AgentNames[i-1];
            jButtonNext.setEnabled(true);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MarketParticipants.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MarketParticipants.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MarketParticipants.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MarketParticipants.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MarketParticipants().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
