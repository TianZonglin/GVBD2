package gvbd.config;

import gvbd.data.NodeFormat;

import java.io.BufferedReader;


/** 
 * gvbd.data.DataConfig类 :
 * 该类包含一些配置项的方法，get&set
 * @method getNodeNum
 * @method getDataPath
 * @method getDataReader
 * @method getNodeFormat
 * @author Mr.T
 * @Time 2017-03-24
 */
public class DataConfig {
	private static String dataPath;
	private static BufferedReader dataReader;
	private static NodeFormat nodeFormat;
	private static int NodeNum;
	
	public static int getNodeNum() {
		return NodeNum;
	}
	public static void setNodeNum(int nodeNum) {
		NodeNum = nodeNum;
	}
	public static String getDataPath() {
		return dataPath;
	}
	public static void setDataPath(String dataPath) {
		DataConfig.dataPath = dataPath;
	}
	public static BufferedReader getDataReader() {
		return dataReader;
	}
	public static void setDataReader(BufferedReader dataReader) {
		DataConfig.dataReader = dataReader;
	}
   /** 
	 * getNodeFormat :
	 * 该方法返回一个gvbd.data.NodeFormat对象
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	public static NodeFormat getNodeFormat() {
		return nodeFormat;
	}
	public static void setNodeFormat(NodeFormat nodeFormat) {
		DataConfig.nodeFormat = nodeFormat;
	}

}
