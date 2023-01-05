package e3ps.project;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ProjectImpl.class },

		iconProperties = @IconProperties(standardIcon = "/jsp/images/template.gif", openIcon = "/jsp/images/template.gif"),

		properties = {

				@GeneratedProperty(name = "number", type = String.class, javaDoc = "템플릿 번호", columnProperties = @ColumnProperties(columnName = "TEMPLATENUMBER")),

				@GeneratedProperty(name = "state", type = String.class, javaDoc = "상태", columnProperties = @ColumnProperties(columnName = "TEMPLATESTATE")),

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
