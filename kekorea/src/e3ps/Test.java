package e3ps;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.ptc.windchill.collector.api.cad.CadCollectedResult;
import com.ptc.windchill.collector.api.cad.CadCollector;
import com.ptc.windchill.collector.api.cad.CadCollector.GatherDependents;
import com.ptc.windchill.collector.api.cad.CadCollector.GatherFamilyGenerics;
import com.ptc.windchill.collector.api.cad.CadCollector.GatherFamilyMembers;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import wt.epm.EPMDocConfigSpec;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMAsStoredConfigSpec;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.filter.NavigationCriteria;
import wt.query.QuerySpec;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

public class Test {

	public static void main(String[] args) throws Exception {

		String eoid = "wt.epm.EPMDocument:1509752";

		EPMDocument top = (EPMDocument) CommonUtils.getObject(eoid);
ConfigSpec
		QuerySpec query = new QuerySpec(EPMMemberLink.class);
		QuerySpecUtils.toOrderBy(query, 0, EPMMemberLink.class, EPMMemberLink.COMP_NUMBER, false);

		EPMAsStoredConfigSpec spec = EPMAsStoredConfigSpec.newEPMAsStoredConfigSpec(top);

		QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(top, query, false, spec);
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			Object[] oo = (Object[]) qr.nextElement();
			EPMDocument ee = (EPMDocument) oo[1];
		}


		System.exit(0);

	}
}