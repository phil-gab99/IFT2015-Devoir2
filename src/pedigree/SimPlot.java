package pedigree;

import java.util.Map;
import java.util.Arrays;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

import org.jfree.chart.plot.XYPlot;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.DefaultXYDataset;

import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import org.jfree.chart.axis.LogAxis;

public class SimPlot {
    
    private static final int FRAME_WIDTH = 800;  //Default frame width
    private static final int FRAME_HEIGHT = 450; //Default frame height
    
    //Acquiring screen details and dimensions
    private static final Toolkit screen = Toolkit.getDefaultToolkit();
    private static final Dimension d = screen.getScreenSize();
    
    public SimPlot(String founders, String maxTime) {
        
        JTextField numFounders = new JTextField(founders);
        JTextField simulationTime = new JTextField(maxTime);
        
        Object[] message = {
            "Founders: ", numFounders,
            "Time of simulation: ", simulationTime
        };
        
        boolean validArguments = false;
        
        do {
            
            int option = JOptionPane.showConfirmDialog(null, message,
            "Arguments", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                
                try {
                    
                    if (Integer.parseInt(numFounders.getText()) >= 0
                    && Double.parseDouble(simulationTime.getText()) >= 0) {
                        
                        validArguments = true;
                    } else {
                        
                        JOptionPane.showMessageDialog(null,
                        "For negative input", "Wrong argument type",
                        JOptionPane.ERROR_MESSAGE);
                    }
                } catch(NumberFormatException e) {
                    
                    JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Wrong argument type", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                
                System.exit(0);
            }
        } while (!validArguments);
        
        Simulation.simulate(Integer.parseInt(numFounders.getText()),
        Double.parseDouble(simulationTime.getText()));
        
        DefaultXYDataset SimData = new DefaultXYDataset();
        createDataset(SimData, "Population Size", Simulation.getPopGrowth());
        createDataset(SimData, "Foremothers", Simulation.getCoalescenceF());
        createDataset(SimData, "Forefathers", Simulation.getCoalescenceM());
        
        XYPlot plot = new XYPlot(SimData, new NumberAxis("Time (1000 years)"), new LogAxis("Number of Sims"), new XYLineAndShapeRenderer());
        
        JFreeChart chart = new JFreeChart("Common Ancestors", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        
        ChartFrame frame = new ChartFrame("Common Ancestors", chart);
        
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        centerComponent(frame, 0);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private void createDataset(DefaultXYDataset set, String label,  
        Map<Double, Integer> mapData) {
    
        int i = 0;
        double[][] data = new double[2][mapData.size()];
        System.out.println("_____________________________________________");
    
        for (Map.Entry<Double, Integer> entry : mapData.entrySet()) {
    
            data[0][i] = entry.getKey() / 1000.0;
            data[1][i] = entry.getValue();
    
            System.out.println(data[0][i] + "\t" + data[1][i]);
            i++;
        }
    
        set.addSeries(label, data);
    }
    
    /**
     * The method {@link #centerComponent(Component, int)} centers a given
     * component with a given offset with respect to the screen dimensions.
     *
     * @param c Component to be centered
     * @param offset Integer indicating offset from center
     */

    private void centerComponent(Component c, int offset) {

        c.setLocation((d.width - c.getWidth()) / 2 - offset,
        (d.height - c.getHeight()) / 2 - offset);
    }
}