package e3ps.korea.cip.beans;

import lombok.Getter;
import lombok.Setter;
import wt.fc.WTObject;

@Getter
@Setter
public class CipColumnData {

	private String oid;
	
	public CipColumnData() {
		
	}
	
//	public CipColumnData(WTObject cip) throws Exception {
//		setOid(cip.getPersistInfo().getObjectIdentifier().getStringValue());
//	}
}
