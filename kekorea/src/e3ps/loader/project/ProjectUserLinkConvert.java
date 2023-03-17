package e3ps.loader.project;

import java.util.ArrayList;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.QuerySpecUtils;
import e3ps.project.ProjectUserLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;

public class ProjectUserLinkConvert {

	public static void main(String[] args) throws Exception {

		ArrayList<String> list = new ArrayList<>();
		list.add("PM");
		list.add("SUB_PM");
		list.add("SOFT");
		list.add("ELEC");
		list.add("MACHINE");
		for (String code : list) {

			CommonCode userTypeCode = CommonCodeHelper.manager.getCommonCode(code, "USER_TYPE");

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ProjectUserLink.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, ProjectUserLink.class, ProjectUserLink.USER_TYPE, code);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ProjectUserLink link = (ProjectUserLink) obj[0];
				link.setProjectUserType(userTypeCode);
				PersistenceHelper.manager.modify(link);
			}
			System.out.println("프로젝트 유저 링크 변경 = " + userTypeCode.getName());
		}
		System.exit(0);
	}
}