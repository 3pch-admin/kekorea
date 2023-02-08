package e3ps.admin.spec;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "spec", type = Spec.class, cascade = true),

		roleB = @GeneratedRole(name = "options", type = Options.class, cascade = true)

)
public class SpecOptionsLink extends _SpecOptionsLink {

	static final long serialVersionUID = 1;

	public static SpecOptionsLink newSpecOptionsLink(Spec spec, Options option) throws WTException {
		SpecOptionsLink instance = new SpecOptionsLink();
		instance.initialize(spec, option);
		return instance;
	}
}
