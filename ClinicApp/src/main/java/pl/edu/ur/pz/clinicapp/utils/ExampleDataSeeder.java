package pl.edu.ur.pz.clinicapp.utils;

import com.github.javafaker.Faker;
import pl.edu.ur.pz.clinicapp.models.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

public class ExampleDataSeeder
{
    private static final Logger logger = Logger.getLogger(ExampleDataSeeder.class.getName());

    private final EntityManager entityManager;
    private final long seed;
    private final Locale locale;
    private Random random;
    private Faker faker;
    private ZonedDateTime now;

    public ExampleDataSeeder(EntityManager entityManager) {
        this(entityManager, new Random().nextLong());
    }

    public ExampleDataSeeder(EntityManager entityManager, long seed) {
        this.entityManager = entityManager;
        this.seed = seed;
        this.locale = Locale.getDefault();
    }

    public ExampleDataSeeder(EntityManager entityManager, long seed, Locale locale) {
        this.entityManager = entityManager;
        this.seed = seed;
        this.locale = locale;
    }

    /**
     * Runs the seeding process.
     */
    public void run() {
        random = new Random(seed);
        faker = new Faker(locale, random);
        now = ZonedDateTime.now(); // TODO: decouple current time from seeding process, make it fully seed dependent?
        logger.info("Running example data seeder with seed: %d and locale: %s".formatted(seed, locale));

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            // TODO: imo move all seeding here
            // TODO: generate N doctors, each with random speciality, timetable

            final var doctorsCount = random.nextInt(doctorSpecialities.length + 5, 2 * doctorSpecialities.length);
            logger.fine("Adding %d doctors (some also being patients), with timetables".formatted(doctorsCount));

            final var doctors = generateDoctors(doctorsCount);

            // TODO: add visits from doctors who are also patients

            final var patientsCount = random.nextInt(doctorsCount * 10, doctorsCount * 30);
            logger.fine("Adding %d patients, with visits at the doctors".formatted(patientsCount));

            final var patients = new ArrayList<Patient>(patientsCount + doctorsCount);
            for (Doctor doctor : doctors) {
                final var user = doctor.asUser();
                final var patient = user.asPatient();
                if (patient != null) {
                    patients.add(patient);
                }
            }

            for (int i = 0; i <= patientsCount; i++) {
                final var patientUser = new User();
                if (1 != random.nextInt(4)) {
                    patientUser.setEmail(faker.internet().emailAddress());
                }
                patientUser.setName(faker.name().firstName());
                patientUser.setSurname(faker.name().lastName());
                if (1 != random.nextInt(10)) {
                    patientUser.setPhone(randomPhoneNumber());
                    // Note: Yes, 1 in 40 might not have both e-mail and phone.
                }
                patientUser.setRole(User.Role.PATIENT);

                final var patient = setupPatient(patientUser);

                entityManager.persist(patientUser);
                patientUser.changePassword("12345678");

                entityManager.persist(patient);
                patients.add(patient);
            }

            logger.fine("Adding timetables and appointments");

            final var receptionist = User.queryByRole(User.Role.RECEPTION).getSingleResult();

            for (Doctor doctor : doctors) {
                logger.finer("Adding stuff for %s".formatted(doctor));

                // TODO: more variation for the timetable splits
                final var doctorUser = doctor.asUser();
                doctorUser.addTimetable(generateBasicTimetable(zonedDay(-10 - random.nextInt(3))));
                if (random.nextBoolean()) {
                    doctorUser.addTimetable(generateBasicTimetable(zonedDay(-3)));
                }
                if (1 == random.nextInt(8)) {
                    doctorUser.addTimetable(generateBasicTimetable(zonedDay(4 + random.nextInt(20))));
                }
                else {
                    doctorUser.addTimetable(generateBasicTimetable(zonedDay(4 + random.nextInt(3))));
                }
                entityManager.flush(); // keep it together
                final var endDay = zonedDay(14);
                final var startDay = doctorUser.getTimetables().get(0).getEffectiveDate().plusDays(random.nextInt(3));

                final var doctorSchedule = PublicSchedule.of(doctor);
                final var openHours = doctorSchedule.generateScheduleEntriesFromTimetables(startDay, endDay);

                // Add some busy simple entries
                for (int i = 0; i < random.nextInt(3); i++) {
                    final var whyChoices = new Schedule.Entry.Type[]{
                            Schedule.Entry.Type.SICK_LEAVE,
                            Schedule.Entry.Type.EMERGENCY_LEAVE,
                            Schedule.Entry.Type.VACATION,
                    };
                    final var why = whyChoices[random.nextInt(whyChoices.length)];
                    final var hours = why == Schedule.Entry.Type.VACATION ? random.nextInt(1, 10) * 24 : 12;
                    final var beginTime = zonedDay(random.nextInt(-7, 7 + 1)).plusHours(7).toInstant();
                    final var endTime = beginTime.plus(hours, ChronoUnit.HOURS);
                    final var newEntry = new Schedule.SimpleEntry(why, beginTime, endTime);
                    if (1 == random.nextInt(4) || openHours.stream().anyMatch(e -> e.overlaps(newEntry))) {
                        if (doctorSchedule.findScheduleEntries(beginTime, endTime).anyMatch(e -> e.overlaps(newEntry))) {
                            continue;
                        }
                        newEntry.setUser(doctorUser);
                        logger.finer("Adding %s".formatted(newEntry));
                        entityManager.persist(newEntry);
                    }
                }

                // TODO: Add some extra hours, sticky to open hours

                // Fill open hours with appointments, with future being less densely further and further
                for (final var entry : openHours) {
                    var currentInstant = entry.getBeginInstant();

                    while (currentInstant.isBefore(entry.getEndInstant())) {
                        final var duration = doctor.getDefaultVisitDuration()
                                .multipliedBy(1 == random.nextInt(20) ? random.nextInt(3) : 1);

                        // Skip some part of potential slots
                        if (1 == random.nextInt(10)) {
                            currentInstant = currentInstant.plus(duration);
                            continue;
                        }

                        // More in the future, less the appointments
                        final var daysInFuture = Duration.between(now.toInstant(), currentInstant).toDays();
                        if (random.nextInt(14) < daysInFuture) {
                            currentInstant = currentInstant.plus(duration);
                            continue;
                        }

                        final var patient = patients.get(random.nextInt(patients.size()));
                        final var patientUser = patient.asUser();

                        // Prevent collisions between existing entries
                        final var collision = Schedule.of(patient)
                                .findScheduleEntries(currentInstant, currentInstant.plus(duration))
                                .anyMatch(e -> e.getType().isBusy());
                        if (collision) {
                            currentInstant = currentInstant.plus(duration);
                            continue;
                        }

                        // TODO: first visit being more likely to be added by receptionist (or user)
                        final var whoAdded = new User[]{ patientUser, doctorUser, receptionist, receptionist };
                        final var addedHoursAhead = random.nextInt(0, 3) + random.nextInt(0, 3);

                        final var appointment = new Appointment();
                        appointment.setDoctor(doctor);
                        appointment.setPatient(patient);
                        appointment.setDate(currentInstant);
                        appointment.setDuration(duration);
                        appointment.setAddedBy(whoAdded[random.nextInt(whoAdded.length)]);
                        appointment.setAddedDate(currentInstant.minus(addedHoursAhead, ChronoUnit.HOURS));
                        appointment.setNotes(String.join(" ", faker.lorem().sentences(random.nextInt(0, 4))));
                        appointment.setStringTags("");

                        logger.finer("Adding %s for patient %s".formatted(appointment, patient));
                        entityManager.persist(appointment);

                        currentInstant = currentInstant.plus(duration);
                    }
                }

                entityManager.flush();
            }

            // TODO: prescriptions, referrals, medical records, etc.
            // TODO: notifications
            // TODO: admins
        }
        catch (Throwable e) {
            transaction.rollback();
            throw e;
        }

