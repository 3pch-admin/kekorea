package e3ps.approval.column;

import e3ps.approval.ErrorReport;

public class ErrorReportColumnData {

	public String oid;
	public String name;
	public String description;
	public String creator;
	public String completeTime;
	public String complete;

	public ErrorReportColumnData(ErrorReport errorReport) throws Exception {
		this.oid = errorReport.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = errorReport.getName();
		this.description = errorReport.getDescription();
		this.creator = errorReport.getOwnership().getOwner().getFullName();
		this.completeTime = errorReport.getCompleteTime() != null
				? errorReport.getCompleteTime().toString().substring(0, 16)
				: "";
		this.complete = errorReport.getComplete() == true ? "처리완료" : "미처리";
	}
}