package org.cdrokar.saboteur.infiltration;

import java.util.Comparator;

import org.cdrokar.saboteur.domain.TargetProfile;

public enum TargetProfileComparator implements Comparator<TargetProfile> {

    INSTANCE;

    public int compare(TargetProfile o1, TargetProfile o2) {

        if (o1.equals(o2)) {
            return 0;
        }

        // Default profile is the last one
        if (o1.equals(TargetProfile.DEFAULT)) {
            return 1;
        }

        if (o2.equals(TargetProfile.DEFAULT)) {
            return -1;
        }

        // Subclasses profiles are taken last
        if (o1.isTargetSubclass() && !o2.isTargetSubclass()) {
            return 1;
        }

        if (o2.isTargetSubclass() && !o1.isTargetSubclass()) {
            return -1;
        }

        // Wildcards profiles are taken last
        if (o1.getClassPath().contains("*") && !o2.getClassPath().contains("*")) {
            return 1;
        }

        if (o2.getClassPath().contains("*") && !o1.getClassPath().contains("*")) {
            return -1;
        }

        // Classpath length (longer path taken precedence)
        return Integer.compare(o2.getClassPath().length(), o1.getClassPath().length());
    }

}
