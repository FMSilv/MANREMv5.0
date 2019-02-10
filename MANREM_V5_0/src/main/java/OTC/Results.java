/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OTC;

import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author João de Sá
 */
public class Results extends javax.swing.JFrame {
    
    OTC_Market_Operator Operator;
    
    private int HOUR_PER_DAY = 24;
    private int START_HOUR = 0;
    
    private boolean OTC;
    
    ArrayList<OTC_AgentData> buyers;
    ArrayList<OTC_AgentData> sellers;
    
    private final String[] MarketTypeComboBox = {"Select Market Type", "Over The Counter", "Day-Ahead"};
    private final String[] outputComboBox1 = {"Select Output", "Bid/Offer Stack", "Generation commitments", 
        "Generator revenues", "Retailer results", "Market price"};
    private final String[] outputComboBox2 = {"Select Output", "Market Results", "Generation commitments", 
        "Generator revenues", "Retailer results", "Market price"};
    private Object[] HourComboBox;
    
    private String TabTitle1 = "Output Data";
    
    private Object[][][] SMP_Data_Day;
    private final String[] Bid_Offer_Stack_Title = {"Identifier","Date", "Volume [MWh]","Price [€/MWh]", "Bidding Company","Accept Bid"};
    private final String[] Generator_commitments = {"GenCo","Hour","Power [MW]","Traded Power [MW]","Price [€/MWh]", "Market Price [€/MWh]"};
    private final String[] Generator_Revenues = {"GenCo","Hour","Traded Power [MW]","Marginal Cost [€/MWh]", "Sales Price [€/MWh]","Revenue [€/h]", "Profit [€/h]"};
    private final String[] Retailer_Results = {"Retailer","Hour","Power [MW]","Traded Power [MW]","Price [€/MWh]", "Market Price [€/MWh]"};
    private final String[] Market_Price = {"Hour", "Market Price [€/MWh]"};
    private final String[] Market_Results = {"Hour", "Traded Power [€/MWh]", "Market Price [€/Mwh]"};
    
    /**
     * Creates new form Results
     */
    public Results(ArrayList<OTC_AgentData> _buyers, ArrayList<OTC_AgentData> _sellers, OTC_Market_Operator _Operator) {
        
        Operator = _Operator;
        
        buyers = _buyers;
        sellers = _sellers;
        Object[][] insertData;
        
        initComponents();
        defineWindow();
        
        initComboBox();
        
        insertData = GenerateBidData(Bid_Offer_Stack_Title);
        printOutputTable(insertData, Bid_Offer_Stack_Title);
        jTab.setTitleAt(0, TabTitle1);
        
    }

    private Results() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void defineWindow() {
        this.setTitle("Market Results");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }
        
    // market = 0 -> OTC
    // market = 1 -> Day Ahead
    private void initComboBox() {
          
        HourComboBox = new Object[HOUR_PER_DAY + 1];
        int indexHour = START_HOUR;
        
        for (int h = 0; h < HOUR_PER_DAY + 1; h++) {
            if (h == 0) {
                HourComboBox[h] = "All Day";
            } else {
                HourComboBox[h] = indexHour;
                indexHour++;
            }
        }
        
        jComboBox_Output.setModel(new DefaultComboBoxModel(outputComboBox1));
        jComboBox_Hour.setModel(new DefaultComboBoxModel(HourComboBox));
        jComboBox_MarketType.setModel(new DefaultComboBoxModel(MarketTypeComboBox));
        jComboBox_Output.setSelectedIndex(0);
        jComboBox_Hour.setSelectedIndex(0);
        jComboBox_MarketType.setSelectedIndex(0);
        
        OTC = true;
    }
    
    private void updateComboBox(boolean OTC) {
        
        if(OTC){
            jComboBox_Output.setModel(new DefaultComboBoxModel(outputComboBox1));
        }
        else{
            jComboBox_Output.setModel(new DefaultComboBoxModel(outputComboBox2));
        }
        jComboBox_Output.setSelectedIndex(0);
        jComboBox_Hour.setSelectedIndex(0);
    }
    
    private void updateOutputNameList(int index) {
        DefaultListModel listModel = new DefaultListModel();
        listModel.removeAllElements();
        listModel.addElement("All Data");
        jLabel3.setVisible(true);
        jComboBox_Hour.setVisible(true);
        
        Object[][] insertData;
        
        
        
        switch (index) {
            case 0:
                printOutputTable(null, Bid_Offer_Stack_Title);
                break;
            case 1: 
                insertData = GenerateBidData(Bid_Offer_Stack_Title);
                printOutputTable(insertData, Bid_Offer_Stack_Title);
            default:
                printOutputTable(null, Bid_Offer_Stack_Title);
                break;
        }
        jList1.setModel(listModel);
        jList1.setSelectedIndex(0);
    }
    
