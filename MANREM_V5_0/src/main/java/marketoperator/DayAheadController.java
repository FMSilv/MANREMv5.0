/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marketoperator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import wholesalemarket_SMP.AgentData;
import wholesalemarket_SMP.Simulation;

/**
 *
 * @author Joao
 */
public class DayAheadController {
    
    private ArrayList<AgentData> buyers = new ArrayList<AgentData>();
    private ArrayList<AgentData> sellers = new ArrayList<AgentData>();
    
    public static int START_HOUR = 0;
    public static int END_HOUR = 23;
    
    private String[] sellerNames;
    private String[] buyerNames;
    
    String Name;
    int id;
    
    ArrayList<Float> price = new ArrayList<Float>();
    ArrayList<Float> power = new ArrayList<Float>();
    
    public String SMPsymsimulation(){
        String results = null;
        
        System.out.println("Starting SMP sym simulation");
        
        this.readinputfile();
        
        // Run Simulation
        
        Simulation sim = new Simulation(buyers, sellers, true);
        sim.run(this.START_HOUR, this.END_HOUR, this.sellerNames, this.buyerNames);
        
        // Write results message content
        
        results = "Results SMPsym;";
        
        results = results + "" + buyers.get(0).getMarket_Price_Sym().get(0) + ";";
        
        results = results + "Producers";
        
        for(int i = 0; i < sellers.size(); i++){
            results = results + ";" + sellers.get(i).getName() + " ";
            
            for(int j = 0; j < sellers.get(i).getTraded_power_Sym().size(); j++){
                results = results + sellers.get(i).getTraded_power_Sym().get(j) + " ";
            }
            
            results = results + "end";
        }
        
        results = results + ";Buyers";
        
        for(int i = 0; i < buyers.size(); i++){
            results = results + ";" + buyers.get(i).getName() + " ";
            
            for(int j = 0; j < buyers.get(i).getTraded_power_Sym().size(); j++){
                results = results + buyers.get(i).getTraded_power_Sym().get(j) + " ";
            }
            
            results = results + "end";
        }
        
        return results;
        
    }
    
    public String SMPasymsimulation(){
        String results = null;
        
        System.out.println("Starting SMP asym simulation");
        
        this.readinputfile();
        
        // Run Simulation
        
        Simulation sim = new Simulation(buyers, sellers, false);
        sim.run(this.START_HOUR, this.END_HOUR, this.sellerNames, this.buyerNames);
        
        // Write results message content
        
        results = "Results SMPasym;";
        
        results = results + "" + buyers.get(0).getMarket_Price_aSym().get(0) + ";";
        
        results = results + "Producers";
        
        for(int i = 0; i < sellers.size(); i++){
            results = results + ";" + sellers.get(i).getName() + " ";
            
            for(int j = 0; j < sellers.get(i).getTraded_power_aSym().size(); j++){
                results = results + sellers.get(i).getTraded_power_aSym().get(j) + " ";
            }
            
            results = results + "end";
        }
        
        results = results + ";Buyers";
        
        for(int i = 0; i < buyers.size(); i++){
            results = results + ";" + buyers.get(i).getName() + " ";
            
            for(int j = 0; j < buyers.get(i).getTraded_power_aSym().size(); j++){
                results = results + buyers.get(i).getTraded_power_aSym().get(j) + " ";
            }
            
            results = results + "end";
        }
        
        return results;
    }
    
    public void readinputfile(){
        try {
            String altName;
            int k=0;
            AgentData newData;
            
            File f = new File("input.xls");
            
            Workbook wb;
            
            wb = Workbook.getWorkbook(f);
            
            // first sheet has all input information
            Sheet s = wb.getSheet(0);
            int row = s.getRows();
            int col = s.getColumns();
            Cell c;
            
            // first block for buyers, second block for producers
            // first column has name of participants
            // second column has number of period
            // third column has period power offer
            // fourth column has period price offer
            
            id = 0;
            c = s.getCell(0,1);
            Name = c.getContents();
            altName = Name;
            
            for(int i = 1; !s.getCell(0,i).getContents().contentEquals(""); i++){
                
                c = s.getCell(0,i);
                altName = c.getContents();
                
                if(!altName.equals(Name)){
                    newData = new AgentData(Name, id, price, power);
                    buyers.add(newData);
                    
                    price = new ArrayList<Float>();
                    power = new ArrayList<Float>();
                    
                    Name = altName;
                    id++;
                }
                
                c = s.getCell(2,i);
                power.add(Float.parseFloat(c.getContents()));
                
                c = s.getCell(3,i);
                price.add(Float.parseFloat(c.getContents()));
                
                k = i;
            }
            
            newData = new AgentData(Name, id, price, power);
            buyers.add(newData);
            
            // Reading Producer's block
            
            id = 0;
            c = s.getCell(0,k+3);
            Name = c.getContents();
            altName = Name;
            price = new ArrayList<Float>();
            power = new ArrayList<Float>();
            
            // cycle starts in k+3 becaus k will be last entry of last buyer, k+1 will be the
            // empty separation cell between buyers and sellers and k+2 will be the header of 
            // the seller block
            
            for(int i = k+3; i < row; i++){
                
                c = s.getCell(0,i);
                altName = c.getContents();
                
                if(!altName.equals(Name)){
                    newData = new AgentData(Name, id, price, power);
                    sellers.add(newData);
                    
                    price = new ArrayList<Float>();
                    power = new ArrayList<Float>();
                    
                    Name = altName;
                    id++;
                }
                
                c = s.getCell(2,i);
                power.add(Float.parseFloat(c.getContents()));
                
                c = s.getCell(3,i);
                price.add(Float.parseFloat(c.getContents()));
                
            }
            
            newData = new AgentData(Name, id, price, power);
            sellers.add(newData);
            
            price = new ArrayList<Float>();
            power = new ArrayList<Float>();

            buyerNames = new String[buyers.size()];
            for(int i = 0; i < buyers.size(); i++){
                buyerNames[i] = buyers.get(i).getName();
            }
            
            sellerNames = new String[sellers.size()];
            for(int i = 0; i < sellers.size(); i++){
                sellerNames[i] = sellers.get(i).getName();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(DayAheadController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(DayAheadController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
