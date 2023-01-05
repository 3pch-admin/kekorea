package e3ps.event;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		HttpSession session = arg0.getSession();
		long time = session.getCreationTime();
		String id = session.getId();
		System.out.println(time + "에 생성된 세션" + id);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// 세션 만료시 호출
		HttpSession session = arg0.getSession();

//		ServletContext context = session.getServletContext();

		long time = session.getCreationTime();

		long last_time = session.getLastAccessedTime();

		long now_time = System.currentTimeMillis();

		String id = session.getId();

		System.out.println("세션 생성타임.." + time);

		System.out.println((now_time - last_time) + "ms 만에 세션이 죽음" + id);

//		session.invalidate();

	}

}
