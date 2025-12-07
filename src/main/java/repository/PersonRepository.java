package repository;

import model.Person;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class PersonRepository implements Repository<Person, String> {
    private final InMemoryRepository<Person, String> repository;

    public PersonRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(Person entity) {
                if (entity.getId() == null) {
                    throw new IllegalArgumentException("Entity ID cannot be null");
                }
                return entity.getId();
            }
        };
    }

    @Override
    public Optional<Person> findById(String s) {
        return repository.findById(s);
    }

    @Override
    public List<Person> findAll() {
        return repository.findAll();
    }

    @Override
    public PagedList<Person> findAll(int page, int pageSize) {
        return repository.findAll(page, pageSize);
    }

    @Override
    public Person save(Person entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(String s) {
        repository.deleteById(s);
    }
}
