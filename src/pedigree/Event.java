package pedigree;

/**
 * The abstract class {@link Event} defines the common attributes of any
 * {@link Event} type object.
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public abstract class Event implements Comparable<Event> {
    
    private Sim subject; // Sim of interest for which Event is defined
    private double time; // Time at which given event will take place
    
    /**
     * The constructor method {@link #Event(Sim, double)} assigns the involved
     * {@link Sim} and the time at which the {@link Event} will take place.
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
     * The getter method {@link #getSubject()} retrieves the {@link Sim}
     * involved with this {@link Event}.
     *
     * @return the subject
     */
    
    public Sim getSubject() {
    	return subject;
    }
    
    /**
     * The getter method {@link #getTime()} retrieves the time at which the
     * {@link Event} will take place.
     *
     * @return Time at which {@link Event} will occur
     */
    
    public double getTime() {
        
    	return time;
    }
    
    /** 
     * The method {@link #compareTo(Event)} defines {@link Event} ordering by
     * each {@link Event}'s time of occurrence.
     * 
     * @param e Other {@link Event} with which to compare to
     * @return The value 0 if the current instance and the other {@link Event}
     * occur at the same time<li>A value less than 0 if the current
     * instance occurs before the other {@link Event}</li> <li>A value greater
     * than 0 if the current instance occurs after the other {@link Event}</li>
     * @see java.lang.Comparable
     * @see java.lang.Double
     */
    @Override
    public int compareTo(Event e) {
        
        return Double.compare(time, e.time);
    }
    
    public String toString() {
        
        return getClass().getSimpleName() + ": " + subject + " - " + time;
    }
}