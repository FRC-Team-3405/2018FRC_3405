package frc.team3405.robot

import com.ctre.CANTalon
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.Talon

class SpeedTalon(deviceNumber: Int) : TalonSRX(deviceNumber), SpeedController {
    override fun set(speed: Double) {
        set(ControlMode.PercentOutput, speed)
    }

    override fun pidWrite(output: Double) {
        set(output)
    }

    override fun stopMotor() {
        set(ControlMode.PercentOutput, 0.0)

    }

    override fun get(): Double {
        return this.motorOutputPercent
    }

    override fun disable() {
        stopMotor()
    }

}