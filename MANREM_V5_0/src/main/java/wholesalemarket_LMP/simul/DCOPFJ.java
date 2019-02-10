package wholesalemarket_LMP.simul;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.jet.math.Functions;
import wholesalemarket_LMP.Wholesale_InputData;

public class DCOPFJ {

    private int totalProducers;
    private int totalSuppliers;
    private int totalBranches;
    private int totalBus;

    private static final int PRODUCER_START_PRICE = 0;
    private static final int PRODUCER_SLOPE_PRICE = 1;
    private static final int PRODUCER_MIN_CAP = 2;
    private static final int PRODUCER_MAX_CAP = 3;

    private static final int SUPPLIER_START_PRICE = 0;
    private static final int SUPPLIER_SLOPE_PRICE = 1;
    private static final int SUPPLIER_SENSITIVE_LOAD = 2;

    private DoubleFactory1D fac1d = DoubleFactory1D.dense;
    private DoubleFactory2D fac2d = DoubleFactory2D.dense;
    // for using Colt's methods e.g. diagonal(), identity(), etc.

    // Input for QuadProgJ
    private DoubleMatrix2D G;
    private DoubleMatrix1D a;
    private DoubleMatrix2D Ceq;
    private DoubleMatrix1D beq;
    private DoubleMatrix2D Ciq;
    private DoubleMatrix1D biq;

    private DoubleMatrix1D PRODUCER_SLOPE;    // [totalPRODUCERS] x [1]
    private DoubleMatrix1D SUPPLIER_SLOPE;    // [totalSUPPLIERS] x [1]
    private DoubleMatrix2D U;
    private DoubleMatrix2D Wrr;
    private DoubleMatrix2D II;
    private DoubleMatrix2D rBusAdm;
    private DoubleMatrix2D JJ;

    private DoubleMatrix1D PRODUCER_STARTPRICE;
    private DoubleMatrix1D C;
    private DoubleMatrix1D FDemand;
    private DoubleMatrix2D Oni;   // totalBus x totalProducers
    private DoubleMatrix2D Z;    // totalBranches x totalBranches
    private DoubleMatrix2D rAdj; // totalBranches x(totalBus-1)
    private DoubleMatrix2D Iii;   // totalProducers x totalProducers
    private DoubleMatrix2D Oik;   // totalProducers x (totalBus-1)
    private DoubleMatrix1D pU;   // totalBranches x 1
    private DoubleMatrix1D capL;  // totalProducers x 1
    private DoubleMatrix1D capU;  // totalProducers x 1
    private DoubleMatrix2D Onj; // totalBranches x totalSuppliers
    private DoubleMatrix2D Oij; // totalProducers x totalSuppliers
    private DoubleMatrix2D Oji; // totalSuppliers x totalProducers
    private DoubleMatrix2D Ijj; // totalSuppliers x totalSuppliers
    private DoubleMatrix2D Ojk; // totalSuppliers x totalBus
    private DoubleMatrix1D sLoadL; // totalSuppliers x 1 
    private DoubleMatrix1D sLoadU; // totalSuppliers x 1

    private DoubleMatrix2D producers_Offer;
    private double[] producers_location;
    private DoubleMatrix2D suppliers_priceSensitiveOffer;
    private double[] suppliers_fixedDemand;
    private double[] suppliers_location;
    private TransmissionGridFormulation GRID_Formulation;
    private boolean priceSensitive;

    private QuadProgJ qpj;

    // Solution from QuadProgJ
    private double[] commitment; // power production quantity
    private double[] voltAngle;  // voltage angle in radians
    private double[] voltAngleDegree;  // voltage angle in degrees
    private double[] lmp;  // lmp (locational marginal prices) == eqMultiplier
    private double[] ineqMultiplier;
    private String[] ineqMultiplierName;
    private double minTVC; // minTVC = SUM (Ai*PGi + Bi*PGi^2)
    private double[] branchFlow; // branchFlow_km = (1/x_km)(delta_k - delta_m)
    private double[][] branchIndex; // branch index
    private double sumSquaredAngleDifference;  // SUM(delta_k - delta_m)^2
    private double[] sLoad; //price-sensitive load demand quantity

