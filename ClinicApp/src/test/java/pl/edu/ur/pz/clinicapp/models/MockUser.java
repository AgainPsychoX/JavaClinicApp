package pl.edu.ur.pz.clinicapp.models;

import java.util.Random;

public class MockUser extends User {
    public MockUser(Role role, String name, String surname) {
        this.setId(new Random().nextInt() % 1000);
        this.setRole(role);
        this.setName(name);
        this.setSurname(surname);
        // TODO: add nicer constructor in User, will would be useful for seeder too
    }
}
