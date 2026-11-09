package ebxPlayground.trigger;

import com.onwbp.adaptation.Adaptation;

import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
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
}
