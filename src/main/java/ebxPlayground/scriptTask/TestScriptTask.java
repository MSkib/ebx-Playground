package ebxPlayground.scriptTask;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;
import com.orchestranetworks.service.Session;

import ebxPlayground.util.EbxUtil;
import ebxPlayground.util.UpdateUtil;

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
