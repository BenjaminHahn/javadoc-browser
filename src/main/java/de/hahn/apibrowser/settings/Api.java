package de.hahn.apibrowser.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * store information about an api
 *
 * @author post_000
 */
public class Api {

    private static final Logger logger = LogManager.getLogger(Api.class.getName());

    private String name;
    private Path directory;
    private Collection<Page> pages;
    private Collection<String> blacklist = new HashSet<>();

    private static List<String> defaultBlacklist = Arrays.asList("class-use.html","index.html");

    public Api(String name, String directory) {
        this.name = name;
        this.directory = Paths.get(directory);
        blacklist.addAll(defaultBlacklist);

        this.pages = ZipScanner.loadPagesForApi(this);
    }

    /**
     * Get the displayname of the api.
     * @return The displayname in readable form or an empty string.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the complete path including the name of the api as it is named on the file system.
     * @return The complete path. ../../foo/bar/api.zip
     */
    public Path getDirectory() {
        return directory;
    }

    public Collection<Page> getPages() {
        assert pages != null : "Pages of " + getName() + " are not loaded";
        return pages;
    }

    @Override
    public String toString() {
        return name;
    }


    public Collection<String> getBlacklist() {
        return blacklist;
    }

    public void addToBlacklist(String word) {
        blacklist.add(word);
    }

    public void addAllToBlacklist(Collection<String> collection) {
        blacklist.addAll(collection);
    }
}
