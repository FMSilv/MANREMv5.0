 package SchedulingModels;


import java.util.ArrayList;
import java.util.List;
import scheduling.DataThermal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import scheduling.AddGenerator;
import scheduling.ProducerScheduling;
//import static scheduling.UnitSelection.Lista_output;

/*
Modelo estabelecido por Conejo et al. para agendamento de produção de portfolios de centrais térmicas.
Modelo Sem Risco
 */


/**
 *
 * @author Af
 */
public class Conejo {
    

public double[][][] executeConejo (ArrayList<DataThermal> Lista_t, int HORIZON, double [] Ppool, int [] Shiftingturn, double [][][] BCprices)throws LpSolveException{
     LpSolve lp;
           
          int Ncol, j, ret = 0; int column;
          int nThermal = 0;
          for (int u = 0; u < Lista_t.size(); u++){
            if(Lista_t.get(u).isSelection()){
                nThermal++;               
            }      
          }
          int varThermal = 1;
          int varTot = 4 + (varThermal+1)*nThermal;
          
          double Pmax = 0;
          for (int u = 0; u < Lista_t.size(); u++){
                if(Lista_t.get(u).isSelection()){
                    Pmax = Pmax + Lista_t.get(u).getMaxP();
                    ProducerScheduling.CommitedID.add(Lista_t.get(u).getID());//Guarda nomes das centrais agendadas
              }}
          
          double BcVSize = Pmax * 0.3;                  

          Ncol = (varTot) * HORIZON; /* there are V variables in the model */
          /* create space large enough for one row */
          int[] colno = new int[Ncol];
          double[] row = new double[Ncol];
          double[] x = new double[Ncol];
          
          double output [][][] = new double[HORIZON][4][nThermal+1];
          
          
          lp = LpSolve.makeLp(0, Ncol);
          if(lp.getLp() == 0)
            ret = 1; /* couldn't construct a new model... */

         
          lp.setAddRowmode(true);  

          if(ret == 0) {
                column = 1;
                
                for(int t = 1; t <= HORIZON; t++){
                     j = 0;
                    //    ----
                    colno[j] = column;
                    row[j++] = -1;
                    //    ----
                    colno[j] = column+1;
                    row[j++] = -1;
                    //    ----
                    colno[j] = column+2;
                    row[j++] = 1;
                    //    ----
                    colno[j] = column+3;
                    row[j++] = 1;
                    //    ----
                    
                    int n = 0;
                    for(int k = 0; k < Lista_t.size(); k++){  //Volumes pool para cada central
                        if(Lista_t.get(k).isSelection()){
                            colno[j] = column + 4 + n;
                            row[j++] = 1;
                            n++;           
                        }
                    }
            
                    n = 0;
                    for(int k = 0; k < Lista_t.size(); k++){ 
                        if(Lista_t.get(k).isSelection()){  //Produção de cada central
                            colno[j] = column + 4 + nThermal + n;
                            row[j++] = -1;
                            n++;           
                        }
                    }
                lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);            
                //lp.addConstraintex(j, row, colno, LpSolve.EQ, SOMA DOS VOLUMES DOS CONTARTOS);

            
                for (int i = 0; i < 4; i++){//Minimum Vbc Volumes = 0 
                    j = 0;
                    colno[j] = column+i;
                    row[j++] = 1;
                    lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
                }            
            
            
                if (Shiftingturn[t-1] == 1){ //LIMITES DE VOLUME DE CONTRATOS   (t-1) porque 1<=t<=24
                    for(int i = 0; i < 4; i++){
                        j = 0;
                        colno[j] = column + i;
                        row[j++] = 1;
                        lp.addConstraintex(j, row, colno, LpSolve.LE, BcVSize); //30% da Potencia Nominal
                    }
                }else{
                    for(int i = 0; i < 4; i++){
                        j = 0;
                        colno[j] = column + i;
                        row[j++] = 1;
        
                        colno[j] = column-varTot+i;
                        row[j++] = -1;
                        lp.addConstraintex(j, row, colno, LpSolve.EQ, 0);
                    }
                }

                column = column + 4;  //Vpool's

                n = 0;            
                for(int k = 0; k < Lista_t.size(); k++){  //Volumes pool para cada central
                    if(Lista_t.get(k).isSelection()){
                        j = 0;                              //Minimum Vpool Volumes = 0
                        colno[j] = column + n;
                        row[j++] = 1;
                        lp.addConstraintex(j, row, colno, LpSolve.GE, 0);

                        j = 0;                              //Max Vpool Volumes <= Production
                        colno[j] = column + n;
                        row[j++] = 1;
                        colno[j] = column +  nThermal + n;
                        row[j++] = -1;
                        lp.addConstraintex(j, row, colno, LpSolve.LE, 0);
                        n++;
                    }
                }
           
                column = column + nThermal;     //Productions

                //n = 0;
                for(int k = 0; k < Lista_t.size(); k++){  //Volumes pool para cada central
                    if(Lista_t.get(k).isSelection()){    
                        //Minimum Limit Production
                        j = 0;
                        colno[j] = column;
                        row[j++] = 1;            
                        lp.addConstraintex(j, row, colno, LpSolve.GE, 0);
            //    
       
                        if(t == 1){
                            j = 0;
                            colno[j] = column;
                            row[j++] = -1;
                            lp.addConstraintex(j, row, colno, LpSolve.LE, -Lista_t.get(k).getPrevProd()+Lista_t.get(k).getRD());
            
                            j = 0;
                            colno[j] = column;
                            row[j++] = 1;
                            lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_t.get(k).getPrevProd()+Lista_t.get(k).getRU());
       
                        }else{
            
                            j = 0;
                            colno[j] = column;
                            row[j++] = -1;
            
                            colno[j] = column - varTot;
                            row[j++] = 1;
            
                            lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_t.get(k).getRD());
            
                            j = 0;
                            colno[j] = column;
                            row[j++] = 1;
            
                            colno[j] = column - varTot;
                            row[j++] = -1;
            
                            lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_t.get(k).getRU());            
                        }
       
                        //Maximum Power Output 
                        j = 0;
                        colno [j] = column; 
                        row[j++] = 1;       
                        lp.addConstraintex(j, row, colno, LpSolve.LE, Lista_t.get(k).getMaxP());     
        
                        column = column + varThermal;
            
                        //n++;
                    }
                }
}

    lp.setAddRowmode(false);
    

