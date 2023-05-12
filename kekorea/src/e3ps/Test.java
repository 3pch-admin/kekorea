package e3ps;

import e3ps.common.convert.ProcessOutputThread;

public class Test {

	public static void main(String[] args) throws Exception {

		String a = "ASD.dwg";
		System.out.println(a.substring(0, a.lastIndexOf(".")));
		
//		Runtime rt = Runtime.getRuntime();
//		String dwgFilePath = "C:\\NA-AF-STB301-013.A.2.dwg";
//		String pdfFileName = "test.pdf";
//		
//		String exec1 = "C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter 2023\\d2p.exe /InFile";
//		String conFile = "C:\\\\Program Files (x86)\\\\AutoDWG\\\\AutoDWG DWG to PDF Converter 2023\\\\AutoDWGPdf.ddp";
//
//		String exec = exec1 + " " + dwgFilePath + " /OutFile " + pdfFileName + " /InConfigFile " + conFile;
//		System.out.println(exec);
//
//		Process p = rt.exec(exec);
//		ProcessOutputThread o = new ProcessOutputThread(p.getInputStream(), new StringBuffer());
//		o.start();
//		p.waitFor();
//	
		System.exit(0);
	}
}