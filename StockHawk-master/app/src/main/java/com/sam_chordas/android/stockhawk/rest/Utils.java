package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();
  public final static int SERVER_DOWN = 0;
  public final static int SERVER_UP = 1;
  public final static int NO_INTERNET_CONNECTION = 2;
  public final static int NO_DATA = 3;
  public final static int HAS_DATA = 4;
  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON) {
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try {
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0) {
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1) {
          jsonObject = jsonObject.getJSONObject("results")
                  .getJSONObject("quote");
          if(jsonObject.getString("Bid").equals("null")){
            return null;
          }
//          if()
          batchOperations.add(buildBatchOperation(jsonObject));
        } else {
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0) {
            for (int i = 0; i < resultsArray.length(); i++) {
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e) {
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }
  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));

      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
              jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-') {
        builder.withValue(QuoteColumns.ISUP, 0);
      } else {
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return builder.build();
  }

  public static String truncateBidPrice(String bidPrice) {
    Log.d(LOG_TAG, "bidPrice: " + bidPrice+", length: "+bidPrice.length()+" is eqal to null "
            +(bidPrice.equals("null")));
    if (bidPrice == null) {
      return null;
    }
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange) {
    String weight = change.substring(0, 1);
    String ampersand = "";
    if (isPercentChange) {
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static int getNetworkStatus(Context c) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
    return sp.getInt(c.getString(R.string.pref_network_status), SERVER_DOWN);
  }


  public static boolean isValidResponse(String response) throws JSONException {
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try {
      jsonObject = new JSONObject();
      if (jsonObject != null && jsonObject.length() != 0) {
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1) {
          jsonObject = jsonObject.getJSONObject("results")
                  .getJSONObject("quote");
          Log.d(LOG_TAG,"Bidutils: "+jsonObject.getString("Bid") +"is valid: "+isEmptyString(jsonObject.getString("Bid")));
          if(isEmptyString(jsonObject.getString("Bid"))){
            return false;
          }else {
            return true;
          }
//          batchOperations.add(buildBatchOperation(jsonObject));
        }
      }else {
        return true;
      }

    }catch (JSONException e){
      e.printStackTrace();
    }
    return false;
  }
  public static boolean isEmptyString(String text) {
    return ( text.equals("null"));
  }
}
