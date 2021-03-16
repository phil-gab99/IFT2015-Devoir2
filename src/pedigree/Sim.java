package pedigree;

/**
 * The class {@link Sim} defines a virtual individual along with methods to
 * access their information.
 * 
 * @version 1.3.11 2021-03-28
 * @author Philippe Gabriel
 */
 
public class Sim implements Comparable<Sim> {
    
    private static int nextSimIdx = 0; // Index keeping track of totals sims
    
    public static final double MIN_MATING_AGE_F = 16.0; // Female min mate age
    public static final double MIN_MATING_AGE_M = 16.0; // Male min mate age
    public static final double MAX_MATING_AGE_F = 50.0; // Female max mate age
    public static final double MAX_MATING_AGE_M = 73.0; // Male max mate age
    
    public enum Sex {F, M}; // Enum holding the two genders to consider

    private final int SIM_IDENT; // Current sim's identity stemming from index
    
    private Sim mother; // Current Sim's mother
    private Sim father; // Current Sim's father
    private Sim mate;   // Current Sim's mate
    
    private double birthtime; // Current Sim's birth date
    private double deathtime; // Current Sim's death date
    
    private Sex sex; // Current Sim's gender
    
    /**
     * Initializes a new {@link Sim} with given mother and father {@link Sim}s
     * as well as their birth date and gender.
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
        deathtime = Double.POSITIVE_INFINITY;
        
        this.sex = sex;
        
        SIM_IDENT = nextSimIdx++;
    }
    
    /**
     * Initializes a new {@link Sim} with given mother and father {@link Sim}s
     * as well as their birth date and a random gender.
     * 
     * @param mother The {@link Sim}'s mother
     * @param father The {@link Sim}'s father
     * @param birthtime The {@link Sim}'s birth date
     */
    
    protected Sim(Sim mother, Sim father, double birthtime) {
        
        this(mother, father, birthtime, Math.random() < 0.5 ? Sex.M : Sex.F);
    }
    
    /**
     * Initializes a founder {@link Sim} with a given gender.
     *
     * @param sex The {@link Sim}'s gender
     */
    
    protected Sim(Sex sex) {
        
        this(null, null, 0.0, sex);
    }
    
    /**
     * Initializes a founder {@link Sim} with a randomly selected gender.
     *
     * @param sex The {@link Sim}'s gender
     */
    
    protected Sim() {
        
        this(Math.random() < 0.5 ? Sex.M : Sex.F);
    }
    
    /**
     * Retrieves the given {@link Sim}'s identity.
     * 
     * @param sim {@link Sim} of interest
     * @return The given {@link Sim}'s String implementation of their identity
     * and gender or nothing if the given {@link Sim} is null
     */
    
    private static String getIdentString(Sim sim) {
        
        return sim == null ? "" : "sim." + sim.SIM_IDENT + "/" + sim.sex;
    }
    
    /**
     * Retrieves the current {@link Sim}'s mother.
     *
     * @return The current {@link Sim}'s mother or null for a founder
     */
    
    public Sim getMother() {
        
        return mother;
    }
    
    /**
     * Retrieves the current {@link Sim}'s father.
     *
     * @return The current {@link Sim}'s father or null for a founder
     */
     
    public Sim getFather() {
        
        return father;
    }
    
    /**
     * Retrieves the current {@link Sim}'s mating partner.
     * 
     * @return The current {@link Sim}'s mate
     */
    
    public Sim getMate() {
        
        return mate;
    }
    
    /**
     * Sets the current's {@link Sim}'s mating partner.
     *
     * @param mate The mating partner to assign to this {@link Sim}
     */
    
    public void setMate(Sim mate) {
        
        this.mate = mate;
    }
    
    /**
     * Retrieves the current {@link Sim}'s birth date.
     * 
     * @return The current {@link Sim}'s birth time
     */
    
    public double getBirthTime() {
        
        return birthtime;
    }
    
    /**
     * Retrieves the current {@link Sim}'s death date.
     * 
     * @return The current {@link Sim}'s death time
     */
    
    public double getDeathTime() {
        
        return deathtime;
    }
    
    /**
     * Sets the current's {@link Sim}'s death date.
     *
     * @param deathtime Death date to set for the current {@link Sim}
     */
    
    public void setDeathTime(double deathtime) {
        
        this.deathtime = deathtime;
    }
    
    /**
     * Retrieves the current {@link Sim}'s gender.
     * 
     * @return The current {@link Sim}'s sex
     */
    
    public Sex getSex() {
        
        return sex;
    }
    
    /**
     * Checks if the current {@link Sim} is alive at the given time.
     *
     * @param time Time at which to undertake the check
     * @return <ul><li>{@code true} if the current {@link Sim} is alive</li>
     * <li>{@code false} otherwise</li></ul>
     */
    
    public boolean isAlive(double time) {
        
        return deathtime > time;
    }
    
    /**
     * Determines whether the current {@link Sim} is a founder or not.
     * 
     * @return <ul><li>{@code true} if the current {@link Sim} is a founder
     * </li><li>{@code false} otherwise</li></ul>
     */
    
    public boolean isFounder() {
        
        return mother == null && father == null;
    }
    
    /**
     * Checks if the current {@link Sim} has a faithful and alive mating
     * partner at the given time.
     * 
     * @param time Time at which to undertake the check
     * @return <ul><li>{@code true} if the current {@link Sim} has a mating
     * partner</li><li>{@code false} otherwise</li></ul>
     */
     
    public boolean isInARelationship(double time) {
        
        return mate != null
            && mate.deathtime > time
            && mate.mate == this;
    }
    
    /**
     * Determines whether the current {@link Sim} is of mating age at the given
     * time or not.
     * 
     * @param time Time at which to undertake the check
     * @return <ul><li>{@code true} if the current {@link Sim} is alive and his
     * age is within the mating age boundaries</li><li>{@code false} otherwise
     * </li></ul>
     */
    
    public boolean isMatingAge(double time) {
        
        if (isAlive(time)) {
            
            double age = time - birthtime;
            
            return Sex.F.equals(sex) ?
                age >= MIN_MATING_AGE_F && age <= MAX_MATING_AGE_F :
                age >= MIN_MATING_AGE_M && age <= MAX_MATING_AGE_M;
        }
        
        return false;
    }
    
    /** 
     * Defines {@link Sim} ordering by {@link Sim}'s death date.
     * 
     * @param s Other {@link Sim} with which to compare to
     * @return <ul><li>The value 0 if the current {@link Sim} and the other
     * {@link Sim} have equal death dates</li><li>A value less than 0 if the
     * current {@link Sim}'s death date is before that of the other {@link Sim}
     * </li><li>A value greater than 0 if the current {@link Sim}'s death date
     * after that of the other {@link Sim}</li></ul>
     * @see java.lang.Comparable
     * @see java.lang.Double
     */
    @Override
    public int compareTo(Sim s) {
        
        return Double.compare(this.deathtime, s.deathtime);
    }
    
    /**
     * Defines the string implementation of a {@link Sim}.
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