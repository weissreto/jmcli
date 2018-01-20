package ch.rweiss.jmx.client.cli.chart.config;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.cli.CommandException;
import ch.rweiss.terminal.chart.unit.Unit;

public class UnitConverter
{
  private String unitSymbolOrName;

  public UnitConverter(String unitSymbolOrName)
  {
    this.unitSymbolOrName = unitSymbolOrName;
  }

  public Unit toUnit()
  {
    if (StringUtils.isBlank(unitSymbolOrName))
    {
      return Unit.NONE;
    }
    unitSymbolOrName = StringUtils.replace(unitSymbolOrName, "_", " ");
    for (Unit unit : Unit.ALL)
    {
      if (unitSymbolOrName.equals(unit.symbol()))
      {
        return unit;
      }
      if (unitSymbolOrName.equalsIgnoreCase(unit.name()))
      {
        return unit;
      }
    }
    throw new CommandException("Unkown unit ''{0}''.\n" +
        "Either specify a unit symbol (e.g. ms, MB, %, etc)\n"+
        "or a unit name (e.g. milli seconds, mega bytes, percentage, etc)", unitSymbolOrName); 
  }
}
