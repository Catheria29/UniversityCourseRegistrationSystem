package repository;

import model.Student;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class StudentRepository implements Repository<Student, String> {
    private final InMemoryRepository<Student, String> repository;

    public StudentRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(Student entity) {
                if (entity.getId() == null) {
                    throw new IllegalArgumentException("Entity ID cannot be null");
                }
                return entity.getId();
            }
        };
    }

    @Override
    public Optional<Student> findById(String s) {
        return repository.findById(s);
    }

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public PagedList<Student> findAll(int page, int pageSize) {
        return repository.findAll(page, pageSize);
    }

    @Override
    public Student save(Student entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(String s) {
        repository.deleteById(s);
    }
}
