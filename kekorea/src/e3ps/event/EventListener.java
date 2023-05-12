package e3ps.event;

import e3ps.org.service.OrgHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.events.KeyedEvent;
import wt.fc.PersistenceManagerEvent;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.org.WTUser;
import wt.services.ServiceEventListenerAdapter;
import wt.vc.wip.WorkInProgressServiceEvent;

public class EventListener extends ServiceEventListenerAdapter {

	private static final String POST_STORE = PersistenceManagerEvent.POST_STORE;
	private static final String POST_MODIFY = PersistenceManagerEvent.POST_MODIFY;
	private static final String POST_CHECKIN = WorkInProgressServiceEvent.POST_CHECKIN;
	private static final String STATE_CHANGE = LifeCycleServiceEvent.STATE_CHANGE;

	public EventListener(String s) {
		super(s);
	}

	public void notifyVetoableEvent(Object obj) throws Exception {
		if (!(obj instanceof KeyedEvent)) {
			return;
		}

		KeyedEvent keyedEvent = (KeyedEvent) obj;
		Object target = keyedEvent.getEventTarget();
		String type = keyedEvent.getEventType();

		if (target instanceof WTDocument) {
			if (STATE_CHANGE.equals(type)) {
				EventHelper.service.detectTask((LifeCycleManaged) target);
			}
		}

		if (target instanceof EPMDocument) {
			if (POST_CHECKIN.equals(type)) {
				EPMDocument epm = (EPMDocument) target;
				System.out.println("epm=" + epm.getAuthoringApplication());
				if (epm.getAuthoringApplication().toString().equals("ACAD")) {
					System.out.println("AutoCAD 변환 시작!!");
					EventHelper.service.convertAutoCADToPDF(epm);
				}
			} else if(POST_STORE.equals(type)) {
				EPMDocument epm = (EPMDocument) target;
				System.out.println("epm11=" + epm.getAuthoringApplication());
			}
		}

		if (target instanceof WTUser) {
			WTUser wtUser = (WTUser) target;
			if (POST_STORE.equals(type)) {
				OrgHelper.service.save(wtUser);
			} else if (POST_MODIFY.equals(type)) {
				OrgHelper.service.modify(wtUser);
			}
		}
	}
}