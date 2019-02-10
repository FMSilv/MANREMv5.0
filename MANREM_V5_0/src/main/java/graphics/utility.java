/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics;

import java.awt.Color;

/**
 *
 * @author Hugo
 */

    
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
//import org.jfree.ui.Spacer;

/**
 * A simple demonstration application showing how to create a line chart using data from an
 * {@link XYDataset}.
 *
 */
public class utility extends JFrame  {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public utility(final String title, ArrayList<Double> xy, int series, String x, String y, String[] Name, int choice, int agent, double score) {

        super(title);

        final XYDataset dataset = createDataset(xy, series,Name, choice, agent, score);
        final JFreeChart chart = createChart(dataset, title,x,y, choice );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//        System.out.println("\n"+cpt.getX()+" "+cpt.getY());
//        chartPanel.setLocation(cpt.getX(), cpt.getY());
//        chartPanel.setAlignmentX(cpt.getAlignmentX());
//        chartPanel.setAlignmentY(cpt.getAlignmentY());
//        chartPanel.setLocation((int)cpt.getAlignmentX(), (int)cpt.getAlignmentY());
//        setLocation(0, 0);
//        setLocation(cpt.getX(), cpt.getY());
//        setLocation(cpt.getX(), cpt.getY());
        setContentPane(chartPanel);
//        setLocation(cpt.getX(), cpt.getY());
//        
//        setBounds(cpt.getX(), cpt.getY(), 500, 270);
        
    }
    
    /**
     * Creates a sample dataset.
     * 
     * @return a sample dataset.
     */
    private XYDataset createDataset(ArrayList<Double> xy, int series, String[] Name, int c, int a, double score) {
        
        if (c==0){   
        final XYSeries series2 = new XYSeries(Name[0]);
        final XYSeries series3 = new XYSeries(Name[1]);
        final XYSeries series4 = new XYSeries(Name[2]);
        
//        series1.add(1, xy.get(0));
        if (a==0){
        for(int i=0; i<xy.size(); i=i+2){
            series2.add(i+1, xy.get(i));
            series4.add(i+1, xy.get(i));
            if(i+1<xy.size()){
            series3.add(i+2, xy.get(i+1));
            }
            
               }

        }if (a==1){
            for(int i=0; i<xy.size(); i=i+2){
            series3.add(i+1, xy.get(i));
                if(i+1<xy.size()){
                series2.add(i+2, xy.get(i+1));
                series4.add(i+2, xy.get(i+1));
                }
            }
        }
        if(score!=0){
            series4.add(xy.size()+1, score);
        }
        
        final XYSeriesCollection dataset = new XYSeriesCollection();
        if(xy.size()>1||a==0){
            dataset.addSeries(series2);
        }
        dataset.addSeries(series3);
        if(score!=0){
//        dataset.addSeries(series4);
        }
        
        return dataset;
        
        }else if (c==1){
            final TimeSeries series2 = new TimeSeries(Name[0]);
            final TimeSeries series3 = new TimeSeries(Name[1]);
            final TimeSeries series4 = new TimeSeries(Name[2]);
//        series1.add(1.0, 1.0);
        Day day = new Day();
        Hour hour = new Hour();
        if (a==0){
            for(int i=0; i<xy.size(); i=i+2){
                  
            day=(Day)day.next();
            series2.add(day, xy.get(i));
            series4.add(day, xy.get(i));
            if(i==0){
                      day=(Day)day.next();
                      }
            if(i+1<xy.size()){
                day=(Day)day.next();
            series3.add(day, xy.get(i+1));
            }
            
            }
        }if (a==1){
                          for(int i=0; i<xy.size(); i=i+2){
                  day=(Day)day.next();
                   
            series3.add(day, xy.get(i));
            if(i==0){
                      day=(Day)day.next();
                       }
            if(i+1<xy.size()){
                day=(Day)day.next();
            series2.add(day, xy.get(i+1));
            series4.add(day, xy.get(i+1));
            }
            
               }
        }
                        if(score!=0){
                            day=(Day)day.next();
            series4.add(day, score);
        }

//        final XYSeriesCollection dataset = new XYSeriesCollection();
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
//        XYDataItem a = new XYDataItem(4,5);
//        series1.add(a);
        if(xy.size()>1||a==0){
        dataset.addSeries(series2);
        }
        if(xy.size()>1||a==1){
        dataset.addSeries(series3);
        }
        if(score!=0){
//        dataset.addSeries(series4);
        }
        return dataset;
        }      
        
        return null;
    }
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the data for the chart.
     * 
     * @return a chart.
     */
    private JFreeChart createChart(final XYDataset dataset,String title,String x,String y,int c) {
        
        // create the chart...
         
        
        JFreeChart chart = ChartFactory.createXYLineChart(
            title,      // chart title
            x,                      // x axis label
            y,                      // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );
         if (c==1){
        chart = ChartFactory.createTimeSeriesChart(title, x, y, dataset, true, true, false);
        }
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
//        legend.setDisplaySeriesShapes(true);
        
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
    //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        
        
//        final XYSplineRenderer renderer = new XYSplineRenderer();
//        renderer.setSeriesLinesVisible(0, false);
//        renderer.setSeriesShapesVisible(1, false);
//        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.
                
        return chart;
        
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    
    
    
    public static void main(final String[] args) {

//        final utility demo = new utility("Line Chart Demo 6");
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);

    }

}
    
    

