package gvbd.servlet;

import gvbd.config.DataConfig;
import gvbd.data.BSPNodeFormatImpl;
import gvbd.graph.GraphData;
import gvbd.util.generateData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class proData extends HttpServlet {

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 此处接收了来自前台的number,avg,random,filename
	 * 即，点击生成数据时所填写的全部数据
	 * 
	 * @author Mr.T
	 * @Time 2017-4-3
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String nre = getServletContext().getRealPath("/dataSimple/");
		String [] args={request.getParameter("number"),request.getParameter("avg"),request.getParameter("random"),nre,request.getParameter("filename")};
		 
		generateData.generate(args);
		//导入 gvbd.util.generateData之后，直接就可以使用其方法，对于节点太少导致的出错已解决

/*		DataConfig.setDataPath(nre+"\\"+request.getParameter("filename")+".txt");
		BufferedReader br=new BufferedReader(new FileReader(new File(DataConfig.getDataPath())));
		DataConfig.setDataReader(br);
		DataConfig.setNodeFormat(new BSPNodeFormatImpl());
		DataConfig.setNodeNum(Integer.parseInt(request.getParameter("number")));*/
		String realPath = request.getSession().getServletContext().getRealPath("");
		GraphData graphData = new GraphData();
		graphData.UndirectGraphTopK(nre+"\\"+request.getParameter("filename")+".txt",realPath,request.getParameter("filename"));
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
