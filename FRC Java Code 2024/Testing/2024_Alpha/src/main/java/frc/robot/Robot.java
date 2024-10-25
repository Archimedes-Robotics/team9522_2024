package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import frc.robot.subsystems.DriveSubsystem;

public class Robot extends TimedRobot {
    private Command m_autonomousCommand;
    private RobotContainer m_robotContainer;
    private final Field2d m_field = new Field2d();
    private final PowerDistribution m_pdh = new PowerDistribution(1, ModuleType.kRev);

    // Variable to hold the max speed value
    private double maxSpeedMetersPerSecond = Constants.DriveConstants.kMaxSpeedMetersPerSecond;

    @Override
    public void robotInit() {
        m_robotContainer = new RobotContainer();
        CameraServer.startAutomaticCapture();
        SmartDashboard.putData("Field", m_field);
        setupSmartDashboard();
    }

    @Override
    public void robotPeriodic() {
        updateSmartDashboard();
        // Update the max speed value from SmartDashboard
        maxSpeedMetersPerSecond = SmartDashboard.getNumber("Max Speed Meters Per Second", maxSpeedMetersPerSecond);
    }

    @Override
    public void autonomousInit() {
      m_autonomousCommand = m_robotContainer.getAutonomousCommand();

      if (m_autonomousCommand != null) {
       m_autonomousCommand.schedule();
      }
    }

    private void setupSmartDashboard() {
        DriveSubsystem driveSubsystem = m_robotContainer.getDriveSubsystem();
        XboxController m_driverController = m_robotContainer.getDriverController();
        SmartDashboard.putBoolean("Controller Status", m_driverController.isConnected());
        SmartDashboard.putData("Swerve Drive", createSwerveDriveSendable(driveSubsystem));
        // Initialize the max speed value on the SmartDashboard
        SmartDashboard.putNumber("Max Speed Meters Per Second", maxSpeedMetersPerSecond);
    }

    private void updateSmartDashboard() {
        boolean isSetXButtonPressed = m_robotContainer.getDriverController().getRawButton(Constants.kSetXButton);
        SmartDashboard.putBoolean("SetX", isSetXButtonPressed);
        // Update PDH voltage
        SmartDashboard.putNumber("PDH Voltage", m_pdh.getVoltage());
        // Update robot velocity
        DriveSubsystem driveSubsystem = m_robotContainer.getDriveSubsystem();
        double robotVelocity = driveSubsystem.getRobotVelocity();
        SmartDashboard.putNumber("Robot Velocity", robotVelocity);
    }

    private Sendable createSwerveDriveSendable(DriveSubsystem driveSubsystem) {
        return new Sendable() {
            @Override
            public void initSendable(SendableBuilder builder) {
                builder.setSmartDashboardType("SwerveDrive");
                for (int i = 0; i < 4; i++) {
                    final int index = i;
                    builder.addDoubleProperty("Module " + index + " Angle", 
                        () -> driveSubsystem.getModuleStates()[index].angle.getRadians(), null);
                    builder.addDoubleProperty("Module " + index + " Velocity", 
                        () -> driveSubsystem.getModuleStates()[index].speedMetersPerSecond, null);
                }
            }
        };
    }
}