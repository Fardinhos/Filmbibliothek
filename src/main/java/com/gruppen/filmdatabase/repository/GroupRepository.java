package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {


    List<Group> findAllByIsPrivateIsFalse();

}
