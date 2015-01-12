package com.airhockey.android.objects;

import java.util.List;

import com.airhockey.android.data.VertexArray;
import com.airhockey.android.objects.ObjectBuilder.DrawCommand;
import com.airhockey.android.objects.ObjectBuilder.GeneratedData;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.util.Geometry.Point;

public class GoalPost {
    private static final int POSITION_COMPONENT_COUNT = 3;
    
    public final float width, height, thickness;
    
    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;
    
    public GoalPost(float width, float height, float thickness)
    {
        GeneratedData generatedData = ObjectBuilder.createGoalPost(new Point(0f, 0f, 0f), width, height, thickness);
        
        this.width = width;
        this.height = height;
        this.thickness = thickness;
        
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }
    
    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 0);
    }
    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
    
}
