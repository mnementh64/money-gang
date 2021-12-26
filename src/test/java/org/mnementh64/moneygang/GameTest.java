package org.mnementh64.moneygang;

import org.junit.jupiter.api.Test;

class GameTest {

    public static final int NB_PLAYERS = 6;
    public static final int NB_ROUNDS = 10;
    public static final int AMOUNT_PER_ROUND_TO_LAUNDER = 1_000_000;

    @Test
    public void testGame() {
        // create the game
        Game game = new Game(
                NB_PLAYERS,
                NB_ROUNDS,
                AMOUNT_PER_ROUND_TO_LAUNDER,
                GameRules.forContext(NB_PLAYERS, AMOUNT_PER_ROUND_TO_LAUNDER));
        game.init();

        // loop through all rounds
        for (int round = 1; round <= NB_ROUNDS; round++) {
            game.playRound(round);
        }

        game.evaluateWinner();
        System.out.printf("Game's winner : %s%n", game.displayWinner());
    }
}