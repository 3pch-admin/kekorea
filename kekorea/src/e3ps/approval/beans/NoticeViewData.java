package e3ps.approval.beans;

import java.util.Vector;

import e3ps.approval.Notice;
import e3ps.common.util.ContentUtils;
import e3ps.org.Department;

public class NoticeViewData {

	public Notice notice;
	public String oid;
	public String name;
	public String creator;
	public String createDate;
	public String description;
	public String iconPath;
	public Department department;
	public Vector<String[]> secondarys;

	public boolean isModify = false;

	public boolean isCreator = false;

	public NoticeViewData(Notice notice) throws Exception {
		this.notice = notice;
		this.oid = notice.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = notice.getName();
		this.description = notice.getDescription() != null ? notice.getDescription() : "";
		this.creator = notice.getOwnership().getOwner().getFullName();
		this.createDate = notice.getCreateTimestamp().toString().substring(0, 16);
		this.iconPath = "/Windchill/jsp/images/notice.png";
		this.department = notice.getDepartment();
		this.secondarys = ContentUtils.getSecondary(this.notice);
	}
}
