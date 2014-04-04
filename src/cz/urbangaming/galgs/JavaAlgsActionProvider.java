package cz.urbangaming.galgs;

import android.content.Context;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class JavaAlgsActionProvider extends ActionProvider implements OnMenuItemClickListener {

    public JavaAlgsActionProvider(Context context) {
        super(context);
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
        subMenu.add(0, GAlg.LINKED_POINTS, 0, R.string.link_points);
        subMenu.add(0, GAlg.CONVEX_HULL_GW, 1, R.string.algorithm_convex_hull_gw);
        subMenu.add(0, GAlg.CONVEX_HULL_GS, 2, R.string.algorithm_convex_hull_gs);
        subMenu.add(0, GAlg.SWEEP_TRIANGULATION, 3, R.string.algorithm_sweep_triangulation);
        subMenu.add(0, GAlg.NAIVE_TRIANGULATION, 4, R.string.algorithm_naive_triangulation);
        subMenu.add(0, GAlg.KD_TREE, 4, R.string.kd_tree);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}