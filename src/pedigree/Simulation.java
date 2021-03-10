package pedigree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
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
    private static MinPQ<Sim> populationQ;
    private static List<Sim> populationList;
    private static double poissonPointProcess;
    private static Random rnd;
    
    // Maps for plotting
    private static Map<Double, Integer> popGrowth;
    private static Map<Double, Integer> coalescenceF;
    private static Map<Double, Integer> coalescenceM;
    
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
     * values.
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
     * The method {@link #getPopGrowth()} retrieves the population growth
     * associated with this simulation.
     *
     * @return The associated {@link Map}
     */
    
    public static Map<Double, Integer> getPopGrowth() {
        
        return popGrowth;
    }
    
    /**
     * The method {@link #getCoalescenceF()} retrieves the female coalescence
     * associated with this simulation.
     *
     * @return The associated {@link Map}
     */
    
    public static Map<Double, Integer> getCoalescenceF() {
        
        return coalescenceF;
    }
    
    /**
     * The method {@link #getCoalescenceM()} retrieves the male coalescence
     * associated with this simulation.
     *
     * @return The associated {@link Map}
     */
    
    public static Map<Double, Integer> getCoalescenceM() {
        
        return coalescenceM;
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
        
        int period = 0;
        
        // Initilizing model with default values if not already initialized
        if (model == null) {
            
            model = new AgeModel();
        }
        
        eventQ = new MinPQ<Event>();
        populationQ = new MinPQ<Sim>();
        populationList = new ArrayList<Sim>();
        poissonPointProcess = model
        .getPoissonPointProcess(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        rnd = new Random();
        
        popGrowth = new TreeMap<Double, Integer>();
        coalescenceF = new TreeMap<Double, Integer>();
        coalescenceM = new TreeMap<Double, Integer>();
        
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
                
                if (e instanceof Death) {
                    
                    deathSim();
                }
            }
            
            if ((e.getTime() / period) > 1) {
                
                popGrowth.put(e.getTime(), populationQ.size());
                period += 100;
            }
        }
        
        MinPQ<Sim> foremothersQ = new MinPQ<Sim>(comparator);
        MinPQ<Sim> forefathersQ = new MinPQ<Sim>(comparator);
        
        dividePop(foremothersQ, forefathersQ);
        
        ancestralFemaleLineage(foremothersQ);
        ancestralMaleLineage(forefathersQ);
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
        
        Sim sim = e.getSubject();
        
        // Setting the appropriate random death time
        sim.setDeathTime(e.getTime() + model.randomAge(rnd));
        
        // Add Death Event for this Sim
        eventQ.insert(new Death(sim, sim.getDeathTime()));
        
        // If the Sim is a woman, add a Reproduction Event
        if (sim.getSex().equals(Sim.Sex.F)) {
            
            eventQ.insert(new Reproduction(sim, e.getTime() +
            AgeModel.randomWaitingTime(rnd, poissonPointProcess)));
        }
        
        // Adding the newly born Sim to the population
        populationQ.insert(sim);
        populationList.add(sim);
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
        
        // If the female Sim is of mating age
        if (e.getSubject().isMatingAge(e.getTime())) {
            
            // Choose father for the newborn child
            chooseFatherSim(e);
            
            // Birth of their child
            if (e.getSubject().isInARelationship(e.getTime())) {
                
                eventQ.insert(new Birth(new Sim(e.getSubject(),
                e.getSubject().getMate(), e.getTime()), e.getTime()));
            }
        }
        
        eventQ.insert(new Reproduction(e.getSubject(), e.getTime() +
        AgeModel.randomWaitingTime(rnd, poissonPointProcess)));
    }
    
    /**
     * The method {@link #chooseFatherSim(Event)} selects a male {@link Sim}
     * with which the female {@link Sim} who is in the process of a
     * {@link Reproduction} {@link Event} will mate with.
     *
     * @param e The {@link Reproduction} {@link Event} details
     */
    
    private static void chooseFatherSim(Event e) {
        
        List<Sim> nonCompatSims = new ArrayList<Sim>();
        
        Sim mate;
        Sim mother = e.getSubject();
        
        // Different procedure on whether the mother has a mate or not
        if (mother.isInARelationship(e.getTime())) {
            
            if (Math.random() < 1 - model.getLoyaltyFactor()) {
                
                mate = getRandomMate(e, nonCompatSims);
                
                mother.setMate(mate);
                
                if (mate != null) {
                    
                    mate.setMate(mother);
                }
            }
        } else {
            
            do {
                
                mate = getRandomMate(e, nonCompatSims);
                
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
            } while (!(mother.isInARelationship(e.getTime())
            || populationList.isEmpty()));
        }
        
        // Restoring population List to its original state
        populationList.addAll(nonCompatSims);
    }
    
    /**
     * The method {@link #getRandomMate(Event)} selects a mating {@link Sim}
     * from our population for the {@link Sim} associated with the given
     * {@link Event}.
     *
     * @param e The {@link Event} details
     * @param badMatch {@link List} of incompatible {@link Sim}s
     */
    
    private static Sim getRandomMate(Event e, List<Sim> badMatch) {
        
        Sim mate = null;
        
        while (mate == null && !populationList.isEmpty()) {
            
            int rndIndex = rnd.nextInt(populationList.size());
            Sim potentialMate = populationList.remove(rndIndex);
            
            mate = potentialMate != null
                && !(potentialMate.getSex().equals(e.getSubject().getSex()))
                && potentialMate.isMatingAge(e.getTime())
                && potentialMate.isAlive(e.getTime()) ? potentialMate : null;
                
            badMatch.add(potentialMate);
        }
        
        return mate;
    }
    
    /**
     * The method {@link #dividePop()} divides the current population into male
     * and female subgroups.
     *
     * @param females The female {@link Sim} subgroup
     * @param males The male {@link Sim} subgroup
     */
    
    private static void dividePop(MinPQ<Sim> females, MinPQ<Sim> males) {
        
        while (!populationQ.isEmpty()) {
            
            Sim sim = populationQ.delMin();
            
            if (sim.getSex().equals(Sim.Sex.F)) {
                
                females.insert(sim);
            } else {
                
                males.insert(sim);
            }
        }
    }
    
    /**
     * The method {@link #ancestralFemaleLineage()} defines female coalescences
     * after the simulation has been completed.
     *
     * @param females The female {@link Sim} subgroup
     */
    
    private static void ancestralFemaleLineage(MinPQ<Sim> females) {
        
        if (females.isEmpty()) {
            
            return;
        }
        
        Sim youngest;
        
        // Iterating until population of founders or if only one mother remains
        while (!((youngest = females.delMin()).isFounder())
            && females.size() > 1) {
            
            if (females.contains(youngest.getMother())) {
                
                coalescenceF.put(youngest.getBirthTime(), females.size());
            } else {
                
                females.insert(youngest.getMother());
            }
        }
    }
    
    /**
     * The method {@link #ancestralMaleLineage()} defines male coalescences
     * after the simulation has been completed.
     *
     * @param males The male {@link Sim} subgroup
     */
    
    private static void ancestralMaleLineage(MinPQ<Sim> males) {
        
        if (males.isEmpty()) {
            
            return;
        }
        
        Sim youngest;
        
        // Iterating until population of founders or if only one father remains
        while (!((youngest = males.delMin()).isFounder()) && males.size() > 1) {
            
            if (males.contains(youngest.getFather())) {
                
                coalescenceM.put(youngest.getBirthTime(), males.size());
            } else {
                
                males.insert(youngest.getFather());
            }
        }
    }
}