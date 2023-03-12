package e3ps.doc.dto;

import java.sql.Timestamp;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;

@Getter
@Setter
public class DocumentDTO {

	private String oid;
	private String name;
	private String number;
	private String description;
	private String location;
	private String state;
//	private String version;
	private String docType;
	private String creator;
	private Timestamp createdDate;
	private String modifier;
	private Timestamp modifiedDate;

	public DocumentDTO() {

	}

	public DocumentDTO(WTDocument document) throws Exception {
		setOid(document.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(document.getName());
		setNumber(document.getNumber());
		setDescription(StringUtils.replaceToValue(document.getDescription()));
//		setLocation(document.getLocation());
		setState(document.getLifeCycleState().getDisplay());
//		setVersion(CommonUtils.getFullVersion(document));
		setCreator(document.getCreatorFullName());
		setCreatedDate(document.getCreateTimestamp());
		setModifier(document.getModifierFullName());
		setModifiedDate(document.getModifyTimestamp());
		setDocType(document.getDocType().getDisplay());
	}
}
