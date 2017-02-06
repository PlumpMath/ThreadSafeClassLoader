package de.scrum_master.thread_safe

import de.scrum_master.app.INumberGenerator
import de.scrum_master.badlib.NumberGenerator
import spock.lang.Specification
import spock.lang.Unroll

import static de.scrum_master.thread_safe.ThreadSafeClassLoader.ObjectConstructionRules.forTargetType

class ThreadSafeClassLoaderTest extends Specification {

  def threadSafeClassLoader = ThreadSafeClassLoader.create(NumberGenerator)

  def cleanup() {
    threadSafeClassLoader.remove()
  }

  @Unroll
  def "Create #hasIface type via #hasFactory (#hasArgs, #hasTypes) "() {
    given:
    def constructionRules = forTargetType(targetType)
    if (hasIface)
      constructionRules.implementingType(NumberGenerator)
    if (arguments)
      constructionRules.arguments(arguments)
    if (argumentTypes)
      constructionRules.argumentTypes(argumentTypes)
    if (factoryMethod)
      constructionRules.factoryMethod(factoryMethod)

    when:
    def numberGenerator = threadSafeClassLoader.get().newObject(constructionRules)

    then:
    numberGenerator.addAndGet(11) == result1
    numberGenerator.addAndGet(9) == result2

    where:
    targetType       | arguments | argumentTypes | factoryMethod | result1 | result2
    NumberGenerator  | null      | null          | null          | 11      | 20
    NumberGenerator  | "3"       | null          | null          | 14      | 23
    NumberGenerator  | 3         | int           | null          | 14      | 23
    NumberGenerator  | null      | null          | "create"      | 11      | 20
    NumberGenerator  | 3         | null          | "create"      | 14      | 23
    NumberGenerator  | 3         | Integer       | "create"      | 14      | 23
    INumberGenerator | null      | null          | null          | 11      | 20
    INumberGenerator | "3"       | null          | null          | 14      | 23
    INumberGenerator | 3         | int           | null          | 14      | 23
    INumberGenerator | null      | null          | "create"      | 11      | 20
    INumberGenerator | 3         | null          | "create"      | 14      | 23
    INumberGenerator | 3         | Integer       | "create"      | 14      | 23

    hasIface = targetType.isInterface() ? "interface" : "class"
    hasArgs = arguments ? "arguments" : "no arguments"
    hasTypes = argumentTypes ? "types" : "no types"
    hasFactory = factoryMethod ? "factory method" : "constructor"
  }

  def "Try to create illegal target type object"() {
    when:
    def dummy = threadSafeClassLoader.get().newObject(forTargetType(String))

    then:
    def error = thrown IllegalArgumentException
    error.message =~ /Class java.lang.String is not protected/
    dummy == null
  }
}
