
package e3ps.bom.partlist.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.approval.service.ApprovalHelper;
import e3ps.bom.partlist.MasterDataLink;
import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.service.DocumentHelper;
import e3ps.project.Project;
import e3ps.project.enums.ProjectStateType;
import e3ps.project.enums.TaskStateType;
import e3ps.project.output.DocumentOutputLink;
import e3ps.project.output.Output;
import e3ps.project.output.ProjectOutputLink;
import e3ps.project.output.TaskOutputLink;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import wt.clients.folder.FolderTaskLogic;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardPartListService extends StandardManager implements PartListService {

	private static final long serialVersionUID = -4881738189892868701L;

	public static StandardPartListService newStandardPartListService() throws WTException {
		StandardPartListService instance = new StandardPartListService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> createPartListMasterAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> appList = (List<String>) param.get("appList");
		List<String> projectOids = (List<String>) param.get("projectOids");
		String name = (String) param.get("name");
		String engType = (String) param.get("engType");
		String description = (String) param.get("descriptionDoc");
		List<ArrayList<String>> jexcels = (List<ArrayList<String>>) param.get("jexcels");

		String taskProgress = (String) param.get("taskProgress");
		String location = "/Default/프로젝트/" + engType + "_수배표";

		ReferenceFactory rf = new ReferenceFactory();
		PartListMaster master = null;
		PartListData data = null;

		boolean isApp = appList.size() > 0;
		Transaction trs = new Transaction();
		try {
			trs.start();

			// String number = DocumentHelper.manager.getNextNumber();
			String number = DocumentHelper.manager.getNextNumber("PP-");
			master = PartListMaster.newPartListMaster();
			master.setNumber(number);
			master.setName(name);
			master.setDescription(description);
//			master.setEngType(engType);
			// 등록자 = 수정자
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);
			master.setOwnership(ownership);

			// 위치는 기계 수배표 전기 수배표로 몰빵..
			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) master, folder);

			master = (PartListMaster) PersistenceHelper.manager.save(master);

			// ContentUtils.updatePrimary(param, master);
			ContentUtils.updateSecondary(param, master);

			double totalPrice = 0D;

			int count = 0;
			for (int i = 0; i < jexcels.size(); i++) {
				data = PartListData.newPartListData();

				ArrayList<String> cells = (ArrayList<String>) jexcels.get(i);
				String lotNo = cells.get(1);

				if (StringUtils.isNull(lotNo)) {
					continue;
				}

				String unitName = cells.get(2);
				String partNo = cells.get(3);
				String partName = cells.get(4);
				String standard = cells.get(5);
				String maker = cells.get(6);
				String customer = cells.get(7);
				String quantity = cells.get(8);
				String unit = cells.get(9);
				String price = cells.get(10);
				String currency = cells.get(11);
				String won = cells.get(12);
				String exchangeRate = cells.get(14);
				String referDrawing = cells.get(15);
				String classification = cells.get(16);
				String note = cells.get(17);

				data.setLotNo(lotNo);
				data.setUnitName(unitName);
				data.setPartNo(partNo);
				data.setPartName(partName);
				data.setStandard(standard);
				data.setMaker(maker);
				data.setCustomer(customer);
				data.setQuantity(quantity);
				data.setUnit(unit);
				data.setPrice(price);
				data.setCurrency(currency);
				data.setWon(Double.parseDouble(won.replaceAll(",", "").replace(".", "")));

				// 정렬
				data.setSort(count);

				// if (StringUtils.isNull(partListDate)) {
				data.setPartListDate(DateUtils.getCurrentTimestamp().toString().substring(0, 10));
				// } else {
				// data.setPartListDate(partListDate);
				// }

				data.setExchangeRate(exchangeRate);
				data.setReferDrawing(referDrawing);
				data.setClassification(classification);
				data.setNote(note);
				data = (PartListData) PersistenceHelper.manager.save(data);

				data = (PartListData) PersistenceHelper.manager.refresh(data);

				MasterDataLink link = MasterDataLink.newMasterDataLink(master, data);
				link.setSort(count);
				PersistenceHelper.manager.save(link);

				totalPrice += data.getWon();

				count++;
			}

			for (int i = 0; projectOids != null && i < projectOids.size(); i++) {
				String projectOid = (String) projectOids.get(i);
				Project project = (Project) rf.getReference(projectOid).getObject();

				if ("기계".equals(engType)) {
					double outputMachinePrice = project.getOutputMachinePrice() != null
							? project.getOutputMachinePrice()
							: 0D;
					outputMachinePrice += totalPrice;
					project.setOutputMachinePrice(outputMachinePrice);
				} else if ("전기".equals(engType)) {
					double outputElecPrice = project.getOutputElecPrice() != null ? project.getOutputElecPrice() : 0D;
					outputElecPrice += totalPrice;
					project.setOutputElecPrice(outputElecPrice);
				}

				project = (Project) PersistenceHelper.manager.modify(project);

				Task parent = ProjectHelper.manager.getProjectTaskByName(project, location, engType);
				String tname = ProjectHelper.manager.getProjectPartListTask(project, parent);

				master.setEngType(engType + "_" + tname);

				master = (PartListMaster) PersistenceHelper.manager.modify(master);
				Task task = ProjectHelper.manager.getProjectTaskByName(project, parent, tname);

				PartListMasterProjectLink link = PartListMasterProjectLink.newPartListMasterProjectLink(master,
						project);
				PersistenceHelper.manager.save(link);

				if (task == null && parent == null) {
					map.put("reload", false);
					map.put("result", FAIL);
					map.put("msg", "해당 프로젝트에 " + engType + "_" + tname + " 태스크가 없습니다.");
					return map;
				}

				if (task != null) {
					Output output = Output.newOutput();
					output.setName(master.getName());
					output.setLocation(master.getLocation());
					output.setTask(task);
					output.setProject(project);
					output.setDocument((LifeCycleManaged) master);
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);

					int o = 0;
					QueryResult result = PersistenceHelper.manager.navigate(project, "output", ProjectOutputLink.class);
					while (result.hasMoreElements()) {
						Output oo = (Output) result.nextElement();
						if (oo.getLocation() != null && oo.getLocation().equals("/Default/프로젝트/의뢰서")) {
							continue;
						}
						o++;
					}

					if (o == 1) {

						task.setStartDate(DateUtils.getCurrentTimestamp());
						task.setState(TaskStateType.INWORK.getDisplay());
						PersistenceHelper.manager.modify(task);

						if (task.getParentTask() != null) {
							String pname = task.getParentTask().getName();
							if (pname.equals("기계_수배표") || pname.equals("전기_수배표")) {
								Task ptask = task.getParentTask();
								ptask.setStartDate(DateUtils.getCurrentTimestamp());
								ptask.setState(TaskStateType.INWORK.getDisplay());
								PersistenceHelper.manager.modify(ptask);
							}
						}

						project.setStartDate(DateUtils.getCurrentTimestamp());
						project.setKekState("설계중");
						project.setState(ProjectStateType.INWORK.getDisplay());
						project = (Project) PersistenceHelper.manager.modify(project);
					}

					if (!StringUtils.isNull(taskProgress)) {
						task.setProgress(Integer.parseInt(taskProgress));

						QueryResult qr = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
						if (qr.size() == 1) {
							task.setState(TaskStateType.INWORK.getDisplay());
							task.setStartDate(DateUtils.getCurrentTimestamp());
						}
						if (Integer.parseInt(taskProgress) == 100) {
							task.setState(TaskStateType.COMPLETE.getDisplay());
							task.setEndDate(DateUtils.getCurrentTimestamp());
						}

						task = (Task) PersistenceHelper.manager.modify(task);
					}
					ProjectHelper.service.setProgressCheck(project);
					ProjectHelper.service.commit(project);
				}

				if (task == null) {
					if (parent != null) {
						Output output = Output.newOutput();
						output.setName(master.getName());
						output.setLocation(master.getLocation());
						output.setTask(parent);
						output.setProject(project);
						output.setDocument((LifeCycleManaged) master);
						output.setOwnership(ownership);
						output = (Output) PersistenceHelper.manager.save(output);

						int o = 0;
						QueryResult result = PersistenceHelper.manager.navigate(project, "output",
								ProjectOutputLink.class);
						while (result.hasMoreElements()) {
//							Output oo = (Output) result.nextElement();

							if (parent.getName().equals("의뢰서")) {
								continue;
							}
							o++;
						}

						if (o == 1) {

							parent.setStartDate(DateUtils.getCurrentTimestamp());
							parent.setState(TaskStateType.INWORK.getDisplay());
							PersistenceHelper.manager.modify(parent);

							project.setStartDate(DateUtils.getCurrentTimestamp());
							project.setKekState("설계중");
							project.setState(ProjectStateType.INWORK.getDisplay());
							project = (Project) PersistenceHelper.manager.modify(project);
						}

						if (!StringUtils.isNull(taskProgress)) {
							parent.setProgress(Integer.parseInt(taskProgress));

							QueryResult qr = PersistenceHelper.manager.navigate(parent, "output", TaskOutputLink.class);
							if (qr.size() == 1) {
								parent.setState(TaskStateType.INWORK.getDisplay());
								parent.setStartDate(DateUtils.getCurrentTimestamp());
							}
							if (Integer.parseInt(taskProgress) == 100) {
								parent.setState(TaskStateType.COMPLETE.getDisplay());
								parent.setEndDate(DateUtils.getCurrentTimestamp());
							}

							parent = (Task) PersistenceHelper.manager.modify(parent);
						}
						ProjectHelper.service.setProgressCheck(project);
						ProjectHelper.service.commit(project);
					}
				}

				// ProjectHelper.service.commit(project);
			}

			master.setTotalPrice(totalPrice);
			PersistenceHelper.manager.modify(master);

			// 전송 테스트
			// ErpHelper.service.sendPartListToERP(master);

			if (isApp) {
				ApprovalHelper.service.submitApp(master, param);
			}

			// oid add
			if (!param.containsKey("oid")) {
				param.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
			}

			if (!param.containsKey("number")) {
				param.put("number", master.getNumber());
			}

			CommonContentHelper.service.createContents(param);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "수배표가 " + CREATE_OK);

			// if (isApp) {
			// map.put("url", "/Windchill/plm/approval/listApproval");
			// } else {
			map.put("url", "/Windchill/plm/partList/listPartList");
			// }

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "수배표 " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/partList/createPartListMaster");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyPartListAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String description = (String) param.get("description");
		String name = (String) param.get("name");
		String oid = (String) param.get("oid");
		String engType = (String) param.get("engType");
		List<ArrayList<String>> jexcels = (List<ArrayList<String>>) param.get("jexcels");
		List<String> projectOids = (List<String>) param.get("projectOids");

		System.out.println("projectOids=" + param);

		List<String> appList = (List<String>) param.get("appList");
		String taskProgress = (String) param.get("taskProgress");
		String location = "/Default/프로젝트/" + engType + "_수배표";
		ArrayList<PartListData> list = new ArrayList<PartListData>();
		PartListMaster master = null;
		PartListData data = null;
		ReferenceFactory rf = new ReferenceFactory();
		boolean isApp = appList.size() > 0;
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			master = (PartListMaster) rf.getReference(oid).getObject();

			master.setDescription(description);
			master.setName(name);
