package ch.rweiss.jmcli;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.terminal.table.RowSorter;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Option;

public class SortColumnOption
{
  private static final String SEPARATOR = ":";
  
  @Option(names = {"-s", "--sort"}, description = "Sorts the table with the given column. Use <column name>:ASC or <columnName>:DESC to specify ording direction. Default is DESCENDING")
  protected String sort;

  public SortColumnOption(String columnName, Direction direction)
  {
    sort = columnName + SEPARATOR + direction.name();
  }
  
  public String getSortColumn()
  {
    return StringUtils.substringBefore(sort, SEPARATOR);
  }
  
  public Direction getSortDirection()
  {
    return Direction.toDirection(StringUtils.substringAfter(sort, SEPARATOR));
  }
  
  public void sort(Table<?> table)
  {
    RowSorter<?> sorter = table.sortColumn(getSortColumn());
    if (getSortDirection() == Direction.ASCENDING)
    {
      sorter.ascending();
    }
    else
    {
      sorter.descending();
    }
  }
  
  public static enum Direction
  {
    ASCENDING,
    DESCENDING;
    
    public static Direction toDirection(String direction)
    {
      if (StringUtils.isBlank(direction))
      {
        return DESCENDING;
      }
      direction = direction.toLowerCase();
      if (StringUtils.startsWith(direction, "d"))
      {
        return DESCENDING;
      }
      return ASCENDING;
    }
  }
}
