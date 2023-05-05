package e3ps.migrator;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StartMigration {

	public static void main(String[] args) throws Exception {

		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("시작 = " + start);

		if (args.length < 1) {
			System.out.println("실행 명령어 입력");
			System.out.println("mak, customer, install, projectType");
			System.out.println("mak 일 경우 엑셀 파일 추가");
			System.exit(0);
		}

		String cmd = args[0];
		StartMigration migration = new StartMigration();
		if (cmd.equals("mak")) {
			File file = new File(args[1]);
			XSSFWorkbook workBook = new XSSFWorkbook(file);
			migration.mak(workBook);
		} else if (cmd.equals("customer")) {
			migration.customer();
		} else if (cmd.equals("install")) {
			migration.install();
		} else if (cmd.equals("projectType")) {
			migration.projectType();
		}
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("종료 = " + end);

		System.exit(0);

	}

	private void projectType() {
		try {
			MigrationHelper.service.projectToProjectType();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void install() {
		try {
			MigrationHelper.service.projectToInstall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void customer() {
		try {
			MigrationHelper.service.projectToCustomer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void mak(XSSFWorkbook workBook) {
		try {
			XSSFSheet sheet = workBook.getSheetAt(0);

			int column = 0;
			for (Row row : sheet) {

				if (column < 1) {
					column++;
					continue;
				}

				String orgMak = row.getCell(0).getStringCellValue();
				String mak = row.getCell(2).getStringCellValue();
				String detail = row.getCell(3).getStringCellValue();

				HashMap<String, Object> params = new HashMap<>();
				params.put("orgMak", orgMak);
				params.put("mak", mak);
				params.put("detail", detail);

				MigrationHelper.service.projectToMak(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
