package producing;

import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import scheduling.EnterGENCO;

public class ProducerTechnology extends javax.swing.JFrame {

    public DefaultTableModel model;
    private Producer generatorData;
    private ProducerInputGui auxData;
    private ArrayList<String> personalInfo;
    private ArrayList<Producer_TechnologyData> technology_struct;
    private final String[] technologies = {"Select Technology", "Combined cycle", "Steam turbine", "Renewable energy"};
    private final String[] fuelType_1 = {"Select Fuel Type", "Natural gas"};
    private final String[] fuelType_2 = {"Select Fuel Type", "Coal"};
    private final String[] fuelType_3 = {"Select Fuel Type", "Water", "Solar", "Wind"};
    private final String[] fuelType = {"Select Fuel Type", "Coal", "Natural gas", "Water", "Solar", "Wind"};

    public ProducerTechnology(Producer _generatorData, ProducerInputGui _auxData) {
        technology_struct = new ArrayList<>();
        generatorData = _generatorData;
        auxData = _auxData;
        //personalInfo = _personalInfo;
        initComponents();
        setWindow(generatorData.getLocalName().replace("_", " "));
        initComboBox();
        initTextFields();
        defineTable(jTable_Technology, technology_struct);
        jButton_Back.setEnabled(false);
    }

    public void setPersonalInfo(ArrayList<String> _personalInfo) {
        personalInfo = _personalInfo;
    }

    private void setWindow(String _name) {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle(_name);
    }

    private void initComboBox() {
        jComboBox_Technology.removeAllItems();
        jComboBox_Fuel.removeAllItems();
        for (String technology : technologies) {
            jComboBox_Technology.addItem(technology);
        }
        for (String fuel : fuelType) {
            jComboBox_Fuel.addItem(fuel);
        }
    }

    private void initTextFields() {
        jTextField_minCap.setText("");
        jTextField_MaxCap.setText("");
    }

