package ch.rweiss.jmx.client.cli.chart.data.channel;

public class DeltaDataChannel extends FunctionDataChannel
{
  public Object lastValue;
  public DeltaDataChannel(DataChannel baseValueDataChannel)
  {
    super(baseValueDataChannel.name(), baseValueDataChannel);
  }

  @Override
  protected Object apply(Object value)
  {
    Object delta;
    lastValue = getLastOrInitValue(value);
    if (value instanceof Long)
    {
      delta = (Long)value-(Long)lastValue;
    }
    else if (value instanceof Integer)
    {
      delta = (Integer)value-(Integer)lastValue;
    }
    else if (value instanceof Float)
    {
      delta = (Float)value-(Float)lastValue;
    }
    else if (value instanceof Double)
    {
      delta = (Double)value-(Double)lastValue;
    }
    else
    {
      delta = value;
    }
    lastValue = value;
    return delta;
  }

  private Object getLastOrInitValue(Object value)
  {
    if (lastValue != null)
    {
      return lastValue;
    }
    return value;
  }
}
