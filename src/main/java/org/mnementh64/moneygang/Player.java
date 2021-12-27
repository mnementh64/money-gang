package org.mnementh64.moneygang;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Player {
    private static final Random random = new Random(System.currentTimeMillis());

    public final UUID uuid;
    public final String name;
    private final int rank;
    private final Role role;
    private int slushFund;
    private boolean godfather;

    public Player(int rank, List<Role> rolesToDispatch) {
        this.uuid = UUID.randomUUID();
        this.rank = rank;
        this.name = String.format("Player %d", rank);
        this.role = rolesToDispatch.remove(random.nextInt(rolesToDispatch.size()));
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", role=" + role +
                (godfather ? ", godfather" : "") +
                '}';
    }

    public void addToSlushFund(int amount) {
        slushFund += amount;
    }

    public void resetSlushFund() {
        slushFund = 0;
    }

    public Role getRole() {
        return role;
    }

    public boolean isGodfather() {
        return godfather;
    }

    public void setGodfather(boolean godfather) {
        this.godfather = godfather;
    }

    public int getSlushFund() {
        return slushFund;
    }
}
