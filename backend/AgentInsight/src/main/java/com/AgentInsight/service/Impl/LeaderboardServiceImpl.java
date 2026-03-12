package com.AgentInsight.service.Impl;

import com.AgentInsight.dto.LeaderboardDto;
import com.AgentInsight.entity.Leaderboard;
import com.AgentInsight.entity.Sales;
import com.AgentInsight.entity.Users;
import com.AgentInsight.repository.LeaderboardRepository;
import com.AgentInsight.repository.SalesRepository;
import com.AgentInsight.repository.UserRepository;
import com.AgentInsight.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardServiceImpl.class);

    private final LeaderboardRepository leaderboardRepository;
    private final SalesRepository salesRepository;
    private final UserRepository userRepository;

    public LeaderboardServiceImpl(LeaderboardRepository leaderboardRepository,
                                  SalesRepository salesRepository,
                                  UserRepository userRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.salesRepository = salesRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<LeaderboardDto> calculateLeaderboard() {
        logger.info("Initiating leaderboard calculation process...");

        List<Sales> allSales = salesRepository.findAll();

        if (allSales.isEmpty()) {
            logger.warn("No sales data found to calculate leaderboard.");
            return Collections.emptyList();
        }

        Map<Users, Double> salesByAgent = allSales.stream()
                .filter(s -> s.getAgent() != null && s.getSaleamount() != null)
                .collect(Collectors.groupingBy(
                        Sales::getAgent,
                        Collectors.summingDouble(Sales::getSaleamount)
                ));

        List<Map.Entry<Users, Double>> sortedSales = salesByAgent.entrySet().stream()
                .sorted(Map.Entry.<Users, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        leaderboardRepository.deleteAllInBatch();
        List<Leaderboard> newEntries = new ArrayList<>();
        int currentRank = 1;

        for (Map.Entry<Users, Double> entry : sortedSales) {
            Leaderboard lb = new Leaderboard();
            lb.setEntryId("LB-" + UUID.randomUUID().toString().substring(0, 8));
            lb.setAgent(entry.getKey());
            lb.setTotalSales(entry.getValue());
            lb.setRank(currentRank++);
            lb.setLastUpdated(new Date());
            newEntries.add(lb);
        }

        List<Leaderboard> savedEntries = leaderboardRepository.saveAllAndFlush(newEntries);

        return savedEntries.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<LeaderboardDto> getLeaderboard() {
        logger.info("Getting leaderboard data");
        return leaderboardRepository.findAllByOrderByRankAsc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaderboardDto updateLeaderboardEntry(String entryId, Leaderboard updated) {
        logger.info("updating leaderboard data");
        Leaderboard lb = leaderboardRepository.findByEntryId(entryId)
                .orElseThrow(() -> new RuntimeException("Entry not found: " + entryId));

        lb.setRank(updated.getRank());
        lb.setTotalSales(updated.getTotalSales());
        lb.setLastUpdated(new Date());

        return mapToDto(leaderboardRepository.saveAndFlush(lb));
    }


    private LeaderboardDto mapToDto(Leaderboard l) {
        return new LeaderboardDto(
                l.getEntryId(),
                l.getAgent() != null ? l.getAgent().getAgentid() : null,
                l.getRank(),
                l.getTotalSales(),
                l.getAgent() != null ? l.getAgent().getName() : "Unknown Agent"
        );
    }
}