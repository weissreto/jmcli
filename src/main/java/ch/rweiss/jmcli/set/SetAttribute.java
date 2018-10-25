package ch.rweiss.jmcli.set;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractBeanCommand;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.list.ListAttributes;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="attribute", description="Sets the value of an attribute")
public class SetAttribute extends AbstractBeanCommand
{
  @Parameters(index="1", paramLabel="ATTRIBUTE", description="The name of the attribute to set")
  private String attributeName;

  @Parameters(index="2", paramLabel="VALUE", description="The value to set")
  private String value;
  
  private Table<Pair<String, StyledText>> attributeValues = declareValueTable();

  public SetAttribute()
  {
    super("Set Attribute");
  }

  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      MAttribute attribute = bean.attribute(attributeName);
      if (attribute != null)
      {
        printBeanNameTitle(bean);
        setValue(attribute);
      }
    }
  }
  
  private void setValue(MAttribute attribute)
  {
    printEmptyLine();
    attributeValues.addRow(Pair.of("Setting attribute", new StyledText(attribute.name(), Styles.VALUE)));
    attributeValues.addRow(Pair.of("Value (Before)", ListAttributes.getValue(attribute)));
    attribute.value(value);
    attributeValues.addRow(Pair.of("Value (Now)", ListAttributes.getValue(attribute)));
    attributeValues.printWithoutHeader();
  }
    
  private static Table<Pair<String,StyledText>> declareValueTable()
  {
    Table<Pair<String, StyledText>> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.NAME)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Value", 60)
          .withStyledTextProvider(pair -> pair.getValue())
          .multiLine()
          .withMinWidth(8)
          .toColumn());
    return table;
  }

}
