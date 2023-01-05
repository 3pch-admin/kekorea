package e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.doc.WTDocumentMaster;
import wt.util.WTException;

@GenAsPersistable(superClass = WTDocumentMaster.class,

		extendable = true

)

public class E3PSDocumentMaster extends _E3PSDocumentMaster {

	static final long serialVersionUID = 1;

	public static E3PSDocumentMaster newE3PSDocumentMaster() throws WTException {
		E3PSDocumentMaster instance = new E3PSDocumentMaster();
		instance.initialize();
		return instance;
	}
}
