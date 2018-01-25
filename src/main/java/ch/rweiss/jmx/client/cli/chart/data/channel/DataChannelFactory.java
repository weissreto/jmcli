package ch.rweiss.jmx.client.cli.chart.data.channel;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.check.Check;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.jmx.client.cli.CommandException;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannelSpecification.Function;

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
    return createFor(specification, null);
  }

  public List<DataChannel> createFor(DataChannelSpecification specification, String nameTemplate)
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
      String name = dataChannelName(nameTemplate, bean, attribute, specification, false);
      MAttributeDataChannel attributeDataChannel = new MAttributeDataChannel(name, attribute);
      if (specification.compositeKeys().isEmpty())
      {
        addDataChannels(dataChannels, attributeDataChannel);
      }
      else
      {
        name = dataChannelName(nameTemplate, bean, attribute, specification, true);
        DataChannel dataChannel = new MCompositeDataChannel(name, attributeDataChannel, specification.compositeKeys());
        dataChannels.add(dataChannel);
      }      
    }
    dataChannels = addPostProcessorFunctions(specification.postProcessorFunctions(), dataChannels);
    return dataChannels;
  }
  
  private static List<DataChannel> addPostProcessorFunctions(List<Function> functions, List<DataChannel> dataChannels)
  {
    for (Function function : functions)
    {
      dataChannels = addPostProcessorFunction(function, dataChannels);
    }
    return dataChannels;
  }
  
  private static List<DataChannel> addPostProcessorFunction(Function function, List<DataChannel> dataChannels)
  {
    return dataChannels
        .stream()
        .map(dataChannel -> createPostProcessorFunction(function, dataChannel))
        .collect(Collectors.toList());
  }
  
  @SuppressWarnings("unchecked")
  private static FunctionDataChannel createPostProcessorFunction(Function function, DataChannel dataChannel)
  {
    try
    {
      String simpleName = StringUtils.capitalize(function.name().toLowerCase())+"DataChannel";
      String className = Function.class.getPackage().getName()+"."+simpleName;
      Class<FunctionDataChannel> functionClass;
      functionClass = (Class<FunctionDataChannel>) Class.forName(className);
      Constructor<FunctionDataChannel> constructor = functionClass.getConstructor(DataChannel.class);
      return constructor.newInstance(dataChannel);
    }
    catch (Exception ex)
    {
      throw new CommandException(ex, "Could not instantiate class for data channel function {0}", function);
    }
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
    else if (SimpleType.INTEGER.equals(type))
    {
      dataChannels.add(attributeDataChannel);
    }
    else if (SimpleType.FLOAT.equals(type))
    {
      dataChannels.add(attributeDataChannel);
    }
    else if (SimpleType.DOUBLE.equals(type))
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

  private static String dataChannelName(String nameTemplate, MBean bean, MAttribute attribute, DataChannelSpecification specification, boolean appendCompositeKeys)
  {
    if (StringUtils.isBlank(nameTemplate))
    {
      return dataChannelName(bean, attribute, specification, appendCompositeKeys);
    }
    if (StringUtils.contains(nameTemplate, "${"))
    {
      return expandNameTemplate(nameTemplate, bean, attribute);
    }
    return nameTemplate;
    
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
  
  private static String expandNameTemplate(String nameTemplate, MBean bean, MAttribute attribute)
  {
    String name = nameTemplate;
    for (String template : getTemplates(nameTemplate))
    {
      name = StringUtils.replace(name, "${"+template+"}", expandTemplate(template.trim(), bean, attribute));
    }
    return name;
  }

  private static String expandTemplate(String template, MBean bean, MAttribute attribute)
  {
    if ("attribute".equals(template))
    {
      return attribute.name();
    }
    if ("bean".equals(template))
    {
      return bean.name().fullQualifiedName();
    }
    if (StringUtils.startsWith(template, "bean."))
    {
      String key = StringUtils.substringAfter(template, "bean.");
      String value = bean.name().valuePartOf(key);
      if (StringUtils.isBlank(value))
      {
        return "Unknown template "+template;
      }
      return value;
    }
    return "Unknown template "+template;
  }

  private static String[] getTemplates(String nameTemplate)
  {
    return StringUtils.substringsBetween(nameTemplate, "${", "}");
  }

}
