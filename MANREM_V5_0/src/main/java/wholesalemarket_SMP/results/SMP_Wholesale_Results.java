package wholesalemarket_SMP.results;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JTable;
import wholesalemarket_SMP.AgentData;

class DecimalFormatRenderer extends DefaultTableCellRenderer {

    private static final DecimalFormat formatter = new DecimalFormat("#.00");

    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        value = formatter.format((Number) value);
        return super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
    }
}

public class SMP_Wholesale_Results extends javax.swing.JFrame {

    private ArrayList<AgentData> sellers_List;
    private ArrayList<AgentData> buyers_List;

    DefaultTableModel tableInput = null;
    DefaultTableModel tableOutput = null;

    private boolean is_Sym;

    private int totalSellers, totalBuyers, indexHour;
    private boolean isInputData;

    private int HOUR_PER_DAY = 24;
    private int START_HOUR = 0;

    private double[] powerPerHour, pricePerHour;
    private double[][] power_Commitment;

    // Print Output
    private final int TABLE_COMMITMENT = 1;
    private final int TABLE_SMP = 2;
    private final int TABLE_PSDEMAND = 3;
    private final int TABLE_PROFITS = 5;

    private Object[][][] sellerData_Hour;
    private Object[][] sellerData_Day;
    private Object[][][] sellerCommitmentData_Hour;
    private Object[][] sellerCommitmentData_Day;
    private Object[][][] sellerProfitData_Hour;
    private Object[][] sellerProfitData_Day;
    private Object[][] marketPrice;
    private Object[][][] buyerData_Hour;
    private Object[][] buyerData_Day;
    private Object[][][] buyerPriceSensitiveData_Hour;
    private Object[][] buyerPriceSensitiveData_Day;
    private Object[] comboBoxHour;
    private Object[][][] SMP_Data_Hour;
    private Object[][] SMP_Data_Day;

    private final String[] outputComboBox = {"Select market results", "Generation commitments",
        "Generator revenues", "Retailer results", "Market price"};
    private final String[] SMP_Title = {"Hour", "System Marginal Price (€/MWh)"};
    private final String[] sellerDataTitle = {"Name", "ID", "Hour", "Price[€/MWh]", "Power[MW]"};
    private final String[] sellerCommitmentTitle = {"Name", "Hour", "Power[MW]", "Traded Power[MW]", "Price[€/MWh]", "Market Price[€/MWh]"};
    private final String[] sellerProfitTitle = {"Name", "Hour", "Power Commitment[MW]", "Marginal Cost[€/MWh]", "Sales Price[€/MWh]", "Revenue[€/h]", "Profit[€/h]"};
    private final String[] buyerDataTitle = {"Name", "ID", "Hour", "Price[€/MWh]", "Power[MW]"};
    private final String[] buyerPriceSensitiveDataTitle = {"Name", "Hour", "Power[MW]", "Traded Power[MW]", "Price[€/MWh]", "Market Price[€/MWh]"};

    public SMP_Wholesale_Results(ArrayList<AgentData> _bids, ArrayList<AgentData> _offers, int _startHour, int _endHour, boolean is_Sym) {
        initComponents();
        this.is_Sym = is_Sym;
        this.sellers_List = _offers;
        this.totalSellers = sellers_List.size();
        this.buyers_List = _bids;
        this.totalBuyers = buyers_List.size();
        this.START_HOUR = _startHour;
        this.HOUR_PER_DAY = _endHour - _startHour + 1;

        createSellersDataMatrix();
        createBuyerDataMatrix();
        createSellerCommitmentDataMatrix();
        createPriceSensitiveDemandMatrix();
        createSMP_Matrix();

        defineWindow();
        updateOutputNameList(0);

        initComboBox();
        int[] index = {0};
        printDetailedInfo(index, -1, TABLE_SMP, SMP_Title, SMP_Data_Day, SMP_Data_Hour, isInputData);
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
        jComboBox_Output_Generator.setModel(new DefaultComboBoxModel(outputComboBox));
        jComboBoxOutputHour.setModel(new DefaultComboBoxModel(comboBoxHour));
        jComboBox_Output_Generator.setSelectedIndex(0);
        jComboBoxOutputHour.setSelectedIndex(0);
    }

    /**
     *
     */
    private void defineWindow() {
        this.setTitle("Spot Market Results");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(false);
        this.setLocationRelativeTo(null);
        tableOutput = (DefaultTableModel) jTable_OutputData.getModel();
        jTable_OutputData.setAutoscrolls(true);
        jTable_OutputData.setShowGrid(true);
        jTable_OutputData.setEnabled(false);
    }

