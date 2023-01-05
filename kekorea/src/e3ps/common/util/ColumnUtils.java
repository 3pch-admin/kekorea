package e3ps.common.util;

import e3ps.admin.column.CodeColumnKeys;
import e3ps.admin.column.LoginHistoryColumnKeys;
import e3ps.admin.column.MailColumnKeys;
import e3ps.approval.column.AgreeColumnKeys;
import e3ps.approval.column.ApprovalColumnKeys;
import e3ps.approval.column.CompleteColumnKeys;
import e3ps.approval.column.IngColumnKeys;
import e3ps.approval.column.NoticeColumnKeys;
import e3ps.approval.column.ReceiveColumnKeys;
import e3ps.approval.column.ReturnColumnKeys;
import e3ps.common.content.column.ContentsColumnKeys;
import e3ps.doc.column.DocumentColumnKeys;
import e3ps.doc.column.OldOutputColumnKeys;
import e3ps.doc.column.OutputColumnKeys;
import e3ps.doc.column.RequestDocumentColumnKeys;
import e3ps.epm.column.EpmLibraryColumnKeys;
import e3ps.epm.column.EpmProductColumnKeys;
import e3ps.epm.column.ViewerColumnKeys;
import e3ps.org.column.UserColumnKeys;
import e3ps.org.service.OrgHelper;
import e3ps.part.column.BomColumnKeys;
import e3ps.part.column.PartElecColumnKeys;
import e3ps.part.column.PartLibraryColumnKeys;
import e3ps.part.column.PartListDataColumnKeys;
import e3ps.part.column.PartProductColumnKeys;
import e3ps.part.column.UnitBomColumnKeys;
import e3ps.partlist.column.PartListMasterColumnKeys;
import e3ps.project.column.AddProjectColumnKeys;
import e3ps.project.column.IssueColumnKeys;
import e3ps.project.column.ProjectColumnKeys;
import e3ps.project.column.TemplateColumnKeys;

public class ColumnUtils {

	// private static final int width = 1300;

	private static final String nameKey = "name";
	private static final String numberKey = "number";
	private static final String stateKey = "state";
	private static final String versionKey = "version";
	private static final String createDateKey = "createDate";
	private static final String creatorKey = "creator";

	private static final String nameDisplay = "명";
	private static final String numberDisplay = "번호";
	private static final String stateDisplay = "상태";
	private static final String versionDisplay = "버전";
	private static final String createDateDisplay = "등록일";
	private static final String creatorDisplay = "등록자";

	private static final int defaultWidth = 1200;

	private ColumnUtils() {

	}

