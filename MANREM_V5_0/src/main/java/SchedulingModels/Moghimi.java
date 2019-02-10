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
import static scheduling.ProducerScheduling.delay;

/**
 *
 * @author AfonsoMCardoso
 */
public class Moghimi {
  
    
 public double [][][] executeMoghimi_cascade (ArrayList<DataHydro> Lista_h, ArrayList<DataWind> Lista_w, int delay, int HORIZON,double [] Ppool, int [] Shiftingturn, double [][][] BCprices)throws LpSolveException{
     LpSolve lp;
           
        int j,nHydro = 0,nWind = 0, ret = 0;
        double Pmax = 0;
        
        
        
        for (int i = 0; i < Lista_h.size(); i++){
            if(Lista_h.get(i).isSelection()){
                nHydro++;
            }
        }
        
        for (int i = 0; i < Lista_w.size(); i++){
            if(Lista_w.get(i).isSelection()){
                nWind++;
            }
        }
         
        int [] I0 = new int[nHydro];
        int varWind = 2; //1 Variável para cada wind
        int varHydro = 16; //16 Variáveis para cada hydro
        
        int y = 0;
        System.out.print("nHydro = "+nHydro+"\n");
        System.out.print("nLista_h = "+Lista_h.size()+"\n");
        while(y<nHydro){
            for (int u = 0; u < Lista_h.size(); u++){
                 System.out.print("u = "+u+"\n");
                if(Lista_h.get(u).getCascadeorder().equals(String.valueOf(y+1))){
                    Pmax = Pmax + Lista_h.get(u).getPi();
                    ProducerScheduling.CommitedID.add(Lista_h.get(u).getID());//Guarda nomes das centrais agendadas
                    if(Lista_h.get(u).getPrevProduction()>0){
                        I0[y] = 1;
                    }else{
                        I0[y] = 0;
                    }
                    y++;
                    System.out.print("yValue = " + y +"\n");
                }
            }
        }
          
        for (int u = 0; u < Lista_w.size(); u++){
            if(Lista_w.get(u).isSelection()){
                Pmax = Pmax + Lista_w.get(u).getMaxP();
                ProducerScheduling.CommitedID.add(Lista_w.get(u).getID());//Guarda nomes das centrais agendadas
            }
        }
                 
          double M = 0.0036;
                  
          int nVar = (2 + (nHydro+nWind) + varHydro*nHydro + varWind*nWind);
          int Ncol = nVar * HORIZON;

          double Vsize = 0.3*Pmax;

          double output [][][] = new double[HORIZON][4][nHydro+nWind+1];
          
          
          
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
                
                j = 0;   //CONTRATOS DE VENDA
                colno[j] = column;
                row[j++] = -1;
                
                       //CONTRATOS DE COMPRA
                colno[j] = column + 1;
                row[j++] = 1;
                
                for (int i = 0; i < nHydro + nWind; i++){   //balanço volumes vpool
                    colno[j] = column + 2 + i;
                    row[j++] = -1;   
                }
                
                for(int i = 0; i < nHydro; i++){
                    colno[j] = column + 1 + (nHydro + nWind) + 1 + (i * varHydro);
                    row[j++] = 1;
                }
                
                for(int i = 0; i < nWind; i++){
                    colno[j] = column + 1 + (nHydro + nWind) + (nHydro * varHydro) + 1 + (i * varWind);
                    row[j++] = 1;
                }
                lp.addConstraintex(j, row, colno, LpSolve.EQ, 0); // 0 ou SOMA DOS VOLUMES LT
                
                //
                if(Shiftingturn[t-1] == 0){
                    j = 0;
                    colno[j] = column;
                    row[j++] = 1;                    
                    colno[j] = column - nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                    
                    j = 0;
                    colno[j] = column + 1;
                    row[j++] = 1;                    
                    colno[j] = column + 1 - nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                    
                }

               
                
                for(int i = 0; i < nHydro; i++){   
                j = 0;                                  //Vpool(hidricas)>0
                colno[j] = column + 2 + i;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                j = 0;                                  //Vpool <= Produção (HIDRICAS)
                colno[j] = column + 2 + i;
                row[j++] = 1;
                colno[j] = column + 1 + (nHydro + nWind) + 1 + (i * varHydro);
                row[j++] = -1;
                lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                }
                
                for(int i = 0; i < nWind; i++){   
                j = 0;                                  //Vpool(eolicas)>0
                colno[j] = column + 1 + nHydro + 1 + i;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                j = 0;                                  //Vpool <= Produção (EOLICAS)
                colno[j] = column + 1 + nHydro + 1 + i;
                row[j++] = 1;
                colno[j] = column + 1 + (nHydro + nWind) + (nHydro*varHydro) + 1 + i*varWind;
                row[j++] = -1;
                lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                }
                
                
                j = 0; //Vsc>0
                colno[j] = column;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                
                j = 0; //Vsc<Vsize
                colno[j] = column;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.LE, Vsize);
                
