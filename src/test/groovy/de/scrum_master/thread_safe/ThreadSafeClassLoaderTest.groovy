package de.scrum_master.thread_safe

import de.scrum_master.app.INumberGenerator
import de.scrum_master.badlib.NumberGenerator
import spock.lang.Specification

import static de.scrum_master.thread_safe.ThreadSafeClassLoader.ObjectConstructionRules.forTargetType

class ThreadSafeClassLoaderTest extends Specification {
  def "Use constructor + arguments + interface"() {
    given:
    def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

    when:
    INumberGenerator numberGenerator = threadSafeClassLoader.get().newObject(
      forTargetType(INumberGenerator)
      .implementingType(NumberGenerator)
      .arguments("3")
    )

    then:
    numberGenerator.addAndGet(11) == 14
    numberGenerator.addAndGet(9) == 23

    cleanup:
    threadSafeClassLoader.remove()
  }

  def "Use constructor + arguments + types + interface"() {
    given:
    def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

    when:
    INumberGenerator numberGenerator = threadSafeClassLoader.get().newObject(
      forTargetType(INumberGenerator)
      .implementingType(NumberGenerator)
      .arguments(3)
      .argumentTypes(int.class)
    )

    then:
    numberGenerator.addAndGet(11) == 14
    numberGenerator.addAndGet(9) == 23

    cleanup:
    threadSafeClassLoader.remove()
  }

  def "Use factory method + arguments + no interface"() {
    given:
    def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

    when:
    NumberGenerator numberGenerator = threadSafeClassLoader.get().newObject(
      forTargetType(NumberGenerator)
        .factoryMethod("create")
        .arguments(3)
    )

    then:
    numberGenerator.addAndGet(11) == 14
    numberGenerator.addAndGet(3) == 17

    cleanup:
    threadSafeClassLoader.remove()
  }

  def "Use factory method + arguments + types + no interface"() {
    given:
    def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

    when:
    NumberGenerator numberGenerator = threadSafeClassLoader.get().newObject(
      forTargetType(NumberGenerator)
        .factoryMethod("create")
        .arguments(3)
        .argumentTypes(Integer)
    )

    then:
    numberGenerator.addAndGet(11) == 14
    numberGenerator.addAndGet(3) == 17

    cleanup:
    threadSafeClassLoader.remove()
  }

  def "Use factory method + arguments + types + interface"() {
    given:
    def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

    when:
    INumberGenerator numberGenerator = threadSafeClassLoader.get().newObject(
      forTargetType(INumberGenerator)
        .implementingType(NumberGenerator)
        .factoryMethod("create")
        .arguments(3)
        .argumentTypes(Integer.class)
    )

    then:
    numberGenerator.addAndGet(11) == 14
    numberGenerator.addAndGet(3) == 17

    cleanup:
    threadSafeClassLoader.remove()
  }

  def "Use factory method + no arguments + interface"() {
    given:
    def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

    when:
    INumberGenerator numberGenerator = threadSafeClassLoader.get().newObject(
      forTargetType(INumberGenerator)
        .implementingType(NumberGenerator)
        .factoryMethod("create")
    )

    then:
    numberGenerator.addAndGet(11) == 11
    numberGenerator.addAndGet(3) == 14

    cleanup:
    threadSafeClassLoader.remove()
  }

  def "Use illegal target type"() {
    given:
    def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

    when:
    def dummy = threadSafeClassLoader.get().newObject(
      forTargetType(String)
    )

    then:
    def error = thrown IllegalArgumentException
    error.message =~ /Class java.lang.String is not protected/
    dummy == null

    cleanup:
    threadSafeClassLoader.remove()
  }
}
