package org.mnementh64.moneygang;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {
    private final int nbPlayers;
    private final int nbTurns;
    private final int amountPerTurnToLaunder;
    private final GameRules gameRules;
    private GameUI gameUI;

    private List<Player> playerList;
    private Player winner;

    private int launderedAmount;
    private int confiscatedAmount;

    public Game(int nbPlayers, int nbTurns, int amountPerTurnToLaunder, GameRules gameRules, GameUI gameUI) {
        this.nbPlayers = nbPlayers;
        this.nbTurns = nbTurns;
        this.amountPerTurnToLaunder = amountPerTurnToLaunder;
        this.gameRules = gameRules;
        this.gameUI = gameUI;
    }

    public void init() {
        // reinit some values
        this.winner = null;
        this.launderedAmount = 0;
        this.confiscatedAmount = 0;

        // create players with roles
        List<Role> roles = prepareRolesToDispatch();
        playerList = IntStream.rangeClosed(1, nbPlayers)
                .mapToObj(rank -> new Player(rank, roles))
                .collect(Collectors.toList());

        // randomly pick-up the first god fatcher
        UUID godFatherId = randomlyChoosePlayerExcluding(null);
        installGodfather(godFatherId);
        System.out.printf("God father for first turn will be : %s%n", getPlayer(godFatherId).name);

        gameUI.init();
    }

    private List<Role> prepareRolesToDispatch() {
        List<Role> rolesToDispatch = new ArrayList<>();
        rolesToDispatch.add(Role.UNDERCOVER);
        for (int i = 1; i < nbPlayers; i++) {
            rolesToDispatch.add(Role.GANGSTER);
        }
        return rolesToDispatch;
    }

    public void playTurn(int turn) {
        System.out.printf("****************** Game turn %d ******************%n", turn);

        // Godfather shares the amount for this turn
        Map<UUID, Integer> playerToTurnAmountMap = godfatcherSharesAmount();
        System.out.println("Amount shared per player :");
        playerToTurnAmountMap.forEach((uuid, amount) -> {
            System.out.printf("  - Player %s : %d%n", getPlayer(uuid), amount);
        });

        // each player decides how much they keep in their slush fund : this could be parallelized
        Map<UUID, Integer> playerToLaunderedAmountMap = new HashMap<>();
        for (Player player : playerList) {
            int launderedAmount = gameUI.letPlayerFillTheirSlushFund(player, playerToTurnAmountMap.get(player.uuid));
            playerToLaunderedAmountMap.put(player.uuid, launderedAmount);
        }
        gameUI.displayLaunderedAmounts(playerList, playerToLaunderedAmountMap);

        // total amount laundered for this turn
        int totalAmountLaunderedThisTurn = playerToLaunderedAmountMap.values().stream().mapToInt(i -> i).sum();

        // apply rules to final laundered amount for this turn
        TurnResult turnResult = gameRules.apply(totalAmountLaunderedThisTurn);
        System.out.printf("Turn result --> %s for amount laundered %d%n", turnResult, totalAmountLaunderedThisTurn);

        // manage consequences of this result
        applyTurnResult(turnResult, totalAmountLaunderedThisTurn);

        // TODO LATER : Does any player want to report a potential mole ?

        // Godfather replacement - skip at last turn
        if (turn < nbTurns) {
            manageGodfatherReplacement(turnResult);
        }

        System.out.printf("> End of Game turn %d%n", turn);
    }

    private void applyTurnResult(TurnResult turnResult, int totalAmountLaunderedThisTurn) {
        switch (turnResult) {
            case TOTAL_CHAOS -> // all players lose their slush funds
                    playerList.forEach(player -> {
                        System.out.printf("  %s reset their slush fund", player);
                        player.resetSlushFund();
                    });
            case SETTLING_OF_ACCOUNTS -> {
                // one player randomly chosen (excluding godfather) loses their slush fund
                Player player = getPlayer(randomlyChoosePlayerExcluding(getGodfather().uuid));
                System.out.printf("  %s reset their slush fund", player);
                player.resetSlushFund();

                addToLaunderedAmount(totalAmountLaunderedThisTurn);
            }
            case EXPECTED -> addToLaunderedAmount(totalAmountLaunderedThisTurn);
            case POLICE_NOTICED -> confiscateAmount(totalAmountLaunderedThisTurn);
            case FAR_TOO_MUCH -> {
                confiscateAmount(launderedAmount);
                System.out.println("  Total amount laundered reduced to zero !");
                launderedAmount = 0;
            }
        }
    }

    private void confiscateAmount(int totalAmountLaunderedThisTurn) {
        confiscatedAmount += totalAmountLaunderedThisTurn;
        System.out.printf("  Total amount confiscated added by %d to reach %d%n", totalAmountLaunderedThisTurn, confiscatedAmount);
    }

    private void addToLaunderedAmount(int totalAmountLaunderedThisTurn) {
        launderedAmount += totalAmountLaunderedThisTurn;
        System.out.printf("  Total amount laundered added by %d to reach %d%n", totalAmountLaunderedThisTurn, launderedAmount);
    }

    private void manageGodfatherReplacement(TurnResult turnResult) {
        UUID nextGodfatherId = null;
        System.out.println("God father handling ...");
        // only case godfather is not automatically dismissed
        if (TurnResult.EXPECTED.equals(turnResult)) {
            if (voteForSameGodfatherReelection()) {
                System.out.println(" - Godfather being re-conducted");
                nextGodfatherId = getGodfather().uuid;
            } else {
                System.out.println(" - Godfather not being re-conducted");
            }
        }

        // no godfather yet designed for next turn, then pick them up randomly
        if (nextGodfatherId == null) {
            System.out.println(" - Pick-up randomly next one");
            nextGodfatherId = randomlyChoosePlayerExcluding(getGodfather().uuid);
        }

        System.out.printf("--> God father for next turn will be : %s%n", getPlayer(nextGodfatherId).name);

        installGodfather(nextGodfatherId);
    }

    private void installGodfather(UUID godfatherId) {
        for (Player player : playerList) {
            player.setGodfather(player.uuid.equals(godfatherId));
        }
    }

    private UUID randomlyChoosePlayerExcluding(UUID uuidToExclude) {
        List<UUID> candidates = playerList.stream()
                .filter(p -> !p.uuid.equals(uuidToExclude)) // works even if uuidToExclude is null
                .map(p -> p.uuid)
                .collect(Collectors.toList());
        Random random = new Random(System.currentTimeMillis());

        return candidates.get(random.nextInt(candidates.size()));
    }

    private boolean voteForSameGodfatherReelection() {
        // TODO : implement a real election system
        Random random = new Random(System.currentTimeMillis());
        return random.nextDouble() > 0.5;
    }

    private Map<UUID, Integer> godfatcherSharesAmount() {
        return gameUI.godfatcherSharesAmount(playerList, amountPerTurnToLaunder);
    }

    public void evaluateWinner() {
        if (confiscatedAmount >= launderedAmount) {
            winner = playerList.stream()
                    .filter(p -> Role.UNDERCOVER.equals(p.getRole()))
                    .findFirst()
                    .orElseThrow(() -> new UnsupportedOperationException("We expect one player to be undercover !"));
        } else {
            winner = playerList.stream()
                    .max(Comparator.comparing(Player::getSlushFund))
                    .orElseThrow(() -> new UnsupportedOperationException("We expect one player to have the biggest slush fund !"));
        }
    }

    public void displayWinner() {
        gameUI.displayWinner(launderedAmount, confiscatedAmount, winner);
    }

    public void debugPlayers(boolean withSlushFund) {
        gameUI.displayPlayers(playerList, withSlushFund);
    }

    private Player getPlayer(UUID uuid) {
        return playerList.stream()
                .filter(p -> p.uuid.equals(uuid))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(String.format("Could not work without a player with ID %s", uuid)));
    }

    private Player getGodfather() {
        return playerList.stream()
                .filter(Player::isGodfather)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Could not work fine without a godfather !!"));
    }
}
