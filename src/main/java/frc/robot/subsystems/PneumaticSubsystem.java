package frc.robot.subsystems;

import javax.lang.model.util.ElementScanner14;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
//import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class PneumaticSubsystem {
    private Compressor compressor;
    private DoubleSolenoid intakeSolenoid;
    //private Solenoid closeSolenoid;

    public PneumaticSubsystem()
    {
        compressor = new Compressor(29, PneumaticsModuleType.REVPH);
        intakeSolenoid = new DoubleSolenoid(29, PneumaticsModuleType.REVPH, 0, 1);
        intakeSolenoid.set(Value.kOff);
        //openSolenoid = new Solenoid(PneumaticsModuleType.REVPH, 0);
        //closeSolenoid = new Solenoid(PneumaticsModuleType.REVPH, 0);
    }

    public void periodic()
    {
        compressor.enableDigital();

        
        
    }

    public void setPneumaticState(int state)
    {
        if (state == 1) {
            intakeSolenoid.set(Value.kForward);
        }
        else if (state == 0) {
            intakeSolenoid.set(Value.kReverse);
        }
        else{
            intakeSolenoid.set(Value.kOff);
            intakeSolenoid.close();
        }
    }

    /*public void setIntakeState()
    {
        
    }*/

}
