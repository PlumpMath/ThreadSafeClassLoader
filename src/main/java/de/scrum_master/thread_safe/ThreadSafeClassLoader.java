package de.scrum_master.thread_safe;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.JclUtils;
import org.xeustechnologies.jcl.proxy.CglibProxyProvider;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadSafeClassLoader extends JarClassLoader {
  private static final JclObjectFactory OBJECT_FACTORY = JclObjectFactory.getInstance();

  static {
    ProxyProviderFactory.setDefaultProxyProvider(new CglibProxyProvider());
  }

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

  public <T> T newObject(ObjectConstructionRules rules) {
    rules.validate(classes);
    Class<T> castTo = rules.targetType;
    return JclUtils.cast(createObject(rules), castTo, castTo.getClassLoader());
  }

  private Object createObject(ObjectConstructionRules rules) {
    String className = rules.implementingType.getName();
    String factoryMethod = rules.factoryMethod;
    Object[] arguments = rules.arguments;
    Class[] argumentTypes = rules.argumentTypes;
    if (factoryMethod == null) {
      if (argumentTypes == null)
        return OBJECT_FACTORY.create(this, className, arguments);
      else
        return OBJECT_FACTORY.create(this, className, arguments, argumentTypes);
    } else {
      if (argumentTypes == null)
        return OBJECT_FACTORY.create(this, className, factoryMethod, arguments);
      else
        return OBJECT_FACTORY.create(this, className, factoryMethod, arguments, argumentTypes);
    }
  }

  public static class ObjectConstructionRules {
    private Class targetType;
    private Class implementingType;
    private String factoryMethod;
    private Object[] arguments;
    private Class[] argumentTypes;

    private ObjectConstructionRules(Class targetType) {
      this.targetType = targetType;
    }

    public static ObjectConstructionRules forTargetType(Class targetType) {
      return new ObjectConstructionRules(targetType);
    }

    public ObjectConstructionRules implementingType(Class implementingType) {
      this.implementingType = implementingType;
      return this;
    }

    public ObjectConstructionRules factoryMethod(String factoryMethod) {
      this.factoryMethod = factoryMethod;
      return this;
    }

    public ObjectConstructionRules arguments(Object... arguments) {
      this.arguments = arguments;
      return this;
    }

    public ObjectConstructionRules argumentTypes(Class... argumentTypes) {
      this.argumentTypes = argumentTypes;
      return this;
    }

    private void validate(List<Class> classes) {
      if (implementingType == null)
        implementingType = targetType;
      if (!classes.contains(implementingType))
        throw new IllegalArgumentException(
          "Class " + implementingType.getName() + " is not protected by this thread-safe classloader"
        );
    }
  }
}
