package pedigree;

/**
 * The class {@link Death} defines an {@link Event} which indicates the death
 * of a new {@link Sim}.
 * 
 * @author Philippe Gabriel
 * @version 1.0 2021-mm-dd
 */

public class Death extends Event {
    
    /**
     * The constructor method {@link #Death(Sim, double)} defines the
     * {@link Event} for the given subject {@link Sim} and the time at which
     * their death will take place.
     * 
     * @param subject {@link Sim} destined to die
     * @param time Time at which death will take place
     */
    
    public Death(Sim subject, double time) {
        
        super(subject, time);
    }
}