package pedigree;

/**
 * The class {@link Birth} defines an {@link Event} which indicates the birth
 * of a new {@link Sim}.
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class Birth extends Event {
    
    /**
     * The constructor method {@link #Birth(Sim, double)} defines the
     * {@link Event} for the given subject {@link Sim} and the time at which
     * their birth will take place.
     * 
     * @param subject {@link Sim} to be born
     * @param time Time at which birth will take place
     */
    
    public Birth(Sim subject, double time) {
        
        super(subject, time);
    }
}