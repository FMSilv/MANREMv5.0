package scheduling;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import SchedulingModels.Afonso_Thermal_Hydro_Wind;
import SchedulingModels.Conejo;
import SchedulingModels.Moghimi;
import SchedulingModels.Zhang;
//import com.sun.org.apache.xml.internal.resolver.Catalog;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
import java.lang.Object;
//import java.util.List;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lpsolve.LpSolveException;
//import static scheduling.ProducerScheduling.Lista_output;
import wholesalemarket_SMP.InputData_Agents;

/**
 *
 * @author Af
 */
        
public class ProducerScheduling extends JFrame {
    
    private WindCosts costswindow;
//    public InputData_Agents MainGenerator;
    public static int a =0;
    private ErrorMessage errormess;
    //private SchedulingOutput outpuchart;
    private SchedulingOutput outpuchart;
    public static int rowtoadd;
    public static int delay;
    public static double CustoCO2;
    public static double CustoNO2;
    public static double[][][] Lista_output; //Vpool; Vbc1; Bbc2; Vsc1; Vsc2; Cmarg 
    public static String [][] data_selec;
    public static ArrayList<String> CommitedID = new ArrayList();
    int order = 0;
   
    /**
     * Creates new form UnitSelection
     */
    public static int model_index = 0; 
    int HORIZON = 24; //24horas
    public static String GencoSelected; 
    public static int nrows = 0;
    public double vetor [];
    public static double [] Ppool;
    //public static double [] Ppool = {2, 2, 2, 2, 2, 2, 3, 4, 6, 5, 6, 3, 5, 5, 5, 1, 3, 4, 5, 4, 3, 5, 3, 3};
    //public double STbil_peak = 80; double STbil_halfpeak = 65; double STbil_offpeak = 48; double STbil_superoffpeak = 20;
    public double [] peak =         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0};
    public double [] halfpeak =     {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1};
    public double [] offpeak =      {1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public double [] superoffpeak = {0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static int [] Shiftingturn =    {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0};          
    public double [] PmeanBC = new double [24] ;
    public static String [] PeriodsLabels = {"1:00-3:00h","3:00-7:00h","7:00-9:00h","9:00-18:00h","18:00-23:00h","23:00-1:00h"};
    public static double [][][] BCprices = new double [24][2][2];
    double varBC = 2;  //variação em relação ao preço médio dos contratos
    private static InputData_Agents Generator;
    public int index_Genco = 0;
    
   
    
    public void computeBCprices(){
          for(int i = 0; i < HORIZON; i++){
          PmeanBC[i] = peak[i]*AddGenerator.InfoGENCO.get(index_Genco).getBCPrices(3) + halfpeak[i]*AddGenerator.InfoGENCO.get(index_Genco).getBCPrices(2) + offpeak[i]*AddGenerator.InfoGENCO.get(index_Genco).getBCPrices(1) + superoffpeak[i]*AddGenerator.InfoGENCO.get(index_Genco).getBCPrices(0);
         // BCprices[i][1][0] = AddGenerator.InfoGENCO.get(order).getBCPrices_purchase(i);   //Buying Price (Block 1)
         // BCprices[i][0][0] = AddGenerator.InfoGENCO.get(order).getBCPrices_sale(i);;   //Selling Price (Block 1)
          BCprices[i][1][0] = PmeanBC[i] + varBC;   //Buying Price (Block 1)
          //BCprices[i][1][1] = PmeanBC[i] + 2*varBC; //Buying Price (Block 2)
          BCprices[i][0][0] = PmeanBC[i] - varBC;   //Selling Price (Block 1)
          //BCprices[i][0][1] = PmeanBC[i] - 2*varBC; //Selling Price (Block 2)
          } 
    }
    
    
    public ProducerScheduling(String Name, InputData_Agents mainGenerator) {
        
        Generator =mainGenerator;
        initComponents();
        this.setTitle("Unit Selection");
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
      
         
        ChooseGENCO.removeAllItems();
         ChooseGENCO.insertItemAt(Name,0);
        ChooseGENCO.setSelectedItem(Name);
        
         for(int i = 0; i<AddGenerator.InfoGENCO.size();i++){
            if(AddGenerator.InfoGENCO.get(i).getName().equals(ChooseGENCO.getSelectedItem().toString())){
                index_Genco = i;
            }
        }

        computeBCprices();  //Cria Array com preços dos contratos bilaterais

        
//        ChooseGENCO.insertItemAt(" ",0);
//        int ind = 1;
//        for(int i = 0; i < AddGenerator.InfoGENCO.size();i++) 
//            if(AddGenerator.InfoGENCO.get(i).isIsScheduled() == false){
//                ChooseGENCO.insertItemAt(AddGenerator.InfoGENCO.get(i).getName(),ind); 
//                ind++;
//    }        
        

        
        ChooseModel.setEnabled(true);
        checkcascade.setEnabled(false);
        delay_box.setEnabled(false);  delay_box.setEditable(false);
        CO2_box.setEnabled(false);    CO2_box.setEditable(false);
        NO2_box.setEnabled(false);    NO2_box.setEditable(false);

        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        SelecTable = new javax.swing.JTable();
        Cancel = new javax.swing.JButton();
        GENERATE = new javax.swing.JButton();
        Back = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ChooseModel = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        ChooseGENCO = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        checkcascade = new javax.swing.JCheckBox();
        delay_box = new javax.swing.JTextField();
        delaytext = new javax.swing.JLabel();
        delayunit = new javax.swing.JLabel();
        Mess1 = new javax.swing.JLabel();
        Mess2 = new javax.swing.JLabel();
        unSelect = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        CO2_box = new javax.swing.JTextField();
        CO2_text = new javax.swing.JLabel();
        CO2_unit = new javax.swing.JLabel();
        NO2_text = new javax.swing.JLabel();
        NO2_box = new javax.swing.JTextField();
        NO2_unit = new javax.swing.JLabel();
        PanelDescription = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        DescriptionText = new javax.swing.JTextArea();

        jLabel5.setText("Delay:");

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel6.setText("h");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Producing Units", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N

        SelecTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Selected", "ID", "Technology", "Fuel", "Power (MW)", "MCost (USD/MW)", "#"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        SelecTable.setAutoscrolls(false);
        jScrollPane1.setViewportView(SelecTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 571, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        GENERATE.setText("Generate Sheduling");
        GENERATE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GENERATEActionPerformed(evt);
            }
        });

        Back.setText("Back");
        Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackActionPerformed(evt);
            }
        });

        jLabel1.setText("Scheduling Model:");

        ChooseModel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Thermal Portfolio", "Hydro + Wind Portfolio", "Thermal + Wind Portfolio", "Thermal + Hydro + Wind Portfolio" }));
        ChooseModel.setEnabled(false);
        ChooseModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseModelActionPerformed(evt);
            }
        });

        jLabel2.setText("Generator:");

        ChooseGENCO.setToolTipText("");
        ChooseGENCO.setEnabled(false);
        ChooseGENCO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseGENCOActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hydro Cascade", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N

        checkcascade.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        checkcascade.setText("Set Hydro Cascade");
        checkcascade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkcascadeActionPerformed(evt);
            }
        });

        delay_box.setEnabled(false);
        delay_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delay_boxActionPerformed(evt);
            }
        });

        delaytext.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        delaytext.setText("Delay:");

        delayunit.setFont(new java.awt.Font("Tahoma", 2, 9)); // NOI18N
        delayunit.setText("h");

        Mess1.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
        Mess1.setText("For Hydro Cascade, Please Select The");

        Mess2.setFont(new java.awt.Font("Lucida Grande", 0, 9)); // NOI18N
        Mess2.setText(" Hydro Units By The Desired # Order!");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(Mess2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(delaytext)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(delay_box, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(delayunit, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Mess1)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(checkcascade)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Mess1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Mess2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkcascade)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delayunit, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delay_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delaytext))
                .addGap(9, 9, 9))
        );

        unSelect.setText("Select");
        unSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unSelectActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Emissions", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N

        CO2_box.setEnabled(false);
        CO2_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CO2_boxActionPerformed(evt);
            }
        });

        CO2_text.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        CO2_text.setText("<html>CO<sub>2</sub></html>");

        CO2_unit.setFont(new java.awt.Font("Tahoma", 2, 9)); // NOI18N
        CO2_unit.setText("USD/Kg");

        NO2_text.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        NO2_text.setText("<html>NO<sub>2</sub></html>");

        NO2_box.setEnabled(false);
        NO2_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NO2_boxActionPerformed(evt);
            }
        });

        NO2_unit.setFont(new java.awt.Font("Tahoma", 2, 9)); // NOI18N
        NO2_unit.setText("USD/Kg");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CO2_text)
                    .addComponent(NO2_text))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(NO2_box, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                    .addComponent(CO2_box))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(CO2_unit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(NO2_unit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CO2_text)
                    .addComponent(CO2_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CO2_unit, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NO2_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NO2_text)
                    .addComponent(NO2_unit, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Features", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N

        DescriptionText.setEditable(false);
        DescriptionText.setBackground(new java.awt.Color(240, 240, 240));
        DescriptionText.setColumns(20);
        DescriptionText.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        DescriptionText.setRows(5);
        DescriptionText.setBorder(null);
        DescriptionText.setCaretColor(new java.awt.Color(240, 240, 240));
        DescriptionText.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jScrollPane2.setViewportView(DescriptionText);

        javax.swing.GroupLayout PanelDescriptionLayout = new javax.swing.GroupLayout(PanelDescription);
        PanelDescription.setLayout(PanelDescriptionLayout);
        PanelDescriptionLayout.setHorizontalGroup(
            PanelDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
        );
        PanelDescriptionLayout.setVerticalGroup(
            PanelDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ChooseGENCO, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(ChooseModel, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(PanelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(452, 452, 452)
                                .addComponent(Back)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(GENERATE)
                                .addGap(18, 18, 18)
                                .addComponent(Cancel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(unSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 2, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(ChooseGENCO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(ChooseModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(PanelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(unSelect)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Cancel)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Back)
                        .addComponent(GENERATE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
//CANCEL BUTTON 
    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
    this.dispose();    }//GEN-LAST:event_CancelActionPerformed

    private void ChooseModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChooseModelActionPerformed
        clearselection(); //all Selections to "false"
        resetboxes();     //all additional boxes "disabled"
        model_index = ChooseModel.getSelectedIndex();
        nrows = 0;        
        switch (model_index){       //CALCULO DO TAMANHO DO ARRAY PARA A TABELA 

            case 0:     //INIT POSITION       
                RefreshbyGENCO();
                 break;
          
            case 1:     //CONEJO POSITION
                  for(DataThermal t : AddGenerator.InfoSheet_t){                    
                    if(t.getGENCO_name().equals(GencoSelected) == true){
                     nrows++;}
                    }
                RefreshbyModel();
                break;
        
            case 2:   //MOGHIMI POSITION
                for(DataHydro h : AddGenerator.InfoSheet_h){
                    if(h.getGENCO_name().equals(GencoSelected) == true){
                          nrows++;}}
           
                    for(DataWind w : AddGenerator.InfoSheet_w){
                        if(w.getGENCO_name().equals(GencoSelected) == true){
                            nrows++;}}
                RefreshbyModel();
                break;
       
            case 3:     //Zhang POSITION                
                for(DataWind w : AddGenerator.InfoSheet_w){
                    if(w.getGENCO_name().equals(GencoSelected) == true){
                        nrows++;}}
        
                for(DataThermal t : AddGenerator.InfoSheet_t){
                    if(t.getGENCO_name().equals(GencoSelected) == true){
                        nrows++;}}
                RefreshbyModel();
                break;
        
            case 4:     //Afonso new model POSITION                
                for(DataWind w : AddGenerator.InfoSheet_w){
                    if(w.getGENCO_name().equals(GencoSelected) == true){
                        nrows++;}}
        
                for(DataHydro h : AddGenerator.InfoSheet_h){
                    if(h.getGENCO_name().equals(GencoSelected) == true){
                        nrows++;}}
                
                for(DataThermal t : AddGenerator.InfoSheet_t){
                    if(t.getGENCO_name().equals(GencoSelected) == true){
                        nrows++;}}
                RefreshbyModel();
                break;
               
        }
           
    }//GEN-LAST:event_ChooseModelActionPerformed

    private void ChooseGENCOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChooseGENCOActionPerformed
        GencoSelected = ChooseGENCO.getSelectedItem().toString();
//        if (ChooseGENCO.getSelectedIndex() != 0){
            ChooseModel.setEnabled(true);      
            ChooseModel.setSelectedIndex(0);
//        }else{
//            ChooseModel.setEnabled(false);
//            ChooseModel.setSelectedIndex(0);
//            DescriptionText.setText("");         
//        }
        clearselection();
        RefreshbyGENCO();    
    }//GEN-LAST:event_ChooseGENCOActionPerformed

    private void BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackActionPerformed
      //Refreshvalues ();   // TODO add your handling code here:
    }//GEN-LAST:event_BackActionPerformed

    private void unSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unSelectActionPerformed

    if(ChooseModel.getSelectedIndex() != 0){
        try{                            //MUDA ESTADO DA CENTRAL: SELECTED ou UNSELECTED
            SelectButtonDisplay();
            String ID = SelecTable.getValueAt(SelecTable.getSelectedRow(), 1).toString();
            
            for(int u = 0; u < AddGenerator.InfoSheet_t.size(); u++){
                if(AddGenerator.InfoSheet_t.get(u).ID == ID){
                    AddGenerator.InfoSheet_t.get(u).setSelection(!AddGenerator.InfoSheet_t.get(u).Selection);
                }
            }
            
            for(int u = 0; u < AddGenerator.InfoSheet_h.size(); u++){
                if(AddGenerator.InfoSheet_h.get(u).ID == ID){
                    AddGenerator.InfoSheet_h.get(u).setSelection(!AddGenerator.InfoSheet_h.get(u).Selection);
                }
            }
            
            for(int u = 0; u < AddGenerator.InfoSheet_w.size(); u++){
                if(AddGenerator.InfoSheet_w.get(u).ID == ID){
                    AddGenerator.InfoSheet_w.get(u).setSelection(!AddGenerator.InfoSheet_w.get(u).Selection);
                }
            }
             
            
            
            if(SelecTable.getValueAt(SelecTable.getSelectedRow(), 2) == "Hydro"){  
                System.out.print("is Hydro \n");
                int index_to_update;
                for(int u = 0; u < AddGenerator.InfoSheet_h.size(); u++){
                    if(AddGenerator.InfoSheet_h.get(u).ID == ID){
                        //index_to_update = u;
                        
                        if(AddGenerator.InfoSheet_h.get(u).Selection == false){
                            order--;
                            int deleted_index = Integer.parseInt(AddGenerator.InfoSheet_h.get(u).Cascadeorder);
                            AddGenerator.InfoSheet_h.get(u).Cascadeorder = "";
                            RefreshOrder(deleted_index);
                        }else{
                            order++;
                            AddGenerator.InfoSheet_h.get(u).Cascadeorder = Integer.toString(order);
                        }
                    }
                }    
            }
            
            RefreshbyModel();
            resetboxes();

        }catch(ArrayIndexOutOfBoundsException exception){}
}else{
        errormess = new ErrorMessage("","Please Select a GENCO And Scheduling Model");
        errormess.setVisible(true);
}      

    }//GEN-LAST:event_unSelectActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void delay_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delay_boxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_delay_boxActionPerformed

    private void checkcascadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkcascadeActionPerformed
if(checkcascade.isSelected() == true){
    delay_box.setEnabled(true); delay_box.setEditable(true);
    
}else{
    delay_box.setEnabled(false);  delay_box.setEditable(false); delay_box.setText(null);
    
}
    }//GEN-LAST:event_checkcascadeActionPerformed

    private void GENERATEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GENERATEActionPerformed
    model_index = ChooseModel.getSelectedIndex();
    GencoSelected = ChooseGENCO.getSelectedItem().toString();

    Ppool = AddGenerator.InfoGENCO.get(index_Genco).getPoolForecast();
    
    
    
    
    switch(model_index){
        case 1:
            ConejoModel();
            a=1;
//            Generator.openRiskAttitude();
            break;
            
        case 2:
            if(checkcascade.isSelected()){
                MoghimiModel_Cascade();
            a=1;
//            Generator.openRiskAttitude();
            }else{
                MoghimiModel_NOcascade();
                a=1;
//            Generator.openRiskAttitude();
            }
            break;
            
        case 3:
            ZhangModel();
            a=1;
//            Generator.openRiskAttitude();
            break;
            
        case 4:
            if(checkcascade.isSelected()){
                AfonsoModel_cascade();
                a=1;
//                Generator.openRiskAttitude();
            }else{
               AfonsoModel_NOcascade();
               a=1;
//               Generator.openRiskAttitude();
            }
           break;
    }
    }//GEN-LAST:event_GENERATEActionPerformed

    private void CO2_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CO2_boxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CO2_boxActionPerformed

    private void NO2_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NO2_boxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NO2_boxActionPerformed
 
    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProducerScheduling.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProducerScheduling.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProducerScheduling.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProducerScheduling.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProducerScheduling("", Generator).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Back;
    private javax.swing.JTextField CO2_box;
    private javax.swing.JLabel CO2_text;
    private javax.swing.JLabel CO2_unit;
    private javax.swing.JButton Cancel;
    public static javax.swing.JComboBox ChooseGENCO;
    public javax.swing.JComboBox ChooseModel;
    private javax.swing.JTextArea DescriptionText;
    private javax.swing.JButton GENERATE;
    private javax.swing.JLabel Mess1;
    private javax.swing.JLabel Mess2;
    private javax.swing.JTextField NO2_box;
    private javax.swing.JLabel NO2_text;
    private javax.swing.JLabel NO2_unit;
    private javax.swing.JPanel PanelDescription;
    private static javax.swing.JTable SelecTable;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JCheckBox checkcascade;
    private javax.swing.JTextField delay_box;
    private javax.swing.JLabel delaytext;
    private javax.swing.JLabel delayunit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton unSelect;
    // End of variables declaration//GEN-END:variables

   
    
public void RefreshOrder (int deletedindex){
        for(int o = 0; o<AddGenerator.InfoSheet_h.size(); o++){           
            if(AddGenerator.InfoSheet_h.get(o).Cascadeorder != ""){
                int oldnumb = Integer.parseInt(AddGenerator.InfoSheet_h.get(o).Cascadeorder);           
                if(oldnumb > deletedindex){
                    System.out.print(oldnumb+" "+ (oldnumb-1) +"\n");
                    AddGenerator.InfoSheet_h.get(o).setCascadeorder(String.valueOf(oldnumb-1));
            }
        }  }      
}    
    
public void SelectButtonDisplay (){
        if (SelecTable.getValueAt(SelecTable.getSelectedRow(),0) == "true"){
            unSelect.setText("Select");
        }else{
        unSelect.setText("Unselect");
        }           
    }   
    
public void RefreshbyGENCO(){
    nrows = 0;
    for(int k = 0; k < AddGenerator.InfoSheet_t.size(); k++){
        if(AddGenerator.InfoSheet_t.get(k).GENCO_name.equals(GencoSelected)){
            nrows++;}}      
    for(int k = 0; k < AddGenerator.InfoSheet_h.size(); k++){
        if(AddGenerator.InfoSheet_h.get(k).GENCO_name.equals(GencoSelected)){
            nrows++;}}  
    for(int k = 0; k < AddGenerator.InfoSheet_w.size(); k++){
        if(AddGenerator.InfoSheet_w.get(k).GENCO_name.equals(GencoSelected)){
            nrows++;}}  
            
    String data_selec[][] = new String[nrows][7];        
    int n = 0; 
    for(int k = 0; k < AddGenerator.InfoSheet_t.size(); k++){
        if(AddGenerator.InfoSheet_t.get(k).GENCO_name.equals(GencoSelected)){                    
            data_selec[n][0] = Boolean.toString(AddGenerator.InfoSheet_t.get(k).Selection);
            data_selec[n][1] = AddGenerator.InfoSheet_t.get(k).ID;
            data_selec[n][2] = AddGenerator.InfoSheet_t.get(k).Tech;
            data_selec[n][3] = AddGenerator.InfoSheet_t.get(k).Fuel;
            data_selec[n][4] = Double.toString(AddGenerator.InfoSheet_t.get(k).MaxP);
            data_selec[n][5] = Double.toString(AddGenerator.InfoSheet_t.get(k).VCost);
            data_selec[n][6] = "";
            n++;            
        }
    } 
    
      for(int k = 0; k < AddGenerator.InfoSheet_h.size(); k++){
        if(AddGenerator.InfoSheet_h.get(k).GENCO_name.equals(GencoSelected)){
                data_selec[n][0] = Boolean.toString(AddGenerator.InfoSheet_h.get(k).Selection);
                data_selec[n][1] = AddGenerator.InfoSheet_h.get(k).ID;
                data_selec[n][2] = AddGenerator.InfoSheet_h.get(k).Tech;
                data_selec[n][3] = AddGenerator.InfoSheet_h.get(k).Fuel;
                data_selec[n][4] = Double.toString(AddGenerator.InfoSheet_h.get(k).Pi);
                data_selec[n][5] = Double.toString(AddGenerator.InfoSheet_h.get(k).VCost);
                data_selec[n][6] = AddGenerator.InfoSheet_h.get(k).Cascadeorder;
                n++;
        }                                      
      } 
      
            for(int k = 0; k < AddGenerator.InfoSheet_w.size(); k++){
                if(AddGenerator.InfoSheet_w.get(k).GENCO_name.equals(GencoSelected)){
                data_selec[n][0] = Boolean.toString(AddGenerator.InfoSheet_w.get(k).Selection);
                data_selec[n][1] = AddGenerator.InfoSheet_w.get(k).ID;
                data_selec[n][2] = AddGenerator.InfoSheet_w.get(k).Tech;
                data_selec[n][3] = AddGenerator.InfoSheet_w.get(k).Fuel;
                data_selec[n][4] = Double.toString(AddGenerator.InfoSheet_w.get(k).MaxP);
                data_selec[n][5] = Double.toString(AddGenerator.InfoSheet_w.get(k).VCost);
                data_selec[n][6] = "";
                n++;
                }
            }
            SelecTable.setModel(new javax.swing.table.DefaultTableModel(
            data_selec,
            new String [] {
                "Selected", "ID", "Technology", "Fuel", "Power (MW)", "MCost (USD/MW)", "#"     
            }));
            
            
}

public void RefreshbyModel(){

/*
    if(checkcascade.isSelected()){  //ADICIONA NUMERO DE REFERENCIA (#) PARA A CASCADE
        int order = 0;              
        for(int i = 0; i < AddGenerator.InfoSheet_h.size(); i++){
            if(AddGenerator.InfoSheet_h.get(i).Selection){
                order = order+1;
                AddGenerator.InfoSheet_h.get(i).Cascadeorder = Integer.toString(order);
            }else{
                AddGenerator.InfoSheet_h.get(i).Cascadeorder = null;
            }
        }        
    }
    */

 String data_selec[][] = new String[nrows][7];
        
    switch(model_index){
        case 1: //CONEJO POSITION 
            int p = 0;
            for (int k = 0; k < AddGenerator.InfoSheet_t.size(); k++){   
                if(AddGenerator.InfoSheet_t.get(k).GENCO_name.equals(GencoSelected)){      
                    data_selec[p][0] = Boolean.toString(AddGenerator.InfoSheet_t.get(k).Selection);
                    data_selec[p][1] = AddGenerator.InfoSheet_t.get(k).ID;
                    data_selec[p][2] = AddGenerator.InfoSheet_t.get(k).Tech;
                    data_selec[p][3] = AddGenerator.InfoSheet_t.get(k).Fuel;
                    data_selec[p][4] = Double.toString(AddGenerator.InfoSheet_t.get(k).MaxP);
                    data_selec[p][5] = Double.toString(AddGenerator.InfoSheet_t.get(k).VCost);
                    data_selec[p][6] = "";
                    p++;
                }
            DescriptionText.setText(" - BC Price Volatility" +"\n"+ " - Buying/Selling Contracts"+"\n"+ " - Fixed and Marginal Costs"+"\n"+" - Production Ramp");
        }
            break;
            
        
        case 2: //Moghimi POSITION 
            int r = 0;
            for (int k = 0; k < AddGenerator.InfoSheet_h.size(); k++){
                if(AddGenerator.InfoSheet_h.get(k).GENCO_name.equals(GencoSelected)){
                    data_selec[r][0] = Boolean.toString(AddGenerator.InfoSheet_h.get(k).Selection);
                    data_selec[r][1] = AddGenerator.InfoSheet_h.get(k).ID;
                    data_selec[r][2] = AddGenerator.InfoSheet_h.get(k).Tech;
                    data_selec[r][3] = AddGenerator.InfoSheet_h.get(k).Fuel;
                    data_selec[r][4] = Double.toString(AddGenerator.InfoSheet_h.get(k).Pi);
                    data_selec[r][5] = Double.toString(AddGenerator.InfoSheet_h.get(k).VCost);
                    data_selec[r][6] = AddGenerator.InfoSheet_h.get(k).Cascadeorder;
                    r++;   
                }
            }
                        
            for (int k = 0; k < AddGenerator.InfoSheet_w.size(); k++){
                if(AddGenerator.InfoSheet_w.get(k).GENCO_name.equals(GencoSelected)){
                    data_selec[r][0] = Boolean.toString(AddGenerator.InfoSheet_w.get(k).Selection);
                    data_selec[r][1] = AddGenerator.InfoSheet_w.get(k).ID;
                    data_selec[r][2] = AddGenerator.InfoSheet_w.get(k).Tech;
                    data_selec[r][3] = AddGenerator.InfoSheet_w.get(k).Fuel;
                    data_selec[r][4] = Double.toString(AddGenerator.InfoSheet_w.get(k).MaxP);
                    data_selec[r][5] = Double.toString(AddGenerator.InfoSheet_w.get(k).VCost);
                    data_selec[r][6] = "";
                    r++;
                }
            }
            DescriptionText.setText(" - Reservoir spacial coupling"+"\n"+ " - Reservoir Volume Constraints" +"\n"+ " - Performance Curves (3) Linearization"+"\n"+ " - Hydro Start-up Costs"+"\n"+ " - Wind Production Mandatory Dispatch");
   
        break;
        
        case 3: //Zhang POSITION       
            int l = 0;
            for (int k = 0; k < AddGenerator.InfoSheet_t.size(); k++){
                if(AddGenerator.InfoSheet_t.get(k).GENCO_name.equals(GencoSelected)){
                    data_selec[l][0] = Boolean.toString(AddGenerator.InfoSheet_t.get(k).Selection);          
                    data_selec[l][1] = AddGenerator.InfoSheet_t.get(k).ID;
                    data_selec[l][2] = AddGenerator.InfoSheet_t.get(k).Tech;
                    data_selec[l][3] = AddGenerator.InfoSheet_t.get(k).Fuel;
                    data_selec[l][4] = Double.toString(AddGenerator.InfoSheet_t.get(k).MaxP);
                    data_selec[l][5] = Double.toString(AddGenerator.InfoSheet_t.get(k).VCost);
                    data_selec[l][6] = "";
                    l++;
                }
            }

            for (int k = 0; k < AddGenerator.InfoSheet_w.size(); k++){
                if(AddGenerator.InfoSheet_w.get(k).GENCO_name.equals(GencoSelected)){
                    data_selec[l][0] = Boolean.toString(AddGenerator.InfoSheet_w.get(k).Selection);
                    data_selec[l][1] = AddGenerator.InfoSheet_w.get(k).ID;
                    data_selec[l][2] = AddGenerator.InfoSheet_w.get(k).Tech;
                    data_selec[l][3] = AddGenerator.InfoSheet_w.get(k).Fuel;
                    data_selec[l][4] = Double.toString(AddGenerator.InfoSheet_w.get(k).MaxP);
                    data_selec[l][5] = Double.toString(AddGenerator.InfoSheet_w.get(k).VCost);
                    data_selec[l][6] = "";
                    l++;
                }     
            }
            DescriptionText.setText(" - Thermal Production Ramp" +"\n"+ " - Thermal Fixed and Marginal Costs"+"\n"+ " - Thermal Emissions Costs"+"\n"+ " - Thermal Start-Up/Shut-Down Costs"+ "\n"+" - Wind Fixed and Marginal Costs"+"\n"+ " - Wind Production Mandatory Dispatch");
       break;
       
       
        case 4:     //Afonso new model POSITION   
            int n = 0;
            for (int k = 0; k < AddGenerator.InfoSheet_t.size(); k++){
                if(AddGenerator.InfoSheet_t.get(k).GENCO_name.equals(GencoSelected)){
                    data_selec[n][0] = Boolean.toString(AddGenerator.InfoSheet_t.get(k).Selection);          
                    data_selec[n][1] = AddGenerator.InfoSheet_t.get(k).ID;
                    data_selec[n][2] = AddGenerator.InfoSheet_t.get(k).Tech;
                    data_selec[n][3] = AddGenerator.InfoSheet_t.get(k).Fuel;
                    data_selec[n][4] = Double.toString(AddGenerator.InfoSheet_t.get(k).MaxP);
                    data_selec[n][5] = Double.toString(AddGenerator.InfoSheet_t.get(k).VCost);
                    data_selec[n][6] = "";
                    n++;
                }
          }
            
            for (int k = 0; k < AddGenerator.InfoSheet_h.size(); k++){
                if(AddGenerator.InfoSheet_h.get(k).GENCO_name.equals(GencoSelected)){
                    data_selec[n][0] = Boolean.toString(AddGenerator.InfoSheet_h.get(k).Selection);
                    data_selec[n][1] = AddGenerator.InfoSheet_h.get(k).ID;
                    data_selec[n][2] = AddGenerator.InfoSheet_h.get(k).Tech;
                    data_selec[n][3] = AddGenerator.InfoSheet_h.get(k).Fuel;
                    data_selec[n][4] = Double.toString(AddGenerator.InfoSheet_h.get(k).Pi);
                    data_selec[n][5] = Double.toString(AddGenerator.InfoSheet_h.get(k).VCost);
                    data_selec[n][6] = AddGenerator.InfoSheet_h.get(k).Cascadeorder;
                    n++;
                }
            }

            for (int k = 0; k < AddGenerator.InfoSheet_w.size(); k++){
                if(AddGenerator.InfoSheet_w.get(k).GENCO_name.equals(GencoSelected)){
                    data_selec[n][0] = Boolean.toString(AddGenerator.InfoSheet_w.get(k).Selection);
                    data_selec[n][1] = AddGenerator.InfoSheet_w.get(k).ID;
                    data_selec[n][2] = AddGenerator.InfoSheet_w.get(k).Tech;
                    data_selec[n][3] = AddGenerator.InfoSheet_w.get(k).Fuel;
                    data_selec[n][4] = Double.toString(AddGenerator.InfoSheet_w.get(k).MaxP);
                    data_selec[n][5] = Double.toString(AddGenerator.InfoSheet_w.get(k).VCost);
                    data_selec[n][6] = "";
                    n++;
                }
            }
            DescriptionText.setText(" - Thermal Production Ramp" +"\n"+ " - Thermal Fixed and Marginal Fuel Costs"+"\n"+ " - Thermal Emissions Costs"+"\n"+ " - Thermal Start-Up/Shut-Down Costs"+"\n"+" - Reservoir spacial coupling"+"\n"+ " - Reservoir Volume Constraints" +"\n"+ " - Performance Curves (3) Linearization"+"\n"+ " - Hydro Fixed and Maginal Costs" + "\n"+" - Hydro Start-up Costs" +"\n"+ " - Wind Fixed and Marginal Costs"+"\n"+ " - Wind Production Mandatory Dispatch");
        break;   
    }

           
          SelecTable.setModel(new javax.swing.table.DefaultTableModel(
            data_selec,
            new String [] {
                "Selected", "ID", "Technology", "Fuel", "Power (MW)", "MCost (USD/MW)", "#"                 
            }));
           
            ListSelectionModel cellSelectionModel = SelecTable.getSelectionModel();
            cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                String selectedData = null;

                int selectedRow = SelecTable.getSelectedRow();

                try{
                    if (SelecTable.getValueAt(selectedRow,0) == "true"){
                        unSelect.setText("Unselect");
                    }else{
                        unSelect.setText("Select");
                    }
                }catch(ArrayIndexOutOfBoundsException p){}
      }});
           
                            
         
}

