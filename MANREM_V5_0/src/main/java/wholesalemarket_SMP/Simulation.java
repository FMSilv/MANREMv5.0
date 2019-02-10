package wholesalemarket_SMP;

import jade.core.AID;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import marketpool.offersresults.AgentOffers;
import marketpool.MarketPool;
import marketpool.offersresults.PeriodResult;
import wholesalemarket_SMP.results.SMP_Wholesale_Results;


//   modificações   <-------------------------------------------------------



import personalassistant.PersonalAssistant;


public class Simulation {

    private ArrayList<AgentData> buyers;
    private ArrayList<AgentData> sellers;
    private ArrayList<AgentOffers> buyers_sim;
    private ArrayList<AgentOffers> sellers_sim;
    private PersonalAssistant market_agent;   //  Added market agent ref. João de Sá <-----
    private boolean is_Sym;
    private String prices;   //  Added String variable to hold prices of period to send to the respective agents
    private String volumes;  //  Added String variable to hold volumes of period to sendo to the respective agent
    
// Added market agent as an argument for the contrcutor. João de Sá <-----------
    
    public Simulation(ArrayList<AgentData> _bids, ArrayList<AgentData> _offers, boolean is_Sym) {
        this.is_Sym = is_Sym;
        this.buyers = _bids;
        this.sellers = _offers;
        this.buyers_sim = convertObject(this.buyers);
        this.sellers_sim = convertObject(this.sellers);
    }

    
    
    public void run(int _startHour, int _endHour, String sellerNames[], String buyerNames[]) {
        MarketPool mp_sym = new MarketPool(buyers_sim, sellers_sim, "output-sym.csv");
        MarketPool mp_asym = new MarketPool(buyers_sim, sellers_sim, "output-asym.csv");
        
        if (is_Sym) {
            mp_sym.runSymmetricalPool();
            for (int i = 0; i < mp_sym.getResultSellers().size(); i++) {
                for (int j = 0; j < mp_sym.getResultSellers().get(i).getSpotResult().size(); j++) {
                    PeriodResult pr = mp_sym.getResultSellers().get(i).getSpotResult().get(j);
                    sellers.get(i).setIsNegotiated_Sym(pr.isNegotiates());
                    sellers.get(i).setTraded_power_Sym((double) pr.getTradedPower());
                    sellers.get(i).setMarket_Price_Sym((double) pr.getMarketPrice());
                    sellers.get(i).setPeriod_TotalPrice_Sym((double) pr.getPeriodTotalPrice());
                }
            }
            for (int i = 0; i < mp_sym.getResultBuyers().size(); i++) {
                for (int j = 0; j < mp_sym.getResultBuyers().get(i).getSpotResult().size(); j++) {
                    PeriodResult pr = mp_sym.getResultBuyers().get(i).getSpotResult().get(j);
                    buyers.get(i).setIsNegotiated_Sym(pr.isNegotiates());
                    buyers.get(i).setTraded_power_Sym((double) pr.getTradedPower());
                    buyers.get(i).setMarket_Price_Sym((double) pr.getMarketPrice());
                    buyers.get(i).setPeriod_TotalPrice_Sym((double) pr.getPeriodTotalPrice());
                }
                
            }
        } else {
            mp_asym.runAsymmetricalPool();

            for (int i = 0; i < mp_asym.getResultSellers().size(); i++) {
                for (int j = 0; j < mp_asym.getResultSellers().get(i).getSpotResult().size(); j++) {
                    PeriodResult pr = mp_asym.getResultSellers().get(i).getSpotResult().get(j);
                    sellers.get(i).setIsNegotiated_aSym(pr.isNegotiates());
                    sellers.get(i).setTraded_power_aSym((double) pr.getTradedPower());
                    sellers.get(i).setMarket_Price_aSym((double) pr.getMarketPrice());
                    sellers.get(i).setPeriod_TotalPrice_aSym((double) pr.getPeriodTotalPrice());
                }
            }
            for (int i = 0; i < mp_asym.getResultBuyers().size(); i++) {
                for (int j = 0; j < mp_asym.getResultBuyers().get(i).getSpotResult().size(); j++) {
                    PeriodResult pr = mp_asym.getResultBuyers().get(i).getSpotResult().get(j);
                    buyers.get(i).setIsNegotiated_aSym(pr.isNegotiates());
                    buyers.get(i).setTraded_power_aSym((double) pr.getTradedPower());
                    buyers.get(i).setMarket_Price_aSym((double) pr.getMarketPrice());
                    buyers.get(i).setPeriod_TotalPrice_aSym((double) pr.getPeriodTotalPrice());
                }
            }
        }

        
        SMP_Wholesale_Results results = new SMP_Wholesale_Results(buyers, sellers, _startHour, _endHour, is_Sym);
        results.setVisible(true);
    }

    private ArrayList<AgentOffers> convertObject(ArrayList<AgentData> _list) {
        ArrayList<AgentOffers> agentList = new ArrayList<>();
        for (AgentData _list1 : _list) {
            agentList.add(new AgentOffers(_list1.getAgent(), _list1.getPrice(), _list1.getPower()));
        }
        return agentList;
    }
}
