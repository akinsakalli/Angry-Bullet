import java.awt.event.KeyEvent;

/**
 * This is a game where a bullet experiences projectile motion.
 * User tries to shoot the targets by changing the velocity and shoot of angle.
 * Uses StdDraw library for visualization.
 * @author Akin Tuna Sakalli
 * @since 20.03.2024
 */

public class Main {
    public static void main(String[] args) {
        // Game Parameters
        int width = 1600; //screen width
        int height = 800; // screen height
        double gravity = 9.80665; // gravity
        double x0 = 120; // x and y coordinates of the bullet’s starting position on the platform double y0 = 120;
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angle

        // Box coordinates for obstacles and targets
        // Each row stores a box containing the following information:
        // x and y coordinates of the lower left rectangle corner, width, and height
        double[][] obstacleArray = {
                {1200, 0, 60, 220},
                {1000, 0, 60, 160},
                {600, 0, 60, 80},
                {600, 180, 60, 160},
                {220, 0, 120, 180}
        };
        double[][] targetArray = {{1160, 0, 30, 30},
                {730, 0, 30, 30},
                {150, 0, 20, 20},
                {1480, 0, 60, 60},
                {340, 80, 60, 30},
                {1500, 600, 60, 60}
        };

        /*
        Obstacles and targets of my own game design
        double[][] obstacleArray = {
                {1220, 0, 50, 150},
                {1400, 0, 50, 150},
                {310, 500, 100, 250},
                {410, 500, 150, 50},
                {400, 0, 100, 250},
                {700, 380, 200, 50},
                {1400, 450, 400, 50},
                {1400, 600, 400, 50},
                {800, 600, 50, 100},
                {800, 700, 250, 50},
                {850, 600, 300, 50},
                {1150, 600, 50, 150},
                {1050, 410, 250, 50}
        };
        double[][] targetArray = {
                {1270, 0, 130, 50},
                {410, 550, 150, 50},
                {1550, 500, 50, 100},
                {850, 650, 300, 50},
                {1300, 410, 50, 50}
        };
         */

        // Canvas size is set as a rectangle which has the width and height that are given in advance.
        StdDraw.setCanvasSize(width, height);
        // X and Y scales are set as the interval of 0 to width and 0 to height, respectively.
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        // Double buffering is enabled in order to get a smoother visual.
        StdDraw.enableDoubleBuffering();
        // A pause duration is set in advance to use it in the program where it is needed.
        int pauseDuration = 60;
        // A new line length variable is declared in order to be used in the drawing of the shooting line,
        // and it is initialized with the same value as the bulletVelocity variable which is set in advance.
        double lineLength = bulletVelocity;

        boolean needsRedraw = true;

        // General loop starts, where all the user inputs will be processed.
        while (true) {
            // Two new variables are declared, velocityXInitial and velocityYInitial
            // velocityXInitial variable holds the X component of the bullet velocity
            double velocityXInitial = 0.58 * bulletVelocity * Math.cos(Math.toRadians(bulletAngle));
            // velocityYInitial variable holds the Y component of the bullet velocity
            double velocityYInitial = 0.58 * bulletVelocity * Math.sin(Math.toRadians(bulletAngle));
            // Both of these components are multiplied by a constant of 0.58 in order to fit the projectile motion into the screen.

            // A gameRunning variable is declared here and initialized to false, which will keep track if the user has started the projectile motion or not.
            boolean gameRunning = false;

            // A variable which indicates whether any input is taken from user in that loop iteration is declared.
            boolean inputDetected = false;

            // If user presses the up key, shooting angle is increased by 1 degrees. Then pause for a small instant.
            if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                bulletAngle += 1.0;
                StdDraw.pause(pauseDuration);
                inputDetected = true;
            }

            // If user presses the down key, shooting angle is decreased by 1 degrees. Then pause for a small instant.
            if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                bulletAngle -= 1.0;
                StdDraw.pause(pauseDuration);
                inputDetected = true;
            }

