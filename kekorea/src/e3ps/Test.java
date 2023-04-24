package e3ps;

public class Test {

	public static void main(String[] args) throws Exception {

		String dwgFilePath = "D:\\NA-AF-STB100-002.dwg";
		String pdfFilePath = "D:\\sample.pdf";
		String bluebeamPath = "C:\\Program Files (x86)\\Bluebeam Software\\Bluebeam Revu\\21\\Revu\\Revu32.exe";

//		String[] command = { trueViewPath, "/nologo", "/plot", "/P", "DWG to PDF.pc3", "/PUBLISHALL", dwgFilePath,
//				"/OUT", pdfFilePath };
		String[] command = { bluebeamPath, "/convert", "DWG", dwgFilePath, pdfFilePath };
		Process process = Runtime.getRuntime().exec(command);
		// 명령어가 실행되는 동안 대기
		process.waitFor();

		// 출력 결과 확인
		System.out.println("DWG 파일이 PDF로 변환되었습니다.");

		System.exit(0);
	}
}
