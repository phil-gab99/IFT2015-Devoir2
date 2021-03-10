package pedigree;

/**
 * The class {@link Main} initiates the application.
 *
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class Main {
    
    /**
     * The method {@link main(String[])} retrieves the user passed arguments
     * necessary for launching the application.
     *
     * @param args <ul><li>args[0] is an {@code int} that holds the number of
     * founders for the simulation</li><li>args[1] is a {@code double} that
     * holds the maximum time of the simulation</li></ul>
     * @throws IllegalArgumentException if less than or more than 2 arguments
     * are passed
     */
    
    public static void main(String[] args) {
        
        if (args.length != 2) {
            
            throw new IllegalArgumentException(
            "Insufficient arguments (expected 2, got " + args.length + ")");
        }
        
        new SimPlot(args[0], args[1]);
    }
}