package pedigree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.Plot;

public class SimPlot extends JFrame {
    
    private static final int FRAME_WIDTH = 800;  //Default frame width
    private static final int FRAME_HEIGHT = 450; //Default frame height
    
    //Acquiring screen details and dimensions
    private static final Toolkit screen = Toolkit.getDefaultToolkit();
    private static final Dimension d = screen.getScreenSize();
    
    public SimPlot() {
        
        float[][] data = {{0,2,4,10,15},{1000,300,24,999,1240}};
        
        ValueAxis xAxis = new NumberAxis("Iliya x x x");
        ValueAxis yAxis = new NumberAxis("Iliya y y");
        
        ChartFrame plot = new ChartFrame("My graph", new JFreeChart("Data", new FastScatterPlot(data, xAxis, yAxis)));
        
        plot.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        
        centerComponent(plot, 0);
        
        plot.setVisible(true);
    }
    
    /**
     * The method {@link #centerComponent(Component, int)} centers a given
     * component with a given offset with respect to the screen dimensions.
     *
     * @param c Component to be centered
     * @param offset Integer indicating offset from center
     */

    public void centerComponent(Component c, int offset) {

        c.setLocation((d.width - c.getWidth()) / 2 - offset,
        (d.height - c.getHeight()) / 2 - offset);
    }
}