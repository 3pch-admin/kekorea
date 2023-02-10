package e3ps.project.template.beans;

import java.sql.Timestamp;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.template.Template;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateColumnData {

	private String oid;
	private String name;
	private String description;
	private int duration;
	private boolean enable;
	private String creator;
	private Timestamp createDate;

	public TemplateColumnData() {

	}

	public TemplateColumnData(Template template) throws Exception {
		setOid(template.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(template.getName());
		setDescription(StringUtils.replaceToValue(template.getDescription(), ""));
		setDuration(template.getDuration());
		setEnable(template.getEnable());
		setCreator(template.getOwnership().getOwner().getFullName());
		setCreateDate(template.getCreateTimestamp());
	}
}
