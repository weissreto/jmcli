package ch.rweiss.jmx.client.cli.chart.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ch.rweiss.jmx.client.cli.CommandException;

public class ChartConfigLoader
{
  private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  private static File chartConfigDirectory = new File("config"+File.separator+"charts"); 
  
  public ChartConfig load(String chart)
  {
    File configFile = new File(chartConfigDirectory, chart+".yaml");
    try (Reader yamlSource = new FileReader(configFile))
    {
      ChartConfig config = mapper.readValue(yamlSource, ChartConfig.class);
      return config;
    }
    catch(IOException ex)
    {
      throw new CommandException(ex, "Cannot read thread config file {0}", configFile.getPath());
    }
  }
}
