package benchmarks

import java.util.concurrent._
import org.openjdk.jmh.annotations._
import zio.{Scope => ZSCope, _}

/*
[info] Benchmark                     (count)  Mode  Cnt     Score     Error  Units
[info] ZioCSForEachParBenchmark.run        1  avgt    5  1275.842 ±  34.084  us/op
[info] ZioCSForEachParBenchmark.run      128  avgt    5  2009.383 ±  32.116  us/op
[info] ZioCSForEachParBenchmark.run     1024  avgt    5  6928.734 ± 390.706  us/op
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@State(Scope.Benchmark)
class ZioCSForEachParBenchmark {

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
        ZIO.foreachPar(Chunk.range(0, count)) { _ =>
          scheduleZIO(executor)
        }
      }.getOrThrow()
    }
  }

}
