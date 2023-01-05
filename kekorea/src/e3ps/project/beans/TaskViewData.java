package e3ps.project.beans;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.ParentTaskChildTaskLink;
import e3ps.project.Task;
import e3ps.project.enums.TaskStateType;
import e3ps.project.service.ProjectHelper;
import wt.fc.PersistenceHelper;

public class TaskViewData {

	public Task task;
	public String oid;
	public String name;
	public String description;
	public String state;
	public int duration;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
	public String iconPath;
	public String taskType;
	public int allocate;
	public String planStartDate;
	public String planEndDate;
	public String startDate;
	public String endDate;

	public boolean isEnd = false;

	public boolean isEditer = false;
	public boolean isPartList = false;

	public boolean isElecPartList = false;

	public boolean isMachinePartList = false;

	public boolean isNormalTask = false;
	public boolean isReq = false;

	public int progress;

	public boolean isComplete = false;

	public double comp = 0D;

	public int holiday;

	public TaskViewData(Task task) throws Exception {
		this.task = task;
		this.oid = task.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = task.getName();
		this.description = StringUtils.replaceToValue(task.getDescription());
		// this.duration = task.getDuration() + "일";

		this.creator = task.getOwnership().getOwner().getFullName();
		this.createDate = task.getCreateTimestamp().toString().substring(0, 10);
		this.iconPath = ContentUtils.getStandardIcon(this.task);
		this.modifier = task.getUpdateUser().getOwner().getFullName();
		this.modifyDate = task.getModifyTimestamp().toString().substring(0, 10);
		this.taskType = task.getTaskType();

		this.allocate = task.getAllocate() != null ? task.getAllocate() : 0;
		this.planStartDate = task.getPlanStartDate() != null ? task.getPlanStartDate().toString().substring(0, 10) : "";
		this.planEndDate = task.getPlanEndDate() != null ? task.getPlanEndDate().toString().substring(0, 10) : "";
		this.startDate = task.getStartDate() != null ? task.getStartDate().toString().substring(0, 10) : "";
		this.endDate = task.getEndDate() != null ? task.getEndDate().toString().substring(0, 10) : "";

		this.isEnd = PersistenceHelper.manager.navigate(this.task, "childTask", ParentTaskChildTaskLink.class)
				.size() > 0 ? false : true;

		this.isReq = task.getName().equals("의뢰서");
		this.isPartList = task.getName().contains("수배표") || task.getName().contains("수배");
		this.isElecPartList = task.getName().contains("전기") && task.getName().contains("수배표");
		this.isMachinePartList = task.getName().contains("기계") && task.getName().contains("수배표");

		// 일반 태스크 = 단순 산출물 등록 % 입력 필요 없음ㅁ
		// 공통 태스크 = 산출물 등록 및 % 관여
		// 전기 기계 = 산출물 등록 및 % 관여
		// SW

		// if (task.getTaskType().equals("일반") || task.getAllocate() == 0) {
		if (task.getTaskType().equals("일반")) {
			this.isNormalTask = true;
		}
		this.state = task.getState();
		this.progress = task.getProgress();
//		this.isEditer = ProjectHelper.manager.isEditer(task.getProject());
		this.isEditer = true;

		this.duration = DateUtils.getDuration(task.getPlanStartDate(), task.getPlanEndDate());
		this.holiday = DateUtils.getPlanDurationHoliday(task.getPlanStartDate(), task.getPlanEndDate());

		if (this.state != null) {
			this.isComplete = this.state.equals(TaskStateType.COMPLETE.getDisplay());
		}
		this.comp = ProjectHelper.getPreferComp(task);
	}
}