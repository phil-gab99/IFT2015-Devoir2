package pedigree;

import java.util.Random;

/**
 * The class {@link Simulation} runs a simulation of {@link Event}s and traces
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class Simulation {
    
    private static AgeModel model;
    private static MinPQ eventQ;
    
    /**
     * The method {@link #simulate(int, double)}
     * 
     * @param n Number of founding {@link Sim}s
     * @param tMax Time length of simulation
     */
    
    public static void simulate(int n, double tMax) {
        
        model = new AgeModel();
        eventQ = new MinPQ();
        
        generateFounders(n);
        
        while (!eventQ.isEmpty()) {
            
            Event e = eventQ.delMin();
            
            if (e.getTime() > tMax) {
                
                break;
            }
            
            // If a death time has not been set or the Sim is still alive
            if (e.getSubject().isAlive(e.getTime())) {
                
                if (e instanceof Birth) {
                    
                    birthSim(e);
                } else if (e instanceof Death) {
                    
                    deathSim(e);
                } else if (e instanceof Reproduction) {
                    
                    reproductionSim(e);
                }
            }
        }
    }
    
    private static void generateFounders(int n) {
        
        for (int i = 0; i < n; i++) {
            
            eventQ.insert(new Birth(new Sim(), 0.0));
        }
    }
    
    private static void birthSim(Event e) {
        
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
    }
    
    private static void deathSim(Event e) {
        
        // Remove the sim from structure
    }
    
    private static void reproductionSim(Event e) {
        
        // If Sim is dead, do nothing
        if (e.getSubject().isAlive(e.getTime())) {
            
            // If the female Sim is of mating age
            if (e.getSubject().isMatingAge(e.getTime())) {
                
                // Choose father for the newborn child
                chooseFatherSim(e);
                
                // Birth of their baby
                eventQ.insert(new Birth(new Sim(e.getSubject(), e.getSubject().getMate(), e.getTime()), e.getTime()));
            }
            
            // Add reproduction event
        }
    }
    
    private static void chooseFatherSim(Event e) {
        
        if (e.getSubject().isInARelationship(e.getTime())) {
            
            // f probability stay with her mate, 1 - f random valid man
        } else {
            
            // Choose another valid man 10 %
        }
    }
}