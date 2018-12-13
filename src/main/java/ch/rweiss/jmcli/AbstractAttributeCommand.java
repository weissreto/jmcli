package ch.rweiss.jmcli;

import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="attribute", description="Prints information about attributes")
public abstract class AbstractAttributeCommand extends AbstractBeanCommand
{
  @Parameters(index="1..*", paramLabel="ATTRIBUTE", description="Attribute name or filter with wildcards. E.g Count, Total*")
  private List<String> attributeFilters = new ArrayList<>();
  
  public WildcardFilters attributeFilters()
  {
    return WildcardFilters.createForFilters(attributeFilters);
  }
}

