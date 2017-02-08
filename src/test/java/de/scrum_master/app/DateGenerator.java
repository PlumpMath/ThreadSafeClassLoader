package de.scrum_master.app;

import de.scrum_master.badlib.DateParser;
import de.scrum_master.thread_safe.ThreadSafeClassLoader;

import java.text.ParseException;
import java.util.Date;
import java.util.function.Function;

import static de.scrum_master.thread_safe.ThreadSafeClassLoader.ObjectConstructionRules.forTargetType;

public class DateGenerator implements Function<String[], Date> {
  public static boolean threadSafeMode = true;

  private static ThreadLocal<ThreadSafeClassLoader> threadSafeClassLoader =
    ThreadSafeClassLoader.create(DateParser.class);

  @Override
  public Date apply(String[] dateAndPattern) {
    DateParser dateParser = threadSafeMode
      ? threadSafeClassLoader.get().newObject(forTargetType(DateParser.class))
      : new DateParser();
    try {
      return dateParser.toDate(dateAndPattern[0], dateAndPattern[1]);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
