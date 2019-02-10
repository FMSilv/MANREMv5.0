package wholesalemarket_SMP;

import jade.core.AID;
import jade.core.Agent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import personalassistant.PersonalAssistant;

public class SMP_Market_Controller {

    private InputData_Agents window;
    private Intraday_Time timeWindow;

    private ArrayList<AgentData> buyers = new ArrayList<AgentData>();
    private ArrayList<AgentData> sellers = new ArrayList<AgentData>();
    
    
    public static int START_HOUR = 0;
    public static int END_HOUR = 23;
    private String[] sellerNames;
    private String[] buyerNames;
    private boolean isSeller = false;

    public SMP_Market_Controller() {
        buyers = new ArrayList<>();
        sellers = new ArrayList<>();
    }

    public void start_InputData(PersonalAssistant market, boolean _isSeller, DefaultListModel _sellerNames, DefaultListModel _buyerNames) {
        isSeller = _isSeller;
        sellerNames = splitAgentTotalNames(_sellerNames.toString(), _sellerNames.getSize());
        buyerNames = splitAgentTotalNames(_buyerNames.toString(), _buyerNames.getSize());     
        InputData_Window(market);
        
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
    
    public String[] splitAgentTotalNames(String _names, int _num) {
        String aux = _names.substring(_names.indexOf("[") + 1, _names.indexOf("]"));
        String[] agentNames = aux.split(", ", _num);
        return agentNames;
    }
    
    public void InputData_Window(Agent market) {
        window = new InputData_Agents(market, this, isSeller, START_HOUR, END_HOUR);
        window.setVisible(true);
    }
    
    public void SMPCaseStudy(String file) {
        window.setCaseStudydata(file);
    }
    
    
    public int getStartHour() {
        return START_HOUR;
    }

    public void setStartHour(int startHour) {
        this.START_HOUR = startHour;
    }

    public int getEndHour() {
        return END_HOUR;
    }

    public void setEndHour(int endHour) {
        this.END_HOUR = endHour;
    }

    
// Added PersonalAssistant agent as argument for run method. João de Sá <------------------
    
    
    public void casetudy(boolean with_wind, PersonalAssistant market){
        PersonalAssistant PA = market;
        String agent;
        int id;
        int k;
        int g;
        File f;

        Workbook wb;

        System.out.println("entrou no run!!");
        
        buyers = new ArrayList<AgentData>();
        sellers = new ArrayList<AgentData>();

        this.sellerNames = new String[99];
        this.buyerNames = new String[99];

        if(with_wind){
            g = 39;
        }else{
            g = 36;
        }
        
        k=1;
        
        for(int i = 2; i < g; i++){
            try {
                ArrayList<Float> price = new ArrayList<Float>();
                ArrayList<Float> power = new ArrayList<Float>();

                System.out.println("" + k);
                agent = "Genco"+k;
                System.out.println("" + agent);
                
                f = new File("files\\case_study.xls");

                wb = Workbook.getWorkbook(f);

                Sheet s = wb.getSheet(0);
                int row = s.getRows();
                int col = s.getColumns();
                Cell c;

                c = s.getCell(0,1);

                sellerNames[k-1] = agent;
                id = k-1;
                
                k++;

                c = s.getCell(i,1);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,2);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,3);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,4);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,5);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,6);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,7);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,8);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,9);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,10);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,11);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,12);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,13);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,14);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,15);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,16);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,17);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,18);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,19);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,20);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,21);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,22);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,23);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,24);
                price.add(Float.parseFloat(c.getContents()));

                i=i+1;

                c = s.getCell(i,1);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,2);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,3);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,4);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,5);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,6);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,7);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,8);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,9);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,10);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,11);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,12);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,13);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,14);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,15);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,16);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,17);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,18);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,19);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,20);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,21);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,22);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,23);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(i,24);
                power.add(Float.parseFloat(c.getContents()));

                AgentData newData = new AgentData(agent, id, price, power);
                sellers.add(newData);

            } catch (IOException ex) {
                Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BiffException ex) {
                Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }    
            
        }
        
        try {
            ArrayList<Float> price = new ArrayList<Float>();
            ArrayList<Float> power = new ArrayList<Float>();

            f = new File("files\\case_study.xls");

            wb = Workbook.getWorkbook(f);


            Sheet s = wb.getSheet(0);
            int row = s.getRows();
            int col = s.getColumns();
            Cell c;

            agent = "Retailco";
            buyerNames[0] = agent;
            id = 0;

            c = s.getCell(0,1);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,2);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,3);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,4);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,5);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,6);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,7);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,8);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,9);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,10);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,11);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,12);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,13);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,14);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,15);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,16);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,17);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,18);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,19);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,20);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,21);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,22);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,23);
            price.add(Float.parseFloat(c.getContents()));
            c = s.getCell(0,24);
            price.add(Float.parseFloat(c.getContents()));

            c = s.getCell(1,1);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,2);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,3);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,4);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,5);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,6);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,7);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,8);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,9);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,10);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,11);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,12);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,13);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,14);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,15);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,16);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,17);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,18);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,19);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,20);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,21);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,22);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,23);
            power.add(Float.parseFloat(c.getContents()));
            c = s.getCell(1,24);
            power.add(Float.parseFloat(c.getContents()));

            AgentData newData = new AgentData(agent, id, price, power);
            buyers.add(newData);
   
        } catch (IOException ex) {
            Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        System.out.println("Vai para a simulation!!");

        Simulation sim = new Simulation(buyers, sellers, true);
        sim.run(this.START_HOUR, this.END_HOUR, this.sellerNames, this.buyerNames);
        
        
    }
 
    public void run(boolean is_Sym, PersonalAssistant market) {
        
            PersonalAssistant PA = market;
            String agent;
            int id;
            int k;
            int g;
            File f;
            
            Workbook wb;
            
            System.out.println("entrou no run!!");
            
            buyers = new ArrayList<AgentData>();
            sellers = new ArrayList<AgentData>();
            
            this.sellerNames = new String[14];
            this.buyerNames = new String[1];
            
            if(is_Sym){
                g = 14;
            }else{
                g = 13;
            }
            
            for(int i = 0; i < g; i++){
                try {
                    ArrayList<Float> price = new ArrayList<Float>();
                    ArrayList<Float> power = new ArrayList<Float>();
                    
                    k = i+1;
                    System.out.println("" + i);
                    System.out.println("" + k);
                    agent = "GenCo"+k;
                    System.out.println("" + agent);
                    
                    f = new File("files\\"+agent+"\\Standard_strat.xls");
                    
                    wb = Workbook.getWorkbook(f);
                    
                    Sheet s = wb.getSheet(0);
                    int row = s.getRows();
                    int col = s.getColumns();
                    Cell c;
                    
                    c = s.getCell(0,1);
                    
                    sellerNames[i] = agent;
                    id = i;
                    
                    c = s.getCell(3,1);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,2);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,3);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,4);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,5);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,6);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,7);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,8);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,9);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,10);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,11);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,12);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,13);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,14);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,15);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,16);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,17);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,18);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,19);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,20);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,21);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,22);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,23);
                    price.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(3,24);
                    price.add(Float.parseFloat(c.getContents()));
                    
                    c = s.getCell(2,1);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,2);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,3);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,4);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,5);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,6);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,7);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,8);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,9);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,10);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,11);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,12);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,13);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,14);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,15);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,16);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,17);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,18);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,19);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,20);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,21);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,22);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,23);
                    power.add(Float.parseFloat(c.getContents()));
                    c = s.getCell(2,24);
                    power.add(Float.parseFloat(c.getContents()));
                    
                    AgentData newData = new AgentData(agent, id, price, power);
                    sellers.add(newData);
                } catch (IOException ex) {
                    Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BiffException ex) {
                    Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            try {
                ArrayList<Float> price = new ArrayList<Float>();
                ArrayList<Float> power = new ArrayList<Float>();

                f = new File("files\\RetailCo1\\Standard_strat.xls");

                wb = Workbook.getWorkbook(f);


                Sheet s = wb.getSheet(0);
                int row = s.getRows();
                int col = s.getColumns();
                Cell c;

                c = s.getCell(0,1);

                agent = c.getContents();
                buyerNames[0] = agent;
                id = 0;

                c = s.getCell(3,1);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,2);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,3);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,4);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,5);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,6);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,7);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,8);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,9);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,10);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,11);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,12);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,13);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,14);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,15);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,16);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,17);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,18);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,19);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,20);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,21);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,22);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,23);
                price.add(Float.parseFloat(c.getContents()));
                c = s.getCell(3,24);
                price.add(Float.parseFloat(c.getContents()));

                c = s.getCell(2,1);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,2);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,3);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,4);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,5);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,6);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,7);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,8);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,9);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,10);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,11);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,12);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,13);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,14);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,15);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,16);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,17);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,18);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,19);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,20);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,21);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,22);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,23);
                power.add(Float.parseFloat(c.getContents()));
                c = s.getCell(2,24);
                power.add(Float.parseFloat(c.getContents()));

                AgentData newData = new AgentData(agent, id, price, power);
                buyers.add(newData);
   
        } catch (IOException ex) {
            Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(SMP_Market_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Vai para a simulation!!");

        Simulation sim = new Simulation(buyers, sellers, true);
        sim.run(this.START_HOUR, this.END_HOUR, this.sellerNames, this.buyerNames);
    }
    
    public ArrayList<AgentData> getBuyers() {
        return buyers;
    }

    public void setBuyers(ArrayList<AgentData> buyers) {
        
        // setBuyers was not working as intended 
        // Whenever setBuyers was called the content of the array list was
        // rewritten, making it so there was never more than one element in the
        // list
        // To correct this, setBuyers simply calls the addBuyers method
        // Which correctly adds a new member to the list
        // To return setBuyers to the previous state simply uncomment the
        // commented line of code and delete the uncommented one
        // João de Sá

        //this.buyers = buyers;
        addBuyers(buyers);

    }
    
    public void addBuyers(ArrayList<AgentData> buyer) {
         for (int i=0; i<buyer.size();i++){
            this.buyers.add(buyer.get(i));
         }
    }

    public ArrayList<AgentData> getSellers() {
        return sellers;
    }

    public void setSellers(ArrayList<AgentData> sellers) {
    
        // setSellers was not working as intended 
        // Whenever setSellers was called the content of the array list was
        // rewritten, making it so there was never more than one element in th
        // list
        // To correct this, setSellers simply calls the addSellers method
        // Which correctly adds a new member to the list
        // To return setSellers to the previous state simply uncomment the
        // commented line of code and delete the uncommented one
        // João de Sá
       
        //this.sellers = sellers;
        addSellers(sellers);
        
        
    }
    public void addSellers(ArrayList<AgentData> seller) {
         for (int i=0; i<seller.size();i++){
            this.sellers.add(seller.get(i));
         }
    }
}
