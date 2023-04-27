package e3ps.epm.numberRule;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.numberRuleCode.NumberRuleCode;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },
// 최초등록자
		properties = {
				@GeneratedProperty(name = "number", type = String.class, javaDoc = "KEK 도면번호", constraints = @PropertyConstraints(required = true),

						columnProperties = @ColumnProperties(columnName = "KekNumber", unique = true)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "KEK 도면명", constraints = @PropertyConstraints(required = true)),
				
				@GeneratedProperty(name = "lotNo", type = Integer.class, javaDoc = "LOT", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "unitName", type = String.class, javaDoc = "UNIT NAME", constraints = @PropertyConstraints(required = true))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "MasterSizeLink",

						foreignKeyRole = @ForeignKeyRole(name = "size", type = NumberRuleCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "master", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "MasterSectorLink",

						foreignKeyRole = @ForeignKeyRole(name = "sector", type = NumberRuleCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "master", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "MasterCompanyLink",

						foreignKeyRole = @ForeignKeyRole(name = "company", type = NumberRuleCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "master", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "MasterDepartmentLink",

						foreignKeyRole = @ForeignKeyRole(name = "department", type = NumberRuleCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "master", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "MasterDocumentLink",

						foreignKeyRole = @ForeignKeyRole(name = "document", type = NumberRuleCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "master", cardinality = Cardinality.ONE)),

		})
public class NumberRuleMaster extends _NumberRuleMaster {

	static final long serialVersionUID = 1;

	public static NumberRuleMaster newNumberRuleMaster() throws WTException {
		NumberRuleMaster instance = new NumberRuleMaster();
		instance.initialize();
		return instance;
	}
}
