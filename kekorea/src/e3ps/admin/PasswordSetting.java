package e3ps.admin;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = { @GeneratedProperty(name = "complex", type = boolean.class),

				@GeneratedProperty(name = "length", type = boolean.class),

				@GeneratedProperty(name = "prange", type = Integer.class),

				@GeneratedProperty(name = "reset", type = Integer.class)

		})
public class PasswordSetting extends _PasswordSetting {
	static final long serialVersionUID = 1;

	public static PasswordSetting newPasswordSetting() throws WTException {
		PasswordSetting instance = new PasswordSetting();
		instance.initialize();
		return instance;
	}
}
