/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SchedulingModels;

import java.util.ArrayList;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import scheduling.DataHydro;
import scheduling.DataThermal;
import scheduling.DataWind;
import scheduling.ProducerScheduling;

/**
 *
 * @author AfonsoMCardoso
 */
public class Afonso_Thermal_Hydro_Wind {
    
    
    public double [][][] executeAfonso_NOcascade (ArrayList<DataThermal> Lista_t,ArrayList<DataHydro> Lista_h, ArrayList<DataWind> Lista_w, int HORIZON, double CustoCO2, double CustoNO2,double [] Ppool, int [] Shiftingturn, double [][][] BCprices)throws LpSolveException{
     LpSolve lp;
           
        int j, ret = 0,nThermal = 0,nHydro = 0,nWind = 0; double Pmax = 0;
          
        for(int i = 0; i<Lista_t.size(); i++){
            if(Lista_t.get(i).isSelection()){
                nThermal++;
            }
        }
        
        for(int i = 0; i<Lista_h.size(); i++){
            if(Lista_h.get(i).isSelection()){
                nHydro++;
            }
        }
        
        for(int i = 0; i<Lista_w.size(); i++){
            if(Lista_w.get(i).isSelection()){
                nWind++;
            }
        }
  
        int varThermal = 4; 
        int varHydro = 16;
        int varWind = 2;
        
        double M = 0.0036;
        
        int [] It0 = new int[nThermal];
        int [] Ih0 = new int[nHydro];
        
        int y = 0;
        for (int u = 0; u < Lista_t.size(); u++){
            if(Lista_t.get(u).isSelection()){
                Pmax = Pmax + Lista_t.get(u).getMaxP();
                ProducerScheduling.CommitedID.add(Lista_t.get(u).getID());//Guarda nomes das centrais agendadas
                if(Lista_t.get(u).getPrevProd()>0){
                    It0[y] = 1;
                }else{
                    It0[y] = 0;
                }
                y++;
            }
        }
        
        y = 0;
            for (int u = 0; u < Lista_h.size(); u++){
                if(Lista_h.get(u).isSelection()){
                    Pmax = Pmax + Lista_h.get(u).getPi();
                    ProducerScheduling.CommitedID.add(Lista_h.get(u).getID());//Guarda nomes das centrais agendadas
                    if(Lista_h.get(u).getPrevProduction()>0){
                        Ih0[y] = 1;
                    }else{
                        Ih0[y] = 0;
                    }
                    y++;
                }
            }
        
        
        for (int u = 0; u < Lista_w.size(); u++){
            if(Lista_w.get(u).isSelection()){
                Pmax = Pmax + Lista_w.get(u).getMaxP();
                ProducerScheduling.CommitedID.add(Lista_w.get(u).getID());//Guarda nomes das centrais agendadas
            }
        }
        
        double BcVSize = Pmax * 0.3;         
        int nVar = (2 + (nThermal+nHydro+nWind) + nThermal*varThermal + nHydro*varHydro + nWind*varWind);
        int Ncol = nVar*HORIZON;
        
        int [][] WindPower = new int [HORIZON][nWind];
        
        
        y = 0;
        for(int u = 0; u < Lista_w.size(); u++){
            if(Lista_w.get(u).isSelection()){
                for(int t = 0; t < HORIZON; t++){
                    WindPower[t][y] = 0 + (int)(Math.random()*Lista_w.get(u).getMaxP()); 
                }
                y++;
            }
        }
          
               
        double output [][][] = new double[HORIZON][4][nThermal+nHydro+nWind+1];
               
          
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
                
                j = 0;                                      //Selling Block
                colno[j] = column;
                row[j++] = -1;
                
                colno[j] = column + 1;                      //Buying Block
                row[j++] = 1;

                
                for(int u = 1; u <= nThermal+nHydro+nWind; u++){                    
                    colno[j] = column + 1 + u;//Vpool's
                    row[j++] = -1;
                }               
                
                for(int u = 0; u < nThermal; u++){           //producao (Térmica)          
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + 1 + (u*varThermal);
                    row[j++] = 1;
                }
                for(int u = 0; u < nHydro; u++){               //producao (Hídrica)      
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + (nThermal*varThermal) + 1 + (u*varHydro);
                    row[j++] = 1;
                }
                for(int u = 0; u < nWind; u++){               //producao (Eólica)      
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + (nThermal*varThermal) + (nHydro*varHydro) + 1  + (u*varWind);
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
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + 1 + u*varThermal;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);                    
                }  
                
