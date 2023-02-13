package e3ps.project.task;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import e3ps.admin.commonCode.CommonCode;
import e3ps.project.Project;
import e3ps.project.ProjectImpl;
import e3ps.project.template.Template;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ProjectImpl.class, Ownable.class },

		tableProperties = @TableProperties(tableName = "J_TASK"),

		properties = {

				@GeneratedProperty(name = "allocate", type = Integer.class, javaDoc = "할당율"),

				@GeneratedProperty(name = "depth", type = Integer.class, javaDoc = "레벨", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "정렬순서", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "taskType", type = String.class, javaDoc = "태스크 타입(NORMAL,MACHINE,ELEC,SOFT", constraints = @PropertyConstraints(required = true)),

		},

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "ParentTaskChildTaskLink",

						foreignKeyRole = @ForeignKeyRole(name = "parentTask", type = Task.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "childTask", cardinality = Cardinality.DEFAULT)),

				@GeneratedForeignKey(name = "TaskProjectLink",

						foreignKeyRole = @ForeignKeyRole(name = "project", type = Project.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "task", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "TaskTemplateLink",

						foreignKeyRole = @ForeignKeyRole(name = "template", type = Template.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "task", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "TaskTaskTypeLink",

						foreignKeyRole = @ForeignKeyRole(name = "taskType", type = CommonCode.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "task", cardinality = Cardinality.ONE))

		}

)
public class Task extends _Task {

	static final long serialVersionUID = 1;

	public static Task newTask() throws WTException {
		Task instance = new Task();
		instance.initialize();
		return instance;
	}
}
