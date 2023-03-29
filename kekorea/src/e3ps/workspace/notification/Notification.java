package e3ps.workspace.notification;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.ownership.Ownership;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "제목", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "내용", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "to", type = Ownership.class, javaDoc = "받는사람", constraints = @PropertyConstraints(required = true))

		}

)
public class Notification extends _Notification {
	static final long serialVersionUID = 1;

	public static Notification newNotification() throws WTException {
		Notification instance = new Notification();
		instance.initialize();
		return instance;
	}
}
