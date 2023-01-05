package e3ps.org.beans;

import java.util.Vector;

import e3ps.common.util.ContentUtils;
import e3ps.org.People;
import wt.org.WTUser;

public class UserViewData {

	public WTUser wtuser;
	public String oid;
	public String name;
	public String id;
	public String email;
	public String duty;
	public String rank;
	public String departmentName;
	public String createDate;
	public String mobile;
	// 기타
	public String iconPath;
	public String resign;
	public Vector<String[]> photo;

	public UserViewData(People user) throws Exception {
		this.wtuser = user.getUser();
		this.oid = user.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = user.getName();
		this.id = user.getId();
		this.email = user.getEmail() != null ? user.getEmail() : "";
		this.duty = user.getDuty() != null ? user.getDuty() : "지정안됨";
		this.rank = user.getRank() != null ? user.getRank() : "지정안됨";
		this.departmentName = user.getDepartment() != null ? user.getDepartment().getName() : "지정안됨";
		this.createDate = user.getCreateTimestamp().toString().substring(0, 10);
		this.mobile = user.getMobile() != null ? user.getMobile() : "등록안됨";
		this.iconPath = "/Windchill/jsp/images/user.gif";
		this.resign = user.getResign() == true ? "퇴사" : "재직중";
		this.photo = ContentUtils.getSecondary(this.wtuser);
	}
}
