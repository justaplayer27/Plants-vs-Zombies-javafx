import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Plants vs Zombies Game Launcher
 * Compiles and launches the JavaFX game
 */
public class GameLauncher {
    private static final String JAVAFX_HOME = "C:\\javafx-sdk";
    private static final String BIN_DIR = "bin";
    private static final String SRC_DIR = "src/main/java";
    private static final String GAME_MAIN = "com.pvz.game.PlantsVsZombiesApp";
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Plants vs Zombies Game Launcher");
        System.out.println("========================================");
        System.out.println();
        
        try {
            // Validate JavaFX
            File javafxHome = new File(JAVAFX_HOME);
            File libDir = new File(javafxHome, "lib");
            
            if (!javafxHome.exists() || !libDir.exists()) {
                System.err.println("ERROR: JavaFX SDK not found at " + JAVAFX_HOME);
                System.err.println("Please download and extract JavaFX SDK from:");
                System.err.println("https://gluonhq.com/products/javafx/");
                System.exit(1);
            }
            
            System.out.println("JavaFX found: " + JAVAFX_HOME);
            System.out.println();
            
            // Create bin directory
            File binDir = new File(BIN_DIR);
            if (!binDir.exists()) {
                binDir.mkdir();
                System.out.println("Created bin directory");
            }
            
            // Compile
            System.out.println("Compiling source files...");
            if (!compile()) {
                System.err.println("Compilation failed!");
                System.exit(1);
            }
            
            System.out.println("Copying resources...");
            copyResources(Paths.get("src/main/resources"), Paths.get(BIN_DIR));
            
            System.out.println("Build successful!");
            System.out.println();
            System.out.println("Starting game...");
            System.out.println();
            
            // Launch game
            launchGame();
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void copyResources(Path source, Path target) throws IOException {
        if (!Files.exists(source)) return;
        Files.walk(source).forEach(path -> {
            try {
                Path dest = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    if (!Files.exists(dest)) Files.createDirectories(dest);
                } else {
                    Files.copy(path, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                // Ignore errors for individual files
            }
        });
    }
    
    private static boolean compile() throws IOException, InterruptedException {
        // Find all Java files
        List<String> javaFiles = new ArrayList<>();
        Path srcPath = Paths.get(SRC_DIR);
        
        if (Files.exists(srcPath)) {
            Files.walk(srcPath)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> javaFiles.add(p.toString()));
        }
        
        if (javaFiles.isEmpty()) {
            System.err.println("ERROR: No Java files found!");
            return false;
        }
        
        // Build javac command
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("--module-path");
        command.add(JAVAFX_HOME + "\\lib");
        command.add("--add-modules");
        command.add("javafx.controls,javafx.fxml,javafx.graphics,javafx.media");
        command.add("-d");
        command.add(BIN_DIR);
        command.add("-cp");
        command.add(BIN_DIR);
        command.addAll(javaFiles);
        
        // Execute
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();
        
        return exitCode == 0;
    }
    
    private static void launchGame() throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("--enable-native-access=javafx.graphics");
        command.add("--module-path");
        command.add(JAVAFX_HOME + "\\lib");
        command.add("--add-modules");
        command.add("javafx.controls,javafx.fxml,javafx.graphics,javafx.media");
        command.add("-cp");
        command.add(BIN_DIR);
        command.add(GAME_MAIN);
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }
}
