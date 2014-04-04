package cz.urbangaming.galgs;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

/**
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
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
        subMenu.clear();
        subMenu.add(1, GAlg.RELOAD_RUBY_SCRIPT, 0, R.string.reload_ruby_script);
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