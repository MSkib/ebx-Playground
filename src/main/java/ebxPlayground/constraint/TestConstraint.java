package ebxPlayground.constraint;

import java.util.Locale;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidationOnRecord;
import com.orchestranetworks.instance.ValueContextForValidationOnTable;
import com.orchestranetworks.schema.ConstraintContextOnTable;
import com.orchestranetworks.schema.ConstraintOnTableWithRecordLevelCheck;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.SchemaNode;

public class TestConstraint implements ConstraintOnTableWithRecordLevelCheck {

    @Override
    public void checkTable(ValueContextForValidationOnTable arg0) {
        // not needed for this example
    }

    @Override
    public void setup(ConstraintContextOnTable constraintContextOnTable) {
        constraintContextOnTable.addMessage(
                UserMessage.createInfo("Setup " + this.getClass().getName()));
    }

    @Override
    public String toUserDocumentation(Locale arg0, ValueContext arg1) throws InvalidSchemaException {
        return "TestConstraint toUserDocumentation";
    }

    @Override
    public void checkRecord(ValueContextForValidationOnRecord valueContextForValidationOnRecord) {

        ValueContext vc = valueContextForValidationOnRecord.getRecord();

        SchemaNode node = vc
                .getNode()
                .getNode(null); // BrassPaths._Root_BranchSite._Root_BranchSite_BranchKey

        try {

        } catch (Exception e) {
            valueContextForValidationOnRecord.addMessage(node,
                    UserMessage.createWarning("Failed to generate branch key: " + e.getMessage()));
        }
    }

}