package cz.urbangaming.galgs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import android.opengl.GLES20;
import android.util.Log;

class Scene {

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    " gl_PointSize = 3.0;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private FloatBuffer vertexBuffer = null;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    /*
     * static float sceneCoords[] = { 0.0f, 0.62f, 0.0f, -0.5f, -0.3f, 0.0f, 0.5f, -0.31f, 0.0f, 0.5f, -0.813f, 0.0f, 0.1f, -0.21f, 0.0f };
     */
    private Vector<Float> sceneCoords = new Vector<Float>();

    // private final int vertexCount = sceneCoords.length / COORDS_PER_VERTEX;
    private int vertexCount = 0;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    private PointsRenderer pointsRenderer = null;

    public Scene(PointsRenderer pointsRenderer) {
        this.pointsRenderer = pointsRenderer; /*
                                               * // initialize vertex byte buffer for shape coordinates ByteBuffer bb = ByteBuffer.allocateDirect( // (number of coordinate values * 4 bytes per float) sceneCoords.length * 4); // use the device hardware's native byte order
                                               * bb.order(ByteOrder.nativeOrder());
                                               * 
                                               * // create a floating point buffer from the ByteBuffer vertexBuffer = bb.asFloatBuffer(); // add the coordinates to the FloatBuffer vertexBuffer.put(sceneCoords); // set the buffer to read the first coordinate vertexBuffer.position(0);
                                               */

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
        Log.d(GAlg.DEBUG_TAG, "TOUCH [" + coords.X() + "," + coords.Y() + "]");
        // Vec2f world = GetWorldCoordsX(coords);
        Vec2f world = muhehe(coords);
        // Log.d(GAlg.DEBUG_TAG, "WORLD [" + world.X() + "," + world.Y() + "]");
        sceneCoords.add(world.X());
        sceneCoords.add(world.Y());
        sceneCoords.add(0.0f);
        newVertexBufferToDraw();
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexCount, vertexBuffer);
    }

    public Vec2f muhehe(Vec2f touch) {
        // 2 / right-left 0 0 -(right+left / right-left)
        // 0 2/(top-bottom) 0 -(top+bottom / top-bottom)
        // 0 0 -2 / farVal-nearVal -(farVal+nearVal / farVal-nearVal)
        // 0 0 0 1
        return new Vec2f(touch.X() / 1000, touch.Y() / 1000);
    }

    private void newVertexBufferToDraw() {
        // initialize vertex byte buffer for shape coordinates
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
        // Log.d(GAlg.DEBUG_TAG, "vertexCount:"+vertexCount+", vertexBuffer:"+vertexBuffer.array()+", vertexStride:"+vertexStride);
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

        // Draw the scene
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}