package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.FilmRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRatingRepository extends JpaRepository<FilmRating, Long> {


    List<FilmRating> findByUserId(Long userId);

    List<FilmRating> findFilmRatingByFilmId(Long filmId);

    List<FilmRating> findByFilmIdIn(List<Long> filmIds);



    List<FilmRating> findByFilmCategoryIsContaining(String category);
}
