/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wholesalemarket_SMP;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import static javax.xml.bind.DatatypeConverter.parseFloat;
import marketpool.tools.AgentsFromExcel;
import scheduling.ProducerScheduling;

/**
 *
 * @author Administrator
 */
public class InputData_Agents extends javax.swing.JFrame {

    private SMP_Market_Controller controller;

    private ArrayList<String> agentList;
    private ArrayList<AgentData> agent_offers = new ArrayList<>();
    

    /*private ArrayList<String> sellerList;
     private ArrayList<String> buyerList;

     private ArrayList<AgentData> bids = new ArrayList<>();
     private ArrayList<AgentData> offers = new ArrayList<>();*/
    private ArrayList<Float> uploadBids_price = new ArrayList<>();
    private ArrayList<Float> uploadBids_power = new ArrayList<>();

    private ArrayList<double[]> uploadBids_pricefile = new ArrayList<>();
    private ArrayList<double[]> uploadBids_powerfile = new ArrayList<>();
    private ArrayList<double[]> uploadOffers_pricefile = new ArrayList<>();
    private ArrayList<double[]> uploadOffers_powerfile = new ArrayList<>();
    private ArrayList<String> uploadOffers_namefile = new ArrayList<>();
    private ArrayList<String> uploadBids_namefile = new ArrayList<>();

    private DefaultTableModel model;
    private SMP_DynamicWindow openDynamicWindow;

    private String outputfilename = "output.csv";
    private String[] agentName;

    private int startHour;
    private Agent market;
    private int endHour;
    private boolean isSeller;
    private String nameLabel;

    private static final String SEARCH_INFO = "Search...";

    /**
     * Creates new form InputData_Seller
     */
    public InputData_Agents(Agent Market, SMP_Market_Controller _controller, boolean _isSeller, int _startHour, int _endHour) {
        controller = _controller;
        agentList = new ArrayList<>();
        isSeller = _isSeller;
        initComponents();
        defineWindow();
        startHour = _startHour;
        endHour = _endHour;
        market = Market;
//        try {
//            
//            uploadFile("input.xlsx", "output-sym.csv");
//        } catch (Exception ex) {
//            System.out.println("InputData.java / InputData function -> " + ex);
//            readButton.setEnabled(false);
//        }
        setPanel(isSeller);
        submitButton.requestFocusInWindow();
        startTable();
        setAgentComboBox();
    }

    private void defineWindow() {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.readButton.setVisible(false);
    }

    private void setAgentComboBox() {
        if (isSeller) {
            agentName = controller.getSellerNames();
        } else {
            agentName = controller.getBuyerNames();
        }
        initComboBox();
    }

    public void initComboBox() {
        boolean verif;

        jComboBox_SelectAgent.removeAllItems();
        jComboBox_SelectAgent.addItem("Select Agent");

        if (agentList.isEmpty()) {
            for (int i = 0; i < agentName.length; i++) {
                jComboBox_SelectAgent.addItem(agentName[i]);
            }
        } else {
            for (int i = 0; i < agentName.length; i++) {
                verif = false;
                for (String name : agentList) {
                    if (name.equalsIgnoreCase(agentName[i])) {
                        verif = true;
                        break;
                    }
                }
                if (!verif) {
                    jComboBox_SelectAgent.addItem(agentName[i]);
                }
            }
        }
        if (jComboBox_SelectAgent.getItemCount() == 0) {
            jComboBox_SelectAgent.addItem("");
        }
        jComboBox_SelectAgent.setSelectedIndex(0);
    }

    private void setPanel(boolean _isSeller) {
        String panelName;
        if (_isSeller) {
            this.setTitle("Generators Offers");
            panelName = "Generators Data";
            jLabel_selectAgent.setText("Generator:");
        } else {
            this.setTitle("Retailers Offers");
            panelName = "Retailers Data";
            jLabel_selectAgent.setText("Retailer:");
        }
        TitledBorder title;
        title = BorderFactory.createTitledBorder(panelName);
        title.setTitleJustification(TitledBorder.CENTER);
        title.setTitlePosition(TitledBorder.TOP);
        jPanel3.setBorder(title);
    }

    private void startTable() {
        defineTable(agentTable, agent_offers);
    }

