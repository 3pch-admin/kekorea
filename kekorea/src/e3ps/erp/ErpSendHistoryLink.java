package e3ps.erp;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, interfaces = { Ownable.class },

		roleA = @GeneratedRole(name = "history", type = ErpSendHistory.class),

		roleB = @GeneratedRole(name = "persist", type = Persistable.class),

		tableProperties = @TableProperties(tableName = "J_ERPSENDHISTORYLINK")

)
public class ErpSendHistoryLink extends _ErpSendHistoryLink {
	static final long serialVersionUID = 1;

	public static ErpSendHistoryLink newErpSendHistoryLink(ErpSendHistory history, Persistable per) throws WTException {
		ErpSendHistoryLink instance = new ErpSendHistoryLink();
		instance.initialize(history, per);
		return instance;
	}
}
