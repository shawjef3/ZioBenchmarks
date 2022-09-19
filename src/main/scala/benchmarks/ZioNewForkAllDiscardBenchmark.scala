package benchmarks

import java.util.concurrent._
import org.openjdk.jmh.annotations._
import zio.{Scope => ZSCope, _}

/*
[info] Benchmark                          (count)  Mode  Cnt      Score     Error  Units
[info] ZioNewForkAllDiscardBenchmark.run        1  avgt    5     59.796 ±  19.144  us/op
[info] ZioNewForkAllDiscardBenchmark.run      128  avgt    5   4607.584 ± 230.917  us/op
[info] ZioNewForkAllDiscardBenchmark.run     1024  avgt    5  40093.696 ± 971.758  us/op
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@State(Scope.Benchmark)
class ZioNewForkAllDiscardBenchmark {

  @Param(Array("1", "128", "1024"))
  var count: Int = 0

  @Benchmark
  def run(): Unit = {
    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run {
        newForkAllDiscard {
          Chunk.fill(count)(ZIO.attempt(()))
        }.flatMap(_.join)
      }.getOrThrow()
    }
  }

}
