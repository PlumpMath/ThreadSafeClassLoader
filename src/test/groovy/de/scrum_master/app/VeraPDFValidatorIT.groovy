package de.scrum_master.thread_safe

import de.scrum_master.app.VeraPDFValidator
import org.spockframework.runtime.ConditionNotSatisfiedError
import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Function

import static java.lang.Math.abs

class VeraPDFValidatorIT extends Specification {

  def pdfFiles = [
    "aufgaben222.pdf", "aufgaben232.pdf", "aufgaben242.pdf",
    "loesungshinweise211.pdf", "loesungshinweise212.pdf", "loesungshinweise221.pdf",
    "loesungshinweise222.pdf", "loesungshinweise231.pdf", "loesungshinweise232.pdf"
  ]

  int[] expectedReportLengths = [
    406733, 292038, 252390,
    967302, 1270734, 3826876,
    1340987, 2207777, 2401283
  ]

  def xmlReportLengthMapper = new Function<byte[], Integer>() {
    @Override
    Integer apply(byte[] bytes) {
      return bytes.length;
    }
  }

  def classLoader = getClass().classLoader
  def pdfStreams = pdfFiles.collect { classLoader.getResourceAsStream(it) }
  def startTime = System.currentTimeMillis()

  def cleanup() {
    VeraPDFValidator.threadSafeMode = true
    pdfStreams.each { it.close() }
  }

  @Unroll
  def "Use #threadMode veraPDF validator in #streamMode stream"() {
    given:
    def pdfStreamsStream = isParallelStream ? pdfStreams.parallelStream() : pdfStreams.stream()
    VeraPDFValidator.threadSafeMode = isThreadSafe

    when:
    int[] results = pdfStreamsStream
      .map(new VeraPDFValidator("1b", true))
      .map(xmlReportLengthMapper)
      .toArray()
    def duration = System.currentTimeMillis() - startTime
    println "Duration $threadMode veraPDF validator in $streamMode stream = $duration ms"

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

  @FailsWith(Throwable)
  def "Failure for non-thread-safe veraPDF validator in parallel stream"() {
    given:
    def pdfStreamsStream = pdfStreams.parallelStream()
    VeraPDFValidator.threadSafeMode = false

    when:
    int[] results = pdfStreamsStream
      .map(new VeraPDFValidator("1b", true))
      .map(xmlReportLengthMapper)
      .toArray()
    def duration = System.currentTimeMillis() - startTime
    println "Duration non-thread-safe veraPDF validator in parallel stream = $duration ms"

    then:
    checkResults(results)
  }

  private void checkResults(int[] results) {
    assert results.length == pdfFiles.size()
    (0..results.length - 1).each {
      // There might be slight length differences for validation results in XML
      assert abs(results[it] - expectedReportLengths[it]) < 10
    }
  }
}