    public DCOPFJ(double[][] _producers_Offer, double[] _producers_location, double[][] _suppliers_priceSensitiveOffer,
            double[] _suppliers_fixedDemand, double[] _suppliers_location, TransmissionGridFormulation _grid, boolean _priceSensitive) {

        producers_Offer = new DenseDoubleMatrix2D(_producers_Offer); //[nº Producers][4]
        producers_location = _producers_location; //[nº Producers][1] -> Location on the grid

        suppliers_priceSensitiveOffer = new DenseDoubleMatrix2D(_suppliers_priceSensitiveOffer); //[nº Producers][3]
        suppliers_fixedDemand = _suppliers_fixedDemand; //[nº Suppliers][1] -> Fixed Demand
        suppliers_location = _suppliers_location; //[nº Suppliers][1] -> Location on the grid

        GRID_Formulation = _grid;

        priceSensitive = _priceSensitive;

        totalBranches = GRID_Formulation.getTotalBranches();
        totalBus = GRID_Formulation.getTotalBus();
        totalProducers = producers_location.length;
        totalSuppliers = suppliers_location.length;

        solveDCOPF();
    }

    private void solveDCOPF() {
        matrix_G();
        matrix_a();
        matrix_Ceq();
        matrix_beq();
        matrix_Ciq();
        matrix_biq();

        qpj = new QuadProgJ(G, a, Ceq, beq, Ciq, biq);
        boolean bHaveSolution = qpj.getIsFeasibleAndOptimal();

        commitment = new double[totalProducers];   // in MWs
        voltAngle = new double[totalBus - 1];  // in radians
        voltAngleDegree = new double[totalBus - 1]; // in degress
        lmp = new double[totalBus];
        ineqMultiplier = new double[2 * totalBranches + 2 * totalProducers];
        minTVC = 0;  // in Euro/h
        branchFlow = new double[totalBranches]; //in MWs
        branchIndex = new double[totalBranches][2]; // columns are FROM and TO; rows are branches
        branchIndex = GRID_Formulation.getBranchIndex();
        double[] fullVoltAngle = new double[totalBus]; // including delta_1 = 0
        sumSquaredAngleDifference = 0;

        // NOTE FOR THE SOLUTION STRUCTURE x* = qpj.getMinX()
        // x* = (p_{G1}...p_{GI}, delta_2...delta_K) for fixed demand
        // x* = (p_{G1}...p_{GI}, p_{L1}^S...p_{LJ}^S, delta_2...delta_K) 
        //      for price-sensitive demand
        sLoad = new double[totalSuppliers];

        if (bHaveSolution) { // QuadProgJ has a solution
            // DC-OPF solution for (p_{G1},...,p_{GI}) in SI
            for (int i = 0; i < totalProducers; i++) {
                commitment[i] = qpj.getMinX()[i] * Wholesale_InputData.getSPowerBase();
            }
            // DC-OPF solution for (p_{L1}^S,...,p_{LJ}^S) in SI
            for (int j = totalProducers; j < totalProducers + totalSuppliers; j++) {
                sLoad[j - totalProducers] = qpj.getMinX()[j] * Wholesale_InputData.getSPowerBase();
            }
            // DC-OPF solution for (delta_2,...,delta_K)
            for (int k = totalProducers + totalSuppliers; k < totalProducers + totalSuppliers + totalBus - 1; k++) {
                voltAngle[k - totalProducers - totalSuppliers] = qpj.getMinX()[k];  // voltAngle in radians
            }

            // Convert voltage angle from radian to degree
            for (int k = 1; k < totalBus - 1; k++) {
                voltAngleDegree[k] = (voltAngle[k] * 180) / Math.PI; // volt angle in degrees
            }

            // lmp: locational marginal prices in SI
            for (int k = 0; k < totalBus; k++) {
                lmp[k] = qpj.getEqMultipliers()[k] / Wholesale_InputData.getSPowerBase();
            }

            for (int j = 0; j < 2 * totalBranches + 2 * totalProducers; j++) {
                ineqMultiplier[j] = qpj.getIneqMultipiers()[j] / Wholesale_InputData.getSPowerBase();
            }
            for (int i = 0; i < totalProducers; i++) {
                minTVC = minTVC + (PRODUCER_STARTPRICE.get(i) / Wholesale_InputData.getSPowerBase()) * commitment[i]
                        + (PRODUCER_SLOPE.get(i) / (Wholesale_InputData.getSPowerBase() * Wholesale_InputData.getSPowerBase())) * commitment[i] * commitment[i];
            }

            for (int k = 1; k < totalBus; k++) {
                fullVoltAngle[k] = voltAngle[k - 1];
            }
            for (int n = 0; n < totalBranches; n++) {
                branchFlow[n] = (1 / GRID_Formulation.getArrayReactance()[n]) * (fullVoltAngle[(int) branchIndex[n][0] - 1]
                        - fullVoltAngle[(int) branchIndex[n][1] - 1]) * Wholesale_InputData.getSPowerBase();

                sumSquaredAngleDifference = sumSquaredAngleDifference
                        + Math.pow((fullVoltAngle[(int) branchIndex[n][0] - 1]
                                - fullVoltAngle[(int) branchIndex[n][1] - 1]), 2);
            }

        }
    }

