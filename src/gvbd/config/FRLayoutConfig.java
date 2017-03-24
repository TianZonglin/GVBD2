package gvbd.config;


/** 
 * FRLayoutConfig类 :
 * 继承自gvbd.config.LayoutConfig类，该类的实例化对象可以包含全部布局算法的配置信息
 * @see LayoutConfig
 * @author Mr.T
 * @Time 2017-03-24
 */
public class FRLayoutConfig extends LayoutConfig {
	private float k;
	private double forceThreshold; 
	private boolean isDirected;
	private float cool;
	private float temperature;

	public float getK() {
		return k;
	}
	public void setK(float k) {
		this.k = k;
	}
	public boolean isDirected() {
		return isDirected;
	}
	public void setDirected(boolean isDirected) {
		this.isDirected = isDirected;
	}
	public double getForceThreshold() {
		return forceThreshold;
	}
	public void setForceThreshold(double forceThreshold) {
		this.forceThreshold = forceThreshold;
	}
	public float getCool() {
		return cool;
	}
	public void setCool(float cool) {
		this.cool = cool;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
}
