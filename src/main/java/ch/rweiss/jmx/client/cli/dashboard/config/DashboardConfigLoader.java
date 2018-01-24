package ch.rweiss.jmx.client.cli.dashboard.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ch.rweiss.jmx.client.cli.CommandException;

public class DashboardConfigLoader
{
  private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  private static File dashboardConfigDirectory = new File("config"+File.separator+"dashboards"); 
  
  public DashboardConfig load(String dashboardName)
  {
    File configFile = new File(dashboardConfigDirectory, dashboardName+".yaml");
    try (Reader yamlSource = new FileReader(configFile))
    {
      DashboardConfig config = mapper.readValue(yamlSource, DashboardConfig.class);
      return config;
    }
    catch(IOException ex)
    {
      throw new CommandException(ex, "Cannot read dashboard config file {0}", configFile.getPath());
    }
  }

}
