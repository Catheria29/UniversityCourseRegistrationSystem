package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Person {
    private final String id;
    private String name;
    private String email;
    protected Person(String id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }

    public abstract String role();

    public abstract void displayProfile();

}
