package com.aaron.pseplanner.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.aaron.pseplanner.bean.Stock;
import com.aaron.pseplanner.service.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 6/18/2017.
 * ArrayAdapter with filter capability.
 */
public abstract class FilterableArrayAdapter<T extends Stock> extends ArrayAdapter<T>
{
    protected String className;

    public FilterableArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects)
    {
        super(context, resource, objects);
        this.className = getClass().getSimpleName();
    }

    /**
     * Filters the list of objects in the implementing adapter.
     *
     * @param searchQuery the search query used in filtering
     */
    public void filter(String searchQuery)
    {
        String searched = searchQuery.trim().toLowerCase();

        clear();

        if(searched.length() == 0)
        {
            addAll(this.getTempList());
        }
        else
        {
            addFilteredStockToActualList(searched);
        }

        LogManager.debug(this.className, "filter", "New list size -> " + getCount());
    }

    private void addFilteredStockToActualList(String searched)
    {
        String symbol;

        for(T dto : this.getTempList())
        {
            symbol = dto.getSymbol().toLowerCase();

            if(symbol.startsWith(searched))
            {
                add(dto);
            }
        }
    }

    /**
     * Updates the list.
     *
     * @param list the list to replace the current
     */
    public void update(@NonNull List<T> list)
    {
        // Store this new list into temp, because the list parameter shares the same reference as the Adapter's list.
        // Thus, calling clear() will clear out both the adapter's list and the new list.
        ArrayList<T> tmpList = new ArrayList<>(list);

        clear();
        addAll(tmpList);

        getTempList().clear();
        getTempList().addAll(tmpList);
    }

    /**
     * Returns the temporary list that is used in filtering the actual list of the adapter.
     *
     * @return ArrayList<T> the temp list adapter
     */
    protected abstract ArrayList<T> getTempList();
}
