package e3ps.part.beans;

import java.util.Comparator;

import net.sf.json.JSONObject;

public class BomCompare implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {

		JSONObject json1 = (JSONObject) o1;
		JSONObject json2 = (JSONObject) o2;

		String f1 = json1.getString("fNumber");
		String f2 = json2.getString("fNumber");

		return f1.compareTo(f2);
	}
}
