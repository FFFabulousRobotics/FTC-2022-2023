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

        motorController = new MotorController(frontLeft, frontRight, backLeft, backRight);
        telemetry.addData("Status", "OpMode initialized!");
    }

    @Override
    public void init_loop() {}

    @Override
    public void start() {}

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
    }

    @Override
    public void stop() {
        telemetry.addData("Status", "OpMode stopped!");
        motorController.stop();
    }
}
