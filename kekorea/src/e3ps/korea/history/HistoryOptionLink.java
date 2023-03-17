package e3ps.korea.history;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.commonCode.CommonCode;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "history", type = History.class),

		roleB = @GeneratedRole(name = "option", type = CommonCode.class),

		properties = {

				@GeneratedProperty(name = "dataField", type = String.class, constraints = @PropertyConstraints(required = true))

		}

)

public class HistoryOptionLink extends _HistoryOptionLink {

	static final long serialVersionUID = 1;

	public static HistoryOptionLink newHistoryOptionLink(History history, CommonCode spec) throws WTException {
		HistoryOptionLink instance = new HistoryOptionLink();
		instance.initialize(history, spec);
		return instance;
	}
}
