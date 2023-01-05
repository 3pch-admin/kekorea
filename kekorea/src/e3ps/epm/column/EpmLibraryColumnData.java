package e3ps.epm.column;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.ThumnailUtils;
import wt.epm.EPMDocument;
import wt.session.SessionHelper;

public class EpmLibraryColumnData {

	// 목록에서 보여 줄것만해서
	public String oid;
	public String[] thumnail;
	public String name;
	public String part_code;
	public String name_of_parts;
	public String dwg_no;
	public String material;
	public String remark;
	public String reference;
	public String version;
	public String modifier;
	public String modifyDate;
	public String creator;
	public String createDate;
	public String state;
	public String location;
	public String[] primary;
	// 기타
	public String iconPath;
	public String creoView;

	public EpmLibraryColumnData(EPMDocument epm) throws Exception {
		String cadType = epm.getAuthoringApplication().toString();
		this.oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
		this.thumnail = ThumnailUtils.getThumnail(this.oid);
		this.name = epm.getCADName();
		this.part_code = IBAUtils.getStringValue(epm, "PART_CODE");

		if ("PROE".equals(cadType)) {
			this.name_of_parts = IBAUtils.getStringValue(epm, "NAME_OF_PARTS");
			this.dwg_no = IBAUtils.getStringValue(epm, "DWG_NO");
		} else if ("ACAD".equals(cadType)) {
			this.dwg_no = IBAUtils.getStringValue(epm, "DWG_No");
			this.name_of_parts = IBAUtils.getStringValue(epm, "TITLE1") + " " + IBAUtils.getStringValue(epm, "TITLE2");
		} else {
			this.dwg_no = epm.getNumber();
			this.name_of_parts = epm.getName();
		}

		this.material = IBAUtils.getStringValue(epm, "MATERIAL");
		this.remark = IBAUtils.getStringValue(epm, "REMARKS");
		this.reference = IBAUtils.getStringValue(epm, "REF_NO");
		this.version = epm.getVersionIdentifier().getSeries().getValue() + "."
				+ epm.getIterationIdentifier().getSeries().getValue();
		this.modifier = epm.getModifierName();
		this.modifyDate = epm.getModifyTimestamp().toString().substring(0, 16);
		this.location = epm.getLocation().substring(9);
		this.creator = epm.getCreatorFullName();
		this.createDate = epm.getCreateTimestamp().toString().substring(0, 16);
		this.state = epm.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.primary = ContentUtils.getPrimary(epm);
		this.location = epm.getFolderPath();
		this.iconPath = ContentUtils.getOpenIcon(this.oid);
		this.creoView = ThumnailUtils.creoViewURL(this.oid);
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
			// } else if (key.equals("number")) {
			// value = this.number;
		} else if (key.equals("version")) {
			value = this.version;
		} else if (key.equals("state")) {
			value = this.state;
		} else if (key.equals("modifier")) {
			value = this.modifier;
		} else if (key.equals("modifyDate")) {
			value = this.modifyDate;
		}
		return value;
	}
}