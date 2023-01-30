package e3ps.korea.cip;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.admin.commonCode.CommonCode;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "cip", type = Cip.class),

		roleB = @GeneratedRole(name = "mak", type = CommonCode.class)

)

public class CipMakLink extends _CipMakLink {
	static final long serialVersionUID = 1;

	public static CipMakLink newCipMakLink(Cip cip, CommonCode mak) throws WTException {
		CipMakLink instance = new CipMakLink();
		instance.initialize(cip, mak);
		return instance;
	}
}
