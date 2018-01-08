package ch.rweiss.jmx.client.cli;

import ch.rweiss.terminal.Color;
import ch.rweiss.terminal.FontStyle;
import ch.rweiss.terminal.Style;

public class Styles
{
  public static final Style TITLE = Style.create().withColor(Color.GREEN).withFontStyle(FontStyle.UNDERLINE).toStyle();
  public static final Style SUB_TITLE = Style.create().withColor(Color.GREEN).toStyle();
  public static final Style ID = Style.create().withColor(Color.YELLOW).toStyle();
  public static final Style NAME = Style.create().withColor(Color.WHITE).toStyle();
  public static final Style NAME_TITLE = Style.create().withColor(Color.WHITE).withFontStyle(FontStyle.UNDERLINE).toStyle();
  public static final Style DESCRIPTION = Style.create().withColor(Color.CYAN).toStyle();
  public static final Style VALUE = Style.create().withColor(Color.BRIGHT_WHITE).toStyle();
  public static final Style ERROR = Style.create().withColor(Color.BRIGHT_RED).toStyle();
}
