package e3ps.workspace.notification.dto;

import java.sql.Timestamp;

import e3ps.common.util.CommonUtils;
import e3ps.workspace.notification.Notification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {

	private String oid;
	private String name;
	private String description;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;

	public NotificationDTO() {

	}

	public NotificationDTO(Notification notification) throws Exception {
		setOid(notification.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(notification.getName());
		setDescription(notification.getDescription());
		setCreator(notification.getOwnership().getOwner().getFullName());
		setCreatedDate(notification.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(notification.getCreateTimestamp()));
	}
}
