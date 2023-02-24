package e3ps.org.beans;

import java.sql.Timestamp;

import e3ps.org.People;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserColumnData {

	private String oid;
	private String name;
	private String id;
	private String email;
	private String duty;
	private String departmentName;
	private Timestamp createdDate;
	private boolean resign;

	public UserColumnData() {

	}

	public UserColumnData(People people) throws Exception {
		setOid(people.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(people.getName());
		setId(people.getId());
		setEmail(people.getEmail());
		setDuty(people.getDuty());
		if (people.getDepartment() != null) {
			setDepartmentName(people.getDepartment().getName());
		}
		setCreatedDate(people.getCreateTimestamp());
		setResign(people.getResign());
	}
}