                j = 0; //Vbc>0
                colno[j] = column + 1;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                
                j = 0; //Vbc<Vsize
                colno[j] = column + 1;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.LE, Vsize);
  
                column = column + 1 + (nHydro + nWind) + 1;

///////////////////////////////////////////////////////////////////////////////
////////////////////////              HYDRO         ///////////////////////////
///////////////////////////////////////////////////////////////////////////////
        y = 0;
        while(y<nHydro){
            for(int u = 0; u < Lista_h.size(); u++){
                if(Lista_h.get(u).getCascadeorder().equals(String.valueOf(y+1))){
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
                        lp.addConstraintex(j, row, colno, LpSolve.EQ,-I0[y]);
                    
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
            
///////////////////////////////////////////////////////////////////////////////
////////////////////////             WIND           ///////////////////////////
///////////////////////////////////////////////////////////////////////////////            
            y = 0;
            for(int u = 0; u < Lista_w.size(); u++){
                if(Lista_w.get(u).isSelection()){
                    
                    j = 0;
                    colno[j] = column;
                    row[j++] = 1;
                    colno[j] = column+1;
                    row[j++] = -Lista_w.get(u).getProduction(t-1);
                    lp.addConstraintex(j, row, colno, LpSolve.EQ,0);
                    
                    j = 0;
                    colno[j] = column+1;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ,1);
            
                    lp.setBinary(column+1, true);
                    column = column + varWind;
                    y++;
                }                        
            }
            
            }//END HORIZON
    
     lp.setAddRowmode(false);
    
///////////////////////////////////////////////////////////////////////////////
////////////////////////          OBJECTIVE         ///////////////////////////
/////////////////////////////////////////////////////////////////////////////// 

        column = 1;
        j = 0;
        for(int t = 1; t <= HORIZON; t++){   
            colno[j] = column;                              //F1
            row[j++] = BCprices[t-1][0][0];
            
            colno[j] = column+1;                              //F1
            row[j++] = -BCprices[t-1][1][0];
            
            
            for (int i = 0; i < nHydro + nWind; i++){
                colno[j] = column + 2 + i;
                row[j++] = Ppool[t-1];
            }
            column = column + 1 + (nHydro + nWind) + 1;
            
            y = 0;
            while(y<nHydro){
                for(int u = 0; u < Lista_h.size(); u++){  
                    if(Lista_h.get(u).getCascadeorder().equals(String.valueOf(y+1))){
                        colno[j] = column + 2;                        //F2
                        row[j++] = -Lista_h.get(u).getStartupcost();
                
                        //colno[j] = column;                          //F3
                        //row[j++] = -Lista_h.get(u).getVCost();
                
                        //colno[j] = column + 4;                      //F4
                        //row[j++] = -Lista_h.get(u).getFCost();
            
                        column = column + varHydro;
                        y++;
                    }   
                }
            }
            
            
            for(int u = 0; u < Lista_w.size(); u++){  
                if(Lista_w.get(u).isSelection()){
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
            
            /* Now let lpsolve calculate a solution */
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
                System.out.println("t = "+ t);
                System.out.println("VSC: " + x[column]);
                output[t-1][0][0] = x[column];
                System.out.println("VBC: " + x[column+1]);
                output[t-1][2][0] = x[column+1];
                
                
                for(int u = 1; u <= nHydro + nWind; u++){
                    System.out.print("Vpool"+ u + ": " + x[column+1+u] + "  "); //pool
                    output[t-1][0][u] = x[column+1+u];                    
                }
                
                System.out.print("\n");
                column = column + 1 + (nHydro + nWind) + 1;
                
                y = 0;
                while(y<nHydro){
                for(int uH = 1; uH <= Lista_h.size(); uH++){
                    if(Lista_h.get(uH-1).getCascadeorder().equals(String.valueOf(y+1))){
                        System.out.println("V_H"+ y+1 + ": " + x[column] + "    I_H: " + x[column+4] + "    Y_H: "+ x[column+2]); //Production 
                        output[t-1][1][y+1] = x[column];  //Producao
                        output[t-1][2][y+1] = Lista_h.get(uH-1).getVCost();  //MarginalCost
                        column = column + varHydro;
                        y++;          
                    }
                }
                }
                
                y = 0;
                for(int uW = 1; uW <= Lista_w.size(); uW++){
                    if(Lista_w.get(uW-1).isSelection()){
                        System.out.println("V_W"+ y+1 + ": " + x[column] + "    I_H: " + x[column+1]); //Production
                        output[t-1][1][nHydro+y+1] = x[column];  //Production 
                        output[t-1][2][nHydro+y+1] = Lista_w.get(uW-1).getVCost();  //Marginal Cost
                        
                        column = column + varWind;
                        y++;
                    }
                }
                System.out.println("\n");
            }
            
          }
          return(output);

}

  
 public double [][][] executeMoghimi_NOcascade (ArrayList<DataHydro> Lista_h, ArrayList<DataWind> Lista_w, int HORIZON,double [] Ppool, int [] Shiftingturn,double [][][] BCprices)throws LpSolveException{
     LpSolve lp;

        int j, nHydro = 0, ret = 0, nWind = 0; double Pmax = 0;
        
        for(int i = 0; i < Lista_h.size(); i++){
            if(Lista_h.get(i).isSelection()){
                  nHydro++;
            }
        }
          
        for(int i = 0; i < Lista_w.size(); i++){
            if(Lista_w.get(i).isSelection()){
                nWind++;
            }
        }
          
        int varWind = 2; //2 Variáveis para cada wind
        int varHydro = 16; //16 Variáveis para cada hydro
        int [] I0 = new int[nHydro];  
        
        
        int y = 0;
        for (int u = 0; u < Lista_h.size(); u++){
            if(Lista_h.get(u).isSelection()){
                Pmax = Pmax + Lista_h.get(u).getPi();
                ProducerScheduling.CommitedID.add(Lista_h.get(u).getID());//Guarda nomes das centrais agendadas
                if(Lista_h.get(u).getPrevProduction()>0){
                    I0[y] = 1;
                }else{
                    I0[y] = 0;
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
          
          double M = 0.0036;
                  
          int nVar = (2 + (nHydro+nWind) + varHydro*nHydro + varWind*nWind);
          int Ncol = nVar * HORIZON;   
         
          double Vsize = 0.3*Pmax;
          double output [][][] = new double[HORIZON][4][nHydro+nWind+1];

                    
          /* create space large enough for one row */
          int[] colno = new int[Ncol];
          double[] row = new double[Ncol];
          double[] x = new double[Ncol];
          
          lp = LpSolve.makeLp(0, Ncol);
          if(lp.getLp() == 0)
            ret = 1; /* couldn't construct a new model... */

          System.out.print("nI = "+I0.length+"\n");
          System.out.print("nHydro = "+nHydro+"\n");
          System.out.print("nWind = "+nWind+"\n");
          System.out.print("nLista_Hydro = "+Lista_h.size()+"\n");
          for(int i = 0; i<Lista_h.size(); i++){
            System.out.print("Cascade = "+Lista_h.get(i).getCascadeorder()+"\n");
            System.out.print("Selection = "+Lista_h.get(i).isSelection()+"\n");
          }
          
         
          lp.setAddRowmode(true);  
          
          int column = 1;
          
        if(ret == 0) {

            for(int t = 1; t <= HORIZON; t++){
                
                j = 0;   //CONTRATO DE VENDA
                colno[j] = column;
                row[j++] = -1;
                
                           //CONTRATO DE COMPRA
                colno[j] = column + 1;
                row[j++] = 1;              
                
                for (int i = 1; i <= nHydro + nWind; i++){   //balanço volumes vpool
                    colno[j] = column + 1 + i;
                    row[j++] = -1;   
                }
                
                for(int i = 0; i < nHydro; i++){
                        colno[j] = column + 1 + (nHydro + nWind) + 1 + (i * varHydro);
                        row[j++] = 1;
                }
                for(int i = 0; i < nWind; i++){
                        colno[j] = column + 1 + (nHydro + nWind) + (nHydro * varHydro) + 1 + (i * varWind);
                        row[j++] = 1;
                }
                lp.addConstraintex(j, row, colno, LpSolve.EQ, 0); // 0 ou SOMA DOS VOLUMES LT
                
                //
                if(Shiftingturn[t-1] == 0){
                    j = 0;
                    colno[j] = column;
                    row[j++] = 1;                    
                    colno[j] = column - nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                    
                    j = 0;
                    colno[j] = column + 1;
                    row[j++] = 1;                    
                    colno[j] = column + 1 - nVar;
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                    
                }
       
                
                for(int i = 0; i < nHydro; i++){   
                    j = 0;                                  //Vpool(hidricas)>0
                    colno[j] = column + 2 + i;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                    j = 0;                                  //Vpool <= Produção (HIDRICAS)
                    colno[j] = column + 2 + i;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nHydro + nWind) + 1 + (i * varHydro);
                    row[j++] = -1;
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                }
                
                for(int i = 0; i < nWind; i++){   
                    j = 0;                                  //Vpool(eolicas)>0
                    colno[j] = column + 1 + nHydro + 1 + i;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                    j = 0;                                  //Vpool <= Produção (EOLICAS)
                    colno[j] = column + 1 + nHydro + 1 + i;
                    row[j++] = 1;
                    colno[j] = column + 1 + (nHydro + nWind) + (nHydro*varHydro) + 1 + i*varWind;
                    row[j++] = -1;
                    //row[j++] = -WindPower[t-1][i];
                    lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                }
                
                
                j = 0; //Vsb>0
                colno[j] = column;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                
                j = 0; //Vsb<Vsize
                colno[j] = column;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.LE, Vsize);
                
                j = 0; //Vbb>0
                colno[j] = column + 1;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                
                
                j = 0; //Vbb<Vsize
                colno[j] = column + 1;
                row[j++] = 1;
                lp.addConstraintex(j, row, colno, LpSolve.LE, Vsize);
  

                column = column + 1 + nHydro + nWind + 1;


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
                            
                            j = 0;                      //29
                            colno[j] = column + 2;
                            row[j++] = 1;  
                            colno[j] = column + 3;
                            row[j++] = -1;
                            colno[j] = column + 4;
                            row[j++] = -1; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,-I0[y]);
                    
                            j = 0;                      //30
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInitReserve());
                            
                            
                        }else{                      
                            j = 0;
                            colno[j] = column + 1;
                            row[j++] = 1;  
                            colno[j] = column + 9;
                            row[j++] = M;
                            colno[j] = column - nVar + 1;
                            row[j++] = -1; 
                            lp.addConstraintex(j, row, colno, LpSolve.EQ,Lista_h.get(u).getInflow()); 
                            
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
            
///////////////////////////////////////////////////////////////////////////////
////////////////////////             WIND           ///////////////////////////
///////////////////////////////////////////////////////////////////////////////            
            y = 0;
            for(int u = 0; u < Lista_w.size(); u++){
                if(Lista_w.get(u).isSelection()){
                    
                    j = 0;
                    colno[j] = column;
                    row[j++] = 1; 
                    colno[j] = column+1;
                    row[j++] = -Lista_w.get(u).getProduction(t-1); 
                    lp.addConstraintex(j, row, colno, LpSolve.EQ,0);
                    
                    j = 0;
                    colno[j] = column + 1;
                    row[j++] = 1; 
                    lp.addConstraintex(j, row, colno, LpSolve.EQ,1);  //OBRIGATORIO INJECTAR PRODUCAO EOLICA
                    
                    lp.setBinary(column + 1, true);
                    column = column + varWind;
                    y++;
                }          
            }                             
            }//END HORIZON
    
     lp.setAddRowmode(false);
    
///////////////////////////////////////////////////////////////////////////////
////////////////////////          OBJECTIVE         ///////////////////////////
/////////////////////////////////////////////////////////////////////////////// 

        column = 1;
        j = 0;
        for(int t = 1; t <= HORIZON; t++){   
            colno[j] = column;                              //F1
            row[j++] = BCprices[t-1][0][0];
            
            colno[j] = column + 1;                              //F1
            row[j++] = -BCprices[t-1][1][0];
            
            
            for (int i = 1; i <= nHydro + nWind; i++){
                colno[j] = column + 1 + i;
                row[j++] = Ppool[t-1];
            }
            
            column = column + 1 + nHydro + nWind + 1;
            
            for(int u = 0; u < Lista_h.size(); u++){  
                if(Lista_h.get(u).isSelection()){
                    colno[j] = column + 2;                        //F2
                    row[j++] = -Lista_h.get(u).getStartupcost();
                
                    //colno[j] = column;                          //F3
                    //row[j++] = -Lista_h.get(u).getVCost();
                    //colno[j] = column + 4;                      //F4
                    //row[j++] = -Lista_h.get(u).getFCost();
            
                    column = column + varHydro;  
                }
            }
            
            for(int u = 0; u < Lista_w.size(); u++){
                if(Lista_w.get(u).isSelection()){
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
            
            /* Now let lpsolve calculate a solution */
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
            System.out.print(x[j]+"\n" );
            }
            
            column = 0;
            for(int t = 1; t <= HORIZON; t++){            
                System.out.println("t = "+ t);
                System.out.println("VSC: " + x[column]);
                output[t-1][0][0] = x[column];
                System.out.println("VBC: " + x[column+1]);
                output[t-1][2][0] = x[column+1];
                
                
                for(int u = 1; u <= nHydro + nWind; u++){
                    System.out.print("Vpool"+ u + ": " + x[column + 1 + u] + "  "); //Vpool
                    output[t-1][0][u] = x[column + 1 + u];                    
                }
                
                System.out.print("\n");
                column = column + 1 + (nHydro + nWind) + 1;
                
                y = 0;
                for(int uH = 1; uH <= Lista_h.size(); uH++){
                    if(Lista_h.get(uH-1).isSelection()){
                        //System.out.println("Prod_H"+ y+1 + ": " + x[column] + "    I_H: " + x[column+4] + "    Y_H: "+ x[column+2]+"\n"); //Production 
                        System.out.println("Producao_H"+ y+1 + ": " + x[column]+"\n");
                        System.out.println("Vreserve_H"+ y+1 + ": " + x[column+1]+"\n");
                        System.out.println("y_H"+ y+1 + ": " + x[column+2]+"\n");
                        System.out.println("z_H"+ y+1 + ": " + x[column+3]+"\n");
                        System.out.println("I_H"+ y+1 + ": " + x[column+4]+"\n");
                        System.out.println("u1_H"+ y+1 + ": " + x[column+5]+"\n");
                        System.out.println("u2_H"+ y+1 + ": " + x[column+6]+"\n");
                        System.out.println("u3_H"+ y+1 + ": " + x[column+7]+"\n");
                        System.out.println("u4_H"+ y+1 + ": " + x[column+8]+"\n");
                        System.out.println("Disch_Tot_H"+ y+1 + ": " + x[column+9]+"\n");
                        System.out.println("w1_H"+ y+1 + ": " + x[column+10]+"\n");
                        System.out.println("w2_H"+ y+1 + ": " + x[column+11]+"\n");
                        System.out.println("w3_H"+ y+1 + ": " + x[column+12]+"\n");
                        System.out.println("d1_H"+ y+1 + ": " + x[column+13]+"\n");
                        System.out.println("d2_H"+ y+1 + ": " + x[column+14]+"\n");
                        System.out.println("w4_H"+ y+1 + ": " + x[column+15]+"\n \n");
                        
                        
                        
                        output[t-1][1][1+y] = x[column];  //Producao
                        output[t-1][2][1+y] = Lista_h.get(uH-1).getVCost();  //MarginalCost
                        column = column + varHydro;
                        y++;
                    }
                }
                
                y = 0;
                for(int uW = 1; uW <= Lista_w.size(); uW++){
                    if(Lista_w.get(uW-1).isSelection()){
                        System.out.println("V_W"+ y+1 + ": " + x[column] + "    I_H: " + x[column+1]); //Production
                        output[t-1][1][nHydro+y+1] = x[column];  //Production 
                        output[t-1][2][nHydro+y+1] = Lista_w.get(uW-1).getVCost();  //Marginal Cost
                        column = column + varWind;
                        y++;
                    }
                }
                System.out.println("\n");
            }
            
          }
          return(output);

}
           
        }

        
   

