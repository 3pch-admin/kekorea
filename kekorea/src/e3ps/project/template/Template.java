package e3ps.project.template;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.project.ProjectImpl;
import wt.fc.WTObject;
import wt.ownership.Ownership;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ProjectImpl.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "제목", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "enable", type = Boolean.class, initialValue = "true", constraints = @PropertyConstraints(required = true))

		}

)

public class Template extends _Template {

	static final long serialVersionUID = 1;

	public static Template newTemplate() throws WTException {
		Template instance = new Template();
		instance.initialize();
		return instance;
	}
}
