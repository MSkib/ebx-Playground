package ebxPlayground.transformation;

import java.util.List;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.addon.dint.DataIntegrationException;
import com.orchestranetworks.addon.dint.schema.DefaultDataTypes;
import com.orchestranetworks.addon.dint.transformation.GenericDataObject;
import com.orchestranetworks.addon.dint.transformation.InputDefinition;
import com.orchestranetworks.addon.dint.transformation.OutputDefinition;
import com.orchestranetworks.addon.dint.transformation.TransformationCategory;
import com.orchestranetworks.addon.dint.transformation.TransformationDefinition;
import com.orchestranetworks.addon.dint.transformation.TransformationExecutionContext;
import com.orchestranetworks.addon.dint.transformation.TransformationFunction;

public class TestTransformation implements TransformationDefinition<GenericDataObject, GenericDataObject> {

	private static final InputDefinition BOOLEAN_INPUT =
			new InputDefinition(
					"BooleanInput",
					UserMessage.createInfo("Boolean Input"),
					UserMessage.createInfo("Boolean value imported from Excel"),
					DefaultDataTypes.BOOLEAN,
					false);

	private static final OutputDefinition BOOLEAN_OUTPUT =
			new OutputDefinition(
					"BooleanOutput",
					UserMessage.createInfo("Boolean Output"),
					UserMessage.createInfo("Boolean value mapped to the target field"),
					DefaultDataTypes.BOOLEAN,
					false);

	@Override
	public String getCode() {
		return "bulkImportServiceForBooleanTransformation";
	}

	@Override
	public UserMessage getLabel() {
		return UserMessage.createInfo("Bulk Import Service for Boolean");
	}

	@Override
	public InputDefinition getInputDefinition() {
		return new InputDefinition(
				"BooleanInput",
				UserMessage.createInfo("Boolean Input"),
				UserMessage.createInfo("Boolean value imported from Excel"),
				List.of(BOOLEAN_INPUT));
	}

	@Override
	public OutputDefinition getOutputDefinition() {
		return new OutputDefinition(
				"BooleanOutput",
				UserMessage.createInfo("Boolean Output"),
				UserMessage.createInfo("Boolean value mapped to the target field"),
				List.of(BOOLEAN_OUTPUT));
	}

	@Override
	public TransformationCategory getTransformationCategory() {
		return TransformationCategory.CONVERSION;
	}

	@Override
	public TransformationFunction<GenericDataObject, GenericDataObject> getFunction() {
		return this::transform;
	}

	private GenericDataObject transform(TransformationExecutionContext<GenericDataObject> context)
			throws DataIntegrationException {
		GenericDataObject input = context.getInputValue();
		Object inputValue = input == null ? null : input.getValue(BOOLEAN_INPUT.getName());

		GenericDataObject output = GenericDataObject.newInstance();
		output.setValue(BOOLEAN_OUTPUT.getName(), toBoolean(inputValue));
		return output;
	}

	private static boolean toBoolean(Object value) throws DataIntegrationException {
		if (value == null) {
			return false;
		}

		if (value instanceof Boolean booleanValue) {
			return booleanValue;
		}

		if (value instanceof String stringValue) {
			String normalizedValue = stringValue.trim();
			if (normalizedValue.isEmpty()) {
				return false;
			}
			if (Boolean.TRUE.toString().equalsIgnoreCase(normalizedValue)) {
				return true;
			}
			if (Boolean.FALSE.toString().equalsIgnoreCase(normalizedValue)) {
				return false;
			}
		}

		throw new DataIntegrationException(
				UserMessage.createError("Expected a Boolean value but received: " + value));
	}

}
