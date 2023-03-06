package e3ps.bom.tbom.dto;

import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.service.TBOMHelper;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;

@Getter
@Setter
public class TBOMMasterViewData {

	private String oid;
	private String name;
	private String description;
	private JSONArray projectArr;
	private JSONArray tbomArr;

	public TBOMMasterViewData() {

	}

	public TBOMMasterViewData(TBOMMaster master) throws Exception {
		setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(master.getName());
		setDescription(master.getDescription());
		
		setTbomArr(TBOMHelper.manager.auiArray(master));
	}
}
