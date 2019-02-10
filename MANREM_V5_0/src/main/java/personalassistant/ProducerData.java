/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalassistant;

import java.util.ArrayList;
/**
 *
 * @author João
 */
public class ProducerData {
    private String Name;
    private String type;
    private String Address;
    private String Phone_number;
    private String Email;
    private String Objective;
    private boolean Participating;
    private String Strategy;
    
    public ArrayList<DataHydro> DataHydro = new ArrayList();
    public ArrayList<DataThermal> DataThermal = new ArrayList();
    public ArrayList<DataWind> DataWind = new ArrayList();

    
    private ArrayList<Boolean> isNegotiated_Sym;
    private ArrayList<Double> traded_power_Sym;
    private ArrayList<Double> market_Price_Sym;
    private ArrayList<Double> period_TotalPrice_Sym;
    
    private ArrayList<Boolean> isNegotiated_aSym;
    private ArrayList<Double> traded_power_aSym;
    private ArrayList<Double> market_Price_aSym;
    private ArrayList<Double> period_TotalPrice_aSym;
    
    ArrayList<Float> price = new ArrayList<Float>();
    ArrayList<Float> power = new ArrayList<Float>();

    public void setName(String _Name){
        this.Name = _Name;
    }
    
    public String getName(){
        return Name;
    }

    public String getAddress(){
        return Address;
    }
    
    public void setAddress(String _Address){
        this.Address = _Address;
    }
    
    
    public String getEmail(){
        return Email;
    }
    
    public void setEmail(String _Email){
        this.Email = _Email;
    }
    
    
    public String getPhone_number(){
        return Phone_number;
    }
    
    public void setPhone_number(String _Phone_number){
        this.Phone_number = _Phone_number;
    }
    
    
    public String getObjective(){
        return Objective;
    }

    public String getStrategy(){
        return Strategy;
    }
    
    public void setStrategy(String _Strategy){
        this.Strategy = _Strategy;
    }
    
    public void setParticipating(boolean _Participating){
        this.Participating = _Participating;
    }
    
    public boolean getParticipating(){
        return Participating;
    }
    
    public  ArrayList<Float> getPower() {
        return power;
    }
    
    public  ArrayList<Float> getPrice() {
        return price;
    }
    
    public void setPower(Float Power) {
        this.power.add(Power);
    }
    
    public void setPrice(Float Price) {
        this.price.add(Price);
    }
    
    public void addDataThermal(String ID, Double PMax, Double Pmin, String Fuel, Double FCost){
        
        DataThermal newDataThermal = new DataThermal(ID, PMax, Pmin, Fuel, FCost);
        
        DataThermal.add(newDataThermal);
        
        
    }
    
    public void addDataWind(String ID, Double PMax, Double Pmin, Double FCost){
        
        DataWind newDataWind = new DataWind(ID, PMax, Pmin, FCost);
        
        DataWind.add(newDataWind);
        
        
    }
    
    public void addDataHydro(String ID, Double Pi, Double FCost){
        
        DataHydro newDataHydro = new DataHydro(ID, Pi, FCost);
        
        DataHydro.add(newDataHydro);
        
        
    }
    
    public void setObjective(String _Objective){
        this.Objective = _Objective;
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
    
    public void Initialize_Market_Price_Sym(){
        this.market_Price_Sym = new ArrayList<Double>();
    }
    
    public void Initialize_Market_Price_aSym(){
        this.market_Price_aSym = new ArrayList<Double>();
    }
    
    public void Initialize_Traded_power_aSym(){
        this.traded_power_aSym = new ArrayList<Double>();
    }
    
    public void Initialize_Traded_power_Sym(){
        this.traded_power_Sym = new ArrayList<Double>();
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

    public static class DataHydro {
        
        private String ID;
        private Double Pi;
        private Double FCost;

        public DataHydro(String ID, Double Pi, Double FCost) {
            this.ID = ID;
            this.Pi = Pi;
            this.FCost = FCost;
            
            
        }
        
        public String getID(){
            return this.ID;
        }
        
        public Double getPi(){
            return this.Pi;
        }
        
        public Double getFCost(){
            return this.FCost;
        }
    }

    public static class DataThermal {
        
        private String ID;
        private Double PMax;
        private Double Pmin;
        private String Fuel;
        private Double FCost;

        public DataThermal(String ID, Double PMax, Double Pmin, String Fuel, Double FCost) {
            this.ID = ID;
            this.PMax = PMax;
            this.Pmin = Pmin;
            this.Fuel = Fuel;
            this.FCost = FCost;
            
            
        }
        
        public String getID(){
            return this.ID;
        }
        
        public Double getPMax(){
            return this.PMax;
        }
        
        public Double getPmin(){
            return this.Pmin;
        }
        
        public String getFuel(){
            return this.Fuel;
        }
        
        public Double getFCost(){
            return this.FCost;
        }
        
    }

    public static class DataWind {
        
        private String ID;
        private Double PMax;
        private Double Pmin;
        private Double FCost;

        public DataWind(String ID, Double PMax, Double Pmin, Double FCost) {
            this.ID = ID;
            this.PMax = PMax;
            this.Pmin = Pmin;
            this.FCost = FCost;
            
            
        }
        
        public String getID(){
            return this.ID;
        }
        
        public Double getPMax(){
            return this.PMax;
        }
        
        public Double getPmin(){
            return this.Pmin;
        }
        
        public Double getFCost(){
            return this.FCost;
        }
    }    
}
