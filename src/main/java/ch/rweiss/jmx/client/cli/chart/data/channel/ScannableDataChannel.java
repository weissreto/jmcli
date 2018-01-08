package ch.rweiss.jmx.client.cli.chart.data.channel;

abstract class ScannableDataChannel extends DataChannel
{
  private Object currentValue;

  ScannableDataChannel(String name)
  {
    super(name);
  }
  
  void scan()
  {
    currentValue = scanValue();    
  }

  protected abstract Object scanValue();

  @Override
  public Object value()
  {
    return currentValue;
  }
}
