package e3ps.project.task.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.project.task.Task;
import wt.method.RemoteInterface;

@RemoteInterface
public interface TaskService {

	/**
	 * 자식들 체크하여 일정 조절
	 */
	public abstract void calculation(ArrayList<Task> list) throws Exception;

	/**
	 * 태스크 진행율 수정
	 */
	public abstract void editProgress(Map<String, Object> params) throws Exception;
}
