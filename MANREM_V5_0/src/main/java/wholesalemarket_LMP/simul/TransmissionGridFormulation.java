package wholesalemarket_LMP.simul;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.math.Functions;
import java.util.ArrayList;

public class TransmissionGridFormulation {

    private WholesaleMarket mainMarket;

    private double[][] branchData;
    private double[][] branchIndex;
    private double[][] VoltAngleDif;
    private double[][] reducedVoltAngleDif;
    private double[][] negativeSusceptance;
    private double[][] busAdmittance;
    private double[][] reducedBusAdmittance;
    private double[][] diagonalAdmittance;
    private double[][] adjacency;
    private double[][] reducedAdjacency;

    private DoubleFactory2D doubleFac2D; //using Colt's methods: diagonal(), identity(), ...

    private double[] arrayMaxCap;
    private double[] arrayReactance;

    private int totalBranches;
    private int totalBus;
    private double penaltyCoeff;

    private DoubleMatrix2D branchDataCOLT;

    private ArrayList<GridData> GRID_LIST;

    private static final int START_BUS = 0;
    private static final int END_BUS = 1;
    private static final int MAX_CAP = 2;
    private static final int REACTANCE = 3;
    private static final int PARAMETERS = 4;

    public TransmissionGridFormulation(WholesaleMarket _main) {
        mainMarket = _main;

        GRID_LIST = mainMarket.getGridData();

        totalBranches = mainMarket.getGridTotalBranches();
        totalBus = mainMarket.getGridTotalBus();

        branchIndex = new double[totalBranches][2];
        branchData = new double[totalBranches][PARAMETERS];
        VoltAngleDif = new double[totalBus][totalBus];
        reducedVoltAngleDif = new double[totalBus - 1][totalBus - 1];
        arrayMaxCap = new double[totalBranches];
        negativeSusceptance = new double[totalBus][totalBus];
        arrayReactance = new double[totalBranches];
        busAdmittance = new double[totalBus][totalBus];
        reducedBusAdmittance = new double[totalBus - 1][totalBus];
        diagonalAdmittance = new double[totalBranches][totalBranches];
        adjacency = new double[totalBranches][totalBus];
        reducedAdjacency = new double[totalBranches][totalBus - 1];

        doubleFac2D = DoubleFactory2D.dense;

        penaltyCoeff = GridData.PENALTY_COEFF;

        for (int i = 0; i < GRID_LIST.size(); i++) {
            branchData[i][START_BUS] = GRID_LIST.get(i).getStartBus();
            branchData[i][END_BUS] = GRID_LIST.get(i).getEndBus();
            branchData[i][MAX_CAP] = GRID_LIST.get(i).getMaxCapacity_pu();
            branchData[i][REACTANCE] = GRID_LIST.get(i).getLosses_pu();
        }

        branchDataCOLT = new DenseDoubleMatrix2D(branchData);
        branchIndex = branchDataCOLT.viewPart(0, START_BUS, totalBranches, 2).toArray();
        arrayMaxCap = branchDataCOLT.viewColumn(MAX_CAP).toArray();
        arrayReactance = branchDataCOLT.viewColumn(REACTANCE).toArray();
        
        matrix_VoltAngleDiff();
        matrix_ReducedVoltAngleDiff();
        matrix_NegativeSusceptance();
        matrix_BusAdmittance();
        matrix_ReducedBusAdmittance();
        matrix_DiagonalAdmittance();
        matrix_Adjacency();
        matrix_ReducedAdjacency();
    }

