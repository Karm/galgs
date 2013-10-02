package cz.urbangaming.galgs;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

public class GAlg extends Activity {
    public static final String DEBUG_TAG = "KARM";
    private PointsRenderer pointsRenderer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        if (detectOpenGLES20()) {
            // Tell the surface view we want to create an OpenGL ES 2.0-compatible
            // context, and set an OpenGL ES 2.0-compatible renderer.
            mGLSurfaceView.setEGLContextClientVersion(2);
            pointsRenderer = new PointsRenderer(this);
            mGLSurfaceView.setRenderer(pointsRenderer);
        } else {
            // error
        }
        setContentView(mGLSurfaceView);
    }

    private boolean detectOpenGLES20() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
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
            float x = event.getAxisValue(MotionEvent.AXIS_X);
            float y = event.getAxisValue(MotionEvent.AXIS_Y);
            Log.d(DEBUG_TAG, "Action was DOWN [" + x + "," + y + "]");
            pointsRenderer.addVertex(new Vec2f(x, y));
            return true;
        case (MotionEvent.ACTION_MOVE):
            Log.d(DEBUG_TAG, "Action was MOVE");
            return true;
        case (MotionEvent.ACTION_UP):
            Log.d(DEBUG_TAG, "Action was UP");
            return true;
        case (MotionEvent.ACTION_CANCEL):
            Log.d(DEBUG_TAG, "Action was CANCEL");
            return true;
        case (MotionEvent.ACTION_OUTSIDE):
            Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                    "of current screen element");
            return true;
        default:
            return super.onTouchEvent(event);
        }
    }

    private GLSurfaceView mGLSurfaceView;
}
