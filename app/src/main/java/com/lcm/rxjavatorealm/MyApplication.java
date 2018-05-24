package com.lcm.rxjavatorealm;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2018/5/24 20:03
 * Desc:
 * *****************************************************************
 */
public class MyApplication extends Application{


    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("Test_DB")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
