package e3ps.org;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.admin.commonCode.CommonCode;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "people", type = People.class),

		roleB = @GeneratedRole(name = "install", type = CommonCode.class)

)
public class PeopleInstallLink extends _PeopleInstallLink {

	static final long serialVersionUID = 1;

	public static PeopleInstallLink newPeopleInstallLink(People people, CommonCode install) throws WTException {
		PeopleInstallLink instance = new PeopleInstallLink();
		instance.initialize(people, install);
		return instance;
	}

}
