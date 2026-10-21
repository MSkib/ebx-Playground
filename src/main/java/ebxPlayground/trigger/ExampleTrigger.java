package ebxPlayground.trigger;

import com.onwbp.adaptation.Adaptation;

import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterDeleteOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeDeleteOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeTransactionCancelContext;
import com.orchestranetworks.schema.trigger.BeforeTransactionCommitContext;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;
import com.orchestranetworks.service.OperationException;


public class ExampleTrigger extends TableTrigger {

    public String simpleParameter;

    public String getSimpleParameter() {
        return simpleParameter;
    }

    public void setSimpleParameter(String simpleParameter) {
        this.simpleParameter = simpleParameter;
    }

    @Override
    public void setup(TriggerSetupContext context) {
        // use it when the data model is published
    }

    @Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException {

        Adaptation newRecord = context.getAdaptationOccurrence();
        System.out.println("record added: " + newRecord.getOccurrencePrimaryKey().format());
        System.out.println("parameter value: " + simpleParameter);
	}

	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext context) throws OperationException {
         super.handleAfterModify(context);
	}

    @Override
    public void handleAfterDelete(AfterDeleteOccurrenceContext aContext) throws OperationException {
        // TODO Auto-generated method stub
        super.handleAfterDelete(aContext);
    }

    @Override
    public void handleBeforeCreate(BeforeCreateOccurrenceContext aContext) throws OperationException {
        // TODO Auto-generated method stub
        super.handleBeforeCreate(aContext);
    }

    @Override
    public void handleBeforeDelete(BeforeDeleteOccurrenceContext aContext) throws OperationException {
        // TODO Auto-generated method stub
        super.handleBeforeDelete(aContext);
    }

    @Override
    public void handleBeforeModify(BeforeModifyOccurrenceContext aContext) throws OperationException {
        // TODO Auto-generated method stub
        super.handleBeforeModify(aContext);
    }

    @Override
    public void handleBeforeTransactionCancel(BeforeTransactionCancelContext handlingContext) {
        // TODO Auto-generated method stub
        super.handleBeforeTransactionCancel(handlingContext);
    }

    @Override
    public void handleBeforeTransactionCommit(BeforeTransactionCommitContext aContext) throws OperationException {
        // TODO Auto-generated method stub
        super.handleBeforeTransactionCommit(aContext);
    }

    @Override
    public void handleNewContext(NewTransientOccurrenceContext aContext) {
        // TODO Auto-generated method stub
        super.handleNewContext(aContext);
    }
}