    public Object[][] GenerateBidData(String[] columnTitle){
        
        int CollumnSize = columnTitle.length;
        int RowSize = buyers.get(0).OTCPoweroffers.size()*(buyers.size()+sellers.size());
        Date dt = new Date();
        int buyersindex;
        int index = 0;
        Object[][] Data = new Object[RowSize][CollumnSize];
        
        // First shows all the bids for first period, buyers then sellers, then shows second period and so on
        
        for(int k = 0; k < buyers.get(0).OTCPoweroffers.size(); k++){
            for(int i = 0; i < (buyers.size() + sellers.size()); i++){
                if(i<buyers.size()){
                    Data[index][0] = "Buy" + (i+k+1);
                    Data[index][1] = "Day X, Hour "+k;
                    Data[index][2] = buyers.get(i).get_PowerOffers().get(k);
                    Data[index][3] = buyers.get(i).get_PriceOffers().get(k);
                    Data[index][4] = buyers.get(i).getName();
                    Data[index][5] = "yes/no";
                }else{
                    buyersindex = i - buyers.size();
                    Data[index][0] = "Sell" + (buyersindex+k+1);
                    Data[index][1] = "Day X, Hour "+k;
                    Data[index][2] = sellers.get(buyersindex).get_PowerOffers().get(k);
                    Data[index][3] = sellers.get(buyersindex).get_PriceOffers().get(k);
                    Data[index][4] = ""+sellers.get(buyersindex).getName();
                    if(sellers.get(buyersindex).getIsNegotiated_OTC()[k])
                        Data[index][5] = "yes";
                    else
                        Data[index][5] = "no";
                }
                index = index +1;
            }
        }
        
        return Data;
    }
    
    public void printOutputTable(Object[][] insertData, String[] columnSize) {
        DefaultTableModel table = new DefaultTableModel(insertData, columnSize);
        jTable1.setModel(table);
        configOutputTable(columnSize.length);
    }
    
    private void configOutputTable(int _size) {

        for (int i = 0; i < _size; i++) {
            if (i == 0) {
                jTable1.getColumnModel().getColumn(i).setPreferredWidth(80);
            } else if (i == 1) {
                jTable1.getColumnModel().getColumn(i).setPreferredWidth(40);
            } else {
                jTable1.getColumnModel().getColumn(i).setMinWidth(100);
            }
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        jTable1.setDefaultRenderer(Object.class, render);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jTab = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jComboBox_Output = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jComboBox_MarketType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBox_Hour = new javax.swing.JComboBox();
        jAcceptResults = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton2.setText("Close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
        jScrollPane2.setViewportView(jTable1);

        jTab.addTab("tab1", jScrollPane2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTab, javax.swing.GroupLayout.PREFERRED_SIZE, 769, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTab, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
        );

        jComboBox_Output.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_Output.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_OutputActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel4.setText("Market type:");

        jComboBox_MarketType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_MarketType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_MarketTypeActionPerformed(evt);
            }
        });

        jLabel1.setText("Output:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jComboBox_MarketType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jComboBox_Output, javax.swing.GroupLayout.Alignment.LEADING, 0, 169, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox_MarketType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox_Output, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Display Output Data");

        jLabel3.setText("Select Hour");

        jComboBox_Hour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_Hour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_HourActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(30, 30, 30)
                        .addComponent(jComboBox_Hour, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_Hour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(29, 29, 29))
        );

        jAcceptResults.setText("Accept Results");
        jAcceptResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAcceptResultsActionPerformed(evt);
            }
        });

        jButton4.setText("jButton4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jAcceptResults)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jAcceptResults)
                    .addComponent(jButton4))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox_MarketTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_MarketTypeActionPerformed
        // TODO add your handling code here:
        if(jComboBox_MarketType.getSelectedIndex() != 0){
            int j = jComboBox_MarketType.getSelectedIndex();
            jComboBox_MarketType.setSelectedIndex(j);
            jLabel1.setEnabled(true);
            if(jComboBox_MarketType.getSelectedIndex() == 1){
                updateComboBox(true);
                OTC = true;
            }
            else{
                updateComboBox(false);
                OTC = false;
            }
            jComboBox_Output.setEnabled(true);
        }
        else{
            jLabel1.setEnabled(false);
            jComboBox_Output.setEnabled(false);
            jList1.setEnabled(false);
            jLabel3.setEnabled(false);
            jComboBox_Hour.setEnabled(false);
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBox_MarketTypeActionPerformed

    private void jComboBox_OutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_OutputActionPerformed
        // TODO add your handling code here:
        if(jComboBox_Output.getSelectedIndex() != 0){
            jLabel1.setEnabled(true);
            jList1.setEnabled(true);
            jLabel3.setEnabled(true);
            jComboBox_Hour.setEnabled(true);
            jButton1.setEnabled(true);
            jComboBox_Hour.setSelectedIndex(0);
            
            
            if(jComboBox_Output.getSelectedIndex() == 1){
                if(OTC){
                    printOutputTable(null, Bid_Offer_Stack_Title);
                }
                else{
                    printOutputTable(null, Market_Results);
                }
            }
            else if(jComboBox_Output.getSelectedIndex() == 2){
                printOutputTable(null, Generator_commitments);
            }
            else if(jComboBox_Output.getSelectedIndex() == 3){
                printOutputTable(null, Generator_Revenues);
            }
            else if(jComboBox_Output.getSelectedIndex() == 4){
                printOutputTable(null, Retailer_Results);
            }
            else{
                printOutputTable(null, Market_Price);
            }
        }
        else {
            jLabel1.setEnabled(false);
            jList1.setEnabled(false);
            jLabel3.setEnabled(false);
            jComboBox_Hour.setEnabled(false);
            jButton1.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBox_OutputActionPerformed

    private void jComboBox_HourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_HourActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_HourActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jAcceptResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAcceptResultsActionPerformed
        // TODO add your handling code here:
        Operator.send_results();
    }//GEN-LAST:event_jAcceptResultsActionPerformed

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
            java.util.logging.Logger.getLogger(Results.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Results.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Results.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Results.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Results().setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAcceptResults;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox_Hour;
    private javax.swing.JComboBox jComboBox_MarketType;
    private javax.swing.JComboBox jComboBox_Output;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTab;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
