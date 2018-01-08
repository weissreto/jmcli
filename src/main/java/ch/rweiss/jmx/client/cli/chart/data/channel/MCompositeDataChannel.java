package ch.rweiss.jmx.client.cli.chart.data.channel;

import java.util.List;

import javax.management.openmbean.CompositeData;

import ch.rweiss.check.Check;
import ch.rweiss.jmx.client.JmxException;

class MCompositeDataChannel extends FunctionDataChannel
{
  private final List<String> keys;
  
  MCompositeDataChannel(String name, ScannableDataChannel scannableDataChannel, List<String> keys)
  {
    super(name, scannableDataChannel);
    Check.parameter("keys").withValue(keys).isNotNull();
    this.keys = keys;
   }

  
  @Override
  protected Object apply(Object value)
  {
    Object resultValue = value;
    for (String key : keys)
    {
      if (value instanceof CompositeData)
      {
        CompositeData compositeData = (CompositeData) resultValue;
        resultValue = compositeData.get(key);
      }
      else
      {
        throw new JmxException("Data "+value+" should be an instance of CompositeData but is "+value.getClass());
      }
    }
    return resultValue;
  }
}