//			master.setEngType(engType);
			master.setOwnership(ownership);

			// ContentUtils.updatePrimary(param, master);
			ContentUtils.updateSecondary(param, master);

			ApprovalHelper.service.deleteAllLine(master);

			ArrayList<PartListMasterProjectLink> lists = PartListHelper.manager.getPartListMasterProjectLink(master);
			System.out.println("klists=" + lists.size());

			// 관련작번 삭제
			master = (PartListMaster) PersistenceHelper.manager.modify(master);

			QueryResult linkQr = PersistenceHelper.manager.navigate(master, "output", DocumentOutputLink.class, false);
			while (linkQr.hasMoreElements()) {
				DocumentOutputLink ll = (DocumentOutputLink) linkQr.nextElement();
				Output oo = (Output) ll.getOutput();

				QueryResult oQr = PersistenceHelper.manager.navigate(oo, "project", ProjectOutputLink.class, false);
				while (oQr.hasMoreElements()) {
					ProjectOutputLink l2 = (ProjectOutputLink) oQr.nextElement();
					PersistenceHelper.manager.delete(oo);
					PersistenceHelper.manager.delete(l2);
				}
			}

			QueryResult linkResult = PersistenceHelper.manager.navigate(master, "project",
					PartListMasterProjectLink.class, false);
			while (linkResult.hasMoreElements()) {
				PartListMasterProjectLink l = (PartListMasterProjectLink) linkResult.nextElement();
				PersistenceHelper.manager.delete(l);
			}

			// 수배표 삭제
			list = PartListHelper.manager.getPartListData(master);

			for (PartListData pData : list) {
				PersistenceHelper.manager.delete(pData);
			}

			// 수배표 등록
			double totalPrice = 0D;
			int count = 0;
			for (int i = 0; i < jexcels.size(); i++) {
				data = PartListData.newPartListData();

				ArrayList<String> cells = (ArrayList<String>) jexcels.get(i);
				String lotNo = cells.get(1);
				if (StringUtils.isNull(lotNo)) {
					continue;
				}

				String unitName = cells.get(2);
				String partNo = cells.get(3);
				String partName = cells.get(4);
				String standard = cells.get(5);
				String maker = cells.get(6);
				String customer = cells.get(7);
				String quantity = cells.get(8);
				String unit = cells.get(9);
				String price = cells.get(10);
				String currency = cells.get(11);
				String won = cells.get(12);
				String exchangeRate = cells.get(14);
				String referDrawing = cells.get(15);
				String classification = cells.get(16);
				String note = cells.get(17);

				data.setLotNo(lotNo);
				data.setUnitName(unitName);
				data.setPartNo(partNo);
				data.setPartName(partName);
				data.setStandard(standard);
				data.setMaker(maker);
				data.setCustomer(customer);
				data.setQuantity(quantity);
				data.setUnit(unit);
				data.setPrice(price);
				data.setCurrency(currency);
				data.setWon(Double.parseDouble(won.replaceAll(",", "").replace(".", "")));
				// data.setPartListDate(partListDate);

				data.setPartListDate(DateUtils.getCurrentTimestamp().toString().substring(0, 10));

				data.setExchangeRate(exchangeRate);
				data.setReferDrawing(referDrawing);
				data.setClassification(classification);
				data.setNote(note);
				data.setSort(count);
				data = (PartListData) PersistenceHelper.manager.save(data);

				data = (PartListData) PersistenceHelper.manager.refresh(data);

				MasterDataLink link = MasterDataLink.newMasterDataLink(master, data);
				link.setSort(count);
				PersistenceHelper.manager.save(link);

				totalPrice += data.getWon();

				count++;
			}

			master.setTotalPrice(totalPrice);
			PersistenceHelper.manager.modify(master);

			master = (PartListMaster) PersistenceHelper.manager.refresh(master);

			for (int i = 0; projectOids != null && i < projectOids.size(); i++) {
				String projectOid = (String) projectOids.get(i);
				Project project = (Project) rf.getReference(projectOid).getObject();

				if ("기계".equals(engType)) {
					ArrayList<PartListMaster> datas = new ArrayList<PartListMaster>();
					datas = PartListHelper.manager.findPartListByProject(project, engType, "");
					double totalPrices = 0D;
					for (PartListMaster masters : datas) {
						totalPrices += masters.getTotalPrice();
					}
					project.setOutputMachinePrice(totalPrices);
				} else if ("전기".equals(engType)) {
					ArrayList<PartListMaster> datas = new ArrayList<PartListMaster>();
					datas = PartListHelper.manager.findPartListByProject(project, engType, "");
					double totalPrices = 0D;
					for (PartListMaster masters : datas) {
						totalPrices += masters.getTotalPrice();
					}
					project.setOutputElecPrice(totalPrices);
				}

				project = (Project) PersistenceHelper.manager.modify(project);

//				QueryResult qlink = PersistenceHelper.manager.navigate(project, "partListMaster",
//						PartListMasterProjectLink.class, false);
//				while (qlink.hasMoreElements()) {
//					PartListMasterProjectLink l2 = (PartListMasterProjectLink) qlink.nextElement();
//					PersistenceHelper.manager.delete(l2);
//				}

				PartListMasterProjectLink link = PartListMasterProjectLink.newPartListMasterProjectLink(master,
						project);
				PersistenceHelper.manager.save(link);

				Task parent = ProjectHelper.manager.getProjectTaskByName(project, location, engType);

				String tname = ProjectHelper.manager.getProjectPartListTask(project, parent);

				master.setEngType(engType + "_" + tname);
				Task task = ProjectHelper.manager.getProjectTaskByName(project, parent, tname);

				// PartListMasterProjectLink link =
				// PartListMasterProjectLink.newPartListMasterProjectLink(master,
				// project);
				// PersistenceHelper.manager.save(link);

				if (task == null && parent == null) {
					map.put("reload", false);
					map.put("result", FAIL);
					map.put("msg", "해당 프로젝트에 " + engType + "_수배표 태스크가 없습니다.");
					return map;
				}

				if (task != null) {
					Output output = Output.newOutput();
					output.setName(master.getName());
					output.setLocation(master.getLocation());
					output.setTask(task);
					output.setProject(project);
					output.setDocument((LifeCycleManaged) master);
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);

					int o = 0;
					QueryResult result = PersistenceHelper.manager.navigate(project, "output", ProjectOutputLink.class);
					while (result.hasMoreElements()) {
						Output oo = (Output) result.nextElement();
						if (oo.getLocation() != null && oo.getLocation().equals("/Default/프로젝트/의뢰서")) {
							continue;
						}
						o++;
					}

					if (o == 1) {

						task.setStartDate(DateUtils.getCurrentTimestamp());
						task.setState(TaskStateType.INWORK.getDisplay());
						PersistenceHelper.manager.modify(task);

						project.setStartDate(DateUtils.getCurrentTimestamp());
						project.setKekState("설계중");
						project.setState(ProjectStateType.INWORK.getDisplay());
						project = (Project) PersistenceHelper.manager.modify(project);
					}

					if (!StringUtils.isNull(taskProgress)) {
						task.setProgress(Integer.parseInt(taskProgress));

						QueryResult qr = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
						if (qr.size() == 1) {
							task.setState(TaskStateType.INWORK.getDisplay());
							task.setStartDate(DateUtils.getCurrentTimestamp());
						}
						if (Integer.parseInt(taskProgress) == 100) {
							task.setState(TaskStateType.COMPLETE.getDisplay());
							task.setEndDate(DateUtils.getCurrentTimestamp());
						}

						task = (Task) PersistenceHelper.manager.modify(task);
					}
					ProjectHelper.service.setProgressCheck(project);
					ProjectHelper.service.commit(project);
				}

				if (task == null) {
					if (parent != null) {
						Output output = Output.newOutput();
						output.setName(master.getName());
						output.setLocation(master.getLocation());
						output.setTask(parent);
						output.setProject(project);
						output.setDocument((LifeCycleManaged) master);
						output.setOwnership(ownership);
						output = (Output) PersistenceHelper.manager.save(output);

						int o = 0;
						QueryResult result = PersistenceHelper.manager.navigate(project, "output",
								ProjectOutputLink.class);
						while (result.hasMoreElements()) {
//							Output oo = (Output) result.nextElement();

							if (parent.getName().equals("의뢰서")) {
								continue;
							}
							o++;
						}

						if (o == 1) {

							parent.setStartDate(DateUtils.getCurrentTimestamp());
							parent.setState(TaskStateType.INWORK.getDisplay());
							PersistenceHelper.manager.modify(parent);

							project.setStartDate(DateUtils.getCurrentTimestamp());
							project.setKekState("설계중");
							project.setState(ProjectStateType.INWORK.getDisplay());
							project = (Project) PersistenceHelper.manager.modify(project);
						}

						if (!StringUtils.isNull(taskProgress)) {
							parent.setProgress(Integer.parseInt(taskProgress));

							QueryResult qr = PersistenceHelper.manager.navigate(parent, "output", TaskOutputLink.class);
							if (qr.size() == 1) {
								parent.setState(TaskStateType.INWORK.getDisplay());
								parent.setStartDate(DateUtils.getCurrentTimestamp());
							}
							if (Integer.parseInt(taskProgress) == 100) {
								parent.setState(TaskStateType.COMPLETE.getDisplay());
								parent.setEndDate(DateUtils.getCurrentTimestamp());
							}

							parent = (Task) PersistenceHelper.manager.modify(parent);
						}
						ProjectHelper.service.setProgressCheck(project);
						ProjectHelper.service.commit(project);
					}
				}
			}
			if (isApp) {
				ApprovalHelper.service.submitApp(master, param);
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "수배표가 " + MODIFY_OK);
			map.put("url", "/Windchill/plm/partList/listPartList");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "수배표 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/partList/modifyPartListMaster");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deletePartListMasterAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		PartListMaster partList = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				partList = (PartListMaster) rf.getReference(oid).getObject();

				ApprovalHelper.service.deleteAllLine(partList);

				String state = partList.getLifeCycleState().toString();
				if (state.equalsIgnoreCase("APPROVED")) {
					map.put("result", FAIL);
					map.put("msg", "수배표 삭제에 실패 했습니다.\n승인된 수배표가 있습니다.\n수배표번호 : " + partList.getNumber() + ".");
					map.put("url", "/Windchill/plm/partList/listPartList");
					return map;
				}

				ArrayList<PartListMasterProjectLink> projectList = PartListHelper.manager
						.getPartListMasterProjectLink(partList);
				for (PartListMasterProjectLink link : projectList) {
					PersistenceHelper.manager.delete(link);
				}

				ApprovalHelper.service.deleteAllLine(partList);

				PersistenceHelper.manager.delete(partList);
			}

			map.put("result", SUCCESS);
			map.put("msg", "수배표가 " + DELETE_OK);
			map.put("url", "/Windchill/plm/partList/listPartList");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "수배표를  " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/partList/listPartList");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		String engType = (String) params.get("engType");
		String description = (String) params.get("description");
		String progress = (String) params.get("taskProgress");
		String location = "/Default/프로젝트/" + engType + "_수배표";
		String number = DocumentHelper.manager.getNextNumber("PP-");
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows"); // 프로젝트
		ArrayList<Map<String, Object>> _addRows = (ArrayList<Map<String, Object>>) params.get("_addRows"); // 파트리스트
		Transaction trs = new Transaction();
		try {
			trs.start();

			PartListMaster master = PartListMaster.newPartListMaster();
			master.setNumber(number);
			master.setName(name);
			master.setDescription(description);
			master.setEngType(engType);
			master.setOwnership(CommonUtils.sessionOwner());
			// 위치는 기계 수배표 전기 수배표로 몰빵..
			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) master, folder);
			master = (PartListMaster) PersistenceHelper.manager.save(master);

			// ContentUtils.updatePrimary(param, master);
