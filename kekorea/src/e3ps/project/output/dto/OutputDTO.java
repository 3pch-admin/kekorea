package e3ps.project.output.dto;

import java.sql.Timestamp;

import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;

@Getter
@Setter
public class OutputDTO {

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

	public OutputDTO() {

	}

	public OutputDTO(WTDocument output) throws Exception {
		setOid(output.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(output.getName());
		setNumber(output.getNumber());
		setDescription(StringUtils.replaceToValue(output.getDescription()));
//		setLocation(document.getLocation());
		setState(output.getLifeCycleState().getDisplay());
//		setVersion(CommonUtils.getFullVersion(document));
		setCreator(output.getCreatorFullName());
		setCreatedDate(output.getCreateTimestamp());
		setModifier(output.getModifierFullName());
		setModifiedDate(output.getModifyTimestamp());
		setDocType(output.getDocType().getDisplay());
	}
}

