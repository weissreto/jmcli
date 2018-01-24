package ch.rweiss.jmx.client.cli.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.rweiss.terminal.Color;

public class ColorGenerator
{
  private static final List<Color> COLORS;
  private static int nextColor = 0;
  
  static
  {
    List<Color> colors  = new ArrayList<>();
    colors.addAll(Color.BRIGHT_STANDARD_COLORS);
    colors.addAll(Color.STANDARD_COLORS);
    colors.remove(Color.BLACK);
    colors.remove(Color.BRIGHT_GREEN);
    colors.remove(Color.BRIGHT_BLACK);
    COLORS = Collections.unmodifiableList(colors);
  }

  public Color nextColor()
  {
    return COLORS.get(nextColor++%COLORS.size());
  }
}
