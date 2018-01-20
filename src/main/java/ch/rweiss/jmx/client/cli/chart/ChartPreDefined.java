package ch.rweiss.jmx.client.cli.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.rweiss.jmx.client.cli.CommandException;
import ch.rweiss.jmx.client.cli.chart.config.ChartConfig;
import ch.rweiss.jmx.client.cli.chart.config.ChartConfigLoader;
import ch.rweiss.jmx.client.cli.chart.config.Serie;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannel;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannelFactory;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannelScanner;
import ch.rweiss.jmx.client.cli.chart.data.channel.DataChannelSpecification;
import ch.rweiss.terminal.Color;
import ch.rweiss.terminal.Position;
import ch.rweiss.terminal.chart.XYChart;
import ch.rweiss.terminal.chart.serie.Axis;
import ch.rweiss.terminal.chart.serie.DataSerie;
import ch.rweiss.terminal.chart.serie.RollingTimeSerie;
import ch.rweiss.terminal.chart.unit.Unit;
import ch.rweiss.terminal.graphics.Point;
import ch.rweiss.terminal.graphics.Rectangle;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="pre", description="Predefined chart")
public class ChartPreDefined extends AbstractJmxClientCommand
{
  @Option(names = {"-H", "--height"}, description = "Height of the chart")
  private int height = -1;
  
  @Option(names = {"-w", "--width"}, description = "Width of the chart")
  private int width = -1;

  @Parameters(index="0", arity="1", paramLabel="CHART", description="Name of the predefined chart that should be displayed")
  private String chartName;
  
  private ChartConfig config;
  private XYChart chart;
  private DataChannelScanner scanner = new DataChannelScanner();
  private List<DataChannelSerie> dataChannelSeries = new ArrayList<>();
  
  @Override
  public void run()
  {
    config = new ChartConfigLoader().load(chartName);
    super.run();
  }

  @Override
  protected void printTitle()
  {
    term.write(config.getTitle());
  }
  
  @Override
  protected void execute()
  {
    ensureChart();

    term.clear().screen();
    
    scanner.scanNow();
    for (DataChannelSerie channel : dataChannelSeries)
    {
      channel.addDataPoint();
    }
    chart.paint();
  }

  private void ensureChart()
  {
    if (chart == null)
    {
      DataChannelFactory factory = new DataChannelFactory(jmxClient);
      for (Serie serieConfig : config.getSeries())
      {        
        DataChannelSpecification specification = new DataChannelSpecification(serieConfig.getDataChannel());
        List<DataChannel> dataChannels = factory.createFor(specification, serieConfig.getName());
        for (DataChannel dataChannel : dataChannels)
        {
          Axis yAxis = new Axis(dataChannel.name(), toUnit(serieConfig.getUnit()));
          RollingTimeSerie serie = new RollingTimeSerie(yAxis, 60, TimeUnit.SECONDS, toColor(serieConfig.getColor()));
          dataChannelSeries.add(new DataChannelSerie(dataChannel, serie));
          scanner.add(dataChannel);
        }
      }      
      chart = new XYChart(config.getTitle(), getChartWindow(), 
          dataChannelSeries.stream().map(channel -> channel.serie).toArray(DataSerie[]::new));
    }
    else
    {
      chart.setWindow(getChartWindow());
    }
  }

  private static Color toColor(String color)
  {
    if ("RED".equals(color))
    {
      return Color.RED;
    }
    if ("YELLOW".equals(color))
    {
      return Color.YELLOW;
    }
    return Color.WHITE;
  }

  private static Unit toUnit(String unit)
  {
    if (StringUtils.isBlank(unit))
    {
      return Unit.NONE;
    }
    Unit u = Unit.fromSymbol(unit);
    if (u != null)
    {
      return u;
    }
    throw new CommandException("Unknown unit {0}", unit);
  }

  private Rectangle getChartWindow()
  {
    int w = width;
    int h = height;
    
    if (width < 0 || height < 0)
    {
      Position maxPosition = term.cursor().maxPosition();
      if (width < 0)
      {
        w = maxPosition.column()-1;
      }
      if (height < 0)
      {
        h = maxPosition.line()-1;
      }        
    }
    return new Rectangle(Point.ORIGIN, w, h);
  }

  private static class DataChannelSerie
  {
    private final DataChannel dataChannel;
    private final RollingTimeSerie serie;

    private DataChannelSerie(DataChannel dataChannel, RollingTimeSerie serie)
    {
      this.dataChannel = dataChannel;
      this.serie = serie;
    }
    
    private void addDataPoint()
    {
      serie.addDataPoint(toLong(dataChannel.value()));
    }

    private static long toLong(Object value)
    {
      if (value instanceof Long)
      {
        return (long)value;
      }
      if (value instanceof Number)
      {
        return ((Number) value).longValue();
      }
      return -1;
    }
  }
}
