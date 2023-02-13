package e3ps.project.template;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.commonCode.CommonCode;
import e3ps.project.task.Task;
import wt.fc.ObjectToObjectLink;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "template", type = Template.class),

		roleB = @GeneratedRole(name = "user", type = WTUser.class),

		foreignKeys = { @GeneratedForeignKey(name = "TemplateUserTypeLink",

				foreignKeyRole = @ForeignKeyRole(name = "userType", type = CommonCode.class,

						constraints = @PropertyConstraints(required = true)),

				myRole = @MyRole(name = "userLink", cardinality = Cardinality.ONE)),

		}

)
public class TemplateUserLink extends _TemplateUserLink {
	static final long serialVersionUID = 1;

	public static TemplateUserLink newTemplateUserLink(Template template, WTUser user) throws WTException {
		TemplateUserLink instance = new TemplateUserLink();
		instance.initialize(template, user);
		return instance;
	}
}
