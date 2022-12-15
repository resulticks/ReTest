package io.mob.resu.reandroidsdk.error;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class Util {


    public static boolean isEditText(View v) {
        try {
            EditText s = (EditText) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }




    public static boolean isTextView(View v) {
        try {
            TextView s = (TextView) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isButton(View v) {
        try {
            Button s = (Button) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }



    public static boolean isImageView(View v) {
        try {
            ImageView s = (ImageView) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isToggleButton(View v) {
        try {

            ToggleButton s = (ToggleButton) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isRadioButton(View v) {
        try {
            RadioButton s = (RadioButton) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isRadioGroup(View v) {
        try {
            RadioGroup s = (RadioGroup) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isWebView(View v) {
        try {
            WebView s = (WebView) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static boolean isViewGroup(View v) {
        try {
            ViewGroup s = (ViewGroup) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isDialogFragments(Fragment v) {
        try {
            DialogFragment s = (DialogFragment) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static boolean isAppCompatActivity(Activity v) {
        try {
            FragmentActivity s = (FragmentActivity) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isCheckBox(View v) {
        try {
            CheckBox s = (CheckBox) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isSwitch(View v) {
        try {
            Switch s = (Switch) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isRatingBar(View v) {
        try {
            RatingBar s = (RatingBar) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isSeekBar(View v) {
        try {
            SeekBar s = (SeekBar) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isSpinner(View v) {
        try {
            Spinner s = (Spinner) v;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


}
