/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.team3405.robot;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.hal.AllianceStationID;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
    public static AutonomousSubsystem autonomousSubsystem;
    public static DriveSubsystem driveSubsystem;

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tv = table.getEntry("tv");
    NetworkTableEntry ledMode = table.getEntry("ledMode");
    NetworkTableEntry camMode = table.getEntry("camMode");
    NetworkTableEntry pipeline0 = table.getEntry("pipeline");
    int targetValue = (int) tv.getDouble(0);
    double horizontalOffset = tx.getDouble(0);
    double verticalOffset = ty.getDouble(0);
    double targetArea = ta.getDouble(0);

    private static int counter = 0;
    //Victor rightDrive1 = new Victor(0);
    Spark rightDrive1;
    Spark rightDrive2;
    public SpeedControllerGroup rightSideDrive;

    //Victor leftDrive1 = new Victor(1);
    Spark leftDrive1;
    Spark leftDrive2;
    public SpeedControllerGroup leftSideDrive;

    DifferentialDrive drive;

    static XboxController controller;

    Spark allThreadMotor;
    Spark winchMotor;
    Spark clawMotor1;
    Spark clawMotor2;

    DigitalInput limitAllThreadUp;
    DigitalInput limitAllThreadDown;
    boolean allThreadMotionUp;
    boolean allThreadMotionDown;
    String gameData;
    Autonomous autoCommand;

    private static final String red1 = AllianceStationID.Red1.toString();
    private static final String red2 = AllianceStationID.Red2.toString();
    private static final String red3 = AllianceStationID.Red3.toString();
    private static final String blue1 = AllianceStationID.Blue1.toString();
    private static final String blue2 = AllianceStationID.Blue2.toString();
    private static final String blue3 = AllianceStationID.Blue3.toString();
    private SendableChooser<String> playerStationChooser = new SendableChooser<>();

    enum Autonomous {
        AUTO1,
        AUTO2,
        AUTO3,
        AUTO4,
        AUTO5,
        AUTO6
    }



    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        limitAllThreadUp = new DigitalInput(1);
        limitAllThreadDown = new DigitalInput(2);

        rightDrive1 = new Spark(0);
        rightDrive2 = new Spark(2);
        rightSideDrive = new SpeedControllerGroup(rightDrive1, rightDrive2);

        leftDrive1 = new Spark(1);
        leftDrive2 = new Spark(3);
        leftSideDrive = new SpeedControllerGroup(leftDrive1, leftDrive2);

        drive = new DifferentialDrive(rightSideDrive, leftSideDrive);

        controller = new XboxController(1);

        allThreadMotor = new Spark(4);
        winchMotor = new Spark(7);
        clawMotor1 = new Spark(4);
        clawMotor2 = new Spark(6);


        autonomousSubsystem = new AutonomousSubsystem();
        driveSubsystem = new DriveSubsystem();

        playerStationChooser.addDefault("Red 2", red2);
        playerStationChooser.addObject("Red 1", red1);
        playerStationChooser.addObject("Red 3", red3);
        playerStationChooser.addObject("Blue 1", blue1);
        playerStationChooser.addObject("Blue 2", blue2);
        playerStationChooser.addObject("Blue 3", blue3);
        SmartDashboard.putData("Player Stations", playerStationChooser);
        gameData = DriverStation.getInstance().getGameSpecificMessage();
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString line to get the auto name from the text box below the Gyro
     *
     * <p>You can add additional auto modes by adding additional comparisons to
     * the switch structure below with additional strings. If using the
     * SendableChooser make sure to add them to the chooser code above as well.
     */
    @Override
    public void autonomousInit() {
        String playerStation = playerStationChooser.getSelected();
        // autoSelected = SmartDashboard.getString("Auto Selector",
        // defaultAuto);
        System.out.println("Player Station: " + playerStation);
        if(playerStation.equals(blue2) && gameData.equals("LRL")) {
            autoCommand = Autonomous.AUTO1;
        }
        if(playerStation.equals(blue3) && gameData.equals("RRR")) {
            autoCommand = Autonomous.AUTO2;
        }

    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        camMode.setNumber(0);
        pipeline0.setNumber(0);
        ledMode.setNumber(0);
        switch (autoCommand) {
            case AUTO1:
                Timer autoTimer = new Timer();
                autoTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        leftSideDrive.set(-0.3);
                        rightSideDrive.set(-0.3);
                        counter++;
                        if(counter > 2 && counter < 3) {
                            leftSideDrive.set(0);
                            rightSideDrive.set(0);
                        } else if(counter >= 3) {
                            if(tv.getDouble(0) == 1.0) {
                                leftSideDrive.set(0.4);
                                rightSideDrive.set(-0.4);
                            }
                            if(ta.getDouble(0) >= 20.0) {
                                leftSideDrive.set(0);
                                rightSideDrive.set(0);
                            }
                            if(tv.getDouble(0) != 1) {
                                leftSideDrive.set(0.3);
                                rightSideDrive.set(0.3);
                            }
                        } else if(counter == 15) {
                            autoTimer.cancel();
                        }
                    }
                },0,1000);
                break;
            case AUTO2:
                // Put default auto code here
                break;
        }
		/*
		Timer autoTimer = new Timer();
		autoTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				leftSideDrive.set(0.5);
				rightSideDrive.set(-0.5);
				counter++;
				if(counter == 2) {
					autoTimer.cancel();
				}
			}
		},0,1000);
		*/
        if(tv.getDouble(0) == 1.0) {
            leftSideDrive.set(0.4);
            rightSideDrive.set(-0.4);
        }
        if(ta.getDouble(0) >= 20.0) {
            leftSideDrive.set(0);
            rightSideDrive.set(0);
        }
        if(tv.getDouble(0) != 1) {
            leftSideDrive.set(-0.3);
            rightSideDrive.set(-0.3);
        }
    }



    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

