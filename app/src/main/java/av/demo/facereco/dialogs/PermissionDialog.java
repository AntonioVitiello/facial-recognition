package av.demo.facereco.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import av.demo.facereco.R;

/**
 * Created by Antonio Vitiello on 20/04/2018.
 */

public class PermissionDialog extends DialogFragment {
    private static String ARG_PERMISSION = "permission";
    private static final String ARG_PERMISSION_ID = "permission_id";

    public static PermissionDialog newInstance(String permission, int permissionId) {
        Bundle args = new Bundle();
        args.putString(ARG_PERMISSION, permission);
        args.putInt(ARG_PERMISSION_ID, permissionId);
        PermissionDialog dialog = new PermissionDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String permission = arguments.getString(ARG_PERMISSION);
        final int permissionId = arguments.getInt(ARG_PERMISSION_ID);
        final FragmentActivity parent = getActivity();

        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.disk_request_permission)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            parent.requestPermissions(new String[]{permission}, permissionId);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (parent != null) {
                                    parent.finish();
                                }
                            }
                        })
                .create();
    }

}
