package e3ps.approval.column;

import e3ps.approval.Notice;
import e3ps.common.util.ContentUtils;

public class NoticeColumnData {

	public String oid;
	public String name;
	public String description;
	public String creator;
	public String createDate;

	public String[] primary;
	public String iconPath;

	public NoticeColumnData(Notice notice) throws Exception {
		this.oid = notice.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = notice.getName();
		this.description = notice.getDescription() != null ? notice.getDescription() : "";
		this.creator = notice.getOwnership().getOwner().getFullName();
		this.createDate = notice.getCreateTimestamp().toString().substring(0, 16);
		this.primary = ContentUtils.getPrimary(this.oid);
		this.iconPath = "/Windchill/jsp/images/notice.png";
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("description")) {
			value = this.description;
		} else if (key.equals("creator")) {
			value = this.creator;
		} else if (key.equals("createDate")) {
			value = this.createDate;
		}
		return value;
	}
}