    public void defineTable(JTable table, ArrayList<Producer_TechnologyData> list) {

        model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int Column) {
                switch (Column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Integer.class;
                    case 3:
                        return Integer.class;
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
        model.addColumn("Technology");
        model.addColumn("Fuel");
        model.addColumn("Min. Cap.");
        model.addColumn("Max. Cap.");

        for (int i = 0; i < list.size(); i++) {
            model.addRow(new Object[0]);
            model.setValueAt(list.get(i).getTechnology(), i, 0);
            model.setValueAt(list.get(i).getFuelType(), i, 1);
            model.setValueAt(list.get(i).getMinCap(), i, 2);
            model.setValueAt(list.get(i).getMaxCap(), i, 3);
        }

        configTable(model, table);
    }

    private void configTable(TableModel _tableGeneratorModel, JTable _table) {
        _table.setAutoscrolls(true);
        _table.setShowGrid(true);
        _table.setEnabled(true);

        for (int i = 0; i < _tableGeneratorModel.getColumnCount(); i++) {
            if (i < 2) {
                _table.getColumnModel().getColumn(i).setMinWidth(100);
                _table.getColumnModel().getColumn(i).setPreferredWidth(100);
            } else {
                _table.getColumnModel().getColumn(i).setMinWidth(50);
                _table.getColumnModel().getColumn(i).setPreferredWidth(50);
            }
        }
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(JLabel.CENTER);
        _table.setDefaultRenderer(Object.class, render);
    }

    private String verifParam() {
        String warning = "";
        if (jComboBox_Technology.getSelectedIndex() != 0) {
            if (jComboBox_Fuel.getSelectedIndex() != 0) {
                try {
                    double minCap = Double.parseDouble(jTextField_minCap.getText());
                    double maxCap = Double.parseDouble(jTextField_MaxCap.getText());
                    if ((minCap >= 0 && minCap < maxCap)) {
                        technology_struct.add(new Producer_TechnologyData(jComboBox_Technology.getSelectedItem().toString(), jComboBox_Fuel.getSelectedItem().toString(), minCap, maxCap));
                    } else {
                        warning += "Warning! Min. Capacity must be greater than zero and smaller than Max. Capacity!\n";
                    }
                } catch (Exception ex) {
                    warning += "Error! Min. & Max. Capacity must be numbers!\n";
                }
            } else {
                warning += "Warning! You must select a fuel type!\n";
            }
        } else {
            warning += "Warning! You must select a generation technology!\n";
        }
        return warning;
    }

    private void delete_update_PowerPlant(JTable table, ArrayList<Producer_TechnologyData> _list, boolean _isUpdate) {
        model = (DefaultTableModel) table.getModel();
        try {
            if (_isUpdate) {
                if (table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    for (int i = 0; i < technologies.length; i++) {
                        if (technologies[i].equalsIgnoreCase(model.getValueAt(selectedRow, 0).toString().trim())) {
                            jComboBox_Technology.setSelectedIndex(i);
                        }
                    }
                    for (int i = 0; i < fuelType.length; i++) {
                        if (fuelType[i].equalsIgnoreCase(model.getValueAt(selectedRow, 1).toString().trim())) {
                            jComboBox_Fuel.setSelectedIndex(i);
                        }
                    }
                    jTextField_minCap.setText(model.getValueAt(selectedRow, 2).toString());
                    jTextField_MaxCap.setText(model.getValueAt(selectedRow, 3).toString());
                    try {
                        _list.remove(_list.get(selectedRow));
                    } catch (Exception ex) {
                    }
                }
            } else {
                if (table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    try {
                        _list.remove(_list.get(selectedRow));
                    } catch (Exception ex) {
                    }
                } else {
                    if (_list.size() > 0) {
                        _list.remove(_list.size() - 1);
                    }
                }
                initComboBox();
                initTextFields();
            }
            defineTable(table, _list);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Table is Empty",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox_Technology = new javax.swing.JComboBox();
        jComboBox_Fuel = new javax.swing.JComboBox();
        jTextField_minCap = new javax.swing.JTextField();
        jTextField_MaxCap = new javax.swing.JTextField();
        jButton_Add = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Technology = new javax.swing.JTable();
        jButton_Update = new javax.swing.JButton();
        jButton_Remove = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();
        jButton_Save = new javax.swing.JButton();
        jButton_Back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Specifications", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("Production Technology :");

        jLabel2.setText("Fuel Type :");

        jLabel3.setText("Min. Production Capacity :");

        jLabel4.setText("Max. Production Capacity :");

        jComboBox_Technology.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox_Technology.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                tecnology_Selected(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        jComboBox_Technology.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_TechnologyActionPerformed(evt);
            }
        });

        jComboBox_Fuel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField_minCap.setText("jTextField1");

        jTextField_MaxCap.setText("jTextField2");

        jButton_Add.setText("Add");
        jButton_Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_AddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField_minCap, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox_Fuel, javax.swing.GroupLayout.Alignment.LEADING, 0, 200, Short.MAX_VALUE)
                    .addComponent(jComboBox_Technology, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField_MaxCap))
                .addGap(18, 18, 18)
                .addComponent(jButton_Add, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox_Technology, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox_Fuel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_minCap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_MaxCap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_Add, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Power Plants", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jTable_Technology.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable_Technology);

        jButton_Update.setText("Update");
        jButton_Update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_UpdateActionPerformed(evt);
            }
        });

        jButton_Remove.setText("Remove");
        jButton_Remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_RemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_Update, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_Remove, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jButton_Update, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_Remove, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 37, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jButton_Cancel.setText("Cancel");
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });

        jButton_Save.setText("Save");
        jButton_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_SaveActionPerformed(evt);
            }
        });

        jButton_Back.setText("Back");
        jButton_Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_BackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 136, Short.MAX_VALUE)
                        .addComponent(jButton_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_Save, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(136, 136, 136)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Cancel)
                    .addComponent(jButton_Save)
                    .addComponent(jButton_Back))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AddActionPerformed
        String warning = verifParam();
        if (warning.isEmpty()) {
            defineTable(jTable_Technology, technology_struct);
            initComboBox();
            initTextFields();
        } else {
            JOptionPane.showMessageDialog(null, warning,
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton_AddActionPerformed

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton_CancelActionPerformed

    private void jButton_RemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_RemoveActionPerformed
        delete_update_PowerPlant(jTable_Technology, technology_struct, false);
    }//GEN-LAST:event_jButton_RemoveActionPerformed

    private void jButton_UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_UpdateActionPerformed
        delete_update_PowerPlant(jTable_Technology, technology_struct, true);
    }//GEN-LAST:event_jButton_UpdateActionPerformed

    private void jButton_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SaveActionPerformed
        if (!technology_struct.isEmpty()) {
            //generatorData.setPersonalInfo(personalInfo);
            generatorData.setList_PowerPlants(technology_struct);

            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "You must add one or more Power Plants!\n",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton_SaveActionPerformed

    private void jButton_BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_BackActionPerformed
        // TODO add your handling code here:
        auxData.setPowerPlant= new EnterGENCO(auxData.getName());
                    
        this.dispose();
    }//GEN-LAST:event_jButton_BackActionPerformed

    private void jComboBox_TechnologyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_TechnologyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_TechnologyActionPerformed

    private void tecnology_Selected(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_tecnology_Selected
        jComboBox_Fuel.removeAllItems();
        if (jComboBox_Technology.getSelectedIndex() == 1) {
            for (String fuel : fuelType_1) {
                jComboBox_Fuel.addItem(fuel);
            }
        } else if (jComboBox_Technology.getSelectedIndex() == 2) {
            for (String fuel : fuelType_2) {
                jComboBox_Fuel.addItem(fuel);
            }
        } else if (jComboBox_Technology.getSelectedIndex() == 3) {
            for (String fuel : fuelType_3) {
                jComboBox_Fuel.addItem(fuel);
            }
        }
    }//GEN-LAST:event_tecnology_Selected

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Add;
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Remove;
    private javax.swing.JButton jButton_Save;
    private javax.swing.JButton jButton_Update;
    private javax.swing.JComboBox jComboBox_Fuel;
    private javax.swing.JComboBox jComboBox_Technology;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Technology;
    private javax.swing.JTextField jTextField_MaxCap;
    private javax.swing.JTextField jTextField_minCap;
    // End of variables declaration//GEN-END:variables
}
