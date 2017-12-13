package ch.weiss.jmx.client.cli.list;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import ch.weiss.terminal.AnsiTerminal;
import ch.weiss.terminal.Style;

public class Table
{
  private final java.util.List<Column> columns = new ArrayList<>();
  private final java.util.List<Row> rows = new ArrayList<>();
  private Row currentRow;
  private AnsiTerminal term = AnsiTerminal.get();
  
  public void addColumn(String title, int width, Style titleStyle, Style cellStyle)
  {
    columns.add(new Column(title, width, titleStyle,cellStyle));
  }

  public void addRow()
  {
    currentRow = new Row();
    rows.add(currentRow);
  }

  public void addValue(String value)
  {
    currentRow.addCell(new Cell(value));
  }

  public void addValue(String value, Style style)
  {
    currentRow.addCell(new Cell(value, style));
  }

  public void print()
  {
    for (Column column : columns)
    {
      column.printTitle();
    }
    term.clear().lineToEnd();
    term.newLine();
    for (Row row : rows)
    {
      int colPos = 0;
      for (Column column : columns)
      {
        column.printCell(row.getCell(colPos++));
      }
      term.clear().lineToEnd();
      term.newLine();
    }
    term.clear().screenToEnd();
  }
  
  private class Column
  {
    private final String title;
    private final int width;
    private final Style titleStyle;
    private Style cellStyle;

    public Column(String title, int width, Style titleStyle, Style cellStyle)
    {
      this.title = title;
      this.width = width;
      this.titleStyle = titleStyle;
      this.cellStyle = cellStyle;
    }

    public void printCell(Cell cell)
    {
      if (cell.getStyle() != null)
      {
        term.style(cell.getStyle());
      }
      else
      {
        term.style(cellStyle);
      }
      String value = trim(cell.getValue());
      term.write(value);
      term.reset();
      term.write(whitespaces(value));
    }

    public void printTitle()
    {
      term.style(titleStyle);
      String trimmedTitle = trim(title);
      term.write(trimmedTitle);
      term.reset();
      term.write(whitespaces(trimmedTitle));
    }

    private String trim(String str)
    {
      return StringUtils.abbreviate(str, width-1);
    }

    private String whitespaces(String str)
    {
      StringBuilder builder = new StringBuilder();
      for (int pos = str.length(); pos < width; pos++)
      {
        builder.append(' ');
      }
      return builder.toString();
    }
  }

  private class Row
  {
    private final java.util.List<Cell> cells = new ArrayList<>();
    
    public void addCell(Cell cell)
    {
      cells.add(cell);
    }

    public Cell getCell(int pos)
    {
      return cells.get(pos);
    }
  }
  
  private class Cell
  {
    private final String value;
    private final Style style;

    public Cell(String value)
    {
      this(value, null);
    }

    public Cell(String value, Style style)
    {
      this.value = value;
      this.style = style;
    }
    
    public String getValue()
    {
      return value;
    }
    
    public Style getStyle()
    {
      return style;
    }
  }
}
