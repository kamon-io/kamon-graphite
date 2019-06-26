package kamon.graphite

import java.net.ServerSocket
import java.time.Instant
import java.util.Scanner

import kamon.metric.{MeasurementUnit, MetricValue, MetricsSnapshot, PeriodSnapshot}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class GraphiteReporterSpec extends WordSpec with BeforeAndAfterAll with Matchers {

  "the GraphiteReporter" should {

    val serverSocket = new ServerSocket(2003)
    val reporter = new GraphiteReporter()
    val now = Instant.ofEpochMilli(1523395554)

    reporter.start()

    "sends counter metrics" in {

      reporter.reportPeriodSnapshot(
        PeriodSnapshot.apply(
          now.minusMillis(1000), now, MetricsSnapshot.apply(
            Nil,
            Nil,
            Nil,
            Seq(
              MetricValue.apply("test.counter", Map("tag 1" -> "value 1 2"), MeasurementUnit.none, 0)
            )

          )
        )
      )
      val lineReader = new Scanner(serverSocket.accept().getInputStream())
      val result = lineReader.nextLine()
      result should startWith ("kamon-graphite.test_counter.count;tag_1=value_1_2;service=kamon-application")

    }



    reporter.stop()

  }

}