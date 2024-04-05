// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.LauncherConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.LaunchNote;
import frc.robot.commands.PrepareLaunch;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

//import frc.robot.subsystems.PWMDrivetrain;
//import frc.robot.subsystems.PWMLauncher;

import frc.robot.subsystems.CANDrivetrain;
import frc.robot.subsystems.CANLauncher;


/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  //private static final double turnSpeed = 0.5; // Adjust this value as needed
  // The robot's subsystems are defined here.
  //private final PWMDrivetrain m_drivetrain = new PWMDrivetrain();
  private final CANDrivetrain m_drivetrain = new CANDrivetrain();
  //private final PWMLauncher m_launcher = new PWMLauncher();
  private final CANLauncher m_launcher = new CANLauncher();

  /*The gamepad provided in the KOP shows up like an XBox controller if the mode switch is set to X mode using the
   * switch on the top.*/
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final CommandXboxController m_operatorController =
      new CommandXboxController(OperatorConstants.kOperatorControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();
    Robot.ph.enableCompressorAnalog(100, 120);
    Robot.doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be accessed via the
   * named factory methods in the Command* classes in edu.wpi.first.wpilibj2.command.button (shown
   * below) or via the Trigger constructor for arbitary conditions
   */
  public void configureBindings() {
    // Set the default command for the drivetrain to drive using the joysticks
    m_drivetrain.setDefaultCommand(
        new RunCommand(
            () ->
                //m_drivetrain.arcadeDrive(
                    //-m_driverController.getLeftY(), -m_driverController.getRightX()), m_drivetrain));
                m_drivetrain.tankDrive(
                    -Math.pow((m_driverController.getLeftY()), 3)*(m_driverController.getHID().getRightBumper()?1:.5), 
                    -Math.pow((m_driverController.getRightY()),3 )*(m_driverController.getHID().getRightBumper()?1:.5)), 
                m_drivetrain
                ));

    /*Create an inline sequence to run when the operator presses and holds the A (green) button. Run the PrepareLaunch
     * command for 1 seconds and then run the LaunchNote command */
    m_operatorController.x().whileTrue(
            new PrepareLaunch(m_launcher)
                .withTimeout(LauncherConstants.kLauncherDelay)
                .andThen(new LaunchNote(m_launcher))
                .handleInterrupt(() -> m_launcher.stop()));

    // Set up a binding to run the intake command while the operator is pressing and holding the
    // left Bumper
    m_operatorController.leftBumper().whileTrue(m_launcher.getIntakeCommand());

    Command raiseArmsCommand = new RunCommand(() -> Robot.doubleSolenoid.set(DoubleSolenoid.Value.kReverse));
    Command lowerArmsCommand = new RunCommand(() -> Robot.doubleSolenoid.set(DoubleSolenoid.Value.kForward));
    m_operatorController.y().whileTrue(raiseArmsCommand);
    m_operatorController.a().whileTrue(lowerArmsCommand);

    //Establishes command in the file
    Command turnCounterClockwiseCommand = new RunCommand(() -> m_drivetrain.turnCounterClockwise(), m_drivetrain);
    Command turnClockwiseCommand = new RunCommand(() -> m_drivetrain.turnClockwise(), m_drivetrain);
  
    // Create a JoystickButton object for the 'x' button
    JoystickButton xButton = new JoystickButton(m_driverController.getHID(), 3); 
    // Create a JoystickButton object for the 'b' button
    JoystickButton bButton = new JoystickButton(m_driverController.getHID(), 2);

    // Run the turnCounterClockwiseCommand while the 'x' button is being pressed
    xButton.whileTrue(turnCounterClockwiseCommand);
    // Run the turnClockwiseCommand while the 'b' button is being pressed
    bButton.whileTrue(turnClockwiseCommand);

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    // return Autos.Auto1(m_drivetrain);
    return Commands.sequence(
      new PrepareLaunch(m_launcher)
                .withTimeout(LauncherConstants.kLauncherDelay)
                .andThen(new LaunchNote(m_launcher)).withTimeout(1)
                .andThen(() -> m_launcher.stop()),
                Commands.waitSeconds(10)
                //Commands.run(() -> m_drivetrain.tankDrive(-0.75, -0.75), m_drivetrain).withTimeout(1.25)
                //Blue Source or Red Amp Commands.run(() -> m_drivetrain.arcade(-0.65, 0.45), m_drivetrain).withTimeout(1.5)
                //Blue Amp or Red Source 
                //Commands.run(() -> m_drivetrain.arcade(-0.65, -0.45), m_drivetrain).withTimeout(1.5)
                //.andThen(() -> m_drivetrain.arcade(0, 0), m_drivetrain)
      );
  }
}
