package e3ps.org.column;

public enum UserColumnKeys {

	// default..
	no("NO"), name("사용자 이름"), id("사용자 아이디"), duty("직급"), departmentName("부서"), email("이메일"), resign("퇴사여부"), createDate("등록일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private UserColumnKeys(String display) {
		this.display = display;
	}
}
