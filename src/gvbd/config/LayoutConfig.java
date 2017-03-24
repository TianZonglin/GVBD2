package gvbd.config;

/** 
 * LayoutConfig类 :
 * 是具体布局算法Config类的父类，包含一些共有的配置项
 * @author Mr.T
 * @Time 2017-03-24
 */
public class  LayoutConfig {
	private int times=0;
	private boolean layoutByTimes;
	private int width;
	private int height;
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public boolean isLayoutByTimes() {
		return layoutByTimes;
	}
	public void setLayoutByTimes(boolean layoutByTimes) {
		this.layoutByTimes = layoutByTimes;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
