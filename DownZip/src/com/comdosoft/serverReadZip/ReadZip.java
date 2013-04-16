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
	private static List<String> setParamName = new ArrayList<String>();
	private static List<Integer> intParam = new ArrayList<Integer>();
	private static String[] setParam;
	private static List<String[]> param = new ArrayList<>();
	private static List<String> idList = new ArrayList<>();
	private static List<String> dbIdList = new ArrayList<>();
	private static List<String> addIdList = new ArrayList<>();
	private static int count = 0;
	private static int colNum = 0;
	private static int addCount = 0;
	private static int updCount = 0;
	private static int intSize = 0;
	private static boolean flag = false;
	private static boolean firstFlag = true;
	private static boolean endFlag = false;
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
				dbIdList = md.getId(tableName);
				while ((s = in.readLine()) != null) {
					addId(s);
					if (count == 0) {
						setParam = s.split(";\\|\\|;");
						setParam[setParam.length - 1] = setParam[setParam.length - 1]
								.substring(
										0,
										setParam[setParam.length - 1].length() - 5);
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
			System.out.println("执行完毕,添加" + addCount + "条,修改" + updCount
					+ "条记录!");
			addCount = 0;
			updCount = 0;
		} catch (IOException e) {
		}
	}

	public static void mInsert(String tableName) {
		paramName.deleteCharAt(paramName.length() - 1);
		if (!endFlag) {
			param.set(
					param.size() - 1,
					new String[] {
							param.get(param.size() - 1)[0].substring(0,
									param.get(param.size() - 1)[0].length() - 5),
							param.get(param.size() - 1)[1] });
		}
		isId();
		mAdd(tableName);
		param.clear();
		paramName.delete(0, paramName.length());
	}

	public static void setParam(String s) {
		String[] arrStr = s.split(";\\|\\|;");
		endFlag = false;
		for (int i = 0; i < arrStr.length; i++) {
			if (arrStr[i].equals("null||end")) {
				paramName.append("null,");
				endFlag = true;
			} else if (arrStr[i].equals("null")) {
				paramName.append("null,");
			} else {
				intParam.add(i);
				paramName.append("?,");
				if (isNumeric(arrStr[i]) && arrStr[i].length() < 11) {
					param.add(new String[] { arrStr[i], "num" });
				} else if (isNumerFloat(arrStr[i]) && arrStr[i].length() < 11) {
					param.add(new String[] { arrStr[i], "float" });
				} else if (isBoolean(arrStr[i])) {
					param.add(new String[] { arrStr[i], "boolean" });
				} else {
					param.add(new String[] { arrStr[i], "string" });
				}
			}
		}
		intSize = intParam.get(intParam.size() - 1);
	}

	public static void brSetParam(String s) {
		String[] arrStr = s.split(";\\|\\|;");
		if (arrStr.length > 0) {
			param.set(param.size() - 1,
					new String[] { param.get(param.size() - 1)[0] + arrStr[0],
							param.get(param.size() - 1)[1] });
		}
		if (arrStr.length > 0) {
			endFlag = false;
			for (int i = 1; i < arrStr.length; i++) {
				if (arrStr[i].equals("null||end")) {
					paramName.append("null,");
					endFlag = true;
				} else if (arrStr[i].equals("null")) {
					paramName.append("null,");
				} else {
					intParam.add(intSize + i);
					paramName.append("?,");
					if (isNumeric(arrStr[i]) && arrStr[i].length() < 11) {
						param.add(new String[] { arrStr[i], "num" });
					} else if (isNumerFloat(arrStr[i])
							&& arrStr[i].length() < 11) {
						param.add(new String[] { arrStr[i], "float" });
					} else if (isBoolean(arrStr[i])) {
						param.add(new String[] { arrStr[i], "boolean" });
					} else {
						param.add(new String[] { arrStr[i], "string" });
					}
				}
			}
		}
	}

	public static void mAdd(String tableName) {
		try {
			boolean flag = false;
			for (int i = 0; i < dbIdList.size(); i++) {
				if (dbIdList.get(i).equals(param.get(0)[0])) {
					flag = true;
				}
			}
			if (!flag) {
				md.addData(tableName, paramName.toString(), param);
				addCount++;
				intParam.clear();
			} else {
				for (int i = 0; i < intParam.size(); i++) {
					setParamName.add(setParam[intParam.get(i)]);
				}
				md.update(tableName, paramName.toString(), param, setParamName);
				System.out.println(updCount++);
				setParamName.clear();
				intParam.clear();
			}
		} catch (IOException e) {
			System.out.println(tableName + "表出错!");
			e.printStackTrace();
		}
	}

	public static void isId() {
		for (int i = 0; i < idList.size(); i++) {
			boolean idFlag = false;
			for (int j = 0; j < dbIdList.size(); j++) {
				if (idList.get(i).equals(dbIdList.get(j))) {
					idFlag = true;
				}
			}
			if (!idFlag) {
				addIdList.add(idList.get(i));
			}
		}
	}

	public static void addId(String s) {
		if (count > 0) {
			String[] arrStr = s.split(";\\|\\|;");
			if (isNumeric(arrStr[0])) {
				idList.add(arrStr[0]);
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

	public static boolean isBoolean(String str) {
		if (str.equals("false") || str.equals("true")) {
			return true;
		}
		return false;
	}

}