                for(int u = 0; u < nHydro; u++){      //    (HYDRO)        
                    j = 0;                              //vpool>= 0;  
                    colno[j] = column + 1 + nThermal + 1 + u;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                    j = 0;                              //vpoool<=producao
                    colno[j] = column + 1 + nThermal + 1 + u;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + nThermal*varThermal + 1 + u*varHydro;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);                    
                } 
                
                for(int u = 0; u < nWind; u++){      //    (WIND)        
                    j = 0;                              //vpool>= 0;  
                    colno[j] = column + 1 + nThermal + nHydro + 1 + u;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                    j = 0;                              //vpool<=producao
                    colno[j] = column + 1 + nThermal + nHydro + 1 + u;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + nThermal*varThermal + nHydro*varHydro + 1 + u*varWind;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);                    
                }  
                
                
                
                if(Shiftingturn[t-1] == 1){
                    j = 0;                                      //2
                    colno[j] = column;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, BcVSize);
                    
                    j = 0;                                      //2
                    colno[j] = column;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                    j = 0;                                      //2
                    colno[j] = column + 1;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, BcVSize);
                    
                    j = 0;                                      //2
                    colno[j] = column + 1;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                }else{
                    j = 0;                                      //3
                    colno[j] = column;
                    row[j++] = 1;
                    colno[j] = column-nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                    
                    j = 0;                                      //3
                    colno[j] = column + 1;
                    row[j++] = 1;
                    colno[j] = column + 1 - nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                }
                
                column = column + 1 + nThermal + nHydro + nWind + 1;
                
                
        //////////////////////////////////////////////////////////////////////
        ///////////////////////   THERMAL UNITS    ///////////////////////////
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
                            lp.addConstraintex(j, row, colno, LpSolve.EQ, -It0[y]);                    
                        }
                    y++;
                    column = column + varThermal;                    
                    }
                }
                
