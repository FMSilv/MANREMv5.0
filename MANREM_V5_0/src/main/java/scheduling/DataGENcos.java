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


public class DataGENcos {
    
    private String Name;
    private String Address1;
    private String Address2;
    private String Email;
    private boolean isScheduled;
    private double [] PoolForecast;
    private double [] BCPrices;
    //private double [] BCPrices_purchase;

    public DataGENcos(String Name, String Address1, String Address2, String Email, boolean isScheduled) {
        this.Name = Name;
        this.Address1 = Address1;
        this.Address2 = Address2;
        this.Email = Email;
        this.isScheduled = isScheduled;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String Address1) {
        this.Address1 = Address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String Address2) {
        this.Address2 = Address2;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public boolean isIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public double[] getPoolForecast() {
        return PoolForecast;
    }

    public void setPoolForecast(double[] k) {
        this.PoolForecast = k;
    }

    public double getBCPrices(int i) {
        return BCPrices[i];
    }

    public void setBCPrices(double [] k) {
        this.BCPrices = k;
    }

//    public double getBCPrices_purchase(int i) {
//        return BCPrices_purchase[i];
//    }
//
//    public void setBCPrices_purchase(int i, double k) {
//        this.BCPrices_purchase[i] = k;
//    }

   
    
    
}


