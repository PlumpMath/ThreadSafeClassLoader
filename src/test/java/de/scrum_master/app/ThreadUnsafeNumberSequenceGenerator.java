package de.scrum_master.app;

import de.scrum_master.badlib.NumberGenerator;

import java.util.function.Function;

public class ThreadUnsafeNumberSequenceGenerator implements Function<NumberSequenceSettings, int[]> {
  @Override
  public int[] apply(NumberSequenceSettings settings) {
    int[] numberSequence = new int[settings.getSequenceSize()];
    NumberGenerator numberGenerator = new NumberGenerator(settings.getStartValue() - settings.getStepSize());
    for (int i = 0; i < settings.getSequenceSize(); i++)
      numberSequence[i] = numberGenerator.addAndGet(settings.getStepSize());
    return numberSequence;
  }
}