        transaction.commit();
    }

    private static final String[] doctorSpecialities = new String[] {
            "lekarz ogólny", "pediatra", "dermatolog", "kardiolog", "okulista",
            "laryngolog", "neurolog", "psycholog", "stomatolog",
    };
    private int uniqueSpecialities = 0; // first doctors generated with `generateDoctors` will have unique specialities

    private static final String[] emailDomains = new String[] { "gmail.com", "yahoo.com", "hotmail.com" };

    /**
     * @return random email address (because faker doesn't match username to email
     */
    private String generateEmailAddress(String name, String surname) {
        final var parts = new ArrayList<>(List.of(
                1 == random.nextInt(5) ? String.valueOf(name.toLowerCase().charAt(0)) : name.toLowerCase(),
                surname.toLowerCase()
        ));
        switch (random.nextInt(10)) {
            case 1 -> parts.add(faker.number().digits(1));
            case 2 -> parts.add(faker.number().digits(2));
            case 3 -> parts.add(faker.numerify("20##"));
            case 4 -> parts.add(faker.numerify("199#"));
            case 5 -> parts.add(faker.numerify("198#"));
        }
        Collections.shuffle(parts);
        return String.join(random.nextBoolean() ? "." : "", parts)
                + "@" + emailDomains[random.nextInt(emailDomains.length)];
    }

    /**
     * @return random phone number (because faker lib provided is not quite good)
     */
    private String randomPhoneNumber() {
        if (locale.getLanguage().equals("pl")) {
            final var parts = 1 == random.nextInt(4);
            final var sb = new StringBuilder(16);
            if (1 == random.nextInt(4)) {
                sb.append("+48");
                if (parts) sb.append(' ');
            }
            sb.append(faker.number().digits(3));
            if (parts) sb.append(' ');
            sb.append(faker.number().digits(3));
            if (parts) sb.append(' ');
            sb.append(faker.number().digits(3));
            return sb.toString();
        }
        return faker.phoneNumber().cellPhone();
    }

    private String randomPESEL() {
        // TODO: https://pl.wikipedia.org/wiki/PESEL#Cyfra_kontrolna_i_sprawdzanie_poprawno%C5%9Bci_numeru
        return faker.number().digits(11);
    }

    private String randomPostCode() {
        if (locale.getLanguage().equals("pl")) {
            return faker.numerify("##-###");
        }
        return faker.address().zipCode();
    }

    private Duration randomDefaultVisitDuration() {
        return switch (random.nextInt(12 + 1)) {
            case 1, 2 -> Duration.ofMinutes(10);
            case 4, 5 -> Duration.ofMinutes(20);
            case 6, 7 -> Duration.ofMinutes(30);
            case 9    -> Duration.ofMinutes(45);
            case 12   -> Duration.ofMinutes(60);
            default   -> Duration.ofMinutes(15);
        };
    }

    private Patient setupPatient(User user) {
        final var city = faker.address().city();
        final var hasStreets = city.indexOf('a') == -1 && city.indexOf('ó') == -1;
        final var patient = new Patient(user, randomPESEL());
        patient.setCity(faker.address().city());
        if (hasStreets) {
            // TODO: fix default faker resource to have better names
            patient.setStreet(faker.address().streetAddress());
        }
        final var hasBuildingLetter = (hasStreets ? 1 : 3) < random.nextInt(8);
        final var maybeLetter = (hasBuildingLetter ? (" " + (char)('a' + random.nextInt(8))) : "");
        patient.setBuilding(random.nextInt(1, hasStreets ? 321 : 1234) + maybeLetter);
        patient.setPostCity(hasStreets || random.nextBoolean() ? city : faker.address().city());
        patient.setPostCode(randomPostCode());

        // Set predictable username for ease of debugging
        user.setDatabaseUsername("u_gp_p_" + patient.getPESEL());

        return patient;
    }

    private ArrayList<Doctor> generateDoctors(int count) {
        final var doctors = new ArrayList<Doctor>(count);
        for (int i = 1; i <= count; i++) {
            final boolean isDoctorAlsoPatient = i == 1 || 1 == random.nextInt(8);

            final var user = new User();
            user.setName(faker.name().firstName());
            user.setSurname(faker.name().lastName());
            user.setEmail(generateEmailAddress(user.getName(), user.getSurname()));
            user.setPhone(randomPhoneNumber());
            user.setRole(User.Role.DOCTOR);

            final var doctor = new Doctor(user);
            if (uniqueSpecialities < doctorSpecialities.length) {
                doctor.setSpeciality(doctorSpecialities[uniqueSpecialities++]);
                doctor.setDefaultVisitDuration(randomDefaultVisitDuration());
            }
            else {
                doctor.setSpeciality(doctorSpecialities[random.nextInt(doctorSpecialities.length)]);
            }

            Patient patient = isDoctorAlsoPatient ? setupPatient(user) : null;

            // Set predictable username for ease of debugging
            if (isDoctorAlsoPatient) {
                user.setDatabaseUsername("u_eg_dp_" + patient.getPESEL());
            }

            logger.finer("Adding doctor %d. %s speciality=%s %s".formatted(i, user, doctor.getSpeciality(),
                    isDoctorAlsoPatient ? "also patient: pesel=%s".formatted(patient.getPESEL()) : ""));

            entityManager.persist(user);
            user.changePassword("12345678");

            entityManager.persist(doctor);
            doctors.add(doctor);

            if (isDoctorAlsoPatient) {
                entityManager.persist(patient);
            }
        }
        return doctors;
    }

    private ZonedDateTime zonedDay() {
        return now.truncatedTo(ChronoUnit.DAYS);
    }
    private ZonedDateTime zonedDay(int daysOffset) {
        return now.truncatedTo(ChronoUnit.DAYS).plusDays(daysOffset);
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
