package repository;

import model.Enrollment;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class EnrollmentRepository implements Repository<Enrollment, String> {
    private final InMemoryRepository<Enrollment, String> repository;

    public EnrollmentRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(Enrollment entity) {
                if (entity.getId() == null){
                    throw new IllegalArgumentException("Entity ID cannot be null");
                }
                return entity.getId();
            }


        };
    }

    @Override public Optional<Enrollment> findById(String s) { return repository.findById(s); }
    @Override public List<Enrollment> findAll() { return repository.findAll(); }
    @Override public PagedList<Enrollment> findAll(int page, int pageSize) { return repository.findAll(page, pageSize); }
    @Override public Enrollment save(Enrollment entity) { return repository.save(entity); }
    @Override public void deleteById(String s) { repository.deleteById(s); }
}
