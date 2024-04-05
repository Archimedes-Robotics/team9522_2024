// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
//import frc.robot.subsystems.PWMDrivetrain;

import frc.robot.subsystems.CANDrivetrain;
import frc.robot.Constants.LauncherConstants;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.subsystems.CANLauncher;

public final class Autos {
  public static Command TaxiCurve(CANDrivetrain drivetrain) {
    return new RunCommand(() -> {drivetrain.tankDrive(-0.6, -0.8);}, drivetrain)
      .withTimeout(3)
      .andThen(() -> drivetrain.tankDrive(0, 0));
  }

  public static Command TaxiStraight(CANDrivetrain drivetrain) {
    return new RunCommand(() -> {drivetrain.tankDrive(0.75, 0.75);}, drivetrain)
      .withTimeout(1.5)
      .andThen(() -> drivetrain.tankDrive(0, 0));
  }

  public static Command ShootnWait(CANLauncher m_launcher) {
    return Commands.sequence(
       new PrepareLaunch(m_launcher)
                 .withTimeout(LauncherConstants.kLauncherDelay)
                 .andThen(new LaunchNote(m_launcher)).withTimeout(2)
                 .andThen(() -> m_launcher.stop()),
                 Commands.waitSeconds(9.5)
       );
  }
  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}

//Blue Source or Red Amp Commands.run(() -> m_drivetrain.arcade(-0.65, 0.45), m_drivetrain).withTimeout(1.5)
                 //Blue Amp or Red Source 
                 //Commands.run(() -> m_drivetrain.arcade(-0.65, -0.45), m_drivetrain).withTimeout(1.5)
                 //.andThen(() -> m_drivetrain.arcade(0, 0), m_drivetrain)
