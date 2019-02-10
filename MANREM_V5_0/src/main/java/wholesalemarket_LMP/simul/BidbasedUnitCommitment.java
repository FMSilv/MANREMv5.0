package wholesalemarket_LMP.simul;

import wholesalemarket_LMP.Wholesale_InputData;

public class BidbasedUnitCommitment {

    private int totalBus;
    private int totalBranches;
    private int totalProducers;
    private int totalSuppliers;
    private boolean priceSensitive = true;

    private double[][] dailyCommitment;
    private double[][] dailyVoltAngle;
    private double[][] dailyLMP;
    private double[] dailyMinTVC;
    private double[][] dailyBranchFlow;
    private double[][] dailyPriceSensitiveDemand;
    private boolean[] bDCOPFHasSolution;

    private WholesaleMarket mainMarket;
    private ISO iso;
    private DCOPFJ dcopf;

    // Index for producersOffer parameters, i.e., in the form of {start,slope,CapMin,CapMax}
    private static final int PRODUCER_START_INDEX = 0;
    private static final int PRODUCER_SLOPE_INDEX = 1;
    private static final int PRODUCER_CAP_MIN = 2;
    private static final int PRODUCER_CAP_MAX = 3;
    private double[][][] producersOffer;

    // Index for supplierPriceSensitiveOffer parameters, i.e., in the form of {start,slope,DemandMax}
    private static final int SUPPLIER_START_INDEX = 0;
    private static final int SUPPLIER_SLOPE_INDEX = 1;
    private static final int SUPPLIER_DEMAND_MAX = 2;

    private double[][] supplierPriceSensitiveOffer;

    public BidbasedUnitCommitment(ISO _iso, WholesaleMarket _mainMarket) {
        mainMarket = _mainMarket;
        iso = _iso;

        totalBus = mainMarket.getGridTotalBus();
        totalBranches = mainMarket.getGridTotalBranches();
        totalProducers = mainMarket.getNrProducers();
        totalSuppliers = mainMarket.getNrSuppliers();

        dailyCommitment = new double[WholesaleMarket.HOUR_PER_DAY][totalProducers];
        dailyVoltAngle = new double[WholesaleMarket.HOUR_PER_DAY][totalBus];
        dailyLMP = new double[WholesaleMarket.HOUR_PER_DAY][totalBus];
        dailyMinTVC = new double[WholesaleMarket.HOUR_PER_DAY];
        dailyBranchFlow = new double[WholesaleMarket.HOUR_PER_DAY][totalBranches];
        dailyPriceSensitiveDemand = new double[WholesaleMarket.HOUR_PER_DAY][totalSuppliers];

        bDCOPFHasSolution = new boolean[24];
    }

