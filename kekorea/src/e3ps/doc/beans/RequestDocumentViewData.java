package e3ps.doc.beans;

import e3ps.common.util.StringUtils;
import e3ps.doc.RequestDocument;
import e3ps.doc.service.DocumentHelper;

public class RequestDocumentViewData {

	public RequestDocument reqDoc;;
	public String oid;
	public String name;
	public String description;

	public String jsonList;

	public RequestDocumentViewData(RequestDocument reqDoc) throws Exception {
		this.reqDoc = reqDoc;
		this.oid = reqDoc.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = reqDoc.getName();
		this.description = StringUtils.replaceToValue(reqDoc.getDescription());

		this.jsonList = DocumentHelper.manager.getJsonList(reqDoc);
	}
}