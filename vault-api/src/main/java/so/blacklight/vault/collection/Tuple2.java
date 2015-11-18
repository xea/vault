package so.blacklight.vault.collection;

/**
 * It groups two arbitrary object into a single combined value. The values are ordered
 * and referred to by their numbers.
 * 
 * @param <T> type of the first element
 * @param <U> type of the second element
 */
public class Tuple2<T, U> {

    private final T t;

    private final U u;

    /**
     * Initialise a new tuple with the given two elements
     * @param t first element
     * @param u
     */
    public Tuple2(final T t, final U u) {
        this.t = t;
        this.u = u;
    }

    /**
     * Return the first element in the tuple
     * 
     * @return first element
     */
    public T first() {
        return t;
    }

    /**
     * Return the second element in the tuple
     * 
     * @return second element
     */
    public U second() {
        return u;
    }
}
