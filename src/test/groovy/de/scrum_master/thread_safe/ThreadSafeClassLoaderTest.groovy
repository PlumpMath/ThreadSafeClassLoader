package de.scrum_master.thread_safe

import de.scrum_master.app.NonThreadSafeNumberSequenceGenerator
import de.scrum_master.app.NumberSequenceSettings
import de.scrum_master.app.ThreadSafeNumberSequenceGenerator
import de.scrum_master.badlib.NumberGenerator
import org.spockframework.runtime.ConditionNotSatisfiedError
import spock.lang.FailsWith
import spock.lang.Specification

class ThreadSafeClassLoaderTest extends Specification {
  def settingsList = [
    new NumberSequenceSettings(1, 1, 10),
    new NumberSequenceSettings(0, 3, 11),
    new NumberSequenceSettings(22, 1, 4),
    new NumberSequenceSettings(1, 2, 3),
    new NumberSequenceSettings(4, 5, 6),
  ]
  def sequenceElementsTotal = settingsList.sum { it.sequenceSize }
  def sequenceElementsMax = settingsList.max { it.sequenceSize }.sequenceSize
  def callDuration = NumberGenerator.SLEEP_MILLIS
  def startTime = System.currentTimeMillis()

  def "Use non-thread-safe number sequence generator in a sequential stream"() {
    when:
    int[][] results = settingsList
      .stream()
      .map(new NonThreadSafeNumberSequenceGenerator())
      .toArray()
    def duration = System.currentTimeMillis() - startTime
    println "Duration sequential non-thread-safe = $duration ms"

    then:
    duration >= sequenceElementsTotal * callDuration
    duration <= sequenceElementsTotal * callDuration + 200
    checkResults(results)
  }

  @FailsWith(ConditionNotSatisfiedError)
  def "Use non-thread-safe number sequence generator in a parallel stream"() {
    when:
    int[][] results = settingsList
      .parallelStream()
      .map(new NonThreadSafeNumberSequenceGenerator())
      .toArray()
    def duration = System.currentTimeMillis() - startTime
    println "Duration multi-threaded non-thread-safe = $duration ms"

    then:
    duration >= sequenceElementsMax * callDuration
    duration <= sequenceElementsMax * callDuration + 200
    checkResults(results)
  }

  def "Use thread-safe number sequence generator in a sequential stream"() {
    when:
    int[][] results = settingsList
      .stream()
      .map(new ThreadSafeNumberSequenceGenerator())
      .toArray()
    def duration = System.currentTimeMillis() - startTime
    println "Duration sequential thread-safe = $duration ms"

    then:
    duration >= sequenceElementsTotal * callDuration
    duration <= sequenceElementsTotal * callDuration + 200
    checkResults(results)
  }

  def "Use thread-safe number sequence generator in a parallel stream"() {
    when:
    int[][] results = settingsList
      .parallelStream()
      .map(new ThreadSafeNumberSequenceGenerator())
      .toArray()
    def duration = System.currentTimeMillis() - startTime
    println "Duration multi-threaded thread-safe = $duration ms"

    then:
    duration >= sequenceElementsMax * callDuration
    duration <= sequenceElementsMax * callDuration + 200
    checkResults(results)
  }

  private void checkResults(int[][] results) {
    assert results.length == settingsList.size()
    (0..results.length - 1).each {
      def settings = settingsList[it]
      def result = results[it]
      // Note: This works parallel streams because lists and arrays are intrinsically ordered stream sources,
      // thus mapping their elements to other types keeps the order. See also:
      // https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Ordering
      assert result.length == settings.sequenceSize
      assert result[0] == settings.startValue
      assert result[result.length - 1] == settings.startValue + settings.stepSize * (settings.sequenceSize - 1)
    }
  }
}
