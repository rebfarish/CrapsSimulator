package deepdive.cnm.edu.crapssimulator.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game {

  private final Object lock = new Object();

  private State state;
  private int point;
  private Random rng;
  private List<Roll> rolls;
  private int wins;
  private int losses;

  public Game(Random rng) {
    this.rng = rng;
    rolls = new LinkedList<>();
    wins = 0;
    losses = 0;
  }

  public void reset() {
    state = State.COME_OUT;
    point = 0;
    synchronized (lock) {
      rolls.clear();
    }
  }

  private State roll() {
    int[] dice = {
        1 + rng.nextInt(6),
        1 + rng.nextInt(6)
    };
    int total = dice[0] + dice[1];
    State state = this.state.roll(total, point);
    if (this.state == State.COME_OUT && state == State.POINT) {
      point = total;
    }
    this.state = state;
    synchronized (lock) {
      rolls.add(new Roll(dice, state));
    }
    return state;
  }

  public State play() {
    reset();
    while (state != State.WIN && state != State.LOSS) {
      roll();
    }
    if (state == State.WIN) {
      wins++;
    } else {
      losses++;
    }
    return state;
  }

  public State getState() {
    return state;
  }

  public List<Roll> getRolls() {
    synchronized (lock) {
      return new LinkedList<>(rolls);
    }
  }

  public int getWins() {
    return wins;
  }

  public int getLosses() {
    return losses;
  }

  public static class Roll {

    private final int[] dice;
    private final State state;

    private Roll(int[] dice, State state) {
      this.dice = Arrays.copyOf(dice, 2);
      this.state = state;
    }

    public int[] getDice() {
      return Arrays.copyOf(dice, 2);
    }

    public State getState() {
      return state;
    }

    @Override
    public String toString() {
      return String.format("%s %s%n", Arrays.toString(dice), state);
    }

  }

  public enum State {

    COME_OUT {
      @Override
      public State roll(int total, int point) {
        switch (total) {
          case 2:
          case 3:
          case 12:
            return LOSS;
          case 7:
          case 11:
            return WIN;
          default:
            return POINT;
        }
      }
    },
    WIN,
    LOSS,
    POINT {
      @Override
      public State roll(int total, int point) {
        if (total == point) {
          return WIN;
        } else if (total == 7) {
          return LOSS;
        } else {
          return POINT;
        }
      }
    };

    public State roll(int total, int point) {
      return this;
    }

  }

}
