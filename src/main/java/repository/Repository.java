package repository;

import utils.PagedList;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    PagedList<T> findAll(int page, int pageSize);
    T save(T entity);
    void deleteById(ID id);
}
