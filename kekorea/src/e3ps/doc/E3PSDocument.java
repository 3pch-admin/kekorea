package e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.doc.WTDocument;
import wt.util.WTException;

@GenAsPersistable(superClass = WTDocument.class,

		extendable = true

)

public class E3PSDocument extends _E3PSDocument {

	static final long serialVersionUID = 1;

	public static E3PSDocument newE3PSDocument() throws WTException {
		E3PSDocument instance = new E3PSDocument();
		instance.initialize();
		return instance;
	}
}
