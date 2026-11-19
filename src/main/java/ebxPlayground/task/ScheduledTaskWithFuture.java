package ebxPlayground.task;

import com.orchestranetworks.scheduler.ScheduledTask;
import java.util.concurrent.CompletableFuture;

public abstract class ScheduledTaskWithFuture extends ScheduledTask {

  private CompletableFuture<Void> future;

  public CompletableFuture<Void> getFuture() {
    return future;
  }

  public void setFuture(CompletableFuture<Void> future) {
    this.future = future;
  }
}
