package e3ps.common.util;

import java.sql.Timestamp;
import java.util.Enumeration;

import e3ps.org.People;
import e3ps.org.PeopleWTUserLink;
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
	 */
	public static boolean isAdmin() throws Exception {
		return isMember(ADMIN_GROUP);
	}

	/**
	 * 관리자 인지 확인 하는 함수
	 */
	public static boolean isMember(String group) throws Exception {
		WTUser user = (wt.org.WTUser) SessionHelper.manager.getPrincipal();
		return isMember(group, user);
	}

	/**
	 * 관리자 인지 확인 하는 함수
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

	/**
	 * 제품 컨테이너 가져오기
	 */
	public static WTContainerRef getPDMLinkProductContainer() throws Exception {
		QuerySpec query = new QuerySpec(PDMLinkProduct.class);
		SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, "=", "Commonspace");
		query.appendWhere(sc, new int[] { 0 });
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			PDMLinkProduct pdmLinkProduct = (PDMLinkProduct) result.nextElement();
			return WTContainerRef.newWTContainerRef(pdmLinkProduct);
		}
		return null;
	}

	/**
	 * 라이브러리 컨테이너 가져오기
	 */
	public static WTContainerRef getWTLibraryContainer() throws Exception {
		QuerySpec query = new QuerySpec(WTLibrary.class);
		SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, "=", "LIBRARY");
		query.appendWhere(sc, new int[] { 0 });
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			WTLibrary wtLibrary = (WTLibrary) result.nextElement();
			return WTContainerRef.newWTContainerRef(wtLibrary);
		}
		return null;
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
	 */
	public static String getPersistableTime(Timestamp time) throws Exception {
		if (time == null) {
			return "";
		}
		return getPersistableTime(time, 10);
	}

	/**
	 * 작성일, 수정일 등 시간에 대한 값을 String 형태로 변경하는 함수
	 */
	public static String getPersistableTime(Timestamp time, int index) throws Exception {
		if (time == null) {
			return "";
		}
		return time.toString().substring(0, index);
	}

	/**
	 * 접속한 사용자의 Ownership 객체를 가져오는 함수
	 */
	public static Ownership sessionOwner() throws Exception {
		return Ownership.newOwnership(SessionHelper.manager.getPrincipal());
	}

	/**
	 * 접속한 사용자의 WTUser 객체를 가져오는 함수
	 */
	public static WTUser sessionUser() throws Exception {
		return (WTUser) SessionHelper.manager.getPrincipal();
	}

	/**
	 * 접속한 사용자의 PEOPEL 객체 리턴
	 */
	public static People sessionPeople() throws Exception {
		WTUser sessionUser = sessionUser();
		QueryResult result = PersistenceHelper.manager.navigate(sessionUser, "people", PeopleWTUserLink.class);
		if (result.hasMoreElements()) {
			People people = (People) result.nextElement();
			return people;
		}
		return null;
	}

	/**
	 * 접속한 사용자가 RevisionControlled 객체의 작성자인지 확인 하는 함수
	 */
	public static boolean isCreator(RevisionControlled rc) throws Exception {
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		return isCreator(rc, sessionUser);
	}

	/**
	 * WTUser 객체가 RevisionControlled 객체의 작성자인지 확인 하는 함수
	 */
	public static boolean isCreator(RevisionControlled rc, WTUser sessionUser) throws Exception {
		return rc.getCreatorName().equals(sessionUser.getName());
	}
}