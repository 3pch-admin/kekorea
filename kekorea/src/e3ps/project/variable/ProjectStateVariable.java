package e3ps.project.variable;

public class ProjectStateVariable {

	// 일반 프로젝트 상태
	public static final String READY = "준비중";
	public static final String INWORK = "작업중";
	public static final String DELAY = "지연됨";
	public static final String COMPLETE = "완료됨";
	public static final String STOP = "중단됨";

	// 국제 전용 프로젝트 상태 값
	public static final String KEK_READY = "준비중";
	public static final String KEK_DESIGN_COMPLETE = "설계완료";
	public static final String KEK_DESIGN_INWORK = "설계중";
	public static final String KEK_CANCEL = "취소";
	public static final String KEK_STOP = "중단됨";
	public static final String KEK_COMPLETE = "작업완료";

}