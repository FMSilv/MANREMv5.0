/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingModels;

import BilateralMarket.MarketClearing;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import scheduling.AddGenerator;
import scheduling.DataThermal;
import scheduling.DataWind;
import scheduling.SchedulingOutput;
import scheduling.ProducerScheduling;

/**
 *
 * @author AfonsoMCardoso
 */
public class Zhang {
    
    public double [][][] executeZhang (ArrayList<DataThermal> Lista_t, ArrayList<DataWind> Lista_w, int HORIZON, double CustoCO2, double CustoNO2,double [] Ppool, int [] Shiftingturn, double[][][] BCprices)throws LpSolveException{
     LpSolve lp;
           
        int j, ret = 0, nThermal = 0, nWind = 0;

        int varThermal = 4;
        int varWind = 2;
        
        for (int u = 0; u < Lista_t.size(); u++){
            if(Lista_t.get(u).isSelection()){ 
                nThermal++;
            }
        }
        for (int u = 0; u < Lista_w.size(); u++){
            if(Lista_w.get(u).isSelection()){ 
                nWind++;
            }
        }
        
        double Pmax = 0;
        int [] I0 = new int[nThermal];
        
        int y = 0;
        for (int u = 0; u < Lista_t.size(); u++){
            if(Lista_t.get(u).isSelection()){
                Pmax = Pmax + Lista_t.get(u).getMaxP();
                ProducerScheduling.CommitedID.add(Lista_t.get(u).getID());//Guarda nomes das centrais agendadas
                if(Lista_t.get(u).getPrevProd()>0){
                    I0[y] = 1;
                }else{
                    I0[y] = 0;        
                }
                y++;
            }
        }
        
        double BcVSize = Pmax * 0.3;              
        int nVar = (2 + (nThermal+nWind) + nThermal*varThermal + nWind*varWind);
        int Ncol = nVar*HORIZON;
        
        y = 0;
        for(int u = 0; u < Lista_w.size(); u++){
            if(Lista_w.get(u).isSelection()){
                ProducerScheduling.CommitedID.add(Lista_w.get(u).getID());//Guarda nomes das centrais agendadas
                y++;               
            }
        }  
        
            double output [][][] = new double[HORIZON][4][nThermal+nWind+1];
             
        /* create space large enough for one row */
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        double[] x = new double[Ncol];
          
        lp = LpSolve.makeLp(0, Ncol);
        if(lp.getLp() == 0)
        ret = 1; /* couldn't construct a new model... */
         
        lp.setAddRowmode(true);  
          
        int column = 1;
        if(ret == 0) {
            for(int t = 1; t <= HORIZON; t++){
              
                j = 0;                                      //1
                colno[j] = column;
                row[j++] = -1;
                
                colno[j] = column + 1;
                row[j++] = 1;

                
                for(int u = 1; u <= nThermal+nWind; u++){                    
                    colno[j] = column + 1 + u;//Vpool's
                    row[j++] = -1;
                }               
                
                for(int u = 0; u < nThermal; u++){           //producao           
                    colno[j] = column + 1 + (nThermal+nWind) + 1 + (u*varThermal);
                    row[j++] = 1;
                }
                for(int u = 0; u < nWind; u++){               //producao      
                    colno[j] = column + 1 + (nThermal+nWind) + 1 + (nThermal*varThermal) + (u*varWind);
                    row[j++] = 1;
                }
                lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);  //0 OU SOMA DE VOLUMES
                
                
                for(int u = 0; u < nThermal; u++){   //vpool>= 0;         (THERMAL)        
                    j = 0;
                    colno[j] = column + 2 + u;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                    j = 0;
                    colno[j] = column + 2 + u;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nThermal+nWind) + 1 + u*varThermal;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);                    
                }  
                
