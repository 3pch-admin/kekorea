package e3ps.project.service;

import java.sql.Timestamp;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import wt.method.RemoteAccess;

public class Schedulers implements Job, RemoteAccess {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("스케줄링 시작..");
		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("시작시간=" + start);
		try {
			SchedulingMethod.startTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
