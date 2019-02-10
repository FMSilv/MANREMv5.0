package wholesalemarket_SMP;


import java.util.ArrayList;
import marketpool.offersresults.AgentOffers;


public class AgentData extends AgentOffers{
    
    private int id;
    private boolean isCompleted;
    private ArrayList<Boolean> isNegotiated_Sym;
    private ArrayList<Double> traded_power_Sym;
    private ArrayList<Double> market_Price_Sym;
    private ArrayList<Double> period_TotalPrice_Sym;
    
    private ArrayList<Boolean> isNegotiated_aSym;
    private ArrayList<Double> traded_power_aSym;
    private ArrayList<Double> market_Price_aSym;
    private ArrayList<Double> period_TotalPrice_aSym;
// New Variables to hold agent's Name and market results < -----------------------------------------
    
    private String name;
    private String results;  //this string will look like "price1 price2 (etc)"

// < ---------------------------------------------------------------------------    
    
    public AgentData(String agent, int _id, ArrayList<Float> price, ArrayList<Float> power) {
        super(agent, price, power);
        this.id = _id;
        this.isCompleted = true;
        
        this.isNegotiated_Sym = new ArrayList<>();
        this.traded_power_Sym = new ArrayList<>();
        this.market_Price_Sym = new ArrayList<>();
        this.period_TotalPrice_Sym = new ArrayList<>();
        
        this.isNegotiated_aSym = new ArrayList<>();
        this.traded_power_aSym = new ArrayList<>();
        this.market_Price_aSym = new ArrayList<>();
        this.period_TotalPrice_aSym = new ArrayList<>();
// AgentData now also holds the agent's name < ---------------------------------
        
        this.name = agent;
        
// < ---------------------------------------------------------------------------        
    }

    public String getName(){
        return name;
    }
    
    
    public boolean getStatus() {
        return isCompleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public ArrayList<Boolean> getIsNegotiated_Sym() {
        return isNegotiated_Sym;
    }
    
    public void setIsNegotiated_Sym(boolean isNegotiated) {
        this.isNegotiated_Sym.add(isNegotiated);
    }

    public ArrayList<Boolean> getIsNegotiated_aSym() {
        return isNegotiated_aSym;
    }

    public void setIsNegotiated_aSym(boolean isNegotiated) {
        this.isNegotiated_aSym.add(isNegotiated);
    }

    public ArrayList<Double> getTraded_power_Sym() {
        return traded_power_Sym;
    }

    public void setTraded_power_Sym(Double traded_power) {
        this.traded_power_Sym.add(traded_power);
    }
    
    public ArrayList<Double> getTraded_power_aSym() {
        return traded_power_aSym;
    }

    public void setTraded_power_aSym(Double traded_power) {
        this.traded_power_aSym.add(traded_power);
    }

    public ArrayList<Double> getMarket_Price_Sym() {
        return market_Price_Sym;
    }

    public void setMarket_Price_Sym(Double market_Price) {
        this.market_Price_Sym.add(market_Price);
    }
    
    public ArrayList<Double> getMarket_Price_aSym() {
        return market_Price_aSym;
    }

    public void setMarket_Price_aSym(Double market_Price) {
        this.market_Price_aSym.add(market_Price);
    }
    
    public ArrayList<Double> getPeriod_TotalPrice_Sym() {
        return period_TotalPrice_Sym;
    }

    public void setPeriod_TotalPrice_Sym(Double period_TotalPrice) {
        this.period_TotalPrice_Sym.add(period_TotalPrice);
    }

    public ArrayList<Double> getPeriod_TotalPrice_aSym() {
        return period_TotalPrice_aSym;
    }

    public void setPeriod_TotalPrice_aSym(Double period_TotalPrice) {
        this.period_TotalPrice_aSym.add(period_TotalPrice);
    }
}