package e3ps.approval.column;

public enum NoticeColumnKeys {

	// default..
	no("NO"), name("공지사항 제목"), description("내용"), creator("등록자"), createDate("등록일"), primary("첨부파일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private NoticeColumnKeys(String display) {
		this.display = display;
	}
}