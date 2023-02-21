package e3ps.bom.partlist.service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.beans.CipColumnData;
import e3ps.org.People;
import e3ps.partlist.MasterDataLink;
import e3ps.partlist.PartListData;
import e3ps.partlist.PartListMaster;
import e3ps.partlist.PartListMasterProjectLink;
import e3ps.partlist.beans.PartListColumnData;
import e3ps.partlist.beans.PartListDataViewData;
import e3ps.partlist.column.PartListMasterColumnData;
import e3ps.project.Project;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class PartListMasterHelper {

	/**
	 * access service
	 */
	public static final PartListMasterService service = ServiceFactory.getService(PartListMasterService.class);

	/**
	 * access helper
	 */
	public static final PartListMasterHelper manager = new PartListMasterHelper();

	public static String excelFormLoc;
	static {
		try {
			excelFormLoc = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator
					+ "jsp" + File.separator + "temp" + File.separator + "pdm" + File.separator + "excelForm";

			File loc = new File(excelFormLoc);

			if (!loc.exists()) {
				loc.mkdirs();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<PartListMaster> findPartListByProject(Project project, String engType, String pname)
			throws Exception {

		ArrayList<PartListMaster> list = new ArrayList<PartListMaster>();
		QuerySpec query = null;

		try {
			query = new QuerySpec();
			int idx = query.appendClassList(PartListMaster.class, true);
			int idx_link = query.appendClassList(PartListMasterProjectLink.class, false);
			int idx_p = query.appendClassList(Project.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			ClassAttribute roleAca = new ClassAttribute(PartListMaster.class, WTAttributeNameIfc.ID_NAME);
			ClassAttribute roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleAObjectRef.key.id"), "=",
					roleAca);
			query.appendWhere(sc, new int[] { idx_link, idx });
			query.appendAnd();

			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_link, idx_p });
			query.appendAnd();

			sc = new SearchCondition(PartListMasterProjectLink.class, "roleBObjectRef.key.id", "=",
					project.getPersistInfo().getObjectIdentifier().getId());
			query.appendWhere(sc, new int[] { idx_link, idx_p });
			query.appendAnd();

			if (engType.equals("기계")) {
				query.appendOpenParen();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_1차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_2차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
			} else if ("전기".equals(engType)) {
				query.appendOpenParen();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_1차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_2차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
			}

			ca = new ClassAttribute(PartListMaster.class, PartListMaster.MODIFY_TIMESTAMP);
			OrderBy by = new OrderBy(ca, true);
			query.appendOrderBy(by, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			System.out.println("query=" + query);
			System.out.println("result=" + result.size());
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PartListMaster master = (PartListMaster) obj[0];
				list.add(master);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> findPartList(Map<String, Object> param) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		List<TBOMMasterColumnData> list = new ArrayList<TBOMMasterColumnData>();
		QuerySpec query = null;

		// search param
		String kekNumber = (String) param.get("kekNumber");
		String keNumber = (String) param.get("keNumber");
		String description = (String) param.get("description");
		String engType = (String) param.get("engType");
		String mak = (String) param.get("mak");
		String pDescription = (String) param.get("pDescription");
		String creatorsOid = (String) param.get("creatorsOid");

		String name = (String) param.get("name");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String modifierOid = (String) param.get("modifierOid");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String statesDoc = (String) param.get("statesDoc");

		ReferenceFactory rf = new ReferenceFactory();

		try {
			query = new QuerySpec();
			int idx = query.appendClassList(PartListMaster.class, true);
			int idx_link = query.appendClassList(PartListMasterProjectLink.class, true);
			int idx_p = query.appendClassList(Project.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			ClassAttribute roleAca = new ClassAttribute(PartListMaster.class, WTAttributeNameIfc.ID_NAME);
			ClassAttribute roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleAObjectRef.key.id"), "=",
					roleAca);
			query.appendWhere(sc, new int[] { idx_link, idx });
			query.appendAnd();
			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_link, idx_p });

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PartListMaster.class, PartListMaster.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PartListMaster.class, PartListMaster.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PartListMaster.class, "creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PartListMaster.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(PartListMaster.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(kekNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(pDescription)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(pDescription);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(mak)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.MAK);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(keNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(engType)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.P_TYPE);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(engType);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			ca = new ClassAttribute(PartListMaster.class, PartListMaster.MODIFY_TIMESTAMP);
			OrderBy by = new OrderBy(ca, true);
			query.appendOrderBy(by, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PartListMaster master = (PartListMaster) obj[0];
				Project project = (Project) obj[2];
				TBOMMasterColumnData data = new TBOMMasterColumnData(master, project);
				list.add(data);
			}
			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public ArrayList<PartListData> getPartListData(PartListMaster master) throws Exception {
		ArrayList<PartListData> list = new ArrayList<PartListData>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MasterDataLink.class, true);

		long ids = master.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(MasterDataLink.class, "roleAObjectRef.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(MasterDataLink.class, MasterDataLink.SORT);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink link = (MasterDataLink) obj[0];
			list.add(link.getPartListData());
		}
		return list;
	}

	public WTPart getPartByYCode(Map<String, Object> param) throws Exception {
		String yCode = (String) param.get("yCode");
		WTPart part = null;

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		int master = query.appendClassList(WTPartMaster.class, false);

		SearchCondition sc = null;
//		ClassAttribute ca = null;

		sc = WorkInProgressHelper.getSearchCondition_CI(WTPart.class);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(WTPart.class, "masterReference.key.id", WTPartMaster.class,
				"thePersistInfo.theObjectIdentifier.id");
		query.appendWhere(sc, new int[] { idx, master });

		if (!StringUtils.isNull(yCode)) {
			IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "PART_CODE", yCode);
		}

		if (query.getConditionCount() > 0)
			query.appendAnd();
		sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
		query.appendWhere(sc, new int[] { idx });

		CommonUtils.addLastVersionCondition(query, idx);

		QueryResult result = PersistenceHelper.manager.find(query);

		System.out.println("query=" + query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			part = (WTPart) obj[0];
		}
		return part;
	}

	public File installExcel(Map<String, Object> param) throws Exception {
//		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");

		ReferenceFactory rf = new ReferenceFactory();

		PartListMaster master = (PartListMaster) rf.getReference(oid).getObject();

		/* Excel Download */
		// Workbook 생성
		File file = null;

		String today = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		String excelName = "수배표_" + master.getName() + "_" + today + ".xlsx";// new
																				// String("수배표.xlsx".getBytes("ISO-8859-1"),
																				// "EUC-KR");
		Map<String, String> headerList = new HashMap<String, String>();
		Map<String, String> list = new HashMap<String, String>();
		ArrayList<PartListData> data = PartListMasterHelper.manager.getPartListData(master);
		// headerList를 세팅할 만큼 세팅합니다.
		headerList.put("0", "NO");
		headerList.put("1", "LOT_NO");
		headerList.put("2", "UNIT_NAME");
		headerList.put("3", "부품번호");
		headerList.put("4", "부품명");
		headerList.put("5", "규격");
		headerList.put("6", "MAKER");
		headerList.put("7", "거래처");
		headerList.put("8", "수량");
		headerList.put("9", "단위");
		headerList.put("10", "단가");
		headerList.put("11", "화폐");
		headerList.put("12", "원화금액");
		headerList.put("13", "수배일자");
		headerList.put("14", "환율");
		headerList.put("15", "참고도면");
		headerList.put("16", "조달구분");
		headerList.put("17", "비고");
		// XSSFWorkbook 세팅
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(master.getName());
		XSSFRow row = null;
		XSSFCell cell = null;
		for (int i = 0; i <= data.size(); i++) {
			// data의 크기만큼 로우를 생성합니다.
			row = sheet.createRow((short) i);
			for (int k = 0; k < headerList.size(); k++) {
				// headerList의 크기만큼 i번째 row에 cell을 생성합니다.
				cell = row.createCell(k);
				// 맨윗줄에 headerList를 세팅합니다.
				if (i == 0) {
					// CellStyle은 필요에따라서 세팅합니다.
					CellStyle style = workbook.createCellStyle();
					style.setFillForegroundColor(HSSFColor.AQUA.index);
					// style.setFillPattern("셀의 패턴을 세팅");
					// style.setAlignment("셀데이터의 정렬조건 세팅");
					cell.setCellStyle(style);
					sheet.setColumnWidth(0, 1000);
					sheet.setColumnWidth(1, 2100);
					sheet.setColumnWidth(2, 5000);
					sheet.setColumnWidth(3, 5000);
					sheet.setColumnWidth(4, 8800);
					sheet.setColumnWidth(5, 8800);
					sheet.setColumnWidth(6, 4000);
					sheet.setColumnWidth(7, 4000);
					sheet.setColumnWidth(8, 1200);
					sheet.setColumnWidth(9, 1200);
					sheet.setColumnWidth(10, 4000);
					sheet.setColumnWidth(11, 2200);
					sheet.setColumnWidth(12, 4000);
					sheet.setColumnWidth(13, 3200);
					sheet.setColumnWidth(14, 3000);
					sheet.setColumnWidth(15, 4000);
					sheet.setColumnWidth(16, 4000);
					sheet.setColumnWidth(17, 6000);
					// headerList의 데이터를 세팅
					cell.setCellValue(headerList.get(Integer.toString(k)));
				}
				// 엑셀파일에 넣을 데이터를 세팅합니다.
				else {
					// i-1을하는이유는 headerList가 i번쨰 row이기 때문.
					PartListDataViewData vdata = new PartListDataViewData(data.get(i - 1));
					// <dataType> excelData = data.get(i-1);
					// 리스트의 크기만큼 세팅합니다.
					list.put("0", i + "");
					list.put("1", vdata.lotNo);
					list.put("2", vdata.unitName);
					list.put("3", vdata.partNo);
					list.put("4", vdata.partName);
					list.put("5", vdata.standard);
					list.put("6", vdata.maker);
					list.put("7", vdata.customer);
					list.put("8", vdata.quantity);
					list.put("9", vdata.unit);
					list.put("10", vdata.price);
					list.put("11", vdata.currency);
					list.put("12", String.format("%,f", vdata.won).substring(0,
							String.format("%,f", vdata.won).lastIndexOf(".")));
					list.put("13", vdata.partListDate);
					list.put("14", vdata.exchangeRate);
					list.put("15", vdata.referDrawing);
					list.put("16", vdata.classification);
					list.put("17", vdata.note);
					cell.setCellValue(list.get(Integer.toString(k)));
				}
			}
		}
		// 엑셀파일 세팅 후 파일 생성
		try {
			System.out.println(excelName);
			file = new File(excelFormLoc + File.separator + excelName);
			// file을 생성할 폴더가 없으면 생성합니다.
			// file.mkdirs();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			// 생성한 엑셀파일을 outputStream 해줍니다.
			workbook.write(fileOutputStream);
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (workbook != null)
				workbook.close();
		}

		return file;
	}

	public String getJsonList(PartListMaster master) throws Exception {
		String jsonList = "[";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MasterDataLink.class, true);

		SearchCondition sc = new SearchCondition(MasterDataLink.class, "roleAObjectRef.key.id", "=",
				master.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		// ClassAttribute ca = new ClassAttribute(MasterDataLink.class,
		// MasterDataLink.CREATE_TIMESTAMP);
		// OrderBy by = new OrderBy(ca, false);
		// query.appendOrderBy(by, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(MasterDataLink.class, MasterDataLink.SORT);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink dd = (MasterDataLink) obj[0];
			PartListData data = dd.getPartListData();
			// PartListData data = (PartListData) result.nextElement();
			String oid = data.getPersistInfo().getObjectIdentifier().getStringValue();
			String ss = String.format("%,f", data.getWon()).substring(0,
					String.format("%,f", data.getWon()).lastIndexOf("."));

			jsonList += "['" + oid + "', '" + StringUtils.replaceToValue(data.getLotNo()) + "', '"
					+ StringUtils
							.replaceToValue(data.getUnitName() != null ? data.getUnitName().replaceAll("'", "") : "")
					+ "', '" + StringUtils.replaceToValue(data.getPartNo()) + "','"
					+ StringUtils.replaceToValue(data.getPartName()) + "', '"
					+ StringUtils.replaceToValue(
							data.getStandard() != null ? data.getStandard().replaceAll("'", "") : "")
					+ "', '"
					+ StringUtils.replaceToValue(data.getMaker() != null ? data.getMaker().replaceAll("'", "") : "")
					+ "', '" + StringUtils.replaceToValue(data.getCustomer()) + "','"
					+ StringUtils.replaceToValue(data.getQuantity()) + "', '"
					+ StringUtils.replaceToValue(data.getUnit()) + "', '" + StringUtils.replaceToValue(data.getPrice())
					// + "', '" + StringUtils.replaceToValue(data.getCurrency()) + "', '" +
					// data.getWon() + "', '"
					+ "', '" + StringUtils.replaceToValue(data.getCurrency()) + "', '" + ss + "', '"
					+ StringUtils.replaceToValue(data.getPartListDate()) + "', '"
					+ StringUtils.replaceToValue(data.getExchangeRate()) + "', '"
					+ StringUtils.replaceToValue(data.getReferDrawing()) + "', '"
					+ StringUtils.replaceToValue(data.getClassification()) + "', '"
					+ StringUtils.replaceToValue(data.getNote()) + "'], ";
		}

		if (result.size() == 0) {
			jsonList += "{}";
		}

		jsonList += "]";
		return jsonList;
	}

	public ArrayList<PartListMasterProjectLink> getPartListMasterProjectLink(PartListMaster master) throws Exception {

		ArrayList<PartListMasterProjectLink> list = new ArrayList<PartListMasterProjectLink>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMasterProjectLink.class, true);

		SearchCondition sc = new SearchCondition(PartListMasterProjectLink.class, "roleAObjectRef.key.id", "=",
				master.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(PartListMasterProjectLink.class, WTAttributeNameIfc.CREATE_STAMP_NAME);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });
		// QueryResult result = PersistenceHelper.manager.navigate(master, "project",
		// PartListMasterProjectLink.class,
		// false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			// PartListMasterProjectLink link = (PartListMasterProjectLink)
			// result.nextElement();
			PartListMasterProjectLink link = (PartListMasterProjectLink) obj[0];
			list.add(link);
		}
		return list;
	}

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<PartListColumnData> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, true);
		int idx_link = query.appendClassList(PartListMasterProjectLink.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		QuerySpecUtils.toInnerJoin(query, PartListMasterProjectLink.class, PartListMaster.class,
				"roleAObjectRef.key.id", WTAttributeNameIfc.ID_NAME, idx_link, idx);
		QuerySpecUtils.toInnerJoin(query, PartListMasterProjectLink.class, Project.class, "roleBObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx_link, idx_p);

		QuerySpecUtils.toOrderBy(query, idx, PartListMaster.class, PartListMaster.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartListMaster master = (PartListMaster) obj[0];
			Project project = (Project) obj[2];
			PartListColumnData column = new PartListColumnData(master, project);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
