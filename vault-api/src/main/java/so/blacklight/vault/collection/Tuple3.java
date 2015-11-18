package so.blacklight.vault.collection;

/**
 * It groups three arbitrary object into a single combined value. The values are ordered
 * and referred to by their numbers.
 * 
 * @param <T> type of the first element
 * @param <U> type of the second element
 * @param <V> type of the third element
 *
 */
public class Tuple3<T, U, V> {

    private final T t;

    private final U u;

    private final V v;

    /**
     * Initialise a new tuple with the given three elements
     * @param t first element
     * @param u second element
     * @param v third element
     */
    public Tuple3(final T t, final U u, final V v) {
        this.t = t;
        this.u = u;
        this.v = v;
    }

    /**
     * Initialise a new tuple with a given two-element tuple and an
     * additional third element
     *
     * @param t two-element tuple
     * @param v third element
     */
    public Tuple3(final Tuple2<T, U> t, final V v) {
        this.t = t.first();
        this.u = t.second();
        this.v = v;
    }

    /**
     * Initialise a new tuple with an element and a two-element tuple.
     * @param t first element
     * @param v two-element tuple
     */
    public Tuple3(final T t, final Tuple2<U, V> v) {
        this.t = t;
        this.u = v.first();
        this.v = v.second();
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

    /**
     * Return the third element in the tuple
     *
     * @return third element
     */
    public V third() {
        return v;
    }
}
