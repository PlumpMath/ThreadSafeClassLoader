package de.scrum_master.badlib;

public class NumberGenerator {
  public static final int SLEEP_MILLIS = 50;
  private static int currentNumber;

  public NumberGenerator() {}

  public NumberGenerator(int initialValue) {
    currentNumber = initialValue;
  }

  public NumberGenerator(String initialValue) {
    this(Integer.parseInt(initialValue));
  }

  public int addAndGet(int summand) {
    currentNumber += summand;
    try {
      Thread.sleep(SLEEP_MILLIS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return currentNumber;
  }

  public static NumberGenerator create(Integer initialValue) {
    return new NumberGenerator(initialValue);
  }

  public static NumberGenerator create() {
    return new NumberGenerator();
  }
}
