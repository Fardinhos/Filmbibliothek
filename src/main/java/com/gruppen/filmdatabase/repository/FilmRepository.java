package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {


    @Query(value = "SELECT * FROM tbl_films f "
            + "WHERE (?1 IS NULL OR f.name REGEXP ?1) "
            + "AND  (?2 IS NULL OR f.category REGEXP ?2) "
            + "AND (?3 IS NULL OR f.cast REGEXP ?3) "
            + "AND (?4 IS NULL OR f.pub_date = ?4) "
            , nativeQuery = true)
    List<Film> filterByParams(String name, String category, String cast, String pubDate);


    @Query(value = "SELECT * FROM tbl_films f "
            + "WHERE (?1 IS NULL OR f.category LIKE ?1) "
            , nativeQuery = true)
    List<Film> filterFilmByCategory(String category);


    List<Film> findByCategoryIsContainingIgnoreCase(String category);

    @Query(value = "Select * from tbl_films c where c.category like %:category%", nativeQuery = true)
    List<Film> findByCategoryCustom(String category);

}
