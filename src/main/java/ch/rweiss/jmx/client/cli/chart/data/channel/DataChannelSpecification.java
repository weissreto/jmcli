package ch.rweiss.jmx.client.cli.chart.data.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.MBeanFilter;
import ch.rweiss.jmx.client.cli.CommandException;

public class DataChannelSpecification
{
  private static final String ATTRIBUTE_SEPARATOR = ".";
  private static final String DOMAIN_SEPARATOR = ":";
  private static final String POST_PROCESSOR_FUNCTION_SEPARATOR = "+";
  private final MBeanFilter beanFilter;
  private final String attributeFilter;
  private final List<String> compositeKeys;
  private final List<Function> postProcessorFunctions = new ArrayList<>();
  
  public enum Function
  {
    DELTA, 
    PERCENTAGE;
  }

  public DataChannelSpecification(String specification)
  {
    String filterSpec = parsePostProcessorFunctions(specification);
    int pos = StringUtils.indexOf(filterSpec, DOMAIN_SEPARATOR);
    if (pos > 0)
    {
      pos = StringUtils.indexOf(filterSpec, ATTRIBUTE_SEPARATOR, pos+1);
    }
    else
    {
      pos = StringUtils.indexOf(filterSpec, ATTRIBUTE_SEPARATOR);
    }
    if (pos < 0)
    {
      throw new CommandException("Data channel specification "+specification+ " has wrong format");     
    }
    beanFilter = MBeanFilter.with(StringUtils.substring(filterSpec, 0, pos));
    int attributeEnd = StringUtils.indexOf(filterSpec, ATTRIBUTE_SEPARATOR, pos+1);
    if (attributeEnd < 0)
    {
      attributeFilter = StringUtils.substring(filterSpec, pos+1);
      compositeKeys = Collections.emptyList();
      return;
    }
    attributeFilter = StringUtils.substring(filterSpec, pos+1, attributeEnd);
    String composite = StringUtils.substring(filterSpec, attributeEnd+1);
    compositeKeys = Arrays.asList(StringUtils.split(composite, ATTRIBUTE_SEPARATOR));
  }

  private String parsePostProcessorFunctions(String specification)
  {
    String functionsSpec = StringUtils.substringAfter(specification, POST_PROCESSOR_FUNCTION_SEPARATOR);
    if (StringUtils.isBlank(functionsSpec))
    {
      return specification;
    }
    String filterSpec = StringUtils.substringBefore(specification, POST_PROCESSOR_FUNCTION_SEPARATOR);
    for (String function : StringUtils.split(functionsSpec, POST_PROCESSOR_FUNCTION_SEPARATOR))
    {
      postProcessorFunctions.add(Function.valueOf(function.toUpperCase())); 
    }
    return filterSpec;
  }

  public MBeanFilter beanFilter()
  {
    return beanFilter;
  }

  public String attributeFilter()
  {
    return attributeFilter;
  }

  public List<String> compositeKeys()
  {
    return compositeKeys;
  }
  
  public List<Function> postProcessorFunctions()
  {
    return postProcessorFunctions;
  }
}
