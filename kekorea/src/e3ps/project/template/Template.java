package e3ps.project.template;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import e3ps.project.ProjectImpl;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ProjectImpl.class },

		properties = {

				@GeneratedProperty(name = "enable", type = Boolean.class, initialValue = "true") }

)

public class Template extends _Template {

	static final long serialVersionUID = 1;

	public static Template newTemplate() throws WTException {
		Template instance = new Template();
		instance.initialize();
		return instance;
	}
}
