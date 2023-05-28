package e3ps.common.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.service.WorkspaceHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTProperties;

public class MailUtils {

	public static final boolean isMail = true;
//	private static final String host = "kokusai-electric.com";
	private static final String port = "465";

	private static final String host = "3pchain.co.kr";
	
	
	private MailUtils() {

	}

	public static void test() throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		InternetAddress to = new InternetAddress();
		to = new InternetAddress("jhkim@3pchain.co.kr");

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject("비밀번호 초기화");
		MimeBodyPart body = new MimeBodyPart();
		body.setContent("AA", "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	private static String getObjMsg(Persistable per) throws Exception {
		String objMsg = "";
		if (per instanceof WTDocument) {
			objMsg = "문서";

		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			if (part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
				objMsg = "가공품";
			} else if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
				objMsg = "구매품";
			}

		} else if (per instanceof EPMDocument) {
			objMsg = "도면";
		}
		return objMsg;
	}

	public static void sendNextMail(ApprovalLine line) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		// 보내는 사람
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		String toMail = line.getOwnership().getOwner().getEMail();
		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		Persistable per = line.getMaster().getPersist();

		String obj = getObjMsg(per);
		String header = "[PDM 결재(승인요청) - " + obj + "]";

		String content = getObjContent(sessionUser, line, "승인ㄹ");

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject(header);
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(content, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	public static void sendReturnMail(ApprovalMaster master, ApprovalLine lines) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		// 보내는 사람
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		// 기안자 에게로

		ApprovalLine line = WorkspaceHelper.manager.getFirstLine(master);

		String toMail = line.getOwnership().getOwner().getEMail();
		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		Persistable per = line.getMaster().getPersist();

		String obj = getObjMsg(per);
		String header = "[PDM 결재(반려) - " + obj + "]";

		// 결제되는 라인 OID
		String content = getObjContent(sessionUser, lines, "반려");

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject(header);
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(content, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	public static void sendEndMail(ApprovalMaster master, ApprovalLine lines) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		// 보내는 사람
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		// 기안자 에게로

		ApprovalLine line = WorkspaceHelper.manager.getFirstLine(master);

		String toMail = line.getOwnership().getOwner().getEMail();
		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		Persistable per = line.getMaster().getPersist();

		String obj = getObjMsg(per);
		String header = "[PDM 결재(최종승인) - " + obj + "]";

		// 결제되는 라인 OID
		String content = getObjContent(sessionUser, lines, "최종승인");

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject(header);
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(content, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	// 결재 첫 시작...
	public static void sendFirstMail(ApprovalLine line) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		// 보내는 사람
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		String toMail = line.getOwnership().getOwner().getEMail();
		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		Persistable per = line.getMaster().getPersist();

		String obj = getObjMsg(per);
		String header = "[PDM 결재(상신) - " + obj + "]";

		String content = getObjContent(sessionUser, line, "상신");

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject(header);
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(content, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	private static String getObjContent(WTUser sessionUser, ApprovalLine line, String types) {
		Persistable per = line.getMaster().getPersist();
		String oid = line.getPersistInfo().getObjectIdentifier().getStringValue();

		String link = codebase + "/plm/approval/infoApproval?oid=" + oid;

		String startHref = "<a href=\"" + link + "\">";

		String objContent = sessionUser.getFullName() + " 사용자가<br>";
		if (per instanceof WTDocument) {

			WTDocument document = (WTDocument) per;
			objContent += startHref + "문서[" + document.getName() + "] 결재를 " + types + "하였습니다.</a>";

		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			String title = "";
			if (part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
				title = "가공품";
			} else if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
				title = "구매품";
			}
			objContent += startHref + title + "[" + part.getNumber() + "] 결재를 " + types + "하였습니다.</a>";
		} else if (per instanceof EPMDocument) {
			// objMsg = "도면";
		}

		objContent += "<br>링크를 클릭해서 해당 업무를 진행 해주세요.";

		objContent += "<br><br><img src=\"" + codebase + "/jsp/images/mail_footer.png\">";
		return objContent;
	}

	public static void sendReassignMail(String toMail, ApprovalLine line) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		String msg = sessionUser.getFullName() + " 사용자가 " + line.getName() + " 결재를 위임 하였습니다.";

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject("결재위임 - " + line.getName());
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(msg, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	public static void sendInitPasswordMail(String toMail, String password) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		String msg = "사용자의 PDM 비밀번호가 변경되었습니다.<br>";
		msg += "변경된 비밀번호는 " + password + " 입니다.";

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject("비밀번호 초기화");
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(msg, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	private static Session getSession() throws Exception {
		Properties prop = new Properties();
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.port", port);
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// Session session = Session.getDefaultInstance(prop);
		//
		Session session = Session.getDefaultInstance(prop, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// return new PasswordAuthentication(ID, PW);
				return new PasswordAuthentication("jhkim@e3ps.com", "e3ps.windchill");
			}
		});

		// session.setDebug(true);
		session.setDebug(false);
		return session;
	}

	public static Map<String, Object> sendCommonMail(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Session session = getSession();

			String toMail = (String) param.get("toMail");
			String name = (String) param.get("name");
			String description = (String) param.get("description");

			Message message = new MimeMessage(session);
			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

			InternetAddress to = new InternetAddress();
			to = new InternetAddress(toMail);

			message.setRecipient(Message.RecipientType.TO, to);
			message.setSubject(name);
			MimeBodyPart body = new MimeBodyPart();
			body.setContent(description, "text/html; charset=EUC-KR");
			Multipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(body);
			message.setContent(multiPart);
			message.setSentDate(new Date());
			Transport.send(message);

			map.put("result", "SUCCESS");
			map.put("msg", "메일이 전송 되었습니다.");
		} catch (Exception e) {
			map.put("result", "FAIL");
			map.put("msg", "메일 전송에 실패하였습니다.");
			map.put("url", "/Windchill/plm/document/createDocument");
			e.printStackTrace();
		}
		return map;
	}

	public static void sendAgreeMail(ApprovalLine agreeLine) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		// 보내는 사람
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		String toMail = agreeLine.getOwnership().getOwner().getEMail();
		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		Persistable per = agreeLine.getMaster().getPersist();

		String obj = getObjMsg(per);
		String header = "[PDM 합의 - " + obj + "]";

		String content = getObjContent(sessionUser, agreeLine, "상신");

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject(header);
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(content, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	public static void sendReceiveMail(ApprovalLine receiveLine) throws Exception {
		Session session = getSession();

		Message message = new MimeMessage(session);
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		// 보내는 사람
		message.setFrom(new InternetAddress(sessionUser.getEMail(), sessionUser.getFullName()));

		String toMail = receiveLine.getOwnership().getOwner().getEMail();
		InternetAddress to = new InternetAddress();
		to = new InternetAddress(toMail);

		Persistable per = receiveLine.getMaster().getPersist();

		String obj = getObjMsg(per);
		String header = "[PDM 수신 - " + obj + "]";

		String content = getObjContent(sessionUser, receiveLine, "상신");

		message.setRecipient(Message.RecipientType.TO, to);
		message.setSubject(header);
		MimeBodyPart body = new MimeBodyPart();
		body.setContent(content, "text/html; charset=EUC-KR");
		Multipart multiPart = new MimeMultipart();
		multiPart.addBodyPart(body);
		message.setContent(multiPart);
		message.setSentDate(new Date());
		Transport.send(message);
	}
}
