import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class MultiplePosts {

    public static void main(String[] args) {
        String baseDirectoryPath = "/Users/smart/OneDrive/Documents/GitHub/saraistudies2/src/pages/classes/";

        // List all available classes (subdirectories)
        File baseDirectory = new File(baseDirectoryPath);
        String[] classes = baseDirectory.list((dir, name) -> new File(dir, name).isDirectory());

        if (classes == null || classes.length == 0) {
            System.out.println("No classes found in the directory.");
            return;
        }

        StdOut.println("");

        // Display available classes
        StdOut.println("Available classes:");
        for (int i = 0; i < classes.length; i++) {
            System.out.println((i + 1) + ". " + classes[i]);
        }

        StdOut.println("");

        // Prompt user to select a class
        StdOut.println("Please enter the number of the class you want to add posts to: ");
        String classIndexInput = StdIn.readLine();
        int classIndex = Integer.parseInt(classIndexInput) - 1;

        if (classIndex < 0 || classIndex >= classes.length) {
            System.out.println("Invalid selection.");
            return;
        }

        String className = classes[classIndex];
        String directoryPath = baseDirectoryPath + className;

        StdOut.println("");
        StdOut.println("Please write the title of each post you want then click enter to add post. Ctrl + Z and then enter when you're done adding posts and want to exit the program.");
        StdOut.println("");

        // Read all titles from standard input, one per line
        while (!StdIn.isEmpty()) {
            String title = StdIn.readLine();
            try {
                createBlogPost(directoryPath, title);
            } catch (IOException e) {
                System.err.println("Error creating blog post: " + e.getMessage());
            }
        }
    }

    public static void createBlogPost(String directoryPath, String title) throws IOException {
        // Get the last file name in the directory
        File directory = new File(directoryPath);
        String[] files = directory.list((dir, name) -> name.startsWith("post-") && name.endsWith(".mdx"));
        if (files == null || files.length == 0) {
            System.out.println("No files found in the directory.");
            return;
        }

        Arrays.sort(files, Comparator.comparingInt(file -> extractNumber(file)));

        String lastFileName = files[files.length - 1];
        String newFileName = getNextFileName(lastFileName);
        String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

        // Create the new file with the specified content
        Path newFilePath = Paths.get(directoryPath, newFileName);
        try (FileWriter writer = new FileWriter(newFilePath.toFile())) {
            writer.write("---\n");
            writer.write("layout: '@layouts/Blog.astro'\n");
            writer.write("title: \"" + title + "\"\n");
            writer.write("date: \"" + date + "\"\n");
            writer.write("author: Sarai Marte\n");
            writer.write("---\n\n");
        }

        System.out.println("New blog post created: " + newFileName);
    }

    private static int extractNumber(String fileName) {
        String numberPart = fileName.substring(5, fileName.length() - 4); // Remove "post-" and ".mdx"
        return Integer.parseInt(numberPart);
    }

    private static String getNextFileName(String lastFileName) {
        int lastNumber = extractNumber(lastFileName);
        int nextNumber;
        if (lastNumber == 9) {
            nextNumber = 99;
        } else if (lastNumber > 9 && String.valueOf(lastNumber).endsWith("9")) {
            nextNumber = lastNumber * 10 + 9;
        } else {
            nextNumber = lastNumber + 1;
        }
        return "post-" + nextNumber + ".mdx";
    }
}
