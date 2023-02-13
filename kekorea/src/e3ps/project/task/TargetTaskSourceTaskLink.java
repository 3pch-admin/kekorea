package e3ps.project.task;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.project.Project;
import e3ps.project.template.Template;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "targetTask", type = Task.class),

		roleB = @GeneratedRole(name = "sourceTask", type = Task.class),

		properties = {

				@GeneratedProperty(name = "lag", type = Integer.class, initialValue = "1") },

		foreignKeys = {

				@GeneratedForeignKey(name = "ProjectTargetSourceLink",

						foreignKeyRole = @ForeignKeyRole(name = "project", type = Project.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "target", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "TemplateTargetSourceLink",

						foreignKeyRole = @ForeignKeyRole(name = "template", type = Template.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "target", cardinality = Cardinality.ONE))

		}

)

public class TargetTaskSourceTaskLink extends _TargetTaskSourceTaskLink {

	static final long serialVersionUID = 1;

	public static TargetTaskSourceTaskLink newTargetTaskSourceTaskLink(Task targetTask, Task sourceTask)
			throws WTException {
		TargetTaskSourceTaskLink instance = new TargetTaskSourceTaskLink();
		instance.initialize(targetTask, sourceTask);
		return instance;
	}
}
