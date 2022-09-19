package benchmarks

import java.util.concurrent._
import org.openjdk.jmh.annotations._
import zio.{Scope => ZSCope, _}

/*
[info] Benchmark                            (count)  Mode  Cnt      Score      Error  Units
[info] ZioCSNewForkAllDiscardBenchmark.run        1  avgt    5   1332.470 ±   49.815  us/op
[info] ZioCSNewForkAllDiscardBenchmark.run      128  avgt    5   5522.639 ±  658.662  us/op
[info] ZioCSNewForkAllDiscardBenchmark.run     1024  avgt    5  45017.808 ± 3550.936  us/op
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@State(Scope.Benchmark)
class ZioCSNewForkAllDiscardBenchmark {

  @Param(Array("1", "128", "1024"))
  var count: Int = 0

  var executor: ScheduledExecutorService = _

  @Setup(Level.Iteration)
  def setup(): Unit = {
    executor = new ScheduledThreadPoolExecutor(java.lang.Runtime.getRuntime.availableProcessors)
  }

  @TearDown(Level.Iteration)
  def tearDown(): Unit = {
    executor.shutdown()
  }

  @Benchmark
  def run(): Unit = {
    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run {
        newForkAllDiscard {
          Chunk.fill(count) {
            scheduleZIO(executor)
          }
        }.flatMap(_.join)
      }.getOrThrow()
    }
  }

}
