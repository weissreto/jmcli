package ch.rweiss.jmx.client.cli.info;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.jmx.client.cli.AbstractBeanCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name="bean", description="Prints information about managment beans")
public class InfoBean extends AbstractBeanCommand
{
  private Table<MBean> description = declareDescriptionTable();
  private Table<Pair<String, String>> properties = declarePropertiesTable();
  private Table<MAttribute> attributes = declareAttributesTable();
  private Table<MOperation> operations = declareOperationsTable();

  public InfoBean()
  {
    super("Bean Info");
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
    printBeanNameTitle(bean);
    printDescription(bean);
    printNameAndType(bean);
    printAttributes(bean);
    printOperations(bean);
  }

  private void printDescription(MBean bean)
  {
    printEmptyLine();
    description.setSingleRow(bean);
    description.printWithoutHeader();
    printEmptyLine();
  }

  private void printNameAndType(MBean bean)
  {
    properties.clear();
    properties.addRow(Pair.of("Name", bean.name().fullQualifiedName()));
    properties.addRow(Pair.of("Type", bean.type()));
    properties.printWithoutHeader();
    printEmptyLine();
  }

  private void printAttributes(MBean bean)
  {
    if (!bean.attributes().isEmpty())
    {
      printSubTitle("Attributes:");

      attributes.setRows(bean.attributes());
      attributes.printWithoutHeader();
      printEmptyLine();
    }
  }

  private void printOperations(MBean bean)
  {
    if (!bean.operations().isEmpty())
    {
      printSubTitle("Operations:");
      operations.setRows(bean.operations());
      operations.printWithoutHeader();
    }
  }
    
  private static Table<MBean> declareDescriptionTable()
  {
    Table<MBean> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, bean -> bean.description())
          .multiLine()
          .withCellStyle(Styles.DESCRIPTION)
          .withMinWidth(8)
          .toColumn());
    return table;
  }

  private static Table<Pair<String,String>> declarePropertiesTable()
  {
    Table<Pair<String, String>> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT)
          .withCellStyle(Styles.NAME)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Value", 60, pair -> pair.getValue())
          .multiLine()
          .withCellStyle(Styles.VALUE)
          .withMinWidth(8)
          .toColumn());
    return table;
  }
  
  private static Table<MAttribute> declareAttributesTable()
  {
    Table<MAttribute> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, attribute -> attribute.name())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.NAME)
          .toColumn());
    table.addColumn(
        table.createColumn("Type", 60, attribute -> attribute.type())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.VALUE)
          .toColumn());
    return table;
  }

  private static Table<MOperation> declareOperationsTable()
  {
    Table<MOperation> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, operation -> operation.name())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.NAME)
          .toColumn());
    table.addColumn(
        table.createColumn("Signature", 60, operation -> operation.signature())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.VALUE)
          .toColumn());
    return table;
  }

  
}
