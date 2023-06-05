package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadStorage {
    public boolean x;
    public boolean y;
    public boolean b;

    public void store(Gamepad gamepad) {
        this.x = gamepad.x;
        this.y = gamepad.y;
        this.b = gamepad.b;
    }
}
