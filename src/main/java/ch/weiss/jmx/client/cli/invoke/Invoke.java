package ch.weiss.jmx.client.cli.invoke;

import ch.weiss.jmx.client.cli.AbstractCommand;
import picocli.CommandLine.Command;

@Command(name = "invoke", description="Invokes operation", subcommands = {
    InvokeOperation.class})
public class Invoke extends AbstractCommand
{
  @Override
  public void run()
  {    
    new InvokeOperation().run();
  }
}
