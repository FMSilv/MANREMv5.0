package wholesalemarket_LMP;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import wholesalemarket_LMP.simul.GridData;

public class UpdateBranchParameters extends javax.swing.JFrame {

    private BranchesInputParameters mainData;
    private ArrayList<GridData> gridList;
    private String name;
    private double startBus, endBus, maxCap, losses;
    private int index;

    public UpdateBranchParameters(BranchesInputParameters _mainData, int _index, String _name, double _startBus,
            double _endBus, double _maxCap, double _losses, ArrayList<GridData> _gridList) {
        initComponents();
        setWindow();
        mainData = _mainData;
        index = _index;
        name = _name;
        startBus = _startBus;
        endBus = _endBus;
        maxCap = _maxCap;
        losses = _losses;
        gridList = _gridList;
        setTextFields();
    }

    private void setWindow() {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle("Update branch data");
    }

    private void setTextFields() {
        jComboBoxBranchName.removeAllItems();
        jComboBoxBranchName.addItem(name);
        jComboBoxBranchName.setEnabled(false);

        jTextFieldFromBus.setText("" + (int) startBus);
        jTextFieldToBus.setText("" + (int) endBus);
        jTextFieldBranchCapact.setText("" + (int) maxCap);
        jTextFieldBranchLosses.setText("" + (double) losses);
    }

    public String verifyBranchTable() {
        String warning = "";
        try {
            double table_StartBus = Double.parseDouble(jTextFieldFromBus.getText());
            double table_EndBus = Double.parseDouble(jTextFieldToBus.getText());

            if (!gridList.isEmpty()) {
                for (GridData BRANCH1 : gridList) {
                    if (name.equalsIgnoreCase(BRANCH1.getName())) {
                    } else {
                        if (table_StartBus == BRANCH1.getStartBus()) {
                            if (table_EndBus == BRANCH1.getEndBus()) {
                                warning += "The branch between start bus" + jTextFieldFromBus.getText()
                                        + " and end bus " + jTextFieldToBus.getText() + " already exists!\n";
                            }
                        }
                        if (table_StartBus == BRANCH1.getEndBus()) {
                            if (table_EndBus == BRANCH1.getStartBus()) {
                                warning += "The reverse branch between start bus" + jTextFieldFromBus.getText()
                                        + " and end bus " + jTextFieldToBus.getText() + " already exists!\n";
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            warning += "Error! Input Data Incorrect!\n";
        }
        return warning;
    }

    public String verifyBranchTextField() {
        String warning = "";

        try {
            int Aux_startBus = Integer.parseInt(jTextFieldFromBus.getText());
            int Aux_endBus = Integer.parseInt(jTextFieldToBus.getText());
            int Aux_capac = Integer.parseInt(jTextFieldBranchCapact.getText());
            double Aux_losses = Double.parseDouble(jTextFieldBranchLosses.getText());

            if (Aux_startBus > mainData.getTotalBus() || Aux_startBus <= 0) {
                warning += "Start BUS has to be a number between 1 and " + Integer.toString(mainData.getTotalBus()) + "!\n";
            }
            if (Aux_endBus > mainData.getTotalBus() || Aux_endBus <= 0) {
                warning += "End BUS has to be a number between 1 and " + Integer.toString(mainData.getTotalBus()) + "!\n";
            }
            if (Aux_startBus == Aux_endBus) {
                warning += "Start and end buses should be different!\n";
            }
            if (Aux_capac <= 0) {
                warning += "Máx Capacity has to be bigger than 0 MW!\n";
            }
            if (Aux_losses <= 0) {
                warning += "Losses has to be bigger than 0.0 Ohms!\n";
            }

        } catch (Exception ex) {
            System.out.println(ex);
            warning += "All input data must be integer values although the losses field must be a double value!\n";
        }
        return warning;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldToBus = new javax.swing.JTextField();
        jTextFieldFromBus = new javax.swing.JTextField();
        jTextFieldBranchLosses = new javax.swing.JTextField();
        jTextFieldBranchCapact = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxBranchName = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton_UpdateInfo = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Update Parameters", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        jLabel6.setText("MW");

        jLabel7.setText("Ohm");

        jLabel5.setText("Branch Losses:");

        jLabel2.setText("From BUS:");

        jLabel1.setText("Name:");

        jComboBoxBranchName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Branch Max. Capacity:");

        jLabel3.setText("To BUS:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldBranchLosses, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldToBus, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldFromBus, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addComponent(jComboBoxBranchName, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldBranchCapact, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxBranchName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldFromBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldToBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldBranchCapact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldBranchLosses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jButton_UpdateInfo.setText("Update");
        jButton_UpdateInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_UpdateInfoActionPerformed(evt);
            }
        });

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton_UpdateInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_UpdateInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_UpdateInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_UpdateInfoActionPerformed
        String warningTextField = verifyBranchTextField();
        String warningList = verifyBranchTable();

        if (!warningTextField.isEmpty()) {
            JOptionPane.showMessageDialog(this, warningTextField,
                    "Verify Input Data", JOptionPane.ERROR_MESSAGE);
        } else {
            if (!warningList.isEmpty()) {
                JOptionPane.showMessageDialog(this, warningList,
                        "Verify Input Data", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    startBus = Double.parseDouble(jTextFieldFromBus.getText());
                    endBus = Double.parseDouble(jTextFieldToBus.getText());
                    maxCap = Double.parseDouble(jTextFieldBranchCapact.getText());
                    losses = Double.parseDouble(jTextFieldBranchLosses.getText());

                    mainData.updateGRID_BRANCH_LIST(index, startBus, endBus, maxCap, losses);
                } catch (Exception ex) {

                }
                this.dispose();
            }
        }
    }//GEN-LAST:event_jButton_UpdateInfoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_UpdateInfo;
    private javax.swing.JComboBox jComboBoxBranchName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldBranchCapact;
    private javax.swing.JTextField jTextFieldBranchLosses;
    private javax.swing.JTextField jTextFieldFromBus;
    private javax.swing.JTextField jTextFieldToBus;
    // End of variables declaration//GEN-END:variables
}
