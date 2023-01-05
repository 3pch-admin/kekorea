package e3ps.partlist;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		properties = {

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "순서"), },

		roleA = @GeneratedRole(name = "partListMaster", type = PartListMaster.class),

		roleB = @GeneratedRole(name = "partListData", type = PartListData.class)

)

public class MasterDataLink extends _MasterDataLink {

	static final long serialVersionUID = 1;

	public static MasterDataLink newMasterDataLink(PartListMaster partListMaster, PartListData partListData)
			throws WTException {
		MasterDataLink instance = new MasterDataLink();
		instance.initialize(partListMaster, partListData);
		return instance;
	}
}