            // If user presses the right key, velocity angle is increased by 1
            // and the line length variable is increased by 5.5, which is an arbitrary number, in order to make the magnitude of the shooting line increase faster.
            // Then pause for a small instant.
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                bulletVelocity += 1.0;
                lineLength += 5.5;
                StdDraw.pause(pauseDuration);
                inputDetected = true;
            }

            // If user presses the left key, velocity angle is decreased by 1
            // and the line length variable is decreased by 5.5, then pause for a small instant.
            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                bulletVelocity -= 1.0;
                lineLength -= 5.5;
                StdDraw.pause(pauseDuration);
                inputDetected = true;
            }

            // Whenever an input from the user is detected or the canvas needs to be redrawn because it is the first iteration of the loop, the canvas is redrawn.
            if (inputDetected || needsRedraw) {
                drawCanvas(x0, bulletVelocity, bulletAngle, lineLength, obstacleArray, targetArray);
                needsRedraw = false;
            }

            // An initial time variable is declared here, which will be used later in the motion equations.
            double initialTime = 0;

            // After the user makes the arrangements with velocity and angle, the game starts as the space is pressed.
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                // Game running variable is changed to true, which will make the program go into the second while loop,
                // where all the motion is occurred and shown to the user.
                gameRunning = true;

                // The initial time is recorded as the user presses enter, which will be used in the motion equations.
                initialTime = System.currentTimeMillis() / 100.0;
            }

            // In order to draw the lines between the adjacent positions of the bullet, the last position of the bullet should be stored in each loop,
            // therefore the old position variables (xOld, yOld) are declared and initialized as the starting positions of the bullet.
            // So that the first line is drawn from the starting to the next instant.
            double xOld = x0;
            double yOld = x0;

            // After the user presses enter and the gameRunning variable is changed to true, motion starts.
            while (gameRunning) {
                // In each loop, current time is recorded and the difference between the current time and the initial time is stored.
                double time = System.currentTimeMillis() / 100.0 - initialTime;
                // Motion equations of the projectile motion are implemented.
                double x = x0 + velocityXInitial * time;
                double y = x0 + velocityYInitial * time - gravity * time * time / 2;
                // Line between the position of the last instant and the current position is drawn.
                StdDraw.line(xOld, yOld, x, y);
                // Current positions are stored in order to draw the next line in the next iteration.
                xOld = x;
                yOld = y;

                // Color is set to black and the bullet is drawn on its current position.
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledCircle(x, y, 5);

                // Game is displayed to the user interface.
                StdDraw.show();
                // Pause for a small instant.
                StdDraw.pause(8);

                // For each obstacle, collision is checked, that is, if the current position of the bullet is inside the obstacle's boundaries, collision is occurred.
                // If the bullet collides with any of the obstacles, the game is stopped and "Hit an obstacle" text is displayed.
                for (double[] obstacle: obstacleArray) {
                    if (obstacle[0] <= x && x <= obstacle[0] + obstacle[2] && obstacle[1] <= y && y <= obstacle[1] + obstacle[3]) {
                        StdDraw.textLeft(10,780, "Hit an obstacle. Press 'r' to shoot again.");
                        gameRunning = false;
                        break;
                    }
                }

                // For each target, collision is checked. If the bullet hit any of the targets, the game is stopped and "congratulations" text is displayed.
                for (double[] target: targetArray) {
                    if (target[0] <= x && x <= target[0] + target[2] && target[1] <= y && y <= target[1] + target[3]) {
                        StdDraw.textLeft(10,780, "Congratulations: You hit the target!");
                        gameRunning = false;
                        break;
                    }
                }

                // If the bullet's x coordinate exceeds the boundaries of the canvas, the game is stopped and "max x reached" text is displayed.
                if (x > 1600) {
                    StdDraw.textLeft(10, 780, "Max X reached. Press 'r' to shoot again.");
                    gameRunning = false;
                }
                // If the bullet touches the ground, that is, if the bullet's y coordinate becomes less than 0, the game is stopped.
                else if (y < 0) {
                    StdDraw.textLeft(10, 780, "Hit the ground. Press 'r' to shoot again.");
                    gameRunning = false;
                }

                // After each of the conditions which stops the game, gameRunning variable is set to false.
                // After the game stops, program expects the user to press 'R' in order to start the game again.
                // If the user presses 'R', velocity, angle and shooting line length variables is set to their initial conditions and the game starts again.
                while (!gameRunning) {
                    StdDraw.show();
                    if (StdDraw.isKeyPressed(KeyEvent.VK_R)){
                        bulletVelocity = 180;
                        lineLength = 180;
                        bulletAngle = 45.0;
                        StdDraw.clear();

                        // Canvas is redrawn because the first iteration of the general loop will be done.
                        needsRedraw = true;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Whenever a new input from the user is detected or the canvas needs to be redrawn, this method draws the canvas.
     * @param x0 the
     * @param bulletVelocity
     * @param bulletAngle
     * @param lineLength
     * @param obstacleArray
     * @param targetArray
     */
    public static void drawCanvas(double x0, double bulletVelocity, double bulletAngle, double lineLength, double[][] obstacleArray, double[][] targetArray) {
        // Canvas is cleared in each redrawing.
        StdDraw.clear();

        // The color is set to dark gray and the obstacles are drawn.
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        for (double[] obstacle : obstacleArray) {
            StdDraw.filledRectangle(obstacle[0] + obstacle[2] / 2, obstacle[1] + obstacle[3] / 2, obstacle[2] / 2, obstacle[3] / 2);
        }

        // The color is set to orange and target rectangles are drawn.
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        for (double[] target : targetArray) {
            StdDraw.filledRectangle(target[0] + target[2] / 2, target[1] + target[3] / 2, target[2] / 2, target[3] / 2);
        }

        // The color is set to black and the shooting platform is drawn.
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(x0 / 2, x0 / 2, x0 / 2, x0 / 2);

        // Pen radius is set to 0.008, which is 4 times the default value, in order to make the shooting line thicker.
        StdDraw.setPenRadius(0.008);
        // The shooting line is drawn.
        StdDraw.line(x0, x0, x0 + lineLength * Math.cos(Math.toRadians(bulletAngle)) * 0.3, x0 + lineLength * Math.sin(Math.toRadians(bulletAngle)) * 0.3);
        // Pen radius is set back to its default value.
        StdDraw.setPenRadius(0.002);

        // The color is set to white and the angle of shooting and velocity of the bullet are written in the shooting platform.
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(x0 / 2, x0 / 2 + 10, "a: " + bulletAngle);
        StdDraw.text(x0 / 2, x0 / 2 - 10, "v: " + bulletVelocity);

        // The map design is showed to user interface.
        StdDraw.show();
    }
}