    // G = blockDiag(U,Wrr), where U = diag(2B) or U = diag(2B, 2D)
    private void matrix_G() {
        PRODUCER_SLOPE = new DenseDoubleMatrix1D(producers_Offer.viewColumn(PRODUCER_SLOPE_PRICE).toArray());

        if (priceSensitive) {
            SUPPLIER_SLOPE = new DenseDoubleMatrix1D(suppliers_priceSensitiveOffer.viewColumn(SUPPLIER_SLOPE_PRICE).toArray());
            U = new DenseDoubleMatrix2D(fac2d.diagonal(fac1d.append(PRODUCER_SLOPE, SUPPLIER_SLOPE).assign(Functions.mult(2))).toArray());
            G = new DenseDoubleMatrix2D(totalProducers + totalSuppliers + totalBus - 1, totalProducers + totalSuppliers + totalBus - 1);
        } else {
            U = new DenseDoubleMatrix2D(fac2d.diagonal(PRODUCER_SLOPE.assign(Functions.mult(2))).toArray());
            G = new DenseDoubleMatrix2D(totalProducers + totalBus - 1, totalProducers + totalBus - 1);
        }

        Wrr = new DenseDoubleMatrix2D(GRID_Formulation.getReducedVoltAngleDiff());
        G.assign(fac2d.composeDiagonal(U, Wrr));
    }

    // a = (A, 0...0) or a = (A,-C,0...0)
    private void matrix_a() {
        PRODUCER_STARTPRICE = new DenseDoubleMatrix1D(producers_Offer.viewColumn(PRODUCER_START_PRICE).toArray());
        if (priceSensitive) {
            C = new DenseDoubleMatrix1D(suppliers_priceSensitiveOffer.viewColumn(SUPPLIER_START_PRICE).toArray());
            a = new DenseDoubleMatrix1D(totalProducers + totalSuppliers + totalBus - 1);
            a.viewPart(0, totalProducers).assign(PRODUCER_STARTPRICE);
            a.viewPart(totalProducers, totalSuppliers).assign(C.assign(Functions.neg));
        } else {
            a = new DenseDoubleMatrix1D(totalProducers + totalBus - 1);
            a.viewPart(0, totalProducers).assign(PRODUCER_STARTPRICE);
        }
    }

    // CeqTranspose = (II, -Br'); Ceq = CeqTranspose'; where Br' is rBusAdm here
    private void matrix_Ceq() {
        II = new DenseDoubleMatrix2D(totalBus, totalProducers);

        for (int i = 0; i < totalBus; i++) {
            for (int j = 0; j < totalProducers; j++) {
                if (producers_location[j] == i + 1) {
                    II.set(i, j, 1);
                }
            }
        }
        rBusAdm = new DenseDoubleMatrix2D(GRID_Formulation.getReducedBusAdmittance());

        if (priceSensitive) {
            JJ = new DenseDoubleMatrix2D(totalBus, totalSuppliers);
            for (int i = 0; i < totalBus; i++) {
                for (int j = 0; j < totalSuppliers; j++) {
                    if (suppliers_location[j] == i + 1) {
                        JJ.set(i, j, 1);
                    }
                }
            }
            DoubleMatrix2D[][] parts = {{II, JJ.assign(Functions.neg), rBusAdm.viewDice().assign(Functions.neg)}};
            DoubleMatrix2D CeqTranspose = new DenseDoubleMatrix2D(totalBus, totalProducers + totalSuppliers + totalBus - 1);
            CeqTranspose.assign(fac2d.compose(parts));
            Ceq = new DenseDoubleMatrix2D(totalProducers + totalSuppliers + totalBus - 1, totalBus);
            Ceq.assign(CeqTranspose.viewDice());
        } else {
            DoubleMatrix2D[][] parts = {{II, rBusAdm.viewDice().assign(Functions.neg)}};
            DoubleMatrix2D CeqTranspose = new DenseDoubleMatrix2D(totalBus, totalProducers + totalBus - 1);
            CeqTranspose.assign(fac2d.compose(parts));
            Ceq = new DenseDoubleMatrix2D(totalProducers + totalBus - 1, totalBus);
            Ceq.assign(CeqTranspose.viewDice());
        }
    }

