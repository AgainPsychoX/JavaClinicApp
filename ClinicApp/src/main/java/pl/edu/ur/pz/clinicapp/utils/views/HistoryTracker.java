package pl.edu.ur.pz.clinicapp.utils.views;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Eases history tracking incl. navigation between history points. Basically stack with extra features.
 * @param <T>
 */
public class HistoryTracker<T> {
    /**
     * Represents point in the history.
     * @param <T>
     */
    static public class HistoryPoint<T> {
        public T which;
        public Object[] context;

        public HistoryPoint(T which, Object... context) {
            this.which = which;
            this.context = context;
        }
    }

    final protected Stack<HistoryPoint<T>> stack;

    /**
     * @return current history point
     */
    public HistoryPoint<T> getCurrent() {
        return stack.peek();
    }

    /**
     * @return previous history point
     */
    public HistoryPoint<T> getPrevious() {
        if (stack.size() < 2) {
            return null;
        }
        return stack.get(stack.size() - 2);
    }

    /**
     * @return list of history points
     */
    public List<HistoryPoint<T>> getHistory() {
        return Collections.unmodifiableList(stack);
    }

    /**
     * Creates empty history tracker.
     */
    public HistoryTracker() {
        stack = new Stack<>();
    }

    /**
     * Creates history tracker with first history point set.
     * @param first the history point to start with
     */
    public HistoryTracker(T first) {
        stack = new Stack<>();
        stack.add(new HistoryPoint<>(first));
    }

    /**
     * Resets history tracker, setting its first history point (cleaning previous history points information).
     * @param first the history point to start with
     */
    public void reset(T first) {
        stack.clear();
        stack.add(new HistoryPoint<>(first));
    }

    /**
     * Navigates to new history point, passed as the type and optional context.
     * @param next the history point value
     * @param context optional context values
     */
    public void go(T next, Object... context) {
        stack.push(new HistoryPoint<>(next, context));
    }

    /**
     * Goes back in the history (if possible)
     * @return current history point
     */
    public HistoryPoint<T> back() {
        if (stack.size() > 1) {
            stack.pop();
        }
        return getCurrent();
    }

    /**
     * @return history tracker data stringified, for debug/inspection proposes
     */
    public String dumpHistoryToString() {
        final var sb = new StringBuilder();
        for (var iterator = stack.iterator(); iterator.hasNext();) {
            final var point = iterator.next();
            sb.append(point.which.toString());
            if (iterator.hasNext()) {
                sb.append(" > ");
            }
        }
        return sb.toString();
    }
}
