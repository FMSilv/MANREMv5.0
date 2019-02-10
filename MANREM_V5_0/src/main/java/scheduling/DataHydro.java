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
    public class DataHydro{          
      
      String GENCO_name;  
      String ID;
      String Tech;
      String Fuel;
      double RU; //Ramp-up
      double RD; //Ramp-down
      double MinReserve;
      double MaxReserve;
      double InitReserve;
      double MinDisch;
      double MaxDisch;
      double VCost;
      boolean Selection;
      String Cascadeorder;
      double pl1;
      double pl2;
      double pl3;
      double pl4;
      double Ul;
      double P01;
      double P02;
      double P03;
      double Pi;
      double curve;
      double startupcost;
      double inflow;
      double mediumlevel;
      double upperlevel;
      double FCost;
      double prevProduction;
 
      public DataHydro(String GENCO_name,String ID, String Tech, String Fuel, double RU, double RD, double MinReserve, double MaxReserve, double InitReserve, double MinDisch, double MaxDisch, double VCost, boolean Selection, String Cascadeorder, double pl1, double pl2, double pl3, double pl4, double Ul, double P01, double P02, double P03, double Pi, double curve, double startupcost, double inflow, double mediumlevel, double upperlevel, double FCost, double prevProduction) throws MinMaxException, MediumValueException{
        if (upperlevel>=MaxReserve){
            throw new MinMaxException("Upper Level Must Be Lower than Maximum Reserve");
        }else{
            if (mediumlevel>=upperlevel){
                throw new MinMaxException("Medium Level Must Be Lower than Upper Level");
            }else{
                if (MinDisch>=MaxDisch){
                    throw new MinMaxException("Minimum Discharge Must Be Lower than Maximum Discharge");
                } else {
                    if (MinReserve>=MaxReserve){
                        throw new MinMaxException("Minimum Reserve Must Be Lower than Maximum Reserve");
                    }else{
                        if (InitReserve>MaxReserve || InitReserve<MinReserve){
                            throw new MediumValueException("Initial Reserve Must Be Within Reserve Bounderies");
                        } else {
                            if (mediumlevel<=MinReserve){
                                throw new MinMaxException("Medium Level Must Be Higher than Minimum Reserve");
                            }else {
                                if (RU>Pi || RD>Pi){
                                    throw new MinMaxException("Ramp Values Mustn't Be Higher Maximum Power");
                                }else {
                                    if (P01 >= P02 || P02 >= P03 || P03>=Pi){
                                        throw new MinMaxException("Power Output Limits Must Be Crescent");
                                    }else {
                                        if (prevProduction > Pi || (prevProduction < P01 && prevProduction>0)){
                                            throw new MinMaxException("Previous Production Must be 0 or Within Power Bouderies");
                                        }else {
                                        
                                
                                        
                                   
        this.GENCO_name = GENCO_name;  
        this.ID = ID;
        this.Tech = Tech;
        this.Fuel = Fuel;
        this.RU = RU;
        this.RD = RD;
        this.MaxReserve = MaxReserve;
        this.MinReserve = MinReserve;
        this.InitReserve = InitReserve;
        this.MaxDisch = MaxDisch;
        this.MinDisch = MinDisch;
        this.VCost = VCost;
        this.Selection = Selection;
        this.Cascadeorder = Cascadeorder;
        this.pl1 = pl1;
        this.pl2 = pl2;
        this.pl3 = pl3;
        this.pl4 = pl4;
        this.Ul = Ul;
        this.P01 = P01;
        this.P02 = P02;
        this.P03 = P03;
        this.Pi = Pi;
        this.curve = curve;
        this.startupcost = startupcost;
        this.inflow = inflow;
        this.mediumlevel = mediumlevel;
        this.upperlevel = upperlevel;
        this.FCost = FCost;
        this.prevProduction = prevProduction;
        
        
    }}}}}}}}}
    }

    public double getPrevProduction() {
        return prevProduction;
    }

    public void setPrevProduction(double prevProduction) {
        this.prevProduction = prevProduction;
    }  
      
    public double getFCost() {
        return FCost;
    }

    public void setFCost(double FCost) {
        this.FCost = FCost;
    }
      
      
    public double getMediumlevel() {
        return mediumlevel;
    }

    public void setMediumlevel(double mediumlevel) {
        this.mediumlevel = mediumlevel;
    }


    public double getUpperlevel() {
        return upperlevel;
    }

    public void setUpperlevel(double upperlevel) {
        this.upperlevel = upperlevel;
    }

    public double getCurve() {
        return curve;
    }

    public void setCurve(double curve) {
        this.curve = curve;
    }

    public double getStartupcost() {
        return startupcost;
    }

    public void setStartupcost(double startupcost) {
        this.startupcost = startupcost;
    }

    public double getInflow() {
        return inflow;
    }

    public void setInflow(double inflow) {
        this.inflow = inflow;
    }

    public double getPl1() {
        return pl1;
    }

    public void setPl1(double pl1) {
        this.pl1 = pl1;
    }

    public double getPl2() {
        return pl2;
    }

    public void setPl2(double pl2) {
        this.pl2 = pl2;
    }

    public double getPl3() {
        return pl3;
    }

    public void setPl3(double pl3) {
        this.pl3 = pl3;
    }

    public double getPl4() {
        return pl4;
    }

    public void setPl4(double pl4) {
        this.pl4 = pl4;
    }

    public double getUl() {
        return Ul;
    }

    public void setUl(double Ul) {
        this.Ul = Ul;
    }

    public double getP01() {
        return P01;
    }

    public void setP01(double P01) {
        this.P01 = P01;
    }

    public double getP02() {
        return P02;
    }

    public void setP02(double P02) {
        this.P02 = P02;
    }

    public double getP03() {
        return P03;
    }

    public void setP03(double P03) {
        this.P03 = P03;
    }

    public double getPi() {
        return Pi;
    }

    public void setPi(double Pi) {
        this.Pi = Pi;
    }

    public String getCascadeorder() {
        return Cascadeorder;
    }

    public void setCascadeorder(String Cascadeorder) {
        this.Cascadeorder = Cascadeorder;
    }


    public String getGENCO_name() {
        return GENCO_name;
    }

    public void setGENCO_name(String GENCO_name) {
        this.GENCO_name = GENCO_name;
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

    public double getMinReserve() {
        return MinReserve;
    }

    public void setMinReserve(double MinReserve) {
        this.MinReserve = MinReserve;
    }

    public double getMaxReserve() {
        return MaxReserve;
    }

    public void setMaxReserve(double MaxReserve) {
        this.MaxReserve = MaxReserve;
    }

    public double getInitReserve() {
        return InitReserve;
    }

    public void setInitReserve(double InitReserve) {
        this.InitReserve = InitReserve;
    }

    public double getMinDisch() {
        return MinDisch;
    }

    public void setMinDisch(double MinDisch) {
        this.MinDisch = MinDisch;
    }

    public double getMaxDisch() {
        return MaxDisch;
    }

    public void setMaxDisch(double MaxDisch) {
        this.MaxDisch = MaxDisch;
    }

    
    }