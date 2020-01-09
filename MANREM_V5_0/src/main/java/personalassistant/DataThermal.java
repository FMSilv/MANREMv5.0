package personalassistant;

public class DataThermal {
    
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
