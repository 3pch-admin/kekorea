package e3ps.doc.meeting;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.project.Project;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "meeting", type = Meeting.class),

		roleB = @GeneratedRole(name = "project", type = Project.class)

)

public class MeetingProjectLink extends _MeetingProjectLink {
	static final long serialVersionUID = 1;

	public static MeetingProjectLink newMeetingProjectLink(Meeting meeting, Project project) throws WTException {
		MeetingProjectLink instance = new MeetingProjectLink();
		instance.initialize(meeting, project);
		return instance;
	}
}
