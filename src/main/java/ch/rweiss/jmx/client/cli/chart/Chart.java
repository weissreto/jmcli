package ch.rweiss.jmx.client.cli.chart;

import picocli.CommandLine.Command;

@Command(name = "chart", description="Draws a chart", subcommands = {
    ChartUserDefined.class})

public class Chart
{

}
