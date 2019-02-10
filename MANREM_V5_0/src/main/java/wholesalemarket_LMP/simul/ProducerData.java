package wholesalemarket_LMP.simul;

import wholesalemarket_LMP.Wholesale_InputData;
import wholesalemarket_LMP.simul.RoundingValues;

public class ProducerData {

    private static final int COST_CAPACITY_PARAM = 4;
    private static final int HOURS_PER_DAY = WholesaleMarket.HOUR_PER_DAY;

    private String name;
    private double atBus;
    private double[] startCost;
    private double[] slopeCost;
    private double[] minCapacity;
    private double[] maxCapacity;
    private double voltageBase;
    private double sPowerBase;
    private double[] startCost_pu;
    private double[] slopeCost_pu;
    private double[] minCap_pu;
    private double[] maxCap_pu;
    private int producer_ID;
    private boolean info_Completed;

    private static final double priceCap = 1000.0;  // max price for LMP
    private double[][] trueProducerOffer;
    private double[] commitment;
    private double[] dayAheadLMP;

    private double[] hourlyVariableCost;// hourlyVariableCost[h] = a*power + b*power^2
    private double[] hourlyNetEarning;// hourlyNetEarning[h] = dispatch[h]*lmp[h]
    private double[] hourlyProfit;// hourlyProfit[h] = dispatch[h]*lmp[h] - hourlyTotalCost[h]
    private double dailyNetEarnings;   // dailyNetEarnings = sum of hourlyNetEarning over 24 hours
    private double dailyProfit;   // dailyProfit = sum of hourlyProfit over 24 hours
    private double[] hourlyRevenue;
    private double dailyRevenue;
    private double[] hourlyTotalCost;          // hourlyTotalCost = totalVariableCost + FCost
    private double fixedCost;  // GenCo's fixed cost

    public ProducerData(String _name, int _ID, double[] _price, double[] _power) {
        name = _name;
        producer_ID = _ID;
        startCost = _price;
        maxCapacity = _power;
        info_Completed = true;
    }
            
    public ProducerData(String _name, int _ID, double _atBus,
            double[] _startCost, double[] _slopeCost, double[] _minCap, double[] _maxCap) {
        name = _name;
        producer_ID = _ID;
        atBus = _atBus;
        startCost = _startCost;
        slopeCost = _slopeCost;
        minCapacity = _minCap;
        maxCapacity = _maxCap;

        fixedCost = 0.0;

        voltageBase = Wholesale_InputData.getVoltageBase();
        sPowerBase = Wholesale_InputData.getSPowerBase();

        startCost_pu = new double[startCost.length];
        slopeCost_pu = new double[slopeCost.length];
        minCap_pu = new double[minCapacity.length];
        maxCap_pu = new double[maxCapacity.length];

        for (int h = 0; h < WholesaleMarket.HOUR_PER_DAY; h++) {
            startCost_pu[h] = RoundingValues.correctRounding(startCost[h] * sPowerBase);
            slopeCost_pu[h] = RoundingValues.correctRounding(slopeCost[h] * sPowerBase * sPowerBase);
            minCap_pu[h] = RoundingValues.correctRounding(minCapacity[h] / sPowerBase);
            maxCap_pu[h] = RoundingValues.correctRounding(maxCapacity[h] / sPowerBase);
        }

        // trueProducerOffer[][] = {hour},{StartCost, SlopeCost, MinCap, MaxCap}
        trueProducerOffer = new double[WholesaleMarket.HOUR_PER_DAY][COST_CAPACITY_PARAM];
        for (int h = 0; h < WholesaleMarket.HOUR_PER_DAY; h++) {
            trueProducerOffer[h][0] = startCost[h];
            trueProducerOffer[h][1] = slopeCost[h];
            trueProducerOffer[h][2] = minCapacity[h];
            trueProducerOffer[h][3] = maxCapacity[h];
        }

        commitment = new double[HOURS_PER_DAY];
        dayAheadLMP = new double[HOURS_PER_DAY];

        hourlyTotalCost = new double[HOURS_PER_DAY];
        hourlyProfit = new double[HOURS_PER_DAY];
        hourlyNetEarning = new double[HOURS_PER_DAY];
        hourlyVariableCost = new double[HOURS_PER_DAY];
        hourlyRevenue = new double[HOURS_PER_DAY];
    }

