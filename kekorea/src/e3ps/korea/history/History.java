package e3ps.korea.history;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.project.Project;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "tuv", type = String.class)

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "ProjectHistoryLink",

						foreignKeyRole = @ForeignKeyRole(name = "project", type = Project.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "history", cardinality = Cardinality.ONE)

				) }

)

public class History extends _History {

	static final long serialVersionUID = 1;

	public static History newHistory() throws WTException {
		History instance = new History();
		instance.initialize();
		return instance;
	}
}
