package de.hahn.apibrowser.settings;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Package {
    private List<String> classPackage;
    private final String name;

    public Package(String path) {
        String s = removeBackDirectory(path);
        this.name = calculateName(s);
        this.classPackage = splitPackage(s);
    }

    private String removeBackDirectory(String s) {
        return StringUtils.remove(s, "../");
    }

    private static String calculateName(String completeName) {
        String[] split = StringUtils.split(completeName, "/");
        return StringUtils.remove(split[split.length - 1], ".html");
    }

    public String getName() {
        return name;
    }

    public List<String> getPackage() {
        return classPackage;
    }

    public String withDots() {
        return join(classPackage, ".");
    }

    public String withSlashes() {
        return join(classPackage, "/");
    }

    private List<String> splitPackage(String completeName) {
        return Arrays.asList(completeName.split("/"));
    }

    private static String join(List<String> string, String separator) {
        return StringUtils.join(string, separator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Package aPackage = (Package) o;

        // when the patch matches almost check the name
        // this allows the jar to have one subdirectory. e.g. api/java/...
        HashSet<String> first = new HashSet<>(classPackage);
        HashSet<String> second = new HashSet<>(aPackage.classPackage);
        first.removeAll(second);
        if (!(first.size() <= 1)) return false;

        return name != null ? name.equals(aPackage.name) : aPackage.name == null;
    }

    @Override
    public int hashCode() {
        int result = classPackage != null ? classPackage.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
