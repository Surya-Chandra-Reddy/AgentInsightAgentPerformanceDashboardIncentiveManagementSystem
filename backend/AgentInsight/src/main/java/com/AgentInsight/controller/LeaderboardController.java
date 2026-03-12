package com.AgentInsight.controller;

import com.AgentInsight.dto.LeaderboardDto;
import com.AgentInsight.entity.Leaderboard;
import com.AgentInsight.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaderboardController {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardDto>> getLeaderboard() {
        logger.info("REST request to fetch the current leaderboard data");
        List<LeaderboardDto> list = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/calculateLeaderboard")
    public ResponseEntity<List<LeaderboardDto>> calculateLeaderboard() {
        logger.info("Calculating leaderboard data...");
        List<LeaderboardDto> refreshedData = leaderboardService.calculateLeaderboard();
        return ResponseEntity.ok(refreshedData);
    }

    @PutMapping("/{entryId}")
    public ResponseEntity<LeaderboardDto> update(@PathVariable String entryId, @RequestBody Leaderboard lb) {
        logger.info("Updating leaderboard entry with id: {}", entryId);
        LeaderboardDto updated = leaderboardService.updateLeaderboardEntry(entryId, lb);
        return ResponseEntity.ok(updated);
    }


}