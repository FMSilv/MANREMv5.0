package wholesalemarket_LMP;

import wholesalemarket_LMP.simul.SupplierData;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import wholesalemarket_LMP.simul.WholesaleMarket;

public class Supplier_InputParameters extends JFrame {

    private Wholesale_InputData market;
    
    private final int initHour = WholesaleMarket.START_HOUR;
    private final int endHour = WholesaleMarket.END_HOUR;
    public int DAILY_HOURS = WholesaleMarket.HOUR_PER_DAY;

    Object[] columnNames_Min = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.MIN_CAP_TITLE);
    Object[] columnNames_Max = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.MAX_CAP_TITLE);
    Object[] columnNames_Start = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.START_PRICE_TITLE);
    Object[] columnNames_Slope = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.SLOPE_PRICE_TITLE);
    
    DefaultTableModel tableSupplierMin = null;
    DefaultTableModel tableSupplierMax = null;
    DefaultTableModel tableSuppliersStart = null;
    DefaultTableModel tableSuppliersSlope = null;

    private ArrayList<SupplierData> SUPPLIER_LIST;

    private SupplierData SUPPLIER;

    private SupplierInputData_Dynamic frame_Dynamic_Demand;

    private final int NAME_COLUMN = 0;
    private final int BUS_COLUMN = 1;
    private final int SUPPLIER_COLUMN = 26;


    private String supplierTotalNames;
    private String[] supplierNames;
    private int totalSuppliers;
    private int supplier_ID;
    private boolean existID;

    public int totalBusNr;

    private String nameData;
    private double busData;

    private Object[][] minDemandMatriz;
    private Object[][] maxDemandMatriz;
    private Object[][] StartCostMatriz;
    private Object[][] SlopeCostMatriz;

    private double[] minDemand_Supplier;
    private double[] maxDemand_Supplier;
    private double[] startCost_Supplier;
    private double[] slopeCost_Supplier;

    public Supplier_InputParameters(Wholesale_InputData _market) {
        market = _market;
        initComponents();
        defineWindow();
    }

    public final void defineWindow() {
        this.setTitle("Retailers Data");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        printTable(null, jTableSupplierInputMin, (DefaultTableModel) jTableSupplierInputMin.getModel(), columnNames_Min);
        printTable(null, jTableSupplierInputMax, (DefaultTableModel) jTableSupplierInputMin.getModel(), columnNames_Max);
        printTable(null, jTableSupplierInputStart, (DefaultTableModel) jTableSupplierInputMin.getModel(), columnNames_Start);
        printTable(null, jTableSupplierInputSlope, (DefaultTableModel) jTableSupplierInputMin.getModel(), columnNames_Slope);
    }

    private void configSupplierTable(TableModel _tableSupplierModel, JTable _table) {
        _table.setAutoscrolls(true);
        _table.setShowGrid(true);
        _table.setEnabled(true);

        for (int i = 0; i < _tableSupplierModel.getColumnCount(); i++) {
            if (i == NAME_COLUMN) {
                _table.getColumnModel().getColumn(i).setMinWidth(150);
                _table.getColumnModel().getColumn(i).setMaxWidth(200);
            } else if (i == BUS_COLUMN) {
                _table.getColumnModel().getColumn(i).setMaxWidth(70);
            } else {
                _table.getColumnModel().getColumn(i).setMaxWidth(70);
            }
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        _table.setDefaultRenderer(Object.class, render);
    }

    public void startSupplierFrame() {
        supplierTotalNames = market.getSupplierNames();
        totalSuppliers = market.getTotalSupplier();
        supplierNames = market.splitAgentTotalNames(supplierTotalNames, totalSuppliers);
        totalBusNr = market.getTotalBus();
        /*frame_SUPPLIER_Demand = new PowerDemandInput(this);
        frame_SUPPLIER_Demand.setVisible(false);*/
        SUPPLIER_LIST = new ArrayList<>();
        getOldTable();
        initComboBox();
    }

    public void initComboBox() {
        boolean verif;

        jComboBoxSupplierName.removeAllItems();
        jComboBoxSupplierName.addItem("Select Supplier Agent");

        if (SUPPLIER_LIST.isEmpty()) {
            for (int i = 0; i < totalSuppliers; i++) {
                jComboBoxSupplierName.addItem(supplierNames[i]);
            }
        } else {
            for (int i = 0; i < totalSuppliers; i++) {
                verif = false;
                for (SupplierData SUPPLIER1 : SUPPLIER_LIST) {
                    if (SUPPLIER1.getName().equalsIgnoreCase(supplierNames[i])) {
                        verif = true;
                        break;
                    }
                }
                if (!verif) {
                    jComboBoxSupplierName.addItem(supplierNames[i]);
                }
            }
        }
        jComboBoxSupplierName.setSelectedIndex(0);
    }

    public void printTable(Object[][] _insertDataMin, JTable _table,
            TableModel _tableModel, Object[] _columnNames) {

        _tableModel = new DefaultTableModel(_insertDataMin, _columnNames);
        _table.setModel(_tableModel);

        configSupplierTable(_tableModel, _table);

        _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _table.setEnabled(true);
    }

    public void getOldTable() {
        tableSupplierMin = (DefaultTableModel) jTableSupplierInputMin.getModel();
        tableSupplierMax = (DefaultTableModel) jTableSupplierInputMin.getModel();
        tableSuppliersStart = (DefaultTableModel) jTableSupplierInputMin.getModel();
        tableSuppliersSlope = (DefaultTableModel) jTableSupplierInputMin.getModel();

        int _rowNr = tableSupplierMin.getRowCount();

        String _name;
        double _atBus;
        double[] minDemand = new double[DAILY_HOURS];
        double[] maxDemand = new double[DAILY_HOURS];
        double[] startCost = new double[DAILY_HOURS];
        double[] slopeCost = new double[DAILY_HOURS];

        for (int i = 0; i < _rowNr; i++) {
            _name = (String) tableSupplierMin.getValueAt(i, NAME_COLUMN);
            _atBus = (double) tableSupplierMin.getValueAt(i, BUS_COLUMN);
            for (int j = 0; j < DAILY_HOURS; j++) {
                minDemand[j] = (double) tableSupplierMin.getValueAt(i, j + 2);
                maxDemand[j] = (double) tableSupplierMax.getValueAt(i, j + 2);
                startCost[j] = (double) tableSuppliersStart.getValueAt(i, j + 2);
                slopeCost[j] = (double) tableSuppliersSlope.getValueAt(i, j + 2);
            }

            for (int j = 1; j < SUPPLIER_LIST.size() + 2; j++) {
                existID = false;
                for (SupplierData SUPPLIER1 : SUPPLIER_LIST) {
                    if (SUPPLIER1.getSupplierID() == j) {
                        existID = true;
                        break;
                    }
                }
                if (!existID) {
                    supplier_ID = j;
                    break;
                }
            }

            SUPPLIER = new SupplierData(_name, supplier_ID, _atBus, startCost, slopeCost, minDemand, maxDemand);

            SUPPLIER_LIST.add(SUPPLIER);
        }
    }

    public void updateTableInfo() {
        if (!SUPPLIER_LIST.isEmpty()) {
            minDemandMatriz = new Object[SUPPLIER_LIST.size()][SUPPLIER_COLUMN];
            maxDemandMatriz = new Object[SUPPLIER_LIST.size()][SUPPLIER_COLUMN];
            StartCostMatriz = new Object[SUPPLIER_LIST.size()][SUPPLIER_COLUMN];
            SlopeCostMatriz = new Object[SUPPLIER_LIST.size()][SUPPLIER_COLUMN];
            int i = 0;
            for (SupplierData SUPPLIER1 : SUPPLIER_LIST) {
                minDemandMatriz[i][NAME_COLUMN] = SUPPLIER1.getName();
                minDemandMatriz[i][BUS_COLUMN] = SUPPLIER1.getAtBus();

                maxDemandMatriz[i][NAME_COLUMN] = SUPPLIER1.getName();
                maxDemandMatriz[i][BUS_COLUMN] = SUPPLIER1.getAtBus();

                StartCostMatriz[i][NAME_COLUMN] = SUPPLIER1.getName();
                StartCostMatriz[i][BUS_COLUMN] = SUPPLIER1.getAtBus();

                SlopeCostMatriz[i][NAME_COLUMN] = SUPPLIER1.getName();
                SlopeCostMatriz[i][BUS_COLUMN] = SUPPLIER1.getAtBus();

                for (int j = 0; j < DAILY_HOURS; j++) {
                    minDemandMatriz[i][j + 2] = SUPPLIER1.getLoadFixedDemand(j);
                    maxDemandMatriz[i][j + 2] = SUPPLIER1.getMaxDemand(j);
                    StartCostMatriz[i][j + 2] = SUPPLIER1.getStartCost(j);
                    SlopeCostMatriz[i][j + 2] = SUPPLIER1.getSlopeCost(j);
                }
                i++;
            }
            printTable(minDemandMatriz, jTableSupplierInputMin, tableSupplierMin, columnNames_Min);
            printTable(maxDemandMatriz, jTableSupplierInputMax, tableSupplierMax, columnNames_Max);
            printTable(StartCostMatriz, jTableSupplierInputStart, tableSuppliersStart, columnNames_Start);
            printTable(SlopeCostMatriz, jTableSupplierInputSlope, tableSuppliersSlope, columnNames_Slope);
        } else {
            printTable(null, jTableSupplierInputMin, tableSupplierMin, columnNames_Min);
            printTable(null, jTableSupplierInputMax, tableSupplierMax, columnNames_Max);
            printTable(null, jTableSupplierInputStart, tableSuppliersStart, columnNames_Start);
            printTable(null, jTableSupplierInputSlope, tableSuppliersSlope, columnNames_Slope);
        }
    }

    public void remove_SUPPLIER_List() {
        int _index = jTableSupplierInputMin.getSelectedRow();
        if (_index == -1) {
            _index = jTableSupplierInputMax.getSelectedRow();
            if (_index == -1) {
                _index = jTableSupplierInputStart.getSelectedRow();
                if (_index == -1) {
                    _index = jTableSupplierInputSlope.getSelectedRow();
                    if (_index == -1) {
                        _index = SUPPLIER_LIST.size() - 1;
                    }
                }
            }
        }

        try {
            SupplierData Aux = SUPPLIER_LIST.remove(_index);

            jComboBoxSupplierName.addItem(Aux.getName());

            updateTableInfo();
            initComboBox();

        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "Error! No Data",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void set_SUPPLIER_List() {
        try {
            for (int j = 1; j < SUPPLIER_LIST.size() + 2; j++) {
                    existID = false;
                    for (SupplierData SUPPLIER1 : SUPPLIER_LIST) {
                        if (SUPPLIER1.getSupplierID() == j) {
                            existID = true;
                            break;
                        }
                    }
                    if (!existID) {
                        supplier_ID = j;
                        break;
                    }
                }
            System.out.println(nameData);
                SUPPLIER = new SupplierData(nameData, supplier_ID, busData, startCost_Supplier, slopeCost_Supplier, minDemand_Supplier, maxDemand_Supplier);

                SUPPLIER_LIST.add(SUPPLIER);

                updateTableInfo();
                initComboBox();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error! Input Data Incorrect",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setNameFrame(String _name) {
        nameData = _name;
    }

    public String getNameFrame() {
        return nameData;
    }

    public void setBusFrame(int _bus) {
        busData = (double) _bus;
    }

    public double getBusFrame() {
        return busData;
    }

    public double[] getMinDemand() {
        return minDemand_Supplier;
    }

    public double[] getMaxDemand() {
        return maxDemand_Supplier;
    }

    public double[] getStartCost() {
        return startCost_Supplier;
    }

    public double[] getSlopeCost() {
        return slopeCost_Supplier;
    }

    public void setMinDemand(double[] _data) {
        minDemand_Supplier = _data;
    }

    public void setMaxDemand(double[] _data) {
        maxDemand_Supplier = _data;
    }

    public void setStartCost(double[] _data) {
        startCost_Supplier = _data;
    }

    public void setSlopeCost(double[] _data) {
        slopeCost_Supplier = _data;
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel26 = new javax.swing.JLabel();
        jComboBoxSupplierName = new javax.swing.JComboBox();
        jButtonRemoveSupplier = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableSupplierInputMin = new javax.swing.JTable();
        jButtonCancelInput = new javax.swing.JButton();
        jButtonSubmitInput = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableSupplierInputMax = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSupplierInputStart = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableSupplierInputSlope = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel26.setText("Agent Name:");

        jComboBoxSupplierName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxSupplierName.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBoxSupplierNamePopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        jButtonRemoveSupplier.setText("Remove Retailer");
        jButtonRemoveSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveSupplierActionPerformed(evt);
            }
        });

        jTableSupplierInputMin.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(jTableSupplierInputMin);

        jButtonCancelInput.setText("Cancel");
        jButtonCancelInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelInputActionPerformed(evt);
            }
        });

        jButtonSubmitInput.setText("Submit");
        jButtonSubmitInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitInputActionPerformed(evt);
            }
        });

        jTableSupplierInputMax.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableSupplierInputMax);

        jTableSupplierInputStart.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTableSupplierInputStart);

        jTableSupplierInputSlope.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTableSupplierInputSlope);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSubmitInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancelInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxSupplierName, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonRemoveSupplier)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jComboBoxSupplierName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRemoveSupplier))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelInput)
                    .addComponent(jButtonSubmitInput))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelInputActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelInputActionPerformed

    private void jComboBoxSupplierNamePopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBoxSupplierNamePopupMenuWillBecomeInvisible
        if (jComboBoxSupplierName.getSelectedIndex() != 0) {
            frame_Dynamic_Demand = new SupplierInputData_Dynamic(this);
            frame_Dynamic_Demand.showWindow_SupplierPowerDemand(jComboBoxSupplierName.getSelectedItem().toString());
            //frame_Dynamic_Demand.setVisible(true);
            /*frame_SUPPLIER_Demand.setName_Bus(jComboBoxSupplierName.getSelectedItem().toString());
            frame_SUPPLIER_Demand.setVisible(true);*/
        }
    }//GEN-LAST:event_jComboBoxSupplierNamePopupMenuWillBecomeInvisible

    private void jButtonRemoveSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveSupplierActionPerformed
        remove_SUPPLIER_List();
    }//GEN-LAST:event_jButtonRemoveSupplierActionPerformed

    private void jButtonSubmitInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitInputActionPerformed
        if (SUPPLIER_LIST.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Need to insert Supplier's Data!\n",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        } else {
            market.setSUPPLIER_List(SUPPLIER_LIST);
            
            //WholesaleMarket auxmarket = new WholesaleMarket(market);
            //auxmarket.startSimulation();
            this.dispose();
        }
    }//GEN-LAST:event_jButtonSubmitInputActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancelInput;
    private javax.swing.JButton jButtonRemoveSupplier;
    private javax.swing.JButton jButtonSubmitInput;
    private javax.swing.JComboBox jComboBoxSupplierName;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTableSupplierInputMax;
    private javax.swing.JTable jTableSupplierInputMin;
    private javax.swing.JTable jTableSupplierInputSlope;
    private javax.swing.JTable jTableSupplierInputStart;
    // End of variables declaration//GEN-END:variables
}
