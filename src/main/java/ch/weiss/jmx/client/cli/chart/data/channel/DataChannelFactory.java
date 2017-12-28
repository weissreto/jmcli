package ch.weiss.jmx.client.cli.chart.data.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.weiss.check.Check;
import ch.weiss.jmx.client.JmxClient;
import ch.weiss.jmx.client.MAttribute;
import ch.weiss.jmx.client.MBean;
import ch.weiss.jmx.client.MBeanName;
import ch.weiss.jmx.client.cli.CommandException;

public class DataChannelFactory
{
  private final JmxClient jmxClient;
  
  public DataChannelFactory(JmxClient jmxClient)
  {
    Check.parameter("jmxClient").withValue(jmxClient).isNotNull();
    this.jmxClient = jmxClient;
  }
  
  public List<DataChannel> createFor(DataChannelSpecification specification)
  {
    List<DataChannel> dataChannels = new ArrayList<>();
    List<MBean> beans = jmxClient.beansThatMatch(specification.beanFilter());
    for (MBean bean : beans)
    {
      MAttribute attribute = bean.attribute(specification.attributeFilter());
      if (attribute == null)
      {
        throw new CommandException("Bean "+bean.name()+" has no "+specification.attributeFilter()+" attribute.");
      }
      String name = dataChannelName(bean, attribute, specification, false);
      MAttributeDataChannel attributeDataChannel = new MAttributeDataChannel(name, attribute);
      if (specification.getCompositeKeys().isEmpty())
      {
        dataChannels.add(attributeDataChannel);
      }
      else
      {
        name = dataChannelName(bean, attribute, specification, true);
        DataChannel dataChannel = new MCompositeDataChannel(name, attributeDataChannel, specification.getCompositeKeys());
        dataChannels.add(dataChannel);
      }      
    }
    return dataChannels;
  }
  
  private static String dataChannelName(MBean bean, MAttribute attribute, DataChannelSpecification specification, boolean appendCompositeKeys)
  {
    StringBuilder builder = new StringBuilder();
    MBeanName beanName = bean.name();
    MBeanName specName = MBeanName.createFor(specification.beanFilter().toString());
    for (int pos = 0; pos < beanName.valueParts().size(); pos++)
    {
      String nameValue = beanName.valueParts().get(pos);
      if (pos < specName.valueParts().size())
      {
        String specValue = specName.valueParts().get(pos);
        if (!Objects.equals(nameValue, specValue))
        {
          builder.append(beanName.valueParts().get(pos));
          builder.append(" ");
        }
      }
      else
      {
        builder.append(nameValue);
        builder.append(" ");
      }
    }
    builder.append(attribute.name());
    if (appendCompositeKeys)
    {
      builder.append(" ");
      for (String key : specification.getCompositeKeys())
      {
        builder.append(key);
        builder.append(" ");
      }
    }
    String name = builder.toString();
    return name.trim();
  }
  
  
  
}
