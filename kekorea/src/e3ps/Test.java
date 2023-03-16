package e3ps;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.QuerySpecUtils;
import e3ps.korea.history.History;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;

public class Test {

	public static void main(String[] args) throws Exception {

		CommonCode userTypeCode = CommonCodeHelper.manager.getCommonCode("ELEC", "USER_TYPE");
		
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ProjectUserLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ProjectUserLink.class, ProjectUserLink.USER_TYPE, "ELEC");
		QueryResult result = PersistenceHelper.manager.find(query);
		while(result.hasMoreElements()) {
			Object[] obj =(Object[])result.nextElement();
			ProjectUserLink link = (ProjectUserLink)obj[0];
			link.setUserType(userTypeCode);
			PersistenceHelper.manager.modify(link);
		}
		System.out.println("프로젝트 유저 링크 변경 = " + userTypeCode.getName());
		System.exit(0);
	}
}