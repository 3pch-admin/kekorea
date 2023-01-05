package e3ps.common.code;

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

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "code", type = String.class),

				@GeneratedProperty(name = "codeType", type = CommonCodeType.class),

				@GeneratedProperty(name = "description", type = String.class),

				@GeneratedProperty(name = "sort", type = int.class),

				@GeneratedProperty(name = "depth", type = int.class),

				@GeneratedProperty(name = "uses", type = boolean.class, initialValue = "true")

		},

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "ParentChildLink",

						foreignKeyRole = @ForeignKeyRole(name = "parent", type = CommonCode.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "child", cardinality = Cardinality.ZERO_TO_ONE)) }

)

public class CommonCode extends _CommonCode {

	static final long serialVersionUID = 1;

	public static CommonCode newCommonCode() throws WTException {
		CommonCode instance = new CommonCode();
		instance.initialize();
		return instance;
	}
}
