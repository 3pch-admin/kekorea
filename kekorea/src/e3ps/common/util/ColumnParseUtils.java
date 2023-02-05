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
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.beans.CipColumnData;
import e3ps.project.Project;
import e3ps.project.beans.ProjectColumnData;
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
				DocumentColumnData data = parse((WTDocument) per);
				list.add(data);
				// 코드
			} else if (per instanceof CommonCode) {
				CommonCodeColumnData data = parse((CommonCode) per);
				list.add(data);
				// 사양
			} else if (per instanceof Spec) {
				SpecColumnData data = parse((Spec) per);
				list.add(data);
				// CS 카테고리
			} else if (per instanceof Category) {
				CategoryItemsLink link = (CategoryItemsLink) obj[1];
				CategoryColumnData data = parse((Category) per, link);
				list.add(data);
				// 프로젝트
			} else if (per instanceof Project) {
				ProjectColumnData data = parse((Project) per);
				list.add(data);
				// Cip
			} else if(per instanceof Cip) {
				CipColumnData data = parse((Cip)per));
				list.add(data);
			}
		}
		return list;
	}

	private static CategoryColumnData parse(Category per, CategoryItemsLink link) throws Exception {
		return new CategoryColumnData(per, link);
	}

	private static SpecColumnData parse(Spec per) throws Exception {
		return new SpecColumnData(per);
	}

	private static CommonCodeColumnData parse(CommonCode per) throws Exception {
		return new CommonCodeColumnData(per);
	}

	private static DocumentColumnData parse(WTDocument per) throws Exception {
		return new DocumentColumnData(per);
	}

	private static ProjectColumnData parse(Project per) throws Exception {
		return new ProjectColumnData(per);
	}

	private static CipColumnData parse(Cip per) throws Exception {
		return new CipColumnData(per);
	}
}
