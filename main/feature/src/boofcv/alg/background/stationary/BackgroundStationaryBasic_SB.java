/*
 * Copyright (c) 2011-2015, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.background.stationary;

import boofcv.alg.InputSanityCheck;
import boofcv.alg.misc.ImageMiscOps;
import boofcv.core.image.FactoryGImageSingleBand;
import boofcv.core.image.GConvertImage;
import boofcv.core.image.GImageSingleBand;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.ImageUInt8;

/**
 * Implementation of {@link BackgroundStationaryBasic} for {@link boofcv.struct.image.MultiSpectral}.
 *
 * @author Peter Abeles
 */
public class BackgroundStationaryBasic_SB<T extends ImageSingleBand>
	extends BackgroundStationaryBasic<T>
{
	// storage for background image
	protected ImageFloat32 background = new ImageFloat32(1,1);

	// wrapper which provides abstraction across image types
	protected GImageSingleBand inputWrapper;

	public BackgroundStationaryBasic_SB(float learnRate, float threshold,
										Class<T> imageType) {
		super(learnRate, threshold, ImageType.single(imageType));

		inputWrapper = FactoryGImageSingleBand.create(imageType);
	}

	/**
	 * Returns the background image.
	 *
	 * @return background image.
	 */
	public ImageFloat32 getBackground() {
		return background;
	}

	@Override
	public void reset() {
		background.reshape(1,1);
	}

	@Override
	public void updateBackground( T frame) {
		if( background.width == 1 ) {
			background.reshape(frame.width, frame.height);
			GConvertImage.convert(frame, background);
			return;
		} else {
			InputSanityCheck.checkSameShape(background,frame);
		}

		inputWrapper.wrap(frame);

		float minusLearn = 1.0f - learnRate;

		int indexBG = 0;
		for (int y = 0; y < frame.height; y++) {
			int indexInput = frame.startIndex + y*frame.stride;
			int end = indexInput + frame.width;
			while( indexInput < end ) {
				float value = inputWrapper.getF(indexInput++);
				float bg = background.data[indexBG];

				background.data[indexBG++] = minusLearn*bg + learnRate*value;
			}
		}
	}

	@Override
	public void segment(T frame, ImageUInt8 segmented) {
		if( background.width == 1 ) {
			ImageMiscOps.fill(segmented,unknownValue);
			return;
		}
		InputSanityCheck.checkSameShape(background,frame,segmented);
		inputWrapper.wrap(frame);

		float thresholdSq = threshold*threshold;

		int indexBG = 0;
		for (int y = 0; y < frame.height; y++) {
			int indexInput = frame.startIndex + y*frame.stride;
			int indexSegmented = segmented.startIndex + y*segmented.stride;

			int end = indexInput + frame.width;
			while( indexInput < end ) {
				float bg = background.data[indexBG];
				float pixelFrame = inputWrapper.getF(indexInput);

				float diff = bg - pixelFrame;
				if (diff * diff <= thresholdSq) {
					segmented.data[indexSegmented] = 0;
				} else {
					segmented.data[indexSegmented] = 1;
				}

				indexInput++;
				indexSegmented++;
				indexBG++;
			}
		}
	}


}
