package e3ps.part.beans;

import java.util.ArrayList;

import wt.part.WTPart;
import wt.part.WTPartUsageLink;

public class PartTreeData implements java.io.Serializable {
	public int level;
	public String oid;
	public WTPart part;
	public WTPartUsageLink link;
	public ArrayList children = new ArrayList();
	public String unit = "";
	public String bgcolor = "";
	public String flag = "";
	public double quantity = 1;
	public String itemSeq = "";
	public String lineImg = "joinbottom";
	public String number = "";
	public String name = "";
	public String plant = "";
	public String version = "";
	public String iteration = "";
	public double baseQuantity = 1;
	public String plmCode = "";
	public String maker = "";
	public String spec = "";
	public String purpose = "";
	public String location = "";
	public PartTreeData sap;
	public PartTreeData parent = null;

	public String state = "";
	public String dwgNo = "";
	public String dwgOid = "";
	public String ecoNo = "";
	public String parentId = "";
	public boolean isChildren;

	public String model = ""; // 프로젝트 코드
	public String productmethod = ""; // 제작 방법
	public String deptcode = ""; // 부서
	public String mat = ""; // 재질;
	public String finish = "";// 후처리
	public String remark = ""; // 비고
	public String weight = ""; // 무게

	public String locationOid = ""; // idx+oid;

	public PartTreeData(WTPart part, WTPartUsageLink link, int level, String rowID) throws Exception {
		this.part = part;
		this.link = link;
		this.level = level;

		PartViewData data = new PartViewData(part);

		oid = data.oid;
		number = data.number;
		name = data.name;

		state = data.state;

		version = data.version;
		iteration = data.iteration;

		if (link != null) {
			double qs = (double) link.getQuantity().getAmount();
			unit = link.getQuantity().getUnit().toString();
			quantity = qs;
			try {
				itemSeq = data.number;
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} else {
			unit = part.getDefaultUnit().toString();
		}
		parentId = rowID;
	}

	public boolean compare(PartTreeData dd) {
		if (!unit.equals(dd.unit)) {
			return false;
		}
		if (quantity != dd.quantity) {
			return false;
		}
		if (baseQuantity != dd.baseQuantity) {
			return false;
		}
		return true;
	}

	public boolean equalsNumber(PartTreeData data) {
		return part.getNumber().equals(data.part.getNumber());
	}

	public void setLocationOid(String locationOid) {
		this.locationOid = locationOid;
	}

}
