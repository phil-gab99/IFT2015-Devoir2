package pedigree;

/**
 * The class {@link Main} initiates the application.
 *
 * @version 1.0.5 2021-03-28
 * @author Philippe Gabriel
 */

public class Main {
    
    /**
     * Retrieves the user passed arguments necessary for beginning the
     * simulation. If no arguments are passed then user must enter arguments
     * from popping dialog.
     *
     * @param args <ul><li>args[0] is an {@code int} that holds the number of
     * founders for the simulation</li><li>args[1] is a {@code double} that
     * holds the maximum time of the simulation</li></ul>
     */
    
    public static void main(String[] args) {
        
        if (args.length != 2) {
            
            new SimPlot("", "");
        } else {
            
            new SimPlot(args[0], args[1]);
        }
    }
}