package ch.rweiss.jmcli.dashboard.config;

public class Section
{
  private int row;
  private int column;
  private String chart;
  
  public int getRow()
  {
    return row;
  }
  
  public void setRow(int row)
  {
    this.row = row;
  }

  public int getColumn()
  {
    return column;
  }
  
  public void setColumn(int column)
  {
    this.column = column;
  }
  
  public String getChart()
  {
    return chart;
  }

  public void setChart(String chart)
  {
    this.chart = chart;
  }
}
