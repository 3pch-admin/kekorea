package e3ps.project.service;

import java.io.Serializable;

import wt.method.RemoteAccess;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTProperties;

public class SchedulingMethod implements Serializable, RemoteAccess {

	public static void _startTask() throws Exception {
		boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
		SessionContext sessioncontext = SessionContext.newContext();

		try {

			SessionHelper.manager.setAdministrator();

			ProjectScheduler.manager.setStartTask();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SessionContext.setContext(sessioncontext);
			SessionServerHelper.manager.setAccessEnforced(bool);
		}

	}

	public static void startTask() throws Exception {

		try {

			WTProperties prover = WTProperties.getServerProperties();
			String server = prover.getProperty("wt.cache.master.codebase");

			if (server == null) {
				server = prover.getProperty("wt.server.codebase");
			}
//			Object obj = RemoteMethodServer.getInstance(new URL(server + "/"), "MethodServer").invoke("_startTask",
//					SchedulingMethod.class.getName(), null, null, null);
			return;
		} catch (Exception e) {
			throw e;
		}
	}

	public static void main(String args[]) {

	}
}