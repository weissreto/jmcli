package ch.rweiss.jmx.client.cli.set;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.cli.AbstractBeanCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.jmx.client.cli.info.InfoAttribute;
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
  
  private Table<MBean> beanTitle = declareBeanTitleTable();
  private Table<MAttribute> attributeTitle = declareAttributeTitleTable();
  private Table<Pair<String, StyledText>> attributeValues = declareValueTable();


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
        printBeanName(bean);
        printAttributeName(attribute);
        setValue(attribute);
      }
    }
  }

  private void printBeanName(MBean bean)
  {
    printEmptyLine();
    beanTitle.addRow(bean);
    beanTitle.printWithoutHeader();
  }

  private void printAttributeName(MAttribute attribute)
  {
    printEmptyLine();
    attributeTitle.addRow(attribute);
    attributeTitle.printWithoutHeader();
  }


  private void setValue(MAttribute attribute)
  {
    printEmptyLine();
    attributeValues.addRow(Pair.of("Value (Before)", InfoAttribute.getValue(attribute)));
    attribute.value(value);
    attributeValues.addRow(Pair.of("Value (Now)", InfoAttribute.getValue(attribute)));
    attributeValues.printWithoutHeader();
  }
  
  private static Table<MBean> declareBeanTitleTable()
  {
    Table<MBean> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, b -> b.name())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.NAME_TITLE)
          .withMinWidth(8)
          .toColumn());
    return table;
  }
  
  private static Table<MAttribute> declareAttributeTitleTable()
  {
    Table<MAttribute> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, b -> "Setting attribute '"+b.name()+"' ...")
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.SUB_TITLE)
          .withMinWidth(8)
          .toColumn());
    return table;
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
