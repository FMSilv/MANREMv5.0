/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;

import static java.lang.Integer.parseInt;
import java.util.List;

/**
 *
 * @author Af
 */
// CRIA LISTA PARA GUARDAR VALORES
   public class DataThermal{                                         
      String GENCO_name;      
      String ID;
      String Tech;
      String Fuel;
      double MinP;
      double MaxP; 
      double RU; //Ramp-up
      double RD; //Ramp-down
      int MinOn;
      int MinOff;
      double PrevProd; //Production at t0
      double FCost;
      double VCost;
      boolean Selection;
      double SUcost;
      double SDcost;
      int initStatus;
      double FuelCfixed;
      double FuelCvar;
      double EmCO2;
      double EmNO2;
            

    public DataThermal(String GENCO_name,String ID, String Tech, String Fuel, double MinP, double MaxP, double RU, double RD, int MinOn, int MinOff, double PrevProd,double FCost, double VCost, boolean Selection, double SUcost,double SDcost, int initStatus, double FuelCfixed, double FuelCvar, double EmCO2, double EmNO2) throws MinMaxException, MediumValueException{
       if (MinP>=MaxP){
       throw new MinMaxException("Minimum Production Must Be Lower than Maximum Production");
       } else {
                if (PrevProd>MaxP || ( PrevProd<MinP && PrevProd>0)){
                    throw new MediumValueException("Previous Production Must Be 0 or Be Within Production Bounderies");
                } else {
                    if (RU>MaxP || RD>MaxP){
                        throw new MinMaxException("Ramp Values Mustn't Be Higher Maximum Power");
                        } else {
                           
                           
        this.GENCO_name = GENCO_name;   
        this.ID = ID;
        this.Tech = Tech;
        this.Fuel = Fuel;
        this.MinP = MinP;
        this.MaxP = MaxP;
        this.RU = RU;
        this.RD = RD;
        this.MinOn = MinOn;
        this.MinOff = MinOff;
        this.PrevProd = PrevProd;     
        this.FCost = FCost;
        this.VCost = VCost;
        this.Selection = Selection;
        this.SUcost = SUcost;
        this.SDcost = SDcost;
        this.initStatus= initStatus;
        this.FuelCfixed = FuelCfixed;
        this.FuelCvar = FuelCvar;
        this.EmCO2 = EmCO2;
        this.EmNO2 = EmNO2;
        
    }}}
    }

    public double getFuelCfixed() {
        return FuelCfixed;
    }

    public void setFuelCfixed(double FuelCfixed) {
        this.FuelCfixed = FuelCfixed;
    }

    public double getFuelCvar() {
        return FuelCvar;
    }

    public void setFuelCvar(double FuelCvar) {
        this.FuelCvar = FuelCvar;
    }

    public double getEmCO2() {
        return EmCO2;
    }

    public void setEmCO2(double EmCO2) {
        this.EmCO2 = EmCO2;
    }

    public double getEmNO2() {
        return EmNO2;
    }

    public void setEmNO2(double EmNO2) {
        this.EmNO2 = EmNO2;
    }

    public double getSDcost() {
        return SDcost;
    }

    public void setSDcost(double SDcost) {
        this.SDcost = SDcost;
    }

    public double getSUcost() {
        return SUcost;
    }

    public void setSUcost(double SUcost) {
        this.SUcost = SUcost;
    }

    public int getInitStatus() {
        return initStatus;
    }

    public void setInitStatus(int initStatus) {
        this.initStatus = initStatus;
    }

    
    
    
    public String getGENCO_name() {
        return GENCO_name;
    }

    public void setGENCO_name(String GENCO_name) {
        this.GENCO_name = GENCO_name;
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

    public void setMinP(double MinP) {
        this.MinP = MinP;
    }

    public double getMaxP() {
        return MaxP;
    }

    public void setMaxP(double MaxP) {
        this.MaxP = MaxP;
    }

    public double getRU() {
        return RU;
    }

    public void setRU(double RU) {
        this.RU = RU;
    }

    public double getRD() {
        return RD;
    }

    public void setRD(double RD) {
        this.RD = RD;
    }

    public int getMinOn() {
        return MinOn;
    }

    public void setMinOn(int MinOn) {
        this.MinOn = MinOn;
    }

    public int getMinOff() {
        return MinOff;
    }

    public void setMinOff(int MinOff) {
        this.MinOff = MinOff;
    }

    public double getPrevProd() {
        return PrevProd;
    }

    public void setPrevProd(double PrevProd) {
        this.PrevProd = PrevProd;
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


    
     
      
      
    public void SetChar(List<String> provisorio){
       ID = provisorio.get(0);
       Tech = provisorio.get(1);
       Fuel = provisorio.get(2);
       MinP = parseInt(provisorio.get(3));
       MaxP = parseInt(provisorio.get(4));
       RU = parseInt(provisorio.get(5));
       RD = parseInt(provisorio.get(6));
       MinOn = parseInt(provisorio.get(7));
       MinOff = parseInt(provisorio.get(8));
       PrevProd = parseInt(provisorio.get(9));
       
     }}
