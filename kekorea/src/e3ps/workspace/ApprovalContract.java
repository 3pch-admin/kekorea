package e3ps.workspace;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ApprovalImpl.class },

		properties = {
				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)) })
public class ApprovalContract extends _ApprovalContract {
	static final long serialVersionUID = 1;

	public static ApprovalContract newApprovalContract() throws WTException {
		ApprovalContract instance = new ApprovalContract();
		instance.initialize();
		return instance;
	}
}