public void clearselection(){
    for(int u = 0; u < AddGenerator.InfoSheet_t.size(); u++)
        AddGenerator.InfoSheet_t.get(u).setSelection(false);
            
    for(int u = 0; u < AddGenerator.InfoSheet_h.size(); u++)
        AddGenerator.InfoSheet_h.get(u).setSelection(false);
            
    for(int u = 0; u < AddGenerator.InfoSheet_w.size(); u++)
        AddGenerator.InfoSheet_w.get(u).setSelection(false);
            
}

public void resetboxes(){
    //activar informações da hydro cascade
        int activehydro = 0;   
        for(int k = 0; k<AddGenerator.InfoSheet_h.size(); k++){
            if (AddGenerator.InfoSheet_h.get(k).Selection){
                activehydro++;
            }
        }
        if(activehydro > 1){
            checkcascade.setEnabled(true);
        }else{
            checkcascade.setEnabled(false); checkcascade.setSelected(false);
            delay_box.setEnabled(false);    delay_box.setEditable(false);   
        }
        
        //activar informações das emissões
        int activethermal = 0;   
        for(int k = 0; k<AddGenerator.InfoSheet_t.size(); k++){
            if (AddGenerator.InfoSheet_t.get(k).Selection == true){
                activethermal++;        
            }
        }
        if(activethermal>0 && (ChooseModel.getSelectedIndex() == 3 || ChooseModel.getSelectedIndex() == 4)){
            CO2_box.setEnabled(true); CO2_box.setEditable(true);    CO2_box.setText(String.valueOf(AddGenerator.preGasesPrice[0]));
            NO2_box.setEnabled(true); CO2_box.setEditable(true);    NO2_box.setText(String.valueOf(AddGenerator.preGasesPrice[1]));
         
        }else{
            CO2_box.setEnabled(false); CO2_box.setEditable(false);
            NO2_box.setEnabled(false); CO2_box.setEditable(false);
        }
            
}