//				ContentUtils.updateSecondary(param, master);

			double totalPrice = 0D;

			int sort = 0;
			for (int i = 0; i < _addRows.size(); i++) {
				Map<String, Object> _addRow = (Map<String, Object>) _addRows.get(i);
				
				String lotNo= (String)_addRow.get("lotNo");
				String unitName= (String)_addRow.get("unitName");
				String partNo= (String)_addRow.get("partNo");
				String partName= (String)_addRow.get("partName");
				String standard= (String)_addRow.get("standard");
				String maker= (String)_addRow.get("maker");
				String customer= (String)_addRow.get("customer");
				int quantity= (int)_addRow.get("quantity");
				String unit= (String)_addRow.get("unit");
				double price= (double)_addRow.get("price");
				String currency= (String)_addRow.get("currency");
				double won= (double)_addRow.get("won");
				double exchangeRate= (double)_addRow.get("exchangeRate");
				String referDrawing= (String)_addRow.get("referDrawing");
				String classification= (String)_addRow.get("classification");
				String note= (String)_addRow.get("note");
				
				
				PartListData data = PartListData.newPartListData();
				data.setLotNo(lotNo);
				data.setUnitName(unitName);
				data.setPartNo(partNo);
				data.setPartName(partName);
				data.setStandard(standard);
				data.setMaker(maker);
				data.setCustomer(customer);
				data.setQuantity(quantity);
				data.setUnit(unit);
				data.setPrice(price);
				data.setCurrency(currency);
				data.setWon(won);
				data.setSort(sort);
				data.setPartListDate(DateUtils.today());
				data.setExchangeRate(exchangeRate);
				data.setReferDrawing(referDrawing);
				data.setClassification(classification);
				data.setNote(note);
				data = (PartListData) PersistenceHelper.manager.save(data);

				
				MasterDataLink link = MasterDataLink.new
				
				link.setSort(sort);
				PersistenceHelper.manager.save(link);

				totalPrice += data.getWon();

				sort++;
				
			}

			for (int i = 0; projectOids != null && i < projectOids.size(); i++) {
				String projectOid = (String) projectOids.get(i);
				Project project = (Project) rf.getReference(projectOid).getObject();

				if ("기계".equals(engType)) {
					double outputMachinePrice = project.getOutputMachinePrice() != null
							? project.getOutputMachinePrice()
							: 0D;
					outputMachinePrice += totalPrice;
					project.setOutputMachinePrice(outputMachinePrice);
				} else if ("전기".equals(engType)) {
					double outputElecPrice = project.getOutputElecPrice() != null ? project.getOutputElecPrice() : 0D;
					outputElecPrice += totalPrice;
					project.setOutputElecPrice(outputElecPrice);
				}

				project = (Project) PersistenceHelper.manager.modify(project);

				Task parent = ProjectHelper.manager.getProjectTaskByName(project, location, engType);
				String tname = ProjectHelper.manager.getProjectPartListTask(project, parent);

				master.setEngType(engType + "_" + tname);

				master = (PartListMaster) PersistenceHelper.manager.modify(master);
				Task task = ProjectHelper.manager.getProjectTaskByName(project, parent, tname);

				PartListMasterProjectLink link = PartListMasterProjectLink.newPartListMasterProjectLink(master,
						project);
				PersistenceHelper.manager.save(link);

				if (task == null && parent == null) {
					map.put("reload", false);
					map.put("result", FAIL);
					map.put("msg", "해당 프로젝트에 " + engType + "_" + tname + " 태스크가 없습니다.");
					return map;
				}

				if (task != null) {
					Output output = Output.newOutput();
					output.setName(master.getName());
					output.setLocation(master.getLocation());
					output.setTask(task);
					output.setProject(project);
					output.setDocument((LifeCycleManaged) master);
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);

					int o = 0;
					QueryResult result = PersistenceHelper.manager.navigate(project, "output", ProjectOutputLink.class);
					while (result.hasMoreElements()) {
						Output oo = (Output) result.nextElement();
						if (oo.getLocation() != null && oo.getLocation().equals("/Default/프로젝트/의뢰서")) {
							continue;
						}
						o++;
					}

					if (o == 1) {

						task.setStartDate(DateUtils.getCurrentTimestamp());
						task.setState(TaskStateType.INWORK.getDisplay());
						PersistenceHelper.manager.modify(task);

						if (task.getParentTask() != null) {
							String pname = task.getParentTask().getName();
							if (pname.equals("기계_수배표") || pname.equals("전기_수배표")) {
								Task ptask = task.getParentTask();
								ptask.setStartDate(DateUtils.getCurrentTimestamp());
								ptask.setState(TaskStateType.INWORK.getDisplay());
								PersistenceHelper.manager.modify(ptask);
							}
						}

						project.setStartDate(DateUtils.getCurrentTimestamp());
						project.setKekState("설계중");
						project.setState(ProjectStateType.INWORK.getDisplay());
						project = (Project) PersistenceHelper.manager.modify(project);
					}

					if (!StringUtils.isNull(taskProgress)) {
						task.setProgress(Integer.parseInt(taskProgress));

						QueryResult qr = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
						if (qr.size() == 1) {
							task.setState(TaskStateType.INWORK.getDisplay());
							task.setStartDate(DateUtils.getCurrentTimestamp());
						}
						if (Integer.parseInt(taskProgress) == 100) {
							task.setState(TaskStateType.COMPLETE.getDisplay());
							task.setEndDate(DateUtils.getCurrentTimestamp());
						}

						task = (Task) PersistenceHelper.manager.modify(task);
					}
					ProjectHelper.service.setProgressCheck(project);
					ProjectHelper.service.commit(project);
				}

				if (task == null) {
					if (parent != null) {
						Output output = Output.newOutput();
						output.setName(master.getName());
						output.setLocation(master.getLocation());
						output.setTask(parent);
						output.setProject(project);
						output.setDocument((LifeCycleManaged) master);
						output.setOwnership(ownership);
						output = (Output) PersistenceHelper.manager.save(output);

						int o = 0;
						QueryResult result = PersistenceHelper.manager.navigate(project, "output",
								ProjectOutputLink.class);
						while (result.hasMoreElements()) {
//								Output oo = (Output) result.nextElement();

							if (parent.getName().equals("의뢰서")) {
								continue;
							}
							o++;
						}

						if (o == 1) {

							parent.setStartDate(DateUtils.getCurrentTimestamp());
							parent.setState(TaskStateType.INWORK.getDisplay());
							PersistenceHelper.manager.modify(parent);

							project.setStartDate(DateUtils.getCurrentTimestamp());
							project.setKekState("설계중");
							project.setState(ProjectStateType.INWORK.getDisplay());
							project = (Project) PersistenceHelper.manager.modify(project);
						}

						if (!StringUtils.isNull(taskProgress)) {
							parent.setProgress(Integer.parseInt(taskProgress));

							QueryResult qr = PersistenceHelper.manager.navigate(parent, "output", TaskOutputLink.class);
							if (qr.size() == 1) {
								parent.setState(TaskStateType.INWORK.getDisplay());
								parent.setStartDate(DateUtils.getCurrentTimestamp());
							}
							if (Integer.parseInt(taskProgress) == 100) {
								parent.setState(TaskStateType.COMPLETE.getDisplay());
								parent.setEndDate(DateUtils.getCurrentTimestamp());
							}

							parent = (Task) PersistenceHelper.manager.modify(parent);
						}
						ProjectHelper.service.setProgressCheck(project);
						ProjectHelper.service.commit(project);
					}
				}

				// ProjectHelper.service.commit(project);
			}

			master.setTotalPrice(totalPrice);
			PersistenceHelper.manager.modify(master);

			// 전송 테스트
			// ErpHelper.service.sendPartListToERP(master);

			if (isApp) {
				ApprovalHelper.service.submitApp(master, param);
			}

			// oid add
			if (!param.containsKey("oid")) {
				param.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
			}

			if (!param.containsKey("number")) {
				param.put("number", master.getNumber());
			}

			CommonContentHelper.service.createContents(param);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "수배표가 " + CREATE_OK);

			// if (isApp) {
			// map.put("url", "/Windchill/plm/approval/listApproval");
			// } else {
			map.put("url", "/Windchill/plm/partList/listPartList");
			// }

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void modify(Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub

	}
}
