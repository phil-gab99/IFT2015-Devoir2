package pedigree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
    private static MinPQ<Sim> populationQ;
    private static List<Sim> populationList;
    private static MinPQ<Sim> foremothersQ;
    private static MinPQ<Sim> forefathersQ;
    private static Set<Sim> foremothersSet;
    private static Set<Sim> forefathersSet;
    private static double poissonPointProcess;
    private static Random rnd;
    
    // Maps for plotting
    // private static Map<Double, Integer> popGrowth;
    private static Map<Sim, Integer> coalescenceF;
    private static Map<Sim, Integer> coalescenceM;
    
    // Anonymous inner type for comparing Sims using their birth dates
    private static Comparator<Sim> comparator = new Comparator<Sim>() {
        
        public int compare(Sim s1, Sim s2) {
            
            // The "youngest" people are those with the higher birth date
            return -Double.compare(s1.getBirthTime(), s2.getBirthTime());
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
    double accidentRate, double loyaltyFactor, double avgLifetimeOffspring,
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
        populationQ = new MinPQ<Sim>();
        populationList = new ArrayList<Sim>();
        foremothersQ = new HashSet<Sim>();
        forefathersQ = new HashSet<Sim>();
        poissonPointProcess = model
        .getPoissonPointProcess(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        rnd = new Random();
        
        // popGrowth = new HashMap<Double, Integer>();
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
        
        foremothersQ = new MinPQ<Sim>(comparator);
        forefathersQ = new MinPQ<Sim>(comparator);
        
        foremothersSet = new HashSet<Sim>();
        forefathersSet = new HashSet<Sim>();
        
        dividePop();
        
        ancestralFemaleLineage();
        ancestralMaleLineage();
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
        }
        
        // Adding the newly born Sim to the population
        populationQ.insert(e.getSubject());
        populationList.add(e.getSubject());
    }
    
    /**
     * The method {@link #deathSim()} completes the appropriate procedure for
     * the {@link Death} of a {@link Sim}.
     */
    
    private static void deathSim() {
        
        populationList.remove(populationQ.delMin());
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
        
        Sim mate;
        Sim mother = e.getSubject();
        
        // Different procedure on whether the mother has a mate or not
        if (mother.isInARelationship(e.getTime())) {
            
            if (Math.random() < 1 - model.getLoyaltyFactor()) {
                
                mate = getRandomMate(e);
                
                mother.setMate(mate);
                
                if (mate != null) {
                    
                    mate.setMate(mother);
                }
            }
        } else {
            
            do {
                
                mate = getRandomMate(e);
                
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
     * The method {@link #getRandomMate(Event)} selects a mating {@link Sim}
     * from our population for the {@link Sim} associated with the given
     * {@link Event}.
     *
     * @param e The {@link Event} details
     */
    
    private static Sim getRandomMate(Event e) {
        
        List<Sim> nonCompatSims = new ArrayList<Sim>();
        
        Sim mate = null;
        
        while (mate == null && !populationList.isEmpty()) {
            
            int rndIndex = rnd.nextInt(populationList.size());
            Sim potentialMate = populationList.remove(rndIndex);
            
            mate = potentialMate != null
                && !(potentialMate.getSex().equals(e.getSubject().getSex()))
                && potentialMate.isMatingAge(e.getTime())
                && potentialMate.isAlive(e.getTime()) ? potentialMate : null;
                
            nonCompatSims.add(potentialMate);
        }
        
        populationList.addAll(nonCompatSims);
        
        return mate;
    }
    
    /**
     * The method {@link #dividePop()} divides the current population into male
     * and female subgroups.
     */
    
    private static void dividePop() {
        
        while (!populationQ.isEmpty()) {
            
            Sim sim = populationQ.delMin();
            
            if (sim.getSex().equals(Sim.Sex.F)) {
                
                foremothersQ.insert(sim);
                foremothersSet.add(sim);
            } else {
                
                forefathersQ.insert(sim);
                forefathersSet.add(sim);
            }
        }
    }
    
    /**
     * The method {@link #ancestralFemaleLineage()} defines female coalescences
     * after the simulation has been completed.
     */
    
    private static void ancestralFemaleLineage() {
        
        Sim youngest = foremothersQ.delMin();
    }
    
    /**
     * The method {@link #ancestralFemaleLineage()} defines male coalescences
     * after the simulation has been completed.
     */
    
    private static void ancestralMaleLineage() {
        
        Sim youngest = forefathersQ.delMin();
    }
}