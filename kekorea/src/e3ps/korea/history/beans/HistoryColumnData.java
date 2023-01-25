package e3ps.korea.history.beans;

import e3ps.korea.history.History;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryColumnData {

	private String oid;
	private String tuv;

	public HistoryColumnData() {

	}

	public HistoryColumnData(History history) throws Exception {
		setOid(history.getPersistInfo().getObjectIdentifier().getStringValue());
		setTuv(history.getTuv());
	}
}
