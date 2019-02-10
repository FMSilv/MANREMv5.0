package wholesalemarket_LMP.results;

import wholesalemarket_LMP.simul.GridData;
import wholesalemarket_LMP.simul.WholesaleMarket;
import wholesalemarket_LMP.simul.SupplierData;
import wholesalemarket_LMP.simul.ProducerData;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class WholesaleShowResults extends javax.swing.JFrame {

    private WholesaleMarket mainMarket;

    private ArrayList<GridData> grid_list;
    private ArrayList<ProducerData> producer_list;
    private ArrayList<SupplierData> supplier_list;

    private ArrayList<double[][]> commitment_list;
    private ArrayList<double[][]> LMP_list;
    private ArrayList<double[][]> priceSensitiveDemand;
    private ArrayList<double[][]> branchPowerFlow_list;

    private int totalBranches;
    private int totalBuses;
    private int totalProducers;
    private int totalSuppliers;

    private boolean isInputData;

    DefaultTableModel tableInput = null;
    DefaultTableModel tableOutput = null;

    private int indexHour;
    
    // Print Output
    private final int TABLE_COMMITMENT = 1;
    private final int TABLE_LMP = 2;
    private final int TABLE_PSDEMAND = 3;
    private final int TABLE_PROFITS = 5;

    private final int SUPPLY_DEMAND_PRODUCER = 0;
    private final int SUPPLY_DEMAND_SUPPLIER = 1;

    private final int BRANCH_DATA_SIZE = 6;
    private final int SUPPLIER_DATA_SIZE = 8;
    private final int HOUR_PER_DAY = WholesaleMarket.HOUR_PER_DAY;
    private final int START_HOUR = WholesaleMarket.START_HOUR;

    private Object[] comboBoxHour;
    private Object[][] busData;
    private Object[][] branchData;
    private Object[][] busLMPData_Day;
    private Object[][][] busLMPData_Hour;
    private Object[][][] producerData_Hour;
    private Object[][] producerData_Day;
    private Object[][][] producerCommitmentData_Hour;
    private Object[][] producerCommitmentData_Day;
    private Object[][][] producerProfitData_Hour;
    private Object[][] producerProfitData_Day;
    private Object[][][] supplierData_Hour;
    private Object[][] supplierData_Day;
    private Object[][][] supplierPriceSensitiveData_Hour;
    private Object[][] supplierPriceSensitiveData_Day;
    private double[] powerPerHour;
    private double[] pricePerHour;
    private double[][] producerProfits;
    private double[][] branchPowerFlow_abs;
    private Object[][] marketPrice;
    private Object[][][] branchPowerFlowData_Hour;
    private Object[][] branchPowerFlowData_Day;

    private final String[] outputComboBox = {"Select market results", "Generation Commitments", "Generator revenues", "LMP per bus", "Retailer results", "Market price", "Branches power flow"};
    private final String[] busLMP_Title = {"Name", "Hour", "LMP(€/MWh)"};
    private final String[] producerDataTitle = {"Name", "ID", "atBus", "Hour", "Start Price(€/MWh)", "Slope Price", "Min. Gen. Power(MW)", "Max. Gen. Power(MW)"};
    private final String[] producerCommitmentTitle = {"Name", "Hour", "Power(MW)", "Min. Gen. Power(MW)", "Max. Gen. Power(MW)"};
    private final String[] producerProfitTitle = {"Name", "Hour", "Power Commitment[MW]", "Marginal Cost[€/MWh]", "Sales Price[€/MWh]", "Revenue[€/h]", "Profit[€/h]"};
    private final String[] supplierPriceSensitiveDataTitle = {"Name", "Hour", "Fixed Load(MW)", "Price-Sensitive Demand(MW)", "Total Load Demand [MW]"};
    private final String[] marketPriceDataTitle = {"Hour", "Power(Mw)", "Price(€/MWh)"};
    private final String[] branchPowerFlowTitle = {"Name", "Start Bus", "End Bus", "Reactance[ohms]", "Max. Capacity[MW]", "Power Flow[MW]"};

    public WholesaleShowResults(WholesaleMarket _mainMarket) {
        initComponents();
        mainMarket = _mainMarket;
        grid_list = mainMarket.getGridData();
        totalBranches = grid_list.size();
        totalBuses = (int) grid_list.get(0).getBusNr();
        producer_list = mainMarket.getProducersData();
        totalProducers = producer_list.size();

        supplier_list = mainMarket.getSupplierData();
        totalSuppliers = supplier_list.size();

        createProducerDataMatrix();
        createBranchDataMatrix();
        createSupplierDataMatrix();
        createBranchPowerFlowMatrix();
        createProducerCommitmentDataMatrix();
        createBranchLMPMatrix();
        creatPriceSensitiveDemandMatrix();

        defineWindow();
        updateOutputNameList(0);

        initComboBox();
    }

    /**
     * ComboBox initialization
     */
    private void initComboBox() {
        comboBoxHour = new Object[HOUR_PER_DAY + 1];
        int indexHour = START_HOUR;
        for (int h = 0; h < HOUR_PER_DAY + 1; h++) {
            if (h == 0) {
                comboBoxHour[h] = "All Day";
            } else {
                comboBoxHour[h] = indexHour;
                indexHour++;
            }
        }

        // ComboBox for the output data
        jComboBox_OutputType.setModel(new DefaultComboBoxModel(outputComboBox));
        jComboBoxOutputHour.setModel(new DefaultComboBoxModel(comboBoxHour));
        jComboBox_OutputType.setSelectedIndex(0);
        jComboBoxOutputHour.setSelectedIndex(0);
    }

    private void defineWindow() {
        this.setTitle("Wholesale Simulation Results");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(false);
        this.setLocationRelativeTo(null);
        tableOutput = (DefaultTableModel) jTable_OutputData.getModel();
        jTable_OutputData.setAutoscrolls(true);
        jTable_OutputData.setShowGrid(true);
        jTable_OutputData.setEnabled(false);
        
        int hour = jComboBoxOutputHour.getSelectedIndex();
        jTabbedPaneOutput.add("Graphical [Output Chart Information]",
                        ChartGenerator.drawLineGraph_SupplyVsDemand(producer_SupplyCurve(hour), supplier_DemandCurve(hour), "Supply and Demand [h =" + hour + "]", "Power [MW]", "Price per hour [€/MWh]"));

                jTabbedPaneOutput.setSelectedIndex(1);
    }

    private void createBranchDataMatrix() {
        branchData = new Object[totalBranches][BRANCH_DATA_SIZE];
        busData = new Object[totalBranches][2];
        for (int i = 0; i < grid_list.size(); i++) {
            branchData[i][0] = grid_list.get(i).getName();
            branchData[i][1] = (int) grid_list.get(i).getBranchID();
            branchData[i][2] = (int) grid_list.get(i).getStartBus();
            branchData[i][3] = (int) grid_list.get(i).getEndBus();
            busData[i][0] = (int) grid_list.get(i).getStartBus();
            busData[i][1] = (int) grid_list.get(i).getEndBus();
            branchData[i][4] = grid_list.get(i).getMaxCapacity();
            branchData[i][5] = grid_list.get(i).getLosses();
        }

    }

    private void createBranchLMPMatrix() {
        LMP_list = mainMarket.getLMPWithTrueCost(); //Size: 1
        double[][] lmp_Matrix = LMP_list.get(0); // [24h][total Bus]

        int columnSize = busLMP_Title.length;
        busLMPData_Hour = new Object[HOUR_PER_DAY][totalBuses][columnSize]; //[24h][total Bus][Name;Hour;LMP]
        busLMPData_Day = new Object[HOUR_PER_DAY * totalBuses][columnSize]; //[24h * total Bus][Name;Hour;LMP]
        int index;
        int nameIndex;
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < totalBuses; i++) {
                nameIndex = i + 1;
                indexHour = h + START_HOUR;
                busLMPData_Hour[h][i][0] = "Bus" + nameIndex;
                busLMPData_Hour[h][i][1] = String.format(indexHour + ".00");
                busLMPData_Hour[h][i][2] = String.format("%.2f", lmp_Matrix[h][i]);
            }
        }
        for (int i = 0; i < HOUR_PER_DAY; i++) {
            for (int j = 0; j < totalBuses; j++) {
                index = i * totalBuses;
                nameIndex = j + 1;
                indexHour = i + START_HOUR;
                busLMPData_Day[index + j][0] = "Bus" + nameIndex;
                busLMPData_Day[index + j][1] = String.format(indexHour + ".00");
                busLMPData_Day[index + j][2] = String.format("%.2f", lmp_Matrix[i][j]);
            }
        }
    }

    private void createProducerDataMatrix() {
        producerData_Hour = new Object[HOUR_PER_DAY][totalProducers][producerDataTitle.length];
        producerData_Day = new Object[HOUR_PER_DAY * totalProducers][producerDataTitle.length];
        int index;

        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < producer_list.size(); i++) {
                indexHour = h + START_HOUR;
                producerData_Hour[h][i][0] = producer_list.get(i).getName();
                producerData_Hour[h][i][1] = (int) producer_list.get(i).getProducerID();
                producerData_Hour[h][i][2] = (int) producer_list.get(i).getAtBus();
                producerData_Hour[h][i][3] = String.format(indexHour + ".00");
                producerData_Hour[h][i][4] = producer_list.get(i).getStartCost(h);
                producerData_Hour[h][i][5] = producer_list.get(i).getSlopeCost(h);
                producerData_Hour[h][i][6] = producer_list.get(i).getMinPot(h);
                producerData_Hour[h][i][7] = producer_list.get(i).getMaxPot(h);
            }
        }
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int j = 0; j < totalProducers; j++) {
                index = h * producer_list.size();
                indexHour = h + START_HOUR;
                producerData_Day[index + j][0] = producer_list.get(j).getName();
                producerData_Day[index + j][1] = (int) producer_list.get(j).getProducerID();
                producerData_Day[index + j][2] = (int) producer_list.get(j).getAtBus();
                producerData_Day[index + j][3] = String.format(indexHour + ".00");
                producerData_Day[index + j][4] = producer_list.get(j).getStartCost(h);
                producerData_Day[index + j][5] = producer_list.get(j).getSlopeCost(h);
                producerData_Day[index + j][6] = producer_list.get(j).getMinPot(h);
                producerData_Day[index + j][7] = producer_list.get(j).getMaxPot(h);
            }
        }
    }

    private void createProducerCommitmentDataMatrix() {
        commitment_list = mainMarket.getProducerAgent_CommitmentWithTrueCost();
        double[][] commitmentMatrix = commitment_list.get(0); // [24h][total Producers]
        producerCommitmentData_Hour = new Object[HOUR_PER_DAY][totalProducers][producerCommitmentTitle.length];
        producerCommitmentData_Day = new Object[HOUR_PER_DAY * totalProducers][producerCommitmentTitle.length];
        producerProfitData_Hour = new Object[HOUR_PER_DAY][totalProducers][producerProfitTitle.length];
        producerProfitData_Day = new Object[HOUR_PER_DAY * totalProducers][producerProfitTitle.length];

        powerPerHour = new double[HOUR_PER_DAY];
        pricePerHour = new double[HOUR_PER_DAY];
        double[][] demandPercentage = new double[HOUR_PER_DAY][commitmentMatrix[0].length];
        double[][] pricePerProducer = new double[HOUR_PER_DAY][commitmentMatrix[0].length];
        marketPrice = new Object[HOUR_PER_DAY][marketPriceDataTitle.length];

        int index;
        int index_AgentBus;
        double startCost;
        double slopeCost;
        double powerCommitment;
        double marginalCost;
        double priceCommitment;
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < commitmentMatrix[0].length; i++) {
                indexHour = h + START_HOUR;
                powerCommitment = Math.abs(commitmentMatrix[h][i]);
                producerCommitmentData_Hour[h][i][0] = producer_list.get(i).getName();
                producerCommitmentData_Hour[h][i][1] = String.format(indexHour + ".00");
                producerCommitmentData_Hour[h][i][2] = String.format("%.2f", powerCommitment);
                producerCommitmentData_Hour[h][i][3] = producer_list.get(i).getMinPot(h);
                producerCommitmentData_Hour[h][i][4] = producer_list.get(i).getMaxPot(h);

                //producerProfit = {"Name", "Hour", "Power Commitment[MW]", "Marginal Cost[€/MWh]", "Sales Price[€/MWh]", "Gross Revenue[€]", "Profit[€]"};
                startCost = producer_list.get(i).getStartCost(h);
                slopeCost = producer_list.get(i).getSlopeCost(h);
                index_AgentBus = (int) producer_list.get(i).getAtBus() - 1;
                marginalCost = startCost + powerCommitment * slopeCost;
                priceCommitment = mainMarket.getLMPWithTrueCost().get(0)[h][index_AgentBus];
                producerProfitData_Hour[h][i][0] = producer_list.get(i).getName();
                producerProfitData_Hour[h][i][1] = String.format(indexHour + ".00");
                producerProfitData_Hour[h][i][2] = String.format("%.2f", powerCommitment);
                producerProfitData_Hour[h][i][3] = String.format("%.2f", marginalCost);
                producerProfitData_Hour[h][i][4] = String.format("%.2f", priceCommitment);
                producerProfitData_Hour[h][i][5] = String.format("%.2f", priceCommitment * powerCommitment);
                producerProfitData_Hour[h][i][6] = String.format("%.2f", (priceCommitment - marginalCost) * powerCommitment);
            }
            for (int i = 0; i < commitmentMatrix[0].length; i++) {
                powerPerHour[h] += commitmentMatrix[h][i];
            }
            for (int i = 0; i < commitmentMatrix[0].length; i++) {
                demandPercentage[h][i] = commitmentMatrix[h][i] / powerPerHour[h];
                pricePerProducer[h][i] = commitmentMatrix[h][i] * producer_list.get(i).getSlopeCost(h) + producer_list.get(i).getStartCost(h);
            }
            for (int i = 0; i < commitmentMatrix[0].length; i++) {
                pricePerHour[h] += demandPercentage[h][i] * pricePerProducer[h][i];
            }
            indexHour = h + START_HOUR;
            marketPrice[h][0] = String.format(indexHour + ".00");
            marketPrice[h][1] = String.format("%.2f", powerPerHour[h]);
            marketPrice[h][2] = String.format("%.2f", pricePerHour[h]);

        }
        for (int i = 0; i < HOUR_PER_DAY; i++) {
            for (int j = 0; j < totalProducers; j++) {
                index = i * totalProducers;
                indexHour = i + START_HOUR;
                powerCommitment = Math.abs(commitmentMatrix[i][j]);
                producerCommitmentData_Day[index + j][0] = producer_list.get(j).getName();
                producerCommitmentData_Day[index + j][1] = String.format(indexHour + ".00");
                producerCommitmentData_Day[index + j][2] = String.format("%.2f", powerCommitment);
                producerCommitmentData_Day[index + j][3] = producer_list.get(j).getMinPot(i);
                producerCommitmentData_Day[index + j][4] = producer_list.get(j).getMaxPot(i);

                //producerProfit = {"Name", "Hour", "Power Commitment[MW]", "Marginal Cost[€/MWh]", "Sales Price[€/MWh]", "Gross Revenue[€]", "Profit[€]"};
                startCost = producer_list.get(j).getStartCost(i);
                slopeCost = producer_list.get(j).getSlopeCost(i);
                index_AgentBus = (int) producer_list.get(j).getAtBus() - 1;
                marginalCost = startCost + powerCommitment * slopeCost;
                priceCommitment = mainMarket.getLMPWithTrueCost().get(0)[i][index_AgentBus];
                producerProfitData_Day[index + j][0] = producer_list.get(j).getName();
                producerProfitData_Day[index + j][1] = String.format(indexHour + ".00");
                producerProfitData_Day[index + j][2] = String.format("%.2f", powerCommitment);
                producerProfitData_Day[index + j][3] = String.format("%.2f", marginalCost);
                producerProfitData_Day[index + j][4] = String.format("%.2f", priceCommitment);
                producerProfitData_Day[index + j][5] = String.format("%.2f", priceCommitment * powerCommitment);
                producerProfitData_Day[index + j][6] = String.format("%.2f", (priceCommitment - marginalCost) * powerCommitment);
            }
        }
    }

    private void createBranchPowerFlowMatrix() {

        branchPowerFlow_list = mainMarket.getGridBranchPowerFlow();

        double[][] powerFlowMatrix = branchPowerFlow_list.get(0);

        int index;
        // branchPowerFlowTitle = {"Name", "Start Bus", "End Bus", "Reactance[ohms]", "Máx Capacity[MW]", "Power Flow[MW]"};
        branchPowerFlowData_Hour = new Object[HOUR_PER_DAY][totalBranches][branchPowerFlowTitle.length];
        branchPowerFlowData_Day = new Object[HOUR_PER_DAY * totalBranches][branchPowerFlowTitle.length];
        branchPowerFlow_abs = new double[HOUR_PER_DAY][totalBranches];

        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < totalBranches; i++) {
                branchPowerFlow_abs[h][i] = Math.abs(powerFlowMatrix[h][i]);
                branchPowerFlowData_Hour[h][i][0] = grid_list.get(i).getName();
                branchPowerFlowData_Hour[h][i][1] = grid_list.get(i).getStartBus();
                branchPowerFlowData_Hour[h][i][2] = grid_list.get(i).getEndBus();
                branchPowerFlowData_Hour[h][i][3] = grid_list.get(i).getLosses();
                branchPowerFlowData_Hour[h][i][4] = grid_list.get(i).getMaxCapacity();
                branchPowerFlowData_Hour[h][i][5] = String.format("%.3f", powerFlowMatrix[h][i]);
            }
        }
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int j = 0; j < totalBranches; j++) {
                index = h * totalBranches;
                branchPowerFlowData_Day[index + j][0] = grid_list.get(j).getName();
                branchPowerFlowData_Day[index + j][1] = grid_list.get(j).getStartBus();
                branchPowerFlowData_Day[index + j][2] = grid_list.get(j).getEndBus();
                branchPowerFlowData_Day[index + j][3] = grid_list.get(j).getLosses();
                branchPowerFlowData_Day[index + j][4] = grid_list.get(j).getMaxCapacity();
                branchPowerFlowData_Day[index + j][5] = String.format("%.3f", powerFlowMatrix[h][j]);
            }
        }
    }

    private void createSupplierDataMatrix() {

        supplierData_Hour = new Object[HOUR_PER_DAY][totalSuppliers][SUPPLIER_DATA_SIZE];
        supplierData_Day = new Object[HOUR_PER_DAY * totalSuppliers][SUPPLIER_DATA_SIZE];
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < totalSuppliers; i++) {
                indexHour = h + START_HOUR;
                supplierData_Hour[h][i][0] = supplier_list.get(i).getName();
                supplierData_Hour[h][i][1] = (int) supplier_list.get(i).getSupplierID();
                supplierData_Hour[h][i][2] = (int) supplier_list.get(i).getAtBus();
                supplierData_Hour[h][i][3] = String.format(indexHour + ".00");
                supplierData_Hour[h][i][4] = supplier_list.get(i).getStartCost(h);
                supplierData_Hour[h][i][5] = supplier_list.get(i).getSlopeCost(h);
                supplierData_Hour[h][i][6] = supplier_list.get(i).getLoadFixedDemand(h);
                supplierData_Hour[h][i][7] = supplier_list.get(i).getMaxDemand(h);
            }
        }
        int index;
        for (int i = 0; i < HOUR_PER_DAY; i++) {
            for (int j = 0; j < totalSuppliers; j++) {
                index = i * totalSuppliers;
                indexHour = i + START_HOUR;
                supplierData_Day[index + j][0] = supplier_list.get(j).getName();
                supplierData_Day[index + j][1] = (int) supplier_list.get(j).getSupplierID();
                supplierData_Day[index + j][2] = (int) supplier_list.get(j).getAtBus();
                supplierData_Day[index + j][3] = String.format(indexHour + ".00");
                supplierData_Day[index + j][4] = supplier_list.get(j).getStartCost(i);
                supplierData_Day[index + j][5] = supplier_list.get(j).getSlopeCost(i);
                supplierData_Day[index + j][6] = supplier_list.get(j).getLoadFixedDemand(i);
                supplierData_Day[index + j][7] = supplier_list.get(j).getMaxDemand(i);
            }

        }
    }

    private void creatPriceSensitiveDemandMatrix() {
        priceSensitiveDemand = mainMarket.getSupplierAgent_PriceSensitiveDemandWithTrueCost();
        int columnSize = supplierPriceSensitiveDataTitle.length;
        double[][] priceSensitiveMatrix = priceSensitiveDemand.get(0); // [24h][total Suppliers]
        supplierPriceSensitiveData_Hour = new Object[HOUR_PER_DAY][totalSuppliers][columnSize];
        supplierPriceSensitiveData_Day = new Object[HOUR_PER_DAY * totalSuppliers][columnSize];
        int index;
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < priceSensitiveMatrix[0].length; i++) {
                indexHour = h + START_HOUR;
                supplierPriceSensitiveData_Hour[h][i][0] = supplier_list.get(i).getName();
                supplierPriceSensitiveData_Hour[h][i][1] = String.format(indexHour + ".00");
                supplierPriceSensitiveData_Hour[h][i][2] = supplier_list.get(i).getLoadFixedDemand(h);
                supplierPriceSensitiveData_Hour[h][i][3] = String.format("%.3f", Math.abs(priceSensitiveMatrix[h][i]));
                supplierPriceSensitiveData_Hour[h][i][4] = String.format("%.3f", supplier_list.get(i).getLoadFixedDemand(h) + Math.abs(priceSensitiveMatrix[h][i]));
            }
        }
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < totalSuppliers; i++) {
                index = h * totalSuppliers;
                indexHour = h + START_HOUR;
                supplierPriceSensitiveData_Day[index + i][0] = supplier_list.get(i).getName();
                supplierPriceSensitiveData_Day[index + i][1] = String.format(indexHour + ".00");
                supplierPriceSensitiveData_Day[index + i][2] = supplier_list.get(i).getLoadFixedDemand(h);
                supplierPriceSensitiveData_Day[index + i][3] = String.format("%.3f", Math.abs(priceSensitiveMatrix[h][i]));
                supplierPriceSensitiveData_Day[index + i][4] = String.format("%.3f", supplier_list.get(i).getLoadFixedDemand(h) + Math.abs(priceSensitiveMatrix[h][i]));
            }
        }

    }

    public void printOutputTable(Object[][] insertData, String[] columnSize) {
        DefaultTableModel table = new DefaultTableModel(insertData, columnSize);
        jTable_OutputData.setModel(table);
        configOutputTable(columnSize.length);
    }

    private void configOutputTable(int _size) {
        for (int i = 0; i < _size; i++) {
            if (i == 0) {
                jTable_OutputData.getColumnModel().getColumn(i).setMinWidth(100);
            } else {
                jTable_OutputData.getColumnModel().getColumn(i).setMinWidth(70);
            }
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        jTable_OutputData.setDefaultRenderer(Object.class, render);
    }

    private void updateOutputNameList(int index) {
        DefaultListModel listModel = new DefaultListModel();
        listModel.removeAllElements();
        listModel.addElement("All Data");
        jLabel2.setVisible(true);
        jComboBoxOutputHour.setVisible(true);

        switch (index) {
            case 0:
                printOutputTable(null, marketPriceDataTitle);
                break;
            case 1: // Info is equal for the case 2
            case 2:
                for (Object[] producerData_Hour1 : producerData_Hour[0]) {
                    listModel.addElement(producerData_Hour1[0].toString());
                }
                printOutputTable(null, producerCommitmentTitle);
                break;
            case 3:
                for (int i = 1; i < totalBuses + 1; i++) {
                    listModel.addElement("Bus" + i);
                }
                printOutputTable(null, busLMP_Title);
                break;
            case 4:
                for (int i = 0; i < totalSuppliers; i++) {
                    listModel.addElement(supplierData_Hour[0][i][0].toString());
                }
                printOutputTable(null, supplierPriceSensitiveDataTitle);
                break;
            case 6:
                for (Object[] branchPowerFlowData1 : branchPowerFlowData_Hour[0]) {
                    listModel.addElement(branchPowerFlowData1[0].toString());
                }
                printOutputTable(null, branchPowerFlowTitle);
                break;
            default:
                printOutputTable(null, marketPriceDataTitle);
                break;
        }
        jList_DataAgentOutput.setModel(listModel);
        jList_DataAgentOutput.setSelectedIndex(0);
    }

    private void printDetailedInfo(int[] selectIndex, int hour, int tableType, String[] tableTitle, Object[][] tableData2D, Object[][][] tableData3D, boolean isInputData) {
        int TotalselectData = selectIndex.length;
        Object[][] selectMatrix;
        int columnSize = tableTitle.length;
        jTabbedPaneOutput.setSelectedIndex(0);
        
        if (TotalselectData < 1 || selectIndex[0] == 0) {
            if (hour == -1) {
                printOutputTable(tableData2D, tableTitle);
            } else {
                printOutputTable(tableData3D[hour], tableTitle);
            }

        } else {
            if (hour == -1) {
                int index = 0;
                selectMatrix = new Object[HOUR_PER_DAY * TotalselectData][columnSize];

                while (index < HOUR_PER_DAY * TotalselectData - 1) {
                    for (int h = 0; h < HOUR_PER_DAY; h++) {
                        for (int j = 0; j < TotalselectData; j++) {
                            for (int i = 0; i < columnSize; i++) {
                                selectMatrix[index][i] = tableData3D[h][selectIndex[j] - 1][i];
                            }
                            index++;
                        }
                    }
                }
            } else {
                selectMatrix = new Object[TotalselectData][columnSize];
                for (int i = 0; i < TotalselectData; i++) {
                    for (int j = 0; j < columnSize; j++) {
                        selectMatrix[i][j] = tableData3D[hour][selectIndex[i] - 1][j];
                    }
                }
            }
            printOutputTable(selectMatrix, tableTitle);
        }
    }

    private void createLineChartOutput(JTabbedPane _tabPane, Object[][] _agentData, double[][] _outputData, int[] _selectIndex, String _title, String _axisX, String _axisY) {
        String[] legend;
        double[][] showData;

        if (_tabPane.getTabCount() != 1) {
            _tabPane.remove(1);
        }

        if (_selectIndex[0] == 0) {
            int totalAgents = _outputData[0].length; // Número total de agentes com resultados gerados

            legend = new String[totalAgents]; // Número de legendas = número total de agentes

            for (int i = 0; i < totalAgents; i++) {
                legend[i] = _agentData[i][0].toString();
            }
            _tabPane.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend, _outputData, _title, _axisX, _axisY));
        } else {
            int totalAgents = _selectIndex.length; // Número total de agentes seleccionados
            legend = new String[totalAgents]; // Número de legendas = número de agentes
            showData = new double[_outputData.length][totalAgents];

            for (int i = 0; i < totalAgents; i++) {
                legend[i] = _agentData[_selectIndex[i] - 1][0].toString(); // Cria legenda com os nomes dos agentes seleccionados
                for (int j = 0; j < _outputData.length; j++) {
                    showData[j][i] = _outputData[j][_selectIndex[i] - 1];
                }
            }
            _tabPane.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend, showData, _title, _axisX, _axisY));
        }
        _tabPane.setSelectedIndex(1);
    }

    private void createLineChartOutput_Supplier(int[] selectIndex, String title, String axisX, String axisY) {
        String legend;
        int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
        if (selectIndex[0] != 0 && selectIndex.length == 1 && hour != -1) {
            double maxDemand = Double.parseDouble(supplierData_Hour[hour][selectIndex[0] - 1][7].toString());
            double minDemand = Double.parseDouble(supplierData_Hour[hour][selectIndex[0] - 1][6].toString());
            double slopeValue = Double.parseDouble(supplierData_Hour[hour][selectIndex[0] - 1][5].toString()); // Slope Price
            double[] chartData = new double[6]; // Min Demand, Start Price, Max Demand, Last Price, Real Demand, Real Price
            legend = supplierData_Hour[hour][selectIndex[0] - 1][0].toString();
            chartData[0] = 0;
            chartData[1] = Double.parseDouble(supplierData_Hour[hour][selectIndex[0] - 1][4].toString()); // Start Price
            chartData[2] = maxDemand - minDemand; // Máx Price Sensitive Demand
            chartData[3] = chartData[1] - chartData[2] * slopeValue;
            chartData[4] = mainMarket.getSupplierAgent_PriceSensitiveDemandWithTrueCost().get(0)[hour][selectIndex[0] - 1]; // Price Sensitive Commitment
            chartData[5] = chartData[1] - chartData[4] * slopeValue;

            jTabbedPaneOutput.add("Graphical [Output Chart Information]",
                    ChartGenerator.drawLineGraph_MarginalCostVsRealPrice(legend, chartData, title+"[Hour:" + hour + "]", axisX, axisY));

            jTabbedPaneOutput.setSelectedIndex(1);
        } else {
            double[][] priceSensitiveChartData = new double[HOUR_PER_DAY][supplierPriceSensitiveData_Hour[0].length];
            axisX = "Hour";
            axisY = "Power [MW]";
            for(int i = 0; i < HOUR_PER_DAY; i++) {
                for(int j = 0; j < supplierPriceSensitiveData_Hour[0].length; j++) {
                    priceSensitiveChartData[i][j] = Double.parseDouble(supplierPriceSensitiveData_Hour[i][j][4].toString());
                }
            }
            createLineChartOutput(jTabbedPaneOutput, supplierData_Hour[0], priceSensitiveChartData, selectIndex, title+" (Fixed + Price Sensitive)", axisX, axisY);
        }
    }

    private void creatChartOutput_ProducerCommitment(int[] selectIndex, String title, String axisX, String axisY) {

        if (selectIndex.length > 1) {
            JOptionPane.showMessageDialog(null, "Please select only one Agent option!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
            if (hour == -1) {
                this.setAlwaysOnTop(false);
                JOptionPane.showMessageDialog(null, "Please select only one hour!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String legend;
                int agentIndex = selectIndex[0] - 1;
                int agentBus = Integer.parseInt(producerData_Hour[hour][agentIndex][2].toString()) - 1;
                double slopeCost = Double.parseDouble(producerData_Hour[hour][agentIndex][5].toString());
                double[] chartData = new double[6]; // Min Power Capacity, Start Price, Max Power Capacity, Last Price, Power Commitment, LMP_k
                legend = producerData_Hour[hour][agentIndex][0].toString();
                chartData[0] = Double.parseDouble(producerData_Hour[hour][agentIndex][6].toString()); // Min Power Capacity
                chartData[1] = Double.parseDouble(producerData_Hour[hour][agentIndex][4].toString()); // Start Cost
                chartData[2] = Double.parseDouble(producerData_Hour[hour][agentIndex][7].toString()); // Máx Power Capacity
                chartData[3] = chartData[1] + slopeCost * chartData[2];
                chartData[4] = Double.parseDouble(producerCommitmentData_Hour[hour][agentIndex][2].toString());
                chartData[5] = Double.parseDouble(busLMPData_Hour[hour][agentBus][2].toString());
                jTabbedPaneOutput.add("Graphical [Output Chart Information]",
                        ChartGenerator.drawLineGraph_MarginalCostVsRealPrice(legend, chartData, title, axisX, axisY));

                jTabbedPaneOutput.setSelectedIndex(1);
            }
        }
    }

    private double[][] producer_SupplyCurve(int _hour) {
        double[][] producerAuxData = new double[totalProducers][6]; // start Price, slope Price, minCap, maxCap, Current Power, Current Price
        double[] producerPrices = new double[totalProducers * 2]; // Price
        for (int i = 0; i < totalProducers; i++) {
            producerAuxData[i][0] = Double.parseDouble(producerData_Hour[_hour][i][4].toString()); // Producer Start Cost
            producerAuxData[i][1] = Double.parseDouble(producerData_Hour[_hour][i][5].toString()); // Producer Slope Price
            producerAuxData[i][2] = Double.parseDouble(producerData_Hour[_hour][i][6].toString()); // Producer Min Generation Cap
            producerPrices[2 * i] = producerAuxData[i][0] + 2 * producerAuxData[i][1] * producerAuxData[i][2]; // Min Capacity Producer Price
            producerAuxData[i][3] = Double.parseDouble(producerData_Hour[_hour][i][7].toString()); // Producer Max Generation Cap
            producerPrices[2 * i + 1] = producerAuxData[i][0] + 2 * producerAuxData[i][1] * producerAuxData[i][3]; // Máx Capacity Producer Price
            producerAuxData[i][4] = producerAuxData[i][2];
            producerAuxData[i][5] = producerPrices[2 * i];
        }

        double[][] producerChartPoints = createSupplyVsDemand(SUPPLY_DEMAND_PRODUCER, totalProducers, producerAuxData, producerPrices);

        return producerChartPoints;
    }

    private double[][] supplier_DemandCurve(int _hour) {
        double[][] supplierAuxData = new double[totalSuppliers][6]; // Fixed Demand, Start Price, Slope Price, PS Demand, Current Power, Current Price
        double[] supplierPrices = new double[totalSuppliers * 2]; // Price's Matrix

        double fixedDemand;
        double startPrice;
        double slopePrice;
        double ps_Demand;

        for (int i = 0; i < totalSuppliers; i++) {
            startPrice = Double.parseDouble(supplierData_Hour[_hour][i][4].toString());
            slopePrice = Double.parseDouble(supplierData_Hour[_hour][i][5].toString());
            fixedDemand = Double.parseDouble(supplierData_Hour[_hour][i][6].toString());
            ps_Demand = Double.parseDouble(supplierData_Hour[_hour][i][7].toString()) - fixedDemand;

            supplierAuxData[i][0] = fixedDemand;
            supplierAuxData[i][1] = startPrice;
            supplierAuxData[i][2] = slopePrice;
            supplierAuxData[i][3] = ps_Demand;
            supplierAuxData[i][4] = 0.0;
            supplierAuxData[i][5] = startPrice;

            supplierPrices[2 * i] = startPrice;
            supplierPrices[2 * i + 1] = startPrice - 2 * slopePrice * ps_Demand;
        }

        double[][] supplierChartPoints = createSupplyVsDemand(SUPPLY_DEMAND_SUPPLIER, totalSuppliers, supplierAuxData, supplierPrices);

        return supplierChartPoints;
    }

    public double[][] createSupplyVsDemand(int _agentType, int agentsNumber, double[][] _agentData, double[] _agentsPrice) {
        double[][] chartPoints = new double[2 * agentsNumber - 1][4]; // leftPoint(power, price), rightPoint(power, price
        double auxValue;
        double firstPrice;
        double lastPrice;
        double auxPower;

        for (int i = 0; i < 2 * agentsNumber; i++) {
            for (int j = i + 1; j < 2 * agentsNumber; j++) {
                if (_agentType == 0) { // Agent Producer
                    if (_agentsPrice[i] > _agentsPrice[j]) {
                        auxValue = _agentsPrice[i];
                        _agentsPrice[i] = _agentsPrice[j];
                        _agentsPrice[j] = auxValue;
                    }
                } else {
                    if (_agentsPrice[i] < _agentsPrice[j]) {
                        auxValue = _agentsPrice[i];
                        _agentsPrice[i] = _agentsPrice[j];
                        _agentsPrice[j] = auxValue;
                    }
                }
            }
        }

        firstPrice = _agentsPrice[0];
        for (int i = 0; i < agentsNumber; i++) {
            if (_agentType == 0) { // Agent Producer
                if (firstPrice >= _agentData[i][5]) {
                    chartPoints[0][0] += _agentData[i][4];
                }
            } else { // Agent Supplier
                chartPoints[0][0] += _agentData[i][0];
                if (firstPrice <= _agentData[i][5]) {
                    chartPoints[0][0] += _agentData[i][4];
                }
            }
        }

        for (int i = 0; i < 2 * agentsNumber - 1; i++) {
            chartPoints[i][1] = _agentsPrice[i];
            chartPoints[i][3] = _agentsPrice[i + 1];

            if (i > 0) {
                chartPoints[i][0] = chartPoints[i - 1][2];
            }

            chartPoints[i][2] = chartPoints[i][0];

            lastPrice = _agentsPrice[i + 1];
            if (_agentType == 0) { // Agent Producer
                for (int j = 0; j < agentsNumber; j++) {
                    if (lastPrice > _agentData[j][5]) {
                        if (Math.abs(_agentData[j][4] - _agentData[j][3]) > 0.000001) { // It is not the same value
                            auxPower = (lastPrice - _agentData[j][0]) / (2 * _agentData[j][1]);
                            chartPoints[i][2] += auxPower - _agentData[j][4];

                            _agentData[j][4] = auxPower;
                            _agentData[j][5] = lastPrice;
                        }
                    }
                }
            } else { // Agent Supplier
                for (int j = 0; j < agentsNumber; j++) {
                    if (lastPrice < _agentData[j][5]) {
                        if (Math.abs(_agentData[j][4] - _agentData[j][3]) > 0.000001) { // It is not the same value
                            auxPower = (_agentData[j][1] - lastPrice) / (2 * _agentData[j][2]);
                            chartPoints[i][2] += auxPower - _agentData[j][4];

                            _agentData[j][4] = auxPower;
                            _agentData[j][5] = lastPrice;
                        }
                    }
                }
            }
        }
        return chartPoints;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jComboBox_OutputType = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_DataAgentOutput = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxOutputHour = new javax.swing.JComboBox();
        jButton_showOutputData = new javax.swing.JButton();
        jTabbedPaneOutput = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable_OutputData = new javax.swing.JTable();
        jButtonChartOutput = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jComboBox_OutputType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_OutputType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_OutputTypeActionPerformed(evt);
            }
        });

        jList_DataAgentOutput.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList_DataAgentOutput);

        jLabel2.setText("Select Hour:");

        jComboBoxOutputHour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton_showOutputData.setText("Display OutputData");
        jButton_showOutputData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_showOutputDataActionPerformed(evt);
            }
        });

        jTable_OutputData.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(jTable_OutputData);

        jTabbedPaneOutput.addTab("Show Output Data [Table]", jScrollPane4);

        jButtonChartOutput.setText("Display Chart");
        jButtonChartOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChartOutputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addComponent(jButtonChartOutput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jComboBox_OutputType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton_showOutputData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxOutputHour, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPaneOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPaneOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jComboBox_OutputType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxOutputHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addComponent(jButton_showOutputData)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonChartOutput)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_OutputTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_OutputTypeActionPerformed
        // TODO add your handling code here:
        if (jComboBox_OutputType.getSelectedIndex() != 0) {
            jLabel2.setEnabled(true);
            jComboBoxOutputHour.setEnabled(true);
            jComboBoxOutputHour.setModel(new DefaultComboBoxModel(comboBoxHour));
            if (jComboBox_OutputType.getSelectedIndex() != 5) {
                jButton_showOutputData.setEnabled(true);
                jButtonChartOutput.setEnabled(true);
                jTable_OutputData.setEnabled(true);
                jList_DataAgentOutput.setEnabled(true);
                updateOutputNameList(jComboBox_OutputType.getSelectedIndex());
            } else {
                jComboBoxOutputHour.removeItemAt(0);
                jButton_showOutputData.setEnabled(false);
                jTable_OutputData.setEnabled(false);
                jList_DataAgentOutput.setEnabled(false);
                jButtonChartOutput.setEnabled(true);
                updateOutputNameList(jComboBox_OutputType.getSelectedIndex());
            }
        } else {
            jButton_showOutputData.setEnabled(false);
            jButtonChartOutput.setEnabled(false);
            jList_DataAgentOutput.setEnabled(false);
            jLabel2.setEnabled(false);
            jComboBoxOutputHour.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBox_OutputTypeActionPerformed

    private void jButton_showOutputDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_showOutputDataActionPerformed
        // TODO add your handling code here:
        isInputData = false;
        int[] selectIndex = jList_DataAgentOutput.getSelectedIndices();
        int comboBoxIndex = jComboBox_OutputType.getSelectedIndex();
        int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
        switch (comboBoxIndex) {
            case 1:
                printDetailedInfo(selectIndex, hour, TABLE_COMMITMENT, producerCommitmentTitle, producerCommitmentData_Day, producerCommitmentData_Hour, isInputData);
                break;
            case 2:
                printDetailedInfo(selectIndex, hour, TABLE_PROFITS, producerProfitTitle, producerProfitData_Day, producerProfitData_Hour, isInputData);
                break;
            case 3:
                printDetailedInfo(selectIndex, hour, TABLE_LMP, busLMP_Title, busLMPData_Day, busLMPData_Hour, isInputData);
                break;
            case 4:
                printDetailedInfo(selectIndex, hour, TABLE_PSDEMAND, supplierPriceSensitiveDataTitle, supplierPriceSensitiveData_Day, supplierPriceSensitiveData_Hour, isInputData);
                break;
            case 5:
                if (hour == -1) {
                    printOutputTable(marketPrice, marketPriceDataTitle);
                } else {
                    Object[][] auxMatrix = new Object[1][marketPriceDataTitle.length];
                    auxMatrix[0] = marketPrice[hour];
                    printOutputTable(auxMatrix, marketPriceDataTitle);
                }
                break;
            case 6:
                printDetailedInfo(selectIndex, hour, TABLE_PSDEMAND, branchPowerFlowTitle, branchPowerFlowData_Day, branchPowerFlowData_Hour, isInputData);
                break;
            default:
                hour = -1;
                printDetailedInfo(selectIndex, hour, TABLE_COMMITMENT, producerCommitmentTitle, null, null, isInputData);
                break;
        }
    }//GEN-LAST:event_jButton_showOutputDataActionPerformed

    private void jButtonChartOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChartOutputActionPerformed
        // TODO add your handling code here:
        int[] selectIndex = jList_DataAgentOutput.getSelectedIndices();
        int comboBoxIndex = jComboBox_OutputType.getSelectedIndex();
        int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
        int printHour = hour + START_HOUR;
        Object[][] auxData;
        String title;
        String axisX;
        String axisY;
        if (jTabbedPaneOutput.getTabCount() != 1) {
            jTabbedPaneOutput.remove(1);
        }
        switch (comboBoxIndex) {
            case 1:
                title = "Generator Commitment";
                axisX = "Hour";
                axisY = "Quantity [MWh]";
                createLineChartOutput(jTabbedPaneOutput, producerData_Hour[0], mainMarket.getProducerAgent_CommitmentWithTrueCost().get(0),
                        selectIndex, title, axisX, axisY);
                break;
            case 2:
                if (hour == -1) {
                    producerProfits = new double[HOUR_PER_DAY][producerData_Hour[0].length];
                    title = "Generators' Profits";
                    axisX = "Hour";
                    axisY = "Profits [€/hour]";
                    for (int h = 0; h < HOUR_PER_DAY; h++) {
                        for (int i = 0; i < producerData_Hour[0].length; i++) {
                            producerProfits[h][i] = Double.parseDouble(producerProfitData_Hour[h][i][6].toString());
                        }
                    }
                    createLineChartOutput(jTabbedPaneOutput, producerData_Hour[0], producerProfits,
                            selectIndex, title, axisX, axisY);
                } else {
                    if (selectIndex.length == 1 && selectIndex[0] != 0) {
                        title = "Price/Power Commitment [Hour:" + printHour + "]";
                        axisX = "Quantity [MWh]";
                        axisY = "Price [€/MWh]";
                        creatChartOutput_ProducerCommitment(selectIndex, title, axisX, axisY);
                    }
                }
                break;
            case 3:
                title = "Grid LMP";
                axisX = "Hour";
                axisY = "Quantity [MWh]";
                auxData = new Object[busLMPData_Hour[0].length][1];
                for (int i = 0; i < busLMPData_Hour[0].length; i++) {
                    auxData[i][0] = busLMPData_Hour[0][i][0];
                }

                createLineChartOutput(jTabbedPaneOutput, auxData, mainMarket.getLMPWithTrueCost().get(0),
                        selectIndex, title, axisX, axisY);
                break;
            case 4:
                title = "Price-Sensitive Demand";
                axisX = "Quantity [MWh]";
                axisY = "Price [€/MWh]";
                createLineChartOutput_Supplier(selectIndex, title, axisX, axisY);
                break;
            case 5:
                printHour += 1;
                hour = jComboBoxOutputHour.getSelectedIndex();
                title = "Supply and Demand [h =" + printHour + "]";
                axisX = "Power [MW]";
                axisY = "Price per hour [€/MWh]";

                jTabbedPaneOutput.add("Graphical [Output Chart Information]",
                        ChartGenerator.drawLineGraph_SupplyVsDemand(producer_SupplyCurve(hour), supplier_DemandCurve(hour), title, axisX, axisY));

                jTabbedPaneOutput.setSelectedIndex(1);
                break;
            case 6:
                title = "Branch Power Flow";
                axisX = "Hour";
                axisY = "abs(Power[MW])";
                
                createLineChartOutput(jTabbedPaneOutput, branchData, branchPowerFlow_abs,
                        selectIndex, title, axisX, axisY);
                break;
            default:
                printHour += 1;
                hour = jComboBoxOutputHour.getSelectedIndex();
                title = "Supply and Demand [h =" + printHour + "]";
                axisX = "Power [MW]";
                axisY = "Price per hour [€/MWh]";

                jTabbedPaneOutput.add("Graphical [Output Chart Information]",
                        ChartGenerator.drawLineGraph_SupplyVsDemand(producer_SupplyCurve(hour), supplier_DemandCurve(hour), title, axisX, axisY));

                jTabbedPaneOutput.setSelectedIndex(1);
                break;
        }
    }//GEN-LAST:event_jButtonChartOutputActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonChartOutput;
    private javax.swing.JButton jButton_showOutputData;
    private javax.swing.JComboBox jComboBoxOutputHour;
    private javax.swing.JComboBox jComboBox_OutputType;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList_DataAgentOutput;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPaneOutput;
    private javax.swing.JTable jTable_OutputData;
    // End of variables declaration//GEN-END:variables
}
