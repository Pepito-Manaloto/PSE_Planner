package com.aaron.pseplanner.service.implementation;

import com.aaron.pseplanner.service.CalculatorService;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by aaron.asuncion on 12/19/2016.
 */
public class DefaultCalculatorService implements CalculatorService
{
    /**
     * Gets the buy gross amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares the number of shares to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getBuyGrossAmount(BigDecimal buyPrice, long shares)
    {
        return buyPrice.multiply(BigDecimal.valueOf(shares));
    }

    /**
     * Gets the buy net(additional fees added) amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param shares the number of shares to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getBuyNetAmount(BigDecimal buyPrice, long shares)
    {
        BigDecimal grossAmount = getBuyGrossAmount(buyPrice, shares);

        return grossAmount.add(grossAmount.multiply(TOTAL_BUY_FEE));
    }

    /**
     * Gets the average price after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getAveragePriceAfterBuy(BigDecimal buyPrice)
    {
        BigDecimal buyPriceWithFees = buyPrice.multiply(TOTAL_BUY_FEE);
        return buyPrice.add(buyPriceWithFees);
    }

    /**
     * Gets the selling price needed in order to break-even after the stock to buy. Adjusted with the additional fee.
     *
     * @param buyPrice the price the stock to buy
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPriceToBreakEven(BigDecimal buyPrice)
    {
        BigDecimal buyPriceWithFees = buyPrice.multiply(TOTAL_BUY_SELL_FEE);
        return buyPrice.add(buyPriceWithFees);
    }

    /**
     * Gets the sell gross amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares the number of shares to sell
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSellGrossAmount(BigDecimal sellPrice, long shares)
    {
        return sellPrice.multiply(BigDecimal.valueOf(shares));
    }

    /**
     * Gets the sell net(additional fees added) amount of a stock trade.
     *
     * @param sellPrice the price the stock to sell
     * @param shares the number of shares to sell
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSellNetAmount(BigDecimal sellPrice, long shares)
    {
        BigDecimal sellGrossAmount = getSellGrossAmount(sellPrice, shares);
        return sellGrossAmount.subtract(sellGrossAmount.multiply(TOTAL_SELL_FEE));
    }

    /**
     * Gets the gain/loss amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares the number of shares in the trade
     * @return BigDecimal
     */
    @Override
    public BigDecimal getGainLossAmount(BigDecimal buyPrice, long shares, BigDecimal sellPrice)
    {
        BigDecimal buyNetAmount = getBuyNetAmount(buyPrice, shares);
        BigDecimal sellNetAmount = getSellNetAmount(sellPrice, shares);

        return sellNetAmount.subtract(buyNetAmount);
    }

    /**
     * Gets the percent gain/loss amount of a stock trade.
     *
     * @param buyPrice the price the stock to buy
     * @param sellPrice the price the stock to sell
     * @param shares the number of shares in the trade
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPercentGainLoss(BigDecimal buyPrice, long shares, BigDecimal sellPrice)
    {
        BigDecimal gainLossAmount = getGainLossAmount(buyPrice, shares, sellPrice);
        BigDecimal buyNetAmount = getBuyNetAmount(buyPrice, shares);

        return ONE_HUNDRED.multiply(gainLossAmount.divide(buyNetAmount, MathContext.DECIMAL64));
    }

    /**
     * Gets risk/reward ratio of a stock trade.
     *
     * @param entryPrice the planned price entry of a stock
     * @param targetPrice the planned target price of a stock
     * @param cutlossPrice the planned cutloss price of a stock
     * @return BigDecimal
     */
    @Override
    public BigDecimal getRiskRewardRatio(BigDecimal entryPrice, BigDecimal targetPrice, BigDecimal cutlossPrice)
    {
        BigDecimal gain = getGainLossAmount(entryPrice, NumberUtils.LONG_ONE, targetPrice);
        BigDecimal loss = getGainLossAmount(entryPrice, NumberUtils.LONG_ONE, cutlossPrice);

        return gain.divide(loss, MathContext.DECIMAL64).abs();
    }

    /**
     * Gets dividend yield of a stock.
     *
     * @param shares the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return BigDecimal
     */
    @Override
    public BigDecimal getDividendYield(long shares, BigDecimal cashDividend)
    {
        return cashDividend.multiply(BigDecimal.valueOf(shares));
    }

