package personalassistant;

import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import wholesalemarket_SMP.AgentData;

public class NewAgent_Name extends javax.swing.JFrame {

    private final PersonalAssistant market;
    private DefaultTableModel model;
    private ArrayList<String> agentList;
    private String agentInfo, agentType;

    public NewAgent_Name(PersonalAssistant _market, int _agentType) {
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
            for (int i = 0; i < market.getBuyerNames().size(); i++) {
                agentList.add(market.getBuyerNames().get(i).getLocalName());
            }
            agentType = "Consumer";
            agentInfo = "buying.Buyer";
        }
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

    private void setWindow() {
        //this.setAlwaysOnTop(true);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Create Agent");
        jTextField_Name.setEnabled(true);
        jTextField_Name.setVisible(true);
        jTextField_Name.setText("New Agent");

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

    private String searchAgent(JTable table, String _name) {
        String warning = "";
        model = (DefaultTableModel) table.getModel();
        try {
            if (table.getRowCount() > 0) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).toString().equalsIgnoreCase(_name)) {
                        warning += "Agent already registered. Indicate a different name!\n";
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Table is Empty",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        return warning;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton_Cancel = new javax.swing.JButton();
        jButton_Next = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        agentTable = new javax.swing.JTable();
        delButton_Seller = new javax.swing.JButton();
        updtButton_Seller = new javax.swing.JButton();
        jTextField_Name = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton_Back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_Cancel.setText("Cancel");
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });

        jButton_Next.setText("Create");
        jButton_Next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_NextActionPerformed(evt);
            }
        });

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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updtButton_Seller, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(delButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(updtButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(delButton_Seller, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextField_Name.setText("jTextField1");

        jLabel1.setText("Agent Name :");

        jButton_Back.setText("Back");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_Name, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_Next, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(134, 134, 134))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton_CancelActionPerformed

    private void jButton_NextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NextActionPerformed
        if (jTextField_Name.getText().equalsIgnoreCase("New Agent") || jTextField_Name.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid Agent Name!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            String warning = searchAgent(agentTable, jTextField_Name.getText().trim().replace(" ", "_"));
            if (warning.isEmpty()) {
                market.createAgent((String) jTextField_Name.getText().trim().replace(" ", "_"), agentInfo);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, warning,
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton_NextActionPerformed

    private void delButton_SellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButton_SellerActionPerformed

    }//GEN-LAST:event_delButton_SellerActionPerformed

    private void updtButton_SellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updtButton_SellerActionPerformed

    }//GEN-LAST:event_updtButton_SellerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable agentTable;
    private javax.swing.JButton delButton_Seller;
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Next;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField_Name;
    private javax.swing.JButton updtButton_Seller;
    // End of variables declaration//GEN-END:variables
}
