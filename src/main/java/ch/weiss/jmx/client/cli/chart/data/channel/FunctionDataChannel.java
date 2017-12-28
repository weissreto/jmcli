package ch.weiss.jmx.client.cli.chart.data.channel;

import ch.weiss.check.Check;

public abstract class FunctionDataChannel extends DataChannel
{
  private final ScannableDataChannel scannableDataChannel;
  
  protected FunctionDataChannel(String name, ScannableDataChannel scannableDataChannel)
  {
    super(name);
    Check.parameter("scannableDataChannel").withValue(scannableDataChannel).isNotNull();
    this.scannableDataChannel = scannableDataChannel;
  }
  
  @Override
  public Object value()
  {
    Object compositeData = scannableDataChannel.value();
    Object value = apply(compositeData);
    return value;
  }

  protected abstract Object apply(Object value);

  ScannableDataChannel scannableDataChannel()
  {
    return scannableDataChannel;
  }
}
