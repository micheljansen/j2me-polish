//#condition polish.midp || polish.usePolishGui
package de.enough.polish.graphics3d.utils;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Generel 3D graphics utility class.
 * <p>
 * Implements helper functions associated with 3d graphics development
 * 
 * @author Anders Bo Pedersen, anders@wicore.dk
 */
public class Utilities3d 
{
	/**
	 * Divides argument image into a matrix of smaller images based on the allowed maxTexDim
	 * size. The resulting Image[row][column] contains the smaller images.  
	 * 
	 * @param sourceImg
	 * @param maxTexDim
	 * @param texMatrix
	 * @return
	 */
	public static Image[][] imageToTextureMatrix(Image sourceImg, int maxTexDim, Image[][] texMatrix)
	{
		if(null != sourceImg)
		{
			if(maxTexDim < 0)
				maxTexDim = 1 << 8;
			
			int imgW = sourceImg.getWidth();
			int imgH = sourceImg.getHeight();
			
    		// define max img dim
    		int maxImgDim = Math.max(imgW, imgH);
    		
    		// find minimum text dim that fits image
    		int i = 0;
    		while(maxImgDim > (1 << ++i));
    		
    		int minTexDimRespect = 1 << i;
    		
    		// is sub divisioning nessesary?
    		if(minTexDimRespect <= maxTexDim)
    		{
    			//No subdivision
    			
    			//init texmatrix?
    			if(null == texMatrix)
    				texMatrix = new Image[1][1];
    			
    			Image base = texMatrix[0][0] = Image.createImage(maxTexDim, maxTexDim); 
    			
    			Graphics g = base.getGraphics();
    			
    			//fill black
    			g.setColor(0x000000);
    			g.fillRect(0, 0, base.getWidth(), base.getHeight());
    			
    			// draw image on texture base
    			g.drawImage(sourceImg, (base.getWidth() - imgW) >> 1, (base.getHeight() - imgH) >> 1, 0);
    		}
    		else
    		{
    			//Sub division needed
    			
    			//define number of needed subdivisions
    			int numSubTexPerRow = 0, numSubTexPerColumn = 0;
    			
    			while(maxTexDim * numSubTexPerRow < imgW)
    				++numSubTexPerRow;
    			
    			while(maxTexDim * numSubTexPerColumn < imgH)
    				++numSubTexPerColumn;
    			
    			//create texSet
    			int totalNumSubTex = numSubTexPerRow*numSubTexPerColumn;
    			int totalRowTexSetDim = numSubTexPerRow * maxTexDim;
    			int totalColumnTexSetDim = numSubTexPerColumn * maxTexDim;
    			
    			//init texmatrix?
    			if(null == texMatrix)
    				texMatrix = new Image[numSubTexPerColumn][numSubTexPerRow];
    			
				//calc generel source image offset
				int xOffsetBase = (totalRowTexSetDim - imgW) >> 1;
				int yOffsetBase = (totalColumnTexSetDim - imgH) >> 1;
    			
    			i = 0;
    			Image img;
    			while(i < totalNumSubTex)
    			{
    				//draw source image to subtex
    				int column = i % numSubTexPerRow;
    				int row = i / numSubTexPerRow;
    				
    				if(null == texMatrix[row] || null == texMatrix[row][column])
    					//create sub texture
    					texMatrix[row][column] = img = Image.createImage(maxTexDim, maxTexDim);
    				else
    					img = texMatrix[row][column];
    				
    				//get graphics handle and set default background to black
    				Graphics g = img.getGraphics();
    				g.setColor(0xFF000000);
    				g.fillRect(0, 0, img.getWidth(), img.getHeight());
    				
    				//calc generel source image offset
    				int xOffset = xOffsetBase;
    				int yOffset = yOffsetBase;
    				
    				//adjust offsets to current sub tex 
    				xOffset -= column * maxTexDim;
    				yOffset -= row * maxTexDim;
    				
    				g.drawImage(sourceImg, xOffset, yOffset, 0);
    				
        			++i;
    			}
    		}
    		
    		return texMatrix;
		}
		else
			return null;
	}
	
	/**
	 * Returns the number of horizontal subdivisions the argument image needs to
	 * be divided into to respect argument maxTexDim size
	 * 
	 * @param sourceImg
	 * @param maxTexDim
	 * @return
	 */
	public static int getNumTextureMatrixPerRow(Image sourceImg, int maxTexDim)
	{
		if(null != sourceImg)
		{
			//define number of needed subdivisions
			int numSubTexPerRow = 0, imgW =sourceImg.getWidth();
			
			while(maxTexDim * numSubTexPerRow < imgW)
				++numSubTexPerRow;
			
			return numSubTexPerRow;
		}
		
		return -1;
	}
	
	/**
	 * Returns the number of vertical subdivisions the argument image needs to
	 * be divided into to respect argument maxTexDim size
	 * 
	 * @param sourceImg
	 * @param maxTexDim
	 * @return
	 */
	public static int getNumTextureMatrixPerColumn(Image sourceImg, int maxTexDim)
	{
		if(null != sourceImg)
		{
			//define number of needed subdivisions
			int numSubTexPerColumn = 0, imgH = sourceImg.getHeight();
			
			while(maxTexDim * numSubTexPerColumn < imgH)
				++numSubTexPerColumn;
			
			return numSubTexPerColumn;
		}
		
		return -1;
	}
}
