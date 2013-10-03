package cz.urbangaming.galgs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Vector;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

class Scene {


    
    
    /*private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    " gl_PointSize = 3.0;" +
                    "}";*/
    private final String    vertexShaderCode = "uniform    mat4        uMVPMatrix;" +
            "attribute  vec4        vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            " gl_PointSize = 4.0;" +
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
    float color[] = { 1f, 0f, 0f, 1.0f };
    private PointsRenderer pointsRenderer = null;

    public Scene(PointsRenderer pointsRenderer) {
        this.pointsRenderer = pointsRenderer; 
        Log.d(GAlg.DEBUG_TAG, "X Dimension is:" + pointsRenderer.getDisplayDimension().x);
        Log.d(GAlg.DEBUG_TAG, "Y Dimension is:" + pointsRenderer.getDisplayDimension().y);
/*
        vertexShaderCode =
                "attribute vec4 vPosition;\n" +
                        "void main() {\n" +
                        "  gl_Position = vPosition;\n" +
                        "  !!!gl_Position = vec4(" +
                        "                       vPosition.x *  2.0 / " + pointsRenderer.getDisplayDimension().x + " - 1.0," +
                        "                       vPosition.y * -2.0 / " + pointsRenderer.getDisplayDimension().y + " + 1.0," +
                        "                       vPosition.z," +
                        "                       1.0" +
                        "                      );!!!!\n" +
                        "    gl_PointSize = 5.0;\n" +
                        "}\n";*/
        
     

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

  /* private void loadOrthoMatrix(float[] matrix, float left, float right, float bottom, float top, float near, float far) {
        float r_l = right - left;
        float t_b = top - bottom;
        float f_n = far - near;
        float tx = - (right + left) / (right - left);
        float ty = - (top + bottom) / (top - bottom);
        float tz = - (far + near) / (far - near);

        matrix[0] = 2.0f / r_l;
        matrix[1] = 0.0f;
        matrix[2] = 0.0f;
        matrix[3] = tx;

        matrix[4] = 0.0f;
        matrix[5] = 2.0f / t_b;
        matrix[6] = 0.0f;
        matrix[7] = ty;

        matrix[8] = 0.0f;
        matrix[9] = 0.0f;
        matrix[10] = 2.0f / f_n;
        matrix[11] = tz;

        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;
    }*/
    
    public Vec2f muhehe(Vec2f touch) {
        /*
        // 2 / right-left       0                   0               -(right+left / right-left)
        // 0               2/(top-bottom)           0               -(top+bottom / top-bottom)
        // 0                    0            -2 / farVal-nearVal    -(farVal+nearVal / farVal-nearVal)
        // 0                    0                   0                           1
        
        
        float matrix[] = new float[16];
       // loadOrthoMatrix(matrix, 0f, 960f, 0f, 540f, 1f, -1f); 
        
        
        Log.d(GAlg.DEBUG_TAG, "FUCKINF MATRIXXXX OUTPUT: " + Arrays.toString(matrix));
        //Matrix.orthoM(m, mOffset, left, right, bottom, top, near, far);
        
        //int useForOrtho = Math.min( width, height );

     // TODO: Is this wrong?
         //Matrix.orthoM( mVMatrix, 0, -useForOrtho / 2, useForOrtho / 2,
          //          -useForOrtho / 2, useForOrtho / 2, 0.1f, 100f );
         
         //Matrix.orthoM(matrix, 0,     -960f/2, 960f/2,  -540f/2,  540f/2, -1f, 1f);
        

        //Matrix.orthoM(matrix, 0,     0f, 960f,  0f,  540f, -1f, 1f);
        
        Matrix.orthoM(matrix, 0,     0, 960,  0,  540, -1f, 1f);

        float resultVec[] = new float[4];
       float rhsVec[] = new float[] {touch.X(), touch.Y(),0,0};
        Log.d(GAlg.DEBUG_TAG, "RESULT VECTOR BEFORE: " + Arrays.toString(resultVec));

        Matrix.multiplyMV(resultVec, 0, matrix, 0, rhsVec, 0);
        Log.d(GAlg.DEBUG_TAG, "RESULT VECTOR AFTER: " + Arrays.toString(resultVec));

        //return new Vec2f(resultVec[0], resultVec[1]);
        
        return new Vec2f(rhsVec[0], rhsVec[1]);
*/
        
        return touch;

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

        
        
        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
 
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, pointsRenderer.getMtrxProjectionAndView(), 0);
        
        
        
        // Draw the scene
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}