package ch.weiss.jmx.client.cli.chart.data.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.lang3.StringUtils;

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
      if (specification.compositeKeys().isEmpty())
      {
        addDataChannels(dataChannels, attributeDataChannel);
      }
      else
      {
        name = dataChannelName(bean, attribute, specification, true);
        DataChannel dataChannel = new MCompositeDataChannel(name, attributeDataChannel, specification.compositeKeys());
        dataChannels.add(dataChannel);
      }      
    }
    if (specification.deltaValues())
    {
      dataChannels = toDeltaDataChannels(dataChannels);
    }
    return dataChannels;
  }
  
  private static List<DataChannel> toDeltaDataChannels(List<DataChannel> dataChannels)
  {    
    return dataChannels
        .stream()
        .map(dataChannel -> new DeltaDataChannel(dataChannel))
        .collect(Collectors.toList());
  }

  private void addDataChannels(List<DataChannel> dataChannels, MAttributeDataChannel attributeDataChannel)
  {
    OpenType<?> type = attributeDataChannel.attribute().openType();
    if (type == null)
    {
      dataChannels.add(attributeDataChannel);
    }
    else if (SimpleType.LONG.equals(type))
    {
      dataChannels.add(attributeDataChannel);
    }
    else if (type instanceof CompositeType)
    {
      addDataChannels(dataChannels, attributeDataChannel, (CompositeType)type, new ArrayList<>());
    }
    else
    {
      throw new CommandException("Attribute type not compatible with charts");
    }
  }

  private void addDataChannels(List<DataChannel> dataChannels, MAttributeDataChannel attributeDataChannel,
      CompositeType type, List<String> parentItemNames)
  {
    for (String itemName : type.keySet())
    {
      List<String> itemNames = new ArrayList<>(parentItemNames);
      itemNames.add(itemName);
      String name = attributeDataChannel.name()+" "+StringUtils.join(itemNames, ".");
      OpenType<?> itemType = type.getType(itemName);
      if (SimpleType.LONG.equals(itemType))
      {        
        dataChannels.add(new MCompositeDataChannel(name, attributeDataChannel, itemNames));
      }
      else if (itemType instanceof CompositeType)
      {
        addDataChannels(dataChannels, attributeDataChannel, (CompositeType)itemType, itemNames);
      }
    }
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
      for (String key : specification.compositeKeys())
      {
        builder.append(key);
        builder.append(" ");
      }
    }
    String name = builder.toString();
    return name.trim();
  }
  
  
  
}
