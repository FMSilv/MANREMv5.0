package wholesalemarket_LMP.simul;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import marketoperator.*;
import wholesalemarket_LMP.Wholesale_InputData;
import wholesalemarket_LMP.results.WholesaleShowResults;
import wholesalemarket_LMP.ReadExcel;

public class WholesaleMarket extends MarketOperator {
    
    public static int START_HOUR = 0;
    public static int END_HOUR = 23;
    public static int HOUR_PER_DAY = 24;

    private ArrayList<GridData> GRID_DataList;
    private GridData GRID_Agent;

    private ArrayList<ProducerData> PRODUCER_DataList;
    private ProducerData PRODUCER_Agent;

    private ArrayList<SupplierData> SUPPLIER_DataList;
    private SupplierData SUPPLIER_Agent;

    private Wholesale_InputData inputData;
    private TransmissionGridFormulation grid_formulation;
    private ISO iso;

    private int grid_TotalBus;
    private int grid_TotalBranches;
    private int grid_ID;
    private double grid_BranchForm;
    private double grid_BranchTo;
    private double grid_MaxCap;
    private double grid_React;

    private int totalProducers;
    private int producer_ID;
    private double producer_atBus;
    private double producer_StartCost;
    private double producer_SlopeCost;
    private double producer_CapLower;
    private double producer_CapMax;
    private boolean exist_Producer_ID;

    private int totalSuppliers;
    private int supplier_ID;
    private double supplier_atBus;
    private double[] supplier_StartCost;
    private double[] supplier_SlopeCost;
    private double[] supplier_minDemand;
    private double[] supplier_maxDemand;
    private boolean exist_Supplier_ID;

    public static final String Default_Case1 = "files/WholesaleInputData/Default_Case1.xls";
    public static final String Default_Case = "files/WholesaleInputData/Default_Case.xls";

    private static final int SUPPLIER_EXCEL_COLUMNS = 5;
    private static final int PRODUCER_EXCEL_COLUMNS = 6;
    private static final int BRANCH_EXCEL_COLUMNS = 4;
    private static final int GRID_EXCEL_COLUMNS = 4;

    private ArrayList<double[][]> producerAgent_CommitmentWithTrueCost;
    private ArrayList<double[][]> LMPWithTrueCost;
    private ArrayList<double[][]> supplierAgent_PriceSensitiveDemandWithTrueCost;
    private ArrayList<double[][]> grid_BranchPowerFlow;
    
    private GridData gridDataClass;
    private ProducerData producerDataClass;
    private SupplierData SupplierDataClass;

    /**
     * Constructor for the Excel Data
     * @param isDefaultCase 
     */
    public WholesaleMarket(boolean isDefaultCase) {
        PRODUCER_DataList = new ArrayList<>();
        SUPPLIER_DataList = new ArrayList<>();
        GRID_DataList = new ArrayList<>();
        producerAgent_CommitmentWithTrueCost = new ArrayList<>();
        LMPWithTrueCost = new ArrayList<>();
        supplierAgent_PriceSensitiveDemandWithTrueCost = new ArrayList<>();
        grid_BranchPowerFlow = new ArrayList<>();

        if (isDefaultCase) {
            studyCaseExamples(Default_Case);
        } else {
            studyCaseExamples(Default_Case1);
        }
    }

    /**
     * Constructor for the input data
     * @param _auxmarket 
     */
    public WholesaleMarket(Wholesale_InputData _auxmarket) {
        inputData = _auxmarket;
        GRID_DataList = inputData.getGRID_List();
        PRODUCER_DataList = inputData.getPRODUCER_List();
        SUPPLIER_DataList = inputData.getSUPPLIER_List();
        grid_TotalBus = (int) GRID_DataList.get(0).getBusNr();
        grid_TotalBranches = (int) GRID_DataList.get(0).getBranchesNr();
        totalProducers = (int) PRODUCER_DataList.size();
        totalSuppliers = (int) SUPPLIER_DataList.size();
        producerAgent_CommitmentWithTrueCost = new ArrayList<>();
        LMPWithTrueCost = new ArrayList<>();
        supplierAgent_PriceSensitiveDemandWithTrueCost = new ArrayList<>();
        grid_BranchPowerFlow = new ArrayList<>();
    }
    
