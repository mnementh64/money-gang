package org.mnementh64.moneygang;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConsoleGameUI extends GameUI {

    @Override
    public void init() {
        // nothing specific
    }

    @Override
    public void displayPlayers(List<Player> playerList, boolean withSlushFund) {
        System.out.println("Players list :");
        playerList.forEach(player -> System.out.printf("  - %s%s%n", player, (withSlushFund ? " with slush fund " + player.getSlushFund() : "")));
    }

    @Override
    public Map<UUID, Integer> godfatcherSharesAmount(List<Player> playerList, int amountPerTurnToLaunder) {
        int equalAmount = amountPerTurnToLaunder / playerList.size();
        return playerList.stream()
                .collect(Collectors.toMap(
                        player -> player.uuid,
                        player -> equalAmount
                ));
    }

    @Override
    public int letPlayerFillTheirSlushFund(Player player, Integer amountToLaunder) {
        final int amountForSlushFund;
        if (player.isGodfather()) {
            amountForSlushFund = amountToLaunder;
        } else if (Role.UNDERCOVER.equals(player.getRole())) {
            amountForSlushFund = 0;
        } else {
            amountForSlushFund = amountToLaunder / 2;
        }
        // half to slush fund
        player.addToSlushFund(amountForSlushFund);
        return amountToLaunder - amountForSlushFund;
    }

    @Override
    public void displayLaunderedAmounts(List<Player> playerList, Map<UUID, Integer> playerToLaunderedAmountMap) {
        System.out.println("Amounts laundered by each player :");
        playerList.forEach(player ->
                System.out.printf("  - Player %s : %d%n", player.name, playerToLaunderedAmountMap.get(player.uuid)));
    }

    @Override
    public void displayWinner(int launderedAmount, int confiscatedAmount, Player winner) {
        System.out.printf("%n%nEnd of game situation%n%n");
        System.out.printf("Amount laundered   : %d%n", launderedAmount);
        System.out.printf("Amount confiscated : %d%n", confiscatedAmount);
        System.out.printf("%nWinner is %s with a slush fund of %d%n%n%n", winner, winner.getSlushFund());
    }
}
