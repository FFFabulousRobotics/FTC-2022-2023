package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class MotorController {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;

    /**
     * The controller for the motors (with a mecanum wheels setup).
     *
     * The velocities for the wheels are as follows:
     *
     * FL & BR: right + forward
     * FR & BL: left + forward
     *
     * @param fl The front left {@link DcMotor}.
     * @param fr The front right {@link DcMotor}.
     * @param bl The back left {@link DcMotor}.
     * @param br The back right {@link DcMotor}.
     */
    public MotorController(DcMotor fl, DcMotor fr, DcMotor bl, DcMotor br) {
        frontLeft = fl;
        frontRight = fr;
        backLeft = bl;
        backRight = br;

        // set the direction so the code becomes more readable
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    /**
     * Turn the motors to perform the desired move. All the parameters are relative to each other.
     *
     * @param vertical Vertical movement speed.
     * @param horizontal Horizontal movement speed.
     * @param turn The angle velocity for turning. Set to positive for clockwise, negative for anti-clockwise.
     */
    public void move(double vertical, double horizontal, double turn) {
        double lFront;
        double lBack;
        double rFront;
        double rBack;
        double k0 = 0.75;

        lFront = -(vertical + turn + horizontal);
        lBack = -(vertical + turn - horizontal);
        rFront = (vertical - turn - horizontal);
        rBack = (vertical - turn + horizontal);

        lFront = Range.clip(lFront, -1.0, 1.0);
        lBack = Range.clip(lBack, -1.0, 1.0);
        rFront = Range.clip(rFront, -1.0, 1.0);
        rBack = Range.clip(rBack, -1.0, 1.0);

        frontLeft.setPower(lFront * k0);
        frontRight.setPower(rFront * k0);
        backLeft.setPower(lBack * k0);
        backRight.setPower(rBack * k0);
    }

    /**
     * Stop all the motors.
     */
    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
