package repository;

import model.Section;
import utils.PagedList;

import java.util.List;
import java.util.Optional;

public class SectionRepository implements Repository<Section, String> {
    private final InMemoryRepository<Section, String> repository;

    public SectionRepository() {
        repository = new InMemoryRepository<>() {
            @Override
            protected String getId(Section entity) {
                return entity.getId();
            }


        };
    }

    @Override public Optional<Section> findById(String s) { return repository.findById(s); }
    @Override public List<Section> findAll() { return repository.findAll(); }
    @Override public PagedList<Section> findAll(int page, int pageSize) { return repository.findAll(page, pageSize); }
    @Override public Section save(Section entity) { return repository.save(entity); }
    @Override public void deleteById(String s) { repository.deleteById(s); }
}

