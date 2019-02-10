package wholesalemarket_LMP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import wholesalemarket_LMP.simul.WholesaleMarket;

public class SupplierInputData_Dynamic extends JFrame implements ActionListener {

    private Supplier_InputParameters mainSupplier;
    private int excelIndex;
    private double[][] defaultData;
    private double[] initValuesMinPower;
    private double[] initValuesMaxPower;
    private double[] initValuesStartPrice;
    private double[] initValuesSlopePrice;
    private int atBus;
    private double[] submitValuesMinPower;
    private double[] submitValuesMaxPower;
    private double[] submitValuesStartPrice;
    private double[] submitValuesSlopePrice;
    private String submitAgentName;

    private static final int SUPPLIER_EXCEL_COLUMNS = 5;
    private static int excel_totalSuppliers;

    // JFRAME
    private JButton submitButton;
    private JButton defaultButton;
    private JButton cancelButton;
    
    private int timeSize;
    
    // _____________________ INICIO NOVA JANELA DE AGENTES ___________________
    private JPanel panel;
    private JFrame frame;

    private final JLabel agentName_Label = new JLabel("Agent Name: ");
    private final JLabel agentBus_Label = new JLabel("Bus: ");
    
    private JTextField agentName_info;
    private JTextField agentBus_info;
    
    private final JLabel[] startPriceLabel = {new JLabel("Price"), new JLabel("Price"), new JLabel("Price")};
    private final JLabel[] slopePriceLabel = {new JLabel("Slope"), new JLabel("Slope"), new JLabel("Slope")};
    private final JLabel[] hourTitleLabel = {new JLabel("Hour"), new JLabel("Hour"), new JLabel("Hour")};
    private final JLabel[] minCap_Label = {new JLabel("Min Power"), new JLabel("Min Power"), new JLabel("Max Power")};
    private final JLabel[] maxCap_Label = {new JLabel("Max Power"), new JLabel("Max Power"), new JLabel("Max Power")};
    private final JLabel[] powerUnitsLabel = {new JLabel("[MW]"), new JLabel("[MW]"), new JLabel("[MW]"), new JLabel("[MW]")};
    private final JLabel[] priceUnitsLabel = {new JLabel("[€/MWh]"), new JLabel("[€/MWh]"), new JLabel("[€/MWh]"), new JLabel("[€/MWh]")};
    
    private final JTextField[] startprice_Info = {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};
    private final JTextField[] minPower_Info = {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};
    private final JTextField[] slopeprice_Info = {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};
    private final JTextField[] maxPower_Info = {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};
    private final JLabel[] hourLabel = {new JLabel("00"), new JLabel("01"), new JLabel("02"), new JLabel("03"),
        new JLabel("04"), new JLabel("05"), new JLabel("06"), new JLabel("07"), new JLabel("08"), new JLabel("09"),
        new JLabel("10"), new JLabel("11"), new JLabel("12"), new JLabel("13"), new JLabel("14"), new JLabel("15"),
        new JLabel("16"), new JLabel("17"), new JLabel("18"), new JLabel("19"), new JLabel("20"), new JLabel("21"),
        new JLabel("22"), new JLabel("23")};
    
    // _____________________ FIM NOVA JANELA DE AGENTES ___________________
    