///////////////////////////////////////////////////////////////////////////////
////////////////////////              HYDRO         ///////////////////////////
///////////////////////////////////////////////////////////////////////////////         
              y = 0;
                for(int u = 0; u < Lista_h.size(); u++){
                    if(Lista_h.get(u).isSelection()){
                        
                        j = 0;                          //6
                        colno[j] = column + 1;
                        row[j++] = -1;                
                        colno[j] = column + 13;
                        row[j++] = Lista_h.get(u).getMediumlevel();                
                        colno[j] = column + 14;
                        row[j++] = (-Lista_h.get(u).getMediumlevel() + Lista_h.get(u).getUpperlevel());                
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //7
                        colno[j] = column + 1;
                        row[j++] = 1;               
                        colno[j] = column + 13;
                        row[j++] = Lista_h.get(u).getMediumlevel() - Lista_h.get(u).getUpperlevel();                
                        colno[j] = column + 14;
                        row[j++] = Lista_h.get(u).getUpperlevel() - Lista_h.get(u).getMaxReserve();                
                        lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_h.get(u).getMediumlevel());
                
                
                        j = 0;                          //8
                        colno[j] = column + 13;
                        row[j++] = -1;                
                        colno[j] = column + 14;
                        row[j++] = 1;                
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //9
                        colno[j] = column + 1;
                        row[j++] = -1;
                        lp.addConstraintex(j, row, colno, LpSolve.LE, -Lista_h.get(u).getMinReserve());
                
                
                        j = 0;                          //10
                        colno[j] = column;
                        row[j++] = 1;                
                        colno[j] = column + 4;
                        row[j++] = -Lista_h.get(u).getP01();               
                        colno[j] = column + 5;
                        row[j++] = -Lista_h.get(u).getPl1();               
                        colno[j] = column + 6;
                        row[j++] = -Lista_h.get(u).getPl2();                
                        colno[j] = column + 7;
                        row[j++] = -Lista_h.get(u).getPl3();               
                        colno[j] = column + 8;
                        row[j++] = -Lista_h.get(u).getPl4();                
                        colno[j] = column + 13;
                        row[j++] = -Lista_h.get(u).getPi();                
                        colno[j] = column + 14;
                        row[j++] = -Lista_h.get(u).getPi();                
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //11
                        colno[j] = column;
                        row[j++] = -1;                
                        colno[j] = column + 4;
                        row[j++] = Lista_h.get(u).getP01();                
                        colno[j] = column + 5;
                        row[j++] = Lista_h.get(u).getPl1();                
                        colno[j] = column + 6;
                        row[j++] = Lista_h.get(u).getPl2();                
                        colno[j] = column + 7;
                        row[j++] = Lista_h.get(u).getPl3();               
                        colno[j] = column + 8;
                        row[j++] = Lista_h.get(u).getPl4();                
                        colno[j] = column + 13;
                        row[j++] = -Lista_h.get(u).getPi();               
                        colno[j] = column + 14;
                        row[j++] = -Lista_h.get(u).getPi();                
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //12
                        colno[j] = column;
                        row[j++] = 1;                
                        colno[j] = column + 4;
                        row[j++] = -Lista_h.get(u).getP02();                
                        colno[j] = column + 5;
                        row[j++] = -(Lista_h.get(u).getPl1() + Lista_h.get(u).getCurve());                
                        colno[j] = column + 6;
                        row[j++] = -(Lista_h.get(u).getPl2() + Lista_h.get(u).getCurve());                
                        colno[j] = column + 7;
                        row[j++] = -(Lista_h.get(u).getPl3() + Lista_h.get(u).getCurve());                
                        colno[j] = column + 8;
                        row[j++] = -(Lista_h.get(u).getPl4() + Lista_h.get(u).getCurve());                
                        colno[j] = column + 13;
                        row[j++] = Lista_h.get(u).getPi();               
                        colno[j] = column + 14;
                        row[j++] = -Lista_h.get(u).getPi();                
                        lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_h.get(u).getPi());
                
                
                        j = 0;                          //13
                        colno[j] = column;
                        row[j++] = -1;                
                        colno[j] = column + 4;
                        row[j++] = Lista_h.get(u).getP02();               
                        colno[j] = column + 5;
                        row[j++] = (Lista_h.get(u).getPl1() + Lista_h.get(u).getCurve());               
                        colno[j] = column + 6;
                        row[j++] = (Lista_h.get(u).getPl2() + Lista_h.get(u).getCurve());                
                        colno[j] = column + 7;
                        row[j++] = (Lista_h.get(u).getPl3() + Lista_h.get(u).getCurve());                
                        colno[j] = column + 8;
                        row[j++] = (Lista_h.get(u).getPl4() + Lista_h.get(u).getCurve());                
                        colno[j] = column + 13;
                        row[j++] = Lista_h.get(u).getPi();                
                        colno[j] = column + 14;
                        row[j++] = -Lista_h.get(u).getPi();               
                        lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_h.get(u).getPi());
                
                
                        j = 0;                          //14
                        colno[j] = column;
                        row[j++] = 1;                
                        colno[j] = column + 4;
                        row[j++] = -Lista_h.get(u).getP03();               
                        colno[j] = column + 5;
                        row[j++] = -(Lista_h.get(u).getPl1() + 2*Lista_h.get(u).getCurve());               
                        colno[j] = column + 6;
                        row[j++] = -(Lista_h.get(u).getPl2() + 2*Lista_h.get(u).getCurve());                
                        colno[j] = column + 7;
                        row[j++] = -(Lista_h.get(u).getPl3() + 2*Lista_h.get(u).getCurve());                
                        colno[j] = column + 8;
                        row[j++] = -(Lista_h.get(u).getPl4() + 2*Lista_h.get(u).getCurve());               
                        colno[j] = column + 13;
                        row[j++] = Lista_h.get(u).getPi();                
                        colno[j] = column + 14;
                        row[j++] = Lista_h.get(u).getPi();               
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 2*Lista_h.get(u).getPi());
                
                
                        j = 0;                          //15
                        colno[j] = column;
                        row[j++] = -1;               
                        colno[j] = column + 4;
                        row[j++] = Lista_h.get(u).getP03();               
                        colno[j] = column + 5;
                        row[j++] = (Lista_h.get(u).getPl1() + 2*Lista_h.get(u).getCurve());                
                        colno[j] = column + 6;
                        row[j++] = (Lista_h.get(u).getPl2() + 2*Lista_h.get(u).getCurve());                
                        colno[j] = column + 7;
                        row[j++] = (Lista_h.get(u).getPl3() + 2*Lista_h.get(u).getCurve());                
                        colno[j] = column + 8;
                        row[j++] = (Lista_h.get(u).getPl4() + 2*Lista_h.get(u).getCurve());               
                        colno[j] = column + 13;
                        row[j++] = Lista_h.get(u).getPi();               
                        colno[j] = column + 14;
                        row[j++] = Lista_h.get(u).getPi();               
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 2*Lista_h.get(u).getPi());
                
                
                        j = 0;                          //16
                        colno[j] = column + 5;
                        row[j++] = 1;               
                        colno[j] = column + 4;
                        row[j++] = -Lista_h.get(u).getUl();              
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //17
                        colno[j] = column + 5;
                        row[j++] = -1;               
                        colno[j] = column + 10;
                        row[j++] = Lista_h.get(u).getUl();              
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //18
                        colno[j] = column + 6;
                        row[j++] = 1;                
                        colno[j] = column + 10;
                        row[j++] = -Lista_h.get(u).getUl();               
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //19
                        colno[j] = column + 7;
                        row[j++] = 1;               
                        colno[j] = column + 11;
                        row[j++] = -Lista_h.get(u).getUl();              
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //20
                        colno[j] = column + 8;
                        row[j++] = 1;                
                        colno[j] = column + 12;
                        row[j++] = -Lista_h.get(u).getUl();               
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //21
                        colno[j] = column + 6;
                        row[j++] = -1;
                        colno[j] = column + 11;
                        row[j++] = Lista_h.get(u).getUl();
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //22
                        colno[j] = column + 7;
                        row[j++] = -1;
                        colno[j] = column + 12;
                        row[j++] = Lista_h.get(u).getUl();
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                        j = 0;                          //23
                        colno[j] = column + 8;
                        row[j++] = -1;
                        colno[j] = column + 15;
                        row[j++] = Lista_h.get(u).getUl();
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
    
                
                        j = 0;                          //24
                        colno[j] = column + 9;
                        row[j++] = 1;               
                        colno[j] = column + 5;
                        row[j++] = -1;               
                        colno[j] = column + 6;
                        row[j++] = -1;                
                        colno[j] = column + 7;
                        row[j++] = -1;                
                        colno[j] = column + 8;
                        row[j++] = -1;                
                        colno[j] = column + 4;
                        row[j++] = -Lista_h.get(u).getMinDisch();                             
                        lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                
   
                        if(t == 1){                 
                            j = 0;
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            colno[j] = column + 9;
                            row[j++] = M;  
                            lp.addConstraintex(j, row, colno, LpSolve.EQ, Lista_h.get(u).getInitReserve() + Lista_h.get(u).getInflow());
                        }else{                      
                            j = 0;
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            colno[j] = column + 9;
                            row[j++] = M;
                            colno[j] = column - nVar + 1;
                            row[j++] = -1; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInflow());                  
                        }               
                
                        if(t <= 1){
                            j = 0;                      //29
                            colno[j] = column + 2;
                            row[j++] = 1;  
                            colno[j] = column + 3;
                            row[j++] = -1;
                            colno[j] = column + 4;
                            row[j++] = -1; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,-Ih0[y]);
                    
                            j = 0;                      //30
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInitReserve());
                        }else{                          //31
                            j = 0;
                            colno[j] = column + 2;
                            row[j++] = 1;  
                            colno[j] = column + 3;
                            row[j++] = -1;
                            colno[j] = column + 4;
                            row[j++] = -1; 
                            colno[j] = column - nVar + 4;
                            row[j++] = 1; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,0);
                        }
                
                        j = 0;                          //32
                        colno[j] = column + 1;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.LE,Lista_h.get(u).getMaxReserve());
                
                        j = 0;                          //33
                        colno[j] = column + 1;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,Lista_h.get(u).getMinReserve());
                
                        lp.setBinary(column+2, true);   //34
                        lp.setBinary(column+3, true);   //35
                        lp.setBinary(column+4, true);   //36
                        lp.setBinary(column+10, true);  //37
                        lp.setBinary(column+11, true);  //38
                        lp.setBinary(column+12, true);  //39
                        lp.setBinary(column+13, true);  //40
                        lp.setBinary(column+14, true);  //41
                        lp.setBinary(column+15, true);  //42
            
                        j = 0;                          //43
                        colno[j] = column;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                        j = 0;                          //44
                        colno[j] = column + 1;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                        j = 0;                          //45
                        colno[j] = column + 5;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                        j = 0;                          //46
                        colno[j] = column + 6;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                        j = 0;                          //47
                        colno[j] = column + 7;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                                
                        j = 0;                          //48
                        colno[j] = column + 8;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                        j = 0;                          //49
                        colno[j] = column + 9;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.GE,0);                
                
                        column = column + varHydro;    
                        y++;
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
                        row[j++] = -WindPower[t-1][y];
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
                
            
            }//end HORIZON LOOP

          
    lp.setAddRowmode(false);
    
    
