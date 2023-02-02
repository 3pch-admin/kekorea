package e3ps.korea.cssheet;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "CS SHEET 명") }

)
public class CSSheet extends _CSSheet {

	static final long serialVersionUID = 1;

	public static CSSheet newCSSheet() throws WTException {
		CSSheet instance = new CSSheet();
		instance.initialize();
		return instance;
	}
}
