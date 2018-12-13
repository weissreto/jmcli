package ch.rweiss.jmcli;

import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="attribute", description="Prints information about attributes")
public abstract class AbstractOperationCommand extends AbstractBeanCommand
{
  @Parameters(index="1..*", paramLabel="OPERATION", description="Operation name or filter with wildcards. E.g gc, getThread*")
  private List<String> operationFilters = new ArrayList<>();
  
  public WildcardFilters operationFilters()
  {
    return WildcardFilters.createForFilters(operationFilters);
  }
}

