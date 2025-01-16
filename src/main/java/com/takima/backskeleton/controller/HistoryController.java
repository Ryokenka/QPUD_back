package com.takima.backskeleton.controller;

import com.takima.backskeleton.model.GameHistory;
import com.takima.backskeleton.repository.GameHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Simple REST controller to show how you might expose quiz or match history.
 */
@RestController
public class HistoryController {

    @Autowired
    private GameHistoryRepository gameHistoryRepo;

    @GetMapping("/history")
    public List<GameHistory> getAllHistory() {
        return gameHistoryRepo.findAll();
    }
}

