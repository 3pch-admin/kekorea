package e3ps.project.schedule;

import java.util.Properties;

import com.ptc.wvs.server.schedule.Schedulable;
import com.ptc.wvs.server.schedule.ScheduledJobProcessor;

public class KEKScheduleProcessor extends ScheduledJobProcessor {

	public KEKScheduleProcessor(String var1) {
		super(var1);
	}

	@Override
	protected void doScheduleJob(Schedulable var1, boolean var2, String var3, Properties var4) {
		if ("KEK_PROJECT_SCHEDULE_BATCH".equals(var1.getIdentifier())) { // 프로젝트 일배치 23:00
			System.out.println("KEK BATCH JOB START!");
			KEKScheduleJobs.startBatch();
			System.out.println("KEK BATCH JOB END!");
		}
	}
}
