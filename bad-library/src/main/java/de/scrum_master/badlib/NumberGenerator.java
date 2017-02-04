package de.scrum_master.badlib;

public class NumberGenerator {
  private static int currentNumber;

  public NumberGenerator(int initialValue) {
    currentNumber = initialValue;
  }

  public int addAndGet(int summand) {
    currentNumber += summand;
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return currentNumber;
  }
}
