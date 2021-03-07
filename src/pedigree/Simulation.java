package pedigree;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The class {@link Simulation} runs a simulation of {@link Event}s and tracks
 * the population of {@link Sim}s following the events.
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class Simulation {
    
    private static AgeModel model;
    private static MinPQ<Event> eventQ;
    private static MinPQ<Sim> population;
    private static MaxPQ<Sim> forefathers;
    private static MaxPQ<Sim> foremothers;
    private static double poissonPointProcess;
    private static Random rnd;
    
    // Maps for plotting
    private static Map<Double, Integer> popGrowth;
    private static Map<Sim, Integer> coalescenceM;
    private static Map<Sim, Integer> coalescenceF;
    
    // Anonymous inner type for comparing Sims using their birth dates
    private static Comparator<Sim> comp = new Comparator<Sim>() {
        
        public int compare(Sim s1, Sim s2) {
            
            return Double.compare(s1.getBirthTime(), s2.getBirthTime());
        }
    };
    
    /**
     * The method {@link #getModel()} retrieves the associated model with the
     * simulation.
     *
     * @return The associated model
     */
    
    public static AgeModel getModel() {
        
        return model;
    }
    
    /**
     * The method
     * {@link #setModelCustomParams(double, double, double, int, double)}
     * allows to modify the parameters of the model according to the passed
     * values
     *
     * @param deathRate The annual death rate
     * @param accidentRate The annual accident rate
     * @param loyaltyFactor The loyalty factor which determines how loyal a
     * {@link Sim} partner is to their mate
     * @param avgLifetimeOffspring The average number of children a mother
     * {@link Sim} will have in her lifetime
     * @param ageScale Maximum age with death rate 1
     */
    
    public static void setModelCustomParams(double deathRate,
    double accidentRate, double loyaltyFactor, int avgLifetimeOffspring,
    double ageScale) {
        
        model = new AgeModel(deathRate, accidentRate, loyaltyFactor,
        avgLifetimeOffspring, ageScale);
    }
    
    /**
     * The method {@link #simulate(int, double)} begins the simulation of
     * {@link Event}s stemming from the {@link Birth} of a given amount of
     * founder {@link Sim}s. The simulation ends after the given maximum time
     * has been reached.
     * 
     * @param n Number of founding {@link Sim}s
     * @param tMax Time length of simulation
     */
    
    public static void simulate(int n, double tMax) {
        
        // Initilizing model with default values if not already initialized
        if (model == null) {
            
            model = new AgeModel();
        }
        
        eventQ = new MinPQ<Event>();
        population = new MinPQ<Sim>();
        forefathers = new MaxPQ<Sim>(comp);
        foremothers = new MaxPQ<Sim>(comp);
        poissonPointProcess = model
        .getPoissonPointProcess(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        rnd = new Random();
        
        popGrowth = new HashMap<Double, Integer>();
        coalescenceM = new HashMap<Sim, Integer>();
        coalescenceF = new HashMap<Sim, Integer>();
        
        generateFounders(n);
        
        // The simulation stops if all Events are finished or time is up
        while (!eventQ.isEmpty()) {
            
            Event e = eventQ.delMin();
            
            if (e.getTime() > tMax) {
                
                break;
            }
            
            // If a death time has not been set or the Sim is still alive
            if (e.getSubject().isAlive(e.getTime())) {
                
                if (e instanceof Birth) {
                    
                    birthSim(e);
                } else if (e instanceof Reproduction) {
                    
                    reproductionSim(e);
                }
            } else {
                
                deathSim();
            }
        }
    }
    
    /**
     * The method {@link #generateFounders(int)} initiates the simulation with
     * the {@link Birth} of a given amount of founder {@link Sim}s.
     *
     * @param n Integer indicating number of founder {@link Sim}s
     */
    
    private static void generateFounders(int n) {
        
        while (n-- > 0) {
            
            eventQ.insert(new Birth(new Sim(), 0.0));
        }
    }
    
    /**
     * The method {@link #birthSim(Event)} completes the appropriate procedure
     * for the {@link Birth} of a {@link Sim}.
     *
     * @param e The {@link Birth} {@link Event} details
     */
    
    private static void birthSim(Event e) {
        
        // Setting the appropriate random death time
        e.getSubject().setDeathTime(e.getTime() + model.randomAge(rnd));
        
        // Add Death Event for this Sim
        eventQ.insert(new Death(e.getSubject(), e.getSubject().getDeathTime()));
        
        // If the Sim is a woman, add a Reproduction Event
        if (e.getSubject().getSex().equals(Sim.Sex.F)) {
            
            eventQ.insert(new Reproduction(e.getSubject(), e.getTime() +
            AgeModel.randomWaitingTime(rnd, poissonPointProcess)));
            
            foremothers.insert(e.getSubject());
        } else {
            
            forefathers.insert(e.getSubject());
        }
        
        // Adding the newly born Sim to the population
        population.insert(e.getSubject());
    }
    
    /**
     * The method {@link #deathSim()} completes the appropriate procedure for
     * the {@link Death} of a {@link Sim}.
     */
    
    private static void deathSim() {
        
        population.delMin();
    }
    
    /**
     * The method {@link #reproductionSim(Event)} completes the appropriate
     * procedure for the {@link Reproduction} relating to a female {@link Sim}.
     *
     * @param e The {@link Reproduction} {@link Event} details
     */
    
    private static void reproductionSim(Event e) {
        
        if (e.getSubject().isAlive(e.getTime())) {
            
            // If the female Sim is of mating age
            if (e.getSubject().isMatingAge(e.getTime())) {
                
                // Choose father for the newborn child
                chooseFatherSim(e);
                
                // Birth of their baby
                if (e.getSubject().isInARelationship(e.getTime())) {
                    
                    eventQ.insert(new Birth(new Sim(e.getSubject(),
                    e.getSubject().getMate(), e.getTime()), e.getTime()));
                }
            }
            
            eventQ.insert(new Reproduction(e.getSubject(), e.getTime() +
            AgeModel.randomWaitingTime(rnd, poissonPointProcess)));
        }
        
        // If Sim is dead, do nothing
    }
    
    /**
     * The method {@link #chooseFatherSim(Event)} selects a male {@link Sim}
     * with which the female {@link Sim} who is in the process of a
     * {@link Reproduction} {@link Event} will mate with.
     *
     * @param e The {@link Reproduction} {@link Event} details
     */
    
    private static void chooseFatherSim(Event e) {
        
        // Converting the priority queue to a list
        List<Sim> pop = population.toList();
        
        Sim mate;
        Sim mother = e.getSubject();
        
        // Different procedure on whether the mother has a mate or not
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
    
    /**
     * The method {@link #getRandomMate(Event, List)} selects a mating
     * {@link Sim} for the {@link Sim} associated with the given {@link Event}
     * from the given list of {@link Sim}s in the population.
     *
     * @param e The {@link Event} details
     * @param pop The {@link Sim} population
     */
    
    private static Sim getRandomMate(Event e, List<Sim> pop) {
        
        Sim mate = null;
        
        while (mate == null && !pop.isEmpty()) {
            
            int rndIndex = rnd.nextInt(pop.size());
            Sim potentialMate = pop.remove(rndIndex);
            
            mate = potentialMate != null
                && !(potentialMate.getSex().equals(e.getSubject().getSex()))
                && potentialMate.isMatingAge(e.getTime())
                && potentialMate.isAlive(e.getTime()) ? potentialMate : null;
        }
        
        return mate;
    }
}