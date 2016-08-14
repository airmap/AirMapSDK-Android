package com.airmap.airmapsdk.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.airmap.airmapsdk.AirMapException;
import com.airmap.airmapsdk.Models.Coordinate;
import com.airmap.airmapsdk.Models.Flight.AirMapFlight;
import com.airmap.airmapsdk.Models.Permits.AirMapAvailablePermit;
import com.airmap.airmapsdk.Models.Permits.AirMapPilotPermit;
import com.airmap.airmapsdk.Models.Status.AirMapStatus;
import com.airmap.airmapsdk.Models.Status.AirMapStatusAdvisory;
import com.airmap.airmapsdk.Models.Status.AirMapStatusPermits;
import com.airmap.airmapsdk.Models.Status.AirMapStatusRequirement;
import com.airmap.airmapsdk.Models.Status.AirMapStatusRequirementNotice;
import com.airmap.airmapsdk.Networking.Callbacks.AirMapCallback;
import com.airmap.airmapsdk.Networking.Services.AirMap;
import com.airmap.airmapsdk.R;
import com.airmap.airmapsdk.UI.Adapters.SectionsPagerAdapter;
import com.airmap.airmapsdk.UI.Fragments.FlightDetailsFragment;
import com.airmap.airmapsdk.UI.Fragments.FlightNoticeFragment;
import com.airmap.airmapsdk.UI.Fragments.ListPermitsFragment;
import com.airmap.airmapsdk.UI.Fragments.ReviewFlightFragment;
import com.airmap.airmapsdk.UI.Fragments.ReviewNoticeFragment;
import com.airmap.airmapsdk.Utils;
import com.mapbox.mapboxsdk.MapboxAccountManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.airmap.airmapsdk.Utils.feetToMeters;

