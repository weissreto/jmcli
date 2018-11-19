package ch.rweiss.jmcli;

import picocli.CommandLine.Mixin;

public abstract class AbstractCommand implements Runnable
{
  @Mixin
  private CommonOptions commonOptions = new CommonOptions();
}
