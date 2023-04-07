package e3ps.admin.numberRuleCode;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "code", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "codeType", type = NumberRuleCodeType.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class),

				@GeneratedProperty(name = "enable", type = Boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "sort", type = Integer.class, initialValue = "0")

		},

		tableProperties = @TableProperties(compositeIndex1 = "+ code + codeType"),

		foreignKeys = { @GeneratedForeignKey(name = "ParentChildLink",

				foreignKeyRole = @ForeignKeyRole(name = "parent", type = NumberRuleCode.class,

						constraints = @PropertyConstraints(required = false)),

				myRole = @MyRole(name = "child", cardinality = Cardinality.ZERO_TO_ONE)) }

)

public class NumberRuleCode extends _NumberRuleCode {

	static final long serialVersionUID = 1;

	public static NumberRuleCode newNumberRuleCode() throws WTException {
		NumberRuleCode instance = new NumberRuleCode();
		instance.initialize();
		return instance;
	}
}
