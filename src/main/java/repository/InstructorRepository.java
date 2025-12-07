package repository;

import model.Instructor;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class InstructorRepository implements Repository<Instructor, String> {
    private final InMemoryRepository<Instructor, String> repository;

    public InstructorRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(Instructor entity) {
                if (entity.getId() == null) {
                    throw new IllegalArgumentException("Entity ID cannot be null");
                }
                return entity.getId();
            }

        };
    }

    @Override
    public Optional<Instructor> findById(String s) {
        return repository.findById(s);
    }

    @Override
    public List<Instructor> findAll() {
        return repository.findAll();
    }

    @Override
    public PagedList<Instructor> findAll(int page, int pageSize) {
        return repository.findAll(page, pageSize);
    }

    @Override
    public Instructor save(Instructor entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(String s) {
        repository.deleteById(s);
    }
}
