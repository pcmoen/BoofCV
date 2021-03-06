/*
 * Copyright (c) 2011-2016, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.feature.dense.impl;

import boofcv.alg.feature.dense.DescribeDenseHogAlg;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

/**
 * @author Peter Abeles
 */
public class TestDescribeDenseHogAlg_MSU8 extends GenericDescribeDenseHogAlgChecks<MultiSpectral<ImageUInt8>> {
	public TestDescribeDenseHogAlg_MSU8() {
		super(ImageType.ms(3, ImageUInt8.class));
	}

	@Override
	public DescribeDenseHogAlg<MultiSpectral<ImageUInt8>,?>
	createAlg(int orientationBins, int widthCell, int widthBlock , int stepBlock) {
		return new DescribeDenseHogAlg_MSU8(orientationBins,widthCell,widthBlock,stepBlock,3);
	}
}