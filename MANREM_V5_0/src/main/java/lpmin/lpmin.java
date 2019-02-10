/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lpmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lpsolve.*;

public class lpmin {

	public lpmin() {
	}

	public double[] execute(double[] V, double[] c, double margin,double[] Vmin,double[] Vmax,int PERIODS) throws LpSolveException {
          LpSolve lp;
           
           
          int Ncol, j, ret = 0;

          /* We will build the model row by row
             So we start with creating a model with 0 rows and 2 columns */
          Ncol = PERIODS; /* there are two variables in the model */

          /* create space large enough for one row */
          int[] colno = new int[Ncol];
          double[] row = new double[Ncol];

          lp = LpSolve.makeLp(0, Ncol);
          if(lp.getLp() == 0)
            ret = 1; /* couldn't construct a new model... */

          if(ret == 0) {
            /* let us name our variables. Not required, but can be useful for debugging */
              for (int i=1; i<=PERIODS;i++){
                  lp.setColName(i, "V"+i);
              }
//            lp.setColName(1, "V1");
//            lp.setColName(2, "V2");
//            lp.setColName(3, "V3");
//            lp.setColName(4, "V4");
//            lp.setColName(5, "V5");
//            lp.setColName(6, "V6");

            lp.setAddRowmode(true);  /* makes building the model faster if it is done rows by row */

            /* construct first row (x1+x2+x3+x4+x5+x6=VT) */
            j = 0;
            for (int i=1; i<=Ncol;i++){
            colno[j] = i; /* first column */
            row[j++] = 1;

//            colno[j] = 2; /* second column */
//            row[j++] = 1;
            }
            /* add the row to lpsolve */
            double Vtotal=0;
            for (int i=0; i<Ncol;i++){
                Vtotal=Vtotal+V[i];
            }
            lp.addConstraintex(j, row, colno, LpSolve.EQ, 0.95*Vtotal);
          }

          if(ret == 0) {
            /* construct second row (xi<1.2*Vi) */
            
            for (int i=1; i<=Ncol;i++){
                j = 0;
            colno[j] = i; /* first column */
            row[j++] = 1;
            
            
//
//            colno[j] = 2; /* second column */
//            row[j++] = 30;
//
//            /* add the row to lpsolve */
//            lp.addConstraintex(j, row, colno, LpSolve.LE, Vmax[i-1]);
            lp.addConstraintex(j, row, colno, LpSolve.LE, V[i-1]);
            }
          }
          
                    if(ret == 0) {
            /* construct second row (xi>0.8*Vi) */
            
            for (int i=1; i<=Ncol;i++){
                j = 0;
            colno[j] = i; /* first column */
            row[j++] = 1;
            
            
//
//            colno[j] = 2; /* second column */
//            row[j++] = 30;
//
//            /* add the row to lpsolve */
            lp.addConstraintex(j, row, colno, LpSolve.GE, Vmin[i-1]);
            }
          }


          if(ret == 0) {
            lp.setAddRowmode(false); /* rowmode should be turned off again when done building the model */

            /* set the objective function (P1*x1+P2*x2) */
            j = 0;

            for (int i=1; i<=Ncol;i++){
            colno[j] = i; /* first column */
            
            row[j++] = c[i-1];

            }


            /* set the objective in lpsolve */
            lp.setObjFnex(j, row, colno);
          }

          if(ret == 0) {
            /* set the object direction to minimize */
            lp.setMinim();

//            /* just out of curioucity, now generate the model in lp format in file model.lp */
//            lp.writeLp("model.lp");

            /* I only want to see important messages on screen while solving */
            lp.setVerbose(LpSolve.IMPORTANT);

            /* Now let lpsolve calculate a solution */
            ret = lp.solve();
            if(ret == LpSolve.OPTIMAL)
              ret = 0;
            else
              ret = 5;
          }

          if(ret == 0) {
            /* a solution is calculated, now lets get some results */

            /* objective value */
            System.out.println("LpSolve Objective value: " + lp.getObjective());

            /* variable values */
            lp.getVariables(row);
            double teste=0;
            for(j = 0; j < Ncol; j++){
              System.out.println(lp.getColName(j + 1) + ": " + row[j]);
              V[j]=row[j];
            teste=teste+row[j];
            }
            System.out.println(teste);
            /* we are done now */
          }
          
          /* clean up such that all used memory by lpsolve is freed */
          if(lp.getLp() != 0)
            lp.deleteLp();

          return(V);
        }

	public static void main(String[] args) {
            double[] V={48.45,55.47,82.54,82.66,71.08,60.69};
            double[] Vmax={48.45,55.47,82.54,82.66,71.08,60.69};
            double[] Vmin={48.45,55.47,82.54,82.66,71.08,60.69};
           double[] c = {43.45,46.10,57.27,55.16,41.50,55.33 };
           int PERIODS=24;
           double m=0.2;
		try {
			new lpmin().execute(V, c, m,Vmin,Vmax,PERIODS);
		}
		catch (LpSolveException e) {
			e.printStackTrace();
		}
	}
}