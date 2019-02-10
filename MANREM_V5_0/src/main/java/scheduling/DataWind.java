/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

/**
 *
 * @author Af
 */
// CRIA LISTA PARA GUARDAR VALORES
     public class DataWind{     
      
      String GENCO_name;   
      String ID;
      String Tech;
      String Fuel;
      double MinP;
      double MaxP;
      double FCost;
      double VCost;
      boolean Selection;
      double [] Production;

    public DataWind(String GENCO_name,String ID, String Tech, String Fuel, double MinP, double MaxP, double FCost, double VCost, boolean Selection, double [] Production) throws MinMaxException{
            if (MinP>=MaxP){
       throw new MinMaxException("Minimum Production Must Be Lower than Maximum Production");
       } else {
      
        this.GENCO_name = GENCO_name;        
        this.ID = ID;
        this.Tech = Tech;
        this.Fuel = Fuel;
        this.MinP = MinP;
        this.MaxP = MaxP;
        this.FCost = FCost;
        this.VCost = VCost;
        this.Selection = Selection;
        this.Production=Production;
    }
    }

    public String getGENCO_name() {
        return GENCO_name;
    }

    public void setGENCO_name(String GENCO_name) {
        this.GENCO_name = GENCO_name;
    }

    public double getFCost() {
        return FCost;
    }

    public void setFCost(double FCost) {
        this.FCost = FCost;
    }

    public double getVCost() {
        return VCost;
    }

    public void setVCost(double VCost) {
        this.VCost = VCost;
    }

    public boolean isSelection() {
        return Selection;
    }

    public void setSelection(boolean Selection) {
        this.Selection = Selection;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTech() {
        return Tech;
    }

    public void setTech(String Tech) {
        this.Tech = Tech;
    }

    public String getFuel() {
        return Fuel;
    }

    public void setFuel(String Fuel) {
        this.Fuel = Fuel;
    }

    public double getMinP() {
        return MinP;
    }

    public void setMinP(int MinP) {
        this.MinP = MinP;
    }

    public double getMaxP() {
        return MaxP;
    }

    public void setMaxP(int MaxP) {
        this.MaxP = MaxP;
    }

    public double getProduction(int i) {
        return Production[i];
    }

    public void setProduction(double[] Production) {
        this.Production = Production;
    }
    
     
     } 
