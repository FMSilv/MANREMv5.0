package wholesalemarket_LMP.simul;

import wholesalemarket_LMP.Wholesale_InputData;

public class SupplierData {

    private final String name;
    private final int supplier_ID;
    private final double atBus;
    private final double[] startCost;
    private final double[] slopeCost;
    private final double[] loadFixedDemand;
    private final double[] maxDemand;
    private final double[] loadSensitiveDemand;
    private final double[][] priceSensitiveDemand;
    private boolean info_Completed;

    private final double sPowerBase;
    private final double[] startCost_pu;
    private final double[] slopeCost_pu;
    private final double[] minDemand_pu;
    private final double[] maxDemand_pu;

    private static final int PRICE_SENSITIVE_PARAM = 3;

    public SupplierData(String _name, int _ID, double[] _price, double[] _power) {
        name = _name;
        supplier_ID = _ID;
        startCost = _price;
        maxDemand = _power;
        info_Completed = true;
        atBus = -1;
        slopeCost = null;
        loadFixedDemand = null;
        loadSensitiveDemand = null;
        priceSensitiveDemand = null;
        sPowerBase = 1;
        startCost_pu = null;
        slopeCost_pu = null;
        minDemand_pu = null;
        maxDemand_pu = null;
    }

    public boolean isInfo_Completed() {
        return info_Completed;
    }

    public void setInfo_Completed(boolean info_Completed) {
        this.info_Completed = info_Completed;
    }
    
    public SupplierData(String _name, int _supplierID, double _atBus,
            double[] _startCost, double[] _slopeCost, double[] _minDemand,
            double[] _maxDemand) {
        name = _name;
        supplier_ID = _supplierID;
        atBus = _atBus;
        startCost = _startCost;
        slopeCost = _slopeCost;
        loadFixedDemand = _minDemand;
        maxDemand = _maxDemand;
        
        sPowerBase = Wholesale_InputData.getSPowerBase();

        loadSensitiveDemand = new double[WholesaleMarket.HOUR_PER_DAY];
        priceSensitiveDemand = new double[WholesaleMarket.HOUR_PER_DAY][PRICE_SENSITIVE_PARAM];

        for (int h = 0; h < WholesaleMarket.HOUR_PER_DAY; h++) {
            loadSensitiveDemand[h] = maxDemand[h] - loadFixedDemand[h];
            priceSensitiveDemand[h][0] = startCost[h];
            priceSensitiveDemand[h][1] = slopeCost[h];
            priceSensitiveDemand[h][2] = loadSensitiveDemand[h];
        }

        startCost_pu = new double[startCost.length];
        slopeCost_pu = new double[slopeCost.length];
        minDemand_pu = new double[loadFixedDemand.length];
        maxDemand_pu = new double[maxDemand.length];

        for (int i = 0; i < startCost.length; i++) {
            startCost_pu[i] = RoundingValues.correctRounding(startCost[i] * sPowerBase);
            slopeCost_pu[i] = RoundingValues.correctRounding(slopeCost[i] * sPowerBase * sPowerBase);
            minDemand_pu[i] = RoundingValues.correctRounding(loadFixedDemand[i] / sPowerBase);
            maxDemand_pu[i] = RoundingValues.correctRounding(maxDemand[i] / sPowerBase);
        }
    }

    public String getName() {
        return name;
    }

    public int getSupplierID() {
        return supplier_ID;
    }

    public double getAtBus() {
        return atBus;
    }

    public double[] getStartCost() {
        return startCost;
    }

    public double getStartCost(int hour) {
        return startCost[hour];
    }

    public double[] getSlopeCost() {
        return slopeCost;
    }

    public double getSlopeCost(int hour) {
        return slopeCost[hour];
    }

    public double[] getLoadFixedDemand() {
        return loadFixedDemand;
    }

    public double getLoadFixedDemand(int hour) {
        return loadFixedDemand[hour];
    }

    public double[] getMaxDemand() {
        return maxDemand;
    }

    public double getMaxDemand(int hour) {
        return maxDemand[hour];
    }

    public double[][] getPriceSensitiveDemand() {
        return priceSensitiveDemand;
      
    }

    public double[] getPriceSnsitiveDemand(int hour) {
        return priceSensitiveDemand[hour];
    }

    public double[] getStartCost_pu() {
        return startCost_pu;
    }

    public double getStartCost_pu(int hour) {
        return startCost_pu[hour];
    }

    public double[] getSlopeCost_pu() {
        return slopeCost_pu;
    }

    public double getSlopeCost_pu(int hour) {
        return slopeCost_pu[hour];
    }

    public double[] getMinDemand_pu() {
        return minDemand_pu;
    }

    public double getMinDemand_pu(int hour) {
        return minDemand_pu[hour];
    }

    public double[] getMaxDemand_pu() {
        return maxDemand_pu;
    }

    public double getMaxDemand_pu(int hour) {
        return maxDemand_pu[hour];
    }
}
