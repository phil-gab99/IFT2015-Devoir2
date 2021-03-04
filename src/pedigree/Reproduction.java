package pedigree;

/**
 * The class {@link Reproduction} defines an {@link Event} which indicates the
 * act of reproduction between two partner {@link Sim}s.
 * 
 * @author Philippe Gabriel
 * @version 1.0 2021-mm-dd
 */

public class Reproduction extends Event {
    
    /**
     * The constructor method {@link #Reproduction(Sim, double)} defines the
     * {@link Event} of reproduction involving the given subject mother
     * {@link Sim} and the time at which reproduction will take place.
     * 
     * @param subject Mother {@link Sim} to give birth
     * @param time Time at which reproduction will take place
     */
    
    public Reproduction(Sim subject, double time) {
        
        super(subject, time);
    }
}