public void ConejoModel(){    
	try{            

           
            Lista_output  = new Conejo().executeConejo(AddGenerator.InfoSheet_t,HORIZON,Ppool, Shiftingturn, BCprices); 
                                                        
    outpuchart = new SchedulingOutput(Generator);
    outpuchart.setVisible(true);
    this.dispose();
            
        }catch (LpSolveException e) {
            e.printStackTrace();
	} 

}

public void ZhangModel(){    	
    try{
            CustoCO2 = Double.parseDouble(CO2_box.getText());
            CustoNO2 = Double.parseDouble(NO2_box.getText());
            

            Lista_output = new Zhang().executeZhang (AddGenerator.InfoSheet_t, AddGenerator.InfoSheet_w, HORIZON,CustoCO2, CustoNO2,Ppool, Shiftingturn,BCprices);  
            
            outpuchart = new SchedulingOutput(Generator);
            outpuchart.setVisible(true);
            this.dispose();
        
        }catch (LpSolveException e) {
            e.printStackTrace();
	}         
    
    }    

public void MoghimiModel_NOcascade(){                                     
       
    try{
        
        Lista_output = new Moghimi().executeMoghimi_NOcascade(AddGenerator.InfoSheet_h,AddGenerator.InfoSheet_w,HORIZON,Ppool, Shiftingturn,BCprices);
            
        outpuchart = new SchedulingOutput(Generator);
        outpuchart.setVisible(true);
        this.dispose();
            
    }catch (LpSolveException e) {
        e.printStackTrace();    
    }
}

