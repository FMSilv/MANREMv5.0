package wholesalemarket_SMP;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
//import wholesalemarket_SMP.InputData_Agents;
import static wholesalemarket_SMP.SMP_Market_Controller.END_HOUR;
import static wholesalemarket_SMP.SMP_Market_Controller.START_HOUR;

public class SMP_DynamicWindow extends JFrame implements ActionListener {

    private InputData_Agents agentData;
    private final int startHour, endHour, timePeriod;
    private boolean isSeller;
    private String agentType;
    private String[] agentNames;
    private ArrayList<String> usedNames;
    private int range;
    private int agentID;
    private ArrayList<double[]> oldPrice, oldPower;
    private String oldName;
    private String selectedAgent;
    private boolean isUpdate;
    private ArrayList<ArrayList<Float>> addPrice;
    private ArrayList<ArrayList<Float>> addPower;
    public int INDEX=0;

    private int uploadIndex_seller = 0;
    private int uploadIndex_buyer = 0;

    private JFrame frame;

    private JPanel panel;

    //private JLabel agentName_Label;
    private final JLabel agentName_Label;
    private final JLabel[] priceLabel = {new JLabel("Price"), new JLabel("Price"), new JLabel("Price")};
    private final JLabel[] hourTitleLabel = {new JLabel("Hour"), new JLabel("Hour"), new JLabel("Hour")};
    private final JLabel[] powerLabel = {new JLabel("Power"), new JLabel("Power"), new JLabel("Power")};
    private final JLabel[] powerUnitsLabel = {new JLabel("[MW]"), new JLabel("[MW]"), new JLabel("[MW]")};
    private final JLabel[] priceUnitsLabel = {new JLabel("[€/MWh]"), new JLabel("[€/MWh]"), new JLabel("[€/MWh]")};

    private final JLabel[] hourLabel = {new JLabel("00"), new JLabel("01"), new JLabel("02"), new JLabel("03"),
        new JLabel("04"), new JLabel("05"), new JLabel("06"), new JLabel("07"), new JLabel("08"), new JLabel("09"),
        new JLabel("10"), new JLabel("11"), new JLabel("12"), new JLabel("13"), new JLabel("14"), new JLabel("15"),
        new JLabel("16"), new JLabel("17"), new JLabel("18"), new JLabel("19"), new JLabel("20"), new JLabel("21"),
        new JLabel("22"), new JLabel("23")};
      private final JTextField[][] priceInfo;
      private final JTextField[][] powerInfo;
//    private final JTextField[] priceInfo = {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};
//    private final JTextField[] powerInfo = {new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};

    private JButton addButton;
    private JButton cancelButton;
    private JButton defaultButton;

    private JComboBox comboBox_Name = new JComboBox();
    
        int column_nr = 1;
        int window_X = 340;
        int window_Y = 380;
        int posX1 = 30;
        int posName = -40;
        int posX2 = posX1 + 40;
        int posX3 = posX2 + 50;
        int posX4 = posX3 + 10;
        int posX5 = posX4 + 60;
        int posX6 = posX5 + 5;

        int posY0 = 10;
        int posY1 = 30;
        int posY2 = posY1 + 25;
        int posY3 = posY2 + 15;

        int fieldY = 25;

        int column2 = 210;
        int column3 = column2 + 210;
        
        int posX = 0;
        int posY = 80 + posY1;
        int max_Pos = 0;

    public SMP_DynamicWindow(InputData_Agents _agentData, boolean _isSeller, int _hi, int _hf, String _selectedAgent) {
        
        agentData = _agentData;
        startHour = _hi;
        endHour = _hf;
        isSeller = _isSeller;
        
//        priceInfo = new JTextField[agentData.getUploadOffers_pricefile().size()][24];
//        powerInfo = new JTextField[agentData.getUploadOffers_pricefile().size()][24];
        
        isUpdate = false;
        agentID = 0;
        timePeriod = endHour - startHour + 1;
        oldName = "";
        selectedAgent = _selectedAgent;
        comboBox_Name.removeAllItems();
        
        
        if (_isSeller) {
            agentType = "Generator";
        priceInfo = new JTextField[agentData.getUploadOffers_pricefile().size()][24];
        powerInfo = new JTextField[agentData.getUploadOffers_pricefile().size()][24];
        for (int i=0; i<agentData.getUploadOffers_pricefile().size();i++){
        comboBox_Name.addItem(selectedAgent+" - "+agentData.getUploadOffers_namefile().get(i));
        }
            updateValues(agentData.getUploadOffers_pricefile(), agentData.getUploadOffers_powerfile());
        } else {
            agentType = "Retailer";
            priceInfo = new JTextField[agentData.getUploadBids_pricefile().size()][24];
            powerInfo = new JTextField[agentData.getUploadBids_pricefile().size()][24];
            for (int i=0; i<agentData.getUploadBids_pricefile().size();i++){
        comboBox_Name.addItem(selectedAgent+" - "+agentData.getUploadBids_namefile().get(i));
        }
            updateValues(agentData.getUploadBids_pricefile(), agentData.getUploadBids_powerfile());
        }
        agentName_Label = new JLabel("Name: ");
        usedNames = agentData.getAgentList();
        //initComboBox();
                    
    }

