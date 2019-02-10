package wholesalemarket_LMP;

import wholesalemarket_LMP.simul.GridData;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import wholesalemarket_LMP.simul.WholesaleMarket;

public class BranchesInputParameters extends JFrame {

    private Wholesale_InputData market;
    private ArrayList<GridData> GRID_BRANCH_LIST;
    private GridData GRID_BRANCH;

    DefaultTableModel tableBranches = null;

    private Object[][] branchesMatriz;

    private int totalBusNr = -1;
    private int totalBranchNr = -1;
    private int branchID;

    private boolean existID;

    private final int NAME_COLUMN = 0;
    private final int START_COLUMN = 1;
    private final int END_COLUMN = 2;
    private final int MAXCAP_COLUMN = 3;
    private final int REACTANCE_COLUMN = 4;
    private final int TABLE_PARAMETERS = 5;
    private final String[] INIT_TEXTFIELD = {"1", "2", "100", "0.01"};
    private final String[] COLUMN_TITLES = {
        "Branch", "Start BUS", "End BUS", "Max. Cap.", "Reactance"
    };

    Pricing_Mechanism_Form backWindow;
    private static int excel_totalBranches;
    private static final int BRANCH_EXCEL_COLUMNS = 4;
    private double[][] defaultData;
    private int defaultIndex;

    public BranchesInputParameters(Wholesale_InputData _market) {
        market = _market;
        initComponents();
        defineWindow();
    }

    public BranchesInputParameters(Wholesale_InputData _market, Pricing_Mechanism_Form _backWindow) {
        market = _market;
        backWindow = _backWindow;
        initComponents();
        defineWindow();
    }

    private void defineWindow() {
        this.setTitle("Branch Parameters");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        printTable(null);
    }

