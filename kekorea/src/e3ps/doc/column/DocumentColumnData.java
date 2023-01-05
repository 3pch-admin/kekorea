package e3ps.doc.column;

import e3ps.common.util.ContentUtils;
import wt.doc.WTDocument;
import wt.session.SessionHelper;

public class DocumentColumnData {

	// OID 1번
	public String oid;
	// 리스트 컬럼
	public String name;
	public String number; // 문서 번호
	public String description;
	public String location;
	// public String kek_number; // 작번
	// public String kek_description; // 작업내용
	// public String mak;
	public String state;
	public String version;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
	public String[] primary;
	// 아이콘
	public String iconPath;

	public DocumentColumnData(WTDocument document) throws Exception {
		this.oid = document.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = document.getName();
		this.number = document.getNumber();
		// this.work = "";
		this.description = document.getDescription();

		System.out.println(document.getContainerName());
		if (document.getContainerName().equalsIgnoreCase("EPLAN")) {
			this.location = document.getLocation();
		} else {
			this.location = document.getLocation().substring(12, document.getLocation().length());
		}

		// this.kek_number = project != null ? project.getKekNumber() : "";
		// this.kek_description = project != null ?
		// StringUtils.replaceToValue(project.getDescription()) : "";
		// this.mak = project != null ? project.getMak() : "";
		this.state = document.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.version = document.getVersionIdentifier().getSeries().getValue() + "."
				+ document.getIterationIdentifier().getSeries().getValue();
		this.creator = document.getCreatorFullName();
		this.createDate = document.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = document.getModifierFullName();
		this.modifyDate = document.getModifyTimestamp().toString().substring(0, 16);
		this.primary = ContentUtils.getPrimary(this.oid);

		this.iconPath = ContentUtils.getStandardIcon(this.oid);
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
		} else if (key.equals("modifier")) {
			value = this.modifier;
		} else if (key.equals("modifyDate")) {
			value = this.modifyDate;
		}
		return value;
	}
	
	
}
