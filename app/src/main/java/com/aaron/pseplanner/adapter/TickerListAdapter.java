package com.aaron.pseplanner.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.activity.CreateTradePlanActivity;
import com.aaron.pseplanner.bean.Ticker;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.constant.IntentRequestCode;
import com.aaron.pseplanner.listener.ListRowOnTouchChangeActivity;
import com.aaron.pseplanner.service.implementation.DefaultFormatService;
import com.aaron.pseplanner.service.FormatService;

import java.util.List;

/**
 * Created by aaron.asuncion on 12/8/2016.
 * Contains all tickers, and is responsible for converting Ticker bean to a UI row in the ListView.
 */
public class TickerListAdapter extends ArrayAdapter<Ticker>
{
    private Activity activity;
    private FormatService formatService;

    public TickerListAdapter(Activity activity, List<Ticker> tickerList)
    {
        super(activity, 0, tickerList);

        this.activity = activity;
        this.formatService = new DefaultFormatService(activity);
    }

    /**
     * Populates the ListView.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        ViewHolder holder;

        if(convertView == null)
        {
            convertView = this.activity.getLayoutInflater().inflate(R.layout.fragment_ticker_row, parent, false);

            holder = new ViewHolder();
            holder.stock = (TextView) convertView.findViewById(R.id.ticker_stock_row);
            holder.price = (TextView) convertView.findViewById(R.id.ticker_price_row);
            holder.change = (TextView) convertView.findViewById(R.id.ticker_change_row);
            holder.percentChange = (TextView) convertView.findViewById(R.id.ticker_percent_change_row);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.list_row_layout);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Ticker ticker = getItem(position);
        holder.setTickerView(ticker, this.formatService, new ListRowOnTouchChangeActivity(this.activity, CreateTradePlanActivity.class, DataKey.EXTRA_TICKER, ticker, IntentRequestCode.CREATE_TRADE_PLAN, holder.layout));

        return convertView;
    }

    /**
     * Holds the references of all the views in a list row, to improve performance by preventing repeated call of findViewById().
     */
    private static class ViewHolder
    {
        TextView stock;
        TextView price;
        TextView change;
        TextView percentChange;
        LinearLayout layout;

        void setTickerView(Ticker ticker, FormatService service, View.OnTouchListener listener)
        {
            layout.setOnTouchListener(listener);
            stock.setText(ticker.getSymbol());
            price.setText(service.formatStockPrice(ticker.getCurrentPrice().doubleValue()));
            change.setText(service.formatStockPrice(ticker.getChange().doubleValue()));
            String percentChangeText = service.formatStockPrice(ticker.getPercentChange().doubleValue()) + "%";
            percentChange.setText(percentChangeText);

            service.formatTextColor(ticker.getChange().doubleValue(), price);
            service.formatTextColor(ticker.getChange().doubleValue(), change);
            service.formatTextColor(ticker.getChange().doubleValue(), percentChange);
        }
    }
}