package e3ps.project.migration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.project.Project;
import e3ps.project.service.StandardProjectService;
import e3ps.project.template.Template;
import e3ps.project.variable.ProjectStateVariable;
import wt.fc.PersistenceHelper;

public class DevMigrationForExcel {

	public static void main(String[] args) throws Exception {

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
				project.setCustomDate(DateUtils.convertDate(customDate));
			}

			if (DateUtil.isCellDateFormatted(row.getCell(6))) {
				Date date = row.getCell(6).getDateCellValue();
				String endDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
				project.setEndDate(DateUtils.convertDate(endDate));
			}

			String install = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "";

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

			// 막종, 막종상세 세팅
			String mak = row.getCell(12) != null ? row.getCell(12).getStringCellValue() : "";
			toConvertCustomer(customer, project);
			toConvertMak(mak, project);

			String model = row.getCell(13) != null ? row.getCell(13).getStringCellValue() : "";
			project.setModel(model);

			double outputElecPrice = row.getCell(15) != null ? row.getCell(15).getNumericCellValue() : 0D;
			project.setOutputElecPrice(outputElecPrice);
			double outputMachinePrice = row.getCell(16) != null ? row.getCell(16).getNumericCellValue() : 0D;
			project.setOutputMachinePrice(outputMachinePrice);

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
		workbook.close();
		System.exit(0);
	}

	private static void toConvertMak(String mak, Project project) throws Exception {
		String code1 = null; // 막종
		String code2 = null; // 막종 상세
		if (mak.equalsIgnoreCase("B-D-Poly")) {
			code1 = "POLY";
			code2 = "B-D-POLY";
		} else if (mak.equalsIgnoreCase("SRN/AlN/SRO")) {
			code1 = "Ad-TiN";
			code2 = "Ad-TiN";
		} else if (mak.equalsIgnoreCase("SRN/AIN/SRO")) {
			code1 = "Al2O3";
			code2 = "Al2O3";
		} else if (mak.equalsIgnoreCase("Al2O3#1")) {
			code1 = "Al2O3";
			code2 = "Al2O3";
		} else if (mak.equalsIgnoreCase("Al2O3(ALD)")) {
			code1 = "Al2O3";
			code2 = "Al2O3";
		} else if (mak.equalsIgnoreCase("Al2O3/HfO2")) {
			code1 = "Al2O3";
			code2 = "Al2O3/ZrO2/ZrO2OLY";
		} else if (mak.equalsIgnoreCase("Al2O3/HfO2(ALD)")) {
			code1 = "Al2O3";
			code2 = "Al2O3/ZrO2";
		} else if (mak.equalsIgnoreCase("Al2O3/ZrO2(ALD)")) {
			code1 = "Al2O3";
			code2 = "Al2O3/ZrO2";
		} else if (mak.equalsIgnoreCase("Al2O3/ZrO2")) {
			code1 = "Al2O3";
			code2 = "Al2O3/ZrO2";
		} else if (mak.equalsIgnoreCase("BCD-Poly")) {
			code1 = "POLY";
			code2 = "BCD-POLY";
		} else if (mak.equalsIgnoreCase("BIO(NO)")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("BIO")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("BIO(CN)")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("Q2 BIO")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("HALPOx")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("HALPOx(NO)")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("HALPOx(BIO)")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("HALPOx/PO(Sel Ox)")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("HALPOX")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("RIO/LP-OX")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("AA300 BIO")) {
			code1 = "BIO";
			code2 = "BIO";
		} else if (mak.equalsIgnoreCase("New BIO")) {
			code1 = "BIO";
			code2 = "BIO(NEW)";
		} else if (mak.equalsIgnoreCase("BIO(NEW)")) {
			code1 = "BIO";
			code2 = "BIO(NEW)";
		} else if (mak.equalsIgnoreCase("NEW-BIO(CN)")) {
			code1 = "BIO";
			code2 = "BIO(NEW)";
		} else if (mak.equalsIgnoreCase("NEW BIO")) {
			code1 = "BIO";
			code2 = "BIO(NEW)";
		} else if (mak.equalsIgnoreCase("Bonding Anneal")) {
			code1 = "Anneal";
			code2 = "Bonding Anneal";
		} else if (mak.equalsIgnoreCase("BPN(C)")) {
			code1 = "BPN";
			code2 = "BPN";
		} else if (mak.equalsIgnoreCase("BPSG")) {
			code1 = "PYRO";
			code2 = "BPSG";
		} else if (mak.equalsIgnoreCase("BPSG(C)")) {
			code1 = "PYRO";
			code2 = "BPSG";
		} else if (mak.equalsIgnoreCase("BPSG(C)_")) {
			code1 = "PYRO";
			code2 = "BPSG";
		} else if (mak.equalsIgnoreCase("BTBAS")) {
			code1 = "BTBAS";
			code2 = "BTBAS";
		} else if (mak.equalsIgnoreCase("Cu-Anneal(CN)")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("Cu-Anneal")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("Cu-ANL")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("CU-Anneal")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("Cu Anneal")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("CU ANL")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("CU ANNEAL")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("Cu-Alloy(CN)")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("CU-Anneal(CN)")) {
			code1 = "Anneal";
			code2 = "Cu-Anneal";
		} else if (mak.equalsIgnoreCase("D2 Anneal")) {
			code1 = "Anneal";
			code2 = "D2-Anneal";
		} else if (mak.equalsIgnoreCase("D2 ANNEAL")) {
			code1 = "Anneal";
			code2 = "D2-Anneal";
		} else if (mak.equalsIgnoreCase("D-POLY")) {
			code1 = "POLY";
			code2 = "D-POLY";
		} else if (mak.equalsIgnoreCase("POLY")) {
			code1 = "POLY";
			code2 = "D-POLY";
		} else if (mak.equalsIgnoreCase("DRY")) {
			code1 = "PYRO";
			code2 = "DRY";
		} else if (mak.equalsIgnoreCase("GPE(Gas Phase Etch)")) {
			code1 = "MMT";
			code2 = "GPE";
		} else if (mak.equalsIgnoreCase("GPE")) {
			code1 = "MMT";
			code2 = "GPE";
		} else if (mak.equalsIgnoreCase("H2-Alloey")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2 Alloy")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2-Anneal")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2-Anneal(C)")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2-ANL")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2 ANNEAL")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2-Anneal(CN)")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2ANNEAL")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2-ANNEAL")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2-ANL(CN)")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2 Anneal(C)")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2 ANL")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("METAL ANL")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("ANNEAL")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2-ANNEAL(C)")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("H2Anl")) {
			code1 = "Anneal";
			code2 = "H2-Anneal";
		} else if (mak.equalsIgnoreCase("HD Poly")) {
			code1 = "POLY";
			code2 = "HD-POLY";
		} else if (mak.equalsIgnoreCase("HQ-SiO2")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("HQ-SiO")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("SRN/SRO->SEG->SiO2")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("CFD-ONO")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("HT-Al2O3")) {
			code1 = "Al2O3";
			code2 = "HT-Al2O3";
		} else if (mak.equalsIgnoreCase("AL2O3(ALD)")) {
			code1 = "Al2O3";
			code2 = "HT-Al2O3";
		} else if (mak.equalsIgnoreCase("고온Al2O3(ALD)")) {
			code1 = "Al2O3";
			code2 = "HT-Al2O3";
		} else if (mak.equalsIgnoreCase("HT-Al2O3(S)")) {
			code1 = "Al2O3";
			code2 = "HT-Al2O3(D)";
		} else if (mak.equalsIgnoreCase("HT-Al2O3(DUAL)")) {
			code1 = "Al2O3";
			code2 = "HT-Al2O3(D)";
		} else if (mak.equalsIgnoreCase("HT-Al2O3(D)")) {
			code1 = "Al2O3";
			code2 = "HT-Al2O3(D)";
		} else if (mak.equalsIgnoreCase("HT ANL")) {
			code1 = "Anneal";
			code2 = "HT-Anneal";
		} else if (mak.equalsIgnoreCase("HTM-CVD")) {
			code1 = "HTM-CVD";
			code2 = "HTM-CVD";
		} else if (mak.equalsIgnoreCase("HTM CVD")) {
			code1 = "HTM-CVD";
			code2 = "HTM-CVD";
		} else if (mak.equalsIgnoreCase("HTM-CVD[CX3000]")) {
			code1 = "HTM-CVD";
			code2 = "HTM-CVD";
		} else if (mak.equalsIgnoreCase("고온 HTM-CVD(평가기)")) {
			code1 = "HTM-CVD";
			code2 = "HTM-CVD";
		} else if (mak.equalsIgnoreCase("M11")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("HTO")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("HTO(CN)")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("DCS-HTO(CN)")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("HTO(Q2LN)")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("Si3N4→ HTO")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("CX-5K HTO(CN)")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("CX-3K HTO(CN)")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("HTO/TEOS")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("ONO-3")) {
			code1 = "HTO";
			code2 = "HTO";
		} else if (mak.equalsIgnoreCase("HT-SiO2")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		}else if (mak.equalsIgnoreCase("HT SiO2")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("HT-SiO2(N/F)")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("HT Sio2")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("HT-SiO2(N/F)")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("MT-SI3N4(HCD)")) {
			code1 = "HQ-SiO2";
			code2 = "HQ-SiO2";
		} else if (mak.equalsIgnoreCase("HT-SiO2(S)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiO2(S)";
		} else if (mak.equalsIgnoreCase("HT-SiO2(S/F)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiO2(S)";
		} else if (mak.equalsIgnoreCase("HT-SIO2(S)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiO2(S)";
		} else if (mak.equalsIgnoreCase("HT-SiO2,HT-SiO2(S)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiO2(S)";
		} else if (mak.equalsIgnoreCase("HT-SiO2(S/F) ")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiO2(S)";
		} else if (mak.equalsIgnoreCase("HT-SiO2 (S/F사양)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiO2(S)";
		} else if (mak.equalsIgnoreCase("BCD/SiON")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiO2(SL)";
		} else if (mak.equalsIgnoreCase("HT-SiON(ASF)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("MT-Si3N4(ASF)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("HT-SiON(S)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("HT-SiON(SL)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("MT-SiN(ASF)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("HT-SiON")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("HT-SiO2(SL)")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("SiON")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("KCB0215885")) {
			code1 = "HQ-SiO2";
			code2 = "HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("LTM-TiN")) {
			code1 = "LTM-TiN";
			code2 = "LTM-TiN";
		} else if (mak.equalsIgnoreCase("LTM-TIN")) {
			code1 = "LTM-TiN";
			code2 = "LTM-TiN";
		} else if (mak.equalsIgnoreCase("LT-SiO2")) {
			code1 = "LT-SiO2";
			code2 = "LT-SiO2";
		} else if (mak.equalsIgnoreCase("LT-TiN")) {
			code1 = "LT-TiN";
			code2 = "LT-TiN";
		} else if (mak.equalsIgnoreCase("MMT")) {
			code1 = "MMT";
			code2 = "MMT";
		} else if (mak.equalsIgnoreCase("PN")) {
			code1 = "MMT";
			code2 = "MMT";
		} else if (mak.equalsIgnoreCase("MMT(BPN→GPOX)")) {
			code1 = "MMT";
			code2 = "MMT";
		} else if (mak.equalsIgnoreCase("AL2O3")) {
			code1 = "Al2O3";
			code2 = "MT-Al2O3";
		} else if (mak.equalsIgnoreCase("Al2O3")) {
			code1 = "Al2O3";
			code2 = "MT-Al2O3";
		} else if (mak.equalsIgnoreCase("MT-Al2O3")) {
			code1 = "Al2O3";
			code2 = "MT-Al2O3";
		} else if (mak.equalsIgnoreCase("MT-Al2O3(ABA)")) {
			code1 = "Al2O3";
			code2 = "MT-Al2O3(ABA)";
		} else if (mak.equalsIgnoreCase("HfO")) {
			code1 = "MT-ZrO2";
			code2 = "MT-HfO2(L)2V200";
		} else if (mak.equalsIgnoreCase("HfO2")) {
			code1 = "MT-ZrO2";
			code2 = "MT-HfO2(L)2V200";
		} else if (mak.equalsIgnoreCase("HfO2(ALD)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-HfO2(L)2V200";
		} else if (mak.equalsIgnoreCase("Hfo")) {
			code1 = "MT-ZrO2";
			code2 = "MT-HfO2(L)2V200";
		} else if (mak.equalsIgnoreCase("HFO2")) {
			code1 = "MT-ZrO2";
			code2 = "MT-HfO2(L)2V200";
		} else if (mak.equalsIgnoreCase("MT-HfO2(2V200A)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-HfO2(L)2V200";
		} else if (mak.equalsIgnoreCase("DCS-MTO")) {
			code1 = "MTO";
			code2 = "MTO";
		} else if (mak.equalsIgnoreCase("MTO")) {
			code1 = "MTO";
			code2 = "MTO";
		} else if (mak.equalsIgnoreCase("Si3N4(ALD/CVD)→MTO로사용중")) {
			code1 = "MTO";
			code2 = "MTO";
		} else if (mak.equalsIgnoreCase("DCS-MT0")) {
			code1 = "MTO";
			code2 = "MTO";
		} else if (mak.equalsIgnoreCase("MTO(CN)")) {
			code1 = "MTO";
			code2 = "MTO";
		} else if (mak.equalsIgnoreCase("MT-Si3N4(DCS)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-Si3N4")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-SiN")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("Si3N4(ALD)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-SiN(D)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-SI3N4(DCS)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-SiN(DCS)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-SIN(D)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("Si3N4→ MT-SiN")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-SI3N4")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("T2DC6-14880")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("ALD/SiN(겸용기)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-Si3N4(HCD)")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("DKNHR5")) {
			code1 = "MT-Si3N4(DCS)";
			code2 = "MT-Si3N4(HCD)";
		} else if (mak.equalsIgnoreCase("MT-SI3N4(HCD)(S)")) {
			code1 = "MT-Si3N4(HCD)";
			code2 = "MT-Si3N4(HCD)S";
		} else if (mak.equalsIgnoreCase("MT-Si3N4(HCD)(S)")) {
			code1 = "MT-Si3N4(HCD)";
			code2 = "MT-Si3N4(HCD)S";
		} else if (mak.equalsIgnoreCase("MT-Si3N4(HCD)S")) {
			code1 = "MT-Si3N4(HCD)";
			code2 = "MT-Si3N4(HCD)S";
		} else if (mak.equalsIgnoreCase("MT-SiN(HCD)(S)")) {
			code1 = "MT-Si3N4(HCD)";
			code2 = "MT-Si3N4(HCD)S";
		} else if (mak.equalsIgnoreCase("SiOCN")) {
			code1 = "MT-SiOCN";
			code2 = "MT-SiOCN";
		} else if (mak.equalsIgnoreCase("Low-k")) {
			code1 = "MT-SiOCN";
			code2 = "MT-SiOCN";
		} else if (mak.equalsIgnoreCase("Low-k SiOCN")) {
			code1 = "MT-SiOCN";
			code2 = "MT-SiOCN";
		} else if (mak.equalsIgnoreCase("Low-k(SiOCN)")) {
			code1 = "MT-SiOCN";
			code2 = "MT-SiOCN";
		} else if (mak.equalsIgnoreCase("MT-SiOCN(LKS)")) {
			code1 = "MT-SiOCN";
			code2 = "MT-SiOCN";
		} else if (mak.equalsIgnoreCase("Low-K")) {
			code1 = "MT-SiOCN";
			code2 = "MT-SiOCN";
		} else if (mak.equalsIgnoreCase("C-Ox")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("DJ-1206VN")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("Al2O3/ZrO(ALD)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("ZrO2(CX3000)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("ZrO(CX3000)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("ZnO(LED)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("ZrO2(ALD)[CX5000]")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("T2DC6-12503")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("HFO2(ALD)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("AL2O3/HFO2(ALD)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("ZrO(ALD)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2";
		} else if (mak.equalsIgnoreCase("ZrO2")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2V200";
		} else if (mak.equalsIgnoreCase("ZrO")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2V200";
		} else if (mak.equalsIgnoreCase("ZrO2(ALD)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2V200";
		} else if (mak.equalsIgnoreCase("ZrO2(200A)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2V200";
		} else if (mak.equalsIgnoreCase("ZrO2(SFA)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2V200";
		} else if (mak.equalsIgnoreCase("ZrO(SFA)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2V200";
		} else if (mak.equalsIgnoreCase("MT-ZrO2(2V200A)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2V200";
		} else if (mak.equalsIgnoreCase("HDPL")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2Vap";
		} else if (mak.equalsIgnoreCase("HDPL(NH3)")) {
			code1 = "MT-ZrO2";
			code2 = "MT-ZrO2(L)2Vap";
		} else if (mak.equalsIgnoreCase("N2-ANL(TIN)")) {
			code1 = "Anneal";
			code2 = "N2 Anneal";
		} else if (mak.equalsIgnoreCase("High-K")) {
			code1 = "MT-ZrO2";
			code2 = "New High-k";
		} else if (mak.equalsIgnoreCase("New High-k")) {
			code1 = "MT-ZrO2";
			code2 = "New High-k";
		} else if (mak.equalsIgnoreCase("High-K 유전막")) {
			code1 = "MT-ZrO2";
			code2 = "New High-k";
		} else if (mak.equalsIgnoreCase("HNB")) {
			code1 = "NHB";
			code2 = "NHB";
		} else if (mak.equalsIgnoreCase("PI CURE")) {
			code1 = "PIQ";
			code2 = "PIQ";
		} else if (mak.equalsIgnoreCase("PI-Curing")) {
			code1 = "PIQ";
			code2 = "PIQ";
		} else if (mak.equalsIgnoreCase("HD-Poly")) {
			code1 = "POLY";
			code2 = "POLY";
		} else if (mak.equalsIgnoreCase("BCD-POLY")) {
			code1 = "POLY";
			code2 = "POLY";
		} else if (mak.equalsIgnoreCase("S-POLY")) {
			code1 = "POLY";
			code2 = "POLY";
		} else if (mak.equalsIgnoreCase("Poly-Si")) {
			code1 = "POLY";
			code2 = "POLY";
		} else if (mak.equalsIgnoreCase("PYRO")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(C)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(N2O)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(NO/N2O)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("고온PYRO(C)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(C;N2O)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(N20)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("고온 PYRO")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(NO)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("고온PYRO&copy;")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(No,N2O)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO C")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("고온PYRO")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("NO/N2O ANL")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO&copy;")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("T2DD6-16832")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("DOXC32")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("DOX604")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(1000C)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("NORMAL PYRO")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("고온PYRO(c)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(NO,N2O)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO NO/N2O")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO/Anneal")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("Harp Anneal(POLY OX)")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("Q2 PYRO")) {
			code1 = "PYRO";
			code2 = "PYRO";
		} else if (mak.equalsIgnoreCase("PYRO(CN)")) {
			code1 = "PYRO";
			code2 = "PYRO(CN)";
		} else if (mak.equalsIgnoreCase("고온PYRO(CN)")) {
			code1 = "PYRO";
			code2 = "PYRO(CN)";
		} else if (mak.equalsIgnoreCase("PYRO(Z3CN)")) {
			code1 = "PYRO";
			code2 = "PYRO(CN)";
		} else if (mak.equalsIgnoreCase("PYRO(CN;N2O)")) {
			code1 = "PYRO";
			code2 = "PYRO(CN)";
		} else if (mak.equalsIgnoreCase("&nbsp;PYRO(CN)")) {
			code1 = "PYRO";
			code2 = "PYRO(CN)";
		} else if (mak.equalsIgnoreCase("PYRO CN")) {
			code1 = "PYRO";
			code2 = "PYRO(CN)";
		} else if (mak.equalsIgnoreCase("Harp Anneal")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("HARP ANL(Thin Ox)")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("PYRO(1050℃)")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("HARP ANNEAL")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("PYRO(HARP ANL)")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("HARP ANL(HDP ANL)")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("HARP ANL(HARP ANL)")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("PYRO(Harp Anneal(1050℃)")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("고온PYRO(HARP ANL)")) {
			code1 = "PYRO";
			code2 = "PYRO(HARP ANL)";
		} else if (mak.equalsIgnoreCase("HT-Anneal")) {
			code1 = "PYRO";
			code2 = "PYRO(HT)";
		} else if (mak.equalsIgnoreCase("PYRO(High Temp OXIDE)")) {
			code1 = "PYRO";
			code2 = "PYRO(HT)";
		} else if (mak.equalsIgnoreCase("MT-Anneal")) {
			code1 = "PYRO";
			code2 = "PYRO(HT)";
		} else if (mak.equalsIgnoreCase("PYRO(Mid Temp OXIDE)")) {
			code1 = "PYRO";
			code2 = "PYRO(HT)";
		} else if (mak.equalsIgnoreCase("PYRO(PAD OX)")) {
			code1 = "PYRO";
			code2 = "PYRO(PAD OX)";
		} else if (mak.equalsIgnoreCase("PYRO(Thin Ox)")) {
			code1 = "PYRO";
			code2 = "PYRO(THIN OX)";
		} else if (mak.equalsIgnoreCase("PYRO(TOZ)")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("PYRO(Toz ANL)")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("Toz Anneal")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("PYRO(TOZ ANL)")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("PYRO(Toz Anneal)")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("고온PYRO(TOZ ANL)")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("Toz ANL")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("신규 Toz ANL")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("PYRO(Toz)")) {
			code1 = "PYRO";
			code2 = "PYRO(TOZ ANL)";
		} else if (mak.equalsIgnoreCase("WELL")) {
			code1 = "PYRO";
			code2 = "PYRO(WELL)";
		} else if (mak.equalsIgnoreCase("PYRO(WELL)")) {
			code1 = "PYRO";
			code2 = "PYRO(WELL)";
		} else if (mak.equalsIgnoreCase("SCB")) {
			code1 = "SCB";
			code2 = "SCB-HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("HT-SiON-SCB(SiN)")) {
			code1 = "SCB";
			code2 = "SCB-HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("V5 SCB")) {
			code1 = "SCB";
			code2 = "SCB-HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("SCB(V6)")) {
			code1 = "SCB";
			code2 = "SCB-HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("HT-SiON-SCB(SiON)")) {
			code1 = "SCB";
			code2 = "SCB-HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("SCB(Trap)")) {
			code1 = "SCB";
			code2 = "SCB-HT-SiON(SL)";
		} else if (mak.equalsIgnoreCase("SCB(UE)_SiON")) {
			code1 = "SCB";
			code2 = "SCB-HT-SiON(UE)";
		} else if (mak.equalsIgnoreCase("Epi SEG")) {
			code1 = "SEG";
			code2 = "SEG";
		} else if (mak.equalsIgnoreCase("Semi Batch SEG")) {
			code1 = "SEG";
			code2 = "SEG";
		} else if (mak.equalsIgnoreCase("SEG")) {
			code1 = "SEG";
			code2 = "SEG";
		} else if (mak.equalsIgnoreCase("KCB0214917")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(CN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(QMN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(C)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(Q2LN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(ALD/CVD)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(M)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(중압)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("DCS MTO")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("SI3N4(CN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4/HTO")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("SI3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("SI3N4(QMN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("THICK Si3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("UC Si3N4(CN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("SI3N4/HTO")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("THIN Si3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("SIN/SIN")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(CN )")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("CX-3K Si3N4(QMN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("CX-5K Si3N4(QMN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(HVM)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4 &copy;")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("AA300 Si3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("SI3N4(Q2LN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Large-Si3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Large Si3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4_Q2")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(AA300)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("SI3N4(Q2MN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Si3N4(Q2MN)")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Thick SiN")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("ALD/CVD-SiN겸용기")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("Cl2 Clean")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("DNTE81")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("T2DC6-12974")) {
			code1 = "Si3N4";
			code2 = "Si3N4";
		} else if (mak.equalsIgnoreCase("MTO/Si3N4")) {
			code1 = "Si3N4";
			code2 = "Si3N4/HTO";
		} else if (mak.equalsIgnoreCase("SOD(WVG) ANL")) {
			code1 = "Anneal";
			code2 = "SOD(WVG)";
		} else if (mak.equalsIgnoreCase("ISO SOD ANL(WVG)")) {
			code1 = "Anneal";
			code2 = "SOD(WVG)";
		} else if (mak.equalsIgnoreCase("SOD(WVG)")) {
			code1 = "Anneal";
			code2 = "SOD(WVG)";
		} else if (mak.equalsIgnoreCase("TEOS")) {
			code1 = "TEOS";
			code2 = "TEOS";
		} else if (mak.equalsIgnoreCase("TRG")) {
			code1 = "TRG";
			code2 = "TRG";
		} else if (mak.equalsIgnoreCase("VR300DSE-2P")) {
			code1 = "VR";
			code2 = "VR";
		} else if (mak.equalsIgnoreCase("LOAD PORT")) {
			code1 = "VR";
			code2 = "VR";
		} else if (mak.equalsIgnoreCase("08SD")) {
			code1 = "VR";
			code2 = "VR";
		} else if (mak.equalsIgnoreCase("Ru")) {
			code1 = "평가작번";
			code2 = "평가작번";
		} else if (mak.equalsIgnoreCase("S/W")) {
			code1 = "평가작번";
			code2 = "평가작번";
		}

	}

	private static void toConvertCustomer(String customer, Project project) throws Exception {
		String code = null;
		if (customer.equalsIgnoreCase("SAMSUNG")) {
			code = "SAMSUNG";
		} else if (customer.equalsIgnoreCase("HYNIX")) {
			code = "SKHynix";
		} else if (customer.equalsIgnoreCase("DB HiTek")) {
			code = "DB HiTek";
		} else if (customer.equalsIgnoreCase("GROBAL FOUNDRIES")) {
			code = "GrobalFoundries";
		} else if (customer.equalsIgnoreCase("KOKUSAI ELECTRIC")) {
			code = "KE";
		} else if (customer.equalsIgnoreCase("HITACHI KOKUSAI ELECTRIC")) {
			code = "KE";
		} else if (customer.equalsIgnoreCase("KE Semiconductor Equipment (Shanghai) Co., Ltd")) {
			code = "KESH";
		} else if (customer.equalsIgnoreCase("HITACHI KOKUSAI ELECTRIC(SHANGHAI)CO.,LTD.")) {
			code = "KESH";
		} else if (customer.equalsIgnoreCase("KEK")) {
			code = "KEK";
		} else if (customer.equalsIgnoreCase("국가핵융합연구소")) {
			code = "국가핵융합연구소";
		} else if (customer.equalsIgnoreCase("기타")) {
			code = "기타";
		}
		CommonCode customerCode = CommonCodeHelper.manager.getCommonCode(code, "CUSTOMER");
		project.setCustomer(customerCode);
	}
}
