package org.mnementh64.moneygang;

import org.junit.jupiter.api.Test;

class GameTest {

    public static final int NB_PLAYERS = 6;
    public static final int NB_TURNS = 10;
    public static final int AMOUNT_PER_TURN_TO_LAUNDER = 1_000_000;

    @Test
    public void testGame() {
        // create the game
        Game game = new Game(
                NB_PLAYERS,
                NB_TURNS,
                AMOUNT_PER_TURN_TO_LAUNDER,
                GameRules.forContext(NB_PLAYERS, AMOUNT_PER_TURN_TO_LAUNDER),
                new ConsoleGameUI());
        game.init();
        game.debugPlayers(false);

        // loop through all turns
        System.out.printf("%n%n>> START GAME%n%n");
        for (int turn = 1; turn <= NB_TURNS; turn++) {
            game.playTurn(turn);
        }

        game.evaluateWinner();
        game.displayWinner();
        game.debugPlayers(true);
        System.out.printf("%n%n>> END GAME%n%n");
    }
}