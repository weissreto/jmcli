package ch.rweiss.jmx.client.cli.list;

import java.util.List;

import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.cli.AbstractAttributeCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name="attributes", description="Lists attributes")
public class ListAttributes extends AbstractAttributeCommand
{  
  private final Table<MAttribute> table = declareTable();

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

  private static Table<MAttribute> declareTable()
  {
    Table<MAttribute> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, attribute -> attribute.name())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .withMinWidth(10)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Value", 40)        
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(attribute -> getValue(attribute))
          .multiLine()
          .withMinWidth(10)
          .toColumn());
    return table;
  }
  
  private static StyledText getValue(MAttribute attribute)
  {
    try
    {
      return new StyledText(attribute.valueAsString(), Styles.VALUE);
    }
    catch(JmxException ex)
    {
      return new StyledText(toErrorMessage(ex), Styles.ERROR);
    }
  }

  private void print(MBean bean)
  {
    List<MAttribute> attributes = getFilteredAttributes(bean);
    if (!attributes.isEmpty())
    {
      printBean(bean);
      table.clear();
      for (MAttribute attribute : attributes)
      {
        table.addRow(attribute);
      }
      table.printWithoutHeader();
    }
  }

  private void printBean(MBean bean)
  {
    printEmptyLine();
    printNameTitle(bean.name().fullQualifiedName());
  }

}
