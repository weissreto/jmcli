package ch.rweiss.jmx.client.cli.config;

import java.io.File;

public class ConfigDirectory
{
  private static final File CONFIG_DIRECTORY = find();
   
  private static File find()
  {
    File configDir = new File("config");
    if (configDir.isDirectory())
    {
      return configDir;
    }
    configDir = new File("..", "config");
    if (configDir.isDirectory())
    {
      return configDir;
    }
    throw new IllegalStateException("Configuration directory not found"); 
  }
  
  public static File of(String configType)
  {
    return new File(CONFIG_DIRECTORY, configType);    
  }
}
