package pedigree;

/**
 * The class {@link AssignFather} defines an {@link Event} which selects the
 * male {@link Sim} with which the female {@link Sim} will mate for the current
 * baby {@link Sim}.
 * 
 * @author Philippe Gabriel
 * @version 1.0 2021-mm-dd
 */

public class AssignFather extends Event {
    
    /**
     * The constructor method {@link #AssignFather(Sim, double)} defines the
     * {@link Event} with the given subject mother {@link Sim} and the time at
     * which the {@link Event} will take place.
     * 
     * @param subject Mother {@link Sim} that gave birth
     * @param time Time at which mating will occur
     */
    
    public AssignFather(Sim subject, double time) {
        
        super(subject, time);
    }
}