package pl.edu.ur.pz.clinicapp.models;

import pl.edu.ur.pz.clinicapp.ClinicApplication;

import java.time.Instant;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class that eases manipulation of anonymized schedule & timetables,
 * avoiding calling concrete user instance while providing vague schedule entries counterparts where necessary.
 */
public class PublicSchedule extends Schedule {
    protected PublicSchedule() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Entry> findScheduleEntries(Instant from, Instant to) {
        // If it's owner viewing their schedule, they always have access to everything
        final var currentUser = ClinicApplication.getUser();
        if (currentUser != null && currentUser.getId().equals(userReference.getId())) {
            return super.findScheduleEntries(from, to);
        }

        /* Iterating over vague busy entries, but picking the detailed counterparts if avaliable. Both of those
         * are streamed in sorted in natural order (by begin timestamp). There cannot be multiple detailed
         * entries starting on the same time (but there can be the vague ones).
         */

        Stream<Entry> entries = SimpleEntry.queryForBusyEntries(userReference, from, to).getResultStream();
        Iterator<Entry> detailsIterator = super.findScheduleEntries(from, to).iterator();
        if (!detailsIterator.hasNext()) {
            return entries;
        }
        Spliterator<Entry> entriesSpliterator = entries.spliterator();
        Iterator<Entry> entriesIterator = Spliterators.iterator(entriesSpliterator);

        final var spliterator = Spliterators.spliterator(new Iterator<Entry>() {
            Entry detailed = detailsIterator.next();

            @Override
            public boolean hasNext() {
                return entriesIterator.hasNext();
            }

            @Override
            public Entry next() {
                final var vague = entriesIterator.next();
                if (vague.getBeginTime().equals(detailed.getBeginTime())
                        && vague.getEndTime().equals(detailed.getEndTime())
                        && vague.getType().equals(detailed.getType())) {
                    final var ret = detailed;
                    detailed = next();
                    return ret;
                }  else {
                    return vague;
                }
            }
        }, entriesSpliterator.getExactSizeIfKnown(), entriesSpliterator.characteristics());

        return StreamSupport.stream(spliterator, false);
    }
}
