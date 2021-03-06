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

package boofcv.alg.fiducial.square;

import boofcv.abst.filter.binary.InputToBinary;
import boofcv.abst.geo.RefineEpipolar;
import boofcv.alg.distort.*;
import boofcv.alg.geo.h.HomographyLinear4;
import boofcv.alg.interpolate.InterpolatePixelS;
import boofcv.alg.shapes.polygon.BinaryPolygonDetector;
import boofcv.core.image.border.BorderType;
import boofcv.core.image.border.FactoryImageBorder;
import boofcv.factory.distort.FactoryDistort;
import boofcv.factory.geo.EpipolarError;
import boofcv.factory.geo.FactoryMultiView;
import boofcv.factory.interpolate.FactoryInterpolation;
import boofcv.struct.calib.IntrinsicParameters;
import boofcv.struct.distort.*;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;
import georegression.geometry.GeometryMath_F64;
import georegression.geometry.UtilPolygons2D_F64;
import georegression.struct.homography.UtilHomography;
import georegression.struct.point.Point2D_F64;
import georegression.struct.se.Se3_F64;
import georegression.struct.shapes.Polygon2D_F64;
import georegression.struct.shapes.Quadrilateral_F64;
import org.ddogleg.struct.FastQueue;
import org.ejml.data.DenseMatrix64F;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Base class for square fiducial detectors.  Searches for quadrilaterals inside the image with a black border
 * and inner contours.  It then removes perspective and lens distortion from the candidate quadrilateral and
 * rendered onto a new image.  The just mentioned image is then passed on to the class which extends this one.
 * After being processed by the extending class, the corners are rotated to match and the 3D pose of the
 * target found.  Lens distortion is removed sparsely for performance reasons.
 * </p>
 *
 * <p>
 * Must call {@link #configure} before it can process an image.
 * </p>
 *
 * <p>
 * Target orientation. Corner 0 = (-r,r), 1 = (r,r) , 2 = (r,-r) , 3 = (-r,-r).
 * </p>
 *
 * @author Peter Abeles
 */
// TODO create unit test for bright object
	// TODO create a way to check to see if the black border is black
public abstract class BaseDetectFiducialSquare<T extends ImageSingleBand> {

	// Storage for the found fiducials
	private FastQueue<FoundFiducial> found = new FastQueue<FoundFiducial>(FoundFiducial.class,true);

	// converts input image into a binary image
	InputToBinary<T> inputToBinary;
	// Detects the squares
	private BinaryPolygonDetector<T> squareDetector;

	// image with lens and perspective distortion removed from it
	ImageFloat32 square;

	// storage for binary image
	ImageUInt8 binary = new ImageUInt8(1,1);

	// Used to compute/remove perspective distortion
	private HomographyLinear4 computeHomography = new HomographyLinear4(true);
	private RefineEpipolar refineHomography = FactoryMultiView.refineHomography(1e-4,100, EpipolarError.SAMPSON);
	private DenseMatrix64F H = new DenseMatrix64F(3,3);
	private DenseMatrix64F H_refined = new DenseMatrix64F(3,3);
	private List<AssociatedPair> pairsRemovePerspective = new ArrayList<AssociatedPair>();
	private ImageDistort<T,ImageFloat32> removePerspective;
	private PointTransformHomography_F32 transformHomography = new PointTransformHomography_F32();

	// How wide the border is relative to the fiducial's total width
	protected double borderWidthFraction;
	// the minimum fraction of border pixels which must be black for it to be considered a fiducial
	private double minimumBorderBlackFraction;

	// used to compute 3D pose of target
	QuadPoseEstimator poseEstimator = new QuadPoseEstimator(1e-6,200);

	// transform from undistorted to distorted image pixels
	PointTransform_F64 pointUndistToDist;

	// boolean indicating if camera intrinsic parameters
	boolean wasIntrinsicSet = false;

	// Storage for results of fiducial reading
	private Result result = new Result();

	// type of input image
	private Class<T> inputType;

	// verbose debugging output
	private boolean verbose = false;

	/**
	 * Configures the detector.
	 *
	 * @param inputToBinary Converts input image into a binary image
	 * @param squareDetector Detects the quadrilaterals in the image
	 * @param borderWidthFraction Fraction of the fiducial's width that the border occupies. 0.25 is recommended.
	 * @param minimumBorderBlackFraction Minimum fraction of pixels inside the border which must be black.  Try 0.65
	 * @param squarePixels  Number of pixels wide the undistorted square image of the fiducial's interior is.
	 *                      This will include the black border.
	 * @param inputType Type of input image it's processing
	 */
	protected BaseDetectFiducialSquare(InputToBinary<T> inputToBinary,
									   BinaryPolygonDetector<T> squareDetector,
									   double borderWidthFraction ,
									   double minimumBorderBlackFraction ,
									   int squarePixels,
									   Class<T> inputType) {

		if( squareDetector.getMinimumSides() != 4 || squareDetector.getMaximumSides() != 4)
			throw new IllegalArgumentException("quadDetector not configured to detect quadrilaterals");
		if( squareDetector.isOutputClockwise() )
			throw new IllegalArgumentException("output polygons needs to be counter-clockwise");

		if( borderWidthFraction <= 0 || borderWidthFraction >= 0.5 )
			throw new RuntimeException("Border width fraction must be 0 < x < 0.5");

		this.borderWidthFraction = borderWidthFraction;
		this.minimumBorderBlackFraction = minimumBorderBlackFraction;

		this.inputToBinary = inputToBinary;
		this.squareDetector = squareDetector;
		this.inputType = inputType;
		this.square = new ImageFloat32(squarePixels,squarePixels);

		for (int i = 0; i < 4; i++) {
			pairsRemovePerspective.add(new AssociatedPair());
		}

		// add corner points in target frame.  Used to compute homography.  Target's center is at its origin
		// see comment in class JavaDoc above.  Note that the target's length is one below.  The scale factor
		// will be provided later one
		poseEstimator.setFiducial(-0.5, 0.5, 0.5, 0.5, 0.5, -0.5, -0.5, -0.5);

		// this combines two separate sources of distortion together so that it can be removed in the final image which
		// is sent to fiducial decoder
		InterpolatePixelS<T> interp = FactoryInterpolation.nearestNeighborPixelS(inputType);
		interp.setBorder(FactoryImageBorder.single(inputType, BorderType.EXTENDED));
		removePerspective = FactoryDistort.distortSB(false, interp, ImageFloat32.class);

		// if no camera parameters is specified default to this
		pointUndistToDist = new DoNothingTransform_F64();
		removePerspective.setModel(new PointToPixelTransform_F32(transformHomography));
	}

	/**
	 * Specifies the image's intrinsic parameters and target size
	 *
	 * @param intrinsic Intrinsic parameters for the distortion free input image
	 * @param cache If there's lens distortion should it cache the transforms?  Speeds it up by about 12%.  Ignored
	 *              if no lens distortion
	 */
	public void configure( IntrinsicParameters intrinsic , boolean cache ) {
		wasIntrinsicSet = true;
		PointTransform_F32 pointSquareToInput;
		if( intrinsic.isDistorted() ) {

			IntrinsicParameters intrinsicUndist = new IntrinsicParameters();

			// full view so that none of the pixels are discarded and to ensure that all pixels in the undistorted
			// image are bounded by the the input image's shape

			PointTransform_F32 pointDistToUndist = LensDistortionOps.
					transform_F32(AdjustmentType.FULL_VIEW, intrinsic, intrinsicUndist, false);
			PointTransform_F32 pointUndistToDist = LensDistortionOps.
					transform_F32(AdjustmentType.FULL_VIEW, intrinsic, null, true);
			PixelTransform_F32 distToUndist = new PointToPixelTransform_F32(pointDistToUndist);
			PixelTransform_F32 undistToDist = new PointToPixelTransform_F32(pointUndistToDist);

			if( cache ) {
				distToUndist = new PixelTransformCached_F32(intrinsic.width, intrinsic.height, distToUndist);
				undistToDist = new PixelTransformCached_F32(intrinsic.width, intrinsic.height, undistToDist);
			}

			squareDetector.setLensDistortion(intrinsic.width,intrinsic.height,distToUndist,undistToDist);

			pointSquareToInput = new SequencePointTransform_F32(transformHomography,pointUndistToDist);

			this.pointUndistToDist = LensDistortionOps.transform_F64(AdjustmentType.FULL_VIEW, intrinsic, null, true);

			intrinsic = intrinsicUndist;
		} else {
			pointSquareToInput = transformHomography;
			pointUndistToDist = new DoNothingTransform_F64();
		}

		poseEstimator.setIntrinsic(intrinsic);

		// provide intrinsic camera parameters
		PixelTransform_F32 squareToInput= new PointToPixelTransform_F32(pointSquareToInput);
		removePerspective.setModel(squareToInput);

		binary.reshape(intrinsic.width,intrinsic.height);
	}

	Polygon2D_F64 interpolationHack = new Polygon2D_F64(4);
	/**
	 * Examines the input image to detect fiducials inside of it
	 *
	 * @param gray Undistorted input image
	 */
	public void process( T gray ) {
		binary.reshape(gray.width,gray.height);

		inputToBinary.process(gray,binary);
		squareDetector.process(gray,binary);
		FastQueue<Polygon2D_F64> candidates = squareDetector.getFoundPolygons();

		found.reset();

		if( verbose ) System.out.println("---------- Got Polygons! "+candidates.size);
		// undistort the squares
		Quadrilateral_F64 q = new Quadrilateral_F64(); // todo predeclare
		for (int i = 0; i < candidates.size; i++) {
			// compute the homography from the input image to an undistorted square image
			Polygon2D_F64 p = candidates.get(i);

			// REMOVE EVENTUALLY  This is a hack around how interpolation is performed
			// Using a surface integral instead would remove the need for this.  Basically by having it start
			// interpolating from the lower extent it samples inside the image more
			// A good unit test to see if this hack is no longer needed is to rotate the order of the polygon and
			// see if it returns the same undistorted image each time
			double best=Double.MAX_VALUE;
			for (int j = 0; j < 4; j++) {
				double found = p.get(0).normSq();
				if( found < best ) {
					best = found;
					interpolationHack.set(p);
				}
				UtilPolygons2D_F64.shiftDown(p);
			}

			UtilPolygons2D_F64.convert(interpolationHack,q);

			// remember, visual clockwise isn't the same as math clockwise, hence
			// counter clockwise visual to the clockwise quad
			pairsRemovePerspective.get(0).set(0, 0, q.a.x, q.a.y);
			pairsRemovePerspective.get(1).set( square.width ,      0        , q.b.x , q.b.y );
			pairsRemovePerspective.get(2).set( square.width , square.height , q.c.x , q.c.y );
			pairsRemovePerspective.get(3).set( 0            , square.height , q.d.x , q.d.y );

			if( !computeHomography.process(pairsRemovePerspective,H) ) {
				if( verbose ) System.out.println("rejected initial homography");
				continue;
			}

			// refine homography estimate
			if( !refineHomography.fitModel(pairsRemovePerspective,H,H_refined) ) {
				if( verbose ) System.out.println("rejected refine homography");
				continue;
			}

			// pass the found homography onto the image transform
			UtilHomography.convert(H_refined, transformHomography.getModel());

			// TODO Improve how perspective is removed
			// The current method introduces artifacts.  If the "square" is larger
			// than the detected region and bilinear interpolation is used then pixels outside will// influence the
			// value of pixels inside and shift things over.  this is all bad

			// remove the perspective distortion and process it
			removePerspective.apply(gray, square);

			BinaryPolygonDetector.Info info = squareDetector.getPolygonInfo().get(i);

			// see if the black border is actually black
			if( minimumBorderBlackFraction > 0 ) {
				double pixelThreshold = (info.edgeInside + info.edgeOutside) / 2;
				double foundFraction = computeFractionBoundary((float) pixelThreshold);
				if( foundFraction < minimumBorderBlackFraction ) {
					if( verbose ) System.out.println("rejected black border fraction "+foundFraction);
					continue;
				}
			}
			if( processSquare(square,result,info.edgeInside,info.edgeOutside)) {
				prepareForOutput(q,result);

				if( verbose ) System.out.println("accepted!");
			} else {
				if( verbose ) System.out.println("rejected process square");
			}
		}
	}

	/**
	 * Computes the fraction of pixels inside the image border which are black
	 * @param pixelThreshold  Pixel's less than this value are considered black
	 * @return fraction of border that's black
	 */
	protected double computeFractionBoundary( float pixelThreshold ) {
		final int w = square.width;
		int radius = (int) (w * borderWidthFraction);

		int innerWidth = w-2*radius;
		int total = w*w - innerWidth*innerWidth;
		int count = 0;
		for (int y = 0; y < radius; y++) {
			int indexTop = y*w;
			int indexBottom = (w - radius + y)*w;

			for (int x = 0; x < w; x++) {
				if( square.data[indexTop++] < pixelThreshold )
					count++;
				if( square.data[indexBottom++] < pixelThreshold )
					count++;
			}
		}

		for (int y = radius; y < w-radius; y++) {
			int indexLeft = y*w;
			int indexRight = y*w + w - radius;

			for (int x = 0; x < radius; x++) {
				if( square.data[indexLeft++] < pixelThreshold )
					count++;
				if( square.data[indexRight++] < pixelThreshold )
					count++;
			}
		}

		return count/(double)total;
	}

	/**
	 * Takes the found quadrilateral and the computed 3D information and prepares it for output
	 */
	private void prepareForOutput(Quadrilateral_F64 imageShape, Result result) {
		// the rotation estimate, apply in counter clockwise direction
		// since result.rotation is a clockwise rotation in the visual sense, which
		// is CCW on the grid
		int rotationCCW = (4-result.rotation)%4;
		for (int j = 0; j < rotationCCW; j++) {

			rotateCounterClockwise(imageShape);
		}

		// save the results for output
		FoundFiducial f = found.grow();
		f.id = result.which;
		f.location.set(imageShape);

		// put it back into input image coordinates
		for (int j = 0; j < 4; j++) {
			Point2D_F64 a = f.location.get(j);
			pointUndistToDist.compute(a.x,a.y,a);
		}

		// estimate position
		if( wasIntrinsicSet )
			computeTargetToWorld(imageShape, result.lengthSide, f.targetToSensor);
		else {
			f.targetToSensor.reset();
		}
	}

	/**
	 * Rotates the corners on the quad
	 */
	private void rotateCounterClockwise(Quadrilateral_F64 quad) {
		Point2D_F64 a = quad.a;
		Point2D_F64 b = quad.b;
		Point2D_F64 c = quad.c;
		Point2D_F64 d = quad.d;

		quad.a = b;
		quad.b = c;
		quad.c = d;
		quad.d = a;
	}

	/**
	 * Given observed location of corners, compute the transform from target to world frame.
	 * See code comments for correct ordering of corners in quad.
	 *
	 * @param quad (Input) Observed location of corner points in distorted pixels.  Order is important.
	 * @param lengthSide (Input) Length of a side on the square
	 * @param targetToWorld (output) transform from target to world frame.
	 */
	public void computeTargetToWorld( Quadrilateral_F64 quad , double lengthSide , Se3_F64 targetToWorld )
	{
		if( !poseEstimator.process(quad) ) {
			throw new RuntimeException("Failed on pose estimation!");
		}

		targetToWorld.set( poseEstimator.getWorldToCamera() );
		GeometryMath_F64.scale(targetToWorld.getT(), lengthSide);
	}

	/**
	 * Returns list of found fiducials
	 */
	public FastQueue<FoundFiducial> getFound() {
		return found;
	}

	/**
	 * Processes the detected square and matches it to a known fiducial.  Black border
	 * is included.
	 *
	 * @param square Image of the undistorted square
	 * @param result Which target and its orientation was found
	 * @param edgeInside Average pixel value along edge inside
	 * @param edgeOutside Average pixel value along edge outside
	 * @return true if the square matches a known target.
	 */
	protected abstract boolean processSquare( ImageFloat32 square , Result result , double edgeInside , double edgeOutside  );

	/**
	 * Used to toggle on/off verbose debugging information
	 * @param verbose true for verbose output
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public BinaryPolygonDetector getSquareDetector() {
		return squareDetector;
	}

	public ImageUInt8 getBinary() {
		return binary;
	}

	public Class<T> getInputType() {
		return inputType;
	}

	public QuadPoseEstimator getPoseEstimator() {
		return poseEstimator;
	}

	public double getBorderWidthFraction() {
		return borderWidthFraction;
	}

	public static class Result {
		int which;
		// length of one of the sides in world units
		double lengthSide;
		// amount of clockwise rotation.  Each value = +90 degrees
		// Just to make things confusion, the rotation is done in the visual clockwise, which
		// is a counter-clockwise rotation when you look at the actual coordinates
		int rotation;
	}
}
