/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OTC;



import java.util.ArrayList;
import marketpool.offersresults.AgentOffers;

// Created by João de Sá
// Based on wholesalemarket_SMP.AgentData.java

public final class OTC_AgentData extends AgentOffers {
    
    private int id;
    
    private boolean isCompleted;
    private boolean OTC;
    private boolean Pool;
    
    private boolean[] isNegotiated_OTC;
    private ArrayList<Double> traded_power_OTC;
    private ArrayList<Double> market_Price_OTC;
    private ArrayList<Double> period_TotalPrice_OTC;
    
    ArrayList<Float> OTCPoweroffers;
    ArrayList<Float> OTCPriceoffers;
    
    
    
    private String name;
    
    
    public OTC_AgentData(String agent, int _id, ArrayList<Float> price, ArrayList<Float> power, boolean isOTC, boolean isPool) {
        super(agent, price, power);
        this.id = _id;
        this.isCompleted = true;
        this.isNegotiated_OTC = new boolean[power.size()];
        
        for(int i = 0; i < power.size(); i++){
            this.isNegotiated_OTC[i] = false;
        }
        
        this.traded_power_OTC = new ArrayList<>();
        this.market_Price_OTC = new ArrayList<>();
        this.period_TotalPrice_OTC = new ArrayList<>();
        
        this.OTC = isOTC;
        this.Pool = isPool;
        
        this.OTCPoweroffers = power;
        this.OTCPriceoffers = price;
        
        
        this.name = agent;
               
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
    
    public boolean[] getIsNegotiated_OTC() {
        return isNegotiated_OTC;
    }
    
    public void setIsNegotiated_OTC(int index, boolean value) {
            
        this.isNegotiated_OTC[index] = value;
            
    }
    
    public ArrayList<Double> getTraded_power_OTC() {
        return traded_power_OTC;
    }

    public void setTraded_power_OTC(Double traded_power) {
        this.traded_power_OTC.add(traded_power);
    }

    public ArrayList<Double> getMarket_Price_OTC() {
        return market_Price_OTC;
    }

    public void setMarket_Price_OTC(Double market_Price) {
        this.market_Price_OTC.add(market_Price);
    }
    
    public ArrayList<Double> getPeriod_TotalPrice_OTC() {
        return period_TotalPrice_OTC;
    }

    public void setPeriod_TotalPrice_OTC(Double period_TotalPrice) {
        this.period_TotalPrice_OTC.add(period_TotalPrice);
    }

    public boolean get_isOTC() {
        return this.OTC;
    }
    
    
    public boolean get_isPool() {
        return this.Pool;
    }
    
    public ArrayList<Float> get_PowerOffers(){
        return this.OTCPoweroffers;
    }
    
    public ArrayList<Float> get_PriceOffers(){
        return this.OTCPriceoffers;
    }
    
}
