/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marketoperator;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

/**
 *
 * @author Hugo
 */
public class MarketOperatorGUI extends JFrame {
    
    public JTextArea text_log = new JTextArea("Log");
    private JScrollPane scroll_log = new JScrollPane(text_log);
    private JPanel panel_center = new JPanel();
    private JLabel label_image = new JLabel(new ImageIcon("images\\system_image_2.png"));
    private JSplitPane split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT, null, scroll_log);
    public MarketOperator mOperator;
    
    public MarketOperatorGUI(MarketOperator mOperator) {
        this.mOperator = mOperator;
        this.setTitle("Market Operator");
//        this.setPreferredSize(new Dimension(900, 600));
        this.setPreferredSize(new Dimension(400,200));
        this.setLocation(520, 300);
//        this.setLocation((int) (screen_size.getWidth()) , (int) (screen_size.getHeight()));

        
        text_log.setEditable(false);
        text_log.setText(" Market Operator initiated");
        text_log.setAlignmentX(0);
        text_log.setAlignmentY(0);
        text_log.setMinimumSize(new Dimension(400, 200));
//        label_image.setMinimumSize(new Dimension(0, 0));
//         BufferedImage image= resizeImage(new ImageIcon("images\\system_image_2.png").getImage(),50 , 50 );
//        label_image=new JLabel(new ImageIcon(image));
        split_pane_log_image = new JSplitPane(JSplitPane.VERTICAL_SPLIT, null, scroll_log);
                panel_center.setPreferredSize(new Dimension(400, 200));
        panel_center.setBackground(Color.WHITE);
        getContentPane().add(panel_center, BorderLayout.NORTH);
        panel_center.setLayout(new FlowLayout());
        panel_center.add(text_log, BorderLayout.NORTH);
//        panel_center.add(split_pane_log_image, BorderLayout.CENTER);
        
        this.pack();
        this.setVisible(false);
        this.setMinimumSize(new Dimension(400, 200));
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(false);
    
}

    public void updateLog1(String s) {
        this.text_log.append("\n " + s);
    }
    public static BufferedImage resizeImage(final Image image, int width, int height) {
final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
final Graphics2D graphics2D = bufferedImage.createGraphics();
graphics2D.setComposite(AlphaComposite.Src);
graphics2D.drawImage(image, 0, 0, width, height, null);
graphics2D.dispose();
return bufferedImage;
}
}