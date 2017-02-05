package de.scrum_master.threadsafe;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadSafeClassLoader extends JarClassLoader {
  private static final JclObjectFactory OBJECT_FACTORY = JclObjectFactory.getInstance();

  private final List<Class> classes = new ArrayList<>();

  public static ThreadLocal<ThreadSafeClassLoader> create(Class... classes) {
    return ThreadLocal.withInitial(
      () -> new ThreadSafeClassLoader(classes)
    );
  }

  private ThreadSafeClassLoader(Class... classes) {
    super();
    this.classes.addAll(Arrays.asList(classes));
    for (Class clazz : classes)
      add(clazz.getProtectionDomain().getCodeSource().getLocation());
  }

  public Object newObject(Class clazz, Class[] argumentTypes, Object... arguments) {
    if (!classes.contains(clazz))
      throw new IllegalArgumentException("Class " + clazz + " is not protected by this thread-safe classloader");
    return OBJECT_FACTORY.create(this, clazz.getName(), arguments, argumentTypes);
  }

  public Object invokeMethod(Object targetObject, String methodName, Class[] argumentTypes, Object... arguments) {
    Class<?> clazz = targetObject.getClass();
    try {
      Method method = clazz.getMethod(methodName, argumentTypes);
      return method.invoke(targetObject, arguments);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
