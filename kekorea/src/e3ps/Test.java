package e3ps;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.QuerySpecUtils;
import e3ps.korea.history.History;
import e3ps.project.Project;
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

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(History.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		SearchCondition sc = new SearchCondition(History.class, "projectReference.key.id", Project.class,
				WTAttributeNameIfc.ID_NAME);
		sc.setFromIndicies(new int[] { idx, idx_p }, 0);
		sc.setOuterJoin(2);
		query.appendWhere(sc, new int[] { idx, idx_p });

		QuerySpecUtils.toOrderBy(query, idx, History.class, History.CREATE_TIMESTAMP, true);
		
		System.out.println(query);
		System.exit(0);
	}
}