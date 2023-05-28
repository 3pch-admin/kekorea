package e3ps.workspace;

import java.util.List;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "approvalList", type = List.class, columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "agreeList", type = List.class, columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "receiveList", type = List.class, columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "favorite", type = Boolean.class)

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
