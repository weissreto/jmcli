package ch.weiss.jmx.client.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.weiss.jmx.client.MAttribute;
import ch.weiss.jmx.client.MBean;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="attribute", description="Prints information about attributes")
public abstract class AbstractAttributeCommand extends AbstractBeanCommand
{
  @Parameters(index="1..*", paramLabel="ATTRIBUTE", description="Attribute name or filter with wildcards. E.g Count, Total*")
  private List<String> attributeFilters = new ArrayList<>();
  private WildcardFilters filters;
  
  @Override
  public void run()
  {
    filters = new WildcardFilters(attributeFilters);
    super.run();
  }

  protected List<MAttribute> getFilteredAttributes(MBean bean)
  {
    return bean
        .attributes()
        .stream()
        .filter(attribute -> filters.matches(attribute.name()))
        .collect(Collectors.toList());
  }
}

