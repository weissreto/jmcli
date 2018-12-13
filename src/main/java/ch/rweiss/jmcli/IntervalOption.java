package ch.rweiss.jmcli;

import picocli.CommandLine.Option;

public class IntervalOption
{
  @Option(names = { "-i", "--interval" }, description = "Refresh interval in seconds")
  private int interval;

  private long lastTimeDataWasGathered = 0;

  public IntervalOption()
  {
    this(0);
  }
  
  public IntervalOption(int defaultInterval)
  {
    interval = defaultInterval;
  }
  
  public boolean isPeriodical()
  {
    return interval > 0;
  }

  public boolean isNotPeriodical()
  {
    return !isPeriodical();
  }

  public long computeTimeUntilNextDataGathering()
  {
    return lastTimeDataWasGathered + waitTime() - System.currentTimeMillis();
  }

  public void dataWasGathered()
  {
    lastTimeDataWasGathered = System.currentTimeMillis();
  }

  public long waitTime()
  {
    return interval*1000l;
  }
}
