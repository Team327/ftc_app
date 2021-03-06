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

         package org.firstinspires.ftc.teamcode.Practice;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.util.Range;



@TeleOp(name="ConveyorOp", group="Iterative Opmode")

public class ConveyorOp extends OpMode
{
    private ConveyorBot         robot;
    private AdjustableIntake    intake;


    private Auto1               auto;


    boolean hasRunInit;
    private boolean IntakeOpen, runningAuto;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {


        telemetry.addData("Status", "Initializing");
        //make the robot

        robot = new ConveyorBot(hardwareMap, telemetry);
        auto = new Auto1(robot);

        intake = new AdjustableIntake(hardwareMap, telemetry, 0.3, 0.5, 0.92, 0.30, 0.55, 0.98);

        IntakeOpen = true;
        intake.fullOpen();

        telemetry.addData("Status", "Initialized");

        telemetry.log().add("Done");
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
        auto.startTime();
        telemetry.addData("Status:", "we could put other stuff in here, like ");



    }



    private boolean prev2a;
    private int     intakeState;

    private void autoLoop(){ // a loop for autonomous mode
            auto.loop();
    }

    private void driverLoop(){ //a loop for driver control mode
        robot.drive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

        if(gamepad2.right_bumper) {
            robot.convey(-gamepad2.right_stick_y/4);
        }
        else
        {
            robot.convey(-gamepad2.right_stick_y);
        }


        if(gamepad2.a && !prev2a) {
            if (IntakeOpen)
            {
                intake.storeArms();
                intakeState = 0;
                IntakeOpen = false;
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


        if(gamepad2.dpad_down)
            robot.unflip();
        else if(gamepad2.dpad_up)
            robot.flip();
        else
            robot.stopFlip();


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





        telemetry.addData("Status", "Driving");
        telemetry.addData("VuMark", robot.updateSensors());

        robot.updateSensors();
        //runs either the driver control loop or the autonomous loop
        if(runningAuto){
            autoLoop();
            if(gamepad1.start&&gamepad1.back){//if you press start and back at the same time, stop the autonomous loop
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
