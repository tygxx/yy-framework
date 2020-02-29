package com.yy.framework.core.thread;

/**
 * 类名称: Concurrent<br>
 * 类描述: 控制多线程等待和唤醒的工具类<br>
 * 修改时间: 2016年12月1日上午11:11:29<br>
 * @author mateng@eversec.cn
 */
public class Concurrent {
	
	public synchronized void notifyPool() {
		this.notifyAll();
	}
	
	public synchronized void waitPool() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
