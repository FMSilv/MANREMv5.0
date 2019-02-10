package wholesalemarket_LMP;

import javax.swing.JOptionPane;
import wholesalemarket_LMP.simul.WholesaleMarket;

public class GridGlobalParameters extends javax.swing.JFrame {

    Wholesale_InputData market;
    Pricing_Mechanism_Form backWindow;
    private final int BUS_MAX = 10;
    private final int BRANCH_MAX = 10;
    private final int HOUR_MAX = 24;
    private final int GRID_EXCEL_ROWS = 1;
    private final int GRID_EXCEL_COLUMNS = 4;
    private static int excel_totalBranch;
    private static int excel_totalProducers;
    private static int excel_totalSuppliers;
    private double[][] defaultData;
    

    public GridGlobalParameters(Wholesale_InputData _market) {
        market = _market;
        initComponents();
        defineWindow();
        uploadExcelData();
    }
    
    public GridGlobalParameters(Wholesale_InputData _market, Pricing_Mechanism_Form _backWindow) {
        market = _market;
        backWindow = _backWindow;
        initComponents();
        defineWindow();
        uploadExcelData();
    }

    private void uploadExcelData() {
        try {
            defaultData = ReadExcel.readExcelData(WholesaleMarket.Default_Case, "GRID_DATA", GRID_EXCEL_ROWS, GRID_EXCEL_COLUMNS, false);
            excel_totalBranch = (int) defaultData[0][1];
            excel_totalProducers = (int) defaultData[0][2];
            excel_totalSuppliers = (int) defaultData[0][3];
            //System.out.println("Branch: "+excel_totalBranch+"\nProducers: "+excel_totalProducers+"\nSuppliers: "+excel_totalSuppliers+"\n");
        } catch (Exception ex) {
            excel_totalBranch = -1;
            excel_totalProducers = -1;
            excel_totalSuppliers = -1;
            JOptionPane.showMessageDialog(this, "Default Data Unavailable",
                    "Default Data Upload", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void defineWindow() {
        defaultValues();
        this.setTitle("Grid Parameters");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
    }
    
    private void defaultValues() {
        jComboBoxBusesNumb.removeAllItems();
        jComboBoxBranchesNumb.removeAllItems();

        for (int i = 2; i < BUS_MAX; i++) {
            jComboBoxBusesNumb.addItem(i);
        }
        for (int i = 1; i < BRANCH_MAX; i++) {
            jComboBoxBranchesNumb.addItem(i);
        }

        jComboBoxBusesNumb.setSelectedIndex(0);
        jComboBoxBranchesNumb.setSelectedIndex(0);
    }

    private String dataAvailable() {
        String warning = "";
        int _totalBranch = Integer.parseInt(jComboBoxBranchesNumb.getSelectedItem().toString());
        int _totalBus = Integer.parseInt(jComboBoxBusesNumb.getSelectedItem().toString());
        if (_totalBus < 2) {
            warning += "The grid must have 2 or more BUSES!\n";
        }
        if (_totalBranch < _totalBus - 1) {
            warning += "Insufficient number of BRANCHES. All BUSES must have at least one connection!\n";
        }
        return warning;
    }

    public static int get_Excel_totalBranches() {
        return excel_totalBranch;
    }

    public static int get_Excel_totalProducers() {
        return excel_totalProducers;
    }

    public static int get_Excel_totalSuppliers() {
        return excel_totalSuppliers;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonNextGrid = new javax.swing.JButton();
        jButtonDefaultGrid = new javax.swing.JButton();
        jButtonCancelGrid = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxBusesNumb = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxBranchesNumb = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jButtonNextGrid.setText("Next");
        jButtonNextGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextGridActionPerformed(evt);
            }
        });

        jButtonDefaultGrid.setText("Back");
        jButtonDefaultGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDefaultGridActionPerformed(evt);
            }
        });

        jButtonCancelGrid.setText("Cancel");
        jButtonCancelGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelGridActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Number of Buses:");

        jComboBoxBusesNumb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Number of Branches:");

        jComboBoxBranchesNumb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxBusesNumb, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxBranchesNumb, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxBusesNumb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBoxBranchesNumb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonDefaultGrid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNextGrid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancelGrid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelGrid)
                    .addComponent(jButtonNextGrid)
                    .addComponent(jButtonDefaultGrid))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelGridActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelGridActionPerformed

    private void jButtonDefaultGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDefaultGridActionPerformed
        //defaultValues();
        backWindow.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonDefaultGridActionPerformed

    private void jButtonNextGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextGridActionPerformed
        String warning = dataAvailable();
        if (warning.isEmpty()) {
            try {
                market.setTotalBranch(Integer.parseInt(jComboBoxBranchesNumb.getSelectedItem().toString()));
                market.setTotalBus(Integer.parseInt(jComboBoxBusesNumb.getSelectedItem().toString()));
                this.setVisible(false);
                backWindow.initJFrames();
                try {
                    //market.createFrame_Producer_Supplier();
                    //market.activeFrame_BRANCHES();
                    backWindow.activeFrame_BRANCHES();
                } catch (Exception ex) {
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error! Review your input data. If the problem persist please report the administrator",
                        "Verify Input Data", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, warning,
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonNextGridActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancelGrid;
    private javax.swing.JButton jButtonDefaultGrid;
    private javax.swing.JButton jButtonNextGrid;
    private javax.swing.JComboBox jComboBoxBranchesNumb;
    private javax.swing.JComboBox jComboBoxBusesNumb;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

   
    
}