//////////////////////////////////////////////////////////////////////
///////////////////////     OBJECTIVE      ///////////////////////////
//////////////////////////////////////////////////////////////////////                

        column = 1;
        j = 0;
        for(int t = 1; t <= HORIZON; t++){
            colno[j] = column;               
            row[j++] = BCprices[t-1][0][0];
            
            colno[j] = column+1;            
            row[j++] = -BCprices[t-1][1][0];


            for (int i = 1; i <= nThermal + nHydro + nWind; i++){
                colno[j] = column + 1 + i;              //18
                row[j++] = Ppool[t-1];
            }
            
            column = column + 1 + (nThermal + nHydro + nWind) + 1;
            
            
            for(int u = 0; u < Lista_t.size(); u++){  
                if(Lista_t.get(u).isSelection()){
                    colno[j] = column;              
                    row[j++] = -(Lista_t.get(u).getVCost()+Lista_t.get(u).getFuelCvar()*(Lista_t.get(u).getEmCO2()*CustoCO2 + Lista_t.get(u).getEmNO2()*CustoNO2));
                    colno[j] = column + 1;              
                    row[j++] = -(Lista_t.get(u).getFCost()+Lista_t.get(u).getFuelCfixed()*(Lista_t.get(u).getEmCO2()*CustoCO2 + Lista_t.get(u).getEmNO2()*CustoNO2));                    
                    
                    colno[j] = column + 2;              
                    row[j++] = -Lista_t.get(u).getSUcost();
                    colno[j] = column + 3;              
                    row[j++] = -Lista_t.get(u).getSDcost();
                             
                    column = column + varThermal;
                }
            }
            
            
            
            for(int u = 0; u < Lista_h.size(); u++){  
                if(Lista_h.get(u).isSelection()){
                    colno[j] = column + 2;                        //F2
                    row[j++] = -Lista_h.get(u).getStartupcost();
                
                    colno[j] = column;                          //F3
                    row[j++] = -Lista_h.get(u).getVCost();
                
                    colno[j] = column + 4;                      //F4
                    row[j++] = -Lista_h.get(u).getFCost();
            
                    column = column + varHydro;  
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
      
        
        
        lp.setObjFnex(j, row, colno);
}
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
           
            System.out.println("Profit: " + lp.getObjective() + " USD \n");     /* objective value */
            
            scheduling.SchedulingOutput.FProfit = lp.getObjective();
            
            lp.getVariables(row);  /* variable values */
            
            for(j = 0; j < Ncol; j++){
                x[j]=row[j];
            }
            
            column = 0;    
            for(int t = 1; t <= HORIZON; t++){
                System.out.println("t = " + t);
                System.out.println("VSC: " + x[column]);
                output[t-1][0][0] = x[column]; //VSC
                
                System.out.println("VBC: " + x[column + 1]);
                output[t-1][2][0] = x[column+1]; //VBC
                
                for (int i = 1; i <= nThermal+nHydro+nWind; i++){
                    System.out.println("Vpool"+i+": " + x[column + 1 + i] + "  ");
                    output[t-1][0][i] = x[column+1+i]; //Vpool
                }
                
                column = column + 1 + nThermal + nHydro + nWind + 1;
                
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
                for(int uH = 1; uH <= Lista_h.size(); uH++){
                    if(Lista_h.get(uH-1).isSelection()){
                        System.out.println("Vh"+ uH + " = " + x[column] + "    H = " + x[column+4] + "    Y = "+ x[column+2]); //Production 
                        output[t-1][1][nThermal+y+1] = x[column];  //Producao
                        output[t-1][2][nThermal+y+1] = Lista_h.get(uH-1).getVCost();  //MarginalCost
                        column = column + varHydro;
                        y++;
                    }
                }
                
    
                y = 0;
                for(int uW = 1; uW <= Lista_w.size(); uW++){
                    if(Lista_w.get(uW-1).isSelection()){
                        System.out.println("Vw" + y+1 + " = " + x[column] + "    I = " + x[column+1]);
                        output[t-1][1][nThermal+nHydro+y+1] = x[column]; //produção
                        output[t-1][2][nThermal+nHydro+y+1] = Lista_w.get(uW-1).getVCost(); //marginal cost
                        column = column + varWind;
                        y++;
                    }
                }
            System.out.print("\n");
            }        
}

          return output;

        }
    
    
    public double [][][] executeAfonso_cascade (ArrayList<DataThermal> Lista_t,ArrayList<DataHydro> Lista_h, ArrayList<DataWind> Lista_w, int HORIZON, double CustoCO2, double CustoNO2,double [] Ppool, int [] Shiftingturn, double [][][] BCprices, int delay)throws LpSolveException{
     LpSolve lp;
           
        int j, ret = 0,nThermal = 0,nHydro = 0,nWind = 0; double Pmax = 0;
          
        for(int i = 0; i<Lista_t.size(); i++){
            if(Lista_t.get(i).isSelection()){
                nThermal++;
            }
        }
        
        for(int i = 0; i<Lista_h.size(); i++){
            if(Lista_h.get(i).isSelection()){
                nHydro++;
            }
        }
        
        for(int i = 0; i<Lista_w.size(); i++){
            if(Lista_w.get(i).isSelection()){
                nWind++;
            }
        }
  
        int varThermal = 4; 
        int varHydro = 16;
        int varWind = 2;
        
        double M = 0.0036;
        
        int [] It0 = new int[nThermal];
        int [] Ih0 = new int[nHydro];
        
        int y = 0;
        for (int u = 0; u < Lista_t.size(); u++){
            if(Lista_t.get(u).isSelection()){
                Pmax = Pmax + Lista_t.get(u).getMaxP();
                ProducerScheduling.CommitedID.add(Lista_t.get(u).getID());//Guarda nomes das centrais agendadas
                if(Lista_t.get(u).getPrevProd()>0){
                    It0[y] = 1;
                }else{
                    It0[y] = 0;
                }
                y++;
            }
        }
        
        y = 0;
        while(y<nHydro){
            for (int u = 0; u < Lista_h.size(); u++){
                if(Lista_h.get(u).getCascadeorder() == String.valueOf(y+1)){
                    Pmax = Pmax + Lista_h.get(u).getPi();
                    ProducerScheduling.CommitedID.add(Lista_h.get(u).getID());//Guarda nomes das centrais agendadas
                    if(Lista_h.get(u).getPrevProduction()>0){
                        Ih0[y] = 1;
                    }else{
                        Ih0[y] = 0;
                    }
                    y++;
                }
            }
        }
        
        for (int u = 0; u < Lista_w.size(); u++){
            if(Lista_w.get(u).isSelection()){
                Pmax = Pmax + Lista_w.get(u).getMaxP();
                ProducerScheduling.CommitedID.add(Lista_w.get(u).getID());//Guarda nomes das centrais agendadas
            }
        }
        
        double BcVSize = Pmax * 0.3;         
        int nVar = (2 + (nThermal+nHydro+nWind) + nThermal*varThermal + nHydro*varHydro + nWind*varWind);
        int Ncol = nVar*HORIZON;
        
        int [][] WindPower = new int [HORIZON][nWind];
        
        
        y = 0;
        for(int u = 0; u < Lista_w.size(); u++){
            if(Lista_w.get(u).isSelection()){
                for(int t = 0; t < HORIZON; t++){
                    WindPower[t][y] = 0 + (int)(Math.random()*Lista_w.get(u).getMaxP()); 
                }
                y++;
            }
        }
          
               
        double output [][][] = new double[HORIZON][4][nThermal+nHydro+nWind+1];
               
          
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
                
                j = 0;                                      //Selling Block
                colno[j] = column;
                row[j++] = -1;
                
                colno[j] = column + 1;                      //Buying Block
                row[j++] = 1;

                
                for(int u = 1; u <= nThermal+nHydro+nWind; u++){                    
                    colno[j] = column + 1 + u;//Vpool's
                    row[j++] = -1;
                }               
                
                for(int u = 0; u < nThermal; u++){           //producao (Térmica)          
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + 1 + (u*varThermal);
                    row[j++] = 1;
                }
                for(int u = 0; u < nHydro; u++){               //producao (Hídrica)      
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + (nThermal*varThermal) + 1  + (u*varHydro);
                    row[j++] = 1;
                }
                for(int u = 0; u < nWind; u++){               //producao (Eólica)      
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + (nThermal*varThermal) + (nHydro*varHydro) + 1 + (u*varWind);
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
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + 1 + u*varThermal;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);                    
                }  
                
                for(int u = 0; u < nHydro; u++){      //    (HYDRO)        
                    j = 0;                              //vpool>= 0;  
                    colno[j] = column + 1 + nThermal + 1 + u;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                    j = 0;                              //vpoool<=producao
                    colno[j] = column + 1 + nThermal + 1 + u;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + nThermal*varThermal + 1 + u*varHydro;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);                    
                } 
                
                for(int u = 0; u < nWind; u++){      //    (WIND)        
                    j = 0;                              //vpool>= 0;  
                    colno[j] = column + 1 + nThermal + nHydro + 1 + u;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                    
                    j = 0;                              //vpool<=producao
                    colno[j] = column + 1 + nThermal + nHydro + 1 + u;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nThermal+nHydro+nWind) + nThermal*varThermal + nHydro*varHydro + 1 + u*varWind;
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
                
                column = column + 1 + nThermal + nHydro + nWind + 1;
                
                
        //////////////////////////////////////////////////////////////////////
        ///////////////////////   THERMAL UNITS    ///////////////////////////
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
                            lp.addConstraintex(j, row, colno, LpSolve.EQ, -It0[y]);                    
                        }
                    y++;
                    column = column + varThermal;                    
                    }
                }
                