//    public void oldTeleopPeriodic() {
//
//        ledMode.setNumber(1);
//        camMode.setNumber(1);
//        // Drive
//        double reduceLeftSpeed = 0;
//        double reduceRightSpeed = 0;
//
//        if(controller.getRawAxis(1) < -0.01) {
//            reduceLeftSpeed += controller.getRawAxis(0)*0.225;
//            reduceRightSpeed += controller.getRawAxis(0)*0.225;
//        } else if(controller.getRawAxis(1) > 0.01) {
//            reduceLeftSpeed -= controller.getRawAxis(0)*0.225;
//            reduceRightSpeed -= controller.getRawAxis(0)*0.225;
//        } else {
//            reduceLeftSpeed = 0;
//            reduceRightSpeed = 0;
//        }
//
//        if(controller.getRawAxis(1) != 0 && controller.getRawAxis(0) != 0) {
//            double leftSpeed = 0.5*-controller.getRawAxis(1)+reduceLeftSpeed;
//            double rightSpeed = 0.5*controller.getRawAxis(1)+reduceRightSpeed;
//            leftSideDrive.set(leftSpeed);
//            rightSideDrive.set(rightSpeed);
//        } else {
//            leftSideDrive.set(0);
//            rightSideDrive.set(0);
//        }
//        // Dime turn
//        if(controller.getRawAxis(0) > 0.1 && controller.getRawAxis(0) < 0.1 && controller.getRawAxis(1) < 0.1 && controller.getRawAxis(1) > -0.1) {
//            leftSideDrive.set(0.5*controller.getRawAxis(0));
//            rightSideDrive.set(0.5*-controller.getRawAxis(0));
//        }
//
//
//
//
//        // Winch Control
//        if(controller.getAButton()) {
//            winchMotor.set(0.4);
//        } else if(controller.getBButton()) {
//            winchMotor.set(-0.4);
//        } else if(controller.getXButton()) {
//            winchMotor.set(0.6);
//        } else if(controller.getYButton()) {
//            winchMotor.set(-0.6);
//        } else {
//            winchMotor.set(0);
//        }
//
//        if(limitAllThreadUp.get()) {
//            allThreadMotionUp = false;
//        } else if(!limitAllThreadUp.get()) {
//            allThreadMotionUp = true;
//        }
//
//        if(limitAllThreadDown.get()) {
//            allThreadMotionDown = false;
//        } else if(!limitAllThreadDown.get()) {
//            allThreadMotionDown = true;
//        }
//
//
//
//        // All Thread Control
////		if((controller.getAButton() && controller.getRawAxis(3) == 1.0) && allThreadMotionUp == true) {
////			allThreadMotor.set(0.4);
////		} else if(allThreadMotionUp == false) {
////			allThreadMotor.set(0);
////		} else if((controller.getBButton() && controller.getRawAxis(3) == 1.0) && allThreadMotionDown == true) {
////			allThreadMotor.set(-0.4);
////		} else if(allThreadMotionDown == false) {
////			allThreadMotor.set(0);
////		} else if((controller.getXButton() && controller.getRawAxis(3) == 1.0) && allThreadMotionUp == true) {
////			allThreadMotor.set(0.6);
////		} else if(allThreadMotionDown == false) {
////			allThreadMotor.set(0);
////		} else if((controller.getYButton() && controller.getRawAxis(3) == 1.0) && allThreadMotionDown == true) {
////			allThreadMotor.set(-0.6);
////		} else if(allThreadMotionDown == false) {
////			allThreadMotor.set(0);
////		} else {
////			allThreadMotor.set(0);
////		}
//
//        if(controller.getAButton()) {
//            allThreadMotor.set(0.4);
//        } else if(controller.getBButton()) {
//            allThreadMotor.set(-0.4);
//        } else if(controller.getXButton()) {
//            allThreadMotor.set(0.6);
//        } else if(controller.getYButton()) {
//            allThreadMotor.set(-0.6);
//        } else {
//            allThreadMotor.set(0);
//        }
//
//        //Cube Intake
//        if(controller.getRawButton(6)) {
//            clawMotor1.set(0.5);
//            clawMotor2.set(0.5);
//        } else if(controller.getRawButton(5)) {
//            clawMotor1.set(-0.5);
//            clawMotor2.set(-0.5);
//        } else {
//            clawMotor1.set(0);
//            clawMotor2.set(0);
//        }
//
//		/*
//		boolean leftStickY = false;
//		boolean leftStickX = false;
//
//		if(controller.getRawAxis(1) > 0.1 || controller.getRawAxis(1) < -0.1) {
//			leftStickY = true;
//		} else {
//			leftStickY = false;
//		}
//
//		if(controller.getRawAxis(0) > 0.1 || controller.getRawAxis(0) < -0.1) {
//			leftStickX = true;
//		} else {
//			leftStickX = false;
//		}
//		*/
//		/*
//		if(leftStick == true || rightStick == true) {
//			leftSideDrive.set(controller.getRawAxis(5));
//			rightSideDrive.set(-controller.getRawAxis(1));
//		} else if(rightStick == false && leftStick == false) {
//			leftSideDrive.set(0);
//			rightSideDrive.set(0);
//		}
//		*/
//
//        // double reduceSpeedLeft = 0;
//        // double reduceSpeedRight = 0;
//
//	/*
//
//		if(controller.getRawAxis(0) < -0.1) {
//			leftSideDrive.set(controller.getRawAxis(0));
//			rightSideDrive.set(controller.getRawAxis(0));
//		}else if(controller.getRawAxis(0) > 0.1) {
//			leftSideDrive.set(-controller.getRawAxis(0));
//			rightSideDrive.set(-controller.getRawAxis(0));
//		}else if(leftStickY == true) {
//			leftSideDrive.set(-controller.getRawAxis(1));
//			rightSideDrive.set(controller.getRawAxis(1));
//		}else if(leftStickY == true && leftStickX == true) {
//			leftSideDrive.set(-controller.getRawAxis(1)-controller.getRawAxis(4));
//			rightSideDrive.set(controller.getRawAxis(1)-controller.getRawAxis(4));
//		}else {
//			leftSideDrive.set(0);
//			rightSideDrive.set(0);
//		}
//		*/
//
//
//
//
//    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }
}

class AutonomousSubsystem extends Subsystem {

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new AutonomousCommand());
    }
}

class DriveSubsystem extends Subsystem {

    private Spark frontRight = new Spark(0);
    private Spark frontLeft = new Spark(1);
    private Spark backLeft = new Spark(3);
    private Spark backRight = new Spark(2);

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new DriveCommand());
    }

    public void arcadeDrive() {
        Double x = Robot.controller.getRawAxis(0);
        Double y = Robot.controller.getRawAxis(1);
        Double maxOutput = .7;
        Double left = (y - x) * maxOutput;
        Double right = (y + x) * maxOutput;

        frontRight.set(right);
        backRight.set(right);

        frontLeft.set(-left);
        backLeft.set(-left);
    }
}

class AutonomousCommand extends Command {

    @Override
    public void execute() {

    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}

class DriveCommand extends Command {

    DriveCommand() {
        requires(Robot.driveSubsystem);
    }

    @Override
    public void execute() {
        Robot.driveSubsystem.arcadeDrive();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}