    public void startBranchFrame() {
        totalBusNr = market.getTotalBus();
        totalBranchNr = market.getTotalBranch();
        GRID_BRANCH_LIST = new ArrayList<>();
        excel_totalBranches = GridGlobalParameters.get_Excel_totalBranches();

        getOldTable();
        initComboBox();
        initTextFields();

        if (excel_totalBranches > 0) {
            try {
                defaultData = ReadExcel.readExcelData(WholesaleMarket.Default_Case, "BRANCH_DATA", excel_totalBranches, BRANCH_EXCEL_COLUMNS, false);
            } catch (Exception ex) {
                jButtonDefaultData.setVisible(false);
                JOptionPane.showMessageDialog(this, "Default Data Unavailable",
                        "Default Data Upload", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            jButtonDefaultData.setVisible(false);
        }

    }

    public void getOldTable() {
        tableBranches = (DefaultTableModel) jTableBranchesInput.getModel();
        int rowNr = tableBranches.getRowCount();
        String name;
        double start;
        double end;
        double maxCap;
        double losses;

        for (int i = 0; i < rowNr; i++) {
            name = (String) tableBranches.getValueAt(i, NAME_COLUMN);
            start = (double) tableBranches.getValueAt(i, START_COLUMN);
            end = (double) tableBranches.getValueAt(i, END_COLUMN);
            maxCap = (double) tableBranches.getValueAt(i, MAXCAP_COLUMN);
            losses = (double) tableBranches.getValueAt(i, REACTANCE_COLUMN);

            for (int j = 1; j < GRID_BRANCH_LIST.size() + 2; j++) {
                existID = false;
                for (GridData GRID1 : GRID_BRANCH_LIST) {
                    if (GRID1.getBranchID() == j) {
                        existID = true;
                        break;
                    }
                }
                if (!existID) {
                    branchID = j;
                    break;
                }
            }

            GRID_BRANCH = new GridData(name, branchID, totalBranchNr, totalBusNr, start, end, maxCap, losses);

            GRID_BRANCH_LIST.add(GRID_BRANCH);
        }
    }

    public void initComboBox() {
        String controlName;
        Boolean verif;
        jComboBoxBranchName.removeAllItems();
        jComboBoxBranchName.addItem("Select Branch");

        if (GRID_BRANCH_LIST.isEmpty()) {
            for (int i = 0; i < totalBranchNr; i++) {
                jComboBoxBranchName.addItem("Branch" + (i + 1));
            }
        } else {
            for (int i = 0; i < totalBranchNr; i++) {
                verif = false;
                controlName = "Branch" + (i + 1);
                for (GridData BRANCH1 : GRID_BRANCH_LIST) {
                    if (BRANCH1.getName().equalsIgnoreCase(controlName)) {
                        verif = true;
                        break;
                    }
                }
                if (!verif) {
                    jComboBoxBranchName.addItem(controlName);
                }
            }
        }
        jComboBoxBranchName.setSelectedIndex(0);
    }

    public final void initTextFields() {
        jTextFieldFromBus.setText(INIT_TEXTFIELD[0]);
        jTextFieldToBus.setText(INIT_TEXTFIELD[1]);
        jTextFieldBranchCapact.setText(INIT_TEXTFIELD[2]);
        jTextFieldBranchLosses.setText(INIT_TEXTFIELD[3]);
    }

    public void set_BRANCH_List() {
        try {
            String name = jComboBoxBranchName.getSelectedItem().toString();
            int index = jComboBoxBranchName.getSelectedIndex();
            jComboBoxBranchName.removeItemAt(index);
            double startBus = Double.parseDouble(jTextFieldFromBus.getText());
            double endBus = Double.parseDouble(jTextFieldToBus.getText());
            double maxCap = Double.parseDouble(jTextFieldBranchCapact.getText());
            double losses = Double.parseDouble(jTextFieldBranchLosses.getText());

            for (int i = 1; i < GRID_BRANCH_LIST.size() + 2; i++) {
                existID = false;
                for (GridData GRID1 : GRID_BRANCH_LIST) {
                    if (GRID1.getBranchID() == i) {
                        existID = true;
                        break;
                    }
                }
                if (!existID) {
                    branchID = i;
                    break;
                }
            }

            GRID_BRANCH = new GridData(name, branchID, totalBranchNr, totalBusNr, startBus, endBus, maxCap, losses);

            GRID_BRANCH_LIST.add(GRID_BRANCH);

            updateTableInfo();
            initTextFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error! Input Data Incorrect",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateTableInfo() {
        if (!GRID_BRANCH_LIST.isEmpty()) {
            branchesMatriz = new Object[GRID_BRANCH_LIST.size()][TABLE_PARAMETERS];
            int i = 0;
            for (GridData BRANCH1 : GRID_BRANCH_LIST) {
                branchesMatriz[i][NAME_COLUMN] = BRANCH1.getName();
                branchesMatriz[i][START_COLUMN] = BRANCH1.getStartBus();
                branchesMatriz[i][END_COLUMN] = BRANCH1.getEndBus();
                branchesMatriz[i][MAXCAP_COLUMN] = BRANCH1.getMaxCapacity();
                branchesMatriz[i][REACTANCE_COLUMN] = BRANCH1.getLosses();
                i++;
            }
            printTable(branchesMatriz);
        } else {
            printTable(null);
        }

    }

    public void remove_BRANCH_List() {
        int _index = jTableBranchesInput.getSelectedRow();
        if (_index == -1) {
            _index = GRID_BRANCH_LIST.size() - 1;
        }
        try {
            GridData Aux = GRID_BRANCH_LIST.remove(_index);

            jComboBoxBranchName.addItem(Aux.getName());
            jTextFieldFromBus.setText("" + (int) Aux.getStartBus());
            jTextFieldToBus.setText("" + (int) Aux.getEndBus());
            jTextFieldBranchCapact.setText("" + (int) Aux.getMaxCapacity());
            jTextFieldBranchLosses.setText("" + Aux.getLosses());

            updateTableInfo();
            initComboBox();

        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "Error! No Data",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void update_Branch_List() {
        int _index = jTableBranchesInput.getSelectedRow();
        if (_index == -1) {
            _index = GRID_BRANCH_LIST.size() - 1;
        }
        try {
            GridData Aux = GRID_BRANCH_LIST.get(_index);

            String _name = Aux.getName();
            double _startBus = Aux.getStartBus();
            double _endBus = Aux.getEndBus();
            double _maxCap = Aux.getMaxCapacity();
            double _losses = Aux.getLosses();
            
            UpdateBranchParameters updateForm = new UpdateBranchParameters(this, _index, _name, _startBus, _endBus, _maxCap, _losses, GRID_BRANCH_LIST);
            updateForm.setVisible(true);
        } catch (IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(this, "Error! No Data",
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void printTable(Object[][] insertData) {
        DefaultTableModel table = new DefaultTableModel(insertData, COLUMN_TITLES);
        jTableBranchesInput.setModel(table);
        configBranchesTable();
    }

    private void configBranchesTable() {
        tableBranches = (DefaultTableModel) jTableBranchesInput.getModel();
        jTableBranchesInput.setAutoscrolls(true);
        jTableBranchesInput.setShowGrid(true);
        jTableBranchesInput.setEnabled(true);

        for (int i = 0; i < tableBranches.getColumnCount(); i++) {
            if (i == MAXCAP_COLUMN || i == REACTANCE_COLUMN) {
                jTableBranchesInput.getColumnModel().getColumn(i).setPreferredWidth(100);
            } else {
                jTableBranchesInput.getColumnModel().getColumn(i).setMaxWidth(70);
            }
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        jTableBranchesInput
                .setDefaultRenderer(Object.class, render);
    }

    public String verifyBranchTable() {
        String warning = "";
        try {
            double table_StartBus = Double.parseDouble(jTextFieldFromBus.getText());
            double table_EndBus = Double.parseDouble(jTextFieldToBus.getText());

            if (!GRID_BRANCH_LIST.isEmpty()) {
                for (GridData BRANCH1 : GRID_BRANCH_LIST) {
                    if (table_StartBus == BRANCH1.getStartBus()) {
                        if (table_EndBus == BRANCH1.getEndBus()) {
                            warning += "The branch between start bus" + jTextFieldFromBus.getText()
                                    + " and end bus " + jTextFieldToBus.getText() + " already exists!\n";
                        }
                    }
                    if (table_StartBus == BRANCH1.getEndBus()) {
                        if (table_EndBus == BRANCH1.getStartBus()) {
                            warning += "The reverse branch between start bus" + jTextFieldFromBus.getText()
                                    + " and end bus " + jTextFieldToBus.getText() + " already exists!\n";
                        }
                    }
                }
            }
        } catch (Exception ex) {
            warning += "Error! Input Data Incorrect!\n";
        }
        return warning;
    }

    public String verifyBranchTextField() {
        String warning = "";

        try {
            int nameIndex = jComboBoxBranchName.getSelectedIndex();
            int Aux_startBus = Integer.parseInt(jTextFieldFromBus.getText());
            int Aux_endBus = Integer.parseInt(jTextFieldToBus.getText());
            int Aux_capac = Integer.parseInt(jTextFieldBranchCapact.getText());
            double Aux_losses = Double.parseDouble(jTextFieldBranchLosses.getText());

            if (nameIndex != 0) {
                if (Aux_startBus > totalBusNr || Aux_startBus <= 0) {
                    warning += "Start BUS has to be a number between 1 and " + Integer.toString(totalBusNr) + "!\n";
                }
                if (Aux_endBus > totalBusNr || Aux_endBus <= 0) {
                    warning += "End BUS has to be a number between 1 and " + Integer.toString(totalBusNr) + "!\n";
                }
                if (Aux_startBus == Aux_endBus) {
                    warning += "Start and end buses should be different!\n";
                }
                if (Aux_capac <= 0) {
                    warning += "Máx Capacity has to be bigger than 0 MW!\n";
                }
                if (Aux_losses <= 0) {
                    warning += "Losses has to be bigger than 0.0 Ohms!\n";
                }
            } else {
                warning += "Should select a Branch! \n";
            }

        } catch (Exception ex) {
            warning += "All input data must be integer values although the losses field must be a double value!\n";
        }
        return warning;
    }

    public int getTotalBus() {
        return totalBusNr;
    }

    public int getTotalBranch() {
        return totalBranchNr;
    }

    public ArrayList<GridData> getGRID_BRANCH_LIST() {
        return GRID_BRANCH_LIST;
    }

    public void setGRID_BRANCH_LIST(ArrayList<GridData> GRID_BRANCH_LIST) {
        this.GRID_BRANCH_LIST = GRID_BRANCH_LIST;
    }

    public void updateGRID_BRANCH_LIST(int _index, double _startBus,
            double _endBus, double _maxCap, double _losses) {
        this.GRID_BRANCH_LIST.get(_index).setStartBus(_startBus);
        this.GRID_BRANCH_LIST.get(_index).setEndBus(_endBus);
        this.GRID_BRANCH_LIST.get(_index).setMaxCapacity(_maxCap);
        this.GRID_BRANCH_LIST.get(_index).setLosses(_losses);

        updateTableInfo();
        initComboBox();
    }

    public String verifyGlobalInfo() {
        String warning = "";
        if (GRID_BRANCH_LIST.isEmpty()) {
            warning += "Need to insert Branch Data!\n";
        } else if (totalBranchNr != GRID_BRANCH_LIST.size()) {
            warning += "Number of branches is not equal to " + Integer.toString(totalBranchNr) + "!\n";
        }
        return warning;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonCancelInput = new javax.swing.JButton();
        jButtonBackInput = new javax.swing.JButton();
        jButtonNextInput = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldToBus = new javax.swing.JTextField();
        jTextFieldFromBus = new javax.swing.JTextField();
        jTextFieldBranchLosses = new javax.swing.JTextField();
        jTextFieldBranchCapact = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxBranchName = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButtonAddBranches = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableBranchesInput = new javax.swing.JTable();
        jButtonRemoveBranches = new javax.swing.JButton();
        jButtonDefaultData = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonCancelInput.setText("Cancel");
        jButtonCancelInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelInputActionPerformed(evt);
            }
        });

        jButtonBackInput.setText("Back");
        jButtonBackInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackInputActionPerformed(evt);
            }
        });

        jButtonNextInput.setText("Save");
        jButtonNextInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextInputActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Branch Data", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        jLabel6.setText("MW");

        jLabel7.setText("Ohm");

        jLabel5.setText("Branch Losses:");

        jLabel2.setText("From BUS:");

        jLabel1.setText("Name:");

        jComboBoxBranchName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Branch Max. Capacity:");

        jLabel3.setText("To BUS:");

        jButtonAddBranches.setText("Add");
        jButtonAddBranches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddBranchesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldBranchLosses, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldToBus, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldFromBus, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAddBranches, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxBranchName, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextFieldBranchCapact, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxBranchName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldFromBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldToBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldBranchCapact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldBranchLosses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jButtonAddBranches, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        jTableBranchesInput.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTableBranchesInput);

        jButtonRemoveBranches.setText("Remove");
        jButtonRemoveBranches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveBranchesActionPerformed(evt);
            }
        });

