package com.mxgraph.filter;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.geom.GeneralPath;
import com.mxgraph.util.mxUtils;


/**
 * http://www.jhlabs.com/ip/filters/index.html
 * ===============================================
 * https://stackoverflow.com/questions/34123731/add-glow-to-a-basic-java-rectangle
 * https://stackoverflow.com/questions/21134488/how-do-i-add-shadow-to-undecorated-jframe-in-netbeans
 * https://stackoverflow.com/questions/14655643/java-create-shadow-effect-on-image/14656403
 */
public class mxHighLightFilter {
	public int width;
	public int height;
	public int x;
	public int y;
	int borderWidth = 100; 
	Color color;
	Color fillColor = Color.RED;
	int size = 50;
	float alpha = 1f;
	float radius = 5f;
	GeneralPath shape;
	Stroke stroke;
	
	public mxHighLightFilter(GeneralPath shape, Color color) {
		this.shape = shape;
		Rectangle pathBounds = shape.getBounds();
		this.width = pathBounds.width;
		this.height = pathBounds.height;
        this.x = pathBounds.x;
        this.y = pathBounds.y;
        this.color = color;
	    stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
	    stroke = new BasicStroke(15.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1f);
	}
	
	public void draw(Graphics2D g2) {
        BufferedImage imgBlur = mxUtils.createBufferedImage(width + borderWidth*2, height + borderWidth*2, null);
        imgBlur = generateBlur(imgBlur);
        g2.translate(-borderWidth, -borderWidth);
        g2.drawImage(imgBlur, x, y, null);
        g2.dispose();
	}
	
	public BufferedImage generateBlur(BufferedImage imgSource) {

        BufferedImage imgBlur;
        imgBlur = new BufferedImage(width + borderWidth*2, height + borderWidth*2, BufferedImage.TRANSLUCENT);
        
        Graphics2D g2 = imgBlur.createGraphics();
        g2.setColor(color);
        g2.translate(-x+borderWidth,-y+borderWidth);
        g2.setStroke(stroke);
		
        g2.draw(shape);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));

        Kernel kernel = makeKernel();
        
		BufferedImageOp op = new ConvolveOp( kernel, ConvolveOp.EDGE_NO_OP, null );
		imgBlur = op.filter(imgBlur, null);
		
        return imgBlur;

    }
	
    protected Kernel makeKernel() {
    	int radius = 20;
    	int pixelCount = radius*radius;
		
    	float[] matrix = new float[pixelCount];
    	
    	for (int i = 0; i < pixelCount; i++) {
    		matrix[i] = 1.0f/500.0f;
    	}
		return new Kernel(radius, radius, matrix);
	}
}