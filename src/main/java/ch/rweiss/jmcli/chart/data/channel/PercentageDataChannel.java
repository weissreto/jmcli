package ch.rweiss.jmcli.chart.data.channel;

import org.apache.commons.lang3.NotImplementedException;

public class PercentageDataChannel extends FunctionDataChannel
{
  public PercentageDataChannel(DataChannel baseValueDataChannel)
  {
    super(baseValueDataChannel.name(), baseValueDataChannel);
  }

  @Override
  protected Object apply(Object value)
  {
    if (value == null)
    {
      return null;
    }
    if (value instanceof Long)
    {
      return ((Long)value)*100L;
    }
    else if (value instanceof Integer)
    {
      return ((Integer)value)*100;
    }
    else if (value instanceof Double)
    {
      return ((Double)value)*100.0d;
    }
    else if (value instanceof Float)
    {
      return ((Float)value)*100.0f;
    }
    throw new NotImplementedException("Data type "+value.getClass().getSimpleName()+" not supported");
  }
}
