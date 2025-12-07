package repository;

import model.Admin;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class AdminRepository implements Repository<Admin, String> {
    private final InMemoryRepository<Admin, String> repository;

    public AdminRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(Admin entity) {
                if (entity.getId() == null) {
                    throw new IllegalArgumentException("Entity ID cannot be null");
                }
                return entity.getId();
            }


        };
    }

    @Override
    public Optional<Admin> findById(String s) {
        return repository.findById(s);
    }

    @Override
    public List<Admin> findAll() {
        return repository.findAll();
    }

    @Override
    public PagedList<Admin> findAll(int page, int pageSize) {
        return repository.findAll(page, pageSize);
    }

    @Override
    public Admin save(Admin entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(String s) {
        repository.deleteById(s);
    }
}