    public double[][] submitTrueSupplyOffer() {
        double[][] trueOffer = new double[WholesaleMarket.HOUR_PER_DAY][4];
        // for PriceCap
        for (int h = 0; h < WholesaleMarket.HOUR_PER_DAY; h++) {
            double maxPrice = trueProducerOffer[h][0] + 2 * trueProducerOffer[h][1] * trueProducerOffer[h][3];

            trueOffer[h][0] = trueProducerOffer[h][0];
            trueOffer[h][1] = trueProducerOffer[h][1];
            trueOffer[h][2] = trueProducerOffer[h][2];

            if (priceCap < trueOffer[h][0]) {
                trueOffer[h][2] = 0;
                trueOffer[h][3] = 0;
            } else if (maxPrice <= priceCap) {
                trueOffer[h][3] = trueProducerOffer[h][3];
            } else {
                trueOffer[h][3] = (priceCap - trueProducerOffer[h][0]) / (2 * trueProducerOffer[h][1]);
            }
        }
        return trueOffer;
    }

    public void updateProfit() {
        dailyProfit = 0;
        dailyNetEarnings = 0;
        dailyRevenue = 0;

        for (int h = 0; h < HOURS_PER_DAY; h++) {
            hourlyVariableCost[h] = startCost[h] * commitment[h] + slopeCost[h] * commitment[h] * commitment[h];
            hourlyTotalCost[h] = hourlyVariableCost[h] + fixedCost;
            hourlyRevenue[h] = commitment[h] * dayAheadLMP[h];

            hourlyProfit[h] = hourlyRevenue[h] - hourlyTotalCost[h];
            hourlyNetEarning[h] = hourlyRevenue[h] - hourlyVariableCost[h];

            dailyProfit += hourlyProfit[h];
            dailyNetEarnings += hourlyNetEarning[h];
            dailyRevenue += hourlyRevenue[h];
        }
    }

    public int getProducerID() {
        return producer_ID;
    }

    public String getName() {
        return name;
    }

    public double getAtBus() {
        return atBus;
    }

    public double getStartCost(int pos) {
        return startCost[pos];
    }

    public double getSlopeCost(int pos) {
        return slopeCost[pos];
    }

    public double getMinPot(int pos) {
        return minCapacity[pos];
    }

    public double getMaxPot(int pos) {
        return maxCapacity[pos];
    }

    public double getStartCost_pu(int pos) {
        return startCost_pu[pos];
    }

    public double getSlopeCost_pu(int pos) {
        return slopeCost_pu[pos];
    }

    public double getMinPot_pu(int pos) {
        return minCap_pu[pos];
    }

    public double getMaxPot_pu(int pos) {
        return maxCap_pu[pos];
    }

    public double[] getCommitment() {
        return commitment;
    }

    public void setCommitment(double[] comm) {
        commitment = comm;
    }
    
    public void setConfirmation(boolean status) {
        info_Completed = status;
    }
    
    public boolean getConfirmation() {
        return info_Completed;
    }

    public double[] getDayAheadLMP() {
        return dayAheadLMP;
    }

    public void setDayAheadLMP(double[] lmprice) {
        dayAheadLMP = lmprice;
    }

    public double getDailyRevenue() {
        return dailyRevenue;
    }

    public double getProfit() {
        return dailyProfit;
    }

    public double getNetEarning() {
        return dailyNetEarnings;
    }

    public double[] getHourlyRevenue() {
        return hourlyRevenue;
    }

    public double[] getHourlyProfit() {
        return hourlyProfit;
    }

    public double[] getHourlyNetEarning() {
        return hourlyNetEarning;
    }
}
