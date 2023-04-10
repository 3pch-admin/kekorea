package e3ps.part.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.bom.partlist.MasterDataLink;
import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.doc.column.DocumentColumnData;
import e3ps.org.People;
import e3ps.part.UnitBom;
import e3ps.part.UnitBomPartLink;
import e3ps.part.UnitSubPart;
import e3ps.part.beans.BomBroker;
import e3ps.part.beans.BomCompare;
import e3ps.part.beans.BomTreeData;
import e3ps.part.beans.PartDTO;
import e3ps.part.beans.PartTreeData;
import e3ps.part.beans.PartViewData;
import e3ps.part.column.BomColumnData;
import e3ps.part.column.PartLibraryColumnData;
import e3ps.part.column.PartListDataColumnData;
import e3ps.part.column.PartProductColumnData;
import e3ps.part.column.UnitBomColumnData;
import e3ps.project.Project;
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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartDocumentLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.KeywordExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;

/**
 * 
 * @author JunHo
 * @since 2018-11-28
 * @version 1.0
 */
public class PartHelper {

	// public static final String[] CADTYPE_DISPLAY = new String[] { "어셈블리
	// (ASSEMBLY)", "파트 (PART)", "도면 (CADDRAWING)" };

	public static final String[] CADTYPE_DISPLAY = new String[] { "어셈블리 (ASSEMBLY)", "파트 (PART)" };

	// public static final String[] CADTYPE_VALUE = new String[] { "separable",
	// "component", "CADDRAWING" };

	public static final String[] CADTYPE_VALUE = new String[] { "separable", "component" };

	public static final String[] PART_STATE_DISPLAY = new String[] { "작업 중", "승인 중", "승인됨", "반려됨", "폐기" };

	public static final String[] PART_STATE_VALUE = new String[] { "INWORK", "UNDERAPPROVAL", "RELEASED", "RETURN",
			"WITHDRAWN" };

	public static final String ELEC_ROOT = "/Default/부품/전장품";

	public static final String LIBRARY_ROOT = "/Default/도면";

	public static final String EPLAN_ROOT = "/Default";

	public static final String PRODUCT_ROOT = "/Default/도면";

	public static final String PRODUCT_CONTEXT = "PRODUCT";

	public static final String LIBRARY_CONTEXT = "LIBRARY";

	public static final String EPLAN_CONTEXT = "EPLAN";

	public static final String ELEC_CONTEXT = "ELEC";

	public static final String COMMON_PART = "/Default/도면/부품/일반부품";
	public static final String NEW_PART = "/Default/도면/부품/신규부품";

	public static final String UNIT_BOM = "/Default/도면/부품/UNIT_BOM";

	public static final String SPEC_PART = "/Default/도면/부품/제작사양서";

	/**
	 * access service
	 */
	public static final PartService service = ServiceFactory.getService(PartService.class);