        jButtonDefaultData.setText("Update");
        jButtonDefaultData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDefaultDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonRemoveBranches, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDefaultData, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonDefaultData, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonRemoveBranches, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(jButtonBackInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNextInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancelInput, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelInput)
                    .addComponent(jButtonNextInput)
                    .addComponent(jButtonBackInput))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddBranchesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddBranchesActionPerformed
        String warningTable = verifyBranchTable();
        String warningTextField = verifyBranchTextField();

        if (!warningTextField.isEmpty()) {
            JOptionPane.showMessageDialog(this, warningTextField,
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        } else {
            if (!warningTable.isEmpty()) {
                JOptionPane.showMessageDialog(this, warningTable,
                        "Verify Input Data", JOptionPane.ERROR_MESSAGE);
            } else {
                if (jComboBoxBranchName.getSelectedIndex() != 0) {
                    set_BRANCH_List();
                    jComboBoxBranchName.setSelectedIndex(0);
                    initTextFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Should select a Branch",
                            "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jButtonAddBranchesActionPerformed

    private void jButtonRemoveBranchesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveBranchesActionPerformed
        remove_BRANCH_List();
    }//GEN-LAST:event_jButtonRemoveBranchesActionPerformed

    private void jButtonCancelInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelInputActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelInputActionPerformed

    private void jButtonBackInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackInputActionPerformed

        backWindow.activeFrame_GRID(false);
        this.dispose();
        //market.activeFrame_GRID(Wholesale_InputData.editHour);
    }//GEN-LAST:event_jButtonBackInputActionPerformed

    private void jButtonNextInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextInputActionPerformed

        String warning = verifyGlobalInfo();

        if (warning.isEmpty()) {
            market.setGRID_List(GRID_BRANCH_LIST);
            backWindow.setMarketType(true, true);
            this.setVisible(false);
            //market.activeFrame_PRODUCER();
        } else {
            JOptionPane.showMessageDialog(this, warning,
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButtonNextInputActionPerformed

    private void jButtonDefaultDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDefaultDataActionPerformed
        // TODO add your handling code here:
        update_Branch_List();
        /*
         if (defaultIndex == excel_totalBranches) {
         defaultIndex = 0;
         }
         jComboBoxBranchName.setSelectedIndex(0);
         jTextFieldFromBus.setText("" + (int) defaultData[defaultIndex][0]);
         jTextFieldToBus.setText("" + (int) defaultData[defaultIndex][1]);
         jTextFieldBranchCapact.setText("" + (int) defaultData[defaultIndex][2]);
         jTextFieldBranchLosses.setText("" + defaultData[defaultIndex][3]);

         defaultIndex++;*/
    }//GEN-LAST:event_jButtonDefaultDataActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddBranches;
    private javax.swing.JButton jButtonBackInput;
    private javax.swing.JButton jButtonCancelInput;
    private javax.swing.JButton jButtonDefaultData;
    private javax.swing.JButton jButtonNextInput;
    private javax.swing.JButton jButtonRemoveBranches;
    private javax.swing.JComboBox jComboBoxBranchName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableBranchesInput;
    private javax.swing.JTextField jTextFieldBranchCapact;
    private javax.swing.JTextField jTextFieldBranchLosses;
    private javax.swing.JTextField jTextFieldFromBus;
    private javax.swing.JTextField jTextFieldToBus;
    // End of variables declaration//GEN-END:variables
}
