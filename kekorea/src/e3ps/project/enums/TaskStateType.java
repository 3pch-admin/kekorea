package e3ps.project.enums;

public enum TaskStateType {

	STAND("준비중"), INWORK("작업 중"), DELAY("지연됨"), COMPLETE("완료됨"), STOP("중단됨");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private TaskStateType(String display) {
		this.display = display;
	}
}