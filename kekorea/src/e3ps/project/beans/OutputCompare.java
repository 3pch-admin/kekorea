package e3ps.project.beans;

import java.util.Comparator;

import e3ps.project.DocumentOutputLink;

public class OutputCompare implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {

		int comp = 0;

		DocumentOutputLink l1 = (DocumentOutputLink) o1;
		DocumentOutputLink l2 = (DocumentOutputLink) o2;

		String f1 = l1.getCreateTimestamp().toString().substring(0, 10);
		String f2 = l2.getCreateTimestamp().toString().substring(0, 10);
		comp = f2.compareTo(f1);
		return comp;
	}
}
