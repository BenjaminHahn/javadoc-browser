package de.hahn.apibrowser.settings;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Read HTML Pages from the file system. This class loads the {@link Page}s for a {@link Api}.
 */
final class ZipScanner {

    private static final Logger logger = LogManager.getLogger(ZipScanner.class.getName());

    /**
     * Reads the HTML pages from the file system.
     *
     * @param api contains the blacklist and directory from which the pages will be fetched.
     * @return all found {@link Page}s.
     */
    static Collection<Page> loadPagesForApi(final Api api) {
        logger.debug("STARTED fetching files for " + api.getName());

        Collection<Page> pages = new ArrayList<>();
        try {
            ZipFile zipFile = new JarFile(api.getDirectory().toFile());
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry jarEntry = entries.nextElement();
                String completeName = jarEntry.getName(); // name and replative path

                if (!isHtmlFile(completeName) || onBlacklist(completeName, api.getBlacklist())) {
                    continue;
                }

                InputStream inputStream = zipFile.getInputStream(jarEntry);

                String content = IOUtils.toString(inputStream, "UTF-8");
                IOUtils.closeQuietly(inputStream);

                Page page = new Page(completeName ,content);
                pages.add(page);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("FINISHED fetching files for " + api.getName() + " size: " + pages.size());
        return pages;
    }

    private static boolean isHtmlFile(String name) {
        return name.endsWith(".html");
    }

    private static boolean onBlacklist(String name, Collection<String> blacklist) {
        for (String s : blacklist) {
            if (name.contains(s)) return true;
        }
        return false;
    }
}
