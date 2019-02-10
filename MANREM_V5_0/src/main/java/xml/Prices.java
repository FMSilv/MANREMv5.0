/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.util.ArrayList;

/**
 *
 * @author Paulo Bonifacio
 */
public class Prices {
     private String Prices;//, Price, pMax, pFlex;
     private ArrayList<String> Price, pMax, pFlex;
     
     
     
     public String                          getPrices(){
        return Prices;
    }
    public ArrayList<String>                getPrice(){
        return Price;
    }
    public ArrayList<String>                 getpMax(){
        return pMax;
    }
    public ArrayList<String>                getpFlex(){
        return pFlex;
    }
    
    public void                             setPrices(String Prices){
        this.Prices  =   Prices;
    }
    public void                             setPrice(String Price){
        this.Price.add(Price); 
    }
    public void                             setpFlex(String pFlex){
        this.pFlex.add(pFlex); 
    }
    public void                             setpMax(String pMax){
        this.pMax.add(pMax);//    =   pMax;
    }
    
    public Prices(){
        this.Price  =   new ArrayList<>();
        this.pFlex  =   new ArrayList<>();
        this.pMax   =   new ArrayList<>();
    }
    ;
        
}
    
