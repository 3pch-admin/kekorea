package e3ps.event;

import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteInterface;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.wip.Workable;

@RemoteInterface
public interface EventService {

	public abstract void replaceLifeCycle(Workable workable) throws WTException;

	/**
	 * 결재 연결
	 * 
	 * @param workable
	 * @throws WTException
	 */
	public abstract void setApprovalLine(Workable workable) throws WTException;

	public abstract void changeToName(Workable workable) throws WTException;

	/**
	 * 최신 버전
	 * 
	 * @param target
	 * @throws WTException
	 */
	public abstract void createVersion(Workable target) throws WTException;

	/**
	 * 도면 파일 이미지 생성
	 * 
	 * @param target
	 * @throws WTException
	 */
	public abstract void publishToImage(Workable target) throws WTException;

	/**
	 * SET FindNumber
	 * 
	 * @param link
	 * @throws WTException
	 */
	public abstract void setFindNumber(WTPartUsageLink link) throws WTException;

	/**
	 * 체크인 방지
	 * 
	 * @param target
	 * @throws WTException
	 */
	public abstract void preCheckInValidate(Workable target) throws WTException;

	/**
	 * 체크..
	 * 
	 * @param target
	 * @throws WTException
	 */
	public abstract void check(Workable target) throws WTException;

	/**
	 * 상태값 변경시 체크
	 * 
	 * @param lcm
	 * @throws WTException
	 */
	public abstract void checkTask(LifeCycleManaged lcm) throws WTException;
}
