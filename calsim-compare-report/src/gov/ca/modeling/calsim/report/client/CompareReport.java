package gov.ca.modeling.calsim.report.client;

import gov.ca.modeling.calsim.report.client.display.CompareReportDisplay;
import gov.ca.modeling.calsim.report.shared.ChartData;
import gov.ca.modeling.calsim.report.shared.ChartDataPoint;
import gov.ca.modeling.calsim.report.shared.ChartsData;
import gov.ca.modeling.calsim.report.shared.ReportData;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.AnnotatedLegendPosition;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.Options;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.ScaleType;

/**
 * A simple report template which relies on the data for the report in JSON
 * format in a relative directory
 */
public class CompareReport implements EntryPoint {

	private CompareReportDisplay display;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Runnable onLoad = new Runnable() {
			@Override
			public void run() {
				//
				display = new CompareReportDisplay();
				buildCharts();
				RootLayoutPanel.get().add(display);
			}
		};
		VisualizationUtils.loadVisualizationApi("1", onLoad,
				AnnotatedTimeLine.PACKAGE, LineChart.PACKAGE, Table.PACKAGE);
	}

	protected void buildCharts() {
		display.clear();
		JsArray<ChartData> timeSeries = ChartsData.get();
		int count = timeSeries.length();
		for (int i = 0; i < count; i++) {
			ChartData chartData = timeSeries.get(i);
			if (chartData.getPlotType().equals(ChartData.TIME_SERIES)) {
				display.add(buildTimeSeriesPanel(chartData));
			} else {
				display.add(buildExceedancePanel(chartData));
			}
		}
	}

	private FlowPanel buildExceedancePanel(ChartData chartData) {
		com.google.gwt.visualization.client.visualizations.LineChart.Options options = LineChart.Options
				.create();
		int width = Math.round(600);
		int height = Math.round(420);
		options.setSize(width, height);
		options.setLegend(LegendPosition.RIGHT);
		DataTable table = DataTable.create();
		table.addColumn(ColumnType.NUMBER, chartData.getTitle());
		JsArrayString seriesNames = chartData.getSeriesNames();
		table.addColumn(ColumnType.NUMBER, seriesNames.get(0));
		table.addColumn(ColumnType.NUMBER, seriesNames.get(1));
		JsArray<ChartDataPoint> dataArray = chartData.getValues();
		int npoints = dataArray.length();
		table.addRows(npoints);
		for (int i = 0; i < npoints; i++) {
			ChartDataPoint array = dataArray.get(i);
			table.setValue(i, 0, array.getX());
			table.setValue(i, 1, array.getY1());
			table.setValue(i, 2, array.getY2());
		}
		LineChart chart = new LineChart(table, options);
		Table.Options tableOptions = Table.Options.create();
		tableOptions.setAlternatingRowStyle(true);
		tableOptions.setHeight(height + "px");
		tableOptions.setWidth(width + "px");
		Table displayTable = new Table(table, tableOptions);
		TabPanel tabPanel = new TabPanel();
		tabPanel.add(chart, "Chart");
		tabPanel.add(displayTable, "Table");
		tabPanel.selectTab(0);
		FlowPanel panel = new FlowPanel();
		panel.add(tabPanel);
		panel.setHeight("600px");
		panel.setWidth("100%");
		return panel;
	}

	private FlowPanel buildTimeSeriesPanel(ChartData timeSeriesData) {
		int width = Math.round(600);
		int height = Math.round(420);
		Options options = Options.create();
		options.setDisplayAnnotations(false);
		// options.setScaleColumns(0, 1);
		options.setScaleType(ScaleType.ALLMAXIMIZE);
		DataTable table = DataTable.create();
		table.addColumn(ColumnType.DATE, "Date");
		JsArrayString seriesNames = timeSeriesData.getSeriesNames();
		table.addColumn(ColumnType.NUMBER, seriesNames.get(0));
		table.addColumn(ColumnType.NUMBER, seriesNames.get(1));
		AnnotatedTimeLine chart = new AnnotatedTimeLine(table, options, width
				+ "px", height + "px");
		options.setLegendPosition(AnnotatedLegendPosition.SAME_ROW);
		VerticalPanel vpanel = new VerticalPanel();
		vpanel.add(new HTML("<h3>Location: " + timeSeriesData.getTitle()
				+ "</h3>"));
		JsArray<ChartDataPoint> dataArray = timeSeriesData.getValues();
		int npoints = dataArray.length();
		table.addRows(npoints * 2);
		ChartDataPoint array = null;
		for (int i = 0; i < npoints; i++) {
			array = dataArray.get(i);
			if (array != null) {
				table.setValue(i, 0, new Date(Math.round(array.getX())));
			}
			table.setValue(i, 0, new Date(Math.round(array.getX())));
			table.setValue(i, 1, array.getY1());
			table.setValue(i, 2, array.getY2());
		}
		vpanel.add(chart);
		Table.Options tableOptions = Table.Options.create();
		tableOptions.setAlternatingRowStyle(true);
		Table displayTable = new Table(table, tableOptions);
		TabPanel tabPanel = new TabPanel();
		tabPanel.add(vpanel, "Chart");
		tabPanel.add(displayTable, "Table");
		tabPanel.selectTab(0);
		FlowPanel container = new FlowPanel();
		container.add(tabPanel);
		container.setHeight("600px");
		container.setWidth("100%");
		return container;
	}

	private FlowPanel buildReportOverviewPanel(ReportData data) {
		FlexTable table = new FlexTable();
		table.setHTML(1, 0, "Report Name");
		table.setHTML(1, 1, data.getReportName());
		table.setHTML(2, 0, "Base Study");
		table.setHTML(2, 1, data.getBaseStudyName());
		table.setHTML(3, 0, "Alternate Study");
		table.setHTML(3, 1, data.getAlternateStudyName());
		ScrollPanel scrollPanel = new ScrollPanel(table);
		FlowPanel panel = new FlowPanel();
		panel.add(scrollPanel);
		return panel;
	}
}
