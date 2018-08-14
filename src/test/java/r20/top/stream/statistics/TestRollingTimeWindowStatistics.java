package r20.top.stream.statistics;

import org.junit.Assert;
import org.junit.Test;

/**
 * author: roger.luo
 *
 * createTime : 2018/8/13 15:06
 *
 * description :
 **/
public class TestRollingTimeWindowStatistics {



  @Test
  public void testRollingTimeWindowStatistics() throws InterruptedException {
    RollingTimeWindowStatistics rtw = new RollingTimeWindowStatistics(2);
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incSuccess();
    rtw.incFail();
    Assert.assertEquals(0.1d, rtw.getFailPercent(), 2);
    Thread.sleep(1000l);
    rtw.incFail();
    Assert.assertEquals(1d, rtw.getFailPercent(), 2);
  }

  public void testRollingTimeWindowCleanUp(){

  }



}
