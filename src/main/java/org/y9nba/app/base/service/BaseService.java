package org.y9nba.app.base.service;

import java.util.Set;

public interface BaseService<T, ID> {
    T save(T entity);
    void delete(T entity);
    void deleteById(ID id);
    T findById(ID id);
    boolean existsById(ID id);
    Set<T> findByUser(Long userId);
}
