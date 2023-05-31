<%@page import="wt.vc.baseline.Baseline"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="wt.part.QuantityUnit"%>
<%@page import="wt.part.Quantity"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.part.WTPartUsageLink"%>
<%@page import="wt.vc.views.View"%>
<%@page import="wt.part.WTPartStandardConfigSpec"%>
<%@page import="wt.part.WTPartAsMaturedConfigSpec"%>
<%@page import="wt.part.WTPartConfigSpec"%>
<%@page import="e3ps.epm.service.EpmHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.part.WTPartHelper"%>
<%@page import="wt.epm.util.EPMHelper"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="e3ps.common.util.QuerySpecUtils"%>
<%@page import="wt.epm.structure.EPMMemberLink"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector.GatherFamilyMembers"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector.GatherFamilyGenerics"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector.GatherDependents"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollectedResult"%>
<%@page import="com.ptc.windchill.collector.api.cad.CadCollector"%>
<%@page import="wt.vc.config.ConfigSpec"%>
<%@page import="java.util.Arrays"%>
<%@page import="wt.filter.NavigationCriteria"%>
<%@page import="wt.fc.collections.WTArrayList"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.epm.workspaces.EPMAsStoredConfigSpec"%>
<%@page import="wt.epm.EPMDocConfigSpec"%>
<%@page import="wt.fc.collections.WTCollection"%>
<%@page import="wt.epm.EPMDocument"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String eoid = "wt.epm.EPMDocument:1509752";

EPMDocument top = (EPMDocument) CommonUtils.getObject(eoid);
WTPart topPart = EpmHelper.manager.getPart(top);

QuerySpec query = new QuerySpec(EPMMemberLink.class);
QuerySpecUtils.toOrderBy(query, 0, EPMMemberLink.class, EPMMemberLink.COMP_NUMBER, false);

EPMAsStoredConfigSpec spec = EPMAsStoredConfigSpec.newEPMAsStoredConfigSpec(top);

QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(top, query, false, spec);

HashMap<String, WTPart> preMap = new HashMap<>();

int findNumber = 1;

DecimalFormat df = new DecimalFormat("0000");

HashMap<String, WTPart> comp = new HashMap<>();
HashMap<String, WTPartUsageLink> compLink = new HashMap<>();

String preNumber = null;
while (qr.hasMoreElements()) {
	Object[] oo = (Object[]) qr.nextElement();
	EPMDocument ee = (EPMDocument) oo[1];
	
	String curNumber = ee.getNumber();

	out.println(ee.getNumber() + "<br>");
	
	
	WTPart part = comp.get(ee.getNumber());
	if (part == null) {
		part = EpmHelper.manager.getPart(ee);

		WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(topPart, part.getMaster());
		link.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
		link.setFindNumber(df.format(findNumber));
		PersistenceServerHelper.manager.insert(link);
		
		findNumber++;

		compLink.put(ee.getNumber(), link);
		comp.put(ee.getNumber(), part);
		
		if(preNumber == null) {
			preNumber = curNumber;
		} else {
			if(!preNumber.equals(curNumber)) {
				comp.remove(preNumber);
				compLink.remove(preNumber);
			}
			preNumber = curNumber;
		}
		
	} else {
		// prePart...
		long compID = part.getPersistInfo().getObjectIdentifier().getId();
		// newPart
		WTPart newPart = EpmHelper.manager.getPart(ee);
		long newID = newPart.getPersistInfo().getObjectIdentifier().getId();
		if (compID == newID) {
			WTPartUsageLink link = compLink.get(ee.getNumber());
			link.getQuantity().setAmount(link.getQuantity().getAmount() + 1);
			PersistenceServerHelper.manager.update(link);
			
// 			comp.put(ee.getNumber(), newPart);
// 			compLink.put(ee.getNumber(), link);
		} else {
			findNumber += 1;
			
			WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(topPart, newPart.getMaster());
			link.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			link.setFindNumber(df.format(findNumber));
			PersistenceServerHelper.manager.insert(link);
			comp.put(ee.getNumber(), newPart);
			compLink.put(ee.getNumber(), link);
		}
	}
}

Iterator it = comp.keySet().iterator();
while (it.hasNext()) {
	String key = (String) it.next();
	WTPart part = comp.get(key);
	// 	out.println(part + "<br>");
}

out.println("종료");

// 모든 링크 제거..

WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec
		.newWTPartStandardConfigSpec((View) topPart.getView().getObject(), null);

QueryResult result = WTPartHelper.service.getUsesWTParts(topPart, configSpec);
// while (result.hasMoreElements()) {
// 	Object[] obj = (Object[]) result.nextElement();
// 	// 1.. wtpart
// 	WTPart p = (WTPart) obj[1];
// 	WTPartUsageLink link = (WTPartUsageLink) obj[0];
// 	PersistenceServerHelper.manager.remove(link);
// }
%>