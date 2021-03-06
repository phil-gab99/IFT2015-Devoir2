package pedigree;

import java.util.List;
import java.util.Random;

/**
 * The class {@link Simulation} runs a simulation of {@link Event}s and traces
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class Simulation {
    
    private static AgeModel model;
    private static MinPQ<Event> eventQ;
    private static MinPQ<Sim> population;
    private static double poissonPointProcess;
    private static Random rnd;
    
    public static AgeModel getModel() {
        
        return model;
    }
    
    public static void setModelCustomParams(double deathRate,
    double accidentRate, double loyaltyFactor, int avgLifetimeOffspring,
    double ageScale) {
        
        model = new AgeModel(deathRate, accidentRate, loyaltyFactor,
        avgLifetimeOffspring, ageScale);
    }
    
    /**
     * The method {@link #simulate(int, double)}
     * 
     * @param n Number of founding {@link Sim}s
     * @param tMax Time length of simulation
     */
    
    public static void simulate(int n, double tMax) {
        
        if (model == null) {
            
            model = new AgeModel();
        }
        
        poissonPointProcess = model.getPoissonPointProcess(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        
        eventQ = new MinPQ<Event>();
        population = new MinPQ<Sim>();
        rnd = new Random();
        
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
        e.getSubject().setDeathTime(e.getTime() + model.randomAge(rnd));
        
        // Add Death Event for this Sim
        eventQ.insert(new Death(e.getSubject(), e.getSubject().getDeathTime()));
        
        // If the Sim is a woman, add a Reproduction Event
        if (e.getSubject().getSex().equals(Sim.Sex.F)) {
            
            eventQ.insert(new Reproduction(e.getSubject(), e.getTime() +
            AgeModel.randomWaitingTime(rnd, poissonPointProcess)));
        }
        
        population.insert(e.getSubject());
    }
    
    private static void deathSim(Event e) {
        
        assert e.getSubject() == population.delMin();
    }
    
    private static void reproductionSim(Event e) {
        
        if (e.getSubject().isAlive(e.getTime())) {
            
            // If the female Sim is of mating age
            if (e.getSubject().isMatingAge(e.getTime())) {
                
                // Choose father for the newborn child
                chooseFatherSim(e);
                
                // Birth of their baby
                eventQ.insert(new Birth(new Sim(e.getSubject(), e.getSubject().getMate(), e.getTime()), e.getTime()));
            }
            
            eventQ.insert(new Reproduction(e.getSubject(), e.getTime() +
            AgeModel.randomWaitingTime(rnd, poissonPointProcess)));
        }
        
        // If Sim is dead, do nothing
    }
    
    private static void chooseFatherSim(Event e) {
        
        List<Sim> pop = population.toList();
        
        Sim mate;
        Sim mother = e.getSubject();
        
        if (mother.isInARelationship(e.getTime())) {
            
            if (Math.random() < 1 - model.getLoyaltyFactor()) {
                
                mate = getRandomMate(e, pop);
                
                mother.setMate(mate);
                
                if (mate != null) {
                    
                    mate.setMate(mother);
                }
            }
        } else {
            
            do {
                
                mate = getRandomMate(e, pop);
                
                if (mate != null) {
                    
                    if (!mate.isInARelationship(e.getTime())) {
                        
                        mother.setMate(mate);
                        mate.setMate(mother);
                    } else {
                        
                        if (Math.random() < 1 - model.getLoyaltyFactor()) {
                            
                            mother.setMate(mate);
                            mate.setMate(mother);
                        }
                    }
                }
            } while (!mother.isInARelationship(e.getTime()) && !pop.isEmpty());
        }
    }
    
    private static Sim getRandomMate(Event e, List<Sim> pop) {
        
        Sim mate;
        
        while (mate != null && !pop.isEmpty()) {
            
            int rndIndex = rnd.nextInt(pop.length);
            Sim potentialMate = pop.remove(rndIndex);
            
            mate = potentialMate != null
                && !(potentialMate.getSex().equals(e.getSubject().getSex()))
                && potentialMate.isMatingAge(e.getTime())
                && potentialMate.isAlive(e.getTime()) ? potentialMate : null;
        }
        
        return mate;
    }
}