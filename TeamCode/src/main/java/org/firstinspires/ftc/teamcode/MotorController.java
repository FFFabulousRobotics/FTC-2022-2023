package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import java.util.stream.DoubleStream;

public class MotorController {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final DcMotor verticalArm;
    private final DcMotor horizontalArm;
    private final DcMotor hand;

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
     * @param va The vertical arm {@link DcMotor}.
     * @param ha The horizontal arm {@link DcMotor}.
     * @param hand The hand {@link DcMotor}.
     */
    public MotorController(DcMotor fl, DcMotor fr, DcMotor bl, DcMotor br, DcMotor va, DcMotor ha, DcMotor hand) {
        frontLeft = fl;
        frontRight = fr;
        backLeft = bl;
        backRight = br;

        this.verticalArm = va;
        this.horizontalArm = ha;
        this.hand = hand;

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

        double[] scaledValues = arrayScale(lFront, lBack, rFront, rBack);

        lFront = scaledValues[0];
        lBack = scaledValues[1];
        rFront = scaledValues[2];
        rBack = scaledValues[3];

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

    /**
     * Lift or retract the vertical arm.
     *
     * @param speed Motor speed.
     */
    public void verticalArm(double speed) {
        double scaledSpeed = scale(speed);

        verticalArm.setPower(scaledSpeed);
    }

    /**
     * Stretch or retract the horizontal arm.
     *
     * @param speed Motor speed.
     */
    public void horizontalArm(double speed) {
        double scaledSpeed = scale(speed);

        horizontalArm.setPower(scaledSpeed);
    }

    /**
     * Turn the hand.
     *
     * @param speed Motor speed.
     */
    public void hand(double speed) {
        double scaledSpeed = scale(speed);

        hand.setPower(Range.clip(speed, -1.0, 1.0));
    }

    /**
     * Scale the double value to [-1.0, 1.0].
     *
     * @param value The value to be scaled.
     * @return The scaled value.
     */
    private double scale(double value) {
        double absValue = Math.abs(value);

        return Range.scale(value, -absValue, absValue, -1.0, 1.0);
    }

    /**
     * Scale double values to [-1.0, 1.0] proportionally.
     *
     * @param values The values to be scaled.
     * @return The scaled values array.
     */
    private double[] arrayScale(double... values) {
        double maxAbs = DoubleStream.of(values).map(d -> Math.abs(d)).max().getAsDouble();

        double[] scaledValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            scaledValues[i] =  Range.scale(values[i], -maxAbs, maxAbs, -1.0, 1.0);
        }

        return scaledValues;
    }
}
