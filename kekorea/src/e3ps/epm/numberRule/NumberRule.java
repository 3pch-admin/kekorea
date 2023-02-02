package e3ps.epm.numberRule;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.commonCode.CommonCode;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "number", type = String.class, columnProperties = @ColumnProperties(columnName = "documentNumber"))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "NumberRuleSizeLink",

						foreignKeyRole = @ForeignKeyRole(name = "size", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "numberRule", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "NumberRuleTypeLink",

						foreignKeyRole = @ForeignKeyRole(name = "drawingType", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "numberRule", cardinality = Cardinality.ONE)),

		}

)

public class NumberRule extends _NumberRule {

	static final long serialVersionUID = 1;

	public static NumberRule newNumberRule() throws WTException {
		NumberRule instance = new NumberRule();
		instance.initialize();
		return instance;
	}
}
