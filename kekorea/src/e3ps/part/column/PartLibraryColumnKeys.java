package e3ps.part.column;

public enum PartLibraryColumnKeys {

	no("NO"), thumnail(""), name("파일이름"), part_code("품번"), name_of_parts("품명"), number("규격"), material(
			"MATERIAL"), remark("REMARK"), maker("MAKER"), version("버전"), creator(
					"작성자"), createDate("작성일"), modifier("수정자"), modifyDate("수정일"), state("상태"), location("FOLDER");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private PartLibraryColumnKeys(String display) {
		this.display = display;
	}
}