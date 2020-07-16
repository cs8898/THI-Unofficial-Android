package ml.raketeufo.thiunofficial.ui.mensa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MensaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MensaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Mensa fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}