//#condition polish.usePolishGui
/*
 * Created on Jul 8, 2008 at 5:10:39 PM.
 * 
 * Copyright (c) 2009 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui.rgbfilters;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.RgbImage;

/**
 * <p>Transforms the color of a specified RGB image.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GrayscaleRgbFilter extends RgbFilter
{
	protected Dimension grayscale;
	protected transient RgbImage output;
	
	/**
	 * Creates a new grayscale filter
	 */
	public GrayscaleRgbFilter()
	{
		// just create a new instance
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#isActive()
	 */
	public boolean isActive()
	{
		boolean isActive = false;
		if (this.grayscale != null) {
			isActive = (this.grayscale.getValue(255) != 0);
		}
		return isActive;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
	 */
	public RgbImage process(RgbImage input)
	{
		if (!isActive()) {
			return input;
		}
		if (this.output == null || this.output.getWidth() != input.getWidth() || this.output.getHeight() != input.getHeight() ) {
			this.output = new RgbImage( input.getWidth(), input.getHeight() );
		}
		int[] rgbInput = input.getRgbData();
		int[] rgbOutput = this.output.getRgbData();
		int grayscalePercent = (this.grayscale.getValue(255) * 100) / 255; 
		int inverseGrayscalePercent = 100 - grayscalePercent;
		int alpha, red, green, blue;
		for (int i = 0; i < rgbOutput.length; i++)
		{
			int pixel = rgbInput[i];
				//TODO keep pixels within keep-range
				alpha = (0xFF000000 & pixel) >>> 24;
				if (alpha == 0) {
					rgbOutput[i] = 0;
					continue;
				}
				red = (0x00FF & (pixel >>> 16));	
				green = (0x0000FF & (pixel >>> 8));
				blue = pixel & (0x000000FF );
				
				int brightness = ((((red + green + blue) / 3 ) & 0x000000FF) * grayscalePercent) / 100;
				red = (red * inverseGrayscalePercent) / 100;
				green = (green * inverseGrayscalePercent) / 100;
				blue = (blue * inverseGrayscalePercent) / 100;
				pixel = (brightness << 0)  | blue
					|   (brightness << 8)  | (green << 8)
					|   (brightness << 16) | (red   << 16)
					|                        (alpha << 24);
				rgbOutput[i] = pixel;
		}
		return this.output;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);
		//#if polish.css.filter-grayscale-grade
			Dimension grayscaleInt = (Dimension) style.getObjectProperty("filter-grayscale-grade");
			if (grayscaleInt != null) {
				this.grayscale = grayscaleInt;
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#releaseResources()
	 */
	public void releaseResources()
	{
		this.output = null;
	}

}