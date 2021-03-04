package pedigree;

/**
 * The class {@link Event}
 * 
 * @author Philippe Gabriel
 * @version 1.0 2021-mm-dd
 */

public abstract class Event implements Comparable {
    
    private Sim subject;
    private double time;
    
    public Event(Sim subject, double time) {
        
        assert time >= 0.0;
        
        this.subject = subject;
        this.time = time;
    }
    
    public int compareTo(Event e) {
        
        return Double.compare(time, e.time);
    }
}