                for(int u = 0; u < nWind; u++){      //    (WIND)        
                    j = 0;                              //vpool>= 0;  
                    colno[j] = column + 1 + nThermal + 1 + u;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                    j = 0;                              //vpoool<=producao
                    colno[j] = column + 1 + nThermal + 1 + u;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nThermal+nWind) + nThermal*varThermal + 1 + u*varWind;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);                    
                }  
                
                
                
                if(Shiftingturn[t-1] == 1){
                    j = 0;                                      //2
                    colno[j] = column;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, BcVSize);
                    
                    j = 0;                                      //2
                    colno[j] = column+1;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, BcVSize);
                    
                }else{
                    j = 0;                                      //3
                    colno[j] = column;
                    row[j++] = 1;
                    colno[j] = column-nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                    
                    j = 0;                                      //3
                    colno[j] = column+1;
                    row[j++] = 1;
                    colno[j] = column+1-nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                }
                
                column = column + 1 + nThermal + nWind + 1;
                
        //////////////////////////////////////////////////////////////////////
        ///////////////////////   THERMAL UNITS    //////////////////////////////
        //////////////////////////////////////////////////////////////////////
                y = 0;
                for (int u = 0; u < Lista_t.size(); u++){
                    if(Lista_t.get(u).isSelection()){
                        lp.setBinary(column+1, true);       //4
                        lp.setBinary(column+2, true);       //5
                        lp.setBinary(column+3, true);       //6
                    
                        if(t == 1){
                            j = 0;                          //7
                            colno[j] = column;
                            row[j++] = 1;
                            lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_t.get(u).getRU() + Lista_t.get(u).getPrevProd());
                        
                            j = 0;                          //8
                            colno[j] = column;
                            row[j++] = 1;
                            lp.addConstraintex(j, row, colno, LpSolve.GE, Lista_t.get(u).getPrevProd() - Lista_t.get(u).getRD());
                    
                        }else{
                            j = 0;                          //9
                            colno[j] = column;
                            row[j++] = 1;
                            colno[j] = column - nVar;
                            row[j++] = -1;
                            lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_t.get(u).getRU());
                        
                            j = 0;                          //10
                            colno[j] = column;
                            row[j++] = 1;
                            colno[j] = column - nVar;
                            row[j++] = -1;
                            lp.addConstraintex(j, row, colno, LpSolve.GE, -Lista_t.get(u).getRD());
                        }
                
                        j = 0;                              //11
                        colno[j] = column;
                        row[j++] = 1;
                        colno[j] = column + 1;
                        row[j++] = -Lista_t.get(u).getMaxP();
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                    
                        j = 0;                              //12
                        colno[j] = column;
                        row[j++] = 1;
                        colno[j] = column + 1;
                        row[j++] = -Lista_t.get(u).getMinP();
                        lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                        if(t > 1){
                            j = 0;                              //13
                            colno[j] = column + 2;
                            row[j++] = 1;
                            colno[j] = column + 3;
                            row[j++] = -1;
                            colno[j] = column + 1;
                            row[j++] = -1;
                            colno[j] = column - nVar + 1;
                            row[j++] = 1;
                            lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                        }else{
                            j = 0;                              //13
                            colno[j] = column + 2;
                            row[j++] = 1;
                            colno[j] = column + 3;
                            row[j++] = -1;
                            colno[j] = column + 1;
                            row[j++] = -1;
                            lp.addConstraintex(j, row, colno, LpSolve.EQ, -I0[y]);                    
                        }
                    y++;
                    column = column + varThermal;                    
                    }
                }
                
        //////////////////////////////////////////////////////////////////////
        ///////////////////////    WIND UNITS    ///////////////////////////
        //////////////////////////////////////////////////////////////////////
                y = 0;
                for(int u = 0; u < Lista_w.size(); u++){
                    if(Lista_w.get(u).isSelection()){
                        j = 0;
                        colno[j] = column;                      //14
                        row[j++] = 1;
                        colno[j] = column + 1;
                        row[j++] = -Lista_w.get(u).getProduction(t-1);
                        lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                
                        lp.setBinary(column+1, true);           //15
   
                        j = 0;
                        colno[j] = column + 1;                  //16    INJECTA SEMPRE APESAR DOS CUSTOS
                        row[j++] = 1;
                        lp.addConstraintex(j, row, colno, LpSolve.EQ, 1);
                    
                        column = column + varWind;
                        y++;
                    }
                }
            }

          
    lp.setAddRowmode(false);
    
    
