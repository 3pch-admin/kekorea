package e3ps.korea.cip;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class },

		properties = {

				@GeneratedProperty(name = "item", type = String.class, javaDoc = "항목"),

				@GeneratedProperty(name = "improvements", type = String.class, javaDoc = "개선항목"),

				@GeneratedProperty(name = "improvement", type = String.class, javaDoc = "개선책"),

				@GeneratedProperty(name = "apply", type = String.class, javaDoc = "적용/미적용"),

				@GeneratedProperty(name = "note", type = String.class, javaDoc = "비고"),

		}

)
public class Cip extends _Cip {

	static final long serialVersionUID = 1;

	public static Cip newCip() throws WTException {
		Cip instance = new Cip();
		instance.initialize();
		return instance;
	}
}
