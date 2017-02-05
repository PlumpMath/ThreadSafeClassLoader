package de.scrum_master.app;

import de.scrum_master.badlib.NumberGenerator;
import de.scrum_master.threadsafe.ThreadSafeClassLoader;

import java.util.function.Function;

import static de.scrum_master.threadsafe.ThreadSafeClassLoader.ObjectConstructionRules.forTargetType;

public class ThreadSafeNumberSequenceGenerator implements Function<NumberSequenceSettings, int[]> {
  private static ThreadLocal<ThreadSafeClassLoader> threadSafeClassLoader =
    ThreadSafeClassLoader.create(NumberGenerator.class);

  @Override
  public int[] apply(NumberSequenceSettings settings) {
    int[] numberSequence = new int[settings.getSequenceSize()];
    INumberGenerator numberGenerator = threadSafeClassLoader.get().newObject(
      forTargetType(INumberGenerator.class)
        .implementingType(NumberGenerator.class)
        .arguments(settings.getStartValue() - settings.getStepSize())
        .argumentTypes(int.class)
    );
    for (int i = 0; i < settings.getSequenceSize(); i++)
      numberSequence[i] = numberGenerator.addAndGet(settings.getStepSize());
    return numberSequence;
  }
}
