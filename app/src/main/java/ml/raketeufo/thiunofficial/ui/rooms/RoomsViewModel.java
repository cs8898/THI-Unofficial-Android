package ml.raketeufo.thiunofficial.ui.rooms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoomsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RoomsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Rooms fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}