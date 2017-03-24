package gvbd.data;

import gvbd.graph.Node;

/** 
 * NodeFormat接口 :
 * 只提供了stringToNode接口，使用时要先实现这个接口？？？？？？？？？？？？？？？？？？；
 * @author Mr.T
 * @Time 2017-03-24
 */
public interface NodeFormat {
	public Node stringToNode(String nodeStr);
}
