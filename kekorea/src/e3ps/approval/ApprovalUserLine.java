package e3ps.approval;

import java.util.List;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "lineType", type = String.class, javaDoc = "라인 타입", initialValue = "\"series\"", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "approvalList", type = List.class, columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "agreeList", type = List.class, columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "receiveList", type = List.class, columnProperties = @ColumnProperties(columnType = ColumnType.BLOB))

		}

)

public class ApprovalUserLine extends _ApprovalUserLine {
	static final long serialVersionUID = 1;

	public static ApprovalUserLine newApprovalUserLine() throws WTException {
		ApprovalUserLine instance = new ApprovalUserLine();
		instance.initialize();
		return instance;
	}
}
