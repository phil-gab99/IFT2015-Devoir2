package pedigree;

import java.util.Random;

public class Simulation {
    
    /**
     * The method {@link #simulate(int, double)}
     * 
     * @param n Number of founding {@link Sim}s
     * @param tMax Time length of simulation
     */
    
    public static void simulate(int n, double tMax) {
        
        AgeModel model = new AgeModel();
        MinPQ eventQ = new MinPQ();
        
        for (int i = 0; i < n; i++) {
            
            eventQ.insert(new Birth(new Sim(), 0.0));
        }
        
        while (!eventQ.isEmpty()) {
            
            Event e = eventQ.delMin();
            
            if (e.getTime() > tMax) {
                
                break;
            }
            
            // If a death time has not been set or the Sim is still alive
            if (e.getSubject().isAlive(e.getTime())) {
                
                if (e instanceof Birth) {
                    
                    // Setting the appropriate random death time
                    e.getSubject().setDeathTime(e.getTime()
                    + model.randomAge(new Random()));
                    
                    // Add Death Event for this Sim
                    eventQ.insert(new Death(e.getSubject(),
                    e.getSubject().getDeathTime()));
                    
                    // If the Sim is a woman, add a Reproduction Event
                    if (e.getSubject().getSex().equals(Sim.Sex.F)) {
                        
                        // Add reproduction event
                    }
                    
                    // Add the sim to structure for population
                } else if (e instanceof Death) {
                    
                    // Remove the sim from structure
                } else if (e instanceof Reproduction) {
                    
                    // If Sim is dead, do nothing
                    if (e.getSubject().isAlive(e.getTime())) {
                        
                        // If the female Sim is of mating age
                        if (e.getSubject().isMatingAge(e.getTime())) {
                            
                            // Add AssignFather Event for the newborn child
                            eventQ.insert(new AssignFather(e.getSubject(), e.getTime()));
                            // Sim 
                        }
                    }
                }
            }
        }
    }
}