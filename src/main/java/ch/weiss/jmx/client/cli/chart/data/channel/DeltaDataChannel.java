package ch.weiss.jmx.client.cli.chart.data.channel;

public class DeltaDataChannel extends FunctionDataChannel
{
  public Long lastValue;
  public DeltaDataChannel(DataChannel baseValueDataChannel)
  {
    super(baseValueDataChannel.name(), baseValueDataChannel);
  }

  @Override
  protected Object apply(Object value)
  {
    Long currentValue = (Long)value;
    long delta = 0L;
    if (lastValue != null && currentValue != null)
    {
      delta = currentValue-lastValue;
    }
    lastValue = currentValue;
    return delta;
  }
}
