module ch.rweiss.jmcli
{
  requires ch.rweiss.terminal.chart;
  requires ch.rweiss.terminal.nativ;
  requires ch.rweiss.jmx.client;
  requires ch.rweiss.check;
  
  requires info.picocli;
  requires org.apache.commons.lang3;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires jackson.annotations;
  requires snakeyaml;
  
  opens ch.rweiss.jmcli to info.picocli;
  opens ch.rweiss.jmcli.chart to info.picocli;
  opens ch.rweiss.jmcli.dashboard to info.picocli;
  opens ch.rweiss.jmcli.info to info.picocli;
  opens ch.rweiss.jmcli.invoke to info.picocli;
  opens ch.rweiss.jmcli.list to info.picocli;
  opens ch.rweiss.jmcli.set to info.picocli;
  
  opens ch.rweiss.jmcli.dashboard.config to com.fasterxml.jackson.databind;
  opens ch.rweiss.jmcli.chart.config to com.fasterxml.jackson.databind;
}