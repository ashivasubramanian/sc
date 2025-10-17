package game_engine.util;

/**
 * Class that represents a pair of elements. This is useful for dealing with elements that are related to each other,
 * and need to be dealt with as pairs of elements. Both elements must be of the same type.
 *
 * @param <T> Represents the type of the pair.
 */
public class Pair<T> {

    /**
     * The first element of the pair.
     */
    private final T first;

    /**
     * The second element of the pair.
     */
    private final T second;

    /**
     * Initializes the pair with two elements.
     *
     * @param first  the first element of the pair.
     * @param second the second element of the pair.
     */
    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of the pair
     * @return the first element
     */
    public T getFirst() { return first; }

    /**
     * Returns the second element of the pair.
     * @return the second element
     */
    public T getSecond() { return second; }
}
