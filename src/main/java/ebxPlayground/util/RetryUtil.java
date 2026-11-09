package ebxPlayground.util;

import com.orchestranetworks.service.LoggingCategory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Fuehrt eine bestimmte Operation nach einer Exception mehrfach aus.</br> Die Wartezeit wird dabei
 * immer verdoppelt.</br> Durch den Delayedexecutor wird kein Thread blockiert dabei.</br> Es wird
 * angenommen, dass die Ausfuehrungszeit der Operation gering ist. Deswegen wird kein extra Thread
 * Pool verwendet.
 *
 * @author DrPhilippBaumgaertel
 */
public class RetryUtil {
  private RetryUtil() {}

  /**
   * Fuehrt eine bestimmte Operation nach einer Exception mehrfach aus.
   *
   * @param r Ausfuehrbare Operation
   * @param times Anzahl der Wiederholungsversuche
   * @param delay initiale Wartezeit nach dem ersten Fehlschlag (wird danach immer verdoppelt)
   * @param timeunit Zeiteinheit des Delays
   * @param exceptionally Consumer, der die finale Exception verarbeiten soll.
   * @return Future fuer das tracken der Ausfuehrung
   */
  public static CompletableFuture<Void> retryWithExponentialDelay(
      Runnable r,
      int times,
      int delay,
      TimeUnit timeunit,
      Consumer<Throwable> exceptionally) { // Optional<CompletableFuture<Void>>
    try {
      r.run();
    } catch (Exception e) {
      LoggingCategory.getKernel().error(e.getMessage());
      LoggingCategory.getKernel().error("Will retry in " + delay + " " + timeunit);
      return retryAsyncWithExponentialDelay(r, times - 1, delay, timeunit, exceptionally);
    }
    return CompletableFuture.completedFuture(null);
  }

  private static CompletableFuture<Void> retryAsyncWithExponentialDelay(
      Runnable r, int times, int delay, TimeUnit timeunit, Consumer<Throwable> exceptionally) {
    // Recursive with composing
    Executor delayed = CompletableFuture.delayedExecutor(delay, timeunit);
    CompletableFuture<Void> future =
        CompletableFuture.runAsync(
            () -> {
              LoggingCategory.getKernel().error("Retrying after " + delay + " " + timeunit);
              r.run();
            },
            delayed);

    if (times > 1) {
      future =
          future.exceptionallyComposeAsync(
              ex -> {
                LoggingCategory.getKernel().error(ex.getMessage());
                LoggingCategory.getKernel().error("Will retry in " + delay * 2 + " " + timeunit);
                return retryAsyncWithExponentialDelay(
                    r, times - 1, delay * 2, timeunit, exceptionally);
              });
    } else {
      future =
          future.exceptionallyComposeAsync(
              ex -> {
                LoggingCategory.getKernel().error(ex.getMessage());
                LoggingCategory.getKernel().error("Will not retry again.");
                exceptionally.accept(ex);
                return CompletableFuture.failedFuture(ex);
              });
    }

    return future;
  }
}
