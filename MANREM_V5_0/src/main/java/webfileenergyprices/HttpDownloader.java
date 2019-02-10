/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfileenergyprices;

import java.io.IOException;
import java.io.BufferedReader;
import java.util.Arrays;

/**
 *
 * @author Filipe
 */
public class HttpDownloader {
    
    public static void main(String[] args) {
        String[] dateString;
        ReadWebFile r1 = new ReadWebFile();
        dateString = r1.getDate();
        
        //String fileURL = "http://www.omie.es/datosPub/marginalpdbcpt/marginalpdbcpt_20161113.1";
        String fileURL = "http://www.omie.es/datosPub/marginalpdbcpt/marginalpdbcpt_"+dateString[0]+dateString[1]+dateString[2]+".1";
        String saveDir = "C:\\Users\\Filipe\\Desktop\\LNEG";
        try {
            HttpDownloadUtility.downloadFile(fileURL, saveDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //String string = saveDir + "\\marginalpdbcpt_20161113.1";
        String string = saveDir + "\\marginalpdbcpt_"+dateString[0]+dateString[1]+dateString[2]+".1";

        BufferedReader br = r1.openFile(string);
        r1.readFile( br, "pt" );
        r1.closeFile( br );
        
        //System.out.println(Arrays.toString(dateString));
        //System.out.println(Arrays.toString(r1.getPtmarginalenergyprices()));
        
    }
    
    
    

    
    
}
