package e3ps.common.util;

import java.util.ArrayList;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.beans.CommonCodeColumnData;
import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.beans.CategoryColumnData;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.beans.SpecColumnData;
import e3ps.doc.column.DocumentColumnData;
import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;

public class ColumnParseUtils {

	public static ArrayList parse(PagingQueryResult qr) throws Exception {
		ArrayList list = new ArrayList<>();
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			Persistable per = (Persistable) obj[0];
			// 문서
			if (per instanceof WTDocument) {
				DocumentColumnData data = document((WTDocument) per);
				list.add(data);
				// 코드
			} else if (per instanceof CommonCode) {
				CommonCodeColumnData data = commonCode((CommonCode) per);
				list.add(data);
				// 사양
			} else if (per instanceof Spec) {
				SpecColumnData data = spec((Spec) per);
				list.add(data);
				// CS 카테고리
			} else if (per instanceof Category) {
				CategoryItemsLink link = (CategoryItemsLink) obj[1];
				CategoryColumnData data = category((Category) per, link);
				list.add(data);
			}

		}
		return list;
	}

	private static CategoryColumnData category(Category per, CategoryItemsLink link) throws Exception {
		return new CategoryColumnData(per, link);
	}

	private static SpecColumnData spec(Spec per) throws Exception {
		return new SpecColumnData(per);
	}

	private static CommonCodeColumnData commonCode(CommonCode per) throws Exception {
		return new CommonCodeColumnData(per);
	}

	private static DocumentColumnData document(WTDocument per) throws Exception {
		return new DocumentColumnData(per);
	}
}
