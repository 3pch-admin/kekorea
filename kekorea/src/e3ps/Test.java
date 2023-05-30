package e3ps;

import e3ps.common.util.CommonUtils;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.vc.struct.StructHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		String eoid = "wt.epm.EPMDocument";
		String oid = "wt.part.WTPart:";
		EPMDocument epm = (EPMDocument)CommonUtils.getObject(eoid);
		WTPart part = (WTPart)CommonUtils.getObject(oid);
		

		QueryResult rs = WTPartHelper.service.getUsesWTParts(part, null)
		

		
		
		System.exit(0);
	}
}
