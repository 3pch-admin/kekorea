package e3ps.korea.configSheet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigSheetDTO {

	private String oid;
	private String number;
	private String name;

	public ConfigSheetDTO() {

	}

	public ConfigSheetDTO(ConfigSheet configSheet) throws Exception {
		setOid(configSheet.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(configSheet.getName());
		setNumber(configSheet.getNumber());
	}
}
