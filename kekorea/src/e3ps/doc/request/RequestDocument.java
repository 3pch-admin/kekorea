package e3ps.doc.request;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.doc.WTDocument;
import wt.util.WTException;

@GenAsPersistable(superClass = WTDocument.class)
public class RequestDocument extends _RequestDocument {

	static final long serialVersionUID = 1;

	public static RequestDocument newRequestDocument() throws WTException {
		RequestDocument instance = new RequestDocument();
		instance.initialize();
		return instance;
	}
}
