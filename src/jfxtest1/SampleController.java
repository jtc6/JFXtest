package jfxtest1;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import it.polito.elite.teaching.cv.utils.Utils;

import org.opencv.highgui.VideoCapture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SampleController {
	@FXML
	private Button Start_btn;
	
	@FXML
	private ImageView currentFrame;
	private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	private boolean cameraActive = false;
	private static int cameraId = 0;
	
	@FXML
	public void startCamera()
	{
		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(cameraId);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						// convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(currentFrame, imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.Start_btn.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.Start_btn.setText("Start Camera");
			
			// stop the timer
			this.stopAquisition();
		}
		
		System.out.println("Camera is now on.");
	}
		
	/**
	 * Get frame from open video
	 * @return
	 */
		private Mat grabFrame()
		
		{
			Mat frame = new Mat();
			if (this.capture.isOpened())
			{
				try
				{
					this.capture.read(frame);
					
					}
			catch (Exception e)
			{
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		return frame;
	}
		private void stopAquisition()
		{
			if (this.timer!=null && !this.timer.isShutdown())
			{
				try
				{
					this.timer.shutdown();
					this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
				}
				catch (InterruptedException e)
				{
					System.err.println("Exception in stopping the frame capture, trying to release camera now..." + e);
					
				}
			}
			if (this.capture.isOpened())
			{
				this.capture.release();
			}
		}
		private void updateImageView(ImageView view, Image image)
		{
			Utils.onFXThread(view.imageProperty(), image);
		}
		protected void setClosed()
		{ 
			this.stopAquisition();
		}
	}
