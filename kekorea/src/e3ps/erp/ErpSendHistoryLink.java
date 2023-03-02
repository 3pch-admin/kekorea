package e3ps.erp;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "history", type = ErpSendHistory.class),

		roleB = @GeneratedRole(name = "persist", type = Persistable.class)

)
public class ErpSendHistoryLink extends _ErpSendHistoryLink {
	static final long serialVersionUID = 1;

	public static ErpSendHistoryLink newErpSendHistoryLink(ErpSendHistory history, Persistable per) throws WTException {
		ErpSendHistoryLink instance = new ErpSendHistoryLink();
		instance.initialize(history, per);
		return instance;
	}
}
