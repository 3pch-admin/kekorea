package e3ps.part.column;

public enum PartListDataColumnKeys {

	no("NO"),

	kekNumber("KEK 작번"),

	lotNo("LOT NO"),

	unitName("UNIT NAME"),

	partNo("부품번호"),

	partName("부품이름"),

	standard("규격"),

	maker("MAKER"),

	customer("거래처"),

	quantity("수량"),

	unit("단위"), price("단가"), currency("화폐"), won("원화금액"),

	partListDate("수배일자"), exchangeRate("환율"),

	referDrawing("참고도면"), classification("조달구분"), note("비고");

	// KEK 작번 KE 작번 작업내용 고객 설치 장소 상태 버전 작성자 수정자 수정일 분류

	private final String display;

	public String getDisplay() {
		return display;
	}

	private PartListDataColumnKeys(String display) {
		this.display = display;
	}
}
