package com.defrag.redmineplugin.view;

import com.intellij.openapi.ui.ValidationInfo;

import javax.swing.*;
import java.util.Optional;

/**
 * Created by defrag on 29.08.17.
 */
public interface ValidatedDialog {

    JPanel getContentPane();

    Optional<ValidationInfo> getValidationInfo();
}