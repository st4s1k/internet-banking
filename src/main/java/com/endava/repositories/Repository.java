package com.endava.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    Optional<T> findById(Long id);

    List<T> findAll();

    boolean save(T o) throws SQLException;

    boolean remove(T o);
}
