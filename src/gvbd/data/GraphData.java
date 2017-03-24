package gvbd.data;

import gvbd.graph.Graph;
import gvbd.graph.Node;

import java.io.BufferedReader;
import java.io.IOException;

/** 
 * GraphData类 :
 * 该类会将输入数据实例化为一个Graph对象
 * @method void loadNodeData(...)核心方法，初始化graph对象
 * @method Graph getGraph()
 * @method void setGraph(Graph graph)
 * @author Mr.T
 * @Time 2017-03-24
 */
public class GraphData {

	private Graph graph;

	public GraphData() {
		this.graph = new Graph();
	}
   /** loadNodeData :
	 * 该方法按照输入的数据创建一个Graph对象
	 * @param nodeDataReader 一个BufferedReader对象，负责读取节点数据
	 * @param nodeFormat 是一个gvbd.data.NodeFormat对象，负责将字符数据转换为点数据
	 * @param vertexNum 创建图中节点的数量
	 * @return void(Null)
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	public void loadNodeData(BufferedReader nodeDataReader,NodeFormat nodeFormat,int vertexNum) {
		graph.createNodes(vertexNum); //按输入的节点数创建相关数量节点的图
		Node [] nodes=graph.getNodes(); //nodes是构造的
		String nodeLine;
		int lineNo=0;
		try {
			nodeLine = nodeDataReader.readLine();
			if(lineNo == 0)System.out.println(nodeLine);
			while (nodeLine != null) {
				Node node = nodeFormat.stringToNode(nodeLine); //转换节点类型
				nodes[lineNo++]=node;
				nodeLine = nodeDataReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

}
