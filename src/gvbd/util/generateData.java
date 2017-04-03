package gvbd.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;





/**
 * generateData
 * 此类的构造方法会接收一个数组，并按照输入参数生成随机数据
 * 写入到对应路径的文件内
 * 
 * @method generate
 * @method undirectedGraph
 * @method edgeDuplicate
 * 
 * @author Mr.T
 * @Time 2017-4-3
 */
public class generateData
{
  public static int VERTEX_COUNT = 110;
  public static int EDGE_COUNT_BASE = 10;
  public static int EDGE_COUNT_RANDOM = 8;
  public static String DEFAULT_VERTEX_VALUE = "10.0";
  public static String DEFAULT_EDGE_WEIGHT = "0";
  public static String VERTEX_ID_VALUE_SPLIT = ":";
  public static String EDGE_ID_WEIGHT_SPLIT = ":";
  public static String VERTEX_EDGE_SPLIT = "\t"; 	//tab分隔
  public static String EDGE_EDGE_SPLIT = " ";
  public static String OUTPUT_PATH = "/home/pagerank.txt";
  public static int STATIC_VERTEX = 0;
  public static int STATIC_EDGE = 0;
  
  /**
   * generateData
   * 此方法会接收一个数组，其会在[服务器]项目根目录新建相应文件
   * @param args[] 系统页面点提交时所填写的各项数据
   * 
   * @author Mr.T
   * @Time 2017-4-3
   */
  public static void generate(String[] args)
  {
    VERTEX_COUNT = Integer.parseInt(args[0]);
    EDGE_COUNT_BASE=Integer.parseInt(args[1]);
    EDGE_COUNT_RANDOM=Integer.parseInt(args[2]);
    OUTPUT_PATH = args[3]+"\\"+args[4]+".txt";
    System.out.println(OUTPUT_PATH);
    Random rVertex = new Random();
    Random rVertexCount = new Random();
    File root = new File(OUTPUT_PATH);

    if(root.exists()){
    	root.delete();
    }
    try
    {
      System.out.println("Begin to generate the dataset for PageRank, please wait......");
      
      FileWriter fw = new FileWriter(root);
      BufferedWriter bw = new BufferedWriter(fw);
      


      int count = rVertexCount.nextInt(EDGE_COUNT_RANDOM) + EDGE_COUNT_BASE;
      StringBuffer sb = new StringBuffer((rVertex.nextInt(VERTEX_COUNT)+1) + EDGE_ID_WEIGHT_SPLIT + DEFAULT_EDGE_WEIGHT);
     
      for (int j = 1; j < count; j++) {
        sb.append(EDGE_EDGE_SPLIT + (rVertex.nextInt(VERTEX_COUNT)+1) + EDGE_ID_WEIGHT_SPLIT + DEFAULT_EDGE_WEIGHT);
      }
  
      bw.write(1 + VERTEX_ID_VALUE_SPLIT + DEFAULT_VERTEX_VALUE + VERTEX_EDGE_SPLIT + edgeDuplicate("1",sb.toString()));
      
      STATIC_VERTEX += 1;
      STATIC_EDGE += count;
      
      System.out.println(STATIC_EDGE);
      for (int i = 1; i < VERTEX_COUNT; i++) //生成相应节点数的随机数据
      {
        count = rVertexCount.nextInt(EDGE_COUNT_RANDOM) + EDGE_COUNT_BASE;
        sb = new StringBuffer((rVertex.nextInt(VERTEX_COUNT)+1) + EDGE_ID_WEIGHT_SPLIT + DEFAULT_EDGE_WEIGHT);
        for (int j = 1; j < count; j++) {
          sb.append(EDGE_EDGE_SPLIT + (rVertex.nextInt(VERTEX_COUNT)+1) + EDGE_ID_WEIGHT_SPLIT + DEFAULT_EDGE_WEIGHT);
        }
        bw.newLine();
        bw.write((i+1) + VERTEX_ID_VALUE_SPLIT + DEFAULT_VERTEX_VALUE + VERTEX_EDGE_SPLIT + edgeDuplicate(Integer.toString((i+1)),sb.toString()));
        
        STATIC_VERTEX += 1;
        STATIC_EDGE += count;
      }
      System.out.println("121321");
      //bw.flush();
      bw.close();
      fw.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.out.println("Generate data successfully and the statics as follws:");
    System.out.println("Size of DataSet : " + (float)root.length() / 1048576.0F + " MB");
    System.out.println("VertexCount : " + STATIC_VERTEX);
    System.out.println("EdgeCount : " + STATIC_EDGE);
    System.out.println("Average outgoing degree : " + STATIC_EDGE / STATIC_VERTEX);
    
    undirectedGraph(OUTPUT_PATH,OUTPUT_PATH); //之前shutdown!注释掉此句，生成文件里就会有数据。已经解决
    
  }
  
  

  
  
  /**
   * undirectedGraph
   * 此方法会先将pathin文件的内容读入，之后将其删掉，然后在将数据处理为undirectd格式，再输出到pathout中去；
   * 此处两路径相同，即对同一个文件进行覆写操作；
   * 功能：将原始数据转换为带邻居的无向图数据
   * 
   * @param pathin 读入文件的路径
   * @param pathout输出文件的路径
   * 
   * @author Mr.T
   * @Time 2017-4-3
   */
  public static void undirectedGraph(String pathin,String pathout){
	try {  
		Reader reader = new InputStreamReader(new FileInputStream(pathin));
		BufferedReader br=new BufferedReader(reader);
		List<String> adjList=new ArrayList<String>();
		String line=br.readLine();
		 System.out.println("LLLLLLLLLLine : " + line);
		while(line!=null){
			adjList.add(line);
			line=br.readLine();
		}
		br.close();
		new File(pathin).delete(); //会将pathin文件删掉
		
		String[] adj=adjList.toArray(new String[adjList.size()]);
		for(int i=0;i<adjList.size();i++)System.out.println(">>> "+adjList.get(i));
		for(int i=0;i<adjList.size();++i){
			String vertex=adj[i];
			System.out.println(vertex);
			if(vertex.split(VERTEX_EDGE_SPLIT).length==1)
				continue;
			String[] edges=vertex.split(VERTEX_EDGE_SPLIT)[1].split(EDGE_EDGE_SPLIT);
			for(int j=0 ;j<edges.length;++j){
				int vertexIndex=Integer.parseInt(edges[j].split(EDGE_ID_WEIGHT_SPLIT)[0])-1;
				adj[vertexIndex]=adj[vertexIndex]+EDGE_EDGE_SPLIT+vertex.split(VERTEX_EDGE_SPLIT)[0].split(VERTEX_ID_VALUE_SPLIT)[0]+EDGE_ID_WEIGHT_SPLIT+DEFAULT_EDGE_WEIGHT;
			}
		}
		BufferedWriter bw=new BufferedWriter(new FileWriter(pathout));
		for(int i=0;i<adj.length;++i){
			bw.write(adj[i].split(VERTEX_EDGE_SPLIT)[0]+VERTEX_EDGE_SPLIT+edgeDuplicate(adj[i].split(VERTEX_EDGE_SPLIT)[0].split(VERTEX_ID_VALUE_SPLIT)[0],adj[i].split(VERTEX_EDGE_SPLIT)[1]));
			//在原有基础上填加重复（相邻）的边
			bw.newLine();
		}
		bw.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
  }  
  
  /**
   * edgeDuplicate
   * 此方法应为generateData类的私有方法，仅仅被generate和undirectedGraph方法调用
   * 功能：找到相邻的节点并返回
   * 
   * @param vertexId 输入的节点
   * @param str 输入的边信息
   * @return sb.toString() 找出输入节点相连节点并组成字符串返回
   * 
   * @author Mr.T
   * @Time 2017-4-3
   */ 
  public static String edgeDuplicate(String vertexId,String str){
	  String[] edges=str.split(EDGE_EDGE_SPLIT);
	  StringBuffer sb=new StringBuffer();
	  Set<String> set=new HashSet<String>();
	  set.add(vertexId);
	  for(int i=0;i<edges.length;++i){
		  if(!set.contains(edges[i].split(EDGE_ID_WEIGHT_SPLIT)[0])){
			  set.add(edges[i].split(EDGE_ID_WEIGHT_SPLIT)[0]);
			  //System.out.println(edges[i].split(EDGE_ID_WEIGHT_SPLIT)[0]);
			  sb.append(EDGE_EDGE_SPLIT + ( edges[i].split(EDGE_ID_WEIGHT_SPLIT)[0]+ 
			            EDGE_ID_WEIGHT_SPLIT + DEFAULT_EDGE_WEIGHT));
		  }
	  }
	  if(sb.length()!=0)
	  sb.deleteCharAt(0);
	  return sb.toString();
  
	  }
  
  
}