    private void matrix_VoltAngleDiff() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 43 -> W(K)
         */
        for (int i = 0; i < totalBranches; i++) {
            VoltAngleDif[(int) branchIndex[i][0] - 1][(int) branchIndex[i][1] - 1] = -2 * penaltyCoeff;
            VoltAngleDif[(int) branchIndex[i][1] - 1][(int) branchIndex[i][0] - 1] = -2 * penaltyCoeff;
        }
        for (int i = 0; i < totalBus; i++) {
            for (int j = 0; j < totalBus; j++) {
                if (j == i) {
                    for (int k = 0; k < totalBus; k++) {
                        if (k != i) {
                            VoltAngleDif[i][j] = VoltAngleDif[i][j] - VoltAngleDif[i][k];
                        }
                    }
                }
            }
        }
        VoltAngleDif = RoundingValues.correctRounding(VoltAngleDif);
    }

    private void matrix_ReducedVoltAngleDiff() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 46 -> Wrr(K)
         */
        DoubleMatrix2D auxVoltAngleDif = new DenseDoubleMatrix2D(VoltAngleDif);
        reducedVoltAngleDif = auxVoltAngleDif.viewPart(1, 1, totalBus - 1, totalBus - 1).toArray();
    }

    private void matrix_NegativeSusceptance() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 17 -> B
         */
        for (int i = 0; i < totalBranches; i++) {
            negativeSusceptance[(int) branchIndex[i][0] - 1][(int) branchIndex[i][1] - 1]
                    = 1 / arrayReactance[i];
            negativeSusceptance[(int) branchIndex[i][1] - 1][(int) branchIndex[i][0] - 1]
                    = 1 / arrayReactance[i];
        }
    }

    private void matrix_BusAdmittance() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 51 -> B'
         */
        for (int i = 0; i < totalBus; i++) {
            for (int j = 0; j < totalBus; j++) {
                if (j == i) {
                    for (int k = 0; k < totalBus; k++) {
                        if (k != i) {
                            busAdmittance[i][j] = busAdmittance[i][j] + negativeSusceptance[i][k];
                        }
                    }
                } else {
                    busAdmittance[i][j] = -negativeSusceptance[i][j];
                }
            }
        }
    }

    private void matrix_ReducedBusAdmittance() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 52 -> B'r
         */
        DoubleMatrix2D auxBusAdmittance = new DenseDoubleMatrix2D(busAdmittance);
        reducedBusAdmittance = auxBusAdmittance.viewPart(1, 0, totalBus - 1, totalBus).toArray();
    }

    private void matrix_DiagonalAdmittance() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 56 -> D
         */
        diagonalAdmittance = doubleFac2D.diagonal(branchDataCOLT.copy().viewColumn(REACTANCE).assign(Functions.inv)).toArray();
    }

    private void matrix_Adjacency() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 53 -> A
         */
        for (int i = 0; i < totalBranches; i++) {
            for (int j = 0; j < totalBus; j++) {
                if (j == (int) branchIndex[i][0] - 1) {
                    adjacency[i][j] = 1;
                } else if (j == (int) branchIndex[i][1] - 1) {
                    adjacency[i][j] = -1;
                } else {
                    adjacency[i][j] = 0;
                }
            }
        }
    }

    private void matrix_ReducedAdjacency() {
        /*
         * DC-OPF paper: Junjie Sun and Leigh Tesfatsion, (2006) "DC OPF Formulation
         *       and Solution Using QuadProgJ", ISU Econ Working Paper Series #06014
         * Eq. 54 -> Ar
         */
        DoubleMatrix2D auxReducedAdjacency = new DenseDoubleMatrix2D(adjacency);
        reducedAdjacency = auxReducedAdjacency.viewPart(0, 1, totalBranches, totalBus - 1).toArray();
    }

    public int getTotalBus() {
        return totalBus;
    }

    public int getTotalBranches() {
        return totalBranches;
    }

    public double[][] getBranchData() {
        return branchData;
    }

    public double[][] getBranchIndex() {
        return branchIndex;
    }

    public double getPenaltyCoeff() {
        return penaltyCoeff;
    }

    public double[][] getVoltAngleDiff() {
        return VoltAngleDif;
    }

    public double[][] getReducedVoltAngleDiff() {
        return reducedVoltAngleDif;
    }

    public double[] getArrayMaxCap() {
        return arrayMaxCap;
    }

    public double[] getArrayReactance() {
        return arrayReactance;
    }

    public double[][] getNegativeSusceptance() {
        return negativeSusceptance;
    }

    public double[][] getBusAdmittance() {
        return busAdmittance;
    }

    public double[][] getReducedBusAdmittance() {
        return reducedBusAdmittance;
    }

    public double[][] getDiagonalAdmittance() {
        return diagonalAdmittance;
    }

    public double[][] getAdjacency() {
        return adjacency;
    }

    public double[][] getReducedAdjacency() {
        return reducedAdjacency;
    }
}
