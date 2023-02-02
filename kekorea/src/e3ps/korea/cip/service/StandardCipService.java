package e3ps.korea.cip.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.korea.cip.Cip;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardCipService extends StandardManager implements CipService{

	public static StandardCipService newStandardCipService() throws Exception {
		StandardCipService instance = new StandardCipService();
		return instance;
	}
	@Override
	public void create(Map<String, Object> params) throws Exception {
//	public Map<String, Object> create(Map<String, Object> param) throws WTException {
//		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
//		ArrayList<Map<String, Object>> modifyRows = (ArrayList<Map<String, Object>>) params.get("modifyRows");
//		
//		Transaction trs = new Transaction();
//		try {
//			trs.start();
//			
//			for(Map<String, Object> addRow : addRows)  {
//				String item = (String)addRow.get("item");
//				String improvements = (String)addRow.get("improvements");
//				String improvement = (String)addRow.get("improvement");
//				String apply = (String)addRow.get("apply");
//				String note =(String)addRow.get("note");
//				
//				Cip cip = Cip.newCip();
//				cip.setItem(item);
//				cip.setImprovements(improvements);
//				cip.setImprovement(improvement);
//				cip.setApply(apply);
//				cip.setNote(note);
//				PersistenceHelper.manager.save(cip);
//			}
//			
//			for(Map<String, Object> modifyRow : addRows)  {
//				String oid = (String)modifyRow.get("oid");
//				String item = (String)modifyRow.get("item");
//				
//				Cip cip = Cip.newCip();
//				cip.setItem(item);
//				PersistenceHelper.manager.modify(cip);
//			}
//			
//			trs.commit();
//			trs = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			trs.rollback();
//			throw e;
//		} finally {
//			if(trs != null)
//				trs.rollback();
//		}
	}
}