    /**
     * Gets percent dividend yield of a stock.
     *
     * @param price the price of the stock to buy
     * @param shares the number of shares to buy
     * @param cashDividend the dividend amount per share
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPercentDividendYield(BigDecimal price, long shares, BigDecimal cashDividend)
    {
        BigDecimal dividendYield = getDividendYield(shares, cashDividend);
        BigDecimal totalAmount = getBuyGrossAmount(price, shares);
        return ONE_HUNDRED.multiply(dividendYield.divide(totalAmount, MathContext.DECIMAL64));
    }

    /**
     * Gets the midpoint of the given price(high, low).
     *
     * @param high highest price
     * @param low lowest price
     * @return BigDecimal
     */
    @Override
    public BigDecimal getMidpoint(BigDecimal high, BigDecimal low)
    {
        return high.subtract(high.subtract(low).divide(TWO, MathContext.DECIMAL64));
    }

    /**
     * Gets the stock broker's commission.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getStockbrokersCommission(BigDecimal grossAmount)
    {
        BigDecimal commission = grossAmount.multiply(STOCK_BROKERS_COMMISSION);
        return commission.compareTo(MINIMUM_COMMISSION) < 0 ? MINIMUM_COMMISSION : commission;
    }

    /**
     * Gets the stock broker's commission's vat.
     *
     * @param stockbrokersCommission the stock trade's commission
     * @return BigDecimal
     */
    @Override
    public BigDecimal getVatOfCommission(BigDecimal stockbrokersCommission)
    {
        return stockbrokersCommission.multiply(VAT);
    }

    /**
     * Gets the clearing fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getClearingFee(BigDecimal grossAmount)
    {
        return grossAmount.multiply(CLEARING_FEE);
    }

    /**
     * Gets the transaction fee.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getTransactionFee(BigDecimal grossAmount)
    {
        return grossAmount.multiply(PSE_TRANSACTION_FEE);
    }

    /**
     * Gets the sales tax.
     *
     * @param grossAmount the stock trade's gross amount
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSalesTax(BigDecimal grossAmount)
    {
        return grossAmount.multiply(SALES_TAX);
    }

    /**
     * Gets the change between the previous price and current price based on the current price and percent change.
     *
     * @param currentPrice the current price
     * @param percentChange the percent change
     * @return BigDecimal the amount change from the previous price
     */
    @Override
    public BigDecimal getChangeBetweenCurrentAndPreviousPrice(double currentPrice, double percentChange)
    {
        if(currentPrice <= 0)
        {
            throw new IllegalArgumentException("Inputs to be calculated must be greater than zero");
        }

        if(percentChange == 0)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal currentPriceBigDecimal = BigDecimal.valueOf(currentPrice);
        BigDecimal previousPrice = getPreviousPrice(currentPrice, percentChange);

        return currentPriceBigDecimal.subtract(previousPrice);
    }

    /**
     * Gets the previous price based on the current price and percent change.
     *
     * @param currentPrice the current amount
     * @param percentChange the percent change
     * @return BigDecimal the previous price
     */
    @Override
    public BigDecimal getPreviousPrice(double currentPrice, double percentChange)
    {
        if(currentPrice <= 0)
        {
            throw new IllegalArgumentException("Inputs to be calculated must be greater than zero");
        }

        if(percentChange == 0)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal oppositePercentChange = getOppositePercentChange(percentChange);
        BigDecimal currentPriceBigDecimal = BigDecimal.valueOf(currentPrice);

        return currentPriceBigDecimal.multiply(oppositePercentChange);
    }

    private BigDecimal getOppositePercentChange(double percentChange)
    {
        BigDecimal percentChangeToDivide = BigDecimal.valueOf(percentChange);
        BigDecimal percentDecimal = percentChangeToDivide.divide(ONE_HUNDRED, MathContext.DECIMAL64);
        return BigDecimal.ONE.subtract(percentDecimal);
    }

    /**
     * Gets day/s difference between the two dates.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return int the number of days between the two dates
     * @throws IllegalArgumentException if the parameters are null
     */
    @Override
    public int getDaysBetween(Date date1, Date date2)
    {
        if(date1 == null || date2 == null)
        {
            throw new IllegalArgumentException("Dates to compare must not be null");
        }

        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long diff;

        if(time1 > time2)
        {
            diff = time1 - time2;
        }
        else if(time1 < time2)
        {
            diff = time2 - time1;
        }
        else
        {
            return 0;
        }

        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