//OBJECTIVE FUNCTION

        column = 1;
        j = 0;
        for(int t = 1; t <= HORIZON; t++){
            
            colno [j] = column;
            row[j++] = -BCprices[t-1][1][0];
            
            colno [j] = column+1;
            row[j++] = -BCprices[t-1][1][1];
            
            colno [j] = column+2;
            row[j++] = BCprices[t-1][0][0];
            
            colno [j] = column+3;
            row[j++] = BCprices[t-1][0][1];
            
            column = column + 4;

            for(int k = 0; k < Lista_t.size(); k++){  //Volumes pool para cada central
                if(Lista_t.get(k).isSelection()){
                    colno [j] = column;
                    row[j++] = Ppool[t-1];
                    column = column + 1;
                }
            }
            
            for(int k = 0; k < Lista_t.size(); k++){  //Volumes pool para cada central
                if(Lista_t.get(k).isSelection()){
                    colno [j] = column;
                    row[j++] = -Lista_t.get(k).getVCost();
                    column = column + varThermal;
                }
            }
            lp.setObjFnex(j, row, colno);
        }           
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

            if(ret == 0) {        
                //scheduling.UnitSelection.Lista_output = new String;
                System.out.println("Profit: " + lp.getObjective()); 
                
                scheduling.SchedulingOutput.FProfit = lp.getObjective();
                
                lp.getVariables(row); 
                for(j = 0; j < Ncol; j++){
                    x[j] = row[j];
                }                
                
                column = 0;
                
                for(int t = 1; t <= HORIZON; t++){                    
                    
                    System.out.println("Vsb1: " + x[column+2]); //Vsb1
                        output[t-1][0][0] = x[column+2];
                    System.out.println("Vsb2: " + x[column+3]); //Vsb2
                        output[t-1][1][0] = x[column+3];
                    System.out.println("Vbb1: " + x[column]); //Vbb1 
                        output[t-1][2][0] = x[column];
                    System.out.println("Vbb2: " + x[column+1]); //Vbb2
                        output[t-1][3][0] = x[column+1];                  
                    
                        column = column + 4;
                                              
                    for(int u = 1; u <= nThermal; u++){                            
                        System.out.println("Vpool"+ u +": " + x[column]); //Productions
                        output[t-1][0][u] = x[column];
                        column = column + 1;
                    }
                    
                    for(int u = 1; u <= nThermal; u++){                            
                        System.out.println("P"+ u +": " + x[column]); //Productions
                            output[t-1][1][u] = x[column];
                            column = column + varThermal;
                    }
                    System.out.println("\n"); 
                    
                    int w = 0;
                    for(int u = 0; u < Lista_t.size(); u++){ 
                        if(Lista_t.get(u).isSelection()){
                            output[t-1][2][w+1] = Lista_t.get(u).getVCost(); //Adiciona custos marginais à tabela de output                    
                            w++;
                        }
                    }
                }
                
            }
            return output;
}
           
        }
