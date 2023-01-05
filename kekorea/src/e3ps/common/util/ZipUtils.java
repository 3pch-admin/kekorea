package e3ps.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import wt.util.WTProperties;

public class ZipUtils {

	private static String root;

	static {
		try {
			root = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator + "jsp"
					+ File.separator + "temp" + File.separator + "pdm";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ZipUtils() {

	}

	public static void compress(String path, String zipFileName) throws Exception {
		// Zip.compress("d:\\test", "d:\\test.zip");
		File file = new File(root + File.separator + path); // 특정 저장될 폴더
		if (!file.exists()) {
			file.mkdirs(); // 폴더 없을시 생성
		}

		File zipFile = new File(root + File.separator + "zip"); // 압축 파일이 저장될 폴더
		if (!zipFile.exists()) {
			zipFile.mkdirs(); // 폴더 없을시 생성
		}
		// 출력 스트림
		FileOutputStream fos = null;
		// 압축 스트림
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(new File(zipFile.getPath() + File.separator + zipFileName));
			zos = new ZipOutputStream(fos);
			zos.setEncoding("EUC-KR");
			// 디렉토리 검색
			explore(file, zos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zos != null)
				zos.close();
			if (fos != null)
				fos.close();
		}
	}

	public static void explore(File file, ZipOutputStream zos) throws Exception {
		explore(file, file.getPath(), zos);
	}

	public static void explore(File file, String root, ZipOutputStream zos) throws Exception {
		// 파일이 디렉토리인지 아닌지
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				explore(files[i], root, zos);
			}
		} else {
			compressZip(file, root, zos);
		}
	}

	public static void compressZip(File file, String root, ZipOutputStream zos) throws Exception {
		FileInputStream fis = null;
		try {
			String zipName = file.getPath().replace(root + File.separator, "");

			fis = new FileInputStream(file);

			ZipEntry entry = new ZipEntry(zipName);
			// 스트림에 밀어넣기(자동 오픈)
			zos.putNextEntry(entry);
			int length = (int) file.length();
			byte[] buffer = new byte[length];
			// 스트림 읽어드리기
			fis.read(buffer, 0, length);
			// 스트림 작성
			zos.write(buffer, 0, length);
			// 스트림 닫기
			zos.closeEntry();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
		}
	}
}