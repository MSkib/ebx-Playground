package ebxPlayground.task;

import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.service.OperationException;

public class TestTask extends ScheduledTask {

    @Override
    public void execute(ScheduledExecutionContext context) throws OperationException, ScheduledTaskInterruption {

    context.getRepository();
    context.getSession();

    }



}
