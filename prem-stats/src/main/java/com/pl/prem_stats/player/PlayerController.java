package com.pl.prem_stats.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This marks the class as a Spring MVC controller where every method returns a domain object
// instead of a view, shorthand for @Controller and @ResponseBody
@RestController
@RequestMapping(path = "api/v1/player")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired // Allows the controller to delegate the business logic back to the service layer

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping // Mapping to handle all get requests
    public List<Player> getPlayers(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String nation
    ) {
        if (team != null && position != null) {
            return playerService.getPlayersByTeamAndPosition(team, position);
        } else if (team != null) {
            return playerService.getPlayersFromTeam(team); // shorthand for return playerService.getPlayersByTeam(name);
        } else if (name != null) {
            return playerService.getPlayersByName(name);
        } else if (position != null) {
            return playerService.getPlayersByPosition(position);
        } else if (nation != null) {
            return playerService.getPlayersByNation(nation);
        } else {
            return playerService.getPlayers();
        }
    }

    @PostMapping // Add new entry to db
    public ResponseEntity<Player> addPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.addPlayer(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
    }

    @PutMapping // Update entry in db
    public ResponseEntity<Player> updatePlayer(@RequestBody Player updatedPlayer) {
        Player resultPlayer = playerService.updatePlayer(updatedPlayer);
        if (resultPlayer != null) {
            return new ResponseEntity<>(resultPlayer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{playerName}") // Delete entry from db
    public ResponseEntity<Player> deletePlayer(@PathVariable String playerName) {
        playerService.deletePlayer(playerName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
