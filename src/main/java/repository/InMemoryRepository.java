package repository;

import utils.PagedList;

import java.util.*;

public abstract class InMemoryRepository<T, ID> implements Repository<T, ID> {

    protected final Map<ID, T> store = new HashMap<>();


    protected InMemoryRepository() {

    }

    @Override
    public Optional<T> findById(ID id) {
        if (!store.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(store.get(id));
    }

    @Override
    public PagedList<T> findAll(int page, int pageSize) {
        int totalCount = store.size();
        return new PagedList<>(new ArrayList<>(store.values()), page, pageSize, totalCount);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public T save(T entity) {
        if (entity == null) return null;
        store.put(getId(entity), entity);
        return entity;
    }

    @Override
    public void deleteById(ID id) {
        store.remove(id);
    }


    protected abstract ID getId(T entity);

}
