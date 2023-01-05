package e3ps.project;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		interfaces = { ContentHolder.class, ProjectImpl.class },

		iconProperties = @IconProperties(standardIcon = "/jsp/images/task.gif", openIcon = "/jsp/images/task.gif"),

		tableProperties = @TableProperties(tableName = "J_TASK"),

		properties = {

				@GeneratedProperty(name = "allocate", type = Integer.class, javaDoc = "할당율"),

				@GeneratedProperty(name = "depth", type = Integer.class, javaDoc = "레벨"),

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "정렬"),

				@GeneratedProperty(name = "taskType", type = String.class, javaDoc = "태스크 타입(NORMAL/MACHINE/ELEC/SOFT)"),

				@GeneratedProperty(name = "state", type = String.class, javaDoc = "상태", columnProperties = @ColumnProperties(columnName = "TASKSTATE")) },

		// 태스크 끼리 모자 관계

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "ParentTaskChildTaskLink",

						foreignKeyRole = @ForeignKeyRole(name = "parentTask", type = Task.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "childTask", cardinality = Cardinality.DEFAULT)),

				@GeneratedForeignKey(name = "ProjectTaskLink",

						foreignKeyRole = @ForeignKeyRole(name = "project", type = Project.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "task", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "TemplateTaskLink",

						foreignKeyRole = @ForeignKeyRole(name = "template", type = Template.class,

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
