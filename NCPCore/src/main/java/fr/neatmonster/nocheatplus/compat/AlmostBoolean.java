package fr.neatmonster.nocheatplus.compat;

/**
 * Some tri-state with booleans in mind.
 * @author mc_dev
 *
 */
public enum AlmostBoolean{
    YES,
    NO,
    MAYBE;

    /**
     * "Match" a boolean.
     * @param value
     * @return
     */
    public static final AlmostBoolean match(final boolean value) {
        return value ? YES : NO;
    }

    /**
     * Match yes/true/y, no/false/n, maybe/default, otherwise returns null.
     * @param input Can be null.
     * @return
     */
    public static final AlmostBoolean match(String input) {
        if (input == null) {
            return null;
        }
        input = input.trim().toLowerCase();
        switch (input) {
            case "true":
            case "yes":
            case "y":
                return AlmostBoolean.YES;
            case "false":
            case "no":
            case "n":
                return AlmostBoolean.NO;
            case "default":
            case "maybe":
                return AlmostBoolean.MAYBE;
            default:
                return null;
        }
    }

    /**
     * Pessimistic interpretation: true iff YES.
     * @return
     */
    public boolean decide(){
        return this == YES;
    }

    /**
     * Optimistic interpretation: true iff not NO.
     * @return
     */
    public boolean decideOptimistically() {
        return this != NO;
    }

}
