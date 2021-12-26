package org.mnementh64.moneygang;

import java.util.List;
import java.util.Random;

public class Player {
    private static final Random random = new Random(System.currentTimeMillis());

    public final String name;
    private final int rank;
    private final Role role;

    public Player(int rank, List<Role> rolesToDispatch) {
        this.rank = rank;
        this.name = String.format("Player %d", rank);
        this.role = rolesToDispatch.remove(random.nextInt(rolesToDispatch.size()));
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
