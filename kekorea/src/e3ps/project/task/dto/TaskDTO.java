package e3ps.project.task.dto;

import java.sql.Timestamp;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
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
	private int holiday;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private int allocate;
	private String taskType;
	private String planStartDate_txt;
	private String planEndDate_txt;
	private String startDate_txt;
	private String endDate_txt;
	private int progress;

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
		setPlanStartDate_txt(CommonUtils.getPersistableTime(task.getPlanStartDate()));
		setPlanEndDate_txt(CommonUtils.getPersistableTime(task.getPlanEndDate()));
		setStartDate_txt(CommonUtils.getPersistableTime(task.getStartDate()));
		setEndDate_txt(CommonUtils.getPersistableTime(task.getEndDate()));
		setProgress(task.getProgress());
		setDuration(DateUtils.getDuration(task.getPlanStartDate(), task.getPlanEndDate()));
		setHoliday(DateUtils.getPlanDurationHoliday(task.getPlanStartDate(), task.getPlanEndDate()));
	}
}
