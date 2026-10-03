package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.FilmInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmInvitationRepository extends JpaRepository<FilmInvitation, Long> {

    List<FilmInvitation> findByInvitedUserId(Long userId);
    List<FilmInvitation> findByInvitingUserId(Long userId);

}
