package de.scrum_master.app;

import de.scrum_master.badlib.NumberGenerator;
import de.scrum_master.threadsafe.ThreadSafeClassLoader;

import java.util.function.Function;

public class ThreadSafeNumberSequenceGenerator implements Function<NumberSequenceSettings, int[]> {

  private static ThreadLocal<ThreadSafeClassLoader> threadSafeClassLoader =
    ThreadSafeClassLoader.create(NumberGenerator.class);

  @Override
  public int[] apply(NumberSequenceSettings settings) {
    System.out.println(Thread.currentThread());
    ThreadSafeClassLoader classLoader = ThreadSafeNumberSequenceGenerator.threadSafeClassLoader.get();
    int[] numberSequence = new int[settings.getSequenceSize()];

    Object numberGenerator = classLoader.newObject(
      NumberGenerator.class, new Class[] { int.class },
      settings.getStartValue() - settings.getStepSize()
    );
    for (int i = 0; i < settings.getSequenceSize(); i++)
      numberSequence[i] = (int) classLoader.invokeMethod(
        numberGenerator, "addAndGet", new Class[] { int.class },
        settings.getStepSize()
      );

    return numberSequence;
  }
}
