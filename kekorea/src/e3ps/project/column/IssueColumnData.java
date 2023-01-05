package e3ps.project.column;

import e3ps.common.util.ContentUtils;
import e3ps.project.Issue;
import e3ps.project.Project;

public class IssueColumnData {

	public String oid;
	public String name;
	public String kek_number;
	public String ke_number;
	public String mak;
	public String description;
	public String pDescription;
	public String creator;
	public String createDate;

	public String iconPath;
	
	public IssueColumnData(Issue issue) throws Exception {
		this(issue, null);
	}

	public IssueColumnData(Issue issue, Project project) throws Exception {
		this.oid = issue.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = issue.getName();
		this.description = issue.getDescription();
		this.kek_number = project.getKekNumber();
		this.ke_number = project.getKeNumber();
		this.mak = project.getMak();
		this.pDescription = project.getDescription();
		this.creator = issue.getOwnership().getOwner().getFullName();
		this.createDate = issue.getCreateTimestamp().toString().substring(0, 16);

		this.iconPath = ContentUtils.getOpenIcon(this.oid);
	}
}
