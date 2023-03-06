package e3ps.doc.meeting;

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

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "회의록 템플릿", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, unique = true)),

				@GeneratedProperty(name = "content", type = String.class, javaDoc = "회의록 템플릿 내용", columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "enable", type = Boolean.class, javaDoc = "사용여부", initialValue = "true")

		}

)
public class MeetingTemplate extends _MeetingTemplate {
	static final long serialVersionUID = 1;

	public static MeetingTemplate newMeetingTemplate() throws WTException {
		MeetingTemplate instance = new MeetingTemplate();
		instance.initialize();
		return instance;
	}
}
