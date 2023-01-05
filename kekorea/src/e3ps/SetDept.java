package e3ps;

import e3ps.org.Department;
import e3ps.org.People;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;

public class SetDept {

	public static void main(String[] args) throws Exception {

		ReferenceFactory rf = new ReferenceFactory();
		String uoid = "e3ps.org.People:104693604";
		String doid = "e3ps.org.Department:92392016";
		Department dept = (Department) rf.getReference(doid).getObject();
		People people = (People) rf.getReference(uoid).getObject();

		people.setDepartment(dept);
		PersistenceHelper.manager.modify(people);

		System.exit(0);
	}

}
