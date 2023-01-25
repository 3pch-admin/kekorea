package e3ps.admin.spec;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "spec", type = Spec.class),

		roleB = @GeneratedRole(name = "option", type = Options.class)

)
public class SpecOptionLink extends _SpecOptionLink {

	static final long serialVersionUID = 1;

	public static SpecOptionLink newSpecOptionLink(Spec spec, Options option) throws WTException {
		SpecOptionLink instance = new SpecOptionLink();
		instance.initialize(spec, option);
		return instance;
	}
}
