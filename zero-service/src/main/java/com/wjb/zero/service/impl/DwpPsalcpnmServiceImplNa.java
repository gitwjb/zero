package com.wjb.zero.service.impl;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.wjb.zero.dao.CommRepositoty;
import com.wjb.zero.service.IDwpPsalcpnmService;
import com.wjb.zero.util.core.DateTimeUtils;
import com.wjb.zero.util.entity.ResultInfo;


/**
 * @description 
 * @author wjb
 */
@Service
@Transactional
//@Primary
public class DwpPsalcpnmServiceImplNa implements IDwpPsalcpnmService{

	@Autowired
	CommRepositoty dwpPsalcpnmRepository;
	
	@Override
	public ResultInfo processDwpPsalcpnm(String sDate,String eDate,String userId) {
		ResultInfo resultInfo = new ResultInfo();
		//调用处理的分步操作
		PrintWriter pr = null;
		try {
			String logFile = "E:\\codehause\\trunk\\JavaEE\\smartrix\\tmp\\log\\sltcpn20181105.log";
			pr = new PrintWriter(new FileWriter(logFile,true),true);

			String stime = DateTimeUtils.lastYearOrMonth(sDate+" 08:00:00", Calendar.DATE, -1, "yyyyMMdd HH:mm:ss");
			String etime = DateTimeUtils.lastYearOrMonth(eDate+" 08:00:00", Calendar.DATE, 0, "yyyyMMdd HH:mm:ss");
			String last0 = eDate.substring(0, 6);//
			String last2 = DateTimeUtils.lastYearOrMonth(last0, Calendar.MONTH, -2, "yyyyMM");
			String last6 = DateTimeUtils.lastYearOrMonth(last0, Calendar.MONTH, -6, "yyyyMM");
			String monthLine = DateTimeUtils.lastYearOrMonth(sDate+" 08:00:00", Calendar.MONTH, -1, "yyyyMMdd HH:mm:ss");
			monthLine = DateTimeUtils.lastYearOrMonth(monthLine, Calendar.DATE, -1, "yyyyMMdd HH:mm:ss");
			String pid = UUID.randomUUID().toString();
			
			pr.println("bokSlt2Dos_d>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			//本航国内     bok + slt -->dos
			bokSlt2Dos_d(stime,etime,last6,last2,last0,userId);//
			pr.println("cleanDosRep>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosRep();//bok 匹配  slt 重复
			pr.println("dos2ssal>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			bokLog(monthLine, "本航国内bok", pid, userId);
			dos2ssal(monthLine,"D");//带去重   一月内未匹配
			pr.println("dos2psal>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			dos2psal(last6,monthLine,"D",userId);//带去重 
			pr.println("exchangePt>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			exchangePt(userId);
			pr.println("cleanDosPsalcpnm>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosPsalcpnm();
			pr.println("本航国内>>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			
			//本航国内   中间表到接口表   
			pr.println("salSlt2Dos_d>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			salSlt2Dos_d(last6,last0,monthLine,userId);//大于1月，或者匹配成功          
			pr.println("cleanDosRep>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosRep();		
			pr.println("dos2psal_x>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			salLog("本行国内中间表",pid,userId);
			dos2psal_x(last6,"D",userId);//
			pr.println("exchangePt>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			exchangePt(userId);//处理换票
			pr.println("cleanDosPsalcpnm>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosPsalcpnm();//清空
			pr.println("中间表国内>>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			
			//外航国内
			pr.println("obok2Dos_d>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			obok2Dos_d(stime,etime,userId);
			pr.println("dos2psal_o>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			obokLog("外航国内obok",pid,userId);
			dos2psal_o(last6,userId);
			pr.println("exchangePt>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			exchangePt(userId);
			pr.println("cleanDosPsalcpnm>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosPsalcpnm();
			pr.println("外航国内>>>>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			//-------------
			
			//本航国际     bok + slt -->dos
			pr.println("bokSlt2Dos_i>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			bokSlt2Dos_i(stime,etime,last6,last2,last0,userId);
			pr.println("cleanDosRep>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosRep();
			pr.println("dos2ssal>>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			bokLog(monthLine, "本航国际bok", pid, userId);
			dos2ssal(monthLine,"I");
			pr.println("dos2psal>>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			dos2psal(last6,monthLine,"I",userId);
			pr.println("exchangePt>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			exchangePt(userId);
			pr.println("cleanDosPsalcpnm>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosPsalcpnm();
			pr.println("本航国际 >>>>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			
			//本航国际   中间表到接口表   
			pr.println("salSlt2Dos_i>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			salSlt2Dos_i(last6,last0,monthLine,userId);//
			pr.println("cleanDosRep>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosRep();
			pr.println("dos2psal_x>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			salLog("本行国际中间表",pid,userId);
			dos2psal_x(last6,"I",userId);
			pr.println("exchangePt>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			exchangePt(userId);
			pr.println("cleanDosPsalcpnm>>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosPsalcpnm();
			pr.println("中间表国际>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			
			//外航国际
			pr.println("obok2Dos_i>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			obok2Dos_i(stime,etime,userId);
			pr.println("dos2psal_o>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			obokLog("外航国际obok",pid,userId);
			dos2psal_o(last6,userId);
			pr.println("exchangePt>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			exchangePt(userId);
			pr.println("cleanDosPsalcpnm>>>>>"+DateTimeUtils.getCurrentDateTime());
			cleanDosPsalcpnm();
			pr.println("外航国际>>>>>>>>>>>>>>>"+DateTimeUtils.getCurrentDateTime());
			resultInfo.setResultText(sDate+" - "+eDate+",销售基库信息更新成功！");
		} catch (Exception e) {
			if(pr!=null) {
				e.printStackTrace(pr);
				pr.append("\r\n"+DateTimeUtils.getCurrentDateTime()+"\r\n");
			}
//			StringWriter sw = new StringWriter();  
//	        PrintWriter pw = new PrintWriter(sw);  
//	        e.printStackTrace(pw);  
//	        String [] exceptions=sw.toString().toUpperCase().split("EXCEPTION:");
//	        System.out.println(exceptions);
//	        String errors="";
//	        if(exceptions.length>0){
//	           	for (int i = 0; i < exceptions.length; i++) {
//	           		int index=exceptions[i].length();
//	           		if(exceptions[i].indexOf("\n")>-1){
//	           			index=exceptions[i].indexOf("\n");
//	           		}
//	           		 System.out.println(exceptions[i].substring(0, index));  
//	           		 errors+="\n"+exceptions[i].substring(0, index);
//	           	}
//	        }
			resultInfo.setResultFlag(false);
//			resultInfo.setResultText("销售基库信息更新失败！"+errors);
		    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}finally {
			if(pr!=null) pr.close();
		}
		
		return resultInfo;
	}
	
	/**
	 * @description  本航国内/国际  接口表数据  进  中间表   (接口表一月以内未匹配数据，要求 先去重后插入)
	 * @param date 日期   格式为(yyyyMMdd)的字符串
	 * @param ind  国内国际标识  D 国内  I 国际
	 * @author wjb
	 */
	private void dos2ssal(String monthLine,String ind) {
		//中间表去重
		StringBuilder sb = new StringBuilder();
		sb.append("delete from DWS_PSALCPNM_20181018 s ");
		sb.append(" where exists(select 1 from DOS_PSALCPNM d ");
		sb.append(" where s.cpntkt=d.saltkt and s.cpncpn=d.tktcpn ");
		sb.append(" and s.cpnprf=d.tktprf)");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
		
		//插入中间表  一月以内没有匹配上的 接口表数据
		sb = new StringBuilder();
		sb.append("insert into DWS_PSALCPNM_20181018(CPNFPM,CPNPRM,CPNBTH,CPNBTS,CPNSUB,CPNDOI,CPNCUR,CPNSRD,");
		sb.append("CPNSRN,CPNSTY,CPNTCA,CPNAGT,CPNPRF,CPNTID,CPNTKT,CPNTKY,CPNFRM,CPNLTK,CPNCJT,");
		sb.append("CPNCTS,CPNCLT,CPNGRP,CPNTUR,CPNPTY,CPNFPT,CPNDSR,CPNICT,CPNCPN,CPNBDS,CPNBUP,");
		sb.append("CPNCLS,CPNFB#,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,CPNPOF,CPNPDC,");
		sb.append("CPNSPA,CPNSTP,CPNETP,CPNPNR,CPNRMC,CPNINV,CPNNPI,CPNSPT,CPNITI,CPNOCI,CPNUTI,");
		sb.append("CPNCTI,CPNCTO,CPNTTY,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,");
		sb.append("ITRCYG_S,ITRCTG_S,ITRSLG_S,CPNGPI,CPNGPP,ITRCLS_S,CPNMTY,CPNMRS,CPNIRS,CPNOAG,");
		sb.append("CPNOPR,CPNOKT,CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,SALIND ");
		sb.append(" ,CPNCM1,CPNCU1,CPNCM2,CPNCU2,CPNBEV,CPNCNC,CPNYQC,CPNGPC,CPNGPU,CPNREC) ");//,CPNSPC,CPNREC
		sb.append(" select  i.CPNFPM,i.TKTPRM,i.TKTBTH,i.TKTBTS,i.TKTSUB,i.TKTDOI,i.TKTCUR,i.TKTSRD,");
		sb.append(" i.TKTSRN,i.TKTSTY,i.TKTTCA,i.TKTAGT,i.TKTPRF,i.TKTTID,i.SALTKT,i.TKTTKY,i.TKTFRM,");
		sb.append(" i.TKTLTK,i.TKTCJT,i.TKTCTS,i.TKTCLT,i.TKTGRP,i.TKTTUR,i.TKTPTY,i.CPNFPT,i.TKTDSR,");
		sb.append(" i.TKTICT,i.TKTCPN,i.CPNBDS,i.CPNBUP,i.CPNCLS,i.CPNFB#,i.CPNFD#,i.CPNPAD,i.CPNPDT,");
		sb.append(" i.CPNPAT,i.CPNPMC,i.CPNPMF,i.CPNPOC,i.CPNPOF,i.TKTPDC,i.CPNSPA,i.CPNSTP,i.TKTETP,");
		sb.append(" i.TKTPNR,i.TKTRMC,i.TKTVOI,i.TKTNRI,i.CPNSPT,i.TKTITI,i.TKTOCI,i.TKTUTI,i.TKTCTI,");
		sb.append(" i.TKTCTO,i.CPNTTY,i.CPNUAI,i.CPNUAT,i.ITRCPN,i.ITRTKS,i.ITRPOC_S,i.ITRPMC_S,");
		sb.append(" i.ITRTKC_S,i.ITRCYG_S,i.ITRCTG_S,i.ITRSLG_S,i.TKTGPI,i.TKTGPP,i.ITRCLS_S,i.TKTTYC,");
		sb.append(" i.TKTMRS,i.CPNIRC,i.TKTOAG,i.TKTOPR,i.TKTOKT,i.CPNFPL,i.CPNFPU,i.CPNMAR,i.CPNCKM,");
		sb.append(" i.CPNCRM,	i.CPNLRD,	i.CPNFZD,'").append(ind).append("' ");
		sb.append(" ,nvl(i.CPNOMC,0),nvl(i.CPNOMU,0),nvl(i.CPNOMC2,0),nvl(i.CPNOMU2,0) ");
		sb.append(" ,nvl(i.CPNATC,0),nvl(i.CPNBTC,0),nvl(i.CPNCTC,0),i.CPNCLC,i.CPNCLU ");
		sb.append(" ,i.cpnrec ");
		sb.append(" from DOS_PSALCPNM i ");
		sb.append(" where i.CPNREC >= to_date('").append(monthLine).append("','yyyy-mm-dd hh24:mi:ss')");
		sb.append(" and i.tkttkt is null");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	} 
	
	/**
	 * @description  本航国内/国际  接口表数据  进  基表   (接口表一月以上未匹配数据，要求 先去重后插入,关联主文件)
	 * @param date 日期   格式为(yyyyMMdd)的字符串
	 * @param ind  国内国际标识  D 国内  I 国际
	 * @param userId  用户ID
	 * @author wjb
	 */
	private void dos2psal_x(String last6,String ind,String userId) {
		//基表去重   删除 基表 6月以内的  和 接口表 相同票号和票联号数据
		StringBuilder sb = new StringBuilder();
		sb.append("delete from DWP_PSALCPNM_20181018 p where p.TKTPRM >='").append(last6).append("' ");
		sb.append(" and exists(select 1 from DOS_PSALCPNM i where i.saltkt=p.tkttkt and i.TKTCPN=p.TKTCPN and i.TKTPRF=p.TKTPRF)");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
		// 插入中间表  一月以上没有匹配上的 接口表数据   and 匹配上的数据
		sb = new StringBuilder();
		sb.append("insert into DWP_PSALCPNM_20181018(ID,CREATE_ID,CREATE_DATE,CREATE_TIME,");
		sb.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTCTD,TKTTID,TKTHOI,TKTPRF,TKTFRM,TKTTKT,TKTCPN,");
		sb.append("TKTSTY,TKTTKY,TKTBTH,TKTSUB,TKTBTS,TKTCUR,TKTCUR2,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,TKTOTK,");
		sb.append("TKTPIS,TKTPFM,TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,TKTRRG,TKTOAG,MCNCYN,DABCTN,MAGAGN,");
		sb.append("MAGREL,MVCDUN,MAGRRG,MMRRRN,MBABAN,MBATSF,MBATSA,MBATSN,MAGRNK,DABCYN,MAGHDC,MAGPRI,TKTPTY,TKTPGN,");
		sb.append("CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,");
		sb.append("ITRSLG_S,ITRCLS_S,DEBRRG,DEBRRN,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,");
		sb.append("TKTPNR,TKTVOI,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,HBTHUB,HBTGRP,");
		sb.append("HBTIND,HBTSIX,HBTORI,DEBARN,TKTDID,TKTDNA,TKTCHL,TKTSDV,");
		sb.append("CPNFPM,CPNFPT,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,");
		sb.append("CPNPOF,CPNBUP,CPNBDS,CPNSTP,MAPPTY,DAINA1,DAIYN1,DAIST1,CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,");
		sb.append("TKTCA1,TKTAN1,TKTPC1,TKTPL1,TKTPU1,TKTUA1,CPNCLC,CPNCLL,CPNCLU,CPNOMC,CPNOML,CPNOMU,CPNOMC2,CPNOML2,");
		sb.append("CPNOMU2,CPNTNU,CPNATC,CPNATL,CPNATU,CPNBTC,CPNBTL,CPNBTU,CPNCTC,CPNCTL,TKTCTU,SALCTY,SALAGN,SALRRG,SALRRN) ");
		sb.append("select sys_guid(),'").append(userId).append("',to_char(sysdate,'yyyyMMdd'),to_char(sysdate,'hh24miss'),");
		sb.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTCTD,TKTTID,TKTHOI,TKTPRF,TKTFRM,SALTKT,TKTCPN,TKTSTY,");
		sb.append("TKTTKY,TKTBTH,TKTSUB,TKTBTS,TKTCUR,TKTCUR2,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,TKTOTK,TKTPIS,TKTPFM,");
		sb.append("TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,DIMAGT.Magrrg,TKTOAG,MASCNY.MCNCYN,DIMGAB.DABCTN,");
		sb.append("MASAGT.MAGAGN,MASAGI.MAGREL,MASVCN.MVCDUN,MASAGT.MAGRRG,MASMRG.MMRRRN,DIMAGT.MBABAN,DIMAGT.MBATSF,");
		sb.append("DIMAGT.MBATSA,DIMAGT.MBATSN,MASAGT.MAGRNK,DIMGAB2.DABCYN,MASAGT.MAGHDC,MASAGT.MAGPRI,TKTPTY,TKTPGN,");
		sb.append("CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,");
		sb.append("ITRCLS_S,DIMGEB.DEBRRG,DIMGEB.DEBRRN,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,TKTPNR,");
		sb.append("TKTVOI,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,");
		sb.append("ngdw.pkg_psal.f_get_hub_apt(i.tktity,i.tkttid,i.hbtind,i.SALRRG),HBTGRP,HBTIND,HBTSIX,");
		sb.append("case when DIMGEB.DEBRRG is not null and DIMGEB.DEBRRG=DIMAGT.Magrrg then '本地始发' else '异地始发' end,");
		sb.append(" case when instr(replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国',''),'-')<>0");
		sb.append("      then DIMGEB_UPL.DEBARN");
		sb.append("      when instr(replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国',''),'-')=0");
		sb.append("      then replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国','')");
		sb.append(" end,  ");
		sb.append(" TKTDID,REFDID.REFDNA,REFDID.REFCHL,REFDID.REFSDV,");
		sb.append(" CPNFPM,CPNFPT,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,CPNPOF,CPNBUP,CPNBDS,CPNSTP,MASAPP.MAPPTY,");
		sb.append(" DIMPAI.DAINA1 || '-' || DIMPAI.DAINA2,DIMPAI.DAIYN1 || '-' || DIMPAI.DAIYN2,DIMPAI.DAIST1 || '-' || DIMPAI.DAIST2,");
		sb.append(" CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,TKTCA1,TKTAN1,TKTPC1,TKTPL1,TKTPU1,TKTUA1,CPNCLC,CPNCLL,CPNCLU,CPNOMC,");
		sb.append(" CPNOML,CPNOMU,CPNOMC2,CPNOML2,CPNOMU2,'1',CPNATC,CPNATL,CPNATU,CPNBTC,CPNBTL,CPNBTU,CPNCTC,CPNCTL,TKTCTU,");
		sb.append(" NVL(DIMGAB.DABCYN,MASCNY.MCNCYN),MASAGT2.MAGAGN,DIMGEB.DEBRRG,DIMGEB.DEBRRN ");
		sb.append(" from DOS_PSALCPNM i ");
		sb.append(" left join NGDW.DIMAGT DIMAGT ON      i.TKTAGT=DIMAGT.MAGAGT");
		sb.append(" left join NGDW.DIMAGT DIMAGT2 ON DIMAGT.MAGPRI=DIMAGT2.MAGAGT");
		sb.append(" left join DRAS.MASCTA MASCTA ON      substr(i.TKTAGT,0,3)=MASCTA.MCTTCC");
		sb.append(" left join DRAS.MASCNY MASCNY ON MASCTA.MCTCNQ=MASCNY.MCNSEQ");
		sb.append(" left join dras.masagt MASAGT ON      i.TKTAGT=MASAGT.MAGAGT");
		sb.append(" left join ngdw.DIMGAB DIMGAB on MASAGT.MAGCTQ=DIMGAB.DABSEQ");
		sb.append(" left join dras.MASMRG MASMRG on MASAGT.MAGRRG=MASMRG.MMRRRG");
		sb.append(" left join dras.masagt MASAGT2 on MASAGT.MAGPRI=MASAGT2.MAGAGT");
		sb.append(" left join NGDW.DIMGAB DIMGAB2 on MASAGT2.MAGCTQ=DIMGAB2.DABSEQ");
		sb.append(" left join dras.MASAGI MASAGI on      i.TKTAGT=MASAGI.MAGAGT");
		sb.append(" left join DRAS.MASVCN MASVCN on i.TKTCLT = MASVCN.MVCVCN and i.TKTDOI BETWEEN MASVCN.MVCSTD AND MASVCN.MVCEDD");
		sb.append(" left join ngdw.MASAPP MASAPP on i.CPNBUP=MASAPP.MAPUPL and i.CPNBDS=MASAPP.MAPDIS");
		sb.append(" left join ngdw.dimgeb DIMGEB_UPL on i.CPNBUP = DIMGEB_UPL.DEBSTC");
		sb.append(" left join ngdw.dimgeb DIMGEB_DIS on i.CPNBDS = DIMGEB_DIS.DEBSTC");
		sb.append(" left join ngdw.refdid REFDID on i.TKTDID = REFDID.REFDID");		
		sb.append(" left join ngdw.dimpai DIMPAI ON i.cpnbup = DIMPAI.DAIUPL AND i.cpnbds = DIMPAI.DAIDIS");
		sb.append(" left join ngdw.dimgeb DIMGEB ON decode(substr(i.TKTITY,0,3),'TGX',substr(i.TKTITY,5,3),substr(i.TKTITY,0,3))=DIMGEB.DEBSTC");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}
	
	/**
	 * @description  本航国内/国际  接口表数据  进  基表   (接口表一月以上未匹配数据，要求 先去重后插入,关联主文件)
	 * @param date 日期   格式为(yyyyMMdd)的字符串
	 * @param ind  国内国际标识  D 国内  I 国际
	 * @param userId  用户ID
	 * @author wjb
	 */
	private void dos2psal(String last6,String monthLine,String ind,String userId) {
		//基表去重   删除 基表 6月以内的  和 接口表 相同票号和票联号数据
		StringBuilder sb = new StringBuilder();
		sb.append("delete from DWP_PSALCPNM_20181018 p where p.TKTPRM >'").append(last6).append("' ");
		sb.append(" and exists(select 1 from DOS_PSALCPNM i where i.saltkt=p.tkttkt and i.TKTCPN=p.TKTCPN and i.TKTPRF=p.TKTPRF)");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
		// 插入中间表  一月以上没有匹配上的 接口表数据   and 匹配上的数据
		sb = new StringBuilder();
		sb.append("insert into DWP_PSALCPNM_20181018(ID,CREATE_ID,CREATE_DATE,CREATE_TIME,");
		sb.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTCTD,TKTTID,TKTHOI,TKTPRF,TKTFRM,TKTTKT,TKTCPN,");
		sb.append("TKTSTY,TKTTKY,TKTBTH,TKTSUB,TKTBTS,TKTCUR,TKTCUR2,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,TKTOTK,");
		sb.append("TKTPIS,TKTPFM,TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,TKTRRG,TKTOAG,MCNCYN,DABCTN,MAGAGN,");
		sb.append("MAGREL,MVCDUN,MAGRRG,MMRRRN,MBABAN,MBATSF,MBATSA,MBATSN,MAGRNK,DABCYN,MAGHDC,MAGPRI,TKTPTY,TKTPGN,");
		sb.append("CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,");
		sb.append("ITRSLG_S,ITRCLS_S,DEBRRG,DEBRRN,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,");
		sb.append("TKTPNR,TKTVOI,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,HBTHUB,HBTGRP,");
		sb.append("HBTIND,HBTSIX,HBTORI,DEBARN,TKTDID,TKTDNA,TKTCHL,TKTSDV,");
		sb.append("CPNFPM,CPNFPT,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,");
		sb.append("CPNPOF,CPNBUP,CPNBDS,CPNSTP,MAPPTY,DAINA1,DAIYN1,DAIST1,CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,");
		sb.append("TKTCA1,TKTAN1,TKTPC1,TKTPL1,TKTPU1,TKTUA1,CPNCLC,CPNCLL,CPNCLU,CPNOMC,CPNOML,CPNOMU,CPNOMC2,CPNOML2,");
		sb.append("CPNOMU2,CPNTNU,CPNATC,CPNATL,CPNATU,CPNBTC,CPNBTL,CPNBTU,CPNCTC,CPNCTL,TKTCTU,SALCTY,SALAGN,SALRRG,SALRRN) ");
		sb.append("select sys_guid(),'").append(userId).append("',to_char(sysdate,'yyyyMMdd'),to_char(sysdate,'hh24miss'),");
		sb.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTCTD,TKTTID,TKTHOI,TKTPRF,TKTFRM,SALTKT,TKTCPN,TKTSTY,");
		sb.append("TKTTKY,TKTBTH,TKTSUB,TKTBTS,TKTCUR,TKTCUR2,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,TKTOTK,TKTPIS,TKTPFM,");
		sb.append("TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,DIMAGT.Magrrg,TKTOAG,MASCNY.MCNCYN,DIMGAB.DABCTN,");
		sb.append("MASAGT.MAGAGN,MASAGI.MAGREL,MASVCN.MVCDUN,MASAGT.MAGRRG,MASMRG.MMRRRN,DIMAGT.MBABAN,DIMAGT.MBATSF,");
		sb.append("DIMAGT.MBATSA,DIMAGT.MBATSN,MASAGT.MAGRNK,DIMGAB2.DABCYN,MASAGT.MAGHDC,MASAGT.MAGPRI,TKTPTY,TKTPGN,");
		sb.append("CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,");
		sb.append("ITRCLS_S,DIMGEB.DEBRRG,DIMGEB.DEBRRN,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,TKTPNR,");
		sb.append("TKTVOI,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,");
		sb.append("ngdw.pkg_psal.f_get_hub_apt(i.tktity,i.tkttid,i.hbtind,i.SALRRG),HBTGRP,HBTIND,HBTSIX,");
		sb.append("case when DIMGEB.DEBRRG is not null and DIMGEB.DEBRRG=DIMAGT.Magrrg then '本地始发' else '异地始发' end,");
		sb.append(" case when instr(replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国',''),'-')<>0");
		sb.append("      then DIMGEB_UPL.DEBARN");
		sb.append("      when instr(replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国',''),'-')=0");
		sb.append("      then replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国','')");
		sb.append(" end,  ");
		sb.append(" TKTDID,REFDID.REFDNA,REFDID.REFCHL,REFDID.REFSDV,");
		sb.append(" CPNFPM,CPNFPT,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,CPNPOF,CPNBUP,CPNBDS,CPNSTP,MASAPP.MAPPTY,");
		sb.append(" DIMPAI.DAINA1 || '-' || DIMPAI.DAINA2,DIMPAI.DAIYN1 || '-' || DIMPAI.DAIYN2,DIMPAI.DAIST1 || '-' || DIMPAI.DAIST2,");
		sb.append(" CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,TKTCA1,TKTAN1,TKTPC1,TKTPL1,TKTPU1,TKTUA1,CPNCLC,CPNCLL,CPNCLU,CPNOMC,");
		sb.append(" CPNOML,CPNOMU,CPNOMC2,CPNOML2,CPNOMU2,'1',CPNATC,CPNATL,CPNATU,CPNBTC,CPNBTL,CPNBTU,CPNCTC,CPNCTL,TKTCTU,");
		sb.append(" NVL(DIMGAB.DABCYN,MASCNY.MCNCYN),MASAGT2.MAGAGN,DIMGEB.DEBRRG,DIMGEB.DEBRRN ");
		sb.append(" from DOS_PSALCPNM i ");
		sb.append(" left join NGDW.DIMAGT DIMAGT ON      i.TKTAGT=DIMAGT.MAGAGT");
		sb.append(" left join NGDW.DIMAGT DIMAGT2 ON DIMAGT.MAGPRI=DIMAGT2.MAGAGT");
		sb.append(" left join DRAS.MASCTA MASCTA ON      substr(i.TKTAGT,0,3)=MASCTA.MCTTCC");
		sb.append(" left join DRAS.MASCNY MASCNY ON MASCTA.MCTCNQ=MASCNY.MCNSEQ");
		sb.append(" left join dras.masagt MASAGT ON      i.TKTAGT=MASAGT.MAGAGT");
		sb.append(" left join ngdw.DIMGAB DIMGAB on MASAGT.MAGCTQ=DIMGAB.DABSEQ");
		sb.append(" left join dras.MASMRG MASMRG on MASAGT.MAGRRG=MASMRG.MMRRRG");
		sb.append(" left join dras.masagt MASAGT2 on MASAGT.MAGPRI=MASAGT2.MAGAGT");
		sb.append(" left join NGDW.DIMGAB DIMGAB2 on MASAGT2.MAGCTQ=DIMGAB2.DABSEQ");
		sb.append(" left join dras.MASAGI MASAGI on      i.TKTAGT=MASAGI.MAGAGT");
		sb.append(" left join DRAS.MASVCN MASVCN on i.TKTCLT = MASVCN.MVCVCN and i.TKTDOI BETWEEN MASVCN.MVCSTD AND MASVCN.MVCEDD");
		sb.append(" left join ngdw.MASAPP MASAPP on i.CPNBUP=MASAPP.MAPUPL and i.CPNBDS=MASAPP.MAPDIS");
		sb.append(" left join ngdw.dimgeb DIMGEB_UPL on i.CPNBUP = DIMGEB_UPL.DEBSTC");
		sb.append(" left join ngdw.dimgeb DIMGEB_DIS on i.CPNBDS = DIMGEB_DIS.DEBSTC");
		sb.append(" left join ngdw.refdid REFDID on i.TKTDID = REFDID.REFDID");		
		sb.append(" left join ngdw.dimpai DIMPAI ON i.cpnbup = DIMPAI.DAIUPL AND i.cpnbds = DIMPAI.DAIDIS");
		sb.append(" left join ngdw.dimgeb DIMGEB ON decode(substr(i.TKTITY,0,3),'TGX',substr(i.TKTITY,5,3),substr(i.TKTITY,0,3))=DIMGEB.DEBSTC");
		sb.append(" where i.CPNREC < to_date('").append(monthLine).append("','yyyy-mm-dd hh24:mi:ss') ");
		sb.append(" or i.tkttkt = i.saltkt");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}

	/**
	 * @description  外航国内/国际接口表   进   基表  (关联主文件)
	 * @param userId  用户ID
	 * @author wjb
	 */
	private void dos2psal_o(String last6,String userId) {
		//基表去重   删除 基表 6月以内的  和 接口表 相同票号和票联号数据
		StringBuilder sb = new StringBuilder();
		sb.append("delete from DWP_PSALCPNM_20181018 p where p.TKTPRM >='").append(last6).append("' ");
		sb.append(" and exists(select 1 from DOS_PSALCPNM i where i.saltkt=p.tkttkt and i.TKTCPN=p.TKTCPN and i.TKTPRF=p.TKTPRF)");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
		
		sb = new StringBuilder();
		sb.append("insert into DWP_PSALCPNM_20181018(ID,CREATE_ID,CREATE_DATE,CREATE_TIME,");
		sb.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTCTD,TKTTID,TKTHOI,TKTPRF,TKTFRM,TKTTKT,TKTCPN,");
		sb.append("TKTSTY,TKTTKY,TKTBTH,TKTSUB,TKTBTS,TKTCUR,TKTCUR2,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,TKTOTK,");
		sb.append("TKTPIS,TKTPFM,TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,TKTRRG,TKTOAG,MCNCYN,DABCTN,MAGAGN,");
		sb.append("MAGREL,MVCDUN,MAGRRG,MMRRRN,MBABAN,MBATSF,MBATSA,MBATSN,MAGRNK,DABCYN,MAGHDC,MAGPRI,TKTPTY,TKTPGN,");
		sb.append("CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,");
		sb.append("ITRSLG_S,ITRCLS_S,DEBRRG,DEBRRN,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,");
		sb.append("TKTPNR,TKTVOI,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,HBTHUB,HBTGRP,");
		sb.append("HBTIND,HBTSIX,HBTORI,DEBARN,TKTDID,TKTDNA,TKTCHL,TKTSDV,");
		sb.append("CPNFPM,CPNFPT,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,");
		sb.append("CPNPOF,CPNBUP,CPNBDS,CPNSTP,MAPPTY,DAINA1,DAIYN1,DAIST1,CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,");
		sb.append("TKTCA1,TKTAN1,TKTPC1,TKTPL1,TKTPU1,TKTUA1,CPNCLC,CPNCLL,CPNCLU,CPNOMC,CPNOML,CPNOMU,CPNOMC2,CPNOML2,");
		sb.append("CPNOMU2,CPNTNU,CPNATC,CPNATL,CPNATU,CPNBTC,CPNBTL,CPNBTU,CPNCTC,CPNCTL,TKTCTU,SALCTY,SALAGN,SALRRG,SALRRN) ");
		sb.append("select sys_guid(),'").append(userId).append("',to_char(sysdate,'yyyyMMdd'),to_char(sysdate,'hh24miss'),");
		sb.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTCTD,TKTTID,TKTHOI,TKTPRF,TKTFRM,TKTTKT,TKTCPN,TKTSTY,");
		sb.append("TKTTKY,TKTBTH,TKTSUB,TKTBTS,TKTCUR,TKTCUR2,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,TKTOTK,TKTPIS,TKTPFM,");
		sb.append("TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,DIMAGT.Magrrg,TKTOAG,MASCNY.MCNCYN,DIMGAB.DABCTN,");
		sb.append("MASAGT.MAGAGN,MASAGI.MAGREL,MASVCN.MVCDUN,MASAGT.MAGRRG,MASMRG.MMRRRN,DIMAGT.MBABAN,DIMAGT.MBATSF,");
		sb.append("DIMAGT.MBATSA,DIMAGT.MBATSN,MASAGT.MAGRNK,DIMGAB2.DABCYN,MASAGT.MAGHDC,MASAGT.MAGPRI,TKTPTY,TKTPGN,");
		sb.append("CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,");
		sb.append("ITRCLS_S,DIMGEB.DEBRRG,DIMGEB.DEBRRN,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,TKTPNR,");
		sb.append("TKTVOI,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,");
		sb.append("ngdw.pkg_psal.f_get_hub_apt(i.tktity,i.tkttid,i.hbtind,i.SALRRG),HBTGRP,HBTIND,HBTSIX,");
		sb.append("case when DIMGEB.DEBRRG is not null and DIMGEB.DEBRRG=DIMAGT.Magrrg then '本地始发' else '异地始发' end,");
		sb.append(" case when instr(replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国',''),'-')<>0");
		sb.append("      then DIMGEB_UPL.DEBARN");
		sb.append("      when instr(replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国',''),'-')=0");
		sb.append("      then replace(replace((DIMGEB_UPL.DEBARN || '-' || DIMGEB_DIS.DEBARN),'中国-',''),'-中国','')");
		sb.append(" end,  ");
		sb.append(" TKTDID,REFDID.REFDNA,REFDID.REFCHL,REFDID.REFSDV,");
		sb.append(" CPNFPM,CPNFPT,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,CPNPOF,CPNBUP,CPNBDS,CPNSTP,MASAPP.MAPPTY,");
		sb.append(" DIMPAI.DAINA1 || '-' || DIMPAI.DAINA2,DIMPAI.DAIYN1 || '-' || DIMPAI.DAIYN2,DIMPAI.DAIST1 || '-' || DIMPAI.DAIST2,");
		sb.append(" CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,TKTCA1,TKTAN1,TKTPC1,TKTPL1,TKTPU1,TKTUA1,CPNCLC,CPNCLL,CPNCLU,CPNOMC,");
		sb.append(" CPNOML,CPNOMU,CPNOMC2,CPNOML2,CPNOMU2,'1',CPNATC,CPNATL,CPNATU,CPNBTC,CPNBTL,CPNBTU,CPNCTC,CPNCTL,TKTCTU,");
		sb.append(" NVL(DIMGAB.DABCYN,MASCNY.MCNCYN),MASAGT2.MAGAGN,DIMGEB.DEBRRG,DIMGEB.DEBRRN ");
		sb.append(" from DOS_PSALCPNM i ");
		sb.append(" left join NGDW.DIMAGT DIMAGT ON      i.TKTAGT=DIMAGT.MAGAGT");
		sb.append(" left join NGDW.DIMAGT DIMAGT2 ON DIMAGT.MAGPRI=DIMAGT2.MAGAGT");
		sb.append(" left join DRAS.MASCTA MASCTA ON      substr(i.TKTAGT,0,3)=MASCTA.MCTTCC");
		sb.append(" left join DRAS.MASCNY MASCNY ON MASCTA.MCTCNQ=MASCNY.MCNSEQ");
		sb.append(" left join dras.masagt MASAGT ON      i.TKTAGT=MASAGT.MAGAGT");
		sb.append(" left join ngdw.DIMGAB DIMGAB on MASAGT.MAGCTQ=DIMGAB.DABSEQ");
		sb.append(" left join dras.MASMRG MASMRG on MASAGT.MAGRRG=MASMRG.MMRRRG");
		sb.append(" left join dras.masagt MASAGT2 on MASAGT.MAGPRI=MASAGT2.MAGAGT");
		sb.append(" left join NGDW.DIMGAB DIMGAB2 on MASAGT2.MAGCTQ=DIMGAB2.DABSEQ");
		sb.append(" left join dras.MASAGI MASAGI on      i.TKTAGT=MASAGI.MAGAGT");
		sb.append(" left join DRAS.MASVCN MASVCN on i.TKTCLT = MASVCN.MVCVCN and i.TKTDOI BETWEEN MASVCN.MVCSTD AND MASVCN.MVCEDD");
		sb.append(" left join ngdw.MASAPP MASAPP on i.CPNBUP=MASAPP.MAPUPL and i.CPNBDS=MASAPP.MAPDIS");
		sb.append(" left join ngdw.dimgeb DIMGEB_UPL on i.CPNBUP = DIMGEB_UPL.DEBSTC");
		sb.append(" left join ngdw.dimgeb DIMGEB_DIS on i.CPNBDS = DIMGEB_DIS.DEBSTC");
		sb.append(" left join ngdw.refdid REFDID on i.TKTDID = REFDID.REFDID");		
		sb.append(" left join ngdw.dimpai DIMPAI ON i.cpnbup = DIMPAI.DAIUPL AND i.cpnbds = DIMPAI.DAIDIS");
		sb.append(" left join ngdw.dimgeb DIMGEB ON decode(substr(i.TKTITY,0,3),'TGX',substr(i.TKTITY,5,3),substr(i.TKTITY,0,3))=DIMGEB.DEBSTC");
		 
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}
	
	/**
	 * @description  清空 接口表
	 * @author wjb
	 */
	private void cleanDosPsalcpnm() {
		String sqldel = "TRUNCATE TABLE DOS_PSALCPNM";
		dwpPsalcpnmRepository.sqlExecute(sqldel);
	}
	
	
	/**
	 * @description 接口表 去重，在进入 中间表/基库之前  .取最近的非C
	 * @author wjb
	 */
	private int cleanDosRep() {
		int ret = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM dos_psalcpnm S ");
		sb.append(" WHERE  EXISTS(SELECT 1 FROM dos_psalcpnm D ");
		sb.append(" WHERE S.rowid<>D.rowid AND S.SALTKT=D.SALTKT AND S.TKTCPN=D.TKTCPN ");
		sb.append(" AND S.TKTPRF=D.TKTPRF AND S.TKTSTY=D.TKTSTY AND S.TKTVFD<=D.TKTVFD) ");
		ret = dwpPsalcpnmRepository.sqlExecute(sb.toString());
		return ret;
	}
	/**
	 * @description  插入基库数据，若销售类型为E，更新基库旧票销售类型为O
	 * @author wjb
	 */
	private int exchangePt(String userId) {
		int ret = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE DWP_PSALCPNM_20181018 O SET O.TKTSTY = 'O' ");
		sb.append(" WHERE EXISTS(SELECT 1 FROM DOS_PSALCPNM N WHERE N.TKTSTY = 'E' AND N.TKTOKT = O.TKTTKT)");
		ret = dwpPsalcpnmRepository.sqlExecute(sb.toString());
		return ret;
	}
	/**
	 * @description 国内： 当天的bok数据与近6个月的slt数据做匹配,关联的结果插入接口表
	 * @param Date 日期   格式为(yyyyMMdd)的字符串
	 * @param userId， 用户ID
	 * @author zyr
	 */
	private void bokSlt2Dos_d(String stime,String etime,String last6,String last2,String last0,String userId) {
		StringBuffer sql=new StringBuffer();
		sql.append("insert into DOS_PSALCPNM (id ,create_id  ,create_date,create_time, ");
		sql.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTTID,TKTPRF,TKTFRM,");
		sql.append("TKTTKT,TKTCPN,TKTSTY,TKTTKY,TKTCUR,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,");
		sql.append("TKTOKT,TKTAGT,TKTTCA,TKTCLT,TKTOAG,TKTPTY,TKTPGN,CPNFPL,CPNFPU,CPNCKM,");
		sql.append("CPNCRM,CPNLRD,CPNFZD,TKTITY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,TKTUTI,");
		sql.append("TKTCTI,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,");
		sql.append("ITRSLG_S,ITRCLS_S,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,");
		sql.append("TKTACE,TKTPNR,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTGPI,TKTGPP,TKTTYC,");
		sql.append("TKTMRS,CPNIRC,CPNFPM,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,");
		sql.append("CPNPOF,CPNBUP,CPNBDS,CPNSTP,CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCA1,TKTPC1,");
		sql.append("CPNOMC,CPNOMC2,CPNBTC,CPNCTC,");
		sql.append("HBTIND,HBTGRP,HBTSIX, ");//hbt
		sql.append("SALTKT,CPNMAR,CPNTTY,CPNFPT,TKTCTD,TKTBTH,TKTSUB,TKTBTS,TKTCUR2,");
		sql.append("TKTCTO,TKTHOI,CPNCLC,CPNCLU,TKTDID,CPNOMU2,CPNOMU,cpnrec )");
		sql.append("(select  SYS_GUID (),'").append(userId).append("', to_char(sysdate,'YYYYMMDD'), to_char(sysdate,'hhmmss') ,");
		sql.append("BOK.cpnprm,SLT.SLTSRD,SLT.SLTDOI,SLT.SLTVFD,SLT.SLTCDT,to_date(SLTTMI,'yyyymmdd hh24:mi:ss'),SLT.SLTUEC,bok.cpntid,bok.cpnprf,SLT.SLTFRM,");
		sql.append("SLT.SLTTKT,bok.CPNCPN,BOK.CPNSTY,SLT.SLTPCF,SLT.SLTCUR,SLT.SLTSRN,SLT.SLTCTP,SLT.SLTCTF,SLT.SLTCTS,SLT.SLTOPR,");
		sql.append("SLT.SLTOKT,SLT.SLTAGT,SLT.SLTCET,SLT.SLTVCN,SLT.SLTOAG,SLT.SLTPTY,SLT.SLTPGN,BOK.CPNFPL,BOK.CPNFPU,BOK.CPNCKM,");
		sql.append("BOK.CPNCRM,BOK.CPNLRD,BOK.CPNFZD,SLT.SLTITY,SLT.SLTITM,SLT.SLTITC,SLT.SLTITI,SLT.SLTCTY1,SLT.SLTOCI,BOK.CPNUTI,");
		sql.append("SLT.SLTCTI,BOK.CPNUAI,BOK.CPNUAT,BOK.ITRCPN,BOK.ITRTKS,BOK.ITRPOC_S,BOK.ITRPMC_S,BOK.ITRTKC_S,BOK.ITRCYG_S,BOK.ITRCTG_S,");
		sql.append("BOK.ITRSLG_S,BOK.ITRCLS_S,SLT.SLTSRC,SLT.SLTTIO,SLT.SLTEBI,SLT.SLTODS,SLT.SLTTRC,SLT.SLTDRC,SLT.SLTFCC,SLT.SLTENR,");
		sql.append("SLT.SLTCIA,SLT.SLTPNR,case when SLT.SLTFOP is not null and SLT.SLTFOP !='' and SLT.SLTFOP!='0' then 'CC' else '' end,SLT.SLTFFP,SLT.SLTCRC,SLT.SLTARF,decode(SLT.SLTGPI,'G','G','Y','G',''),SLT.SLTGPP,SLT.SLTMTY,");
		sql.append("SLT.SLTMRS,BOK.CPNIRS,BOK.CPNFPM,BOK.CPNFD#,BOK.CPNPAD,BOK.CPNPDT,BOK.CPNPAT,BOK.CPNPMC,BOK.CPNPMF,BOK.CPNPOC,");
		sql.append("BOK.CPNPOF,BOK.CPNBUP,BOK.CPNBDS,BOK.CPNSTP,BOK.CPNCLS,BOK.CPNFB#,nvl2(BOK.CPNSPC,'Y',''),BOK.CPNSPT,SLT.SLTFP1,SLT.SLTCA1,SLT.SLTPA1,");
		sql.append("nvl(BOK.CPNCM1,0),NVL(BOK.CPNCM2,0),nvl(BOK.CPNCNC,0),nvl(BOK.CPNYQC,0),");
		sql.append("ngdw.pkg_psal.f_get_flttyp(SLT.SLTITY),ngdw.pkg_psal.f_get_grp(SLT.SLTITY),ngdw.pkg_psal.f_get_six(BOK.CPNBUP,BOK.CPNBDS), ");//hbt
		sql.append("BOK.CPNTKT,BOK.CPNMAR,BOK.CPNTTY,substr(bok.CPNFPT,0,8),SLT.SLTCTD,SLT.SLTBTH,SLT.SLTSUB,substr(SLT.SLTBTH,1,3),'CNY',SLT.SLTCTO,decode(SLT.SLTPRF,'784','H','O'),BOK.CPNGPC,BOK.CPNGPU,SLT.SLTDID, ");
		sql.append("nvl(BOK.CPNCU2,0),NVL(BOK.CPNCU1,0),bok.cpnrec ");
		sql.append(" from NGDW.DWB_PBOKCPND bok ");
		sql.append(" left join (select * from NGDW.DWB_PSLTTKTD where SLTSTY<>'C' AND SLTPRM > '").append(last6).append("' and SLTPRM <= '").append(last0).append("') slt on bok.CPNTKT=slt.SLTTKT");
		sql.append(" where  bok.CPNREC >= to_date('").append(stime).append("','yyyy-mm-dd hh24:mi:ss')");
		sql.append(" and bok.CPNREC <= to_date('").append(etime).append("','yyyy-mm-dd hh24:mi:ss') ");
		sql.append(" and bok.cpnfpm >").append(last2).append(" and bok.cpnfpm <=").append(last0);
		sql.append(" )");
		dwpPsalcpnmRepository.sqlExecute(sql.toString());
	}
	
	/**
	 * @description 国内： 中间表（DWS_PSALCPNM）的数据与近6个月的slt数据做匹配,关联的结果插入接口表
	 * @param Date 日期   格式为(yyyyMMdd)的字符串
	 * @param userId， 用户ID
	 * @author zyr
	 */
	private void salSlt2Dos_d(String last6,String last0,String monthLine,String userId) {
		StringBuffer sql=new StringBuffer();
		sql.append("insert into DOS_PSALCPNM ( id ,create_id  ,create_date,create_time, ");
		sql.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTCDT,TKTICT,TKTUEC,TKTTID,TKTPRF,TKTFRM,");
		sql.append("TKTTKT,TKTCPN,TKTSTY,TKTTKY,TKTCUR,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,");
		sql.append("HBTIND,HBTGRP,HBTSIX, ");//hbt
		sql.append("TKTOKT,TKTAGT,TKTTCA,TKTCLT,TKTOAG,TKTPTY,TKTPGN,CPNFPL,CPNFPU,CPNCKM,");
		sql.append("CPNCRM,CPNLRD,CPNFZD,TKTITY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,TKTUTI,");
		sql.append("TKTCTI,CPNUAI,CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,");
		sql.append("ITRSLG_S,ITRCLS_S,TKTDSR,TKTETP,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,");
		sql.append("TKTACE,TKTPNR,TKTFPN,SLTFFP,SLTCRC,TKTGRP,TKTGPI,TKTGPP,TKTTYC,TKTMRS,");
		sql.append("CPNIRC,CPNFPM,CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,CPNPOF,");
		sql.append("CPNBUP,CPNBDS,CPNSTP,CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCA1,TKTPC1,");
		sql.append("CPNOMC,CPNOMC2,CPNBTC,CPNCTC,SALTKT,CPNMAR,CPNTTY,CPNFPT,TKTCTD,TKTBTH,");
		sql.append("TKTSUB,TKTBTS,TKTCUR2,TKTCTO,TKTHOI,CPNCLC,CPNCLU,TKTDID,CPNOMU,CPNOMU2,cpnrec)");
		sql.append("(select SYS_GUID (),'").append(userId).append("', to_char(sysdate,'YYYYMMDD'), to_char(sysdate,'hhmmss') ,");
		sql.append("SAL.cpnprm,SLT.SLTSRD,SLT.SLTDOI,SLT.SLTVFD,SLT.SLTCDT,to_date(SLTTMI,'yyyymmdd hh24:mi:ss'),SLT.SLTUEC,sal.cpntid,sal.cpnprf,SLT.SLTFRM,");
		sql.append("SLT.SLTTKT,sal.CPNCPN,sal.cpnsty,SLT.SLTPCF,SLT.SLTCUR,SLT.SLTSRN,SLT.SLTCTP,SLT.SLTCTF,SLT.SLTCTS,SLT.SLTOPR,");
		sql.append("ngdw.pkg_psal.f_get_flttyp(SLT.SLTITY),ngdw.pkg_psal.f_get_grp(SLT.SLTITY),ngdw.pkg_psal.f_get_six(sal.CPNBUP,sal.CPNBDS),  ");//hbt
		sql.append("SLT.SLTOKT,SLT.SLTAGT,SLT.SLTCET,SLT.SLTVCN,SLT.SLTOAG,SLT.SLTPTY,SLT.SLTPGN,SAL.CPNFPL,SAL.CPNFPU,SAL.CPNCKM,");
		sql.append("SAL.CPNCRM,SAL.CPNLRD,SAL.CPNFZD,SLT.SLTITY,SLT.SLTITM,SLT.SLTITC,SLT.SLTITI,SLT.SLTCTY1,SLT.SLTOCI,SAL.CPNUTI,");
		sql.append("SLT.SLTCTI,SAL.CPNUAI,SAL.CPNUAT,SAL.ITRCPN,SAL.ITRTKS,SAL.ITRPOC_S,SAL.ITRPMC_S,SAL.ITRTKC_S,SAL.ITRCYG_S,SAL.ITRCTG_S,");
		sql.append("SAL.ITRSLG_S,SAL.ITRCLS_S,SLT.SLTSRC,SLT.SLTTIO,SLT.SLTEBI,SLT.SLTODS,SLT.SLTTRC,SLT.SLTDRC,SLT.SLTFCC,SLT.SLTENR,");
		sql.append("SLT.SLTCIA,SLT.SLTPNR,case when SLT.SLTFOP is not null and SLT.SLTFOP !='' and SLT.SLTFOP!='0' then 'CC' else '' end,");
		sql.append("SLT.SLTFFP,SLT.SLTCRC,SLT.SLTARF,decode(SLT.SLTGPI,'G','G','Y','G',''),SLT.SLTGPP,SLT.SLTMTY,");
		sql.append("SLT.SLTMRS,SAL.CPNIRS,SAL.CPNFPM,SAL.CPNFD#,SAL.CPNPAD,SAL.CPNPDT,SAL.CPNPAT,SAL.CPNPMC,SAL.CPNPMF,SAL.CPNPOC,");
		sql.append("SAL.CPNPOF,SAL.CPNBUP,SAL.CPNBDS,SAL.CPNSTP,SAL.CPNCLS,SAL.CPNFB#,SAL.CPNSPA,SAL.CPNSPT,SLT.SLTFP1,SLT.SLTCA1,SLT.SLTPA1,");
		sql.append("nvl(SAL.CPNCM1,0),NVL(SAL.CPNCM2,0),nvl(SAL.CPNCNC,0),nvl(SAL.CPNYQC,0),");
		sql.append("SAL.CPNTKT,SAL.CPNMAR,SAL.CPNTTY,substr(sal.CPNFPT,0,8),SLT.SLTCTD,SLT.SLTBTH,SLT.SLTSUB,substr(SLT.SLTBTH,1,3),");
		sql.append("'CNY',SLT.SLTCTO,decode(SLT.SLTPRF,'784','H','O'),SAL.CPNGPC,SAL.CPNGPU,SLT.SLTDID,nvl(SAL.CPNCU1,0),nvl(SAL.CPNCU2,0),sal.cpnrec ");
		sql.append(" from DWS_PSALCPNM_20181018 sal ");
		sql.append(" left join (select * from NGDW.DWB_PSLTTKTD where SLTSTY<>'C' AND SLTPRM > '").append(last6).append("' and SLTPRM <= '").append(last0).append("') slt on sal.CPNTKT=slt.SLTTKT");
		sql.append(" where sal.SALIND='D' and (sal.cpnrec<to_date('").append(monthLine).append("','yyyyMMdd hh24:mi:ss') or nvl(length(slt.slttkt),0)>0)");
		sql.append(" ) ");
		dwpPsalcpnmRepository.sqlExecute(sql.toString());
		
		sql=new StringBuffer();
		sql.append("delete from DWS_PSALCPNM_20181018 s ");
		sql.append(" where exists(select 1 from DOS_PSALCPNM d where s.cpntkt=d.saltkt and s.cpncpn=d.tktcpn ");
		sql.append(" and s.cpnprf=d.tktprf) ");
		dwpPsalcpnmRepository.sqlExecute(sql.toString());
	}
	
	/**
	 * @description 国内： 外航分摊数据与主文件进行关联,关联的结果插入接口表
	 * @param Date日期   格式为(yyyyMMdd)的字符串
	 * @param userId， 用户ID
	 * @author zyr
	 */
	private void obok2Dos_d(String stime,String etime,String userId) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into dos_psalcpnm(/* 外航国内*/create_id,create_date,create_time,");
		sb.append("TKTHOI,CPNFPM,TKTTID,TKTPRF,TKTFRM,");
		sb.append("TKTTKT,SALTKT,TKTCPN,TKTSTY,");
		sb.append("TKTTKY,TKTLTK,TKTCJT,TKTCTS,TKTRMC,");
		sb.append("TKTPNR,TKTPTY,CPNFD#,CPNPDT,CPNPAT,");
		sb.append("CPNBUP,CPNBDS,CPNCLS,CPNPMC,CPNPMF,");
		sb.append("CPNPOC,CPNPOF,CPNFB#,TKTTUR,TKTCUR2,");
		sb.append("TKTDOI,TKTTCA,TKTAGT,TKTSRD,");
		sb.append("TKTPRM,TKTICT,TKTDSR,");
		sb.append("TKTVOI,CPNSTP,CPNTTY,TKTITY,");
		sb.append("TKTPC1,TKTPL1,TKTPU1,CPNCLC,CPNCLU,CPNOMC,CPNOMU,CPNOMC2,CPNOMU2,CPNATC,CPNBTC,CPNCTC,");
		sb.append("TKTCUR,CPNUAT,CPNUAI,");
		sb.append("ITRPOC_S,ITRPMC_S,CPNREC,TKTITI,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,ITRCPN,ITRTKS,");
		sb.append("ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,");
		sb.append("CPNSPT,CPNSPA,ITRCLS_S,CPNFPT,TKTCDT)");
		sb.append("select '").append(userId).append("',to_char(sysdate,'YYYYMMDD'),to_char(sysdate,'hh24miss'),");
		sb.append("decode(CPNPRF,'784','H','O'),CPNFPM,CPNTID,CPNPRF,CPNFRM,");
		sb.append("CPNTKT,CPNTKT,CPNCPN,CPNSTY,");
		sb.append("CPNTKY,CPNLTK,CPNCJT,CPNCTS,CPNRMC,");
		sb.append("CPNPNR,CPNPTY,CPNFD#,CPNPDT,CPNPAT,");
		sb.append("CPNBUP,CPNBDS,CPNCLS,CPNPMC,CPNPMF,");
		sb.append("CPNPOC,CPNPOF,CPNFB#,CPNTUR,'CNY',");
		sb.append("CPNDOI,CPNTCA,CPNAGT,CPNSRD,");
		sb.append("CPNPRM,CPNICT,CPNDSR,");
		sb.append("CPNINV,CPNSTP,CPNTTY,CPNITY,");
		sb.append("0,0,0,CPNGPC,CPNGPU,nvl(CPNCM1,0),nvl(CPNCU1,0),0,0,0,0,0,");
		sb.append("CPNCUR,CPNUAT,CPNUAI,");
		sb.append("ITRPOC_S,ITRPMC_S,CPNREC,CPNITI,CPNOCI,");
		sb.append("CPNUTI,CPNCTI,CPNCTO,ITRCPN,ITRTKS,");
		sb.append("ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,");
		sb.append("CPNSPT,CPNSPA,ITRCLS_S,substr(CPNFPT,0,8),CPNCDT");
		sb.append(" from ngdw.dwb_pobokcpnd obok");
		sb.append(" where obok.cpnrec>=to_date('").append(stime).append("','yyyymmdd hh24:mi:ss')");
		sb.append(" and obok.cpnrec<=to_date('").append(etime).append("','yyyymmdd hh24:mi:ss')");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}
	
	/**
	 * @description 国际： 当天的bok数据与近6个月的slt数据做匹配,关联的结果插入接口表
	 * @param Date 日期   格式为(yyyyMMdd)的字符串
	 * @param userId， 用户ID
	 * @author zyr
	 */
	private void bokSlt2Dos_i(String stime,String etime,String last6,String last2,String last0,String userId) {
		StringBuffer sql=new StringBuffer();
		sql.append("insert into DOS_PSALCPNM (id ,create_id  ,create_date,create_time, ");
		sql.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTICT,TKTTID,TKTHOI,TKTPRF,TKTFRM,TKTTKT,");
		sql.append("TKTCPN,TKTSTY,TKTTKY,TKTCUR,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,");
		sql.append("TKTOTK,TKTPFM,TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,TKTPIS,");
		sql.append("TKTOAG,TKTPTY,TKTPGN,CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,");
		sql.append("TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,TKTUTI,TKTCTI,CPNUAI,");
		sql.append("CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,ITRCLS_S,");
		sql.append("TKTDSR,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,TKTPNR,TKTVOI,");
		sql.append("TKTFPN,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,CPNFPM,CPNFPT,");
		sql.append("HBTIND,HBTGRP,HBTSIX, ");//hbt
		sql.append("CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,CPNPOF,CPNBUP,CPNBDS,");
		sql.append("CPNSTP,CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,TKTCA1,TKTAN1,TKTPC1,");
		sql.append("TKTPL1,TKTPU1,TKTUA1,CPNOMC,CPNOMU,CPNATC,CPNBTC,CPNCTC,SALTKT,");
		sql.append("TKTUEC,TKTBTH,TKTSUB,TKTBTS,TKTCTO,CPNCLC,CPNCLU,TKTDID,CPNOMU2,CPNOMC2,TKTCUR2,TKTETP,cpnrec )");
		sql.append("(select  SYS_GUID (),'").append(userId).append("', to_char(sysdate,'YYYYMMDD'), to_char(sysdate,'hhmmss') ,");
		sql.append("BOK.cpnprm,SLT.TKTSRD,SLT.TKTDOI,SLT.TKTVFD,SLT.TKTICT,bok.cpntid,SLT.TKTHOI,bok.cpnprf,SLT.TKTFRM,SLT.TKTTKT,");
		sql.append("bok.cpncpn,BOK.CPNSTY,decode(SLT.TKTTKY,'T','P',SLT.TKTTKY),SLT.TKTCUR,SLT.TKTSRN,");
		sql.append("decode(SLT.TKTCJT,NULL,SLT.TKTTKT,'',SLT.TKTTKT,SLT.TKTLTK),");
		sql.append("SLT.TKTCJT,decode(SLT.TKTCTS,null,'1','','1',SLT.TKTCTS),SLT.TKTOPR,SLT.TKTOKT,");
		sql.append("SLT.TKTOTK,SLT.TKTPFM,SLT.TKTPLK,SLT.TKTPCN,SLT.TKTPCU,SLT.TKTJCN,SLT.TKTAGT,SLT.TKTTCA,SLT.TKTCLT,SLT.TKTPIS,");
		sql.append("SLT.TKTOAG,SLT.TKTPTY,SLT.TKTPGN,BOK.CPNFPL,BOK.CPNFPU,BOK.CPNMAR,BOK.CPNCKM,BOK.CPNCRM,BOK.CPNLRD,BOK.CPNFZD,");
		sql.append("SLT.TKTITY,BOK.CPNTTY,SLT.TKTITM,SLT.TKTITC,SLT.TKTITI,SLT.TKTOCT,SLT.TKTOCI,SLT.TKTUTI,SLT.TKTCTI,BOK.CPNUAI,");
		sql.append("BOK.CPNUAT,BOK.ITRCPN,BOK.ITRTKS,BOK.ITRPOC_S,BOK.ITRPMC_S,BOK.ITRTKC_S,BOK.ITRCYG_S,BOK.ITRCTG_S,BOK.ITRSLG_S,BOK.ITRCLS_S,");
		sql.append("SLT.TKTDSR,SLT.TKTEBI,SLT.TKTGDS,SLT.TKTTUR,SLT.TKTPDC,SLT.TKTFCC,SLT.TKTRMC,SLT.TKTACE,SLT.TKTPNR,SLT.TKTVOI,");
		sql.append("SLT.TKTFPN,SLT.TKTGRP,SLT.TKTNRI,SLT.TKTGPI,SLT.TKTGPP,SLT.TKTTYC,SLT.TKTIRC,BOK.CPNIRS,BOK.CPNFPM,substr(bok.CPNFPT,0,8),");
		sql.append("ngdw.pkg_psal.f_get_flttyp(SLT.TKTITY),ngdw.pkg_psal.f_get_grp(SLT.TKTITY),ngdw.pkg_psal.f_get_six(BOK.CPNBUP,BOK.CPNBDS), ");//hbt
		sql.append("BOK.CPNFD#,BOK.CPNPAD,BOK.CPNPDT,BOK.CPNPAT,BOK.CPNPMC,BOK.CPNPMF,BOK.CPNPOC,BOK.CPNPOF,BOK.CPNBUP,BOK.CPNBDS,");
		sql.append("BOK.CPNSTP,BOK.CPNCLS,BOK.CPNFB#,BOK.CPNSPA,BOK.CPNSPT,SLT.TKTFP1,SLT.TKTCN1,SLT.TKTCA1,SLT.TKTAN1,");
		sql.append("case when SLT.TKTPC1 =null then 0 when SLT.TKTPC1='' then 0 when SLT.TKTSTY = 'R' or SLT.TKTSTY='A' then -abs(SLT.TKTPC1) else SLT.TKTPC1 end, ");
		sql.append("case when SLT.TKTPL1 =null then 0 when SLT.TKTPL1='' then 0 when SLT.TKTSTY = 'R' or SLT.TKTSTY='A' then -abs(SLT.TKTPL1) else SLT.TKTPL1 end ,");
		sql.append("case when SLT.TKTPU1 =null then 0 when SLT.TKTPU1='' then 0 when SLT.TKTSTY = 'R' or SLT.TKTSTY='A' then -abs(SLT.TKTPU1) else SLT.TKTPU1 end ,");
		sql.append("SLT.TKTUA1,nvl(BOK.CPNCM1,0),nvl(BOK.CPNCU1,0),nvl(BOK.CPNBEV,0),nvl(BOK.CPNCNC,0),");
		sql.append("nvl(BOK.CPNYQC,0),BOK.CPNTKT,SLT.TKTUEC,SLT.TKTBTH,SLT.TKTSUB,SLT.TKTBTS,SLT.TKTCTO,BOK.CPNGPC,BOK.CPNGPU,SLT.TKTDID, ");
		sql.append("nvl(BOK.CPNCU2,0),NVL(BOK.CPNCM2,0),SLT.TKTCUR2,SLT.TKTETP,bok.cpnrec ");
		sql.append(" from NGDW.DWB_PBOKCPNI bok");
		sql.append(" left join (select * from NGDW.DWB_PSLTTKTI where TKTSTY<>'C' AND TKTPRM>'").append(last6).append("' and TKTPRM<='").append(last0).append("') slt on bok.CPNTKT=slt.TKTTKT ");
		sql.append(" where  bok.CPNREC >= to_date('").append(stime).append("','yyyy-mm-dd hh24:mi:ss') ");
		sql.append(" and bok.CPNREC <= to_date('").append(etime).append("','yyyy-mm-dd hh24:mi:ss') ");
		sql.append(" and bok.cpnfpm > ").append(last2).append(" and bok.cpnfpm <=").append(last0);
		sql.append(" ) ");
		dwpPsalcpnmRepository.sqlExecute(sql.toString());
	}
	
	/**
	 * @description 国际： 中间表（DWS_PSALCPNM）的数据与近6个月的slt数据做匹配,关联的结果插入接口表
	 * @param Date 日期   格式为(yyyyMMdd)的字符串
	 * @param userId， 用户ID
	 * @author zyr
	 */
	private void salSlt2Dos_i(String last6,String last0,String monthLine,String userId) {
		StringBuffer sql=new StringBuffer();
		sql.append("insert into DOS_PSALCPNM (id ,create_id  ,create_date,create_time, ");
		sql.append("TKTPRM,TKTSRD,TKTDOI,TKTVFD,TKTICT,TKTTID,TKTHOI,TKTPRF,TKTFRM,TKTTKT,");
		sql.append("TKTCPN,TKTSTY,TKTTKY,TKTCUR,TKTSRN,TKTLTK,TKTCJT,TKTCTS,TKTOPR,TKTOKT,");
		sql.append("TKTOTK,TKTPFM,TKTPLK,TKTPCN,TKTPCU,TKTJCN,TKTAGT,TKTTCA,TKTCLT,TKTPIS,");
		sql.append("TKTOAG,TKTPTY,TKTPGN,CPNFPL,CPNFPU,CPNMAR,CPNCKM,CPNCRM,CPNLRD,CPNFZD,");
		sql.append("TKTITY,CPNTTY,TKTITM,TKTITC,TKTITI,TKTOCT,TKTOCI,TKTUTI,TKTCTI,CPNUAI,");
		sql.append("CPNUAT,ITRCPN,ITRTKS,ITRPOC_S,ITRPMC_S,ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,ITRCLS_S,");
		sql.append("TKTDSR,TKTEBI,TKTGDS,TKTTUR,TKTPDC,TKTFCC,TKTRMC,TKTACE,TKTPNR,TKTVOI,");
		sql.append("TKTFPN,TKTGRP,TKTNRI,TKTGPI,TKTGPP,TKTTYC,TKTMRS,CPNIRC,CPNFPM,CPNFPT,");
		sql.append("HBTIND,HBTGRP,HBTSIX, ");//hbt
		sql.append("CPNFD#,CPNPAD,CPNPDT,CPNPAT,CPNPMC,CPNPMF,CPNPOC,CPNPOF,CPNBUP,CPNBDS,");
		sql.append("CPNSTP,CPNCLS,CPNFB#,CPNSPA,CPNSPT,TKTFP1,TKTCN1,TKTCA1,TKTAN1,TKTPC1,");
		sql.append("TKTPL1,TKTPU1,TKTUA1,CPNOMC,CPNOMU,CPNOMC2,CPNOMU2,CPNATC,CPNBTC,");
		sql.append("CPNCTC,SALTKT,TKTUEC,TKTBTH,TKTSUB,TKTBTS,TKTCTO,CPNCLC,CPNCLU,TKTDID,TKTCUR2,cpnrec )");
		sql.append("(select  SYS_GUID (),'").append(userId).append("', to_char(sysdate,'YYYYMMDD'), to_char(sysdate,'hhmmss') ,");
		sql.append("SAL.cpnprm,SLT.TKTSRD,SLT.TKTDOI,SLT.TKTVFD,SLT.TKTICT,sal.cpntid,SLT.TKTHOI,sal.cpnprf,SLT.TKTFRM,SLT.TKTTKT,");
		sql.append("SAL.CPNCPN,sal.cpnsty,decode(SLT.TKTTKY,'T','P',SLT.TKTTKY),SLT.TKTCUR,SLT.TKTSRN,decode(SLT.TKTCJT,NULL,SLT.TKTTKT,'',SLT.TKTTKT,SLT.TKTLTK),SLT.TKTCJT,decode(SLT.TKTCTS,null,'1','','1',SLT.TKTCTS),SLT.TKTOPR,SLT.TKTOKT,");
		sql.append("SLT.TKTOTK,SLT.TKTPFM,SLT.TKTPLK,SLT.TKTPCN,SLT.TKTPCU,SLT.TKTJCN,SLT.TKTAGT,SLT.TKTTCA,SLT.TKTCLT,SLT.TKTPIS,");
		sql.append("SLT.TKTOAG,SLT.TKTPTY,SLT.TKTPGN,SAL.CPNFPL,SAL.CPNFPU,SAL.CPNMAR,SAL.CPNCKM,SAL.CPNCRM,SAL.CPNLRD,SAL.CPNFZD,");
		sql.append("SLT.TKTITY,SAL.CPNTTY,SLT.TKTITM,SLT.TKTITC,SLT.TKTITI,SLT.TKTOCT,SLT.TKTOCI,SLT.TKTUTI,SLT.TKTCTI,SAL.CPNUAI,");
		sql.append("SAL.CPNUAT,SAL.ITRCPN,SAL.ITRTKS,SAL.ITRPOC_S,SAL.ITRPMC_S,SAL.ITRTKC_S,SAL.ITRCYG_S,SAL.ITRCTG_S,SAL.ITRSLG_S,SAL.ITRCLS_S,");
		sql.append("SLT.TKTDSR,SLT.TKTEBI,SLT.TKTGDS,SLT.TKTTUR,SLT.TKTPDC,SLT.TKTFCC,SLT.TKTRMC,SLT.TKTACE,SLT.TKTPNR,SLT.TKTVOI,");
		sql.append("SLT.TKTFPN,SLT.TKTGRP,SLT.TKTNRI,SLT.TKTGPI,SLT.TKTGPP,SLT.TKTTYC,SLT.TKTIRC,SAL.CPNIRS,SAL.CPNFPM,substr(sal.CPNFPT,0,8),");
		sql.append("ngdw.pkg_psal.f_get_flttyp(SLT.TKTITY),ngdw.pkg_psal.f_get_grp(SLT.TKTITY),ngdw.pkg_psal.f_get_six(sal.CPNBUP,sal.CPNBDS), ");//hbt
		sql.append("SAL.CPNFD#,SAL.CPNPAD,SAL.CPNPDT,SAL.CPNPAT,SAL.CPNPMC,SAL.CPNPMF,SAL.CPNPOC,SAL.CPNPOF,SAL.CPNBUP,SAL.CPNBDS,");
		sql.append("SAL.CPNSTP,SAL.CPNCLS,SAL.CPNFB#,SAL.CPNSPA,SAL.CPNSPT,SLT.TKTFP1,SLT.TKTCN1,SLT.TKTCA1,SLT.TKTAN1,");
		sql.append("case when SLT.TKTPC1 =null then 0 when SLT.TKTPC1='' then 0 when SLT.TKTSTY = 'R' or SLT.TKTSTY='A' then -abs(SLT.TKTPC1) else SLT.TKTPC1 end ,");
		sql.append("case when SLT.TKTPL1 =null then 0 when SLT.TKTPL1='' then 0 when SLT.TKTSTY = 'R' or SLT.TKTSTY='A' then -abs(SLT.TKTPL1) else SLT.TKTPL1 end ,");
		sql.append("case when SLT.TKTPU1 =null then 0 when SLT.TKTPU1='' then 0 when SLT.TKTSTY = 'R' or SLT.TKTSTY='A' then -abs(SLT.TKTPU1) else SLT.TKTPU1 end,");
		sql.append("SLT.TKTUA1,nvl(sal.CPNCM1,0),nvl(sal.CPNCU1,0),NVL(sal.CPNCM2,0),nvl(sal.CPNCU2,0),nvl(SAL.CPNBEV,0),nvl(SAL.CPNCNC,0),");
		sql.append("nvl(SAL.CPNYQC,0),SAL.CPNTKT,SLT.TKTUEC,SLT.TKTBTH,SLT.TKTSUB,SLT.TKTBTS,SLT.TKTCTO,SAL.CPNGPC,SAL.CPNGPU,SLT.TKTDID,SLT.TKTCUR2,sal.cpnrec ");
		sql.append(" from DWS_PSALCPNM_20181018 sal ");
		sql.append(" left join (select * from NGDW.DWB_PSLTTKTI where TKTSTY<>'C' AND TKTPRM>'").append(last6).append("' and TKTPRM<='").append(last0).append("') slt on SAL.CPNTKT=slt.TKTTKT");
		sql.append(" where sal.SALIND='I' and (sal.cpnrec<to_date('").append(monthLine).append("','yyyy-mm-dd hh24:mi:ss') or nvl(length(slt.tkttkt),0)>0)");
		sql.append(" )");
		dwpPsalcpnmRepository.sqlExecute(sql.toString());
		
		sql=new StringBuffer();
		sql.append("delete from DWS_PSALCPNM_20181018 s ");
		sql.append(" where exists(select 1 from DOS_PSALCPNM d where s.cpntkt=d.saltkt and s.cpncpn=d.tktcpn ");
		sql.append(" and s.cpnprf=d.tktprf) ");
		dwpPsalcpnmRepository.sqlExecute(sql.toString());
	}
	
	/**
	 * @description 国际： 外航分摊数据与主文件进行关联,关联的结果插入接口表
	 * @param Date 日期   格式为(yyyyMMdd)的字符串
	 * @param userId， 用户ID
	 * @author zyr
	 */
	private void obok2Dos_i(String stime,String etime,String userId) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into dos_psalcpnm(/* 外航国际*/create_id,create_date,create_time,");
		sb.append("TKTHOI,CPNFPM,TKTTID,TKTPRF,TKTFRM,");
		sb.append("TKTTKT,SALTKT,TKTCPN,TKTSTY,");
		sb.append("TKTTKY,TKTLTK,TKTCJT,TKTCTS,TKTRMC,");
		sb.append("TKTPNR,TKTPTY,CPNFD#,CPNPDT,CPNPAT,");
		sb.append("CPNBUP,CPNBDS,CPNCLS,CPNPMC,CPNPMF,");
		sb.append("CPNPOC,CPNPOF,CPNFB#,TKTTUR,");
		sb.append("TKTDOI,TKTTCA,TKTAGT,TKTSRD,");
		sb.append("TKTPRM,TKTICT,TKTDSR,");
		sb.append("TKTVOI,CPNSTP,CPNTTY,TKTITY,");
		sb.append("TKTPC1,TKTPL1,TKTPU1,CPNCLC,CPNCLU,CPNOMC,CPNOMU,CPNOMC2,CPNOMU2,CPNATC,CPNBTC,CPNCTC,");
		sb.append("TKTCUR,CPNUAT,CPNUAI,");
		sb.append("ITRPOC_S,ITRPMC_S,CPNREC,TKTITI,TKTOCI,");
		sb.append("TKTUTI,TKTCTI,TKTCTO,ITRCPN,ITRTKS,");
		sb.append("ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,");
		sb.append("CPNSPT,CPNSPA,ITRCLS_S,CPNFPT,TKTCDT)");
		sb.append("select '").append(userId).append("',to_char(sysdate,'YYYYMMDD'),to_char(sysdate,'hh24miss'),");
		sb.append("decode(CPNPRF,'784','H','O'),CPNFPM,CPNTID,CPNPRF,CPNFRM,");
		sb.append("CPNTKT,CPNTKT,CPNCPN,CPNSTY,");
		sb.append("decode(CPNTKY,'T','P',CPNTKY),decode(CPNLTK,null,cpntkt,CPNLTK),CPNCJT,decode(CPNCTS,null,1,CPNCTS),CPNRMC,");
		sb.append("CPNPNR,CPNPTY,CPNFD#,CPNPDT,CPNPAT,");
		sb.append("CPNBUP,CPNBDS,CPNCLS,CPNPMC,CPNPMF,");
		sb.append("CPNPOC,CPNPOF,CPNFB#,CPNTUR,");
		sb.append("CPNDOI,CPNTCA,CPNAGT,CPNSRD,");
		sb.append("CPNPRM,CPNICT,CPNDSR,");
		sb.append("CPNINV,CPNSTP,CPNTTY,CPNITY,");
		sb.append("0,0,0,CPNGPC,CPNGPU,nvl(CPNCM1,0),nvl(CPNCU1,0),0,0,0,0,0,");
		sb.append("CPNCUR,CPNUAT,CPNUAI,");
		sb.append("ITRPOC_S,ITRPMC_S,CPNREC,CPNITI,CPNOCI,");
		sb.append("CPNUTI,CPNCTI,CPNCTO,ITRCPN,ITRTKS,");
		sb.append("ITRTKC_S,ITRCYG_S,ITRCTG_S,ITRSLG_S,");
		sb.append("CPNSPT,CPNSPA,ITRCLS_S,substr(CPNFPT,0,8),CPNCDT");
		sb.append(" from ngdw.dwb_pobokcpni obok");
		sb.append(" where obok.cpnrec>=to_date('").append(stime).append("','yyyymmdd hh24:mi:ss')");
		sb.append(" and obok.cpnrec<=to_date('").append(etime).append("','yyyymmdd hh24:mi:ss')");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}

	/**
	 * @description 清空表dwp_psalcpnm_roaming
	 * @author wjb
	 */
	private void cleanRoaming() {
		StringBuilder sb = new StringBuilder();
		sb.append("TRUNCATE TABLE dwp_psalcpnm_roaming");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}
	
	/**
	 * @description
	 * @param monthline 时间分割点，tp 本航国际bok，本航国内bok,  pid 随机数   userId 用户id
	 * @author wjb
	 */
	private void bokLog(String monthline,String tp,String pid,String userId) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into dwp_psalcpnm_roaming(CREATE_ID,CREATE_DATE,CREATE_TIME,PID,SRCDES,CPNREC,INTODWP,INTODWS)");
		sb.append("select '").append(userId).append("',to_char(sysdate,'yyyyMMdd'),to_char(sysdate,'hh24miss'),");
		sb.append("'").append(pid).append("'  PID,'").append(tp).append("'  SRC,");
		sb.append("cpnrec 接收时间,");
		sb.append("sum(case when s.cpnrec<to_date('").append(monthline).append("','yyyyMMdd hh24:mi:ss') or s.tkttkt=s.saltkt then 1 else 0 end) 基库,");
		sb.append("sum(case when s.cpnrec>=to_date('").append(monthline).append("','yyyyMMdd hh24:mi:ss') and nvl(length(s.tkttkt),0)=0 then 1 else 0 end) 中间表");
		sb.append(" from dos_psalcpnm s group by cpnrec");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
		
		roaming2detail( tp, pid);
	}
	/**
	 * @description
	 * @param tp 本航国际中间表，本航国内中间表,  pid 随机数   userId 用户id
	 * @author wjb
	 */
	private void salLog(String tp,String pid,String userId) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into dwp_psalcpnm_roaming(CREATE_ID,CREATE_DATE,CREATE_TIME,PID,SRCDES,CPNREC,INTODWP,INTODWS)");
		sb.append("select '").append(userId).append("',to_char(sysdate,'yyyyMMdd'),to_char(sysdate,'hh24miss'),");
		sb.append("'").append(pid).append("'  PID,'").append(tp).append("'  SRC,");
		sb.append("cpnrec 接收时间,");
		sb.append("count(1) 基库,");
		sb.append("-1*count(1) 中间表");
		sb.append(" from dos_psalcpnm s group by cpnrec");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
		
		roaming2detailRollup( tp, pid);
	}
	/**
	 * @description
	 * @param tp 外航国际obok，外航国内obok,  pid 随机数   userId 用户id
	 * @author wjb
	 */
	private void obokLog(String tp,String pid,String userId) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into dwp_psalcpnm_roaming(CREATE_ID,CREATE_DATE,CREATE_TIME,PID,SRCDES,CPNREC,INTODWP,INTODWS)");
		sb.append("select '").append(userId).append("',to_char(sysdate,'yyyyMMdd'),to_char(sysdate,'hh24miss'),");
		sb.append("'").append(pid).append("'  PID,'").append(tp).append("'  SRC,");
		sb.append("cpnrec 接收时间,");
		sb.append("count(1) 基库,");
		sb.append("0 中间表");
		sb.append(" from dos_psalcpnm s group by cpnrec");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
		
		roaming2detailRollup( tp, pid);
	}
	/**
	 * @description
	 * @param   pid 随机数  
	 * @author wjb
	 */
	private void roaming2detail(String tp,String pid) {
		StringBuilder sb = new StringBuilder();
		sb.append("MERGE INTO dwp_psalcpnm_detail s");
		sb.append(" USING (SELECT cpnrec,sum(nvl(INTODWP,0)) AS mathched,sum(nvl(INTODWS,0)) as unmatched ");
		sb.append(" FROM dwp_psalcpnm_roaming ");
		sb.append(" where pid ='").append(pid).append("' and SRCDES='").append(tp).append("' group by cpnrec) d");
		sb.append(" ON (s.cpnrec=d.cpnrec)");
		sb.append(" WHEN MATCHED THEN");
		sb.append(" UPDATE SET s.MATHCHED = d.mathched,s.unmatched=d.unmatched ");
		sb.append(" WHEN NOT MATCHED THEN ");
		sb.append(" INSERT(s.cpnrec,s.MATHCHED,s.unmatched) VALUES(d.cpnrec,d.mathched,d.unmatched)");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}
	
	/**
	 * @description
	 * @param   pid 随机数  
	 * @author wjb
	 */
	private void roaming2detailRollup(String tp,String pid) {
		StringBuilder sb = new StringBuilder();
		sb.append("MERGE INTO dwp_psalcpnm_detail s");
		sb.append(" USING (SELECT cpnrec,sum(nvl(INTODWP,0)) AS mathched,sum(nvl(INTODWS,0)) as unmatched ");
		sb.append(" FROM dwp_psalcpnm_roaming ");
		sb.append(" where pid ='").append(pid).append("' and SRCDES='").append(tp).append("' group by cpnrec) d");
		sb.append(" ON (s.cpnrec=d.cpnrec)");
		sb.append(" WHEN MATCHED THEN");
		sb.append(" UPDATE SET s.MATHCHED = s.MATHCHED+d.mathched,s.unmatched=s.unmatched+d.unmatched ");
		sb.append(" WHEN NOT MATCHED THEN ");
		sb.append(" INSERT(s.cpnrec,s.MATHCHED,s.unmatched) VALUES(d.cpnrec,d.mathched,d.unmatched)");
		dwpPsalcpnmRepository.sqlExecute(sb.toString());
	}
}
