package personalassistant;

import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class LoadAgent_Name extends javax.swing.JFrame {

    private final PersonalAssistant market;
    private DefaultTableModel model;
    private ArrayList<String> agentList;
    private String agentInfo, agentType;
    public static boolean isLoaded = false;
    
    public LoadAgent_Name(PersonalAssistant _market, int _agentType, String[] _agentNameList) {
        market = _market;
        initComponents();
        setWindow();
        jButton_Back.setEnabled(false);
        agentList = new ArrayList<>();
        if (_agentType == 1) {
            for (int i = 0; i < market.getSellerNames().size(); i++) {
                agentList.add(market.getSellerNames().get(i).getLocalName());
            }
            agentType = "Generator";
            agentInfo = "selling.Seller";

        } else if (_agentType == 2) {
            for (int i = 0; i < market.getBuyerNames().size(); i++) {
                agentList.add(market.getBuyerNames().get(i).getLocalName());
            }
            agentType = "Retailer";
            agentInfo = "buying.Buyer";
        } else if (_agentType == 3) {
            for (int i = 0; i < market.getMediumConsumerNames().size(); i++) {
                agentList.add(market.getMediumConsumerNames().get(i).getLocalName());
            }
            agentType = "Consumer";
            agentInfo = "buying.Buyer";
        } else if (_agentType == 4) {
            for (int i = 0; i < market.getProducerNames().size(); i++) {
                agentList.add(market.getProducerNames().get(i).getLocalName());
            }
            agentType = "Producer";
            agentInfo = "producing.Producer";
        }
        initComboBox(_agentNameList);
        configWindowType();
        setInfoTable();
    }
    
    private void configWindowType() {
        TitledBorder title;
        title = BorderFactory.createTitledBorder(agentType + "s");
        title.setTitleJustification(TitledBorder.CENTER);
        title.setTitlePosition(TitledBorder.TOP);
        jPanel1.setBorder(title);
    }
    
    private void initComboBox(String[] _agentNameList) {
        jComboBox_loadName.removeAllItems();
        jComboBox_loadName.addItem("Select " + agentType + " agent");
        for(int i = 0; i < _agentNameList.length; i++) {
            jComboBox_loadName.addItem(_agentNameList[i]);
        }
    }

    private void setWindow() {
        //this.setAlwaysOnTop(true);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Load Agent");
        
        configWindowType();
    }

    private void setInfoTable() {
        defineTable(agentTable, agentList);
    }

    public void defineTable(JTable table, ArrayList<String> list) {

        model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int Column) {
                switch (Column) {
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
        model.addColumn("Type");

        for (int i = 0; i < list.size(); i++) {
            model.addRow(new Object[0]);
            model.setValueAt(list.get(i), i, 0);
            model.setValueAt(agentType, i, 1);
        }

        configTable(model, table);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        agentTable = new javax.swing.JTable();
        delButton_Seller = new javax.swing.JButton();
        updtButton_Seller = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();
        jButton_Next = new javax.swing.JButton();
        jComboBox_loadName = new javax.swing.JComboBox();
        jButton_Back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Agent Name :");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "New Agent", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(delButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updtButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(updtButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(delButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton_Cancel.setText("Cancel");
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });

        jButton_Next.setText("Load");
        jButton_Next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_NextActionPerformed(evt);
            }
        });

        jComboBox_loadName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_loadName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_loadNameActionPerformed(evt);
            }
        });

        jButton_Back.setText("Back");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox_loadName, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(jButton_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_Next, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox_loadName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Cancel)
                    .addComponent(jButton_Next)
                    .addComponent(jButton_Back))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void delButton_SellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButton_SellerActionPerformed

    }//GEN-LAST:event_delButton_SellerActionPerformed

    private void updtButton_SellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updtButton_SellerActionPerformed

    }//GEN-LAST:event_updtButton_SellerActionPerformed

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton_CancelActionPerformed

    private void jButton_NextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NextActionPerformed
        String agentName = jComboBox_loadName.getSelectedItem().toString();
        if (agentName.equalsIgnoreCase("New Agent") || agentName.isEmpty() || jComboBox_loadName.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Invalid Agent Name!",
                "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            market.createAgent((String) agentName.trim().replace(" ", "_"), agentInfo);
            isLoaded = true;
            this.dispose();
        }
    }//GEN-LAST:event_jButton_NextActionPerformed

    private void jComboBox_loadNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_loadNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_loadNameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable agentTable;
    private javax.swing.JButton delButton_Seller;
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Next;
    private javax.swing.JComboBox jComboBox_loadName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton updtButton_Seller;
    // End of variables declaration//GEN-END:variables
}
