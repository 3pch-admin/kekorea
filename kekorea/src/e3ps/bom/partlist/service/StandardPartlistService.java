
package e3ps.bom.partlist.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.bom.partlist.MasterDataLink;
import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.service.DocumentHelper;
import e3ps.project.Project;
import e3ps.project.output.DocumentOutputLink;
import e3ps.project.output.Output;
import e3ps.project.output.ProjectOutputLink;
import e3ps.project.output.TaskOutputLink;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.workspace.service.WorkspaceHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
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

public class StandardPartlistService extends StandardManager implements PartlistService {

	private static final long serialVersionUID = -4881738189892868701L;

	public static StandardPartlistService newStandardPartlistService() throws WTException {
		StandardPartlistService instance = new StandardPartlistService();
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
		String location = "/Default/????????????/" + engType + "_?????????";

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
			// ????????? = ?????????
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);
			master.setOwnership(ownership);

			// ????????? ?????? ????????? ?????? ???????????? ??????..
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

				// ??????
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

				if ("??????".equals(engType)) {
					double outputMachinePrice = project.getOutputMachinePrice() != null
							? project.getOutputMachinePrice()
							: 0D;
					outputMachinePrice += totalPrice;
					project.setOutputMachinePrice(outputMachinePrice);
				} else if ("??????".equals(engType)) {
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
					map.put("msg", "?????? ??????????????? " + engType + "_" + tname + " ???????????? ????????????.");
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
						if (oo.getLocation() != null && oo.getLocation().equals("/Default/????????????/?????????")) {
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
							if (pname.equals("??????_?????????") || pname.equals("??????_?????????")) {
								Task ptask = task.getParentTask();
								ptask.setStartDate(DateUtils.getCurrentTimestamp());
								ptask.setState(TaskStateType.INWORK.getDisplay());
								PersistenceHelper.manager.modify(ptask);
							}
						}

						project.setStartDate(DateUtils.getCurrentTimestamp());
						project.setKekState("?????????");
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

							if (parent.getName().equals("?????????")) {
								continue;
							}
							o++;
						}

						if (o == 1) {

							parent.setStartDate(DateUtils.getCurrentTimestamp());
							parent.setState(TaskStateType.INWORK.getDisplay());
							PersistenceHelper.manager.modify(parent);

							project.setStartDate(DateUtils.getCurrentTimestamp());
							project.setKekState("?????????");
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

			// ?????? ?????????
			// ErpHelper.service.sendPartListToERP(master);

			if (isApp) {
				WorkspaceHelper.service.submitApp(master, param);
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
			map.put("msg", "???????????? " + CREATE_OK);

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
			map.put("msg", "????????? " + CREATE_FAIL);
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
		String location = "/Default/????????????/" + engType + "_?????????";
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

			WorkspaceHelper.service.deleteAllLine(master);

			ArrayList<PartListMasterProjectLink> lists = PartlistHelper.manager.getPartListMasterProjectLink(master);
			System.out.println("klists=" + lists.size());

			// ???????????? ??????
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

			// ????????? ??????
			list = PartlistHelper.manager.getPartListData(master);

			for (PartListData pData : list) {
				PersistenceHelper.manager.delete(pData);
			}

			// ????????? ??????
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

				if ("??????".equals(engType)) {
					ArrayList<PartListMaster> datas = new ArrayList<PartListMaster>();
					datas = PartlistHelper.manager.findPartListByProject(project, engType, "");
					double totalPrices = 0D;
					for (PartListMaster masters : datas) {
						totalPrices += masters.getTotalPrice();
					}
					project.setOutputMachinePrice(totalPrices);
				} else if ("??????".equals(engType)) {
					ArrayList<PartListMaster> datas = new ArrayList<PartListMaster>();
					datas = PartlistHelper.manager.findPartListByProject(project, engType, "");
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
					map.put("msg", "?????? ??????????????? " + engType + "_????????? ???????????? ????????????.");
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
						if (oo.getLocation() != null && oo.getLocation().equals("/Default/????????????/?????????")) {
							continue;
						}
						o++;
					}

					if (o == 1) {

						task.setStartDate(DateUtils.getCurrentTimestamp());
						task.setState(TaskStateType.INWORK.getDisplay());
						PersistenceHelper.manager.modify(task);

						project.setStartDate(DateUtils.getCurrentTimestamp());
						project.setKekState("?????????");
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

							if (parent.getName().equals("?????????")) {
								continue;
							}
							o++;
						}

						if (o == 1) {

							parent.setStartDate(DateUtils.getCurrentTimestamp());
							parent.setState(TaskStateType.INWORK.getDisplay());
							PersistenceHelper.manager.modify(parent);

							project.setStartDate(DateUtils.getCurrentTimestamp());
							project.setKekState("?????????");
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
				WorkspaceHelper.service.submitApp(master, param);
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "???????????? " + MODIFY_OK);
			map.put("url", "/Windchill/plm/partList/listPartList");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "????????? " + MODIFY_FAIL);
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

				WorkspaceHelper.service.deleteAllLine(partList);

				String state = partList.getLifeCycleState().toString();
				if (state.equalsIgnoreCase("APPROVED")) {
					map.put("result", FAIL);
					map.put("msg", "????????? ????????? ?????? ????????????.\n????????? ???????????? ????????????.\n??????????????? : " + partList.getNumber() + ".");
					map.put("url", "/Windchill/plm/partList/listPartList");
					return map;
				}

				ArrayList<PartListMasterProjectLink> projectList = PartlistHelper.manager
						.getPartListMasterProjectLink(partList);
				for (PartListMasterProjectLink link : projectList) {
					PersistenceHelper.manager.delete(link);
				}

				WorkspaceHelper.service.deleteAllLine(partList);

				PersistenceHelper.manager.delete(partList);
			}

