package e3ps.migrator;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;

public class StartMigration {

	public static void main(String[] args) throws Exception {

		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("시작 = " + start);

		if (args.length != 1) {
			System.out.println("엑셀 파일 추가.");
			System.exit(0);
		}

		File file = new File(args[0]);
		XSSFWorkbook workBook = new XSSFWorkbook(file);
		StartMigration migration = new StartMigration();
		migration.start(workBook);
		
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("종료 = " + end);

		System.exit(0);

	}

	private void start(XSSFWorkbook workBook) {
		try {
			XSSFSheet sheet = workBook.getSheetAt(0);

			int column = 0;
			for (Row row : sheet) {

				if (column < 1) {
					column++;
					continue;
				}

				String orgMak = row.getCell(0).getStringCellValue();
				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(Project.class, true);
				QuerySpecUtils.toEqualsAnd(query, idx, Project.class, Project.MAK, orgMak);
				QueryResult result = PersistenceHelper.manager.find(query);
				Project project = null;
				while (result.hasMoreElements()) {
					Object[] obj = (Object[]) result.nextElement();
					project = (Project) obj[0];
					System.out.println("=" + project.getKekNumber());

					CommonCode makCode = null;
					CommonCode detailCode = null;
					String mak = row.getCell(2).getStringCellValue();
					String detail = row.getCell(3).getStringCellValue();
					if (!StringUtils.isNull(mak)) {
						makCode = CommonCodeHelper.manager.getCommonCode(mak, "MAK");
					}

					if (!StringUtils.isNull(detail)) {
						detailCode = CommonCodeHelper.manager.getCommonCode(detail, "MAK_DETAIL");
					}

					if (makCode != null && detailCode != null) {
						HashMap<String, Object> map = new HashMap<>();
						map.put("detailCode", detailCode);
						map.put("makCode", makCode);
						map.put("project", project);
						MigrationHelper.service.projectToMak(map);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
