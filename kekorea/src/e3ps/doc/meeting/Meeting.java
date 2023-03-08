package e3ps.doc.meeting;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ColumnType;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { Ownable.class, ContentHolder.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "희의록 제목", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "content", type = String.class, javaDoc = "희외록 내용", columnProperties = @ColumnProperties(columnType = ColumnType.BLOB)),

				@GeneratedProperty(name = "state", type = String.class, constraints = @PropertyConstraints(required = true))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "MeetingTemplateLink",

						foreignKeyRole = @ForeignKeyRole(name = "template", type = MeetingTemplate.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "meeting", cardinality = Cardinality.ONE)),

		}

)

public class Meeting extends _Meeting {
	static final long serialVersionUID = 1;

	public static Meeting newMeeting() throws WTException {
		Meeting instance = new Meeting();
		instance.initialize();
		return instance;
	}

}
