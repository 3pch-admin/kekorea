package e3ps.korea.cip.beans;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.ContentUtils;
import e3ps.korea.cip.Cip;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CipColumnData {

	private String oid;
	private String item;
	private String improvements;
	private String improvement;
	private String apply;
	private String mak;
	private String detail;
	private String install;
	private String customer;
	private String note;
	private String creator;
	private Timestamp createdDate;
	private String preView;
	private String icons;

	public CipColumnData() {

	}

	public CipColumnData(Cip cip) throws Exception {
		setOid(cip.getPersistInfo().getObjectIdentifier().getStringValue());
		setItem(cip.getItem());
		setImprovements(cip.getImprovements());
		setImprovement(cip.getImprovement());
		setApply(cip.getApply());
		setMak(cip.getMak().getName());
		setDetail(cip.getDetail().getName());
		setNote(cip.getNote());
		setCreator(cip.getOwnership().getOwner().getFullName());
		setCreatedDate(cip.getCreateTimestamp());
		setInstall(cip.getInstall().getName());
		setCustomer(cip.getCustomer().getName());
		setPreView(ContentUtils.getPreViewBase64(cip));
		setIcons(AUIGridUtils.secondaryTemplate(cip));
	}
}
