package ch.rweiss.jmcli;

import picocli.CommandLine.Option;

public class CommonOptions
{
	@Option(names = { "-h", "--help"}, usageHelp = true, description = "Display this help and exit")
    private boolean help;
    
	@Option(names = { "-V", "--version"}, versionHelp = true, description = "Display version info and exit")
    private boolean version;
	
	@Option(names = {"-v", "--verbose"}, description = "Display details message")
    private boolean verbose;
}
