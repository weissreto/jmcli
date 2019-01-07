package ch.rweiss.jmcli.set;

import java.lang.management.ManagementFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class RegisterMxBeanExtension implements BeforeAllCallback, AfterAllCallback
{
  private final Object mxBean;
  private final ObjectName name;
  
  public RegisterMxBeanExtension(String name, Object mxBean)
  {
    super();
    try
    {
      this.name = new ObjectName(name);
    }
    catch (MalformedObjectNameException ex)
    {
      throw new RuntimeException(ex);
    }
    this.mxBean = mxBean;
  }
  
  public String getName()
  {
    return name.toString();
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception
  {
    ManagementFactory.getPlatformMBeanServer().registerMBean(mxBean, name); 
  }
  
  @Override
  public void afterAll(ExtensionContext context) throws Exception
  {
    ManagementFactory.getPlatformMBeanServer().unregisterMBean(name);
  }
}