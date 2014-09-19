package com.weight.craig.catshanks.Managers;

import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * A texture batcher draws multiple sprites of the same texture in a batch, substantially reducing the amount of gl draw calls
 * needed, thus resulting in an decrease in total draw time.(Assuming textures are packed well.)
 *
 * This particular Texture Batcher attempts to not only draw in batches, but does so while attempting to minimize garbage collection.
 * Which is of particular importance when working with android devices.
 *
 * Current features include gpu scaling,automatic coordinate transformation(Must call setSize) and color manipulation.
 * Todo:Gpu Rotation
 * Created by Craig on 11/27/13.
 */
public class TextureBatcher {
    private TextureManager TM= TextureManager.getInstance();
    private static TextureBatcher instance;

    //Batcher variables.
    private int shaderProgram=-1;
    private static final int NO_TEXTURE=-1;
    private int texture=NO_TEXTURE;
    private int BatchCount=0;
    private final int BatchLimit=100;
    private boolean Initialized=false;

    //Gl variable references.
    private int TranslateDrawingLocation;
    private int TranslateTextureLocation;
    private final int vaSrcPosition=0;
    private final int vaSrcOffset=1;
    private final int vaDestPosition=2;
    private final int vaColor=3;

    //float arrays containing pixel data.
    private float [] destPoints=new float[BatchLimit * 16];
    private float [] srcPoints= new float[BatchLimit * 8];
    private float [] VerticeColors =new float[BatchLimit * 16];
    private short [] VerticeIndices= new short[BatchLimit * 6];

    //Texture size information variables(Minimize garbage collection)
    private PointF srcTextureSize=new PointF();
    private RectF srcCopyRect=new RectF();
    private float srcRight, srcBottom;

    //Offsets used for indexing of vertexes for the current drawing operations.
    private int CopyDestOffset;
    private int CopySrcOffset;
    private int CopyColorOffset;
    private int CopyIndicesOffset;
    private int CopyVertexIndexOffset;

    //Scaling/positioning information.
    private float DestRight;
    private float DestBottom;
    private float PositionX;
    private float PositionY;

    //Color Tranformation values.
    private float vRed=1.0f;
    private float vBlue=1.0f;
    private float vGreen=1.0f;
    private float vAlpha=1.0f;

    //Stores screen width and height.
    private int width;
    private int height;

    //Byte buffers used to hold data that will later be sent into gl shader.
    private ByteBuffer BBDestBuffer=ByteBuffer.allocateDirect(destPoints.length*4);
    private ByteBuffer BBSrcBuffer=ByteBuffer.allocateDirect(srcPoints.length*4);
    private ByteBuffer BBColorBuffer=ByteBuffer.allocateDirect(VerticeColors.length*4);
    private ByteBuffer BBIndicesBuffer =ByteBuffer.allocateDirect(VerticeIndices.length * Short.SIZE);

    //Float buffers used to send information into shader.
    private FloatBuffer vDestBuffer;
    private FloatBuffer vSrcBuffer;
    private FloatBuffer vColorBuffer;
    private ShortBuffer vIndices;

    /**
     * Entry point into the texture batcher.
     * @return Reference to the texture batcher.
     */
    public static synchronized TextureBatcher getInstance(){
        if(instance==null) {
            instance=new TextureBatcher();
        }
        return instance;
    }