///////////////////////////////////////////////////////////////////////////////
////////////////////////              HYDRO         ///////////////////////////
///////////////////////////////////////////////////////////////////////////////         
              y = 0;
        while(y<nHydro){
            for(int u = 0; u < Lista_h.size(); u++){
                if(Lista_h.get(u).getCascadeorder() == String.valueOf(y+1)){
                    j = 0;                          //6
                    colno[j] = column + 1;
                    row[j++] = -1;                
                    colno[j] = column + 13;
                    row[j++] = Lista_h.get(u).getMediumlevel();                
                    colno[j] = column + 14;
                    row[j++] = (-Lista_h.get(u).getMediumlevel() + Lista_h.get(u).getUpperlevel());                
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //7
                    colno[j] = column + 1;
                    row[j++] = 1;               
                    colno[j] = column + 13;
                    row[j++] = Lista_h.get(u).getMediumlevel() - Lista_h.get(u).getUpperlevel();                
                    colno[j] = column + 14;
                    row[j++] = Lista_h.get(u).getUpperlevel() - Lista_h.get(u).getMaxReserve();                
                    lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_h.get(u).getMediumlevel());
                
                
                    j = 0;                          //8
                    colno[j] = column + 13;
                    row[j++] = -1;                
                    colno[j] = column + 14;
                    row[j++] = 1;                
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //9
                    colno[j] = column + 1;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_h.get(u).getMinReserve());
                
                
                    j = 0;                          //10
                    colno[j] = column;
                    row[j++] = 1;                
                    colno[j] = column + 4;
                    row[j++] = -Lista_h.get(u).getP01();               
                    colno[j] = column + 5;
                    row[j++] = -Lista_h.get(u).getPl1();               
                    colno[j] = column + 6;
                    row[j++] = -Lista_h.get(u).getPl2();                
                    colno[j] = column + 7;
                    row[j++] = -Lista_h.get(u).getPl3();               
                    colno[j] = column + 8;
                    row[j++] = -Lista_h.get(u).getPl4();                
                    colno[j] = column + 13;
                    row[j++] = -Lista_h.get(u).getPi();                
                    colno[j] = column + 14;
                    row[j++] = -Lista_h.get(u).getPi();                
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //11
                    colno[j] = column;
                    row[j++] = -1;                
                    colno[j] = column + 4;
                    row[j++] = Lista_h.get(u).getP01();                
                    colno[j] = column + 5;
                    row[j++] = Lista_h.get(u).getPl1();                
                    colno[j] = column + 6;
                    row[j++] = Lista_h.get(u).getPl2();                
                    colno[j] = column + 7;
                    row[j++] = Lista_h.get(u).getPl3();               
                    colno[j] = column + 8;
                    row[j++] = Lista_h.get(u).getPl4();                
                    colno[j] = column + 13;
                    row[j++] = -Lista_h.get(u).getPi();               
                    colno[j] = column + 14;
                    row[j++] = -Lista_h.get(u).getPi();                
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //12
                    colno[j] = column;
                    row[j++] = 1;                
                    colno[j] = column + 4;
                    row[j++] = -Lista_h.get(u).getP02();                
                    colno[j] = column + 5;
                    row[j++] = -(Lista_h.get(u).getPl1() + Lista_h.get(u).getCurve());                
                    colno[j] = column + 6;
                    row[j++] = -(Lista_h.get(u).getPl2() + Lista_h.get(u).getCurve());                
                    colno[j] = column + 7;
                    row[j++] = -(Lista_h.get(u).getPl3() + Lista_h.get(u).getCurve());                
                    colno[j] = column + 8;
                    row[j++] = -(Lista_h.get(u).getPl4() + Lista_h.get(u).getCurve());                
                    colno[j] = column + 13;
                    row[j++] = Lista_h.get(u).getPi();               
                    colno[j] = column + 14;
                    row[j++] = -Lista_h.get(u).getPi();                
                    lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_h.get(u).getPi());
                
                
                    j = 0;                          //13
                    colno[j] = column;
                    row[j++] = -1;                
                    colno[j] = column + 4;
                    row[j++] = Lista_h.get(u).getP02();               
                    colno[j] = column + 5;
                    row[j++] = (Lista_h.get(u).getPl1() + Lista_h.get(u).getCurve());               
                    colno[j] = column + 6;
                    row[j++] = (Lista_h.get(u).getPl2() + Lista_h.get(u).getCurve());                
                    colno[j] = column + 7;
                    row[j++] = (Lista_h.get(u).getPl3() + Lista_h.get(u).getCurve());                
                    colno[j] = column + 8;
                    row[j++] = (Lista_h.get(u).getPl4() + Lista_h.get(u).getCurve());                
                    colno[j] = column + 13;
                    row[j++] = Lista_h.get(u).getPi();                
                    colno[j] = column + 14;
                    row[j++] = -Lista_h.get(u).getPi();               
                    lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_h.get(u).getPi());
                
                
                    j = 0;                          //14
                    colno[j] = column;
                    row[j++] = 1;                
                    colno[j] = column + 4;
                    row[j++] = -Lista_h.get(u).getP03();               
                    colno[j] = column + 5;
                    row[j++] = -(Lista_h.get(u).getPl1() + 2*Lista_h.get(u).getCurve());               
                    colno[j] = column + 6;
                    row[j++] = -(Lista_h.get(u).getPl2() + 2*Lista_h.get(u).getCurve());                
                    colno[j] = column + 7;
                    row[j++] = -(Lista_h.get(u).getPl3() + 2*Lista_h.get(u).getCurve());                
                    colno[j] = column + 8;
                    row[j++] = -(Lista_h.get(u).getPl4() + 2*Lista_h.get(u).getCurve());               
                    colno[j] = column + 13;
                    row[j++] = Lista_h.get(u).getPi();                
                    colno[j] = column + 14;
                    row[j++] = Lista_h.get(u).getPi();               
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 2*Lista_h.get(u).getPi());
                
                
                    j = 0;                          //15
                    colno[j] = column;
                    row[j++] = -1;               
                    colno[j] = column + 4;
                    row[j++] = Lista_h.get(u).getP03();               
                    colno[j] = column + 5;
                    row[j++] = (Lista_h.get(u).getPl1() + 2*Lista_h.get(u).getCurve());                
                    colno[j] = column + 6;
                    row[j++] = (Lista_h.get(u).getPl2() + 2*Lista_h.get(u).getCurve());                
                    colno[j] = column + 7;
                    row[j++] = (Lista_h.get(u).getPl3() + 2*Lista_h.get(u).getCurve());                
                    colno[j] = column + 8;
                    row[j++] = (Lista_h.get(u).getPl4() + 2*Lista_h.get(u).getCurve());               
                    colno[j] = column + 13;
                    row[j++] = Lista_h.get(u).getPi();               
                    colno[j] = column + 14;
                    row[j++] = Lista_h.get(u).getPi();               
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 2*Lista_h.get(u).getPi());
                
                
                    j = 0;                          //16
                    colno[j] = column + 5;
                    row[j++] = 1;               
                    colno[j] = column + 4;
                    row[j++] = -Lista_h.get(u).getUl();              
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //17
                    colno[j] = column + 5;
                    row[j++] = -1;               
                    colno[j] = column + 10;
                    row[j++] = Lista_h.get(u).getUl();              
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //18
                    colno[j] = column + 6;
                    row[j++] = 1;                
                    colno[j] = column + 10;
                    row[j++] = -Lista_h.get(u).getUl();               
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //19
                    colno[j] = column + 7;
                    row[j++] = 1;               
                    colno[j] = column + 11;
                    row[j++] = -Lista_h.get(u).getUl();              
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //20
                    colno[j] = column + 8;
                    row[j++] = 1;                
                    colno[j] = column + 12;
                    row[j++] = -Lista_h.get(u).getUl();               
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //21
                    colno[j] = column + 6;
                    row[j++] = -1;
                    colno[j] = column + 11;
                    row[j++] = Lista_h.get(u).getUl();
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //22
                    colno[j] = column + 7;
                    row[j++] = -1;
                    colno[j] = column + 12;
                    row[j++] = Lista_h.get(u).getUl();
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                
                
                    j = 0;                          //23
                    colno[j] = column + 8;
                    row[j++] = -1;
                    colno[j] = column + 15;
                    row[j++] = Lista_h.get(u).getUl();
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
    
                
                    j = 0;                          //24
                    colno[j] = column + 9;
                    row[j++] = 1;               
                    colno[j] = column + 5;
                    row[j++] = -1;               
                    colno[j] = column + 6;
                    row[j++] = -1;                
                    colno[j] = column + 7;
                    row[j++] = -1;                
                    colno[j] = column + 8;
                    row[j++] = -1;                
                    colno[j] = column + 4;
                    row[j++] = -Lista_h.get(u).getMinDisch();                             
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                
                
                    if(t <= delay){
                        if(t <= 1){                 //25
                            j = 0;
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            colno[j] = column + 9;
                            row[j++] = M;  
                            lp.addConstraintex(j, row, colno, LpSolve.EQ, Lista_h.get(u).getInitReserve() + Lista_h.get(u).getInflow());
                        }else{                      //26
                            j = 0;
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            colno[j] = column + 9;
                            row[j++] = M;
                            colno[j] = column - nVar + 1;
                            row[j++] = -1; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInflow());
                        }
                    }else{  //if  t>delay
                        if(u == 0){                 //27
                            j = 0;
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            colno[j] = column + 9;
                            row[j++] = M;
                            colno[j] = column - nVar + 1;
                            row[j++] = -1; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInflow());
                        }else{                      //28
                            j = 0;
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            colno[j] = column + 9;
                            row[j++] = M;
                            colno[j] = column - nVar + 1;
                            row[j++] = -1; 
                            colno[j] = column - (delay*nVar) - varHydro + 9;
                            row[j++] = -M; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInflow());
                        }                  
                    }
                
                    if(t <= 1){
                        j = 0;                      //29
                        colno[j] = column + 2;
                        row[j++] = 1;  
                        colno[j] = column + 3;
                        row[j++] = -1;
                        colno[j] = column + 4;
                        row[j++] = -1; 
                        lp.addConstraintex(j, row, colno, LpSolve.EQ,-Ih0[y]);
                    
                        j = 0;                      //30
                        colno[j] = column + 1;
                        row[j++] = 1;  
                        lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInitReserve());
                    }else{                          //31
                        j = 0;
                        colno[j] = column + 2;
                        row[j++] = 1;  
                        colno[j] = column + 3;
                        row[j++] = -1;
                        colno[j] = column + 4;
                        row[j++] = -1; 
                        colno[j] = column - nVar + 4;
                        row[j++] = 1; 
                        lp.addConstraintex(j, row, colno, LpSolve.EQ,0);
                    }
                
                    j = 0;                          //32
                    colno[j] = column + 1;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.LE,Lista_h.get(u).getMaxReserve());
                
                    j = 0;                          //33
                    colno[j] = column + 1;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,Lista_h.get(u).getMinReserve());
                
                    lp.setBinary(column+2, true);   //34
                    lp.setBinary(column+3, true);   //35
                    lp.setBinary(column+4, true);   //36
                    lp.setBinary(column+10, true);  //37
                    lp.setBinary(column+11, true);  //38
                    lp.setBinary(column+12, true);  //39
                    lp.setBinary(column+13, true);  //40
                    lp.setBinary(column+14, true);  //41
                    lp.setBinary(column+15, true);  //42
            
                    j = 0;                          //43
                    colno[j] = column;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                    j = 0;                          //44
                    colno[j] = column + 1;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                    j = 0;                          //45
                    colno[j] = column + 5;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                    j = 0;                          //46
                    colno[j] = column + 6;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                    j = 0;                          //47
                    colno[j] = column + 7;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                                
                    j = 0;                          //48
                    colno[j] = column + 8;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                    j = 0;                          //49
                    colno[j] = column + 9;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.GE,0);
                
                
                    column = column + varHydro;                        
                    y++;
                }
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
                        row[j++] = -WindPower[t-1][y];
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
                
            
            }//end HORIZON LOOP

          
    lp.setAddRowmode(false);
    
    
