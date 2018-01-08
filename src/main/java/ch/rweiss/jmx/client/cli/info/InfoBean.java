package ch.rweiss.jmx.client.cli.info;

import ch.rweiss.jmx.client.cli.AbstractBeanCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MOperation;
import picocli.CommandLine.Command;

@Command(name="bean", description="Prints information about managment beans")
public class InfoBean extends AbstractBeanCommand
{
  @Override
  protected void printTitle()
  {
    term.write("Bean Info");
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
    printNameTitle(bean);
    printDescription(bean);
    printName(bean);
    printType(bean);
    printAttributes(bean);
    printOperations(bean);
  }

  private void printNameTitle(MBean bean)
  {
    term.newLine();
    printNameTitle(bean.name().fullQualifiedName());
    term.newLine();
  }

  private void printDescription(MBean bean)
  {
    printFirstColumn(Styles.DESCRIPTION, bean.description());
    term.newLine();
    term.newLine();
  }

  private void printName(MBean bean)
  {
    printNameValue("Name", bean.name().fullQualifiedName());    
  }

  private void printType(MBean bean)
  {
    printNameValue("Type", bean.type());    
  }

  private void printAttributes(MBean bean)
  {
    if (!bean.attributes().isEmpty())
    {
      term.newLine();
      printFirstColumn(Styles.SUB_TITLE, "Attributes:");
      term.newLine();
    }
    for (MAttribute attribute : bean.attributes())
    {
      printAttribute(attribute);
    }
  }

  private void printAttribute(MAttribute attribute)
  {
    printNameValue(2, attribute.name(), attribute.type());
  }

  private void printOperations(MBean bean)
  {
    if (!bean.operations().isEmpty())
    {
      term.newLine();
      printFirstColumn(Styles.SUB_TITLE, "Operations:");
      term.newLine();
    }
    for (MOperation operation : bean.operations())
    {
      printOperation(operation);
    }
  }

  private void printOperation(MOperation operation)
  {
    printNameValue(2, operation.name(), operation.signature());
  }
}
