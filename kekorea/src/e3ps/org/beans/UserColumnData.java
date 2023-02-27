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
	private String department_oid;
	private String department_name;
	private String department_code;
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
			setDepartment_oid(people.getDepartment().getPersistInfo().getObjectIdentifier().getStringValue());
			setDepartment_name(people.getDepartment().getName());
			setDepartment_code(people.getDepartment().getCode());
		}
		setCreatedDate(people.getCreateTimestamp());
		setResign(people.getResign());
	}
}
