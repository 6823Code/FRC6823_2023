package frc.robot;

import java.util.List;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.SwerveDriveSubsystem;
import frc.robot.util.Constants;

public class PathHandler {

    //path handler implementation heavily "influenced" by 2930 Sonic Squirrels
    private SwerveDriveSubsystem swerveDriveSubsystem;
    private PathConstraints constraints;

    public PathHandler(SwerveDriveSubsystem swerveDriveSubsystem)
    {
        this.swerveDriveSubsystem = swerveDriveSubsystem;
        
        constraints = new PathConstraints(Constants.kMaxVelocity, Constants.kMaxAccel);
    }

    public Command TeleopScore(boolean dir){
        TrajectoryConfig trajectoryConfig = new TrajectoryConfig(Constants.kMaxVelocity, Constants.kMaxAccel);
        if(dir){
            trajectoryConfig.setReversed(true);
        }

        /*double x = swerveDriveSubsystem.getRobotPose().getX();
        double y = swerveDriveSubsystem.getRobotPose().getY();
        Rotation2d heading = swerveDriveSubsystem.getRobotPose().getRotation();*/

        Trajectory trajectory = TrajectoryGenerator.generateTrajectory
            (new Pose2d(0, 0, new Rotation2d(0)), 

            List.of(), 

             new Pose2d(0.47, 0, new Rotation2d(0)), trajectoryConfig);
        
        PIDController xController = new PIDController(Constants.kP, 0.000, 0);
        PIDController yController = new PIDController(Constants.kP, 0.000, 0);
        ProfiledPIDController turnController = new ProfiledPIDController(Constants.kPThetaController, Constants.kIThetaController, Constants.kDThetaController, Constants.kTurnControlConstraints);
        turnController.enableContinuousInput(Math.PI, Math.PI);
     
        SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
            trajectory, 
            swerveDriveSubsystem::getRobotPose, 
            swerveDriveSubsystem.getKinematics(), 
            xController, yController, turnController, 
            swerveDriveSubsystem::setSwerveModuleStates, 
            swerveDriveSubsystem);

        return new SequentialCommandGroup(new InstantCommand(() -> swerveDriveSubsystem.resetPose()), swerveControllerCommand, new InstantCommand(() -> swerveDriveSubsystem.brake()));
    }

    public Command balanceAuto(){
        PathPlannerTrajectory path = PathPlanner.loadPath("Balance Auto", constraints);
        
        return PPSwerveControlCommand(path, true).beforeStarting(new InstantCommand(() -> swerveDriveSubsystem.setPose(1.88, 3.27, 0)));
    }

    public Command getPath(int node, int piece, boolean reverse){
        PathPlannerTrajectory path;

        if (reverse){
            path = PathPlanner.loadPath(Constants.alliance + " " + node + "," + piece +"r", constraints);
        }
        else{
            path = PathPlanner.loadPath(Constants.alliance + " " + node + "," + piece, constraints);
        }
        
        if (path != null){
            return PPSwerveControlCommand(path, true).beforeStarting(new InstantCommand(() -> swerveDriveSubsystem.setPose(1.84, 1.06, 0)));
        }
        return new WaitCommand(15);
    }


    public Command PPSwerveControlCommand(PathPlannerTrajectory path, boolean stopAtEnd){

        PIDController xController = new PIDController(Constants.kP, 0, 0);
        PIDController yController = new PIDController(Constants.kP, 0, 0);
        PIDController turnController = new PIDController(Constants.kPThetaController, Constants.kIThetaController, Constants.kDThetaController);
        turnController.enableContinuousInput(Math.PI, Math.PI);
        ProfiledPIDController thetaController = new ProfiledPIDController(Constants.kPThetaController, Constants.kIThetaController, Constants.kDThetaController, Constants.kTurnControlConstraints);

        Command swerveControllerCommand = new SwerveControllerCommand(
            path, swerveDriveSubsystem::getRobotPose, swerveDriveSubsystem.getKinematics(),
            xController, yController, thetaController, 
            swerveDriveSubsystem::setSwerveModuleStates, swerveDriveSubsystem);
        
        if (stopAtEnd){
            swerveControllerCommand = swerveControllerCommand.andThen(new InstantCommand(() -> swerveDriveSubsystem.brake()));
        }

        return swerveControllerCommand;
    }

}