package e3ps;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.org.Department;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);

		SearchCondition sc = new SearchCondition(KeDrawing.class, KeDrawing.LATEST, SearchCondition.IS_TRUE);
		query.appendWhere(sc, new int[] { idx });
//		QuerySpecUtils.toBoolean(query, idx, KeDrawing.class, KeDrawing.LATEST, "true");

		QuerySpecUtils.toOrderBy(query, idx, KeDrawing.class, KeDrawing.CREATE_TIMESTAMP, true);
		System.out.println(query);
		QueryResult result = PersistenceHelper.manager.find(query);
	}
}
