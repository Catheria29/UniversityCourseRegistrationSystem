package model;

public final class Admin extends Person {

    public Admin(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String role() {
        return "ADMIN";
    }

    @Override
    public void displayProfile() {
        System.out.println("Admin: " + getName() + " (" + getId() + ")");
    }
}
