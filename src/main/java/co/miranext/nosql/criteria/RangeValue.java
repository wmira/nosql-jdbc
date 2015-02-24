package co.miranext.nosql.criteria;

/**
 *
 */
public class RangeValue<T> {

    private T start;
    private T end;
    public RangeValue(final T start, final T end) {
        this.start = start;
        this.end = end;
    }

    public T getStartRange() {
        return start;
    }

    public T getEndRange() {
        return start;
    }
}
