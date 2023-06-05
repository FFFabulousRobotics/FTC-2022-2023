package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.DoubleStream;

@TeleOp
public class ManualOpMode extends OpMode {

    MotorController motorController;

    double TRIGGER_TURNING = 0.5;
    double RIGHT_STICK_X_SIGNIFICANCE = 0.15;
    double RIGHT_STICK_Y_SIGNIFICANCE = 0.1;
    double HAND_SERVO_TIGHTNESS = 0.25;
    double ANGLE_SERVO_IDLE = 0.6;
    double ANGLE_SERVO_FLAT = 0.2;
    double ANGLE_SERVO_UP = 0.9;

    GamepadStorage previousGamepad;

    int[] wheelsOG;
    int[] armsOG;
    int[] dWheels;
    int[] dArms;

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

        previousGamepad = new GamepadStorage();

        motorController.tilt(0.6);
        telemetry.addData("Tilt", "idle");

        telemetry.addData("Status", "OpMode initialized!");
    }

    @Override
    public void start() {
        telemetry.addData("Status", "OpMode Running!");
    }

    @Override
    public void loop() {
        int[] wheels = motorController.getWheels();
        int[] arms = motorController.getArms();
        double[] servos = motorController.getServos();
        double[] servosRounded =
                DoubleStream.of(servos)
                        .map(x ->
                                BigDecimal.valueOf(x).setScale(2, RoundingMode.HALF_UP).doubleValue()
                        ).toArray();

        int[] dWheels = {
                wheels[0] - wheelsOG[0],
                wheels[1] - wheelsOG[1],
                wheels[2] - wheelsOG[2],
                wheels[3] - wheelsOG[3]
        };
        int[] dArms = {
                arms[0] - armsOG[0],
                arms[1] - armsOG[1]
        };

        // get the gamepad values
        double vertical = gamepad1.left_stick_y;
        double horizontal = -gamepad1.left_stick_x;
        double turn;

        if (gamepad1.left_trigger > 0 && gamepad1.right_trigger == 0) {
            turn = -gamepad1.left_trigger * TRIGGER_TURNING;
        } else if (gamepad1.right_trigger > 0 && gamepad1.left_trigger == 0) {
            turn = gamepad1.right_trigger * TRIGGER_TURNING;
        } else {
            turn = 0;
        }

        if (gamepad1.left_bumper) {
            motorController.setSpeedMode(MotorController.SpeedMode.SLOW);
        } else {
            motorController.setSpeedMode(MotorController.SpeedMode.FAST);
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

        if (absRY >= RIGHT_STICK_Y_SIGNIFICANCE && absRX < RIGHT_STICK_X_SIGNIFICANCE) {
            verticalArm = gamepad1.right_stick_y;
            horizontalArm = 0;
        } else if (absRX >= RIGHT_STICK_X_SIGNIFICANCE && absRY < RIGHT_STICK_Y_SIGNIFICANCE) {
            verticalArm = 0;
            horizontalArm = gamepad1.right_stick_x;
        } else {
            if (dArms[0] >= 2000) {
                verticalArm = -0.1;
            } else {
                verticalArm = 0;
            }
            horizontalArm = 0;
        }


        telemetry.addData("VerticalArm", verticalArm);
        telemetry.addData("HorizontalArm", horizontalArm);

        motorController.setVerticalArm(verticalArm);
        motorController.setHorizontalArm(horizontalArm);

        if (gamepad1.a) {
            motorController.setSender(0);
            telemetry.addData("Sender", "send");
        } else if (dArms[0] > 0) {
            motorController.setSender(0.5);
        } else {
            motorController.setSender(1);
            telemetry.addData("Sender", "not send");
        }

        if (gamepad1.b && !previousGamepad.b) {
            if (servosRounded[2] == HAND_SERVO_TIGHTNESS) {
                motorController.setHand(1);
                telemetry.addData("Hand", "opened");
            } else {
                motorController.setHand(HAND_SERVO_TIGHTNESS);
                telemetry.addData("Hand", "closed");
            }
        }

        if (gamepad1.y && !previousGamepad.y) {
            if (servosRounded[1] == ANGLE_SERVO_FLAT) {
                motorController.tilt(ANGLE_SERVO_IDLE);
                telemetry.addData("Tilt", "idle");
            } else {
                motorController.tilt(ANGLE_SERVO_FLAT);
                telemetry.addData("Tilt", "flat");
            }
        }
        if (gamepad1.x && !previousGamepad.x) {
            if (servosRounded[1] == ANGLE_SERVO_UP) {
                motorController.tilt(ANGLE_SERVO_IDLE);
                telemetry.addData("Tilt", "idle");
            } else {
                motorController.tilt(ANGLE_SERVO_UP);
                telemetry.addData("Tilt", "up");
            }
        }

        previousGamepad.store(gamepad1);

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

        telemetry.addData("D FrontLeft", dWheels[0]);
        telemetry.addData("D FrontRight", dWheels[1]);
        telemetry.addData("D BackLeft", dWheels[2]);
        telemetry.addData("D BackRight", dWheels[3]);

        telemetry.addData("D VerticalArm", dArms[0]);
        telemetry.addData("D HorizontalArm", dArms[1]);
    }

    @Override
    public void stop() {
        telemetry.addData("Status", "OpMode stopped!");
        motorController.stop();
    }
}
