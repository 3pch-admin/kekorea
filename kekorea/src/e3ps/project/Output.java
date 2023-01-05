package e3ps.project;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.doc.E3PSDocumentMaster;
import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "location", type = String.class),

				@GeneratedProperty(name = "description", type = String.class),

		},

		foreignKeys = {

				// front target object, before source user

				@GeneratedForeignKey(name = "DocumentMasterOutputLink",

						foreignKeyRole = @ForeignKeyRole(name = "master", type = E3PSDocumentMaster.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "output", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "DocumentOutputLink",

						foreignKeyRole = @ForeignKeyRole(name = "document", type = LifeCycleManaged.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "output", cardinality = Cardinality.ONE)),

				// front target object, before source user

				@GeneratedForeignKey(name = "ProjectOutputLink",

						foreignKeyRole = @ForeignKeyRole(name = "project", type = Project.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "output", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "TemplateOutputLink",

						foreignKeyRole = @ForeignKeyRole(name = "template", type = Template.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "output", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "TaskOutputLink",

						foreignKeyRole = @ForeignKeyRole(name = "task", type = Task.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "output", cardinality = Cardinality.DEFAULT))

		}

)
public class Output extends _Output {

	static final long serialVersionUID = 1;

	public static Output newOutput() throws WTException {
		Output instance = new Output();
		instance.initialize();
		return instance;
	}

}
