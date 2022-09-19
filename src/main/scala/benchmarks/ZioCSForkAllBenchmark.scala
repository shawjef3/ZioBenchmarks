package benchmarks

import java.util.concurrent._
import org.openjdk.jmh.annotations._
import zio.{Scope => ZSCope, _}

/*
[info] Benchmark                  (count)  Mode  Cnt      Score     Error  Units
[info] ZioCSForkAllBenchmark.run        1  avgt    5   1316.653 ± 122.861  us/op
[info] ZioCSForkAllBenchmark.run      128  avgt    5   3211.437 ±  38.129  us/op
[info] ZioCSForkAllBenchmark.run     1024  avgt    5  17186.760 ± 461.684  us/op
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@State(Scope.Benchmark)
class ZioCSForkAllBenchmark {

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
        ZIO.forkAll {
          Chunk.fill(count) {
            scheduleZIO(executor)
          }
        }.flatMap(_.join)
      }.getOrThrow()
    }
  }

}