    // FDemand = someFunction(atNodeByLSE, loadProfile); beq = FDemand
    private void matrix_beq() {
        FDemand = new DenseDoubleMatrix1D(totalBus);

        for (int i = 0; i < totalBus; i++) {
            double lp = 0;
            for (int j = 0; j < totalSuppliers; j++) {
                if (suppliers_location[j] == i + 1) {
                    lp += suppliers_fixedDemand[j];
                }
            }
            FDemand.set(i, lp);
        }
        beq = new DenseDoubleMatrix1D(totalBus);
        beq.assign(FDemand);
    }

    // Ciq matrix formulation
    //
    // (1) FIXED DEMAND CASE:
    // CiqTranspose = {{Oni, Z*rAdj},{Oni, -Z*rAdj},{Iii, Oik},{-Iii, Oik}};
    // Ciq = CiqTranspose'
    //
    // (2) PRICE-SENSITIVE DEMAND CASE:
    // CiqTranspose = {{MatrixT}, {MatrixG}, {MatrixL}};
    // MatrixT = {{Oni, Onj, Z*rAdj}, {Oni, Onj, -Z*rAdj}};
    // MatrixG = {{Iii, Oij, Oik},{-Iii, Oij, Oik}};
    // MatrixL = {{Oji, Ijj, Ojk},{Oji, -Ijj, Ojk}};
    private void matrix_Ciq() {
        Oni = new DenseDoubleMatrix2D(totalBranches, totalProducers);
        Z = new DenseDoubleMatrix2D(GRID_Formulation.getDiagonalAdmittance());
        rAdj = new DenseDoubleMatrix2D(GRID_Formulation.getReducedAdjacency());
        Iii = new DenseDoubleMatrix2D(totalProducers, totalProducers).assign(fac2d.identity(totalProducers));
        Oik = new DenseDoubleMatrix2D(totalProducers, totalBus - 1);

        if (priceSensitive) {
            Onj = new DenseDoubleMatrix2D(totalBranches, totalSuppliers);
            Oij = new DenseDoubleMatrix2D(totalProducers, totalSuppliers);
            Oji = new DenseDoubleMatrix2D(totalSuppliers, totalProducers);
            Ijj = new DenseDoubleMatrix2D(totalSuppliers, totalSuppliers).assign(fac2d.identity(totalSuppliers));
            Ojk = new DenseDoubleMatrix2D(totalSuppliers, totalBus - 1);
            DoubleMatrix2D[][] parts = {
                {Oni, Onj, Z.zMult(rAdj, null)},
                {Oni, Onj, Z.copy().assign(Functions.neg).zMult(rAdj, null)},
                {Iii, Oij, Oik},
                {Iii.copy().assign(Functions.neg), Oij, Oik},
                {Oji, Ijj, Ojk},
                {Oji, Ijj.copy().assign(Functions.neg), Ojk}};
            DoubleMatrix2D CiqTranspose = new DenseDoubleMatrix2D(2 * totalBranches + 2 * totalProducers + 2 * totalSuppliers, totalProducers + totalSuppliers + totalBus - 1);
            CiqTranspose.assign(fac2d.compose(parts));
            Ciq = new DenseDoubleMatrix2D(totalProducers + totalSuppliers + totalBus - 1, 2 * totalBranches + 2 * totalProducers + 2 * totalSuppliers);
            Ciq.assign(CiqTranspose.viewDice());
        } else {
            DoubleMatrix2D[][] parts = {
                {Oni, Z.zMult(rAdj, null)},
                {Oni, Z.copy().assign(Functions.neg).zMult(rAdj, null)},
                {Iii, Oik},
                {Iii.copy().assign(Functions.neg), Oik}};
            DoubleMatrix2D CiqTranspose = new DenseDoubleMatrix2D(2 * totalBranches + 2 * totalProducers, totalProducers + totalBus - 1);
            CiqTranspose.assign(fac2d.compose(parts));
            Ciq = new DenseDoubleMatrix2D(totalProducers + totalBus - 1, 2 * totalBranches + 2 * totalProducers);
            Ciq.assign(CiqTranspose.viewDice());
        }
    }

