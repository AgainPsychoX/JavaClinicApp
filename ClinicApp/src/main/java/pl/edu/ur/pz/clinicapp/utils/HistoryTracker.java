package pl.edu.ur.pz.clinicapp.utils;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class HistoryTracker<T> {
    static public class HistoryPoint<T> {
        public T which;
        public Object[] context;

        public HistoryPoint(T which, Object... context) {
            this.which = which;
            this.context = context;
        }
    }

    final protected Stack<HistoryPoint<T>> stack;

    public HistoryPoint<T> getCurrent() {
        return stack.peek();
    }
    public HistoryPoint<T> getPrevious() {
        if (stack.size() < 2) {
            return null;
        }
        return stack.get(stack.size() - 2);
    }

    public List<HistoryPoint<T>> getHistory() {
        return Collections.unmodifiableList(stack);
    }

    public HistoryTracker() {
        stack = new Stack<>();
    }

    public HistoryTracker(T first) {
        stack = new Stack<>();
        stack.add(new HistoryPoint<>(first));
    }

    public void reset(T first) {
        stack.clear();
        stack.add(new HistoryPoint<>(first));
    }

    public void go(T next, Object... context) {
        stack.push(new HistoryPoint<>(next, context));
    }

    public HistoryPoint<T> back() {
        if (stack.size() > 1) {
            stack.pop();
        }
        return getCurrent();
    }

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
