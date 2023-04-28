package org.mstor.model;

import java.util.Set;

public interface LabelSupport {

    void addLabel(String label);

    void removeLabel(String label);

    Set<String> getLabels();
}
