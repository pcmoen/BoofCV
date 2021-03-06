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

package boofcv.demonstrations.binary;

import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.gui.SelectInputPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.PathLabel;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Demonstrates different ways to threshold an image
 *
 * @author Peter Abeles
 */
// TODO create top panel
// select algorithm
// configure universal threshold
// configure adaptive threshold
public class DemoImageThresholdingApp<T extends ImageSingleBand> extends SelectInputPanel
	implements DemoThresholdingPanel.Listener
{

	Class<T> imageType;
	T imageInput;
	ImageUInt8 imageBinary = new ImageUInt8(1,1);
	BufferedImage work;

	ImagePanel gui = new ImagePanel();
	DemoThresholdingPanel control = new DemoThresholdingPanel(100,false,20,1.0,this);

	boolean processedImage = false;

	// which algorithm is being used
	int which=0;

	public DemoImageThresholdingApp(Class<T> imageType) {
		this.imageType = imageType;

		imageInput = GeneralizedImageOps.createSingleBand(imageType,1,1);

		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());

		body.add(control,BorderLayout.WEST);
		body.add(gui,BorderLayout.CENTER);

		setMainGUI(body);
	}

	protected void process() {

		int threshValue = control.getValueThreshold();
		boolean thresholdDown = control.getDirection();
		int threshRadius = control.getThreshRadius();
		double threshScale = control.getScale();

		switch( which ) {
			case 0:
				GThresholdImageOps.threshold(imageInput,imageBinary,threshValue,thresholdDown);
				break;

			case 1: {
				threshValue = GThresholdImageOps.computeOtsu(imageInput,0,255);
				GThresholdImageOps.threshold(imageInput, imageBinary, threshValue, thresholdDown);
			} break;

			case 2: {
				threshValue = GThresholdImageOps.computeEntropy(imageInput, 0, 255);
				GThresholdImageOps.threshold(imageInput, imageBinary, threshValue, thresholdDown);
			} break;

			case 3:
				GThresholdImageOps.localSquare(imageInput, imageBinary,
						threshRadius, threshScale, thresholdDown, null, null);
				break;

			case 4:
				GThresholdImageOps.localGaussian(imageInput, imageBinary,
						threshRadius, threshScale, thresholdDown, null, null);
				break;

			case 5:
				GThresholdImageOps.localSauvola(imageInput, imageBinary,
						threshRadius, 0.3f, thresholdDown);
				break;

			case 6:
				GThresholdImageOps.localBlockMinMax(imageInput, imageBinary,
						threshRadius*2+1, threshScale, thresholdDown ,15 );
				break;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (work == null || work.getWidth() != imageInput.width || work.getHeight() != imageInput.height) {
					work = new BufferedImage(imageInput.width, imageInput.height, BufferedImage.TYPE_INT_BGR);
				}
				VisualizeBinaryData.renderBinary(imageBinary, false, work);
				gui.setBufferedImage(work);
				gui.setPreferredSize(new Dimension(imageInput.width, imageInput.height));
				processedImage = true;
				gui.repaint();
			}
		});
	}

	@Override
	public void changeInput(String name, int index) {
		BufferedImage image = media.openImage(inputRefs.get(index).getPath());
		if( image != null ) {
			setInputImage(image);
			imageInput.reshape(image.getWidth(),image.getHeight());
			imageBinary.reshape(image.getWidth(),image.getHeight());

			ConvertBufferedImage.convertFrom(image, imageInput, true);

			process();
		}
	}

	@Override
	public void loadConfigurationFile(String fileName) {}

	@Override
	public boolean getHasProcessedImage() {
		return processedImage;
	}

	public static void main( String args[] ) {
		DemoImageThresholdingApp app = new DemoImageThresholdingApp(ImageFloat32.class);

		java.util.List<PathLabel> inputs = new ArrayList<PathLabel>();
		inputs.add(new PathLabel("particles", UtilIO.pathExample("particles01.jpg")));
		inputs.add(new PathLabel("shapes",UtilIO.pathExample("shapes/shapes01.png")));
		inputs.add(new PathLabel("stained",UtilIO.pathExample("segment/stained_handwriting.jpg")));

		app.setInputList(inputs);

		// wait for it to process one image so that the size isn't all screwed up
		while( !app.getHasProcessedImage() ) {
			Thread.yield();
		}

		ShowImages.showWindow(app, "Thresholding Demo", true);

		System.out.println("Done");
	}

	@Override
	public void changeSelected(int which) {
		this.which = which;

		new Thread() {
			public void run() { process(); }
		}.start();
	}

	@Override
	public void settingChanged() {
		new Thread() {
			public void run() { process(); }
		}.start();
	}
}
