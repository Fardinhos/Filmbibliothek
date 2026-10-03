package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT x FROM User x WHERE x.email = ?1")
    User findByEmail(String email);


    @Query(value = "SELECT * FROM tbl_user u "
            + "WHERE"
            + " ?1 IS NULL"
            + " OR u.first_name REGEXP ?1"
            + " OR u.email REGEXP ?1"
            + " OR u.last_name REGEXP ?1",
            nativeQuery = true)
    List<User> filterBySearchString(String searchString);

    List<User> findByIsAdmin(boolean isAdmin);

}
