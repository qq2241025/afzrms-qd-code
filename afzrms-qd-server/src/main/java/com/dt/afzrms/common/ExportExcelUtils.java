package com.dt.afzrms.common;


import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.vo.AlarmLocrecordVo;
import com.dt.afzrms.vo.TjDeptAlarmVo;
import com.dt.afzrms.vo.TjTermAlarmVo;
import com.dt.afzrms.vo.TjTermOnofflineVo;
import com.dt.afzrms.vo.TjTermOperationVo;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class ExportExcelUtils {
	
	//导出超速报警查询【"序号","车牌号","设备序列号","所属部门","SIM号码", "报警时间", "报警时间","行驶车速【km/h】","超速阀值【km/h】","行驶方向"】
	public static void exportOverspeedAlarm(String sheetName,String[] header,List<AlarmLocrecordVo> result,OutputStream out){
		WritableWorkbook wwb = null;  
		try {
			wwb = Workbook.createWorkbook(out);
			// 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表 
			WritableSheet ws = wwb.createSheet(sheetName, 0);  
			//创建单元格样式  
			WritableCellFormat wcf = getWritableSheet();
			
			ws.setColumnView(0, 5);
			ws.setColumnView(1, 20);
			ws.setColumnView(2, 10);
			ws.setColumnView(3, 14);
			ws.setColumnView(4, 14);
			ws.setColumnView(5, 18);
			ws.setColumnView(6, 18);
			ws.setColumnView(7, 18);
			ws.setColumnView(8, 18);
			
			for (int i = 0; i < header.length; i++) {
				String title = header[i];
				ws.addCell(new Label(i, 0,title, wcf));
			}
			//写入表格数据
			if(result !=null && result.size() >0){
				for (int i = 0; i < result.size(); i++) {
					AlarmLocrecordVo dept= result.get(i); 
					ws.addCell(new Label(0, i + 1, i + 1+"", wcf));//序号 
					ws.addCell(new Label(1, i + 1, dept.getTermName(), wcf)); //"车牌号
					ws.addCell(new Label(2, i + 1, dept.getDeviceId(), wcf)); //设备序列号
					ws.addCell(new Label(3, i + 1, dept.getDeptName()+"", wcf));//所属部门
					ws.addCell(new Label(4, i + 1, dept.getSimcard()+"", wcf));//sim
					ws.addCell(new Label(5, i + 1,dept.getGpsTime()!=null?dept.getGpsTime():"", wcf));//报警时间
					ws.addCell(new Label(6, i + 1,saveTwoNum(dept.getSpeed())+"km/h", wcf));//行驶车速
					ws.addCell(new Label(7, i + 1,dept.getSpeedThreshold()+"km/h", wcf));//超速阀值
					ws.addCell(new Label(8, i + 1,getGPSDirect(dept.getDirection()), wcf));//行驶方向
				}
			}
			//写入Exel工作表  
			wwb.write();  
			wwb.close();
			out.flush();
			out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	//导出区域报警查询【"序号","车牌号","所属部门","SIM号码", "报警时间", "行驶车速【km/h】","区域类型","行驶方向"】
	public static void exportAreaAlarm(String sheetName,String[] header,List<AlarmLocrecordVo> result,OutputStream out){
		WritableWorkbook wwb = null;  
		try {
			wwb = Workbook.createWorkbook(out);
	        // 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表 
	        WritableSheet ws = wwb.createSheet(sheetName, 0);  
	        //创建单元格样式  
	        WritableCellFormat wcf = getWritableSheet();
	        
	        ws.setColumnView(0, 5);
	        ws.setColumnView(1, 20);
	        ws.setColumnView(2, 10);
	        ws.setColumnView(3, 14);
	        ws.setColumnView(4, 14);
	        ws.setColumnView(5, 18);
	        ws.setColumnView(6, 18);
	        ws.setColumnView(7, 18);
	        
            for (int i = 0; i < header.length; i++) {
			  String title = header[i];
	          ws.addCell(new Label(i, 0,title, wcf));
	        }
            //写入表格数据
		    if(result !=null && result.size() >0){
		       for (int i = 0; i < result.size(); i++) {
		    	    AlarmLocrecordVo dept= result.get(i); 
		    	    ws.addCell(new Label(0, i + 1, i + 1+"", wcf));//序号 
		    	    ws.addCell(new Label(1, i + 1, dept.getTermName(), wcf)); //"车牌号
	                ws.addCell(new Label(2, i + 1, dept.getDeptName()+"", wcf));//所属部门
	                ws.addCell(new Label(3, i + 1, dept.getSimcard()+"", wcf));//sim
	                ws.addCell(new Label(4, i + 1,dept.getGpsTime()!=null?dept.getGpsTime():"", wcf));//报警时间
	                ws.addCell(new Label(5, i + 1,saveTwoNum(dept.getSpeed())+"km/h", wcf));//行驶车速
	                ws.addCell(new Label(6, i + 1,getAlaramTypeStr(dept.getAlarmSubType()), wcf));//区域类型
	                ws.addCell(new Label(7, i + 1,getGPSDirect(dept.getDirection()), wcf));//行驶方向
			   }
		    }
		    //写入Exel工作表  
            wwb.write();  
            wwb.close();
            out.flush();
            out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} 
	}
	

	//导出车辆运行统计【"序号","统计时间","车牌号","行驶有效时间","行驶里程", "最大速度时间点", "最大速度", "平均速度"】
	public static void exportTermOperation(String sheetName,String[] header,List<TjTermOperationVo> result,OutputStream out){
		WritableWorkbook wwb = null;  
		try {
			wwb = Workbook.createWorkbook(out);
	        // 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表 
	        WritableSheet ws = wwb.createSheet(sheetName, 0);  
	        //创建单元格样式  
	        WritableCellFormat wcf = getWritableSheet();
	        
	        ws.setColumnView(0, 5);
	        ws.setColumnView(1, 20);
	        ws.setColumnView(2, 10);
	        ws.setColumnView(3, 14);
	        ws.setColumnView(4, 14);
	        ws.setColumnView(5, 18);
	        ws.setColumnView(6, 18);
	        ws.setColumnView(7, 18);
	        
	        
            for (int i = 0; i < header.length; i++) {
			  String title = header[i];
	          ws.addCell(new Label(i, 0,title, wcf));
	        }
            //写入表格数据
		    if(result !=null && result.size() >0){
		       for (int i = 0; i < result.size(); i++) {
		    	    TjTermOperationVo dept= result.get(i); 
		    	    ws.addCell(new Label(0, i + 1, i + 1+"", wcf));//序号 
		    	    ws.addCell(new Label(1, i + 1, dept.getTjDate()!=null?DateUtil.dateTimeToStr(dept.getTjDate(), "yyyy-MM-dd hh:mm:ss"):"", wcf)); //统计时间
	                ws.addCell(new Label(2, i + 1, dept.getName(), wcf)); //"车牌号
	                ws.addCell(new Label(3, i + 1, getTimer(dept.getTravelTime())+"", wcf));//行驶有效时间
	                ws.addCell(new Label(4, i + 1, dept.getDistance()+"", wcf));//行驶里程
	                ws.addCell(new Label(5, i + 1,dept.getMaxSpeedTime()!=null?DateUtil.dateTimeToStr(dept.getMaxSpeedTime(), "yyyy-MM-dd hh:mm:ss"):"", wcf));//最大速度时间点
	                ws.addCell(new Label(6, i + 1,saveTwoNum(dept.getMaxSpeed())+"km/h", wcf));//最大速度
	                ws.addCell(new Label(7, i + 1,saveTwoNum(dept.getAverageSpeed())+"km/h", wcf));//平均速度
			   }
		    }
		    //写入Exel工作表  
            wwb.write();  
            wwb.close();
            out.flush();
            out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//导出车辆违章统计【"序号","统计时间","车牌号","区域报警次数","超速报警次数", "区域超速报警次数"】
	public static void exportTermAlarm(String sheetName,String[] header,List<TjTermAlarmVo> result,OutputStream out){
		WritableWorkbook wwb = null;  
		try {
			wwb = Workbook.createWorkbook(out);
	        // 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表 
	        WritableSheet ws = wwb.createSheet(sheetName, 0);  
	        //创建单元格样式  
	        WritableCellFormat wcf = getWritableSheet();
	        
	        ws.setColumnView(0, 5);
	        ws.setColumnView(1, 20);
	        ws.setColumnView(2, 10);
	        ws.setColumnView(3, 14);
	        ws.setColumnView(4, 14);
	        ws.setColumnView(5, 18);
	        
	        
            for (int i = 0; i < header.length; i++) {
			  String title = header[i];
	          ws.addCell(new Label(i, 0,title, wcf));
	        }
            //写入表格数据
		    if(result !=null && result.size() >0){
		       for (int i = 0; i < result.size(); i++) {
		    	    TjTermAlarmVo dept= result.get(i); 
		    	    ws.addCell(new Label(0, i + 1, i + 1+"", wcf));//序号 
		    	    ws.addCell(new Label(1, i + 1, dept.getTjDate()!=null?DateUtil.dateTimeToStr(dept.getTjDate(), "yyyy-MM-dd hh:mm:ss"):"", wcf)); //统计时间
	                ws.addCell(new Label(2, i + 1, dept.getName(), wcf)); //"车牌号",
	                ws.addCell(new Label(3, i + 1, dept.getAreaAlarmCount()+"次", wcf));//"区域报警次数",
	                ws.addCell(new Label(4, i + 1, dept.getSpeedAlarmCount()+"次", wcf));//"超速报警次数",
	                ws.addCell(new Label(5, i + 1,dept.getAreaSpeedAlarmCount()+"次", wcf));//"区域超速报警次数",
			   }
		    }
		    //写入Exel工作表  
            wwb.write();  
            wwb.close();
            out.flush();
            out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//导出部门统计功能【"序号","统计时间","所属部门","区域报警次数","超速报警次数", "区域超速报警次数"】
	public static void exportDeptExcel(String sheetName,String[] header,List<TjDeptAlarmVo> result,OutputStream out){
		WritableWorkbook wwb = null;  
		try {
			wwb = Workbook.createWorkbook(out);
	        // 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表 
	        WritableSheet ws = wwb.createSheet(sheetName, 0);  
	        //创建单元格样式  
	        WritableCellFormat wcf = getWritableSheet();
	        
	        ws.setColumnView(0, 5);
	        ws.setColumnView(1, 20);
	        ws.setColumnView(2, 10);
	        ws.setColumnView(3, 14);
	        ws.setColumnView(4, 14);
	        ws.setColumnView(5, 18);
	        
            for (int i = 0; i < header.length; i++) {
			  String title = header[i];
	          ws.addCell(new Label(i, 0,title, wcf));
	        }
            //写入表格数据
		    if(result !=null && result.size() >0){
		       for (int i = 0; i < result.size(); i++) {
		    	    TjDeptAlarmVo dept= result.get(i); //序号
		    	    ws.addCell(new Label(0, i + 1, i + 1+"", wcf)); //统计时间
		    	    ws.addCell(new Label(1, i + 1, dept.getTjDate()!=null?DateUtil.dateTimeToStr(dept.getTjDate(), "yyyy-MM-dd hh:mm:ss"):"", wcf)); //统计时间
	                ws.addCell(new Label(2, i + 1, dept.getName(), wcf)); //"所属部门",
	                ws.addCell(new Label(3, i + 1, dept.getAreaAlarmCount()+"次", wcf));//"区域报警次数",
	                ws.addCell(new Label(4, i + 1, dept.getSpeedAlarmCount()+"次", wcf));//"超速报警次数",
	                ws.addCell(new Label(5, i + 1,dept.getAreaSpeedAlarmCount()+"次", wcf));//"区域超速报警次数",
			   }
		    }
		    //写入Exel工作表  
            wwb.write();  
            wwb.close();
            out.flush();
            out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} 
	}
	
	//导出车辆在线离线统计【"序号","统计时间","离线车辆","在线车辆","离线率数"】
	public static void exportTermOnoffline(String sheetName,String[] header,List<TjTermOnofflineVo> result,OutputStream out){
		WritableWorkbook wwb = null;  
		try {
			wwb = Workbook.createWorkbook(out);
	        // 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表 
	        WritableSheet ws = wwb.createSheet(sheetName, 0);  
	        //创建单元格样式  
	        WritableCellFormat wcf = getWritableSheet();
	        
	        ws.setColumnView(0, 5);
	        ws.setColumnView(1, 20);
	        ws.setColumnView(2, 10);
	        ws.setColumnView(3, 14);
	        ws.setColumnView(4, 14);
	        
	        for (int i = 0; i < header.length; i++) {
			   String title = header[i];
	           ws.addCell(new Label(i, 0,title, wcf));
	        }
	        //写入表格数据
		    if(result !=null && result.size() >0){
		       for (int i = 0; i < result.size(); i++) {
		    	    TjTermOnofflineVo dept= result.get(i); 
		    	    ws.addCell(new Label(0, i + 1, i + 1+"", wcf)); ///序号
		    	    ws.addCell(new Label(1, i + 1, dept.getTjDate()!=null?DateUtil.dateTimeToStr(dept.getTjDate(), "yyyy-MM-dd hh:mm:ss"):"", wcf)); //统计时间
		    	    ws.addCell(new Label(2, i + 1, dept.getOnlineCount()+"", wcf)); //离线车辆
	                ws.addCell(new Label(3, i + 1, dept.getOfflineCount()+"", wcf)); //在线车辆
	                ws.addCell(new Label(4, i + 1, Math.round(dept.getOnlineRate() * 100)+"%", wcf));//离线率
		       }
		    }
		    //写入Exel工作表  
	        wwb.write();  
	        wwb.close();
	        out.flush();
	        out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	//导出综合查询统计【"序号","统计时间段","终端名称","行驶里程(km)","最大速度时间点","最大速度(km/h)","最小速度(km/h)","超速报警次数","区域报警次数"】
	public static void zhongheTongji(String sheetName,String[] header,List<Object[]> result, String beginTime, String endTime,OutputStream out){
		
		WritableWorkbook wwb = null;  
		try {
			wwb = Workbook.createWorkbook(out);
			// 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表 
			WritableSheet ws = wwb.createSheet(sheetName, 0);  
			//创建单元格样式  
			WritableCellFormat wcf = getWritableSheet();
			
			ws.setColumnView(0, 5);
			ws.setColumnView(1, 35);
			ws.setColumnView(2, 10);
			ws.setColumnView(3, 14);
			ws.setColumnView(4, 14);
			ws.setColumnView(5, 18);
			ws.setColumnView(6, 18);
			ws.setColumnView(7, 18);
			ws.setColumnView(8, 18);
			
			for (int i = 0; i < header.length; i++) {
				String title = header[i];
				ws.addCell(new Label(i, 0,title, wcf));
			}
			String dtime =beginTime +"---"+ endTime  ;
			//写入表格数据
			if(result !=null && result.size() >0){
				for (int i = 0; i < result.size(); i++) {
					Object[] dept= result.get(i); 
					ws.addCell(new Label(0, i + 1, i + 1+"", wcf));//序号 
					ws.addCell(new Label(1, i + 1, dtime, wcf)); //统计时间段
					ws.addCell(new Label(2, i + 1, dept[6]+"", wcf)); //终端名称
					ws.addCell(new Label(3, i + 1, dept[0]+"", wcf));//行驶里程(km)
					ws.addCell(new Label(4, i + 1, dept[7]+"", wcf));//最大速度时间点
					ws.addCell(new Label(5, i + 1,dept[2]+"", wcf));//最大速度(km/h)
					ws.addCell(new Label(6, i + 1,dept[3]+"", wcf));//最小速度(km/h)
					ws.addCell(new Label(7, i + 1,dept[4]+"", wcf));//超速报警次数
					ws.addCell(new Label(8, i + 1,dept[5]+"", wcf));//区域报警次数
				}
			}
			//写入Exel工作表  
			wwb.write();  
			wwb.close();
			out.flush();
			out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	private static WritableCellFormat getWritableSheet(){
		WritableCellFormat wcf = new WritableCellFormat();  
		try {
			wcf.setBackground(Colour.WHITE);
			wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			wcf.setAlignment(Alignment.LEFT);
		} catch (WriteException e) {
			e.printStackTrace();
		} 
		return wcf;
	}
	
	private static String getAlaramTypeStr(String type){
		String types = "";
		if("0".equals(type)){
			types = "进区域";
		}else if("1".equals(type)){
			types = "进区域";
		}else{
			types = "进出区域限速";
		} 
		return types;
	}
	
	
	//获取GPS方向
	private static String getGPSDirect(Float val){
		String result = "";
	    if ((val >= 338 && val <= 360) || (val >= 0 && val < 23)) {
	    	result ="正北";
		} else if (val >= 23 && val < 68) {
			result ="东北";
		} else if (val >= 68 && val < 113) {
			result ="东北";
		} else if (val >= 113 && val < 158) {
	    	result ="东南";
		} else if (val >= 158 && val < 203) {
	    	result ="正南";
		} else if (val >= 203 && val < 248) {
	    	result ="西南";
		} else if (val >= 248 && val < 293) {
	    	result ="正西";
		} else if (val >= 293 && val < 338) {
	    	result ="西北";
		} else {
	    	result ="未知方向";
		}
		return result;
	};
	 
	private static String saveTwoNum(float num){
		
		DecimalFormat df2 = new DecimalFormat("###.00");

		return df2.format(num);
	}
	
	//时间保持前段一致性
	private static String getTimer(int theTime){
		String result= "";
		int hour = 0;// 小时  
		int minu = 0;// 分 
		String sminu = "";
		if(theTime>=0 && theTime<10){
		     result = "0"+theTime+"分";
		}else{
		     if(theTime >= 60) { 
		    	 hour = Math.round(theTime/60); 
		    	 minu = Math.round(theTime%60); 
		    	 if(theTime % 60 ==0){
					 result= hour+"小时";
					 return result;
				 }
				 if(minu >= 0 && minu < 10){
					 sminu = "0"+minu+"分";
				 }else{
					 sminu = minu+"分";
				 }
				 if(hour >0 && hour< 10){
				     result= "0"+hour+"小时"+ sminu;
				 }else{
					 result= hour+"小时"+ sminu; 
				 }
			 }else{
			     if(theTime >= 0 && theTime < 10){
			    	 result = "0"+theTime+"分";
				 }else{
					 result = theTime+"分";
				 }
			 } 
		}
		return result; 
	}
	
}
