package com.ptc.wvs.common.util;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.ptc.wvs.common.util.JobTypeResource")
public final class JobTypeResource_ko extends WTListResourceBundle {
   /**
/* bcwti
 *
 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
    * 
    * ------------------------------------------------
    * Table and column labels and as headings in jsps
    * ------------------------------------------------
    * ########################### Names of the OOTB WVS Job Types ####################################
    **/
   @RBEntry("게시 작업")
   @RBComment("Name to display for a Publish Job")
   public static final String publishJob = "publishJob";

   @RBEntry("게시 작업")
   @RBComment("Plural name to display for Publish Jobs")
   public static final String publishJobs = "publishJobs";

   @RBEntry("인쇄 작업")
   @RBComment("Name to display for a Print Job")
   public static final String printJob = "printJob";

   @RBEntry("인쇄 작업")
   @RBComment("Plural name to display for Print Jobs")
   public static final String printJobs = "printJobs";

   @RBEntry("간섭 검사 작업")
   @RBComment("Name to display for a Clash Job")
   public static final String clashJob = "clashJob";

   @RBEntry("간섭 검사 작업")
   @RBComment("Plural name to display for Interference Detection Jobs")
   public static final String clashJobs = "clashJobs";

   @RBEntry("축소판 작업")
   @RBComment("Name to display for a Thumbnail Job")
   public static final String thumbnailJob = "thumbnailJob";

   @RBEntry("축소판 작업")
   @RBComment("Plural name to display for Thumbnail Jobs")
   public static final String thumbnailJobs = "thumbnailJobs";

   @RBEntry("정리 작업")
   @RBComment("Name to display for a Cleanup Job")
   public static final String cleanupJob = "cleanupJob";

   @RBEntry("정리 작업")
   @RBComment("Plural name to display for Cleanup Jobs")
   public static final String cleanupJobs = "cleanupJobs";
   
	@RBEntry("KEK 프로젝트 스케줄")
	public static final String kekJob = "kekJob";
}
