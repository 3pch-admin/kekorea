package e3ps.common.util;

import java.util.ArrayList;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.dto.CommonCodeDTO;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.doc.dto.DocumentDTO;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.epm.dto.EpmDTO;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.dto.CipDTO;
import e3ps.org.People;
import e3ps.org.dto.UserDTO;
import e3ps.part.dto.PartDTO;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.beans.KePartDTO;
import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.part.WTPart;

public class AUILazyLoadUtils {

	public static ArrayList parse(PagingQueryResult qr) throws Exception {
		ArrayList list = new ArrayList<>();
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			Persistable per = (Persistable) obj[0];

			System.out.println("Lazy Loading Data Class = " + per.getClass().getName());

			// 의뢰서
			if (per instanceof RequestDocument) {
				RequestDocumentProjectLink link = (RequestDocumentProjectLink) obj[1];
				RequestDocumentDTO data = parse(link);
				list.add(data);
				// 문서
			} else if (per instanceof WTDocument) {
				DocumentDTO data = parse((WTDocument) per);
				list.add(data);
				// 코드
			} else if (per instanceof CommonCode) {
				CommonCodeDTO data = parse((CommonCode) per);
				list.add(data);
				// 사양
			} else if (per instanceof Project) {
				ProjectDTO data = parse((Project) per);
				list.add(data);
				// Cip
			} else if (per instanceof Cip) {
				CipDTO data = parse((Cip) per);
				list.add(data);
				// kepart
			} else if (per instanceof KePart) {
				KePartDTO data = parse((KePart) per);
				list.add(data);
				// epm
			} else if (per instanceof EPMDocument) {
				EpmDTO data = parse((EPMDocument) per);
				list.add(data);
				// wtpart
			} else if (per instanceof WTPart) {
				PartDTO data = parse((WTPart) per);
				list.add(data);
			} else if (per instanceof PartListMaster) {
				PartListMasterProjectLink link = (PartListMasterProjectLink) obj[1];
				PartListDTO data = parse(link);
				list.add(data);
				// ke 도면
			} else if (per instanceof KeDrawing) {
				KeDrawing keDrawing = (KeDrawing) per;
				KeDrawingDTO data = parse(keDrawing);
				list.add(data);
			} else if (per instanceof TBOMMaster) {
//				TBOMMasterProjectLink link = (TBOMMasterProjectLink)oo[2];
//				TBOMMaster master = (TBOMMaster) per;
//				TBOMMasterDTO data = parse(master);
			} else if (per instanceof WorkOrder) {
//				WorkOrder workOrder = (WorkOrder) per;
//				WorkOrderDTO data = parse(workOrder);
			} else if (per instanceof People) {
				People people = (People) per;
				UserDTO data = parse(people);
				list.add(data);
			}
		}
		return list;
	}

//	private static TBOMMasterDTO parse(TBOMMaster master) throws Exception {
////		return new TBOMMasterDTO(master);
//	}
//
//	private static WorkOrderDTO parse(WorkOrder workOrder) throws Exception {
//		return new WorkOrderDTO(workOrder);
//	}

	/**
	 * 사용자 LAZY 로드
	 */
	private static UserDTO parse(People people) throws Exception {
		return new UserDTO(people);
	}

	/**
	 * KE 도면 LAZY 로드
	 */
	private static KeDrawingDTO parse(KeDrawing keDrawing) throws Exception {
		return new KeDrawingDTO(keDrawing);
	}

	/**
	 * 수배표 LAZY 로드
	 */
	private static PartListDTO parse(PartListMasterProjectLink per) throws Exception {
		return new PartListDTO(per);
	}

	/**
	 * 부품 LAZY 로드
	 */
	private static PartDTO parse(WTPart per) throws Exception {
		return new PartDTO(per);
	}

	/**
	 * 도면 LAZY 로드
	 */
	private static EpmDTO parse(EPMDocument per) throws Exception {
		return new EpmDTO(per);
	}

	/**
	 * KE 부품 LAZY 로드
	 */
	private static KePartDTO parse(KePart per) throws Exception {
		return new KePartDTO(per);
	}

	private static CommonCodeDTO parse(CommonCode per) throws Exception {
		return new CommonCodeDTO(per);
	}

	private static RequestDocumentDTO parse(RequestDocumentProjectLink per) throws Exception {
		return new RequestDocumentDTO(per);
	}

	private static DocumentDTO parse(WTDocument per) throws Exception {
		return new DocumentDTO(per);
	}

	private static ProjectDTO parse(Project per) throws Exception {
		return new ProjectDTO(per);
	}

	private static CipDTO parse(Cip per) throws Exception {
		return new CipDTO(per);
	}
}