	public static String[] getHeaders(String module) {
		String[] headers = null;

		if ("add_list_project".equals(module)) {
			headers = new String[AddProjectColumnKeys.values().length];
			for (int i = 0; i < AddProjectColumnKeys.values().length; i++) {
				String header = AddProjectColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_unit_bom".equals(module)) {
			headers = new String[UnitBomColumnKeys.values().length];
			for (int i = 0; i < UnitBomColumnKeys.values().length; i++) {
				String header = UnitBomColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_ycode".equals(module)) {
			headers = new String[PartListDataColumnKeys.values().length];
			for (int i = 0; i < PartListDataColumnKeys.values().length; i++) {
				String header = PartListDataColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_old_output".equals(module)) {
			headers = new String[OldOutputColumnKeys.values().length];
			for (int i = 0; i < OldOutputColumnKeys.values().length; i++) {
				String header = OldOutputColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_viewer".equals(module)) {
			headers = new String[ViewerColumnKeys.values().length];
			for (int i = 0; i < ViewerColumnKeys.values().length; i++) {
				String header = ViewerColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_request_document".equals(module)) {
			headers = new String[RequestDocumentColumnKeys.values().length];
			for (int i = 0; i < RequestDocumentColumnKeys.values().length; i++) {
				String header = RequestDocumentColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_partlist".equals(module)) {
			headers = new String[PartListMasterColumnKeys.values().length];
			for (int i = 0; i < PartListMasterColumnKeys.values().length; i++) {
				String header = PartListMasterColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_template".equals(module)) {
			headers = new String[TemplateColumnKeys.values().length];
			for (int i = 0; i < TemplateColumnKeys.values().length; i++) {
				String header = TemplateColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_project".equals(module)) {
			headers = new String[ProjectColumnKeys.values().length];
			for (int i = 0; i < ProjectColumnKeys.values().length; i++) {
				String header = ProjectColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("contents_list".equals(module)) {
			headers = new String[ContentsColumnKeys.values().length];
			for (int i = 0; i < ContentsColumnKeys.values().length; i++) {
				String header = ContentsColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_code".equals(module)) {
			headers = new String[CodeColumnKeys.values().length];
			for (int i = 0; i < CodeColumnKeys.values().length; i++) {
				String header = CodeColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_user".equals(module) || "add_list_user".equals(module)) {
			headers = new String[UserColumnKeys.values().length];
			for (int i = 0; i < UserColumnKeys.values().length; i++) {
				String header = UserColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_notice".equals(module)) {
			headers = new String[NoticeColumnKeys.values().length];
			for (int i = 0; i < NoticeColumnKeys.values().length; i++) {
				String header = NoticeColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_login".equals(module)) {
			headers = new String[LoginHistoryColumnKeys.values().length];
			for (int i = 0; i < LoginHistoryColumnKeys.values().length; i++) {
				String header = LoginHistoryColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_product_part".equals(module) || "add_product_part".equals(module)) {
			headers = new String[PartProductColumnKeys.values().length];
			for (int i = 0; i < PartProductColumnKeys.values().length; i++) {
				String header = PartProductColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_library_part".equals(module) || "add_library_part".equals(module)) {
			headers = new String[PartLibraryColumnKeys.values().length];
			for (int i = 0; i < PartLibraryColumnKeys.values().length; i++) {
				String header = PartLibraryColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_eplan_part".equals(module) || "add_eplan_part".equals(module)) {
			headers = new String[PartLibraryColumnKeys.values().length];
			for (int i = 0; i < PartLibraryColumnKeys.values().length; i++) {
				String header = PartLibraryColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_elec_part".equals(module) || "add_elec_part".equals(module)) {
			headers = new String[PartElecColumnKeys.values().length];
			for (int i = 0; i < PartElecColumnKeys.values().length; i++) {
				String header = PartElecColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_product_epm".equals(module) || "add_product_epm".equals(module)) {
			headers = new String[EpmProductColumnKeys.values().length];
			for (int i = 0; i < EpmProductColumnKeys.values().length; i++) {
				String header = EpmProductColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_library_epm".equals(module) || "add_library_epm".equals(module)) {
			headers = new String[EpmLibraryColumnKeys.values().length];
			for (int i = 0; i < EpmLibraryColumnKeys.values().length; i++) {
				String header = EpmLibraryColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_output".equals(module)) {
			headers = new String[OutputColumnKeys.values().length];
			for (int i = 0; i < OutputColumnKeys.values().length; i++) {
				String header = OutputColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_document".equals(module) || "add_list_document".equals(module)) {
			headers = new String[e3ps.doc.column.DocumentColumnKeys.values().length];
			for (int i = 0; i < DocumentColumnKeys.values().length; i++) {
				String header = DocumentColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_old_document".equals(module) || "add_list_old_document".equals(module)) {
			headers = new String[e3ps.doc.column.DocumentColumnKeys.values().length];
			for (int i = 0; i < DocumentColumnKeys.values().length; i++) {
				String header = DocumentColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_approval".equals(module)) {
			headers = new String[ApprovalColumnKeys.values().length];
			for (int i = 0; i < ApprovalColumnKeys.values().length; i++) {
				String header = ApprovalColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_ing".equals(module)) {
			headers = new String[IngColumnKeys.values().length];
			for (int i = 0; i < IngColumnKeys.values().length; i++) {
				String header = IngColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_complete".equals(module)) {
			headers = new String[CompleteColumnKeys.values().length];
			for (int i = 0; i < CompleteColumnKeys.values().length; i++) {
				String header = CompleteColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_receive".equals(module)) {
			headers = new String[ReceiveColumnKeys.values().length];
			for (int i = 0; i < ReceiveColumnKeys.values().length; i++) {
				String header = ReceiveColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_agree".equals(module)) {
			headers = new String[AgreeColumnKeys.values().length];
			for (int i = 0; i < AgreeColumnKeys.values().length; i++) {
				String header = AgreeColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_return".equals(module)) {
			headers = new String[ReturnColumnKeys.values().length];
			for (int i = 0; i < ReturnColumnKeys.values().length; i++) {
				String header = ReturnColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_login".equals(module)) {
			headers = new String[LoginHistoryColumnKeys.values().length];
			for (int i = 0; i < LoginHistoryColumnKeys.values().length; i++) {
				String header = LoginHistoryColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_mail".equals(module)) {
			headers = new String[MailColumnKeys.values().length];
			for (int i = 0; i < MailColumnKeys.values().length; i++) {
				String header = MailColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_bom".equals(module)) {
			headers = new String[BomColumnKeys.values().length];
			for (int i = 0; i < BomColumnKeys.values().length; i++) {
				String header = BomColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		} else if ("list_issue".equals(module)) {
			headers = new String[IssueColumnKeys.values().length];
			for (int i = 0; i < IssueColumnKeys.values().length; i++) {
				String header = IssueColumnKeys.values()[i].getDisplay();
				headers[i] = header;
			}
		}
		return headers;
	}

	public static String[] getColumnKey(String module) {
		String[] keys = null;
		if ("add_list_project".equals(module)) {
			keys = new String[AddProjectColumnKeys.values().length];
			for (int i = 0; i < AddProjectColumnKeys.values().length; i++) {
				String key = AddProjectColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_unit_bom".equals(module)) {
			keys = new String[UnitBomColumnKeys.values().length];
			for (int i = 0; i < UnitBomColumnKeys.values().length; i++) {
				String key = UnitBomColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_ycode".equals(module)) {
			keys = new String[PartListDataColumnKeys.values().length];
			for (int i = 0; i < PartListDataColumnKeys.values().length; i++) {
				String key = PartListDataColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_viewer".equals(module)) {
			keys = new String[ViewerColumnKeys.values().length];
			for (int i = 0; i < ViewerColumnKeys.values().length; i++) {
				String key = ViewerColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_request_document".equals(module)) {
			keys = new String[RequestDocumentColumnKeys.values().length];
			for (int i = 0; i < RequestDocumentColumnKeys.values().length; i++) {
				String key = RequestDocumentColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_partlist".equals(module)) {
			keys = new String[PartListMasterColumnKeys.values().length];
			for (int i = 0; i < PartListMasterColumnKeys.values().length; i++) {
				String key = PartListMasterColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_template".equals(module)) {
			keys = new String[TemplateColumnKeys.values().length];
			for (int i = 0; i < TemplateColumnKeys.values().length; i++) {
				String key = TemplateColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_project".equals(module)) {
			keys = new String[ProjectColumnKeys.values().length];
			for (int i = 0; i < ProjectColumnKeys.values().length; i++) {
				String key = ProjectColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("contents_list".equals(module)) {
			keys = new String[ContentsColumnKeys.values().length];
			for (int i = 0; i < ContentsColumnKeys.values().length; i++) {
				String key = ContentsColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_code".equals(module)) {
			keys = new String[CodeColumnKeys.values().length];
			for (int i = 0; i < CodeColumnKeys.values().length; i++) {
				String key = CodeColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_user".equals(module) || "add_list_user".equals(module)) {
			keys = new String[UserColumnKeys.values().length];
			for (int i = 0; i < UserColumnKeys.values().length; i++) {
				String key = UserColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_notice".equals(module)) {
			keys = new String[NoticeColumnKeys.values().length];
			for (int i = 0; i < NoticeColumnKeys.values().length; i++) {
				String key = NoticeColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_login".equals(module)) {
			keys = new String[LoginHistoryColumnKeys.values().length];
			for (int i = 0; i < LoginHistoryColumnKeys.values().length; i++) {
				String key = LoginHistoryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_product_part".equals(module) || "add_product_part".equals(module)) {
			keys = new String[PartProductColumnKeys.values().length];
			for (int i = 0; i < PartProductColumnKeys.values().length; i++) {
				String key = PartProductColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_library_part".equals(module) || "add_library_part".equals(module)) {
			keys = new String[PartLibraryColumnKeys.values().length];
			for (int i = 0; i < PartLibraryColumnKeys.values().length; i++) {
				String key = PartLibraryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_eplan_part".equals(module) || "add_eplan_part".equals(module)) {
			keys = new String[PartLibraryColumnKeys.values().length];
			for (int i = 0; i < PartLibraryColumnKeys.values().length; i++) {
				String key = PartLibraryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_elec_part".equals(module) || "add_elec_part".equals(module)) {
			keys = new String[PartElecColumnKeys.values().length];
			for (int i = 0; i < PartElecColumnKeys.values().length; i++) {
				String key = PartElecColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_product_epm".equals(module) || "add_product_epm".equals(module)) {
			keys = new String[EpmProductColumnKeys.values().length];
			for (int i = 0; i < EpmProductColumnKeys.values().length; i++) {
				String key = EpmProductColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_library_epm".equals(module) || "add_library_epm".equals(module)) {
			keys = new String[EpmLibraryColumnKeys.values().length];
			for (int i = 0; i < EpmLibraryColumnKeys.values().length; i++) {
				String key = EpmLibraryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_output".equals(module)) {
			keys = new String[OutputColumnKeys.values().length];
			for (int i = 0; i < OutputColumnKeys.values().length; i++) {
				String key = OutputColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_document".equals(module) || "add_list_document".equals(module)) {
			keys = new String[DocumentColumnKeys.values().length];
			for (int i = 0; i < DocumentColumnKeys.values().length; i++) {
				String key = DocumentColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_old_document".equals(module) || "add_list_old_document".equals(module)) {
			keys = new String[DocumentColumnKeys.values().length];
			for (int i = 0; i < DocumentColumnKeys.values().length; i++) {
				String key = DocumentColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_approval".equals(module)) {
			keys = new String[ApprovalColumnKeys.values().length];
			for (int i = 0; i < ApprovalColumnKeys.values().length; i++) {
				String key = ApprovalColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_ing".equals(module)) {
			keys = new String[IngColumnKeys.values().length];
			for (int i = 0; i < IngColumnKeys.values().length; i++) {
				String key = IngColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_complete".equals(module)) {
			keys = new String[CompleteColumnKeys.values().length];
			for (int i = 0; i < CompleteColumnKeys.values().length; i++) {
				String key = CompleteColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_receive".equals(module)) {
			keys = new String[ReceiveColumnKeys.values().length];
			for (int i = 0; i < ReceiveColumnKeys.values().length; i++) {
				String key = ReceiveColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_agree".equals(module)) {
			keys = new String[AgreeColumnKeys.values().length];
			for (int i = 0; i < AgreeColumnKeys.values().length; i++) {
				String key = AgreeColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_return".equals(module)) {
			keys = new String[ReturnColumnKeys.values().length];
			for (int i = 0; i < ReturnColumnKeys.values().length; i++) {
				String key = ReturnColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_login".equals(module)) {
			keys = new String[LoginHistoryColumnKeys.values().length];
			for (int i = 0; i < LoginHistoryColumnKeys.values().length; i++) {
				String key = LoginHistoryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_mail".equals(module)) {
			keys = new String[MailColumnKeys.values().length];
			for (int i = 0; i < MailColumnKeys.values().length; i++) {
				String key = MailColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_bom".equals(module)) {
			keys = new String[BomColumnKeys.values().length];
			for (int i = 0; i < BomColumnKeys.values().length; i++) {
				String key = BomColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_issue".equals(module)) {
			keys = new String[IssueColumnKeys.values().length];
			for (int i = 0; i < IssueColumnKeys.values().length; i++) {
				String header = IssueColumnKeys.values()[i].name();
				keys[i] = header;
			}
		} else if ("list_old_output".equals(module)) {
			keys = new String[OldOutputColumnKeys.values().length];
			for (int i = 0; i < OldOutputColumnKeys.values().length; i++) {
				String header = OldOutputColumnKeys.values()[i].name();
				keys[i] = header;
			}
		}
		return keys;
	}

	public static String[] getColumnStyles(String module, String[] headers) {
		String[] styles = OrgHelper.manager.getUserTableStyles(module);
		if (styles == null) {
			int size = headers.length;
			styles = new String[size];
			for (int i = 0; i < styles.length; i++) {
				styles[i] = "''";
			}
		}
		return styles;
	}

	public static String[] getColumnHeaders(String module) {
		String[] headers = OrgHelper.manager.getUserTableHeaders(module);
		String prefix = getPrefix(module);
		if (headers == null) {
			headers = getHeaders(module);
			if (headers == null) {
				headers = new String[] { prefix + numberDisplay, prefix + nameDisplay, stateDisplay, versionDisplay,
						creatorDisplay, createDateDisplay };
			}

		}
		return headers;
	}

	public static String[] getColumnIndexs(String module) {
		String[] indexs = OrgHelper.manager.getUserTableIndexs(module);
		if (indexs == null) {
			indexs = getColumnIndex(module);
			if (indexs == null) {
				indexs = new String[] { nameKey, numberKey, stateKey, versionKey, createDateKey, creatorKey };
			}
		}
		return indexs;
	}

	private static String[] getColumnIndex(String module) {
		String[] keys = null;

		if ("list_request_document".equals(module)) {
			keys = new String[RequestDocumentColumnKeys.values().length];
			for (int i = 0; i < RequestDocumentColumnKeys.values().length; i++) {
				String key = RequestDocumentColumnKeys.values()[i].getDisplay();
				keys[i] = key;
			}
		} else if ("list_partlist".equals(module)) {
			keys = new String[PartListMasterColumnKeys.values().length];
			for (int i = 0; i < PartListMasterColumnKeys.values().length; i++) {
				String key = PartListMasterColumnKeys.values()[i].getDisplay();
				keys[i] = key;
			}
		} else if ("list_ycode".equals(module)) {
			keys = new String[PartListDataColumnKeys.values().length];
			for (int i = 0; i < PartListDataColumnKeys.values().length; i++) {
				String key = PartListDataColumnKeys.values()[i].getDisplay();
				keys[i] = key;
			}
		} else if ("list_template".equals(module)) {
			keys = new String[TemplateColumnKeys.values().length];
			for (int i = 0; i < TemplateColumnKeys.values().length; i++) {
				String key = TemplateColumnKeys.values()[i].getDisplay();
				keys[i] = key;
			}
		} else if ("list_project".equals(module)) {
			keys = new String[ProjectColumnKeys.values().length];
			for (int i = 0; i < ProjectColumnKeys.values().length; i++) {
				String key = ProjectColumnKeys.values()[i].getDisplay();
				keys[i] = key;
			}
		} else if ("contents_list".equals(module)) {
			keys = new String[ContentsColumnKeys.values().length];
			for (int i = 0; i < ContentsColumnKeys.values().length; i++) {
				String key = ContentsColumnKeys.values()[i].getDisplay();
				keys[i] = key;
			}
		} else if ("list_code".equals(module)) {
			keys = new String[CodeColumnKeys.values().length];
			for (int i = 0; i < CodeColumnKeys.values().length; i++) {
				String key = CodeColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_user".equals(module) || "add_list_user".equals(module)) {
			keys = new String[UserColumnKeys.values().length];
			for (int i = 0; i < UserColumnKeys.values().length; i++) {
				String key = UserColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_notice".equals(module)) {
			keys = new String[NoticeColumnKeys.values().length];
			for (int i = 0; i < NoticeColumnKeys.values().length; i++) {
				String key = NoticeColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_login".equals(module)) {
			keys = new String[LoginHistoryColumnKeys.values().length];
			for (int i = 0; i < LoginHistoryColumnKeys.values().length; i++) {
				String key = LoginHistoryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_product_part".equals(module) || "add_product_part".equals(module)) {
			keys = new String[PartProductColumnKeys.values().length];
			for (int i = 0; i < PartProductColumnKeys.values().length; i++) {
				String key = PartProductColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_library_part".equals(module) || "add_library_part".equals(module)) {
			keys = new String[PartLibraryColumnKeys.values().length];
			for (int i = 0; i < PartLibraryColumnKeys.values().length; i++) {
				String key = PartLibraryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_eplan_part".equals(module) || "add_eplan_part".equals(module)) {
			keys = new String[PartLibraryColumnKeys.values().length];
			for (int i = 0; i < PartLibraryColumnKeys.values().length; i++) {
				String key = PartLibraryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_elec_part".equals(module) || "add_elec_part".equals(module)) {
			keys = new String[PartElecColumnKeys.values().length];
			for (int i = 0; i < PartElecColumnKeys.values().length; i++) {
				String key = PartElecColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_product_epm".equals(module) || "add_product_epm".equals(module)) {
			keys = new String[EpmProductColumnKeys.values().length];
			for (int i = 0; i < EpmProductColumnKeys.values().length; i++) {
				String key = EpmProductColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_library_epm".equals(module) || "add_library_epm".equals(module)) {
			keys = new String[EpmLibraryColumnKeys.values().length];
			for (int i = 0; i < EpmLibraryColumnKeys.values().length; i++) {
				String key = EpmLibraryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_document".equals(module) || "add_list_document".equals(module)
				|| "list_output".equals(module)) {
			keys = new String[DocumentColumnKeys.values().length];
			for (int i = 0; i < DocumentColumnKeys.values().length; i++) {
				String key = DocumentColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_old_document".equals(module) || "add_list_old_document".equals(module)) {
			keys = new String[DocumentColumnKeys.values().length];
			for (int i = 0; i < DocumentColumnKeys.values().length; i++) {
				String key = DocumentColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_approval".equals(module)) {
			keys = new String[ApprovalColumnKeys.values().length];
			for (int i = 0; i < ApprovalColumnKeys.values().length; i++) {
				String key = ApprovalColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_ing".equals(module)) {
			keys = new String[IngColumnKeys.values().length];
			for (int i = 0; i < IngColumnKeys.values().length; i++) {
				String key = IngColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_complete".equals(module)) {
			keys = new String[CompleteColumnKeys.values().length];
			for (int i = 0; i < CompleteColumnKeys.values().length; i++) {
				String key = CompleteColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_receive".equals(module)) {
			keys = new String[ReceiveColumnKeys.values().length];
			for (int i = 0; i < ReceiveColumnKeys.values().length; i++) {
				String key = ReceiveColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_agree".equals(module)) {
			keys = new String[AgreeColumnKeys.values().length];
			for (int i = 0; i < AgreeColumnKeys.values().length; i++) {
				String key = AgreeColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_return".equals(module)) {
			keys = new String[ReturnColumnKeys.values().length];
			for (int i = 0; i < ReturnColumnKeys.values().length; i++) {
				String key = ReturnColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_login".equals(module)) {
			keys = new String[LoginHistoryColumnKeys.values().length];
			for (int i = 0; i < LoginHistoryColumnKeys.values().length; i++) {
				String key = LoginHistoryColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_mail".equals(module)) {
			keys = new String[MailColumnKeys.values().length];
			for (int i = 0; i < MailColumnKeys.values().length; i++) {
				String key = MailColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_bom".equals(module)) {
			keys = new String[BomColumnKeys.values().length];
			for (int i = 0; i < BomColumnKeys.values().length; i++) {
				String key = BomColumnKeys.values()[i].name();
				keys[i] = key;
			}
		} else if ("list_issue".equals(module)) {
			keys = new String[IssueColumnKeys.values().length];
			for (int i = 0; i < IssueColumnKeys.values().length; i++) {
				String header = IssueColumnKeys.values()[i].getDisplay();
				keys[i] = header;
			}
		} else if ("list_old_output".equals(module)) {
			keys = new String[OldOutputColumnKeys.values().length];
			for (int i = 0; i < OldOutputColumnKeys.values().length; i++) {
				String header = OldOutputColumnKeys.values()[i].getDisplay();
				keys[i] = header;
			}
		}
		return keys;
	}

	public static String[] getColumnKeys(String module) {
		String[] keys = OrgHelper.manager.getUserTableKeys(module);
		if (keys == null) {
			keys = getColumnKey(module);
			if (keys == null) {
				keys = new String[] { nameKey, numberKey, stateKey, versionKey, createDateKey, creatorKey };
			}
		}
		return keys;
	}

	private static String getPrefix(String module) {
		String prefix = "";
		if ("part".equals(module)) {
			prefix = "부품";
		} else if ("epm".equals(module)) {
			prefix = "도면";
		} else if ("document".equals(module)) {
			prefix = "문서";
		} else if ("project".equals(module)) {
			prefix = "프로젝트";
		}
		return prefix;
	}

	public static String[] getColumnCols(String module, String[] headers) {
		// % no.. px...
		String[] cols = OrgHelper.manager.getUserTableCols(module);
		if (cols == null) {
			int size = headers.length;
			cols = new String[size];
			for (int i = 0; i < cols.length; i++) {
				if (i == 2) {
					cols[i] = "*";
				} else {
					cols[i] = String.valueOf(defaultWidth / headers.length - 1);
				}
			}
		}
		return cols;
	}
}
