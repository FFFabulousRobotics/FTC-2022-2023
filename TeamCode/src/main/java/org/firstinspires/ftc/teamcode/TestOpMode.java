package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class TestOpMode extends OpMode {

    MotorController motorController;

    @Override
    public void init() {
        // get the motors
        DcMotor frontLeft = hardwareMap.get(DcMotor.class, "FL");
        DcMotor frontRight = hardwareMap.get(DcMotor.class, "FR");
        DcMotor backLeft = hardwareMap.get(DcMotor.class, "BL");
        DcMotor backRight = hardwareMap.get(DcMotor.class, "BR");
        DcMotor armLeft = hardwareMap.get(DcMotor.class, "AL");
        DcMotor armRight = hardwareMap.get(DcMotor.class, "AR");
        DcMotor hand = hardwareMap.get(DcMotor.class, "HAND");

        motorController = new MotorController(frontLeft, frontRight, backLeft, backRight,
                armLeft, armRight, hand);
        telemetry.addData("Status", "OpMode initialized!");
    }

    @Override
    public void loop() {
            telemetry.addData("Status", "OpMode running!");

            // get the gamepad values
            double vertical = gamepad1.left_stick_y;
            double horizontal = gamepad1.left_stick_x;
            double turn;

            if (gamepad1.left_bumper && !gamepad1.right_bumper) {
                turn = -0.3;
            } else if (gamepad1.right_bumper && !gamepad1.left_bumper) {
                turn = 0.3;
            } else {
                turn = 0;
            }

            // for debug
            telemetry.addData("Vertical", vertical);
            telemetry.addData("Horizontal", horizontal);
            telemetry.addData("Turn", turn);

            motorController.move(vertical, horizontal, turn);

            double verticalArm = gamepad1.right_stick_y;
            double horizontalArm;
            double hand;

            if (gamepad1.dpad_right && !gamepad1.dpad_left) {
                hand = 0.5;
            } else if (gamepad1.dpad_left && !gamepad1.dpad_right) {
                hand = -0.5;
            } else {
                hand = 0;
            }

            if (gamepad1.dpad_up && !gamepad1.dpad_down) {
                horizontalArm = 0.5;
            } else if (gamepad1.dpad_down &&!gamepad1.dpad_up) {
                horizontalArm = -0.5;
            } else {
                horizontalArm = 0;
            }

            telemetry.addData("VerticalArm", verticalArm);
            telemetry.addData("HorizontalArm", horizontalArm);
            telemetry.addData("Hand", hand);

            motorController.verticalArm(verticalArm);
            motorController.horizontalArm(horizontalArm);
            motorController.hand(hand);
    }

    @Override
    public void stop() {
        telemetry.addData("Status", "OpMode stopped!");
        motorController.stop();
    }
}
