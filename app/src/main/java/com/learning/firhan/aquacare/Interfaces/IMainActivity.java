package com.learning.firhan.aquacare.Interfaces;

import android.os.Bundle;

public interface IMainActivity {
    void setFragment(String fragmentTag, boolean addToBackStack, Bundle bundle);
    void setTitle(String title, boolean hasBack);
}
