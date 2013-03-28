package com.comdosoft.serverReadZip;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReadZip {

	private static StringBuffer paramName = new StringBuffer();
	private static List<String[]> param = new ArrayList<>();
	private static int count = 0;
	private static int colNum = 0;
	private static int colCount = 0;
	private static boolean flag = false;
	private static boolean firstFlag = true;
	private static MysqlDAOImpl md = new MysqlDAOImpl();

	public static void loadZipFile(String zipname) {
		try {
			ZipInputStream zin = new ZipInputStream(
					new FileInputStream(zipname));
			ZipEntry entry;
			while ((entry = zin.getNextEntry()) != null) {
				String tableName = entry.getName().substring(0,
						entry.getName().length() - 4);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						zin));
				String s;
				while ((s = in.readLine()) != null) {
					if (count == 0) {
						colNum = getColNum(s);
					}
					if (count > 0 && colNum == getColNum(s)
							&& s.indexOf("||end") != -1) {
						setParam(s);
						mInsert(tableName);
						flag = false;
					} else {
						flag = true;
					}
					if (count > 0 && flag) {
						if (firstFlag) {
							setParam(s);
							firstFlag = false;
						} else {
							brSetParam(s);
						}
						if (s.indexOf("||end") != -1) {
							mInsert(tableName);
							firstFlag = true;
							flag = false;
						}
					}
					count++;
				}
				count = 0;
			}
			zin.closeEntry();
			zin.close();
		} catch (IOException e) {
		}
	}

	public static void mInsert(String tableName) {
		try {
			paramName.deleteCharAt(paramName.length() - 1);
			param.set(
					param.size() - 1,
					new String[] {
							param.get(param.size() - 1)[0].substring(0,
									param.get(param.size() - 1)[0].length() - 5),
							param.get(param.size() - 1)[1] });
			md.addData(tableName, paramName.toString(), param);
			param.clear();
			paramName.delete(0, paramName.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setParam(String s) {
		String[] arrStr = s.split(";\\|\\|;");
		colCount = arrStr.length - 1;
		for (int i = 0; i < arrStr.length; i++) {
			if (arrStr[i].equals("null")) {
				paramName.append("null,");
			} else {
				paramName.append("?,");
				if (isNumeric(arrStr[i]) && arrStr[i].length() < 11) {
					param.add(new String[] { arrStr[i], "num" });
				} else if (isNumerFloat(arrStr[i])) {
					param.add(new String[] { arrStr[i], "float" });
				} else {
					param.add(new String[] { arrStr[i], "string" });
				}
			}
		}
	}

	public static void brSetParam(String s) {
		String[] arrStr = s.split(";\\|\\|;");
		if (arrStr.length > 0) {
			param.set(colCount - 1,
					new String[] { param.get(colCount - 1)[0] + arrStr[0],
							param.get(colCount - 1)[1] });
		}
		// System.out.println("append:" + param.get(colCount - 1)[0]);
		if (arrStr.length > 0) {
			for (int i = 1; i < arrStr.length; i++) {
				if (arrStr[i].equals("null")) {
					paramName.append("null,");
				} else {
					paramName.append("?,");
					if (isNumeric(arrStr[i]) && arrStr[i].length() < 11) {
						param.add(new String[] { arrStr[i], "num" });
					} else if (isNumerFloat(arrStr[i])) {
						param.add(new String[] { arrStr[i], "float" });
					} else {
						param.add(new String[] { arrStr[i], "string" });
					}
				}
			}
		}
	}

	public static int getColNum(String s) {
		String[] arrStr = s.split(";\\|\\|;");
		return arrStr.length;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static boolean isNumerFloat(String str) {
		Pattern pattern = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
		return pattern.matcher(str).matches();
	}

}