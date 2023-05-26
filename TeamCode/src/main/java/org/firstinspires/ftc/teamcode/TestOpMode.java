package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TestOpMode extends OpMode {
    Servo as;
    boolean xPrevious;
    boolean yPrevious;

    @Override
    public void init() {
        as = hardwareMap.get(Servo.class, "AS");
        as.scaleRange(0.0, 1.0);
        xPrevious = false;
        yPrevious = false;
    }

    @Override
    public void loop() {
        if (gamepad1.x && !gamepad1.y) {
            if (!xPrevious) {
                as.setPosition(as.getPosition() - 0.01);
            }
        } else if (gamepad1.y && !gamepad1.x) {
            if (!yPrevious) {
                as.setPosition(as.getPosition() + 0.01);
            }
        }
        xPrevious = gamepad1.x;
        yPrevious = gamepad1.y;
        telemetry.addData("Servo pos", as.getPosition());
    }
}
