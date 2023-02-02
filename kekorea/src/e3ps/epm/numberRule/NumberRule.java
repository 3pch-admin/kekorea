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

				@GeneratedProperty(name = "number", type = String.class, javaDoc = "도면번호", columnProperties = @ColumnProperties(columnName = "documentNumber"), constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "도면명", constraints = @PropertyConstraints(required = true))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "NumberRuleSizeLink",

						foreignKeyRole = @ForeignKeyRole(name = "size", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "numberRule", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "NumberRuleBusinessSectorLink",

						foreignKeyRole = @ForeignKeyRole(name = "businessSector", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "numberRule", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "NumberRuleDrawingCompanyLink",

						foreignKeyRole = @ForeignKeyRole(name = "drawingCompany", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "numberRule", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "NumberRuleDepartmentLink",

						foreignKeyRole = @ForeignKeyRole(name = "department", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "numberRule", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "NumberRuleDocumentLink",

						foreignKeyRole = @ForeignKeyRole(name = "document", type = CommonCode.class,

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