	/**
	 * access helper
	 */
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
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
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
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				System.out.println("ids=" + ids);
				sc = new SearchCondition(WTPart.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
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
		}
	}

	public ArrayList<BomTreeData> getDesendentPart(String oid) throws Exception {
		ArrayList<BomTreeData> list = new ArrayList<BomTreeData>();
		ReferenceFactory rf = new ReferenceFactory();
		WTPart root = (WTPart) rf.getReference(oid).getObject();
		// treeIcon == collapse
		BomTreeData data = new BomTreeData(root, null, 0);
		list.add(data);
		list = getOneDeptBomData(list, root);
		return list;
	}

	private ArrayList<BomTreeData> getOneDeptBomData(ArrayList<BomTreeData> list, WTPart root) throws Exception {

		String viewName = root.getViewName();
		if (StringUtils.isNull(viewName)) {
			viewName = "Engineering";
		}

		View view = ViewHelper.service.getView(viewName);

		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
		QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			WTPart child = (WTPart) obj[1];
			BomTreeData data = new BomTreeData(child, link, 1, BomTreeData.expand, true);
			list.add(data);
			getOhterLevelBomData(list, data, child.getLifeCycleState().toString());
		}
		return list;
	}

	private void getOhterLevelBomData(ArrayList<BomTreeData> list, BomTreeData data, String state) {
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
				BomTreeData pdata = new BomTreeData(childPart, link, data.level + 1, BomTreeData.collapse, false);
				list.add(pdata);
				getOhterLevelBomData(list, pdata, childPart.getLifeCycleState().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param oid
	 * @return ArrayList<BomColumnData>
	 */
	public ArrayList<BomColumnData> desendentPart(String oid) {
		ArrayList<BomColumnData> list = new ArrayList<BomColumnData>();
		ReferenceFactory rf = new ReferenceFactory();
		WTPart root = null;
		BomColumnData data = null;
		try {
			root = (WTPart) rf.getReference(oid).getObject();
			data = new BomColumnData(root);
			list.add(data);
			list = desendentPart(list, root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param list
	 * @param root
	 * @return ArrayList<BomColumnData>
	 */
	private ArrayList<BomColumnData> desendentPart(ArrayList<BomColumnData> list, WTPart root) {
		View view = null;
		try {

			// System.out.println("root=" + root.getViewName());
			// if (root.getViewName() == null) {
			view = ViewHelper.service.getView("Engineering");
			// } else {
			// view = ViewHelper.service.getView(root.getViewName());
			// }

			System.out.println("view=" + view);

			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
			QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];
				WTPart child = (WTPart) obj[1];
				BomColumnData data = new BomColumnData(child, link, 1);
				list.add(data);
				gatherDesendentPart(list, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param list
	 * @param data
	 */
	private void gatherDesendentPart(ArrayList<BomColumnData> list, BomColumnData data) {
		View view = null;
		try {
			// view = ViewHelper.service.getView(data.part.getViewName());
			view = ViewHelper.service.getView("Engineering");

			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
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
				gatherDesendentPart(list, pdata);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<PartViewData> getDownPart(String oid) {
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		View view = null;

		ArrayList<PartViewData> list = new ArrayList<PartViewData>();
		try {
			part = (WTPart) rf.getReference(oid).getObject();
			view = ViewHelper.service.getView(part.getViewName());

			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
			QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];
				WTPart child = (WTPart) obj[1];
				PartViewData data = new PartViewData(child, link, 1);
				list.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<PartViewData> getUpPart(String oid) {
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<PartViewData> list = new ArrayList<PartViewData>();
		try {
			part = (WTPart) rf.getReference(oid).getObject();
			QueryResult result = StructHelper.service.navigateUsedByToIteration(part, true, new LatestConfigSpec());
			while (result.hasMoreElements()) {
				WTPart p = (WTPart) result.nextElement();
				PartViewData data = new PartViewData(p);
				list.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<PartViewData> infoEndPart(String oid, ArrayList<PartViewData> list) {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = null;
		try {
			part = (WTPart) rf.getReference(oid).getObject();
			QueryResult result = StructHelper.service.navigateUsedByToIteration(part, true, new LatestConfigSpec());
			while (result.hasMoreElements()) {
				WTPart p = (WTPart) result.nextElement();
				infoEndPart(p.getPersistInfo().getObjectIdentifier().getStringValue(), list);
			}

			if (result.size() == 0) {
				list.add(new PartViewData(part));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

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

	public QueryResult referencedBy(EPMDocument epm) throws Exception {
		QuerySpec query = new QuerySpec(EPMDocument.class, EPMReferenceLink.class);
		SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.DOC_TYPE, "=", "CADDRAWING");
		query.appendWhere(sc, new int[] { 0 });
		return PersistenceHelper.manager.navigate(epm.getMaster(), "referencedBy", query);
	}

	public boolean isPart(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartMaster.class, true);
		SearchCondition sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, "=",
				number.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		boolean isPart = result.size() > 0 ? true : false;
		return isPart;
	}

	public Map<String, Object> checkPdmPartNumber(Map<String, Object> param) {
		String number = (String) param.get("number");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean isExist = false;
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(WTPartMaster.class, true);
			SearchCondition sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, "=", number);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			if (result.hasMoreElements()) {
				isExist = true;
			}

			map.put("isExist", isExist);
			// map.put("result", SUCCESS);
			if (isExist == false) {
				// map.put("msg", "PDM 서버에 존재하지 않는 자재마스터 번호 입니다.\n등록을 진행해주세요.");
			} else {
				map.put("result", SUCCESS);
				map.put("msg", "PDM 서버에 존재하는 부품 번호 입니다.\n다른 부품 번호를 입력해주세요.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", "중복체크 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
		}
		return map;
	}

	public WTPart getBaselinePart(String oid, ManagedBaseline baseline) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();

		WTPart part = (WTPart) rf.getReference(oid).getObject();

		if (baseline != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					baseline.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=",
					part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}

		return part;
	}

	public WTPart getLatestPart(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, "=", number.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
		query.appendWhere(sc, new int[] { idx });

		CommonUtils.addLastVersionCondition(query, idx);

		QueryResult result = PersistenceHelper.manager.find(query);
		WTPart part = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			part = (WTPart) obj[0];
		}
		return part;
	}

	public WTPartUsageLink getUsageLink(WTPart parent, WTPartMaster master) throws Exception {
		WTPartUsageLink link = null;

		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(WTPartUsageLink.class, true);

		SearchCondition sc = new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", "=",
				parent.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=",
				master.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);

		if (result.hasMoreElements()) {
			// while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			link = (WTPartUsageLink) obj[0];
		}
		return link;
	}

	public boolean isProduct(WTPart part) throws Exception {
		String name = part.getContainer().getName();
		return isProduct(name);
	}

	public boolean isLibrary(WTPart part) throws Exception {
		String name = part.getContainer().getName();
		return isLibrary(name);
	}

	public boolean isProduct(String name) throws Exception {
		boolean isProduct = false;
		if (name.equalsIgnoreCase("Commonspace")) {
			isProduct = true;
		}
		return isProduct;
	}

	public boolean isLibrary(String name) throws Exception {
		boolean isLibrary = false;
		if (name.equalsIgnoreCase("LIBRARY")) {
			isLibrary = true;
		}
		return isLibrary;
	}

	public ArrayList<ArrayList<PartTreeData>> compareBomList(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart latestPart = (WTPart) rf.getReference(oid).getObject();
		return compareBomList(latestPart);
	}

	private ArrayList<ArrayList<PartTreeData>> compareBomList(WTPart latestPart) throws Exception {

		ArrayList<ArrayList<PartTreeData>> result = new ArrayList<ArrayList<PartTreeData>>();

		ArrayList<PartTreeData> addPartList = new ArrayList<PartTreeData>();
		ArrayList<PartTreeData> removePartList = new ArrayList<PartTreeData>();
		ArrayList<PartTreeData> changePartList = new ArrayList<PartTreeData>();

		QueryResult qr = VersionControlHelper.service.allIterationsOf(latestPart.getMaster());

		WTPart prePart = null;
		int b = 0;
		while (qr.hasMoreElements()) {
			WTPart p = (WTPart) qr.nextElement();
			String state = p.getLifeCycleState().toString();

			if (b == 0) {
				b++;
				continue;
			}

			if (state.equals("RELEASED")) {
				// if( b == 1) {
				prePart = p;
				break;
			}
		}
		BomBroker broker = new BomBroker();

		System.out.println("la=" + latestPart.getVersionIdentifier().getSeries().getValue() + ">"
				+ latestPart.getIterationIdentifier().getSeries().getValue());
		System.out.println("la=" + prePart.getVersionIdentifier().getSeries().getValue() + ">"
				+ prePart.getIterationIdentifier().getSeries().getValue());

		PartTreeData latestData = broker.getTree(latestPart, true, null, null);
		PartTreeData preData = broker.getTree(prePart, true, null, prePart.getLifeCycleState().toString());

		ArrayList<PartTreeData[]> list = new ArrayList<PartTreeData[]>();
		broker.compareBom(latestData, preData, list);

		for (int i = 0; i < list.size(); i++) {
			PartTreeData[] o = (PartTreeData[]) list.get(i);
			PartTreeData data = o[0];
			PartTreeData data2 = o[1];

			String bgcolor = "black"; // white

			if (data == null) {
				System.out.println("싹제=" + data2.number);
				bgcolor = "red";// "#D3D3D3"; //삭제
				data2.flag = "삭제";
				data2.bgcolor = bgcolor;
				removePartList.add(data2);
			} else {
				if (data2 == null) {
					System.out.println("추가" + data.number);
					bgcolor = "blue";// "#8FBC8F"; //green
					data.flag = "추가";
					data.bgcolor = bgcolor;
					addPartList.add(data);
				} else {
					if (!data.compare(data2)) {
						System.out.println("변경" + data2.number);
						bgcolor = "#FFD700"; // gold
						data.flag = "변경";
						data.bgcolor = bgcolor;
						changePartList.add(data);
					}
				}
			}
		}

		result.add(changePartList);
		result.add(removePartList);
		result.add(addPartList);

		return result;
	}

	public ArrayList<WTDocument> getWTDocument(WTPart part) {
		ArrayList<WTDocument> list = new ArrayList<WTDocument>();
		try {

			QueryResult result = PersistenceHelper.manager.navigate(part, "document", WTDocumentWTPartLink.class);
			while (result.hasMoreElements()) {
				WTDocument refDocument = (WTDocument) result.nextElement();
				list.add(refDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<WTDocumentWTPartLink> getWTDocumentWTPartLink(WTPart part) {
		ArrayList<WTDocumentWTPartLink> list = new ArrayList<WTDocumentWTPartLink>();
		try {

			QueryResult result = PersistenceHelper.manager.navigate(part, "document", WTDocumentWTPartLink.class,
					false);
			while (result.hasMoreElements()) {
				WTDocumentWTPartLink link = (WTDocumentWTPartLink) result.nextElement();
				list.add(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<BomTreeData> getDesendentBom(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart root = (WTPart) rf.getReference(oid).getObject();
		return getDesendentBom(root);
	}

	public ArrayList<BomTreeData> getDesendentBom(WTPart root) throws Exception {
		ArrayList<BomTreeData> list = new ArrayList<BomTreeData>();
		BomTreeData data = new BomTreeData(root, null, 0);
		list.add(data);
		getDesendentBoms(list, root, 0);
		return list;
	}

	public void getDesendentBoms(ArrayList<BomTreeData> list, WTPart root, int level) throws Exception {
		View view = null;
		try {
			view = ViewHelper.service.getView(root.getViewName());
			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
			QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];
				if (!(obj[1] instanceof WTPart)) {
					continue;
				}
				WTPart childPart = (WTPart) obj[1];
				BomTreeData data = new BomTreeData(childPart, link, level + 1);
				list.add(data);
				getDesendentBoms(list, childPart, data.level);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray getBomData(Map<String, Object> param) throws Exception {
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		WTPart root = (WTPart) rf.getReference(oid).getObject();
		BomTreeData data = new BomTreeData(root, null, 0);
		JSONArray jsonArray = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("type", "root");
		rootNode.put("id", data.oid);
		rootNode.put("title", data.number + ", " + data.name);
		if (WorkInProgressHelper.isCheckedOut(root)) {
			rootNode.put("icon", "/Windchill/jsp/images/editor/partcheckout.gif");
		} else {
			rootNode.put("icon", data.iconPath);
		}
		rootNode.put("name", data.name);
		rootNode.put("thumnail", data.thumnail[1]);
		rootNode.put("qty", data.qty);
		rootNode.put("number", data.number);
		rootNode.put("fNumber", "");
		rootNode.put("minus", data.isMinus);
		rootNode.put("version", data.version);
		rootNode.put("expanded", true);
		rootNode.put("folder", true);
		getSubBomData(root, rootNode, data.level);
		jsonArray.add(rootNode);
		return jsonArray;
	}

	private static void getSubBomData(WTPart root, JSONObject rootNode, int level) throws Exception {

		View view = null;
		try {
			view = ViewHelper.service.getView(root.getViewName());
			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
			QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);
			JSONArray jsonChildren = new JSONArray();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) obj[0];
				if (!(obj[1] instanceof WTPart)) {
					continue;
				}
				WTPart childPart = (WTPart) obj[1];

				BomTreeData data = new BomTreeData(childPart, link, level + 1);

				JSONObject node = new JSONObject();
				node.put("id", data.oid);
				node.put("title", data.number + ", " + data.name);
				if (WorkInProgressHelper.isCheckedOut(childPart)) {
					node.put("icon", "/Windchill/jsp/images/editor/partcheckout.gif");
				} else {
					node.put("icon", data.iconPath);
				}
				node.put("lazy", true);
				node.put("minus", data.isMinus);
				node.put("thumnail", data.thumnail[1]);
				node.put("qty", data.qty);
				node.put("version", data.version);
				node.put("name", data.name);
				node.put("number", data.number);
				node.put("fNumber", data.findNumber);

				// if (level == 0) {
				// node.put("expanded", false);
				// } else {
				node.put("expanded", true);
				// }
				node.put("folder", true);
				getSubBomData(childPart, node, data.level);
				jsonChildren.add(node);
				Collections.sort(jsonChildren, new BomCompare());
			}
			rootNode.put("children", jsonChildren);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WTPartUsageLink getUsageLink(WTPart parent, WTPart child) throws Exception {
		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(WTPartUsageLink.class, true);

		SearchCondition sc = null;
		long pid = parent.getPersistInfo().getObjectIdentifier().getId();
		sc = new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", "=", pid);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		long id = child.getMaster().getPersistInfo().getObjectIdentifier().getId();
		sc = new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=", id);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		WTPartUsageLink link = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			link = (WTPartUsageLink) obj[0];
		}
		return link;
	}

	public Map<String, Object> getBomBind(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		try {

			String[] s = new String[] { "BOM", "NON BOM" };
			Map<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("name", "선택");
			emptyMap.put("value", "");
			list.add(0, emptyMap);

			for (String ss : s) {
				String value = ss;
				String display = ss;

				Map<String, Object> ptMap = new HashMap<String, Object>();
				ptMap.put("name", display);
				ptMap.put("value", value);
				list.add(ptMap);
			}
			map.put("result", SUCCESS);
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
		}
		return map;
	}

	public Map<String, Object> checkPartNumber(Map<String, Object> param) throws Exception {
		String partNumber = (String) param.get("partNumber");
		// String context = (String) param.get("context");
		// String location = (String) param.get("location");
		int index = (int) param.get("index");
		Map<String, Object> map = new HashMap<String, Object>();

		System.out.println("param=" + param);

		try {

			if (!StringUtils.isNull(partNumber)) {

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(WTPartMaster.class, true);

				SearchCondition sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, "=",
						partNumber.toUpperCase());
				query.appendWhere(sc, new int[] { idx });

				QueryResult result = PersistenceHelper.manager.find(query);

				if (result.hasMoreElements()) {
					map.put("result", SUCCESS);
					map.put("nResult", "부품번호중복");
					map.put("index", index);
					return map;
				}
			}

			// if (!StringUtils.isNull(context)) {
			// Folder folder = null;
			// if ("라이브러리".equals(context)) {
			// folder = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
			// if(folder == null) {
			// map.put("result", SUCCESS);
			// map.put("msg", "폴더 경고");
			// }
			//
			// } else if ("도면".equals(context)) {
			// folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			// if(folder == null) {
			// map.put("result", SUCCESS);
			// map.put("msg", "폴더 경고2");
			// }
			//
			// }
			//
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> plmPartDataCheck(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		String number = (String) param.get("number");
		int index = (int) param.get("index");
		try {

			if (!StringUtils.isNull(number)) {

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(WTPartMaster.class, true);
				SearchCondition sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, "=",
						number.toUpperCase().trim());
				query.appendWhere(sc, new int[] { idx });
				QueryResult result = PersistenceHelper.manager.find(query);

				if (result.hasMoreElements()) {
					map.put("check", "true");
				} else {
					map.put("check", "false");
				}
			} else {
				map.put("check", "false");
			}

			map.put("result", SUCCESS);
			map.put("index", index);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
		}
		return map;
	}

	public Map<String, Object> plmPartCheckYcode(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		String yCode = (String) param.get("number");
		int index = (int) param.get("index");
		try {
			if (!StringUtils.isNull(yCode)) {

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(WTPart.class, true);

				IBAUtils.addIBAConditionEquals(query, WTPart.class, idx, "PART_CODE", yCode);

				System.out.println("q=" + query);
				QueryResult result = PersistenceHelper.manager.find(query);
				if (result.hasMoreElements()) {
					map.put("check", "true");
				} else {
					map.put("check", "false");
				}
			} else {
				map.put("check", "false");
			}

			map.put("result", SUCCESS);
			map.put("index", index);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
		}
		return map;
	}

	/**
	 * @param param
	 * @return QuerySpec
	 */
	public Map<String, Object> findYcode(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<PartListDataColumnData> list = new ArrayList<PartListDataColumnData>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String name = (String) param.get("name");

		String dwg_no = (String) param.get("dwg_no");
		String predate = (String) param.get("predate");
//		String statesPart = (String) param.get("statesPart");
		String postdate = (String) param.get("postdate");
//		String predate_m = (String) param.get("predate_m");
//		String postdate_m = (String) param.get("postdate_m");

//		String creatorsOid = (String) param.get("creatorsOid");
//		String modifierOid = (String) param.get("modifierOid");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

//		String location = (String) param.get("location");
//		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
//		String fileName = (String) param.get("fileName");
		// context
//		String context = (String) param.get("context");

		// String partTypes = (String) param.get("partTypes");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(PartListData.class, true);
			int idx_link = query.appendClassList(MasterDataLink.class, false);
			int idx_master = query.appendClassList(PartListMaster.class, false);
			int idx_pl = query.appendClassList(PartListMasterProjectLink.class, false);
			int idx_p = query.appendClassList(Project.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			query.appendOpenParen();

			sc = new SearchCondition(PartListMaster.class, "thePersistInfo.theObjectIdentifier.id",
					MasterDataLink.class, "roleAObjectRef.key.id");
			query.appendWhere(sc, new int[] { idx_master, idx_link });

			query.appendAnd();

			sc = new SearchCondition(Project.class, "thePersistInfo.theObjectIdentifier.id",
					PartListMasterProjectLink.class, "roleBObjectRef.key.id");
			query.appendWhere(sc, new int[] { idx_p, idx_pl });

			query.appendCloseParen();

			query.appendAnd();

			query.appendOpenParen();

			sc = new SearchCondition(PartListData.class, "thePersistInfo.theObjectIdentifier.id", MasterDataLink.class,
					"roleBObjectRef.key.id");
			query.appendWhere(sc, new int[] { idx, idx_link });

			query.appendAnd();

			sc = new SearchCondition(PartListMaster.class, "thePersistInfo.theObjectIdentifier.id",
					MasterDataLink.class, "roleAObjectRef.key.id");
			query.appendWhere(sc, new int[] { idx_master, idx_link });

			query.appendCloseParen();

			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = new SearchCondition(PartListData.class, PartListData.PART_NO, "=", number);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = new SearchCondition(PartListData.class, PartListData.PART_NAME, "=", name);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(dwg_no)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = new SearchCondition(PartListData.class, PartListData.STANDARD, "=", dwg_no);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(PartListData.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(PartListData.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
			}

			ca = new ClassAttribute(PartListData.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			System.out.println("ggg::" + query);
			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PartListData pld = (PartListData) obj[0];
				Project pro = (Project) obj[1];
				PartListDataColumnData data = new PartListDataColumnData(pld, pro);
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

	public Map<String, Object> findUnitBom(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<UnitBomColumnData> list = new ArrayList<UnitBomColumnData>();
		QuerySpec query = null;

		// search param
//		String number = (String) param.get("number");
//		String name = (String) param.get("name");

		String uCode = (String) param.get("uCode");
		String yCode = (String) param.get("yCode");
		String uSpec = (String) param.get("uSpec");
		String ySpec = (String) param.get("ySpec");
		String uPartName = (String) param.get("uPartName");
		String yPartName = (String) param.get("yPartName");

//		String dwg_no = (String) param.get("dwg_no");
//		String predate = (String) param.get("predate");
//		String statesPart = (String) param.get("statesPart");
//		String postdate = (String) param.get("postdate");
//		String predate_m = (String) param.get("predate_m");
//		String postdate_m = (String) param.get("postdate_m");
//
//		String creatorsOid = (String) param.get("creatorsOid");
//		String modifierOid = (String) param.get("modifierOid");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

//		String location = (String) param.get("location");
//		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			SearchCondition sc = null;
			ClassAttribute ca = null;
			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
			}

			int idx = query.appendClassList(UnitBom.class, true);
			query.setAdvancedQueryEnabled(true);

			if (!StringUtils.isNull(uCode)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(UnitBom.class, UnitBom.U_CODE);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(uCode);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(uSpec)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(UnitBom.class, UnitBom.SPEC);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(uSpec);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(uPartName)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(UnitBom.class, UnitBom.PART_NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(uPartName);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(yCode) || !StringUtils.isNull(ySpec) || !StringUtils.isNull(yPartName)) {
				QuerySpec subQs = searchSubQuery(yCode, ySpec, yPartName);
				SubSelectExpression subfrom = new SubSelectExpression(subQs);
				subfrom.setFromAlias(new String[] { "C0" }, 0);

				int subIndex = query.appendFrom(subfrom);

				if (query.getConditionCount() > 0)
					query.appendAnd();

				SearchCondition sc2 = new SearchCondition(
						new ClassAttribute(UnitBom.class, "thePersistInfo.theObjectIdentifier.id"), "=",
						new KeywordExpression(query.getFromClause().getAliasAt(subIndex) + ".IDA3A5"));

				sc2.setFromIndicies(new int[] { idx, subIndex }, 0);
				sc2.setOuterJoin(0);
				query.appendWhere(sc2, new int[] { idx, subIndex });
			}
			ca = new ClassAttribute(UnitBom.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				UnitBom unitBom = (UnitBom) obj[0];
				UnitBomColumnData data = new UnitBomColumnData(unitBom);
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

	private QuerySpec searchSubQuery(String yCode, String ySpec, String yPartName) throws Exception {
		QuerySpec subQs = new QuerySpec();
		int idx = subQs.appendClassList(UnitSubPart.class, false);
		int link_idx = subQs.appendClassList(UnitBomPartLink.class, false);

		subQs.setDistinct(true);
		subQs.setAdvancedQueryEnabled(true);
		subQs.appendSelect(new ClassAttribute(UnitBomPartLink.class, "roleAObjectRef.key.id"), false);

		SearchCondition sc = new SearchCondition(
				new ClassAttribute(UnitSubPart.class, "thePersistInfo.theObjectIdentifier.id"), "=",
				new ClassAttribute(UnitBomPartLink.class, "roleBObjectRef.key.id"));
		sc.setOuterJoin(0);
		subQs.appendWhere(sc, new int[] { idx, link_idx });

		if (!StringUtils.isNull(yCode)) {
			if (subQs.getConditionCount() > 0)
				subQs.appendAnd();

			SearchCondition sc2 = new SearchCondition(UnitSubPart.class, UnitSubPart.PART_NO, SearchCondition.LIKE,
					"%" + yCode.toUpperCase() + "%");
			subQs.appendWhere(sc2, new int[] { idx });
		}

		if (!StringUtils.isNull(ySpec)) {
			if (subQs.getConditionCount() > 0)
				subQs.appendAnd();

			SearchCondition sc2 = new SearchCondition(UnitSubPart.class, UnitSubPart.STANDARD, SearchCondition.LIKE,
					"%" + ySpec.toUpperCase() + "%");
			subQs.appendWhere(sc2, new int[] { idx });
		}

		if (!StringUtils.isNull(yPartName)) {
			if (subQs.getConditionCount() > 0)
				subQs.appendAnd();

			SearchCondition sc2 = new SearchCondition(UnitSubPart.class, UnitSubPart.PART_NAME, SearchCondition.LIKE,
					"%" + yPartName.toUpperCase() + "%");
			subQs.appendWhere(sc2, new int[] { idx });
		}

		return subQs;
	}

	public ArrayList<UnitBomPartLink> getUnitBomPartLink(UnitBom unitBom) throws Exception {
		ArrayList<UnitBomPartLink> list = new ArrayList<UnitBomPartLink>();

		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(UnitBomPartLink.class, true);

		SearchCondition sc = new SearchCondition(UnitBomPartLink.class, "roleAObjectRef.key.id", "=",
				unitBom.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(UnitBomPartLink.class, UnitBomPartLink.CREATE_TIMESTAMP);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			UnitBomPartLink link = (UnitBomPartLink) obj[0];
			list.add(link);
		}

		return list;
	}

	public JSONArray getBomUnitBomTree(Map<String, Object> param) throws Exception {
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		UnitBom unitBom = (UnitBom) rf.getReference(oid).getObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("id", unitBom.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("title", unitBom.getUCode());
		rootNode.put("name", unitBom.getPartName());
		rootNode.put("spec", unitBom.getSpec());
		rootNode.put("maker", unitBom.getMaker());
		rootNode.put("unit", unitBom.getUnit());
		rootNode.put("customer", unitBom.getCustomer());
		rootNode.put("icon", "/Windchill/wtcore/images/part.gif");
		rootNode.put("folder", true);
		rootNode.put("expanded", true);
//		getBomTreeSubData(root, rootNode);

		JSONArray jsonChildren = new JSONArray();

		ArrayList<UnitBomPartLink> list = PartHelper.manager.getUnitBomPartLink(unitBom);

		for (UnitBomPartLink usage : list) {
			UnitSubPart sp = usage.getSubPart();
			JSONObject node = new JSONObject();
			node.put("id", sp.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("title", sp.getPartNo());
			node.put("name", sp.getPartName());
			node.put("spec", sp.getStandard());
			node.put("quantity", sp.getQuantity());
			node.put("maker", sp.getMaker());
			node.put("unit", sp.getUnit());
			node.put("customer", sp.getCustomer());
			node.put("lotNo", sp.getLotNo());
			node.put("unitName", sp.getUnitName());

			node.put("unit", sp.getUnit());
			node.put("price", sp.getPrice());
			node.put("currency", sp.getCurrency());
			node.put("won", sp.getWon());

			node.put("classification", sp.getClassification());
			node.put("note", sp.getNote());

			node.put("icon", "/Windchill/wtcore/images/part.gif");
			node.put("folder", true);
			node.put("expanded", true);
			jsonChildren.add(node);
		}
		rootNode.put("children", jsonChildren);

		jsonArray.add(rootNode);
		return jsonArray;
	}

	/**
	 * 부품 일괄 등록시 PART_CODE IBA 값 검증 있으면 NG 리턴
	 */
	public Map<String, Object> bundleValidatorNumber(String number) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toIBAEquals(query, idx, WTPart.class, "PART_CODE", number);
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

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<PartDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.CREATE_TIMESTAMP, false);
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

	//부품 정보 관련문서
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

	//버전 이력
	public JSONArray list(WTPartMaster master) throws Exception {
		ArrayList<PartDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "masterReference.key.id", master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while(result.hasMoreElements()) {
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
