/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static scheduling.AddGenerator.InfoGENCO;
import static scheduling.SchedulingOutput.BCOffers;
import scheduling.SchedulingOutput.Contract;
import scheduling.SchedulingOutput.PoolOffers;

/**
 *
 * @author AfonsoMCardoso
 */
public class Import_export {
    
   public static ArrayList<String> Lista_GENCOS_BC = new ArrayList();
   public static String [][] loadedGencoData;
   public static int HORIZON = 24;
 
   public static void exportPool(ArrayList <PoolOffers> PoolOffers, int HORIZON, String Name) throws IOException, WriteException{ 
        
            //File Pool = new File("files\\"+Name+"\\input.xls");
            XSSFWorkbook workbook = new XSSFWorkbook();
            
            XSSFSheet buyersheet = workbook.createSheet("Buyers");
            XSSFSheet poolsheet = workbook.createSheet("Sellers");
            
            System.out.print("Pool Offers :"+PoolOffers.size()+"\n");

            poolsheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
            poolsheet.addMergedRegion(new CellRangeAddress(0,1,1,1));
            poolsheet.addMergedRegion(new CellRangeAddress(0,1,26,26));

            Row row0 = poolsheet.createRow(0);
            row0.createCell(26).setCellValue("UNIT");
            row0.createCell(0).setCellValue("Agente");
            row0.createCell(1).setCellValue("Tipo Oferta");            
            
            Row row1 = poolsheet.createRow(1);
            
            for (int t = 1; t<=24; t++){
                row0.createCell(1+t).setCellValue("Periodo "+t);
                row1.createCell(1+t).setCellValue("Fraccao "+1);
            }
                
        //Pool       
        for(int x = 0; x < PoolOffers.size(); x++){  //4 volumes de contratos (2 de venda, 2 de compra)
       
            Row rowUp = poolsheet.createRow(2+2*x);
            Row rowDown = poolsheet.createRow(3+2*x);
            
            rowUp.createCell(1).setCellValue("€/MWh");
            rowDown.createCell(1).setCellValue("MW");
            
            poolsheet.addMergedRegion(new CellRangeAddress(2+x*2,3+x*2,0,0));
            poolsheet.addMergedRegion(new CellRangeAddress(2+x*2,3+x*2,26,26));
     
            rowUp.createCell(0).setCellValue("Seller "+(x+1));            
            rowUp.createCell(26).setCellValue(ProducerScheduling.CommitedID.get(x));
                 
            for(int y = 0; y<HORIZON; y++){
                rowUp.createCell(2+y).setCellValue(String.valueOf(PoolOffers.get(x).getMarginalCost(y)));
                rowDown.createCell(2+y).setCellValue(String.valueOf(PoolOffers.get(x).getVolume(y)));
                
            }
        }
try{   
        FileOutputStream fos = new FileOutputStream("files\\"+Name+"\\input.xlsx");
        workbook.write(fos);
        fos.close();
        
        PoolOffers.clear();
        ProducerScheduling.CommitedID.clear();
        System.out.print("ok!");
        
}catch(FileNotFoundException e){
    e.printStackTrace();
}catch(IOException e){
    e.printStackTrace();
}
        
        
    }
    
   
   public static void LoadGencoINFO(String Name) throws IOException{
    
        File inputWorkbook = new File("files\\"+Name+"\\morada.xls");
        Workbook w;
        try{
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);
            int row = 0;
            
                Cell name = sheet.getCell(0,row);
                EnterGENCO.Name1.setText(name.getContents());
                EnterGENCO.personal_info.add(name.getContents());
                Cell address1 = sheet.getCell(1,row);
                EnterGENCO.Address1.setText(address1.getContents());
                EnterGENCO.personal_info.add(address1.getContents());
                Cell Address2 = sheet.getCell(2,row);
                EnterGENCO.Address2.setText(Address2.getContents());
                EnterGENCO.personal_info.add(Address2.getContents());
                Cell mail1 = sheet.getCell(3,row);
                EnterGENCO.mail1.setText(mail1.getContents());
                EnterGENCO.personal_info.add(mail1.getContents());
            
        
        }catch(BiffException e){
            e.printStackTrace();
        }
    }
   
   public static void LoadHydro() throws IOException{
        
        File inputWorkbook = new File("Centrais Hidricas.xls");
        Workbook HydroUnits;
        try{
            HydroUnits = Workbook.getWorkbook(inputWorkbook);
    
            //HYDRO DATA
            Sheet HydroData = HydroUnits.getSheet(0);
            AddGenerator.preHydroData = new double[HydroData.getRows()-1][8];
            AddGenerator.preLinearization = new double[HydroData.getRows()-1][5];
            AddGenerator.preOutputLimits = new double[HydroData.getRows()-1][4];
            AddGenerator.curves = new double[HydroData.getRows()-1];
            AddGenerator.preinflow = new double[HydroData.getRows()-1];
            AddGenerator.precosts = new double[HydroData.getRows()-1][2];

            for(int u = 1; u < HydroData.getRows(); u++){
                for(int c = 0; c < HydroData.getColumns(); c++){
                    Cell cell = HydroData.getCell(c,u);                   
                    AddGenerator.preHydroData[u-1][c] = Double.valueOf(cell.getContents());
                }
            } 

            HydroUnits = Workbook.getWorkbook(inputWorkbook);
            
            //PIECEWISE LINEAR APPROXIMATION OF THE PERFORMANCE CURVES
            Sheet Linearization = HydroUnits.getSheet(1);
            for(int u = 1; u < Linearization.getRows(); u++){
                for(int c = 0; c < Linearization.getColumns()-1; c++){
                    Cell cell = Linearization.getCell(c,u);
                    AddGenerator.preLinearization[u-1][c] = Double.parseDouble(cell.getContents());
                }                                   
                Cell curv = Linearization.getCell(Linearization.getColumns()-1,u);
                AddGenerator.curves[u-1] = Double.valueOf(curv.getContents());
            }
            
            //POWER OUTPUT LIMITS
            Sheet PoutLim = HydroUnits.getSheet(2);
            for(int u = 1; u < PoutLim.getRows(); u++){
                for(int c = 0; c < PoutLim.getColumns(); c++){
                    Cell cell = PoutLim.getCell(c,u);
                    AddGenerator.preOutputLimits[u-1][c] = Double.valueOf(cell.getContents());
                }                    
            }  
            
            //Hourly (Constant) Inflow
            Sheet inflow = HydroUnits.getSheet(3);
            for(int u = 1; u < inflow.getRows(); u++){
                Cell cell = inflow.getCell(0,u);
                AddGenerator.preinflow[u-1] = Double.valueOf(cell.getContents());
            } 
            
            //Costs
            Sheet costs = HydroUnits.getSheet(4);
            for(int u = 1; u < costs.getRows(); u++){
                Cell cell1 = costs.getCell(0,u);
                AddGenerator.precosts[u-1][0] = Double.valueOf(cell1.getContents());
                Cell cell2 = costs.getCell(1,u);
                AddGenerator.precosts[u-1][1] = Double.valueOf(cell2.getContents());
            }          
            
        }catch(BiffException e){
            e.printStackTrace();
        }
    }        
    
   public static void LoadThermal() throws IOException{
        
        File inputWorkbook = new File("Centrais Termicas.xls");
        Workbook ThermalUnits;
        try{
            ThermalUnits = Workbook.getWorkbook(inputWorkbook);
                        
            //THERMAL UNITS DATA
            Sheet ThermalData = ThermalUnits.getSheet(0);
            AddGenerator.preThermalData = new double[ThermalData.getRows()][10];
            AddGenerator.preThermalcosts = new double[ThermalData.getRows()][2];
            AddGenerator.preThermalFuelCons = new double[ThermalData.getRows()][2];
            AddGenerator.preThermalEMISSIONS = new double[ThermalData.getRows()][2];
            AddGenerator.preThermalFuelindex = new String[ThermalData.getRows()];
            
            
            for(int u = 0; u < ThermalData.getRows()-2; u++){
                
                Cell cost5 = ThermalData.getCell(0,u+2);
                AddGenerator.preThermalFuelindex[u] = cost5.getContents();
                
                Cell cost1 = ThermalData.getCell(11,u+2);
                AddGenerator.preThermalcosts[u][0] = Double.valueOf(cost1.getContents());
                Cell cost2 = ThermalData.getCell(12,u+2);
                AddGenerator.preThermalcosts[u][1] = Double.valueOf(cost2.getContents());
                
                Cell em1 = ThermalData.getCell(13,u+2);
                AddGenerator.preThermalFuelCons[u][0] = Double.valueOf(em1.getContents());
                Cell em2 = ThermalData.getCell(14,u+2);
                AddGenerator.preThermalFuelCons[u][1] = Double.valueOf(em2.getContents());               
                for(int c = 1; c <= 10; c++){
                    Cell cell = ThermalData.getCell(c,u+2);
                    AddGenerator.preThermalData[u][c-1] = Double.valueOf(cell.getContents());                     
                }
                
                Cell cell2 = ThermalData.getCell(15,u+2);
                AddGenerator.preThermalEMISSIONS[u][0] = Double.valueOf(cell2.getContents());  
               // AddGenerator.preThermalEMISSIONS[u][0] = cell2.getContents();  
                Cell cell3 = ThermalData.getCell(16,u+2);
                AddGenerator.preThermalEMISSIONS[u][1] = Double.valueOf(cell3.getContents());  
                        
                        }
            
            //EMISSION PRICES
            Sheet EmissionsPrice = ThermalUnits.getSheet(1);
            Cell cell = EmissionsPrice.getCell(1,1);
            AddGenerator.preGasesPrice[0] = Double.valueOf(cell.getContents());
            Cell cell2 = EmissionsPrice.getCell(1,2);
            AddGenerator.preGasesPrice[1] = Double.valueOf(cell2.getContents());
         
            
        }catch(BiffException e){
            e.printStackTrace();
        }
    }        
    
   public static void LoadWind() throws IOException{
    
     File inputWorkbook = new File("Centrais Eolicas.xls");
        Workbook WindUnits;
        try{
            WindUnits = Workbook.getWorkbook(inputWorkbook);
                        
            //WIND UNITS DATA
            Sheet WindData = WindUnits.getSheet(0);
            AddGenerator.preWindData = new double[WindData.getRows()-1][2];
            AddGenerator.preWindForecast = new double[24][WindData.getRows()];
            AddGenerator.preWindCosts = new double[WindData.getRows()-1][2];

            for(int u = 1; u < WindData.getRows(); u++){
                for(int c = 0; c < WindData.getColumns()-2; c++){
                    Cell cell = WindData.getCell(c,u);
                    AddGenerator.preWindData[u-1][c] = Double.valueOf(cell.getContents());
            }
                //COSTS
                    Cell cell1 = WindData.getCell(2,u);
                    AddGenerator.preWindCosts[u-1][0] = Double.valueOf(cell1.getContents());
                    Cell cell2 = WindData.getCell(3,u);
                    AddGenerator.preWindCosts[u-1][1] = Double.valueOf(cell2.getContents());
            }
                       
    
             //Production
            Sheet Production = WindUnits.getSheet(1);
            for(int u = 1; u < Production.getRows(); u++){
                for(int c = 0; c < Production.getColumns(); c++){
                    Cell cell = Production.getCell(c,u);
                    AddGenerator.preWindForecast[u-1][c] = Double.valueOf(cell.getContents());
            }
            }
            
          
            
            
    }catch(BiffException r){
        
    }
    }

   public static void LoadBCOffers() throws IOException{
    
        //GET ELEGIBLE FILES
        File folder = new File("");
        File[] listOfFiles = folder.listFiles();        
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".xls")) {
                for (int i = 0 ; i < AddGenerator.InfoGENCO.size(); i++){
                    if(AddGenerator.InfoGENCO.get(i).getName() == file.getName().concat(".xls")){
                        Lista_GENCOS_BC.add(AddGenerator.InfoGENCO.get(i).getName());
                    }}}}
        
 
        //SAVE CONTRACTS INFO   
        int nGENCOs = Lista_GENCOS_BC.size();
        
        for(int i = 0; i < nGENCOs; i++){
            File inputWorkbook = new File(Lista_GENCOS_BC.get(i)+".xls");
            Workbook w;
            
            try{
                w = Workbook.getWorkbook(inputWorkbook);
                Sheet sheet = w.getSheet(0);
             
                
                for(int t = 1; t <= 24; t++){
                    if(ProducerScheduling.Shiftingturn[t-1] == 1){
                        Cell cell1 = sheet.getCell(0,t); //Vsb1
                        Cell cell2 = sheet.getCell(1,t); //Vsb2
                        Cell cell3 = sheet.getCell(2,t); //Vbb1
                        Cell cell4 = sheet.getCell(3,t); //Vbb2
                        
                        Contract bc1 = new Contract(Lista_GENCOS_BC.get(i),"IDENT" ,"Sale" ,String.valueOf(t), Double.valueOf(cell1.getContents()), 5,false);
                        BCOffers.add(bc1);
                        Contract bc2 = new Contract(Lista_GENCOS_BC.get(i),"IDENT" ,"Sale" ,String.valueOf(t), Double.valueOf(cell2.getContents()), 5,false);
                        BCOffers.add(bc2);
                        Contract bc3 = new Contract(Lista_GENCOS_BC.get(i),"IDENT" ,"Purchase" ,String.valueOf(t), Double.valueOf(cell3.getContents()), 5,false);
                        BCOffers.add(bc3);
                        Contract bc4 = new Contract(Lista_GENCOS_BC.get(i),"IDENT" ,"Purchase" ,String.valueOf(t), Double.valueOf(cell4.getContents()), 5,false);
                        BCOffers.add(bc4);
                    }      
                }
        }catch(BiffException e){
            e.printStackTrace();
        }
        }
          int nSC = 1;   //Counters for Contracts identifiers
          int nBC = 1;  //Counters for Contracts identifiers
        for(int i = 0; i<BCOffers.size(); i++){
            if(BCOffers.get(i).getVolume()==0){
                BCOffers.remove(i);
            }
            if(BCOffers.get(i).getOrder()== "Sale"){
                BCOffers.get(i).setID("SC"+nSC);
                nSC++;
            }else{
                BCOffers.get(i).setID("BC"+nBC);
                nBC++;
            }
            
            
            }
            
    
    }
   
   public static void LoadGENERATOR_toTable(String genco_name) throws IOException{
    
    File inputWorkbook = new File("files\\"+genco_name+"\\morada.xls");
    Workbook genco;
        try{
            genco = Workbook.getWorkbook(inputWorkbook);
            Sheet ThermalData = genco.getSheet(1);
            Sheet HydroData = genco.getSheet(2);
            Sheet WindData = genco.getSheet(3);
            
            int nThermal = ThermalData.getRows();
            int nHydros = HydroData.getRows();
            int nWinds = WindData.getRows();
            int dataindex = 0;
            loadedGencoData = new String[(nThermal-2)+(nHydros-1)+(nWinds-2)][6];
            
//SHOW TABLE
            
            for(int u = 2; u < nThermal; u++){
                loadedGencoData[dataindex][0] = ThermalData.getCell(0,u).getContents();
                loadedGencoData[dataindex][1] = "Thermal";
                loadedGencoData[dataindex][2] = ThermalData.getCell(1,u).getContents();
                loadedGencoData[dataindex][3] = ThermalData.getCell(2,u).getContents();          
                loadedGencoData[dataindex][4] = ThermalData.getCell(3,u).getContents();
                loadedGencoData[dataindex][5] = ThermalData.getCell(12,u).getContents();  
                dataindex++;
            } 
            
            for(int u = 1; u < nHydros; u++){
                loadedGencoData[dataindex][0] = HydroData.getCell(0,u).getContents();
                loadedGencoData[dataindex][1] = "Hydro";
                loadedGencoData[dataindex][2] = "Water";
                loadedGencoData[dataindex][3] = null;          
                loadedGencoData[dataindex][4] = HydroData.getCell(18,u).getContents();
                loadedGencoData[dataindex][5] = HydroData.getCell(21,u).getContents();  
                dataindex++;
            } 

            for(int u = 2; u < nWinds; u++){
                loadedGencoData[dataindex][0] = WindData.getCell(0,u).getContents();
                loadedGencoData[dataindex][1] = "Wind";
                loadedGencoData[dataindex][2] = "Wind";
                loadedGencoData[dataindex][3] = WindData.getCell(1,u).getContents();;          
                loadedGencoData[dataindex][4] = WindData.getCell(2,u).getContents();
                loadedGencoData[dataindex][5] = WindData.getCell(4,u).getContents();  
                dataindex++;
            }
            
            
        }catch(BiffException e){
            e.printStackTrace();
        }
    
   }

   public static void SaveLoadedGenerator(String genco_name) throws IOException, MinMaxException, MediumValueException{
   
    File inputWorkbook = new File("files\\"+genco_name+"\\morada.xls");
        Workbook genco;
        try{
            genco = Workbook.getWorkbook(inputWorkbook);
            Sheet ThermalData = genco.getSheet(1);
            Sheet HydroData = genco.getSheet(2);
            Sheet WindData = genco.getSheet(3);
            Sheet PricesData = genco.getSheet(4);
            
            int nThermal = ThermalData.getRows();
            int nHydros = HydroData.getRows();
            int nWinds = WindData.getRows();
            //int dataindex = 0;
            //loadedGencoData = new String[(nThermal-2)+(nHydros-1)+(nWinds-2)][6];
            
//SHOW TABLE
            
            for(int u = 2; u < nThermal; u++){            
                DataThermal xx = new DataThermal(genco_name,ThermalData.getCell(0,u).getContents(),"Thermal",ThermalData.getCell(1,u).getContents(),Double.parseDouble(ThermalData.getCell(2,u).getContents()),Double.parseDouble(ThermalData.getCell(3,u).getContents()),Double.parseDouble(ThermalData.getCell(4,u).getContents()),Double.parseDouble(ThermalData.getCell(5,u).getContents()),0,0,Double.parseDouble(ThermalData.getCell(17,u).getContents()),Double.parseDouble(ThermalData.getCell(11,u).getContents()),Double.parseDouble(ThermalData.getCell(12,u).getContents()),false,Double.parseDouble(ThermalData.getCell(8,u).getContents()),Double.parseDouble(ThermalData.getCell(9,u).getContents()),Integer.parseInt(ThermalData.getCell(10,u).getContents()),Double.parseDouble(ThermalData.getCell(13,u).getContents()), Double.parseDouble(ThermalData.getCell(14,u).getContents()), Double.parseDouble(ThermalData.getCell(15,u).getContents()), Double.parseDouble(ThermalData.getCell(16,u).getContents()));
                AddGenerator.InfoSheet_t.add(xx);   
                
            } 
            
            for(int u = 1; u < nHydros; u++){
                DataHydro xx = new DataHydro(genco_name,HydroData.getCell(0,u).getContents(),"Hydro","Water",Double.parseDouble(HydroData.getCell(18,u).getContents()),Double.parseDouble(HydroData.getCell(18,u).getContents()),Double.parseDouble(HydroData.getCell(6,u).getContents()),Double.parseDouble(HydroData.getCell(7,u).getContents()),Double.parseDouble(HydroData.getCell(3,u).getContents()),Double.parseDouble(HydroData.getCell(1,u).getContents()),Double.parseDouble(HydroData.getCell(2,u).getContents()),Double.parseDouble(HydroData.getCell(21,u).getContents()),false,"",Double.parseDouble(HydroData.getCell(9,u).getContents()),Double.parseDouble(HydroData.getCell(10,u).getContents()),Double.parseDouble(HydroData.getCell(11,u).getContents()),Double.parseDouble(HydroData.getCell(12,u).getContents()),Double.parseDouble(HydroData.getCell(13,u).getContents()),Double.parseDouble(HydroData.getCell(15,u).getContents()),Double.parseDouble(HydroData.getCell(16,u).getContents()),Double.parseDouble(HydroData.getCell(17,u).getContents()),Double.parseDouble(HydroData.getCell(18,u).getContents()),Double.parseDouble(HydroData.getCell(14,u).getContents()),Double.parseDouble(HydroData.getCell(8,u).getContents()),Double.parseDouble(HydroData.getCell(19,u).getContents()),Double.parseDouble(HydroData.getCell(4,u).getContents()),Double.parseDouble(HydroData.getCell(5,u).getContents()),Double.parseDouble(HydroData.getCell(20,u).getContents()),Double.parseDouble(HydroData.getCell(22,u).getContents()));
                AddGenerator.InfoSheet_h.add(xx); 
            } 

            for(int u = 2; u < nWinds; u++){
                double [] forecastWind = new double [24];
                for (int t = 0; t<HORIZON; t++){
                     forecastWind[t] = Double.parseDouble(WindData.getCell(5+t, u).getContents());
                }                
                DataWind xx = new DataWind(genco_name,WindData.getCell(0, u).getContents(),"Wind","",Double.parseDouble(WindData.getCell(1, u).getContents()),Double.parseDouble(WindData.getCell(2, u).getContents()),Double.parseDouble(WindData.getCell(3, u).getContents()),Double.parseDouble(WindData.getCell(4, u).getContents()),false, forecastWind);
                AddGenerator.InfoSheet_w.add(xx);    
            }
            
            double [] pool = new double[24];
            double [] bc_sale = new double[4];
            double [] bc_purchase = new double[4];
            
            for(int s = 0; s < 4; s++){
                bc_sale[s] = Double.parseDouble(PricesData.getCell(1, 1+s).getContents());
                //bc_purchase[s] = Double.parseDouble(PricesData.getCell(1, 1+s).getContents());
            }
            for (int t = 0; t < HORIZON; t++){
                pool[t] = Double.parseDouble(PricesData.getCell(0, 1+t).getContents()); 
            }
            
          AddGenerator.InfoGENCO.get(AddGenerator.InfoGENCO.size()-1).setBCPrices(bc_sale);
          AddGenerator.InfoGENCO.get(AddGenerator.InfoGENCO.size()-1).setPoolForecast(pool);
          //  AddGenerator.InfoGENCO.get(AddGenerator.InfoGENCO.size()-1).setPoolForecast(t, Double.parseDouble(PricesData.getCell(0, 1+t).getContents()));
            
        }catch(BiffException e){
            e.printStackTrace();
        }
   
   
   
   
   }
   
}
    






