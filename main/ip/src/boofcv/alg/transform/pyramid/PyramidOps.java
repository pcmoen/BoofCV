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

package boofcv.alg.transform.pyramid;

import boofcv.abst.filter.FilterImageInterface;
import boofcv.abst.filter.derivative.ImageGradient;
import boofcv.abst.filter.derivative.ImageHessian;
import boofcv.alg.interpolate.InterpolatePixelS;
import boofcv.alg.transform.pyramid.impl.ImplPyramidOps;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.pyramid.ImagePyramid;

import java.lang.reflect.Array;


/**
 * Various operations related to image pyramids.
 *
 * @author Peter Abeles
 */
public class PyramidOps {

	/**
	 * Creates an array of single band images for each layer in the provided pyramid.  Each image will
	 * be the same size as the corresponding layer in the pyramid.
	 *
	 * @param pyramid (Input) Image pyramid
	 * @param outputType (Input) Output image type
	 * @param <O> Output image type
	 * @return An array of images
	 */
	public static <O extends ImageSingleBand>
	O[] declareOutput( ImagePyramid<?> pyramid , Class<O> outputType ) {
		O[] ret = (O[])Array.newInstance(outputType,pyramid.getNumLayers());

		for( int i = 0; i < ret.length; i++ ) {
			int w = pyramid.getWidth(i);
			int h = pyramid.getHeight(i);
			ret[i] = GeneralizedImageOps.createSingleBand(outputType,w,h);
		}

		return ret;
	}

	/**
	 * Reshapes each image in the array to match the layers in the pyramid
	 * @param pyramid (Input) Image pyramid
	 * @param output (Output) List of images which is to be resized
	 * @param <O> Image type
	 */
	public static <O extends ImageSingleBand>
	void reshapeOutput( ImagePyramid<?> pyramid , O[] output ) {

		for( int i = 0; i < output.length; i++ ) {
			int w = pyramid.getWidth(i);
			int h = pyramid.getHeight(i);
			output[i].reshape(w, h);
		}
	}

	/**
	 * <p>
	 * Runs an image filter through each layer in the pyramid.
	 * </p>
	 *
	 * <p>
	 * It is assumed that the output has the same scales as the input.  If not
	 * initialized then it will be initialized.  If already initialized it is
	 * assumed to be setup for the same input image size.
	 * </p>
	 *
	 * @param input Input pyramid.
	 * @param filter Filter being applied to the pyramid.
	 * @param output Output pyramid where filter results are saved.
	 */
	public static <I extends ImageSingleBand, O extends ImageSingleBand>
	void filter(ImagePyramid<I> input, FilterImageInterface<I, O> filter, O[] output )
	{
		for( int i = 0; i < input.getNumLayers(); i++ ) {
			I imageIn = input.getLayer(i);
			filter.process(imageIn,output[i]);
		}
	}

	/**
	 * <p>
	 * Computes the gradient for each image the pyramid.
	 * </p>
	 *
	 * <p>
	 * It is assumed that the gradient has the same scales as the input.  If not
	 * initialized then it will be initialized.  If already initialized it is
	 * assumed to be setup for the same input image size.
	 * </p>
	 * 
	 * @param input Input pyramid.
	 * @param gradient Computes image gradient
	 * @param derivX Pyramid where x-derivative is stored.
	 * @param derivY Pyramid where y-derivative is stored.
	 */
	public static <I extends ImageSingleBand, O extends ImageSingleBand>
	void gradient(ImagePyramid<I> input, ImageGradient<I, O> gradient, O[] derivX, O[] derivY )
	{
		for( int i = 0; i < input.getNumLayers(); i++ ) {
			I imageIn = input.getLayer(i);
			gradient.process(imageIn,derivX[i],derivY[i]);
		}
	}

	/**
	 * <p>
	 * Computes the hessian (2nd order derivative) for each image the pyramid.
	 * </p>
	 *
	 * @param derivX (Input) Pyramid where x-derivative is stored.
	 * @param derivY (Input) Pyramid where y-derivative is stored.
	 * @param hessian (Input) Computes hessian from gradient
	 * @param derivXX (Output) Second derivative XX
	 * @param derivYY (Output) Second derivative YY
	 * @param derivXY (Output) Second derivative XY
	 */
	public static <I extends ImageSingleBand, O extends ImageSingleBand>
	void hessian(O[] derivX, O[] derivY , ImageHessian<O> hessian , O[] derivXX, O[] derivYY , O[] derivXY )
	{
		for( int i = 0; i < derivX.length; i++ ) {
			hessian.process(derivX[i],derivY[i],derivXX[i],derivYY[i],derivXY[i]);
		}
	}

	/**
	 * Scales down the input by a factor of 2.  Every other pixel along both axises is skipped.
	 */
	public static <T extends ImageSingleBand>
	void scaleDown2(T input , T output ) {
		if( input instanceof ImageFloat32 ) {
			ImplPyramidOps.scaleDown2((ImageFloat32)input,(ImageFloat32)output);
		} else if( input instanceof ImageUInt8 ) {
			ImplPyramidOps.scaleDown2((ImageUInt8)input,(ImageUInt8)output);
		} else {
			throw new IllegalArgumentException("Image type not yet supported");
		}
	}

	/**
	 * Scales an image up using interpolation
	 *
	 * @param scale How much larger the output image will be.
	 */
	public static <T extends ImageSingleBand>
	void scaleImageUp(T input , T output , int scale, InterpolatePixelS<T> interp ) {
		if( scale <= 1 )
			throw new IllegalArgumentException("Scale must be >= 2");
		if( input instanceof ImageFloat32 ) {
			ImplPyramidOps.scaleImageUp((ImageFloat32)input,(ImageFloat32)output,scale,(InterpolatePixelS)interp);
		} else if( input instanceof ImageUInt8 ) {
			ImplPyramidOps.scaleImageUp((ImageUInt8)input,(ImageUInt8)output,scale,(InterpolatePixelS)interp);
		} else {
			throw new IllegalArgumentException("Image type not yet supported");
		}
	}
}
