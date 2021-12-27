package org.mnementh64.moneygang;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class GameUI {

    public abstract void displayWinner(int launderedAmount, int confiscatedAmount, Player winner);

    public abstract void displayPlayers(List<Player> playerList, boolean withSlushFund);

    public abstract void init();

    public abstract Map<UUID, Integer> godfatcherSharesAmount(List<Player> playerList, int amountPerTurnToLaunder);

    public abstract int letPlayerFillTheirSlushFund(Player player, Integer amountToLaunder);

    public abstract void displayLaunderedAmounts(List<Player> playerList, Map<UUID, Integer> playerToLaunderedAmountMap);
}
