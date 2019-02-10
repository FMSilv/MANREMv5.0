package wholesalemarket_LMP.simul;

import java.util.ArrayList;

public class ISO {

    private WholesaleMarket mainMarket;
    private BidbasedUnitCommitment buc;

    private ArrayList<ProducerData> PRODUCER_AgentList;
    private ArrayList<SupplierData> SUPPLIER_AgentList;
    
    private double[][] fixedDemandBySupplier;           // [total Suppliers] x [24h]
    private double[][][] trueSupplyOfferByProducer;       // [total Producers] x [hour] x [{ Start, Slope, Cap Min, Cap Máx }]
    private double[][][] trueDemandBidBySupplier;       // [total Suppliers] x [24h] x [{ Start, Slope, sLoadU }]

    private int totalProducers;
    private int totalSuppliers;
    private static final int PRODUCER_PARAMETERS = 4;
    private static final int SUPPLIER_PS_PARAMETERS = 3;
    private final int HOURS_PER_DAY = WholesaleMarket.HOUR_PER_DAY;
    
    
    
    // constructor
    public ISO(WholesaleMarket model) {
        mainMarket = model;
        
        PRODUCER_AgentList = mainMarket.getProducersData();
        SUPPLIER_AgentList = mainMarket.getSupplierData();

        totalProducers = mainMarket.getNrProducers();
        totalSuppliers = mainMarket.getNrSuppliers();

        trueDemandBidBySupplier = new double[totalSuppliers][HOURS_PER_DAY][SUPPLIER_PS_PARAMETERS];
        fixedDemandBySupplier = new double[totalSuppliers][HOURS_PER_DAY];
        trueSupplyOfferByProducer = new double[totalProducers][WholesaleMarket.HOUR_PER_DAY][PRODUCER_PARAMETERS];    
        
        buc = new BidbasedUnitCommitment(this, mainMarket);
    }

    public String computeCompetitiveEquilibriumResults() {
        String solveOPF_Problems;
        submitTrueSupplyOffersAndDemandBids();
        
        solveOPF_Problems = buc.solveOPF();
        if (solveOPF_Problems.isEmpty()) {
            mainMarket.addProducerAgent_CommitmentWithTrueCost(buc.getDailyCommitment());
            mainMarket.addLMPWithTrueCost(buc.getDailyLMP());
            mainMarket.addSupplierAgent_PriceSensitiveDemandWithTrueCost(buc.getDailyPriceSensitiveDemand());
            mainMarket.addBranchPowerFlow(buc.getDailyBranchFlow());
        } 
        return solveOPF_Problems;
    }

    public void submitTrueSupplyOffersAndDemandBids() {
        for (int i = 0; i < totalProducers; i++) {
            trueSupplyOfferByProducer[i] = PRODUCER_AgentList.get(i).submitTrueSupplyOffer();
        }

        for (int j = 0; j < totalSuppliers; j++) {
            fixedDemandBySupplier[j] = SUPPLIER_AgentList.get(j).getLoadFixedDemand();
            for (int k = 0; k < HOURS_PER_DAY; k++) {
                trueDemandBidBySupplier[j][k] = SUPPLIER_AgentList.get(j).getPriceSensitiveDemand()[k];
            }
        }
    }
    
    public double[][][] getSupplyOfferByGen() {
        return trueSupplyOfferByProducer;
    }

    public double[][] getLoadProfileByLSE() {
        return fixedDemandBySupplier;
    }

    public double[][][] getDemandBidByLSE() {
        return trueDemandBidBySupplier;
    }

    public BidbasedUnitCommitment getBUC() {
        return buc;
    }
}
