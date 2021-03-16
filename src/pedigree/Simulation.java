package pedigree;

import java.util.ArrayList;
import java.util.Comparator;
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
     * Retrieves the population growth associated with this simulation.
     *
     * @return The associated {@link Map}
     */
    
    public static Map<Double, Integer> getPopGrowth() {
        
        return popGrowth;
    }
    
    /**
     * Retrieves the female coalescence associated with this simulation.
     *
     * @return The associated {@link Map}
     */
    
    public static Map<Double, Integer> getCoalescenceF() {
        
        return coalescenceF;
    }
    
    /**
     * Retrieves the male coalescence associated with this simulation.
     *
     * @return The associated {@link Map}
     */
    
    public static Map<Double, Integer> getCoalescenceM() {
        
        return coalescenceM;
    }
    
    /**
     * Begins the simulation of {@link Event}s stemming from the {@link Birth}
     * of a given amount of founder {@link Sim}s. The simulation ends after the
     * given maximum time has been reached or if no more {@link Event}s remain
     * to be applied.
     * 
     * @param n Number of founding {@link Sim}s
     * @param tMax Maximum time length of simulation
     */
    
    public static void simulate(int n, double tMax) {
        
        int interval = 100; // Intervals at which population size is evaluated
        int period = 0;     // Time period for sampling population size

        model = new AgeModel();
        eventQ = new MinPQ<Event>();
        populationQ = new MinPQ<Sim>();
        populationList = new ArrayList<Sim>();
        poissonPointProcess = model
        .getPoissonPointProcess(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        rnd = new Random();
        
        // Initiliazing TreeMaps to preserve natural ordering by keys
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
                period += interval;
            }
        }
        
        MinPQ<Sim> foremothersQ = new MinPQ<Sim>(comparator);
        MinPQ<Sim> forefathersQ = new MinPQ<Sim>(comparator);
        
        dividePop(foremothersQ, forefathersQ);
        
        ancestralLineage(foremothersQ, "getMother", coalescenceF);
        ancestralLineage(forefathersQ, "getFather", coalescenceM);
    }
    
    /**
     * Initiates the simulation with the {@link Birth} of a given amount of
     * founder {@link Sim}s.
     *
     * @param n Integer indicating number of founder {@link Sim}s
     */
    
    private static void generateFounders(int n) {
        
        while (n-- > 0) {
            
            eventQ.insert(new Birth(new Sim(), 0.0));
        }
    }
    
    /**
     * Completes the appropriate procedure for the {@link Birth} of a
     * {@link Sim}.
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
     * Completes the appropriate procedure for the {@link Death} of a
     * {@link Sim}.
     */
    
    private static void deathSim() {
        
        populationList.remove(populationQ.delMin());
    }
    
    /**
     * Completes the appropriate procedure for the {@link Reproduction}
     * relating to a female {@link Sim}.
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
     * Selects a male {@link Sim} with which the female {@link Sim} who is in
     * the process of a {@link Reproduction} {@link Event} will mate with.
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
     * Selects a mating {@link Sim} from the present population for the
     * {@link Sim} associated with the given {@link Event}.
     *
     * @param e The {@link Event} details
     * @param checkedSim {@link List} of checked {@link Sim}s
     */
    
    private static Sim getRandomMate(Event e, List<Sim> checkedSim) {
        
        Sim mate = null;
        
        while (mate == null && !populationList.isEmpty()) {
            
            int rndIndex = rnd.nextInt(populationList.size());
            Sim potentialMate = populationList.remove(rndIndex);
            
            mate = potentialMate != null
                && !(potentialMate.getSex().equals(e.getSubject().getSex()))
                && potentialMate.isMatingAge(e.getTime())
                && potentialMate.isAlive(e.getTime()) ? potentialMate : null;
                
            checkedSim.add(potentialMate); // For rebalancing population list
        }
        
        return mate;
    }
    
    /**
     * Divides the current population into female and male subgroups.
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
     * Defines gender coalescence after the simulation has been completed.
     *
     * @param subgroup The {@link Sim} gender subgroup
     * @param parent Method name depending on the gender passed which can be
     * either <ul><li>getMother for the female subgroup</li><li>getFather for
     * the male subgroup</li></ul>
     * @param coalescence The Map for holding the value pairs to plot
     */
    
    private static void ancestralLineage(MinPQ<Sim> subgroup, String parent,
        Map<Double, Integer> coalescence) {
        
        if (subgroup.isEmpty()) {
            
            return;
        }
        
        Sim youngest;
        
        while (!((youngest = subgroup.delMin()).isFounder())
            && subgroup.size() > 1) {
            
            Sim parentSim = null;
            
            try {
                
                parentSim = (Sim)youngest.getClass().getDeclaredMethod(parent)
                .invoke(youngest, new Object[0]);
            } catch(Exception e) {
                
                e.printStackTrace();
            }
            
            if (subgroup.contains(parentSim)) {
                
                coalescence.put(youngest.getBirthTime(), subgroup.size());
            } else {
                
                subgroup.insert(parentSim);
            }
        }
    }
}