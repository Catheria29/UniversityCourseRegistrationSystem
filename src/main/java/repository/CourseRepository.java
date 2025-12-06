package repository;

import model.Course;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class CourseRepository implements Repository<Course, String> {
    private final InMemoryRepository<Course, String> repository;

    public CourseRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(Course entity) {
                return entity.getCode();
            }
        };
    }

    @Override public Optional<Course> findById(String s) { return repository.findById(s); }
    @Override public List<Course> findAll() { return repository.findAll(); }
    @Override public PagedList<Course> findAll(int page, int pageSize) { return repository.findAll(page, pageSize); }
    @Override public Course save(Course entity) { return repository.save(entity); }
    @Override public void deleteById(String s) { repository.deleteById(s); }
}
