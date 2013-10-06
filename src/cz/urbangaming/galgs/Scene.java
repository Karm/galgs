package cz.urbangaming.galgs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.util.Log;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
class Scene {
    private final String vertexShaderCode =
            "uniform   mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "    gl_PointSize = " + GAlg.POINT_SIZE + ";" +
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
    private List<Float> sceneCoords = new ArrayList<Float>();
    private int vertexCount = 0;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex
    float color[] = { 1f, 0f, 0f, 1.0f };
    private PointsRenderer pointsRenderer = null;

    // -1 means "none selected" X Y Z index
    private int selectedVertexIndexes[] = { -1, -1, -1 };
    private boolean vertexSelected = false;

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

    public void clearScene() {
        sceneCoords.clear();
        newVertexBufferToDraw();
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexCount, vertexBuffer);
    }

    public void addRandomPoints() {
        sceneCoords.addAll(Utils.generateSomeVertices(GAlg.HOW_MANY_POINTS_GENERATE,
                GAlg.BORDER_POINT_POSITION,
                GAlg.BORDER_POINT_POSITION,
                pointsRenderer.getSurfaceWidth() - GAlg.BORDER_POINT_POSITION,
                pointsRenderer.getSurfaceHeight() - GAlg.BORDER_POINT_POSITION));
        newVertexBufferToDraw();
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexCount, vertexBuffer);
    }

    public void addVertex(float x, float y) {
        sceneCoords.add(x);
        sceneCoords.add(y);
        sceneCoords.add(0.0f);
        newVertexBufferToDraw();
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexCount, vertexBuffer);
    }

    public void selectVertex(float x, float y) {
        if (!vertexSelected) {
            // TODO: Shouldn't we somehow mark the selected vertex? Colour?
            for (int i = 0; i < sceneCoords.size(); i += 3) {
                float thisX = sceneCoords.get(i);
                float thisY = sceneCoords.get(i + 1);
                // float thisZ = sceneCoords.get(i + 2);
                if (Utils.isInRectangle(x, y, GAlg.FINGER_ACCURACY, thisX, thisY)) {
                    selectedVertexIndexes[0] = i;
                    selectedVertexIndexes[1] = i + 1;
                    selectedVertexIndexes[2] = i + 2;
                    vertexSelected = true;
                    break;
                }
            }
        }
    }

    public void moveSelectedVertexTo(float x, float y) {
        if (vertexSelected) {
            // Update location x,y
            sceneCoords.set(selectedVertexIndexes[0], x);
            sceneCoords.set(selectedVertexIndexes[1], y);
            newVertexBufferToDraw();
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexCount, vertexBuffer);
        }
    }

    public void deselectVertex() {
        vertexSelected = false;
        selectedVertexIndexes[0] = -1;
        selectedVertexIndexes[1] = -1;
        selectedVertexIndexes[2] = -1;
    }

    public void removeVertex(float x, float y) {
        // Does it make any sense to initialize the capacity here?
        List<Float> newSceneCoords = new ArrayList<Float>(sceneCoords.size());
        for (int i = 0; i < sceneCoords.size(); i += 3) {
            float thisX = sceneCoords.get(i);
            float thisY = sceneCoords.get(i + 1);
            float thisZ = sceneCoords.get(i + 2);
            if (!Utils.isInRectangle(x, y, GAlg.FINGER_ACCURACY, thisX, thisY)) {
                newSceneCoords.add(thisX);
                newSceneCoords.add(thisY);
                newSceneCoords.add(thisZ);
            }
        }
        sceneCoords = newSceneCoords;
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