package cz.urbangaming.galgs;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

public class RubyAlgsActionProvider extends ActionProvider implements OnMenuItemClickListener {
    private Map<Integer, String> rubyMethods = null;

    public RubyAlgsActionProvider(Context context, Map<Integer, String> rubyMethods) {
        super(context);
        this.rubyMethods = rubyMethods;

    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        Log.d(this.getClass().getSimpleName(), "onPrepareSubMenu");
        subMenu.clear();
        for (int rubyMethodKey : rubyMethods.keySet()) {
            Log.d(GAlg.DEBUG_TAG, "Adding menu item " + rubyMethods.get(rubyMethodKey));
            subMenu.add(1, rubyMethodKey, 0, rubyMethods.get(rubyMethodKey));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}