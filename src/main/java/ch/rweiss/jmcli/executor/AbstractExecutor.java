package ch.rweiss.jmcli.executor;

import ch.rweiss.check.Check;
import ch.rweiss.jmcli.ui.CommandUi;

public class AbstractExecutor
{
  private CommandUi ui = new CommandUi();
  private String name;
  
  AbstractExecutor(String name)
  {
    Check.parameter("name").withValue(name).isNotBlank();
    this.name = name;
  }

  public String name()
  {
    return name;
  }
  
  public CommandUi ui()
  {
    return ui;
  }
  
  protected void printHeader()
  {
    ui.printHeader(name);
  }
}
