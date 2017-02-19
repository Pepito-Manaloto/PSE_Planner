package com.aaron.pseplanner.service.implementation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.constant.Constants;
import com.aaron.pseplanner.service.FormatService;

import org.apache.commons.lang3.time.FastDateFormat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by aaron.asuncion on 12/8/2016.
 */
public class FormatServiceImpl implements FormatService
{
    private static int GREEN;
    private static final String DATE_PATTERN = "MMMM dd, yyyy";
    private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance(DATE_PATTERN);

    public FormatServiceImpl(Activity activity)
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            GREEN = activity.getColor(R.color.darkGreen);
        }
        else
        {
            GREEN = activity.getResources().getColor(R.color.darkGreen);
        }
    }

    /**
     * Formats the given number, adding commas and rounding down to 4 decimal places.
     */
    @Override
    public String formatStockPrice(double number)
    {
        return format(number, Constants.STOCK_PRICE_FORMAT, RoundingMode.DOWN);
    }

    /**
     * Formats the given number, adding commas and rounding up to 2 decimal places.
     */
    @Override
    public String formatPrice(double number)
    {
        return format(number, Constants.PRICE_FORMAT, RoundingMode.UP);
    }

    /**
     * Formats the given number, adding percent and rounding up to 2 decimal places.
     */
    @Override
    public String formatPercent(double number)
    {
        return format(number, Constants.PRICE_FORMAT, RoundingMode.UP) + "%";
    }

    /**
     * Formats the given number, removes any decimal point/s.
     */
    @Override
    public String formatShares(long number)
    {
        return format(number, Constants.SHARES_FORMAT, RoundingMode.DOWN);
    }

    /**
     * Formats the TextView, Green for positive number, while Red for negative number.
     */
    @Override
    public void formatTextColor(double price, TextView text)
    {
        if(price > 0)
        {
            text.setTextColor(GREEN);
        }
        else if(price < 0)
        {
            text.setTextColor(Color.RED);
        }
        else
        {
            text.setTextColor(Color.BLACK);
        }
    }

    /**
     * Adds '+' if the number is position, else add '-'.
     *
     * @param number the number to check
     * @param text   the text to append the sign
     * @return text with number sign appended
     */
    @Override
    public String addNumberSign(double number, String text)
    {
        if(number > 0)
        {
            text = "+" + text;
        }
//        else if(number < 0)
//        {
//            text = "-" + text;
//        }
        // else do nothing

        return text;
    }

    /**
     * Formats the date to 'MMMM dd, yyyy' pattern.
     *
     * @param date the Date to format
     * @return formatted date string
     */
    @Override
    public String formatDate(Date date)
    {
        return DATE_FORMATTER.format(date);
    }

    /**
     * Formats the given number with the given format.
     */
    protected String format(double number, String format, RoundingMode mode)
    {
        if(number == 0)
        {
            return "0";
        }

        DecimalFormat df = new DecimalFormat(format);
        df.setRoundingMode(mode);

        return df.format(number);
    }
}
