package e3ps.approval;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "contract", type = ApprovalContract.class),

		roleB = @GeneratedRole(name = "persist", type = Persistable.class),

		tableProperties = @TableProperties(tableName = "APPROVALPERSISTABLELINK")

)
public class ApprovalContractPersistableLink extends _ApprovalContractPersistableLink {
	static final long serialVersionUID = 1;

	public static ApprovalContractPersistableLink newApprovalContractPersistableLink(ApprovalContract contract,
			Persistable persist) throws WTException {
		ApprovalContractPersistableLink instance = new ApprovalContractPersistableLink();
		instance.initialize(contract, persist);
		return instance;
	}
}
