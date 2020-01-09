package personalassistant;

public class DataHydro {

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
