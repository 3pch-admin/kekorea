package e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.project.Project;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "reqDoc", type = RequestDocument.class),

		roleB = @GeneratedRole(name = "project", type = Project.class)

)

public class ReqDocumentProjectLink extends _ReqDocumentProjectLink {

	static final long serialVersionUID = 1;

	public static ReqDocumentProjectLink newReqDocumentProjectLink(RequestDocument reqDoc, Project project)
			throws WTException {
		ReqDocumentProjectLink instance = new ReqDocumentProjectLink();
		instance.initialize(reqDoc, project);
		return instance;
	}
}
