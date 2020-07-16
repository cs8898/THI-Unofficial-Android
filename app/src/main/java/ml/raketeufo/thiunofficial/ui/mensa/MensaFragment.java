package ml.raketeufo.thiunofficial.ui.mensa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import ml.raketeufo.thiunofficial.R;

public class MensaFragment extends Fragment {

    private MensaViewModel mensaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mensaViewModel =
                ViewModelProviders.of(this).get(MensaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mensa, container, false);
        final TextView textView = root.findViewById(R.id.text_mensa);
        mensaViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}