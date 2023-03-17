package frc.robot.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

//import edu.wpi.first.wpilibj2.command.Subsystem;
//import java.util.HashSet;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.JoystickHandler;
import frc.robot.Pigeon2Handler;
import frc.robot.subsystems.SwerveDriveSubsystem;

public class FieldSpaceDrive extends CommandBase {
    //Declare subsystem, Joystick Handler, pigeon2
    private SwerveDriveSubsystem swerveDrive;
    private JoystickHandler joystickHandler;
    private Pigeon2Handler pigeon2Handler;
    private SimpleWidget speedRateWidget;
    private SimpleWidget turnRateWidget;
    private boolean drive, slowMode;

    public FieldSpaceDrive(SwerveDriveSubsystem subsystem, 
    JoystickHandler joystickHandler, Pigeon2Handler pigeon2Handler) {
        //Instantiate subsystem, Joystick Handler, pigeon2
        this.swerveDrive = subsystem;
        this.joystickHandler = joystickHandler;
        this.pigeon2Handler = pigeon2Handler;
        this.speedRateWidget = Shuffleboard.getTab("Preferences").addPersistent("Speed Rate", 0.5)
        .withWidget(BuiltInWidgets.kNumberSlider);
        this.turnRateWidget = Shuffleboard.getTab("Preferences").addPersistent("Turn Rate", 0.5)
        .withWidget(BuiltInWidgets.kNumberSlider);
        addRequirements(swerveDrive);
        drive = true;
        slowMode = false;
    }

    @Override
    public void execute() {
        pigeon2Handler.printEverything();
        joystickHandler.updateDeadZone();

        //Set speed and turn rates for full throttle and not full throttle
        double speedRate = speedRateWidget.getEntry().getDouble(1);
        double turnRate = turnRateWidget.getEntry().getDouble(1);
        double modeMultiplier = 1;

        if (slowMode){
            modeMultiplier = 0.4;
        }

        // if (joystickHandler.isFullThrottle()) {
        //     speedRate = 1;
        //     turnRate = .6;
        // }

        //Set xval, yval, spinval to the scaled values from the joystick, bounded on [-1, 1]
        double xval = joystickHandler.getAxis1() * -speedRate * 5 * modeMultiplier;
        double yval = joystickHandler.getAxis0() * -speedRate * 5 * modeMultiplier;
        double spinval = joystickHandler.getAxis5() * -turnRate * 5 * modeMultiplier;

        // mapping field space to robot space
        //double txval = getTransX(xval, yval, robotAngle);
        //double tyval = getTransY(xval, yval, robotAngle);
        if (drive){
            swerveDrive.drive(ChassisSpeeds.fromFieldRelativeSpeeds(xval, yval, spinval, getRobotAngle()));
        }
    }

    public Rotation2d getRobotAngle()
    {
        return pigeon2Handler.getAngleDeg();
    }

    public void zero() { //Zeroes direction
        pigeon2Handler.zeroYaw();
    }
    public void drive(boolean drive)
    {
        this.drive = drive;
    }

    public void toggleSlowmo(){
        slowMode = !slowMode;
    }
}