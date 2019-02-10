package wholesalemarket_LMP.simul;

import wholesalemarket_LMP.Wholesale_InputData;

public class GridData {

    private String name;
    private int branch_ID;
    private double total_Branches;
    private double total_Bus;
    private double startBus;
    private double endBus;
    private double maxCapacity;
    private double reactance;
    private final double maxCapacity_pu;
    private final double reactance_pu;
    private double voltageBase;
    private double sPowerBase;

    public static final int STARTBUS = 0;
    public static final int ENDBUS = 1;
    public static final int MAX_CAPACITY = 2;
    public static final int REACTANCE = 3;
    public static final double PENALTY_COEFF = 0.05;

    public GridData(String _name, int _branchID, double _branchesNr, double _busNr,
            double _startBus, double _endBus, double _maxCapacity, double _reactance) {
        name = _name;
        branch_ID = _branchID;
        total_Branches = _branchesNr;
        total_Bus = _busNr;
        startBus = _startBus;
        endBus = _endBus;
        maxCapacity = _maxCapacity;
        reactance = _reactance;

        voltageBase = Wholesale_InputData.getVoltageBase();
        sPowerBase = Wholesale_InputData.getSPowerBase();

        // Convert MaxCap from SI to PU
        maxCapacity_pu = RoundingValues.correctRounding(maxCapacity / sPowerBase);
        // Convert reactance from SI to PU, x(pu) = x/Zo = x/(Vo^2/So) = (x*So)/Vo^2
        reactance_pu = RoundingValues.correctRounding((reactance * sPowerBase) / (voltageBase * voltageBase));
    }

    public String getName() {
        return name;
    }

    public int getBranchID() {
        return branch_ID;
    }

    public double getBranchesNr() {
        return total_Branches;
    }

    public double getBusNr() {
        return total_Bus;
    }

    public double getStartBus() {
        return startBus;
    }

    public double getEndBus() {
        return endBus;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public double getMaxCapacity_pu() {

        return maxCapacity_pu;
    }

    public double getLosses() {
        return reactance;
    }

    public double getLosses_pu() {
        return reactance_pu;
    }

    public double getVoltageBase() {
        return voltageBase;
    }

    public double getSPowerBase() {
        return sPowerBase;
    }

    public void setName(String _name) {
        name = _name;
    }

    public void setBranchesNr(double _branchesNr) {
        total_Branches = _branchesNr;
    }

    public void setBusNr(double _busNr) {
        total_Bus = _busNr;
    }

    public void setStartBus(double _startBus) {
        startBus = _startBus;
    }

    public void setEndBus(double _endBus) {
        endBus = _endBus;
    }

    public void setMaxCapacity(double _maxCapacity) {
        maxCapacity = _maxCapacity;
    }

    public void setLosses(double _losses) {
        reactance = _losses;
    }
}
