package gvbd.layout;

/** 
 * gvbd.layout.Layout接口 :
 * 该接口声明了一些布局算法的基本操作，包括：
 * initAlgo() 初始化算法；
 * goAlgo() 执行算法；
 * doLayout() 执行布操作；
 * 继承接口时（各算法类都继承该接口）需要实现相应的方法；
 * @author Mr.T
 * @Time 2017-03-24
 */
public interface Layout {
	public void initAlgo();
	public void goAlgo() throws Exception;
	public void doLayout();

}
