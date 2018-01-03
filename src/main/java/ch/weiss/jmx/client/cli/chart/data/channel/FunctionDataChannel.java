package ch.weiss.jmx.client.cli.chart.data.channel;

import ch.weiss.check.Check;

public abstract class FunctionDataChannel extends DataChannel
{
  private final DataChannel baseValueDataChannel;
  
  protected FunctionDataChannel(String name, DataChannel baseValueDataChannel)
  {
    super(name);
    Check.parameter("baseValueDataChannel").withValue(baseValueDataChannel).isNotNull();
    this.baseValueDataChannel = baseValueDataChannel;
  }
  
  @Override
  public Object value()
  {
    Object baseValue = baseValueDataChannel.value();
    Object value = apply(baseValue);
    return value;
  }

  protected abstract Object apply(Object value);

  ScannableDataChannel scannableDataChannel()
  {
    if (baseValueDataChannel instanceof ScannableDataChannel)
    {
      return (ScannableDataChannel) baseValueDataChannel;
    }
    return ((FunctionDataChannel)baseValueDataChannel).scannableDataChannel();
  }
}
