package pl.edu.ur.pz.clinicapp.utils;

import pl.edu.ur.pz.clinicapp.models.Timetable;
import pl.edu.ur.pz.clinicapp.models.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.logging.Logger;

public class ExampleDataSeeder
{
    private static final Logger logger = Logger.getLogger(ExampleDataSeeder.class.getName());

    private final EntityManager entityManager;
    private final long seed;
    private Random random;

    public ExampleDataSeeder(EntityManager entityManager) {
        this(entityManager, new Random().nextLong());
    }

    public ExampleDataSeeder(EntityManager entityManager, long seed) {
        this.entityManager = entityManager;
        this.seed = seed;
    }

    private ZonedDateTime zonedDay() {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }
    private ZonedDateTime zonedDay(int daysOffset) {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(daysOffset);
    }

    public void run() {
        random = new Random(seed);
        logger.info("Running example data seeder with seed: %d".formatted(seed));

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // TODO: imo move all seeding here, and make example data seeding optional (minimalist/structure only mode)
        // TODO: generate N doctors, each with random speciality, timetable

        final var user = User.getByLogin("lwojcik@gmail.com");
        user.clearTimetables();
        user.addTimetable(generateBasicTimetable(zonedDay(-10)));
        user.addTimetable(generateBasicTimetable(zonedDay(-3)));
        user.addTimetable(generateBasicTimetable(zonedDay(4)));

        // TODO: generate K patients, each selecting one or more doctors, and have appointments generated for them
        // TODO: ...

        transaction.commit();
    }

    private Timetable generateBasicTimetable() {
        final var effectiveDate = zonedDay().minusDays(random.nextInt(1, 10));
        return generateBasicTimetable(effectiveDate);
    }
    private Timetable generateBasicTimetable(ZonedDateTime effectiveDate) {
        final var timetable = new Timetable(effectiveDate);
        for (var day : DayOfWeek.values()) {
            if (day == DayOfWeek.SATURDAY)
                break;
            if (random.nextDouble() < 0.1)
                continue;
            int h = random.nextDouble() < 0.3 ? 1 : 2;
            int s = random.nextInt(7 * h, 15 * h) * (60 / h);
            int e = s + random.nextInt(4 * h, 8 * h) * (60 / h);
            timetable.add(new Timetable.Entry(day, s, e));
        }
        return timetable;
    }
}
