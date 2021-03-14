package pedigree;

/**
 * The class {@link Main} initiates the application.
 *
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class Main {
    
    /**
     * Retrieves the user passed arguments necessary for beginning the
     * simulation.
     *
     * @param args <ul><li>args[0] is an {@code int} that holds the number of
     * founders for the simulation</li><li>args[1] is a {@code double} that
     * holds the maximum time of the simulation</li></ul>
     * @throws IllegalArgumentException if not exactly 2 arguments are passed
     */
    
    public static void main(String[] args) {
        
        if (args.length != 2) {
            
            throw new IllegalArgumentException(
            "Insufficient arguments (expected 2, got " + args.length + ")");
        }
        
        new SimPlot(args[0], args[1]);
    }
}