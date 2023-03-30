package e3ps.part.beans;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.ThumnailUtils;
import e3ps.part.service.PartHelper;
import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.session.SessionHelper;

@Getter
@Setter
public class PartDTO {

	private String oid;
	private String cadType;
	private String thumnail;
	private String name;
	private String part_code;
	private String name_of_parts;
	private String number;
	private String material;
	private String remark;
	private String maker;
	private String version;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private String state;
	private String location;
	private String preView;

	public PartDTO() {

	}

	public PartDTO(WTPart part) throws Exception {
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);
		setOid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setThumnail(AUIGridUtils.getThumnailSmall(part));
		setName(part.getName());
		setPart_code(IBAUtils.getStringValue(part, "PART_CODE"));

		if (epm != null) {
			setCadType(epm.getAuthoringApplication().toString());
		}

		// 프로이
		if ("PROE".equals(getCadType())) {
			setNumber(IBAUtils.getStringValue(part, "DWG_NO"));
			setName_of_parts(IBAUtils.getStringValue(part, "NAME_OF_PARTS"));
			// 오토캐드
		} else if ("ACAD".equals(getCadType())) {
			setNumber(IBAUtils.getStringValue(part, "DWG_NO"));
			setName_of_parts(IBAUtils.getStringValue(part, "TITLE1") + " " + IBAUtils.getStringValue(part, "TITLE2"));
		} else {
			setNumber(part.getNumber());
			setName_of_parts(part.getName());
		}

		setMaterial(IBAUtils.getStringValue(part, "MATERIAL"));
		setRemark(IBAUtils.getStringValue(part, "REMARKS"));
		setMaker(IBAUtils.getStringValue(part, "MAKER"));
		setVersion(CommonUtils.getFullVersion(part));
		setCreator(part.getCreatorFullName());
		setCreatedDate(part.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(part.getCreateTimestamp()));
		setModifier(part.getModifierFullName());
		setModifiedDate(part.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(part.getModifyTimestamp()));
		setState(part.getLifeCycleState().getDisplay());
		setLocation(part.getLocation());
		setPreView(ContentUtils.getPreViewBase64(part));
	}
}
