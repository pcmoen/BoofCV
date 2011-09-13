/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
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

package boofcv.alg.feature.detect.interest;

import boofcv.abst.filter.derivative.AnyImageDerivative;
import boofcv.alg.transform.gss.ScaleSpacePyramid;
import boofcv.alg.transform.gss.UtilScaleSpace;
import boofcv.core.image.inst.FactoryImageGenerator;
import boofcv.struct.image.ImageFloat32;
import georegression.struct.point.Point2D_I32;

import java.util.List;


/**
 * @author Peter Abeles
 */
public class TestFeaturePyramid extends GenericFeatureScaleDetector {

	@Override
	protected Object createDetector(GeneralFeatureDetector<ImageFloat32, ImageFloat32> detector) {
		AnyImageDerivative<ImageFloat32,ImageFloat32> deriv = UtilScaleSpace.createDerivatives(ImageFloat32.class, FactoryImageGenerator.create(ImageFloat32.class));

		return new FeaturePyramid<ImageFloat32,ImageFloat32>(detector,deriv,1);
	}

	@Override
	protected List<Point2D_I32> detectFeature(ImageFloat32 input, double[] scales, Object detector) {
		ScaleSpacePyramid<ImageFloat32> ss = new ScaleSpacePyramid<ImageFloat32>(ImageFloat32.class,scales);
		ss.update(input);

		FeaturePyramid<ImageFloat32,ImageFloat32> alg =
				(FeaturePyramid<ImageFloat32,ImageFloat32>)detector;
		alg.detect(ss);

		return (List)alg.getInterestPoints();
	}

}
