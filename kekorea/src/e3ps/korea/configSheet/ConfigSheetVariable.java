package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.commonCode.CommonCode;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "note", type = String.class, javaDoc = "NOTE", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "apply", type = String.class, javaDoc = "APPLY")

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "VariableCategoryLink",

						foreignKeyRole = @ForeignKeyRole(name = "category", type = CommonCode.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "variable", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "VariableItemLink",

						foreignKeyRole = @ForeignKeyRole(name = "item", type = CommonCode.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "variable", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "VariableSpecLink",

						foreignKeyRole = @ForeignKeyRole(name = "spec", type = CommonCode.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "variable", cardinality = Cardinality.ONE)),

		}

)
public class ConfigSheetVariable extends _ConfigSheetVariable {
	static final long serialVersionUID = 1;

	public static ConfigSheetVariable newConfigSheetVariable() throws WTException {
		ConfigSheetVariable instance = new ConfigSheetVariable();
		instance.initialize();
		return instance;
	}
}