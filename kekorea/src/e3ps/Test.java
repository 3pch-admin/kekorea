package e3ps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.project.Project;
import net.sf.json.JSONArray;

public class Test {

	public static void main(String[] args) throws Exception {

		String oid = "e3ps.project.Project:95197670";
		Project p = (Project) CommonUtils.getObject(oid);

		String s = String.format("%,.0f",p.getMachinePrice());
		System.out.println(s);

	}

}
