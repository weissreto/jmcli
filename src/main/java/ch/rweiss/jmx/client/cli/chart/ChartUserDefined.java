package ch.rweiss.jmx.client.cli.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.rweiss.jmx.client.cli.CommandException;
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

@Command(name="user", description="User defined chart")
public class ChartUserDefined extends AbstractJmxClientCommand
{
  @Option(names = {"-d", "--delta"}, description = "Displays delta instead of absolute values")
  protected boolean delta;

  @Option(names = {"-H", "--height"}, description = "Height of the chart")
  private int height = -1;
  
  @Option(names = {"-w", "--width"}, description = "Width of the chart")
  private int width = -1;
  
  @Option(names = {"-u", "--unit"}, description = "Unit of the values")
  private String unit = "";
  
  @Option(names = {"-t", "--title"}, description = "Chart title")
  private String title = "";

  @Parameters(index="0", arity="0..*", paramLabel="VALUES", description="List of attribute names which values should be displayed")
  private List<String> beanAttributeNames = new ArrayList<>();

  private XYChart chart;
  private DataChannelScanner scanner = new DataChannelScanner();
  private List<DataChannelSerie> dataChannelSeries = new ArrayList<>();
  private static final List<Color> COLORS;
  static
  {
    List<Color> colors  = new ArrayList<>();
    colors.addAll(Color.BRIGHT_STANDARD_COLORS);
    colors.addAll(Color.STANDARD_COLORS);
    colors.remove(Color.BLACK);
    colors.remove(Color.BRIGHT_BLACK);
    COLORS = Collections.unmodifiableList(colors);
  }

  
  @Override
  protected void printTitle()
  {
    term.write(title);
  }
  
  @Override
  public void run()
  {
    super.run();
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
      int color=0;
      for (String beanAttributeName : beanAttributeNames)
      {        
        DataChannelSpecification specification = new DataChannelSpecification(beanAttributeName, delta);
        List<DataChannel> dataChannels = factory.createFor(specification);
        for (DataChannel dataChannel : dataChannels)
        {
          Axis yAxis = new Axis(dataChannel.name(), getUnit());
          RollingTimeSerie serie = new RollingTimeSerie(yAxis, 60, TimeUnit.SECONDS, COLORS.get(color++%COLORS.size()));
          dataChannelSeries.add(new DataChannelSerie(dataChannel, serie));
          scanner.add(dataChannel);
        }
      }      
      chart = new XYChart(title, getChartWindow(), 
          dataChannelSeries.stream().map(channel -> channel.serie).toArray(DataSerie[]::new));
    }
    else
    {
      chart.setWindow(getChartWindow());
    }
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

  private Unit getUnit()
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
      return -1;
    }
  }
}
