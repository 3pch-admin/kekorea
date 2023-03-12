package e3ps.common.util;

import java.sql.Timestamp;
import java.util.Enumeration;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTValuedHashMap;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.VersionReference;

public class CommonUtils {

	private static ReferenceFactory rf = null;
	private static final String ADMIN_GROUP = "Administrators";

	private CommonUtils() {

	}

	/**
	 * 관리자 그룹인지 확인 하는 함수
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isAdmin() throws Exception {
		return isMember(ADMIN_GROUP);
	}

	/**
	 * 관리자 인지 확인 하는 함수
	 * 
	 * @param group : 그룹명
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isMember(String group) throws Exception {
		WTUser user = (wt.org.WTUser) SessionHelper.manager.getPrincipal();
		return isMember(group, user);
	}

	/**
	 * 관리자 인지 확인 하는 함수
	 * 
	 * @param group : 그룹명
	 * @param user  : 관리자 인지 확인할 객체
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isMember(String group, WTUser user) throws Exception {
		Enumeration en = user.parentGroupNames();
		while (en.hasMoreElements()) {
			String st = (String) en.nextElement();
			if (st.equals(group)) {
				return true;
			}
		}
		return false;
	}

	private static WTLibrary getEPLANContext() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTLibrary.class, true);
		SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, "=", "EPLAN");
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		WTLibrary context = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			context = (WTLibrary) obj[0];
		}
		return context;
	}

	private static WTLibrary getLibraryContext() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTLibrary.class, true);
		SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, "=", "LIBRARY");
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		WTLibrary context = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			context = (WTLibrary) obj[0];
		}
		return context;
	}

	private static PDMLinkProduct getPDMLinkContext() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PDMLinkProduct.class, true);
		SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, "=", "Commonspace");
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		PDMLinkProduct context = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			context = (PDMLinkProduct) obj[0];
		}
		return context;
	}

	public static WTContainerRef getEPLAN() throws Exception {
		WTLibrary eplan = getEPLANContext();
		WTContainerRef container = WTContainerRef.newWTContainerRef(eplan);
		return container;
	}

	public static WTContainerRef getLibrary() throws Exception {
		WTLibrary library = getLibraryContext();
		WTContainerRef container = WTContainerRef.newWTContainerRef(library);
		return container;
	}

	public static WTContainerRef getContainer() throws Exception {
		PDMLinkProduct product = getPDMLinkContext();
		WTContainerRef container = WTContainerRef.newWTContainerRef(product);
		return container;
	}

	/**
	 * 최신 버전의 객체 인지 확인 하는 함수
	 * 
	 * @param oid : 최신 버전을 확인할 객체의 OID
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isLatestVersion(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		Persistable persistable = (Persistable) rf.getReference(oid).getObject();
		return isLatestVersion(persistable);
	}

	/**
	 * 최신 버전의 객체 인지 확인 하는 함수
	 * 
	 * @param persistable : 최신 버전을 확인할 객체
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isLatestVersion(Persistable persistable) throws Exception {
		WTHashSet set = new WTHashSet();
		set.add(persistable);

		WTValuedHashMap map = (WTValuedHashMap) VersionControlHelper.service.getLatestRevisions(set);
		VersionReference reference = (VersionReference) map.get(persistable);

		boolean isLatestRevision = reference.references(persistable);
		boolean isLatestIteration = VersionControlHelper.isLatestIteration((Iterated) persistable);

		return (isLatestIteration && isLatestRevision);
	}

	/**
	 * 최신 버전의 객체를 가져오는 함수
	 * 
	 * @param oid : 최신 버전의 객체를 가져올 객체의 OID
	 * @return Persistable
	 * @throws Exception
	 */
	public static Persistable getLatestVersion(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		Persistable persistable = (Persistable) rf.getReference(oid).getObject();
		WTHashSet set = new WTHashSet();
		set.add(persistable);

		WTValuedHashMap map = (WTValuedHashMap) VersionControlHelper.service.getLatestRevisions(set);
		VersionReference reference = (VersionReference) map.get(persistable);
		return reference.getObject();
	}

