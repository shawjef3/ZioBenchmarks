import java.util.concurrent.{CompletableFuture, ScheduledExecutorService, TimeUnit}
import zio._

package object benchmarks {
  def schedule(executor: ScheduledExecutorService): CompletableFuture[Unit] = {
    val c = new CompletableFuture[Unit]()
    executor.schedule(() => c.complete(()), 1, TimeUnit.MILLISECONDS)
    c
  }

  def scheduleZIO(executor: ScheduledExecutorService): Task[Unit] =
    ZIO.fromCompletionStage {
      schedule(executor)
    }

  def newForkAllDiscard[R, E, A](as: => Iterable[ZIO[R, E, A]])(implicit trace: Trace): URIO[R, Fiber[E, Unit]] =
    ZIO.suspendSucceed(as.foldRight[URIO[R, Fiber[E, Unit]]](Exit.succeed(Fiber.unit))(_.fork.zipWith(_)(_ *> _)))
}
