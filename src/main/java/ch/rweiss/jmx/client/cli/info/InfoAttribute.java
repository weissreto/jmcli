package ch.rweiss.jmx.client.cli.info;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.cli.AbstractAttributeCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name="attribute", description="Prints information about attributes")
public class InfoAttribute extends AbstractAttributeCommand
{
  private Table<MBean> beanTitle = declareBeanTitleTable();
  private Table<MAttribute> attributeTitle = declareAttributeTitleTable();
  private Table<MAttribute> description = declareDescriptionTable();
  private Table<Pair<String, StyledText>> properties = declarePropertiesTable();
  
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
    printEmptyLine();
    beanTitle.clear();
    beanTitle.addRow(bean);
    beanTitle.printWithoutHeader();
  }

  private void print(MAttribute attr)
  {
    printNameTitle(attr);
    printDescription(attr);
    
    properties.clear();
    properties.addRow(Pair.of("Name", new StyledText(attr.name(), Styles.VALUE)));
    properties.addRow(Pair.of("Type", new StyledText(attr.type(), Styles.VALUE)));
    properties.addRow(Pair.of("Readable", new StyledText(Boolean.toString(attr.isReadable()), Styles.VALUE)));
    properties.addRow(Pair.of("Writable", new StyledText(Boolean.toString(attr.isWritable()), Styles.VALUE)));
    properties.addRow(Pair.of("Value", getValue(attr)));
    properties.printWithoutHeader();
  }

  private void printNameTitle(MAttribute attr)
  {
    printEmptyLine();
    attributeTitle.clear();
    attributeTitle.addRow(attr);
    attributeTitle.printWithoutHeader();
  }
  
  private void printDescription(MAttribute attr)
  {
    printEmptyLine();
    description.clear();
    description.addRow(attr);
    description.printWithoutHeader();
    printEmptyLine();
  }

  private static StyledText getValue(MAttribute attr)
  {
    try
    {
      return new StyledText(attr.valueAsString(), Styles.VALUE);
    }
    catch(JmxException ex)
    {
      return new StyledText(toErrorMessage(ex), Styles.ERROR);
    }
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
        table.createColumn("", 40, attribute -> attribute.name())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.SUB_TITLE)
          .withMinWidth(8)
          .toColumn());
    return table;
  }

  private static Table<MAttribute> declareDescriptionTable()
  {
    Table<MAttribute> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, attribute -> attribute.description())
          .multiLine()
          .withCellStyle(Styles.DESCRIPTION)
          .withMinWidth(8)
          .toColumn());
    return table;
  }

  private static Table<Pair<String,StyledText>> declarePropertiesTable()
  {
    Table<Pair<String, StyledText>> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT)
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

