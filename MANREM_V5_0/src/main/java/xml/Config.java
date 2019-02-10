/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

/**
 *
 * @author Paulo Bonifacio
 */
public class Config {
    
    public String Strategy, Protocol, Preferences;
    
    public String                           getStrategy(){
        return Strategy;
    }
    public String                           getProtocol(){
        return Protocol;
    }
    public String                           getPreferences(){
        return Preferences;
    }
    
    public void                             setStrategy(String Strategy){
        this.Strategy   =    Strategy;
    }
    public void                             setProtocol(String Protocol){
        this.Protocol   =   Protocol;
    }
    public void                             setPreferences(String Preferences){
        this.Preferences    =   Preferences;
    }/**/
    
    public Config(){}
}
