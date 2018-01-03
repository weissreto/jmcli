package ch.weiss.jmx.client.cli.chart.data.channel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.weiss.jmx.client.MBeanFilter;
import ch.weiss.jmx.client.cli.CommandException;

public class DataChannelSpecification
{
  private final MBeanFilter beanFilter;
  private final String attributeFilter;
  private final List<String> compositeKeys;
  private final boolean deltaValues;

  public DataChannelSpecification(String specification, boolean deltaValues)
  {
    this.deltaValues = deltaValues;
    int pos = StringUtils.indexOf(specification, ":");
    if (pos > 0)
    {
      pos = StringUtils.indexOf(specification, ".", pos+1);
    }
    else
    {
      pos = StringUtils.indexOf(specification, ".");
    }
    if (pos < 0)
    {
      throw new CommandException("Data channel specification "+specification+ " has wrong format");     
    }
    beanFilter = MBeanFilter.with(StringUtils.substring(specification, 0, pos));
    int attributeEnd = StringUtils.indexOf(specification, ".", pos+1);
    if (attributeEnd < 0)
    {
      attributeFilter = StringUtils.substring(specification, pos+1);
      compositeKeys = Collections.emptyList();
      return;
    }
    attributeFilter = StringUtils.substring(specification, pos+1, attributeEnd);
    String composite = StringUtils.substring(specification, attributeEnd+1);
    compositeKeys = Arrays.asList(StringUtils.split(composite, "."));
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
  
  public boolean deltaValues()
  {
    return deltaValues;
  }
}
