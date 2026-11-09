package ebxPlayground.scriptTask;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class TestScriptTask extends ScriptTask {

    private String myParameter;

    public final String getMyParameter() {
        return myParameter;
    }

    public final void setMyParameter(final String myParameter) {
        this.myParameter = myParameter;
    }

    @Override
    public void executeScript(ScriptTaskContext arg0) throws OperationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeScript'");
    }

}
