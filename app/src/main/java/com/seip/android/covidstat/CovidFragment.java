package com.seip.android.covidstat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seip.android.covidstat.current.CoronaUpdateResponseModel;
import com.squareup.picasso.Picasso;
import com.seip.android.covidstat.databinding.FragmentCovidBinding;
import com.seip.android.covidstat.models.Country;
import com.seip.android.covidstat.viewmodels.CovidViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CovidFragment extends Fragment {
    private CovidViewModel viewModel;
    private FragmentCovidBinding binding;
    public CovidFragment() { }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCovidBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity()).get(CovidViewModel.class);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadData();
        });
        final ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Toast.makeText(getActivity(), "Network Unavailable", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    viewModel.loadData();
                }
            });
        }
        viewModel.getResponseModelMutableLiveData().observe(getViewLifecycleOwner(), country -> {
            binding.countryNameTV.setText(country.getCountry());
            Picasso.get().load(country.getCountryInfo().getFlag()).fit().into(binding.flagIV);

//            binding.countryFlagIV.setText(country.getCountry());
//            Picasso.get().load(country.getCountryInfo().getFlag())
//                    .fit()
//                    .into(binding.flagIV);
            binding.dateTimeTV.setText(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z").format(new Date(country.getUpdated())));
            binding.caseTodayTV.setText(String.valueOf(country.getTodayCases()));
            binding.deathTodayTV.setText(String.valueOf(country.getTodayDeaths()));
            binding.recoveredTodayTV.setText(String.valueOf(country.getTodayRecovered()));
            binding.totalCaseTV.setText(String.valueOf(country.getCases()));
            binding.totalDeathTV.setText(String.valueOf(country.getDeaths()));
            binding.totalRecoverTV.setText(String.valueOf(country.getRecovered()));

        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: 2/6/2022 set observer on the LiveData object from the CovidViewModel
        // TODO: 2/6/2022 update the UI
    }

    private void updateUI(Country country) {
        // TODO: 2/6/2022 pass the country object to the layout as variable
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.covid_menu, menu);
        final SearchView searchView = (SearchView) menu.
                findItem(R.id.item_search)
                .getActionView();
        searchView.setQueryHint("Search a country");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setCountry(query);
                viewModel.loadData();
                searchView.setQuery(null, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}