    public SMP_DynamicWindow(InputData_Agents _agentData, boolean _isSeller, int _hi, int _hf, ArrayList<double[]> _oldPrice, ArrayList<double[]> _oldPower, String _oldName, int _ID, String _selectedAgent) {
        
        
        agentData = _agentData;
       
        
        
        startHour = _hi;
        endHour = _hf;
        isSeller = _isSeller;
        isUpdate = true;
        agentID = _ID;
        oldPrice = _oldPrice;
        oldPower = _oldPower;
        oldName = _oldName;
        selectedAgent = _selectedAgent;
        timePeriod = endHour - startHour + 1;

        if (_isSeller) {
            agentType = "Generator";
            priceInfo = new JTextField[agentData.getUploadOffers_pricefile().size()][24];
        powerInfo = new JTextField[agentData.getUploadOffers_pricefile().size()][24];
             comboBox_Name.removeAllItems();
        for (int i=0; i<agentData.getUploadOffers_pricefile().size();i++){
        comboBox_Name.addItem(selectedAgent+" - "+agentData.getUploadOffers_namefile().get(i));
        }
        updateValues(agentData.getUploadOffers_pricefile(), agentData.getUploadOffers_powerfile());
        } else {
            agentType = "Retailer";
            priceInfo = new JTextField[agentData.getUploadBids_pricefile().size()][24];
            powerInfo = new JTextField[agentData.getUploadBids_pricefile().size()][24];
                 comboBox_Name.removeAllItems();
        for (int i=0; i<agentData.getUploadBids_pricefile().size();i++){
        comboBox_Name.addItem(selectedAgent+" - "+agentData.getUploadBids_namefile().get(i));
        }
         updateValues(agentData.getUploadBids_pricefile(), agentData.getUploadBids_powerfile());
        }
        agentName_Label = new JLabel("Name: ");
        usedNames = agentData.getAgentList();
   
    }

    public void updateValues(ArrayList<double[]> price, ArrayList<double[]> power) {
        double[] Price, Power;
        for (int j = 0; j < price.size(); j++) {
            Price=price.get(j);
            Power=power.get(j);
        for (int i = startHour; i <= endHour; i++) {
            priceInfo[j][i] = new JTextField();
            powerInfo[j][i] = new JTextField();
            priceInfo[j][i].setText(String.format("%.2f", (Price[i])).replace(",", "."));
            powerInfo[j][i].setText(String.format("%.2f", (Power[i])).replace(",", "."));
        }
    }
    }

