package cz.urbangaming.galgs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
class PointsRenderer implements GLSurfaceView.Renderer {

    public final float[] mtrxProjection = new float[16];
    public final float[] mtrxView = new float[16];
    public final float[] mtrxProjectionAndView = new float[16];

    private int surfaceWidth = 0;
    private int surfaceHeight = 0;

    private Scene mScene = null;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame colorVertices
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mScene = new Scene(this);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Draw background colorVertices
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Draw scene
        mScene.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Hmm...is this singlethreaded, right?
        this.surfaceWidth = width;
        this.surfaceHeight = height;

        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);
        // Clear our matrices
        // TODO: Hmm, let's do it better...
        for (int i = 0; i < 16; i++) {
            mtrxProjection[i] = 0.0f;
            mtrxView[i] = 0.0f;
            mtrxProjectionAndView[i] = 0.0f;
        }

        // Screen to the drawing coordinates
        final float left = 0;
        final float right = width;
        final float bottom = height;
        final float top = 0;
        final float near = -1f;
        final float far = 1f;

        Matrix.orthoM(mtrxProjection, 0, left, right, bottom, top, near, far);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
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

    public static float[] floatVectorToArray(List<Float> sceneCoords) {
        float[] ret = new float[sceneCoords.size()];
        Iterator<Float> iterator = sceneCoords.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().floatValue();
        }
        return ret;
    }

    public int getSurfaceWidth() {
        return surfaceWidth;
    }

    public int getSurfaceHeight() {
        return surfaceHeight;
    }

    // TODO: This is rather silly, let's remove this delegate-chain because we ain't gonna do anything but calling mScene here...

    public void removeVertex(float x, float y) {
        mScene.removeVertex(x, y);
    }

    public void addVertex(float x, float y) {
        mScene.addVertex(x, y);
    }

    public void clearScene() {
        mScene.clearScene();
    }

    public void addRandomPoints() {
        mScene.addRandomPoints();
    }

    public void selectVertex(float x, float y) {
        mScene.selectVertex(x, y);
    }

    public void moveSelectedVertexTo(float x, float y) {
        mScene.moveSelectedVertexTo(x, y);
    }

    public void deselectVertex() {
        mScene.deselectVertex();
    }

    public void renderAlgorithm(int algorithmUsed) {
        mScene.renderLines(algorithmUsed);
    }

}
