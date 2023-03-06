package e3ps.project.task.dto;

import java.util.ArrayList;

public class TaskTreeNode {

	private String oid;
	private String name;
	private String description;
	private int duration;
	private String planStartDate;
	private String planEndDate;
	private String startDate;
	private String endDate;
	private String taskType;
	private boolean isNew;
	private int _$depth;
	private ArrayList<TaskTreeNode> children = new ArrayList<>();

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(String planStartDate) {
		this.planStartDate = planStartDate;
	}

	public String getPlanEndDate() {
		return planEndDate;
	}

	public void setPlanEndDate(String planEndDate) {
		this.planEndDate = planEndDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public int get_$depth() {
		return _$depth;
	}

	public void set_$depth(int _$depth) {
		this._$depth = _$depth;
	}

	public ArrayList<TaskTreeNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TaskTreeNode> children) {
		this.children = children;
	}

}