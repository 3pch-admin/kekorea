package e3ps.org;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.admin.commonCode.CommonCode;
import wt.fc.ObjectToObjectLink;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "people", type = People.class),

		roleB = @GeneratedRole(name = "mak", type = CommonCode.class)

)

public class PeopleMakLink extends _PeopleMakLink {

	static final long serialVersionUID = 1;

	public static PeopleMakLink newPeopleMakLink(People people, CommonCode mak) throws Exception {
		PeopleMakLink instance = new PeopleMakLink();
		instance.initialize(people, mak);
		return instance;
	}
}
