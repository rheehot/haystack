package haystack.ui;

import com.intellij.openapi.ui.Messages;
import haystack.core.models.ActionForm;
import haystack.core.models.FieldModel;
import haystack.core.models.PageModel;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CreateActionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textActionName;
    private JTextField textParameters;
    private JTextField textVariable;
    private JCheckBox methodInRepositoryCheckBox;
    private JCheckBox reducerActionCheckBox;
    private JPanel reducerPanel;
    private JTextField reducerActionName;
    private JTextField reducerParameters;
    private ActionFormCallbacks mListener;

    public CreateActionDialog(ActionFormCallbacks listener) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        mListener = listener;

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());
        reducerActionCheckBox.addChangeListener(e -> reducerPanel.setVisible(reducerActionCheckBox.isSelected()));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        ActionForm actionForm = new ActionForm();
        if (textActionName.getText().trim().length() > 0) {
            actionForm.setActionName(textActionName.getText().trim());
            if (textParameters.getText().trim().length() != 0) {
                String[] ps = textParameters.getText().trim().split(",");
                List<FieldModel> fields = new ArrayList<>();
                for (String s : ps) {
                    String strim = s.trim();
                    if (strim.length() > 0) {
                        int spaceIndex = strim.indexOf(" ");
                        if (spaceIndex > 0) {
                            String type = strim.substring(0, spaceIndex);
                            String vari = strim.substring(spaceIndex).trim();
                            FieldModel field = new FieldModel(vari, vari, type, "");
                            fields.add(field);
                        } else {
                            Messages.showMessageDialog("Unvalid String, define parameters like " +
                                    "'String p1, int p2'", "Error", Messages.getErrorIcon());
                            return;
                        }
                    }
                }

                actionForm.setParameters(fields);
            }
        }


        actionForm.setMethodInRepository(methodInRepositoryCheckBox.isSelected());
        actionForm.setReducer(reducerActionCheckBox.isSelected());
        if (reducerActionCheckBox.isSelected()) {
            if (reducerActionName.getText().trim().length() == 0) {
                Messages.showMessageDialog("Please input Reducer Action Name", "Error",
                        Messages.getErrorIcon());
                return;
            } else {
                actionForm.setReducerActionName(reducerActionName.getText().trim());
            }

            if (reducerParameters.getText().trim().length() != 0) {
                String[] ps = reducerParameters.getText().trim().split(",");
                List<FieldModel> fields = new ArrayList<>();
                for (String s : ps) {
                    String strim = s.trim();
                    if (strim.length() > 0) {
                        int spaceIndex = strim.indexOf(" ");
                        if (spaceIndex > 0) {
                            String type = strim.substring(0, spaceIndex);
                            String vari = strim.substring(spaceIndex).trim();
                            FieldModel field = new FieldModel(vari, vari, type, "");
                            fields.add(field);
                        } else {
                            Messages.showMessageDialog("Unvalid String, define reducer parameters like " +
                                    "'String p1, int p2'", "Error", Messages.getErrorIcon());
                            return;
                        }
                    }
                }

                actionForm.setReducerParameters(fields);
            }

            if (textVariable.getText().trim().length() > 0) {
                String stateParam = textVariable.getText().trim();
                int spaceIndex = stateParam.lastIndexOf(" ");
                if (spaceIndex > 0) {
                    String type = stateParam.substring(0, spaceIndex).trim();
                    String vari = stateParam.substring(spaceIndex).trim();
                    FieldModel field = new FieldModel(vari, vari, type, "");
                    actionForm.setStateVariable(field);
                } else {
                    Messages.showMessageDialog("Unvalid String, define variable like 'Map<String," +
                            " User> map' or 'int i'", "Error", Messages.getErrorIcon());
                    return;
                }

            }
        }

        if (mListener != null) {
            mListener.onActionReady(actionForm);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CreateActionDialog dialog = new CreateActionDialog(new ActionFormCallbacks() {
            @Override
            public void onActionReady(ActionForm actionForm) {
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);

    }

    public interface ActionFormCallbacks {
        void onActionReady(ActionForm actionForm);
    }
}


