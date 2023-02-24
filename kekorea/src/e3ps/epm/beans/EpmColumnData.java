package e3ps.epm.beans;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.ThumnailUtils;
import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.session.SessionHelper;

@Getter
@Setter
public class EpmColumnData {

	private String oid;
	private String cadType;
	private String thumnail;
	private String name;
	private String part_code;
	private String name_of_parts;
	private String dwg_no;
	private String material;
	private String remark;
	private String reference;
	private String version;
	private String modifier;
	private Timestamp modifiedDate;
	private String creator;
	private Timestamp createdDate;
	private String state;
	private String location;
	private String[] primary;
	private String creoView;

	public EpmColumnData() {

	}

	public EpmColumnData(EPMDocument epm) throws Exception {
		setOid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
		setCadType(epm.getAuthoringApplication().toString());
		setThumnail(AUIGridUtils.getThumnailSmall(epm));
		setName(epm.getName());
		setMaterial(IBAUtils.getStringValue(epm, "MATERIAL"));
		setRemark(IBAUtils.getStringValue(epm, "REMARKS"));
		if (getCadType().equals("PROE")) {
			setName_of_parts(IBAUtils.getStringValue(epm, "NAME_OF_PARTS"));
			setDwg_no(IBAUtils.getStringValue(epm, "DWG_NO"));
		} else if (getCadType().equals("ACAD")) {
			setName_of_parts(IBAUtils.getStringValue(epm, "TITLE1") + " " + IBAUtils.getStringValue(epm, "TITLE2"));
			setDwg_no(IBAUtils.getStringValue(epm, "DWG_No"));
		} else {
			setName_of_parts(epm.getName());
			setDwg_no(epm.getNumber());
		}
		setReference(IBAUtils.getStringValue(epm, "REF_NO"));
		setPart_code(IBAUtils.getStringValue(epm, "PART_CODE"));
		setVersion(CommonUtils.getFullVersion(epm));
		setModifier(epm.getModifierFullName());
		setModifiedDate(epm.getModifyTimestamp());
		setCreator(epm.getCreatorFullName());
		setCreatedDate(epm.getCreateTimestamp());
		setState(epm.getLifeCycleState().getDisplay());
		setLocation(epm.getLocation());
		setCreoView(ThumnailUtils.creoViewURL(this.oid));
	}
}
