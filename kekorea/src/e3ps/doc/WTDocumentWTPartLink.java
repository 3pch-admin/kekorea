package e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.doc.WTDocument;
import wt.fc.ObjectToObjectLink;
import wt.part.WTPart;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "document", type = WTDocument.class),

		roleB = @GeneratedRole(name = "part", type = WTPart.class))

public class WTDocumentWTPartLink extends _WTDocumentWTPartLink {
	static final long serialVersionUID = 1;

	public static WTDocumentWTPartLink newWTDocumentWTPartLink(WTDocument document, WTPart part) throws WTException {
		WTDocumentWTPartLink instance = new WTDocumentWTPartLink();
		instance.initialize(document, part);
		return instance;
	}

}
