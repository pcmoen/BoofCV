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

import boofcv.abst.filter.binary.InputToBinary;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.LinearContourLabelChang2004;
import boofcv.demonstrations.shapes.ThresholdControlPanel;
import boofcv.factory.filter.binary.ConfigThreshold;
import boofcv.factory.filter.binary.FactoryThresholdBinary;
import boofcv.gui.DemonstrationBase;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ImageZoomPanel;
import boofcv.gui.image.ShowImages;
import boofcv.struct.image.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Abeles
 */
public class VisualizeBinaryContourApp <T extends ImageSingleBand> extends DemonstrationBase<T>
	implements ThresholdControlPanel.Listener
{
	VisualizePanel guiImage;
	ContourControlPanel controls = new ContourControlPanel(this);

	LinearContourLabelChang2004 contourAlg;
	InputToBinary<T> inputToBinary;

	ImageUInt8 binary = new ImageUInt8(1,1);
	ImageSInt32 labeled = new ImageSInt32(1,1);

	BufferedImage original;
	BufferedImage work;

	public VisualizeBinaryContourApp(List<String> exampleInputs, ImageType<T> imageType) {
		super(exampleInputs, imageType);

		guiImage = new VisualizePanel();

		add(BorderLayout.WEST, controls);
		add(BorderLayout.CENTER, guiImage);

		ConfigThreshold config = controls.getThreshold().createConfig();
		inputToBinary = FactoryThresholdBinary.threshold(config,imageType.getImageClass());
		contourAlg = new LinearContourLabelChang2004(controls.getConnectRule());
	}

	@Override
	public void processImage(final BufferedImage buffered, T input) {

		if( buffered != null ) {

			original = conditionalDeclare(buffered,original);
			work = conditionalDeclare(buffered,work);

			this.original.createGraphics().drawImage(buffered,0,0,null);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Dimension d = guiImage.getPreferredSize();
					if( d.getWidth() < buffered.getWidth() || d.getHeight() < buffered.getHeight() ) {
						guiImage.setPreferredSize(new Dimension(buffered.getWidth(), buffered.getHeight()));
					}
				}});
		}

		binary.reshape(input.width,input.height);
		labeled.reshape(input.width,input.height);

		inputToBinary.process(input,binary);

		synchronized (this) {
			contourAlg.process(binary,labeled);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				viewUpdated();
			}
		});
	}

	@Override
	public void imageThresholdUpdated() {
		synchronized (this) {
			ConfigThreshold config = controls.getThreshold().createConfig();
			inputToBinary = FactoryThresholdBinary.threshold(config,imageType.getImageClass());
			reprocessSingleImage();
		}
	}

	public void contourAlgUpdated() {
		synchronized (this) {
			contourAlg = new LinearContourLabelChang2004(controls.getConnectRule());
			reprocessSingleImage();
		}
	}

	/**
	 * Called when how the data is visualized has changed
	 */
	public void viewUpdated() {
		BufferedImage active = null;
		if( controls.selectedView == 0 ) {
			active = original;
		} else if( controls.selectedView == 1 ) {
			VisualizeBinaryData.renderBinary(binary,false,work);
			active = work;
			work.setRGB(0, 0, work.getRGB(0, 0));
		} else {
			Graphics2D g2 = work.createGraphics();
			g2.setColor(Color.BLACK);
			g2.fillRect(0,0,work.getWidth(),work.getHeight());
			active = work;
		}

		guiImage.setScale(controls.zoom);

		guiImage.setBufferedImage(active);
		guiImage.repaint();
	}

	class VisualizePanel extends ImageZoomPanel {
		@Override
		protected void paintInPanel(AffineTransform tran, Graphics2D g2) {
			synchronized (VisualizeBinaryContourApp.this) {
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				List<Contour> contours = contourAlg.getContours().toList();
				g2.setStroke(new BasicStroke(1));
				g2.setColor(Color.RED);
				VisualizeBinaryData.renderExternal(contours,true,true, scale, g2);
			}
		}
	}

	public static void main(String[] args) {

		List<String> examples = new ArrayList<String>();
		examples.add("shapes/shapes02.png");
		examples.add("shapes/concave01.jpg");
		examples.add("shapes/polygons01.jpg");

		VisualizeBinaryContourApp app = new VisualizeBinaryContourApp(examples,ImageType.single(ImageFloat32.class));

		app.openFile(new File(examples.get(0)));

		app.waitUntilDoneProcessing();

		ShowImages.showWindow(app,"Contour Visualization",true);
	}
}
