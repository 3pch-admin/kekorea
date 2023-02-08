package e3ps.korea.history;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.admin.spec.Spec;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "history", type = History.class),

		roleB = @GeneratedRole(name = "spec", type = Spec.class),

		properties = {

				@GeneratedProperty(name = "value", type = String.class)

		}

)
public class HistorySpecLink extends _HistorySpecLink {

	static final long serialVersionUID = 1;

	public static HistorySpecLink newHistorySpecLink(History history, Spec spec) throws WTException {
		HistorySpecLink instance = new HistorySpecLink();
		instance.initialize(history, spec);
		return instance;
	}

}
