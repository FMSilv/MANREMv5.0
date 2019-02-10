/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalassistant;

import jade.core.Agent;
import java.io.IOException;
import webfileenergyprices.*;
import java.io.BufferedReader;

/**
 *
 * @author Filipe
 */
public class ExternalAssistant extends Agent{

    public static String[] date;
    public static double[] ptmarginalenergyprices = new double[24];
    public static double[] esmarginalenergyprices = new double[24];
    
    @Override
    protected void setup() {
        System.out.println("Real Time Agent initiated!");
        
        String[] dateString;
        ReadWebFile r = new ReadWebFile();
        dateString = r.getDate();
        this.date = dateString;
        
        String fileURL = "http://www.omie.es/datosPub/marginalpdbcpt/marginalpdbcpt_"+dateString[0]+dateString[1]+dateString[2]+".1";
        //String saveDir = "C:\\Users\\Filipe\\Desktop\\LNEG";
        String saveDir = "webfiles\\Energy Prices";
        try {
            HttpDownloadUtility.downloadFile(fileURL, saveDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        String string = saveDir + "\\marginalpdbcpt_"+dateString[0]+dateString[1]+dateString[2]+".1";

        BufferedReader br_pt = r.openFile(string);
        BufferedReader br_es = r.openFile(string);
        this.ptmarginalenergyprices = r.readFile( br_pt, "pt");
        this.esmarginalenergyprices = r.readFile( br_es, "es");
        r.closeFile( br_pt );
        r.closeFile( br_es );
    }

    
}
