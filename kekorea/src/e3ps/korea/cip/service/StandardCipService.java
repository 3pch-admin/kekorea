package e3ps.korea.cip.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.korea.cip.Cip;
import wt.pom.Transaction;
import wt.services.StandardManager;

public class StandardCipService extends StandardManager implements CipService{

	public static StandardCipService newStandardCipService() throws Exception {
		StandardCipService instance = new StandardCipService();
		return instance;
	}
	@Override
	public void create(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		
		Transaction trs = new Transaction();
		try {
			trs.start();
			
			for(Map<String, Object> addRow : addRows)  {
//				String
				
				Cip cip = Cip.newCip();
			}
		} catch (Exception e) {
			
		} finally {
			
		}
		
	}

}