    public void showWindow_SupplierPowerDemand(String _agentName) {
        submitAgentName = _agentName;
        agentName_info = new JTextField(_agentName);
        agentBus_info = new JTextField();
        int window_X = 400;
        int window_Y = 600;
        
        int nameLabel_size = 90;
        int nameInfo_size = 170;
        int busLabel_size = 30;
        int busInfo_size = 30;
                
        int posName = 0;
        int posNameLabel = 30 + posName;
        int posNameinfo = posNameLabel + posName + nameLabel_size;
        int posBusLabel = posNameinfo + posName + nameInfo_size + 20;
        int posBusinfo = posBusLabel + posName + busLabel_size;
        
        int unitsLabel_size = 60;
        
        int posX1 = 30;
        int posX2 = posX1 + unitsLabel_size + 5;
        int posX3 = posX2 + unitsLabel_size + 5;
        int posX4 = posX3 + unitsLabel_size + 5;
        int posX5 = posX4 + unitsLabel_size + 5;
        int posX6 = posX5 + 5;
        
        int posY0 = 20;
        int posY1 = 40;
        int posY2 = posY1 + 25;
        int posY3 = posY2 + 15;
        
        int fieldY = 25;
        
        int posX = 0;
        int column_nr = 1;
        if (timeSize > 12) {
            column_nr = 2;
            window_X = 800;
        }
        
        panel = new JPanel();
        panel.setLayout(null);
        
        frame = new JFrame("Retailer Offer Data");
        frame.setSize(window_X, window_Y);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Position on the screen: Center
        frame.add(panel);
        
        agentName_Label.setBounds(posNameLabel, posY0, nameLabel_size, fieldY);
        agentName_info.setBounds(posNameinfo, posY0, nameInfo_size, fieldY);
        agentName_info.setEditable(false);
        
        agentBus_Label.setBounds(posBusLabel, posY0, busLabel_size, fieldY);
        agentBus_info.setBounds(posBusinfo, posY0, busInfo_size, fieldY);
        
        panel.add(agentName_Label);
        panel.add(agentName_info);
        panel.add(agentBus_Label);
        panel.add(agentBus_info);
        
        int column2 = window_X/2;
        
        for (int i = 0; i < column_nr; i++) {
            hourTitleLabel[i].setBounds(posX1 + posX, posY2, unitsLabel_size, fieldY);
            panel.add(hourTitleLabel[i]);
            
            startPriceLabel[i].setBounds(posX2 + posX, posY2, unitsLabel_size, fieldY);
            panel.add(startPriceLabel[i]);
            
            priceUnitsLabel[i].setBounds(posX2 + posX - 10, posY3, unitsLabel_size, fieldY);
            panel.add(priceUnitsLabel[i]);
            
            slopePriceLabel[i].setBounds(posX3 + posX, posY2, unitsLabel_size, fieldY);
            panel.add(slopePriceLabel[i]);
            
            priceUnitsLabel[i+2].setBounds(posX3 + posX - 10, posY3, unitsLabel_size, fieldY);
            panel.add(priceUnitsLabel[i+2]);
            
            minCap_Label[i].setBounds(posX4 + posX, posY2, unitsLabel_size + 10, fieldY);
            panel.add(minCap_Label[i]);
            
            powerUnitsLabel[i].setBounds(posX4 + posX + 15, posY3, unitsLabel_size, fieldY);
            panel.add(powerUnitsLabel[i]);
            
            maxCap_Label[i].setBounds(posX5 + posX + 10, posY2, unitsLabel_size + 10, fieldY);
            panel.add(maxCap_Label[i]);
            
            powerUnitsLabel[i+2].setBounds(posX5 + posX + 15, posY3, unitsLabel_size, fieldY);
            panel.add(powerUnitsLabel[i+2]);
            
            posX += column2;
        }
        
        posX = 0;
        int posY = 80 + posY1;
        int max_Pos = 0;
        for (int i = WholesaleMarket.START_HOUR; i <= WholesaleMarket.END_HOUR; i++) {
            if (i == WholesaleMarket.START_HOUR + 12) {
                posX = column2;
                posY = 80 + posY1;
            }
            hourLabel[i].setBounds(posX1 + posX + 10, posY, 30, fieldY);
            startprice_Info[i].setBounds(posX2 + posX - 15, posY, unitsLabel_size + 5, fieldY);
            slopeprice_Info[i].setBounds(posX3 + posX - 10, posY, unitsLabel_size  + 5, fieldY);
            minPower_Info[i].setBounds(posX4 + posX, posY, unitsLabel_size + 5, fieldY);
            maxPower_Info[i].setBounds(posX5 + posX + 5, posY, unitsLabel_size + 5, fieldY);
            
            posY += 30;
            if (posY > max_Pos) {
                max_Pos = posY;
            }
            panel.add(hourLabel[i]);
            panel.add(startprice_Info[i]);
            panel.add(slopeprice_Info[i]);
            panel.add(minPower_Info[i]);
            panel.add(maxPower_Info[i]);
        }
        
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        int posY_Button = max_Pos + 20;
        int posX_Button = window_X / 2;
        submitButton.setBounds(posX_Button + 65, posY_Button, 85, fieldY);
        panel.add(submitButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setBounds(posX_Button - 35, posY_Button, 85, fieldY);
        panel.add(cancelButton);
        defaultButton = new JButton("Default");
        defaultButton.addActionListener(this);
        defaultButton.setBounds(posX_Button - 135, posY_Button, 85, fieldY);
        panel.add(defaultButton);

        frame.setSize(window_X, posY_Button + 70);
        defaultValues();
        printValues();
    }
    
    public void printValues() {
        for (int i = WholesaleMarket.START_HOUR; i <= WholesaleMarket.END_HOUR; i++) {
            startprice_Info[i].setText(initValuesStartPrice[i-WholesaleMarket.START_HOUR]+"");
            slopeprice_Info[i].setText(initValuesSlopePrice[i-WholesaleMarket.START_HOUR]+"");
            minPower_Info[i].setText(initValuesMinPower[i-WholesaleMarket.START_HOUR]+"");
            maxPower_Info[i].setText(initValuesMaxPower[i-WholesaleMarket.START_HOUR]+"");
        }
    }

    public SupplierInputData_Dynamic(Supplier_InputParameters _mainSupplier) {
        mainSupplier = _mainSupplier;

        timeSize = WholesaleMarket.END_HOUR - WholesaleMarket.START_HOUR + 1;
        initValuesMinPower = new double[timeSize];
        initValuesMaxPower = new double[timeSize];
        initValuesStartPrice = new double[timeSize];
        initValuesSlopePrice = new double[timeSize];

        excel_totalSuppliers = GridGlobalParameters.get_Excel_totalSuppliers();
    }

    private void defaultValues() {
        if (excel_totalSuppliers > 0) {
            try {
                if (excelIndex == excel_totalSuppliers) {
                    excelIndex = 0;
                }
                int indexName = excelIndex + 1;

                defaultData = ReadExcel.readExcelData(WholesaleMarket.Default_Case, "SUPPLIER" + indexName, WholesaleMarket.HOUR_PER_DAY, SUPPLIER_EXCEL_COLUMNS, true);
                for (int h = 0; h < defaultData.length; h++) {
                    initValuesMinPower[h] = defaultData[h][0];
                    initValuesStartPrice[h] = defaultData[h][1];
                    initValuesSlopePrice[h] = defaultData[h][2];
                    initValuesMaxPower[h] = defaultData[h][3];
                }
                excelIndex++;
            } catch (Exception ex) {

            }
        }
    }

    private String getInputData() {
        String warning = "";
        submitValuesMinPower = new double[timeSize];
        submitValuesMaxPower = new double[timeSize];
        submitValuesStartPrice = new double[timeSize];
        submitValuesSlopePrice = new double[timeSize];
        try {
            atBus = Integer.parseInt(agentBus_info.getText());
            for (int h = 0; h < timeSize; h++) {
                submitValuesMinPower[h] = Double.parseDouble(minPower_Info[h+WholesaleMarket.START_HOUR].getText());
                submitValuesMaxPower[h] = Double.parseDouble(maxPower_Info[h+WholesaleMarket.START_HOUR].getText());
                submitValuesStartPrice[h] = Double.parseDouble(startprice_Info[h+WholesaleMarket.START_HOUR].getText());
                submitValuesSlopePrice[h] = Double.parseDouble(slopeprice_Info[h+WholesaleMarket.START_HOUR].getText());
            }
        } catch (Exception ex) {
            warning += "Error! Incorrect input data! \n";
        }
        return warning;
    }

    private String verifSupplierValues() {
        String warning = "";
        if (atBus <= 0 || atBus > mainSupplier.totalBusNr) {
            warning += "Agent BUS has to be lower than " + mainSupplier.totalBusNr + "! \n";
        }
        for (int h = 0; h < submitValuesMaxPower.length; h++) {
            if (submitValuesMaxPower[h] <= submitValuesMinPower[h]) {
                warning += "At hour " + (h + 1) + " -> máx MW has to be bigger than min MW! \n";
            }
            if (submitValuesStartPrice[h] < 0.0) {
                warning += "At hour " + (h + 1) + " -> value (start €) has to be bigger than 0.0! \n";
            }
            if (submitValuesSlopePrice[h] < 0.0) {
                warning += "At hour " + (h + 1) + " -> value (slope €) has to be bigger than 0.0! \n";
            }
        }
        return warning;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String warningGlobal = "";
            String warningVerify = "";
            try {
                warningGlobal = getInputData();
                if (!warningGlobal.isEmpty()) {
                    JOptionPane.showMessageDialog(this, warningGlobal,
                            "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                } else {
                    warningVerify = verifSupplierValues();
                    if (!warningVerify.isEmpty()) {
                        JOptionPane.showMessageDialog(this, warningVerify,
                                "Verify Input Data", JOptionPane.ERROR_MESSAGE);
                    } else {
                        System.out.println("Entrou no local correcto!");
                        System.out.println("Nome: " +submitAgentName);
                        mainSupplier.setNameFrame(submitAgentName);
                        System.out.println("Bus: " +atBus);
                        mainSupplier.setBusFrame(atBus);
                        System.out.println("Min Power 0: " +submitValuesMinPower[0]);
                        mainSupplier.setMinDemand(submitValuesMinPower);
                        System.out.println("Max Power 0: " +submitValuesMaxPower[0]);
                        mainSupplier.setMaxDemand(submitValuesMaxPower);
                        System.out.println("Start Price 0: " +submitValuesStartPrice[0]);
                        mainSupplier.setStartCost(submitValuesStartPrice);
                        System.out.println("Slope Price 0: " +submitValuesSlopePrice[0]);
                        mainSupplier.setSlopeCost(submitValuesSlopePrice);
                        System.out.println("Set Supplier List");
                        mainSupplier.set_SUPPLIER_List();
                        System.out.println("Init ComboBox");
                        mainSupplier.initComboBox();
                        frame.dispose();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error! Impossible to submit input data! \n",
                        "Verify Input Data", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == cancelButton) {
            frame.dispose();
        } else if (e.getSource() == defaultButton) {
            defaultValues();
            printValues();
        }
    }
}
