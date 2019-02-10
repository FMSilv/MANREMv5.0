/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalassistant;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;

import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author Administrator
 */
public class Bilateral_NegotiationOption extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	private final PersonalAssistant market;
    private final int DayHours = 24;
    private int inter_protocol, tariff, hoursPeriod, hourDeadline, minutesDeadline, dayDeadline, monthDeadline, yearDeadline;
	JXDatePicker jXDatePicker2 = new JXDatePicker();
    
    
    /**
     * Creates new form Bilateral_NegotiationOption
     */
    public Bilateral_NegotiationOption(PersonalAssistant _market) {
        market = _market;
        initComponents();
        setWindow();
        setForms();

        this.getRootPane().setDefaultButton(jButton_Save);
        jCheckBox_AlternatingOffers.setSelected(true);

        jCheckBox_AlternatingOffers.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_ContractNet.setSelected(false);
                }
            }
        });

        jCheckBox_ContractNet.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    jCheckBox_AlternatingOffers.setSelected(false);
                }
            }
        });
    }

    private void setWindow() {
        //this.setAlwaysOnTop(true);
        this.setTitle("Bilateral Negotiation");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    private void setForms() {
		jXDatePicker2.setFormats("yyyy-MM-dd");
        jXDatePicker2.setDate(new Date());

        JSpinner.DateEditor time = new JSpinner.DateEditor(jSpinner_Time, "HH:mm");
        DateFormatter formatter = (DateFormatter) time.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        jSpinner_Time.setEditor(time);
        jSpinner_Time.setValue(new Date());
        JComponent editor = jSpinner_Time.getEditor();

        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        }

        jComboBox_Tariff.removeAllItems();
        for (int i = 2; i <= DayHours; i++) {
            jComboBox_Tariff.addItem(i);
        }

        jComboBox_Hours.removeAllItems();
        for (int i = 1; i < DayHours; i++) {
            jComboBox_Hours.addItem(i);
        }

        DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        dlcr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        jComboBox_Tariff.setRenderer(dlcr);
        jComboBox_Hours.setRenderer(dlcr);
    }

    private void getTradingProcess() {
        if(jCheckBox_AlternatingOffers.isSelected()) {
            inter_protocol = 1;
        } else {
            inter_protocol = 2;
        }
    }
    
    private void getTimePeriods() {
        tariff = Integer.parseInt(jComboBox_Tariff.getSelectedItem().toString());
        hoursPeriod = Integer.parseInt(jComboBox_Hours.getSelectedItem().toString());
    }
    
    private String verifNegotiationDeadline() {
        String warning = "";
        int hourUser, hourSystem, minutesUser, minutesSystem;
        int dateUser, dateSystem;

        try {
            Calendar cal = Calendar.getInstance();
            cal.getTime();

            hourUser = Integer.parseInt(new SimpleDateFormat("HH").format(jSpinner_Time.getValue()));
            minutesUser = Integer.parseInt(new SimpleDateFormat("mm").format(jSpinner_Time.getValue()));
            hourSystem = Integer.parseInt(new SimpleDateFormat("HH").format(cal.getTime()));
            minutesSystem = Integer.parseInt(new SimpleDateFormat("mm").format(cal.getTime()));
            dateUser = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(jXDatePicker2.getDate()));
            dateSystem = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));

            if (jXDatePicker2.getDate().after(new Date())) {
                hourDeadline = hourUser;
                minutesDeadline = minutesUser;
                dayDeadline = Integer.parseInt(new SimpleDateFormat("dd").format(jXDatePicker2.getDate()));
                monthDeadline = Integer.parseInt(new SimpleDateFormat("MM").format(jXDatePicker2.getDate()));
                yearDeadline = Integer.parseInt(new SimpleDateFormat("yyyy").format(jXDatePicker2.getDate()));
            } else if (dateUser == dateSystem) {
                if ((hourUser > hourSystem) || ((hourUser == hourSystem) && (minutesUser > minutesSystem))) {
                    hourDeadline = hourUser;
                    minutesDeadline = minutesUser;
                    dayDeadline = Integer.parseInt(new SimpleDateFormat("dd").format(jXDatePicker2.getDate()));
                    monthDeadline = Integer.parseInt(new SimpleDateFormat("MM").format(jXDatePicker2.getDate()));
                    yearDeadline = Integer.parseInt(new SimpleDateFormat("yyyy").format(jXDatePicker2.getDate()));
                } else {
                    warning += "Time must be greater than current time! \n";
                }
            } else {
                warning += "Date must be greater than current date! \n";
            }
        } catch (NumberFormatException ex) {
            warning += "There is an error on the input data! \n";
        }
        return warning;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox_AlternatingOffers = new javax.swing.JCheckBox();
        jCheckBox_ContractNet = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox_Tariff = new javax.swing.JComboBox();
        jComboBox_Hours = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jSpinner_Time = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton_Cancel = new javax.swing.JButton();
        jButton_Save = new javax.swing.JButton();
        jButton_Back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Trading Process", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("Interaction Protocol :");

        jCheckBox_AlternatingOffers.setText("Alternating Offers");

        jCheckBox_ContractNet.setText("Contract Net");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jLabel1)
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox_ContractNet)
                    .addComponent(jCheckBox_AlternatingOffers))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCheckBox_AlternatingOffers))
                .addGap(18, 18, 18)
                .addComponent(jCheckBox_ContractNet)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION), "Time Periods", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel2.setText("Tariff Rates :");

        jLabel3.setText("Hours :");

        jComboBox_Tariff.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox_Hours.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox_Hours, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_Tariff, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox_Tariff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox_Hours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Negotiation Deadline", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jSpinner_Time.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.HOUR));

        jLabel4.setText("Day :");

        jLabel5.setText("Hour :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
                .addComponent(jSpinner_Time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner_Time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSpinner_Time.getAccessibleContext().setAccessibleName("");

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
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_Save, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_Cancel)
                    .addComponent(jButton_Save)
                    .addComponent(jButton_Back))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton_CancelActionPerformed

    private void jButton_BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_BackActionPerformed
        // TODO add your handling code here:
        market.setContractType();
    }//GEN-LAST:event_jButton_BackActionPerformed

    private void jButton_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_SaveActionPerformed
        String warning = verifNegotiationDeadline();
        if (warning.isEmpty()) {
            getTradingProcess();
            getTimePeriods();
            market.setBilateral_tradingProcess(inter_protocol);
            market.setBilateral_tariff(tariff);
            market.setBilateral_hoursPeriod(hoursPeriod);
            market.setBilateral_DeadlineDate(yearDeadline, monthDeadline, dayDeadline, hourDeadline, minutesDeadline);
            if(inter_protocol == 2) {
                market.setMenus_availability(true);
                market.setBilateral_contractType(2);
            } else {
                market.setMenus_availability(false);
                market.setBilateral_contractType(1);
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, warning,
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton_SaveActionPerformed

    private void jXDatePicker2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jXDatePicker2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Save;
    private javax.swing.JCheckBox jCheckBox_AlternatingOffers;
    private javax.swing.JCheckBox jCheckBox_ContractNet;
    private javax.swing.JComboBox jComboBox_Hours;
    private javax.swing.JComboBox jComboBox_Tariff;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner jSpinner_Time;
    // End of variables declaration//GEN-END:variables
}
