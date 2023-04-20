package e3ps.common.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.util.WTProperties;

public class AutoCADConverter {

	private static String dwgPath = null;
	private static String pdfPath = null;
	static {
		try {
			if (dwgPath == null) {
				dwgPath = WTProperties.getServerProperties().getProperty("wt.temp") + File.separator + "autocad"
						+ File.separator + "dwg";
				File dwgFolder = new File(dwgPath);
				if (!dwgFolder.exists()) {
					dwgFolder.mkdirs();
				}
			}
			if (pdfPath == null) {
				pdfPath = WTProperties.getServerProperties().getProperty("wt.temp") + File.separator + "autocad"
						+ File.separator + "pdf";
				File pdfFolder = new File(pdfPath);
				if (!pdfFolder.exists()) {
					pdfFolder.mkdirs();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Runtime runtime = Runtime.getRuntime();

		String outputPath = "D:" + File.separator;

		String exec1 = "C:\\Converter2023\\D2P.exe";
		String confFile = "C:\\Converter2023\\AutoDWGPdf.ddp";

		String exec = exec1 + " /InFile D:\\NA-AF-STB100-002.dwg /OutFile D:\\NA-AF-STB100-002.dwg /C " + confFile;
		System.out.println(exec);
		Process p = runtime.exec(exec);
		ProcessOutputThread o = new ProcessOutputThread(p.getInputStream(), new StringBuffer());
		o.start();
		p.waitFor();
	}

	public void convert(EPMDocument epm) {
		try {
			// create dwg
			writeData(epm);
			String dwg = dwgPath + File.separator + epm.getCADName();
			String pdf = pdfPath + File.separator + epm.getCADName().substring(0, epm.getCADName().lastIndexOf("."))
					+ ".pdf";
			// convert pdf
			Process process = Runtime.getRuntime().exec(
					"C:\\Program Files\\Autodesk\\DWG TrueView 2022 - English\\dwgviewr.exe /nologo /silent /plot /P \"DWG to PDF.pc3\" /T \""
							+ dwg + "\" /D \"" + pdf + "\"");
			process.waitFor();
			int exitCode = process.exitValue();
			if (exitCode == 0) {
				System.out.println("DWG to PDF conversion succeeded.");
				// 캐드에 pdf 첨부파일 추가
				attach(pdf);
			} else {
				System.out.println("DWG to PDF conversion failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 오토캐드 데이터에 PDF 추가
	 */
	private void attach(String pdf) throws Exception {

	}

	/**
	 * PDF로 변경할 도면 파일 생성
	 */
	private void writeData(EPMDocument epm) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
		ApplicationData data = (ApplicationData) result.nextElement();
		InputStream is = ContentServerHelper.service.findLocalContentStream(data);
		// cad data create....
		File file = new File(dwgPath + File.separator + epm.getCADName());
		OutputStream outputStream = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		outputStream.close();
	}
}