    public String solveOPF() {
        producersOffer = iso.getSupplyOfferByGen();
        String warning = "";
        double totalMinProducerCapacity = 0.0;
        double totalMaxProducerCapacity = 0.0;
        double[] producers_location = new double[totalProducers];
        for (int i = 0; i < totalProducers; i++) {
            producers_location[i] = mainMarket.getProducersData().get(i).getAtBus();
        }

        double[] suppliers_location = new double[totalSuppliers];
        for (int j = 0; j < totalSuppliers; j++) {
            suppliers_location[j] = mainMarket.getSupplierData().get(j).getAtBus();
        }

        double[][][] supplier_PriceSensitiveOffer = iso.getDemandBidByLSE();
        int totalRow = supplier_PriceSensitiveOffer.length;
        int totalColumn = supplier_PriceSensitiveOffer[0][0].length;

        double[] auxLoad = new double[24]; // Total Demand
        for (int h = 0; h < WholesaleMarket.HOUR_PER_DAY; h++) {
            double[][] producers_per_hour = new double[totalProducers][producersOffer[0][0].length];
            // SI to PU conversion for supply offer and load profile
            for(int i = 0; i < producersOffer.length; i++){
                totalMinProducerCapacity += producersOffer[i][h][PRODUCER_CAP_MIN];
                totalMaxProducerCapacity += producersOffer[i][h][PRODUCER_CAP_MAX];
                
                // Convert Start_Price from SI to PU-adjusted
                producers_per_hour[i][PRODUCER_START_INDEX] = producersOffer[i][h][PRODUCER_START_INDEX] * Wholesale_InputData.getSPowerBase();
                // Convert Slope_Price from SI to PU-adjusted
                producers_per_hour[i][PRODUCER_SLOPE_INDEX] = producersOffer[i][h][PRODUCER_SLOPE_INDEX] * Wholesale_InputData.getSPowerBase() * Wholesale_InputData.getSPowerBase();
                // Convert CapMin from SI to PU
                producers_per_hour[i][PRODUCER_CAP_MIN] = producersOffer[i][h][PRODUCER_CAP_MIN] / Wholesale_InputData.getSPowerBase();
                // Convert CapMax from SI to PU
                producers_per_hour[i][PRODUCER_CAP_MAX] = producersOffer[i][h][PRODUCER_CAP_MAX] / Wholesale_InputData.getSPowerBase();
            }
            producers_per_hour = RoundingValues.correctRounding(producers_per_hour);

            //NOTE: phaseAngle is assumed to be zero at first bus, i.e. phaseAngle[0]=0
            double[] supplier_HourlyLoadProfile = new double[totalSuppliers];

            auxLoad[h] = 0.0;
            for (int j = 0; j < totalSuppliers; j++) {
                supplier_HourlyLoadProfile[j] = iso.getLoadProfileByLSE()[j][h];
                // Calculate total demand
                auxLoad[h] += supplier_HourlyLoadProfile[j];
            }

            supplierPriceSensitiveOffer = new double[totalRow][totalColumn];
            for (int i = 0; i < totalRow; i++) {
                System.arraycopy(supplier_PriceSensitiveOffer[i][h], 0, supplierPriceSensitiveOffer[i], 0, totalColumn);
            }

            // SI to PU conversion for price sensitive demand
            for (double[] supplierPriceSensitiveOffer1 : supplierPriceSensitiveOffer) {
                // Convert Start Price from SI to PU-adjusted
                supplierPriceSensitiveOffer1[SUPPLIER_START_INDEX] = supplierPriceSensitiveOffer1[SUPPLIER_START_INDEX] * Wholesale_InputData.getSPowerBase();
                // Convert Slope Price from SI to PU-adjusted
                supplierPriceSensitiveOffer1[SUPPLIER_SLOPE_INDEX] = supplierPriceSensitiveOffer1[SUPPLIER_SLOPE_INDEX] * Wholesale_InputData.getSPowerBase() * Wholesale_InputData.getSPowerBase();
                // Convert DemandMax from SI to PU
                supplierPriceSensitiveOffer1[SUPPLIER_DEMAND_MAX] = supplierPriceSensitiveOffer1[SUPPLIER_DEMAND_MAX] / Wholesale_InputData.getSPowerBase();
            }

            boolean checkProducer_MinMax_CapacityOK = true;
            if (totalMinProducerCapacity > auxLoad[h]) {
                warning += "Producer total min. capacity is greater than total fixed demand at hour " + h + " \n";
                checkProducer_MinMax_CapacityOK = false;
            }

            if (totalMaxProducerCapacity < auxLoad[h]) {
                warning += "Producer total máx. capacity is less than total fixed demand at hour " + h + "\n";
                checkProducer_MinMax_CapacityOK = false;
            }

            if (checkProducer_MinMax_CapacityOK) {
                for (int j = 0; j < totalSuppliers; j++) {
                    // Convert hourly LP from SI to PU
                    supplier_HourlyLoadProfile[j] = supplier_HourlyLoadProfile[j] / Wholesale_InputData.getSPowerBase();
                }

                supplier_HourlyLoadProfile = RoundingValues.correctRounding(supplier_HourlyLoadProfile);
                
                dcopf = new DCOPFJ(producers_per_hour, producers_location, supplierPriceSensitiveOffer,
                        supplier_HourlyLoadProfile, suppliers_location, mainMarket.getTransmissionGridFormulation(), priceSensitive);

                bDCOPFHasSolution[h] = dcopf.getIsSolutionFeasibleAndOptimal();
                dailyCommitment[h] = dcopf.getCommitment();
                dailyVoltAngle[h] = dcopf.getVoltAngle();
                dailyLMP[h] = dcopf.getLMP();
                dailyMinTVC[h] = dcopf.getMinTVC();
                dailyBranchFlow[h] = dcopf.getBranchFlow();
                dailyPriceSensitiveDemand[h] = dcopf.getSLoad();
            } else {
                bDCOPFHasSolution[h] = false;
                double[] commitment = new double[totalProducers];   // in MWs
                double[] voltAngle = new double[totalBus - 1];  // in radians
                double[] lmp = new double[totalBus];
                double[] branchFlow = new double[totalBranches]; //in MWs
                double[] psLoad = new double[totalSuppliers]; //in MWs

                dailyCommitment[h] = commitment;
                dailyVoltAngle[h] = voltAngle;
                dailyLMP[h] = lmp;
                dailyMinTVC[h] = 0.0;
                dailyBranchFlow[h] = branchFlow;
                dailyPriceSensitiveDemand[h] = psLoad;
            }
        }
        if (warning.isEmpty()) {
            for (int h = 0; h < WholesaleMarket.HOUR_PER_DAY; h++) {
                if (!bDCOPFHasSolution[h]) {
                    warning += "At hour " + h + " DCOPF has no solution!\n";
                }
            }
        }
        return warning;
    }

    /**
     * dailyCommitment: Hour-by-Node
     *
     * @return
     */
    public double[][] getDailyBranchFlow() {
        return dailyBranchFlow;
    }

    /**
     * dailyCommitment: Hour-by-Producer
     *
     * @return
     */
    public double[][] getDailyCommitment() {
        return dailyCommitment;
    }

    /**
     * dailyPriceSensitiveDemand: Hour-by-Supplier
     *
     * @return
     */
    public double[][] getDailyPriceSensitiveDemand() {
        return dailyPriceSensitiveDemand;
    }

    /**
     * dailyPhaseAngle: Hour-by-Node (excluding Node 1)
     */
    public double[][] getDailyVoltAngle() {
        return dailyVoltAngle;
    }

    /**
     * dailyLMP: Hour-by-Node
     *
     * @return
     */
    public double[][] getDailyLMP() {
        return dailyLMP;
    }

    public int[] getHasSolution() {
        int[] hasSolution = new int[WholesaleMarket.HOUR_PER_DAY];

        for (int i = 0; i < WholesaleMarket.HOUR_PER_DAY; i++) {
            if (bDCOPFHasSolution[i]) {
                hasSolution[i] = 1;
            }
        }

        return hasSolution;
    }
}
