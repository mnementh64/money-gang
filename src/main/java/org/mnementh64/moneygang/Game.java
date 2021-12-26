package org.mnementh64.moneygang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {
    private final int nbPlayers;
    private final int nbRounds;
    private final int amountPerRoundToLaunder;
    private final GameRules gameRules;

    private List<Player> playerList;
    private Player winner;

    public Game(int nbPlayers, int nbRounds, int amountPerRoundToLaunder, GameRules gameRules) {
        this.nbPlayers = nbPlayers;
        this.nbRounds = nbRounds;
        this.amountPerRoundToLaunder = amountPerRoundToLaunder;
        this.gameRules = gameRules;
    }


    public void init() {
        this.winner = null;
        List<Role> roles = prepareRolesToDispatch();
        playerList = IntStream.rangeClosed(1, nbPlayers)
                .mapToObj(rank -> new Player(rank, roles))
                .peek(System.out::println)
                .collect(Collectors.toList());
    }

    private List<Role> prepareRolesToDispatch() {
        List<Role> rolesToDispatch = new ArrayList<>();
        rolesToDispatch.add(Role.GODFATHER);
        rolesToDispatch.add(Role.UNDERCOVER);
        for (int i = 2; i < nbPlayers; i++) {
            rolesToDispatch.add(Role.GANGSTER);
        }
        return rolesToDispatch;
    }

    public void playRound(int round) {
        System.out.printf("****************** Game round %d ******************%n", round);


        System.out.printf("> End of Game round %d%n", round);
    }

    public void evaluateWinner() {
        // FIXME
        winner = playerList.get(0);
    }

    public String displayWinner() {
        return String.format("The winner is %s", winner);
    }
}