//////////////////////////////////////////////////////////////////////
///////////////////////     OBJECTIVE      ///////////////////////////
//////////////////////////////////////////////////////////////////////                

        column = 1;
        j = 0;
        for(int t = 1; t <= HORIZON; t++){
            colno[j] = column;               
            row[j++] = BCprices[t-1][0][0];
            
            colno[j] = column+1;            
            row[j++] = -BCprices[t-1][1][0];


            for (int i = 1; i <= nThermal + nHydro + nWind; i++){
                colno[j] = column + 1 + i;              //18
                row[j++] = Ppool[t-1];
            }
            
            column = column + 1 + (nThermal + nHydro + nWind) + 1;
            
            
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
            
            
            
            for(int u = 0; u < Lista_h.size(); u++){  
                if(Lista_h.get(u).isSelection()){
                    colno[j] = column + 2;                        //F2
                    row[j++] = -Lista_h.get(u).getStartupcost();
                
                    colno[j] = column;                          //F3
                    row[j++] = -Lista_h.get(u).getVCost();
                
                    colno[j] = column + 4;                      //F4
                    row[j++] = -Lista_h.get(u).getFCost();
            
                    column = column + varHydro;  
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
      
        
        
        lp.setObjFnex(j, row, colno);
}
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
           
            System.out.println("Profit: " + lp.getObjective() + " USD \n");     /* objective value */
            
            scheduling.SchedulingOutput.FProfit = lp.getObjective();
            
            lp.getVariables(row);  /* variable values */
            
            for(j = 0; j < Ncol; j++){
                x[j]=row[j];
            }
            
            column = 0;    
            for(int t = 1; t <= HORIZON; t++){
                System.out.println("t = " + t);
                System.out.println("VSC: " + x[column]);
                output[t-1][0][0] = x[column]; //VSC
                
                System.out.println("VBC: " + x[column + 1]);
                output[t-1][2][0] = x[column+1]; //VBC
                
                for (int i = 1; i <= nThermal+nHydro+nWind; i++){
                    System.out.println("Vpool"+i+": " + x[column + 1 + i] + "  ");
                    output[t-1][0][i] = x[column+1+i]; //Vpool
                }
                
                column = column + 1 + (nThermal + nHydro + nWind) + 1;
                
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
                for(int uH = 1; uH <= Lista_h.size(); uH++){
                    if(Lista_h.get(uH-1).isSelection()){
                        System.out.println("Vh"+ uH + " = " + x[column] + "    H = " + x[column+4] + "    Y = "+ x[column+2]); //Production 
                        output[t-1][1][nThermal+y+1] = x[column];  //Producao
                        output[t-1][1][nThermal+y+1] = Lista_h.get(uH-1).getVCost();  //MarginalCost
                        column = column + varHydro;
                        y++;
                    }
                }
                
    
                y = 0;
                for(int uW = 1; uW <= Lista_w.size(); uW++){
                    if(Lista_w.get(uW-1).isSelection()){
                        System.out.println("Vw" + y+1 + " = " + x[column] + "    I = " + x[column+1]);
                        output[t-1][1][nThermal+nHydro+y+1] = x[column]; //produção
                        output[t-1][2][nThermal+nHydro+y+1] = Lista_w.get(uW-1).getVCost(); //marginal cost
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
    

