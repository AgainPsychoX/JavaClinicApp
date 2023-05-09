package pl.edu.ur.pz.clinicapp.utils;

import pl.edu.ur.pz.clinicapp.models.Timetable;
import pl.edu.ur.pz.clinicapp.models.User;

import javax.persistence.EntityManager;
import java.time.DayOfWeek;
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

    public void run() {
        random = new Random(seed);
        logger.info("Running example data seeder with seed: %d".formatted(seed));

        // TODO: imo move all seeding here, and make example data seeding optional (minimalist/structure only mode)
        // TODO: generate N doctors, each with random speciality, timetable

        final var user = User.getByLogin("lwojcik@gmail.com");
        user.getTimetables().clear();
        user.getTimetables().add(generateBasicTimetable());

        // TODO: generate K patients, each selecting one or more doctors, and have appointments generated for them
        // TODO: ...
    }

    private Timetable generateBasicTimetable() {
        return new Timetable() {{
            for (var day : DayOfWeek.values()) {
                if (day == DayOfWeek.SATURDAY)
                    break;
                if (random.nextDouble() < 0.1)
                    continue;
                int h = random.nextDouble() < 0.3 ? 1 : 2;
                int s = random.nextInt(7 * h, 15 * h) * (60 / h);
                int e = s + random.nextInt(4 * h, 8 * h) * (60 / h);
                add(new Timetable.Entry(this, day, s, e));
            }
        }};
    }
}
