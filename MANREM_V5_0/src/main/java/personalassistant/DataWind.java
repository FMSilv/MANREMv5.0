package personalassistant;

public class DataWind {

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
