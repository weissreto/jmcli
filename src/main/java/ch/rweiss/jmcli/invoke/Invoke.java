package ch.rweiss.jmcli.invoke;

import ch.rweiss.jmcli.AbstractCommand;
import picocli.CommandLine.Command;

@Command(name = "invoke", description="Invokes operation", subcommands = {
    InvokeOperation.Cmd.class})
public class Invoke extends AbstractCommand
{
  @Override
  public void run()
  {    
    new InvokeOperation.Cmd().run();
  }
}