//////////////////////////////////////////////////////////////////////
///////////////////////     OBJECTIVE      ///////////////////////////
//////////////////////////////////////////////////////////////////////                

        column = 1;
        j = 0;
        for(int t = 1; t <= HORIZON; t++){
            colno[j] = column;                  //17
            row[j++] = BCprices[t-1][0][0];
            
            colno[j] = column+1;                  //17
            row[j++] = -BCprices[t-1][1][0];

            for (int i = 1; i <= nThermal + nWind; i++){
                colno[j] = column + 1 + i;              //18
                row[j++] = Ppool[t-1];
            }
            
            column = column + 1 + nThermal + nWind + 1;
            
            
            for(int u = 0; u < Lista_t.size(); u++){  
                if(Lista_t.get(u).isSelection()){
                    colno[j] = column;              
                    row[j++] = -(Lista_t.get(u).getVCost()+Lista_t.get(u).getFuelCvar()*(Lista_t.get(u).getEmCO2()*CustoCO2 + Lista_t.get(u).getEmNO2()*CustoNO2));
                    colno[j] = column + 1;              
                    row[j++] = -(Lista_t.get(u).getFCost()+Lista_t.get(u).getFuelCfixed()*(Lista_t.get(u).getEmCO2()*CustoCO2 + Lista_t.get(u).getEmNO2()*CustoNO2));                    
                    //     if(t > 1){
                    colno[j] = column + 2;              
                    row[j++] = -Lista_t.get(u).getSUcost();
                    colno[j] = column + 3;              
                    row[j++] = -Lista_t.get(u).getSDcost();
                 //   }                
                
                    column = column + varThermal;
                }
            }
            
          
            for(int u = 0; u < Lista_w.size(); u++){
                if(Lista_w.get(u).isSelection()){
                    colno[j] = column;              
                    row[j++] = -Lista_w.get(u).getVCost();
                    colno[j] = column + 1;              
                    row[j++] = -Lista_w.get(u).getFCost();            
            
                    column = column + varWind;
                }    
            }            
        }
      
        
        
        lp.setObjFnex(j, row, colno);
}
          
 
          if(ret == 0) {
            /* set the object direction to maximize */
            lp.setMaxim();
            lp.setVerbose(LpSolve.IMPORTANT);   /* Only want to see important messages on screen while solving */

            ret = lp.solve();
            if(ret == LpSolve.OPTIMAL)
              ret = 0;
            else
              ret = 5;
          }

          if(ret == 0) { /* a solution is calculated */
           
            System.out.println("Profit: " + lp.getObjective());     /* objective value */
            
            scheduling.SchedulingOutput.FProfit = lp.getObjective();
            
            
            lp.getVariables(row);  /* variable values */
            
            for(j = 0; j < Ncol; j++){
                x[j]=row[j];
            }
            
            column = 0;    
            for(int t = 1; t <= HORIZON; t++){
                System.out.println("t = " + t);
                System.out.println("VSC: " + x[column] + "\n");
                output[t-1][0][0] = x[column]; //VSC
                
                System.out.println("VBC: " + x[column+1] + "\n");
                output[t-1][2][0] = x[column+1]; //VBC
                
                
                for (int i = 1; i <= nThermal+nWind; i++){
                    System.out.println("Vpool"+i+": " + x[column + 1 + i] + "  ");
                    output[t-1][0][i] = x[column+1+i]; //Vpool
                }
                column = column + 1 + nThermal + nWind + 1;
                
                y = 0;
                for(int uT = 1; uT <= Lista_t.size(); uT++){
                    if(Lista_t.get(uT-1).isSelection()){
                        System.out.println("Vt" + y+1 + " = " + x[column] + "     I = " + x[column+1] + "    Y = " + x[column+2] + "    Z = " + x[column+3]);
                        output[t-1][1][y+1] = x[column]; //produção
                        output[t-1][2][y+1] = Lista_t.get(uT-1).getVCost(); //marginal cost
                        column = column + varThermal;
                        y++;
                    }
                }
                
                y = 0;
                for(int uW = 1; uW <= Lista_w.size(); uW++){
                    if(Lista_w.get(uW-1).isSelection()){
                        System.out.println("Vw" + y+1 + " = " + x[column] + "    I = " + x[column+1]);
                        output[t-1][1][nThermal+y+1] = x[column]; //produção
                        output[t-1][2][nThermal+y+1] = Lista_w.get(uW-1).getVCost(); //marginal cost
                        column = column + varWind;
                        y++;
                    }
                }
            System.out.print("\n");
            }
          }


          return output;

}
           
        }
