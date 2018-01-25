package ch.rweiss.jmx.client.cli.chart.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ch.rweiss.jmx.client.cli.CommandException;

public class ChartConfigLoader
{
  private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  private static File chartConfigDirectory = new File("config"+File.separator+"charts"); 
  
  public ChartConfig load(String chart)
  {
    File dir = chartConfigDirectory;
    String fileName = chart;
    if (StringUtils.contains(chart, "."))
    {
      String relativeDirectory = StringUtils.substringBeforeLast(chart, ".");
      fileName = StringUtils.substringAfterLast(chart, ".");
      relativeDirectory = StringUtils.replace(relativeDirectory, ".", File.separator);
      dir = new File(dir, relativeDirectory);
    }
    
    File configFile = new File(dir, fileName+".yaml");
    try (Reader yamlSource = new FileReader(configFile))
    {
      ChartConfig config = mapper.readValue(yamlSource, ChartConfig.class);
      return config;
    }
    catch(IOException ex)
    {
      throw new CommandException(ex, "Cannot read chart config file {0}", configFile.getPath());
    }
  }
}
