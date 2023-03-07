package e3ps.common.util;

import java.util.ArrayList;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.dto.CommonCodeDTO;
import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.beans.CategoryColumnData;
import e3ps.admin.spec.Spec;
import e3ps.doc.column.DocumentColumnData;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.epm.dto.EpmDTO;
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.dto.CipDTO;
import e3ps.part.beans.PartColumnData;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.beans.KePartColumnData;
import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.part.WTPart;

public class ColumnParseUtils {

	public static ArrayList parse(PagingQueryResult qr) throws Exception {
		ArrayList list = new ArrayList<>();
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			Persistable per = (Persistable) obj[0];

			// 의뢰서
			if (per instanceof RequestDocument) {
				RequestDocumentProjectLink link = (RequestDocumentProjectLink) obj[1];
				RequestDocumentDTO data = parse(link);
				list.add(data);
				// 문서
			} else if (per instanceof WTDocument) {
				DocumentColumnData data = parse((WTDocument) per);
				list.add(data);
				// 코드
			} else if (per instanceof CommonCode) {
				CommonCodeDTO data = parse((CommonCode) per);
				list.add(data);
				// 사양
			} else if (per instanceof Spec) {
//				SpecColumnData data = parse((Spec) per);
//				list.add(data);
				// CS 카테고리
			} else if (per instanceof Category) {
				CategoryItemsLink link = (CategoryItemsLink) obj[1];
				CategoryColumnData data = parse((Category) per, link);
				list.add(data);
				// 프로젝트
			} else if (per instanceof Project) {
				ProjectDTO data = parse((Project) per);
				list.add(data);
				// Cip
			} else if (per instanceof Cip) {
				CipDTO data = parse((Cip) per);
				list.add(data);
				// kepart
			} else if (per instanceof KePart) {
				KePartColumnData data = parse((KePart) per);
				list.add(data);
				// epm
			} else if (per instanceof EPMDocument) {
				EpmDTO data = parse((EPMDocument) per);
				list.add(data);
				// wtpart
			} else if (per instanceof WTPart) {
				PartColumnData data = parse((WTPart) per);
				list.add(data);
			}
		}
		return list;
	}

	private static PartColumnData parse(WTPart per) throws Exception {
		return new PartColumnData(per);
	}

	private static EpmDTO parse(EPMDocument per) throws Exception {
		return new EpmDTO(per);
	}

	private static KePartColumnData parse(KePart per) throws Exception {
		return new KePartColumnData(per);
	}

	private static CategoryColumnData parse(Category per, CategoryItemsLink link) throws Exception {
		return new CategoryColumnData(per, link);
	}

//	private static SpecColumnData parse(Spec per) throws Exception {
//		return new SpecColumnData(per);
//	}

	private static CommonCodeDTO parse(CommonCode per) throws Exception {
		return new CommonCodeDTO(per);
	}

	private static RequestDocumentDTO parse(RequestDocumentProjectLink per) throws Exception {
		return new RequestDocumentDTO(per);
	}

	private static DocumentColumnData parse(WTDocument per) throws Exception {
		return new DocumentColumnData(per);
	}

	private static ProjectDTO parse(Project per) throws Exception {
		return new ProjectDTO(per);
	}

	private static CipDTO parse(Cip per) throws Exception {
		return new CipDTO(per);
	}
}
