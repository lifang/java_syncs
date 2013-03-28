package com.comdosoft.serverReadZip;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadFile {

	public static void main(String[] args) {
		// /opt/projects/lantan_official/public/zip_dirs
		readFile("//opt//projects//lantan_official//public/zip_dirs");
	}

	public static void readFile(String filePach) {
		String fileMonth = "";
		String fileDay = "";
		SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
		String date=df.format(new Date());
		File month = new File(filePach);
		if (month.isDirectory()) {
			String[] monthList = month.list();
			for (int i = 0; i < monthList.length; i++) {
				if(date.substring(0, 7).equals(monthList[i])){
					fileMonth = filePach + "//" + monthList[i];
					File day = new File(fileMonth);
					if(day.isDirectory()){
						String[] dayList = day.list();
						for (int j = 0; j < dayList.length; j++) {
							if(date.equals(dayList[j])){
								fileDay = fileMonth + "//" + dayList[j];
								File dayFile = new File(fileDay);
								if(dayFile.isDirectory()){
									String[] dayStr = dayFile.list();
									for (int k = 0; k < dayStr.length; k++) {
										ReadZip.loadZipFile(fileDay+"//"+dayStr[k]);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
