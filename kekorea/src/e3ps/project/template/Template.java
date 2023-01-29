package e3ps.project.template;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.project.ProjectImpl;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ProjectImpl.class },

		properties = {

				@GeneratedProperty(name = "number", type = String.class, javaDoc = "템플릿 번호",

						columnProperties = @ColumnProperties(columnName = "TEMPLATENUMBER", unique = true), constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "enable", type = boolean.class, initialValue = "true") }

)

public class Template extends _Template {

	static final long serialVersionUID = 1;

	public static Template newTemplate() throws WTException {
		Template instance = new Template();
		instance.initialize();
		return instance;
	}
}
