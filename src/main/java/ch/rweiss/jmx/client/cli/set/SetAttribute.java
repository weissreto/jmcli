package ch.rweiss.jmx.client.cli.set;

import ch.rweiss.jmx.client.cli.AbstractBeanCommand;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="attribute", description="Sets the value of an attribute")
public class SetAttribute extends AbstractBeanCommand
{
  @Parameters(index="1", paramLabel="ATTRIBUTE", description="The name of the attribute to set")
  private String attributeName;

  @Parameters(index="2", paramLabel="VALUE", description="The value to set")
  private String value;

  @Override
  protected void printTitle()
  {
    term.write("Set Attribute");
  }

  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      MAttribute attribute = bean.attribute(attributeName);
      if (attribute != null)
      {
        term.newLine();
        printNameTitle(bean.name().fullQualifiedName());
        term.newLine();
        setValue(attribute);
      }
    }
  }

  private void setValue(MAttribute attribute)
  {
    printNameValue(attribute.name() + " (Before)", attribute.valueAsString());
    attribute.value(value);
    printNameValue(attribute.name() + " (After)", attribute.valueAsString());
  }


}
