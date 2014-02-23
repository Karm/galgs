package cz.urbangaming.galgs;

import org.ruboto.JRubyAdapter;
import org.ruboto.Log;
import org.ruboto.SplashActivity;

import android.content.Intent;
import android.os.Bundle;

public class RubyActivity extends org.ruboto.RubotoActivity {

    public void onCreate(Bundle bundle) {
        Log.d("EntryPointActivity onCreate:");

        if (JRubyAdapter.isInitialized()) {
            getScriptInfo().setRubyClassName(getClass().getSimpleName());
        } else {
            showSplash();
            finish();
        }

        super.onCreate(bundle);
    }

    private void showSplash() {
        Intent splashIntent = new Intent(this, SplashActivity.class);
        splashIntent.putExtra(Intent.EXTRA_INTENT, futureIntent());
        startActivity(splashIntent);
    }
    
    // The Intent to to call when done. Defaults to calling this Activity again.
    // Override to change.
    protected Intent futureIntent() {
        Log.d("getIntent():"+getIntent());
        //if (getIntent() == null || !getIntent().getAction().equals(Intent.ACTION_VIEW)) {
        //    return new Intent(getIntent()).setAction(Intent.ACTION_VIEW);
        //} else {
            return getIntent();
        //}
    }

}
