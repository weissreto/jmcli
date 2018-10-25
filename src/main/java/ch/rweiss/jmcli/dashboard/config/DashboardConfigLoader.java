package ch.rweiss.jmcli.dashboard.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ch.rweiss.jmcli.CommandException;
import ch.rweiss.jmcli.config.ConfigDirectory;

public class DashboardConfigLoader
{
  private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  private static final File DASHBOARD_CONFIG_DIRECTORY = ConfigDirectory.of("dashboards"); 
  
  public DashboardConfig load(String dashboardName)
  {
    File configFile = new File(DASHBOARD_CONFIG_DIRECTORY, dashboardName+".yaml");
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
