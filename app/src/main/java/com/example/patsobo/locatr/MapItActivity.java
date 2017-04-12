package com.example.patsobo.locatr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by patsobo on 3/7/2017.
 */

public class MapItActivity extends SingleFragmentActivity {
    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return MapItFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if( errorCode != ConnectionResult.SUCCESS ) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog( this, errorCode, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel( DialogInterface dialog ) {
                                    // Leave if services are unavailable
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }
}
