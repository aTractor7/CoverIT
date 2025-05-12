package com.example.GuitarApp.services;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {

    List<T> findPage(int page, int pageSize, Optional<String> sortField);

    T findOne(int id);

    T create(T entity);

    T update(int id, T updatedEntity);

    void delete(int id);
}
