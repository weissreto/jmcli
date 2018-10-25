package ch.rweiss.jmcli.histo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.VmTypeConverter;

public class HistoDumpParser
{
  private final String histoDump;

  public HistoDumpParser(String histoDump)
  {
    this.histoDump = histoDump;
  }

  public List<ClassInfo> parse()
  {
    return Arrays.stream(StringUtils.split(histoDump, '\n'))
      .map(this::parseLine)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }
  
  public ClassInfo parseLine(String line)
  {
    if (StringUtils.isBlank(line))
    {
      return null;
    }
      
    if (!line.contains(":"))
    {
      return null;
    }
    
    line = StringUtils.substringAfter(line, ":").trim();
    String instances = StringUtils.substringBefore(line, " ");
    line = StringUtils.substringAfter(line, " ").trim();
    String bytes = StringUtils.substringBefore(line, " ");
    String className = StringUtils.substringAfter(line, " ").trim();
    return new ClassInfo(
        new VmTypeConverter(className).toDisplayName(), 
        Long.parseLong(instances), 
        Long.parseLong(bytes));    
  }

}