    public void defineTable(JTable table, ArrayList<AgentData> list) {

        sortList(list);
        model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int Column) {
                switch (Column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Integer.class;
                    case 2:
                        return Boolean.class;
                    case 3:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
        model.addColumn("Name");
        model.addColumn("ID");
        model.addColumn("Price");
        model.addColumn("Power");

        for (int i = 0; i < list.size(); i++) {
            model.addRow(new Object[0]);
            model.setValueAt(list.get(i).getAgent(), i, 0);
            model.setValueAt(list.get(i).getId(), i, 1);
            model.setValueAt(list.get(i).getStatus(), i, 2);
            model.setValueAt(list.get(i).getStatus(), i, 3);
        }

        configTable(model, table);
    }

    private void sortList(ArrayList<AgentData> _list) {
        for (int i = 0; i < _list.size() - 1; i++) {
            for (int j = i; j < _list.size() - i - 1; j++) {
                if (_list.get(j).getId() > _list.get(j + 1).getId()) {
                    AgentData temp = _list.get(j + 1);
                    _list.set(j + 1, _list.get(j));
                    _list.set(j, temp);
                }
            }
        }
    }

    private void configTable(TableModel _tableSupplierModel, JTable _table) {
        _table.setAutoscrolls(true);
        _table.setShowGrid(true);
        _table.setEnabled(true);

        for (int i = 0; i < _tableSupplierModel.getColumnCount(); i++) {
            if (i == 0) {
                _table.getColumnModel().getColumn(i).setMinWidth(100);
                _table.getColumnModel().getColumn(i).setPreferredWidth(100);
            } else {
                _table.getColumnModel().getColumn(i).setMinWidth(10);
                _table.getColumnModel().getColumn(i).setPreferredWidth(10);
            }
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        _table.setDefaultRenderer(Object.class, render);
    }

    public void setList(String _name, int _ID, ArrayList<Float> _price, ArrayList<Float> _power, boolean _isSeller) {
        int id;
        id = verifID(_ID, agent_offers);
        agent_offers.add(new AgentData(_name, id, roundValue(_price, 2), roundValue(_power, 2)));
        defineTable(agentTable, agent_offers);
        startTable();
        read_usedAgents();
        initComboBox();
    }

    private void create_uploadArray(ArrayList<Float> _price, ArrayList<Float> _power) {
        double[] price = new double[_price.size()];
        double[] power = new double[_power.size()];

        for (int i = 0; i < _price.size(); i++) {
            price[i] = _price.get(i);
            power[i] = _power.get(i);
        }

        if (isSeller) {
            uploadOffers_powerfile.add(power);
            uploadOffers_pricefile.add(price);
        } else {
            uploadBids_powerfile.add(power);
            uploadBids_pricefile.add(price);
        }
    }

    public ArrayList<double[]> getUploadBids_pricefile() {
        return uploadBids_pricefile;
    }

    public ArrayList<double[]> getUploadBids_powerfile() {
        return uploadBids_powerfile;
    }

    public ArrayList<double[]> getUploadOffers_pricefile() {
        return uploadOffers_pricefile;
    }

    public ArrayList<double[]> getUploadOffers_powerfile() {
        return uploadOffers_powerfile;
    }
    
    public ArrayList<String> getUploadOffers_namefile() {
        return uploadOffers_namefile;
    }
       
    public ArrayList<String> getUploadBids_namefile() {
        return uploadBids_namefile;
    }

    private ArrayList<Float> roundValue(ArrayList<Float> _values, int roundPrecision) {
        ArrayList<Float> finalValue = new ArrayList<>();
        int auxValue;
        for (Float value1 : _values) {
            auxValue = (int) (value1 * Math.pow(10, roundPrecision));
            finalValue.add((float) (auxValue / Math.pow(10, roundPrecision)));
        }
        return finalValue;
    }

    private int verifID(int _id, ArrayList<AgentData> _list) {
        int id = _id;
        if (id == 0) {
            for (int i = 1; i < _list.size() + 2; i++) {
                id = 0;
                for (AgentData agent : _list) {
                    if (agent.getId() == i) {
                        id = -1;
                        break;
                    }
                }
                if (id == 0) {
                    id = i;
                    break;
                }
            }
        }
        return id;
    }

    private void deleteAgent(JTable table, ArrayList<AgentData> _list) {
        model = (DefaultTableModel) table.getModel();
        try {
            if (table.getSelectedRow() != -1) {
                int[] row = table.getSelectedRows();
                for (int i = 0; i < row.length; i++) {
                    for (AgentData agent : _list) {
                        if (agent.getId() == (Integer) model.getValueAt(row[i], 1)) {
                            _list.remove(agent);
                            break;
                        }
                    }
                }
            } else {
                if (_list.size() > 0) {
                    _list.remove(_list.size() - 1);
                }
            }
            defineTable(table, _list);
            sortList(_list);
            read_usedAgents();
            initComboBox();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Table is Empty",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setComboBox_Index() {
        jComboBox_SelectAgent.setSelectedIndex(0);
    }

    private void updateAgentInfo(JTable table, ArrayList<AgentData> _list) {
        int agentID = 0;
        model = (DefaultTableModel) table.getModel();
        ArrayList<double[]> price = new ArrayList<double[]>(24);
        ArrayList<double[]> power = new ArrayList<double[]>(24);
        double[] Price = new double[24];
        double[] Power = new double[24];
        try {
            if (table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                for (AgentData agent : _list) {
                    if (agent.getId() == (Integer) model.getValueAt(row, 1)) {
                        agentID = agent.getId();
//                        for (int i = 0; i < 3; i++) {
                            
                        for (int j = 0; j < price.get(0).length; j++) {
                            Price[j] = agent.getPeriodPrice(j);
                            Power[j] = agent.getPeriodPower(j);
                        }
                        price.add(Price);
                        power.add(Power);
                        }
                        _list.remove(agent);
                        break;
//                    }
                }
                for (int i = 0; i < agentList.size(); i++) {
                    if (agentList.get(i).equalsIgnoreCase(model.getValueAt(row, 0).toString())) {
                        agentList.remove(i);
                    }
                }
                openDynamicWindow = new SMP_DynamicWindow(this, isSeller, startHour, endHour, price, power, model.getValueAt(row, 0).toString(), agentID, model.getValueAt(row, 0).toString());
                openDynamicWindow.createWindow();
                openDynamicWindow.updateValues(price, power);

                sortList(_list);
            }
        } catch (Exception ex) {
            System.out.println("InputData.java / updateAgentInfo/ " + ex);
        }
    }

    public String uploadFile(String inputfile, String outputfile) {
        String warning = "";
        if (inputfile != null && !inputfile.isEmpty()) {
            AgentsFromExcel agts = new AgentsFromExcel();
            if (agts.readFile(inputfile)) {
                ArrayList<Float> bPrices;
                ArrayList<Float> bPowers;
                uploadBids_price.removeAll(uploadBids_price);
                uploadBids_power.removeAll(uploadBids_power);

                if (isSeller) {
                    String[] anames = agts.getAgentsNames("Sellers");
                    for (int i = 1; i <= anames.length; i++) {
                        bPrices = agts.getAgentPrices("Sellers", anames[i - 1]);
                        bPowers = agts.getAgentPowers("Sellers", anames[i - 1]);
                        
                        if (agts.getCellValue("Sellers", (2*i), 26)!= ""){
                        uploadOffers_namefile.add(agts.getCellValue("Sellers", (2*i), 26));
                        }else{
                            uploadOffers_namefile.add(anames[i - 1]);
                        }
                        create_uploadArray(bPrices, bPowers);
                    }
//                    for (int j=0;j<5;j++){
//                         System.out.println("\nLinha "+j+": ");
//                        for (int i=0;i<30;i++){
//                            System.out.println("Coluna "+i+": "+ agts.getCellValue("Sellers", j, i));
//                        }
//                    }
                } else {
                    String[] anames = agts.getAgentsNames("Buyers");
                    for (int i = 1; i <= anames.length; i++) {
                        bPrices = agts.getAgentPrices("Buyers", anames[i - 1]);
                        bPowers = agts.getAgentPowers("Buyers", anames[i - 1]);

//                        uploadBids_namefile.add("Bid "+i);
                         if (agts.getCellValue("Buyers", (2*i), 26)!= ""){
                        uploadBids_namefile.add(agts.getCellValue("Buyers", (2*i), 26));
                        }else{
                            uploadBids_namefile.add(anames[i - 1]);
                        }
                        create_uploadArray(bPrices, bPowers);
                    }
                }

            } else {
                warning += "Error opening the input file!\nCheck if the file is in the correct location.";
            }
        } else {
            warning += "Invalid filename!\nCheck if the file is in the correct location.";
        }

        //
        if (outputfile != null && !outputfile.isEmpty()) {
            this.outputfilename = outputfile;
        }
        return warning;
    }

    public void readFile(String inputfile, String outputfile) {
        if (inputfile != null && !inputfile.isEmpty()) {
            // Get Agents From Excel file
            AgentsFromExcel agts = new AgentsFromExcel();
            if (agts.readFile(inputfile)) {
                agent_offers.removeAll(agent_offers);

                ArrayList<Float> bPrices;
                ArrayList<Float> bPowers;
               

                if (isSeller) {
                    String[] anames = agts.getAgentsNames("Sellers");
                    for (int i = 1; i <= anames.length; i++) {
                        bPrices = agts.getAgentPrices("Sellers", anames[i - 1]);
                        bPowers = agts.getAgentPowers("Sellers", anames[i - 1]);
                        uploadOffers_namefile.add(agts.getCellValue("Sellers", (2*i)+2, 27));

                        create_uploadArray(bPrices, bPowers);
                        setList("Seller" + i, 0, bPrices, bPowers, true);
                    }
                } else {
                    String[] anames = agts.getAgentsNames("Buyers");
                    for (int i = 1; i <= anames.length; i++) {
                        bPrices = agts.getAgentPrices("Buyers", anames[i - 1]);
                        bPowers = agts.getAgentPowers("Buyers", anames[i - 1]);
                        uploadBids_namefile.add("Bid "+i);

                        create_uploadArray(bPrices, bPowers);
                        setList("Buyer" + i, 0, bPrices, bPowers, false);
                    }
                }

                defineTable(agentTable, agent_offers);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Error opening the input file!\nCheck if the file is in the correct location.",
                        "Market Simulator",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Invalid filename!\nCheck if the file is in the correct location.",
                    "Market Simulator",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        //
        if (outputfile != null && !outputfile.isEmpty()) {
            this.outputfilename = outputfile;
        }
    }

    private void read_usedAgents() {
        DefaultTableModel agent_model = (DefaultTableModel) agentTable.getModel();
        agentList.removeAll(agentList);

        for (int i = 0; i < agent_model.getRowCount(); i++) {
            agentList.add(agentTable.getValueAt(i, 0).toString());
        }
    }

    public ArrayList<String> getAgentList() {
        return agentList;
    }

    public void setCaseStudydata(String Case) {
        
        
                isSeller=true;    
                try {
//              System.out.println("files\\"+jComboBox_SelectAgent.getSelectedItem()+"\\input.xlsx"); 
//            JOptionPane.showMessageDialog(null, "Loading Data. Select when finished", "Invalid Selection", JOptionPane.INFORMATION_MESSAGE);
            
            uploadFile("files\\"+Case+"\\input.xlsx", "output-sym.csv");
        } catch (Exception ex) {
            System.out.println("InputData.java / InputData function -> " + ex);
            readButton.setEnabled(false);
    }
//                ArrayList<ArrayList<Float>> addPrice = new ArrayList<ArrayList<Float>>();
//                ArrayList<ArrayList<Float>> addPower = new ArrayList<ArrayList<Float>>();
                ArrayList<Float> addPrices = new ArrayList<Float>();
                ArrayList<Float> addPowers = new ArrayList<Float>();
                double[] Price = new double[uploadOffers_pricefile.get(0).length];
                double[] Power = new double[uploadOffers_powerfile.get(0).length];
                agent_offers.removeAll(agent_offers);
                int ID=  controller.getSellers().size()+controller.getBuyers().size()+1;
                for(int i=0; i<uploadOffers_namefile.size();i++){
                    Price=uploadOffers_pricefile.get(i);
                    Power=uploadOffers_powerfile.get(i);
                    addPrices.removeAll(addPrices);
                    addPowers.removeAll(addPowers);
                    for(int j=0; j<uploadOffers_pricefile.get(0).length;j++){
                    addPrices.add(Float.parseFloat(String.valueOf(Price[j]).replace(",", ".")));
                    addPowers.add(Float.parseFloat(String.valueOf(Power[j]).replace(",", ".")));
                    }
                agent_offers.add(new AgentData(uploadOffers_namefile.get(i), ID, roundValue(addPrices, 2), roundValue(addPowers, 2)));
                ID++;
                }
                controller.addSellers(agent_offers);
                                isSeller=false;    
                try {
//              System.out.println("files\\"+jComboBox_SelectAgent.getSelectedItem()+"\\input.xlsx"); 
//            JOptionPane.showMessageDialog(null, "Loading Data. Select when finished", "Invalid Selection", JOptionPane.INFORMATION_MESSAGE);
            
            uploadFile("files\\"+Case+"\\input.xlsx", "output-sym.csv");
        } catch (Exception ex) {
            System.out.println("InputData.java / InputData function -> " + ex);
            readButton.setEnabled(false);
    }
                agent_offers.removeAll(agent_offers);
                          for(int i=0; i<uploadBids_namefile.size();i++){
                    Price=uploadBids_pricefile.get(i);
                    Power=uploadBids_powerfile.get(i);
                    addPrices.removeAll(addPrices);
                    addPowers.removeAll(addPowers);
                    for(int j=0; j<uploadBids_pricefile.get(0).length;j++){
                    addPrices.add(Float.parseFloat(String.valueOf(Price[j]).replace(",", ".")));
                    addPowers.add(Float.parseFloat(String.valueOf(Power[j]).replace(",", ".")));
                    }
                agent_offers.add(new AgentData(uploadBids_namefile.get(i), ID, roundValue(addPrices, 2), roundValue(addPowers, 2)));
                ID++;
                }
//                          controller.addBuyers(agent_offers);
    } 
    
    
    public void openRiskAttitude() {
        
        
//        new ProducerScheduling(jComboBox_SelectAgent.getSelectedItem().toString()).setVisible(true);
       
//        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
//        msg.setOntology("market_ontology");
//        msg.setProtocol("hello_protocol");
//        msg.setContent("spot");
//        msg.addReceiver(new AID(jComboBox_SelectAgent.getSelectedItem().toString(), AID.ISLOCALNAME));       
//        market.send(msg); 
        
//       File f = new File("files\\"+jComboBox_SelectAgent.getSelectedItem()+"\\input.xlsx");
//                while(!f.exists() && !f.isDirectory()) { 
//            try {
//             Thread.currentThread().sleep(10000);                 //1000 milliseconds is one second.
//            } catch(InterruptedException e) {
//             Thread.currentThread().interrupt();
//            }
            
//            f = new File("files\\"+jComboBox_SelectAgent.getSelectedItem()+"\\input.xlsx");
//                }
//        while(0==ProducerScheduling.a){
//            
//        }
        try {
//              System.out.println("files\\"+jComboBox_SelectAgent.getSelectedItem()+"\\input.xlsx"); 
//            JOptionPane.showMessageDialog(null, "Loading Data. Select when finished", "Invalid Selection", JOptionPane.INFORMATION_MESSAGE);
            
            uploadFile("files\\"+jComboBox_SelectAgent.getSelectedItem()+"\\input.xlsx", "output-sym.csv");
        } catch (Exception ex) {
            System.out.println("InputData.java / InputData function -> " + ex);
            readButton.setEnabled(false);
        }
        openDynamicWindow = new SMP_DynamicWindow(this, isSeller, startHour, endHour, jComboBox_SelectAgent.getSelectedItem().toString());
        openDynamicWindow.createWindow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        agentTable = new javax.swing.JTable();
        delButton_Seller = new javax.swing.JButton();
        updtButton_Seller = new javax.swing.JButton();
        jLabel_selectAgent = new javax.swing.JLabel();
        jComboBox_SelectAgent = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        readButton = new javax.swing.JButton();
        submitButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seller Information", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP)));

        agentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        agentTable.setName("seller_Table"); // NOI18N
        jScrollPane3.setViewportView(agentTable);

        delButton_Seller.setText("Remove");
        delButton_Seller.setName("delButton_Seller"); // NOI18N
        delButton_Seller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delButton_SellerActionPerformed(evt);
            }
        });

        updtButton_Seller.setText("Update");
        updtButton_Seller.setName("updtButton_Seller"); // NOI18N
        updtButton_Seller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updtButton_SellerActionPerformed(evt);
            }
        });

