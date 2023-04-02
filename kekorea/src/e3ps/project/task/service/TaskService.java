package e3ps.project.task.service;

import java.util.ArrayList;

import e3ps.project.task.Task;
import java.util.ArrayList;

import e3ps.project.task.Task;
import wt.method.RemoteInterface;

@RemoteInterface
public interface TaskService {

	/**
	 * 자식들 체크하여 일전 조절
	 */
	public abstract void calculation(ArrayList<Task> list) throws Exception;
}
