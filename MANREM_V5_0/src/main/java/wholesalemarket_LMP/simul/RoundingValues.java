package wholesalemarket_LMP.simul;

public class RoundingValues {
    
    private static final double TOL = 1.0E-9;
    private static final double Max_Rounding = 1.0E6;
        
    public static double correctRounding(double num) {
        for (int i = 1; i < Max_Rounding; i = i * 10) {
            if (Math.abs(num * i - Math.rint(num * i)) < TOL) {
                num = Math.rint(num * i) / i;
                break;
            }
        }
        return num;
    }
    
    public static double[][] correctRounding(double[][] data){
    for(int i=0; i<data.length; i++){
      for(int j=0; j<data[0].length; j++){
        for(int p=1; p<1.0E6; p=p*10){
          if(Math.abs(data[i][j]*p-Math.rint(data[i][j]*p)) < TOL){
            data[i][j] = Math.rint(data[i][j]*p)/p;
            break;
          }
        }
      }
    }
    return data;
  }

  public static double[] correctRounding(double[] data){
    for(int i=0; i<data.length; i++){
      for(int p=1; p<1.0E6; p=p*10){
        if(Math.abs(data[i]*p-Math.rint(data[i]*p)) < TOL){
          data[i] = Math.rint(data[i]*p)/p;
          break;
        }
      }
    }
    return data;
  }

}