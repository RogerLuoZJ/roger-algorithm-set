package r20.top.stream.statistics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: roger.luo
 *
 * createTime : 2018/8/13 10:24
 *
 * description : rolling time window the timeunit is second
 **/
public class RollingTimeWindowStatistics {

  private Integer windowSize;

  private Slince[] counter;

  private static final Integer ROLLING_WINDOW_SIZE = 120;

  private static final Integer CLEAN_UP_BUFFER = 30;

  private Long beginTime;

  /**
   *
   * @param windowSize 时间窗口大小
   */
  public RollingTimeWindowStatistics(Integer windowSize){
    this.windowSize = windowSize;
    if ( windowSize <= 0 ) {
      throw new IllegalArgumentException(" window size must positive number");
    } else {
      this.windowSize = windowSize;
    }

    this.counter = new Slince[ROLLING_WINDOW_SIZE];

    for (int i = 0 ; i < this.counter.length ; i++) {
      this.counter[i] = new Slince();
    }

    this.beginTime = System.currentTimeMillis();

  }

  public double getFailPercent(){
    return getFailPercent(this.getIndex());
  }

  public double getFailPercent(Integer index){
     int lastIndex = 0;
     double failNum = 0;
     double totalNum = 0;
     for (int i = 0 ; i < this.windowSize ; i++) {
       lastIndex = ((index - i + RollingTimeWindowStatistics.ROLLING_WINDOW_SIZE) % RollingTimeWindowStatistics.ROLLING_WINDOW_SIZE);
       failNum += this.counter[lastIndex].getFailNum();
       totalNum += this.counter[lastIndex].getTotalNum();
     }

     return failNum/totalNum;
  }


  public double incSuccessAndGetFailPercent(){
      int index = this.incSuccess();
      return this.getFailPercent(index);
  }

  public double incFailAndGetFailPercent(){
    int index = this.incFail();
    return this.getFailPercent(index);
  }

  public int incSuccess(){
    int index = getIndex();
    this.counter[index].incSuccess();
    return index;
  }

  public int incFail(){
    int index = getIndex();
    this.counter[getIndex()].incFail();
    return index;
  }

  private int getIndex() {
    return (int) ((System.currentTimeMillis() - this.beginTime) % RollingTimeWindowStatistics.ROLLING_WINDOW_SIZE);
  }

  /**
   * 根据BUFFER 清除能够避免由于stw， 导致部分数组在该轮没有清空
   */
  public void cleanUp(){
    for (int i = this.getIndex() + 1 ; i < RollingTimeWindowStatistics.CLEAN_UP_BUFFER; i++) {
      this.counter[i % RollingTimeWindowStatistics.CLEAN_UP_BUFFER].reset();
    }
  }


  private class Slince{

    private AtomicInteger failCounter = new AtomicInteger();

    private AtomicInteger totalCounter = new AtomicInteger();

    public void incSuccess(){
      incTotal();
    }

    private void incTotal() {
      this.totalCounter.incrementAndGet();
    }

    public void incFail(){
      incTotal();
      this.failCounter.incrementAndGet();
    }


    public synchronized void reset(){
      this.failCounter.set(0);
      this.totalCounter.set(0);
    }

    public int getTotalNum() {
      return this.totalCounter.get();
    }

    public int getFailNum() {
      return this.failCounter.get();
    }
  }

}


