<%@page import="wt.epm.build.EPMBuildHistory"%>
<%@page import="wt.epm.build.EPMBuildRule"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
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
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.part.WTPartHelper"%>
<%@page import="wt.epm.util.EPMHelper"%>
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
<%@page import="wt.epm.workspaces.EPMAsStoredConfigSpec"%>
<%@page import="wt.epm.EPMDocConfigSpec"%>
<%@page import="wt.fc.collections.WTCollection"%>
<%@page import="wt.epm.EPMDocument"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String eoid = "wt.epm.EPMDocument:1514728";

ReferenceFactory rf = new ReferenceFactory();
EPMDocument top = (EPMDocument) rf.getReference(eoid).getObject();
WTPart topPart = getPart(top);

//모든 링크 제거..

WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec
		.newWTPartStandardConfigSpec((View) topPart.getView().getObject(), null);

QueryResult result = WTPartHelper.service.getUsesWTParts(topPart, configSpec);
while (result.hasMoreElements()) {
	Object[] obj = (Object[]) result.nextElement();
	// 1.. wtpart
	WTPart p = (WTPart) obj[1];
	if(p.getPartType().toString().equalsIgnoreCase("separable")) {
		recursivePart(p);
	}
	WTPartUsageLink link = (WTPartUsageLink) obj[0];
	PersistenceServerHelper.manager.remove(link);
}

QuerySpec query = new QuerySpec(EPMMemberLink.class);
ClassAttribute ca = new ClassAttribute(EPMMemberLink.class, EPMMemberLink.COMP_NUMBER);
OrderBy by = new OrderBy(ca, false);
query.appendOrderBy(by, new int[]{0});
// QuerySpecUtils.toOrderBy(query, 0, EPMMemberLink.class, EPMMemberLink.COMP_NUMBER, false);

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

	
	String epmDocType = ee.getDocType().toString();
	//CADASSEMBLY
	
	String curNumber = ee.getNumber();

	out.println(ee.getNumber() + "<br>");

	WTPart part = comp.get(ee.getNumber());
	if (part == null) {
		part = getPart(ee);

		WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(topPart, part.getMaster());
		link.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
		link.setFindNumber(df.format(findNumber));
		PersistenceServerHelper.manager.insert(link);

		findNumber++;

		compLink.put(ee.getNumber(), link);
		comp.put(ee.getNumber(), part);

		if (preNumber == null) {
			preNumber = curNumber;
		} else {
			if (!preNumber.equals(curNumber)) {
				comp.remove(preNumber);
				compLink.remove(preNumber);
			}
			preNumber = curNumber;
		}
		if(epmDocType.equalsIgnoreCase("CADASSEMBLY")) {
			assemblyPart(ee, findNumber);
		}
	} else {
		// prePart...
		long compID = part.getPersistInfo().getObjectIdentifier().getId();
		// newPart
		WTPart newPart = getPart(ee);
		long newID = newPart.getPersistInfo().getObjectIdentifier().getId();
		if (compID == newID) {
			WTPartUsageLink link = compLink.get(ee.getNumber());
			link.getQuantity().setAmount(link.getQuantity().getAmount() + 1);
			PersistenceServerHelper.manager.update(link);
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

out.println("종료");
%>


<%!// 모든 BOM 구조 삭제 .. 재귀함수
	public static void recursivePart(WTPart part) throws Exception {

		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec((View) part.getView().getObject(), null);

		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			// 1.. wtpart
			WTPart p = (WTPart) obj[1];
			if(p.getPartType().toString().equalsIgnoreCase("separable")) {
				recursivePart(p);
			}
			WTPartUsageLink link = (WTPartUsageLink) obj[0];
			PersistenceServerHelper.manager.remove(link);
		}
	}%>
	
<%!
// 부품 재 조립
public static void assemblyPart(EPMDocument ee, int findNumber) throws Exception {
	WTPart topPart = getPart(ee);
	QuerySpec query = new QuerySpec(EPMMemberLink.class);
	
	ClassAttribute ca = new ClassAttribute(EPMMemberLink.class, EPMMemberLink.COMP_NUMBER);
	OrderBy by = new OrderBy(ca, false);
	query.appendOrderBy(by, new int[]{0});
	
// 	QuerySpecUtils.toOrderBy(query, 0, EPMMemberLink.class, EPMMemberLink.COMP_NUMBER, false);

	EPMAsStoredConfigSpec spec = EPMAsStoredConfigSpec.newEPMAsStoredConfigSpec(ee);

	QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(ee, query, false, spec);

	HashMap<String, WTPart> preMap = new HashMap<>();

	DecimalFormat df = new DecimalFormat("0000");

	HashMap<String, WTPart> comp = new HashMap<>();
	HashMap<String, WTPartUsageLink> compLink = new HashMap<>();

	String preNumber = null;
	while (qr.hasMoreElements()) {
		Object[] oo = (Object[]) qr.nextElement();
		EPMDocument epm = (EPMDocument) oo[1];

		
		String epmDocType = epm.getDocType().toString();
		//CADASSEMBLY
		
		String curNumber = epm.getNumber();

// 		out.println(ee.getNumber() + "<br>");

		WTPart part = comp.get(epm.getNumber());
		if (part == null) {
			part = getPart(epm);

			WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(topPart, part.getMaster());
			link.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			link.setFindNumber(df.format(findNumber));
			PersistenceServerHelper.manager.insert(link);

			findNumber++;

			compLink.put(epm.getNumber(), link);
			comp.put(epm.getNumber(), part);

			if (preNumber == null) {
				preNumber = curNumber;
			} else {
				if (!preNumber.equals(curNumber)) {
					comp.remove(preNumber);
					compLink.remove(preNumber);
				}
				preNumber = curNumber;
			}

			if(epmDocType.equalsIgnoreCase("CADASSEMBLY")) {
				assemblyPart(epm, findNumber);
			}
		} else {
			// prePart...
			long compID = part.getPersistInfo().getObjectIdentifier().getId();
			// newPart
			WTPart newPart = getPart(epm);
			long newID = newPart.getPersistInfo().getObjectIdentifier().getId();
			if (compID == newID) {
				WTPartUsageLink link = compLink.get(epm.getNumber());
				link.getQuantity().setAmount(link.getQuantity().getAmount() + 1);
				PersistenceServerHelper.manager.update(link);
			} else {
				findNumber += 1;
			
				WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(topPart, newPart.getMaster());
				link.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
				link.setFindNumber(df.format(findNumber));
				PersistenceServerHelper.manager.insert(link);
				comp.put(epm.getNumber(), newPart);
				compLink.put(epm.getNumber(), link);
			}
		}
	}
}
%>

<%!
// 부품 가져오기
public static  WTPart getPart(EPMDocument epm) throws Exception {
	WTPart part = null;
	if (epm == null) {
		return part;
	}

	QueryResult result = null;
	if (VersionControlHelper.isLatestIteration(epm)) {
		result = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class);
	} else {
		result = PersistenceHelper.manager.navigate(epm, "built", EPMBuildHistory.class);
	}

	while (result.hasMoreElements()) {
		part = (WTPart) result.nextElement();
	}
	return part;
}

%>