    /**
     *
     */
    private void createSellersDataMatrix() {
        this.sellerData_Hour = new Object[HOUR_PER_DAY][totalSellers][sellerDataTitle.length];
        this.sellerData_Day = new Object[HOUR_PER_DAY * totalSellers][sellerDataTitle.length];
        int index;

        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < sellers_List.size(); i++) {
                index = h * sellers_List.size();
                indexHour = h + START_HOUR;
                sellerData_Hour[h][i][0] = sellers_List.get(i).getAgent();
                sellerData_Hour[h][i][1] = (int) sellers_List.get(i).getId();
                sellerData_Hour[h][i][2] = indexHour;
                sellerData_Hour[h][i][3] = String.format("%.2f", sellers_List.get(i).getPrice().get(indexHour));
                sellerData_Hour[h][i][4] = String.format("%.2f", sellers_List.get(i).getPower().get(indexHour));

                sellerData_Day[index + i][0] = sellers_List.get(i).getAgent();
                sellerData_Day[index + i][1] = (int) sellers_List.get(i).getId();
                sellerData_Day[index + i][2] = indexHour;
                sellerData_Day[index + i][3] = String.format("%.2f", sellers_List.get(i).getPrice().get(indexHour));
                sellerData_Day[index + i][4] = String.format("%.2f", sellers_List.get(i).getPower().get(indexHour));
            }
        }
    }

    /**
     *
     */
    private void createSellerCommitmentDataMatrix() {
        sellerCommitmentData_Hour = new Object[HOUR_PER_DAY][totalSellers][sellerCommitmentTitle.length];
        sellerCommitmentData_Day = new Object[HOUR_PER_DAY * totalSellers][sellerCommitmentTitle.length];
        sellerProfitData_Hour = new Object[HOUR_PER_DAY][totalSellers][sellerProfitTitle.length];
        sellerProfitData_Day = new Object[HOUR_PER_DAY * totalSellers][sellerProfitTitle.length];
        power_Commitment = new double[HOUR_PER_DAY][totalSellers];
        powerPerHour = new double[HOUR_PER_DAY];
        pricePerHour = new double[HOUR_PER_DAY];
        marketPrice = new Object[HOUR_PER_DAY][3];

        int index;
        double marginalCost;
        double aux_realPrice, aux_powerCommitment, aux_revenue, aux_profit;

        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < totalSellers; i++) {
                indexHour = h + START_HOUR;
                if (is_Sym) {
                    sellerCommitmentData_Hour[h][i][0] = sellers_List.get(i).getAgent();
                    sellerCommitmentData_Hour[h][i][1] = indexHour;
                    sellerCommitmentData_Hour[h][i][2] = String.format("%.2f", sellers_List.get(i).getPower().get(indexHour));
                    sellerCommitmentData_Hour[h][i][3] = String.format("%.2f", sellers_List.get(i).getTraded_power_Sym().get(indexHour));
                    sellerCommitmentData_Hour[h][i][4] = String.format("%.2f", sellers_List.get(i).getPrice().get(indexHour));
                    sellerCommitmentData_Hour[h][i][5] = String.format("%.2f", sellers_List.get(i).getMarket_Price_Sym().get(indexHour));
                } else {
                    sellerCommitmentData_Hour[h][i][0] = sellers_List.get(i).getAgent();
                    sellerCommitmentData_Hour[h][i][1] = indexHour;
                    sellerCommitmentData_Hour[h][i][2] = String.format("%.2f", sellers_List.get(i).getPower().get(indexHour));
                    sellerCommitmentData_Hour[h][i][3] = String.format("%.2f", sellers_List.get(i).getTraded_power_aSym().get(indexHour));
                    sellerCommitmentData_Hour[h][i][4] = String.format("%.2f", sellers_List.get(i).getPrice().get(indexHour));
                    sellerCommitmentData_Hour[h][i][5] = String.format("%.2f", sellers_List.get(i).getMarket_Price_aSym().get(indexHour));
                }

                if (is_Sym) {
                    marginalCost = sellers_List.get(i).getPrice().get(indexHour);
                    aux_powerCommitment = sellers_List.get(i).getTraded_power_Sym().get(indexHour);
                    aux_realPrice = sellers_List.get(i).getMarket_Price_Sym().get(indexHour);
                    aux_revenue = aux_realPrice * aux_powerCommitment;
                    aux_profit = (aux_realPrice - marginalCost) * aux_powerCommitment;
                } else {
                    marginalCost = sellers_List.get(i).getPrice().get(indexHour);
                    aux_powerCommitment = sellers_List.get(i).getTraded_power_aSym().get(indexHour);
                    aux_realPrice = sellers_List.get(i).getMarket_Price_aSym().get(indexHour);
                    aux_revenue = aux_realPrice * aux_powerCommitment;
                    aux_profit = (aux_realPrice - marginalCost) * aux_powerCommitment;
                }

                sellerProfitData_Hour[h][i][0] = sellers_List.get(i).getAgent();
                sellerProfitData_Hour[h][i][1] = indexHour;
                sellerProfitData_Hour[h][i][2] = String.format("%.2f", aux_powerCommitment);
                sellerProfitData_Hour[h][i][3] = String.format("%.2f", marginalCost);
                sellerProfitData_Hour[h][i][4] = String.format("%.2f", aux_realPrice);
                sellerProfitData_Hour[h][i][5] = String.format("%.2f", aux_revenue);
                sellerProfitData_Hour[h][i][6] = String.format("%.2f", aux_profit);

                powerPerHour[h] += aux_powerCommitment;
                power_Commitment[h][i] = aux_powerCommitment;
            }

            if (is_Sym) {
                pricePerHour[h] = sellers_List.get(0).getMarket_Price_Sym().get(indexHour);
            } else {
                pricePerHour[h] = sellers_List.get(0).getMarket_Price_aSym().get(indexHour);
            }

            indexHour = h * START_HOUR;
            marketPrice[h][0] = indexHour;
            marketPrice[h][1] = String.format("%.2f", powerPerHour[h]);
            marketPrice[h][2] = String.format("%.2f", pricePerHour[h]);

            for (int j = 0; j < totalSellers; j++) {
                index = h * totalSellers;
                indexHour = h + START_HOUR;

                if (is_Sym) {
                    sellerCommitmentData_Day[index + j][0] = sellers_List.get(j).getAgent();
                    sellerCommitmentData_Day[index + j][1] = indexHour;
                    sellerCommitmentData_Day[index + j][2] = String.format("%.2f", sellers_List.get(j).getPower().get(indexHour));
                    sellerCommitmentData_Day[index + j][3] = String.format("%.2f", sellers_List.get(j).getTraded_power_Sym().get(indexHour));
                    sellerCommitmentData_Day[index + j][4] = String.format("%.2f", sellers_List.get(j).getPrice().get(indexHour));
                    sellerCommitmentData_Day[index + j][5] = String.format("%.2f", sellers_List.get(j).getMarket_Price_Sym().get(indexHour));

                    marginalCost = sellers_List.get(j).getPrice().get(indexHour);
                    aux_powerCommitment = sellers_List.get(j).getTraded_power_Sym().get(indexHour);
                    aux_realPrice = sellers_List.get(j).getMarket_Price_Sym().get(indexHour);
                    aux_revenue = aux_realPrice * aux_powerCommitment;
                    aux_profit = (aux_realPrice - marginalCost) * aux_powerCommitment;

                    sellerProfitData_Day[index + j][0] = sellers_List.get(j).getAgent();
                    sellerProfitData_Day[index + j][1] = indexHour;
                    sellerProfitData_Day[index + j][2] = String.format("%.2f", aux_powerCommitment);
                    sellerProfitData_Day[index + j][3] = String.format("%.2f", marginalCost);
                    sellerProfitData_Day[index + j][4] = String.format("%.2f", aux_realPrice);
                    sellerProfitData_Day[index + j][5] = String.format("%.2f", aux_revenue);
                    sellerProfitData_Day[index + j][6] = String.format("%.2f", aux_profit);
                } else {
                    sellerCommitmentData_Day[index + j][0] = sellers_List.get(j).getAgent();
                    sellerCommitmentData_Day[index + j][1] = indexHour;
                    sellerCommitmentData_Day[index + j][2] = String.format("%.2f", sellers_List.get(j).getPower().get(indexHour));
                    sellerCommitmentData_Day[index + j][3] = String.format("%.2f", sellers_List.get(j).getTraded_power_aSym().get(indexHour));
                    sellerCommitmentData_Day[index + j][4] = String.format("%.2f", sellers_List.get(j).getPrice().get(indexHour));
                    sellerCommitmentData_Day[index + j][5] = String.format("%.2f", sellers_List.get(j).getMarket_Price_aSym().get(indexHour));

                    marginalCost = sellers_List.get(j).getPrice().get(indexHour);
                    aux_powerCommitment = sellers_List.get(j).getTraded_power_aSym().get(indexHour);
                    aux_realPrice = sellers_List.get(j).getMarket_Price_aSym().get(indexHour);
                    aux_revenue = aux_realPrice * aux_powerCommitment;
                    aux_profit = (aux_realPrice - marginalCost) * aux_powerCommitment;

                    sellerProfitData_Day[index + j][0] = sellers_List.get(j).getAgent();
                    sellerProfitData_Day[index + j][1] = indexHour;
                    sellerProfitData_Day[index + j][2] = String.format("%.2f", aux_powerCommitment);
                    sellerProfitData_Day[index + j][3] = String.format("%.2f", marginalCost);
                    sellerProfitData_Day[index + j][4] = String.format("%.2f", aux_realPrice);
                    sellerProfitData_Day[index + j][5] = String.format("%.2f", aux_revenue);
                    sellerProfitData_Day[index + j][6] = String.format("%.2f", aux_profit);
                }
            }
        }
    }

    /**
     *
     */
    private void createBuyerDataMatrix() {
        buyerData_Hour = new Object[HOUR_PER_DAY][totalBuyers][buyerDataTitle.length];
        buyerData_Day = new Object[HOUR_PER_DAY * totalBuyers][buyerDataTitle.length];
        int index;
        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < totalBuyers; i++) {
                index = h * totalBuyers;
                indexHour = h + START_HOUR;
                buyerData_Hour[h][i][0] = buyers_List.get(i).getAgent();
                buyerData_Hour[h][i][1] = (int) buyers_List.get(i).getId();
                buyerData_Hour[h][i][2] = indexHour;
                buyerData_Hour[h][i][3] = String.format("%.2f", buyers_List.get(i).getPrice().get(indexHour));
                buyerData_Hour[h][i][4] = String.format("%.2f", buyers_List.get(i).getPeriodPower(indexHour));

                buyerData_Day[index + i][0] = buyers_List.get(i).getAgent();
                buyerData_Day[index + i][1] = (int) buyers_List.get(i).getId();
                buyerData_Day[index + i][2] = indexHour;
                buyerData_Day[index + i][3] = String.format("%.2f", buyers_List.get(i).getPrice().get(indexHour));
                buyerData_Day[index + i][4] = String.format("%.2f", buyers_List.get(i).getPeriodPower(indexHour));
            }
        }
    }

    /**
     *
     */
    private void createPriceSensitiveDemandMatrix() {
        int index;

        if (is_Sym) {
            buyerPriceSensitiveData_Hour = new Object[HOUR_PER_DAY][totalBuyers][buyerPriceSensitiveDataTitle.length];
            buyerPriceSensitiveData_Day = new Object[HOUR_PER_DAY * totalBuyers][buyerPriceSensitiveDataTitle.length];
        } else {
            buyerPriceSensitiveData_Hour = new Object[HOUR_PER_DAY][totalBuyers][buyerPriceSensitiveDataTitle.length];
            buyerPriceSensitiveData_Day = new Object[HOUR_PER_DAY * totalBuyers][buyerPriceSensitiveDataTitle.length];
        }

        for (int h = 0; h < HOUR_PER_DAY; h++) {
            for (int i = 0; i < totalBuyers; i++) {
                index = h * totalBuyers;
                indexHour = h + START_HOUR;

                if (is_Sym) {
                    buyerPriceSensitiveData_Hour[h][i][0] = buyers_List.get(i).getAgent();
                    buyerPriceSensitiveData_Hour[h][i][1] = indexHour;
                    buyerPriceSensitiveData_Hour[h][i][2] = String.format("%.2f", buyers_List.get(i).getPower().get(indexHour));
                    buyerPriceSensitiveData_Hour[h][i][3] = String.format("%.2f", buyers_List.get(i).getTraded_power_Sym().get(indexHour));
                    buyerPriceSensitiveData_Hour[h][i][4] = String.format("%.2f", buyers_List.get(i).getPrice().get(indexHour));
                    buyerPriceSensitiveData_Hour[h][i][5] = String.format("%.2f", buyers_List.get(i).getMarket_Price_Sym().get(indexHour));

                    buyerPriceSensitiveData_Day[index + i][0] = buyers_List.get(i).getAgent();
                    buyerPriceSensitiveData_Day[index + i][1] = indexHour;
                    buyerPriceSensitiveData_Day[index + i][2] = String.format("%.2f", buyers_List.get(i).getPower().get(indexHour));
                    buyerPriceSensitiveData_Day[index + i][3] = String.format("%.2f", buyers_List.get(i).getTraded_power_Sym().get(indexHour));
                    buyerPriceSensitiveData_Day[index + i][4] = String.format("%.2f", buyers_List.get(i).getPrice().get(indexHour));
                    buyerPriceSensitiveData_Day[index + i][5] = String.format("%.2f", buyers_List.get(i).getMarket_Price_Sym().get(indexHour));

                } else {
                    buyerPriceSensitiveData_Hour[h][i][0] = buyers_List.get(i).getAgent();
                    buyerPriceSensitiveData_Hour[h][i][1] = indexHour;
                    buyerPriceSensitiveData_Hour[h][i][2] = String.format("%.2f", buyers_List.get(i).getPower().get(indexHour));
                    buyerPriceSensitiveData_Hour[h][i][3] = String.format("%.2f", buyers_List.get(i).getTraded_power_aSym().get(indexHour));
                    buyerPriceSensitiveData_Hour[h][i][4] = String.format("%.2f", buyers_List.get(i).getPrice().get(indexHour));
                    buyerPriceSensitiveData_Hour[h][i][5] = String.format("%.2f", buyers_List.get(i).getMarket_Price_aSym().get(indexHour));

                    buyerPriceSensitiveData_Day[index + i][0] = buyers_List.get(i).getAgent();
                    buyerPriceSensitiveData_Day[index + i][1] = indexHour;
                    buyerPriceSensitiveData_Day[index + i][2] = String.format("%.2f", buyers_List.get(i).getPower().get(indexHour));
                    buyerPriceSensitiveData_Day[index + i][3] = String.format("%.2f", buyers_List.get(i).getTraded_power_aSym().get(indexHour));
                    buyerPriceSensitiveData_Day[index + i][4] = String.format("%.2f", buyers_List.get(i).getPrice().get(indexHour));
                    buyerPriceSensitiveData_Day[index + i][5] = String.format("%.2f", buyers_List.get(i).getMarket_Price_aSym().get(indexHour));
                }
            }
        }
    }

    private void createSMP_Matrix() {
        this.SMP_Data_Hour = new Object[HOUR_PER_DAY][1][SMP_Title.length];
        this.SMP_Data_Day = new Object[HOUR_PER_DAY][SMP_Title.length];

        for (int h = 0; h < HOUR_PER_DAY; h++) {
            indexHour = h + START_HOUR;

            if (is_Sym) {
                SMP_Data_Hour[h][0][0] = indexHour;
                SMP_Data_Hour[h][0][1] = String.format("%.2f", sellers_List.get(0).getMarket_Price_Sym().get(indexHour));

                SMP_Data_Day[h][0] = indexHour;
                SMP_Data_Day[h][1] = String.format("%.2f", sellers_List.get(0).getMarket_Price_Sym().get(indexHour));
            } else {
                SMP_Data_Hour[h][0][0] = indexHour;
                SMP_Data_Hour[h][0][1] = String.format("%.2f", sellers_List.get(0).getMarket_Price_aSym().get(indexHour));

                SMP_Data_Day[h][0] = indexHour;
                SMP_Data_Day[h][1] = String.format("%.2f", sellers_List.get(0).getMarket_Price_aSym().get(indexHour));
            }
        }
    }

    /**
     *
     * @param insertData
     * @param columnSize
     */
    public void printOutputTable(Object[][] insertData, String[] columnSize) {
        DefaultTableModel table = new DefaultTableModel(insertData, columnSize);
        jTable_OutputData.setModel(table);
        configOutputTable(columnSize.length);
    }

    /**
     *
     * @param _size
     */
    private void configOutputTable(int _size) {

        for (int i = 0; i < _size; i++) {
            if (i == 0) {
                jTable_OutputData.getColumnModel().getColumn(i).setPreferredWidth(80);
            } else if (i == 1) {
                jTable_OutputData.getColumnModel().getColumn(i).setPreferredWidth(40);
            } else {
                jTable_OutputData.getColumnModel().getColumn(i).setMinWidth(100);
            }
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        jTable_OutputData.setDefaultRenderer(Object.class, render);
    }

    /**
     *
     * @param index
     */
    private void updateOutputNameList(int index) {
        DefaultListModel listModel = new DefaultListModel();
        listModel.removeAllElements();
        listModel.addElement("All Data");
        jLabel2.setVisible(true);
        jComboBoxOutputHour.setVisible(true);

        switch (index) {
            case 0:
                printOutputTable(null, sellerCommitmentTitle);
                break;
            case 1: // Info is equal for the case 2
            case 2:
                for (Object[] sellerData_Hour1 : sellerData_Hour[0]) {
                    listModel.addElement(sellerData_Hour1[0].toString().replace(",", "."));
                }
                printOutputTable(null, sellerCommitmentTitle);
                break;
            case 3:
                for (int i = 0; i < totalBuyers; i++) {
                    listModel.addElement(buyerData_Hour[0][i][0].toString().replace(",", "."));
                }
                printOutputTable(null, buyerPriceSensitiveDataTitle);
                break;
            case 4:
                printOutputTable(null, SMP_Title);
                break;
            default:
                printOutputTable(null, sellerCommitmentTitle);
                break;
        }
        jList_Data_Generator_Output.setModel(listModel);
        jList_Data_Generator_Output.setSelectedIndex(0);
    }

    /**
     *
     * @param selectIndex
     * @param hour
     * @param tableType
     * @param tableTitle
     * @param tableData2D
     * @param tableData3D
     * @param isInputData
     */
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

    /**
     *
     * @param _tabPane
     * @param _agentData
     * @param _outputData
     * @param _selectIndex
     * @param _title
     * @param _axisX
     * @param _axisY
     */
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
                legend[i] = _agentData[i][0].toString().replace(",", ".");
            }
            _tabPane.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend, _outputData, _title, _axisX, _axisY));
        } else {
            int totalAgents = _selectIndex.length; // Número total de agentes seleccionados
            legend = new String[totalAgents]; // Número de legendas = número de agentes
            showData = new double[_outputData.length][totalAgents];

            for (int i = 0; i < totalAgents; i++) {
                legend[i] = _agentData[_selectIndex[i] - 1][0].toString().replace(",", "."); // Cria legenda com os nomes dos agentes seleccionados
                for (int j = 0; j < _outputData.length; j++) {
                    showData[j][i] = _outputData[j][_selectIndex[i] - 1];
                }
            }
            _tabPane.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend, showData, _title, _axisX, _axisY));
        }
        _tabPane.setSelectedIndex(1);
    }

    /**
     *
     * @param selectIndex
     * @param title
     * @param axisX
     * @param axisY
     */
    private void creatChartOutput_ProducerCommitment(int[] selectIndex, String title, String axisX, String axisY, Object[][][] _data) {

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
                double[] chartData = new double[5]; // Min Power Capacity, Marginal Cost, Max Power Capacity, Last Price, Power Commitment
                legend = sellerData_Hour[hour][agentIndex][0].toString().replace(",", ".");
                chartData[0] = 0; // Min Power Capacity
                chartData[1] = Double.parseDouble(sellerData_Hour[hour][agentIndex][3].toString().replace(",", ".")); // Marginal Cost
                chartData[2] = Double.parseDouble(sellerData_Hour[hour][agentIndex][4].toString().replace(",", ".")); // Máx Power Capacity
                chartData[3] = chartData[1]; // Last Price
                chartData[4] = Double.parseDouble(_data[hour][agentIndex][2].toString().replace(",", ".")); // Power Commitment
                jTabbedPaneOutput.add("Graphical [Output Chart Information]",
                        ChartGenerator.drawLineGraph_MarginalCostVsRealPrice(legend, chartData, title, axisX, axisY));

                jTabbedPaneOutput.setSelectedIndex(1);
            }
        }
    }

    /**
     *
     * @param selectIndex
     * @param title
     * @param axisX
     * @param axisY
     */
    private void createLineChartOutput_Supplier(int[] selectIndex, Object[][][] _agentData, String title, String axisX, String axisY, Object[][][] _data) {
        String legend;

        int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
        if (selectIndex[0] != 0 && selectIndex.length == 1 && hour != -1) {
            double maxDemand = Double.parseDouble(buyerData_Hour[hour][selectIndex[0] - 1][4].toString().replace(",", "."));
            double[] chartData = new double[6]; // Min Demand, Start Price, Max Demand, Last Price, Real Demand, Real Price
            legend = buyerData_Hour[hour][selectIndex[0] - 1][0].toString().replace(",", ".");
            chartData[0] = 0;
            chartData[1] = Double.parseDouble(_agentData[hour][selectIndex[0] - 1][3].toString().replace(",", ".")); // Start Price
            chartData[2] = maxDemand; // Máx Price Sensitive Demand
            chartData[3] = chartData[1];
            chartData[4] = Double.parseDouble(_data[hour][selectIndex[0] - 1][2].toString().replace(",", "."));// Power Commitment
            chartData[5] = Double.parseDouble(buyers_List.get(0).getMarket_Price_Sym().get(hour).toString().replace(",", "."));

            jTabbedPaneOutput.add("Graphical [Output Chart Information]",
                    ChartGenerator.drawLineGraph_MarginalCostVsRealPrice(legend, chartData, title + "[Hour:" + hour + "]", axisX, axisY));

            jTabbedPaneOutput.setSelectedIndex(1);
        } else {
            double[][] priceSensitiveChartData = new double[HOUR_PER_DAY][_data[0].length];
            axisX = "Hour";
            axisY = "Power [MW]";
            for (int i = 0; i < HOUR_PER_DAY; i++) {
                for (int j = 0; j < _data[0].length; j++) {
                    priceSensitiveChartData[i][j] = Double.parseDouble(_data[i][j][2].toString().replace(",", "."));
                }
            }
            createLineChartOutput(jTabbedPaneOutput, _agentData[0], priceSensitiveChartData, selectIndex, title, axisX, axisY);
        }
    }
    
        private void createDemandVsSuplly(int[] selectIndex, Object[][][] _agentData, String title, String axisX, String axisY) {
        String legend;

        double[][] demand = new double[totalBuyers][2];
        double[][] supply = new double[totalSellers][2];
        int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
        if (selectIndex[0] == 0 && selectIndex.length == 1 && hour != -1) {
     
            for (int i=0;i<totalBuyers;i++){
                demand[i][0]= Double.valueOf(buyerData_Hour[hour][i][3].toString().replace(",", "."));
                demand[i][1]= Double.valueOf(buyerData_Hour[hour][i][4].toString().replace(",", "."));
            }
         Arrays.sort(demand, new Comparator<double[]>() {
        @Override
        public int compare(double[] o1, double[] o2) {
            return Double.compare(o2[0], o1[0]);
        }
    });
         for (int i=0;i<totalBuyers;i++){
             if(i>0){
              demand[i][1]+=demand[i-1][1];
           }
         }
                    for (int i=0;i<totalSellers;i++){
                supply[i][0]= Double.valueOf(sellerData_Hour[hour][i][3].toString().replace(",", "."));
                supply[i][1]= Double.valueOf(sellerData_Hour[hour][i][4].toString().replace(",", "."));
            }
         Arrays.sort(supply, new Comparator<double[]>() {
        @Override
        public int compare(double[] o1, double[] o2) {
            return Double.compare(o1[0], o2[0]);
        }
    });
         for (int i=0;i<totalSellers;i++){
             if(i>0){
              supply[i][1]+=supply[i-1][1];
           }
         }
             jTabbedPaneOutput.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph_SupplyVsDemand(supply, demand, title, axisX, axisY));
        
//         double[][] priceSensitiveChartData = new double[totalBuyers][2];
//                  for (int i=0;i<totalBuyers;i++){
//                priceSensitiveChartData[i][0]=0;
//            }
            jTabbedPaneOutput.setSelectedIndex(1);
        } else {
//            double[][] priceSensitiveChartData = new double[HOUR_PER_DAY][_data[0].length];
//            axisX = "Hour";
//            axisY = "Power [MW]";
//            for (int i = 0; i < HOUR_PER_DAY; i++) {
//                for (int j = 0; j < _data[0].length; j++) {
//                    priceSensitiveChartData[i][j] = Double.parseDouble(_data[i][j][2].toString().replace(",", "."));
//                }
//            }
//            createLineChartOutput(jTabbedPaneOutput, _agentData[0], priceSensitiveChartData, selectIndex, title, axisX, axisY);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jComboBox_Output_Generator = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList_Data_Generator_Output = new javax.swing.JList();
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

        jComboBox_Output_Generator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_Output_Generator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_Output_GeneratorActionPerformed(evt);
            }
        });

        jList_Data_Generator_Output.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList_Data_Generator_Output);

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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jComboBoxOutputHour, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonChartOutput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_showOutputData, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane3)
                                .addComponent(jComboBox_Output_Generator, 0, 200, Short.MAX_VALUE))
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPaneOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPaneOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jComboBox_Output_Generator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3)
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_Output_GeneratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Output_GeneratorActionPerformed
        // TODO add your handling code here:
        if (jComboBox_Output_Generator.getSelectedIndex() != 0) {
            jLabel2.setEnabled(true);
            jComboBoxOutputHour.setEnabled(true);
            jComboBoxOutputHour.setModel(new DefaultComboBoxModel(comboBoxHour));
            jButton_showOutputData.setEnabled(true);
            jButtonChartOutput.setEnabled(true);
            jTable_OutputData.setEnabled(true);
            jList_Data_Generator_Output.setEnabled(true);
            if (jComboBox_Output_Generator.getSelectedIndex() == 4) {
                jComboBoxOutputHour.setEnabled(false);
            }
            updateOutputNameList(jComboBox_Output_Generator.getSelectedIndex());
        } else {
            jButton_showOutputData.setEnabled(false);
            jButtonChartOutput.setEnabled(false);
            jList_Data_Generator_Output.setEnabled(false);
            jLabel2.setEnabled(false);
            jComboBoxOutputHour.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBox_Output_GeneratorActionPerformed

    private void jButton_showOutputDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_showOutputDataActionPerformed
        // TODO add your handling code here:
        isInputData = false;
        int[] selectIndex = jList_Data_Generator_Output.getSelectedIndices();

        int comboBoxIndex = jComboBox_Output_Generator.getSelectedIndex();
        int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
        switch (comboBoxIndex) {
            case 1:
                jComboBoxOutputHour.setEnabled(false);
                printDetailedInfo(selectIndex, hour, TABLE_COMMITMENT, sellerCommitmentTitle, sellerCommitmentData_Day, sellerCommitmentData_Hour, isInputData);
                break;
            case 2:
                printDetailedInfo(selectIndex, hour, TABLE_PROFITS, sellerProfitTitle, sellerProfitData_Day, sellerProfitData_Hour, isInputData);
                break;
            case 3:
                printDetailedInfo(selectIndex, hour, TABLE_PSDEMAND, buyerPriceSensitiveDataTitle, buyerPriceSensitiveData_Day, buyerPriceSensitiveData_Hour, isInputData);
                break;
            case 4:
                jComboBoxOutputHour.setEnabled(true);
                printDetailedInfo(selectIndex, hour, TABLE_SMP, SMP_Title, SMP_Data_Day, SMP_Data_Hour, isInputData);
                break;
            default:
                hour = -1;
                printDetailedInfo(selectIndex, hour, TABLE_SMP, SMP_Title, SMP_Data_Day, SMP_Data_Hour, isInputData);
                break;
        }
    }//GEN-LAST:event_jButton_showOutputDataActionPerformed

    private void jButtonChartOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChartOutputActionPerformed
        // TODO add your handling code here:
        int[] selectIndex = jList_Data_Generator_Output.getSelectedIndices();
        int comboBoxIndex = jComboBox_Output_Generator.getSelectedIndex();
        int hour = jComboBoxOutputHour.getSelectedIndex() - 1;
        int printHour = hour + START_HOUR;
        String title;
        String axisX;
        String axisY;
        if (jTabbedPaneOutput.getTabCount() != 1) {
            jTabbedPaneOutput.remove(1);
        }
        switch (comboBoxIndex) {
            case 1:
                double[][] producer_Sym = new double[HOUR_PER_DAY][2];
                title = "Generators";
                axisX = "Hour";
                axisY = "Quantity [MWh]";
                if (selectIndex[0] != 0 && selectIndex.length == 1) {
                    String[] legend = {sellers_List.get(selectIndex[0] - 1).getAgent() + " - Power", sellers_List.get(selectIndex[0] - 1).getAgent() + " - Tradded Power"};
                    for (int h = 0; h < HOUR_PER_DAY; h++) {
                        producer_Sym[h][0] = Double.parseDouble(sellerCommitmentData_Hour[h][selectIndex[0] - 1][2].toString().replace(",", "."));
                        producer_Sym[h][1] = Double.parseDouble(sellerCommitmentData_Hour[h][selectIndex[0] - 1][3].toString().replace(",", "."));
                    }
                    if (jTabbedPaneOutput.getTabCount() != 1) {
                        jTabbedPaneOutput.remove(1);
                    }
                    jTabbedPaneOutput.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend, producer_Sym, title, axisX, axisY));
                    jTabbedPaneOutput.setSelectedIndex(1);
                } else {
                    createLineChartOutput(jTabbedPaneOutput, sellerData_Hour[0], power_Commitment,
                            selectIndex, title, axisX, axisY);
                }
                break;
            case 2:
                if (hour == -1) {
                    double[][] sellerProfits = new double[HOUR_PER_DAY][sellerData_Hour[0].length];
                    title = "Generators Profits";
                    axisX = "Hour";
                    axisY = "Profits [€/hour]";
                    for (int h = 0; h < HOUR_PER_DAY; h++) {
                        for (int i = 0; i < sellerData_Hour[0].length; i++) {
                            sellerProfits[h][i] = Double.parseDouble(sellerProfitData_Hour[h][i][6].toString().replace(",", "."));
                        }
                    }
                    createLineChartOutput(jTabbedPaneOutput, sellerData_Hour[0], sellerProfits,
                            selectIndex, title, axisX, axisY);
                } else {
                    if (selectIndex.length == 1 && selectIndex[0] != 0) {
                        title = "Price/Power Commitment [Hour:" + printHour + "] - Symmetrical Pool";
                        axisX = "Power [MW]";
                        axisY = "Price [€/MWh]";
                        creatChartOutput_ProducerCommitment(selectIndex, title, axisX, axisY, sellerCommitmentData_Hour);
                    }
                }
                break;
            case 3:
                double[][] supplier_Sym = new double[HOUR_PER_DAY][2];
                title = "Retailers";
                axisX = "Hour";
                axisY = "Quantity [MWh]";
                if (selectIndex[0] != 0 && selectIndex.length == 1) {
                    String[] legend = {buyers_List.get(selectIndex[0] - 1).getAgent() + " - Power", buyers_List.get(selectIndex[0] - 1).getAgent() + " - Tradded Power"};
                    for (int h = 0; h < HOUR_PER_DAY; h++) {
                        supplier_Sym[h][0] = Double.parseDouble(buyerPriceSensitiveData_Hour[h][selectIndex[0] - 1][2].toString().replace(",", "."));
                        supplier_Sym[h][1] = Double.parseDouble(buyerPriceSensitiveData_Hour[h][selectIndex[0] - 1][3].toString().replace(",", "."));
                    }
                    if (jTabbedPaneOutput.getTabCount() != 1) {
                        jTabbedPaneOutput.remove(1);
                    }
                    jTabbedPaneOutput.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend, supplier_Sym, title, axisX, axisY));
                    jTabbedPaneOutput.setSelectedIndex(1);
                } else {
                    createLineChartOutput_Supplier(selectIndex, buyerData_Hour, title, axisX, axisY, buyerPriceSensitiveData_Hour);
                }
                break;
            case 4:
                printHour += 1;
                jComboBoxOutputHour.setEnabled(true);
                hour = jComboBoxOutputHour.getSelectedIndex();
                title = "Supply and Demand [h =" + printHour + "]";
                axisX = "Quantity [MWh]";
                axisY = "Price per hour [€/MWh]";
                double[][] SMP_Values = new double[HOUR_PER_DAY][1];
                if(jComboBoxOutputHour.getSelectedIndex()==0){
                
                String[] legend = {"SMP - Symmetrical Pool"};
                for (int h = 0; h < HOUR_PER_DAY; h++) {
                    SMP_Values[h][0] = Double.parseDouble(SMP_Data_Day[h][1].toString().replace(",", "."));
                }
                if (jTabbedPaneOutput.getTabCount() != 1) {
                    jTabbedPaneOutput.remove(1);
                }
                jTabbedPaneOutput.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend, SMP_Values, title, axisX, axisY));
                jTabbedPaneOutput.setSelectedIndex(1);
                
                }else{
                    createDemandVsSuplly(selectIndex, buyerData_Hour, title, axisX, axisY); 
                    }
            break;
            default:
                title = "System Marginal Price [€/MWh]";
                axisX = "Hour";
                axisY = "Price [€/MWh]";
                SMP_Values = new double[HOUR_PER_DAY][1];
                String[] legend1 = {"SMP - Symmetrical Pool"};
                for (int h = 0; h < HOUR_PER_DAY; h++) {
                    SMP_Values[h][0] = Double.parseDouble(SMP_Data_Day[h][1].toString().replace(",", "."));
                }
                if (jTabbedPaneOutput.getTabCount() != 1) {
                    jTabbedPaneOutput.remove(1);
                }
                jTabbedPaneOutput.add("Graphical [Output Chart Information]", ChartGenerator.drawLineGraph(legend1, SMP_Values, title, axisX, axisY));
                jTabbedPaneOutput.setSelectedIndex(1);
                break;
        }
    }//GEN-LAST:event_jButtonChartOutputActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonChartOutput;
    private javax.swing.JButton jButton_showOutputData;
    private javax.swing.JComboBox jComboBoxOutputHour;
    private javax.swing.JComboBox jComboBox_Output_Generator;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList_Data_Generator_Output;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPaneOutput;
    private javax.swing.JTable jTable_OutputData;
    // End of variables declaration//GEN-END:variables
}
