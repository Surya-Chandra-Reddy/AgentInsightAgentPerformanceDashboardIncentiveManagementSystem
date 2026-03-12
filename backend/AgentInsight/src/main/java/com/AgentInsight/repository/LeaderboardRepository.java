package com.AgentInsight.repository;

import com.AgentInsight.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    List<Leaderboard> findAllByOrderByRankAsc();
    Optional<Leaderboard> findByEntryId(String entryId);
}