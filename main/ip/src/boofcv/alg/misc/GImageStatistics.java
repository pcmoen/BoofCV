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

package boofcv.alg.misc;

import boofcv.struct.image.*;

/**
 * Generalized version of {@link ImageStatistics}.  Type checking is performed at runtime instead of at compile type.
 *
 * @author Peter Abeles
 */
public class GImageStatistics {
	/**
	 * Returns the absolute value of the element with the largest absolute value, across all bands
	 *
	 * @param input Input image. Not modified.
	 * @return Largest pixel absolute value.
	 */
	public static double maxAbs( ImageBase input ) {
		if( input instanceof ImageSingleBand ) {
			if (ImageUInt8.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageUInt8) input);
			} else if (ImageSInt8.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageSInt8) input);
			} else if (ImageUInt16.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageUInt16) input);
			} else if (ImageSInt16.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageSInt16) input);
			} else if (ImageSInt32.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageSInt32) input);
			} else if (ImageSInt64.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageSInt64) input);
			} else if (ImageFloat32.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageFloat32) input);
			} else if (ImageFloat64.class == input.getClass()) {
				return ImageStatistics.maxAbs((ImageFloat64) input);
			} else {
				throw new IllegalArgumentException("Unknown Image Type: " + input.getClass().getSimpleName());
			}
		} else if( input instanceof ImageInterleaved ) {
			if (InterleavedU8.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedU8) input);
			} else if (InterleavedS8.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedS8) input);
			} else if (InterleavedU16.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedU16) input);
			} else if (InterleavedS16.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedS16) input);
			} else if (InterleavedS32.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedS32) input);
			} else if (InterleavedS64.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedS64) input);
			} else if (InterleavedF32.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedF32) input);
			} else if (InterleavedF64.class == input.getClass()) {
				return ImageStatistics.maxAbs((InterleavedF64) input);
			} else {
				throw new IllegalArgumentException("Unknown Image Type: " + input.getClass().getSimpleName());
			}
		} else {
			throw new IllegalArgumentException("MultiSpectral image support needs to be added");
		}
	}

	/**
	 * Returns the maximum pixel value across all bands.
	 *
	 * @param input Input image. Not modified.
	 * @return Maximum pixel value.
	 */
	public static double max( ImageBase input ) {
		if( input instanceof ImageSingleBand ) {
			if (ImageUInt8.class == input.getClass()) {
				return ImageStatistics.max((ImageUInt8) input);
			} else if (ImageSInt8.class == input.getClass()) {
				return ImageStatistics.max((ImageSInt8) input);
			} else if (ImageUInt16.class == input.getClass()) {
				return ImageStatistics.max((ImageUInt16) input);
			} else if (ImageSInt16.class == input.getClass()) {
				return ImageStatistics.max((ImageSInt16) input);
			} else if (ImageSInt32.class == input.getClass()) {
				return ImageStatistics.max((ImageSInt32) input);
			} else if (ImageSInt64.class == input.getClass()) {
				return ImageStatistics.max((ImageSInt64) input);
			} else if (ImageFloat32.class == input.getClass()) {
				return ImageStatistics.max((ImageFloat32) input);
			} else if (ImageFloat64.class == input.getClass()) {
				return ImageStatistics.max((ImageFloat64) input);
			} else {
				throw new IllegalArgumentException("Unknown Image Type");
			}
		} else if( input instanceof ImageInterleaved ) {
			if (InterleavedU8.class == input.getClass()) {
				return ImageStatistics.max((InterleavedU8) input);
			} else if (InterleavedS8.class == input.getClass()) {
				return ImageStatistics.max((InterleavedS8) input);
			} else if (InterleavedU16.class == input.getClass()) {
				return ImageStatistics.max((InterleavedU16) input);
			} else if (InterleavedS16.class == input.getClass()) {
				return ImageStatistics.max((InterleavedS16) input);
			} else if (InterleavedS32.class == input.getClass()) {
				return ImageStatistics.max((InterleavedS32) input);
			} else if (InterleavedS64.class == input.getClass()) {
				return ImageStatistics.max((InterleavedS64) input);
			} else if (InterleavedF32.class == input.getClass()) {
				return ImageStatistics.max((InterleavedF32) input);
			} else if (InterleavedF64.class == input.getClass()) {
				return ImageStatistics.max((InterleavedF64) input);
			} else {
				throw new IllegalArgumentException("Unknown Image Type");
			}
		} else {
			throw new IllegalArgumentException("MultiSpectral image support needs to be added");
		}
	}

	/**
	 * Returns the minimum pixel value across all bands
	 *
	 * @param input Input image. Not modified.
	 * @return Minimum pixel value.
	 */
	public static double min( ImageBase input ) {
		if( input instanceof ImageSingleBand ) {
			if (ImageUInt8.class == input.getClass()) {
				return ImageStatistics.min((ImageUInt8) input);
			} else if (ImageSInt8.class == input.getClass()) {
				return ImageStatistics.min((ImageSInt8) input);
			} else if (ImageUInt16.class == input.getClass()) {
				return ImageStatistics.min((ImageUInt16) input);
			} else if (ImageSInt16.class == input.getClass()) {
				return ImageStatistics.min((ImageSInt16) input);
			} else if (ImageSInt32.class == input.getClass()) {
				return ImageStatistics.min((ImageSInt32) input);
			} else if (ImageSInt64.class == input.getClass()) {
				return ImageStatistics.min((ImageSInt64) input);
			} else if (ImageFloat32.class == input.getClass()) {
				return ImageStatistics.min((ImageFloat32) input);
			} else if (ImageFloat64.class == input.getClass()) {
				return ImageStatistics.min((ImageFloat64) input);
			} else {
				throw new IllegalArgumentException("Unknown Image Type: " + input.getClass().getSimpleName());
			}
		} else if( input instanceof ImageInterleaved ) {
			if (InterleavedU8.class == input.getClass()) {
				return ImageStatistics.min((InterleavedU8) input);
			} else if (InterleavedS8.class == input.getClass()) {
				return ImageStatistics.min((InterleavedS8) input);
			} else if (InterleavedU16.class == input.getClass()) {
				return ImageStatistics.min((InterleavedU16) input);
			} else if (InterleavedS16.class == input.getClass()) {
				return ImageStatistics.min((InterleavedS16) input);
			} else if (InterleavedS32.class == input.getClass()) {
				return ImageStatistics.min((InterleavedS32) input);
			} else if (InterleavedS64.class == input.getClass()) {
				return ImageStatistics.min((InterleavedS64) input);
			} else if (InterleavedF32.class == input.getClass()) {
				return ImageStatistics.min((InterleavedF32) input);
			} else if (InterleavedF64.class == input.getClass()) {
				return ImageStatistics.min((InterleavedF64) input);
			} else {
				throw new IllegalArgumentException("Unknown Image Type: " + input.getClass().getSimpleName());
			}
		} else {
			throw new IllegalArgumentException("MultiSpectral image support needs to be added");
		}
	}

	/**
	 * <p>
	 * Returns the sum of all the pixels in the image across all bands.
	 * </p>
	 *
	 * @param input Input image. Not modified.
	 * @return Sum of pixel intensity values
	 */
	public static double sum( ImageBase input ) {

		if( input instanceof ImageSingleBand ) {
			if (ImageUInt8.class == input.getClass()) {
				return ImageStatistics.sum((ImageUInt8) input);
			} else if (ImageSInt8.class == input.getClass()) {
				return ImageStatistics.sum((ImageSInt8) input);
			} else if (ImageUInt16.class == input.getClass()) {
				return ImageStatistics.sum((ImageUInt16) input);
			} else if (ImageSInt16.class == input.getClass()) {
				return ImageStatistics.sum((ImageSInt16) input);
			} else if (ImageSInt32.class == input.getClass()) {
				return ImageStatistics.sum((ImageSInt32) input);
			} else if (ImageSInt64.class == input.getClass()) {
				return ImageStatistics.sum((ImageSInt64) input);
			} else if (ImageFloat32.class == input.getClass()) {
				return ImageStatistics.sum((ImageFloat32) input);
			} else if (ImageFloat64.class == input.getClass()) {
				return ImageStatistics.sum((ImageFloat64) input);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else if( input instanceof ImageInterleaved ) {
			if (InterleavedU8.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedU8) input);
			} else if (InterleavedS8.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedS8) input);
			} else if (InterleavedU16.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedU16) input);
			} else if (InterleavedS16.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedS16) input);
			} else if (InterleavedS32.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedS32) input);
			} else if (InterleavedS64.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedS64) input);
			} else if (InterleavedF32.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedF32) input);
			} else if (InterleavedF64.class == input.getClass()) {
				return ImageStatistics.sum((InterleavedF64) input);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else {
			throw new IllegalArgumentException("MultiSpectral image support needs to be added");
		}
	}

	/**
	 * Returns the mean pixel intensity value.
	 *
	 * @param input Input image. Not modified.
	 * @return Mean pixel value
	 */
	public static double mean( ImageBase input ) {

		if( input instanceof ImageSingleBand ) {
			if (ImageUInt8.class == input.getClass()) {
				return ImageStatistics.mean((ImageUInt8) input);
			} else if (ImageSInt8.class == input.getClass()) {
				return ImageStatistics.mean((ImageSInt8) input);
			} else if (ImageUInt16.class == input.getClass()) {
				return ImageStatistics.mean((ImageUInt16) input);
			} else if (ImageSInt16.class == input.getClass()) {
				return ImageStatistics.mean((ImageSInt16) input);
			} else if (ImageSInt32.class == input.getClass()) {
				return ImageStatistics.mean((ImageSInt32) input);
			} else if (ImageSInt64.class == input.getClass()) {
				return ImageStatistics.mean((ImageSInt64) input);
			} else if (ImageFloat32.class == input.getClass()) {
				return ImageStatistics.mean((ImageFloat32) input);
			} else if (ImageFloat64.class == input.getClass()) {
				return ImageStatistics.mean((ImageFloat64) input);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else if( input instanceof ImageInterleaved ) {
			if (InterleavedU8.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedU8) input);
			} else if (InterleavedS8.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedS8) input);
			} else if (InterleavedU16.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedU16) input);
			} else if (InterleavedS16.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedS16) input);
			} else if (InterleavedS32.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedS32) input);
			} else if (InterleavedS64.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedS64) input);
			} else if (InterleavedF32.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedF32) input);
			} else if (InterleavedF64.class == input.getClass()) {
				return ImageStatistics.mean((InterleavedF64) input);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else {
			throw new IllegalArgumentException("MultiSpectral image support needs to be added");
		}
	}

	/**
	 * Computes the variance of pixel intensity values inside the image.
	 *
	 * @param input Input image. Not modified.
	 * @param mean Mean pixel intensity value.
	 * @return Pixel variance
	 */
	public static <T extends ImageSingleBand> double variance( T input , double mean ) {

		if( ImageUInt8.class == input.getClass() ) {
			return ImageStatistics.variance((ImageUInt8)input,mean);
		} else if( ImageSInt8.class == input.getClass() ) {
			return ImageStatistics.variance((ImageSInt8)input,mean);
		} else if( ImageUInt16.class == input.getClass() ) {
			return ImageStatistics.variance((ImageUInt16)input,mean);
		} else if( ImageSInt16.class == input.getClass() ) {
			return ImageStatistics.variance((ImageSInt16)input,mean);
		} else if( ImageSInt32.class == input.getClass() ) {
			return ImageStatistics.variance((ImageSInt32)input,mean);
		} else if( ImageSInt64.class == input.getClass() ) {
			return ImageStatistics.variance((ImageSInt64)input,mean);
		} else if( ImageFloat32.class == input.getClass() ) {
			return ImageStatistics.variance((ImageFloat32)input,mean);
		} else if( ImageFloat64.class == input.getClass() ) {
			return ImageStatistics.variance((ImageFloat64)input,mean);
		} else {
			throw new IllegalArgumentException("Unknown image Type");
		}
	}

	/**
	 * Computes the mean of the difference squared between the two images.
	 *
	 * @param inputA Input image. Not modified.
	 * @param inputB Input image. Not modified.
	 * @return Mean difference squared
	 */
	public static <T extends ImageBase> double meanDiffSq( T inputA , T inputB ) {

		if( inputA instanceof ImageSingleBand ) {
			if (ImageUInt8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageUInt8) inputA, (ImageUInt8) inputB);
			} else if (ImageSInt8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageSInt8) inputA, (ImageSInt8) inputB);
			} else if (ImageUInt16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageUInt16) inputA, (ImageUInt16) inputB);
			} else if (ImageSInt16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageSInt16) inputA, (ImageSInt16) inputB);
			} else if (ImageSInt32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageSInt32) inputA, (ImageSInt32) inputB);
			} else if (ImageSInt64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageSInt64) inputA, (ImageSInt64) inputB);
			} else if (ImageFloat32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageFloat32) inputA, (ImageFloat32) inputB);
			} else if (ImageFloat64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((ImageFloat64) inputA, (ImageFloat64) inputB);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else if( inputA instanceof ImageInterleaved ) {
			if (InterleavedU8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedU8) inputA, (InterleavedU8) inputB);
			} else if (InterleavedS8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedS8) inputA, (InterleavedS8) inputB);
			} else if (InterleavedU16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedU16) inputA, (InterleavedU16) inputB);
			} else if (InterleavedS16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedS16) inputA, (InterleavedS16) inputB);
			} else if (InterleavedS32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedS32) inputA, (InterleavedS32) inputB);
			} else if (InterleavedS64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedS64) inputA, (InterleavedS64) inputB);
			} else if (InterleavedF32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedF32) inputA, (InterleavedF32) inputB);
			} else if (InterleavedF64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffSq((InterleavedF64) inputA, (InterleavedF64) inputB);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else {
			throw new IllegalArgumentException("MultiSpectral images needs to be added");
		}
	}

	/**
	 * Computes the mean of the absolute value of the difference between the two images across all bands
	 *
	 * @param inputA Input image. Not modified.
	 * @param inputB Input image. Not modified.
	 * @return Mean absolute difference
	 */
	public static <T extends ImageBase> double meanDiffAbs( T inputA , T inputB ) {

		if( inputA instanceof ImageSingleBand ) {
			if (ImageUInt8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageUInt8) inputA, (ImageUInt8) inputB);
			} else if (ImageSInt8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageSInt8) inputA, (ImageSInt8) inputB);
			} else if (ImageUInt16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageUInt16) inputA, (ImageUInt16) inputB);
			} else if (ImageSInt16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageSInt16) inputA, (ImageSInt16) inputB);
			} else if (ImageSInt32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageSInt32) inputA, (ImageSInt32) inputB);
			} else if (ImageSInt64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageSInt64) inputA, (ImageSInt64) inputB);
			} else if (ImageFloat32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageFloat32) inputA, (ImageFloat32) inputB);
			} else if (ImageFloat64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((ImageFloat64) inputA, (ImageFloat64) inputB);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else if( inputA instanceof ImageInterleaved ) {
			if (InterleavedU8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedU8) inputA, (InterleavedU8) inputB);
			} else if (InterleavedS8.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedS8) inputA, (InterleavedS8) inputB);
			} else if (InterleavedU16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedU16) inputA, (InterleavedU16) inputB);
			} else if (InterleavedS16.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedS16) inputA, (InterleavedS16) inputB);
			} else if (InterleavedS32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedS32) inputA, (InterleavedS32) inputB);
			} else if (InterleavedS64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedS64) inputA, (InterleavedS64) inputB);
			} else if (InterleavedF32.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedF32) inputA, (InterleavedF32) inputB);
			} else if (InterleavedF64.class == inputA.getClass()) {
				return ImageStatistics.meanDiffAbs((InterleavedF64) inputA, (InterleavedF64) inputB);
			} else {
				throw new IllegalArgumentException("Unknown image Type");
			}
		} else {
			throw new IllegalArgumentException("MultiSpectral images needs to be added");
		}
	}

	/**
	 * Computes the histogram of intensity values for the image.  For floating point images it is rounded
	 * to the nearest integer using "(int)value".
	 *
	 * @param input (input) Image.
	 * @param minValue (input) Minimum possible intensity value   Ignored for unsigned images.
	 * @param histogram (output) Storage for histogram. Number of elements must be equal to max value.
	 */
	public static void histogram( ImageSingleBand input , int minValue , int histogram[] ) {
		if( ImageUInt8.class == input.getClass() ) {
			ImageStatistics.histogram((ImageUInt8)input,histogram);
		} else if( ImageSInt8.class == input.getClass() ) {
			ImageStatistics.histogram((ImageSInt8)input,minValue,histogram);
		} else if( ImageUInt16.class == input.getClass() ) {
			ImageStatistics.histogram((ImageUInt16)input,histogram);
		} else if( ImageSInt16.class == input.getClass() ) {
			ImageStatistics.histogram((ImageSInt16)input,minValue,histogram);
		} else if( ImageSInt32.class == input.getClass() ) {
			ImageStatistics.histogram((ImageSInt32)input,minValue,histogram);
		} else if( ImageSInt64.class == input.getClass() ) {
			ImageStatistics.histogram((ImageSInt64)input,minValue,histogram);
		} else if( ImageFloat32.class == input.getClass() ) {
			ImageStatistics.histogram((ImageFloat32)input,minValue,histogram);
		} else if( ImageFloat64.class == input.getClass() ) {
			ImageStatistics.histogram((ImageFloat64)input,minValue,histogram);
		} else {
			throw new IllegalArgumentException("Unknown image Type");
		}
	}
}
