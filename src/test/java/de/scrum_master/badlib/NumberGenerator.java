package de.scrum_master.badlib;

public class NumberGenerator {
  public static final int SLEEP_MILLIS = 50;
  private static int currentNumber;

  // JclUtils.cast would work without interface INumberGenerator in ThreadSafeNumberSequenceGenerator.apply
  // if this class had a no-argument constructor
  // public NumberGenerator() {}

  public NumberGenerator(int initialValue) {
    currentNumber = initialValue;
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
}
