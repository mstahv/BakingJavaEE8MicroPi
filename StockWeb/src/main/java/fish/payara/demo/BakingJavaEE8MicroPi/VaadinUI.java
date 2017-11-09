/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fish.payara.demo.BakingJavaEE8MicroPi;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.Background;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotLine;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.annotations.Push;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.server.VaadinCDIServlet;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fish.payara.micro.cdi.ClusteredCDIEventBus;
import fish.payara.micro.cdi.Inbound;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author mstahv
 */
@CDIUI("")
@Push
public class VaadinUI extends UI {

    @Inject
    StockSessionManager mgr;

    Chart chart = new Chart(ChartType.SPLINE);
    DataSeries ls = new DataSeries();

    Label latestStockInfo = new Label();

    @Override
    protected void init(VaadinRequest request) {
        Configuration configuration = chart.getConfiguration();
        configuration.setSeries(ls);
        configuration.setTitle("");
        configuration.getChart().setBackgroundColor(new SolidColor(255,255,255,0));
        XAxis xAxis = configuration.getxAxis();
        xAxis.setType(AxisType.DATETIME);
        xAxis.setTickPixelInterval(150);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle("Price"));
        yAxis.setPlotLines(new PlotLine(0, 1, new SolidColor("#808080")));

        configuration.getTooltip().setEnabled(false);
        configuration.getLegend().setEnabled(false);
        setContent(
                new VerticalLayout(
                        new Label("<h1>Stockwatcher demo</h1>", ContentMode.HTML),
                        chart,
                        latestStockInfo
                )
        );
        mgr.registerSession(this);
    }

    @Override
    public void detach() {
        super.detach();
        mgr.deregisterSession(this);
    }

    public void showStock(Stock stock) {
        access(() -> {
            ls.add(
                    new DataSeriesItem(System.currentTimeMillis(), stock.getPrice()),
                    true,
                    (ls.getData().size() > 20));
            latestStockInfo.setValue(stock.getSymbol() + " : " + stock.getPrice());
        });
    }

    @WebServlet(urlPatterns = {"/vaadin-ui/*", "/VAADIN/*"}, name = "MyUIServlet", asyncSupported = true)
    public static class MyUIServlet extends VaadinCDIServlet {

    }
}
