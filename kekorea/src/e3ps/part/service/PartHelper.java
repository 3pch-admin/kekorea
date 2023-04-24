package e3ps.part.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.bom.partlist.MasterDataLink;
import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;

import e3ps.doc.column.DocumentColumnData;
import e3ps.korea.cip.Cip;
import e3ps.org.People;
import e3ps.part.UnitBom;
import e3ps.part.UnitBomPartLink;
import e3ps.part.UnitSubPart;
import e3ps.part.beans.BomBroker;
import e3ps.part.beans.BomCompare;
import e3ps.part.beans.BomTreeData;
import e3ps.part.beans.PartTreeData;
import e3ps.part.beans.PartViewData;
import e3ps.part.column.BomColumnData;
import e3ps.part.column.PartLibraryColumnData;
import e3ps.part.column.PartListDataColumnData;
import e3ps.part.column.PartProductColumnData;
import e3ps.part.column.UnitBomColumnData;
import e3ps.part.dto.PartDTO;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import e3ps.part.dto.PartDTO;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;

import wt.lifecycle.State;
import wt.org.WTUser;

import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;

public class PartHelper {

	/**
	 * 도면 저장 기본위치 제품 - 라이브러리 동일
	 */
	public static final String DEFAULT_ROOT = "/Default/도면";

	/**
	 * 제품, 라이브러리 컨데이터 구분 변수
	 */
	public static final String PRODUCT_CONTAINER = "PRODUCT";
	public static final String LIBRARY_CONTAINER = "LIBRARY";

	public static final String COMMON_DEFAULT_ROOT = "/Default/도면/부품/일반부품";
	public static final String NEW_DEFAULT_ROOT = "/Default/도면/부품/신규부품";
	public static final String SPEC_DEFAULT_ROOT = "/Default/도면/부품/제작사양서";

	public static final PartService service = ServiceFactory.getService(PartService.class);
	public static final PartHelper manager = new PartHelper();


	public static File excelForm;
	public static String excelFormLoc;

	private static String[] excelHeader = new String[] { "구분", "상위UnitLevel", "상위Unit품명", "상위Unit품번", "상위Unit규격",
			"Level", "품명", "품번", "규격", "버전", "QTY", "정규여부", "BOM", "자재소분류", "조달구분", "Material", "Color/Finish",
			"Dimension", "제작대수", "ORDER", "Maker", "Customer", "Option", "비고", "단가" };

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

