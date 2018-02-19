package frc.team3405.robot.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Talon
import edu.wpi.first.wpilibj.command.Subsystem
import edu.wpi.first.wpilibj.drive.MecanumDrive
import frc.team3405.robot.Robot
import frc.team3405.robot.commands.DriveCommand

class DriveTrain : Subsystem() {
    //Must install Talon library with CTRE phoenix tools.
    private val frontLeft: TalonSRX = TalonSRX(2)
    private val frontRight: TalonSRX = TalonSRX(3)
    private val backRight: TalonSRX = TalonSRX(0)
    private val backLeft: TalonSRX = TalonSRX(1)
    //private val robotDrive: MecanumDrive = MecanumDrive(frontLeft, backLeft, frontRight, backRight)

    override fun initDefaultCommand() {
        defaultCommand = DriveCommand()
    }

    fun arcadeDrive() {
        val x = Robot.joystick.leftX
        val y = Robot.joystick.leftY
        val maxOutput = .7
        val left: Double = (y - x) * maxOutput
        val right: Double = (y + x) * maxOutput

//        frontRight.set(right)
//        backRight.set(right)
//
//        frontLeft.set(-left)
//        backLeft.set(-left)
    }


    fun mechanumDrive() {
//        robotDrive.driveCartesian(Robot.joystick.leftX, Robot.joystick.leftY, Robot.joystick.rightX)
        //robotDrive.driveCartesian(1.0, 1.0, 0.0)
        ControlMode.MotionProfileArc
        frontRight.set(ControlMode.PercentOutput, 1.0)
        frontLeft.set(ControlMode.PercentOutput, 1.0)
        backLeft.set(ControlMode.PercentOutput, 1.0)
        backRight.set(ControlMode.PercentOutput, 1.0)
        System.out.println("Move")
    }

}

