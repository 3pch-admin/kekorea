package e3ps.admin.specCode;

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

				@GeneratedProperty(name = "codeType", type = SpecCodeType.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class),

				@GeneratedProperty(name = "enable", type = Boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "sort", type = Integer.class, initialValue = "0")

		},

		tableProperties = @TableProperties(compositeIndex1 = "+ code + codeType"),

		foreignKeys = { @GeneratedForeignKey(name = "ParentChildLink",

				foreignKeyRole = @ForeignKeyRole(name = "parent", type = SpecCode.class,

						constraints = @PropertyConstraints(required = false)),

				myRole = @MyRole(name = "child", cardinality = Cardinality.ZERO_TO_ONE)) }

)

public class SpecCode extends _SpecCode {

	static final long serialVersionUID = 1;

	public static SpecCode newSpecCode() throws WTException {
		SpecCode instance = new SpecCode();
		instance.initialize();
		return instance;
	}
}
