package e3ps.loader.commonCode;

import java.io.File;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.common.util.StringUtils;
import e3ps.loader.service.LoaderHelper;

public class InstallLoader {

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("엑셀 파일 추가.");
			System.exit(0);
		}

		File file = new File(args[0]);
		XSSFWorkbook workBook = new XSSFWorkbook(file);
		InstallLoader loader = new InstallLoader();
		loader.load(workBook);
		System.out.println("설치장소 로더 종료!!");
		System.exit(0);
	}

	private void load(XSSFWorkbook workBook) {
		try {
			XSSFSheet sheet = workBook.getSheetAt(0);

			int column = 0;
			for (Row row : sheet) {

				if (column < 1) {
					column++;
					continue;
				}

				String customer = row.getCell(2).getStringCellValue();
				String install = row.getCell(3).getStringCellValue();

				if (!StringUtils.isNull(customer) && !StringUtils.isNull(install)) {
					LoaderHelper.service.loadeInstall(customer, install);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}