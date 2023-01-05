package e3ps.part;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "unitBom", type = UnitBom.class),

		roleB = @GeneratedRole(name = "subPart", type = UnitSubPart.class)

)

public class UnitBomPartLink extends _UnitBomPartLink {

	static final long serialVersionUID = 1;

	public static UnitBomPartLink newUnitBomPartLink(UnitBom unitBom, UnitSubPart subPart) throws WTException {
		UnitBomPartLink instance = new UnitBomPartLink();
		instance.initialize(unitBom, subPart);
		return instance;
	}
}