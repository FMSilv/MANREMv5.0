/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;


import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import scheduling.DataHydro;
import scheduling.DataThermal;
import scheduling.DataWind;
import scheduling.ProducerScheduling.*;
import static scheduling.ProducerScheduling.ChooseGENCO;
import wholesalemarket_SMP.InputData_Agents;
/**
 *
 * @author AfonsoMCardoso
 */
public final class SchedulingOutput extends javax.swing.JFrame {

    double[] totVsc = new double[24];
    public static double FProfit;
    double[] totVbc = new double[24];
    double[] totVpool = new double[24];
    int HORIZON = 24;
    public InputData_Agents MainGenerator;
    public static ArrayList<PoolOffers> PoolOffers = new ArrayList();
    public static ArrayList<Contract> BCOffers = new ArrayList();
    public static ArrayList<Transactions> BCTransactions = new ArrayList();
    
    /**
     * Creates new form FinalScheduling
     */
    public SchedulingOutput(InputData_Agents Generator)  {
        
        initComponents();
        MainGenerator=Generator;
        this.setTitle("Scheduling Output");
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        charts();
        Profit_label.setText("USD "+ NumberFormat.getCurrencyInstance(Locale.US).format(FProfit));
        
    }
    
    public static void main(String[] args) {
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        SAVEbutton = new javax.swing.JButton();
        jtabedpan = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        PricesCosts = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        Profit_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton2.setText("Discard");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        SAVEbutton.setText("Save");
        SAVEbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVEbuttonActionPerformed(evt);
            }
        });

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jtabedpan.addTab("Trading Settlement", jPanel2);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));
        jtabedpan.addTab("Pool ", jPanel4);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jtabedpan.addTab("Production", jPanel3);

        PricesCosts.setLayout(new javax.swing.BoxLayout(PricesCosts, javax.swing.BoxLayout.LINE_AXIS));
        jtabedpan.addTab("Prices & Costs", PricesCosts);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Forecasted Profit", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 1, 10))); // NOI18N

        Profit_label.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        Profit_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Profit_label.setText("Forecasted Profit:  ");
        Profit_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Profit_label, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(134, 134, 134))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Profit_label, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(33, 33, 33)
                .addComponent(jButton2)
                .addGap(29, 29, 29)
                .addComponent(SAVEbutton)
                .addGap(23, 23, 23))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtabedpan)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jtabedpan, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(SAVEbutton)
                                .addGap(18, 23, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jtabedpan.getAccessibleContext().setAccessibleName("Settlement");
        jtabedpan.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        resetSelections();
        this.dispose();
    }
    
    
   public void charts(){
      getdatatochart();  
      

    //SETTLEMENT CHART  
    DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
      
      for (int t = 0; t < 24; t++) {
          String hour = String.valueOf(t+1);
            dataset1.addValue(totVpool[t], "Pool",hour);  
            dataset1.addValue(totVsc[t], "Selling Contracts",hour);
            dataset1.addValue(totVbc[t], "Buying Contracts",hour);
        }    
    JFreeChart chart1 = ChartFactory.createLineChart("", "Hour", "Power(MW)", dataset1, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot p1 = chart1.getCategoryPlot();
    p1.setRangeGridlinePaint(Color.black);
    ChartPanel chartpn1 = new ChartPanel(chart1);  
    chartpn1.setDomainZoomable(true);
    jPanel2.removeAll();
    jPanel2.add(chartpn1,BorderLayout.CENTER);
    jPanel2.updateUI();
    
    
    //PRODUCTION CHART  
    DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
     
    int nUnits = ProducerScheduling.Lista_output[0][1].length - 1;
    for (int t = 0; t < 24; t++) {
          String hour = String.valueOf(t+1);
          for (int u = 1; u <= nUnits; u++) {  
            dataset2.addValue(ProducerScheduling.Lista_output[t][1][u],ProducerScheduling.CommitedID.get(u-1) ,hour);  
            }    
    }
    JFreeChart chart2 = ChartFactory.createStackedBarChart("", "Hour", "Power(MW)", dataset2, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot p2 = chart2.getCategoryPlot();
    p2.setRangeGridlinePaint(Color.black);
    ChartPanel chartpn2 = new ChartPanel(chart2);  
    chartpn2.setDomainZoomable(true);
    jPanel3.removeAll();
    jPanel3.add(chartpn2,BorderLayout.CENTER);
    jPanel3.updateUI();
    
    
    //Prices & Costs Chart
    DefaultCategoryDataset dataset3 = new DefaultCategoryDataset();
     
    int Units = ProducerScheduling.Lista_output[0][1].length - 1;
    for (int t = 0; t < 24; t++) {
          String hour = String.valueOf(t+1);
          dataset3.addValue(ProducerScheduling.Ppool[t], "Pool",hour);
          dataset3.addValue(ProducerScheduling.BCprices[t][0][0], "Selling Contract",hour);
          dataset3.addValue(ProducerScheduling.BCprices[t][1][0], "Buying Contract",hour);
          for (int u = 1; u <= Units; u++) {  
            dataset3.addValue(ProducerScheduling.Lista_output[t][2][u],ProducerScheduling.CommitedID.get(u-1) ,hour);  
           }
    }
    
    JFreeChart chart3 = ChartFactory.createLineChart("", "Hour", "Price/Cost (USD/MW)", dataset3, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot p3 = chart3.getCategoryPlot();
    p2.setRangeGridlinePaint(Color.black);
    ChartPanel chartpn3 = new ChartPanel(chart3);  
    chartpn3.setDomainZoomable(true);
    PricesCosts.removeAll();
    PricesCosts.add(chartpn3,BorderLayout.CENTER);
    PricesCosts.updateUI();
    
    
    //Pool Chart
        DefaultCategoryDataset dataset4 = new DefaultCategoryDataset();
     
    int nUnit = ProducerScheduling.Lista_output[0][1].length - 1;
    for (int t = 0; t < 24; t++) {
          String hour = String.valueOf(t+1);
          for (int u = 1; u <= nUnit; u++) {  
            dataset4.addValue(ProducerScheduling.Lista_output[t][0][u],ProducerScheduling.CommitedID.get(u-1) ,hour);  
           }
    }
    
    JFreeChart chart4 = ChartFactory.createStackedAreaChart("", "Hour", "Power (MW)", dataset4, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot p4 = chart4.getCategoryPlot();
    p2.setRangeGridlinePaint(Color.black);
    ChartPanel chartpn4 = new ChartPanel(chart4);  
    chartpn4.setDomainZoomable(true);
    jPanel4.removeAll();
    jPanel4.add(chartpn4,BorderLayout.CENTER);
    jPanel4.updateUI();
       
    }//GEN-LAST:event_jButton2ActionPerformed

    private void SAVEbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVEbuttonActionPerformed


        for(int i = 0; i < AddGenerator.InfoGENCO.size(); i++){
            if(AddGenerator.InfoGENCO.get(i).getName() == ProducerScheduling.GencoSelected)
                AddGenerator.InfoGENCO.get(i).setIsScheduled(true);
            }
        
        addBCtoMarketPlatform();
        addPooltoMarketPlatform();
        
        try{
            //Import_export.writeSchedulingOutput(output, GENCOname, HORIZON);
            //Import_export.exportPool(PoolOffers, HORIZON,ChooseGENCO.getSelectedItem().toString());
            Import_export.exportPool(PoolOffers, HORIZON,ChooseGENCO.getSelectedItem().toString());
        }
        catch(Exception e){
            System.out.print(e);
        }
        
        resetSelections();
        
        this.dispose();
        MainGenerator.openRiskAttitude();
    }//GEN-LAST:event_SAVEbuttonActionPerformed

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PricesCosts;
    private javax.swing.JLabel Profit_label;
    private javax.swing.JButton SAVEbutton;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jtabedpan;
    // End of variables declaration//GEN-END:variables
  
    
public void getdatatochart(){
  //Get total hourly pool volumes
    int nUnits = ProducerScheduling.Lista_output[0][1].length - 1;
        for (int t = 0; t < 24; t++){
            for(int u = 1; u<nUnits+1; u++){
                totVpool[t] = totVpool[t]+ProducerScheduling.Lista_output[t][0][u];
            }
        }

       //Get total bilateral pool volumes
        for (int t = 0; t < 24; t++){
                totVsc[t] = ProducerScheduling.Lista_output[t][0][0] + ProducerScheduling.Lista_output[t][1][0];
                totVbc[t] = -ProducerScheduling.Lista_output[t][2][0] - ProducerScheduling.Lista_output[t][3][0];
            }
        }
  
public void addPooltoMarketPlatform(){   
        
    for (int unit = 1; unit<ProducerScheduling.Lista_output[0][1].length; unit++){
        double[] provVolume = new double[24]; 
        double[] provMarginalCost = new double[24];
        
        for(int t = 0; t < HORIZON; t++){                  
            
            provVolume[t] = ProducerScheduling.Lista_output[t][0][unit];
            provMarginalCost[t] = ProducerScheduling.Lista_output[t][2][unit];
        }

          PoolOffers P1 = new PoolOffers(ProducerScheduling.GencoSelected,provVolume, provMarginalCost);                       
          SchedulingOutput.PoolOffers.add(P1);      
    }
    
    for (int k = 0; k<PoolOffers.size();k++){
            System.out.print("\n");
                for(int i = 0; i<24; i++){
                    System.out.print(PoolOffers.get(k).getVolume(i)+" ");
                }
                System.out.print("\n");
                for(int i = 0; i<24; i++){
                    System.out.print(PoolOffers.get(k).getMarginalCost(i)+" ");             
                }
                 System.out.print("\n");
    }
}


public void addBCtoMarketPlatform(){
        int periodindex = 0; 
        for(int t = 0; t < 24; t++){
            if(ProducerScheduling.Shiftingturn[t] == 1){           
                Contract bc1 = new Contract(ProducerScheduling.GencoSelected,"IDENT" ,"Sale" ,ProducerScheduling.PeriodsLabels[periodindex], ProducerScheduling.Lista_output[t][0][0], ProducerScheduling.BCprices[t][0][0],false);
                BCOffers.add(bc1);
                Contract bc2 = new Contract(ProducerScheduling.GencoSelected,"IDENT" ,"Sale" ,ProducerScheduling.PeriodsLabels[periodindex], ProducerScheduling.Lista_output[t][1][0], ProducerScheduling.BCprices[t][0][1],false);
                BCOffers.add(bc2);
                Contract bc3 = new Contract(ProducerScheduling.GencoSelected,"IDENT" ,"Purchase" ,ProducerScheduling.PeriodsLabels[periodindex], ProducerScheduling.Lista_output[t][2][0], ProducerScheduling.BCprices[t][1][0],false);
                BCOffers.add(bc3);
                Contract bc4 = new Contract(ProducerScheduling.GencoSelected,"IDENT" ,"Purchase" ,ProducerScheduling.PeriodsLabels[periodindex], ProducerScheduling.Lista_output[t][3][0], ProducerScheduling.BCprices[t][1][1],false);       
                BCOffers.add(bc4);
                periodindex++;
            }
                }

            int nSC = 1;   //Counters for Contracts identifiers
            int nBC = 1;  //Counters for Contracts identifiers
                for(int i = BCOffers.size()-1; i >= 0; i--){
                    if(BCOffers.get(i).getVolume() == 0){
                        BCOffers.remove(i);
                    }
                }
                for(int i = 0; i < BCOffers.size(); i++){
                    if(BCOffers.get(i).getOrder() == "Sale"){
                        BCOffers.get(i).setID("SC"+nSC);
                        nSC++;
                    }else{
                        BCOffers.get(i).setID("BC"+nBC);
                        nBC++;
                    }  
            }


}

public void resetSelections(){
for (int i = 0; i<AddGenerator.InfoSheet_t.size(); i++){
    AddGenerator.InfoSheet_t.get(i).setSelection(false);
}
for (int i = 0; i<AddGenerator.InfoSheet_h.size(); i++){
    AddGenerator.InfoSheet_h.get(i).setSelection(false);
     AddGenerator.InfoSheet_h.get(i).setCascadeorder("");
}
for (int i = 0; i<AddGenerator.InfoSheet_w.size(); i++){
    AddGenerator.InfoSheet_w.get(i).setSelection(false);
}
}


    
public static class Contract{          
      
      String GENCO_name;  
      String ID;
      String Order;
      String Period;
      double Volume;
      double Price;
      boolean fractionOfTotal;

        public Contract(String GENCO_name, String ID, String Order, String Period, double Volume, double Price, boolean fractionOfTotal) {
            this.GENCO_name = GENCO_name;
            this.ID = ID;
            this.Order = Order;
            this.Period = Period;
            this.Volume = Volume;
            this.Price = Price;
            this.fractionOfTotal = fractionOfTotal;
        }

        public String getGENCO_name() {
            return GENCO_name;
        }

        public void setGENCO_name(String GENCO_name) {
            this.GENCO_name = GENCO_name;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getOrder() {
            return Order;
        }

        public void setOrder(String Order) {
            this.Order = Order;
        }

        public String getPeriod() {
            return Period;
        }

        public void setPeriod(String Period) {
            this.Period = Period;
        }

        public double getVolume() {
            return Volume;
        }

        public void setVolume(double Volume) {
            this.Volume = Volume;
        }

        public double getPrice() {
            return Price;
        }

        public void setPrice(double Price) {
            this.Price = Price;
        }

        public boolean isFractionOfTotal() {
            return fractionOfTotal;
        }

        public void setFractionOfTotal(boolean fractionOfTotal) {
            this.fractionOfTotal = fractionOfTotal;
        }
      
     }

public static class Transactions{

        String PurchaseOffer;  
        String SaleOffer;
        double Volume;
        double Price;
        double TransactionCost;

        public Transactions(String PurchaseOffer, String SaleOffer, double Volume, double Price, double TransactionCost) {
            this.PurchaseOffer = PurchaseOffer;
            this.SaleOffer = SaleOffer;
            this.Volume = Volume;
            this.Price = Price;
            this.TransactionCost = TransactionCost;
        }

        public String getPurchaseOffer() {
            return PurchaseOffer;
        }

        public void setPurchaseOffer(String PurchaseOffer) {
            this.PurchaseOffer = PurchaseOffer;
        }

        public String getSaleOffer() {
            return SaleOffer;
        }

        public void setSaleOffer(String SaleOffer) {
            this.SaleOffer = SaleOffer;
        }

        public double getVolume() {
            return Volume;
        }

        public void setVolume(double Volume) {
            this.Volume = Volume;
        }

        public double getPrice() {
            return Price;
        }

        public void setPrice(double Price) {
            this.Price = Price;
        }

        public double getTransactionCost() {
            return TransactionCost;
        }

        public void setTransactionCost(double TransactionCost) {
            this.TransactionCost = TransactionCost;
        }
      
  

}

public static class PoolOffers{

    String GENCOID;
    double [] VolumePool = new double [24];
    double [] MarginalCost = new double [24];

        public PoolOffers(String GENCOID, double[] VolumePool, double [] MarginalCost) {
            this.GENCOID = GENCOID;
            this.VolumePool = VolumePool;
            this.MarginalCost = MarginalCost;
        }

        public String getGENCOID() {
            return GENCOID;
        }

        public void setGENCOID(String GENCOID) {
            this.GENCOID = GENCOID;
        }

        public double getVolume(int i) {
            return VolumePool[i];
        }

        public void setVolume(double Volume, int i) {
            this.VolumePool[i] = Volume;
        }

        public double getMarginalCost(int i) {
            return MarginalCost[i];
        }

        public void setMarginalCost(double MarginalCost, int i) {
            this.MarginalCost[i] = MarginalCost;
        }



}
}