	/**
	 * 최신 버전의 객체를 가져오는 함수
	 * 
	 * @param persistable : 최신 버전의 객체를 가져올 객체
	 * @return Persistable
	 * @throws Exception
	 */
	public static Persistable getLatestVersion(Persistable persistable) throws Exception {
		WTHashSet set = new WTHashSet();
		set.add(persistable);

		WTValuedHashMap map = (WTValuedHashMap) VersionControlHelper.service.getLatestRevisions(set);
		VersionReference reference = (VersionReference) map.get(persistable);
		return reference.getObject();
	}

	/**
	 * 객체를 가져오는 함수
	 * 
	 * @param oid : 가져올 객체의 OID
	 * @return Persistable
	 * @throws Exception
	 */
	public static Persistable getObject(String oid) throws Exception {
		if (rf == null) {
			rf = new ReferenceFactory();
		}
		return rf.getReference(oid).getObject();
	}

	/**
	 * 객체의 버전을 가져오는 함수
	 * 
	 * @param rc : 버전을 가져올 객체
	 * @return String
	 * @throws Exception
	 */
	public static String getVersion(RevisionControlled rc) throws Exception {
		return rc.getVersionIdentifier().getSeries().getValue();
	}

	/**
	 * 객체의 이터레이션을 가져오는 함수
	 * 
	 * @param rc : 이터레이션을 가져올 객체
	 * @return String
	 * @throws Exception
	 */
	public static String getIteration(RevisionControlled rc) throws Exception {
		return rc.getIterationIdentifier().getSeries().getValue();
	}

	/**
	 * 객체의 버전+이터레이션을 가져오는 함수
	 * 
	 * @param rc : 버전+이터레이션을 가져올 객체
	 * @return String
	 * @throws Exception
	 */
	public static String getFullVersion(RevisionControlled rc) throws Exception {
		return rc.getVersionIdentifier().getSeries().getValue() + "."
				+ rc.getIterationIdentifier().getSeries().getValue();
	}

	/**
	 * 작성일, 수정일 등 시간에 대한 값을 String 형태로 변경하는 함수
	 * 
	 * @param time : java.sql.Timestamp 객체
	 * @return String
	 * @throws Exception
	 */
	public static String getPersistableTime(Timestamp time) throws Exception {
		return getPersistableTime(time, 10);
	}

	/**
	 * 작성일, 수정일 등 시간에 대한 값을 String 형태로 변경하는 함수
	 * 
	 * @param time  : java.sql.Timestamp 객체
	 * @param index : String 자르기 위한 위치
	 * @return String
	 * @throws Exception
	 */
	public static String getPersistableTime(Timestamp time, int index) throws Exception {
		return time.toString().substring(0, index);
	}

	/**
	 * 접속한 사용자의 Ownership 객체를 가져오는 함수
	 * 
	 * @return Ownership
	 * @throws Exception
	 */
	public static Ownership sessionOwner() throws Exception {
		return Ownership.newOwnership(SessionHelper.manager.getPrincipal());
	}

	/**
	 * 접속한 사용자의 WTUser 객체를 가져오는 함수
	 * 
	 * @return WTUser
	 * @throws Exception
	 */
	public static WTUser sessionUser() throws Exception {
		return (WTUser) SessionHelper.manager.getPrincipal();
	}

	/**
	 * 접속한 사용자가 RevisionControlled 객체의 작성자인지 확인 하는 함수
	 * 
	 * @param rc : RevisionControlled 상속 받은 객체들
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isCreator(RevisionControlled rc) throws Exception {
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		return isCreator(rc, sessionUser);
	}

	/**
	 * WTUser 객체가 RevisionControlled 객체의 작성자인지 확인 하는 함수
	 * 
	 * @param rc          : RevisionControlled 상속받은 객체들
	 * @param sessionUser : WTUser 객체
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isCreator(RevisionControlled rc, WTUser sessionUser) throws Exception {
		return rc.getCreatorName().equals(sessionUser.getName());
	}
}