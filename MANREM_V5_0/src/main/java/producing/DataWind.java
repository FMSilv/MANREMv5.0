/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package producing;

import scheduling.MinMaxException;

/**
 *
 * @author João
 * * Copied from DataWind on scheduling package whose author was Afonso Mota Cardoso Neves da Silva 
 */
public class DataWind{     
      
      String GENCO_name;   
      String ID;
      double MinP;
      double MaxP;
      double FCost;
      double VCost;
      double [] Production;

    public DataWind(String GENCO_name,String ID, double MinP, double MaxP, double FCost, double VCost, double[] Production) throws MinMaxException{
        if (MinP>=MaxP){
            throw new MinMaxException("Minimum Production Must Be Lower than Maximum Production");
        } else {
      
            this.GENCO_name = GENCO_name;        
            this.ID = ID;
            this.MinP = MinP;
            this.MaxP = MaxP;
            this.FCost = FCost;
            this.VCost = VCost;
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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
