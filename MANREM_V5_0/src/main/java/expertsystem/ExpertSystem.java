/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package expertsystem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import jpl.*;
import jpl.Query;
/**
 *
 * @author Hugo
 */
public class ExpertSystem {
public String[][] sol;
    
            public ExpertSystem(String[] arg10,String tactics,String[][] user, int[] weight,String[][] CF) throws IOException{

       
        Path file2 = FileSystems.getDefault().getPath("", "negshell.pl");
        int index=0;
        byte[] fileArray1;
        fileArray1 = Files.readAllBytes(file2);
        String str = new String(fileArray1, "UTF-8");
//        String[][] aux=new String[4][];
        String[][] aux1={{"characteristics","feedback"},{"general_benefit","benefit_set","opponent_benefit","deadline","consistent"},
                       {"negotiator"}};
        
        String[][] aux= new String[aux1.length+user.length][];
        
        for(int j=0; j<aux1.length;j++){
            aux[j]=aux1[j];
        }
        for(int j=aux1.length; j<aux1.length+user.length;j++){
            aux[j]=user[j-aux1.length];
        }
//        String[] aux={"characteristics","feedback"};
//        String[] aux1={"general_benefit","benefit_set"};
//        String[] aux2={"negotiator"};
//        String[] aux3={"though","moderate", "soft"};
        String[] arg={"srcm","tft","lpcm","edcm","conc","tbou","rtft"};
        String[] tactics10={"Competition","Collaboration","Accomodation","max Score","max Score with deal","deal","deadline"};
        
//       for(int i=0; i<tactics10.length;i++){
//           if(tactics10[i].equals(tactics)){
//               index=i;
//           i=tactics10.length;
//       }
//       }
       
//        String[][] CF={{"100","100","-100","-100","-100","100","50","100","100","100","100"},{"50","50","100","50","50","50","100","100","100","100","100"}
//                      ,{"100","0","0","100","100","0","50","100","100","100","100"},{"100","100","0","0","0","100","100","100","100","100","100"}
//                      ,{"100","80","0","0","90","0","100","100","100","100","100"},{"100","0","100","0","80","0","50","100","100","100","100"}
//                      ,{"100","0","0","100","100","0","0","100"}};
        DecimalFormat twodecimal = new DecimalFormat("0.00");
        sol=new String[arg.length][2];
        ArrayList<Double> auxsol = new ArrayList<>();
        int[] best=new int[arg.length];
//        double[] auxsol=new double[arg.length];
            
        String[][] CFarg={{"40","-40","10","40","30","20","40","60"},{"90","-20","-20","-50","50","90","50","-50"},{"60","20","0","0","0","40","60","80"}
                         ,{"70","80","0","-20","40","40","60","80"},{"-50","0","100","100","10","-50","-10","10"},{"10","-10","40","50","-10","50","60","70"}
                         ,{"0","0","40","40","30","30","10","-50"}};
        String s = "";
        String all="";
         for(int j=0; j<1;j++){
             System.out.println( "\nTactic: "+tactics+"\n\n");
             all=all+"\nTactic: "+tactics+"\n\n";
        for(int i=0; i<arg.length;i++){
        s = "";
     Path file = FileSystems.getDefault().getPath("", arg[i]+".pl");
        try {
    // Create the empty file with default permissions, etc.
    Files.createFile(file);
} catch (FileAlreadyExistsException x) {
    System.err.format("file named %s" +
        " already exists%n", file);
} catch (IOException x) {
    // Some other sort of failure, such as permissions.
    System.err.format("createFile error: %s%n", x);
}
        s=string2file(aux,arg[i],CF[0],CFarg[i], weight)+ str;
        Files.write(file, s.getBytes());
            
                String t1 = "consult('"+arg[i]+".pl"+"')";
	Query q1 = new Query(t1);
         

		System.out.println( t1 + " " + (q1.hasSolution() ? "succeeded" : "failed") );

                
                String t4 = "solve(strategy(X),CF)";
		Query q4 = new Query(t4);
//                String a=q4.oneSolution().get("CF").toString();
                auxsol.add(i,Double.valueOf(twodecimal.format(Double.valueOf(q4.oneSolution().get("CF").toString())).replace(",", ".")));
//                System.out.println( t4 + " is " +(q4.hasSolution() ? "provable" : "not provable") );
//		System.out.println( "first solution of " + t4 + ": CF = " + q4.oneSolution().get("CF"));
        }
        for(int i=0; i<arg.length;i++){
        best[i] =auxsol.indexOf(Collections.max(auxsol));
        sol[i][0]=arg[best[i]];
        sol[i][1]=""+auxsol.get(best[i]);
        auxsol.remove(best[i]);
        auxsol.add(best[i], -200.00);
        System.out.println( "Solution "+(i+1)+": "+"Strategy "+sol[i][0]+" with CF= "+ sol[i][1]+"\n");
        all=all+"Solution "+(i+1)+": "+"Strategy "+sol[i][0]+" with CF= "+ sol[i][1]+"\n";
        }
        auxsol.removeAll(auxsol);
         }
        System.out.println(all);
//        return sol;
            }
    /**
     * @param args the command line arguments
     */
    public void main(String[] args) throws IOException {
        
         String[] arg={"srcm","tft","lpcm","edcm","conc","tbou","rtft"};
        String tactics="Competition";
        String[][] user={{"though","moderate", "soft"}};
//        String[][] sol=new String[arg.length][1];
        int[] w={1,50,3};
        String[][] CF={{"100","100","-100","-100","-100","100","50","100","100","100","100"},{"50","50","100","50","50","50","100","100","100","100","100"}
                      ,{"100","0","0","100","100","0","50","100","100","100","100"},{"100","100","0","0","0","100","100","100","100","100","100"}
                      ,{"100","80","0","0","90","0","100","100","100","100","100"},{"100","0","100","0","80","0","50","100","100","100","100"}
                      ,{"100","0","0","100","100","0","0","100","100","100","100"}};
        
            try{
			new ExpertSystem(arg,tactics,user,w,CF);
		}
		catch (IOException e) {
                    	e.printStackTrace();
		
                }
            
            
    }
          String string2file(String[][] aux,String Arg,String[] CF,String[] CFarg, int[] weight){
        String a ="";
        
        a=createrule("strategy",aux[0],"100",aux[0].length,"X")+createrule("characteristics",aux[1],CF[0],aux[1].length,Arg)+createfact("general_benefit",String.valueOf(Double.valueOf(CFarg[0])*Double.valueOf(CF[1])/100),Arg)
         +createfact("benefit_set",String.valueOf(Double.valueOf(CFarg[1])*Double.valueOf(CF[2])/100),Arg)+createfact("opponent_benefit",String.valueOf(Double.valueOf(CFarg[2])*Double.valueOf(CF[3])/100),Arg)
         +createfact("deadline",String.valueOf(Double.valueOf(CFarg[3])*Double.valueOf(CF[4])/100),Arg)+createfact("consistent",String.valueOf(Double.valueOf(CFarg[4])*Double.valueOf(CF[5])/100),Arg)
         +createrule("feedback",aux[2],CF[6],aux[2].length,Arg)+createrule("negotiator",aux[3],CF[7],aux[3].length,Arg)+createfact(aux[3][0],String.valueOf(Double.valueOf(CFarg[5+weight[0]])*Double.valueOf(CF[8])/100),Arg);
        
        
//                +createfact("though",String.valueOf(Double.valueOf(CFarg[5])*Double.valueOf(CF[8])/100),Arg)
//         +createfact("moderate",String.valueOf(Double.valueOf(CFarg[6])*Double.valueOf(CF[9])/100),Arg)+createfact("soft",String.valueOf(Double.valueOf(CFarg[7])*Double.valueOf(CF[10])/100),Arg)+"\n\n";
        return a;
    }
    String createrule(String Then, String[] If, String Cf, int ifs, String Arg){
        String a ="";
        Then=cp(Then, Arg);
        for (int i=0; i<ifs; i++){
            if(i>0){
               a=a+","+cp(If[i],Arg); 
            }else{
            a=a+cp(If[i],Arg);
            }
        }
        a="rule(("+Then+" :- ("+a+")),"+Cf+").\n";
        return a;
    }
        String createfact(String Then, String Cf, String Arg){
        String a ="";
        
        Then=cp(Then, Arg);
        
        a="rule("+Then+","+Cf+").\n";
        return a;
    }
        String cp(String Name, String Arg){
        String a ="";
        
        a=Name+"("+Arg+")";
        return a;
    }
}
