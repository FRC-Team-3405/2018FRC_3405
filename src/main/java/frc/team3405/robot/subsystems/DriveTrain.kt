package frc.team3405.robot.subsystems

import edu.wpi.first.wpilibj.RobotDrive
import edu.wpi.first.wpilibj.Talon
import edu.wpi.first.wpilibj.command.Subsystem
import edu.wpi.first.wpilibj.drive.MecanumDrive
import edu.wpi.first.wpilibj.drive.RobotDriveBase
import frc.team3405.robot.Robot
import frc.team3405.robot.commands.DriveCommand

class DriveTrain : Subsystem() {
    private val frontLeft: Talon = Talon(0)
    private val frontRight: Talon = Talon(1)
    private val backRight: Talon = Talon(2)
    private val backLeft: Talon = Talon(3)
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

        frontRight.set(right)
        backRight.set(right)

        frontLeft.set(-left)
        backLeft.set(-left)
    }


    fun mechanumDrive() {
        robotDrive.driveCartesian(Robot.joystick.leftX, Robot.joystick.leftY, Robot.joystick.rightX)
    }

}

