package so.blacklight.vault;

public class Tuple2<T, U> {

    private final T t;

    private final U u;

    public Tuple2(final T t, final U u) {
        this.t = t;
        this.u = u;
    }

    public T first() {
        return t;
    }

    public U second() {
        return u;
    }
}
