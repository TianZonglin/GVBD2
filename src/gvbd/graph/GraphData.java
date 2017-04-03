package gvbd.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * GraphData
 * 此类的
 * 写入到对应路径的文件内
 * 
 * @method hashMapToJson
 * @method UndirectGraphTopK
 * 
 * @author Mr.T
 * @Time 2017-4-3
 */
public class GraphData {

//	public static void main(String[] args) {
//		try {
//			String filename = "C:/Users/Administrator/Desktop/undirectwuminxia.txt";
//			InputStreamReader isr = new InputStreamReader(new FileInputStream(
//					filename), "utf-8");
//			BufferedReader br = new BufferedReader(isr);
//
//			List<String> keyList = new ArrayList<String>();
//			List<String> nodeCountList = new ArrayList<String>();
//
//			String line = "";
//			while ((line = br.readLine()) != null) {
//				String[] keyArr = line.split("\t");
//				String[] arr = line.split("\t")[1].split(" ");
//				String record = keyArr[0] + "\t" + arr.length;
//				nodeCountList.add(record);
//			}
//
//			Comparator comparator = new ComparatorListSort();
//			Collections.sort(nodeCountList, comparator);
//			Iterator it3 = nodeCountList.iterator();
//
//			FileOutputStream writerStream = new FileOutputStream(
//					"C:/Users/Administrator/Desktop/count1.json");
//			BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(
//					writerStream, "UTF-8"));
//			HashMap<String, Integer> hash = new HashMap<String, Integer>();
//			int counter = 0;
//			while (it3.hasNext()) {
//				String[] jsondata = it3.next().toString().split("\t");
//
//				if (Integer.parseInt(jsondata[1]) > 4) {
//					hash.put(jsondata[0], Integer.parseInt(jsondata[1]));
//				} else {
//					counter++;
//				}
//			}
//			hash.put("其他", counter);
//			String data = hashMapToJson(hash);
//			buff.write(data);
//			buff.close();
//			System.out.println("写入ok");
//			br.close();
//
//		} catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("没有指定文件");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	
	
	
   /**
    * generateData
    * 此方法会接收一个数组，其会在[服务器]项目根目录新建相应文件
    * @param args[] 系统页面点提交时所填写的各项数据
    * 
    * @author Mr.T
    * @Time 2017-4-3
    */
	public void UndirectGraphTopK(String pathin,String realPath, String filename) {
		try {
			//String filename = "C:/Users/Administrator/Desktop/undirectwuminxia.txt";
			InputStreamReader isr = new InputStreamReader(new FileInputStream(pathin), "utf-8");
			//FileInputStream文件输入流，用来读取文件，前提是目标文件必须存在！
			//FileOutputStream输出流，写入文件时可以创建文件
			//以上二者均继承自Input/OutputStream
			
			BufferedReader br = new BufferedReader(isr);

			List<String> keyList = new ArrayList<String>();
			List<String> nodeCountList = new ArrayList<String>();

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] keyArr = line.split("\t");
				String[] arr = line.split("\t")[1].split(" ");
				String record = keyArr[0] + "\t" + arr.length;
				nodeCountList.add(record);
			}

			Comparator comparator = new ComparatorListSort();
			Collections.sort(nodeCountList, comparator);
			Iterator it3 = nodeCountList.iterator();
			
			FileOutputStream writerStream = new FileOutputStream(realPath+"\\dataTopK\\"+filename+".json");
			//此处老是报路径错误，原因是项目中没有dataTopK这个
			
			BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
			HashMap<String, Integer> hash = new HashMap<String, Integer>();
			int counter = 0,number=0;
			while (it3.hasNext()) {
				String[] jsondata = it3.next().toString().split("\t");
				
				if (number<10) {
					hash.put(jsondata[0], Integer.parseInt(jsondata[1]));
					number++;
				} else {
					counter++;
				}
			}
			hash.put("其他", counter);
			String data = hashMapToJson(hash);
			buff.write(data);
			buff.close();
			System.out.println("写入ok");
			br.close();

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(e+"没有指定文件");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	   /**
	    * hashMapToJson
	    * 此方法会接收一个数组，其会在[服务器]项目根目录新建相应文件
	    * 
	    * @param map 系统页面点提交时所填写的各项数据
	    * 
	    * @author Mr.T
	    * @Time 2017-4-3
	    */
		public static String hashMapToJson(HashMap map) {
			String string = "{data:[";
			for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
				Entry e = (Entry) it.next();
				string += "{name:'" + e.getKey() + "',";
				string += "data:" + e.getValue() + "},";
			}
			string = string.substring(0, string.lastIndexOf(","));
			string += "]}";
			return string; 
		}
		
		
		
		
		

}
