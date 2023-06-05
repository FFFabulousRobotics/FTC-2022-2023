package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

public class MotorController {
    private final DcMotor frontLeft; // control hub motor 1
    private final DcMotor frontRight; // control hub motor 0
    private final DcMotor backLeft; // control hub motor 3
    private final DcMotor backRight; // control hub motor 2
    private final DcMotor verticalArm; // expansion hub motor 0
    private final Servo sender; // expansion hub servo 0
    private final DcMotor horizontalArm; // expansion hub motor 1
    private final Servo angleServo; // expansion hub servo 2
    private final Servo handServo; // expansion hub servo 1

    public static double k1 = 0.7; // move
    public static final double k2 = 0.7; // vertical arm
    public static final double k3 = 0.5; // horizontal arm

    /**
     * The controller for the motors (with a mecanum wheels setup).
     *
     * The velocities for the wheels are as follows:
     *
     * FL & BR: right + forward
     * FR & BL: left + forward
     *
     * @param frontLeft The front left {@link DcMotor}.
     * @param frontRight The front right {@link DcMotor}.
     * @param backLeft The back left {@link DcMotor}.
     * @param backRight The back right {@link DcMotor}.
     */
    public MotorController(
            DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight,
            DcMotor verticalArm, Servo sender,
            DcMotor horizontalArm, Servo angleServo, Servo handServo) {

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;

        this.verticalArm = verticalArm;
        this.sender = sender;

        this.horizontalArm = horizontalArm;
        this.angleServo = angleServo;
        this.handServo = handServo;

        this.sender.setDirection(Servo.Direction.REVERSE);
        this.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.verticalArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.horizontalArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.angleServo.scaleRange(0.0, 1.0);
    }

    /**
     * Turn the motors to perform the desired move. All the parameters are relative to each other.
     *
     * @param vertical Vertical movement speed.
     * @param horizontal Horizontal movement speed.
     * @param turn The angle velocity for turning. Set to positive for clockwise, negative for anti-clockwise.
     */
    public void move(double vertical, double horizontal, double turn) {
        if (k1 == 0.1) {
            turn = turn * 2;
        }

        double lFront;
        double lBack;
        double rFront;
        double rBack;

        lFront = (vertical + turn + horizontal);
        lBack = (vertical + turn - horizontal);
        rFront = -(vertical - turn - horizontal);
        rBack = -(vertical - turn + horizontal);

        double[] values = {lFront, lBack, rFront, rBack};

        if (DoubleStream.of(values).map(Math::abs).anyMatch(d -> d > 1)) {
            double[] scaledValues = arrayScale(lFront, lBack, rFront, rBack);

            lFront = scaledValues[0];
            lBack = scaledValues[1];
            rFront = scaledValues[2];
            rBack = scaledValues[3];
        }

        frontLeft.setPower(lFront * k1);
        frontRight.setPower(rFront * k1);
        backLeft.setPower(lBack * k1);
        backRight.setPower(rBack * k1);
    }

    public void setVerticalArm(double speed) {
        verticalArm.setPower(speed * k2);
    }

    public void setSender(double position) {
        sender.setPosition(position);
    }

    public void setHorizontalArm(double speed) {
        horizontalArm.setPower(speed * k3);
    }

    public void tilt(double angle) {
        angleServo.setPosition(angle);
    }

    public void setHand(double position) {
        handServo.setPosition(position);
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
        OptionalDouble maxAbsOptional = DoubleStream.of(values).map(Math::abs).max();
        if (!maxAbsOptional.isPresent())
            return values;
        double maxAbs = maxAbsOptional.getAsDouble();

        double[] scaledValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            scaledValues[i] = Range.scale(values[i], -maxAbs, maxAbs, -1.0, 1.0);
        }

        return scaledValues;
    }

    // fl, fr, bl, br
    public int[] getWheels() {
        return new int[] {
                frontLeft.getCurrentPosition(),
                frontRight.getCurrentPosition(),
                backLeft.getCurrentPosition(),
                backRight.getCurrentPosition()
        };
    }

    // va, ha
    public int[] getArms() {
        return new int[] {
                verticalArm.getCurrentPosition(),
                horizontalArm.getCurrentPosition()
        };
    }

    // sender, as, hs
    public double[] getServos() {
        return new double[] {
                sender.getPosition(),
                angleServo.getPosition(),
                handServo.getPosition()
        };
    }

    public void setArmsPosition(int verticalArmPosition, int horizontalArmPosition) {
        verticalArm.setTargetPosition(verticalArmPosition);
        horizontalArm.setTargetPosition(horizontalArmPosition);
    }

    public void setSpeedMode(SpeedMode mode) {
        switch (mode) {
            case SLOW:
                k1 = 0.1;
                break;
            case FAST:
                k1 = 0.7;
                break;
        }
    }

    enum SpeedMode {
        SLOW,
        FAST
    }
}
