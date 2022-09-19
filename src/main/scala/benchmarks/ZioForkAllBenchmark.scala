package benchmarks

import java.util.concurrent._
import org.openjdk.jmh.annotations._
import zio.{Scope => ZSCope, _}

/*
[info] Benchmark                (count)  Mode  Cnt     Score    Error  Units
[info] ZioForkAllBenchmark.run        1  avgt    5    16.092 ±  4.196  us/op
[info] ZioForkAllBenchmark.run      128  avgt    5   537.698 ± 15.301  us/op
[info] ZioForkAllBenchmark.run     1024  avgt    5  3947.343 ± 80.066  us/op
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@State(Scope.Benchmark)
class ZioForkAllBenchmark {

  @Param(Array("1", "128", "1024"))
  var count: Int = 0

  @Benchmark
  def run(): Unit = {
    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run {
        ZIO.forkAll {
          Chunk.fill(count)(ZIO.attempt(()))
        }.map(_.join)
      }.getOrThrow()
    }
  }

}
