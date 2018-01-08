package ch.rweiss.jmx.client.cli.chart.data.channel;

import ch.rweiss.check.Check;
import ch.rweiss.jmx.client.MAttribute;

class MAttributeDataChannel extends ScannableDataChannel
{
  private final MAttribute attribute;
  
  MAttributeDataChannel(String name, MAttribute attribute)
  {
    super(name);
    Check.parameter("attribute").withValue(attribute).isNotNull();
    this.attribute = attribute;
  }

  @Override
  protected Object scanValue()
  {
    return attribute.value();
  }

  MAttribute attribute()
  {
    return attribute;
  }
  
}
