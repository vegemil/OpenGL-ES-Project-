/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android.objects;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

import java.util.ArrayList;
import java.util.List;

import android.util.FloatMath;

import com.airhockey.android.util.Geometry.Circle;
import com.airhockey.android.util.Geometry.Cube;
import com.airhockey.android.util.Geometry.Cylinder;
import com.airhockey.android.util.Geometry.Point;
import com.airhockey.android.util.Geometry.Vector;

class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    static GeneratedData createPuck(Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints)
                 + sizeOfOpenCylinderInVertices(numPoints);
        
        ObjectBuilder builder = new ObjectBuilder(size);

        Circle puckTop = new Circle(
            puck.center.translateY(puck.height / 2f),
            puck.radius);
        
        builder.appendCircle(puckTop, numPoints);
        //builder.appendCircle(puck, numPoints);

        return builder.build();
    }

    static GeneratedData createMallet(
        Point center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2
                 + sizeOfOpenCylinderInVertices(numPoints) * 2;
        
        ObjectBuilder builder = new ObjectBuilder(size);                                      
        
        // First, generate the mallet base.
        float baseHeight = height * 0.25f;
        
        Circle baseCircle = new Circle(
            center.translateY(-baseHeight), 
            radius);
        Cylinder baseCylinder = new Cylinder(
            baseCircle.center.translateY(-baseHeight / 2f), 
            radius, baseHeight);

        builder.appendCircle(baseCircle, numPoints);
        //builder.appendCircle(baseCylinder, numPoints);
                
        // Now generate the mallet handle.
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        
        Circle handleCircle = new Circle(
            center.translateY(height * 0.5f), 
            handleRadius);        
        Cylinder handleCylinder = new Cylinder(
            handleCircle.center.translateY(-handleHeight / 2f),
            handleRadius, handleHeight);                

        builder.appendCircle(handleCircle, numPoints);
        //builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }    

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private void appendCircle(Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = 
                  ((float) i / (float) numPoints)
                * ((float) Math.PI * 2f);
            
            vertexData[offset++] = 
                  circle.center.x 
                + circle.radius * FloatMath.cos(angleInRadians);
            
            vertexData[offset++] = circle.center.y;
            
            vertexData[offset++] = 
                  circle.center.z 
                + circle.radius * FloatMath.sin(angleInRadians);            
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex,
                    numVertices);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }
    
    static GeneratedData createGoalPost(Point center, float width, float height, float thickness)
    {
        int size = 500;
       
        ObjectBuilder builder  = new ObjectBuilder(size);
        
        //중심점을 크로스바의 중심으로 이동
        Cube crossBar_1 = new Cube(center.translateY(height * 0.5f - thickness * 0.5f) , width, thickness, thickness);
        Cube crossBar_2 = new Cube(center.translateX(width * 0.5f - thickness * 0.5f) , thickness, height, thickness);
        Cube crossBar_3 = new Cube(center.translateX(-width * 0.5f + thickness * 0.5f) , thickness, height, thickness);
        
        builder.appendCube(crossBar_1);
        //builder.appendOpenCube(crossBar_2);
        //builder.appendOpenCube(crossBar_3);
       
        return builder.build();
    }
    
    private void appendCube(Cube cube) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = 36;

        //Triangle (Front)
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        //Triangle (Back)
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
         
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
                
        //Triangle (Left)
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        //Triangle (Right)
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        //Triangle (Top)
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y + cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        //Triangle (Bottom)
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
              
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 0f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z - cube.thickness * 0.5f;
        vertexData[offset++] = 0f;  vertexData[offset++] = 0f;
                
        vertexData[offset++] = cube.center.x + cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        vertexData[offset++] = cube.center.x - cube.width * 0.5f;
        vertexData[offset++] = cube.center.y - cube.height * 0.5f;
        vertexData[offset++] = cube.center.z + cube.thickness * 0.5f;
        vertexData[offset++] = 1f;  vertexData[offset++] = 1f;
        
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES, startVertex, numVertices);   
            }
        });
    }
    
    
   
    static GeneratedData createDesk(Point center, float width, float height, float thickness)
    {
        int size = 100;
       
        ObjectBuilder builder  = new ObjectBuilder(size);
                
        //중심점을 책상의 중심으로 이동
        Cube deskUpper = new Cube(center.translate(new Vector(0f, height * 0.5f - thickness * 0.5f, 0f)) , width, thickness, height);
        Cube deskLeg_1 = new Cube(center.translate(new Vector(width * 0.5f - thickness * 0.5f, 0f, -height * 0.5f + thickness)), thickness, height, thickness);
        Cube deskLeg_2 = new Cube(center.translate(new Vector(-width * 0.5f + thickness * 0.5f, 0f, - height * 0.5f + thickness)), -thickness, height, thickness);
        Cube deskLeg_3 = new Cube(center.translateX(width * 0.5f - thickness * 0.5f) , thickness, -height, -thickness);
        Cube deskLeg_4 = new Cube(center.translateX(-width * 0.5f + thickness * 0.5f) , -thickness, -height, -thickness);
        
        builder.appendCube(deskUpper);
        builder.appendCube(deskLeg_1);
        builder.appendCube(deskLeg_2);
        builder.appendCube(deskLeg_3);
        builder.appendCube(deskLeg_4);
       
        return builder.build();
    }
    
    static GeneratedData createChair(Point center, float width, float height, float thickness)
    {
        int size = 80;
       
        ObjectBuilder builder  = new ObjectBuilder(size);
                
        //중심점을 크로스바의 중심으로 이동
        Cube deskUpper = new Cube(center.translate(new Vector(0f, height * 0.5f - thickness * 0.5f, 0f)) , width, thickness, height);
        Cube deskLeg_1 = new Cube(center.translate(new Vector(width * 0.5f - thickness * 0.5f, 0f, -height * 0.5f + thickness)), thickness, height, thickness);
        Cube deskLeg_2 = new Cube(center.translate(new Vector(-width * 0.5f + thickness * 0.5f, 0f, - height * 0.5f + thickness)), -thickness, height, thickness);
        Cube deskLeg_3 = new Cube(center.translateX(width * 0.5f - thickness * 0.5f) , thickness, -height, -thickness);
        Cube deskLeg_4 = new Cube(center.translateX(-width * 0.5f + thickness * 0.5f) , -thickness, -height, -thickness);
        
        
        builder.appendCube(deskUpper);
        //builder.appendOpenCube(deskLeg_1);
        //builder.appendOpenCube(deskLeg_2);
        //builder.appendOpenCube(deskLeg_3);
        //builder.appendOpenCube(deskLeg_4);
       
        return builder.build();
    }
}
