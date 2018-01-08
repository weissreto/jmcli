package ch.rweiss.jmx.client.cli.info;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.rweiss.jmx.client.cli.AbstractBeanCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.jmx.client.cli.WildcardFilters;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.jmx.client.MParameter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="operation", description="Prints information about operations")
public class InfoOperation extends AbstractBeanCommand
{
  @Parameters(index="1..*", paramLabel="OPERATION", description="Operation name or filter with wildcards. E.g gc, getThread*")
  private List<String> operationFilters = new ArrayList<>();
  private WildcardFilters filters;

  @Override
  public void run()
  {
    filters = new WildcardFilters(operationFilters);
    super.run();
  }
  
  @Override
  protected void printTitle()
  {
    term.write("Operation Info");
  }

  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      List<MOperation> operations = getFilteredOperations(bean);
      if (!operations.isEmpty())
      {
        printBeanNameTitle(bean);
        for (MOperation op : operations)
        {
          print(op);
        }
      }
    }
  }

  private List<MOperation> getFilteredOperations(MBean bean)
  {
    return bean
      .operations()
      .stream()
      .filter(operation -> filters.matches(operation.name()))
      .collect(Collectors.toList());
  }
  
  private void printBeanNameTitle(MBean bean)
  {
    term.newLine();
    printNameTitle(bean.name().fullQualifiedName());
    term.newLine();
  }

  private void print(MOperation op)
  {
    printNameTitle(op);
    printDescription(op);
    printName(op);
    printSignature(op);
    printImpact(op);
    printReturnType(op);
    printParameters(op);
  }

  private void printNameTitle(MOperation op)
  {
    term.newLine();
    printNameTitle(2, op.signature());
    term.newLine();
  }
  
  private void printDescription(MOperation op)
  {
    printFirstColumn(2, Styles.DESCRIPTION, op.description());
    term.newLine();
    term.newLine();
  }
  
  private void printName(MOperation op)
  {
    printNameValue(2, "Name", op.name());
  }

  private void printSignature(MOperation op)
  {
    printNameValue(2, "Signature", op.signature());
  }

  private void printImpact(MOperation op)
  {
    printNameValue(2, "Impact", op.impact().toString());
  }

  private void printReturnType(MOperation op)
  {
    printNameValue(2, "ReturnType", op.returnType());
  }

  private void printParameters(MOperation op)
  {
    if (!op.parameters().isEmpty())
    {
      term.newLine();
      printFirstColumn(2, Styles.SUB_TITLE, "Parameters:");
      term.newLine();
      for (MParameter param : op.parameters())
      {
        printParameter(param);
      }
    }
  }

  private void printParameter(MParameter param)
  {
    printName(4, param.name());
    term.newLine();
    printNameValue(6, "Type", param.type());
    printNameValue(6, "Description", param.description());
    term.newLine();
  }
}

