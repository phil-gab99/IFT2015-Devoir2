package pedigree;

import java.util.Arrays;
import java.util.Random;

/**
 * The class {@link AgeModel} models the lifespan of a {@link Sim} following a
 * Gompertz-Makeham distribution.
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class AgeModel {

    /**
     * Test for tabulating random lifespans from command line.
     * 
     * @param args Format: death-rate accident-rate [scale]
     */
    
    public static void main(String[] args) {
        
        int arg_idx = 0;
        
        double dth = Double.parseDouble(args[arg_idx++]);
        double acc = Double.parseDouble(args[arg_idx++]);
        double scale = DEFAULT_SCALE;
        
        if (arg_idx < args.length) {
            
            scale = Double.parseDouble(args[arg_idx++]);
        }
        
        AgeModel model = new AgeModel(dth, acc, scale);
        Random rnd = new Random();
        
        int smp_size = 1000; // this many random values
        double[] lifespan = new double[smp_size];
        double avg = 0.0;
        
        for (int r = 0; r < smp_size; r++) {
            
            double d = model.randomAge(rnd);
            avg += d;
            lifespan[r] = d;
        }
        
        avg /= smp_size;
        Arrays.sort(lifespan);
        
        // plot for distribution function
            // 1st and 3rd columns should match
            // (empirical vs. theoretical cumulative distribution function)
        for (int r = 0; r<smp_size; r++) {
            
            System.out.println((1 + r) + "\t" + lifespan[r] + "\t" +
            smp_size * (1.0 - model.getSurvival(lifespan[r])));
        }
        
        double span = model.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        
        double stable_rate = 2.0 / span;
        System.out.println("avg\t" + avg + "\tmating span(mother): " + span +
        "\tstable " + stable_rate + "\t// 1/" + span / 2.0);
    }
    
    private final double DEATH_RATE;
    private final double ACCIDENT_RATE;
    private final double AGE_FACTOR;
    
    private static final double DEFAULT_DEATH_RATE = 12.5;
    private static final double DEFAULT_ACCIDENT_RATE = 0.01; // Yearly rate
    private static final double DEFAULT_SCALE = 100.0; // "maximum" age [with death rate 1]
    
    /**
     * The constructor method {@link AgeModel}
     * 
     * 
     */
    
    public AgeModel(double deathRate, double accidentRate, double ageScale) {
        
        this.DEATH_RATE = deathRate;
        this.ACCIDENT_RATE = accidentRate;
        this.AGE_FACTOR = Math.exp(ageScale / DEATH_RATE);
    }
    
    /**
     * The constructor method {@link AgeModel} instantiates a model with the
     * default human values.
     */
    
    public AgeModel() {
        
        this(DEFAULT_DEATH_RATE, DEFAULT_ACCIDENT_RATE, DEFAULT_SCALE);
    }
    
    /**
     * The method {@link #expectedParenthoodSpan} calculates the expected time
     * span (TS) for mating: average number of children will be TS/mating rate.
     * 
     * @param min_age Minimum age of sexual maturity
     * @param max_age Maximum age of parenting
     * @return Expected time span for mating
     */
    
    public double expectedParenthoodSpan(double min_age, double max_age) {
        
        // integration of the survival function over the mating age
        
        // numerical integration with simpson's rule, dynamic setting of resolution
        
        int n = 1; // number of intervals along the range
        double d = (max_age - min_age) / n;
        double st = d * 0.5 * (getSurvival(min_age) + getSurvival(max_age));
        double espan = 0.0;
        double old_espan = -1.0; // does not matter much
        
        for (int iter = 1; iter < 20; iter++) {
            
            double x0 = min_age + d * 0.5;
            double s2 = 0.0;
            
            for (int i = 0; i < n; i++) {
                
                double x = x0 + i * d;
                s2 += getSurvival(x);
            }
            
            double old_st = st;
            
            st = 0.5 * (st + d * s2); // simple trapezoidal 
            espan = (4.0 * st - old_st) / 3.0; // Simpson's ... better than st

            n = n * 2;
            d = d * 0.5;
            
            // first five iteration kept 
            if (iter > 5
            && (Math.abs(old_espan - espan) < 1e-7 * old_espan
                || (espan == 0.0 && old_espan == 0.0))) {
                
                break;
            }
            
            old_espan = espan;
        }
        
        return espan;
    }
    
    /**
     * The method {@link #getSurvival} determines the probability of surviving
     * past the given age.
     * 
     * @param age Age with which probability is calculated
     * @return Probability of dying after the given age
     */
    
    public double getSurvival(double age) {
        
        return Math.exp(-ACCIDENT_RATE * age - DEATH_RATE * Math.expm1(age / DEATH_RATE) / AGE_FACTOR);
    }
    
    /**
     * The method {@link #ramdomAge} generates a random value with the
     * specified lifespan distribution.
     * 
     * @param rnd Pseudorandom number generator for uniform[0,1]
     * @return a random value distributed by Gomperz-Makeham
     */
    
    public double randomAge(Random rnd) {
        
        // pseudorandom by exponential for accident-related death
        double accidentalDeath = -Math.log(rnd.nextDouble()) / ACCIDENT_RATE;
        
        // pseudorandom by Gompertz for old-age
        double ageDeath = DEATH_RATE * Math.log1p(-Math.log(rnd.nextDouble()) / DEATH_RATE * AGE_FACTOR);
        
        return Math.min(ageDeath, accidentalDeath);
    }
    
    /**
     * The method {@link randomWaitingTime} generates an exponentially
     * distributed random variable.
     * 
     * @param rnd Random number generator
     * @param rate Inverse of the mean
     * @return Exponential(rate)
     */
    
    public static double randomWaitingTime(Random rnd, double rate) {
        
        return -Math.log(rnd.nextDouble()) / rate;
    }
    
    /**
     * The method {@link #toString} defines the string implementation of an
     * {@link AgeModel}.
     * 
     * @return String implementation of {@link AgeModel}
     * @see java.lang.Object
     */
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("[acc ").append(ACCIDENT_RATE).append(", age ")
        .append(DEATH_RATE).append(", agefactor ").append(AGE_FACTOR)
        .append("]");
        
        return sb.toString();
    }
}