package e3ps.org.dto;

import java.sql.Timestamp;

import e3ps.org.People;
import e3ps.org.PeopleWTUserLink;
import lombok.Getter;
import lombok.Setter;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;

@Getter
@Setter
public class UserDTO {

	private String oid;
	private String woid;
	private String name;
	private String id;
	private String email;
	private String duty;
	private String department_oid;
	private String department_name;
	private String department_code;
	private Timestamp createdDate;
	private boolean resign;

	public UserDTO() {

	}

	public UserDTO(WTUser wtUser) throws Exception {
		People people = null;
		QueryResult result = PersistenceHelper.manager.navigate(wtUser, "people", PeopleWTUserLink.class);
		if (result.hasMoreElements()) {
			people = (People) result.nextElement();
		}
		setOid(people.getPersistInfo().getObjectIdentifier().getStringValue());
		setWoid(wtUser.getPersistInfo().getObjectIdentifier().getStringValue());
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

	public UserDTO(People people) throws Exception {
		setOid(people.getPersistInfo().getObjectIdentifier().getStringValue());
		setWoid(people.getWtUser().getPersistInfo().getObjectIdentifier().getStringValue());
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
