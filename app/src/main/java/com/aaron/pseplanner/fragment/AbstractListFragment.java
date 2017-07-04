package com.aaron.pseplanner.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.adapter.FilterableArrayAdapter;
import com.aaron.pseplanner.bean.Stock;
import com.aaron.pseplanner.exception.HttpRequestException;
import com.aaron.pseplanner.listener.OnScrollShowHideFastScroll;
import com.aaron.pseplanner.listener.SearchOnQueryTextListener;
import com.aaron.pseplanner.service.FormatService;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.PSEPlannerService;
import com.aaron.pseplanner.service.implementation.DefaultFormatService;
import com.aaron.pseplanner.service.implementation.FacadePSEPlannerService;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by Aaron on 2/17/2017.
 * Abstract ListFragment class with concrete implementation for showing/hiding fast scroll and method for updating the list adapter.
 */
public abstract class AbstractListFragment<T extends Stock & Parcelable> extends ListFragment
{
    public static final String CLASS_NAME = AbstractListFragment.class.getSimpleName();

    @BindView(R.id.textview_last_updated)
    protected TextView lastUpdatedTextView;

    protected PSEPlannerService pseService;
    protected FormatService formatService;
    protected SearchOnQueryTextListener searchListener;
    protected Unbinder unbinder;

    /**
     * Initializes non-fragment user interface.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.pseService = new FacadePSEPlannerService(getActivity());
        this.formatService = new DefaultFormatService(getActivity());

        LogManager.debug(CLASS_NAME, "onCreateView", "");
    }

    /**
     * Called after onCreateView(), sets the action listeners of the UI.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnScrollListener(new OnScrollShowHideFastScroll());

        LogManager.debug(CLASS_NAME, "onActivityCreated", "");
    }

    /**
     * Updates the list view on UI thread, including the last updated text view.
     *
     * @param list        the new list
     * @param lastUpdated the last updated date
     */
    protected void updateListOnUiThread(final List<T> list, final String lastUpdated)
    {
        if(list != null && !list.isEmpty())
        {
            this.getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    setListAdapter(getArrayAdapter(list));

                    if(lastUpdatedTextView != null)
                    {
                        lastUpdatedTextView.setText(getActivity().getString(R.string.last_updated, lastUpdated));
                    }

                    searchListener.setSearchListAdapater(getListAdapter());
                }
            });
        }
    }

    /**
     * A Fragment may continue to exist after its Views are destroyed, you need to call .unbind() from a Fragment to release the reference to the Views (and allow the associated
     * memory to be reclaimed).
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(this.unbinder != null)
        {
            this.unbinder.unbind();
        }
    }

    public void setSearchListener(SearchOnQueryTextListener searchListener)
    {
        this.searchListener = searchListener;
    }

    /**
     * Returns the filterable list adapter. Even though it is unchecked, we are sure of the type because the parameter passed to setListAdapter() is from getArrayAdapter(List).
     *
     * @return FilterableArrayAdapter
     */
    @Override
    @SuppressWarnings("unchecked")
    public FilterableArrayAdapter<T> getListAdapter()
    {
        return (FilterableArrayAdapter<T>) super.getListAdapter();
    }

    /**
     * Returns the ArrayAdapter that will be used to populate the ListFragment.
     *
     * @return ArrayAdapter
     */
    protected abstract FilterableArrayAdapter<T> getArrayAdapter(List<T> list);

    /**
     * Updates the list of this fragment list by getting the latest data through http request.
     *
     * @throws HttpRequestException http request failed
     */
    public abstract void updateListFromWeb() throws HttpRequestException;

    /**
     * Updates the list of this fragment list by getting the latest data from the database.
     */
    public abstract void updateListFromDatabase();
}
