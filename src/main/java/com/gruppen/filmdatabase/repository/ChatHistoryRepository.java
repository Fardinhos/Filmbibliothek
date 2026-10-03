package com.gruppen.filmdatabase.repository;

import com.gruppen.filmdatabase.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {


    public Optional<ChatHistory> findByFirstUserIdAndSecondUserId(Long firstUserId, Long secondUserId);


}
