package ch.rweiss.jmx.client.cli.info;

import java.util.List;

import ch.rweiss.jmx.client.cli.AbstractAttributeCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import picocli.CommandLine.Command;

@Command(name="attribute", description="Prints information about attributes")
public class InfoAttribute extends AbstractAttributeCommand
{
  @Override
  protected void printTitle()
  {
    term.write("Attribute Info");
  }

  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      List<MAttribute> attributes = getFilteredAttributes(bean);
      if (!attributes.isEmpty())
      {
        printBeanNameTitle(bean);
        for (MAttribute attr : attributes)
        {
          print(attr);
        }
      }
    }
  }


  private void printBeanNameTitle(MBean bean)
  {
    term.newLine();
    printNameTitle(bean.name().fullQualifiedName());
  }

  private void print(MAttribute attr)
  {
    printNameTitle(attr);
    printDescription(attr);
    printName(attr);
    printType(attr);
    printReadable(attr);
    printWritable(attr);
    printValue(attr);
  }

  private void printNameTitle(MAttribute attr)
  {
    term.newLine();
    printNameTitle(2, attr.name());
    term.newLine();
  }
  
  private void printDescription(MAttribute attr)
  {
    printFirstColumn(2, Styles.DESCRIPTION, attr.description());
    term.newLine();
    term.newLine();
  }

  private void printName(MAttribute attr)
  {
    printNameValue(2, "Name", attr.name());
  }

  private void printType(MAttribute attr)
  {
    printNameValue(2, "Type", attr.type());
  }

  private void printReadable(MAttribute attr)
  {
    printNameValue(2, "IsReadable", Boolean.toString(attr.isReadable()));
  }

  private void printWritable(MAttribute attr)
  {
    printNameValue(2, "IsWritable", Boolean.toString(attr.isWritable()));
  }

  private void printValue(MAttribute attr)
  {
    try
    {
      printNameValue(2, "Value", attr.valueAsString());
    }
    catch(JmxException ex)
    {
      printNameError(2, "Value", ex);
    }
  }
}