    /*public void initComboBox() {
        boolean verif;

        comboBox_Name.removeAllItems();
        //comboBox_Name.addItem("Select Agent");

        if (usedNames.isEmpty()) {
            for (int i = 0; i < agentNames.length; i++) {
                comboBox_Name.addItem(agentNames[i]);
            }
        } else {
            for (int i = 0; i < agentNames.length; i++) {
                verif = false;
                for (String name : usedNames) {
                    if (name.equalsIgnoreCase(agentNames[i])) {
                        verif = true;
                        break;
                    }
                }
                if (!verif) {
                    comboBox_Name.addItem(agentNames[i]);
                }
            }
        }
        if (comboBox_Name.getItemCount() == 0) {
            comboBox_Name.addItem("");
        }
        comboBox_Name.setSelectedIndex(0);
    }
*/
    public void createWindow() {
        range = (endHour - startHour); // To separate TextFields into three columns

      
        if (range > 15) {
            column_nr = 3;
            window_X = 720;
            posX1 = 10;
            posName = 0;
        } else if (range > 7) {
            column_nr = 2;
            window_X = 520;
            posX1 = 20;
            posName = 0;
        }

   

        panel = new JPanel();
        panel.setLayout(null);
//        panel.setMaximumSize(new Dimension(20000, 20000));
//        panel.setMinimumSize(new Dimension(window_X, window_Y));
//        panel.setPreferredSize(new Dimension(window_X, window_Y));
      
               
        

       
        //agentName_Label = new JLabel("Agent's Name: ");
        //selectAgent = new JComboBox();
        //selectAgent.addItem("Select Agent");

        frame = new JFrame(agentType);
//        frame.setMaximumSize(new Dimension(window_X, window_Y));
        frame.setSize(new Dimension(window_X, window_Y));
//        frame.setMinimumSize(new Dimension(window_X, window_Y));
//        frame.getContentPane().setMaximumSize(new Dimension(window_X, window_Y));
//        frame.getContentPane().setSize(new Dimension(window_X, window_Y));
//        frame.getContentPane().setMinimumSize(new Dimension(window_X, window_Y));
        
//        frame.setResizable(false);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Position on the screen: Center

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(50, 30, 300, 50);
        
//        JPanel contentPane = new JPanel(null);
//        contentPane.setPreferredSize(new Dimension(window_X, window_Y));
//        contentPane.add(scrollPane);
        
//        frame.setContentPane(contentPane);
//        frame.pack();
        
        frame.add(scrollPane);
        frame.setVisible(true);
//        frame.add(panel);
        
     

        agentName_Label.setBounds(posX2 + posName, posY0, 110, fieldY);
        comboBox_Name.setBounds(posX2 + posName + 110, posY0, 200, fieldY);
        //selectAgent.setBounds(140, posY1, 200, fieldY);
        panel.add(agentName_Label);
        panel.add(comboBox_Name);
        comboBox_Name.setSelectedIndex(0);
        
                      comboBox_Name.addActionListener(new ActionListener() {                  
                  
            @Override
            public void actionPerformed(ActionEvent e) {
                 
        
        posX = 0;
        int j=comboBox_Name.getSelectedIndex();
  
        
            posY = 80 + posY1;
            posX = 0;
        for (int i = startHour; i <= endHour; i++) {
          priceInfo[j][i].setText(priceInfo[j][i].getText());
          powerInfo[j][i].setText(powerInfo[j][i].getText());
//          panel.remove(powerInfo[INDEX][i]);        
           panel.add(priceInfo[j][i]);
           panel.add(powerInfo[j][i]);
//           System.out.println("P"+j+i+"="+priceInfo[j][i].getText()+" Limites: "+priceInfo[j][i].getBounds()+" Limites"+"(P"+INDEX+i+"):  "+priceInfo[INDEX][i].getBounds());
//           panel.add(powerInfo[j][i]);
////            panel.add(hourLabel[i]);
        }
        INDEX=j;
        
        
        
            }
            });
        

        
        for (int i = 0; i < column_nr; i++) {
            hourTitleLabel[i].setBounds(posX2 + posX, posY2, 50, fieldY);
            panel.add(hourTitleLabel[i]);
            priceLabel[i].setBounds(posX4 + posX + 5, posY2, 50, fieldY);
            panel.add(priceLabel[i]);
            priceUnitsLabel[i].setBounds(posX3 + posX + 10, posY3, 60, fieldY);
            panel.add(priceUnitsLabel[i]);
            powerLabel[i].setBounds(posX5 + posX + 10, posY2, 50, fieldY);
            panel.add(powerLabel[i]);
            powerUnitsLabel[i].setBounds(posX6 + posX + 10, posY3, 50, fieldY);
            panel.add(powerUnitsLabel[i]);
            posX += column2;
        }
        
        
//                posX = 0;
        
        for (int j = 0; j < priceInfo.length; j++) {
            posY = 80 + posY1;
            posX = 0;
        for (int i = startHour; i <= endHour; i++) {
              if (i == startHour + 8) {
                posX = column2;
                posY = 80 + posY1;
            }
            if (i == startHour + 16) {
                posX = column3;
                posY = 80 + posY1;
            }
            hourLabel[i].setBounds(posX2 + posX + 10, posY, 30, fieldY);
            priceInfo[j][i].setBounds(posX3 + posX, posY, 65, fieldY);
            powerInfo[j][i].setBounds(posX5 + posX, posY, 65, fieldY);
            posY += 30;
//            hourLabel[i].setBounds(posX2 + 0 + 10, posY, 30, fieldY);
            
            
//            priceInfo[j][i].setBounds(posX3 + posX, posY, 65, fieldY);
//            powerInfo[j][i].setBounds(posX5 + posX, posY, 65, fieldY);
//            posY += 30;
            if (posY > max_Pos) {
                max_Pos = posY;
            }
            if (j<1) {
            panel.add(priceInfo[j][i]);
            panel.add(powerInfo[j][i]);
            panel.add(hourLabel[i]);
        }
        }
        }
        

        
        addButton = new JButton("Save");
        addButton.addActionListener(this);
        int posY_Button = max_Pos + 20;
        int posX_Button = window_X / 2;
        addButton.setBounds(posX_Button - 45, posY_Button, 85, fieldY);
        panel.add(addButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setBounds(posX_Button + 65, posY_Button, 85, fieldY);
        panel.add(cancelButton);
        defaultButton = new JButton("Default");
        defaultButton.addActionListener(this);
        defaultButton.setBounds(posX_Button - 135, posY_Button, 85, fieldY);
        panel.add(defaultButton);
              

        frame.setSize(window_X, posY_Button + 80);
//        frame.setMaximumSize(new Dimension(window_X, window_Y));
//        frame.setMinimumSize(new Dimension(window_X, window_Y));
        
    }

    public String verifInfo() {
        String warning = "";
        addPrice = new ArrayList<ArrayList<Float>>();
        addPower = new ArrayList<ArrayList<Float>>();
        ArrayList<Float> addPrices = new ArrayList<Float>();
        ArrayList<Float> addPowers = new ArrayList<Float>();

        for (int j = 0; j < priceInfo.length; j++) {
           for (int i = 0; i < 24; i++) {
               
        
            if (i >= startHour && i <= endHour) {
                if (Float.parseFloat(priceInfo[j][i].getText()) < 0.0f) {
                    warning += "Price at " + i + "h must be bigger than zero!\n";
                } else if (Float.parseFloat(powerInfo[j][i].getText()) < 0.0f) {
                    warning += "Power at " + i + "h must be bigger than zero!\n";
                } else {
                    addPrices.add(Float.parseFloat(priceInfo[j][i].getText().replace(",", ".")));
                    addPowers.add(Float.parseFloat(powerInfo[j][i].getText().replace(",", ".")));
                }
            } else {
                addPrices.add(0.0f);
                addPowers.add(0.0f);
            }
        }
        addPrice.add(j, addPrices);
        addPower.add(j, addPowers);
        addPrices = new ArrayList<Float>();
        addPowers = new ArrayList<Float>();
        }
        return warning;
    }

    private String readValues(boolean _isCancelButton) {
        String warning = "";
       addPrice = new ArrayList<ArrayList<Float>>();
        addPower = new ArrayList<ArrayList<Float>>();
        ArrayList<Float> addPrices = new ArrayList<Float>();
        ArrayList<Float> addPowers = new ArrayList<Float>();
        try {
            if (_isCancelButton) {
                warning += verifInfo();
            } else {
                if (comboBox_Name.getSelectedIndex() != -1 && !comboBox_Name.getSelectedItem().toString().isEmpty()) {
                    warning += verifInfo();
                } else {
                    warning += "Please select one " + agentType + " agent!";
                }
            }

        } catch (Exception ex) {
            warning += "Incorrect Information\n";
        }
        return warning;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String warning = "";
        if (e.getSource() == addButton) {
            warning = readValues(false);
            if (warning.isEmpty()) {
                for (int i =0; i<addPrice.size();i++){
                    if (isSeller){
                agentData.setList(selectedAgent+" - "+agentData.getUploadOffers_namefile().get(i), agentID, addPrice.get(i), addPower.get(i), isSeller);
                    }else{
                   agentData.setList(selectedAgent+" - "+agentData.getUploadBids_namefile().get(i), agentID, addPrice.get(i), addPower.get(i), isSeller);     
                    }
                }
                agentData.setComboBox_Index();
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        warning,
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == cancelButton) {
            if (isUpdate) {
                updateValues(oldPrice, oldPower);
                warning = readValues(true);
                if (oldName.isEmpty()) {
                    warning += "An Error has occurred!";
                }
                if (warning.isEmpty()) {
                     for (int i =0; i<addPrice.size();i++){
                       if (isSeller){
                        agentData.setList(selectedAgent+" - "+agentData.getUploadOffers_namefile().get(i), agentID, addPrice.get(i), addPower.get(i), isSeller);
                       }else{
                        agentData.setList(selectedAgent+" - "+agentData.getUploadBids_namefile().get(i), agentID, addPrice.get(i), addPower.get(i), isSeller);     
                    }
                     }
                    agentData.setComboBox_Index();
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            warning,
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                agentData.setComboBox_Index();
                frame.dispose();
            }

        } else if (e.getSource() == defaultButton) {
            try {
                if (isSeller) {
                    updateValues(agentData.getUploadOffers_pricefile(), agentData.getUploadOffers_powerfile());
                    uploadIndex_seller++;
                    if (uploadIndex_seller == agentData.getUploadOffers_pricefile().size()) {
                        uploadIndex_seller = 0;
                    }
                } else {
                    updateValues(agentData.getUploadBids_pricefile(), agentData.getUploadBids_powerfile());
                    uploadIndex_buyer++;
                    if (uploadIndex_buyer == agentData.getUploadBids_pricefile().size()) {
                        uploadIndex_buyer = 0;
                    }
                }
            } catch (Exception ex) {
                defaultButton.setEnabled(false);
            }

        }
    }
}
