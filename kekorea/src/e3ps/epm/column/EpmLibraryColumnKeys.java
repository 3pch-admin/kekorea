package e3ps.epm.column;

public enum EpmLibraryColumnKeys {

	// default..
	no("NO"), thumnail(""), name("파일이름"), part_code("품번"),
	name_of_parts("품명"), dwg_no("규격"), material("MATERIAL"),
	remark("REMARK"), reference("REFERENCE 도면"),version("버전"),
	modifier("수정자"), modifyDate("수정일"),creator("작성자"),
	createDate("작성일"),
	 state("상태"),location("FOLDER"),primary("파일");

	   /* createDate("작성일"), */ 
	 

	private final String display;

	public String getDisplay() {
		return display;
	}

	private EpmLibraryColumnKeys(String display) {
		this.display = display;
	}
}
