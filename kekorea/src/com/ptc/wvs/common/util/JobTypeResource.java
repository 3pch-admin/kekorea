package com.ptc.wvs.common.util;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.ptc.wvs.common.util.JobTypeResource")
public final class JobTypeResource extends WTListResourceBundle {
	/**
	 * /* bcwti
	 *
	 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights
	 * Reserved.
	 *
	 * This software is the confidential and proprietary information of PTC and is
	 * subject to the terms of a software license agreement. You shall not disclose
	 * such confidential information and shall use it only in accordance with the
	 * terms of the license agreement.
	 *
	 * ecwti
	 * 
	 * ------------------------------------------------ Table and column labels and
	 * as headings in jsps ------------------------------------------------
	 * ########################### Names of the OOTB WVS Job Types
	 * ####################################
	 **/
	@RBEntry("Publish Job")
	@RBComment("Name to display for a Publish Job")
	public static final String publishJob = "publishJob";

	@RBEntry("Publish Jobs")
	@RBComment("Plural name to display for Publish Jobs")
	public static final String publishJobs = "publishJobs";

	@RBEntry("Print Job")
	@RBComment("Name to display for a Print Job")
	public static final String printJob = "printJob";

	@RBEntry("Print Jobs")
	@RBComment("Plural name to display for Print Jobs")
	public static final String printJobs = "printJobs";

	@RBEntry("Interference Detection Job")
	@RBComment("Name to display for a Clash Job")
	public static final String clashJob = "clashJob";

	@RBEntry("Interference Detection Jobs")
	@RBComment("Plural name to display for Interference Detection Jobs")
	public static final String clashJobs = "clashJobs";

	@RBEntry("Thumbnail Job")
	@RBComment("Name to display for a Thumbnail Job")
	public static final String thumbnailJob = "thumbnailJob";

	@RBEntry("Thumbnail Jobs")
	@RBComment("Plural name to display for Thumbnail Jobs")
	public static final String thumbnailJobs = "thumbnailJobs";

	@RBEntry("Cleanup Job")
	@RBComment("Name to display for a Cleanup Job")
	public static final String cleanupJob = "cleanupJob";

	@RBEntry("Cleanup Jobs")
	@RBComment("Plural name to display for Cleanup Jobs")
	public static final String cleanupJobs = "cleanupJobs";

	@RBEntry("KEK PROJECT SCHEDULE")
	public static final String kekJob = "kekJob";
}
