package org.y9nba.app.base.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.y9nba.app.base.repository.BaseRepository;

import java.util.Set;


public abstract class BaseServiceImpl<R extends BaseRepository<T, ID>, T, ID> implements BaseService<T, ID> {
    public final R repository;

    public BaseServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public T findById(ID id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
                );
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public Set<T> findByUser(Long userId) {
        return null;
    }
}
