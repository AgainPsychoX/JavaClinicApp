package pl.edu.ur.pz.clinicapp.models;

public class User {
    public enum Role {
        ANONYMOUS,
        PATIENT,
        RECEPTIONIST,
        NURSE,
        DOCTOR,
        ADMIN;

        public boolean isGroupUser() {
            return this == RECEPTIONIST || this == NURSE;
        }

        public String toString() {
            // TODO: when we have localization it will look much nicer
            if (this == PATIENT)      return "Pacjent";
            if (this == RECEPTIONIST) return "Recepcja";
            if (this == NURSE)        return "Pielęgniarka";
            if (this == DOCTOR)       return "Lekarz";
            if (this == ADMIN)        return "Administrator";
            return this.name();
        }
    }

    public Role role;
    public String email;

    public String pesel;
    public String name;
    public String surname;



    static public User authorize(String emailOrPESEL, String password) {
        // TODO: actual authentication
        if (password.equals("asdf1234")) {
            if (emailOrPESEL.equals(MockPatient.email) || emailOrPESEL.equals(MockPatient.pesel)) return MockPatient;
            if (emailOrPESEL.equals(MockReceptionist.email)) return MockReceptionist;
            if (emailOrPESEL.equals(MockNurses.email)) return MockNurses;
            if (emailOrPESEL.equals(MockDoctor.email)) return MockDoctor;
            if (emailOrPESEL.equals(MockAdmin.email)) return MockAdmin;
        }
        return null;
    }



    /* * * * * * * * * * * * * * * * * * * * *
     * Mocks for testing and development
     */

    public static User MockPatient = new User() {{
        role = Role.PATIENT;
        email = "anna.nowak.123@example.com";
        pesel = "99032301234";
        name = "Anna";
        surname = "Nowak";
    }};
    public static User MockReceptionist = new User() {{
        role = Role.RECEPTIONIST;
        email = "reception@example.com";
        // No name as it's shared account (at least for now)
    }};
    public static User MockNurses = new User() {{
        role = Role.NURSE;
        email = "nurses@example.com";
        // No name as it's shared account (at least for now)
    }};
    public static User MockDoctor = new User() {{
        role = Role.DOCTOR;
        email = "k.malogrodzka@example.com";
        name = "Katarzyna";
        surname = "Małogrodzka-Sylwester";
    }};
    public static User MockAdmin = new User() {{
        role = Role.ADMIN;
        email = "admin@example.com";
        name = "Jan";
        surname = "Kowalski";
    }};
}
