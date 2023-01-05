package e3ps.doc;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.IconProperties;

import wt.doc.WTDocument;
import wt.util.WTException;

//제목 번호 설명
@GenAsPersistable(superClass = WTDocument.class, interfaces = {},

		iconProperties = @IconProperties(openIcon = "jsp/images/requestdocument.png", standardIcon = "jsp/images/requestdocument.png"),

		properties = {

		},

		foreignKeys = {

		}

)
public class RequestDocument extends _RequestDocument {

	static final long serialVersionUID = 1;

	public static RequestDocument newRequestDocument() throws WTException {
		RequestDocument instance = new RequestDocument();
		instance.initialize();
		return instance;
	}
}
