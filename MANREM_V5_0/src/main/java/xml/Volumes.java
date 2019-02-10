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
public class Volumes {
    
    private String              Volumes; 
    private ArrayList<String>   Volume, vMin, vMax, vFlex;
    
    public String                           getVolumes(){
        return Volumes;
    }
    public ArrayList<String>                           getVolume(){
        return Volume;
    }
    public ArrayList<String>                           getvMax(){
        return vMax;
    }
    public ArrayList<String>                           getvMin(){
        return vMin;
    }
    public ArrayList<String>                           getvFlex(){
        return vFlex;
    }
    
    public void                             setVolumes(String Volumes){
        this.Volumes    =   Volumes;
    }
    public void                             setVolume(String Volume){
        this.Volume.add(Volume);//     =   Volume;
    }
    public void                             setvMax(String vMax){
        this.vMax.add(vMax);//    =   vMax;
    }
    public void                             setvMin(String vMin){
        this.vMin.add(vMin);//    =   vMin;
    }
    public void                             setvFlex(String vFlex){
        this.vFlex.add(vFlex); //   =   vFlex;
    }
    
    public Volumes(){
    
    this.Volume     = new ArrayList<>();
    this.vFlex      = new ArrayList<>();
    this.vMax       = new ArrayList<>();
    this.vMin       = new ArrayList<>();
    }
    
}
