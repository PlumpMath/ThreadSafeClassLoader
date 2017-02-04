package de.scrum_master.app;

import de.scrum_master.badlib.NumberGenerator;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.function.Function;

public class ThreadSafeNumberSequenceGenerator implements Function<NumberSequenceSettings, int[]> {

  private static ThreadLocal<JarClassLoader> jarClassLoader = ThreadLocal.withInitial(() -> {
    JarClassLoader jarCL = new JarClassLoader();
    URL location = NumberGenerator.class.getProtectionDomain().getCodeSource().getLocation();
    System.out.println(location);
    jarCL.add(location);
    return jarCL;
  });

  private JclObjectFactory objectFactory = JclObjectFactory.getInstance();

  @Override
  public int[] apply(NumberSequenceSettings settings) {
    System.out.println(Thread.currentThread());
    int[] numberSequence = new int[settings.getSequenceSize()];
    Object numberGenerator = objectFactory.create(
      jarClassLoader.get(),
      NumberGenerator.class.getName(),
      new Object[] { settings.getStartValue() - settings.getStepSize() },
      new Class[] { int.class }
    );
    try {
      Method addAndGet = numberGenerator.getClass().getMethod("addAndGet", int.class);
      for (int i = 0; i < settings.getSequenceSize(); i++)
        numberSequence[i] = (int) addAndGet.invoke(numberGenerator, settings.getStepSize());
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
    return numberSequence;
  }
}
