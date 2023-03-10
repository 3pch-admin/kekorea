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
import org.json.JSONArray;

import e3ps.bom.partlist.MasterDataLink;
import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.bom.partlist.dto.PartListDataViewData;
import e3ps.bom.tbom.dto.TBOMMasterDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.org.People;
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

public class PartlistHelper {

	/**
	 * access service
	 */
	public static final PartlistService service = ServiceFactory.getService(PartlistService.class);

	/**
	 * access helper
	 */
	public static final PartlistHelper manager = new PartlistHelper();

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

			if (engType.equals("??????")) {
				query.appendOpenParen();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_1???_??????");
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_2???_??????");
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
			} else if ("??????".equals(engType)) {
				query.appendOpenParen();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_1???_??????");
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_2???_??????");
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
		List<TBOMMasterDTO> list = new ArrayList<TBOMMasterDTO>();
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

			// ?????????
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

			// ?????????
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
				TBOMMasterDTO data = new TBOMMasterDTO(master, project);
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
		// Workbook ??????
		File file = null;

		String today = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		String excelName = "?????????_" + master.getName() + "_" + today + ".xlsx";// new
																				// String("?????????.xlsx".getBytes("ISO-8859-1"),
																				// "EUC-KR");
		Map<String, String> headerList = new HashMap<String, String>();
		Map<String, String> list = new HashMap<String, String>();
		ArrayList<PartListData> data = PartlistHelper.manager.getPartListData(master);
		// headerList??? ????????? ?????? ???????????????.
		headerList.put("0", "NO");
		headerList.put("1", "LOT_NO");
		headerList.put("2", "UNIT_NAME");
		headerList.put("3", "????????????");
		headerList.put("4", "?????????");
		headerList.put("5", "??????");
		headerList.put("6", "MAKER");
		headerList.put("7", "?????????");
		headerList.put("8", "??????");
		headerList.put("9", "??????");
		headerList.put("10", "??????");
		headerList.put("11", "??????");
		headerList.put("12", "????????????");
		headerList.put("13", "????????????");
		headerList.put("14", "??????");
		headerList.put("15", "????????????");
		headerList.put("16", "????????????");
		headerList.put("17", "??????");
		// XSSFWorkbook ??????
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(master.getName());
		XSSFRow row = null;
		XSSFCell cell = null;
		for (int i = 0; i <= data.size(); i++) {
			// data??? ???????????? ????????? ???????????????.
			row = sheet.createRow((short) i);
			for (int k = 0; k < headerList.size(); k++) {
				// headerList??? ???????????? i?????? row??? cell??? ???????????????.
				cell = row.createCell(k);
				// ???????????? headerList??? ???????????????.
				if (i == 0) {
					// CellStyle??? ?????????????????? ???????????????.
					CellStyle style = workbook.createCellStyle();
					style.setFillForegroundColor(HSSFColor.AQUA.index);
					// style.setFillPattern("?????? ????????? ??????");
					// style.setAlignment("??????????????? ???????????? ??????");
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
					// headerList??? ???????????? ??????
					cell.setCellValue(headerList.get(Integer.toString(k)));
				}
				// ??????????????? ?????? ???????????? ???????????????.
				else {
					// i-1?????????????????? headerList??? i?????? row?????? ??????.
					PartListDataViewData vdata = new PartListDataViewData(data.get(i - 1));
					// <dataType> excelData = data.get(i-1);
					// ???????????? ???????????? ???????????????.
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
		// ???????????? ?????? ??? ?????? ??????
		try {
			System.out.println(excelName);
			file = new File(excelFormLoc + File.separator + excelName);
			// file??? ????????? ????????? ????????? ???????????????.
			// file.mkdirs();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			// ????????? ??????????????? outputStream ????????????.
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

	/**
	 * ????????? ???????????? ??????
	 * 
	 * @param params : ?????? ????????? ?????? ??????
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<PartListDTO> list = new ArrayList<>();

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
			PartListMasterProjectLink link = (PartListMasterProjectLink) obj[1];
			PartListDTO column = new PartListDTO(link);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * ???????????? ??????????????? JSONArray ????????? ???????????? ??????
	 * 
	 * @param oid : ?????????????????? ?????? OID
	 * @return org.json.JSONArray
	 * @throws Exception
	 */
	public JSONArray getData(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MasterDataLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, MasterDataLink.class, "roleAObjectRef.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, MasterDataLink.class, MasterDataLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink link = (MasterDataLink) obj[0];
			PartListData data = link.getData();

			Map<String, Object> map = new HashMap<>();
			map.put("lotNo", data.getLotNo());
			map.put("unitName", data.getUnitName());
			map.put("partNo", data.getPartNo());
			map.put("partName", data.getPartName());
			map.put("standard", data.getStandard());
			map.put("maker", data.getMaker());
			map.put("customer", data.getCustomer());
			map.put("quantity", data.getQuantity());
			map.put("unit", data.getUnit());
			map.put("price", data.getPrice());
			map.put("currency", data.getCurrency());
			map.put("won", data.getWon());
			map.put("partListDate", data.getPartListDate());
			map.put("exchangeRate", data.getExchangeRate());
			map.put("referDrawing", data.getReferDrawing());
			map.put("classification", data.getClassification());
			map.put("note", data.getNote());
			list.add(map);
		}
		return new JSONArray(list);
	}

	/**
	 * ???????????? ??????????????? ArrayList<Map<String, Object>> ????????? ???????????? ??????
	 * 
	 * @param oid : ?????????????????? ?????? OID
	 * @return ArrayList<Map<String, Object>>
	 * @throws Exception
	 */
	public ArrayList<Map<String, Object>> getArrayMap(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MasterDataLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, MasterDataLink.class, "roleAObjectRef.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, MasterDataLink.class, MasterDataLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink link = (MasterDataLink) obj[0];
			PartListData data = link.getData();

			Map<String, Object> map = new HashMap<>();
			map.put("lotNo", data.getLotNo());
			map.put("unitName", data.getUnitName());
			map.put("partNo", data.getPartNo());
			map.put("partName", data.getPartName());
			map.put("standard", data.getStandard());
			map.put("maker", data.getMaker());
			map.put("customer", data.getCustomer());
			map.put("quantity", data.getQuantity());
			map.put("unit", data.getUnit());
			map.put("price", data.getPrice());
			map.put("currency", data.getCurrency());
			map.put("won", data.getWon());
			map.put("partListDate", data.getPartListDate());
			map.put("exchangeRate", data.getExchangeRate());
			map.put("referDrawing", data.getReferDrawing());
			map.put("classification", data.getClassification());
			map.put("note", data.getNote());
			list.add(map);
		}
		return list;
	}

	public Map<String, Object> compare(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		String oid = (String) params.get("oid");
		String _oid = (String) params.get("_oid");
		String compareType = (String) params.get("compareType");

		ArrayList<Map<String, Object>> result = getArrayMap(oid); // ??????
		ArrayList<Map<String, Object>> _result = getArrayMap(_oid); // ??????

		// ??? ??? ??????
		int maxSize = Math.max(result.size(), _result.size());
		// ????????? ?????? ArrayList??? ?????? null ?????? ??????
		int diff = Math.abs(result.size() - _result.size());
		if (result.size() < _result.size()) {
			for (int i = 0; i < diff; i++) {
				Map<String, Object> empty = new HashMap<String, Object>();
				result.add(empty);
			}
		} else if (result.size() > _result.size()) {
			for (int i = 0; i < diff; i++) {
				Map<String, Object> empty = new HashMap<String, Object>();
				_result.add(empty);
			}
		}

		ArrayList<Map<String, Object>> dataList = new ArrayList<>(maxSize); // ?????? ??????
		ArrayList<Map<String, Object>> _dataList = new ArrayList<>(maxSize); // ?????? ??????

		for (int i = 0; i < result.size(); i++) {
			boolean isEquals = true;
			Map<String, Object> data = result.get(i); // ??????
			for (int j = 0; j < _result.size(); j++) {
				Map<String, Object> _data = _result.get(j); // ??????

				if (compareType.equals("quantity")) { // LOT NO ??????
					String value = (String) data.get("partNo") + "-" + (String) data.get("lotNo") + "-"
							+ (int) data.get("quantity");
					String _value = (String) _data.get("partNo") + "-" + (String) _data.get("lotNo") + "-"
							+ (int) _data.get("quantity");

					System.out.println("q=" + value);
					System.out.println("q22=" + _value);

					if (value.equals(_value)) {
						dataList.add(data);
						_dataList.add(_data);
//						result.remove(i);
						_result.remove(j);
						isEquals = true;
						break;
					}
//					} else if (!value.equals(_value)) {
//						Map<String, Object> empty = new HashMap<String, Object>();
//						dataList.add(data);
//						_dataList.add(empty);
////						break;
//						continue;
//					}
					isEquals = false;
				} else {
					String value = (String) data.get("partNo") + "-" + (String) data.get("lotNo");
					String _value = (String) _data.get("partNo") + "-" + (String) _data.get("lotNo");

					System.out.println("value=" + value);
					System.out.println("_value=" + _value);

					System.out.println("i=" + i);
					System.out.println("j=" + j);

					if (value.equals(_value)) {
						System.out.println("11i=" + i);
						System.out.println("22j=" + j);

						dataList.add(data);
						_dataList.add(_data);
//						result.remove(i);
						_result.remove(j);
						isEquals = true;
						break;
					}
//					} else if (!value.equals(_value)) {
//						Map<String, Object> empty = new HashMap<String, Object>();
//						dataList.add(data);
//						_dataList.add(empty);
////						break;
//						continue;
//					}
					isEquals = false;
				}
			}
			System.out.println("isEquals=" + isEquals);
			if (!isEquals) {
				Map<String, Object> empty = new HashMap<String, Object>();
//				dataList.add(data);
//				_dataList.add(empty);
				dataList.add(empty);
				_dataList.add(_data);
			}
		}

		// ?????????
		_dataList.addAll(_result); // ?????? ????????? ?????????..

		int _diff = Math.abs(dataList.size() - _dataList.size());
		if (dataList.size() < _dataList.size()) {
			for (int i = 0; i < _diff; i++) {
				Map<String, Object> empty = new HashMap<String, Object>();
				dataList.add(empty);
			}
		} else if (dataList.size() > _dataList.size()) {
			for (int i = 0; i < _diff; i++) {
				Map<String, Object> empty = new HashMap<String, Object>();
				_dataList.add(empty);
			}
		}

		// ???????????? ??????????????
		for (int i = dataList.size() - 1; i >= 0; i--) {
//		for (int i = 0; i < dataList.size(); i++) {
			Map<String, Object> m = dataList.get(i);
			Map<String, Object> _m = _dataList.get(i);
			// ?????? ?????? ????????? ????????????..
			if (m.size() == 0 && _m.size() == 0) {
				dataList.remove(i);
				_dataList.remove(i);
			}
		}

		map.put("dataList", dataList);
		map.put("_dataList", _dataList);
		return map;
	}

	public net.sf.json.JSONArray jsonArrayAui(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		PartListMaster partListMaster = (PartListMaster) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, true);
		int idx_link = query.appendClassList(PartListMasterProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, PartListMaster.class, PartListMasterProjectLink.class,
				WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PartListMasterProjectLink.class, "roleAObjectRef.key.id",
				partListMaster.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartListMasterProjectLink link = (PartListMasterProjectLink) obj[1];
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("description", project.getDescription());
			list.add(map);
		}
		return net.sf.json.JSONArray.fromObject(list);
	}
}
