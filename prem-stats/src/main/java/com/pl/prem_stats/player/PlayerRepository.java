package com.pl.prem_stats.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
    void deleteByName(String playerName); // method to delete player by their name
    // method to find the player by their name and optional is used in cases
    // where player doesnt exist
    Optional<Player> findByName(String name);
}