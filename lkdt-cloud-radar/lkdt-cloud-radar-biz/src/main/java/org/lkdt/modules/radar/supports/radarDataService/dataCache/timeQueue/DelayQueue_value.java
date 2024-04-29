package org.lkdt.modules.radar.supports.radarDataService.dataCache.timeQueue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayQueue_value {
	/*
	 * 无界队列，塞进去的数据有规定的时间在什么时候才可以取，默认是排好顺序的，等待时间最长的排在前面，先往外拿
	 * DelayQueue做定时执行任务,谁时间快到了要执行了，就先取出谁
	 */
	static BlockingQueue<MyTask> tasks=new DelayQueue<>();
	static Random r=new Random();


	/*
	 *  MyTask往DelayQueue里面装的时候必须实现Delayed接口
	 *  内部类MyTask用来模拟一个接口
	 */
	static class MyTask implements Delayed{
		long runningTime;

		MyTask(long rt){
			this.runningTime=rt;
		}

		/*
		 * 实现Comparable接口的方法
		 */
		@Override
		public int compareTo(Delayed o) {
			if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS))
				return -1; //轮到该执行的时间已经过去了
			else if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS))
				return 1; //还剩一会儿时间才执行
			else
				return 0;
		}

		/*
		 * 还有多长时间我就可以往外拿了
		 */
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(runningTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}

		public String toString(){
			return ""+runningTime;
		}
	}



	public static void main(String[] args) throws InterruptedException {
		long now=System.currentTimeMillis();
		/*
		 * 五个任务
		 */
		MyTask t1 = new MyTask(now + 1000); //现在开始，1秒钟之后进行
		MyTask t2 = new MyTask(now + 2000);
		MyTask t3 = new MyTask(now + 1500);
		MyTask t4 = new MyTask(now + 2500);
		MyTask t5 = new MyTask(now + 500);
		/*
		 * 把任务扔进队列里面去
		 */
		tasks.put(t1);
		tasks.put(t2);
		tasks.put(t3);
		tasks.put(t4);
		tasks.put(t5);

		System.out.println(tasks);

		for(int i=0;i<5;i++){
			System.out.println(tasks.take());
		}
	}
}