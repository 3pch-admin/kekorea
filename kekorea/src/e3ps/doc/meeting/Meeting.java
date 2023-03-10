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

import wt.doc.WTDocument;
import wt.util.WTException;

@GenAsPersistable(superClass = WTDocument.class,

		foreignKeys = {

				@GeneratedForeignKey(name = "MeetingTinyLink",

						foreignKeyRole = @ForeignKeyRole(name = "tiny", type = MeetingTemplate.class,

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
