package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.FilmStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilmStatisticRepository extends JpaRepository<FilmStatistic, Long> {

    Optional<FilmStatistic> findByFilmId(Long filmId);
}
