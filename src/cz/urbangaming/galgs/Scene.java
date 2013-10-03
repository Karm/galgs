package cz.urbangaming.galgs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import android.opengl.GLES20;
import android.util.Log;

/**
 * 
 * @author Michal Karm Babacek
 * 
 */
class Scene {

    private final String vertexShaderCode =
            "uniform   mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "    gl_PointSize = 5.0;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform   vec4    vColor;" +
                    "void main() {" +
                    "    gl_FragColor = vColor;" +
                    "}";

    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private FloatBuffer vertexBuffer = null;

    static final int COORDS_PER_VERTEX = 3;
    private Vector<Float> sceneCoords = new Vector<Float>();
    private int vertexCount = 0;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex
    float color[] = { 1f, 0f, 0f, 1.0f };
    private PointsRenderer pointsRenderer = null;

    public Scene(PointsRenderer pointsRenderer) {
        this.pointsRenderer = pointsRenderer;
        newVertexBufferToDraw();
        // prepare shaders and OpenGL program
        int vertexShader = PointsRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = PointsRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram); // create OpenGL program executables
    }

    public void addVertex(Vec2f coords) {
        sceneCoords.add(coords.X());
        sceneCoords.add(coords.Y());
        sceneCoords.add(0.0f);
        newVertexBufferToDraw();
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexCount, vertexBuffer);
    }

    private void newVertexBufferToDraw() {
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer bb = ByteBuffer.allocateDirect(sceneCoords.size() * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(PointsRenderer.floatVectorToArray(sceneCoords));
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        vertexCount = sceneCoords.size() / COORDS_PER_VERTEX;
        Log.d(GAlg.DEBUG_TAG, "Scene coords to draw: " + sceneCoords.toString());
    }

    public void draw() {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the scene vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the scene coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the scene
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, pointsRenderer.mtrxProjectionAndView, 0);

        // Draw the scene
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}