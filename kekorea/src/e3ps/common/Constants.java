package e3ps.common;

/**
 * 전역 상수 클래스
 * 
 * @author Administrator
 *
 */
public class Constants {

	/**
	 * 라이프 사이클이 아닌 객체에 대해서 상태값을 처리할때 사용할 상수
	 * @author Administrator
	 *
	 */
	public class State {
		public static final String INWORK = "작업 중";
		public static final String APPROVED = "승인됨";
		public static final String APPROVING = "승인중";
		public static final String REJECT = "반려됨";
	}
}
