package e3ps;

import java.util.ArrayList;

import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;

public class Test {

	public static void main(String[] args) throws Exception {
		ArrayList<WTPart> list = new ArrayList<>();
		String oid = "wt.part.WTPart:1234";
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		try {
			part = (WTPart) rf.getReference(oid).getObject();
			QueryResult result = StructHelper.service.navigateUsedByToIteration(part, true, new LatestConfigSpec());
			while (result.hasMoreElements()) {
				WTPart p = (WTPart) result.nextElement();
				// 퍼시스에 맞게 단품인지 자재 인지 확인하는 로직으로 if문 처리 한다
				// 자재이면 게속 검색
				if (p.getPartType().toString().equals("자재")) {
					recurcivePart(p, list);
					// 단품이면 담는다
				} else if (p.getPartType().toString().equals("단품")) {
					list.add(p);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (WTPart p : list) {
			// 담겨 있는 부품이 단품이 인지 확인한다.
			System.out.println(p.getName());

		}

	}

	private static void recurcivePart(WTPart parent, ArrayList<WTPart> list) throws Exception {
		QueryResult result = StructHelper.service.navigateUsedByToIteration(parent, true, new LatestConfigSpec());
		while (result.hasMoreElements()) {
			WTPart p = (WTPart) result.nextElement();
			// 퍼시스에 맞게 단품인지 자재 인지 확인하는 로직으로 if문 처리 한다
			// 자재이면 게속 검색
			if (p.getPartType().toString().equals("자재")) {
				recurcivePart(p, list);
				// 단품이면 담는다
			} else if (p.getPartType().toString().equals("단품")) {
				list.add(p);
			}
		}
	}
}
