package de.hahn.apibrowser.settings;

import java.util.regex.Pattern;

public class Page {

    private final Package classPackage;
    private final String completeName;
    private final String content;

    public Page(String completePath, String content) {
        this.completeName = completePath;
        classPackage = new Package(completePath);
        this.content = content;
    }

    public String getName() {
        return classPackage.getName();
    }

    public String getCompleteName() {
        return completeName;
    }

    public Package getPackage() {
        return classPackage;
    }

    public String getContent() {
        return content;
    }

    public int compareTo(Object o) {
        if (!(o instanceof Page)) {
            return 0;
        }

        Page other = (Page) o;

        boolean startsWithCharThis = Pattern.matches("[a-zA-Z].*", getName());
        boolean startsWithCharOther = Pattern.matches("[a-zA-Z].*", other.getName());

        // when the other does not start with an char then this should be above
        if (startsWithCharThis && !startsWithCharOther) {
            return -1;
        } else if (!startsWithCharThis && startsWithCharOther) {
            // other way around
            return 1;
        }

        int name = getName().toLowerCase().compareTo(other.getName().toLowerCase());
        if (name != 0) {
            // when names are not the same return here the natural order
            return name;
        }

        // the method should never come so far.. but when then the keys are the
        // same
        return 0;
    }
}
