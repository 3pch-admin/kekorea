package e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.util.WTException;

@GenAsPersistable(superClass = E3PSDocument.class,

		extendable = true

)
public class PRJDocument extends _PRJDocument {

	static final long serialVersionUID = 1;

	public static PRJDocument newPRJDocument() throws WTException {
		PRJDocument instance = new PRJDocument();
		instance.initialize();
		return instance;
	}
}
