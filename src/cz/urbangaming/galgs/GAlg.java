package cz.urbangaming.galgs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ruboto.JRubyAdapter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import cz.urbangaming.galgs.utils.Point2D;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class GAlg extends FragmentActivity implements OnNavigationListener {
    public static final String DEBUG_TAG = "KARM";

    private PointsRenderer pointsRenderer = null;

    // Ruby integration
    public static final String GALGS_CLASS_DIR = Environment.getExternalStorageDirectory().toString() + File.separator + "scripts";
    public static final String GALGS_CLASS_FILE = "galg_algorithms.rb";

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
    public static final int KD_TREE = 65;
    public static final int RELOAD_RUBY_SCRIPT = 162;

    public static final int REMOVE_ALL_POINTS = 70;
    public static final int ADD_RANDOM_POINTS = 80;

    private ArrayAdapter<String> aAdpt = null;
    private ActionProvider javaAlgsActionProvider = null;
    private ActionProvider rubyAlgsActionProvider = null;
    private File galgsRubyClassesDirectory = null;

    // Menus end

    private int currentWorkMode = WORK_MODE_ADD;

    // /////// Some settings: Move it outside... /////////

    // TODO: THIS IS SO EPICLY WRONG! I must calculate it accordingly to display's density...
    public static final float POINT_SIZE = 7f;
    public static final int FINGER_ACCURACY = Math.round(POINT_SIZE) * 3;
    // No, it's not very convenient to have points too close to boundaries
    public static final int BORDER_POINT_POSITION = Math.round(POINT_SIZE) * 3;
    public static final int HOW_MANY_POINTS_GENERATE = Math.round(POINT_SIZE) * 3;

    // Ruby dynamically generated options
    @SuppressLint("UseSparseArrays")
    private Map<Integer, String> rubyMethods = new HashMap<Integer, String>();

    public static final Pattern pattern = Pattern.compile("[ \\t]*def[ \\t]*galgs_([^(]*)\\(.*");
    public static final int MAX_METHOD_NAME_LENGTH = 30;

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (aAdpt != null && itemPosition == aAdpt.getPosition(getResources().getString(R.string.workmode_add))) {
            currentWorkMode = WORK_MODE_ADD;
            return true;
        } else if (aAdpt != null && itemPosition == aAdpt.getPosition(getResources().getString(R.string.workmode_delete))) {
            currentWorkMode = WORK_MODE_DELETE;
            return true;
        } else if (aAdpt != null && itemPosition == aAdpt.getPosition(getResources().getString(R.string.workmode_edit))) {
            currentWorkMode = WORK_MODE_EDIT;
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayList<String> itemList = new ArrayList<String>();
        //TODO:Make static, use String constants strings.xml
        itemList.add(getResources().getString(R.string.workmode_add));
        itemList.add(getResources().getString(R.string.workmode_edit));
        itemList.add(getResources().getString(R.string.workmode_delete));
        this.aAdpt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemList);
        actionBar.setListNavigationCallbacks(aAdpt, this);
        mGLSurfaceView = new GLSurfaceView(this);

        if (detectOpenGLES20()) {
            // Tell the surface view we want to create an OpenGL ES 2.0-compatible
            // context, and set an OpenGL ES 2.0-compatible renderer.
            mGLSurfaceView.setEGLContextClientVersion(2);
            pointsRenderer = new PointsRenderer(this);
            mGLSurfaceView.setRenderer(pointsRenderer);
        } else {
            // TODO: Handle as an unrecoverable error and leave the activity somehow...
        }

        // External files preparation

        InputStream in = null;
        OutputStream out = null;
        try {
            Log.d(DEBUG_TAG, "Media rady: " + isExternalStorageWritable());

            AssetManager assetManager = getAssets();
            in = assetManager.open(GALGS_CLASS_FILE);
            if (in != null) {
                galgsRubyClassesDirectory = new File(GALGS_CLASS_DIR);
                galgsRubyClassesDirectory.mkdir();
                if (!galgsRubyClassesDirectory.isDirectory()) {
                    Log.d(DEBUG_TAG, "Hmm, " + galgsRubyClassesDirectory + " does not exist, trying mkdirs...");
                    galgsRubyClassesDirectory.mkdirs();
                }
                File outputFile = new File(galgsRubyClassesDirectory, GALGS_CLASS_FILE);
                if (outputFile.exists()) {
                    // Load from what user might have edited
                    outputFile = new File(galgsRubyClassesDirectory, GALGS_CLASS_FILE + ".orig");
                }
                out = new FileOutputStream(outputFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } else {
                Log.e("IO HELL", "Asset " + GALGS_CLASS_FILE + " not found...");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Stops the thing from trashing the context on pause/resume.
        mGLSurfaceView.setPreserveEGLContextOnPause(true);
        setContentView(mGLSurfaceView);

    }

    private void loadRubyMethodsToMenu(BufferedReader bufferedReader) throws IOException, InterruptedException {
        String line = null;
        rubyMethods.clear();
        Log.d(DEBUG_TAG, "I'm in loadRubyMethodsToMenu nad  bufferedReader.read():" + bufferedReader.read());

        while ((line = bufferedReader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches() && matcher.group(1) != null && matcher.group(1).length() <= MAX_METHOD_NAME_LENGTH) {
                Log.d(DEBUG_TAG, "Adding method " + matcher.group(1));
                rubyMethods.put(matcher.group(1).hashCode(), matcher.group(1));
            } else {
                Log.d(DEBUG_TAG, "Line [" + line + "] does not contain the expected method...");
            }
        }
        bufferedReader.close();
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        Log.d(DEBUG_TAG, "copyFile: in.available():"+in.available());
        while ((read = in.read(buffer)) != -1) {
            Log.d(DEBUG_TAG, "copyFile: buffer:"+new String(buffer));
            out.write(buffer, 0, read);
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: OMG, put this in a separate, non blocking task!!!
        Log.d(DEBUG_TAG, "CALLED onCreateOptionsMenu!");
        try {
            File rbClassFile = new File(galgsRubyClassesDirectory, GALGS_CLASS_FILE);
            Log.d(DEBUG_TAG, "rbClassFile.getAbsolutePath() " + rbClassFile.getAbsolutePath());
            Log.d(DEBUG_TAG, "rbClassFile.exists() " + rbClassFile.exists());
            Log.d(DEBUG_TAG, "rbClassFile.length() " + rbClassFile.length());
            Log.d(DEBUG_TAG, "rbClassFile.canRead() " + rbClassFile.canRead());
            Log.d(DEBUG_TAG, "rbClassFile.canWrite() " + rbClassFile.canWrite());
            FileReader rbClassFileReader = new FileReader(rbClassFile);
            Log.d(DEBUG_TAG, "rbClassFile " + rbClassFileReader.getEncoding());

            loadRubyMethodsToMenu(new BufferedReader(rbClassFileReader));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
        //JavaAlgs Action provider
        javaAlgsActionProvider = new JavaAlgsActionProvider(this);
        //RubyAlgs Action provider
        Log.d(DEBUG_TAG, "Ruby methods just before creatingProvider:" + rubyMethods);

        rubyAlgsActionProvider = new RubyAlgsActionProvider(this, rubyMethods);
        
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.galg, menu);
        menu.getItem(0).setActionProvider(javaAlgsActionProvider);
        menu.getItem(1).setActionProvider(rubyAlgsActionProvider);

        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean itemHandled = true;
        switch (item.getItemId()) {
        case R.id.remove_all_points:
            pointsRenderer.clearScene();
            break;
        case R.id.generate_random_points:
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
        case KD_TREE:
            doTheJob(KD_TREE);
            break;
        case RELOAD_RUBY_SCRIPT:
            invalidateOptionsMenu();
            break;
        default:
            if (rubyMethods.containsKey(item.getItemId())) {
                if (!JRubyAdapter.isInitialized()) {
                    Toast.makeText(this, getResources().getString(R.string.init_jruby_thing), Toast.LENGTH_LONG).show();
                    JRubyAdapter.setUpJRuby(this);
                }
                doTheJob(item.getItemId());
            } else {
                itemHandled = false;
            }
            break;
        }
        return itemHandled;
    }

    //WRONG!!! What about onPause and onResume?
    // What about user clicking the button several times in a row?
    // Must re-think this bit... 
    protected void doTheJob(final int algorithm) {
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

    // TODO:
    // replace this stuff with a simple         Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
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
            Point2D point2d = new Point2D(event.getRawX(), event.getRawY());
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
                pointsRenderer.moveSelectedVertexTo(event.getRawX(), event.getRawY());
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

    public Map<Integer, String> getRubyMethods() {
        return rubyMethods;
    }
}
