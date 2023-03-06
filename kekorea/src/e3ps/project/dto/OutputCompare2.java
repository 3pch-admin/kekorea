package e3ps.project.dto;

import java.util.Comparator;

import e3ps.common.util.CommonUtils;
import e3ps.doc.E3PSDocumentMaster;
import e3ps.project.DocumentMasterOutputLink;
import wt.enterprise.RevisionControlled;

public class OutputCompare2 implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {

		int comp = 0;

		DocumentMasterOutputLink l1 = (DocumentMasterOutputLink) o1;
		DocumentMasterOutputLink l2 = (DocumentMasterOutputLink) o2;

		E3PSDocumentMaster mm1 = l1.getMaster();
		E3PSDocumentMaster mm2 = l2.getMaster();

		RevisionControlled rc1 = null;
		RevisionControlled rc2 = null;

		try {
			rc1 = CommonUtils.getLatestObject(mm1);
			rc2 = CommonUtils.getLatestObject(mm2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String f1 = rc1.getCreateTimestamp().toString().substring(0, 10);
		String f2 = rc2.getCreateTimestamp().toString().substring(0, 10);
		comp = f2.compareTo(f1);
		return comp;
	}
}
