package gvbd.layout;

import gvbd.config.FRLayoutConfig;
import gvbd.config.LayoutConfig;
import gvbd.graph.Edge;
import gvbd.graph.Graph;
import gvbd.graph.Node;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
/** 
 * gvbd.layout.FRForceLayout类 :
 * 该类是FR力导向算法的实现类，有若干方法包括：
 * 构造函数FRForceLayout(..)；
 * 实现接口Layout的方法；
 * 还有一些私有方法；
 * @author Mr.T
 * @Time 2017-03-24
 */
public class FRForceLayout implements Layout {
	private Graph graph;
	private boolean isDirected;
	private int times;
	private boolean layoutByTimes;
	private int width;
	private int height;
	private double resultantForceX;
	private double resultantForceY;
	private double forceThreshold;
	private int area;
	private double k;
	private float cool;
	private float temperature;
	
    /** 
     * FRForceLayout :
	 * 此类的构造函数，将本类中定义的变量赋予layoutConfig对象配置的值
	 * @param graph 原始结构图，传入的外来变量
	 * @param layoutConfig 配置对象
	 * @return void(Null)
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	public FRForceLayout(Graph graph, LayoutConfig layoutConfig) {
		this.graph = graph; 
		FRLayoutConfig forceLayoutConfig = (FRLayoutConfig) layoutConfig;
		this.height = forceLayoutConfig.getHeight();
		this.width = forceLayoutConfig.getWidth();
		this.isDirected = forceLayoutConfig.isDirected();
		this.area = this.height * this.width;
		System.out.println("tttt"+this.area);
		
		this.k = Math.sqrt(this.area / this.graph.getNodes().length)*forceLayoutConfig.getK();
		this.layoutByTimes = forceLayoutConfig.isLayoutByTimes();//是否有迭代次数输入
		this.times = forceLayoutConfig.getTimes();
		this.forceThreshold = forceLayoutConfig.getForceThreshold();
		this.cool=forceLayoutConfig.getCool();
		this.temperature=forceLayoutConfig.getTemperature();
		System.out.println("k="+this.k);
		System.out.println("time="+this.times);
	}

	/** 
     * initAlgo :
	 * 初始化算法，会获取graph的顶点数和顶点数组Node[]，然后遍历全部节点，设置节点坐标；
	 * 初始坐标是随机的，然后开始计算相关引力斥力，不断迭代直到稳定；
	 * @return void(Null)
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	@Override
	public void initAlgo() {
		System.out.println("定点数=" + this.graph.getNodes().length);
		Node[] nodes=this.graph.getNodes();
		
		for  (int i=0 ;i<nodes.length;++i) {
			Node node = nodes[i];
			node.getNodeLayoutData().setX(Math.random() * width);//随机生成第一次的坐标
			node.getNodeLayoutData().setY(Math.random() * height);
			node.getNodeLayoutData().setOldX(node.getNodeLayoutData().getX());//紧接着将上述坐标作为旧坐标
			node.getNodeLayoutData().setOldY(node.getNodeLayoutData().getY());
		}
	}
	
	/** 
     * goAlgo :
	 * 遍历全部节点{
	 * 		对于当前节点{
	 * 			找到与此节点有边的节点并遍历（计算引力）{
	 * 				计算此节点与边另一头节点的距离；
	 * 				按公式产生attractForceX、attractForceY；
	 * 			}
	 * 			遍历全部节点（计算斥力）{
	 * 				计算此节点与边另一头节点的距离；
	 * 				按公式产生repulsionForceX、repulsionForceY；
	 * 			}
	 * 			生成resultantForceX、resultantForceY
	 * 			按算法计算并更新新坐标；
	 * 			更新旧坐标；
	 * 		}				
	 * }
	 * @return void(Null)
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	@Override
	public void goAlgo() throws Exception {
		this.resultantForceX = 0;
		this.resultantForceY = 0;

		// 计算引力
		Node [] nodes=this.graph.getNodes();
		for(int i=0;i<nodes.length;++i) {
		//	long start=System.currentTimeMillis();
			
			double forceX;
			double forceY;
			Node node1 = nodes[i];
			
			
	//		System.out.println("顶点"+node1.getNodeId()+"位置  x="+node1.getNodeLayoutData().getOldX()+"  y="+node1.getNodeLayoutData().getOldY());
			
			
			List<Edge> edges = node1.getEdges();
			Iterator<Edge> edgeIt = edges.iterator();
			double attractForceX = 0;
			double attractForceY = 0;
			// 计算引力
			while (edgeIt.hasNext()) {
				Edge edge = edgeIt.next();
				Node node2=nodes[edge.getTarget()-1];
				double dist=dist(node1,node2);
				
				
	//			System.out.println("顶点"+node2.getNodeId()+"位置  x="+node2.getNodeLayoutData().getOldX()+"  y="+node2.getNodeLayoutData().getOldY());
	//			System.out.println("距离="+dist);
				
	//			System.out.println("引力="+attractiveForce(dist)+"   x方向="+(node2.getNodeLayoutData().getOldX()-node1.getNodeLayoutData().getOldX())/(dist+0.00001d)*attractiveForce(dist)+"   y方向="+(node2.getNodeLayoutData().getOldY()-node1.getNodeLayoutData().getOldY())/(dist+0.00001d)*attractiveForce(dist));
				
				

				// 引力公式 力等于距离
				attractForceX += (node2.getNodeLayoutData().getOldX()-node1.getNodeLayoutData().getOldX())/(dist+0.00001d)*attractiveForce(dist);
				attractForceY += (node2.getNodeLayoutData().getOldY()-node1.getNodeLayoutData().getOldY())/(dist+0.00001d)*attractiveForce(dist);

			}
			
	//		System.out.println("引力合力         x="+attractForceX+"             y="+attractForceY);
			// 计算斥力
			double repulsionForceX = 0;
			double repulsionForceY = 0;
			for(int j=0;j<nodes.length;++j) {
				Node node2 = nodes[j];
				
				
				
				
				
				if (node1.getNodeId() != node2.getNodeId()) {
					
	//				System.out.println("顶点"+node2.getNodeId()+"位置  x="+node2.getNodeLayoutData().getOldX()+"  y="+node2.getNodeLayoutData().getOldY());
					
					double dist=dist(node1,node2);
					repulsionForceX += (node1.getNodeLayoutData().getOldX()-node2.getNodeLayoutData().getOldX()+0.00001d)/dist*repulsiveForce(dist);
					repulsionForceY += (node1.getNodeLayoutData().getOldY()-node2.getNodeLayoutData().getOldY()+0.00001d)/dist*repulsiveForce(dist);
					
		//			System.out.println("斥力="+repulsiveForce(dist)+"   x方向="+(node1.getNodeLayoutData().getOldX()-node2.getNodeLayoutData().getOldX()+0.00001d)/dist*repulsiveForce(dist)+"   y方向="+(node1.getNodeLayoutData().getOldY()-node2.getNodeLayoutData().getOldY()+0.00001d)/dist*repulsiveForce(dist));
					
					
				}
			}
			
		//	long end=System.currentTimeMillis();
		//	System.out.println(end-start);
			
	//		System.out.println("斥力合力         x="+repulsionForceX+"             y="+repulsionForceY);
			
			
			forceX = attractForceX + repulsionForceX;
			forceY = attractForceY + repulsionForceY;
			double force=Math.sqrt(forceX*forceX+forceY*forceY);
			
			
	//		System.out.println("合力   x="+ forceX+"   y="+forceY);
			
			

			this.resultantForceX += Math.abs(forceX);
			this.resultantForceY += Math.abs(forceY);
			


//			node1.getNodeLayoutData().setX(
//					node1.getNodeLayoutData().getOldX() +  20*forceX/Math.abs(forceX));
//			node1.getNodeLayoutData().setY(
//					node1.getNodeLayoutData().getOldY() + 20*forceY/Math.abs(forceY));

	//		System.out.println("温度="+this.temperature);
			node1.getNodeLayoutData().setX(
					node1.getNodeLayoutData().getOldX() +  (Math.abs(forceX)>this.temperature?((forceX>0?1:-1)*this.temperature):forceX));
			node1.getNodeLayoutData().setY(
					node1.getNodeLayoutData().getOldY() +  (Math.abs(forceY)>this.temperature?((forceY>0?1:-1)*this.temperature):forceY));
			
	//		System.out.println("位移    x="+(Math.abs(forceX)>this.temperature?((forceX>0?1:-1)*this.temperature):forceX)+"    y="+(Math.abs(forceY)>this.temperature?((forceY>0?1:-1)*this.temperature):forceY));
		//	node1.getNodeLayoutData().setX(0>(this.width<node1.getNodeLayoutData().getX()?this.width:node1.getNodeLayoutData().getX())?0:(this.width<node1.getNodeLayoutData().getX()?this.width:node1.getNodeLayoutData().getX()));
		//	node1.getNodeLayoutData().setY(0>(this.height<node1.getNodeLayoutData().getY()?this.width:node1.getNodeLayoutData().getY())?0:(this.width<node1.getNodeLayoutData().getY()?this.width:node1.getNodeLayoutData().getY()));

			if(node1.getNodeLayoutData().getX()<0){
				node1.getNodeLayoutData().setX(0);
			}
			if(node1.getNodeLayoutData().getX()>width){
				node1.getNodeLayoutData().setX(width);
			}
			if(node1.getNodeLayoutData().getY()<0){
				node1.getNodeLayoutData().setY(0);
			}
			if(node1.getNodeLayoutData().getY()>height){
				node1.getNodeLayoutData().setY(height);
			}
			
	//		System.out.println("顶点新位置    x="+node1.getNodeLayoutData().getX()+"    y="+node1.getNodeLayoutData().getY());

		
		}
		for (int i=0;i<nodes.length;++i){
			Node node=nodes[i];
			node.getNodeLayoutData().setOldX(node.getNodeLayoutData().getX());
			node.getNodeLayoutData().setOldY(node.getNodeLayoutData().getY());
		}

		
		
	}
	/** 
	 * doLayout :
	 * 会通过外部调用的方式来执行此段代码；
	 * 按设置的迭代次数迭代算法goAlgo()，会判断是否定义了迭代次数；
	 * 如果有次数则直接简单的重复迭代，如果没有，则需要进行其他的设置；
	 * @return void(Null)
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	@Override
	public void doLayout() {
		try {

			this.initAlgo();
			if (layoutByTimes) {
				for (int i = 0; i < times; ++i) {
					this.goAlgo();
/*					System.out.println("!!!!!!!!!合力x="
							+ this.resultantForceX
							+ "   合力y="
							+ this.resultantForceY
							+ "   合力="
							+ Math.sqrt(this.resultantForceX
									* this.resultantForceX
									+ this.resultantForceY
									+ this.resultantForceY));
					System.out.println();*/
					this.temperature=cool(this.temperature);
				}
			} else { //如果没有迭代参数，则会按一次迭代来执行
				this.goAlgo();
				int times = 1;
				double force = Math.sqrt(this.resultantForceX
						* this.resultantForceX + this.resultantForceY
						+ this.resultantForceY);
				System.out.println(force);
				while (this.forceThreshold < force) {
					this.goAlgo();
					times++;
					System.out.println(force);
					force = Math.sqrt(this.resultantForceX
							* this.resultantForceX + this.resultantForceY
							+ this.resultantForceY);
				}
				System.out.println("times=" + times);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("暂时没实现这种类型的布局");
			e.printStackTrace();
		}


	}

	/** 
	 * dist :
	 * 私有方法，计算两个节点之间的欧式距离；
	 * 按设置的迭代次数迭代算法goAlgo()，会判断是否定义了迭代次数；
	 * 如果有次数则直接简单的重复迭代，如果没有，则需要进行其他的设置；
	 * @return void(Null)
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	private double dist(Node node1, Node node2) {
		double dist = Math.sqrt((node1.getNodeLayoutData().getOldX() - node2
				.getNodeLayoutData().getOldX())
				* (node1.getNodeLayoutData().getOldX() - node2.getNodeLayoutData()
						.getOldX())
				+ (node1.getNodeLayoutData().getOldY() - node2.getNodeLayoutData()
						.getOldY())
				* (node1.getNodeLayoutData().getOldY() - node2.getNodeLayoutData()
						.getOldY()));
		return dist;
	}
	/** 
	 * 计算吸引力attractiveForce；输入为两节点;
	 * 没用到！
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	private double attractiveForce(Node node1,Node node2){
		double force=dist(node1,node2)*dist(node1, node2)/this.k;
		return force;
	}
	/** 
	 * 计算吸引力attractiveForce；输入为两点距离，公式为dist^2/k;
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	private double attractiveForce(double dist){
		double force=dist*dist/this.k;
		return force;
	}
	
	/** 
	 * 计算排斥力repulsiveForce；输入为两节点;
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	private double repulsiveForce(Node node1,Node node2){
		double force=this.k*this.k/dist(node1,node2);
		return force;
	}
	/** 
	 * 计算repulsiveForce；输入为两点距离，公式为k^2/dist;
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	private double repulsiveForce(double dist){
		double force=this.k*this.k/dist;
		return force;
	}
	/** 
	 * cool :
	 * 传入温度temperature，变量cool是在外部配置的降温参数
	 * @return t*cool
	 * @author Mr.T
	 * @Time 2017-03-24
	 */
	private float cool(float t){
		return t*cool;
	}

}
