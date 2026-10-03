package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.FilmError;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmErrorRepository extends JpaRepository<FilmError, Long> {


    public List<FilmError> findByIsResolvedFalse();

}
