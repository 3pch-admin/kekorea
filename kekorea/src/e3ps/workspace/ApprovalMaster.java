package e3ps.workspace;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ApprovalImpl.class },

		properties = {
				@GeneratedProperty(name = "viewDisabled", type = boolean.class, javaDoc = "반려함출력여부", initialValue = "false") },

		foreignKeys = {

				@GeneratedForeignKey(name = "PersistableLineMasterLink",

						foreignKeyRole = @ForeignKeyRole(name = "persist", type = Persistable.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "lineMaster", cardinality = Cardinality.ONE))

		}

)

public class ApprovalMaster extends _ApprovalMaster {

	static final long serialVersionUID = 1;

	public static ApprovalMaster newApprovalMaster() throws WTException {
		ApprovalMaster instance = new ApprovalMaster();
		instance.initialize();
		return instance;
	}
}
