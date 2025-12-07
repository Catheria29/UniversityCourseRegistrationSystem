package model;

import lombok.Getter;
import lombok.Setter;
import model.interfaces.Searchable;

@Getter
@Setter
public abstract class Person implements Searchable {
    private final String id;
    private String name;
    private String email;

    protected Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public abstract String role();

    public abstract void displayProfile();

    @Override
    public boolean matches(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return (id != null && id.toLowerCase().contains(q))
                || (name != null && name.toLowerCase().contains(q))
                || (email != null && email.toLowerCase().contains(q));
    }
}
