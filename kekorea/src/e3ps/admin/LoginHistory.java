package e3ps.admin;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "ip", type = String.class, javaDoc = "접속 아이피"),

				@GeneratedProperty(name = "id", type = String.class, javaDoc = "접속 아이디")

		},

		iconProperties = @IconProperties(standardIcon = "/jsp/images/user.gif", openIcon = "/jsp/images/user.gif"),

		tableProperties = @TableProperties(tableName = "J_LOGINHISTORY")

)
public class LoginHistory extends _LoginHistory {

	static final long serialVersionUID = 1;

	public static LoginHistory newLoginHistory() throws WTException {
		LoginHistory instance = new LoginHistory();
		instance.initialize();
		return instance;
	}
}