        jLabel_selectAgent.setText("jLabel1");

        jComboBox_SelectAgent.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_SelectAgent.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                jComboBox_SelectAgentPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        jComboBox_SelectAgent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_SelectAgentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel_selectAgent)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_SelectAgent, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(delButton_Seller, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(updtButton_Seller, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_selectAgent)
                    .addComponent(jComboBox_SelectAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(updtButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(delButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        searchTextField.setText("Search...");
        searchTextField.setToolTipText("");
        searchTextField.setName("search_TextField"); // NOI18N
        searchTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchTextFieldFocusLost(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.setName("search_Button"); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        clearButton.setText("Clear");
        clearButton.setName("clear_Button"); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchTextField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton)
                    .addComponent(clearButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        readButton.setText("Read File");
        readButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readButtonActionPerformed(evt);
            }
        });

        submitButton.setText("Save");
        submitButton.setName("submit_Button"); // NOI18N
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setName("cancel_Button"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(readButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(submitButton)
                    .addComponent(readButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void delButton_SellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButton_SellerActionPerformed
        deleteAgent(agentTable, agent_offers);
    }//GEN-LAST:event_delButton_SellerActionPerformed

    private void updtButton_SellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updtButton_SellerActionPerformed
        read_usedAgents();
        updateAgentInfo(agentTable, agent_offers);
    }//GEN-LAST:event_updtButton_SellerActionPerformed

    private void searchTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextFieldFocusGained
        if (searchTextField.getText().compareToIgnoreCase(SEARCH_INFO) == 0) {
            searchTextField.setText("");
        }
    }//GEN-LAST:event_searchTextFieldFocusGained

    private void searchTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextFieldFocusLost
        if (searchTextField.getText().isEmpty()) {
            searchTextField.setText(SEARCH_INFO);
        }
    }//GEN-LAST:event_searchTextFieldFocusLost

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        ArrayList<AgentData> offersAux = new ArrayList<>();
        if (!searchTextField.getText().isEmpty() && searchTextField.getText().compareToIgnoreCase(SEARCH_INFO) != 0) {
            for (AgentData agent : agent_offers) {
                if (agent.getAgent().contains(searchTextField.getText())) {
                    offersAux.add(agent);
                }
            }
            defineTable(agentTable, offersAux);
        } else {
            defineTable(agentTable, agent_offers);
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        searchTextField.setText(SEARCH_INFO);
        startTable();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void readButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readButtonActionPerformed
        // TODO add your handling code here:
        readFile("input.xlsx", "output-sym.csv");
    }//GEN-LAST:event_readButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        // TODO add your handling code here:
        try {
            if (isSeller) {
                
// ALTERALÕES < ---------------------------------------------------------------------------------                
                
                
                System.out.println("\n\n\n Vai juntar as ofertas ao agente buyer " + agentName + "\n\n\n");
                
                
// ALTERAÇÕES < ---------------------------------------------------------------------------------                
                controller.setSellers(agent_offers);
            } else {
                
// ALTERALÕES < ---------------------------------------------------------------------------------                
                
                
                System.out.println("\n\n\n Vai juntar as ofertas ao agente seller" + agentName + "\n\n\n");
                
                
// ALTERAÇÕES < ---------------------------------------------------------------------------------                
                controller.setBuyers(agent_offers);
            }
            this.dispose();
        } catch (Exception ex) {
            System.out.println("Submit Button Error: " + ex);
        }
    }//GEN-LAST:event_submitButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void jComboBox_SelectAgentPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jComboBox_SelectAgentPopupMenuWillBecomeInvisible
        // TODO add your handling code here:
        if (jComboBox_SelectAgent.getSelectedIndex() != 0) {
            read_usedAgents();
            if (isSeller) {
                RiskAttitude riskAttitude = new RiskAttitude(market,null, this, jComboBox_SelectAgent.getSelectedItem().toString(), 2);
                riskAttitude.setVisible(true);
            } else {
                openRiskAttitude();
            }
        }
    }//GEN-LAST:event_jComboBox_SelectAgentPopupMenuWillBecomeInvisible

    private void jComboBox_SelectAgentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_SelectAgentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_SelectAgentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable agentTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton delButton_Seller;
    private javax.swing.JComboBox jComboBox_SelectAgent;
    private javax.swing.JLabel jLabel_selectAgent;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton readButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JButton submitButton;
    private javax.swing.JButton updtButton_Seller;
    // End of variables declaration//GEN-END:variables
}
