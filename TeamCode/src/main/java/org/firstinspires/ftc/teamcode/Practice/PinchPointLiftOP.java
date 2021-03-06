/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
//testing on robotics computer
package org.firstinspires.ftc.teamcode.Practice;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.util.Range;



@TeleOp(name="PinchPointLiftOP", group="Iterative Opmode")

public class PinchPointLiftOP extends OpMode
{
    private PinchPointRobot      robot;
    private AdjustableIntake    intake;
    private Auto2               auto;

    boolean hasRunInit;
    private boolean IntakeOpen, Grip, runningAuto;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        //make the robot
        robot = new PinchPointRobot(hardwareMap, telemetry);
        intake = new AdjustableIntake(hardwareMap, telemetry, 0.1, 0.3, 0.8, 0.3, 0.5, 1);
        auto = new Auto2(robot);

        IntakeOpen = true;
        Grip = true;
        intake.fullOpen();

        telemetry.addData("Status", "Initialized");
        hasRunInit = false;
        runningAuto = true;

    }




    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData("Status:", "Maybe we could put an auto here, just call the loop here.");
        if(!hasRunInit)
        {
            robot.initVuf();
            hasRunInit = true;
        }



    }




    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        telemetry.addData("Status:", "we could put other stuff in here, like ");
        auto.startTime();


    }



    private boolean prev2a,         prev2lb;

    private int     intakeState;

    private void autoLoop(){//a loop for the autonomous period
        auto.loop();
    }

    private void driverLoop(){//a loop for the driver control period

        robot.drive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

        telemetry.addData("X pos", robot.getCurrentPos().x);
        telemetry.addData("Y pos", robot.getCurrentPos().y);
        telemetry.addData("Z pos", robot.getCurrentPos().z);

        robot.lift(gamepad2.left_stick_y);


        if(gamepad2.a && !prev2a) {
            if (IntakeOpen)
            {
                intake.storeArms();
                intakeState = 0;
                IntakeOpen = false;
//                robot.liftGrip();
            }
            else
            {
                intake.fullOpen();
                IntakeOpen = true;
            }
        }
        prev2a = gamepad2.a;

        if(IntakeOpen) {
            intake.shiftLeft(gamepad2.right_trigger);
            intake.shiftRight(gamepad2.right_trigger);
        }

        if(gamepad2.dpad_up){
            robot.gripUp();
        }
        if(gamepad2.dpad_down){
            robot.gripDown();
        }

        if(gamepad2.left_bumper && !prev2lb && IntakeOpen) {

            if(Grip)
            {
                robot.ungrip();
                Grip = !Grip;
            }
            else //if(!Grip) <- don't know why this is necessary. Is this supposed to help if Grip is Undefined?
            {
                robot.grip();
                Grip = !Grip;

            }
        }
        prev2lb = gamepad2.left_bumper;


        if(gamepad2.b)
            intakeState = 3;
        else if(gamepad2.x)
            intakeState = 2;
        else if(gamepad2.y)
            intakeState = 1;

        switch(intakeState)
        {
            case 0:
                intake.stopIntake();
                break;

            case 1:
                intake.intake();
                break;

            case 2:
                intake.rotateLeft();
                break;

            case 3:
                intake.rotateRight();
                break;

            case 4:     ///TODO find a way to activate this
                intake.outtake();
                break;


            default:
                intake.stopIntake();
                break;
        }
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        /*currently used buttons gamepad 1:
         * left_stick
         * right_stick
         * */
        /*currently used buttons gamepad 2:
        * a
        * right_trigger
        * left_bumper
        * b
        * left_stick
        * right_stick
        * x
        * y
        * */

        telemetry.addData("Status", "Driving, I hope");

        telemetry.addData("leftGrip", robot.leftGrip.getPosition());
        telemetry.addData("rightGrip", robot.rightGrip.getPosition());

        robot.updateSensors();

        //runs either the driver control or the autonomous loop
        if(runningAuto){
            autoLoop();
            if(gamepad1.start&&gamepad1.back){//if the driver presses both start and back, exit autonomous and start driver control
                runningAuto = false;
            }
        }
        else
        {
            driverLoop();
        }

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
