/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfileenergyprices;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Filipe
 */

public class ReadWebFile {
    
    BufferedReader br = null;
    public static double[] ptmarginalenergyprices = new double[24];
    public static double[] esmarginalenergyprices = new double[24];
    
    public BufferedReader openFile(String string){
	try {
            br = new BufferedReader(new FileReader(string));
        } catch(Exception e){
            System.out.println("Could not find file!");
        }
        return br;
    }
    
    public double[] readFile(BufferedReader br, String c_place){
        double[] energyprices = new double[24];
        int array_indice = 0;
        String sCurrentLine;
        try{
            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
                int counter_semicolon = 0;
                for( int i=0; i<sCurrentLine.length(); i++ ) {
                    if( sCurrentLine.charAt(i) == ';' ) {
                        counter_semicolon++;
                    }
                }
                if( counter_semicolon == 6 ){
                    String[] parts = sCurrentLine.split(";");
                    if(c_place.equals("pt")){
                        energyprices[array_indice] = Double.valueOf(parts[4]);
                    }else{
                        energyprices[array_indice] = Double.valueOf(parts[5]);
                    }
                    array_indice++;
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return energyprices;
    }   
        
        
    public void closeFile(BufferedReader br){
    try {
        if (br != null)br.close();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    }

    
    public String[] getDate(){
        String[] dateArray = new String[3];
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (month/day/year)
        DateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");

        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();        
        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        String reportDate = dateformat.format(today);

        // Print what date is today!
        //System.out.println("Report Date: " + reportDate);
        
        dateArray = reportDate.split("/");
        //System.out.println(Arrays.toString(array));

        return dateArray;
    }
    
    
    
    public double[] getPtmarginalenergyprices() {
        return ptmarginalenergyprices;
    }
    
    public double[] getEsmarginalenergyprices() {
        return esmarginalenergyprices;
    }
    
    
    
    
}