			map.put("result", SUCCESS);
			map.put("msg", "???????????? " + DELETE_OK);
			map.put("url", "/Windchill/plm/partList/listPartList");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????????  " + DELETE_FAIL);
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
		String location = "/Default/????????????/" + engType + "_?????????";
		String number = DocumentHelper.manager.getNextNumber("PP-");
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows"); // ????????????
		ArrayList<Map<String, Object>> _addRows = (ArrayList<Map<String, Object>>) params.get("_addRows"); // ???????????????
		Transaction trs = new Transaction();
		try {
			trs.start();

			PartListMaster master = PartListMaster.newPartListMaster();
			master.setNumber(number);
			master.setName(name);
			master.setDescription(description);
			master.setOwnership(CommonUtils.sessionOwner());
			// ????????? ?????? ????????? ?????? ???????????? ??????..
			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) master, folder);
			master = (PartListMaster) PersistenceHelper.manager.save(master);

			// ContentUtils.updatePrimary(param, master);
//				ContentUtils.updateSecondary(param, master);

			double total = 0D;
			int sort = 0;
			for (int i = 0; i < _addRows.size(); i++) {
				Map<String, Object> _addRow = (Map<String, Object>) _addRows.get(i);

				String lotNo = (String) _addRow.get("lotNo");
				String unitName = (String) _addRow.get("unitName");
				String partNo = (String) _addRow.get("partNo");
				String partName = (String) _addRow.get("partName");
				String standard = (String) _addRow.get("standard");
				String maker = (String) _addRow.get("maker");
				String customer = (String) _addRow.get("customer");
				int quantity = (int) _addRow.get("quantity");
				String unit = (String) _addRow.get("unit");
				double price = (double) _addRow.get("price");
				String currency = (String) _addRow.get("currency");
				double won = (double) _addRow.get("won");
				double exchangeRate = (double) _addRow.get("exchangeRate");
				String referDrawing = (String) _addRow.get("referDrawing");
				String classification = (String) _addRow.get("classification");
				String note = (String) _addRow.get("note");

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

				MasterDataLink link = MasterDataLink.newMasterDataLink(master, data);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);

				total += data.getWon();
				sort++;
			}

			for (Map<String, Object> addRow : addRows) {
				String oid = (String) addRow.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);

				if ("??????".equals(engType)) {
					double outputMachinePrice = project.getOutputMachinePrice() != null
							? project.getOutputMachinePrice()
							: 0D;
					outputMachinePrice += total;
					project.setOutputMachinePrice(outputMachinePrice);
				} else if ("??????".equals(engType)) {
					double outputElecPrice = project.getOutputElecPrice() != null ? project.getOutputElecPrice() : 0D;
					outputElecPrice += total;
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
					map.put("msg", "?????? ??????????????? " + engType + "_" + tname + " ???????????? ????????????.");
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
						if (oo.getLocation() != null && oo.getLocation().equals("/Default/????????????/?????????")) {
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
							if (pname.equals("??????_?????????") || pname.equals("??????_?????????")) {
								Task ptask = task.getParentTask();
								ptask.setStartDate(DateUtils.getCurrentTimestamp());
								ptask.setState(TaskStateType.INWORK.getDisplay());
								PersistenceHelper.manager.modify(ptask);
							}
						}

						project.setStartDate(DateUtils.getCurrentTimestamp());
						project.setKekState("?????????");
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

							if (parent.getName().equals("?????????")) {
								continue;
							}
							o++;
						}

						if (o == 1) {

							parent.setStartDate(DateUtils.getCurrentTimestamp());
							parent.setState(TaskStateType.INWORK.getDisplay());
							PersistenceHelper.manager.modify(parent);

							project.setStartDate(DateUtils.getCurrentTimestamp());
							project.setKekState("?????????");
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

			// ?????? ?????????
			// ErpHelper.service.sendPartListToERP(master);

			if (isApp) {
				WorkspaceHelper.service.submitApp(master, param);
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
			map.put("msg", "???????????? " + CREATE_OK);

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

	@Override
	public void create(PartListDTO dto) throws Exception {
		String name = dto.getName();
		String engType = dto.getEngType();
		String description = dto.getContent();
		ArrayList<String> secondarys = dto.getSecondarys();
		ArrayList<Map<String, Object>> _addRows = dto.get_addRows();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows();

		String location = "/Default/????????????/" + engType + "_?????????";

		Transaction trs = new Transaction();
		try {
			trs.start();

			String number = DocumentHelper.manager.getNextNumber("PP-");
			PartListMaster partListMaster = PartListMaster.newPartListMaster();
			partListMaster.setNumber(number);
			partListMaster.setName(name);
			partListMaster.setDescription(description);
			partListMaster.setOwnership(CommonUtils.sessionOwner());

			// ????????? ?????? ????????? ?????? ???????????? ??????..
			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) partListMaster, folder);

			partListMaster = (PartListMaster) PersistenceHelper.manager.save(partListMaster);

			for (String secondary : secondarys) {
				ApplicationData applicationData = ApplicationData.newApplicationData(partListMaster);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(partListMaster, applicationData, secondary);
			}

			for (Map<String, Object> _addRow : _addRows) {
				String oid = (String) _addRow.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				PartListMasterProjectLink link = PartListMasterProjectLink.newPartListMasterProjectLink(partListMaster,
						project);
				PersistenceHelper.manager.save(link);
			}

			int sort = 0;
			for (Map<String, Object> addRow : addRows) {
				// ????????? ?????????..
				PartListData data = PartListData.newPartListData();

				String lotNo = (String) addRow.get("lotNo");
				String unitName = (String) addRow.get("unitName");
				String partNo = (String) addRow.get("partNo");
				String partName = (String) addRow.get("partName");
				String standard = (String) addRow.get("standard");
				String maker = (String) addRow.get("maker");
				String customer = (String) addRow.get("customer");
				int quantity = (int) addRow.get("quantity");
				String unit = (String) addRow.get("unit");
				int price = (int) addRow.get("price");
				String currency = (String) addRow.get("currency");
				int won = (int) addRow.get("won");
				Object exchangeRate = (Object) addRow.get("exchangeRate");
				String referDrawing = (String) addRow.get("referDrawing");
				String classification = (String) addRow.get("classification");
				String note = (String) addRow.get("note");

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

				if (exchangeRate instanceof Double) {
					double value = (double) exchangeRate;
					data.setExchangeRate((int) value);
				} else {
					data.setExchangeRate((int) exchangeRate);
				}

				data.setReferDrawing(referDrawing);
				data.setClassification(classification);
				data.setNote(note);
				data.setPartListDate(new Timestamp(new Date().getTime()));
				PersistenceHelper.manager.save(data);

				MasterDataLink link = MasterDataLink.newMasterDataLink(partListMaster, data);
				link.setSort(sort++);
				PersistenceHelper.manager.save(link);
			}

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
}
