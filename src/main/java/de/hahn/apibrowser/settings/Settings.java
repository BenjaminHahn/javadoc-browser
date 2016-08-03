package de.hahn.apibrowser.settings;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Reads the settings from a file on the system. Creates Collection of {@link Api}s. Grants access
 * to the all {@link Api}s.
 */
public final class Settings {

    private static final Logger logger = LogManager.getLogger(Settings.class.getName());

    private static Settings self;

    private static final String SETTINGS_TXT = "settings.txt";

    /**
     * Holds all the {@link Api}s of the application found in the settings.txt.
     */
    private static List<Api> apis = new ArrayList<>();

    private Settings() {
    }

    /**
     * Loads the settings.txt on the first call.
     *
     * @return the Singleton instance.
     */
    public static Settings loadFile() {
        if (self == null) {
            self = new Settings();
            apis = self.readApiFromSettingsFile();
            apis.addAll(self.readApisInSameFolder());
        }
        return self;
    }

    public static Settings get() {
        return self;
    }

    /**
     * @return All {@link Api} found in the settings.txt.
     */
    public Collection<Api> getApis() {
        return apis;
    }

    /**
     * Takes a path to a settings file and generates {@link Api}s from this file.
     *
     * @return a collection of apis specified in the file without pages.
     */
    private List<Api> readApiFromSettingsFile() {
        List<Api> apis = new ArrayList<>();
        String[] linesFile = readSettingsFile();

        Stream.of(linesFile).parallel()
                .forEach(line -> {
                    String cuttedString = cutAfterSemicolon(line);
                    // split string
                    StringTokenizer tokenizer = new StringTokenizer(cuttedString, "=");
                    String name = tokenizer.nextToken();
                    String val = tokenizer.nextToken();

                    // transform path to windows readable string
                    String directory = Paths.get(val).toAbsolutePath().toString();

                    Api api = new Api(name, directory);

                    // create the blacklist
                    String cuttedBefore = cutBeforeSemicolon(line);
                    Collection<String> blacklist = readBlacklistSettings(cuttedBefore);
                    api.addAllToBlacklist(blacklist);

                    apis.add(api);
                });

        return apis;
    }

    private List<Api> readApisInSameFolder() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(""))) {
            return StreamSupport.stream(stream.spliterator(), false)
                    .filter(p -> p.getFileName().toString().endsWith(".zip") ||
                            p.getFileName().toString().endsWith(".jar"))
                    .map(p -> {
                        String name = p.getFileName().toString();
                        String dir = p.toAbsolutePath().toString();

                        String removeExtension = StringUtils
                                .remove(StringUtils.remove(name, ".zip"), ".jar");
                        return new Api(removeExtension, dir);
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Read a file and save every line to a collection. Ignore empty lines and comments("//").
     *
     * @return all the non comment lines in the settings file.
     */
    private String[] readSettingsFile() {
        Collection<String> input = new ArrayList<>();
        String absPath = Paths.get("", SETTINGS_TXT).toAbsolutePath().toString();
        logger.debug("looking for file in: " + absPath);
        try {
            Scanner scanner = new Scanner(new File(absPath));
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                // ignore comments and empty lines
                if (!nextLine.startsWith("//") && !nextLine.isEmpty()) {
                    input.add(nextLine);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            createDefaultFile(absPath);
        }
        return input.toArray(new String[input.size()]);
    }

    private void createDefaultFile(String dir) {
        File defaultFile = new File(dir);
        try {
            if (!defaultFile.exists()) defaultFile.createNewFile();
            PrintWriter writer = new PrintWriter(defaultFile);
            writer.println("// this is a comment");
            writer.println("// specific paths are written in following syntax.");
            writer.println("// name=path; *blacklist -> EXAMPLE: Java=C:\\JavaApi\\java; *String *Integer");
            writer.println("// The blacklist does only belong to the api");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the tokens representing forbidden Strings. Tokens are behind a ';' and marked with a
     * '*'.
     *
     * @param blacklistTokens A String containing blacklist tokens.
     * @return The Collection contains the single forbidden words.
     */
    private Collection<String> readBlacklistSettings(String blacklistTokens) {
        Collection<String> blacklist = new ArrayList<>();

        StringTokenizer tokenizer = new StringTokenizer(blacklistTokens, "*");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                blacklist.add(token);
            }
        }
        return blacklist;
    }

    /**
     * Cut the the part of the String after the sequence occurred.
     *
     * @param s String to cut.
     * @return the trimmed string.
     */
    private String cutAfterSemicolon(String s) {
        int occurence = s.indexOf(";");
        return occurence == -1 ? s : s.substring(0, occurence);
    }

    /**
     * Cut the the part of the String before the sequence occurred.
     *
     * @param s String to cut
     * @return the trimmed string
     */
    private String cutBeforeSemicolon(String s) {
        int occurence = s.indexOf(";");
        return occurence == -1 ? s : s.substring(occurence + 1);
    }
}