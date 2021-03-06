package ch.rweiss.jmcli.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.rweiss.jmcli.AbstractCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.chart.Chart.DataChannelSerie;
import ch.rweiss.jmcli.chart.ColorGenerator;
import ch.rweiss.jmcli.chart.config.ChartConfig;
import ch.rweiss.jmcli.chart.config.ChartConfigLoader;
import ch.rweiss.jmcli.chart.config.Serie;
import ch.rweiss.jmcli.chart.data.channel.DataChannel;
import ch.rweiss.jmcli.chart.data.channel.DataChannelFactory;
import ch.rweiss.jmcli.chart.data.channel.DataChannelScanner;
import ch.rweiss.jmcli.chart.data.channel.DataChannelSpecification;
import ch.rweiss.jmcli.dashboard.config.DashboardConfig;
import ch.rweiss.jmcli.dashboard.config.DashboardConfigLoader;
import ch.rweiss.jmcli.dashboard.config.Section;
import ch.rweiss.jmcli.executor.AbstractJmxExecutor;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.terminal.Color;
import ch.rweiss.terminal.Position;
import ch.rweiss.terminal.chart.XYChart;
import ch.rweiss.terminal.chart.serie.Axis;
import ch.rweiss.terminal.chart.serie.DataSerie;
import ch.rweiss.terminal.chart.serie.RollingTimeSerie;
import ch.rweiss.terminal.graphics.Point;
import ch.rweiss.terminal.graphics.Rectangle;
import ch.rweiss.terminal.widget.Grid;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

public class Dashboard extends AbstractJmxExecutor
{
  @Command(name = "dashboard", description="Draws a dashboard")
  public static final class Cmd extends AbstractCommand
  {
    @Parameters(index="0", arity="1", paramLabel="DASHBOARD", 
      description="Name of the dashboard that should be displayed")
    private String dashboardName;
    
    @Mixin
    private IntervalOption intervalOption = new IntervalOption(1);
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();

    @Override
    public void run()
    {
      new Dashboard(this).execute();
    }
  }
  
  private Grid grid;
  
  private DataChannelScanner scanner = new DataChannelScanner();
  private List<DataChannelSerie> dataChannelSeries = new ArrayList<>();
  private ColorGenerator colorGenerator = new ColorGenerator();

  private Cmd command;

  public Dashboard(Cmd command)
  {
    super("Dashboard", command.intervalOption, command.jvmOption);
    this.command = command;
  }
  
  @Override
  protected void printHeader()
  {
    // Do not print header
  }
  
  @Override
  protected void execute(CommandUi ui, JmxClient jmxClient)
  {
    ui.terminal().offScreen().on();
    try
    {
      ensureGrid();
      scanner.scanNow();
      for (DataChannelSerie channel : dataChannelSeries)
      {
        channel.addDataPoint();
      }
      
      grid.bounds(windowBounds());
      grid.paint(ui.terminal().graphics());
      ui.terminal().offScreen().syncToScreen();
    }
    finally
    {
      ui.terminal().offScreen().off();
    }
  }

  private void ensureGrid()
  {
    if (grid == null)
    {
      DashboardConfig config = new DashboardConfigLoader().load(command.dashboardName);
      List<XYChart> charts = new ArrayList<>(config.getSections().size());
      int rows=0;
      int columns=0;
      for (Section section : config.getSections())
      {
        rows = Math.max(rows,  section.getRow()+1);
        columns = Math.max(columns, section.getColumn()+1);
      }
      for (Section section : config.getSections())
      {
        XYChart chart = createChart(section.getChart());
        int pos = section.getRow()*columns+section.getColumn();
        if (pos < charts.size())
        {
          charts.set(pos, chart);
        }
        else
        {
          charts.add(pos, chart);
        }
        rows = Math.max(rows,  section.getRow()+1);
        columns = Math.max(columns, section.getColumn()+1);
      }
      grid = new Grid(rows, columns, charts);
    }
  }

  private XYChart createChart(String chartName)
  {
    ChartConfig config = new ChartConfigLoader().load(chartName);
    DataChannelFactory factory = new DataChannelFactory(jmxClient());
    List<DataChannelSerie> chartSeries = new ArrayList<>();
    for (Serie serieConfig : config.getSeries())
    {        
      DataChannelSpecification specification = new DataChannelSpecification(serieConfig.getDataChannel());
      List<DataChannel> dataChannels = factory.createFor(specification, serieConfig.getName());
      for (DataChannel dataChannel : dataChannels)
      {
        Axis yAxis = new Axis(dataChannel.name(), serieConfig.getUnit());
        RollingTimeSerie serie = new RollingTimeSerie(yAxis, 60, TimeUnit.SECONDS, ensureColor(serieConfig.getColor()));
        DataChannelSerie dataChannelSerie = new DataChannelSerie(dataChannel, serie);
        chartSeries.add(dataChannelSerie);
        dataChannelSeries.add(dataChannelSerie);
        scanner.add(dataChannel);
      }
    }      
    return new XYChart(config.getTitle(), windowBounds(), 
        chartSeries.stream().map(channel -> channel.serie).toArray(DataSerie[]::new));
  }

  private Color ensureColor(Color color)
  {
    if (color == null)
    {
      return colorGenerator.nextColor();
    }
    return color;
  }

  private Rectangle windowBounds()
  {
    Position maxPosition = ui().terminal().cursor().maxPosition();
    int w = maxPosition.column();
    int h = maxPosition.line();
    return new Rectangle(Point.ORIGIN, w, h);
  }
}
