package pedigree;

/**
 * The class Sim defines a virtual individual along with methods to access
 * their information.
 * 
 * @author Philippe Gabriel
 * @version 1.0 2021-03-17
 */
 
public class Sim implements Comparable<Sim> {
    
    private static int NEXT_SIM_IDX = 0; // Index keeping track of totals sims
    
    public static double MIN_MATING_AGE_F = 16.0; // Female minimum mating age
    public static double MIN_MATING_AGE_M = 16.0; // Male minimum mating age
    public static double MAX_MATING_AGE_F = 50.0; // Female maximum mating age
    public static double MAX_MATING_AGE_M = 73.0; // Male maximum mating age
    
    public enum Sex {F, M}; // Enum holding the two genders to consider

    private final int sim_ident; // Current sim's identity defining their index
    
    private Sim mother; // Current Sim's mother
    private Sim father; // Current Sim's father
    private Sim mate;   // Current Sim's mate
    
    private double birthtime; // Current Sim's birth date
    private double deathtime; // Current Sim's death date
    
    private Sex sex; // Current Sim's gender
    
    /**
     * The constructor method {@link Sim} 
     * 
     * @param mother The {@link Sim}'s mother
     * @param father The {@link Sim}'s father
     * @param birthtime The {@link Sim}'s birth date
     * @param sex The {@link Sim}'s gender
     */
    
    protected Sim(Sim mother, Sim father, double birthtime, Sex sex) {
        
        this.mother = mother;
        this.father = father;
        
        this.birthtime = birthtime;
        this.deathtime = Double.POSITIVE_INFINITY;
        
        this.sex = sex;
        
        this.sim_ident = NEXT_SIM_IDX++;
    }
    
    /**
     * A founding Sim.
     * 
     */
    public Sim(Sex sex) {
        
        this(null, null, 0.0, sex);
    }
    
    /**
     * The getter method {@link #getIdentString} retrieves the given
     * {@link Sim}'s identity.
     * 
     * @param sim {@link Sim} of interest
     * @return The given {@link Sim}'s String implementation of their identity
     * and gender or nothing if the given {@link Sim} is null
     */
    
    private static String getIdentString(Sim sim) {
        
        return sim == null ? "" : "sim." + sim.sim_ident + "/" + sim.sex;
    }
    
    /**
     * The getter method {@link #getMother} retrieves the current {@link Sim}'s
     * mother.
     *
     * @return The current {@link Sim}'s mother or null for a founder
     */
     
    public Sim getMother() {
        
        return mother;
    }
    
    /**
     * The getter method {@link #getFather} retrieves the current {@link Sim}'s
     * father.
     *
     * @return The current {@link Sim}'s father or null for a founder
     */
     
    public Sim getFather() {
        
        return father;
    }
    
    /**
     * The getter method {@link #getMate} retrieves the current {@link Sim}'s
     * mating partner.
     * 
     * @return The current {@link Sim}'s mate
     */
    
    public Sim getMate() {
        
        return mate;
    }
    
    /**
     * The setter method {@link #setMate} sets the current's {@link Sim}'s 
     * mating partner.
     *
     * @param mate The current {@link Sim}'s mating {@link Sim}
     */
    
    public void setMate(Sim mate) {
        
        this.mate = mate;
    }
    
    /**
     * The getter method {@link #getBirthTime} retrieves the current
     * {@link Sim}'s birth date.
     * 
     * @return The current {@link Sim}'s birth time
     */
    
    public double getBirthTime() {
        
        return birthtime;
    }
    
    /**
     * The getter method {@link #getDeathTime} retrieves the current
     * {@link Sim}'s death date.
     * 
     * @return The current {@link Sim}'s death time
     */
    
    public double getDeathTime() {
        
        return deathtime;
    }
    
    /**
     * The setter method {@link #setDeathTime} sets the current's {@link Sim}'s 
     * death date.
     *
     * @param deathtime Death date to set for the current {@link Sim}
     */
    
    public void setDeathTime(double deathtime) {
        
        this.deathtime = deathtime;
    }
    
    /**
     * The getter method {@link #getSex} retrieves the current {@link Sim}'s
     * gender.
     * 
     * @return The current {@link Sim}'s sex
     */
    
    public Sex getSex() {
        
        return sex;
    }
    
    /**
     * The method isFounder checks if the current {@link Sim} is a founder or
     * not.
     * 
     * @return <code>true</code> if the current {@link Sim} is a founder
     * <li><code>false</code> if the current {@link Sim} is not a founder</li>
     */
    
    public boolean isFounder() {
        
        return mother == null && father == null;
    }
    
    /**
     * The method {@link #isMatingAge} checks whether the current {@link Sim}
     * is of mating age at the given time.
     * 
     * @param time Time at which the comparison is to be undertaken
     * @return true if the current {@link Sim} is alive and his age is within
     * the mating age boundaries<li>false otherwise</li>
     */
     
    public boolean isMatingAge(double time) {
        
        if (time < getDeathTime()) {
            
            double age = time - getBirthTime();
            
            return
                Sex.F.equals(getSex()) ?
                    age >= MIN_MATING_AGE_F && age <= MAX_MATING_AGE_F
                    : age >= MIN_MATING_AGE_M && age <= MAX_MATING_AGE_M;
        }
        
        return false;
    }
    
    /**
     * The method {@link #isInARelationship} checks whether the current
     * {@link Sim} has a faithful and alive mating partner at the given time.
     * 
     * @param time Time at which the test is to be undertaken
     * @return true if the current {@link Sim} has a mating partner<li>false
     * otherwise</li>
     */
     
    public boolean isInARelationship(double time) {
        
        return mate != null
            && mate.getDeathTime() > time
            && mate.getMate() == this;
    }
    
    /** 
     * The method {@link #compareTo} defines {@link Sim} ordering by
     * {@link Sim}'s death date.
     * 
     * @param o Other {@link Sim} with which to compare to
     * @return The value 0 if the current instance and the other {@link Sim}
     * have equal death dates<li>A value less than 0 if the current
     * instance's death date is less than that of the other {@link Sim}</li>
     * <li>A value greater than 0 if the current instance's death date is
     * greater than that of the other {@link Sim}</li>
     * @see java.lang.Comparable
     * @see java.lang.Double
     */
    @Override
    public int compareTo(Sim o) {
        
        return Double.compare(this.deathtime, o.deathtime);
    }
    
    /**
     * The method {@link #toString} defines the string implementation of a
     * {@link Sim}.
     * 
     * @return String implementation of {@link Sim}
     * @see java.lang.Object
     */
    @Override
    public String toString() {
        
        return getIdentString(this) +
        " [" + birthtime + ".." + deathtime +
        ", mate " + getIdentString(mate) +
        "\tmom " + getIdentString(getMother()) +
        "\tdad " + getIdentString(getFather()) + "]";
    }
}