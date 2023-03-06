package e3ps.korea.cip.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.korea.cip.Cip;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CipDTO {

	private String oid;
	private String item;
	private String improvements;
	private String improvement;
	private String apply;

	private String mak_code;
	private String mak_name;
	private String mak_oid;
	private String detail_code;
	private String detail_name;
	private String detail_oid;
	private String install_code;
	private String install_name;
	private String install_oid;
	private String customer_code;
	private String customer_name;
	private String customer_oid;
	private String note;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String preView;
	private String icons;

	public CipDTO() {

	}

	public CipDTO(Cip cip) throws Exception {
		setOid(cip.getPersistInfo().getObjectIdentifier().getStringValue());
		setItem(cip.getItem());
		setImprovements(cip.getImprovements());
		setImprovement(cip.getImprovement());
		setApply(cip.getApply());
		setMak_code(cip.getMak().getCode());
		setMak_name(cip.getMak().getName());
		setMak_oid(cip.getMak().getPersistInfo().getObjectIdentifier().getStringValue());
		setDetail_code(cip.getDetail().getCode());
		setDetail_name(cip.getDetail().getName());
		setDetail_oid(cip.getPersistInfo().getObjectIdentifier().getStringValue());
		setNote(cip.getNote());
		setCreator(cip.getOwnership().getOwner().getFullName());
		setCreatedDate(cip.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(cip.getCreateTimestamp()));
		setInstall_code(cip.getInstall().getCode());
		setInstall_name(cip.getInstall().getName());
		setInstall_oid(cip.getInstall().getPersistInfo().getObjectIdentifier().getStringValue());
		setCustomer_code(cip.getCustomer().getCode());
		setCustomer_name(cip.getCustomer().getName());
		setCustomer_oid(cip.getCustomer().getPersistInfo().getObjectIdentifier().getStringValue());
		setPreView(ContentUtils.getPreViewBase64(cip));
		setIcons(AUIGridUtils.secondaryTemplate(cip));
	}
}
