package e3ps.project.task.dto;

import java.sql.Timestamp;

import e3ps.common.util.CommonUtils;
import e3ps.project.task.Task;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import e3ps.project.task.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {

	private String oid;
	private String name;
	private String description;
	private String state;
	private int duration;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private int allocate;
	private String taskType;

	public TaskDTO() {

	}

	public TaskDTO(Task task) throws Exception {
		setOid(task.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(task.getName());
		setDescription(task.getDescription());
		setState(task.getState());
		setDuration(task.getDuration());
		setCreator(task.getOwnership().getOwner().getFullName());
		setCreatedDate(task.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(task.getCreateTimestamp()));
		setModifier(task.getUpdateUser().getOwner().getFullName());
		setModifiedDate(task.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(task.getModifyTimestamp()));
		setAllocate(task.getAllocate() != null ? task.getAllocate() : 0);
		setTaskType(task.getTaskType() != null ? task.getTaskType().getName() : "일반");
	}
}
