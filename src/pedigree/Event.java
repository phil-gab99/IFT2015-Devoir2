package pedigree;

/**
 * The abstract class {@link Event} defines the common attributes of any
 * {@link Event} type object.
 * 
 * @version 1.1.5 2021-03-28
 * @author Philippe Gabriel
 */

public abstract class Event implements Comparable<Event> {
    
    private Sim subject; // Sim of interest for which Event is defined
    private double time; // Time at which given event will take place
    
    /**
     * Initializes an {@link Event} involving a subject {@link Sim} and the
     * time at which the {@link Event} will take place.
     *
     * @param subject {@link Sim} of interest
     * @param time Time at which {@link Event} will take place
     */
    
    public Event(Sim subject, double time) {
        
        assert time >= 0.0; // Time must be non-negative
        
        this.subject = subject;
        this.time = time;
    }
    
    /**
     * Retrieves the {@link Sim} involved with this {@link Event}.
     *
     * @return The subject {@link Sim} related with this {@link Event}
     */
    
    public Sim getSubject() {
        
    	return subject;
    }
    
    /**
     * Retrieves the time at which the {@link Event} will take place.
     *
     * @return Time at which {@link Event} will occur
     */
    
    public double getTime() {
        
    	return time;
    }
    
    /** 
     * Defines {@link Event} ordering by each {@link Event}'s time of
     * occurrence.
     * 
     * @param e Other {@link Event} with which to compare to
     * @return <ul><li>The value 0 if the current {@link Event} and the other
     * {@link Event} occur at the same time</li><li>A value less than 0 if the
     * current {@link Event} occurs before the other {@link Event}</li><li>A
     * value greater than 0 if the current {@link Event} occurs after the other
     * {@link Event}</li></ul>
     * @see java.lang.Comparable
     * @see java.lang.Double
     */
    @Override
    public int compareTo(Event e) {
        
        return Double.compare(time, e.time);
    }
}