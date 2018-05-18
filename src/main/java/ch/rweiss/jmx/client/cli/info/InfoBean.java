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
  private Table<MBean> nameTitle = declareNameTitleTable();
  private Table<MBean> description = declareDescriptionTable();
  private Table<Pair<String, String>> properties = declarePropertiesTable();
  private Table<String> attribAndOpTitle = declareAttribsAndOpsTable();
  private Table<MAttribute> attributes = declareAttributesTable();
  private Table<MOperation> operations = declareOperationsTable();

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
    printNameAndType(bean);
    printAttributes(bean);
    printOperations(bean);
  }

  private void printNameTitle(MBean bean)
  {
    printEmptyLine();
    nameTitle.setSingleRow(bean);
    nameTitle.printWithoutHeader();
    printEmptyLine();
  }

  private void printDescription(MBean bean)
  {
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
      attribAndOpTitle.setSingleRow("Attributes:");
      attribAndOpTitle.printWithoutHeader();

      attributes.setRows(bean.attributes());
      attributes.printWithoutHeader();
      printEmptyLine();
    }
  }

  private void printOperations(MBean bean)
  {
    if (!bean.operations().isEmpty())
    {
      attribAndOpTitle.setSingleRow("Operations:");
      attribAndOpTitle.printWithoutHeader();
      operations.setRows(bean.operations());
      operations.printWithoutHeader();
    }
  }
  
  private static Table<MBean> declareNameTitleTable()
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
  
  private static Table<String> declareAttribsAndOpsTable()
  {
    Table<String> table = new Table<>();
    table.addColumn(
        table.createColumn("Title", 20, title -> title)
          .withAbbreviateStyle(AbbreviateStyle.RIGHT)
          .withCellStyle(Styles.SUB_TITLE)
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
