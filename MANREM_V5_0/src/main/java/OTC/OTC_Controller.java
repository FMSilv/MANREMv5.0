/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OTC;

import jade.core.Agent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import personalassistant.PersonalAssistant;
import wholesalemarket_SMP.SMP_Market_Controller;

/**
 * Based on wholesalemarket_SMP.SMP_Market_Controller.java
 * Changes made by: João de Sá
 */
public class OTC_Controller {
    
    private Participants window;
    
    public static int START_HOUR = 0;
    public static int END_HOUR = 23;
    private String[] sellerNames;
    private String[] buyerNames;
    private boolean isSeller = false;
    
    private ArrayList<Float> Volumes;
    private ArrayList<Float> Prices;
    
    private ArrayList<OTC_AgentData> buyers;
    private ArrayList<OTC_AgentData> sellers;
    
    private OTC_AgentData new_agent;

    public OTC_Controller() {
        buyers = new ArrayList<>();
        sellers = new ArrayList<>();
    }

    public void start_InputData(PersonalAssistant market, boolean _isSeller, DefaultListModel _sellerNames, DefaultListModel _buyerNames) {
        isSeller = _isSeller;
        sellerNames = splitAgentTotalNames(_sellerNames.toString(), _sellerNames.getSize());
        buyerNames = splitAgentTotalNames(_buyerNames.toString(), _buyerNames.getSize()); 
        
        InputData_Window(market);
        
    }
    
   public String[] splitAgentTotalNames(String _names, int _num) {
        String aux = _names.substring(_names.indexOf("[") + 1, _names.indexOf("]"));
        String[] agentNames = aux.split(", ", _num);
        return agentNames;
    }
    
    public void InputData_Window(Agent market) {
        window = new Participants(market, this, isSeller, START_HOUR, END_HOUR);
        window.setVisible(true);
    }
    
    
    public void setSellerNames(String[] sellerNames) {
        this.sellerNames = sellerNames;
    }

    public void setBuyerNames(String[] buyerNames) {
        this.buyerNames = buyerNames;
    }
    public String[] getSellerNames() {
        return sellerNames;
    }

    public String[] getBuyerNames() {
        return buyerNames;
    }  

    public void ReadFile(String AgentName, boolean isSeller, boolean isOTC, boolean isPool) throws BiffException{
        
        File f = new File("files\\"+AgentName+"\\OTC_data.xls");
        
        //File needs to be .xls and the syntax needs to be as follows
        // column 1 volumes column 2 prices
        // first row only has colummn titles, all subsequent rows are values
        // Joao de Sá
        
        
        Workbook wb;
        try {
            wb = Workbook.getWorkbook(f);
            Sheet s = wb.getSheet(0);
            int row = s.getRows();
            int col = s.getColumns();
            Cell c;
            
            Volumes = new ArrayList<>();
            Prices = new ArrayList<>();
            
            
            for(int i = 0; i < row; i++){
                for(int j=0; j < col; j++){
                    c = s.getCell(j, i);
                    
                    if(i > 0 && j == 0){
                        Volumes.add(Float.parseFloat(c.getContents()));
                    }else if(i > 0 && j == 1){
                        Prices.add(Float.parseFloat(c.getContents()));
                    }
                    
                }
            }

            
            int id = buyers.size() + sellers.size();
            new_agent = new OTC_AgentData(AgentName, id, Prices, Volumes, isOTC, isPool);
            
            
            if(isSeller){
                if(sellers.isEmpty())
                    sellers.add(new_agent);
                else{
                    for(int i = 0; i < sellers.size(); i++){
                        if(sellers.get(i).getName().equals(AgentName)){
                            JOptionPane.showMessageDialog(null, "Agent information already added!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                    }
                    sellers.add(new_agent);
                }
                    
            } else{
                if(buyers.isEmpty())
                    buyers.add(new_agent);
                else{
                    for(int i = 0; i < buyers.size(); i++){
                        if(buyers.get(i).getName().equals(AgentName)){
                            JOptionPane.showMessageDialog(null, "Agent information already added!\n",
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                    }
                    buyers.add(new_agent);
                }
            }
            
            

        } catch (IOException ex) {
            Logger.getLogger(OTC_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
    
    
    // Method that generates messages with information for OTC simulation
    // message is stored in a string array
    // message[0] -> agent name
    // message[1] -> participating in OTC? "y" for true, "n" for false
    // message[2] -> participating in pool? "y for true, "n" for false
    // message[3] -> Volumes
    // message[4] -> Prices
    
    
    public String[] Message_generator(boolean isSeller, int i){
        
        String[] message = new String[6];
        
        
        if(isSeller){
            
            message[0] = "seller";
            
            message[1] = sellers.get(i).getName();
            
            
            if(sellers.get(i).get_isOTC())
                message[2] = "y";
            else
                message[2] = "n";
            if(sellers.get(i).get_isPool())
                message[3] = "y";
            else
                message[3] = "n";
            
            
            message[4] = "";
            for(int j = 0; j < sellers.get(i).get_PowerOffers().size(); j++){
                message[4] = message[4] + Float.toString(sellers.get(i).get_PowerOffers().get(j));
                message[4] = message[4] + " ";
            }
            
            message[5] = "";
            for(int j = 0; j < sellers.get(i).get_PowerOffers().size(); j++){
                message[5] = message[5] + Float.toString(sellers.get(i).get_PriceOffers().get(j));
                message[5] = message[5] + " ";
            }
            
 
        }
        else{
            
            message[0] = "buyer";
            
            message[1] = buyers.get(i).getName();
            
            
            if(buyers.get(i).get_isOTC())
                message[2] = "y";
            else
                message[2] = "n";
            if(buyers.get(i).get_isPool())
                message[3] = "y";
            else
                message[3] = "n";
            
            
            message[4] = "";
            for(int j = 0; j < buyers.get(i).get_PowerOffers().size(); j++){
                message[4] = message[4] + Float.toString(buyers.get(i).get_PowerOffers().get(j));
                message[4] = message[4] + " ";
            }
            
            message[5] = "";
            for(int j = 0; j < buyers.get(i).get_PowerOffers().size(); j++){
                message[5] = message[5] + Float.toString(buyers.get(i).get_PriceOffers().get(j));
                message[5] = message[5] + " ";
            }
        }
        
        return message;
    }
    
    public void addOTCBuyers(ArrayList<OTC_AgentData> buyer) {
         for (int i=0; i<buyer.size();i++){
            this.buyers.add(buyer.get(i));
         }
    }
    
    public void addOTCSellers(ArrayList<OTC_AgentData> buyer) {
         for (int i=0; i<buyer.size();i++){
            this.sellers.add(buyer.get(i));
         }
    }
    
}
