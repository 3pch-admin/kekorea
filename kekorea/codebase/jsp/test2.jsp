<%@page import="java.util.HashMap"%>
<%@page import="e3ps.part.beans.PartNumberCompare"%>
<%@page import="e3ps.part.column.BomColumnData"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="wt.vc.views.ViewHelper"%>
<%@page import="wt.vc.views.View"%>
<%@page import="wt.vc.baseline.Baseline"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="e3ps.part.beans.PartTreeData"%>
<%@page import="e3ps.part.beans.BomBroker"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.migrator.MigratorHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<WTPart> compareList = new ArrayList<WTPart>();

	String oid = "wt.part.WTPart:511572680";

	ReferenceFactory rf = new ReferenceFactory();
	WTPart part = (WTPart) rf.getReference(oid).getObject();
	QueryResult result = VersionControlHelper.service.allIterationsOf(part.getMaster());

	compareList.add(0, part);

	while (result.hasMoreElements()) {
		WTPart p = (WTPart) result.nextElement();
		String state = p.getLifeCycleState().toString();

		if (state.equals("RELEASED")) {
			compareList.add(1, p);
			break;
		}
	}
	for (WTPart compare : compareList) {
		// // 		out.println("ㅂ교 버전 = " + compare.getVersionIdentifier().getValue() + "."
		// 				+ compare.getIterationIdentifier().getValue() + "<br>");
		// 		out.println("상ㅌ- =" + compare.getLifeCycleState().getDisplay() + "<br>");
	}

	BomBroker broker = new BomBroker();

	WTPart latestPart = (WTPart) compareList.get(0);
	WTPart prePart = (WTPart) compareList.get(1);

	PartTreeData latestData = broker.getTree(latestPart, true, null, null);

	for (int i = 0; i < latestData.children.size(); i++) {
		PartTreeData ch = (PartTreeData) latestData.children.get(i);
	}

	PartTreeData preData = broker.getTree(prePart, true, null, prePart.getLifeCycleState().toString());

	ArrayList<PartTreeData[]> list = new ArrayList<PartTreeData[]>();
	broker.compareBom(latestData, preData, list);

	ArrayList<ArrayList<PartTreeData>> ll = PartHelper.manager.compareBomList(oid);
	for (int i = 0; i < ll.size(); i++) {
		ArrayList<PartTreeData> data = (ArrayList<PartTreeData>) ll.get(i);

		for (int j = 0; j < data.size(); j++) {
			PartTreeData pd = (PartTreeData) data.get(j);
			if (pd.parent != null) {
				out.println("상위 품번 :  " + pd.parent.number + ", 버전 : " + pd.parent.version + "."
						+ pd.parent.iteration + ", 상태 : " + pd.parent.state);
			}
			out.println("<font color=\"" + pd.bgcolor + "\">" + pd.flag + "</font> : " + pd.number + "<br>");
		}
	}
%>



<table>
	<tr>
		<td valign="top"><table>
				<tr>
					<th>레벨</th>
					<th>번호</th>
					<th>상태값</th>
					<th>버전</th>
					<th>수량</th>
				</tr>
				<%
					for (int i = 0; i < list.size(); i++) {
						PartTreeData[] o = (PartTreeData[]) list.get(i);
						PartTreeData data = o[0];
						PartTreeData data2 = o[1];

						String bgcolor = "black"; //white

						if (data == null) {
							bgcolor = "red";//"#D3D3D3";		//삭제
						} else {
							if (data2 == null) {
								bgcolor = "blue";//"#8FBC8F";		//green
							} else {
								if (!data.compare(data2)) {
									bgcolor = "#FFD700"; //gold
								}
							}
						}

						if (data != null) {
				%>
				<tr>
					<th><%=data != null ? data.level : ""%></th>
					<th><font color="<%=bgcolor%>"><%=data != null ? data.number : ""%></font></th>
					<th><%=data != null ? data.version : ""%></th>
					<th><%=data != null ? data.part.getLifeCycleState().getDisplay() : ""%></th>
					<th><%=data != null ? data.quantity : ""%></th>
				</tr>
				<%
					} else if (data2 != null) {
				%>
				<tr>
					<th><%=data2 != null ? data2.level : ""%></th>
					<th><font color="<%=bgcolor%>"><%=data2 != null ? data2.number : ""%></font></th>
					<th><%=data2 != null ? data2.version : ""%></th>
					<th><%=data2 != null ? data2.part.getLifeCycleState().getDisplay() : ""%></th>
					<th><%=data2 != null ? data2.quantity : ""%></th>
				</tr>
				<%
					}
					}
				%>
			</table></td>
		<td valign="top"><table>
				<tr>
					<th>레벨</th>
					<th>번호</th>
					<th>상태값</th>
					<th>버전</th>
					<th>수량</th>
				</tr>
				<%
					for (int i = 0; i < list.size(); i++) {
						PartTreeData[] o = (PartTreeData[]) list.get(i);
						PartTreeData data = o[0];
						PartTreeData data2 = o[1];

						String bgcolor = "black"; //white

						if (data == null) {
							bgcolor = "red";//"#D3D3D3";		//삭제
						} else {
							if (data2 == null) {
								bgcolor = "blue";//"#8FBC8F";		//green
							} else {
								if (!data.compare(data2)) {
									bgcolor = "#FFD700"; //gold
								}
							}
						}
				%>
				<tr>
					<th><%=data != null ? data.level : ""%></th>
					<th><font color="<%=bgcolor%>"><%=data != null ? data.number : ""%></font></th>
					<th><%=data != null ? data.version : ""%></th>
					<th><%=data != null ? data.part.getLifeCycleState().getDisplay() : ""%></th>
					<th><%=data != null ? data.quantity : ""%></th>
				</tr>
				<%
					}
				%>
			</table></td>
	</tr>
</table>
