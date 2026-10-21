package ebxPlayground.function;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class ExampleFunction implements ValueFunction {

    public String simpleParameter;

    public String getSimpleParameter() {
        return simpleParameter;
    }

    public void setSimpleParameter(String simpleParameter) {
        this.simpleParameter = simpleParameter;
    }

    @Override
    public void setup(ValueFunctionContext arg0) {
        // nothing to do
    }

    @Override
    public Object getValue(Adaptation record) {
        
        return simpleParameter + " added value";
    }
}
