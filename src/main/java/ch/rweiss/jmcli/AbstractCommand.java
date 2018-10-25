package ch.rweiss.jmcli;

import picocli.CommandLine.Option;

public abstract class AbstractCommand implements Runnable
{
  @Option(names = { "-V", "--version" }, versionHelp = true, description = "Print version information and exit")
  private boolean versionRequested;
  
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
  private boolean usageHelpRequested;
  
  @Option(names = {"-v", "--verbose"}, description = "Display details message")
  private boolean verbose;
}