    /**
     * Read the Default Case from the Excel File and save the data
     * @param _Case : String with the Excel file directory
     */
    public void studyCaseExamples(String _Case) {
        double[][] grid_excelDATA = ReadExcel.readExcelData(_Case, "GRID_DATA", 1, GRID_EXCEL_COLUMNS, false);
        grid_TotalBus = (int) grid_excelDATA[0][0];
        if (grid_TotalBus != 0) {
            grid_TotalBranches = (int) grid_excelDATA[0][1];
            totalProducers = (int) grid_excelDATA[0][2];
            totalSuppliers = (int) grid_excelDATA[0][3];

            double[][] branch_excelDATA = ReadExcel.readExcelData(_Case, "BRANCH_DATA", grid_TotalBranches, BRANCH_EXCEL_COLUMNS, false);
            for (int b = 0; b < grid_TotalBranches; b++) {
                int indexName = b + 1;
                gridDataClass = new GridData("Branch" + indexName, indexName, grid_TotalBranches, grid_TotalBus,
                        branch_excelDATA[b][0], branch_excelDATA[b][1], branch_excelDATA[b][2], branch_excelDATA[b][3]);
                GRID_DataList.add(gridDataClass);
            }
            for (int p = 0; p < totalProducers; p++) {
                int indexName = p + 1;
                double[][] producer_excelDATA = ReadExcel.readExcelData(_Case, "PRODUCER" + indexName, HOUR_PER_DAY, PRODUCER_EXCEL_COLUMNS, true);
                double[][] auxMatrix = new double[PRODUCER_EXCEL_COLUMNS - 1][HOUR_PER_DAY];
                for(int i = 0; i < PRODUCER_EXCEL_COLUMNS - 1; i++) {
                    for (int h = 0; h < HOUR_PER_DAY; h++) {
                        auxMatrix[i][h] = producer_excelDATA[h][i];
                    }
                }
                
                producerDataClass = new ProducerData("Producer" + indexName, (int) producer_excelDATA[0][4], (int) producer_excelDATA[0][5], auxMatrix[2],
                        auxMatrix[3], auxMatrix[0], auxMatrix[1]);
                PRODUCER_DataList.add(producerDataClass);
            }
            for (int s = 0; s < totalSuppliers; s++) {
                int indexName = s + 1;
                double[][] supplier_excelDATA = ReadExcel.readExcelData(_Case, "SUPPLIER" + indexName, HOUR_PER_DAY, SUPPLIER_EXCEL_COLUMNS, true);
                double[][] auxMatrix = new double[SUPPLIER_EXCEL_COLUMNS - 1][HOUR_PER_DAY];
                for (int i = 0; i < SUPPLIER_EXCEL_COLUMNS - 1; i++) {
                    for (int h = 0; h < HOUR_PER_DAY; h++) {
                        auxMatrix[i][h] = supplier_excelDATA[h][i];
                    }
                }
                
                SupplierDataClass = new SupplierData("Supplier" + indexName, indexName, supplier_excelDATA[0][4], auxMatrix[1], auxMatrix[2],
                        auxMatrix[0], auxMatrix[3]);
                SUPPLIER_DataList.add(SupplierDataClass);
            }
        }
    }

    /**
     * Read Agent messages
     * @param content : String with all Grid, Producer and Supplier Info
     */
    