    // biq = (-pU, -pU, capL, -capU) or biq = (-pU, -pU, capL, -capU, sLoadL, -sLoadU)
    private void matrix_biq() {
        pU = new DenseDoubleMatrix1D(GRID_Formulation.getArrayMaxCap());
        capL = new DenseDoubleMatrix1D(producers_Offer.viewColumn(PRODUCER_MIN_CAP).toArray());
        capU = new DenseDoubleMatrix1D(producers_Offer.viewColumn(PRODUCER_MAX_CAP).toArray());

        if (priceSensitive) {
            double[] dSensitiveDemandL = new double[totalSuppliers];
            double[] dSensitiveDemandU = new double[totalSuppliers];
            for (int i = 0; i < totalSuppliers; i++) {
                dSensitiveDemandU[i] = suppliers_priceSensitiveOffer.get(i, SUPPLIER_SENSITIVE_LOAD);
            }

            sLoadL = new DenseDoubleMatrix1D(dSensitiveDemandL);
            sLoadU = new DenseDoubleMatrix1D(dSensitiveDemandU);
            DoubleMatrix1D[] parts
                    = {pU.copy().assign(Functions.neg), pU.copy().assign(Functions.neg),
                        capL, capU.copy().assign(Functions.neg),
                        sLoadL, sLoadU.copy().assign(Functions.neg)};
            biq = new DenseDoubleMatrix1D(2 * totalBranches + 2 * totalProducers + 2 * totalSuppliers);
            biq.assign(fac1d.make(parts));

            ineqMultiplierName = new String[2 * totalBranches + 2 * totalProducers + 2 * totalSuppliers];
            for (int i = 0; i < totalBranches; i++) {
                ineqMultiplierName[i] = "-BFlow " + (i + 1);
                ineqMultiplierName[totalBranches + i] = "+BFlow " + (i + 1);
            }
            for (int i = 0; i < totalProducers; i++) {
                ineqMultiplierName[2 * totalBranches + i] = "capL " + (i + 1);
                ineqMultiplierName[2 * totalBranches + totalProducers + i] = "capU " + (i + 1);
            }
            for (int i = 0; i < totalSuppliers; i++) {
                ineqMultiplierName[2 * totalBranches + 2 * totalProducers + i] = "-PS " + (i + 1);
                ineqMultiplierName[2 * totalBranches + 2 * totalProducers + totalSuppliers + i] = "+PS " + (i + 1);
            }

        } else {
            DoubleMatrix1D[] parts = {
                pU.copy().assign(Functions.neg),
                pU.copy().assign(Functions.neg),
                capL,
                capU.copy().assign(Functions.neg)
            };
            biq = new DenseDoubleMatrix1D(2 * totalBranches + 2 * totalProducers);
            biq.assign(fac1d.make(parts));

            ineqMultiplierName = new String[2 * totalBranches + 2 * totalProducers];
            for (int i = 0; i < totalBranches; i++) {
                ineqMultiplierName[i] = "-BFlow " + (i + 1);
                ineqMultiplierName[totalBranches + i] = "+BFlow " + (i + 1);
            }
            for (int i = 0; i < totalBranches; i++) {
                ineqMultiplierName[2 * totalBranches + i] = "capL " + (i + 1);
                ineqMultiplierName[2 * totalBranches + totalProducers + i] = "capU " + (i + 1);
            }
        }
    }

    public double[] getCommitment() {
        return commitment;
    }

    public double[] getSLoad() {
        return sLoad;
    }

    public double[] getVoltAngle() {
        return voltAngle;
    }

    public double[] getVoltAngleDegree() {
        return voltAngleDegree;
    }

    public double[] getLMP() {
        return lmp;
    }

    public double[] getIneqMultiplier() {
        return ineqMultiplier;
    }

    public String[] getIneqMultiplierName() {
        return ineqMultiplierName;
    }

    public double getMinTVC() {
        return minTVC;
    }

    public double[] getBranchFlow() {
        return branchFlow;
    }

    public double getSumSquaredAngleDifference() {
        return sumSquaredAngleDifference;
    }

    public int getNumBindingConstraints() {
        return qpj.getNumBC();
    }

    public int[] getActiveSet() {
        return qpj.getActiveSet();
    }

    public boolean getIsSolutionFeasibleAndOptimal() {
        return qpj.getIsFeasibleAndOptimal();
    }
}
