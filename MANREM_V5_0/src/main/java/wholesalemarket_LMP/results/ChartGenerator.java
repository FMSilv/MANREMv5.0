package wholesalemarket_LMP.results;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import wholesalemarket_LMP.simul.WholesaleMarket;

public class ChartGenerator {
    
    public static ChartPanel drawLineGraph_InputSupplier(String[] name, double[] dataMin, double[] dataMax, String _title, String _axisX, String _axisY) {
        DefaultCategoryDataset bardataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataMin.length; i++) {
            bardataset.setValue(dataMin[i], "Min Capacity", name[i]);
            bardataset.setValue(dataMax[i], "Máx Capacity", name[i]);
        }

        JFreeChart barchart = ChartFactory.createBarChart(
                _title, //Title  
                _axisX, // X-axis Label  
                _axisY, // Y-axis Label  
                bardataset, // Dataset  
                PlotOrientation.VERTICAL, //Plot orientation  
                true, // Show legend  
                true, // Use tooltips  
                false // Generate URLs  
        );

        CategoryPlot plot = barchart.getCategoryPlot();  // Get the Plot object for a bar graph 

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        GradientPaint color0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray);
        GradientPaint color1 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, Color.lightGray);
        renderer.setSeriesPaint(0, color0);
        renderer.setSeriesPaint(1, color1);

        return new ChartPanel(barchart);
    }

    /**
     *
     * @param _legend
     * @param _data: [Agents][{Min Power; Start Price; Máx Power; Last Price}]
     * @param _title
     * @param _axisXX
     * @param _axisYY
     * @return
     */
    public static ChartPanel drawLineGraph_MarginalCost(String[] _legend, double[][] _data, String _title, String _axisXX, String _axisYY) {
        ArrayList<XYSeries> seriesList = new ArrayList<>();
        XYSeries series;
        for (int i = 0; i < _data.length; i++) {
            series = new XYSeries(_legend[i]);
            series.add(_data[i][0], 0);
            series.add(_data[i][0], _data[i][1]); // Min Power; Start Price
            series.add(_data[i][2], _data[i][3]); // Máx Power; Last Price
            seriesList.add(series);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        for(XYSeries series1:seriesList) {
            dataset.addSeries(series1);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(_title, _axisXX, _axisYY, dataset, PlotOrientation.VERTICAL, true, true, false);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        for(int i = 0; i < seriesList.size(); i++) {
            plot.getRenderer().setSeriesStroke(i, new BasicStroke(2));
        }
        
        return new ChartPanel(chart);
    }

    public static ChartPanel drawLineGraph_MarginalCostVsRealPrice(String _legend, double[] _data, String _title, String _axisXX, String _axisYY) {
        XYSeries series = new XYSeries(_legend);
        if (_data[0] < _data[2]) { // IT's a producer [Start Price lower than Last Price]
            series.add(_data[0], 0);
        }
        series.add(_data[0], _data[1]); // Min Power; Start Price
        series.add(_data[2], _data[3]); // Máx Power; Last Price

        XYSeries series1 = new XYSeries("Price/Power Commitment");
        series1.add(_data[4], _data[5]); // Price and Power Commitment per Hour

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(series1);

        JFreeChart chart = ChartFactory.createXYLineChart(_title, _axisXX, _axisYY, dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        /*ValueAxis yAxis = plot.getRangeAxis();
         yAxis.setRange(min - 5, max + 5);*/

        plot.getRenderer().setSeriesStroke(0, new BasicStroke(1));
        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2));

        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        plot.getRenderer().setSeriesPaint(1, Color.RED);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);

        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);

        return new ChartPanel(chart);
    }

    public static ChartPanel drawBarGraph_Branch(String[] name, double[] data, String _title, String _axisX, String _axisY) {
        DefaultCategoryDataset bardataset = new DefaultCategoryDataset();
        for (int i = 0; i < data.length; i++) {
            bardataset.setValue(data[i], "Branch Máx Capacity", name[i]);
        }

        JFreeChart barchart = ChartFactory.createBarChart(
                _title, //Title  
                _axisX, // X-axis Label  
                _axisY, // Y-axis Label  
                bardataset, // Dataset  
                PlotOrientation.VERTICAL, //Plot orientation  
                false, // Show legend  
                true, // Use tooltips  
                false // Generate URLs  
        );
        CategoryPlot plot = barchart.getCategoryPlot();  // Get the Plot object for a bar graph 
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        /*GradientPaint color1 = new GradientPaint(0.0f, 0.0f, Color.RED, 0.0f, 0.0f, Color.RED);
        renderer.setSeriesPaint(0, color2);*/
        renderer.setBarPainter(new StandardBarPainter());

        return new ChartPanel(barchart);
    }

    public static ChartPanel drawLineGraph(String[] _legend, double[][] _data, String _title, String _axisXX, String _axisYY) {
        int dataRowSize = _data.length;
        int dataColumnSize = _data[0].length;
        double min = _data[0][0];
        double max = min;
        int indexHour;
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < dataColumnSize; i++) {
            XYSeries series = new XYSeries(_legend[i]);
            for (int j = 0; j < dataRowSize; j++) {
                indexHour = j + WholesaleMarket.START_HOUR;
                if (min > _data[j][i]) {
                    min = _data[j][i];
                }
                if (max < _data[j][i]) {
                    max = _data[j][i];
                }
                series.add(indexHour, _data[j][i]);
            }
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(_title, _axisXX, _axisYY, dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        /*ValueAxis yAxis = plot.getRangeAxis();
         yAxis.setRange(min - 5, max + 5);*/

        for (int i = 0; i < plot.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesStroke(i, new BasicStroke(2));
        }

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        return new ChartPanel(chart);
    }

    public static ChartPanel drawLineGraph_SupplyVsDemand(double[][] _supplyData, double[][] _demandData, String _title, String _axisXX, String _axisYY) {
        XYSeries series = new XYSeries("Generators: Supply Function");
        for(int i = 0; i < _supplyData.length; i++) {
            series.add(_supplyData[i][0], _supplyData[i][1]); // Left Power, Left Price
            series.add(_supplyData[i][2], _supplyData[i][3]); // Right Power, Right Price
        }
        series.add(_supplyData[_supplyData.length-1][2], _supplyData[_supplyData.length-1][3]+50);
        
        XYSeries series1 = new XYSeries("Retailers: Demand Function");
        series1.add(_demandData[0][0], _demandData[0][1]+50);
        for(int i = 0; i < _demandData.length; i++) {
            series1.add(_demandData[i][0], _demandData[i][1]); // Left Power, Left Price
            series1.add(_demandData[i][2], _demandData[i][3]); // Right Power, Right Price
        }
        series1.add(_demandData[_demandData.length-1][2], 0);
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(series1);

        JFreeChart chart = ChartFactory.createXYLineChart(_title, _axisXX, _axisYY, dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();

        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        plot.getRenderer().setSeriesPaint(1, Color.RED);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);

        return new ChartPanel(chart);
    }
}
