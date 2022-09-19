package benchmarks

import java.util.concurrent._
import org.openjdk.jmh.annotations._

/*
[info] Benchmark                     (count)  Mode  Cnt     Score     Error  Units
[info] CompletionStageBenchmark.run        1  avgt    5  1252.076 ± 108.125  us/op
[info] CompletionStageBenchmark.run      128  avgt    5  1280.802 ±  83.292  us/op
[info] CompletionStageBenchmark.run     1024  avgt    5  1685.868 ±  57.667  us/op
 */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@State(Scope.Benchmark)
class CompletionStageBenchmark {

  @Param(Array("1", "128", "1024"))
  var count: Int = 0

  var executor: ScheduledExecutorService = _

  @Setup(Level.Iteration)
  def setup(): Unit = {
    executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime.availableProcessors)
  }

  @TearDown(Level.Iteration)
  def tearDown(): Unit = {
    executor.shutdown()
  }

  @Benchmark
  def run(): Unit = {
    val cs = Array.fill(count) {
      schedule(executor)
    }
    CompletableFuture.allOf(cs: _*).join()
  }

}
