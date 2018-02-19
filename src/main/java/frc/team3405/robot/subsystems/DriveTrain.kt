package frc.team3405.robot.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.Talon
import edu.wpi.first.wpilibj.command.Subsystem
import edu.wpi.first.wpilibj.drive.MecanumDrive
import frc.team3405.robot.Robot
import frc.team3405.robot.SpeedTalon
import frc.team3405.robot.commands.DriveCommand

class DriveTrain : Subsystem() {
    //Must install Talon library with CTRE phoenix tools.
    private val frontLeft: SpeedTalon = SpeedTalon(2)
    private val frontRight: SpeedTalon = SpeedTalon(3)
    private val backRight: SpeedTalon = SpeedTalon(0)
    private val backLeft: SpeedTalon = SpeedTalon(1)
    private val robotDrive: MecanumDrive = MecanumDrive(frontLeft, backLeft, frontRight, backRight)

    override fun initDefaultCommand() {
        defaultCommand = DriveCommand()
    }

    fun arcadeDrive() {
        val x = Robot.joystick.leftX
        val y = Robot.joystick.leftY
        val maxOutput = .7
        val left: Double = (y - x) * maxOutput
        val right: Double = (y + x) * maxOutput


    }


    fun mecanumDrive() {
        robotDrive.driveCartesian(Robot.joystick.leftX, Robot.joystick.leftY, Robot.joystick.rightX)
    }

}

