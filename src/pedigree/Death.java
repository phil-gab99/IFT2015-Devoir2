package pedigree;

/**
 * The class {@link Death} defines an {@link Event} which indicates the death
 * of a new {@link Sim}.
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class Death extends Event {
    
    /**
     * Defines a {@link Death} type {@link Event} for the given subject
     * {@link Sim} and the time at which their death will take place.
     * 
     * @param subject {@link Sim} destined to die
     * @param time Time at which death will take place
     */
    
    public Death(Sim subject, double time) {
        
        super(subject, time);
    }
}