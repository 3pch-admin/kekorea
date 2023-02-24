package e3ps.org.beans;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.People;
import e3ps.org.service.OrgHelper;
import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class UserViewData {

	private WTUser wtuser;
	private String woid;
	private String oid;
	private String name;
	private String id;
	private String email;
	private String duty;
	private String departmentName;
	private String createdDate;
	private String resign;

	public UserViewData(WTUser wtUser) throws Exception {
		People user = OrgHelper.manager.getUser(wtUser.getName());
		setWtuser(wtUser);
		setWoid(wtUser.getPersistInfo().getObjectIdentifier().getStringValue());
		setOid(user.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(user.getName());
		setId(user.getId());
		setEmail(StringUtils.replaceToValue(user.getEmail()));
		setDuty(StringUtils.replaceToValue(user.getDuty(), "지정안됨"));
		if (user.getDepartment() != null) {
			setDepartmentName(user.getDepartment().getName());
		}
		setCreatedDate(CommonUtils.getPersistableTime(user.getCreateTimestamp()));
		setResign(user.getResign() == true ? "퇴사" : "재직중");
	}
}
