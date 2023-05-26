package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@TeleOp
public class ManualOpMode extends OpMode {

    MotorController motorController;
    VuforiaLocalizer vuforia;
    VuforiaTrackables targets;

    int[] wheelsOG;
    int[] armsOG;

    boolean left_bumper_previous;

    @Override
    public void init() {
        // get the motors
        DcMotor frontLeft = hardwareMap.get(DcMotor.class, "FL");
        DcMotor frontRight = hardwareMap.get(DcMotor.class, "FR");
        DcMotor backLeft = hardwareMap.get(DcMotor.class, "BL");
        DcMotor backRight = hardwareMap.get(DcMotor.class, "BR");
        DcMotor verticalArm = hardwareMap.get(DcMotor.class, "VA");
        DcMotor horizontalArm = hardwareMap.get(DcMotor.class, "HA");

        Servo sender = hardwareMap.get(Servo.class, "Sender");
        Servo angleServo = hardwareMap.get(Servo.class, "AS");
        Servo handServo = hardwareMap.get(Servo.class, "HS");

        motorController = new MotorController(frontLeft, frontRight, backLeft, backRight, verticalArm, sender, horizontalArm, angleServo, handServo);

        wheelsOG = motorController.getWheels();
        armsOG = motorController.getArms();

        left_bumper_previous = false;

        telemetry.addData("Status", "OpMode initialized!");
    }

    @Override
    public void start() {
        telemetry.addData("Status", "OpMode Running!");
    }

    @Override
    public void loop() {
        // get the gamepad values
        double k0 = 0.5;

        double vertical = gamepad1.left_stick_y * k0;
        double horizontal = -gamepad1.left_stick_x * k0;
        double turn;

        if (gamepad1.left_trigger > 0 && gamepad1.right_trigger == 0) {
            turn = -gamepad1.left_trigger * 0.5;
        } else if (gamepad1.right_trigger > 0 && gamepad1.left_trigger == 0) {
            turn = gamepad1.right_trigger * 0.5;
        } else {
            turn = 0;
        }

        if (gamepad1.dpad_up && !gamepad1.dpad_down) {
            vertical = -0.01;
        } else if (gamepad1.dpad_down && !gamepad1.dpad_up) {
            vertical = 0.01;
        }

        if (gamepad1.dpad_left && !gamepad1.dpad_right) {
            horizontal = 0.005;
        } else if (gamepad1.dpad_right && !gamepad1.dpad_left) {
            horizontal = -0.005;
        }

        // for debug
        telemetry.addData("Vertical", vertical);
        telemetry.addData("Horizontal", horizontal);
        telemetry.addData("Turn", turn);

        motorController.move(vertical, horizontal, turn);

        double verticalArm;
        double horizontalArm;

        double absRX = Math.abs(gamepad1.right_stick_x);
        double absRY = Math.abs(gamepad1.right_stick_y);

        double ySignificance = 0.1;
        double xSignificance = 0.15;

        if (gamepad1.right_stick_button) {
            verticalArm = -0.1;
            horizontalArm = 0.1;
        } else if (absRY >= ySignificance && absRX < xSignificance) {
            verticalArm = gamepad1.right_stick_y;
            horizontalArm = 0;
        } else if (absRX >= xSignificance && absRY < ySignificance) {
            verticalArm = 0;
            horizontalArm = gamepad1.right_stick_x;
        } else {
            verticalArm = 0;
            horizontalArm = 0;
        }

        telemetry.addData("VerticalArm", verticalArm);
        telemetry.addData("HorizontalArm", horizontalArm);

        motorController.setVerticalArm(verticalArm);
        motorController.setHorizontalArm(horizontalArm);

        if (gamepad1.a) {
            motorController.send();
            telemetry.addData("Sender", "send");
        } else {
            motorController.unsend();
            telemetry.addData("Sender", "not send");
        }

        if (gamepad1.b) {
            motorController.setHand(1);
            telemetry.addData("Hand", "opened");
        } else {
            motorController.setHand(0.3);
            telemetry.addData("Hand", "closed");
        }

        if (gamepad1.x && !gamepad1.y) {
            motorController.tilt(1);
            telemetry.addData("Tilt", "tilted");
        } else if (gamepad1.y && !gamepad1.x) {
            motorController.tilt(0.11);
            telemetry.addData("Tilt", "flat");
        } else {
            motorController.tilt(0.6);
            telemetry.addData("Tilt", "idle");
        }


        int[] wheels = motorController.getWheels();
        int[] arms = motorController.getArms();
        double[] servos = motorController.getServos();

        if (gamepad1.left_bumper && !left_bumper_previous) {
            motorController.setArmsPosition(arms[0], armsOG[1] - 800);
            telemetry.addData("extend", "yes");
        }
        left_bumper_previous = gamepad1.left_bumper;

        telemetry.addLine("------------------------------------");

        telemetry.addData("FrontLeft pos", wheels[0]);
        telemetry.addData("FrontRight pos", wheels[1]);
        telemetry.addData("BackLeft pos", wheels[2]);
        telemetry.addData("BackRight pos", wheels[3]);

        telemetry.addData("VerticalArm pos", arms[0]);
        telemetry.addData("HorizontalArm pos", arms[1]);

        telemetry.addData("Sender pos", servos[0]);
        telemetry.addData("AngleServo pos", servos[1]);
        telemetry.addData("HandServo pos", servos[2]);

        telemetry.addLine("------------------------------------");

        telemetry.addData("D FrontLeft", wheels[0] - wheelsOG[0]);
        telemetry.addData("D FrontRight", wheels[1] - wheelsOG[1]);
        telemetry.addData("D BackLeft", wheels[2] - wheelsOG[2]);
        telemetry.addData("D BackRight", wheels[3] - wheelsOG[3]);

        telemetry.addData("D VerticalArm", arms[0] - armsOG[0]);
        telemetry.addData("D HorizontalArm", arms[1] - armsOG[1]);
    }

    @Override
    public void stop() {
        telemetry.addData("Status", "OpMode stopped!");
        motorController.stop();
    }
}
