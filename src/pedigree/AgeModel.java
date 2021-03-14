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
    
    // Yearly rate
    static final double DEFAULT_DEATH_RATE = 12.5;    // Doubles every 8 years
    static final double DEFAULT_ACCIDENT_RATE = 0.01; // Constant accident rate
    
    // Constant human factors
    static final double DEFAULT_LOYALTY_FACTOR = 0.9;
    static final double DEFAULT_AVG_LIFETIME_OFFSPRING = 2.0;
    
    // "maximum" age [with death rate 1]
    static final double DEFAULT_SCALE = 100.0;
    
    private final double DEATH_RATE;
    private final double ACCIDENT_RATE;
    private final double LOYALTY_FACTOR;
    private final double AVG_LIFETIME_OFFSPRING;
    private final double AGE_FACTOR;
    
    /**
     * Initializes the model with the given rates and factors.
     *
     * @param deathRate The annual death rate
     * @param accidentRate The annual accident rate
     * @param loyaltyFactor The loyalty factor which determines how loyal a
     * {@link Sim} partner is to their mate
     * @param avgLifetimeOffspring The average number of children a mother
     * {@link Sim} will have in her lifetime
     * @param ageScale Maximum age with death rate 1
     */
    
    public AgeModel(double deathRate, double accidentRate,
    double loyaltyFactor, double avgLifetimeOffspring, double ageScale) {
        
        DEATH_RATE = deathRate;
        ACCIDENT_RATE = accidentRate;
        LOYALTY_FACTOR = loyaltyFactor;
        AVG_LIFETIME_OFFSPRING = avgLifetimeOffspring;
        AGE_FACTOR = Math.exp(ageScale / DEATH_RATE);
    }
    
    /**
     * Initializes a model with the default human values.
     */
    
    public AgeModel() {
        
        this(DEFAULT_DEATH_RATE, DEFAULT_ACCIDENT_RATE, DEFAULT_LOYALTY_FACTOR,
        DEFAULT_AVG_LIFETIME_OFFSPRING, DEFAULT_SCALE);
    }
    
    /**
     * Calculates the expected time span (TS) for mating: average number of
     * children will be TS/mating rate.
     * 
     * @param minAge Minimum age of sexual maturity
     * @param maxAge Maximum age of parenting
     * @return Expected time span for mating
     */
    
    public double expectedParenthoodSpan(double minAge, double maxAge) {
        
        // integration of the survival function over the mating age
        
        // numerical integration with simpson's rule, dynamic setting of
        // resolution
        
        int n = 1; // number of intervals along the range
        double d = (maxAge - minAge) / n;
        double st = d * 0.5 * (getSurvival(minAge) + getSurvival(maxAge));
        double espan = 0.0;
        double old_espan = -1.0; // does not matter much
        
        for (int iter = 1; iter < 20; iter++) {
            
            double x0 = minAge + d * 0.5;
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
     * Determines the probability of surviving past the given age.
     * 
     * @param age Age with which probability is calculated
     * @return Probability of dying after the given age
     */
    
    public double getSurvival(double age) {
        
        return Math.exp(-ACCIDENT_RATE * age
        - DEATH_RATE * Math.expm1(age / DEATH_RATE) / AGE_FACTOR);
    }
    
    /**
     * Calculates the Poisson Point Process from the given age interval of
     * maturity.
     * 
     * @param minAge Minimum age of sexual maturity
     * @param maxAge Maximum age of parenting
     * @return The Poisson Point Process rate associated with the model
     */
    
    public double getPoissonPointProcess(double minAge, double maxAge) {
        
        return AVG_LIFETIME_OFFSPRING / expectedParenthoodSpan(minAge, maxAge);
    }
    
    /**
     * Retrieves the loyalty factor associated with this model.
     *
     * @return The loyalty factor associated with this model
     */
    
    public double getLoyaltyFactor() {
        
        return LOYALTY_FACTOR;
    }
    
    /**
     * Generates a random value with the specified lifespan distribution.
     * 
     * @param rnd Pseudorandom number generator for uniform[0,1]
     * @return a random value distributed by Gomperz-Makeham
     */
    
    public double randomAge(Random rnd) {
        
        // pseudorandom by exponential for accident-related death
        double accidentalDeath = -Math.log(rnd.nextDouble()) / ACCIDENT_RATE;
        
        // pseudorandom by Gompertz for old-age
        double ageDeath = DEATH_RATE *
        Math.log1p(-Math.log(rnd.nextDouble()) / DEATH_RATE * AGE_FACTOR);
        
        return Math.min(ageDeath, accidentalDeath);
    }
    
    /**
     * Generates an exponentially distributed random variable.
     * 
     * @param rnd Random number generator
     * @param rate Inverse of the mean
     * @return Exponential(rate)
     */
    
    public static double randomWaitingTime(Random rnd, double rate) {
        
        return -Math.log(rnd.nextDouble()) / rate;
    }
    
    /**
     * Defines the string implementation of an {@link AgeModel}.
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