	public Map<String, Object> findEplan(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<DocumentColumnData> list = new ArrayList<DocumentColumnData>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String name = (String) param.get("name");
		String statesDoc = (String) param.get("statesDoc");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");
		String description = (String) param.get("description");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String sub_folder = (String) param.get("sub_folder");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(WTDocument.class, true);
			int master = query.appendClassList(WTDocumentMaster.class, false);
			// int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
			// int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
			// int idx_o = query.appendClassList(Output.class, false);
			// int idx_p = query.appendClassList(Project.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			// ClassAttribute roleAca = null;
			// ClassAttribute roleBca = null;
			//
			// query.appendOpenParen();
			//
			// roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
			// roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);
			//
			// sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class,
			// "roleAObjectRef.key.id"), "=",
			// roleAca);
			// query.appendWhere(sc, new int[] { idx_olink, idx_o });
			// query.appendAnd();
			// sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class,
			// "roleBObjectRef.key.id"), "=",
			// roleBca);
			// query.appendWhere(sc, new int[] { idx_olink, idx });
			//
			// query.appendAnd();
			//
			// roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
			// roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
			//
			// sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class,
			// "roleAObjectRef.key.id"), "=",
			// roleAca);
			// query.appendWhere(sc, new int[] { idx_plink, idx_o });
			// query.appendAnd();
			// sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class,
			// "roleBObjectRef.key.id"), "=",
			// roleBca);
			// query.appendWhere(sc, new int[] { idx_plink, idx_p });
			//
			// query.appendCloseParen();
			//
			// query.appendAnd();

			sc = WorkInProgressHelper.getSearchCondition_CI(WTDocument.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(WTDocument.class, "masterReference.key.id", WTDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(WTDocument.class, WTDocument.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(WTDocument.class, WTDocument.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getWtUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getWtUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			Folder folder = null;
			if (StringUtils.isNull(location)) {
				location = "/Default";
			}

			folder = FolderTaskLogic.getFolder(location, CommonUtils.getEPLAN());

			if (!StringUtils.isNull(folder)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
						new int[] { f_idx });

				if (!StringUtils.isNull(sub_folder)) {
					ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
					for (int i = 0; i < folders.size(); i++) {
						Folder sub = (Folder) folders.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(
								new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
								new int[] { f_idx });
					}
				}
				query.appendCloseParen();
			}

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(WTDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.addLastVersionCondition(query, idx);
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(WTDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTDocument document = (WTDocument) obj[0];
				DocumentColumnData data = new DocumentColumnData(document);
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

	public File exportBomExcel(Map<String, Object> param) {
		String oid = (String) param.get("oid");
		WTPart part = null;
		WritableWorkbook workBook = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			part = (WTPart) rf.getReference(oid).getObject();
			excelForm = new File(excelFormLoc + File.separator + part.getNumber() + "_BOM.xls");
			workBook = Workbook.createWorkbook(excelForm);
			WritableSheet sheet = workBook.createSheet(part.getNumber() + "_BOM", 0);
			printHeader(sheet);
			setColumnView(sheet);
			printData(sheet, oid);
			workBook.write();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workBook != null)
					workBook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return excelForm;
	}

	private void printData(WritableSheet sheet, String oid) throws Exception {
		HashMap<String, BomColumnData> temp = new HashMap<String, BomColumnData>();
		ArrayList<BomColumnData> arrList = PartHelper.manager.desendentPart(oid);
		WritableCellFormat format = getCellFormat(Alignment.CENTRE, Colour.WHITE);
		int rows = 1;

		for (BomColumnData pdata : arrList) {
			int idx = 0;
			String depth = Integer.toString(pdata.level);
			BomColumnData parent = (BomColumnData) temp.get(Integer.toString(pdata.level - 1));
			if (parent != null) {
				parent.children.add(pdata);
				pdata.parent = parent;
			}
			temp.put(depth, pdata);

			// 구분 상위UnitLevel 상위Unit품명 상위Unit품번 상위Unit규격 Level 품명 품번 규격 버전 QTY 정규여부 BOM
			// 자재소분류 조달구분 Material Color/Finish Dimension 제작대수 ORDER Maker Customer Option
			// 비고 단가

			String gubun = "가공품";
			if (pdata.isLibrary) {
				gubun = "구매품";
			}

			sheet.addCell(new Label(idx++, rows, gubun, format));
			sheet.addCell(
					new Label(idx++, rows, pdata.parent != null ? String.valueOf(pdata.parent.level) : "0", format));
			sheet.addCell(new Label(idx++, rows, pdata.parent != null ? pdata.parent.name : "", format));
			sheet.addCell(new Label(idx++, rows, pdata.parent != null ? pdata.parent.number : "", format));
			sheet.addCell(new Label(idx++, rows, pdata.parent != null ? pdata.parent.spec : "", format));
			// level
			sheet.addCell(new Label(idx++, rows, String.valueOf(pdata.level), format));
			sheet.addCell(new Label(idx++, rows, pdata.name, format));
			sheet.addCell(new Label(idx++, rows, pdata.number, format));
			sheet.addCell(new Label(idx++, rows, pdata.spec, format));
			sheet.addCell(new Label(idx++, rows, pdata.version, format));
			// qty
			sheet.addCell(new Label(idx++, rows, String.valueOf(pdata.amount), format));
			sheet.addCell(new Label(idx++, rows, "정규여부?", format));

			// ㅠㅐㅡ
			sheet.addCell(new Label(idx++, rows, pdata.bom, format));
			sheet.addCell(new Label(idx++, rows, pdata.master_type, format));

			// 조달구분
			sheet.addCell(new Label(idx++, rows, "조달구분?", format));
			// Material Color/Finish Dimension 제작대수 ORDER Maker Customer Option

			sheet.addCell(new Label(idx++, rows, pdata.material, format));
			sheet.addCell(new Label(idx++, rows, pdata.color_finish, format));
			sheet.addCell(new Label(idx++, rows, pdata.dimension, format));

			sheet.addCell(new Label(idx++, rows, "제작대수", format));
			sheet.addCell(new Label(idx++, rows, "ORDER", format));

			sheet.addCell(new Label(idx++, rows, pdata.maker, format));
			sheet.addCell(new Label(idx++, rows, "CUSTOMER", format));
			sheet.addCell(new Label(idx++, rows, "OPTION", format));

			sheet.addCell(new Label(idx++, rows, "비고", format));
			sheet.addCell(new Label(idx++, rows, "단가", format));
			rows++;
		}
	}

	private void setColumnView(WritableSheet sheet) throws Exception {
		int idx = 0;
		for (int i = 0; i < excelHeader.length; i++) {
			sheet.setColumnView(idx++, new Integer(34));
		}
	}

	private void printHeader(WritableSheet sheet) throws Exception {
		WritableCellFormat format = getCellFormat(Alignment.CENTRE, Colour.GRAY_25);

		int idx = 0;
		for (String header : excelHeader) {
			sheet.setRowView(idx, 30);
			sheet.addCell(new Label(idx++, 0, header, format));
		}
	}

	private WritableCellFormat getCellFormat(Alignment alignment, Colour color) {
		WritableCellFormat format = null;
		try {
			format = new WritableCellFormat();
			if (color != null) {
				format.setBackground(color);
			}
			format.setBorder(Border.ALL, BorderLineStyle.THIN);

			if (alignment != null) {
				format.setAlignment(alignment);
			}

			format.setVerticalAlignment(VerticalAlignment.CENTRE);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return format;
	}

	/**
	 * @param param
	 * @return QuerySpec
	 */
	public Map<String, Object> find(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		QuerySpec query = null;

		// search param

		String number = (String) param.get("number");
		String name = (String) param.get("partName");

		String maker = (String) param.get("maker");
		String predate = (String) param.get("predate");
		String statesPart = (String) param.get("statesPart");
		String postdate = (String) param.get("postdate");
		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");
		String latest = (String) param.get("latest");

		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");

		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		String fileName = (String) param.get("fileName");
		// context
		String context = (String) param.get("context");

		// String partTypes = (String) param.get("partTypes");

		List<String> partTypes = (List<String>) param.get("partTypes");

		// iba

		String SPEC = (String) param.get("SPEC");
		String MAKER = (String) param.get("MAKER");
		String MASTER_TYPE = (String) param.get("MASTER_TYPE");
		String partCode = (String) param.get("partCode");
		String material = (String) param.get("material");
		String remark = (String) param.get("remark");
		try {
			query = new QuerySpec();

			int idx = query.appendClassList(WTPart.class, true);
			int master = query.appendClassList(WTPartMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(WTPart.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(WTPart.class, "masterReference.key.id", WTPartMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			if (!StringUtils.isNull(statesPart)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(WTPart.class, "state.state", SearchCondition.EQUAL, statesPart);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(WTPart.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(WTPart.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(WTPart.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(WTPart.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(partTypes)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();

				query.appendOpenParen();

				for (int k = 0; k < partTypes.size(); k++) {
					sc = new SearchCondition(WTPart.class, WTPart.PART_TYPE, "=", partTypes.get(k));
					query.appendWhere(sc, new int[] { idx });
					if (k != partTypes.size() - 1) {
						query.appendOr();
					}
				}
				query.appendCloseParen();
			}

			// 대소문자 구분
			if (!StringUtils.isNull(fileName)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(WTPart.class, WTPart.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(fileName);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getWtUser().getPersistInfo().getObjectIdentifier().getId();
				System.out.println("ids=" + ids);
				sc = new SearchCondition(WTPart.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getWtUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTPart.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			Folder folder = null;
			if (context.equalsIgnoreCase(PRODUCT_CONTEXT)) {
				if (StringUtils.isNull(location)) {
					location = PRODUCT_ROOT;
				}
				folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			} else if (context.equalsIgnoreCase(LIBRARY_CONTEXT)) {
				if (StringUtils.isNull(location)) {
					location = LIBRARY_ROOT;
				}
				folder = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
			} else if (context.equalsIgnoreCase(EPLAN_CONTEXT)) {
				if (StringUtils.isNull(location)) {
					location = EPLAN_ROOT;
				}
				folder = FolderTaskLogic.getFolder(location, CommonUtils.getEPLAN());
			}

			if (!StringUtils.isNull(folder)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
						new int[] { f_idx });

				if (!StringUtils.isNull(location)) {
					ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
					for (int i = 0; i < folders.size(); i++) {
						Folder sub = (Folder) folders.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(
								new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
								new int[] { f_idx });
					}
				}
				query.appendCloseParen();
			}

			if (!StringUtils.isNull(number)) {
				IBAUtils.queryNumber(query, WTPart.class, idx, number);
			}

			if (!StringUtils.isNull(name)) {
				IBAUtils.queryName(query, WTPart.class, idx, name);
			}

			// iba
			if (!StringUtils.isNull(remark)) {
				IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "REMARK", remark);
			}

			if (!StringUtils.isNull(material)) {
				IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "MATERIAL", material);
			}

			if (!StringUtils.isNull(partCode)) {
				IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "PART_CODE", partCode);
			}

			if (!StringUtils.isNull(SPEC)) {
				IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "SPEC", SPEC);
			}

			if (!StringUtils.isNull(MAKER)) {
				IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "MAKER", maker);
			}

			if (!StringUtils.isNull(MASTER_TYPE)) {
				IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "MASTER_TYPE", MASTER_TYPE);
			}

			if ("true".equals(latest)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.addLastVersionCondition(query, idx);
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(WTPart.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPart part = (WTPart) obj[0];

				if (part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
					PartProductColumnData data = new PartProductColumnData(part);
					list.add(data);
				} else if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
					PartLibraryColumnData data = new PartLibraryColumnData(part);
					list.add(data);
				} else if (part.getContainer().getName().equalsIgnoreCase("EPLAN")) {
					PartLibraryColumnData data = new PartLibraryColumnData(part);
					list.add(data);
				}
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

	public ArrayList<BomColumnData> desendentPart(WTPart root) {
		ArrayList<BomColumnData> list = new ArrayList<BomColumnData>();
		BomColumnData data = null;
		try {
			data = new BomColumnData(root);
			list.add(data);
			list = desendentPart(list, root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<BomColumnData> desendentPart(WTPart root, String state) {
		ArrayList<BomColumnData> list = new ArrayList<BomColumnData>();
		BomColumnData data = null;
		try {
			data = new BomColumnData(root);
			list.add(data);
			list = desendentPart(list, root, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private ArrayList<BomColumnData> desendentPart(ArrayList<BomColumnData> list, WTPart root, String state) {
		View view = null;
		try {
			State s = State.toState(state);
			view = ViewHelper.service.getView(root.getViewName());

			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, s);
			QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];

				if (!(obj[1] instanceof WTPart)) {
					continue;
				}

				WTPart child = (WTPart) obj[1];
				BomColumnData data = new BomColumnData(child, link, 1);
				list.add(data);
				gatherDesendentPart(list, data, child.getLifeCycleState().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private void gatherDesendentPart(ArrayList<BomColumnData> list, BomColumnData data, String state) {
		View view = null;
		try {
			view = ViewHelper.service.getView(data.part.getViewName());
			State s = State.toState(state);
			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, s);
			QueryResult result = WTPartHelper.service.getUsesWTParts(data.part, configSpec);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];
				if (!(obj[1] instanceof WTPart)) {
					continue;
				}
				WTPart childPart = (WTPart) obj[1];
				BomColumnData pdata = new BomColumnData(childPart, link, data.level + 1);
				list.add(pdata);
				gatherDesendentPart(list, pdata, childPart.getLifeCycleState().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();

	public EPMDocument getEPMDocument(WTPart part) throws Exception {
		EPMDocument epm = null;
		if (part == null) {
			return epm;

		}

		QueryResult result = null;
		if (VersionControlHelper.isLatestIteration(part)) {
			result = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class);
		} else {
			result = PersistenceHelper.manager.navigate(part, "builtBy", EPMBuildHistory.class);
		}
		while (result.hasMoreElements()) {
			epm = (EPMDocument) result.nextElement();
		}
		return epm;
	}

	/**
	 * 부품 일괄 등록시 PART_CODE IBA 값 검증 있으면 NG 리턴
	 */
	public Map<String, Object> bundleValidatorNumber(String number) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toIBAEqualsAnd(query, WTPart.class, idx, "PART_CODE", number);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			map.put("ycode_check", "NG(YCODE)");
			map.put("ycode", false);
		} else {
			map.put("ycode_check", "OK");
			map.put("ycode", true);
		}
		return map;
	}

	/**
	 * 부품 일괄 등록시 규격(WTPart Number 검증 있을 경우 NG 리턴)
	 */
	public Map<String, Object> bundleValidatorSpec(String spec) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartMaster.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPartMaster.class, WTPartMaster.NUMBER, spec);
		QueryResult result = PersistenceHelper.manager.find(query);

		if (result.hasMoreElements()) {
			map.put("dwg_check", "NG(DWG_NO)");
			map.put("dwg", false);
		} else {
			map.put("dwg_check", "OK");
			map.put("dwg", true);
		}
		return map;
	}

	/**
	 * 부품 검색 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<PartDTO> list = new ArrayList<PartDTO>();

		boolean latest = (boolean) params.get("latest");
		String oid = (String) params.get("oid"); // 폴더 OID
		String container = (String) params.get("container");
		String name = (String) params.get("name");
		String part_code = (String) params.get("part_code");
		String name_of_part = (String) params.get("name_of_part");
		String number = (String) params.get("number");
		String material = (String) params.get("material");
		String remarks = (String) params.get("remarks");
		String maker = (String) params.get("maker");
		String creator = (String) params.get("creator");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String modifier = (String) params.get("modifier");
		String modifiedFrom = (String) params.get("modifiedFrom");
		String modifiedTo = (String) params.get("modifiedTo");
		String state = (String) params.get("state");
		
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		int idx_m = query.appendClassList(WTPartMaster.class, false);
		int idx_u = query.appendClassList(WTUser.class, false);
		
		QuerySpecUtils.toCI(query, idx, WTPart.class);

		QuerySpecUtils.toInnerJoin(query, WTPart.class, WTPartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NUMBER, number);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.CREATE_TIMESTAMP , createdFrom, createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, modifiedFrom, modifiedTo);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.LIFE_CYCLE_STATE, state);
		
		if (!StringUtils.isNull(creator)) {
			WTUser creators = (WTUser) CommonUtils.getObject(creator);
			CommonCode creatorCode = CommonCodeHelper.manager.getCommonCode("CREATOR", "USER_TYPE");

			QuerySpecUtils.toInnerJoin(query, WTUser.class, WTPart.class, WTAttributeNameIfc.ID_NAME,
					"iterationInfo.creator.key.id", idx_u, idx);
			QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "iterationInfo.creator.key.id", creators);
			QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "userTypeReference.key.id",
					creatorCode);
		}
		if (!StringUtils.isNull(modifier)) {
			WTUser modifiers = (WTUser) CommonUtils.getObject(modifier);
			CommonCode modifierCode = CommonCodeHelper.manager.getCommonCode("MODIFIER", "USER_TYPE");

			QuerySpecUtils.toInnerJoin(query, WTUser.class, WTPart.class, WTAttributeNameIfc.ID_NAME,
					"iterationInfo.modifier.key.id", idx_u, idx);
			QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "iterationInfo.modifier.key.id", modifiers);
			QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "userTypeReference.key.id",
					modifierCode);
		}
		
		// iba
		if (!StringUtils.isNull(remarks)) {
			IBAUtils.addIBAConditionEquals(query, WTPart.class, idx, "REMARKS", remarks);
		}

		if (!StringUtils.isNull(material)) {
			IBAUtils.addIBAConditionEquals(query, WTPart.class, idx, "MATERIAL", material);
		}

		if (!StringUtils.isNull(part_code)) {
			IBAUtils.addIBAConditionEquals(query, WTPart.class, idx, "PART_CODE", part_code);
		}

		if (!StringUtils.isNull(maker)) {
			IBAUtils.addIBAConditionEquals(query, WTPart.class, idx, "MAKER", maker);
		}

		if (!StringUtils.isNull(name_of_part)) {
			IBAUtils.addIBAConditionEquals(query, WTPart.class, idx, "NAME_OF_PARTS", name_of_part);
		}
		
		Folder folder = null;
		if (!StringUtils.isNull(oid)) {
			folder = (Folder) CommonUtils.getObject(oid);
		} else {
			if (container.equals(PRODUCT_CONTAINER)) {
				folder = FolderTaskLogic.getFolder(DEFAULT_ROOT, CommonUtils.getPDMLinkProductContainer());
			} else if (container.equalsIgnoreCase(LIBRARY_CONTAINER)) {
				folder = FolderTaskLogic.getFolder(DEFAULT_ROOT, CommonUtils.getWTLibraryContainer());
			}
		}

		if (folder != null) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
			SearchCondition fsc = new SearchCondition(fca, "=",
					new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
			fsc.setOuterJoin(0);
			query.appendWhere(fsc, new int[] { f_idx, idx });
			query.appendAnd();
			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });
		}

		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTPart.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			PartDTO column = new PartDTO(part);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	// 부품 정보 관련문서
	public JSONArray jsonArrayAui(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
//		WTPart part = (WTPart) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocumentWTPartLink link = (WTDocumentWTPartLink) obj[1];
			WTDocument document = link.getDocument();
			Map<String, String> map = new HashMap<>();
			map.put("oid", document.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", document.getNumber());
			map.put("name", document.getName());
			map.put("version", CommonUtils.getFullVersion(document));
			map.put("state", document.getLifeCycleState().getDisplay());
			map.put("modifier", document.getModifierFullName());
			map.put("modifiedDate_txt", CommonUtils.getPersistableTime(document.getModifyTimestamp()));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	// 버전 이력
	public JSONArray list(WTPartMaster master) throws Exception {
		ArrayList<PartDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			PartDTO dto = new PartDTO(part);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 품번으로 최신 부품 있는지 확인 한다.
	 */
	public WTPart getWTPart(String partNo) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartMaster.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPartMaster.class, WTPartMaster.NUMBER, partNo);

		return null;
	}
}
