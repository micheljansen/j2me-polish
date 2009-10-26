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
 * <p>Blurs an image.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Nagendra Sharma, nagendra@prompttechnologies.net
 * @author Robert Virkus, j2mepolish@enough.de (blur animation & fixes)
 */
public class GaussianBlurRgbFilter extends RgbFilter {
	protected Dimension blur;
	protected transient RgbImage output;
	int width, height;

	/**
	 * Creates a new GaussianBlurRgb Filter
	 */
	public GaussianBlurRgbFilter() {
		// just create a new instance
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#isActive()
	 */
	public boolean isActive() {
		if (this.blur == null) {
			return false;
		}
		return (this.blur.getValue(255) != 0);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
	 */
	public RgbImage process(RgbImage input) {

		if (!isActive()) {
			return input;
		}
		this.width = input.getWidth();
		this.height = input.getHeight();
		if (this.output == null || this.output.getWidth() != input.getWidth()
				|| this.output.getHeight() != input.getHeight()) {
			this.output = new RgbImage(input.getWidth(), input.getHeight());
		}
		int[] rgbInput = input.getRgbData();
		int[] rgbOutput = this.output.getRgbData();

		int dimension = this.blur.getValue(100) * 3;
		int outerPercentage = dimension % 100;
		dimension /= 100;
		if (outerPercentage == 0) {
			outerPercentage = 100;
		} else {
			dimension++;
		}
		
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				// On the border, return black, since there is no pixel to be modified.
				int sourcePixel = rgbInput[x + y * this.width];
//				if (x < dimension || y < dimension
//						|| x >= (this.width - dimension)
//						|| y >= (this.height - dimension)) {
//					rgbOutput[x + y * this.width] = sourcePixel;
//					continue;
//				}

				int red   = (sourcePixel & 0x00ff0000) >> 16 ;
				int green = (sourcePixel & 0x0000ff00) >> 8;
				int blue  =  sourcePixel & 0x000000ff; 
				int totalPercentage = 100;
				int startY = y - dimension;
				int endY = y + dimension;
				for (int dy = startY; dy <= endY; dy++) {
					int startX = x - dimension;
					int endX = x + dimension;
					for (int dx = startX; dx <= endX; dx++) {
						//System.out.println("dx: " + dx + " dy: " + 
						//                       dy + " w: " + w);
						if (dx < 0 || dy < 0 || dx >= this.width || dy >= this.height ) {
							continue;
						}
						if (dx != x || dy != y) {
							int c = rgbInput[dx + dy * this.width];
							int percentage = ((c >>> 24) * 100) / 255;
							if (dx == startX || dx == endX || dy == startY || dy == endY ) {
								percentage = (percentage * outerPercentage) / 100;
							}
							red +=   (((c & 0x00ff0000) >> 16) * percentage) / 100;
							green += (((c & 0x0000ff00) >> 8)  * percentage) / 100;
							blue +=   ((c & 0x000000ff)        * percentage) / 100;
							totalPercentage += percentage;
						}
					}
				}
				
				red =   ((red   * 100) / totalPercentage) << 16;
				green = ((green * 100) / totalPercentage) << 8;
				blue =  ((blue  * 100) / totalPercentage);
			

				rgbOutput[x + y * this.width] = (sourcePixel & 0xff000000) | red | green | blue ;
			}
		}

		return this.output;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle) {
		super.setStyle(style, resetStyle);
		//#if polish.css.filter-blur-grade
			Dimension blurInt = (Dimension) style.getObjectProperty("filter-blur-grade");
			if (blurInt != null) {
				this.blur = blurInt;
			}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#releaseResources()
	 */
	public void releaseResources() {
		this.output = null;
	}

}
