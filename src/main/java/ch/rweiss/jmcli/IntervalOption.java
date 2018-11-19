package ch.rweiss.jmcli;

import picocli.CommandLine.Option;

public class IntervalOption
{
  @Option(names = { "-i", "--interval" }, description = "Refresh interval in seconds")
  private int interval = 0;

  private long lastTimeDataWasGathered = 0;

  public void setDefault(int defaultInterval)
  {
    interval = defaultInterval;
  }

  public boolean isPeriodical()
  {
    return interval > 0;
  }

  boolean isNotPeriodical()
  {
    return !isPeriodical();
  }

  long computeTimeUntilNextDataGathering()
  {
    return lastTimeDataWasGathered + waitTime() - System.currentTimeMillis();
  }

  void dataWasGathered()
  {
    lastTimeDataWasGathered = System.currentTimeMillis();
  }

  long waitTime()
  {
    return interval*1000l;
  }
}
