package e3ps;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ptc.wa.licensing.LicensingHelper;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.project.Project;
import e3ps.project.service.ProjectHelper;
import e3ps.project.service.StandardProjectService;
import e3ps.project.template.Template;
import e3ps.project.variable.ProjectStateVariable;
import wt.fc.PersistenceHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		
		LicensingHelper.getTotalActiveUsersCount()
		
		File file = new File(args[0]);

		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);

		int rows = sheet.getPhysicalNumberOfRows(); // 시트의 행 개수 가져오기

		// 모든 행(row)을 순회하면서 데이터 가져오기
		for (int i = 1; i < rows; i++) {
			Row row = sheet.getRow(i);
			String customer = row.getCell(2).getStringCellValue();
			String description = row.getCell(3).getStringCellValue();
			double duration = row.getCell(4).getNumericCellValue();
			double elecPrice = row.getCell(5) != null ? row.getCell(5).getNumericCellValue() : 0D;

			Project project = Project.newProject();
			project.setDescription(description);
			project.setDuration((int) duration);
			project.setElecPrice(elecPrice);

			
			if (DateUtil.isCellDateFormatted(row.getCell(1))) {
				Date date = row.getCell(21).getDateCellValue();
				String customDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
				project.setCustomDate(DateUtils.convertDate(customDate));;
			}
			
			if (DateUtil.isCellDateFormatted(row.getCell(6))) {
				Date date = row.getCell(6).getDateCellValue();
				String endDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
				project.setEndDate(DateUtils.convertDate(endDate));
			}

			String install = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "";
			CommonCode installCode = CommonCodeHelper.manager.getCommonCode(install, "INSTALL");
			project.setInstall(installCode);

			String keNumber = "";
			String kekNumber = "";

			// numeric
			if (row.getCell(8) != null && (row.getCell(8).getCellType() == 0)) {
				keNumber = String.valueOf(row.getCell(8).getNumericCellValue());
			} else {
				keNumber = row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "공백데이터";
			}

			// numeric
			if (row.getCell(9) != null && (row.getCell(9).getCellType() == 0)) {
				kekNumber = String.valueOf(row.getCell(9).getNumericCellValue());
			} else {
				kekNumber = row.getCell(9) != null ? row.getCell(9).getStringCellValue() : "공백데이터";
			}

			String kekState = row.getCell(10) != null ? row.getCell(10).getStringCellValue()
					: ProjectStateVariable.INWORK;
			double machinePrice = row.getCell(11) != null ? row.getCell(11).getNumericCellValue() : 0D;
			project.setMachinePrice(machinePrice);

			String mak = row.getCell(12) != null ? row.getCell(12).getStringCellValue() : "";
			CommonCode makCode = CommonCodeHelper.manager.getCommonCode(mak, "MAK");
			project.setMak(makCode);

			String model = row.getCell(13) != null ? row.getCell(13).getStringCellValue() : "";
			project.setModel(model);

			double outputElecPrice = row.getCell(15) != null ? row.getCell(15).getNumericCellValue() : 0D;
			project.setOutputElecPrice(outputElecPrice);
			double outputMachinePrice = row.getCell(16) != null ? row.getCell(16).getNumericCellValue() : 0D;
			project.setOutputMachinePrice(outputMachinePrice);

			// 작성자??
			int ida3a2 = (int) row.getCell(18).getNumericCellValue();
			if (ida3a2 > 0) {
//				WTUser creator = (WTUser) CommonUtils.getObject("wt.org.WTUser:" + ida3a2);
			}

			if (DateUtil.isCellDateFormatted(row.getCell(19))) {
				Date date = row.getCell(19).getDateCellValue();
				String pDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
				project.setPDate(DateUtils.convertDate(pDate));
			}

			String projectType = row.getCell(20).getStringCellValue();
			CommonCode projectTypeCode = CommonCodeHelper.manager.getCommonCode(projectType, "PROJECT_TYPE");
			project.setProjectType(projectTypeCode);

			if (DateUtil.isCellDateFormatted(row.getCell(21))) {
				Date date = row.getCell(21).getDateCellValue();
				String planEnd = new SimpleDateFormat("yyyy-MM-dd").format(date);
				project.setPlanEndDate(DateUtils.convertDate(planEnd));
			}

			if (DateUtil.isCellDateFormatted(row.getCell(22))) {
				Date date = row.getCell(22).getDateCellValue();
				String planStart = new SimpleDateFormat("yyyy-MM-dd").format(date);
				project.setPlanStartDate(DateUtils.convertDate(planStart));
			}

			double progress = row.getCell(23) != null ? row.getCell(23).getNumericCellValue() : 0D;
			project.setProgress((int) progress);

			if (DateUtil.isCellDateFormatted(row.getCell(24))) {
				Date date = row.getCell(24).getDateCellValue();
				String startDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
				project.setStartDate(DateUtils.convertDate(startDate));
			}
			String state = row.getCell(25) != null ? row.getCell(25).getStringCellValue() : "";
			String userId = row.getCell(38) != null ? row.getCell(38).getStringCellValue() : "";

			CommonCode customerCode = CommonCodeHelper.manager.getCommonCode(customer, "CUSTOMER");
			project.setCustomer(customerCode);

			project.setUserId(userId);
			project.setKekNumber(kekNumber);
			project.setKeNumber(keNumber);
			project.setKekState(kekState);
			project.setState(state);

			project = (Project) PersistenceHelper.manager.save(project);
			project = (Project) PersistenceHelper.manager.refresh(project);

			Template t = (Template) CommonUtils.getObject("e3ps.project.template.Template:1297632");
			StandardProjectService service = new StandardProjectService();
			service.copyTask(project, t);

			System.out.println(i + "번째 완료!");

		}

		System.out.println("완료..!");
		System.exit(0);
	}
}