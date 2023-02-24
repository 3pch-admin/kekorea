package e3ps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import e3ps.bom.tbom.TBOMData;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.TBOMMasterDataLink;
import e3ps.bom.tbom.beans.TBOMColumnData;
import e3ps.bom.tbom.service.TBOMHelper;
import e3ps.common.util.CommonUtils;

public class Test {

	public static void main(String[] args) throws Exception {

		// 256727
		// 256745

		String oid = "e3ps.bom.tbom.TBOMMaster:256745";
		String oid2 = "e3ps.bom.tbom.TBOMMaster:256762";

		TBOMMaster m = (TBOMMaster) CommonUtils.getObject(oid);
		TBOMMaster m2 = (TBOMMaster) CommonUtils.getObject(oid2);

		// 1 2 3 4 5
		// 3 4 5 6

		// 1 2

		ArrayList<TBOMData> link = TBOMHelper.manager.getData(m);
		ArrayList<TBOMData> link2 = TBOMHelper.manager.getData(m2);
		ArrayList<TBOMData> link3 = new ArrayList<>();
		for (TBOMData data : link) {
			boolean isEquals = false;
			for (TBOMData data2 : link2) {
				String num = data.getKePart().getMaster().getKePartNumber();
				String num2 = data.getKePart().getMaster().getKePartNumber();
				if (num.equals(num2)) {
					link3.add(data2);
					link2.remove(data2);
					isEquals = true;
					break;
				}
			}
			System.out.println(isEquals);
			if (!isEquals) {
				link3.add(data);
			}
		}
		for (TBOMData dd : link3) {
			System.out.println("d=" + dd.getKePart().getMaster().getKePartNumber());
		}
		System.exit(0);
	}
}
