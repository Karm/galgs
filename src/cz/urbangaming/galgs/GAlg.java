package cz.urbangaming.galgs;

import org.ruboto.JRubyAdapter;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import cz.urbangaming.galgs.utils.Point2D;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class GAlg extends FragmentActivity {
    public static final String DEBUG_TAG = "KARM";

    private PointsRenderer pointsRenderer = null;

    // Menus begin
    public static final int WORK_MODE = 10;
    public static final int WORK_MODE_EDIT = 20;
    public static final int WORK_MODE_ADD = 30;
    public static final int WORK_MODE_DELETE = 40;

    public static final int SELECT_ALGORITHM = 50;
    public static final int CONVEX_HULL_GW = 60;
    public static final int CONVEX_HULL_GS = 61;
    public static final int LINKED_POINTS = 62;
    public static final int LINKED_POINTS_RUBY = 666;
    public static final int RED_STAR = 668;

    public static final int SWEEP_TRIANGULATION = 63;
    public static final int NAIVE_TRIANGULATION = 64;

    public static final int REMOVE_ALL_POINTS = 70;
    public static final int ADD_RANDOM_POINTS = 80;
    // Menus end

    private int currentWorkMode = WORK_MODE_ADD;

    // /////// Some settings: Move it outside... /////////

    // TODO: THIS IS SO EPICLY WRONG! I must calculate it accordingly to display's density...
    public static final float POINT_SIZE = 7f;
    public static final int FINGER_ACCURACY = Math.round(POINT_SIZE) * 3;
    // No, it's not very convenient to have points too close to boundaries
    public static final int BORDER_POINT_POSITION = Math.round(POINT_SIZE) * 3;
    public static final int HOW_MANY_POINTS_GENERATE = Math.round(POINT_SIZE) * 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        if (detectOpenGLES20()) {
            // Tell the surface view we want to create an OpenGL ES 2.0-compatible
            // context, and set an OpenGL ES 2.0-compatible renderer.
            mGLSurfaceView.setEGLContextClientVersion(2);
            pointsRenderer = new PointsRenderer();
            mGLSurfaceView.setRenderer(pointsRenderer);
        } else {
            // TODO: Handle as an unrecoverable error and leave the activity somehow...
        }
        setContentView(mGLSurfaceView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Intentionally left blank
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        SubMenu submenu = menu.addSubMenu(0, WORK_MODE, 0, R.string.work_mode);
        switch (currentWorkMode) {
        case WORK_MODE_ADD:
            submenu.add(0, WORK_MODE_DELETE, 0, R.string.workmode_delete);
            submenu.add(0, WORK_MODE_EDIT, 1, R.string.workmode_edit);
            break;
        case WORK_MODE_DELETE:
            submenu.add(0, WORK_MODE_ADD, 0, R.string.workmode_add);
            submenu.add(0, WORK_MODE_EDIT, 1, R.string.workmode_edit);
            break;
        case WORK_MODE_EDIT:
            submenu.add(0, WORK_MODE_ADD, 0, R.string.workmode_add);
            submenu.add(0, WORK_MODE_DELETE, 1, R.string.workmode_delete);
            break;
        default:
            // nothing
            break;
        }
        menu.add(1, REMOVE_ALL_POINTS, 1, R.string.remove_all_points);
        menu.add(1, ADD_RANDOM_POINTS, 2, R.string.generate_random_points);
        submenu = menu.addSubMenu(3, SELECT_ALGORITHM, 3, R.string.select_algorithm);
        submenu.add(3, CONVEX_HULL_GW, 0, R.string.algorithm_convex_hull_gw);
        submenu.add(3, CONVEX_HULL_GS, 0, R.string.algorithm_convex_hull_gs);
        submenu.add(3, SWEEP_TRIANGULATION, 0, R.string.algorithm_sweep_triangulation);
        submenu.add(3, NAIVE_TRIANGULATION, 0, R.string.algorithm_naive_triangulation);
        submenu.add(3, RED_STAR, 0, R.string.algorithm_red_star);
        menu.add(1, LINKED_POINTS, 4, R.string.link_points);
        menu.add(1, LINKED_POINTS_RUBY, 5, R.string.link_points_ruby);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean itemHandled = true;
        switch (item.getItemId()) {
        case WORK_MODE_ADD:
            currentWorkMode = WORK_MODE_ADD;
            break;
        case WORK_MODE_DELETE:
            currentWorkMode = WORK_MODE_DELETE;
            break;
        case WORK_MODE_EDIT:
            currentWorkMode = WORK_MODE_EDIT;
            break;
        case REMOVE_ALL_POINTS:
            pointsRenderer.clearScene();
            break;
        case ADD_RANDOM_POINTS:
            pointsRenderer.addRandomPoints();
            break;
        case CONVEX_HULL_GW:
            doTheJob(CONVEX_HULL_GW);
            break;
        case CONVEX_HULL_GS:
            doTheJob(CONVEX_HULL_GS);
            break;
        case LINKED_POINTS:
            pointsRenderer.linkPoints(LINKED_POINTS);
            break;
        case SWEEP_TRIANGULATION:
            doTheJob(SWEEP_TRIANGULATION);
            break;
        case NAIVE_TRIANGULATION:
            doTheJob(NAIVE_TRIANGULATION);
            break;
        case LINKED_POINTS_RUBY:
            // TODO: OMG, put it in a thread/task and show some loading animation...
            JRubyAdapter.setUpJRuby(this);
            doTheJob(LINKED_POINTS_RUBY);
            break;
        case RED_STAR:
            JRubyAdapter.setUpJRuby(this);
            doTheJob(RED_STAR);
            break;
        default:
            itemHandled = false;
            break;
        }
        return itemHandled;
    }

    private void doTheJob(final int algorithm) {
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                long time = 0L;
                time = System.currentTimeMillis();
                pointsRenderer.renderAlgorithm(algorithm);
                perflog(System.currentTimeMillis() - time);
            }
        });
        worker.start();
    }

    private void perflog(long info) {
        Log.d(DEBUG_TAG, "Computed in " + String.valueOf(info));
        MyDialogFragment dialog = new MyDialogFragment("Computed in " + String.valueOf(info) + " ms");
        dialog.show(this.getSupportFragmentManager(), "Notice");
    }

    private class MyDialogFragment extends DialogFragment {
        String message = null;

        public MyDialogFragment(String message) {
            super();
            this.message = message;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message);
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private boolean detectOpenGLES20() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

    @Override
    protected void onResume() {
        // Ideally a application should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
        case (MotionEvent.ACTION_DOWN):
            Point2D point2d = new Point2D(event.getX(), event.getY());
            Log.d(DEBUG_TAG, "Action was DOWN " + point2d.toString());
            switch (currentWorkMode) {
            case WORK_MODE_ADD:
                pointsRenderer.addVertex(point2d);
                break;
            case WORK_MODE_DELETE:
                pointsRenderer.removeVertex(point2d);
                break;
            case WORK_MODE_EDIT:
                pointsRenderer.selectVertex(point2d);
                break;
            default:
                break;
            }
            return true;
        case (MotionEvent.ACTION_MOVE):
            if (currentWorkMode == WORK_MODE_EDIT) {
                pointsRenderer.moveSelectedVertexTo(event.getX(), event.getY());
            }
            return true;
        case (MotionEvent.ACTION_UP):
            Log.d(DEBUG_TAG, "Action was UP");
            if (currentWorkMode == WORK_MODE_EDIT) {
                pointsRenderer.deselectVertex();
            }
            return true;
        case (MotionEvent.ACTION_CANCEL):
            Log.d(DEBUG_TAG, "Action was CANCEL");
            return true;
        case (MotionEvent.ACTION_OUTSIDE):
            Log.d(DEBUG_TAG, "Movement occurred outside bounds of current screen element");
            return true;
        default:
            return super.onTouchEvent(event);
        }
    }

    private GLSurfaceView mGLSurfaceView;
}
