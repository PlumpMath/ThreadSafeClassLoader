package de.scrum_master.app;

public class NumberSequenceSettings {
  private final int startValue;
  private final int stepSize;
  private final int sequenceSize;

  public NumberSequenceSettings(int startValue, int stepSize, int sequenceSize) {
    this.startValue = startValue;
    this.stepSize = stepSize;
    this.sequenceSize = sequenceSize;
  }

  public int getStartValue() {
    return startValue;
  }

  public int getStepSize() {
    return stepSize;
  }

  public int getSequenceSize() {
    return sequenceSize;
  }
}
