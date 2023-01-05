package e3ps.part.column;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.ThumnailUtils;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.session.SessionHelper;

public class PartLibraryColumnData {

	public String oid;
	public String[] thumnail;
	public String name;

	public String part_code;

	public String name_of_parts;
	public String number;

	public String material;
	public String remark;
	public String maker;

	public String version;
	public String creator;

	public String createDate;

	public String modifier;
	public String modifyDate;
	public String state;
	public String location;
	public String[] primary;

	public String creoView;
	// 기타
	public String iconPath;

	public PartLibraryColumnData(WTPart part) throws Exception {
		EPMDocument epm = e3ps.part.service.PartHelper.manager.getEPMDocument(part);
		String cadType = "";
		if (epm != null) {
			cadType = epm.getAuthoringApplication().toString();
		}
		this.oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
		this.thumnail = ThumnailUtils.getThumnail(this.oid);
		this.name = part.getName();// IBAUtils.getStringValue(part, "PRODUCT_NAME");
		this.part_code = IBAUtils.getStringValue(part, "PART_CODE");

		if ("PROE".equals(cadType)) {
			this.name_of_parts = IBAUtils.getStringValue(part, "NAME_OF_PARTS");
			this.number = IBAUtils.getStringValue(part, "DWG_NO");
		} else if ("ACAD".equals(cadType)) {
			this.number = IBAUtils.getStringValue(part, "DWG_No");
			this.name_of_parts = IBAUtils.getStringValue(part, "TITLE1") + " "
					+ IBAUtils.getStringValue(part, "TITLE2");
		} else {
			this.number = part.getNumber();
			this.name_of_parts = part.getName();
		}

		this.material = IBAUtils.getStringValue(part, "MATERIAL");
		this.remark = IBAUtils.getStringValue(part, "REMARKS");
		this.maker = IBAUtils.getStringValue(part, "MAKER");
		this.version = part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue();
		this.creator = part.getCreatorFullName();
		this.createDate = part.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = part.getModifierFullName();
		this.modifyDate = part.getModifyTimestamp().toString().substring(0, 16);
		this.state = part.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());

		if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
			this.location = part.getLocation().substring(9);
		} else if (part.getContainer().getName().equalsIgnoreCase("EPLAN")) {
			this.location = part.getLocation();
		}

		this.primary = ContentUtils.getPrimary(part);
		this.iconPath = ContentUtils.getOpenIcon(this.oid);
		this.creoView = ThumnailUtils.creoViewURL(this.oid);
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("number")) {
			value = this.number;
		} else if (key.equals("state")) {
			value = this.state;
		} else if (key.equals("version")) {
			value = this.version;
		}
		return value;
	}
}