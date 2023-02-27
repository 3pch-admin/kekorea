package e3ps.workspace;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "결재 제목", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "state", type = String.class, javaDoc = "결재 상태", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "startTime", type = Timestamp.class, javaDoc = "결재 시작 시간"),

				@GeneratedProperty(name = "completeTime", type = Timestamp.class, javaDoc = "결재 완료 시간"),

				@GeneratedProperty(name = "completeUserID", type = String.class, javaDoc = "결재 완료 유저")

		}

)
public interface ApprovalImpl extends _ApprovalImpl {

}
