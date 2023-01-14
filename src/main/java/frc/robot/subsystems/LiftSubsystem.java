package frc.robot.subsystems;

import java.util.HashSet;
import java.util.Map;
import com.revrobotics.CANSparkMax;
//import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
//import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LiftSubsystem extends SubsystemBase {

    private CANSparkMax leftLiftMotor;
    private CANSparkMax rightLiftMotor;
    private SimpleWidget leftPowWidget;
    private SimpleWidget rightPowWidget;
    private double leftLiftPower;
    private double rightLiftPower;

    public LiftSubsystem() {
        this.leftLiftMotor = new CANSparkMax(16, CANSparkMax.MotorType.kBrushed);
        this.rightLiftMotor = new CANSparkMax(17, CANSparkMax.MotorType.kBrushed);
        leftPowWidget = Shuffleboard.getTab("Preferences").add("Left Lift Power", 1)
        .withWidget(BuiltInWidgets.kNumberSlider)
        .withProperties(Map.of("min", -1, "max", 1));
        rightPowWidget = leftPowWidget = Shuffleboard.getTab("Preferences").add("Right Lift Power", 1)
        .withWidget(BuiltInWidgets.kNumberSlider)
        .withProperties(Map.of("min", -1, "max", 1));
        periodic();

        SendableRegistry.addChild(this, leftLiftMotor);
        SendableRegistry.addChild(this, rightLiftMotor);
        SendableRegistry.addLW(this, "Lift");

    }

    public HashSet<Subsystem> liftDown() {
        HashSet<Subsystem> tree = new HashSet<Subsystem>();
        tree.add(this);
        leftLiftMotor.set(-leftLiftPower);
        rightLiftMotor.set(-rightLiftPower);
        return tree;
    }
    
    public CommandBase liftUp() {
        
        return this.runOnce( () -> {
            leftLiftMotor.set(leftLiftPower);
            rightLiftMotor.set(rightLiftPower);
            } 
        );
    }

    public CommandBase leftUp(){
        return this.runOnce(() -> {
        leftLiftMotor.set(leftLiftPower);
        }
        );
    }

    public HashSet<Subsystem> leftDown(){
        HashSet<Subsystem> tree = new HashSet<Subsystem>();
        tree.add(this);
        leftLiftMotor.set(-leftLiftPower);
        return tree;
    }

    public HashSet<Subsystem> rightUp(){
        HashSet<Subsystem> tree = new HashSet<Subsystem>();
        tree.add(this);
        rightLiftMotor.set(rightLiftPower);
        return tree;
    }

    public HashSet<Subsystem> rightDown(){
        HashSet<Subsystem> tree = new HashSet<Subsystem>();
        tree.add(this);
        rightLiftMotor.set(-rightLiftPower);
        return tree;
    }

    public CommandBase liftStop() {
        return this.runOnce(() ->{        
        leftLiftMotor.set(0);
        rightLiftMotor.set(0);
        });
    }

    @Override
    public void periodic(){
        leftLiftPower = leftPowWidget.getEntry().getDouble(1);
        rightLiftPower = rightPowWidget.getEntry().getDouble(1);
    }
}
