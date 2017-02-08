package de.scrum_master.app

import spock.lang.Specification
import spock.lang.Unroll

import java.text.SimpleDateFormat

class DateGeneratorIT extends Specification {
  String[][] datesAndPatterns = [
    ["1971-05-08", "yyyy-MM-dd"], ["25.12.2030", "dd.MM.yyyy"], ["1999-02-02", "yyyy-MM-dd"],
    ["30.10.2017", "dd.MM.yyyy"], ["1980-01-22", "yyyy-MM-dd"], ["19.08.1850", "dd.MM.yyyy"],
  ] * 100
  def startTime = System.currentTimeMillis()

  def cleanup() {
    DateGenerator.threadSafeMode = true
  }

  @Unroll
  def "Use #threadMode date generator in #streamMode stream"() {
    given:
    def dpList = Arrays.asList(datesAndPatterns)
    def datesToBeParsedStream = isParallelStream ? dpList.parallelStream() : dpList.stream()
    DateGenerator.threadSafeMode = isThreadSafe

    when:
    Date[] results = datesToBeParsedStream
      .map(new DateGenerator())
      .toArray()
    def duration = System.currentTimeMillis() - startTime
    println "Duration $threadMode date generator in $streamMode stream = $duration ms"

    then:
    checkResults(results)

    where:
    isThreadSafe | isParallelStream
    false        | false
    true         | false
    true         | true

    threadMode = isThreadSafe ? "thread-safe" : "non-thread-safe"
    streamMode = isParallelStream ? "parallel" : "serial"
  }

  def "Failure for non-thread-safe date generator in parallel stream"() {
    given:
    def dpList = Arrays.asList(datesAndPatterns)
    def datesToBeParsedStream = dpList.parallelStream()
    DateGenerator.threadSafeMode = false

    when:
    datesToBeParsedStream
      .map(new DateGenerator())
      .toArray()

    then:
    def exception = thrown Exception
    // Should be something like:
    //   java.lang.NumberFormatException: For input string: ""
    //   (...) java.text.ParseException: Unparseable date: "19.08.1850"
    println "Caught expected exception: $exception"
    def duration = System.currentTimeMillis() - startTime
    println "Duration non-thread-safe date generator in parallel stream until error = $duration ms"
  }

  private void checkResults(Date[] results) {
    assert results.length == datesAndPatterns.size()
    (0..results.length - 1).each {
      assert new SimpleDateFormat(datesAndPatterns[it][1]).format(results[it]) == datesAndPatterns[it][0]
    }
  }
}
