package e3ps.project.template.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.template.Template;
import e3ps.project.template.service.TemplateHelper;
import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class TemplateDTO {

	private String oid;
	private String name;
	private String description;
	private int duration;
	private boolean enable;
	private String creator;
	private String creatorId;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private String modifierId;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	
	@JsonIgnore
	private WTUser pm;
	@JsonIgnore
	private WTUser subPm;

	public TemplateDTO() {

	}

	public TemplateDTO(Template template) throws Exception {
		setOid(template.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(template.getName());
		setDescription(StringUtils.replaceToValue(template.getDescription(), ""));
		setDuration(template.getDuration());
		setEnable(template.getEnable());
		setCreator(template.getOwnership().getOwner().getFullName());
		setCreatorId(template.getOwnership().getOwner().getName());
		setCreatedDate(template.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(template.getCreateTimestamp()));
		setModifier(template.getUpdateUser().getOwner().getFullName());
		setModifierId(template.getUpdateUser().getOwner().getName());
		setModifiedDate(template.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(template.getModifyTimestamp()));
		setPm(TemplateHelper.manager.getUserType(template, "PM"));
		setSubPm(TemplateHelper.manager.getUserType(template, "SUB_PM"));
	}
}
