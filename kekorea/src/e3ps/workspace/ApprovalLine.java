package e3ps.workspace;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import lombok.Getter;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ApprovalImpl.class },

		properties = {

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "결재 순서", initialValue = "0", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "type", type = String.class, javaDoc = "결재 타입", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(columnName = "approvalType")),
				
                @GeneratedProperty(name = "state", type = String.class, javaDoc = "결재 상태", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "role", type = String.class, javaDoc = "결재 역할", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "reads", type = Boolean.class, javaDoc = "결재 확인 여부", initialValue = "false"),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "결재 의견", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "absence", type = Boolean.class, javaDoc = "부재중", initialValue = "false"),

				@GeneratedProperty(name = "absenceID", type = String.class, javaDoc = "대결자 아이디")

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "LineMasterLink",

						foreignKeyRole = @ForeignKeyRole(name = "master", type = ApprovalMaster.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "line", cardinality = Cardinality.ONE_TO_MANY))

		})
public class ApprovalLine extends _ApprovalLine {
	static final long serialVersionUID = 1;

	public static ApprovalLine newApprovalLine() throws WTException {
		ApprovalLine instance = new ApprovalLine();
		instance.initialize();
		return instance;
	}
}
