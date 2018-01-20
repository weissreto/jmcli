package ch.rweiss.jmx.client.cli.chart.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.bitset.IntBitSet;
import ch.rweiss.jmx.client.cli.CommandException;
import ch.rweiss.terminal.Color;

class ColorConverter
{
  private static final Map<String, Color> STANDARD_COLORS = new HashMap<>();
  static
  {
    STANDARD_COLORS.put("BLACK", Color.BLACK);
    STANDARD_COLORS.put("RED", Color.RED);
    STANDARD_COLORS.put("GREEN", Color.GREEN);
    STANDARD_COLORS.put("YELLOW", Color.YELLOW);
    STANDARD_COLORS.put("BLUE", Color.BLUE);
    STANDARD_COLORS.put("MAGENTA", Color.MAGENTA);
    STANDARD_COLORS.put("CYAN", Color.CYAN);
    STANDARD_COLORS.put("WHITE", Color.WHITE);

    STANDARD_COLORS.put("BRIGHT_BLACK", Color.BRIGHT_BLACK);
    STANDARD_COLORS.put("BRIGHT_RED", Color.BRIGHT_RED);
    STANDARD_COLORS.put("BRIGHT_GREEN", Color.BRIGHT_GREEN);
    STANDARD_COLORS.put("BRIGHT_YELLOW", Color.BRIGHT_YELLOW);
    STANDARD_COLORS.put("BRIGHT_BLUE", Color.BRIGHT_BLUE);
    STANDARD_COLORS.put("BRIGHT_MAGENTA", Color.BRIGHT_MAGENTA);
    STANDARD_COLORS.put("BRIGHT_CYAN", Color.BRIGHT_CYAN);
    STANDARD_COLORS.put("BRIGHT_WHITE", Color.BRIGHT_WHITE);
  }
  
  private String color;
  
  ColorConverter(String color)
  {
    this.color = color == null ? null : color.trim();
  }
  
  Color toColor()
  {
    if (StringUtils.isBlank(color))
    {
      return null;
    }    
    Color standardColor = STANDARD_COLORS.get(color.toUpperCase());
    if (standardColor != null)
    {
      return standardColor;
    }
    
    if (color.length() == 7 && StringUtils.startsWith(color, "#"))
    {
      IntBitSet rgb = IntBitSet.fromInt(Integer.parseInt(StringUtils.substringAfter(color, "#"), 16));
      int red = rgb.subSet(16,23).toInt(); 
      int green = rgb.subSet(8,15).toInt();
      int blue = rgb.subSet(0,7).toInt();
      return new Color(red, blue, green);
    }
    throw new CommandException("Unknown color ''{0}''.\nEither specify color name (e.g. RED, YELLOW, ...)\nor rgb code (e.g. #FF0000 for red)", color); 
  }
  
}
