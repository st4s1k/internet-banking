package com.endava.repositories;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    Optional<T> findById(Long id);

    List<T> findAll();

    boolean save(T o);

    boolean remove(T o);
}
