package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaCurrentGame;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.Tfod;

import java.util.List;

@Autonomous(name = "TFTestOpMode (Blocks to Java)")
public class TFTestOpMode extends LinearOpMode {

  private VuforiaCurrentGame vuforiaPOWERPLAY;
  private Tfod tfod;

  Recognition recognition;

  /**
   * This function is executed when this Op Mode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    AutoMove control = new AutoMove();

    control.init(hardwareMap);

    List<Recognition> recognitions;
    int index;

    vuforiaPOWERPLAY = new VuforiaCurrentGame();
    tfod = new Tfod();

    // Sample TFOD Op Mode using a Custom Model
    // Initialize Vuforia to provide TFOD with camera
    // images.
    // The following block uses the device's back camera.
    vuforiaPOWERPLAY.initialize(
        "AdUOt3L/////AAABmZecIMYFSkGNn8vdCiGcT+Rktn5VewpS17LaEVQLqk4OKhDDPmCKSO+7TXLgSLAXclfhjgcw2RyrRIt3PK/X8epQJ2QCQEmLA9hiTh145k2yU7oAro9iFJdCFqn/+Rd/YrTQBBLg8UZKwXhGMvIS7KDiF37EXAaUtZufs0fJFFWxjEJXUMhMy+RyIiUujBO8FcDqYKLg794GAhBqQBfjuYd30XDiUepTrptT6imZmTPv6a/6UskixoJAnLPPyjP2zP9EAoCeIxO8PAjZ+ZpfqV4xGZ0ICmBWHX2DkJuRv8yGyNr98+cqzAs73iOOpskO58dKTP3MzK7+tA/M+rYY25+0g/U9SmrIBS/C7bYhn9x5", // vuforiaLicenseKey
        hardwareMap.get(WebcamName.class, "Webcam 1"), // cameraDirection
        "",
        false, // useExtendedTracking
        false, // enableCameraMonitoring
        VuforiaLocalizer.Parameters.CameraMonitorFeedback.NONE, // cameraMonitorFeedback
        0, // dx
        0, // dy
        0, // dz
        AxesOrder.XYZ, // axesOrder
        0, // firstAngle
        -90, // secondAngle
        0, // thirdAngle
        true); // useCompetitionFieldTargetLocations
    // The following block uses a webcam.
    // Initialize TFOD before waitForStart.
    // Use the Manage page to upload your custom model.
    // In the next block, replace
    // YourCustomModel.tflite with the name of your
    // custom model.
    // Set isModelTensorFlow2 to true if you used a TensorFlow
    // 2 tool, such as ftc-ml, to create the model.
    //
    // Set isModelQuantized to true if the model is
    // quantized. Models created with ftc-ml are quantized.
    //
    // Set inputSize to the image size corresponding to the model.
    // If your model is based on SSD MobileNet v2
    // 320x320, the image size is 300 (srsly!).
    // If your model is based on SSD MobileNet V2 FPNLite 320x320, the image size is 320.
    // If your model is based on SSD MobileNet V1 FPN 640x640 or
    // SSD MobileNet V2 FPNLite 640x640, the image size is 640.
    tfod.useDefaultModel();
    tfod.initialize(vuforiaPOWERPLAY, (float) 0.5, true, true);
    tfod.setClippingMargins(810, 390, 810, 390);
    tfod.activate();
    // Enable following block to zoom in on target.
    tfod.setZoom(2, 16 / 9);
    telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
    telemetry.addData(">", "Press Play to start");
    telemetry.update();
    // Wait for start command from Driver Station.
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      while (opModeIsActive()) {
        control.updatePos();
        // Put loop blocks here.
        // Get a list of recognitions from TFOD.
        recognitions = tfod.getRecognitions();
        // If list is empty, inform the user. Otherwise, go
        // through list and display info for each recognition.
        if (JavaUtil.listLength(recognitions) == 0) {
          telemetry.addData("TFOD", "No items detected.");
          if (time >= 20 && !control.moved()) {
            control.three();
          }
        } else {
          index = 0;
          // Iterate through list and call a function to
          // display info for each recognized object.
          for (Recognition recognition_item : recognitions) {
            recognition = recognition_item;

            if (recognition_item.getLabel() == "1 Bolt") {
              control.one();
            } else if (recognition_item.getLabel() == "2 Bulb") {
              control.two();
            } else if (recognition_item.getLabel() == "3 Panel") {
              control.three();
            }

            // Display info.
            displayInfo(index);
            // Increment index.
            index = index + 1;
          }
        }
        telemetry.update();
      }
    }
    // Deactivate TFOD.
    tfod.deactivate();

    vuforiaPOWERPLAY.close();
    tfod.close();
  }

  /**
   * Describe this function...
   */
  private void displayInfo(int i) {
    // Display the location of the top left corner
    // of the detection boundary for the recognition
    telemetry.addData("Label: " + recognition.getLabel() + ", Confidence: " + recognition.getConfidence(), "X: " + Math.round(JavaUtil.averageOfList(JavaUtil.createListWith(Double.parseDouble(JavaUtil.formatNumber(recognition.getLeft(), 0)), Double.parseDouble(JavaUtil.formatNumber(recognition.getRight(), 0))))) + ", Y: " + Math.round(JavaUtil.averageOfList(JavaUtil.createListWith(Double.parseDouble(JavaUtil.formatNumber(recognition.getTop(), 0)), Double.parseDouble(JavaUtil.formatNumber(recognition.getBottom(), 0))))));
  }
}
