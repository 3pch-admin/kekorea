package e3ps.project.template;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "template", type = Template.class),

		roleB = @GeneratedRole(name = "user", type = WTUser.class),

		properties = {

				@GeneratedProperty(name = "userType", type = String.class)

		}

)
public class TemplateUserLink extends _TemplateUserLink {
	static final long serialVersionUID = 1;

	public static TemplateUserLink newTemplateUserLink(Template template, WTUser user) throws WTException {
		TemplateUserLink instance = new TemplateUserLink();
		instance.initialize(template	, user);
		return instance;
	}
}
