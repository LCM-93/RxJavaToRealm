package com.lcm.rxjavatorealm.RxRealm;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2018/5/24 14:33
 * Desc:
 * *****************************************************************
 */
public class RealmObservable {
    private RealmObservable() {
    }

    public static <T extends Object> Observable<T> createObservable(final Function<Realm,T> function){
        return Observable.create(new ObservableOnSubscribeRealm<T>() {
            @Override
            public T get(Realm realm) throws Exception {
                T t = function.apply(realm);
                if(t != null){
                    if( t instanceof RealmObject){
                        return (T) realm.copyFromRealm((RealmObject)t);
                    }else if(t instanceof RealmList){
                        return (T) realm.copyFromRealm((List<RealmObject>) t);
                    }else if(t instanceof RealmResults){
                        return (T) realm.copyFromRealm((List<RealmObject>) t);
                    }
                    return t;
                }
                return t;
            }
        });
    }
}
