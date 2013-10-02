package cz.urbangaming.galgs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.WindowManager;

class PointsRenderer implements GLSurfaceView.Renderer {

    private Point displayDimension = null;

    public PointsRenderer(Context context) {
        super();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayDimension = new Point();
        wm.getDefaultDisplay().getSize(displayDimension);
    }

    private Scene mScene;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mScene = new Scene(this);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Draw scene
        mScene.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);
        // float ratio = (float) width / height;
        // Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        // GLU.gluOrtho2D(unused,0,width,0,height);
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static float[] floatVectorToArray(Vector<Float> floats) {
        Log.d(GAlg.DEBUG_TAG, "FUCKINF INPUT: " + floats.toString());
        float[] ret = new float[floats.size()];
        Iterator<Float> iterator = floats.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().floatValue();
        }
        Log.d(GAlg.DEBUG_TAG, "FUCKINF OUTPUT: " + Arrays.toString(ret));
        return ret;
    }

    public Point getDisplayDimension() {
        return displayDimension;
    }

    public void addVertex(Vec2f coords) {
        mScene.addVertex(coords);
    }

}