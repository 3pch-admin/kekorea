package e3ps.epm.numberRule;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "version", type = Integer.class, javaDoc = "버전", initialValue = "1", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "latest", type = Boolean.class, javaDoc = "최신버전여부", initialValue = "true", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "note", type = String.class, javaDoc = "개정사유", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "state", type = String.class, javaDoc = "상태", constraints = @PropertyConstraints(required = true)),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "NumberRuleMasterLink",

						foreignKeyRole = @ForeignKeyRole(name = "master", type = NumberRuleMaster.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "numberRule", cardinality = Cardinality.ONE))

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