public class CreateFlightActivity extends AppCompatActivity implements
        FlightDetailsFragment.OnFragmentInteractionListener,
        ListPermitsFragment.OnFragmentInteractionListener,
        FlightNoticeFragment.OnFragmentInteractionListener,
        ReviewFlightFragment.OnFragmentInteractionListener,
        ReviewNoticeFragment.OnFragmentInteractionListener {

    public static final String COORDINATE = "coordinate";
    public static final String KEY_VALUE_EXTRAS = "keyValueExtras";
    public static final String MAPBOX_API_KEY = "mapbox_api_key";
    public static final String FLIGHT = "flight";
    private static final int REQUEST_DECISION_FLOW = 1;
    private static final int REQUEST_CUSTOM_PROPERTIES = 2;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private SectionsPagerAdapter adapter;

    private int currentPage;
    private AirMapFlight flight; //The flight the user wants to create
    private AirMapStatus flightStatus; //The status for the flight
    private ArrayList<AirMapStatusRequirementNotice> notices;

    private ArrayList<AirMapStatusPermits> statusPermits; //List of all permits that might be required
    private ArrayList<AirMapPilotPermit> permitsFromWallet; //Permits the user has in their wallet that pertains to this flight

    private ArrayList<AirMapPilotPermit> selectedPermits; //Permits that do not need to be applied for and can be attached to flight
    private ArrayList<AirMapAvailablePermit> permitsToApplyFor; //Permits that need to be applied for before attaching to flight
    private ArrayList<AirMapAvailablePermit> permitsToShowInReview; //selectedPermits + permitsToApplyFor temporarily transformed into AvailablePermit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFlight(savedInstanceState);
        MapboxAccountManager.start(this, Utils.getMapboxApiKey());
        setContentView(R.layout.airmap_activity_create_flight);
        initializeViews();
        setupToolbar();
        setupViewPager();

        notices = new ArrayList<>();
        statusPermits = new ArrayList<>();
        permitsFromWallet = new ArrayList<>();
        selectedPermits = new ArrayList<>();
        permitsToApplyFor = new ArrayList<>();
        permitsToShowInReview = new ArrayList<>();
    }

    private void setupFlight(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            flight = (AirMapFlight) savedInstanceState.getSerializable(FLIGHT);
            currentPage = savedInstanceState.getInt("index");
        } else {
            //Initialize Flight with default values
            Coordinate coordinate = (Coordinate) getIntent().getSerializableExtra(COORDINATE);
            currentPage = 0;
            if (coordinate == null) {
                if (flight != null && flight.getCoordinate() != null) {
                    coordinate = flight.getCoordinate();
                } // else We don't know where to create the flight anymore...
            }
            Date startsAt = new Date();
            Date endsAt = new Date(startsAt.getTime() + 60 * 60 * 1000); //Default duration of 1 hr
            flight = new AirMapFlight()
                    .setCoordinate(coordinate) //The passed in coordinate
                    .setStartsAt(startsAt) //Default to null to prevent submitting a flight in the past
                    .setEndsAt(endsAt) //Default duration of 1 hour
                    .setPublic(true) //Share with AirMap by default
                    .setNotify(true) //Notify airports by default
                    .setMaxAltitude(feetToMeters(400)) //400 ft by default
                    .setBuffer(feetToMeters(1000)); //1000 ft by default
        }
    }

    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.container);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.airmap_flight_details);
    }

    private void setupViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        switch (currentPage) { //Using fall through to add all necessary fragments
            case 3:
                fragments.add(0, ReviewFlightFragment.newInstance());
            case 2:
                fragments.add(0, FlightNoticeFragment.newInstance());
            case 1:
                fragments.add(0, ListPermitsFragment.newInstance());
            case 0:
                fragments.add(0, FlightDetailsFragment.newInstance());
        }
        adapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                Fragment fragment = adapter.getItem(position);
                if (fragment instanceof FlightDetailsFragment) {
                    getSupportActionBar().setTitle(R.string.airmap_flight_details);
                } else if (fragment instanceof ListPermitsFragment) {
                    getSupportActionBar().setTitle(R.string.airmap_permits);
                } else if (fragment instanceof FlightNoticeFragment) {
                    getSupportActionBar().setTitle(R.string.airmap_flight_notice);
                } else if (fragment instanceof ReviewFlightFragment) {
                    getSupportActionBar().setTitle(R.string.airmap_review);
                }
            }
        });
        goToLastPage();
    }

    private void invalidateFurtherFragments(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = adapter.getCount() - 1; i > position; i--) {
                    adapter.remove(i);
                }
            }
        });

    }

    @Override
    public void showDecisionFlow(final AirMapStatusPermits permit) {
        Intent intent = new Intent(this, DecisionFlowActivity.class);
        intent.putExtra(DecisionFlowActivity.ARG_PERMIT, permit);
        startActivityForResult(intent, REQUEST_DECISION_FLOW);
    }

    @Override
    public void flightDetailsSaveClicked(AirMapFlight response) {
        Intent data = new Intent();
        data.putExtra(FLIGHT, response);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onFlightSubmitted(AirMapFlight response) {
        Intent data = new Intent();
        data.putExtra(FLIGHT, response);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void flightDetailsNextClicked(AirMapStatus flightStatus) {
        this.flightStatus = flightStatus;
        List<AirMapStatusAdvisory> advisories = flightStatus.getAdvisories();
        for (AirMapStatusAdvisory advisory : advisories) {
            AirMapStatusRequirement requirement = advisory.getRequirements();
            if (requirement.getPermit() != null && requirement.getPermit().getTypes() != null && !requirement.getPermit().getTypes().isEmpty()) {
                AirMapStatusPermits permit = requirement.getPermit();
                permit.setAuthorityName(advisory.getName()); //Manually set authority name
                statusPermits.add(permit);
            } else if (requirement.getNotice() != null && !requirement.getNotice().getPhoneNumber().isEmpty()) {
                notices.add(requirement.getNotice());
            }
        }
        invalidateFurtherFragments(0); //To prevent creating multiple instances of the next fragment
        if (!statusPermits.isEmpty()) {
            AirMap.getAuthenticatedPilotPermits(new AirMapCallback<List<AirMapPilotPermit>>() {
                @Override
                public void onSuccess(List<AirMapPilotPermit> response) {
                    for (AirMapPilotPermit pilotPermit : response) {
                        if (pilotPermit.getShortDetails().isSingleUse()) {
                            continue;
                        }
                        for (AirMapStatusPermits statusPermit : statusPermits) {
                            for (AirMapAvailablePermit availablePermit : statusPermit.getTypes()) {
                                if (availablePermit.getId().equals(pilotPermit.getPermitId())) {
                                    permitsFromWallet.add(pilotPermit); //Only add permits that would pertain to this flight
                                }
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.add(ListPermitsFragment.newInstance());
                        }
                    });
                }

                @Override
                public void onError(AirMapException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateFlightActivity.this, "Error getting Permit Wallet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } else if (!notices.isEmpty()) {
            //Show only submit digital notices
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.add(FlightNoticeFragment.newInstance());
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(1, true);
            }
        });
    }

    private void goToLastPage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(adapter.getCount(), true);
            }
        });
    }

    public void onCustomPropertiesNextButtonClick(AirMapAvailablePermit permit) {
        permitsToApplyFor.add(permit);
        for (Fragment fragment : adapter.getItems()) {
            if (fragment instanceof ListPermitsFragment) {
                ((ListPermitsFragment) fragment).onEnabledPermit(permit);
            }
        }
    }

    @Override
    public void onListPermitsNextClicked(ArrayList<AirMapAvailablePermit> selectedInAdapter) {
        permitsToShowInReview = selectedInAdapter;
        //Since permit wallet permits and all other permits were combined in the adapter, they need to be separated out again
        for (AirMapAvailablePermit availablePermit : selectedInAdapter) {
            AirMapPilotPermit pilotPermit = isFromWallet(availablePermit);
            if (pilotPermit != null) {
                selectedPermits.add(pilotPermit);
            } else {
                permitsToApplyFor.add(availablePermit);
            }
        }

        if (!notices.isEmpty()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Fragment fragment = adapter.getItem(i);
                if (fragment instanceof FlightNoticeFragment) {
                    viewPager.setCurrentItem(i, true);
                    return;
                }
            }
            adapter.add(FlightNoticeFragment.newInstance());
        } else {
            for (int i = 0; i < adapter.getCount(); i++) {
                Fragment fragment = adapter.getItem(i);
                if (fragment instanceof ReviewFlightFragment) {
                    viewPager.setCurrentItem(i, true);
                    return;
                }
            }
            adapter.add(ReviewFlightFragment.newInstance());
        }
        goToLastPage();
    }

    private AirMapPilotPermit isFromWallet(AirMapAvailablePermit availablePermit) {
        for (AirMapPilotPermit pilotPermit : permitsFromWallet) {
            if (pilotPermit.getPermitId().equals(availablePermit.getId())) {
                return pilotPermit;
            }
        }
        return null;
    }

    @Override
    public void onFlightNoticeNextButtonClicked() {
        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment fragment = adapter.getItem(i);
            if (fragment instanceof ReviewFlightFragment) {
                viewPager.setCurrentItem(i, true);
                return;
            }
        }
        adapter.add(ReviewFlightFragment.newInstance());
        goToLastPage();
    }

    public void decisionFlowFinished(String permitId) {
        if (permitId != null) {
            AirMap.getPermit(permitId, new AirMapCallback<List<AirMapAvailablePermit>>() {
                @Override
                public void onSuccess(List<AirMapAvailablePermit> permits) {
                    if (!permits.isEmpty()) {
                        Intent intent = new Intent(CreateFlightActivity.this, CustomPropertiesActivity.class);
                        intent.putExtra(CustomPropertiesActivity.PERMIT, permits.get(0)); //There should only be one permit
                        startActivityForResult(intent, REQUEST_CUSTOM_PROPERTIES);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateFlightActivity.this, "Error getting permit", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(AirMapException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateFlightActivity.this, "Error getting permit", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DECISION_FLOW) {
            if (resultCode == RESULT_OK) {
                String permitId = data.getStringExtra(DecisionFlowActivity.PERMIT_ID);
                decisionFlowFinished(permitId);
            } else if (resultCode == DecisionFlowActivity.RESULT_CANT_FLY) {
                decisionFlowFinished(null);
            }
        } else if (requestCode == REQUEST_CUSTOM_PROPERTIES) {
            if (resultCode == RESULT_OK) {
                onCustomPropertiesNextButtonClick((AirMapAvailablePermit) data.getSerializableExtra(CustomPropertiesActivity.PERMIT));
            }
        }
    }

    @Override
    public void flightChanged() {
        invalidateFurtherFragments(0);
        flightStatus = null;
        notices.clear();
        permitsFromWallet.clear();
        statusPermits.clear();
        selectedPermits.clear();
        permitsToApplyFor.clear();
        permitsToShowInReview.clear();
    }

    @Override
    public ArrayList<AirMapStatusPermits> getStatusPermits() {
        return statusPermits;
    }

    @Override
    public ArrayList<AirMapPilotPermit> getPermitsFromWallet() {
        return permitsFromWallet;
    }

    @Override
    public ArrayList<AirMapPilotPermit> getSelectedPermits() {
        return selectedPermits;
    }

    @Override
    public ArrayList<AirMapAvailablePermit> getPermitsToShowInReview() {
        return permitsToShowInReview;
    }

    @Override
    public ArrayList<AirMapAvailablePermit> getPermitsToApplyFor() {
        return permitsToApplyFor;
    }

    @Override
    public AirMapStatus getFlightStatus() {
        return flightStatus;
    }

    @Override
    public ArrayList<AirMapStatusRequirementNotice> getNotices() {
        return notices;
    }

    @Override
    public AirMapFlight getFlight() {
        return flight;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        flight = (AirMapFlight) savedInstanceState.getSerializable(FLIGHT);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(FLIGHT, flight);
        outState.putInt("index", currentPage);
        getIntent().putExtra(FLIGHT, flight);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        } else {
            super.onBackPressed();
        }
    }
}