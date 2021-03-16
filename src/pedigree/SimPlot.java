package pedigree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.text.DecimalFormat;

import java.util.Map;

import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * The class {@link SimPlot} manages the graphical interface elements for
 * retrieving the user arguments to begin the simulation and displaying the
 * data in the form of charts and in standard out.
 *
 * @version 1.9.12 2021-03-28
 * @author Philippe Gabriel
 */

public class SimPlot {
    
    private static final int FRAME_WIDTH = 1440; //Default frame width
    private static final int FRAME_HEIGHT = 900; //Default frame height
    
    //Acquiring screen details and dimensions
    private static final Toolkit screen = Toolkit.getDefaultToolkit();
    private static final Dimension d = screen.getScreenSize();
    
    //Input fields
    private JTextField numFounders;
    private JTextField simulationTime;
    
    /**
     * Initiates the simulation with the given parameters and charts the data.
     *
     * @param founders Number of founding {@link Sim}s
     * @param maxTime Time length of simulation
     */
    
    public SimPlot(String founders, String maxTime) {
        
        setSimulationParams(founders, maxTime);
        
        // Starting simulation
        Simulation.simulate(Integer.parseInt(numFounders.getText()),
        Double.parseDouble(simulationTime.getText()));
        
        // Building the different datasets
        DefaultXYDataset SimData = new DefaultXYDataset();
        createDataset(SimData, "Population Size", Simulation.getPopGrowth());
        createDataset(SimData, "Foremothers", Simulation.getCoalescenceF());
        createDataset(SimData, "Forefathers", Simulation.getCoalescenceM());
        
        XYPlot plot = new XYPlot(
            SimData,
            new NumberAxis("Time (1000 years)"),
            new LogAxis("Number of Sims"),
            new XYLineAndShapeRenderer()
        );
        
        JFreeChart chart = new JFreeChart(
            "Common Ancestors",
            JFreeChart.DEFAULT_TITLE_FONT,
            plot,
            true
        );
        
        ChartFrame frame = new ChartFrame("Common Ancestors", chart);
        
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        centerComponent(frame, 0);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /**
     * Retrieves the user-inputted values detailing the simulation procedure.
     *
     * @param founders Number of founding {@link Sim}s
     * @param maxTime Time length of simulation
     */
    
    private void setSimulationParams(String founders, String maxTime) {
        
        numFounders = new JTextField(founders);
        simulationTime = new JTextField(maxTime);
        
        Object[] message = {
            
            "Founders: ", numFounders,
            "Time of simulation: ", simulationTime
        };
        
        boolean validArguments = false;
        
        do {
            
            int option = JOptionPane.showConfirmDialog(
                null,
                message,
                "Arguments",
                JOptionPane.OK_CANCEL_OPTION
            );
            
            if (option == JOptionPane.OK_OPTION) { // User pressed Ok button
                
                try {
                    
                    if (Integer.parseInt(numFounders.getText()) >= 0
                    && Double.parseDouble(simulationTime.getText()) >= 0) {
                        
                        validArguments = true;
                    } else {
                        
                        JOptionPane.showMessageDialog(
                            null,
                            "For negative input",
                            "Wrong argument type",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch(NumberFormatException e) {
                    
                    JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Wrong argument type",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {                               // User cancelled operation
                
                System.exit(0);
            }
        } while (!validArguments);
    }
    
    /**
     * Adds series of value pairs to the given dataset and prints the data in
     * standard out.
     *
     * @param set {@link #DefaultXYDataset} which will hold the dataset of
     * interest
     * @param label Associated label with a series
     * @param mapData {@link Map} holding the series of value pairs to plot
     */
    
    private void createDataset(DefaultXYDataset set, String label,  
        Map<Double, Integer> mapData) {
    
        System.out.println(label);
    
        DecimalFormat dFormat = new DecimalFormat("0.000000");
        double[][] data = new double[2][mapData.size()];
        int i = 0;
    
        for (Map.Entry<Double, Integer> entry : mapData.entrySet()) {
    
            data[0][i] = entry.getKey() / 1000.0;
            data[1][i] = entry.getValue();
    
            System.out.println(dFormat.format(data[0][i]) + "\t" + data[1][i]);
            i++;
        }
    
        System.out.println("________________________________________________");
    
        set.addSeries(label, data);
    }
    
    /**
     * Centers a given component with a given offset with respect to the screen
     * dimensions.
     *
     * @param c Component to be centered
     * @param offset Integer indicating offset from center
     */

    private void centerComponent(Component c, int offset) {

        c.setLocation((d.width - c.getWidth()) / 2 - offset,
        (d.height - c.getHeight()) / 2 - offset);
    }
}