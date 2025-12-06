package repository;

import model.AdminActionLog;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class AdminActionLogRepository implements Repository<AdminActionLog, String> {
    private final InMemoryRepository<AdminActionLog, String> repository;

    public AdminActionLogRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(AdminActionLog entity) {
                if (entity.getId() == null){
                    throw new IllegalArgumentException("Entity ID cannot be null");
                }
                return entity.getId();
            }

        };
    }

    @Override public Optional<AdminActionLog> findById(String s) { return repository.findById(s); }
    @Override public List<AdminActionLog> findAll() { return repository.findAll(); }
    @Override public PagedList<AdminActionLog> findAll(int page, int pageSize) { return repository.findAll(page, pageSize); }
    @Override public AdminActionLog save(AdminActionLog entity) { return repository.save(entity); }
    @Override public void deleteById(String s) { repository.deleteById(s); }
}
