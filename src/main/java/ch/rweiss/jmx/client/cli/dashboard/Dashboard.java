package ch.rweiss.jmx.client.cli.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.rweiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.rweiss.jmx.client.cli.chart.Chart.DataChannelSerie;
import ch.rweiss.jmx.client.cli.chart.ColorGenerator;
import ch.rweiss.jmx.client.cli.chart.config.ChartConfig;
import ch.rweiss.jmx.client.cli.chart.config.ChartConfigLoader;
import ch.rweiss.jmx.client.cli.chart.config.Serie;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannel;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannelFactory;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannelScanner;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannelSpecification;
import ch.rweiss.jmx.client.cli.dashboard.config.DashboardConfig;
import ch.rweiss.jmx.client.cli.dashboard.config.DashboardConfigLoader;
import ch.rweiss.jmx.client.cli.dashboard.config.Section;
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
import picocli.CommandLine.Parameters;

@Command(name = "dashboard", description="Draws a dashboard")
public class Dashboard extends AbstractJmxClientCommand
{
  @Parameters(index="0", arity="1", paramLabel="DASHBOARD", 
      description="Name of the dashboard that should be displayed")
  private String dashboardName;
  private Grid grid;
  
  private DataChannelScanner scanner = new DataChannelScanner();
  private List<DataChannelSerie> dataChannelSeries = new ArrayList<>();
  private ColorGenerator colorGenerator = new ColorGenerator();

  Dashboard()
  {
    super("Dashboard");
  }
  
  @Override
  protected void printHeader()
  {
    // Do not print header
  }
  
  @Override
  protected void execute()
  {
    ensureGrid();

    term.clear().screen();
    
    scanner.scanNow();
    for (DataChannelSerie channel : dataChannelSeries)
    {
      channel.addDataPoint();
    }
    
    grid.bounds(windowBounds());
    grid.paint(term.graphics()); 
  }

  private void ensureGrid()
  {
    if (grid == null)
    {
      DashboardConfig config = new DashboardConfigLoader().load(dashboardName);
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
    DataChannelFactory factory = new DataChannelFactory(jmxClient);
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
    Position maxPosition = term.cursor().maxPosition();
    int w = maxPosition.column();
    int h = maxPosition.line();
    return new Rectangle(Point.ORIGIN, w, h);
  }
}
