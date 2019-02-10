package wholesalemarket_LMP;

import jade.core.Agent;
import wholesalemarket_LMP.simul.ProducerData;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import wholesalemarket_LMP.simul.WholesaleMarket;
import wholesalemarket_SMP.RiskAttitude;

public class Producer_InputParameters extends JFrame {

    private Wholesale_InputData market;
    
    private Agent Market;
    
    private final int initHour = WholesaleMarket.START_HOUR;
    private final int endHour = WholesaleMarket.END_HOUR;
    public int DAILY_HOURS = WholesaleMarket.HOUR_PER_DAY;

    Object[] columnNames_Min = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.MIN_CAP_TITLE);
    Object[] columnNames_Max = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.MAX_CAP_TITLE);
    Object[] columnNames_Start = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.START_PRICE_TITLE);
    Object[] columnNames_Slope = DataInput_Producer_Supplier.createColumnTitlesMinCap(initHour, endHour, DataInput_Producer_Supplier.Type.SLOPE_PRICE_TITLE);
    
    DefaultTableModel tableProducerMinCap = null;
    DefaultTableModel tableProducerMaxCap = null;
    DefaultTableModel tableProducerStartCost = null;
    DefaultTableModel tableProducerSlopeCost = null;

    private ArrayList<ProducerData> PRODUCER_LIST;

    private ProducerData PRODUCER;

    private ProducerInputData_Dynamic frame_Dynamic_Demand;

    private final int NAME_COLUMN = 0;
    private final int BUS_COLUMN = 1;
    private final int PRODUCER_COLUMN = 26;
    
    private String producerTotalNames;
    private String[] producerNames;
    private DefaultListModel agentNames1;
    private DefaultListModel agentNames2;
    private int totalProducers;
    private int producer_ID;
    private boolean existID;

    public int totalBusNr;

    private String nameData;
    private double busData;

    private Object[][] minCapacityMatriz;
    private Object[][] maxCapacityMatriz;
    private Object[][] StartCostMatriz;
    private Object[][] SlopeCostMatriz;

    private double[] minCapacity_Producer;
    private double[] maxCapacity_Producer;
    private double[] startCost_Producer;
    private double[] slopeCost_Producer;

    public Producer_InputParameters(Agent Market, Wholesale_InputData _market) {
        Market = Market;
        market = _market;
        initComponents();
        defineWindow();
    }

    public final void defineWindow() {
        this.setTitle("Generators Data");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        printTable(null, jTableProducerInputMin, (DefaultTableModel) jTableProducerInputMin.getModel(), columnNames_Min);
        printTable(null, jTableProducerInputMax, (DefaultTableModel) jTableProducerInputMin.getModel(), columnNames_Max);
        printTable(null, jTableProducerInputStart, (DefaultTableModel) jTableProducerInputMin.getModel(), columnNames_Start);
        printTable(null, jTableProducerInputSlope, (DefaultTableModel) jTableProducerInputMin.getModel(), columnNames_Slope);
    }

    private void configProducerTable(TableModel _tableModel, JTable _table) {
        _table.setAutoscrolls(true);
        _table.setShowGrid(true);
        _table.setEnabled(true);

        for (int i = 0; i < _tableModel.getColumnCount(); i++) {
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

    public void startProducerFrame() {
        producerTotalNames = market.getProducerNames();
        totalProducers = market.getTotalProducer();
        producerNames = market.splitAgentTotalNames(producerTotalNames, totalProducers);
        totalBusNr = market.getTotalBus();
        PRODUCER_LIST = new ArrayList<>();
        getOldTable();
        initComboBox();
    }

    public void initComboBox() {
        boolean verif;

        jComboBoxProducerName.removeAllItems();
        jComboBoxProducerName.addItem("Select Producer Agent");

        if (PRODUCER_LIST.isEmpty()) {
            for (int i = 0; i < totalProducers; i++) {
                jComboBoxProducerName.addItem(producerNames[i]);
            }
        } else {
            for (int i = 0; i < totalProducers; i++) {
                verif = false;
                for (ProducerData PRODUCER1 : PRODUCER_LIST) {
                    if (PRODUCER1.getName().equalsIgnoreCase(producerNames[i])) {
                        verif = true;
                        break;
                    }
                }
                if (!verif) {
                    jComboBoxProducerName.addItem(producerNames[i]);
                }
            }
        }
        jComboBoxProducerName.setSelectedIndex(0);
    }

    public void printTable(Object[][] _insertDataMin, JTable _table,
            TableModel _tableModel, Object[] _columnNames) {

        _tableModel = new DefaultTableModel(_insertDataMin, _columnNames);
        _table.setModel(_tableModel);

        configProducerTable(_tableModel, _table);

        _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _table.setEnabled(true);
    }

    public void getOldTable() {
        tableProducerMinCap = (DefaultTableModel) jTableProducerInputMin.getModel();
        tableProducerMaxCap = (DefaultTableModel) jTableProducerInputMin.getModel();
        tableProducerStartCost = (DefaultTableModel) jTableProducerInputMin.getModel();
        tableProducerSlopeCost = (DefaultTableModel) jTableProducerInputMin.getModel();

        int _rowNr = tableProducerMinCap.getRowCount();

        String _name;
        double _atBus;
        double[] minProducerCap = new double[DAILY_HOURS];
        double[] maxProducerCap = new double[DAILY_HOURS];
        double[] startCost = new double[DAILY_HOURS];
        double[] slopeCost = new double[DAILY_HOURS];

        for (int i = 0; i < _rowNr; i++) {
            _name = (String) tableProducerMinCap.getValueAt(i, NAME_COLUMN);
            _atBus = (double) tableProducerMinCap.getValueAt(i, BUS_COLUMN);
            for (int j = 0; j < DAILY_HOURS; j++) {
                minProducerCap[j] = (double) tableProducerMinCap.getValueAt(i, j + 2);
                maxProducerCap[j] = (double) tableProducerMaxCap.getValueAt(i, j + 2);
                startCost[j] = (double) tableProducerStartCost.getValueAt(i, j + 2);
                slopeCost[j] = (double) tableProducerSlopeCost.getValueAt(i, j + 2);
            }

            for (int j = 1; j < PRODUCER_LIST.size() + 2; j++) {
                existID = false;
                for (ProducerData PRODUCER1 : PRODUCER_LIST) {
                    if (PRODUCER1.getProducerID()== j) {
                        existID = true;
                        break;
                    }
                }
                if (!existID) {
                    producer_ID = j;
                    break;
                }
            }

            PRODUCER = new ProducerData(_name, producer_ID, _atBus, startCost, slopeCost, minProducerCap, maxProducerCap);

            PRODUCER_LIST.add(PRODUCER);
        }
    }

    public void updateTableInfo() {
        if (!PRODUCER_LIST.isEmpty()) {
            minCapacityMatriz = new Object[PRODUCER_LIST.size()][PRODUCER_COLUMN];
            maxCapacityMatriz = new Object[PRODUCER_LIST.size()][PRODUCER_COLUMN];
            StartCostMatriz = new Object[PRODUCER_LIST.size()][PRODUCER_COLUMN];
            SlopeCostMatriz = new Object[PRODUCER_LIST.size()][PRODUCER_COLUMN];
            int i = 0;
            for (ProducerData PRODUCER1 : PRODUCER_LIST) {
                minCapacityMatriz[i][NAME_COLUMN] = PRODUCER1.getName();
                minCapacityMatriz[i][BUS_COLUMN] = PRODUCER1.getAtBus();

                maxCapacityMatriz[i][NAME_COLUMN] = PRODUCER1.getName();
                maxCapacityMatriz[i][BUS_COLUMN] = PRODUCER1.getAtBus();

                StartCostMatriz[i][NAME_COLUMN] = PRODUCER1.getName();
                StartCostMatriz[i][BUS_COLUMN] = PRODUCER1.getAtBus();

                SlopeCostMatriz[i][NAME_COLUMN] = PRODUCER1.getName();
                SlopeCostMatriz[i][BUS_COLUMN] = PRODUCER1.getAtBus();

                for (int j = 0; j < DAILY_HOURS; j++) {
                    minCapacityMatriz[i][j + 2] = PRODUCER1.getMinPot(j);
                    maxCapacityMatriz[i][j + 2] = PRODUCER1.getMaxPot(j);
                    StartCostMatriz[i][j + 2] = PRODUCER1.getStartCost(j);
                    SlopeCostMatriz[i][j + 2] = PRODUCER1.getSlopeCost(j);
                }
                i++;
            }
            printTable(minCapacityMatriz, jTableProducerInputMin, tableProducerMinCap, columnNames_Min);
            printTable(maxCapacityMatriz, jTableProducerInputMax, tableProducerMaxCap, columnNames_Max);
            printTable(StartCostMatriz, jTableProducerInputStart, tableProducerStartCost, columnNames_Start);
            printTable(SlopeCostMatriz, jTableProducerInputSlope, tableProducerSlopeCost, columnNames_Slope);
        } else {
            printTable(null, jTableProducerInputMin, tableProducerMinCap, columnNames_Min);
            printTable(null, jTableProducerInputMax, tableProducerMaxCap, columnNames_Max);
            printTable(null, jTableProducerInputStart, tableProducerStartCost, columnNames_Start);
            printTable(null, jTableProducerInputSlope, tableProducerSlopeCost, columnNames_Slope);
        }
    }

    public void remove_PRODUCER_List() {
        int _index = jTableProducerInputMin.getSelectedRow();
        if (_index == -1) {
            _index = jTableProducerInputMax.getSelectedRow();
            if (_index == -1) {
                _index = jTableProducerInputStart.getSelectedRow();
                if (_index == -1) {
                    _index = jTableProducerInputSlope.getSelectedRow();
                    if (_index == -1) {
                        _index = PRODUCER_LIST.size() - 1;
                    }
                }
            }
        }

        try {
            ProducerData Aux = PRODUCER_LIST.remove(_index);

            jComboBoxProducerName.addItem(Aux.getName());

            updateTableInfo();
            initComboBox();

        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "Error! No Data",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void set_PRODUCER_List() {
        try {
            for (int j = 1; j < PRODUCER_LIST.size() + 2; j++) {
                    existID = false;
                    for (ProducerData PRODUCER1 : PRODUCER_LIST) {
                        if (PRODUCER1.getProducerID()== j) {
                            existID = true;
                            break;
                        }
                    }
                    if (!existID) {
                        producer_ID = j;
                        break;
                    }
                }

                PRODUCER = new ProducerData(nameData, producer_ID, busData, startCost_Producer, slopeCost_Producer, minCapacity_Producer, maxCapacity_Producer);

                PRODUCER_LIST.add(PRODUCER);

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
        return minCapacity_Producer;
    }

    public double[] getMaxDemand() {
        return maxCapacity_Producer;
    }

    public double[] getStartCost() {
        return startCost_Producer;
    }

    public double[] getSlopeCost() {
        return slopeCost_Producer;
    }

    public void setMinDemand(double[] _data) {
        minCapacity_Producer = _data;
    }

    public void setMaxDemand(double[] _data) {
        maxCapacity_Producer = _data;
    }

    public void setStartCost(double[] _data) {
        startCost_Producer = _data;
    }

    public void setSlopeCost(double[] _data) {
        slopeCost_Producer = _data;
    }
    
    public void openRiskAttitude() {
        frame_Dynamic_Demand = new ProducerInputData_Dynamic(this);
        frame_Dynamic_Demand.showWindow_ProducerOffer(jComboBoxProducerName.getSelectedItem().toString());
        //frame_Dynamic_Demand.setVisible(true);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel26 = new javax.swing.JLabel();
        jComboBoxProducerName = new javax.swing.JComboBox();
        jButtonRemoveProducer = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableProducerInputMin = new javax.swing.JTable();
        jButtonCancelInput = new javax.swing.JButton();
        jButtonNextInput = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableProducerInputMax = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProducerInputStart = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableProducerInputSlope = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel26.setText("Agent Name:");

        jComboBoxProducerName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxProducerName.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBoxProducerNamePopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        jComboBoxProducerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxProducerNameActionPerformed(evt);
            }
        });

        jButtonRemoveProducer.setText("Remove Generator");
        jButtonRemoveProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveProducerActionPerformed(evt);
            }
        });

        jTableProducerInputMin.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(jTableProducerInputMin);

        jButtonCancelInput.setText("Cancel");
        jButtonCancelInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelInputActionPerformed(evt);
            }
        });

        jButtonNextInput.setText("Submit");
        jButtonNextInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextInputActionPerformed(evt);
            }
        });

        jTableProducerInputMax.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableProducerInputMax);

        jTableProducerInputStart.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTableProducerInputStart);

        jTableProducerInputSlope.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTableProducerInputSlope);

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
                        .addComponent(jButtonNextInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancelInput))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxProducerName, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonRemoveProducer)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jComboBoxProducerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRemoveProducer))
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
                    .addComponent(jButtonNextInput))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelInputActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelInputActionPerformed

    private void jComboBoxProducerNamePopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBoxProducerNamePopupMenuWillBecomeInvisible
        if (jComboBoxProducerName.getSelectedIndex() != 0) {
            RiskAttitude riskAttitude = new RiskAttitude(Market,this, null, jComboBoxProducerName.getSelectedItem().toString(), 1);
            riskAttitude.setVisible(true);
            //frame_Dynamic_Demand = new ProducerInputData_Dynamic(this);
            //frame_Dynamic_Demand.showWindow_ProducerOffer(jComboBoxProducerName.getSelectedItem().toString());
            //frame_Dynamic_Demand.setVisible(true);
        }
    }//GEN-LAST:event_jComboBoxProducerNamePopupMenuWillBecomeInvisible

    private void jButtonRemoveProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveProducerActionPerformed
        remove_PRODUCER_List();
    }//GEN-LAST:event_jButtonRemoveProducerActionPerformed

    private void jButtonNextInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextInputActionPerformed
        if (PRODUCER_LIST.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Need to insert Producer's Data!\n",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        } else {
            market.setPRODUCER_List(PRODUCER_LIST);
            //market.activeFrame_SUPPLIER();
            //WholesaleMarket auxmarket = new WholesaleMarket(market);
            //auxmarket.startSimulation();
            this.dispose();
        }
    }//GEN-LAST:event_jButtonNextInputActionPerformed

    private void jComboBoxProducerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxProducerNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxProducerNameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancelInput;
    private javax.swing.JButton jButtonNextInput;
    private javax.swing.JButton jButtonRemoveProducer;
    private javax.swing.JComboBox jComboBoxProducerName;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTableProducerInputMax;
    private javax.swing.JTable jTableProducerInputMin;
    private javax.swing.JTable jTableProducerInputSlope;
    private javax.swing.JTable jTableProducerInputStart;
    // End of variables declaration//GEN-END:variables
}
