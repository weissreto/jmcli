package ch.rweiss.jmcli.executor;

import ch.rweiss.jmcli.ui.CommandUi;

public abstract class AbstractSimpleExecutor extends AbstractExecutor
{
  
  protected AbstractSimpleExecutor(String name)
  {
    super(name);
  }
  
  /**
   * Execute  
   * @param ui
   */
  protected abstract void execute(CommandUi ui);

  
  public void execute()
  {
    try
    {
      printHeader();
      execute(ui());
    }
    finally
    {
      ui().terminal().reset();
    }
  }
}
