package com.defrag.redmineplugin.view.form;

import com.defrag.redmineplugin.view.ValidatedDialog;
import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Optional;

/**
 * Created by defrag on 18.10.17.
 */
public class CheckoutBranchForm extends JDialog implements ValidatedDialog<String> {

    @Getter
    private JPanel contentPane;

    private JTextField branchNameTxt;

    public CheckoutBranchForm(Integer taskId) {
        branchNameTxt.setText("feature/" + taskId);

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    @Override
    public Optional<ValidationInfo> getValidationInfo() {
        if (StringUtils.isBlank(branchNameTxt.getText())) {
            return Optional.of(new ValidationInfo("Наименование ветки не может быть пустым", branchNameTxt));
        }
        return Optional.empty();
    }

    @Override
    public String getData() {
        return branchNameTxt.getText();
    }
}