package e3ps.org.beans;

import java.util.Vector;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.People;
import e3ps.org.service.OrgHelper;
import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class UserData {

	private WTUser wtuser;
	private String woid;
	private String oid;
	private String name;
	private String id;
	private String email;
	private String duty;
	private String rank;
	private String departmentName;
	private String createDate;
	private String mobile;
	private String iconPath;
	private String resign;
	private Vector<String[]> photo;
	
	public UserData(WTUser wtuser) throws Exception {
		People user = OrgHelper.manager.getUser(wtuser.getName());
		setWtuser(wtuser);
		setWoid(wtuser.getPersistInfo().getObjectIdentifier().getStringValue());
		setOid(user.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(user.getName());
		setId(user.getId());
		setEmail(StringUtils.checkReplaceStr(user.getEmail(), ""));
		setDuty(StringUtils.checkReplaceStr(user.getDuty(), "지정안됨"));
		setRank(StringUtils.checkReplaceStr(user.getRank(), "지정안됨"));
		setDepartmentName(user.getDepartment() != null ? user.getDepartment().getName() : "");
		setCreateDate(CommonUtils.getPersistableTime(user.getCreateTimestamp()));
		setMobile(StringUtils.checkReplaceStr(user.getMobile(), ""));
		setIconPath("/Windchill/jsp/images/user.gif");
		setResign(user.getResign() == true ? "퇴사" : "재직중");
	}
}
