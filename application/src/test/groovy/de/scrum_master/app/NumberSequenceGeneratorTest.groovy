package de.scrum_master.app

import org.spockframework.runtime.ConditionNotSatisfiedError
import spock.lang.FailsWith
import spock.lang.Specification

class NumberSequenceGeneratorTest extends Specification {
  def settingsList = [
    new NumberSequenceSettings(1, 1, 10),
    new NumberSequenceSettings(0, 3, 11),
    new NumberSequenceSettings(22, 1, 4),
    new NumberSequenceSettings(1, 2, 3),
    new NumberSequenceSettings(4, 5, 6),
  ]
  def sequenceElementsTotal = 10 + 11 + 4 + 3 + 6
  def sequenceElementsMax = [10, 11, 4, 3, 6].max()
  def callDuration = 50
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
    assert results.length == 5

    assert results[0].length == 10
    assert results[0][0] == 1
    assert results[0][9] == 10

    assert results[1].length == 11
    assert results[1][0] == 0
    assert results[1][10] == 30

    assert results[2].length == 4
    assert results[2][0] == 22
    assert results[2][3] == 25

    assert results[3].length == 3
    assert results[3][0] == 1
    assert results[3][2] == 5

    assert results[4].length == 6
    assert results[4][0] == 4
    assert results[4][5] == 29
  }
}
