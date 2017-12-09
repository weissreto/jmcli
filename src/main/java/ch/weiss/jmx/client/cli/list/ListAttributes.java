package ch.weiss.jmx.client.cli.list;

import java.util.List;

import ch.weiss.jmx.client.JmxException;
import ch.weiss.jmx.client.MAttribute;
import ch.weiss.jmx.client.MBean;
import ch.weiss.jmx.client.cli.AbstractAttributeCommand;
import picocli.CommandLine.Command;

@Command(name="attributes", description="Lists attributes")
public class ListAttributes extends AbstractAttributeCommand
{  
  @Override
  protected void printTitle()
  {
    term.write("Attributes");    
  }
  
  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      print(bean);
    }
  }

  private void print(MBean bean)
  {
    List<MAttribute> attributes = getFilteredAttributes(bean);
    if (!attributes.isEmpty())
    {
      printBean(bean);
      for (MAttribute attribute : attributes)
      {
        try
        {
          printNameValue(attribute.name(), attribute.valueAsString());
        }
        catch(JmxException ex)
        {
          printNameError(attribute.name(), ex);
        }
      }
    }
  }

  private void printBean(MBean bean)
  {
    term.newLine();
    printNameTitle(bean.name().fullQualifiedName());
    term.newLine();
  }

}