    /**
     * Compiles the default shader.
     * @return The unique id of the default shader program.
     */
    public int compileDefaultShader(){
        String VertexShaderSource=""+
            "uniform vec4 scrTranslate;\n" +
            "attribute vec4 position;\n" +
            "attribute vec2 texTranslate;\n"+
            "attribute vec2 textureCoordinate;\n" +
            "attribute vec4 color;\n"+
            "varying highp vec2 textureCoordinateVarying;\n" +
            "varying lowp vec4 colorp;\n" +
            "void main(){\n" +
                "gl_Position= (position / scrTranslate)- vec4(1,-1,0,0);\n"+
                "textureCoordinateVarying=(textureCoordinate / texTranslate);\n"+
                "colorp=color;\n"+
            "}";

        String FragmentShaderSource="" +
            "uniform sampler2D textureUnit;\n" +
            "varying lowp vec4 colorp;\n" +
            "varying highp vec2 textureCoordinateVarying;\n" +
            "void main(){\n" +
                "gl_FragColor=texture2D(textureUnit,textureCoordinateVarying) * colorp;\n" +
            "}";

        //Compile vertex shader.
        int vertexShader=GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader,VertexShaderSource);
        GLES20.glCompileShader(vertexShader);

        //Compile fragment shader.
        int fragmentShader=GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader,FragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);

        //Enable these to diagnose shader compilation problems.
        //Log.i("vCompile",GLES20.glGetShaderInfoLog(vertexShader));
        //Log.i("fCompile",GLES20.glGetShaderInfoLog(fragmentShader));

        int program=GLES20.glCreateProgram();
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);

        //Bind attributes
        GLES20.glBindAttribLocation(program, vaDestPosition, "position");
        GLES20.glBindAttribLocation(program,vaSrcOffset,"texTranslate");
        GLES20.glBindAttribLocation(program,vaSrcPosition,"textureCoordinate");
        GLES20.glBindAttribLocation(program,vaColor,"color");

        //Link the program
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        return program;
    }

    /**
     * Initializes the texture batcher.(Note setSize must be called prior to drawing, otherwise the scale will be completely messed up.)
     */
    public void Initialize(){
        //Compiles the default shader, and then sets the GPU's shader to the default shader.
        setShaderProgram(compileDefaultShader());

        //Setup output ordering for buffers.
        BBDestBuffer.order(ByteOrder.nativeOrder());
        BBSrcBuffer.order(ByteOrder.nativeOrder());
        BBColorBuffer.order(ByteOrder.nativeOrder());
        BBIndicesBuffer.order(ByteOrder.nativeOrder());

        //set up vertex buffers.
        vDestBuffer=BBDestBuffer.asFloatBuffer();
        vSrcBuffer=BBSrcBuffer.asFloatBuffer();
        vColorBuffer=BBColorBuffer.asFloatBuffer();
        vIndices=BBIndicesBuffer.asShortBuffer();

        //Log.i("isDirect","Dest: "+ vDestBuffer.isDirect() + " Src:" + vSrcBuffer.isDirect());
        //
        TranslateDrawingLocation=GLES20.glGetUniformLocation(shaderProgram,"scrTranslate");
        TranslateTextureLocation=GLES20.glGetUniformLocation(shaderProgram,"textureUnit");
        GLES20.glUniform2f(TranslateTextureLocation,0,0);
        Initialized=true;
    }

    /**
     * Sends in the screen width/height to the shader, which is then used for various scaling operations.
     * @param width Screen Width.
     * @param height Screen Height.
     */
    public void setSize(int width, int height){
        this.width=width;
        this.height=height;
        //Send coordinate transformation info to shader.
        GLES20.glUniform4f(TranslateDrawingLocation, (float)width/2.0f , -(float)height/2.0f , 1, 1);
    }

    /**
     * Sets the shader program being used/
     * @param program A unique identifier of a compiled shader.
     */
    public void setShaderProgram(int program){
        shaderProgram=program;
    }

    /**
     * Begins a sprite batching operation.
     */
    public void begin(){
        if (BatchCount!=0) flush();
        else BatchCount=0;
    }


    /**
     * Draws all sprites for the current batching operation to the screen.
     */
    public void end(){
        flush();
    }

    /**
     * Draws sprites sent to spriteBatcher during the current batching operation to the screen, and then resets the BatchCount;
     */
    private void flush(){
        //Vertex Buffer copies. (For some reason is faster than modifying vertexes individually)
        vSrcBuffer.put(srcPoints).position(0);
        vDestBuffer.put(destPoints).position(0);
        vColorBuffer.put(VerticeColors).position(0);
        vIndices.put(VerticeIndices).position(0);

        //Send Destination Coodinates to shader
        GLES20.glEnableVertexAttribArray(vaDestPosition);
        GLES20.glVertexAttribPointer(vaDestPosition, 4, GLES20.GL_FLOAT, false, 0, vDestBuffer);
        //Send Texture Coordinates to shader
        GLES20.glEnableVertexAttribArray(vaSrcPosition);
        GLES20.glVertexAttribPointer(vaSrcPosition, 2, GLES20.GL_FLOAT, false, 0, vSrcBuffer);
        //Send Color modification data to shader.
        GLES20.glEnableVertexAttribArray(vaColor);
        GLES20.glVertexAttribPointer(vaColor, 4, GLES20.GL_FLOAT, false, 0, vColorBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,6 * BatchCount,GLES20.GL_UNSIGNED_SHORT,vIndices);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * BatchCount);
        //GLES20.glDrawElements()
        BatchCount = 0;
    }

    /**
     * Sets the color transformation values.
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param a Alpha
     */
    public void setColor(byte r, byte g, byte b, byte a){
        vRed=r/255;
        vGreen=g/255;
        vBlue=b/255;
        vAlpha=a/255;
    }

    /**
     * Sets the color transformation values. (No boundary enforcement)
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param a Alpha
     */
    public void setColor(float r, float g, float b, float a){
        vRed=r;
        vGreen=g;
        vBlue=b;
        vAlpha=a;
    }

    /**
     * Drawing command that is used for final output generation
     * @param x X position to draw texture region
     * @param y Y position to draw texture region
     * @param scaleX Width scale at which to draw the given texture region.
     * @param scaleY Height scale at which to draw the given texture region.
     */
    private void draw(float x, float y,float scaleX,float scaleY){
        //Scaling and positioning performed by the gpu. (Todo:Add Gpu Rotation)
        //Minor Cpu mathematics required.
        //PositionX + DestRight

        srcRight =srcCopyRect.left + srcCopyRect.width();
        srcBottom =srcCopyRect.top + srcCopyRect.height();

        PositionX= x;
        PositionY=y;
        DestRight = PositionX + (srcCopyRect.width() * scaleX);
        DestBottom = PositionY + (srcCopyRect.height() * scaleY);
        GenerateBuffers();
    }


    /**
     * Used to generate all of the output buffer data
     */
    private void GenerateBuffers(){
        GenerateSrcBuffer();
        GenerateDestBuffer();
        GenerateIndicesBuffer();
        GenerateColorBuffer(vRed,vGreen,vBlue,vAlpha);
        BatchCount++;
    }

    /**
     * Generates all of the texture coping data.
     */
    private void GenerateSrcBuffer(){
        CopySrcOffset=BatchCount*8;
        //0
        //top-left
        srcPoints[CopySrcOffset]=srcCopyRect.left;
        srcPoints[CopySrcOffset + 1]=srcCopyRect.top;

        //1
        //bottom-left
        srcPoints[CopySrcOffset + 2]=srcCopyRect.left;
        srcPoints[CopySrcOffset + 3]= srcBottom;


        //2
        //bottom-right
        srcPoints[CopySrcOffset + 4]= srcRight;
        srcPoints[CopySrcOffset + 5]= srcBottom;

        //3
        //top-right
        srcPoints[CopySrcOffset + 6]= srcRight;
        srcPoints[CopySrcOffset + 7]=srcCopyRect.top;
    }



    /**
     * Generates the points necessary for the output buffer
     * without allocating any new memory, thus preventing garbage collection.
     */
    private void GenerateDestBuffer() {
        CopyDestOffset=BatchCount * 16;

        //0
        //Upper left
        destPoints [CopyDestOffset] = PositionX;
        destPoints [CopyDestOffset + 1] = PositionY;
        destPoints [CopyDestOffset + 2] = 0.0f;
        destPoints [CopyDestOffset + 3] = 1.0f;

        //1
        //Bottom-Left
        destPoints [CopyDestOffset + 4] = PositionX;
        destPoints [CopyDestOffset + 5] = DestBottom;
        destPoints [CopyDestOffset + 6] = 0.0f;
        destPoints [CopyDestOffset + 7] = 1.0f;

        //2
        //Bottom Right
        destPoints [CopyDestOffset + 8] = DestRight;
        destPoints [CopyDestOffset + 9] = DestBottom;
        destPoints [CopyDestOffset + 10] = 0.0f;
        destPoints [CopyDestOffset + 11] = 1.0f;

        //3
        //Upper-Right
        destPoints [CopyDestOffset + 12] = DestRight;
        destPoints [CopyDestOffset + 13] = PositionY;
        destPoints [CopyDestOffset + 14] = 0.0f;
        destPoints [CopyDestOffset + 15] = 1.0f;
    }

    /**
     * Generates color buffer for color modification.
     * @param r Red Channel Color
     * @param g Green Channel Color
     * @param b Blue Channel Color
     * @param a Alpha Channel Color
     */
    private void GenerateColorBuffer(float r, float g, float b,float a){
        CopyColorOffset=BatchCount*16;
        for(int i=0; i<16; i+=4){
            VerticeColors[CopyColorOffset+i]=r;
            VerticeColors[CopyColorOffset+(i+1)]=g;
            VerticeColors[CopyColorOffset+(i+2)]=b;
            VerticeColors[CopyColorOffset+(i+3)]=a;
        }
    }

    private void GenerateIndicesBuffer(){
        CopyIndicesOffset=BatchCount * 6;
        CopyVertexIndexOffset=BatchCount * 4;
        //0, 1, 2, 3, 2, 1
        VerticeIndices[CopyIndicesOffset]=(short)(CopyVertexIndexOffset + 0);
        VerticeIndices[CopyIndicesOffset + 1]=(short)(CopyVertexIndexOffset + 1);
        VerticeIndices[CopyIndicesOffset + 2]=(short)(CopyVertexIndexOffset + 2);
        VerticeIndices[CopyIndicesOffset + 3]=(short)(CopyVertexIndexOffset + 2);
        VerticeIndices[CopyIndicesOffset + 4]=(short)(CopyVertexIndexOffset + 3);
        VerticeIndices[CopyIndicesOffset + 5]=(short)(CopyVertexIndexOffset + 0);
    }

    /**
     * Changes the texture in use.
     * @param texture Textur Id.
     */
    private void changeTexture(int texture){
        this.texture=texture;
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
        srcTextureSize=TM.getTextureSize(texture);
        GLES20.glVertexAttrib2f(vaSrcOffset,srcTextureSize.x,srcTextureSize.y);
    }

    /***
     * Draws a portion of a texture to the given destination points on screen.
     * @param texture Texture being used to draw with.
     * @param left Left source texture point.
     * @param top Top source texture point
     * @param right Right source texture point.
     * @param bottom Bottom source texture point.
     * @param destX Destination drawing point. (x-axis)
     * @param destY Destination drawing point. (y-axis)
     */
    public void Draw(int texture,float left, float top,float right, float bottom, float destX, float destY){
        if (Initialized){
            if(this.texture==NO_TEXTURE) changeTexture(texture);

            srcCopyRect.left=left;
            srcCopyRect.top=top;
            srcCopyRect.right=right;
            srcCopyRect.bottom=bottom;

            if(this.texture==texture){
                draw(destX,destY,1.0f,1.0f);
                if (BatchCount==BatchLimit) flush();
            }
            else{
                flush();
                changeTexture(texture);
                draw(destX,destY,1.0f,1.0f);
            }

        }
    }

    /***
     * Draws a portion of a texture to the given destination points on screen at the desired width and height.
     * @param texture Texture being used to draw with.
     * @param left Left source texture point.
     * @param top Top source texture point
     * @param right Right source texture point.
     * @param bottom Bottom source texture point.
     * @param destX Destination drawing point. (x-axis)
     * @param destY Destination drawing point. (y-axis)
     * @param destWidth Destination width.
     * @param destHeight Destination height.
     */
    public void Draw(int texture,float left, float top,float right, float bottom, float destX, float destY,float destWidth,float destHeight){
        if (Initialized){
            if(this.texture==NO_TEXTURE) changeTexture(texture);
            srcCopyRect.left=left;
            srcCopyRect.top=top;
            srcCopyRect.right=right;
            srcCopyRect.bottom=bottom;
            if(this.texture==texture){
                draw(destX, destY, destWidth / (srcCopyRect.right - srcCopyRect.left), destHeight/ (srcCopyRect.bottom - srcCopyRect.top));
                if (BatchCount==BatchLimit) flush();
            }
            else{
                flush();
                changeTexture(texture);
                draw(destX, destY, destWidth / (srcCopyRect.right - srcCopyRect.left), destHeight/ (srcCopyRect.bottom - srcCopyRect.top));
            }
        }
    }

    /**
     * Draws the desired portion of a texture to the given x and y positions without scaling.
     * @param texture Texture being drawn to screen.
     * @param srcRect Source texture coordinates.
     * @param x X screen coordinate.
     * @param y Y screen coordinate.
     */
    public void Draw(int texture,RectF srcRect, float x, float y){
        if (Initialized){
            if(texture==NO_TEXTURE) changeTexture(texture);

            srcCopyRect.left=srcRect.left;
            srcCopyRect.top=srcRect.top;
            srcCopyRect.right=srcRect.right;
            srcCopyRect.bottom=srcRect.bottom;

            if(this.texture==texture){
                draw(x, y, 1.0f, 1.0f);
                if (BatchCount==BatchLimit) flush();
            }
            else{
                flush();
                changeTexture(texture);
                draw(x, y, 1.0f, 1.0f);
            }
        }
    }

    /**
     * Draws a portion of a texture to the desired screen coordinates at the desired scale
     * @param texture Texture that is being used.
     * @param srcRect Source texture coordinates.
     * @param x X screen coordinate.
     * @param y Y screen coordinate.
     * @param scale Percentage by which to scale the image (0.0-x.x)
     */
    public void Draw(int texture,RectF srcRect, float x, float y,float scale){
        if (Initialized){
            if(texture==NO_TEXTURE) changeTexture(texture);

            srcCopyRect.left=srcRect.left;
            srcCopyRect.top=srcRect.top;
            srcCopyRect.right=srcRect.right;
            srcCopyRect.bottom=srcRect.bottom;

            if(this.texture==texture){
                draw(x, y, scale, scale);
                if (BatchCount==BatchLimit) flush();
            }
            else{
                flush();
                changeTexture(texture);
                draw(x, y, scale, scale);
            }
        }
    }

    /**
     * Draw a portion of a texture to the desired position, with custom scaling for the width and height
     * @param texture Texture being used to draw with.
     * @param srcRect Source texture coordinates.
     * @param x X screen coordinate.
     * @param y Y screen coordinate.
     * @param scaleX X-scaling factor.
     * @param scaleY Y-scaling factor.
     */
    public void Draw(int texture,RectF srcRect, float x, float y,float scaleX,float scaleY){
        if (Initialized){
            if(texture==NO_TEXTURE) changeTexture(texture);

            srcCopyRect.left=srcRect.left;
            srcCopyRect.top=srcRect.top;
            srcCopyRect.right=srcRect.right;
            srcCopyRect.bottom=srcRect.bottom;

            if(this.texture==texture){
                draw(x, y, scaleX, scaleY);
                if (BatchCount==BatchLimit) flush();
            }
            else{
                flush();
                changeTexture(texture);
                draw(x, y, scaleX, scaleY);
            }
        }
    }
}
