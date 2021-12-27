package org.mnementh64.moneygang;

public class GameRules {

    public static GameRules forContext(int nbPlayers, int amountPerRoundToLaunder) {
        // TODO
        return new GameRules();
    }

    public TurnResult apply(int totalAmountLaundered) {
        if (totalAmountLaundered <= 250_000) {
            return TurnResult.TOTAL_CHAOS;
        }
        if (totalAmountLaundered <= 490_000) {
            return TurnResult.SETTLING_OF_ACCOUNTS;
        }
        if (totalAmountLaundered <= 700_000) {
            return TurnResult.EXPECTED;
        }
        if (totalAmountLaundered <= 800_000) {
            return TurnResult.POLICE_NOTICED;
        }
        return TurnResult.FAR_TOO_MUCH;
    }
}
