package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XZY;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous
public class AutoOpMode extends OpMode {

    float mmPerInch = 25.4f;

    VuforiaLocalizer vuforia;
    VuforiaTrackables targets;

    @SuppressLint("SdCardPath")
    String TFOD_MODEL_ASSET = "/sdcard/FIRST/tflitemodels/MyModel.tflite";

    String[] LABELS = {"1", "2", "3"};

    TFObjectDetector tfod;

    void identifyTarget(int targetIndex, String targetName, float dx, float dy, float dz, float rx, float ry, float rz) {
        VuforiaTrackable aTarget = targets.get(targetIndex);
        aTarget.setName(targetName);
        aTarget.setLocation(OpenGLMatrix.translation(dx, dy, dz)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, rx, ry, rz)));
    }

    @Override
    public void init() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey =
                "AdUOt3L/////AAABmZecIMYFSkGNn8vdCiGcT+Rktn5VewpS17LaEVQLqk4OKhDDPmCKSO+7TXLgSLAXclfhjgcw2RyrRIt3PK/X8epQJ2QCQEmLA9hiTh145k2yU7oAro9iFJdCFqn/+Rd/YrTQBBLg8UZKwXhGMvIS7KDiF37EXAaUtZufs0fJFFWxjEJXUMhMy+RyIiUujBO8FcDqYKLg794GAhBqQBfjuYd30XDiUepTrptT6imZmTPv6a/6UskixoJAnLPPyjP2zP9EAoCeIxO8PAjZ+ZpfqV4xGZ0ICmBWHX2DkJuRv8yGyNr98+cqzAs73iOOpskO58dKTP3MzK7+tA/M+rYY25+0g/U9SmrIBS/C7bYhn9x5";
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        parameters.useExtendedTracking = false;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        targets = vuforia.loadTrackablesFromAsset("PowerPlay");

        final float mmTargetHeight   = 6 * mmPerInch;          // the height of the center of the target image above the floor
        final float halfField        = 72 * mmPerInch;
        final float halfTile         = 12 * mmPerInch;
        final float oneAndHalfTile   = 36 * mmPerInch;

        identifyTarget(0, "Red Audience Wall",   -halfField,  -oneAndHalfTile, mmTargetHeight, 90, 0,  90);
        identifyTarget(1, "Red Rear Wall",        halfField,  -oneAndHalfTile, mmTargetHeight, 90, 0, -90);
        identifyTarget(2, "Blue Audience Wall",  -halfField,   oneAndHalfTile, mmTargetHeight, 90, 0,  90);
        identifyTarget(3, "Blue Rear Wall",       halfField,   oneAndHalfTile, mmTargetHeight, 90, 0, -90);

        final float CAMERA_FORWARD_DISPLACEMENT  = 155.0f;   // eg: Enter the forward distance from the center of the robot to the camera lens
        final float CAMERA_VERTICAL_DISPLACEMENT = 85.0f;   // eg: Camera is 6 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 20.0f;

        OpenGLMatrix cameraLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XZY, DEGREES, 90, 90, 0));

        for (VuforiaTrackable trackable : targets) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setCameraLocationOnRobot(parameters.cameraName, cameraLocationOnRobot);
        }

        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);

        tfod.setZoom(1.0, 16.0/9.0);
    }

    @Override
    public void start() {
        targets.activate();
        tfod.activate();
    }

    @Override
    public void loop() {
        boolean targetVisible = false;
        OpenGLMatrix lastLocation = null;
        for (VuforiaTrackable trackable : targets) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                telemetry.addData("Visible Target", trackable.getName());
                targetVisible = true;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                break;
            }
        }

        // Provide feedback as to where the robot is located (if we know).
        if (targetVisible) {
            // express position (translation) of robot in inches.
            VectorF translation = lastLocation.getTranslation();
            telemetry.addData("Pos (inches)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

            // express the rotation of the robot in degrees.
            Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
            telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
        }
        else {
            telemetry.addData("Visible Target", "none");
        }

        List<Recognition> recognitions = tfod.getUpdatedRecognitions();

        if (recognitions != null) {
            telemetry.addData("# Object Detected", recognitions.size());
            for (Recognition recognition : recognitions) {
                double col = (recognition.getLeft() + recognition.getRight()) / 2 ;
                double row = (recognition.getTop()  + recognition.getBottom()) / 2 ;
                double width  = Math.abs(recognition.getRight() - recognition.getLeft()) ;
                double height = Math.abs(recognition.getTop()  - recognition.getBottom()) ;

                telemetry.addData(""," ");
                telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100 );
                telemetry.addData("- Position (Row/Col)","%.0f / %.0f", row, col);
                telemetry.addData("- Size (Width/Height)","%.0f / %.0f", width, height);
            }
            telemetry.update();
        }
    }

    @Override
    public void stop() {
        if (tfod != null) {
            tfod.shutdown();
        }
    }
}