public void MoghimiModel_Cascade(){
    try{    
        delay = Integer.parseInt(delay_box.getText());
        
        if (delay>0){
             //SOLVER - HYDRO CASCADE
            try{  
                            
                Lista_output = new Moghimi().executeMoghimi_cascade(AddGenerator.InfoSheet_h,AddGenerator.InfoSheet_w,delay,HORIZON,Ppool, Shiftingturn,BCprices);
                            
                outpuchart = new SchedulingOutput(Generator);
                outpuchart.setVisible(true);
                this.dispose();

            }catch (LpSolveException e) {
                e.printStackTrace();
            } 
                  
        }else{  //if delay <=0
            errormess = new ErrorMessage("Please Check Entered Data:","Delay Period Must be Positive");
            errormess.setVisible(true);   
        }
  
    }catch(NumberFormatException e){
        errormess = new ErrorMessage("Please Check Entered Data:","Delay Period Must be Integer and Positive");
        errormess.setVisible(true);
        } 

}

public void AfonsoModel_cascade(){
    try{    
        delay = Integer.parseInt(delay_box.getText());
        CustoCO2 = Double.parseDouble(CO2_box.getText());
        CustoNO2 = Double.parseDouble(NO2_box.getText());

        if (delay>0){

            try{  
                            
                Lista_output = new Afonso_Thermal_Hydro_Wind().executeAfonso_cascade(AddGenerator.InfoSheet_t, AddGenerator.InfoSheet_h, AddGenerator.InfoSheet_w, HORIZON, CustoCO2,CustoNO2,Ppool, Shiftingturn, BCprices, delay);
                            
                outpuchart = new SchedulingOutput(Generator);
                outpuchart.setVisible(true);
                this.dispose();

            }catch (LpSolveException e) {
                e.printStackTrace();
            } 
                  
        }else{  //if delay <=0
            errormess = new ErrorMessage("Please Check Entered Data:","Delay Period Must be Positive");
            errormess.setVisible(true);   
        }
  
    }catch(NumberFormatException e){
        errormess = new ErrorMessage("Please Check Entered Data:","Delay Period Must be Integer and Positive");
        errormess.setVisible(true);
        } 

}

public void AfonsoModel_NOcascade(){
try{
            CustoCO2 = Double.parseDouble(CO2_box.getText());
            CustoNO2 = Double.parseDouble(NO2_box.getText());
            

            Lista_output = new Afonso_Thermal_Hydro_Wind().executeAfonso_NOcascade(AddGenerator.InfoSheet_t, AddGenerator.InfoSheet_h, AddGenerator.InfoSheet_w, HORIZON, CustoCO2,CustoNO2,Ppool, Shiftingturn, BCprices);
            
            outpuchart = new SchedulingOutput(Generator);
            outpuchart.setVisible(true);
            this.dispose();
        
        }catch (LpSolveException e) {
            e.printStackTrace();
	}         
    
    } 

}