    /**
     * private void receiveGridContent(String content) {
        if (content.contains("_grid;")) {
            String[] content_information = content.split(";");
            grid_TotalBus = Integer.parseInt(content_information[1]);
            grid_TotalBranches = Integer.parseInt(content_information[2]);

            for (int i = 3; i < grid_TotalBranches + 3; i++) {
                grid_ID = Integer.parseInt(content_information[i]);
                grid_BranchForm = Double.parseDouble(content_information[i + grid_TotalBranches]);
                grid_BranchTo = Double.parseDouble(content_information[i + 2 * grid_TotalBranches]);
                grid_MaxCap = Double.parseDouble(content_information[i + 3 * grid_TotalBranches]);
                grid_React = Double.parseDouble(content_information[i + 4 * grid_TotalBranches]);

                GRID_Agent = new GridData(null, grid_ID, grid_TotalBranches,
                        grid_TotalBus, grid_BranchForm, grid_BranchTo, grid_MaxCap, grid_React);

                GRID_DataList.add(GRID_Agent);
            }
        }
    }
    * private void receiveProducerContent(String content) {
        if (content.contains("_producer;")) {
            String[] content_information = content.split(";");
            exist_Producer_ID = false;
            for (ProducerData PRODUCER1 : PRODUCER_DataList) {
                if (PRODUCER1.getProducerID() == Integer.parseInt(content_information[1])) {
                    exist_Producer_ID = true;
                    break;
                }
            }

            if (!exist_Producer_ID) {
                producer_ID = Integer.parseInt(content_information[1]);
                producer_atBus = Double.parseDouble(content_information[2]);
                producer_StartCost = Double.parseDouble(content_information[3]);
                producer_SlopeCost = Double.parseDouble(content_information[4]);
                producer_CapLower = Double.parseDouble(content_information[5]);
                producer_CapMax = Double.parseDouble(content_information[6]);

                PRODUCER_Agent = new ProducerData(null, producer_ID, producer_atBus,
                        producer_StartCost, producer_SlopeCost, producer_CapLower, producer_CapMax);

                PRODUCER_DataList.add(PRODUCER_Agent);
            }

            totalProducers = PRODUCER_DataList.size();
        }
    }
    * private void receiveSupplierContent(String content) {
        if (content.contains("_supplier;")) {
            String[] content_information = content.split(";");
            exist_Supplier_ID = false;
            for (SupplierData SUPPLIER1 : SUPPLIER_DataList) {
                if (SUPPLIER1.getSupplierID() == Integer.parseInt(content_information[1])) {
                    exist_Supplier_ID = true;
                    break;
                }
            }

            if (!exist_Supplier_ID) {
                supplier_ID = Integer.parseInt(content_information[1]);
                supplier_atBus = Integer.parseInt(content_information[2]);

                supplier_StartCost = new double[HOUR_PER_DAY];
                supplier_SlopeCost = new double[HOUR_PER_DAY];
                supplier_minDemand = new double[HOUR_PER_DAY];
                supplier_maxDemand = new double[HOUR_PER_DAY];

                for (int i = 0; i < HOUR_PER_DAY; i++) {
                    int j = i + 3;
                    supplier_StartCost[i] = Double.parseDouble(content_information[j]);
                    supplier_SlopeCost[i] = Double.parseDouble(content_information[j + HOUR_PER_DAY]);
                    supplier_minDemand[i] = Double.parseDouble(content_information[j + 2 * HOUR_PER_DAY]);
                    supplier_maxDemand[i] = Double.parseDouble(content_information[j + 3 * HOUR_PER_DAY]);
                }

                SUPPLIER_Agent = new SupplierData(null, supplier_ID, supplier_atBus, supplier_StartCost, supplier_SlopeCost, supplier_minDemand, supplier_maxDemand);

                SUPPLIER_DataList.add(SUPPLIER_Agent);
            }
            totalSuppliers = SUPPLIER_DataList.size();
        }
    }*/

    public void startSimulation() {
        String simulationProblem;
        if (!SUPPLIER_DataList.isEmpty()) {
            grid_formulation = new TransmissionGridFormulation(this);
            iso = new ISO(this);
            simulationProblem = iso.computeCompetitiveEquilibriumResults();
            if (simulationProblem.isEmpty()) {
                WholesaleShowResults aux = new WholesaleShowResults(this);
                aux.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,simulationProblem,
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public ArrayList<GridData> getGridData() {
        return GRID_DataList;
    }

    public ArrayList<ProducerData> getProducersData() {
        return PRODUCER_DataList;
    }

    public ArrayList<SupplierData> getSupplierData() {
        return SUPPLIER_DataList;
    }

    public int getGridTotalBus() {
        return grid_TotalBus;
    }

    public int getGridTotalBranches() {
        return grid_TotalBranches;
    }

    public int getNrProducers() {
        return totalProducers;
    }

    public int getNrSuppliers() {
        return totalSuppliers;
    }

    public void addProducerAgent_CommitmentWithTrueCost(double[][] object) {
        producerAgent_CommitmentWithTrueCost.add(object);
    }

    public ArrayList<double[][]> getProducerAgent_CommitmentWithTrueCost() {
        return producerAgent_CommitmentWithTrueCost;
    }

    public void addLMPWithTrueCost(double[][] object) {
        LMPWithTrueCost.add(object);
    }

    public ArrayList<double[][]> getLMPWithTrueCost() {
        return LMPWithTrueCost;
    }

    public void addSupplierAgent_PriceSensitiveDemandWithTrueCost(double[][] object) {
        supplierAgent_PriceSensitiveDemandWithTrueCost.add(object);
    }
    
    public void addBranchPowerFlow(double[][] object) {
        grid_BranchPowerFlow.add(object);
    }
    
    public ArrayList<double[][]> getGridBranchPowerFlow() {
        return grid_BranchPowerFlow;
    }

    public ArrayList<double[][]> getSupplierAgent_PriceSensitiveDemandWithTrueCost() {
        return supplierAgent_PriceSensitiveDemandWithTrueCost;
    }

    public TransmissionGridFormulation getTransmissionGridFormulation() {
        return grid_formulation;
    }
}
