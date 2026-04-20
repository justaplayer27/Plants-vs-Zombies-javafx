import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Plants vs Zombies Java Launcher
 * Handles compilation and game startup
 */
public class Launcher {
    private static final String JAVAFX_HOME = "C:\\javafx-sdk";
    private static final String PROJECT_DIR = new File("").getAbsolutePath();
    private static final String BIN_DIR = "bin";
    private static final String SRC_DIR = "src/main/java";
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Plants vs Zombies Game Launcher");
        System.out.println("========================================");
        System.out.println();
        
        try {
            // Check JavaFX
            if (!checkJavaFX()) {
                System.err.println("ERROR: JavaFX SDK not found at " + JAVAFX_HOME);
                System.err.println("Please download JavaFX and extract to C:\\javafx-sdk");
                System.exit(1);
            }
            
            // Create bin directory
            createBinDirectory();
            
            // Compile project
            if (!compileProject()) {
                System.err.println("ERROR: Compilation failed!");
                System.exit(1);
            }
            
            System.out.println();
            System.out.println("Compilation successful!");
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
    
    private static boolean checkJavaFX() {
        File javafxHome = new File(JAVAFX_HOME);
        File libDir = new File(javafxHome, "lib");
        return javafxHome.exists() && libDir.exists();
    }
    
    private static void createBinDirectory() throws IOException {
        Path binPath = Paths.get(PROJECT_DIR, BIN_DIR);
        if (!Files.exists(binPath)) {
            Files.createDirectories(binPath);
            System.out.println("Created bin directory");
        }
    }
    
    private static boolean compileProject() throws IOException, InterruptedException {
        System.out.println("Compiling source files...");
        System.out.println();
        
        // Find all Java files
        List<String> javaFiles = findJavaFiles();
        
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
        
        // Execute compilation
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(PROJECT_DIR));
        pb.inheritIO();
        
        Process process = pb.start();
        int exitCode = process.waitFor();
        
        return exitCode == 0;
    }
    
    private static List<String> findJavaFiles() throws IOException {
        List<String> javaFiles = new ArrayList<>();
        Path srcPath = Paths.get(PROJECT_DIR, SRC_DIR);
        
        if (Files.exists(srcPath)) {
            Files.walk(srcPath)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> javaFiles.add(p.toString()));
        }
        
        return javaFiles;
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
        command.add("com.pvz.game.PlantsVsZombiesApp");
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(PROJECT_DIR));
        pb.inheritIO();
        
        Process process = pb.start();
        process.waitFor();
    }
}
