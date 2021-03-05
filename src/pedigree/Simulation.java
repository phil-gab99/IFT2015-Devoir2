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
            
            if (e.getSubject().getDeathTime() > e.getTime()) {
                
                if (e instanceof Birth) {
                    
                    eventQ.insert(new Death(e.getSubject(),
                    e.getTime() + model.randomAge(new Random())));
                    
                    if (e.getSubject().getSex().equals(Sim.Sex.F)) {
                        
                        // Add reproduction event
                    }
                    
                    // Add the sim to structure for population
                } else if (e instanceof Death) {
                    
                    // Remove the sim from structure
                } else if (e instanceof Reproduction) {
                    
                    // if () {
                    // 
                    // }
                }
            }
        }
    }
}