/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package risk;

import java.util.ArrayList;


/**
 *
 * @author Hugo
 */
public class Risk {
    
//    dP[i]=dP[i]-deviation[i]*risk+aux[i];
//                dP[i]=dP[i]-prices[i];
                //                dP[i]= ((Math.log(-(us(lbda,(prices[i]+deviation[i]),prices[i+aux.length],aux[i],risk,deviation[i])+us(lbda,prices[i]-deviation[i],prices[i+aux.length],aux[i],risk,deviation[i]))/2+1)/(lbda*prices[i+aux.length]))-deviation[i]*risk+aux[i])-prices[i];
           //                    dP[i]= (Math.log(-(ub(lbda,(prices[i]+deviation[i]),prices[i+aux.length]/1000,aux[i],risk,deviation[i])+ub(lbda,prices[i]-deviation[i],prices[i+aux.length]/1000,aux[i],risk,deviation[i]))/2+1)/(lbda*prices[i+aux.length]/1000)-deviation[i]*risk+aux[i])-prices[i];

                       
    public static double[] deltaPb(double lbda, double[] prices, double[] aux,double risk, double[] deviation, double[] k){
        
        double [] dP= new double[aux.length];
        
        double a=0.0;
        double b=0.0;
        
        for (int i=0; i<aux.length; i++){
//            a=(Math.log(-(ub(lbda,(prices[i]+deviation[i]),aux[i],risk,deviation[i])+ub(lbda,prices[i]-deviation[i],aux[i],risk,deviation[i]))/2+1))/(lbda);
//            b=aux[i]-deviation[i]*risk-prices[i];
            a=ub(lbda,(prices[i]+risk*deviation[i+aux.length]),aux[i],risk,deviation[i+aux.length],k[i]);
            if (a<-0.5){
                a=-0.5;
            }
            
            dP[i]= (Math.log(-(a+ub(lbda,prices[i]-risk*deviation[i+aux.length],aux[i],risk,deviation[i+aux.length],k[i]))/2+1)/(lbda*k[i]))-deviation[i+aux.length]*risk;               
             }      
        return dP;
    }
    
        public static double[] deltaPs(double lbda, double[] prices, ArrayList<Double> aux,double risk, double[] deviation, double[] k){
        
        double [] dP= new double[prices.length/2];
        
        double a=0.0,b=0.0,c=0.0,d=0.0, e=0.0;
        
        for (int i=0; i<prices.length/2; i++){ 
            a=us(lbda,prices[i]-(1-risk)*deviation[i+prices.length/2],prices[i+prices.length/2],aux.get(i),risk,deviation[i+prices.length/2],k[i]);
//            b=us(lbda,(prices[i]+(1-risk)*deviation[i]),prices[i+aux.length],aux[i],risk,deviation[i],k[i]);
           
            if(a<-0.5){
                a=-0.5;
            }
//            c=-Math.log(-(a+b)/2+1);
//            d=c/((lbda*k[i]))+deviation[i]*(1-risk);
//            e=prices[i]-d;
            dP[i]= (-(Math.log(-(us(lbda,(prices[i]+(1-risk)*deviation[i+prices.length/2]),prices[i+prices.length/2],aux.get(i),risk,deviation[i+prices.length/2],k[i])+a)/2+1)/(lbda*k[i]))+deviation[i+prices.length/2]*(1-risk));
               
//               dP[i]= prices[i]-(-(Math.log(-(us(lbda,(prices[i]+(1-risk)*deviation[i]),prices[i+aux.length],aux[i],risk,deviation[i],k[i])+a)/2+1)/(lbda*k[i]))+deviation[i]*(1-risk)+aux[i]);
                   }
        return dP;
    }
    
    public static double[] deviation(int PERIODS){
        
        double [] deviation= new double[2*PERIODS];
        
        for(int i=0; i<2*PERIODS; i++){
//            deviation[i]=10;
            if(i<PERIODS){
                deviation[i]=-5*Math.random();
            }else{
        deviation[i]=5*Math.random();
            }
        }
        
        return deviation;
    }
    
       public static double ubuyer(double lbda, double[] prices, double[] Pmec,double risk, double[] deviation, double[] k){
        
        double u=0.0;
        double a=0;
        
        for (int i=0; i<Pmec.length; i++){
            a=1-Math.exp(-lbda*k[i]*(Pmec[i]-prices[i]-risk*deviation[i+Pmec.length]));
            if (a<-0.5){
                a=-0.5;
            }
            u=u+a;
        }
        
        return u;
        
    }
       
              public static double ub(double lbda, double price, double Pmec,double risk, double deviation,double k){
        
        double u=0.0;
                
            u=1-Math.exp(-lbda*k*((Pmec-price-risk*deviation)));
                
        return u;
        
    }
    
    public static double useller(double lbda, double[] prices, ArrayList<Double> cost,double risk, double[] deviation, double[] k ){
        
        double u=0.0;
        double a=0;
        
        for (int i=0; i<deviation.length/2; i++){
            a=1-Math.exp(-lbda*k[i]*((prices[i]-cost.get(i) -(1-risk)*deviation[i+deviation.length/2])));
            if (a<-0.5){
                a=-0.5;
            }
            u=u+a;
        }
        
        return u;
        
    }
       public static double us(double lbda, double price,double volume, double cost,double risk, double deviation,double k ){
        
        double a=0.0,u=0.0;
            
//            a=-lbda*k*((price-cost-(1-risk)*deviation));
//            u=1-Math.exp(a);
            u=1-Math.exp(-lbda*k*((price-cost-(1-risk)*deviation)));
                
        return u;
        
    }
}
