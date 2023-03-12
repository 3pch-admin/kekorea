package e3ps;

import e3ps.admin.commonCode.CommonCode;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {

		// inser into commoncode ~~~

		// update delete

		// oid
//		CommonCode commonCode = CommonCode.newCommonCode();

		// commonCode.getPersistInfo().getObjectIdentifier().getStringValue();

		
		// DAO
		// "e3ps.admin.commonCode.CommonCode:182906";

		// select * from commocode wher code=1;

		ReferenceFactory rf = new ReferenceFactory();
		CommonCode c = (CommonCode) rf.getReference("e3ps.admin.commonCode.CommonCode:182906").getObject();
		c.setName("개조1");
		
		PersistenceHelper.manager.modify(c);
		PersistenceHelper.manager.delete(c);

		System.out.println("c.=" + c.getName());

//		commonCode.setName("ASDAD");
//		commonCode.setCode("ASDASD");
//		PersistenceHelper.manager.save(commonCode);

		System.exit(0);
	}
}