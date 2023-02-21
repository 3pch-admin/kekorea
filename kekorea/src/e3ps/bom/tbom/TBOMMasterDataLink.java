package e3ps.bom.tbom;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		properties = {

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "순서"),

		},

		roleA = @GeneratedRole(name = "master", type = TBOMMaster.class),

		roleB = @GeneratedRole(name = "data", type = TBOMData.class)

)
public class TBOMMasterDataLink extends _TBOMMasterDataLink {

	static final long serialVersionUID = 1;

	public static TBOMMasterDataLink newTBOMMasterDataLink(TBOMMaster master, TBOMData data) throws WTException {
		TBOMMasterDataLink instance = new TBOMMasterDataLink();
		instance.initialize(master, data);
		return instance;
	}

}
