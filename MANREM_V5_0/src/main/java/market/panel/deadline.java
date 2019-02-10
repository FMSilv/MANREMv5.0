/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package market.panel;

import buying.BuyerInputGui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Hugo
 */
public class deadline extends javax.swing.JPanel {
    

    /**
     * Creates new form risk
     */
    public deadline() {
        initComponents();
    }
    
    private void initComponents() {
        Date date_proposed = new Date();
        
            date_proposed.setTime(System.currentTimeMillis());
        
          
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);

        //Panel north
        JPanel panel_north = new JPanel();

        panel_north.setLayout(new BorderLayout());
        panel_north.setMinimumSize(new Dimension(400, 70));
        panel_north.setPreferredSize(new Dimension(400, 70));
        panel_north.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel panel_text_background = new JPanel();
        JLabel label_text = new JLabel();
        panel_text_background.setMinimumSize(new Dimension(345, 60));
        panel_text_background.setPreferredSize(new Dimension(345, 60));
        label_text.setMinimumSize(new Dimension(345, 60));
        label_text.setPreferredSize(new Dimension(345, 60));
        label_text.setHorizontalAlignment(SwingConstants.CENTER);
       
            label_text.setText("<html>Enter your deadline </html>");
        
        label_text.setFont(font_1);
        panel_text_background.add(label_text);
        panel_north.add(panel_text_background, BorderLayout.CENTER);


        try {
            BufferedImage picture = ImageIO.read(new File(icon_agenda_location));
            JPanel panel_pic_background = new JPanel();
            JLabel label_pic = new JLabel(new ImageIcon(picture));
            panel_pic_background.setMinimumSize(new Dimension(55, 55));
            panel_pic_background.setPreferredSize(new Dimension(55, 55));
            panel_pic_background.add(label_pic);
            panel_north.add(panel_pic_background, BorderLayout.EAST);
        } catch (IOException ex) {
            Logger.getLogger(BuyerInputGui.class.getName()).log(Level.SEVERE, null, ex);
        }
//        
        this.add(panel_north, BorderLayout.NORTH);
    }
      private String location = "images\\";
            private String icon_agenda_location = location + "icon1.png";
    private Font font_1 = new Font("Arial", Font.BOLD, 13);
}
