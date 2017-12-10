package ch.weiss.jmx.client.cli.invoke;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.weiss.jmx.client.MBean;
import ch.weiss.jmx.client.MOperation;
import ch.weiss.jmx.client.cli.AbstractBeanCommand;
import ch.weiss.jmx.client.cli.CommandException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="operation", description="Invokes an operation")
public class InvokeOperation extends AbstractBeanCommand
{
  @Parameters(index="1", paramLabel="OPERATION", description="The name or signature of the operation to call")
  private String operationName;

  @Parameters(index="2..*", paramLabel="PARAMETER", description="Operation parameters")
  private List<String> parameters = new ArrayList<>();

  @Override
  protected void printTitle()
  {
    term.write("Invoke Operation");
  }

  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      MOperation operation = findOperation(bean);
      if (operation != null)
      {
        term.newLine();
        printNameTitle(bean.name().fullQualifiedName());
        term.newLine();
        invoke(operation);
      }
    }
  }

  private MOperation findOperation(MBean bean)
  {
    if (isSignatureSpecified())
    {
      return findOperationWithSignature(bean);
    }
    return findOperationWithoutSignature(bean);
  }

  private MOperation findOperationWithSignature(MBean bean)
  {
    String operation = StringUtils.substringBefore(operationName, "(");
    String typesStr = StringUtils.substringBetween(operationName, "(", ")");
    String[] types = typesStr.split(",");
    return bean.operation(operation, types);
  }

  private boolean isSignatureSpecified()
  {
    return StringUtils.contains(operationName, "(") && StringUtils.contains(operationName, ")");
  }

  private MOperation findOperationWithoutSignature(MBean bean)
  {
    List<MOperation> operations = bean.operations(operationName);
    if (operations.isEmpty())
    {
      throw new CommandException("Operation {0} not found for bean {1}", operationName, bean.name().fullQualifiedName());
    }
    if (operations.size() == 1)
    {
      return operations.get(0);
    }
    operations = operations
        .stream()
        .filter(operation -> operation.parameters().size() == parameters.size())
        .collect(Collectors.toList());
    if (operations.isEmpty())
    {
      throw new CommandException("Operation {0} with {1} parameters not found for bean {2}", operationName, parameters.size(), bean.name().fullQualifiedName());
    }
    if (operations.size() == 1)
    {
      return operations.get(0);
    }
    throw new CommandException("More than one operation {0} with {1} parameters found for bean {2}. Please specify operation signature.", operationName, parameters.size(), bean.name().fullQualifiedName());
  }

  private void invoke(MOperation operation)
  {
    printNameValue("Invokeing Operation", operation.signature());
    printNameValue("with parameters", parameters.toString());
    String result = operation.invoke(parameters);
    printNameValue("Result", result);
  }


}
