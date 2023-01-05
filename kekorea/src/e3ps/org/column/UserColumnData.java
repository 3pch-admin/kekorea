package e3ps.org.column;

import e3ps.org.People;

public class UserColumnData {

	// 목록에서 보여 줄것만해서
	public String oid;
	public String name;
	public String id;
	public String email;
	public String duty;
	public String departmentName;
	public String createDate;
	public String resign;
	// 기타
	public String iconPath;

	public UserColumnData(People user) throws Exception {
		this.oid = user.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = user.getName();
		this.id = user.getId();
		this.email = user.getEmail() != null ? user.getEmail() : "";
		this.duty = user.getDuty() != null ? user.getDuty() : "지정안됨";
		this.departmentName = user.getDepartment() != null ? user.getDepartment().getName() : "지정안됨";
		this.resign = user.getResign() == true ? "퇴사" : "재직중";
		this.createDate = user.getCreateTimestamp().toString().substring(0, 10);

		// 기타
		this.iconPath = "/Windchill/jsp/images/user.gif";
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("id")) {
			value = this.id;
		} else if (key.equals("email")) {
			value = this.email;
		} else if (key.equals("duty")) {
			value = this.duty;
		} else if (key.equals("createDate")) {
			value = this.createDate;
		} else if (key.equals("departmentName")) {
			value = this.departmentName;
		} else if (key.equals("resign")) {
			value = this.resign;
		}
		return value;
	}
}
