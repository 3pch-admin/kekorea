package e3ps;

import e3ps.erp.service.ErpHelper;
import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;

public class Test4 {

	public static void main(String[] args) throws Exception {

//		String oid = "e3ps.org.People:89660621";
//		String doid = "e3ps.org.Department:89654913";
//
//		//89654913
		ReferenceFactory rf = new ReferenceFactory();
//		Department d = (Department) rf.getReference(doid).getObject();
//		People p = (People) rf.getReference(oid).getObject();
//
//		p.setDepartment(d);
//
//		PersistenceHelper.manager.modify(p);\
		
		
		WTDocument docu = (WTDocument)rf.getReference("wt.doc.WTDocument:105810892").getObject();
		
		ErpHelper.service.sendOutputToERP(docu);
		
		System.out.println("종료");
		System.exit(0);

	}
}