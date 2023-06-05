package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class AutoMove {

    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    public Servo angleServo;

    public int[] positionsOG;
    public int[] position;

    public void init(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        angleServo = hardwareMap.get(Servo.class, "AS");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        angleServo.setPosition(0.6);

        positionsOG = new int[] {
                frontLeft.getCurrentPosition(),
                frontRight.getCurrentPosition(),
                backLeft.getCurrentPosition(),
                backRight.getCurrentPosition()
        };
    }

    public void updatePos() {
        position = new int[] {
                frontLeft.getCurrentPosition(),
                frontRight.getCurrentPosition(),
                backLeft.getCurrentPosition(),
                backRight.getCurrentPosition()
        };
    }

    public boolean moved() {
        return !(
                positionsOG[0] == position[0] &&
                positionsOG[1] == position[1] &&
                positionsOG[2] == position[2] &&
                positionsOG[3] == position[3]
                );
    }

    public void one() {
        frontLeft.setTargetPosition(positionsOG[0] + 700);
        frontRight.setTargetPosition(positionsOG[1] + 700);
        backLeft.setTargetPosition(positionsOG[2] - 700);
        backRight.setTargetPosition(positionsOG[3] - 700);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(-0.5);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        frontLeft.setTargetPosition(positionsOG[0] + 700 - 1300);
        frontRight.setTargetPosition(positionsOG[1] + 700 + 1300);
        backLeft.setTargetPosition(positionsOG[2] - 700 - 1300);
        backRight.setTargetPosition(positionsOG[3] - 700 + 1300);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
    }

    public void two() {
        frontLeft.setTargetPosition(positionsOG[0] - 1300);
        frontRight.setTargetPosition(positionsOG[1] + 1300);
        backLeft.setTargetPosition(positionsOG[2] - 1300);
        backRight.setTargetPosition(positionsOG[3] + 1300);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
    }

    public void three() {
        frontLeft.setTargetPosition(positionsOG[0] - 600);
        frontRight.setTargetPosition(positionsOG[1] - 600);
        backLeft.setTargetPosition(positionsOG[2] + 600);
        backRight.setTargetPosition(positionsOG[3] + 600);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(-0.5);
        frontRight.setPower(-0.5);
        backLeft.setPower(0.5);
        backRight.setPower(0.5);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        frontLeft.setTargetPosition(positionsOG[0] - 2000);
        frontRight.setTargetPosition(positionsOG[1] + 400);
        backLeft.setTargetPosition(positionsOG[2] - 400);
        backRight.setTargetPosition(positionsOG[3] + 2000);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
    }
}
