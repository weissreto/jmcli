module ch.rweiss.jmx.client.cli
{
  requires ch.rweiss.terminal.chart;
  requires ch.rweiss.terminal.nativ;
  requires ch.rweiss.jmx.client;
  requires ch.rweiss.check;
  
  requires info.picocli;
  requires org.apache.commons.lang3;
  requires jackson.dataformat.yaml;
  requires jackson.core;
  requires com.fasterxml.jackson.databind;
  requires jackson.annotations;
  requires snakeyaml;
  
  opens ch.rweiss.jmx.client.cli to info.picocli;
  opens ch.rweiss.jmx.client.cli.chart to info.picocli;
  opens ch.rweiss.jmx.client.cli.dashboard to info.picocli;
  opens ch.rweiss.jmx.client.cli.info to info.picocli;
  opens ch.rweiss.jmx.client.cli.invoke to info.picocli;
  opens ch.rweiss.jmx.client.cli.list to info.picocli;
  opens ch.rweiss.jmx.client.cli.set to info.picocli;
  
  opens ch.rweiss.jmx.client.cli.dashboard.config to com.fasterxml.jackson.databind;
  opens ch.rweiss.jmx.client.cli.chart.config to com.fasterxml.jackson.databind;
}