package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StockWidgetService extends RemoteViewsService {
    private static final String LOG_TAG=StockWidgetService.class.getSimpleName();
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data=null;
            @Override
            public void onCreate() {
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
            }

            @Override
            public void onDataSetChanged() {
                if(data!=null){
                    data.close();

                }
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Log.d(LOG_TAG,"count: "+data.getCount()+", columns: "+data.getColumnCount());
            }

            @Override
            public void onDestroy() {
                if(data!=null){
                    data.close();
                }
            }

            @Override
            public int getCount() {
                if(data!=null){
                    return data.getCount();
                }
                return 0;
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position== AdapterView.INVALID_POSITION||data==null){
                    return null;
                }
                data.moveToFirst();
                data.moveToPosition(position);
                RemoteViews views=new RemoteViews(getPackageName(), R.layout.list_item_quote);
                String symbol=data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                views.setTextViewText(R.id.stock_symbol,symbol);
                views.setTextViewText(R.id.bid_price,data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
                views.setTextViewText(R.id.change,data.getString(data.getColumnIndex(QuoteColumns.CHANGE)));

                if (data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return data.getInt(data.getColumnIndex(QuoteColumns._ID));
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
