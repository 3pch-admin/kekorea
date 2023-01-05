package e3ps.project.column;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.project.Template;

public class TemplateColumnData {

	public String oid;
	public String name;
	public String duration;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
	// 기타
	public String iconPath;

	public TemplateColumnData(Template template) throws Exception {
		this.oid = template.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = template.getName();
		this.duration = DateUtils.getDuration(template.getPlanStartDate(), template.getPlanEndDate()) + "일";
		// this.duration = String.valueOf(template.getDuration()) + "일";
		this.creator = template.getOwnership().getOwner().getFullName();
		this.createDate = template.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = template.getUpdateUser().getOwner().getFullName();
		this.modifyDate = template.getModifyTimestamp().toString().substring(0, 16);
		this.iconPath = ContentUtils.getOpenIcon(oid);
	}
}
