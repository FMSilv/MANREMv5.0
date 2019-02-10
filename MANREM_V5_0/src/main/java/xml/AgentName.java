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
public class AgentName {
    
    private String Name;                                                   //  local agent name
    private String Type;                                                   // type id *Buyer/Seller
    private String NumberAtributes;        
    private Volumes VolData;
    private Prices PrcData;
    private Config CfgData;
    
    
    public String getName(){
        return Name;
    }
    
    public String getType(){
        return Type;
    }
    
    public String getNumberAtributes(){
        return NumberAtributes;
    }
    
    public Volumes getVolData(){
        return VolData;
    }
   
    public Prices getPrcData(){
        return PrcData;
    }
   
    public Config getCfgData(){
        return CfgData;
    }
    
    public void setName(String Name){
        this.Name = Name;
    }
    
    public void setType(String Type){
        this.Type = Type;
    }
    
    public void setNumberAtributes(String NumberAtributes){
        this.NumberAtributes    =   NumberAtributes;
    } 
    
    public void setVolData(Volumes VolData) {
        this.VolData = VolData;
    }
    
    public void setPrcData(Prices PrcData) {
        this.PrcData = PrcData;
    }
    public void setCfgData(Config CfgData) {
        this.CfgData = CfgData;
    }
    
    
    public AgentName(){}
    
    // provavelmente não é necessário
    @Override
    public String toString(){
        
        return  this.Name + " " + this.Type + " " + this.NumberAtributes + " " 
                + this.CfgData + " " + this.PrcData + " " + this.VolData;
                
                 
    }
}

